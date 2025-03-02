package com.motocart.products_microservice.service;

import com.motocart.products_microservice.dto.ProductsDTO;
import com.motocart.products_microservice.repository.dao.ProductsDao;
import com.motocart.products_microservice.repository.entity.ProductsEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductsService {

    private final ProductsDao productsDao;

    public ProductsService(ProductsDao productsDao) {
        this.productsDao = productsDao;
    }

    public ProductsEntity addProduct(ProductsDTO productsDTO) {
        ProductsEntity product = new ProductsEntity();
        product.setName(productsDTO.getName());
        product.setProductCode(generateProductCode(productsDTO.getFirmName(), productsDTO.getName()));
        product.setDescription(productsDTO.getDescription());
        product.setPrice(productsDTO.getPrice());
        return productsDao.save(product);
    }

    public ProductsEntity updateProduct(ProductsDTO productsDTO) {
        Optional<ProductsEntity> optionalProduct = Optional.ofNullable(productsDao.findByProductCode(productsDTO.getProductCode()));
        if (optionalProduct.isPresent()) {
            ProductsEntity product = optionalProduct.get();
            product.setName(productsDTO.getName());
            product.setDescription(productsDTO.getDescription());
            product.setPrice(productsDTO.getPrice());
            return productsDao.save(product);
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
