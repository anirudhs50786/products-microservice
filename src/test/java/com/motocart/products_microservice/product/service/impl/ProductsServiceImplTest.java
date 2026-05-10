package com.motocart.products_microservice.product.service.impl;

import com.motocart.library.common.dto.ProductDTO;
import com.motocart.library.common.dto.response.APIResponse;
import com.motocart.library.common.types.ResponseStatus;
import com.motocart.products_microservice.cloudinary.service.CloudinaryService;
import com.motocart.products_microservice.product.entity.ProductsEntity;
import com.motocart.products_microservice.product.repository.ProductsRepository;
import org.junit.jupiter.api.BeforeEach;
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

    @Mock
    private ProductsRepository productsRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private ProductsServiceImpl productsService;

    private ProductDTO productDTO;
    private ProductsEntity productEntity;

    @BeforeEach
    void setUp() {
        productDTO = ProductDTO.builder()
                .productName("Helmet")
                .productDescription("Full face helmet")
                .firmName("MT")
                .build();

        productEntity = ProductsEntity.builder()
                .productId(1)
                .productName("Helmet")
                .productDescription("Full face helmet")
                .firmName("MT")
                .build();
    }

    @Test
    void addProduct_withImage_success() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "helmet.jpg", "image/jpeg", "image".getBytes());
        when(cloudinaryService.uploadFile(image)).thenReturn(Map.of("url", "http://cloudinary.com/helmet.jpg", "public_id", "helmet_123"));
        when(productsRepository.save(any())).thenReturn(productEntity);

        APIResponse<ProductDTO> response = productsService.addProduct(productDTO, image);

        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
        verify(cloudinaryService).uploadFile(image);
        verify(productsRepository).save(any());
    }

    @Test
    void addProduct_imageUploadFails_returnsPartialResponse() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "helmet.jpg", "image/jpeg", "image".getBytes());
        when(cloudinaryService.uploadFile(image)).thenThrow(new RuntimeException("Upload failed"));
        when(productsRepository.save(any())).thenReturn(productEntity);

        APIResponse<ProductDTO> response = productsService.addProduct(productDTO, image);

        assertEquals(ResponseStatus.PARTIAL, response.getStatus());
        assertFalse(response.getMessages().isEmpty());
        verify(productsRepository).save(any());
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
        when(productsRepository.findByProductId(productDTO.getProductId())).thenReturn(null);

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
        when(productsRepository.findByProductId(99)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> productsService.deleteProduct(99));
        verify(productsRepository, never()).save(any());
    }
}
