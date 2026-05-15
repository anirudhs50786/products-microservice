package com.motocart.products_microservice.category.api;

import com.motocart.library.common.dto.CategoriesDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoriesResource {

    ResponseEntity<CategoriesDTO> createCategory(CategoriesDTO category);

    ResponseEntity<CategoriesDTO> updateCategory(CategoriesDTO requestCategoriesDTO);

    ResponseEntity<String> deleteCategory(int categoryId);

    ResponseEntity<List<CategoriesDTO>> getCategories();

    ResponseEntity<CategoriesDTO> getCategory(int categoryId);
}
