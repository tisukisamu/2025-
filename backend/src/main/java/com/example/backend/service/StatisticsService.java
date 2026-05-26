package com.example.backend.service;

import com.example.backend.dto.CheckinRankingDTO;
import com.example.backend.dto.StatisticsDTO;
import com.example.backend.entity.Habit;
import com.example.backend.entity.Statistics;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CheckRecordRepository;
import com.example.backend.repository.HabitRepository;
import com.example.backend.repository.StatisticsRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final HabitRepository habitRepository;
    private final CheckRecordRepository checkRecordRepository;
    private final UserRepository userRepository;

    public StatisticsDTO getHabitStatistics(Long habitId) {
        User user = getCurrentUser();
        
        Habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new ResourceNotFoundException("习惯", "id", habitId));
        
        Statistics stats = statisticsRepository.findByUserIdAndHabitId(user.getId(), habitId)
            .orElseGet(() -> {
                Statistics s = new Statistics();
                s.setUser(user);
                s.setHabit(habit);
                s.setTotalDays(0);
                s.setStreakDays(0);
                s.setMaxStreak(0);
                s.setCompleteRate(BigDecimal.ZERO);
                return s;
            });
        
        return StatisticsDTO.builder()
            .id(stats.getId())
            .habitId(habit.getId())
            .habitName(habit.getName())
            .totalDays(stats.getTotalDays())
            .streakDays(stats.getStreakDays())
            .maxStreak(stats.getMaxStreak())
            .completeRate(stats.getCompleteRate())
            .lastCheckDate(stats.getLastCheckDate())
            .build();
    }

    public List<StatisticsDTO> getAllStatistics() {
        User user = getCurrentUser();
        List<Statistics> stats = statisticsRepository.findByUserId(user.getId());
        
        return stats.stream()
            .map(s -> StatisticsDTO.builder()
                .id(s.getId())
                .habitId(s.getHabit().getId())
                .habitName(s.getHabit().getName())
                .totalDays(s.getTotalDays())
                .streakDays(s.getStreakDays())
                .maxStreak(s.getMaxStreak())
                .completeRate(s.getCompleteRate())
                .lastCheckDate(s.getLastCheckDate())
                .build())
            .collect(Collectors.toList());
    }

    public List<CheckinRankingDTO> getCheckinRanking(int limit) {
        User currentUser = getCurrentUser();
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getStatus() == User.Status.ACTIVE)
                .collect(Collectors.toList());

        List<CheckinRankingDTO> rows = users.stream().map(user -> {
            List<Statistics> stats = statisticsRepository.findByUserId(user.getId());
            long totalCheckDays = stats.stream()
                    .mapToLong(s -> s.getTotalDays() == null ? 0 : s.getTotalDays())
                    .sum();
            int maxStreak = stats.stream()
                    .mapToInt(s -> s.getMaxStreak() == null ? 0 : s.getMaxStreak())
                    .max()
                    .orElse(0);
            double avgCompleteRate = stats.isEmpty()
                    ? 0.0
                    : stats.stream()
                    .map(s -> s.getCompleteRate() == null ? BigDecimal.ZERO : s.getCompleteRate())
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(stats.size()), 2, RoundingMode.HALF_UP)
                    .doubleValue();
            double score = BigDecimal.valueOf(totalCheckDays)
                    .add(BigDecimal.valueOf(maxStreak).multiply(BigDecimal.valueOf(2)))
                    .add(BigDecimal.valueOf(avgCompleteRate).multiply(BigDecimal.valueOf(0.6)))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            return CheckinRankingDTO.builder()
                    .userId(user.getId())
                    .userName(user.getName())
                    .userAvatar(user.getAvatar())
                    .totalCheckDays(totalCheckDays)
                    .maxStreak(maxStreak)
                    .avgCompleteRate(avgCompleteRate)
                    .score(score)
                    .currentUser(user.getId().equals(currentUser.getId()))
                    .build();
        }).sorted(Comparator
                .comparing(CheckinRankingDTO::getScore).reversed()
                .thenComparing(CheckinRankingDTO::getTotalCheckDays).reversed()
                .thenComparing(CheckinRankingDTO::getMaxStreak).reversed()
                .thenComparing(CheckinRankingDTO::getAvgCompleteRate).reversed())
                .limit(Math.max(1, Math.min(limit, 100)))
                .collect(Collectors.toList());

        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).setRank(i + 1);
        }
        return rows;
    }

    public TrendData getTrend(LocalDate start, LocalDate end) {
        User user = getCurrentUser();
        List<Habit> habits = habitRepository.findActiveHabitsByUserId(user.getId());
        
        TrendData trend = new TrendData();
        trend.setStartDate(start);
        trend.setEndDate(end);
        trend.setDailyData(new ArrayList<>());
        
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            TrendData.DailyData daily = new TrendData.DailyData();
            daily.setDate(date);
            
            int total = habits.size();
            int completed = 0;
            
            for (Habit habit : habits) {
                if (checkRecordRepository.existsByHabitIdAndCheckDate(habit.getId(), date)) {
                    completed++;
                }
            }
            
            daily.setTotalHabits(total);
            daily.setCompletedHabits(completed);
            daily.setCompleteRate(total > 0 ? 
                BigDecimal.valueOf((double) completed / total * 100).setScale(2, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO);
            
            trend.getDailyData().add(daily);
        }
        
        return trend;
    }

    public HeatmapData getHeatmap(int year) {
        User user = getCurrentUser();
        List<Habit> habits = habitRepository.findActiveHabitsByUserId(user.getId());
        
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        
        List<Object[]> checkCounts = checkRecordRepository.findCheckCountsByUserIdAndDateRange(
            user.getId(), start, end);
        
        java.util.Map<LocalDate, Integer> countByDate = new java.util.HashMap<>();
        for (Object[] row : checkCounts) {
            LocalDate date = (LocalDate) row[1];
            Long count = (Long) row[2];
            countByDate.merge(date, count.intValue(), Integer::sum);
        }
        
        HeatmapData heatmap = new HeatmapData();
        heatmap.setYear(year);
        heatmap.setDays(new ArrayList<>());
        
        int totalHabits = habits.size();
        
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            int count = countByDate.getOrDefault(date, 0);
            
            int level = 0;
            if (count > 0 && totalHabits > 0) {
                double ratio = (double) count / totalHabits;
                if (ratio >= 0.75) level = 4;
                else if (ratio >= 0.5) level = 3;
                else if (ratio >= 0.25) level = 2;
                else level = 1;
            }
            
            HeatmapData.DayData day = new HeatmapData.DayData();
            day.setDate(date);
            day.setCount(count);
            day.setLevel(level);
            
            heatmap.getDays().add(day);
        }
        
        return heatmap;
    }

    private User getCurrentUser() {
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userDetails.getId()));
    }

    @Data
    public static class TrendData {
        private LocalDate startDate;
        private LocalDate endDate;
        private List<DailyData> dailyData = new ArrayList<>();

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class DailyData {
            private LocalDate date;
            private int totalHabits;
            private int completedHabits;
            private BigDecimal completeRate;
        }
    }

    @Data
    public static class HeatmapData {
        private int year;
        private List<DayData> days = new ArrayList<>();

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class DayData {
            private LocalDate date;
            private int count;
            private int level;
        }
    }
}
