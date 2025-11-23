package com.motocart.products_microservice.api.rest.impl;

import com.motocart.products_microservice.api.rest.ProductsResource;
import com.motocart.products_microservice.dto.ProductDTO;
import com.motocart.products_microservice.service.ProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/products")
@Slf4j
public class ProductsResourceImpl implements ProductsResource {

    private final ProductsService productsService;

    public ProductsResourceImpl(ProductsService productsService) {
        this.productsService = productsService;
    }

    @PostMapping(produces = "application/json")
    @Override
    public ResponseEntity<String> createProduct(@RequestBody ProductDTO product) {
        if (product == null) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Product data supplied");
        }
        try {
            productsService.addProduct(product);
            return ResponseEntity.status(HttpStatus.OK).body("Request Success");
        } catch (Exception exception) {
            log.error("Error while creating the product. {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create and save Product");
        }
    }

    @PutMapping(produces = "application/json")
    @Override
    public ResponseEntity<String> updateProduct(@RequestBody ProductDTO product) {
        if (product == null) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Product data supplied");
        }
        try {
            productsService.updateProduct(product);
            return ResponseEntity.status(HttpStatus.OK).body("Request Success");
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update and save Product");
        }
    }
}
