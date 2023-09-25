package net.example.report.promotionanalysis.model.dto;

import java.math.BigDecimal;

public record SalesFactsWithSharePromo(String month,
                                       String chainName,
                                       Long productCategoryCode,
                                       String productCategoryName,
                                       Integer regularQuantity,
                                       Integer promoQuantity,
                                       BigDecimal shareOfPromoSalesInPercent) {
}
