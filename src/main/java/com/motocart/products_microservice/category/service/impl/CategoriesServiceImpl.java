package com.motocart.products_microservice.category.service.impl;

import com.motocart.products_microservice.category.entity.CategoriesEntity;
import com.motocart.products_microservice.category.repository.CategoriesRepository;
import com.motocart.products_microservice.category.service.CategoriesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoriesRepository;

    public CategoriesServiceImpl(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public CategoriesEntity addCategory(String category) {
        CategoriesEntity categoriesEntity = CategoriesEntity.builder().categoryName(category).build();
        return categoriesRepository.save(categoriesEntity);
    }

    @Override
    public List<CategoriesEntity> getCategories() {
        return categoriesRepository.findAll();
    }

    @Override
    public CategoriesEntity getCategory(int categoryId) {
        Optional<CategoriesEntity> optionalCategoryEntity = Optional.ofNullable(categoriesRepository.findById(categoryId));
        return optionalCategoryEntity.orElseThrow(() -> new IllegalArgumentException("Category not found."));
    }

    @Override
    public void deleteCategory(int categoryId) {
        categoriesRepository.deleteById(categoryId);
    }

    @Override
    public CategoriesEntity updateCategory(int categoryId, String categoryName) {
        Optional<CategoriesEntity> optionalCategoryEntity = Optional.ofNullable(categoriesRepository.findById(categoryId));
        if (optionalCategoryEntity.isPresent()) {
            CategoriesEntity categoriesEntity = optionalCategoryEntity.get();
            categoriesEntity.setCategoryName(categoryName);
            return categoriesRepository.save(categoriesEntity);
        }
        throw new IllegalArgumentException("Category does not exist. Create a new category before updating");
    }

}
