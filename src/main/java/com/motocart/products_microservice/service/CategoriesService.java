package com.motocart.products_microservice.service;

import java.util.List;

public interface CategoriesService {

    void addCategory(String category);

    List<String> getCategories();

    String getCategory(int categoryId);

    void deleteCategory(int categoryId);

    void updateCategory(int categoryId, String categoryName);
}
