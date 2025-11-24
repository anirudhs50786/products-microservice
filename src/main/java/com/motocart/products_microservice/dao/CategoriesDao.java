package com.motocart.products_microservice.dao;

import com.motocart.products_microservice.entity.ProductCategoriesEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriesDao extends JpaRepository<ProductCategoriesEntity, Long> {

    @NonNull
    List<ProductCategoriesEntity> findAll();

    ProductCategoriesEntity findById(int categoryId);

    void deleteById(int categoryId);
}
