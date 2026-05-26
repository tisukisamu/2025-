package com.club.fund.repository;

import com.club.fund.entity.FundFlow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FundFlowRepository extends JpaRepository<FundFlow, Long> {

    Page<FundFlow> findByClubId(Long clubId, Pageable pageable);

    List<FundFlow> findByFundApplyId(Long fundApplyId);

    @Query("SELECT ff FROM FundFlow ff WHERE ff.club.id = :clubId AND ff.flowType = :flowType ORDER BY ff.createTime DESC")
    List<FundFlow> findByClubIdAndFlowType(@Param("clubId") Long clubId, @Param("flowType") String flowType);

    @Query("SELECT ff FROM FundFlow ff WHERE ff.club.id = :clubId AND ff.createTime BETWEEN :startTime AND :endTime ORDER BY ff.createTime DESC")
    Page<FundFlow> findByClubIdAndTimeRange(@Param("clubId") Long clubId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, Pageable pageable);

    @Query("SELECT SUM(ff.amount) FROM FundFlow ff WHERE ff.club.id = :clubId AND ff.flowType = 'EXPENSE' AND ff.createTime BETWEEN :startTime AND :endTime")
    Double sumExpenseByClubIdAndTimeRange(@Param("clubId") Long clubId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT SUM(ff.amount) FROM FundFlow ff WHERE ff.club.id = :clubId AND ff.flowType = 'INCOME' AND ff.createTime BETWEEN :startTime AND :endTime")
    Double sumIncomeByClubIdAndTimeRange(@Param("clubId") Long clubId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
