package com.example.backend.service;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.dto.HabitDTO;
import com.example.backend.dto.CreateHabitRequest;
import com.example.backend.dto.UpdateHabitRequest;
import com.example.backend.entity.Category;
import com.example.backend.entity.Habit;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.HabitRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AchievementService achievementService;

    public List<HabitDTO> getHabits(Long categoryId, String status) {
        User user = getCurrentUser();
        List<Habit> habits;
        
        if (categoryId != null && status != null) {
            habits = habitRepository.findByUserIdAndCategoryIdAndStatus(
                user.getId(), categoryId, Habit.Status.valueOf(status));
        } else if (categoryId != null) {
            habits = habitRepository.findByUserIdAndCategoryId(user.getId(), categoryId);
        } else if (status != null) {
            habits = habitRepository.findByUserIdAndStatus(user.getId(), Habit.Status.valueOf(status));
        } else {
            habits = habitRepository.findByUserId(user.getId());
        }
        
        return habits.stream()
            .map(HabitDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public List<HabitDTO> getTodayHabits() {
        User user = getCurrentUser();
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        String dayName = String.valueOf(dayOfWeek.getValue() % 7);
        
        List<Habit> habits = habitRepository.findTodayHabitsByUserId(user.getId(), dayName);
        return habits.stream()
            .map(HabitDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public HabitDTO getHabitById(Long id) {
        Habit habit = habitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("习惯", "id", id));
        
        if (!habit.getUser().getId().equals(getCurrentUser().getId())) {
            throw new BusinessException(403, "无权访问该习惯");
        }
        
        return HabitDTO.fromEntity(habit);
    }

    @Transactional
    public HabitDTO createHabit(CreateHabitRequest request) {
        User user = getCurrentUser();
        
        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        habit.setIcon(request.getIcon());
        habit.setColor(request.getColor());
        habit.setRepeatType(Habit.RepeatType.valueOf(request.getRepeatType()));
        habit.setRepeatDays(request.getRepeatDays());
        habit.setReminderTime(request.getReminderTime());
        if (request.getReminderEnabled() != null) {
            habit.setReminderEnabled(request.getReminderEnabled());
        } else {
            habit.setReminderEnabled(request.getReminderTime() != null);
        }
        habit.setUser(user);
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("分类", "id", request.getCategoryId()));
            habit.setCategory(category);
        }
        
        Habit saved = habitRepository.save(habit);
        achievementService.evaluateForCurrentUser();
        return HabitDTO.fromEntity(saved);
    }

    @Transactional
    public HabitDTO updateHabit(Long id, UpdateHabitRequest request) {
        Habit habit = habitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("习惯", "id", id));
        
        if (!habit.getUser().getId().equals(getCurrentUser().getId())) {
            throw new BusinessException(403, "无权修改该习惯");
        }
        
        if (request.getName() != null) {
            habit.setName(request.getName());
        }
        if (request.getDescription() != null) {
            habit.setDescription(request.getDescription());
        }
        if (request.getIcon() != null) {
            habit.setIcon(request.getIcon());
        }
        if (request.getColor() != null) {
            habit.setColor(request.getColor());
        }
        if (request.getRepeatType() != null) {
            habit.setRepeatType(Habit.RepeatType.valueOf(request.getRepeatType()));
        }
        if (request.getRepeatDays() != null) {
            habit.setRepeatDays(request.getRepeatDays());
        }
        if (request.getReminderTime() != null) {
            habit.setReminderTime(request.getReminderTime());
        }
        if (request.getReminderEnabled() != null) {
            habit.setReminderEnabled(request.getReminderEnabled());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("分类", "id", request.getCategoryId()));
            habit.setCategory(category);
        }
        
        Habit saved = habitRepository.save(habit);
        return HabitDTO.fromEntity(saved);
    }

    @Transactional
    public void deleteHabit(Long id) {
        Habit habit = habitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("习惯", "id", id));
        
        if (!habit.getUser().getId().equals(getCurrentUser().getId())) {
            throw new BusinessException(403, "无权删除该习惯");
        }
        
        habit.setStatus(Habit.Status.DELETED);
        habitRepository.save(habit);
    }

    @Transactional
    public void pauseHabit(Long id) {
        Habit habit = habitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("习惯", "id", id));
        
        if (!habit.getUser().getId().equals(getCurrentUser().getId())) {
            throw new BusinessException(403, "无权操作该习惯");
        }
        
        habit.setStatus(Habit.Status.PAUSED);
        habitRepository.save(habit);
    }

    @Transactional
    public void resumeHabit(Long id) {
        Habit habit = habitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("习惯", "id", id));
        
        if (!habit.getUser().getId().equals(getCurrentUser().getId())) {
            throw new BusinessException(403, "无权操作该习惯");
        }
        
        habit.setStatus(Habit.Status.ACTIVE);
        habitRepository.save(habit);
    }

    @Transactional
    public int batchUpdateStatus(List<Long> ids, String action) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "请选择至少一个习惯");
        }
        if (action == null || action.trim().isEmpty()) {
            throw new BusinessException(400, "操作类型不能为空");
        }

        User user = getCurrentUser();
        String op = action.trim().toUpperCase();
        List<Habit> changed = new ArrayList<>();

        for (Long id : ids) {
            Habit habit = habitRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("习惯", "id", id));
            if (!habit.getUser().getId().equals(user.getId())) {
                continue;
            }
            if ("PAUSE".equals(op)) {
                habit.setStatus(Habit.Status.PAUSED);
            } else if ("RESUME".equals(op)) {
                habit.setStatus(Habit.Status.ACTIVE);
            } else if ("DELETE".equals(op)) {
                habit.setStatus(Habit.Status.DELETED);
            } else {
                throw new BusinessException(400, "不支持的批量操作");
            }
            changed.add(habit);
        }

        if (!changed.isEmpty()) {
            habitRepository.saveAll(changed);
        }
        return changed.size();
    }

    private User getCurrentUser() {
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userDetails.getId()));
    }
}
