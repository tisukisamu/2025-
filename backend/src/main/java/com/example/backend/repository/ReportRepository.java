package com.example.backend.repository;

import com.example.backend.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = "SELECT r FROM Report r JOIN FETCH r.reporter JOIN FETCH r.product p LEFT JOIN FETCH p.images WHERE r.status = :status ORDER BY r.createTime DESC",
            countQuery = "SELECT COUNT(r) FROM Report r WHERE r.status = :status")
    Page<Report> findByStatusWithRelations(@Param("status") Report.ReportStatus status, Pageable pageable);

    @Query(value = "SELECT r FROM Report r JOIN FETCH r.reporter JOIN FETCH r.product p LEFT JOIN FETCH p.images ORDER BY r.createTime DESC",
            countQuery = "SELECT COUNT(r) FROM Report r")
    Page<Report> findAllWithRelations(Pageable pageable);

    @Query(value = "SELECT r FROM Report r JOIN FETCH r.reporter JOIN FETCH r.product p LEFT JOIN FETCH p.images WHERE r.reporter.id = :reporterId ORDER BY r.createTime DESC",
            countQuery = "SELECT COUNT(r) FROM Report r WHERE r.reporter.id = :reporterId")
    Page<Report> findByReporterIdWithRelations(@Param("reporterId") Long reporterId, Pageable pageable);

    @Query("SELECT r FROM Report r JOIN FETCH r.reporter JOIN FETCH r.product p LEFT JOIN FETCH p.images WHERE r.id = :id")
    Optional<Report> findByIdWithRelations(@Param("id") Long id);

    Page<Report> findByStatusOrderByCreateTimeDesc(Report.ReportStatus status, Pageable pageable);

    Page<Report> findByReporterIdOrderByCreateTimeDesc(Long reporterId, Pageable pageable);

    Page<Report> findByProductIdOrderByCreateTimeDesc(Long productId, Pageable pageable);

    boolean existsByReporterIdAndProductId(Long reporterId, Long productId);

    long countByStatus(Report.ReportStatus status);
}
