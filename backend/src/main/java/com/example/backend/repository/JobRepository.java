package com.example.backend.repository;

import com.example.backend.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByCompanyId(Long companyId);
    List<Job> findByStatus(Job.Status status);
    long countByCompanyId(Long companyId);
    long countByCompanyIdAndStatus(Long companyId, Job.Status status);
    
    @Query("SELECT j FROM Job j WHERE " +
           "(:title IS NULL OR j.title LIKE %:title%) AND " +
           "(:location IS NULL OR j.location LIKE %:location%) AND " +
           "(:education IS NULL OR j.education = :education) AND " +
           "(:experience IS NULL OR j.experience = :experience) AND " +
           "j.status = 'ACTIVE'")
    Page<Job> searchJobs(@Param("title") String title,
                         @Param("location") String location,
                         @Param("education") String education,
                         @Param("experience") String experience,
                         Pageable pageable);
}
