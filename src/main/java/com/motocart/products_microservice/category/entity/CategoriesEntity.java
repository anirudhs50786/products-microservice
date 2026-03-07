package com.motocart.products_microservice.category.entity;

import com.motocart.products_microservice.product.entity.ProductsEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "product_category")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_Id", updatable = false, nullable = false)
    private int categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "category_desc")
    private String categoryDesc;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    List<ProductsEntity> productsEntity;
}
