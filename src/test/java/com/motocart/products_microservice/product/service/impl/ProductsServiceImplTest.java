package com.motocart.products_microservice.product.service.impl;

import com.motocart.library.common.dto.ProductDTO;
import com.motocart.library.common.dto.ProductReviewDTO;
import com.motocart.library.common.dto.response.APIResponse;
import com.motocart.library.common.types.Permission;
import com.motocart.library.common.types.ResponseStatus;
import com.motocart.library.security.authentication.EntitlementService;
import com.motocart.products_microservice.audit.AuditEventBuilder;
import com.motocart.products_microservice.cloudinary.service.CloudinaryService;
import com.motocart.products_microservice.data.TestDataGenerator;
import com.motocart.products_microservice.product.entity.ProductEntity;
import com.motocart.products_microservice.product.entity.ProductPriceEntity;
import com.motocart.products_microservice.product.repository.ProductPriceRepository;
import com.motocart.products_microservice.product.repository.ProductReviewRepository;
import com.motocart.products_microservice.product.repository.ProductsRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsServiceImplTest {

    // Mock classes
    @Mock
    private ProductsRepository productsRepository;

    @Mock
    private EntitlementService entitlementService;

    @Mock
    private ProductPriceRepository priceRepository;

    @Mock
    private ProductReviewRepository productReviewRepository;

    @Mock
    private AuditEventBuilder auditEventBuilder;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private ProductRatingCalculatorService ratingCalculatorService;

    // Class being tested
    @InjectMocks
    private ProductsServiceImpl productsService;

    // Test Data objects
    private static ProductDTO productDTO;
    private static ProductEntity productEntity;
    private static ProductPriceEntity priceEntity;
    private static ProductReviewDTO productReviewDTO;

    @BeforeAll
    static void setUp() {
        productDTO = TestDataGenerator.getProductDTOBasic();
        productEntity = TestDataGenerator.getProductEntityBasic();
        priceEntity = TestDataGenerator.getProductPriceEntityBasic();
        productReviewDTO = TestDataGenerator.getProductReviewDTOBasic();
    }

    @Test
    void addProduct_withImage_success() {
        // Arrange
        MockMultipartFile image = new MockMultipartFile("image", "helmet.jpg", "image/jpeg", "image".getBytes());
        when(cloudinaryService.uploadFile(image)).thenReturn(Map.of("url", "http://cloudinary.com/helmet.jpg", "public_id", "helmet_123"));
        when(productsRepository.save(any())).thenReturn(productEntity);
        doNothing().when(entitlementService).canAccess(Permission.PRODUCTS_CREATE);
        when(priceRepository.save(any())).thenReturn(priceEntity);
        doNothing().when(auditEventBuilder).publishAddProductAuditEvent(any());

        // Act
        APIResponse<ProductDTO> response = productsService.addProduct(productDTO, image);

        // Assert
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
        verify(cloudinaryService).uploadFile(image);
        verify(productsRepository).save(any());
        verify(priceRepository).save(any());
        verify(auditEventBuilder).publishAddProductAuditEvent(any());
        verify(entitlementService).canAccess(Permission.PRODUCTS_CREATE);
        assertNotNull(response.getData());
    }

    @Test
    void addProduct_imageUploadFails_returnsPartialResponse() {
        // Arrange
        MockMultipartFile image = new MockMultipartFile("image", "helmet.jpg", "image/jpeg", "image".getBytes());
        when(cloudinaryService.uploadFile(image)).thenThrow(new RuntimeException("Upload failed"));
        when(productsRepository.save(any())).thenReturn(productEntity);
        doNothing().when(entitlementService).canAccess(Permission.PRODUCTS_CREATE);

        // Act
        APIResponse<ProductDTO> response = productsService.addProduct(productDTO, image);

        // Assert
        assertEquals(ResponseStatus.PARTIAL, response.getStatus());
        assertFalse(response.getMessages().isEmpty());
        verify(productsRepository).save(any());
        verify(entitlementService).canAccess(Permission.PRODUCTS_CREATE);
    }

    @Test
    void addProduct_withoutImage_success() {
        when(productsRepository.save(any())).thenReturn(productEntity);

        APIResponse<ProductDTO> response = productsService.addProduct(productDTO, null);

        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
        verify(cloudinaryService, never()).uploadFile(any());
        verify(productsRepository).save(any());
    }

    @Test
    void updateProduct_productExists_success() {
        when(productsRepository.findByProductId(productDTO.getProductId())).thenReturn(Optional.of(productEntity));

        assertDoesNotThrow(() -> productsService.updateProduct(productDTO));
        verify(productsRepository).save(productEntity);
    }

    @Test
    void updateProduct_productNotFound_throwsException() {
        when(productsRepository.findByProductId(productDTO.getProductId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productsService.updateProduct(productDTO));
        verify(productsRepository, never()).save(any());
    }

    @Test
    void getProductsByName_returnsMatchingProducts() {
        when(productsRepository.findByName("Helmet")).thenReturn(List.of(productEntity));

        List<ProductDTO> result = productsService.getProductsByName("Helmet");

        assertEquals(1, result.size());
        assertEquals("Helmet", result.getFirst().getProductName());
    }

    @Test
    void deleteProduct_productExists_softDeletes() {
        when(productsRepository.findByProductId(1)).thenReturn(Optional.of(productEntity));

        assertDoesNotThrow(() -> productsService.deleteProduct(1));
        assertTrue(productEntity.isArchived());
        verify(productsRepository).save(productEntity);
    }

    @Test
    void deleteProduct_productNotFound_throwsException() {
        when(productsRepository.findByProductId(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productsService.deleteProduct(99));
        verify(productsRepository, never()).save(any());
    }

    @Test
    void addProduct_nullProduct_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> productsService.addProduct(null, null));
    }

    @Test
    void addProductReview_success() {
        // Arrange
        when(productsRepository.findByProductId(productReviewDTO.getProductId())).thenReturn(Optional.of(productEntity));
        when(ratingCalculatorService.determineReviewRating(anyInt())).thenReturn(4.5);

        // Act
        productsService.addProductReview(productReviewDTO);

        // Assert
        verify(productReviewRepository).save(any());
        verify(productsRepository).save(any());
    }
}
