package com.motocart.products_microservice.api.rest;

import com.motocart.products_microservice.dto.ProductDTO;
import org.springframework.http.ResponseEntity;

public interface ProductsResource {

    ResponseEntity<String> createProduct(ProductDTO product);

    ResponseEntity<String> updateProduct(ProductDTO product);
}
