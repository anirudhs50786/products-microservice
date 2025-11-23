package com.motocart.products_microservice.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ProductDTO implements Serializable {
    private int id;
    private String name;
    private String productCode;
    private String firmName;
    private String description;
    private Long price;
}
