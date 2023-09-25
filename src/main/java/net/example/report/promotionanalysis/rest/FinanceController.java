package net.example.report.promotionanalysis.rest;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.example.report.promotionanalysis.model.dto.PageRequestDto;
import net.example.report.promotionanalysis.model.dto.PageResponse;
import net.example.report.promotionanalysis.model.dto.PriceDto;
import net.example.report.promotionanalysis.model.dto.PriceResponse;
import net.example.report.promotionanalysis.service.PriceService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final PriceService priceService;

    @GetMapping("/prices")
    @Operation(summary = "Получение цен с учетом фильтров по наименованию сети и/или коду продукта.")
    public ResponseEntity<?> getPriceBy(@RequestParam(value = "materialCode", required = false) Long materialCode,
                                        @RequestParam(value = "chainName", required = false) String chainName) {

        log.info("Request [GET] /finance/prices materialCode={}, chainName={}", materialCode, chainName);
        List<PriceDto> prices = new ArrayList<>();
        if (materialCode != null && !chainName.isBlank()) {
            priceService.findPriceBy(materialCode, chainName).ifPresent(prices::add);

        } else if (materialCode != null) {
            prices = priceService.findAllPricesBy(materialCode);

        } else if (!chainName.isBlank()) {
            prices = priceService.findAllPricesBy(chainName);
        } else {
            return ResponseEntity.badRequest().body("Должен быть заполнен хотя бы один параметр запроса.");
        }
        return ResponseEntity.ok(new PriceResponse(prices));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/prices/all")
    @Operation(summary = "Постраничное получение всех цен.")
    public PageResponse<PriceDto> findAll(PageRequestDto pageRequest) {
        log.info("Request [GET] /finance/prices pageRequest={}", pageRequest);
        return PageResponse.of(
                priceService.findAll(PageRequest.of(pageRequest.page(), pageRequest.size())));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/prices")
    @Operation(summary = "Регистрация новой цены.")
    public void registerNewPrice(@RequestBody PriceDto priceDto) {
        log.info("Request [POST] /finance/prices priceDto={}", priceDto);
        priceService.registerNewPrice(priceDto);
    }

    @PutMapping("/prices")
    @Operation(summary = "Обновление цены по наименованию сети и коду продукта.")
    public ResponseEntity<?> updatePrice(@RequestBody PriceDto priceDto) {
        log.info("Request [PUT] /finance/prices priceDto={}", priceDto);
        return priceService.updatePrice(priceDto)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/prices")
    @Operation(summary = "Удаление цены по наименованию сети и коду продукта.")
    public ResponseEntity<?> deletePrice(@RequestParam String chainName,
                                         @RequestParam Long materialCode) {
        log.info("Request [DELETE] /finance/prices chainName={}, materialCode={}", chainName, materialCode);
        return priceService.deletePrice(materialCode, chainName)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

}
