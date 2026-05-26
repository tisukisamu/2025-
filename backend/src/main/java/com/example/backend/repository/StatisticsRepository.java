package com.example.backend.repository;

import com.example.backend.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    Optional<Statistics> findByTypeAndDate(String type, LocalDate date);
}
