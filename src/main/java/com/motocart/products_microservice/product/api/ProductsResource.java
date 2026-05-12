package com.motocart.products_microservice.product.api;

import com.motocart.library.common.dto.ProductDTO;
import com.motocart.library.common.dto.ProductReviewDTO;
import com.motocart.library.common.dto.request.ProductReviewRequestDTO;
import com.motocart.library.common.dto.response.APIResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductsResource {

    APIResponse<ProductDTO> createProduct(ProductDTO product, MultipartFile productImage) throws Exception;

    ResponseEntity<String> updateProduct(ProductDTO product);

    ResponseEntity<List<ProductDTO>> getProductByName(String productName);

    ResponseEntity<ProductDTO> getProductById(int productId);

    ResponseEntity<String> deleteProduct(int productId);

    ResponseEntity<ProductReviewDTO> addProductReview(ProductReviewDTO productReview);

    ResponseEntity<Page<ProductReviewDTO>> getProductReviews(ProductReviewRequestDTO requestDTO);
}
