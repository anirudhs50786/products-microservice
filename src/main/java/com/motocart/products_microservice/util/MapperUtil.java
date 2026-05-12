package com.motocart.products_microservice.util;

import com.motocart.library.common.dto.CategoriesDTO;
import com.motocart.library.common.dto.MessageDTO;
import com.motocart.library.common.dto.ProductDTO;
import com.motocart.library.common.dto.ProductReviewDTO;
import com.motocart.library.common.dto.response.APIResponse;
import com.motocart.library.common.types.ResponseStatus;
import com.motocart.products_microservice.category.entity.CategoriesEntity;
import com.motocart.products_microservice.product.entity.ProductPriceEntity;
import com.motocart.products_microservice.product.entity.ProductReviewEntity;
import com.motocart.products_microservice.product.entity.ProductsEntity;
import com.motocart.products_microservice.product.service.ProductsService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class MapperUtil {

    public static List<String> mapToStringList(List<CategoriesEntity> productCategoriesEntitiesList) {
        return productCategoriesEntitiesList.stream().map(CategoriesEntity::getCategoryName).toList();
    }

    public static List<ProductDTO> toProductDTOList(List<ProductsEntity> productsEntities, Map<Integer, BigDecimal> productPriceMap) {
        return productsEntities.stream().map(productsEntity -> toProductDTO(productsEntity, productPriceMap)).toList();
    }

    public static ProductDTO toProductDTO(ProductsEntity productsEntity, Map<Integer, BigDecimal> productPriceMap) {
        BigDecimal productPrice = productPriceMap.get(productsEntity.getProductId());
        return ProductDTO.builder()
                .productId(productsEntity.getProductId())
                .productName(productsEntity.getProductName())
                .productDescription(productsEntity.getProductDescription())
                .imageUrl(productsEntity.getImageURL())
                .productPrice(productPrice)
                .imageId(productsEntity.getImageId())
                .firmName(productsEntity.getFirmName())
                .build();
    }

    public static ProductsEntity toProductEntity(ProductDTO productDTO) {
        return ProductsEntity.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .productDescription(productDTO.getProductDescription())
                .category(CategoriesEntity.builder().categoryId(productDTO.getCategoryId()).build())
                .firmName(productDTO.getFirmName())
                .build();
    }

    public static APIResponse<ProductDTO> toProductApiResponse(ProductsEntity productsEntity,
                                                               List<MessageDTO> messageDTOS,
                                                               ResponseStatus responseStatus,
                                                               ProductPriceEntity productPrice) {
        Map<Integer, BigDecimal> productPriceMap = Map.of(productsEntity.getProductId(), productPrice.getPrice());
        return APIResponse.<ProductDTO>builder()
                .data(toProductDTO(productsEntity, productPriceMap))
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

    public static ProductReviewEntity toProductReviewEntity(ProductReviewDTO productReview, ProductsEntity product) {
        String mediaLinks = String.join(ProductsService.MEDIA_LINK_SEPARATOR, productReview.getMediaLinks());
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
}
