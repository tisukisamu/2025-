package com.example.backend.repository;

import com.example.backend.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query(value = "SELECT f FROM Feedback f JOIN FETCH f.user WHERE f.user.id = :userId ORDER BY f.createTime DESC",
            countQuery = "SELECT COUNT(f) FROM Feedback f WHERE f.user.id = :userId")
    Page<Feedback> findByUserIdWithUser(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT f FROM Feedback f JOIN FETCH f.user WHERE f.status = :status ORDER BY f.createTime DESC",
            countQuery = "SELECT COUNT(f) FROM Feedback f WHERE f.status = :status")
    Page<Feedback> findByStatusWithUser(@Param("status") Feedback.FeedbackStatus status, Pageable pageable);

    @Query(value = "SELECT f FROM Feedback f JOIN FETCH f.user ORDER BY f.createTime DESC",
            countQuery = "SELECT COUNT(f) FROM Feedback f")
    Page<Feedback> findAllWithUser(Pageable pageable);

    @Query("SELECT f FROM Feedback f JOIN FETCH f.user LEFT JOIN FETCH f.replier WHERE f.id = :id")
    Optional<Feedback> findByIdWithUsers(@Param("id") Long id);

    Page<Feedback> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    Page<Feedback> findByStatusOrderByCreateTimeDesc(Feedback.FeedbackStatus status, Pageable pageable);

    Page<Feedback> findAllByOrderByCreateTimeDesc(Pageable pageable);

    long countByStatus(Feedback.FeedbackStatus status);
}
