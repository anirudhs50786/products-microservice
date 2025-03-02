package com.motocart.products_microservice.service;

import com.motocart.products_microservice.dto.ProductsDTO;

public interface ProductsService {

    void addProduct(ProductsDTO productsDTO);

    void updateProduct(ProductsDTO productsDTO);
}
