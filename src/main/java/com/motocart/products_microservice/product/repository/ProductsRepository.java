package com.motocart.products_microservice.product.repository;

import com.motocart.products_microservice.product.entity.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<ProductsEntity, Integer> {

    @Query(value = "SELECT * FROM products WHERE product_name LIKE CONCAT('%', :productName, '%')", nativeQuery = true)
    List<ProductsEntity> findByName(@Param("productName") String productName);

    ProductsEntity findByProductId(int productId);
}
