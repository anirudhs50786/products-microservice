package com.motocart.products_microservice.product.repository;

import com.motocart.products_microservice.product.entity.ProductPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPriceEntity, Integer> {

    @Query(value = "SELECT * FROM product_price_history WHERE product_id = :productId AND effective_to = null", nativeQuery = true)
    Optional<ProductPriceEntity> getLatestPriceForProduct(@Param("productId") int productId);
}
