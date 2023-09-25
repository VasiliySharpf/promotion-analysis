package net.example.report.promotionanalysis.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "material_code")
    private Long materialCode;

    @Column(name = "material_desc_rus")
    private String materialDescriptionRus;

    @JoinColumn(name = "category_code")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

}
