package com.example.backend.repository;

import com.example.backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCategory(String category);
    List<Question> findByType(Question.Type type);
    List<Question> findByDifficulty(Question.Difficulty difficulty);
    List<Question> findByTagsContaining(String tag);
}
