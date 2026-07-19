package com.motocart.products_microservice.product.service.impl;

import com.motocart.library.common.dto.MessageDTO;
import com.motocart.library.common.dto.ProductDTO;
import com.motocart.library.common.dto.ProductReviewDTO;
import com.motocart.library.common.dto.request.ProductReviewRequestDTO;
import com.motocart.library.common.dto.response.APIResponse;
import com.motocart.library.common.types.ImageType;
import com.motocart.library.common.types.MessageType;
import com.motocart.library.common.types.Permission;
import com.motocart.library.common.types.ResponseStatus;
import com.motocart.library.security.authorization.EntitlementService;
import com.motocart.products_microservice.audit.AuditEventBuilder;
import com.motocart.products_microservice.category.entity.CategoriesEntity;
import com.motocart.products_microservice.cloudinary.dto.CloudinaryUploadResponseDTO;
import com.motocart.products_microservice.cloudinary.service.CloudinaryService;
import com.motocart.products_microservice.product.entity.ProductEntity;
import com.motocart.products_microservice.product.entity.ProductImageEntity;
import com.motocart.products_microservice.product.entity.ProductPriceEntity;
import com.motocart.products_microservice.product.entity.ProductReviewEntity;
import com.motocart.products_microservice.product.repository.ProductImageRepository;
import com.motocart.products_microservice.product.repository.ProductPriceRepository;
import com.motocart.products_microservice.product.repository.ProductReviewRepository;
import com.motocart.products_microservice.product.repository.ProductsRepository;
import com.motocart.products_microservice.product.service.ProductsService;
import com.motocart.products_microservice.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductsServiceImpl implements ProductsService {

    private final ProductsRepository productsRepository;

    private final CloudinaryService CloudinaryService;

    private final EntitlementService entitlementService;

    private final ProductPriceRepository priceRepository;

    private final ProductRatingCalculatorService ratingCalculatorService;

    private final ProductReviewRepository reviewRepository;

    private final AuditEventBuilder auditEventBuilder;

    private final ProductImageRepository productImageRepository;

    List<String> allowedTypes = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public APIResponse<ProductDTO> addProduct(ProductDTO productDTO, MultipartFile[] productImages) {
        entitlementService.canAccess(Permission.PRODUCTS_CREATE);
        validateAddProductRequestDTO(productDTO, productImages);

        // Upload images in parallel via CloudinaryService
        List<CloudinaryUploadResponseDTO> uploadResponses = new ArrayList<>();
        List<MessageDTO> messageDTOS = new ArrayList<>();
        ResponseStatus status = processImageUpload(uploadResponses, productImages, messageDTOS);

        // Save product first
        ProductEntity product = MapperUtil.toProductEntity(productDTO);
        productsRepository.save(product);

        // Save successful image uploads
        List<ProductImageEntity> imagesToSave = MapperUtil.toProductImageEntities(uploadResponses, product);
        if (!imagesToSave.isEmpty()) {
            productImageRepository.saveAll(imagesToSave);
        }

        // Save price
        ProductPriceEntity productPrice = MapperUtil.toProductPriceEntity(productDTO, product);
        priceRepository.save(productPrice);

        log.debug("saved new product with {} images", imagesToSave.size());
        auditEventBuilder.publishAddProductAuditEvent(product);

        return MapperUtil.toProductApiResponse(product, messageDTOS, status, productPrice, imagesToSave);
    }

    private void validateAddProductRequestDTO(ProductDTO productDTO, @Nullable MultipartFile[] productImages) {
        if (productDTO == null) {
            throw new IllegalArgumentException("Product data cannot be null");
        }
        if (productDTO.getProductPrice() == null || BigDecimal.ZERO.equals(productDTO.getProductPrice())) {
            throw new IllegalArgumentException("Product price cannot be null or zero");
        }
        if (productDTO.getCategoryId() <= 0) {
            throw new IllegalArgumentException("Product category Id cannot be null or zero");
        }
        if (productDTO.getProductName() == null || productDTO.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (productDTO.getFirmName() == null || productDTO.getFirmName().trim().isEmpty()) {
            throw new IllegalArgumentException("Firm name cannot be null or empty");
        }
        if (productDTO.getProductDescription() == null || productDTO.getProductDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Product description cannot be null or empty");
        }
        if (productImages == null) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        List<MultipartFile> invalidImages = Arrays.stream(productImages).filter(image -> !allowedTypes.contains(image.getContentType())).toList();
        if (!invalidImages.isEmpty()) {
            throw new IllegalArgumentException("Invalid file type uploaded for file(s) {}" + invalidImages.stream().map(MultipartFile::getOriginalFilename).collect(Collectors.joining(", ")));
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateProduct(ProductDTO productDTO) {
        entitlementService.canAccess(Permission.PRODUCTS_UPDATE);
        CategoriesEntity category = CategoriesEntity.builder().categoryId(productDTO.getCategoryId()).build();
        Optional<ProductEntity> optionalProduct = productsRepository.findByProductId(productDTO.getProductId());
        if (optionalProduct.isPresent()) {
            ProductEntity product = optionalProduct.get();
            ProductEntity productOriginal = new ProductEntity(product);
            product.setProductName(productDTO.getProductName());
            product.setProductDescription(productDTO.getProductDescription());
            product.setCategory(category);
            if (hasPriceChanged(product.getProductId(), productDTO.getProductPrice())) {
                updateLatestProductPrice(product, productDTO);
            }
            log.debug("updated products data");
            auditEventBuilder.publishUpdateProductAuditEvent(productOriginal, product);
            productsRepository.save(product);
            return;
        }
        log.error("Product does not exist. Create a new Product before updating");
        throw new IllegalArgumentException("Product does not exist. Create a new Product before updating");
    }

    @Override
    public List<ProductDTO> getProductsByName(String product) {
        List<ProductEntity> productsEntities = productsRepository.findByName(product);
        List<Integer> productIds = productsEntities.stream().map(ProductEntity::getProductId).toList();
        Map<Integer, BigDecimal> productPriceMap = priceRepository.getLatestPriceForProductList(productIds).stream().collect(Collectors.toMap(price -> price.getProduct().getProductId(), ProductPriceEntity::getPrice));

        return MapperUtil.toProductDTOList(productsEntities, productPriceMap);
    }

    @Override
    public ProductDTO getProductsById(int productId) {
        ProductEntity product = productsRepository.findByProductId(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        BigDecimal price = priceRepository.getLatestPriceForProduct(productId).map(ProductPriceEntity::getPrice).orElse(BigDecimal.ZERO);
        List<ProductImageEntity> productImageEntities = productImageRepository.findByProductProductIdAndImageType(productId, ImageType.PRODUCT);
        return MapperUtil.toProductDTO(product, Map.of(productId, price), productImageEntities);
    }

    private boolean hasPriceChanged(int productId, BigDecimal newPrice) {
        Optional<ProductPriceEntity> optionalProductPrice = priceRepository.getLatestPriceForProduct(productId);
        ProductPriceEntity latestProductPrice = optionalProductPrice.orElse(null);
        if (latestProductPrice == null || latestProductPrice.getPrice() == null) {
            return true;
        }
        return latestProductPrice.getPrice().compareTo(newPrice) != 0;
    }

    private void updateLatestProductPrice(ProductEntity product, ProductDTO productDTO) {
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
        Optional<ProductEntity> optionalProduct = productsRepository.findByProductId(productId);
        if (optionalProduct.isPresent()) {
            ProductEntity product = optionalProduct.get();
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
        ProductEntity product = productsRepository.findByProductId(productReview.getProductId()).orElseThrow(() -> new IllegalArgumentException("Product not found"));
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

    private ResponseStatus processImageUpload(List<CloudinaryUploadResponseDTO> uploadResponses, MultipartFile[] productImages, List<MessageDTO> messageDTOS) {
        ResponseStatus status = ResponseStatus.SUCCESS;
        if (productImages != null && productImages.length > 0) {
            uploadResponses.addAll(CloudinaryService.uploadFilesInParallel(productImages));

            // Track failures
            List<CloudinaryUploadResponseDTO> failedUploads = uploadResponses.stream()
                    .filter(r -> !r.isSuccess())
                    .toList();

            if (!failedUploads.isEmpty()) {
                status = ResponseStatus.PARTIAL;
                failedUploads.forEach(failed -> messageDTOS.add(MessageDTO.builder()
                        .message("Failed to upload: " + failed.getOriginalFileName() + " - " + failed.getErrorMessage())
                        .type(MessageType.ERROR)
                        .build()));
            }
        }
        return status;
    }

}
