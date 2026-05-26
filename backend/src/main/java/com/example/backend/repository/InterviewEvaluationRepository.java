package com.example.backend.repository;

import com.example.backend.entity.InterviewEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewEvaluationRepository extends JpaRepository<InterviewEvaluation, Long> {
    List<InterviewEvaluation> findByInterviewId(Long interviewId);
    Optional<InterviewEvaluation> findByInterviewIdAndInterviewerId(Long interviewId, Long interviewerId);
}
