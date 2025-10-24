package com.motocart.products_microservice.api.rest;

import com.motocart.products_microservice.dto.ProductsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface ProductsResource {

    ResponseEntity<String> createProduct(@RequestBody ProductsDTO product);

    ResponseEntity<String> updateProduct(@RequestBody ProductsDTO product);
}
