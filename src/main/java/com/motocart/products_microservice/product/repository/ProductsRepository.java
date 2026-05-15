package com.motocart.products_microservice.product.repository;

import com.motocart.products_microservice.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductsRepository extends JpaRepository<ProductEntity, Integer> {

    @Query(value = "SELECT * FROM products WHERE product_name LIKE CONCAT('%', :productName, '%')", nativeQuery = true)
    List<ProductEntity> findByName(@Param("productName") String productName);

    Optional<ProductEntity> findByProductId(int productId);
}
