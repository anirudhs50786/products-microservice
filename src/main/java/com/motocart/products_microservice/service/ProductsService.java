package com.motocart.products_microservice.service;

import com.motocart.products_microservice.dto.ProductsDTO;
import com.motocart.products_microservice.repository.entity.ProductsEntity;

public interface ProductsService {

    ProductsEntity addProduct(ProductsDTO productsDTO);

    ProductsEntity updateProduct(ProductsDTO productsDTO);
}
