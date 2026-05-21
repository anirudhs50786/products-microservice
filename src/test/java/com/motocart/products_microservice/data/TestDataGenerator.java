package com.motocart.products_microservice.data;

import com.motocart.library.common.dto.ProductDTO;
import com.motocart.library.common.dto.ProductReviewDTO;
import com.motocart.products_microservice.product.entity.ProductEntity;
import com.motocart.products_microservice.product.entity.ProductPriceEntity;

import java.math.BigDecimal;

public class TestDataGenerator {

    public static ProductDTO getProductDTOBasic() {
        return ProductDTO.builder()
                .productName("Helmet")
                .productPrice(BigDecimal.valueOf(300.0))
                .productDescription("Full face helmet")
                .firmName("MT")
                .productId(2)
                .categoryId(1)
                .build();
    }

    public static ProductEntity getProductEntityBasic() {
        return ProductEntity.builder()
                .productId(1)
                .productName("Helmet")
                .productDescription("Full face helmet")
                .firmName("MT")
                .build();
    }

    public static ProductPriceEntity getProductPriceEntityBasic() {
        return ProductPriceEntity.builder()
                .price(BigDecimal.valueOf(300.0))
                .product(getProductEntityBasic())
                .effectiveFrom(java.time.LocalDateTime.now())
                .changedBy("ADMIN")
                .changeReason("Product Created")
                .build();
    }

    public static ProductReviewDTO getProductReviewDTOBasic() {
        return ProductReviewDTO.builder()
                .productId(1)
                .reviewedBy("John Doe")
                .comment("Great product!")
                .rating(5)
                .build();
    }
}
