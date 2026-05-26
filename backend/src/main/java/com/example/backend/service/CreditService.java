package com.example.backend.service;

import com.example.backend.dto.CreditRecordDTO;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.CreditRecord;
import com.example.backend.entity.User;
import com.example.backend.repository.CreditRecordRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRecordRepository creditRecordRepository;
    private final UserRepository userRepository;

    public static final int REGISTER_BONUS = 50;
    public static final int COMPLETE_PROFILE_BONUS = 20;
    public static final int PUBLISH_PRODUCT_BONUS = 5;
    public static final int SUCCESSFUL_TRADE_BONUS = 30;
    public static final int POSITIVE_REVIEW_BONUS = 10;
    public static final int NEGATIVE_REVIEW_PENALTY = -10;
    public static final int CANCEL_ORDER_PENALTY = -5;
    public static final int REPORT_VERIFIED_BONUS = 15;
    public static final int BE_REPORTED_PENALTY = -20;
    public static final int DAILY_LOGIN_BONUS = 2;
    public static final int SHARE_TOPIC_BONUS = 3;
    public static final int RECEIVE_LIKE_BONUS = 1;

    public int getUserCredit(Long userId) {
        Integer total = creditRecordRepository.getTotalPointsByUserId(userId);
        return total != null ? total : 0;
    }

    public String getUserLevel(Long userId) {
        int credit = getUserCredit(userId);
        if (credit >= 500) return "钻石会员";
        if (credit >= 300) return "金牌会员";
        if (credit >= 150) return "银牌会员";
        if (credit >= 50) return "铜牌会员";
        return "新手上路";
    }

    public Map<String, Object> getUserCreditInfo(Long userId) {
        Map<String, Object> info = new HashMap<>();
        info.put("credit", getUserCredit(userId));
        info.put("level", getUserLevel(userId));
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        int monthlyPoints = creditRecordRepository.findByUserIdAndCreateTimeBetweenWithUser(userId, startOfMonth, now)
                .stream()
                .mapToInt(CreditRecord::getPoints)
                .sum();
        info.put("monthlyPoints", monthlyPoints);
        
        return info;
    }

    public PageResponse<CreditRecordDTO> getCreditHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<CreditRecord> recordPage = creditRecordRepository.findByUserIdWithUser(userId, pageable);
        Page<CreditRecordDTO> dtoPage = recordPage.map(CreditRecordDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    @Transactional
    public void addCredit(Long userId, CreditRecord.CreditType type, Integer points, String description,
                          Long relatedId, String relatedType) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        if (relatedId != null && relatedType != null) {
            if (creditRecordRepository.existsByUserIdAndTypeAndRelatedIdAndRelatedType(userId, type, relatedId, relatedType)) {
                return;
            }
        }

        CreditRecord record = new CreditRecord();
        record.setUser(user);
        record.setType(type);
        record.setPoints(points);
        record.setDescription(description);
        record.setRelatedId(relatedId);
        record.setRelatedType(relatedType);
        creditRecordRepository.save(record);
    }

    @Transactional
    public void addCreditForRegister(Long userId) {
        addCredit(userId, CreditRecord.CreditType.REGISTER, REGISTER_BONUS, "注册奖励", null, null);
    }

    @Transactional
    public void addCreditForCompleteProfile(Long userId) {
        addCredit(userId, CreditRecord.CreditType.COMPLETE_PROFILE, COMPLETE_PROFILE_BONUS, "完善个人资料", null, null);
    }

    @Transactional
    public void addCreditForPublishProduct(Long userId, Long productId) {
        addCredit(userId, CreditRecord.CreditType.PUBLISH_PRODUCT, PUBLISH_PRODUCT_BONUS, 
                "发布商品", productId, "PRODUCT");
    }

    @Transactional
    public void addCreditForSuccessfulTrade(Long userId, Long orderId) {
        addCredit(userId, CreditRecord.CreditType.SUCCESSFUL_TRADE, SUCCESSFUL_TRADE_BONUS, 
                "交易成功", orderId, "ORDER");
    }

    @Transactional
    public void addCreditForReview(Long userId, Long reviewId, boolean isPositive) {
        int points = isPositive ? POSITIVE_REVIEW_BONUS : NEGATIVE_REVIEW_PENALTY;
        String desc = isPositive ? "获得好评" : "收到差评";
        addCredit(userId, CreditRecord.CreditType.POSITIVE_REVIEW, points, desc, reviewId, "REVIEW");
    }

    @Transactional
    public void addCreditForDailyLogin(Long userId) {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = start.plusDays(1);
        
        boolean alreadyLoggedIn = !creditRecordRepository
                .findByUserIdAndCreateTimeBetweenWithUser(userId, start, end).isEmpty();
        
        if (!alreadyLoggedIn) {
            addCredit(userId, CreditRecord.CreditType.DAILY_LOGIN, DAILY_LOGIN_BONUS, "每日登录", null, null);
        }
    }

    @Transactional
    public void addCreditForCancelOrder(Long userId, Long orderId) {
        addCredit(userId, CreditRecord.CreditType.CANCEL_ORDER, CANCEL_ORDER_PENALTY, 
                "取消订单", orderId, "ORDER");
    }
}
