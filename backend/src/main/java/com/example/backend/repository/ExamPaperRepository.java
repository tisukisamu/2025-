package com.example.backend.repository;

import com.example.backend.entity.ExamPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPaperRepository extends JpaRepository<ExamPaper, Long> {
    List<ExamPaper> findByCreatedBy(Long createdBy);
}
