package com.club.fund.repository;

import com.club.fund.entity.FundApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FundApplyRepository extends JpaRepository<FundApply, Long> {

    Optional<FundApply> findByApplyNo(String applyNo);

    Page<FundApply> findByClubId(Long clubId, Pageable pageable);

    Page<FundApply> findByApplicantId(Long applicantId, Pageable pageable);

    Page<FundApply> findByStatus(String status, Pageable pageable);

    @Query("SELECT fa FROM FundApply fa WHERE fa.club.id = :clubId AND fa.status = :status AND fa.deleted = 0")
    List<FundApply> findByClubIdAndStatus(@Param("clubId") Long clubId, @Param("status") String status);

    @Query("SELECT fa FROM FundApply fa WHERE fa.club.id = :clubId AND fa.status IN :statuses AND fa.deleted = 0")
    List<FundApply> findByClubIdAndStatusIn(@Param("clubId") Long clubId, @Param("statuses") List<String> statuses);

    @Query("SELECT fa FROM FundApply fa WHERE fa.status IN :statuses AND fa.deleted = 0")
    Page<FundApply> findByStatusIn(@Param("statuses") List<String> statuses, Pageable pageable);

    @Query("SELECT fa FROM FundApply fa WHERE fa.club.id IN :clubIds AND fa.status = :status AND fa.deleted = 0")
    Page<FundApply> findByClubIdsAndStatus(@Param("clubIds") List<Long> clubIds, @Param("status") String status, Pageable pageable);

    @Query("SELECT fa FROM FundApply fa WHERE fa.club.id = :clubId AND fa.createTime BETWEEN :startTime AND :endTime AND fa.deleted = 0")
    List<FundApply> findByClubIdAndTimeRange(@Param("clubId") Long clubId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT SUM(fa.amount) FROM FundApply fa WHERE fa.club.id = :clubId AND fa.status = 'COMPLETED' AND fa.deleted = 0")
    Double sumCompletedAmountByClubId(@Param("clubId") Long clubId);
}
