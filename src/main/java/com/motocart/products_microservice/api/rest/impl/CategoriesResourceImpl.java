package com.motocart.products_microservice.api.rest.impl;

import com.motocart.products_microservice.api.rest.CategoriesResource;
import com.motocart.products_microservice.service.CategoriesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/categories")
public class CategoriesResourceImpl implements CategoriesResource {

    private final CategoriesService categoriesService;

    public CategoriesResourceImpl(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @PostMapping
    @Override
    public ResponseEntity<String> createCategory(@RequestParam String categoryName) {
        try {
            categoriesService.addCategory(categoryName);
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (Exception exception) {
            log.error("Error while creating the category. {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PutMapping
    @Override
    public ResponseEntity<String> updateCategory(@RequestParam int categoryId, @RequestParam String categoryName) {
        try {
            categoriesService.updateCategory(categoryId, categoryName);
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (Exception exception) {
            log.error("Error while updating the category. {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @DeleteMapping
    @Override
    public ResponseEntity<String> deleteCategory(@RequestParam int categoryId) {
        try {
            categoriesService.deleteCategory(categoryId);
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (Exception exception) {
            log.error("Error while deleting the category. {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @GetMapping
    @Override
    public ResponseEntity<List<String>> getCategories() {
        try {
            List<String> categories = categoriesService.getCategories();
            return ResponseEntity.status(HttpStatus.OK).body(categories);
        } catch (Exception exception) {
            log.error("Error while fetching the categories. {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    @GetMapping
    @Override
    public ResponseEntity<String> getCategory(@RequestParam int categoryId) {
        try {
            String category = categoriesService.getCategory(categoryId);
            return ResponseEntity.status(HttpStatus.OK).body(category);
        } catch (IllegalArgumentException exception) {
            log.warn("Incorrect category Id given. {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        } catch (Exception exception) {
            log.error("Error while fetching the category for the id. {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }
}
