package com.motocart.products_microservice.product.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ProductDTO implements Serializable {
    private int productId;
    private String productName;
    private String productCode;
    private String firmName;
    private String productDescription;
    private Long productPrice;
    private String imageUrl;
}
