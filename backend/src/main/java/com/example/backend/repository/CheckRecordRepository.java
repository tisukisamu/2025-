package com.example.backend.repository;

import com.example.backend.entity.CheckRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckRecordRepository extends JpaRepository<CheckRecord, Long> {
    
    List<CheckRecord> findByHabitId(Long habitId);
    
    List<CheckRecord> findByUserId(Long userId);
    
    List<CheckRecord> findByHabitIdAndCheckDateBetween(Long habitId, LocalDate start, LocalDate end);
    
    Optional<CheckRecord> findByHabitIdAndCheckDate(Long habitId, LocalDate date);
    
    List<CheckRecord> findByUserIdAndCheckDate(Long userId, LocalDate date);
    
    List<CheckRecord> findByUserIdAndCheckDateBetween(Long userId, LocalDate start, LocalDate end);
    
    @Query("SELECT c FROM CheckRecord c WHERE c.habit.id = :habitId")
    Long countByHabitId(@Param("habitId") Long habitId);
    
    @Query("SELECT c FROM CheckRecord c WHERE c.user.id = :userId AND c.checkDate BETWEEN :start AND :end ORDER BY c.checkDate DESC")
    Page<CheckRecord> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                                @Param("start") LocalDate start,
                                                @Param("end") LocalDate end,
                                                Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM CheckRecord c WHERE c.habit.id = :habitId AND c.checkDate BETWEEN :start AND :end")
    Long countByHabitIdAndDateRange(@Param("habitId") Long habitId, 
                                     @Param("start") LocalDate start, 
                                     @Param("end") LocalDate end);
    
    boolean existsByHabitIdAndCheckDate(Long habitId, LocalDate date);
    
    @Query("SELECT c.habit.id, c.checkDate, COUNT(c) FROM CheckRecord c WHERE c.user.id = :userId AND c.checkDate BETWEEN :start AND :end GROUP BY c.habit.id, c.checkDate")
    List<Object[]> findCheckCountsByUserIdAndDateRange(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
