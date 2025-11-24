package com.motocart.products_microservice.service.impl;

import com.motocart.products_microservice.dao.CategoriesDao;
import com.motocart.products_microservice.entity.ProductCategoriesEntity;
import com.motocart.products_microservice.service.CategoriesService;
import com.motocart.products_microservice.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesDao categoriesDao;

    public CategoriesServiceImpl(CategoriesDao categoriesDao) {
        this.categoriesDao = categoriesDao;
    }

    @Override
    public void addCategory(String category) {
        ProductCategoriesEntity categoriesEntity = ProductCategoriesEntity.builder().categoryName(category).build();
        categoriesDao.save(categoriesEntity);
    }

    @Override
    public List<String> getCategories() {
        List<ProductCategoriesEntity> categoriesEntity = categoriesDao.findAll();
        return MapperUtil.mapToStringList(categoriesEntity);
    }

    @Override
    public String getCategory(int categoryId) {
        Optional<ProductCategoriesEntity> optionalCategoryEntity = Optional.ofNullable(categoriesDao.findById(categoryId));
        return optionalCategoryEntity.orElseThrow(() -> new IllegalArgumentException("Category not found.")).getCategoryName();
    }

    @Override
    public void deleteCategory(int categoryId) {
        categoriesDao.deleteById(categoryId);
    }

    @Override
    public void updateCategory(int categoryId, String categoryName) {
        Optional<ProductCategoriesEntity> optionalCategoryEntity = Optional.ofNullable(categoriesDao.findById(categoryId));
        if (optionalCategoryEntity.isPresent()) {
            ProductCategoriesEntity productCategoriesEntity = optionalCategoryEntity.get();
            productCategoriesEntity.setCategoryName(categoryName);
        }
        throw new IllegalArgumentException("Category does not exist. Create a new category before updating");
    }

}
