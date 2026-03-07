package com.motocart.products_microservice.category.repository;

import com.motocart.products_microservice.category.entity.CategoriesEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriesRepository extends JpaRepository<CategoriesEntity, Integer> {

    @NonNull
    List<CategoriesEntity> findAll();

    CategoriesEntity findById(int categoryId);

    void deleteById(int categoryId);
}
