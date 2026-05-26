package com.example.backend.repository;

import com.example.backend.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @Query(value = "SELECT f FROM Favorite f JOIN FETCH f.product p LEFT JOIN FETCH p.images WHERE f.user.id = :userId ORDER BY f.createTime DESC",
            countQuery = "SELECT COUNT(f) FROM Favorite f WHERE f.user.id = :userId")
    Page<Favorite> findByUserIdWithProduct(@Param("userId") Long userId, Pageable pageable);

    Page<Favorite> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    long countByProductId(Long productId);
}
