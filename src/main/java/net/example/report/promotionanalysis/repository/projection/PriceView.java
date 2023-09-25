package net.example.report.promotionanalysis.repository.projection;

public interface PriceView {
    String getChainName();
    Long getMaterialCode();
    Double getRegularPricePerUnit();
}
