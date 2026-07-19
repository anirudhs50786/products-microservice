package com.motocart.products_microservice.util;

import com.motocart.library.common.dto.*;
import com.motocart.library.common.dto.response.APIResponse;
import com.motocart.library.common.types.ImageType;
import com.motocart.library.common.types.ResponseStatus;
import com.motocart.products_microservice.category.entity.CategoriesEntity;
import com.motocart.products_microservice.cloudinary.dto.CloudinaryUploadResponseDTO;
import com.motocart.products_microservice.product.entity.ProductEntity;
import com.motocart.products_microservice.product.entity.ProductImageEntity;
import com.motocart.products_microservice.product.entity.ProductPriceEntity;
import com.motocart.products_microservice.product.entity.ProductReviewEntity;
import com.motocart.products_microservice.product.service.ProductsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public final class MapperUtil {

    public static List<String> mapToStringList(List<CategoriesEntity> productCategoriesEntitiesList) {
        return productCategoriesEntitiesList.stream().map(CategoriesEntity::getCategoryName).toList();
    }

    public static List<ProductDTO> toProductDTOList(List<ProductEntity> productsEntities, Map<Integer, BigDecimal> productPriceMap) {
        return productsEntities.stream().map(productsEntity -> toProductDTO(productsEntity, productPriceMap, null)).toList();
    }

    public static ProductDTO toProductDTO(ProductEntity productEntity, Map<Integer, BigDecimal> productPriceMap, List<ProductImageEntity> imageEntities) {
        if (productEntity == null || productPriceMap == null) {
            return null;
        }
        BigDecimal productPrice = productPriceMap.get(productEntity.getProductId());
        return ProductDTO.builder()
                .productId(productEntity.getProductId())
                .productName(productEntity.getProductName())
                .productDescription(productEntity.getProductDescription())
                .images(toProductImageDTOs(imageEntities))
                .productPrice(productPrice)
                .firmName(productEntity.getFirmName())
                .build();
    }

    public static List<ProductImageDTO> toProductImageDTOs(List<ProductImageEntity> productImageEntities) {
        if (productImageEntities == null || productImageEntities.isEmpty()) {
            return null;
        }
        return productImageEntities.stream().map(MapperUtil::toProductImageDTO).toList();
    }

    private static ProductImageDTO toProductImageDTO(ProductImageEntity productImageEntity) {
        ProductImageDTO productImageDTO = new ProductImageDTO();
        productImageDTO.setImageId(productImageEntity.getImageId());
        productImageDTO.setImageUrl(productImageEntity.getImageUrl());
        //productImageDTO.setImageType(productImageEntity.getImageType());
        //productImageDTO.setProductId(productImageEntity.getProduct().getProductId());
        return productImageDTO;
    }

    public static ProductEntity toProductEntity(ProductDTO productDTO) {
        return ProductEntity.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .productDescription(productDTO.getProductDescription())
                .category(CategoriesEntity.builder().categoryId(productDTO.getCategoryId()).build())
                .firmName(productDTO.getFirmName())
                .build();
    }

    public static APIResponse<ProductDTO> toProductApiResponse(ProductEntity productEntity,
                                                               List<MessageDTO> messageDTOS,
                                                               ResponseStatus responseStatus,
                                                               ProductPriceEntity productPrice,
                                                               List<ProductImageEntity> imageEntities) {
        Map<Integer, BigDecimal> productPriceMap = Map.of(productEntity.getProductId(), productPrice.getPrice());
        return APIResponse.<ProductDTO>builder()
                .data(toProductDTO(productEntity, productPriceMap, imageEntities))
                .messages(messageDTOS)
                .status((responseStatus))
                .build();
    }

    public static ProductReviewDTO toProductReviewDTO(ProductReviewEntity productReviewEntity) {
        return ProductReviewDTO.builder()
                .reviewId(productReviewEntity.getReviewId())
                .productId(productReviewEntity.getProduct().getProductId())
                .comment(productReviewEntity.getComment())
                .helpfulVotes(productReviewEntity.getHelpfulVotes())
                .rating(productReviewEntity.getRating())
                .mediaLinks(List.of(productReviewEntity.getMediaLinks().split(ProductsService.MEDIA_LINK_SEPARATOR)))
                .reviewedBy(productReviewEntity.getReviewedBy())
                .reviewedAt(productReviewEntity.getReviewedAt())
                .build();
    }

    public static ProductReviewEntity toProductReviewEntity(ProductReviewDTO productReview, ProductEntity product) {
        String mediaLinks = productReview.getMediaLinks() == null ? null : String.join(ProductsService.MEDIA_LINK_SEPARATOR, productReview.getMediaLinks());
        return ProductReviewEntity.builder()
                .reviewId(productReview.getReviewId())
                .product(product)
                .rating(productReview.getRating())
                .comment(productReview.getComment())
                .helpfulVotes(productReview.getHelpfulVotes())
                .mediaLinks(mediaLinks)
                .reviewedBy(productReview.getReviewedBy())
                .reviewedAt(productReview.getReviewedAt())
                .isVerified(productReview.isVerified())
                .build();
    }

    public static CategoriesDTO toCategoriesDTO(CategoriesEntity categoriesEntity) {
        return CategoriesDTO.builder()
                .categoryId(categoriesEntity.getCategoryId())
                .categoryName(categoriesEntity.getCategoryName())
                .categoryDesc(categoriesEntity.getCategoryDesc())
                .build();
    }

    public static List<CategoriesDTO> toCategoriesDTOList(List<CategoriesEntity> categories) {
        return categories.stream().map(MapperUtil::toCategoriesDTO).toList();
    }

    public static List<ProductImageEntity> toProductImageEntities(List<CloudinaryUploadResponseDTO> uploadResponses, ProductEntity product) {
        return uploadResponses.stream()
                .filter(CloudinaryUploadResponseDTO::isSuccess)
                .map(response -> ProductImageEntity.builder()
                        .imageUrl(response.getImageUrl())
                        .imageId(response.getImageId())
                        .imageType(ImageType.PRODUCT)
                        .product(product)
                        .createdAt(LocalDateTime.now())
                        .build())
                .toList();
    }

    public static ProductPriceEntity toProductPriceEntity(ProductDTO productDTO, ProductEntity product) {
        return ProductPriceEntity.builder()
                .price(productDTO.getProductPrice())
                .effectiveFrom(LocalDateTime.now())
                .changedBy("ADMIN")
                .changeReason("Product Created")
                .product(product)
                .build();
    }
}
