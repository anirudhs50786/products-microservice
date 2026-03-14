package com.motocart.products_microservice.product.api.impl;

import com.motocart.library.common.dto.ProductDTO;
import com.motocart.products_microservice.product.api.ProductsResource;
import com.motocart.products_microservice.product.service.ProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
            log.error("Error while updating the product. {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update and save Product");
        }
    }

    @GetMapping(name = "/{productName}", produces = "application/json")
    @Override
    public ResponseEntity<List<ProductDTO>> getProductByName(@PathVariable String productName) {
        if (productName == null) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Product Name supplied");
        }
        try {
            List<ProductDTO> productDTOS = productsService.getProductsByName(productName);
            return ResponseEntity.status(HttpStatus.OK).body(productDTOS);
        } catch (Exception exception) {
            log.error("Error while fetching the product by name. {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
