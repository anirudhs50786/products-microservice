package com.motocart.products_microservice.product.service.impl;

import com.motocart.products_microservice.product.dto.ProductDTO;
import com.motocart.products_microservice.product.entity.ProductsEntity;
import com.motocart.products_microservice.product.repository.ProductsRepository;
import com.motocart.products_microservice.product.service.ProductsService;
import com.motocart.products_microservice.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductsServiceImpl implements ProductsService {

    private final ProductsRepository productsRepository;

    public ProductsServiceImpl(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @Override
    public void addProduct(ProductDTO productDTO) {
        ProductsEntity product = ProductsEntity.builder()
                .productName(productDTO.getProductName())
                .productCode(generateProductCode(productDTO.getFirmName(), productDTO.getProductName()))
                .productDescription(productDTO.getProductDescription())
                .productPrice(productDTO.getProductPrice())
                .build();
        log.debug("saved new product");
        productsRepository.save(product);
    }

    @Override
    public void updateProduct(ProductDTO productDTO) {
        Optional<ProductsEntity> optionalProduct = Optional.ofNullable(productsRepository.findByProductId(productDTO.getProductId()));
        if (optionalProduct.isPresent()) {
            ProductsEntity product = optionalProduct.get();
            product.setProductName(productDTO.getProductName());
            product.setProductDescription(productDTO.getProductDescription());
            product.setProductPrice(productDTO.getProductPrice());
            log.debug("updated products data");
            productsRepository.save(product);
            return;
        }
        log.error("Product does not exist. Create a new Product before updating");
        throw new IllegalArgumentException("Product does not exist. Create a new Product before updating");
    }

    @Override
    public List<ProductDTO> getProductsByName(String product) {
        return MapperUtil.toProductDTOList(productsRepository.findByName(product));
    }

    private String generateProductCode(String firmName, String productName) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(firmName, 0, 2);
        buffer.append("x");
        buffer.append(productName);
        return buffer.toString();
    }
}
