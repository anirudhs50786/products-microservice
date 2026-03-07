package com.motocart.products_microservice.category.dto;

import com.motocart.products_microservice.product.dto.ProductDTO;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CategoriesDTO implements Serializable {

    private int categoryId;

    private String categoryName;

    private String categoryDesc;

    @Nullable
    private List<ProductDTO> productDTO;
}
