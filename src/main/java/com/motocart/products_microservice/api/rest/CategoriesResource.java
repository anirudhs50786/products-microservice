package com.motocart.products_microservice.api.rest;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoriesResource {

    ResponseEntity<String> createCategory(String categoryName);

    ResponseEntity<String> updateCategory(int categoryId, String categoryName);

    ResponseEntity<String> deleteCategory(int categoryId);

    ResponseEntity<List<String>> getCategories();

    ResponseEntity<String> getCategory(int categoryId);
}
