package com.motocart.products_microservice.product.service.impl;

import com.motocart.library.common.util.TextUtil;
import com.motocart.products_microservice.product.entity.ProductReviewEntity;
import com.motocart.products_microservice.product.repository.ProductReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductRatingCalculatorService {

    private final ProductReviewRepository productReviewRepository;

    // Global platform average
    // Ideally fetched from analytics/cache/config
    private static final double GLOBAL_AVERAGE_RATING = 4.1;

    // Minimum reviews before full confidence
    private static final int MINIMUM_REVIEWS_THRESHOLD = 50;

    public ProductRatingCalculatorService(ProductReviewRepository productReviewRepository) {
        this.productReviewRepository = productReviewRepository;
    }

    public double determineReviewRating(int productId) {
        List<ProductReviewEntity> reviews = productReviewRepository.findByProductId(productId);
        if (reviews.isEmpty()) {
            return 0.0;
        }

        double weightedRatingSum = 0.0;
        double totalWeight = 0.0;

        for (ProductReviewEntity review : reviews) {
            double weight = calculateWeight(review);
            weightedRatingSum += review.getRating() * weight;
            totalWeight += weight;
        }

        // Weighted average
        double weightedAverage = weightedRatingSum / totalWeight;

        // Bayesian confidence adjustment
        int reviewCount = reviews.size();
        double finalScore = ((double) reviewCount / (reviewCount + MINIMUM_REVIEWS_THRESHOLD)) * weightedAverage +
                ((double) MINIMUM_REVIEWS_THRESHOLD / (reviewCount + MINIMUM_REVIEWS_THRESHOLD)) * GLOBAL_AVERAGE_RATING;

        // Round to 1 decimal place
        return Math.round(finalScore * 10.0) / 10.0;
    }

    private static double calculateWeight(ProductReviewEntity review) {
        double weight = 1.0;
        if (review.isVerified()) {
            weight += 0.3;
        }
        if (review.getComment() != null && !review.getComment().trim().isEmpty()) {
            weight += 0.1;
        }
        if (TextUtil.isNotBlank(review.getMediaLinks())) {
            weight += 0.1;
        }
        // Prevent excessive influence
        return Math.min(weight, 1.5);
    }

    public int determineTotalReviews(int productId) {
        return productReviewRepository.findByProductId(productId).size();
    }
}
