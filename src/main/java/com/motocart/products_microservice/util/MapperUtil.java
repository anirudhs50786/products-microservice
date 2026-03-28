package com.motocart.products_microservice.util;

import com.motocart.library.common.dto.MessageDTO;
import com.motocart.library.common.dto.ProductDTO;
import com.motocart.library.common.dto.response.APIResponse;
import com.motocart.library.common.types.ResponseStatus;
import com.motocart.products_microservice.category.entity.CategoriesEntity;
import com.motocart.products_microservice.product.entity.ProductsEntity;

import java.util.List;

public final class MapperUtil {

    public static List<String> mapToStringList(List<CategoriesEntity> productCategoriesEntitiesList) {
        return productCategoriesEntitiesList.stream().map(CategoriesEntity::getCategoryName).toList();
    }

    public static List<ProductDTO> toProductDTOList(List<ProductsEntity> productsEntities) {
        return productsEntities.stream().map(MapperUtil::toProductDTO).toList();
    }

    private static ProductDTO toProductDTO(ProductsEntity productsEntity) {
        return ProductDTO.builder()
                .productId(productsEntity.getProductId())
                .productName(productsEntity.getProductName())
                .productDescription(productsEntity.getProductDescription())
                .productPrice(productsEntity.getProductPrice())
                .imageUrl(productsEntity.getImageURL())
                .imageId(productsEntity.getImageId())
                .firmName(productsEntity.getFirmName())
                .build();
    }

    public static ProductsEntity toProductEntity(ProductDTO productDTO) {
        return ProductsEntity.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .productDescription(productDTO.getProductDescription())
                .productPrice(productDTO.getProductPrice())
                .firmName(productDTO.getFirmName())
                .build();
    }

    public static APIResponse<ProductDTO> toProductApiResponse(ProductsEntity productsEntity,
                                                               List<MessageDTO> messageDTOS,
                                                               ResponseStatus responseStatus) {
        return APIResponse.<ProductDTO>builder()
                .data(toProductDTO(productsEntity))
                .messages(messageDTOS)
                .status((responseStatus))
                .build();
    }

}
