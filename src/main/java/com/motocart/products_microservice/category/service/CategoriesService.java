package com.motocart.products_microservice.category.service;

import com.motocart.products_microservice.category.entity.CategoriesEntity;

import java.util.List;

public interface CategoriesService {

    CategoriesEntity addCategory(String category);

    List<CategoriesEntity> getCategories();

    CategoriesEntity getCategory(int categoryId);

    void deleteCategory(int categoryId);

    CategoriesEntity updateCategory(int categoryId, String categoryName);
}
