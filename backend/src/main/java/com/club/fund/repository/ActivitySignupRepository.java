package com.club.fund.repository;

import com.club.fund.entity.ActivitySignup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivitySignupRepository extends JpaRepository<ActivitySignup, Long> {

    List<ActivitySignup> findByActivityId(Long activityId);

    Page<ActivitySignup> findByActivityId(Long activityId, Pageable pageable);

    List<ActivitySignup> findByUserId(Long userId);

    Optional<ActivitySignup> findByActivityIdAndUserId(Long activityId, Long userId);

    boolean existsByActivityIdAndUserId(Long activityId, Long userId);

    @Query("SELECT CASE WHEN COUNT(as) > 0 THEN true ELSE false END FROM ActivitySignup as WHERE as.activity.id = :activityId AND as.user.id = :userId AND as.status = 1")
    boolean existsActiveSignup(@Param("activityId") Long activityId, @Param("userId") Long userId);

    @Query("SELECT COUNT(as) FROM ActivitySignup as WHERE as.activity.id = :activityId AND as.status = 1")
    int countByActivityId(@Param("activityId") Long activityId);

    @Modifying
    @Query("UPDATE ActivitySignup as SET as.status = 0 WHERE as.activity.id = :activityId AND as.user.id = :userId")
    int cancelSignup(@Param("activityId") Long activityId, @Param("userId") Long userId);
}
