package com.example.backend.repository;

import com.example.backend.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    
    List<Habit> findByUserId(Long userId);
    
    List<Habit> findByUserIdAndStatus(Long userId, Habit.Status status);
    
    List<Habit> findByUserIdAndCategoryId(Long userId, Long categoryId);
    
    List<Habit> findByUserIdAndCategoryIdAndStatus(Long userId, Long categoryId, Habit.Status status);
    
    @Query("SELECT h FROM Habit h WHERE h.user.id = :userId AND h.status = 'ACTIVE'")
    List<Habit> findActiveHabitsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT h FROM Habit h WHERE h.user.id = :userId AND h.status = 'ACTIVE' AND (h.repeatType = 'DAILY' OR h.repeatDays LIKE %:dayOfWeek%)")
    List<Habit> findTodayHabitsByUserId(@Param("userId") Long userId, @Param("dayOfWeek") String dayOfWeek);
    
    long countByUserId(Long userId);
    
    long countByUserIdAndStatus(Long userId, Habit.Status status);

    long countByUserIdAndStatusNot(Long userId, Habit.Status status);
}
