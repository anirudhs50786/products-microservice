package com.motocart.products_microservice.product.repository;

import com.motocart.products_microservice.product.entity.ProductReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReviewEntity, Integer> {

    List<ProductReviewEntity> findByProductId(int productId);

    Page<ProductReviewEntity> findByProductId(int productId, Pageable pageable);
}
