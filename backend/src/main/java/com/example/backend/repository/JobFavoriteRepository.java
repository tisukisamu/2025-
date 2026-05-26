package com.example.backend.repository;

import com.example.backend.entity.JobFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobFavoriteRepository extends JpaRepository<JobFavorite, Long> {
    boolean existsByUserIdAndJobId(Long userId, Long jobId);
    Optional<JobFavorite> findByUserIdAndJobId(Long userId, Long jobId);
    List<JobFavorite> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<JobFavorite> findByUserId(Long userId);
}
