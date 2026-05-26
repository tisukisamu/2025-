package com.example.backend.service;

import com.example.backend.dto.CalendarDTO;
import com.example.backend.dto.CheckDTO;
import com.example.backend.dto.TodayOverviewDTO;
import com.example.backend.dto.CheckRequest;
import com.example.backend.entity.CheckRecord;
import com.example.backend.entity.Habit;
import com.example.backend.entity.Statistics;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CheckRecordRepository;
import com.example.backend.repository.HabitRepository;
import com.example.backend.repository.StatisticsRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckService {

    private final CheckRecordRepository checkRecordRepository;
    private final HabitRepository habitRepository;
    private final StatisticsRepository statisticsRepository;
    private final UserRepository userRepository;
    private final AchievementService achievementService;

    @Transactional
    public CheckDTO checkIn(Long habitId, String note) {
        User user = getCurrentUser();
        Habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new ResourceNotFoundException("习惯", "id", habitId));
        
        if (!habit.getUser().getId().equals(user.getId())) {
            throw new BusinessException(403, "无权操作该习惯");
        }
        
        LocalDate today = LocalDate.now();
        if (checkRecordRepository.existsByHabitIdAndCheckDate(habitId, today)) {
            throw new BusinessException(400, "今日已打卡");
        }
        
        CheckRecord record = new CheckRecord();
        record.setHabit(habit);
        record.setUser(user);
        record.setCheckDate(today);
        record.setNote(note);
        
        CheckRecord saved = checkRecordRepository.save(record);
        
        updateStatistics(habit, today);
        achievementService.evaluateForCurrentUser();
        
        return CheckDTO.fromEntity(saved);
    }

    @Transactional
    public void cancelCheck(Long habitId, LocalDate date) {
        User user = getCurrentUser();
        Habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new ResourceNotFoundException("习惯", "id", habitId));
        
        if (!habit.getUser().getId().equals(user.getId())) {
            throw new BusinessException(403, "无权操作该习惯");
        }
        
        CheckRecord record = checkRecordRepository.findByHabitIdAndCheckDate(habitId, date)
            .orElseThrow(() -> new ResourceNotFoundException("打卡记录", "日期", date));
        
        checkRecordRepository.delete(record);
        
        recalculateStatistics(habit);
    }

    public List<CheckDTO> getTodayChecks() {
        User user = getCurrentUser();
        LocalDate today = LocalDate.now();
        List<CheckRecord> records = checkRecordRepository.findByUserIdAndCheckDate(user.getId(), today);
        return records.stream()
            .map(CheckDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public TodayOverviewDTO getTodayOverview() {
        User user = getCurrentUser();
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        String dayName = String.valueOf(dayOfWeek.getValue() % 7);
        
        List<Habit> todayHabits = habitRepository.findTodayHabitsByUserId(user.getId(), dayName);
        List<CheckRecord> todayRecords = checkRecordRepository.findByUserIdAndCheckDate(user.getId(), today);
        
        List<Long> checkedHabitIds = todayRecords.stream()
            .map(r -> r.getHabit().getId())
            .collect(Collectors.toList());
        
        List<TodayOverviewDTO.HabitWithStatusDTO> habitStatusList = new ArrayList<>();
        for (Habit habit : todayHabits) {
            Optional<Statistics> stats = statisticsRepository.findByUserIdAndHabitId(user.getId(), habit.getId());
            int streakDays = stats.map(Statistics::getStreakDays).orElse(0);
            
            habitStatusList.add(TodayOverviewDTO.HabitWithStatusDTO.builder()
                .id(habit.getId())
                .name(habit.getName())
                .icon(habit.getIcon())
                .color(habit.getColor())
                .checked(checkedHabitIds.contains(habit.getId()))
                .streakDays(streakDays)
                .build());
        }
        
        int total = todayHabits.size();
        int completed = (int) habitStatusList.stream().filter(TodayOverviewDTO.HabitWithStatusDTO::isChecked).count();
        double rate = total > 0 ? (double) completed / total * 100 : 0;
        
        return TodayOverviewDTO.builder()
            .totalHabits(total)
            .completedHabits(completed)
            .pendingHabits(total - completed)
            .completeRate(Math.round(rate * 100.0) / 100.0)
            .habits(habitStatusList)
            .build();
    }

    public CalendarDTO getCalendar(String month, Long habitId) {
        User user = getCurrentUser();
        YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        
        List<Habit> habits;
        if (habitId != null) {
            Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new ResourceNotFoundException("习惯", "id", habitId));
            habits = List.of(habit);
        } else {
            habits = habitRepository.findActiveHabitsByUserId(user.getId());
        }
        
        List<CheckRecord> records = checkRecordRepository.findByUserIdAndCheckDateBetween(
            user.getId(), start, end);
        
        java.util.Map<LocalDate, List<CheckRecord>> recordsByDate = records.stream()
            .collect(Collectors.groupingBy(CheckRecord::getCheckDate));
        
        List<CalendarDTO.DayRecordDTO> days = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            List<CheckRecord> dayRecords = recordsByDate.getOrDefault(date, new ArrayList<>());
            
            List<CalendarDTO.HabitCheckDTO> habitChecks = habits.stream()
                .map(h -> CalendarDTO.HabitCheckDTO.builder()
                    .habitId(h.getId())
                    .habitName(h.getName())
                    .checked(dayRecords.stream().anyMatch(r -> r.getHabit().getId().equals(h.getId())))
                    .build())
                .collect(Collectors.toList());
            
            int completed = (int) habitChecks.stream().filter(CalendarDTO.HabitCheckDTO::isChecked).count();
            int total = habits.size();
            double rate = total > 0 ? (double) completed / total * 100 : 0;
            
            days.add(CalendarDTO.DayRecordDTO.builder()
                .day(date.getDayOfMonth())
                .date(date.toString())
                .totalHabits(total)
                .completedHabits(completed)
                .completeRate(Math.round(rate * 100.0) / 100.0)
                .habits(habitChecks)
                .build());
        }
        
        return CalendarDTO.builder()
            .month(month)
            .days(days)
            .build();
    }

    public Page<CheckDTO> getHistory(Long habitId, int page, int size) {
        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        
        Page<CheckRecord> records;
        if (habitId != null) {
            records = checkRecordRepository.findByUserIdAndDateRange(
                user.getId(), LocalDate.MIN, LocalDate.MAX, pageable);
        } else {
            records = checkRecordRepository.findByUserIdAndDateRange(
                user.getId(), LocalDate.MIN, LocalDate.MAX, pageable);
        }
        
        return records.map(CheckDTO::fromEntity);
    }

    private void updateStatistics(Habit habit, LocalDate checkDate) {
        User user = habit.getUser();
        Optional<Statistics> optStats = statisticsRepository.findByUserIdAndHabitId(user.getId(), habit.getId());
        
        Statistics stats = optStats.orElseGet(() -> {
            Statistics s = new Statistics();
            s.setUser(user);
            s.setHabit(habit);
            return s;
        });
        
        stats.setTotalDays(stats.getTotalDays() + 1);
        
        if (stats.getLastCheckDate() != null && 
            stats.getLastCheckDate().plusDays(1).equals(checkDate)) {
            stats.setStreakDays(stats.getStreakDays() + 1);
        } else {
            stats.setStreakDays(1);
        }
        
        if (stats.getStreakDays() > stats.getMaxStreak()) {
            stats.setMaxStreak(stats.getStreakDays());
        }
        
        stats.setLastCheckDate(checkDate);
        
        long totalDaysSinceCreation = java.time.temporal.ChronoUnit.DAYS.between(
            habit.getCreatedAt().toLocalDate(), checkDate) + 1;
        double rate = (double) stats.getTotalDays() / totalDaysSinceCreation * 100;
        stats.setCompleteRate(BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP));
        
        statisticsRepository.save(stats);
    }

    private void recalculateStatistics(Habit habit) {
        User user = habit.getUser();
        List<CheckRecord> records = checkRecordRepository.findByHabitId(habit.getId());
        
        Optional<Statistics> optStats = statisticsRepository.findByUserIdAndHabitId(user.getId(), habit.getId());
        Statistics stats = optStats.orElseGet(() -> {
            Statistics s = new Statistics();
            s.setUser(user);
            s.setHabit(habit);
            return s;
        });
        
        stats.setTotalDays(records.size());
        
        if (records.isEmpty()) {
            stats.setStreakDays(0);
            stats.setMaxStreak(0);
            stats.setLastCheckDate(null);
            stats.setCompleteRate(BigDecimal.ZERO);
        } else {
            records.sort((a, b) -> b.getCheckDate().compareTo(a.getCheckDate()));
            stats.setLastCheckDate(records.get(0).getCheckDate());
            
            int maxStreak = 1;
            int currentStreak = 1;
            for (int i = 1; i < records.size(); i++) {
                if (records.get(i - 1).getCheckDate().minusDays(1).equals(records.get(i).getCheckDate())) {
                    currentStreak++;
                    maxStreak = Math.max(maxStreak, currentStreak);
                } else {
                    currentStreak = 1;
                }
            }
            
            if (records.get(0).getCheckDate().equals(LocalDate.now()) ||
                records.get(0).getCheckDate().equals(LocalDate.now().minusDays(1))) {
                stats.setStreakDays(currentStreak);
            } else {
                stats.setStreakDays(0);
            }
            stats.setMaxStreak(maxStreak);
            
            long totalDaysSinceCreation = java.time.temporal.ChronoUnit.DAYS.between(
                habit.getCreatedAt().toLocalDate(), LocalDate.now()) + 1;
            double rate = (double) stats.getTotalDays() / totalDaysSinceCreation * 100;
            stats.setCompleteRate(BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP));
        }
        
        statisticsRepository.save(stats);
    }

    private User getCurrentUser() {
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userDetails.getId()));
    }
}
