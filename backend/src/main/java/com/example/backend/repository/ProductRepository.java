package com.example.backend.repository;

import com.example.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findBySellerId(Long sellerId, Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByStatusAndAuditStatus(
            Product.ProductStatus status,
            Product.AuditStatus auditStatus,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR p.title LIKE %:keyword% OR p.description LIKE %:keyword%) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:tradeType IS NULL OR p.tradeType = :tradeType) AND " +
            "(:sellerId IS NULL OR p.seller.id = :sellerId) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "p.auditStatus = :auditStatus")
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("tradeType") String tradeType,
            @Param("sellerId") Long sellerId,
            @Param("status") Product.ProductStatus status,
            @Param("auditStatus") Product.AuditStatus auditStatus,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE p.seller.id = :sellerId AND p.status != :status")
    List<Product> findBySellerIdAndStatusNot(@Param("sellerId") Long sellerId, @Param("status") Product.ProductStatus status);

    long countBySellerId(Long sellerId);

    long countByStatusAndAuditStatus(Product.ProductStatus status, Product.AuditStatus auditStatus);

    @Query("SELECT p FROM Product p JOIN FETCH p.seller WHERE p.id = :id")
    Optional<Product> findByIdWithSeller(@Param("id") Long id);

    @Query("SELECT p FROM Product p JOIN FETCH p.seller JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithSellerAndImages(@Param("id") Long id);
}
