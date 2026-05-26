package com.example.backend.repository;

import com.example.backend.entity.TalentPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TalentPoolRepository extends JpaRepository<TalentPool, Long> {
    List<TalentPool> findByCompanyId(Long companyId);
    List<TalentPool> findByCompanyIdAndGroupId(Long companyId, Long groupId);
    Optional<TalentPool> findByCompanyIdAndResumeId(Long companyId, Long resumeId);
    boolean existsByCompanyIdAndResumeId(Long companyId, Long resumeId);
    List<TalentPool> findByCompanyIdAndStatus(Long companyId, TalentPool.Status status);
}
