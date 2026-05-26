package com.club.fund.repository;

import com.club.fund.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("SELECT a FROM Activity a WHERE a.club.id = :clubId AND a.deleted = 0 ORDER BY a.createTime DESC")
    Page<Activity> findByClubId(@Param("clubId") Long clubId, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.club.id IN :clubIds AND a.deleted = 0 ORDER BY a.createTime DESC")
    Page<Activity> findByClubIds(@Param("clubIds") List<Long> clubIds, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.club.id = :clubId AND a.status = :status AND a.deleted = 0 ORDER BY a.createTime DESC")
    List<Activity> findByClubIdAndStatus(@Param("clubId") Long clubId, @Param("status") String status);

    @Query("SELECT a FROM Activity a WHERE a.club.id = :clubId AND a.status = 'PUBLISHED' AND a.deleted = 0 ORDER BY a.startTime DESC")
    List<Activity> findPublishedByClubId(@Param("clubId") Long clubId);

    @Query("SELECT a FROM Activity a WHERE a.status = 'PUBLISHED' AND a.deleted = 0 ORDER BY a.startTime DESC")
    Page<Activity> findAllPublished(Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.activityName LIKE %:keyword% AND a.status = 'PUBLISHED' AND a.deleted = 0")
    Page<Activity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.status = :status AND a.deleted = 0 ORDER BY a.createTime DESC")
    Page<Activity> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.deleted = 0 ORDER BY a.createTime DESC")
    Page<Activity> findAllNonDeleted(Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.club.id IN :clubIds AND a.status = :status AND a.deleted = 0 ORDER BY a.createTime DESC")
    Page<Activity> findByClubIdsAndStatus(@Param("clubIds") List<Long> clubIds, @Param("status") String status, Pageable pageable);
}
