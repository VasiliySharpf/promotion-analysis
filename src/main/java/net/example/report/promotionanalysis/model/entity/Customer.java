package net.example.report.promotionanalysis.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "ship_code")
    private Long shipCode;

    @Column(name = "ship_name")
    private String shipName;

    @JoinColumn(name = "chain_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Chain chain;

}
