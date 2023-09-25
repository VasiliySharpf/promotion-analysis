package net.example.report.promotionanalysis.mapper;

import net.example.report.promotionanalysis.model.dto.PriceDto;
import net.example.report.promotionanalysis.repository.projection.PriceView;
import org.springframework.stereotype.Component;

@Component
public class PriceViewMapper implements Mapper<PriceView, PriceDto>{

    @Override
    public PriceDto map(PriceView priceView) {
        return new PriceDto(
                priceView.getChainName(),
                priceView.getMaterialCode(),
                priceView.getRegularPricePerUnit());
    }
}
