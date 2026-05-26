package com.example.backend.repository;

import com.example.backend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNo(String orderNo);

    Page<Order> findByBuyerIdOrderByCreateTimeDesc(Long buyerId, Pageable pageable);

    Page<Order> findBySellerIdOrderByCreateTimeDesc(Long sellerId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE (o.buyer.id = :userId OR o.seller.id = :userId) ORDER BY o.createTime DESC")
    Page<Order> findByUserIdOrderByCreateTimeDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.buyer.id = :buyerId AND o.seller.id = :sellerId AND o.status = :status")
    List<Order> findByBuyerIdAndSellerIdAndStatus(
            @Param("buyerId") Long buyerId,
            @Param("sellerId") Long sellerId,
            @Param("status") Order.OrderStatus status
    );

    long countByBuyerId(Long buyerId);

    long countBySellerId(Long sellerId);

    long countByStatus(Order.OrderStatus status);

    boolean existsByProductIdAndStatusIn(Long productId, List<Order.OrderStatus> statuses);

    @Query("SELECT o FROM Order o JOIN FETCH o.buyer JOIN FETCH o.seller JOIN FETCH o.product WHERE o.id = :id")
    Optional<Order> findByIdWithBuyerAndSellerAndProduct(@Param("id") Long id);

    @Query(value = "SELECT o FROM Order o JOIN FETCH o.buyer JOIN FETCH o.seller JOIN FETCH o.product WHERE (o.buyer.id = :userId OR o.seller.id = :userId) ORDER BY o.createTime DESC",
            countQuery = "SELECT COUNT(o) FROM Order o WHERE o.buyer.id = :userId OR o.seller.id = :userId")
    Page<Order> findByUserIdWithAllRelations(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT o FROM Order o JOIN FETCH o.buyer JOIN FETCH o.seller JOIN FETCH o.product ORDER BY o.createTime DESC",
            countQuery = "SELECT COUNT(o) FROM Order o")
    Page<Order> findAllWithRelations(Pageable pageable);

    @Query(value = "SELECT o FROM Order o JOIN FETCH o.buyer JOIN FETCH o.seller JOIN FETCH o.product WHERE o.status = :status ORDER BY o.createTime DESC",
            countQuery = "SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Page<Order> findByStatusWithRelations(@Param("status") Order.OrderStatus status, Pageable pageable);
}
