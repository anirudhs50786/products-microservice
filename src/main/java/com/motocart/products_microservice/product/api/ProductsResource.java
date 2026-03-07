package com.motocart.products_microservice.product.api;

import com.motocart.products_microservice.product.dto.ProductDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductsResource {

    ResponseEntity<String> createProduct(ProductDTO product);

    ResponseEntity<String> updateProduct(ProductDTO product);

    ResponseEntity<List<ProductDTO>> getProductByName(String productName);
}
