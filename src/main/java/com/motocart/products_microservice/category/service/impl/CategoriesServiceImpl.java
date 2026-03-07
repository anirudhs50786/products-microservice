package com.motocart.products_microservice.category.service.impl;

import com.motocart.products_microservice.category.entity.CategoriesEntity;
import com.motocart.products_microservice.category.repository.CategoriesRepository;
import com.motocart.products_microservice.category.service.CategoriesService;
import com.motocart.products_microservice.util.MapperUtil;
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
    public void addCategory(String category) {
        CategoriesEntity categoriesEntity = CategoriesEntity.builder().categoryName(category).build();
        categoriesRepository.save(categoriesEntity);
    }

    @Override
    public List<String> getCategories() {
        List<CategoriesEntity> categoriesEntity = categoriesRepository.findAll();
        return MapperUtil.mapToStringList(categoriesEntity);
    }

    @Override
    public String getCategory(int categoryId) {
        Optional<CategoriesEntity> optionalCategoryEntity = Optional.ofNullable(categoriesRepository.findById(categoryId));
        return optionalCategoryEntity.orElseThrow(() -> new IllegalArgumentException("Category not found.")).getCategoryName();
    }

    @Override
    public void deleteCategory(int categoryId) {
        categoriesRepository.deleteById(categoryId);
    }

    @Override
    public void updateCategory(int categoryId, String categoryName) {
        Optional<CategoriesEntity> optionalCategoryEntity = Optional.ofNullable(categoriesRepository.findById(categoryId));
        if (optionalCategoryEntity.isPresent()) {
            CategoriesEntity categoriesEntity = optionalCategoryEntity.get();
            categoriesEntity.setCategoryName(categoryName);
        }
        throw new IllegalArgumentException("Category does not exist. Create a new category before updating");
    }

}
