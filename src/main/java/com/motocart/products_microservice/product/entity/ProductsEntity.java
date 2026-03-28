package com.motocart.products_microservice.product.entity;

import com.motocart.products_microservice.category.entity.CategoriesEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", updatable = false, nullable = false)
    private int productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "firm_name")
    private String firmName;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "product_price")
    private Long productPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoriesEntity category;

    @Column(name = "image_url")
    private String imageURL;

    @Column(name = "image_id")
    private String imageId;

    @Column(name = "is_archived")
    private boolean isArchived;


}
