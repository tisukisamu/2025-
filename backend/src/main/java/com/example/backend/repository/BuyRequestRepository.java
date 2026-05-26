package com.example.backend.repository;

import com.example.backend.entity.BuyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuyRequestRepository extends JpaRepository<BuyRequest, Long> {

    @Query(value = "SELECT br FROM BuyRequest br JOIN FETCH br.user WHERE br.status = :status ORDER BY br.createTime DESC",
            countQuery = "SELECT COUNT(br) FROM BuyRequest br WHERE br.status = :status")
    Page<BuyRequest> findByStatusWithUser(@Param("status") BuyRequest.BuyRequestStatus status, Pageable pageable);

    @Query(value = "SELECT br FROM BuyRequest br JOIN FETCH br.user ORDER BY br.createTime DESC",
            countQuery = "SELECT COUNT(br) FROM BuyRequest br")
    Page<BuyRequest> findAllWithUser(Pageable pageable);

    @Query("SELECT br FROM BuyRequest br JOIN FETCH br.user WHERE br.id = :id")
    Optional<BuyRequest> findByIdWithUser(@Param("id") Long id);

    @Query(value = "SELECT br FROM BuyRequest br JOIN FETCH br.user WHERE br.user.id = :userId ORDER BY br.createTime DESC",
            countQuery = "SELECT COUNT(br) FROM BuyRequest br WHERE br.user.id = :userId")
    Page<BuyRequest> findByUserIdWithUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT br FROM BuyRequest br JOIN FETCH br.user WHERE br.category = :category ORDER BY br.createTime DESC")
    Page<BuyRequest> findByCategory(@Param("category") String category, Pageable pageable);

    long countByStatus(BuyRequest.BuyRequestStatus status);
}
