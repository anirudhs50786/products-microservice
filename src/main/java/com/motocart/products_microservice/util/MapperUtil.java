package com.motocart.products_microservice.util;

import com.motocart.products_microservice.entity.ProductCategoriesEntity;

import java.util.List;

public class MapperUtil {

    public static List<String> mapToStringList(List<ProductCategoriesEntity> productCategoriesEntitiesList) {
        return productCategoriesEntitiesList.stream().map(ProductCategoriesEntity::getCategoryName).toList();
    }
}
