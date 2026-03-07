package com.motocart.products_microservice.product.service;

import com.motocart.products_microservice.product.dto.ProductDTO;

import java.util.List;

public interface ProductsService {

    void addProduct(ProductDTO productDTO);

    void updateProduct(ProductDTO productDTO);

    List<ProductDTO> getProductsByName(String product);
}
