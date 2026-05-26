package com.club.fund.service;

import com.alibaba.fastjson2.JSON;
import com.club.fund.common.Constants;
import com.club.fund.common.ErrorCode;
import com.club.fund.dto.request.FundApplyRequest;
import com.club.fund.dto.response.ActivityResponse;
import com.club.fund.dto.response.ApprovalRecordResponse;
import com.club.fund.dto.response.ClubResponse;
import com.club.fund.dto.response.FundApplyResponse;
import com.club.fund.dto.response.UserResponse;
import com.club.fund.entity.Activity;
import com.club.fund.entity.ApprovalRecord;
import com.club.fund.entity.Club;
import com.club.fund.entity.FundApply;
import com.club.fund.entity.FundFlow;
import com.club.fund.entity.User;
import com.club.fund.exception.BusinessException;
import com.club.fund.repository.ActivityRepository;
import com.club.fund.repository.ApprovalRecordRepository;
import com.club.fund.repository.ClubRepository;
import com.club.fund.repository.FundApplyRepository;
import com.club.fund.repository.FundFlowRepository;
import com.club.fund.repository.UserRepository;
import com.club.fund.util.CommonUtil;
import com.club.fund.vo.FundFlowVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FundService {

    private final FundApplyRepository fundApplyRepository;
    private final FundFlowRepository fundFlowRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ApprovalRecordRepository approvalRecordRepository;

    @Transactional
    public FundApplyResponse createApply(FundApplyRequest request, Long applicantId) {
        Club club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLUB_NOT_FOUND));

        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        FundApply apply = new FundApply();
        apply.setApplyNo(CommonUtil.generateApplyNo());
        apply.setClub(club);
        apply.setApplicant(applicant);
        apply.setApplyType(request.getApplyType());
        apply.setAmount(request.getAmount());
        apply.setReason(request.getReason());
        apply.setVouchers(request.getVouchers() != null ? JSON.toJSONString(request.getVouchers()) : "[]");
        apply.setStatus(Constants.STATUS_PENDING);
        apply.setCurrentStep(1);
        apply.setDeleted(0);

        if (request.getActivityId() != null) {
            Activity activity = activityRepository.findById(request.getActivityId()).orElse(null);
            apply.setActivity(activity);
        }

        apply = fundApplyRepository.save(apply);
        return convertToResponse(apply);
    }

    @Transactional
    public FundApplyResponse updateApply(Long id, FundApplyRequest request) {
        FundApply apply = fundApplyRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLY_NOT_FOUND));

        if (!Constants.STATUS_PENDING.equals(apply.getStatus()) && 
            !Constants.STATUS_REJECTED.equals(apply.getStatus())) {
            throw new BusinessException(ErrorCode.APPLY_STATUS_ERROR);
        }

        apply.setApplyType(request.getApplyType());
        apply.setAmount(request.getAmount());
        apply.setReason(request.getReason());
        apply.setVouchers(request.getVouchers() != null ? JSON.toJSONString(request.getVouchers()) : "[]");
        apply.setStatus(Constants.STATUS_PENDING);
        apply.setCurrentStep(1);

        if (request.getActivityId() != null) {
            Activity activity = activityRepository.findById(request.getActivityId()).orElse(null);
            apply.setActivity(activity);
        }

        apply = fundApplyRepository.save(apply);
        return convertToResponse(apply);
    }

    @Transactional(readOnly = true)
    public FundApplyResponse getApplyById(Long id) {
        FundApply apply = fundApplyRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLY_NOT_FOUND));
        return convertToResponse(apply);
    }

    @Transactional(readOnly = true)
    public Page<FundApplyResponse> getApplyList(Long clubId, String status, Pageable pageable) {
        if (clubId != null && status != null) {
            return fundApplyRepository.findByClubIdAndStatus(clubId, status).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                    ));
        } else if (clubId != null) {
            return fundApplyRepository.findByClubId(clubId, pageable).map(this::convertToResponse);
        } else if (status != null) {
            return fundApplyRepository.findByStatus(status, pageable).map(this::convertToResponse);
        }
        return fundApplyRepository.findAll(pageable).map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<FundApplyResponse> getMyApplies(Long applicantId, Pageable pageable) {
        return fundApplyRepository.findByApplicantId(applicantId, pageable).map(this::convertToResponse);
    }

    @Transactional
    public void deleteApply(Long id) {
        FundApply apply = fundApplyRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLY_NOT_FOUND));
        
        if (!Constants.STATUS_PENDING.equals(apply.getStatus())) {
            throw new BusinessException(ErrorCode.APPLY_STATUS_ERROR);
        }
        
        apply.setDeleted(1);
        fundApplyRepository.save(apply);
    }

    @Transactional(readOnly = true)
    public Page<FundFlowVO> getFlowList(Long clubId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        if (startTime != null && endTime != null) {
            return fundFlowRepository.findByClubIdAndTimeRange(clubId, startTime, endTime, pageable)
                    .map(this::convertToFlowVO);
        }
        return fundFlowRepository.findByClubId(clubId, pageable).map(this::convertToFlowVO);
    }

    @Transactional
    public void addIncome(Long clubId, BigDecimal amount, String description, Long operatorId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLUB_NOT_FOUND));

        User operator = userRepository.findById(operatorId).orElse(null);

        BigDecimal balanceBefore = club.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);

        FundFlow flow = new FundFlow();
        flow.setFlowNo(CommonUtil.generateFlowNo());
        flow.setClub(club);
        flow.setFlowType(Constants.FLOW_TYPE_INCOME);
        flow.setAmount(amount);
        flow.setBalanceBefore(balanceBefore);
        flow.setBalanceAfter(balanceAfter);
        flow.setDescription(description);
        flow.setOperator(operator);

        fundFlowRepository.save(flow);

        club.setBalance(balanceAfter);
        clubRepository.save(club);
    }

    private FundApplyResponse convertToResponse(FundApply apply) {
        FundApplyResponse response = new FundApplyResponse();
        response.setId(apply.getId());
        response.setApplyNo(apply.getApplyNo());
        response.setApplyType(apply.getApplyType());
        response.setAmount(apply.getAmount());
        response.setReason(apply.getReason());
        try {
            response.setVouchers(JSON.parseArray(apply.getVouchers(), String.class));
        } catch (Exception ignored) {
            response.setVouchers(List.of());
        }
        response.setStatus(apply.getStatus());
        response.setCurrentStep(apply.getCurrentStep());
        response.setCreateTime(apply.getCreateTime());
        response.setUpdateTime(apply.getUpdateTime());

        if (apply.getClub() != null) {
            ClubResponse clubResponse = new ClubResponse();
            clubResponse.setId(apply.getClub().getId());
            clubResponse.setClubName(apply.getClub().getClubName());
            response.setClub(clubResponse);
        }

        if (apply.getApplicant() != null) {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(apply.getApplicant().getId());
            userResponse.setRealName(apply.getApplicant().getRealName());
            response.setApplicant(userResponse);
        }

        if (apply.getActivity() != null) {
            ActivityResponse activityResponse = new ActivityResponse();
            activityResponse.setId(apply.getActivity().getId());
            activityResponse.setActivityName(apply.getActivity().getActivityName());
            response.setActivity(activityResponse);
        }

        List<ApprovalRecord> records = approvalRecordRepository.findByFundApplyIdOrderByCreateTime(apply.getId());
        response.setApprovalRecords(records.stream().map(this::convertToRecordResponse).collect(Collectors.toList()));

        return response;
    }

    private ApprovalRecordResponse convertToRecordResponse(ApprovalRecord record) {
        ApprovalRecordResponse response = new ApprovalRecordResponse();
        response.setId(record.getId());
        response.setStep(record.getStep());
        response.setAction(record.getAction());
        response.setComment(record.getComment());
        response.setCreateTime(record.getCreateTime());

        if (record.getApprover() != null) {
            response.setApproverName(record.getApprover().getRealName());
        }

        return response;
    }

    private FundFlowVO convertToFlowVO(FundFlow flow) {
        FundFlowVO vo = new FundFlowVO();
        vo.setId(flow.getId());
        vo.setFlowNo(flow.getFlowNo());
        vo.setFlowType(flow.getFlowType());
        vo.setAmount(flow.getAmount());
        vo.setBalanceBefore(flow.getBalanceBefore());
        vo.setBalanceAfter(flow.getBalanceAfter());
        vo.setDescription(flow.getDescription());
        vo.setCreateTime(flow.getCreateTime());

        if (flow.getOperator() != null) {
            vo.setOperatorName(flow.getOperator().getRealName());
        }

        if (flow.getFundApply() != null) {
            vo.setApplyNo(flow.getFundApply().getApplyNo());
        }

        return vo;
    }
}
