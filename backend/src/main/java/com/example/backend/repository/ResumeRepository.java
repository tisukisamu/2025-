package com.example.backend.repository;

import com.example.backend.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);
    
    @Query("SELECT r FROM Resume r WHERE " +
           "(:keyword IS NULL OR r.name LIKE %:keyword% OR r.skills LIKE %:keyword%) AND " +
           "(:education IS NULL OR r.education = :education) AND " +
           "(:experience IS NULL OR r.experience = :experience)")
    List<Resume> filterResumes(@Param("keyword") String keyword,
                               @Param("education") String education,
                               @Param("experience") String experience);
}
