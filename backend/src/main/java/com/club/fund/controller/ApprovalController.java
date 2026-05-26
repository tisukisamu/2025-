package com.club.fund.controller;

import com.club.fund.common.Result;
import com.club.fund.dto.request.ApprovalRequest;
import com.club.fund.dto.response.FundApplyResponse;
import com.club.fund.service.ApprovalService;
import com.club.fund.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @GetMapping("/list")
    public Result<Page<FundApplyResponse>> getPendingApprovals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        String roleCode = SecurityUtil.getCurrentUserRole();
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(approvalService.getPendingApprovals(userId, roleCode, pageable));
    }

    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @RequestBody ApprovalRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        String roleCode = SecurityUtil.getCurrentUserRole();
        approvalService.approve(id, request, userId, roleCode);
        return Result.success();
    }

    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @RequestBody ApprovalRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        String roleCode = SecurityUtil.getCurrentUserRole();
        approvalService.reject(id, request, userId, roleCode);
        return Result.success();
    }
}
