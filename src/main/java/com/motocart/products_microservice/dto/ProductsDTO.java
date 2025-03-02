package com.motocart.products_microservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductsDTO {
    private String name;
    private String productCode;
    private String firmName;
    private String description;
    private Long price;
}
