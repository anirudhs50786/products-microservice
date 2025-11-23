package com.motocart.products_microservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
@Builder
public class ProductsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    private String name;
    @Column(name = "product_code")
    private String productCode;
    @Column(name = "category_Id")
    private Long categoryId;
    private String firmName;
    private String description;
    private Long price;
}
