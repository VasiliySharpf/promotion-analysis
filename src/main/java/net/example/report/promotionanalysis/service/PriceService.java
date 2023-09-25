package net.example.report.promotionanalysis.service;

import net.example.report.promotionanalysis.model.dto.PriceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PriceService {

    Optional<PriceDto> findPriceBy(Long materialCode, String chainName);

    List<PriceDto> findAllPricesBy(String chainName);

    List<PriceDto> findAllPricesBy(Long materialCode);

    Page<PriceDto> findAll(Pageable pageable);

    boolean updatePrice(PriceDto priceDto);

    void registerNewPrice(PriceDto priceDto);

    boolean deletePrice(Long materialCode, String chainName);

}
