package com.example.backend.service;

import com.example.backend.dto.AchievementProgressDTO;
import com.example.backend.dto.AchievementRankingDTO;
import com.example.backend.entity.Achievement;
import com.example.backend.entity.Habit;
import com.example.backend.entity.Statistics;
import com.example.backend.entity.User;
import com.example.backend.entity.UserAchievement;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AchievementRepository;
import com.example.backend.repository.HabitRepository;
import com.example.backend.repository.StatisticsRepository;
import com.example.backend.repository.UserAchievementRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final StatisticsRepository statisticsRepository;
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    public List<AchievementProgressDTO> getMyAchievements(Boolean earnedOnly, String conditionType, String sortBy) {
        User user = getCurrentUser();
        Map<Long, UserAchievement> earnedMap = userAchievementRepository.findByUserId(user.getId()).stream()
                .collect(Collectors.toMap(ua -> ua.getAchievement().getId(), ua -> ua));

        long totalChecks = safeLong(statisticsRepository.sumTotalDaysByUserId(user.getId()));
        long maxStreak = calcMaxStreak(user.getId());
        long totalHabits = habitRepository.countByUserIdAndStatusNot(user.getId(), Habit.Status.DELETED);

        Achievement.ConditionType type = (conditionType == null || conditionType.trim().isEmpty())
                ? null
                : Achievement.ConditionType.valueOf(conditionType.trim());

        Comparator<AchievementProgressDTO> comparator = Comparator
                .comparing(AchievementProgressDTO::getConditionType)
                .thenComparing(AchievementProgressDTO::getThreshold);
        if ("PROGRESS_DESC".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(AchievementProgressDTO::getProgressPercent).reversed();
        } else if ("LATEST_EARNED".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(
                    AchievementProgressDTO::getEarnedAt,
                    java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())
            );
        }

        return achievementRepository.findAll().stream()
                .filter(a -> type == null || a.getConditionType() == type)
                .map(a -> toProgressDTO(a, earnedMap.get(a.getId()), currentValue(a, totalChecks, maxStreak, totalHabits)))
                .filter(dto -> !Boolean.TRUE.equals(earnedOnly) || Boolean.TRUE.equals(dto.getEarned()))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getMySummary() {
        List<AchievementProgressDTO> list = getMyAchievements(false, null, null);
        long earned = list.stream().filter(AchievementProgressDTO::getEarned).count();
        Map<String, Object> map = new HashMap<>();
        map.put("total", list.size());
        map.put("earned", earned);
        map.put("inProgress", list.size() - earned);
        map.put("completionRate", list.isEmpty() ? 0.0 : Math.round((double) earned * 10000.0 / list.size()) / 100.0);
        return map;
    }

    public List<AchievementRankingDTO> getAchievementRanking(Integer limit) {
        int safeLimit = (limit == null || limit <= 0) ? 20 : Math.min(limit, 100);
        long totalAchievements = achievementRepository.count();
        Map<Long, Object[]> earnedSummary = new HashMap<>();
        for (Object[] row : userAchievementRepository.summarizeByUserId()) {
            Long userId = (Long) row[0];
            earnedSummary.put(userId, row);
        }
        Long currentUserId = getCurrentUser().getId();
        List<AchievementRankingDTO> ranking = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            if (user.getStatus() != User.Status.ACTIVE) {
                continue;
            }
            Object[] row = earnedSummary.get(user.getId());
            long earnedCount = row == null ? 0L : ((Long) row[1]);
            LocalDateTime latestEarnedAt = row == null ? null : (LocalDateTime) row[2];
            double completionRate = totalAchievements <= 0
                    ? 0.0
                    : Math.round((double) earnedCount * 10000.0 / totalAchievements) / 100.0;
            ranking.add(AchievementRankingDTO.builder()
                    .userId(user.getId())
                    .userName(user.getName())
                    .userAvatar(user.getAvatar())
                    .earnedCount(earnedCount)
                    .totalAchievements(totalAchievements)
                    .completionRate(completionRate)
                    .latestEarnedAt(latestEarnedAt)
                    .currentUser(user.getId().equals(currentUserId))
                    .build());
        }
        ranking.sort(
                Comparator.comparing(AchievementRankingDTO::getEarnedCount, Comparator.reverseOrder())
                        .thenComparing(
                                AchievementRankingDTO::getLatestEarnedAt,
                                Comparator.nullsLast(Comparator.reverseOrder())
                        )
                        .thenComparing(AchievementRankingDTO::getUserId)
        );
        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).setRank(i + 1);
        }
        int max = Math.min(safeLimit, ranking.size());
        List<AchievementRankingDTO> top = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            top.add(ranking.get(i));
        }
        AchievementRankingDTO currentUserRow = ranking.stream()
                .filter(row -> Boolean.TRUE.equals(row.getCurrentUser()))
                .findFirst()
                .orElse(null);
        if (currentUserRow != null && currentUserRow.getEarnedCount() > 0
                && top.stream().noneMatch(row -> row.getUserId().equals(currentUserRow.getUserId()))) {
            top.add(currentUserRow);
        }
        return top;
    }

    @Transactional
    public void evaluateForCurrentUser() {
        User user = getCurrentUser();
        long totalChecks = safeLong(statisticsRepository.sumTotalDaysByUserId(user.getId()));
        long maxStreak = calcMaxStreak(user.getId());
        long totalHabits = habitRepository.countByUserIdAndStatusNot(user.getId(), Habit.Status.DELETED);

        List<Achievement> all = achievementRepository.findAll();
        for (Achievement a : all) {
            long value = currentValue(a, totalChecks, maxStreak, totalHabits);
            if (value >= a.getThreshold() && !userAchievementRepository.existsByUserIdAndAchievementId(user.getId(), a.getId())) {
                UserAchievement ua = new UserAchievement();
                ua.setUser(user);
                ua.setAchievement(a);
                ua.setEarnedAt(LocalDateTime.now());
                userAchievementRepository.save(ua);
            }
        }
    }

    private long calcMaxStreak(Long userId) {
        List<Statistics> list = statisticsRepository.findTopStreaksByUserId(userId);
        if (list == null || list.isEmpty()) return 0L;
        return list.stream().map(Statistics::getMaxStreak).filter(v -> v != null).mapToLong(Integer::longValue).max().orElse(0L);
    }

    private long currentValue(Achievement a, long totalChecks, long maxStreak, long totalHabits) {
        if (a.getConditionType() == Achievement.ConditionType.TOTAL_CHECKS) return totalChecks;
        if (a.getConditionType() == Achievement.ConditionType.MAX_STREAK) return maxStreak;
        if (a.getConditionType() == Achievement.ConditionType.TOTAL_HABITS) return totalHabits;
        return 0L;
    }

    private AchievementProgressDTO toProgressDTO(Achievement a, UserAchievement earned, long currentValue) {
        double percent = a.getThreshold() != null && a.getThreshold() > 0
                ? Math.min(100.0, (double) currentValue / a.getThreshold() * 100.0)
                : 0.0;

        return AchievementProgressDTO.builder()
                .id(a.getId())
                .code(a.getCode())
                .name(a.getName())
                .description(a.getDescription())
                .icon(a.getIcon())
                .conditionType(a.getConditionType())
                .threshold(a.getThreshold())
                .currentValue(currentValue)
                .progressPercent(Math.round(percent * 100.0) / 100.0)
                .earned(earned != null)
                .earnedAt(earned != null ? earned.getEarnedAt() : null)
                .build();
    }

    private long safeLong(Long v) {
        return v == null ? 0L : v;
    }

    private User getCurrentUser() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userDetails.getId()));
    }
}
