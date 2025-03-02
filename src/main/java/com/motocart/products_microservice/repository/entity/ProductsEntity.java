package com.motocart.products_microservice.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class ProductsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "product_code")
    private String productCode;
    private String firmName;
    private String description;
    private double price;
}
