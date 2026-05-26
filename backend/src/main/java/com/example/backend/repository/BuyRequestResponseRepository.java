package com.example.backend.repository;

import com.example.backend.entity.BuyRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuyRequestResponseRepository extends JpaRepository<BuyRequestResponse, Long> {

    @Query(value = "SELECT r FROM BuyRequestResponse r JOIN FETCH r.responder LEFT JOIN FETCH r.product WHERE r.buyRequest.id = :buyRequestId ORDER BY r.createTime DESC",
            countQuery = "SELECT COUNT(r) FROM BuyRequestResponse r WHERE r.buyRequest.id = :buyRequestId")
    Page<BuyRequestResponse> findByBuyRequestIdWithRelations(@Param("buyRequestId") Long buyRequestId, Pageable pageable);

    @Query("SELECT r FROM BuyRequestResponse r JOIN FETCH r.responder LEFT JOIN FETCH r.product LEFT JOIN FETCH r.buyRequest WHERE r.id = :id")
    Optional<BuyRequestResponse> findByIdWithRelations(@Param("id") Long id);

    List<BuyRequestResponse> findByBuyRequestIdOrderByCreateTimeDesc(Long buyRequestId);

    List<BuyRequestResponse> findByResponderIdOrderByCreateTimeDesc(Long responderId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM BuyRequestResponse r WHERE r.buyRequest.id = :buyRequestId AND r.responder.id = :responderId")
    boolean existsByBuyRequestIdAndResponderId(@Param("buyRequestId") Long buyRequestId, @Param("responderId") Long responderId);
}
