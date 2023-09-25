package net.example.report.promotionanalysis.repository.projection;

public interface SalesFactsByMonthView {

    String getMonth();

    Integer getRegularQuantity();

    Integer getPromoQuantity();

    Integer getAllQuantity();

    String getChainName();

    Long getProductCategoryCode();

    String getProductCategoryName();

}
