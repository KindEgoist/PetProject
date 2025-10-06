package com.max.productInfo.repository;

import com.max.productInfo.dto.ProductInfo;
import com.max.productInfo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT new com.max.productInfo.dto.ProductInfo(p.id, p.name, p.price) FROM Product p WHERE p.id = :id")
    Optional<ProductInfo> findProductInfoById(@Param("id") Long id);
}
