package com.motocart.products_microservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "product_category")
@Data
@Builder
public class ProductCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_Id", updatable = false, nullable = false)
    private int categoryId;
    @Column(name = "category_Id")
    private String categoryName;
}
