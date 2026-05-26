package com.example.backend.repository;

import com.example.backend.entity.CreditRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CreditRecordRepository extends JpaRepository<CreditRecord, Long> {

    @Query(value = "SELECT cr FROM CreditRecord cr JOIN FETCH cr.user WHERE cr.user.id = :userId ORDER BY cr.createTime DESC",
            countQuery = "SELECT COUNT(cr) FROM CreditRecord cr WHERE cr.user.id = :userId")
    Page<CreditRecord> findByUserIdWithUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT cr FROM CreditRecord cr JOIN FETCH cr.user WHERE cr.user.id = :userId AND cr.type = :type")
    List<CreditRecord> findByUserIdAndTypeWithUser(@Param("userId") Long userId, @Param("type") CreditRecord.CreditType type);

    @Query("SELECT SUM(cr.points) FROM CreditRecord cr WHERE cr.user.id = :userId")
    Integer getTotalPointsByUserId(@Param("userId") Long userId);

    @Query("SELECT cr FROM CreditRecord cr JOIN FETCH cr.user WHERE cr.user.id = :userId AND cr.createTime BETWEEN :start AND :end")
    List<CreditRecord> findByUserIdAndCreateTimeBetweenWithUser(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END FROM CreditRecord cr WHERE cr.user.id = :userId AND cr.type = :type AND cr.relatedId = :relatedId AND cr.relatedType = :relatedType")
    boolean existsByUserIdAndTypeAndRelatedIdAndRelatedType(@Param("userId") Long userId, @Param("type") CreditRecord.CreditType type, @Param("relatedId") Long relatedId, @Param("relatedType") String relatedType);
}
