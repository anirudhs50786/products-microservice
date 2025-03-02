package com.motocart.products_microservice.events;

import com.motocart.products_microservice.dto.ProductsDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductsEvent {
    private String eventType;
    private String correlationId;
    private ProductsDTO productsDTO;
}
