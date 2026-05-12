package com.motocart.products_microservice.product.api.impl;

import com.motocart.library.common.dto.ProductDTO;
import com.motocart.library.common.dto.ProductReviewDTO;
import com.motocart.library.common.dto.request.ProductReviewRequestDTO;
import com.motocart.library.common.dto.response.APIResponse;
import com.motocart.library.common.exception.GlobalException;
import com.motocart.products_microservice.product.api.ProductsResource;
import com.motocart.products_microservice.product.service.ProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/products")
@Slf4j
public class ProductsResourceImpl implements ProductsResource {

    private final ProductsService productsService;

    public ProductsResourceImpl(ProductsService productsService) {
        this.productsService = productsService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    @Override
    public APIResponse<ProductDTO> createProduct(@RequestPart ProductDTO product, @RequestPart(required = false) MultipartFile productImage) {
        try {
            return productsService.addProduct(product, productImage);
        } catch (Exception exception) {
            log.error("Error while creating the product. {}", exception.getMessage());
            throw new GlobalException("Error while creating the product", exception);
        }
    }

    @PutMapping(produces = "application/json")
    @Override
    public ResponseEntity<String> updateProduct(@RequestBody ProductDTO product) {
        if (product == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Product data supplied");
        }
        try {
            productsService.updateProduct(product);
            return ResponseEntity.status(HttpStatus.OK).body("Request Success");
        } catch (Exception exception) {
            log.error("Error while updating the product. {}", exception.getMessage());
            throw new GlobalException("Error while updating the product", exception);
        }
    }

    @GetMapping(path = "/{productName}", produces = "application/json")
    @Override
    public ResponseEntity<List<ProductDTO>> getProductByName(@PathVariable String productName) {
        if (productName == null) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Product Name supplied");
        }
        try {
            List<ProductDTO> productDTOS = productsService.getProductsByName(productName);
            return ResponseEntity.status(HttpStatus.OK).body(productDTOS);
        } catch (Exception exception) {
            throw new GlobalException("Error while fetching the product by name: " + productName, exception);
        }
    }

    @GetMapping(path = "/{productId}", produces = "application/json")
    @Override
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int productId) {
        if (productId == 0) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Product Name supplied");
        }
        try {
            ProductDTO productDTO = productsService.getProductsById(productId);
            return ResponseEntity.status(HttpStatus.OK).body(productDTO);
        } catch (Exception exception) {
            log.error("Error while fetching the product by product Id. {}", exception.getMessage());
            throw new GlobalException("Error while fetching the product by product Id", exception);
        }
    }

    @DeleteMapping(path = "/{productId}", produces = "application/json")
    @Override
    public ResponseEntity<String> deleteProduct(@PathVariable int productId) {
        try {
            productsService.deleteProduct(productId);
            return ResponseEntity.status(HttpStatus.OK).body("Product deleted");
        } catch (IllegalArgumentException exception) {
            log.warn("Product not found for deletion. {}", exception.getMessage());
            throw new GlobalException("Product not found for deletion", exception);
        } catch (Exception exception) {
            log.error("Error while deleting the product. {}", exception.getMessage());
            throw new GlobalException("Error while deleting the product", exception);
        }
    }

    @PostMapping(path = "/review", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    @Override
    public ResponseEntity<ProductReviewDTO> addProductReview(@RequestBody ProductReviewDTO productReview) {
        try {
            ProductReviewDTO productReviewDTO = productsService.addProductReview(productReview);
            return ResponseEntity.status(HttpStatus.OK).body(productReviewDTO);
        } catch (Exception exception) {
            log.error("Error while adding review for the product. {}", exception.getMessage());
            throw new GlobalException("Error while adding review for the product", exception);
        }
    }

    @GetMapping(path = "/review", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    @Override
    public ResponseEntity<Page<ProductReviewDTO>> getProductReviews(@RequestBody ProductReviewRequestDTO requestDTO) {
        try {
            Page<ProductReviewDTO> productReviewDTO = productsService.getProductReviews(requestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(productReviewDTO);
        } catch (Exception exception) {
            log.error("Error while getting review for the product. {}", exception.getMessage());
            throw new GlobalException("Error while getting review for the product", exception);
        }
    }
}
