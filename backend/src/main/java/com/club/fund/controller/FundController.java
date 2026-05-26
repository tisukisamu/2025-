package com.club.fund.controller;

import com.club.fund.common.Result;
import com.club.fund.exception.BusinessException;
import com.club.fund.dto.request.FundApplyRequest;
import com.club.fund.dto.response.FundApplyResponse;
import com.club.fund.service.FundService;
import com.club.fund.util.SecurityUtil;
import com.club.fund.vo.FundFlowVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/fund")
@RequiredArgsConstructor
public class FundController {

    private final FundService fundService;

    @PostMapping("/apply")
    public Result<FundApplyResponse> createApply(@Valid @RequestBody FundApplyRequest request) {
        Long applicantId = SecurityUtil.getCurrentUserId();
        return Result.success(fundService.createApply(request, applicantId));
    }

    @PutMapping("/{id}")
    public Result<FundApplyResponse> updateApply(@PathVariable Long id, @Valid @RequestBody FundApplyRequest request) {
        return Result.success(fundService.updateApply(id, request));
    }

    @GetMapping("/{id}")
    public Result<FundApplyResponse> getApplyById(@PathVariable Long id) {
        return Result.success(fundService.getApplyById(id));
    }

    @GetMapping("/list")
    public Result<Page<FundApplyResponse>> getApplyList(
            @RequestParam(required = false) Long clubId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(fundService.getApplyList(clubId, status, pageable));
    }

    @GetMapping("/my")
    public Result<Page<FundApplyResponse>> getMyApplies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long applicantId = SecurityUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(fundService.getMyApplies(applicantId, pageable));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteApply(@PathVariable Long id) {
        fundService.deleteApply(id);
        return Result.success();
    }

    @GetMapping("/flow")
    public Result<Page<FundFlowVO>> getFlowList(
            @RequestParam Long clubId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(fundService.getFlowList(clubId, parseDateTime(startTime), parseDateTime(endTime), pageable));
    }

    @PostMapping("/income")
    public Result<Void> addIncome(
            @RequestParam Long clubId,
            @RequestParam BigDecimal amount,
            @RequestParam String description) {
        Long operatorId = SecurityUtil.getCurrentUserId();
        fundService.addIncome(clubId, amount, description, operatorId);
        return Result.success();
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception ignored) {
        }
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception ignored) {
        }
        try {
            return LocalDateTime.parse(value);
        } catch (Exception ignored) {
        }
        throw new BusinessException("时间格式错误");
    }
}
