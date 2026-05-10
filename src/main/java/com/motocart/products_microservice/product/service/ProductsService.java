package com.motocart.products_microservice.product.service;

import com.motocart.library.common.dto.ProductDTO;
import com.motocart.library.common.dto.ProductReviewDTO;
import com.motocart.library.common.dto.request.ProductReviewRequestDTO;
import com.motocart.library.common.dto.response.APIResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductsService {

    String MEDIA_LINK_SEPARATOR = ",";

    APIResponse<ProductDTO> addProduct(ProductDTO productDTO, MultipartFile productImage);

    void updateProduct(ProductDTO productDTO);

    List<ProductDTO> getProductsByName(String product);

    void deleteProduct(int productId);

    ProductReviewDTO addProductReview(ProductReviewDTO productReview);

    Page<ProductReviewDTO> getProductReviews(ProductReviewRequestDTO requestDTO);
}
