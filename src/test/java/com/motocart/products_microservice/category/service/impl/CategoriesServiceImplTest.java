package com.motocart.products_microservice.category.service.impl;

import com.motocart.products_microservice.category.entity.CategoriesEntity;
import com.motocart.products_microservice.category.repository.CategoriesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriesServiceImplTest {

    @Mock
    private CategoriesRepository categoriesRepository;

    @InjectMocks
    private CategoriesServiceImpl categoriesService;

    private CategoriesEntity categoriesEntity;

    @BeforeEach
    void setUp() {
        categoriesEntity = CategoriesEntity.builder()
                .categoryId(1)
                .categoryName("Helmets")
                .build();
    }

    @Test
    void addCategory_success() {
        categoriesService.addCategory("Helmets");
        verify(categoriesRepository).save(any(CategoriesEntity.class));
    }

    @Test
    void getCategories_returnsAllCategories() {
        when(categoriesRepository.findAll()).thenReturn(List.of(categoriesEntity));

        List<CategoriesEntity> result = categoriesService.getCategories();

        assertEquals(1, result.size());
    }

    @Test
    void getCategories_emptyList() {
        when(categoriesRepository.findAll()).thenReturn(List.of());

        List<CategoriesEntity> result = categoriesService.getCategories();

        assertTrue(result.isEmpty());
    }

    @Test
    void getCategory_exists_returnsCategoryName() {
        when(categoriesRepository.findById(1)).thenReturn(categoriesEntity);

        CategoriesEntity result = categoriesService.getCategory(1);

        assertEquals("Helmets", result);
    }

    @Test
    void getCategory_notFound_throwsException() {
        when(categoriesRepository.findById(99)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> categoriesService.getCategory(99));
    }

    @Test
    void deleteCategory_success() {
        categoriesService.deleteCategory(1);
        verify(categoriesRepository).deleteById(1);
    }

    @Test
    void updateCategory_notFound_throwsException() {
        when(categoriesRepository.findById(99)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> categoriesService.updateCategory(99, "Gloves"));
        verify(categoriesRepository, never()).save(any());
    }
}
