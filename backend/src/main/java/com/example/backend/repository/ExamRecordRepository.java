package com.example.backend.repository;

import com.example.backend.entity.ExamRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRecordRepository extends JpaRepository<ExamRecord, Long> {
    List<ExamRecord> findByUserId(Long userId);
    List<ExamRecord> findByPaperId(Long paperId);
    Optional<ExamRecord> findByPaperIdAndUserId(Long paperId, Long userId);
}
