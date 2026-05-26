package com.example.backend.repository;

import com.example.backend.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByCreatedByOrderByCreatedAtDesc(Long createdBy);
    List<Report> findByType(Report.Type type);
}
