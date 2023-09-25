package net.example.report.promotionanalysis.integration.service;

import net.example.report.promotionanalysis.IntegrationTestBase;
import net.example.report.promotionanalysis.exception.PriceRegistrationException;
import net.example.report.promotionanalysis.model.dto.PriceDto;
import net.example.report.promotionanalysis.repository.PriceRepository;
import net.example.report.promotionanalysis.repository.projection.PriceView;
import net.example.report.promotionanalysis.service.PriceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static net.example.report.promotionanalysis.integration.TestData.*;
import static org.junit.jupiter.api.Assertions.*;

public class PriceServiceTest extends IntegrationTestBase {

    @Autowired
    PriceService priceService;

    @Autowired
    PriceRepository priceRepository;

    @Test
    void registerNewPrice() {

        // before
        Optional<PriceView> priceBefore = priceRepository.findByProductCodeAndChainName(PRODUCT_CODE_3, CHAIN_NAME_2);
        assertTrue(priceBefore.isEmpty());

        Double priceValue = 55.55;
        var priceDto = new PriceDto(CHAIN_NAME_2, PRODUCT_CODE_3, priceValue);
        priceService.registerNewPrice(priceDto);

        // after
        Optional<PriceView> priceAfter = priceRepository.findByProductCodeAndChainName(PRODUCT_CODE_3, CHAIN_NAME_2);
        assertTrue(priceAfter.isPresent());

    }

    @Test
    void registerPrice_when_priceAlreadyExists() {

        Optional<PriceView> price = priceRepository.findByProductCodeAndChainName(PRODUCT_CODE_1, CHAIN_NAME_1);
        // already exists
        assertTrue(price.isPresent());

        Double priceValue = 55.55;
        var priceDto = new PriceDto(CHAIN_NAME_1, PRODUCT_CODE_1, priceValue);
        var exception = assertThrows(PriceRegistrationException.class,
                () -> priceService.registerNewPrice(priceDto));

        int exceptionTextLengthIsNotLessThan = 23;
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().length() > exceptionTextLengthIsNotLessThan);
        assertEquals("Ошибка регистрации цены", exception.getMessage().substring(0, exceptionTextLengthIsNotLessThan));

    }

}
