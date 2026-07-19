package com.motocart.products_microservice.product.repository;

import com.motocart.library.common.types.ImageType;
import com.motocart.products_microservice.product.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
    // Product images
    List<ProductImageEntity> findByProductProductIdAndImageType(int productId, ImageType imageType);

    // Review images
    List<ProductImageEntity> findByProductReviewIdAndImageType(Long reviewId, ImageType imageType);

    // General queries
    List<ProductImageEntity> findByProductProductId(int productId);

    List<ProductImageEntity> findByProductReviewId(Long reviewId);

    // Cleanup
    void deleteByProductProductId(int productId);

    void deleteByProductReviewId(Long reviewId);
}