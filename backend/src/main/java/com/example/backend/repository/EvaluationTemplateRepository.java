package com.example.backend.repository;

import com.example.backend.entity.EvaluationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationTemplateRepository extends JpaRepository<EvaluationTemplate, Long> {
    List<EvaluationTemplate> findByCreatedBy(Long createdBy);
}
