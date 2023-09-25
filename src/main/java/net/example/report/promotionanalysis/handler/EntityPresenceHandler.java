package net.example.report.promotionanalysis.handler;

import lombok.RequiredArgsConstructor;
import net.example.report.promotionanalysis.exception.ChainNotFoundException;
import net.example.report.promotionanalysis.exception.ProductNotFoundException;
import net.example.report.promotionanalysis.model.entity.Chain;
import net.example.report.promotionanalysis.model.entity.Product;
import net.example.report.promotionanalysis.repository.ChainRepository;
import net.example.report.promotionanalysis.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class EntityPresenceHandler {

    private final ChainRepository chainRepository;
    private final ProductRepository productRepository;

    public Chain checkChainByName(String name) {
        return chainRepository.findByName(name)
                .orElseThrow(() ->
                        new ChainNotFoundException("Не найдена сеть по наименованию: " + Objects.toString(name)));
    }

    public Product checkProductByCode(Long code) {
        return productRepository.findById(code)
                .orElseThrow(() -> new ProductNotFoundException("Не найден продукт по коду: " + Objects.toString(code)));

    }

}
