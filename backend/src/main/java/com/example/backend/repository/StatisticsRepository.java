package com.example.backend.repository;

import com.example.backend.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    
    List<Statistics> findByUserId(Long userId);
    
    Optional<Statistics> findByUserIdAndHabitId(Long userId, Long habitId);
    
    @Query("SELECT SUM(s.totalDays) FROM Statistics s WHERE s.user.id = :userId")
    Long sumTotalDaysByUserId(@Param("userId") Long userId);
    
    @Query("SELECT AVG(s.completeRate) FROM Statistics s WHERE s.user.id = :userId")
    Double avgCompleteRateByUserId(@Param("userId") Long userId);
    
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId ORDER BY s.streakDays DESC")
    List<Statistics> findTopStreaksByUserId(@Param("userId") Long userId);
}
