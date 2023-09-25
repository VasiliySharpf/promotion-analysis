package net.example.report.promotionanalysis.repository;

import net.example.report.promotionanalysis.model.entity.Price;
import net.example.report.promotionanalysis.repository.projection.PriceView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    @Query(value =
            "select " +
            "   chain.name as chainName, " +
            "   price.product.materialCode as materialCode, " +
            "   price.regularPricePerUnit as regularPricePerUnit " +
            "from Price price " +
            "   join price.chain as chain " +
            "where price.product.materialCode = ?1 and chain.name = ?2")
    Optional<PriceView> findByProductCodeAndChainName(Long materialCode, String chainName);

    @Query(value =
            "select " +
            "   chain.name as chainName, " +
            "   price.product.materialCode as materialCode, " +
            "   price.regularPricePerUnit as regularPricePerUnit " +
            "from Price price " +
            "   join price.chain as chain " +
            "where chain.name = :name")
    List<PriceView> findAllByChainName(@Param("name") String chainName);

    @Query(value =
            "select " +
            "   chain.name as chainName, " +
            "   price.product.materialCode as materialCode, " +
            "   price.regularPricePerUnit as regularPricePerUnit " +
            "from Price price " +
            "   join price.chain as chain " +
            "where price.product.materialCode = ?1")
    List<PriceView> findAllByMaterialCode(Long materialCode);

    @Query(value =
            "select " +
            "   chain.name as chainName, " +
            "   price.product.materialCode as materialCode, " +
            "   price.regularPricePerUnit as regularPricePerUnit " +
            "from Price price " +
            "   join price.chain as chain")
    Page<PriceView> findAllPrices(Pageable pageable);

    @Modifying
    @Query(value =
            "update Price p " +
            "set p.regularPricePerUnit = :pricePerUnit " +
            "where p.product.materialCode = :materialCode" +
            "       and p.chain.name = :chainName ")
    int updatePrice(@Param("materialCode") Long materialCode,
                    @Param("chainName") String chainName,
                    @Param("pricePerUnit") Double pricePerUnit);


    @Modifying
    @Query(value =
            "delete from Price p " +
            "where p.product.materialCode = :materialCode" +
             "       and p.chain.name = :chainName ")
    int deletePrice(Long materialCode, String chainName);

}
