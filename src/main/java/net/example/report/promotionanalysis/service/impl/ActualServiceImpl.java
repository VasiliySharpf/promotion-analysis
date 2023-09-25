package net.example.report.promotionanalysis.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.example.report.promotionanalysis.model.dto.ActualDto;
import net.example.report.promotionanalysis.model.dto.PageResponse;
import net.example.report.promotionanalysis.model.dto.SalesFactsByDay;
import net.example.report.promotionanalysis.model.dto.SalesFactsWithSharePromo;
import net.example.report.promotionanalysis.repository.ActualRepository;
import net.example.report.promotionanalysis.repository.projection.ActualPromoUpdateView;
import net.example.report.promotionanalysis.service.ActualService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActualServiceImpl implements ActualService {

    private final ActualRepository actualRepository;
    private final TransactionTemplate transactionTemplate;


    @Transactional(readOnly = true)
    @Override
    public List<SalesFactsWithSharePromo> getSalesFactsWithSharePromo() {
        return actualRepository.getSalesFactsByMonth().stream()
                .map(view -> new SalesFactsWithSharePromo(
                        view.getMonth(),
                        view.getChainName(),
                        view.getProductCategoryCode(),
                        view.getProductCategoryName(),
                        view.getRegularQuantity(),
                        view.getPromoQuantity(),
                        calculateShareOfPromotionalSales(
                                Optional.ofNullable(view.getPromoQuantity()).orElse(0),
                                Optional.ofNullable(view.getAllQuantity()).orElse(0))))
                .toList();
    }

    @Override
    public PageResponse<SalesFactsByDay> getSalesFactsByDayWithFiltersByChainsAndProducts(Collection<String> chainNames, Collection<Long> productCodes, Pageable pageable) {

        Page<ActualDto> factsPage = actualRepository.getSalesFactsByChainsAndProducts(chainNames, productCodes, pageable);
        Map<LocalDate, List<ActualDto>> mapActualsByDay = factsPage.stream()
                .collect(groupingBy(ActualDto::date, toList()));

        List<SalesFactsByDay> salesFacts = new ArrayList<>();
        mapActualsByDay.forEach((day, actuals) -> salesFacts.add(new SalesFactsByDay(day, actuals)));

        return PageResponse.of(
                salesFacts.stream().sorted(Comparator.comparing(SalesFactsByDay::day)).toList(),
                factsPage.getNumber(),
                factsPage.getSize(),
                factsPage.getTotalElements());
    }

    @Override
    public void updateShipmentTypes() {

        log.info("Начало обновления признака промо в фактах отгрузок");
        Optional<Long> actualLastIdOpt = actualRepository.getMinIdWherePromoIsEmpty();
        if (actualLastIdOpt.isEmpty()) {
            log.info("Нет данных для обновления");
            return;
        }
        long actualLastId = actualLastIdOpt.get();
        List<CompletableFuture<Void>> allTasks = new ArrayList<>();
        while (true) {
            // обновление небольшими транзакциями, чтобы не блокировать всю таблицу на время работы метода
            // также метод getActualsForUpdatePromo() выбирает записи по условию 'shipment_type is null'
            // в случае непредвиденной ситуации можно будет продолжить обновление
            final long currentId = actualLastId;
            Pair<Long, List<CompletableFuture<Void>>> maxUpdatedIdAndTaskList = transactionTemplate.execute(status -> updateShipmentTypeStartingFrom(currentId));
            if (Objects.isNull(maxUpdatedIdAndTaskList)) {
                break;
            } else if (Objects.isNull(maxUpdatedIdAndTaskList.getLeft())) {
                allTasks.addAll(maxUpdatedIdAndTaskList.getRight());
                break;
            }
            actualLastId = maxUpdatedIdAndTaskList.getLeft();
            allTasks.addAll(maxUpdatedIdAndTaskList.getRight());
        }

        // дождемся окончания выполнения всех задач
        checkTasksForUpdatingShipmentType(allTasks);
        log.info("Значение признака промо в фактах отгрузок обновлено");

    }

    private Pair<Long, List<CompletableFuture<Void>>> updateShipmentTypeStartingFrom(long actualId) {

        List<ActualPromoUpdateView> actuals = actualRepository.getActualsForUpdatePromo(actualId);
        if (actuals.isEmpty()) {
            // нет данных для обновления
            return null;
        }

        Map<String, List<Long>> actualsMap = actuals.stream()
                .collect(groupingBy(
                        ActualPromoUpdateView::getShipmentType,
                        mapping(ActualPromoUpdateView::getActualId, toList())));


        List<CompletableFuture<Void>> taskList = new ArrayList<>();

        actualsMap.forEach((shipmentType, ids) ->
                taskList.add(
                        CompletableFuture
                                .runAsync(() -> actualRepository.updateShipmentType(ids, shipmentType))
                                .exceptionally(exception -> {
                                    log.error("Ошибка обновления признака промо в фактах отгрузок {}", exception.getMessage());
                                    return null;
                                })));


        Long maxUpdatedId = actuals.stream()
                .map(ActualPromoUpdateView::getActualId)
                .max(Long::compare)
                .orElse(null);

        log.info("Запущены задачи обновления признака промо maxUpdatedId={}", maxUpdatedId);
        // вернем пару: максимальный ID фактов отгрузок - список задач обновления
        return Pair.of(maxUpdatedId, taskList);
    }

    private void checkTasksForUpdatingShipmentType(List<CompletableFuture<Void>> allTasks) {
        for (var task : allTasks) {
            long maximumWaitingTime = 2L;
            try {
                task.get(maximumWaitingTime, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.error("Ошибка обновления признака промо в фактах отгрузок {}", e.getMessage());
            }
        }
    }

    private BigDecimal calculateShareOfPromotionalSales(int promoQuantity, int allQuantity) {

        if (promoQuantity < 0 || allQuantity == 0) {
            // отрицательное значение promoQuantity сигнализирует о том, что в расчетном месяце были только возвраты
            // ранее проданных товаров по промо
            return new BigDecimal(0);
        }

        double shareOfPromoSales = ((double) promoQuantity / allQuantity) * 100;
        BigDecimal result = new BigDecimal(shareOfPromoSales);
        return result.setScale(2, RoundingMode.HALF_UP);
    }

}
