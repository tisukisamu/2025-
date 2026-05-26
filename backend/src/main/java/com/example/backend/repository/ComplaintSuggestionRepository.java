package com.example.backend.repository;

import com.example.backend.entity.ComplaintSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintSuggestionRepository extends JpaRepository<ComplaintSuggestion, Long> {
    List<ComplaintSuggestion> findByUserId(Long userId);
    List<ComplaintSuggestion> findByStatus(ComplaintSuggestion.Status status);
    List<ComplaintSuggestion> findByType(ComplaintSuggestion.Type type);
}
