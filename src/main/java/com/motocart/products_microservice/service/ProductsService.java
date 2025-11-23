package com.motocart.products_microservice.service;

import com.motocart.products_microservice.dto.ProductDTO;

public interface ProductsService {

    void addProduct(ProductDTO productDTO);

    void updateProduct(ProductDTO productDTO);
}
