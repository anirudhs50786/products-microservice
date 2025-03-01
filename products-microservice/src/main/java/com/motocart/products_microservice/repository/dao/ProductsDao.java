package com.motocart.products_microservice.repository.dao;

import com.motocart.products_microservice.repository.entity.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsDao extends JpaRepository<ProductsEntity, Long> {
    ProductsEntity findByName(String name);
}
