package net.example.report.promotionanalysis.integration.repository;

import net.example.report.promotionanalysis.IntegrationTestBase;
import net.example.report.promotionanalysis.repository.PriceRepository;
import net.example.report.promotionanalysis.repository.projection.PriceView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static net.example.report.promotionanalysis.integration.TestData.*;
import static org.junit.jupiter.api.Assertions.*;

public class PriceRepositoryTest extends IntegrationTestBase {

    @Autowired
    PriceRepository priceRepository;

    @Test
    void findByProductCodeAndChainName() {
        Optional<PriceView> priceView = priceRepository.findByProductCodeAndChainName(PRODUCT_CODE_1, CHAIN_NAME_1);
        assertTrue(priceView.isPresent());
        assertEquals(PRODUCT_CODE_1, priceView.get().getMaterialCode());
        assertEquals(CHAIN_NAME_1, priceView.get().getChainName());
    }

    @Test
    void findAllByChainName() {
        int expectedRecordCount = 4;
        List<PriceView> prices = priceRepository.findAllByChainName(CHAIN_NAME_2);
        assertFalse(prices.isEmpty());
        assertEquals(expectedRecordCount, prices.size());
    }

    @Test
    void findAllByMaterialCode() {
        int expectedRecordCount = 2;
        List<PriceView> prices = priceRepository.findAllByMaterialCode(PRODUCT_CODE_2);
        assertFalse(prices.isEmpty());
        assertEquals(expectedRecordCount, prices.size());
    }

    @Test
    void findAll() {
        int pageNumber = 0;
        int pageSize = 7;
        Page<PriceView> prices = priceRepository.findAllPrices(PageRequest.of(pageNumber, pageSize));
        assertFalse(prices.isEmpty());
        assertEquals(pageSize, prices.get().count());
    }

    @Test
    void updatePrice() {

        Double expectedPriceValue = 10000.001;
        // before
        Optional<PriceView> priceBefore = priceRepository.findByProductCodeAndChainName(PRODUCT_CODE_1, CHAIN_NAME_2);
        assertTrue(priceBefore.isPresent());
        assertNotEquals(expectedPriceValue, priceBefore.get().getRegularPricePerUnit());

        int numberOfModified = priceRepository.updatePrice(PRODUCT_CODE_1, CHAIN_NAME_2, expectedPriceValue);
        assertEquals(1, numberOfModified);

        // after
        Optional<PriceView> priceAfter = priceRepository.findByProductCodeAndChainName(PRODUCT_CODE_1, CHAIN_NAME_2);
        assertTrue(priceAfter.isPresent());
        assertEquals(expectedPriceValue, priceAfter.get().getRegularPricePerUnit());
    }

    @Test
    void updatePrice_whenChainNotExist() {

        Double priceValue = 10000.001;
        int numberOfModified = priceRepository.updatePrice(PRODUCT_CODE_1, NON_EXISTENT_CHAIN, priceValue);
        assertEquals(0, numberOfModified);

    }

    @Test
    void deletePrice() {

        Optional<PriceView> priceBefore = priceRepository.findByProductCodeAndChainName(PRODUCT_CODE_1, CHAIN_NAME_2);
        assertTrue(priceBefore.isPresent());
        int count = priceRepository.deletePrice(PRODUCT_CODE_1, CHAIN_NAME_2);
        assertEquals(1, count);
        Optional<PriceView> priceAfter = priceRepository.findByProductCodeAndChainName(PRODUCT_CODE_1, CHAIN_NAME_2);
        assertFalse(priceAfter.isPresent());

    }

}
