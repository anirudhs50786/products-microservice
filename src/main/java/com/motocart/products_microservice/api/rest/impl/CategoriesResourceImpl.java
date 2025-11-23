package com.motocart.products_microservice.api.rest.impl;

import com.motocart.products_microservice.api.rest.CategoriesResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/categories")
public class CategoriesResourceImpl implements CategoriesResource {

    @PostMapping
    @Override
    public ResponseEntity<String> createCategory(@RequestParam int categoryName) {
        return null;
    }

    @PutMapping
    @Override
    public ResponseEntity<String> updateCategory(@RequestParam int categoryId) {
        return null;
    }

    @DeleteMapping
    @Override
    public ResponseEntity<String> deleteCategory(@RequestParam int categoryId) {
        return null;
    }

    @GetMapping
    @Override
    public ResponseEntity<List<String>> getCategories() {
        return null;
    }

    @GetMapping
    @Override
    public ResponseEntity<String> getCategory(@RequestParam int categoryId) {
        return null;
    }
}
