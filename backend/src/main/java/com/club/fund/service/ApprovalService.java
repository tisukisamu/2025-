package com.club.fund.service;

import com.club.fund.common.Constants;
import com.club.fund.common.ErrorCode;
import com.club.fund.dto.request.ApprovalRequest;
import com.club.fund.dto.response.ClubResponse;
import com.club.fund.dto.response.FundApplyResponse;
import com.club.fund.dto.response.UserResponse;
import com.club.fund.entity.*;
import com.club.fund.exception.BusinessException;
import com.club.fund.repository.*;
import com.club.fund.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final FundApplyRepository fundApplyRepository;
    private final ApprovalRecordRepository approvalRecordRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final FundFlowRepository fundFlowRepository;
    private final NotificationRepository notificationRepository;

    public Page<FundApplyResponse> getPendingApprovals(Long userId, String roleCode, Pageable pageable) {
        if (Constants.ROLE_PRESIDENT.equals(roleCode)) {
            List<Club> clubs = clubRepository.findByPresidentId(userId);
            List<Long> clubIds = clubs.stream().map(Club::getId).collect(Collectors.toList());
            return fundApplyRepository.findByClubIdsAndStatus(clubIds, Constants.STATUS_PENDING, pageable)
                    .map(this::convertToResponse);
        } else if (Constants.ROLE_TEACHER.equals(roleCode)) {
            List<Club> clubs = clubRepository.findByTeacherId(userId);
            List<Long> clubIds = clubs.stream().map(Club::getId).collect(Collectors.toList());
            return fundApplyRepository.findByClubIdsAndStatus(clubIds, Constants.STATUS_PRESIDENT_APPROVED, pageable)
                    .map(this::convertToResponse);
        } else if (Constants.ROLE_ADMIN.equals(roleCode)) {
            return fundApplyRepository.findByStatusIn(List.of(Constants.STATUS_PENDING, Constants.STATUS_PRESIDENT_APPROVED), pageable)
                    .map(this::convertToResponse);
        }
        return Page.empty(pageable);
    }

    @Transactional
    public void approve(Long applyId, ApprovalRequest request, Long approverId, String roleCode) {
        FundApply apply = fundApplyRepository.findById(applyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLY_NOT_FOUND));

        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!canApprove(apply, approverId, roleCode)) {
            throw new BusinessException(ErrorCode.NO_APPROVAL_PERMISSION);
        }

        ApprovalRecord record = new ApprovalRecord();
        record.setFundApply(apply);
        record.setApprover(approver);
        record.setStep(apply.getCurrentStep());
        record.setAction(Constants.APPROVAL_ACTION_APPROVE);
        record.setComment(request.getComment());
        approvalRecordRepository.save(record);

        if (Constants.ROLE_PRESIDENT.equals(roleCode) || (Constants.ROLE_ADMIN.equals(roleCode) && Constants.STATUS_PENDING.equals(apply.getStatus()))) {
            apply.setStatus(Constants.STATUS_PRESIDENT_APPROVED);
            apply.setCurrentStep(2);
        } else if (Constants.ROLE_TEACHER.equals(roleCode) || (Constants.ROLE_ADMIN.equals(roleCode) && Constants.STATUS_PRESIDENT_APPROVED.equals(apply.getStatus()))) {
            apply.setStatus(Constants.STATUS_TEACHER_APPROVED);
            apply.setCurrentStep(3);
            processFundDeduction(apply);
        }

        fundApplyRepository.save(apply);
        sendNotification(apply, "审批通过", request.getComment());
    }

    @Transactional
    public void reject(Long applyId, ApprovalRequest request, Long approverId, String roleCode) {
        FundApply apply = fundApplyRepository.findById(applyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLY_NOT_FOUND));

        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!canApprove(apply, approverId, roleCode)) {
            throw new BusinessException(ErrorCode.NO_APPROVAL_PERMISSION);
        }

        ApprovalRecord record = new ApprovalRecord();
        record.setFundApply(apply);
        record.setApprover(approver);
        record.setStep(apply.getCurrentStep());
        record.setAction(Constants.APPROVAL_ACTION_REJECT);
        record.setComment(request.getComment());
        approvalRecordRepository.save(record);

        apply.setStatus(Constants.STATUS_REJECTED);
        fundApplyRepository.save(apply);

        sendNotification(apply, "审批驳回", request.getComment());
    }

    private boolean canApprove(FundApply apply, Long approverId, String roleCode) {
        if (Constants.ROLE_PRESIDENT.equals(roleCode)) {
            if (!Constants.STATUS_PENDING.equals(apply.getStatus())) {
                return false;
            }
            Club club = apply.getClub();
            return club.getPresident() != null && club.getPresident().getId().equals(approverId);
        } else if (Constants.ROLE_TEACHER.equals(roleCode)) {
            if (!Constants.STATUS_PRESIDENT_APPROVED.equals(apply.getStatus())) {
                return false;
            }
            Club club = apply.getClub();
            return club.getTeacher() != null && club.getTeacher().getId().equals(approverId);
        } else if (Constants.ROLE_ADMIN.equals(roleCode)) {
            return Constants.STATUS_PENDING.equals(apply.getStatus()) || Constants.STATUS_PRESIDENT_APPROVED.equals(apply.getStatus());
        }
        return false;
    }

    @Transactional
    public void processFundDeduction(FundApply apply) {
        Club club = apply.getClub();
        BigDecimal balance = club.getBalance();
        BigDecimal amount = apply.getAmount();

        if (balance.compareTo(amount) < 0) {
            throw new BusinessException(ErrorCode.BALANCE_NOT_ENOUGH);
        }

        User operator = apply.getApplicant();

        FundFlow flow = new FundFlow();
        flow.setFlowNo(CommonUtil.generateFlowNo());
        flow.setClub(club);
        flow.setFundApply(apply);
        flow.setFlowType(Constants.FLOW_TYPE_EXPENSE);
        flow.setAmount(amount);
        flow.setBalanceBefore(balance);
        flow.setBalanceAfter(balance.subtract(amount));
        flow.setDescription("资金申请支出: " + apply.getReason());
        flow.setOperator(operator);
        fundFlowRepository.save(flow);

        club.setBalance(balance.subtract(amount));
        clubRepository.save(club);

        apply.setStatus(Constants.STATUS_COMPLETED);
        fundApplyRepository.save(apply);
    }

    private void sendNotification(FundApply apply, String title, String content) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content != null ? content : "");
        notification.setType(Constants.NOTIFICATION_TYPE_APPROVAL);
        notification.setReceiver(apply.getApplicant());
        notification.setRelatedId(apply.getId());
        notification.setRelatedType("FUND_APPLY");
        notificationRepository.save(notification);
    }

    private FundApplyResponse convertToResponse(FundApply apply) {
        FundApplyResponse response = new FundApplyResponse();
        response.setId(apply.getId());
        response.setApplyNo(apply.getApplyNo());
        response.setApplyType(apply.getApplyType());
        response.setAmount(apply.getAmount());
        response.setReason(apply.getReason());
        response.setStatus(apply.getStatus());
        response.setCurrentStep(apply.getCurrentStep());
        response.setCreateTime(apply.getCreateTime());

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

        return response;
    }
}
