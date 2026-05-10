package com.motocart.products_microservice.product.service.impl;

import com.motocart.library.common.dto.MessageDTO;
import com.motocart.library.common.dto.ProductDTO;
import com.motocart.library.common.dto.ProductReviewDTO;
import com.motocart.library.common.dto.request.ProductReviewRequestDTO;
import com.motocart.library.common.dto.response.APIResponse;
import com.motocart.library.common.types.MessageType;
import com.motocart.library.common.types.Permission;
import com.motocart.library.common.types.ResponseStatus;
import com.motocart.library.security.authentication.EntitlementService;
import com.motocart.products_microservice.cloudinary.service.CloudinaryService;
import com.motocart.products_microservice.product.entity.ProductPriceEntity;
import com.motocart.products_microservice.product.entity.ProductReviewEntity;
import com.motocart.products_microservice.product.entity.ProductsEntity;
import com.motocart.products_microservice.product.repository.ProductPriceRepository;
import com.motocart.products_microservice.product.repository.ProductReviewRepository;
import com.motocart.products_microservice.product.repository.ProductsRepository;
import com.motocart.products_microservice.product.service.ProductsService;
import com.motocart.products_microservice.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ProductsServiceImpl implements ProductsService {

    private final ProductsRepository productsRepository;

    private final CloudinaryService CloudinaryService;

    private final EntitlementService entitlementService;

    private final ProductPriceRepository priceRepository;

    private final ProductRatingCalculatorService ratingCalculatorService;

    private final ProductReviewRepository reviewRepository;

    public ProductsServiceImpl(ProductsRepository productsRepository,
                               CloudinaryService cloudinaryService,
                               EntitlementService entitlementService,
                               ProductPriceRepository priceRepository, ProductRatingCalculatorService ratingCalculatorService, ProductReviewRepository reviewRepository) {
        this.productsRepository = productsRepository;
        this.CloudinaryService = cloudinaryService;
        this.entitlementService = entitlementService;
        this.priceRepository = priceRepository;
        this.ratingCalculatorService = ratingCalculatorService;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public APIResponse<ProductDTO> addProduct(ProductDTO productDTO, MultipartFile productImage) {
        entitlementService.canAccess(Permission.PRODUCTS_CREATE);
        List<MessageDTO> messageDTOS = new ArrayList<>();
        ResponseStatus status = ResponseStatus.SUCCESS;
        ProductsEntity product = MapperUtil.toProductEntity(productDTO);

        if (productImage != null && !productImage.isEmpty()) {
            try {
                Map response = CloudinaryService.uploadFile(productImage);
                product.setImageURL(response.get("url").toString());
                product.setImageId(response.get("public_id").toString());
            } catch (Exception e) {
                log.error("Failed to upload image to cloud: {}", e.getMessage());
                messageDTOS.add(MessageDTO.builder()
                        .message("Failed to upload product image")
                        .type(MessageType.ERROR)
                        .build());
                status = ResponseStatus.PARTIAL;
            }
        }
        productsRepository.save(product);
        log.debug("saved new product");
        return MapperUtil.toProductApiResponse(product, messageDTOS, status);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateProduct(ProductDTO productDTO) {
        entitlementService.canAccess(Permission.PRODUCTS_UPDATE);
        Optional<ProductsEntity> optionalProduct = productsRepository.findByProductId(productDTO.getProductId());
        if (optionalProduct.isPresent()) {
            ProductsEntity product = optionalProduct.get();
            product.setProductName(productDTO.getProductName());
            product.setProductDescription(productDTO.getProductDescription());
            if (hasPriceChanged(product.getProductId(), productDTO.getProductPrice())) {
                updateLatestProductPrice(product, productDTO);
            }
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

    private boolean hasPriceChanged(int productId, BigDecimal newPrice) {
        Optional<ProductPriceEntity> optionalProductPrice = priceRepository.getLatestPriceForProduct(productId);
        ProductPriceEntity latestProductPrice = optionalProductPrice.orElse(null);
        if (latestProductPrice == null || latestProductPrice.getPrice() == null) {
            return true;
        }
        return latestProductPrice.getPrice().compareTo(newPrice) != 0;
    }

    private void updateLatestProductPrice(ProductsEntity product, ProductDTO productDTO) {
        // update previous latest price date
        priceRepository.getLatestPriceForProduct(product.getProductId()).ifPresent(latestProductPrice -> {
            latestProductPrice.setEffectiveTo(LocalDateTime.now());
            priceRepository.save(latestProductPrice);
        });

        // Add new price
        ProductPriceEntity productPriceEntity = ProductPriceEntity.builder()
                .price(productDTO.getProductPrice())
                .product(product)
                .changedBy("ADMIN")
                .effectiveTo(null)
                .changeReason("Product Price Updated")
                .effectiveFrom(LocalDateTime.now())
                .build();
        priceRepository.save(productPriceEntity);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteProduct(int productId) {
        entitlementService.canAccess(Permission.PRODUCTS_DELETE);
        Optional<ProductsEntity> optionalProduct = productsRepository.findByProductId(productId);
        if (optionalProduct.isPresent()) {
            ProductsEntity product = optionalProduct.get();
            product.setArchived(true);
            productsRepository.save(product);
            log.debug("soft deleted product with id {}", productId);
            return;
        }
        log.error("Product with id {} not found for deletion", productId);
        throw new IllegalArgumentException("Product not found");
    }

    @Override
    public ProductReviewDTO addProductReview(ProductReviewDTO productReview) {
        ProductsEntity product = productsRepository.findByProductId(productReview.getProductId()).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        ProductReviewEntity productReviewEntity = MapperUtil.toProductReviewEntity(productReview, product);
        reviewRepository.save(productReviewEntity);
        product.setAverageReviewScore(ratingCalculatorService.determineReviewRating(productReview.getProductId()));
        product.setTotalReviews(ratingCalculatorService.determineTotalReviews(productReview.getProductId()));
        productsRepository.save(product);
        return productReview;
    }

    @Override
    public Page<ProductReviewDTO> getProductReviews(ProductReviewRequestDTO requestDTO) {
        Pageable pageable = PageRequest.of(requestDTO.getPage(),
                requestDTO.getSize(),
                Sort.by(requestDTO.getDirection(), requestDTO.getSortBy()));
        Page<ProductReviewEntity> reviewEntityPage = reviewRepository.findByProductId(requestDTO.getProductId(), pageable);
        return reviewEntityPage.map(MapperUtil::toProductReviewDTO);
    }

}
