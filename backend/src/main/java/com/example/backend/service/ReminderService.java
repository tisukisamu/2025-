package com.example.backend.service;

import com.example.backend.dto.ReminderDTO;
import com.example.backend.entity.CheckRecord;
import com.example.backend.entity.Habit;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CheckRecordRepository;
import com.example.backend.repository.HabitRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final HabitRepository habitRepository;
    private final CheckRecordRepository checkRecordRepository;
    private final UserRepository userRepository;

    public List<ReminderDTO> getTodayReminders(Boolean onlyPending) {
        User user = getCurrentUser();
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        String dayName = String.valueOf(dayOfWeek.getValue() % 7);
        List<Habit> todayHabits = habitRepository.findTodayHabitsByUserId(user.getId(), dayName);
        List<CheckRecord> todayRecords = checkRecordRepository.findByUserIdAndCheckDate(user.getId(), today);
        Set<Long> checkedIds = todayRecords.stream().map(r -> r.getHabit().getId()).collect(Collectors.toSet());

        return todayHabits.stream()
                .filter(h -> Boolean.TRUE.equals(h.getReminderEnabled()))
                .filter(h -> h.getReminderTime() != null)
                .filter(h -> !Boolean.TRUE.equals(onlyPending) || !checkedIds.contains(h.getId()))
                .sorted(Comparator.comparing(Habit::getReminderTime))
                .map(h -> ReminderDTO.builder()
                        .habitId(h.getId())
                        .habitName(h.getName())
                        .icon(h.getIcon())
                        .color(h.getColor())
                        .reminderTime(h.getReminderTime())
                        .checked(checkedIds.contains(h.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    public java.util.Map<String, Object> getTodaySummary() {
        List<ReminderDTO> all = getTodayReminders(false);
        long completed = all.stream().filter(ReminderDTO::getChecked).count();
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("total", all.size());
        map.put("completed", completed);
        map.put("pending", all.size() - completed);
        return map;
    }

    private User getCurrentUser() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userDetails.getId()));
    }
}
