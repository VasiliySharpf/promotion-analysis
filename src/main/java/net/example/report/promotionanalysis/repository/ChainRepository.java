package net.example.report.promotionanalysis.repository;

import net.example.report.promotionanalysis.model.entity.Chain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChainRepository extends JpaRepository<Chain, Integer> {

    Optional<Chain> findByName(String name);

}
