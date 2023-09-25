package net.example.report.promotionanalysis.repository;

import net.example.report.promotionanalysis.model.dto.ActualDto;
import net.example.report.promotionanalysis.model.entity.Actual;
import net.example.report.promotionanalysis.repository.projection.ActualPromoUpdateView;
import net.example.report.promotionanalysis.repository.projection.SalesFactsByMonthView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ActualRepository extends JpaRepository<Actual, Long> {

    @Query(nativeQuery = true, value =
            "SELECT " +
            "    TO_CHAR(actuals.date, 'YYYY-MM-01') AS month, " +
            "    SUM(CASE " +
            "        WHEN actuals.shipment_type = 'REGULAR' " +
            "            THEN actuals.quantity " +
            "        ELSE 0 " +
            "    END) AS regularQuantity, " +
            "    SUM(CASE " +
            "        WHEN actuals.shipment_type = 'PROMO' " +
            "            THEN actuals.quantity " +
            "        ELSE 0 " +
            "    END) AS promoQuantity, " +
            "    SUM(actuals.quantity) AS allQuantity, " +
            "    COALESCE(chains.name, '') AS chainName, " +
            "    COALESCE(categories.name, '') AS productCategoryName, " +
            "    categories.code AS productCategoryCode " +
            "FROM promo.actuals AS actuals " +
            "    LEFT JOIN promo.customers AS customers " +
            "        ON actuals.ship_code = customers.ship_code " +
            "    LEFT JOIN promo.chains chains " +
            "        ON customers.chain_id = chains.id " +
            "    LEFT JOIN promo.products AS products " +
            "        ON actuals.material_code = products.material_code " +
            "    LEFT JOIN promo.categories AS categories " +
            "        ON products.category_code = categories.code " +
            "GROUP BY " +
            "    month, " +
            "    chainName, " +
            "    productCategoryCode, " +
            "    productCategoryName " +
            "HAVING " +
            "    SUM(actuals.quantity) <> 0 " +
            "ORDER BY " +
            "    month ")
    List<SalesFactsByMonthView> getSalesFactsByMonth();

    @Query(value =
            "select new net.example.report.promotionanalysis.model.dto.ActualDto( " +
            "   actual.date, " +
            "   actual.product.materialCode, " +
            "   actual.customer.shipCode, " +
            "   chain.name, " +
            "   actual.quantity, " +
            "   actual.actualSalesValue, " +
            "   actual.shipmentType) " +
            "from Actual as actual " +
            "   join Chain as chain " +
            "       on actual.customer.chain = chain " +
            "where " +
            "   chain.name in (:chains) " +
            "   and actual.product.materialCode in (:products)")
    Page<ActualDto> getSalesFactsByChainsAndProducts(@Param("chains") Collection<String> chains,
                                                     @Param("products") Collection<Long> productCodes,
                                                     Pageable pageable);

    @Query(nativeQuery = true, value =
            "SELECT " +
            "    actuals.id AS actualId, " +
            "    CASE " +
            "        WHEN (actuals.actual_sales_value / actuals.quantity) < prices.regular_price_per_unit " +
            "            THEN 'PROMO' " +
            "        ELSE 'REGULAR' " +
            "    END AS shipmentType " +
            "FROM promo.actuals AS actuals " +
            "    INNER JOIN promo.customers AS customers " +
            "        ON actuals.ship_code = customers.ship_code " +
            "    INNER JOIN promo.chains AS chains " +
            "        ON customers.chain_id = chains.id " +
            "    INNER JOIN promo.prices AS prices " +
            "        ON actuals.material_code = prices.material_code " +
            "            AND chains.id = prices.chain_id " +
            "WHERE actuals.id > :currentId AND actuals.shipment_type IS NULL " +
            "ORDER BY " +
            "    actuals.id " +
            "FOR UPDATE " +
            "LIMIT 500")
    List<ActualPromoUpdateView> getActualsForUpdatePromo(@Param("currentId") Long currentId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value =
            "UPDATE promo.actuals " +
            "SET shipment_type = :shipmentType " +
            "WHERE id in (:ids)")
    int updateShipmentType(Collection<Long> ids, String shipmentType);

    @Query(nativeQuery = true, value =
            "SELECT " +
            "    actuals.id - 1 " +
            "FROM promo.actuals AS actuals " +
            "WHERE actuals.shipment_type IS NULL " +
            "ORDER BY " +
            "    actuals.id " +
            "LIMIT 1")
    Optional<Long> getMinIdWherePromoIsEmpty();

    boolean existsActualByShipmentTypeIsNull();

}
