package net.example.report.promotionanalysis.integration.repository;

import net.example.report.promotionanalysis.IntegrationTestBase;
import net.example.report.promotionanalysis.model.dto.ActualDto;
import net.example.report.promotionanalysis.repository.ActualRepository;
import net.example.report.promotionanalysis.repository.projection.SalesFactsByMonthView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static net.example.report.promotionanalysis.integration.TestData.*;


public class ActualRepositoryTest extends IntegrationTestBase {

    @Autowired
    ActualRepository actualRepository;

    @Test
    void getSalesFactsByMonth() {
        int expectedCountOfRecords = 8;
        List<SalesFactsByMonthView> salesFacts = actualRepository.getSalesFactsByMonth();
        assertFalse(salesFacts.isEmpty());
        assertEquals(expectedCountOfRecords, salesFacts.size());
    }

    @Test
    void getSalesFactsByChainsAndProducts() {

        int pageNumber = 0;
        int pageSize = 7;
        int expectedTotalElements = 14;
        var chains = List.of(CHAIN_NAME_1, CHAIN_NAME_2);
        var products = List.of(PRODUCT_CODE_2, PRODUCT_CODE_4);
        Page<ActualDto> page = actualRepository.getSalesFactsByChainsAndProducts(chains, products, PageRequest.of(pageNumber, pageSize));
        assertFalse(page.isEmpty());
        assertEquals(pageSize, page.get().count());
        assertEquals(expectedTotalElements, page.getTotalElements());

    }

}
