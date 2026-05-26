package com.example.backend.repository;

import com.example.backend.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user.id = :userId")
    List<UserAchievement> findByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(ua) > 0 FROM UserAchievement ua WHERE ua.user.id = :userId AND ua.achievement.id = :achievementId")
    boolean existsByUserIdAndAchievementId(@Param("userId") Long userId, @Param("achievementId") Long achievementId);

    @Query("SELECT ua.user.id, COUNT(ua), MAX(ua.earnedAt) FROM UserAchievement ua GROUP BY ua.user.id")
    List<Object[]> summarizeByUserId();
}
