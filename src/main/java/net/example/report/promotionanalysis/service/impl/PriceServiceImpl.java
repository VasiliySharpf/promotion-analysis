package net.example.report.promotionanalysis.service.impl;

import lombok.RequiredArgsConstructor;
import net.example.report.promotionanalysis.exception.PriceRegistrationException;
import net.example.report.promotionanalysis.handler.EntityPresenceHandler;
import net.example.report.promotionanalysis.mapper.PriceViewMapper;
import net.example.report.promotionanalysis.model.dto.PriceDto;
import net.example.report.promotionanalysis.model.entity.Chain;
import net.example.report.promotionanalysis.model.entity.Price;
import net.example.report.promotionanalysis.model.entity.Product;
import net.example.report.promotionanalysis.repository.PriceRepository;
import net.example.report.promotionanalysis.service.PriceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;
    private final PriceViewMapper priceViewMapper;
    private final EntityPresenceHandler entityPresenceHandler;


    @Override
    public Optional<PriceDto> findPriceBy(Long materialCode, String chainName) {
        return priceRepository.findByProductCodeAndChainName(materialCode, chainName)
                .map(priceViewMapper::map);
    }

    @Override
    public List<PriceDto> findAllPricesBy(String chainName) {
        return priceRepository.findAllByChainName(chainName).stream()
                .map(priceViewMapper::map)
                .toList();
    }

    @Override
    public List<PriceDto> findAllPricesBy(Long materialCode) {
        return priceRepository.findAllByMaterialCode(materialCode).stream()
                .map(priceViewMapper::map)
                .toList();
    }

    @Override
    public Page<PriceDto> findAll(Pageable pageable) {
        return priceRepository.findAllPrices(pageable)
                .map(priceViewMapper::map);
    }

    @Transactional
    @Override
    public boolean updatePrice(PriceDto priceDto) {
        int numberOfModified = priceRepository.updatePrice(
                priceDto.materialCode(),
                priceDto.chainName(),
                priceDto.regularPricePerUnit());

        return numberOfModified > 0;
    }

    @Transactional
    @Override
    public void registerNewPrice(PriceDto priceDto) {

        Chain chain = entityPresenceHandler.checkChainByName(priceDto.chainName());
        Product product = entityPresenceHandler.checkProductByCode(priceDto.materialCode());

        Price price = Price.builder()
                .chain(chain)
                .product(product)
                .regularPricePerUnit(priceDto.regularPricePerUnit())
                .build();

        // опустил проверку существования цены по ключу "КодПродукта + Сеть", чтобы не делать лишний запрос к БД
        // если такая пара уже существует - получим ошибку попытки сохранения неуникального ключа (duplicate key value violates unique constraint)
        try {
            priceRepository.save(price);
        } catch (Exception e) {
            throw new PriceRegistrationException("Ошибка регистрации цены: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public boolean deletePrice(Long materialCode, String chainName) {
        return priceRepository.deletePrice(materialCode, chainName) > 0;
    }

}
