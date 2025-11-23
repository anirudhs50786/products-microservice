package com.motocart.products_microservice.service.impl;

import com.motocart.products_microservice.dao.ProductsDao;
import com.motocart.products_microservice.dto.ProductDTO;
import com.motocart.products_microservice.entity.ProductsEntity;
import com.motocart.products_microservice.service.ProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ProductsServiceImpl implements ProductsService {

    private final ProductsDao productsDao;

    public ProductsServiceImpl(ProductsDao productsDao) {
        this.productsDao = productsDao;
    }

    public void addProduct(ProductDTO productDTO) {
        ProductsEntity product = ProductsEntity.builder()
                .name(productDTO.getName())
                .productCode(generateProductCode(productDTO.getFirmName(), productDTO.getName()))
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .build();
        log.debug("saved new product");
        productsDao.save(product);
    }

    public void updateProduct(ProductDTO productDTO) {
        Optional<ProductsEntity> optionalProduct = Optional.ofNullable(productsDao.findByProductCode(productDTO.getProductCode()));
        if (optionalProduct.isPresent()) {
            ProductsEntity product = optionalProduct.get();
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            log.debug("updated products data");
            productsDao.save(product);
            return;
        }
        throw new IllegalArgumentException("Product does not exist. Create a new Product before updating");
    }

    private String generateProductCode(String firmName, String productName) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(firmName, 0, 2);
        buffer.append("x");
        buffer.append(productName);
        return buffer.toString();
    }
}
