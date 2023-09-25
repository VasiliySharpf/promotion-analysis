package net.example.report.promotionanalysis.rest;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.example.report.promotionanalysis.model.dto.*;
import net.example.report.promotionanalysis.service.ActualService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final ActualService actualService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/actuals-by-month")
    @Operation(summary = "Выгрузка фактов продаж с учётом признака промо: " +
            "сеть, категория, месяц, факт продаж в штуках по базовой цене, факт продаж по промо цене, доля продаж по промо, %.")
    public List<SalesFactsWithSharePromo> getSalesFactsWithSharePromo() {
        log.info("Request [GET] /analysis/actuals-by-month");
        return actualService.getSalesFactsWithSharePromo();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/actuals-by-day")
    @Operation(summary = "Выгрузка фактов по дням, согласно фильтрации по списку наименований сетей и списку продуктов.")
    public PageResponse<SalesFactsByDay> getSalesFactsByDay(@RequestParam List<String> chainNames,
                                                            @RequestParam List<Long> productCodes,
                                                            PageRequestDto pageRequest) {
        log.info("Request [GET] /analysis/actuals-by-day");
        return actualService.getSalesFactsByDayWithFiltersByChainsAndProducts(
                chainNames, productCodes, PageRequest.of(pageRequest.page(), pageRequest.size()));
    }


    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/actuals/promo")
    @Operation(summary = "Заполнение признака промо в таблице фактов продаж.")
    public void updateShipmentTypes() {
        log.info("Request [PUT] /analysis/actuals/promo");
        actualService.updateShipmentTypes();
    }

}
