package com.motocart.products_microservice.product.repository;

import com.motocart.products_microservice.product.entity.ProductReviewEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository {

    List<ProductReviewEntity> findByProductId(int productId);
}
