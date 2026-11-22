package com.skmarkethub.backend.repository;

import com.skmarkethub.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);
    boolean existsBySlug(String slug);
    boolean existsBySku(String sku);
    List<Product> findByStatus(String status);
    List<Product> findByCategoryIdAndStatus(Long categoryId, String status);
    List<Product> findByBrandIdAndStatus(Long brandId, String status);
    List<Product> findByNameContainingIgnoreCaseAndStatus(String keyword, String status);
    List<Product> findBySellerId(Long sellerId);
}