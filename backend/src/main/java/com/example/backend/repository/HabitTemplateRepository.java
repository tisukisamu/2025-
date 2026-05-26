package com.example.backend.repository;

import com.example.backend.entity.HabitTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitTemplateRepository extends JpaRepository<HabitTemplate, Long> {

    @Query("SELECT t FROM HabitTemplate t WHERE " +
            "(:keyword IS NULL OR t.name LIKE %:keyword% OR t.description LIKE %:keyword% OR t.tags LIKE %:keyword%) " +
            "AND (:categoryName IS NULL OR t.categoryName = :categoryName) " +
            "AND (:repeatType IS NULL OR t.repeatType = :repeatType) " +
            "ORDER BY t.name ASC")
    List<HabitTemplate> search(
            @Param("keyword") String keyword,
            @Param("categoryName") String categoryName,
            @Param("repeatType") com.example.backend.entity.Habit.RepeatType repeatType
    );
}
