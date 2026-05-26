package com.example.backend.repository;

import com.example.backend.entity.CandidateFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateFeedbackRepository extends JpaRepository<CandidateFeedback, Long> {
    Optional<CandidateFeedback> findByInterviewIdAndUserId(Long interviewId, Long userId);
}
