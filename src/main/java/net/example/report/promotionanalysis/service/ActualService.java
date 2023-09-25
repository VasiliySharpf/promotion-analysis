package net.example.report.promotionanalysis.service;

import net.example.report.promotionanalysis.model.dto.PageResponse;
import net.example.report.promotionanalysis.model.dto.SalesFactsByDay;
import net.example.report.promotionanalysis.model.dto.SalesFactsWithSharePromo;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface ActualService {

    List<SalesFactsWithSharePromo> getSalesFactsWithSharePromo();

    PageResponse<SalesFactsByDay> getSalesFactsByDayWithFiltersByChainsAndProducts(Collection<String> chainNames,
                                                                                   Collection<Long> productCodes,
                                                                                   Pageable pageable);

    void updateShipmentTypes();

}
