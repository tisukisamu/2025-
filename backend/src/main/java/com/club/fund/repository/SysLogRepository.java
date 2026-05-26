package com.club.fund.repository;

import com.club.fund.entity.SysLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SysLogRepository extends JpaRepository<SysLog, Long> {

    Page<SysLog> findByUserId(Long userId, Pageable pageable);

    List<SysLog> findByUsername(String username);

    @Query("SELECT sl FROM SysLog sl WHERE sl.createTime BETWEEN :startTime AND :endTime ORDER BY sl.createTime DESC")
    Page<SysLog> findByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, Pageable pageable);

    @Query("SELECT sl FROM SysLog sl WHERE sl.userId = :userId AND sl.createTime BETWEEN :startTime AND :endTime ORDER BY sl.createTime DESC")
    List<SysLog> findByUserIdAndTimeRange(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("""
            SELECT sl FROM SysLog sl
            WHERE (:username IS NULL OR sl.username LIKE CONCAT('%', :username, '%'))
              AND (:operation IS NULL OR sl.operation LIKE CONCAT('%', :operation, '%'))
              AND (:status IS NULL OR sl.status = :status)
            ORDER BY sl.createTime DESC
            """)
    Page<SysLog> search(@Param("username") String username,
                        @Param("operation") String operation,
                        @Param("status") Integer status,
                        Pageable pageable);
}
