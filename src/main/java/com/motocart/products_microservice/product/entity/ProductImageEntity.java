package com.motocart.products_microservice.product.entity;

import com.motocart.library.common.types.ImageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_images")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "image_id", nullable = false)
    private String imageId; // cloudinary public_id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_review_id", nullable = true)
    private ProductReviewEntity productReview; // nullable for product images

    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false)
    private ImageType imageType; // PRODUCT or REVIEW


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}