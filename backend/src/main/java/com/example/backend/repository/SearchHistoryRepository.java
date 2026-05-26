package com.example.backend.repository;

import com.example.backend.entity.SearchHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findByUserIdOrderByUpdateTimeDesc(Long userId, Pageable pageable);

    Optional<SearchHistory> findByUserIdAndKeyword(Long userId, String keyword);

    void deleteByUserIdAndKeyword(Long userId, String keyword);

    @Modifying
    @Query("DELETE FROM SearchHistory s WHERE s.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT s.keyword FROM SearchHistory s WHERE s.user.id = :userId ORDER BY s.updateTime DESC")
    List<String> findKeywordsByUserId(@Param("userId") Long userId, Pageable pageable);
}
