package com.motocart.products_microservice.product.service;

import com.motocart.library.common.dto.ProductDTO;
import com.motocart.library.common.dto.response.APIResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductsService {

    APIResponse<ProductDTO> addProduct(ProductDTO productDTO, MultipartFile productImage);

    void updateProduct(ProductDTO productDTO);

    List<ProductDTO> getProductsByName(String product);
}
