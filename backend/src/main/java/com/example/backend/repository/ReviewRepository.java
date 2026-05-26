package com.example.backend.repository;

import com.example.backend.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByOrderId(Long orderId);

    @Query(value = "SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.order o JOIN FETCH o.product WHERE o.product.id = :productId ORDER BY r.createTime DESC",
            countQuery = "SELECT COUNT(r) FROM Review r WHERE r.order.product.id = :productId")
    Page<Review> findByProductIdWithUserAndOrder(@Param("productId") Long productId, Pageable pageable);

    @Query(value = "SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.order o JOIN FETCH o.product p WHERE p.seller.id = :sellerId ORDER BY r.createTime DESC",
            countQuery = "SELECT COUNT(r) FROM Review r WHERE r.order.product.seller.id = :sellerId")
    Page<Review> findBySellerIdWithUserAndOrder(@Param("sellerId") Long sellerId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.order.product.seller.id = :sellerId")
    Double getAverageRatingBySellerId(@Param("sellerId") Long sellerId);

    long countByOrderProductId(Long productId);

    boolean existsByOrderId(Long orderId);

    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.order o JOIN FETCH o.product WHERE r.id = :id")
    Optional<Review> findByIdWithUserAndOrder(@Param("id") Long id);
}
