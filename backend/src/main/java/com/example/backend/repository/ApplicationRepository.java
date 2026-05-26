package com.example.backend.repository;

import com.example.backend.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUserId(Long userId);
    List<Application> findByJobId(Long jobId);
    List<Application> findByJobIdIn(List<Long> jobIds);
    boolean existsByUserIdAndJobId(Long userId, Long jobId);
    
    @Query("SELECT COUNT(a) FROM Application a JOIN Job j ON a.jobId = j.id WHERE j.companyId = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);
    
    @Query("SELECT COUNT(a) FROM Application a JOIN Job j ON a.jobId = j.id WHERE j.companyId = :companyId AND a.status = :status")
    long countByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") Application.Status status);
    
    @Query("SELECT a FROM Application a JOIN Job j ON a.jobId = j.id WHERE j.companyId = :companyId")
    List<Application> findByCompanyId(@Param("companyId") Long companyId);
    
    @Query(value = "SELECT DATE(a.applied_at) as date, COUNT(*) as count FROM applications a " +
           "JOIN jobs j ON a.job_id = j.id WHERE j.company_id = :companyId " +
           "AND DATE(a.applied_at) BETWEEN :start AND :end " +
           "GROUP BY DATE(a.applied_at) ORDER BY DATE(a.applied_at)", nativeQuery = true)
    List<Object[]> countByDateRange(@Param("companyId") Long companyId, 
                                    @Param("start") LocalDate start, 
                                    @Param("end") LocalDate end);
}
