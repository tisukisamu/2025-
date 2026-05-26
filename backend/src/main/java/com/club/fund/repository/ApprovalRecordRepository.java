package com.club.fund.repository;

import com.club.fund.entity.ApprovalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, Long> {

    List<ApprovalRecord> findByFundApplyId(Long fundApplyId);

    @Query("SELECT ar FROM ApprovalRecord ar WHERE ar.fundApply.id = :fundApplyId ORDER BY ar.createTime ASC")
    List<ApprovalRecord> findByFundApplyIdOrderByCreateTime(@Param("fundApplyId") Long fundApplyId);

    @Query("SELECT ar FROM ApprovalRecord ar WHERE ar.approver.id = :approverId ORDER BY ar.createTime DESC")
    List<ApprovalRecord> findByApproverId(@Param("approverId") Long approverId);
}
