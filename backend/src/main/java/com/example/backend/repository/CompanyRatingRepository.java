package com.example.backend.repository;

import com.example.backend.entity.CompanyRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRatingRepository extends JpaRepository<CompanyRating, Long> {
    List<CompanyRating> findByCompanyId(Long companyId);
    Optional<CompanyRating> findByCompanyIdAndUserId(Long companyId, Long userId);
    boolean existsByCompanyIdAndUserId(Long companyId, Long userId);
}
