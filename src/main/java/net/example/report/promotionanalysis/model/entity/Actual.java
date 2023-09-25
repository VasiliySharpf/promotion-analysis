package net.example.report.promotionanalysis.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import net.example.report.promotionanalysis.model.enums.ShipmentType;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "actuals")
public class Actual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @JoinColumn(name = "material_code")
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @JoinColumn(name = "ship_code")
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "actual_sales_value")
    private Double actualSalesValue;

    @Column(name = "shipment_type")
    @Enumerated(value = EnumType.STRING)
    private ShipmentType shipmentType;

}
