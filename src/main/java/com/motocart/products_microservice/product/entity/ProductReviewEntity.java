package com.motocart.products_microservice.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_reviews",
        indexes = {
                @Index(name = "idx_product_reviews_product_id", columnList = "product_id")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", updatable = false, nullable = false)
    private int reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductsEntity product;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "comment")
    private String comment;

    @Column(name = "helpful_votes")
    private int helpfulVotes;

    @Column(name = "media_links")
    private String mediaLinks;

    @Column(name = "reviewed_by", nullable = false)
    private String reviewedBy;

    @Column(name = "reviewed_at", nullable = false)
    private LocalDateTime reviewedAt;

    @Column(name = "is_verified")
    private boolean isVerified;
}
