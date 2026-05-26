package com.example.backend.service;

import com.example.backend.dto.CreateHabitFromTemplateRequest;
import com.example.backend.dto.HabitDTO;
import com.example.backend.dto.HabitTemplateDTO;
import com.example.backend.entity.Category;
import com.example.backend.entity.Habit;
import com.example.backend.entity.HabitTemplate;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.HabitRepository;
import com.example.backend.repository.HabitTemplateRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitTemplateService {

    private final HabitTemplateRepository habitTemplateRepository;
    private final HabitRepository habitRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AchievementService achievementService;

    public List<HabitTemplateDTO> getTemplates(String keyword, String categoryName, String repeatType) {
        String kw = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        String cat = (categoryName == null || categoryName.trim().isEmpty()) ? null : categoryName.trim();
        Habit.RepeatType rt = (repeatType == null || repeatType.trim().isEmpty()) ? null : Habit.RepeatType.valueOf(repeatType.trim());
        return habitTemplateRepository.search(kw, cat, rt).stream()
                .map(HabitTemplateDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public HabitDTO createHabitFromTemplate(Long templateId, CreateHabitFromTemplateRequest request) {
        User user = getCurrentUser();
        HabitTemplate template = habitTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("习惯模板", "id", templateId));

        Habit habit = new Habit();
        habit.setName(request.getName() != null && !request.getName().trim().isEmpty() ? request.getName().trim() : template.getName());
        habit.setDescription(template.getDescription());
        habit.setIcon(template.getIcon());
        habit.setColor(template.getColor());
        habit.setRepeatType(template.getRepeatType());
        habit.setRepeatDays(template.getRepeatDays());

        LocalTime reminderTime = request.getReminderTime() != null ? request.getReminderTime() : template.getReminderTime();
        habit.setReminderTime(reminderTime);
        if (request.getReminderEnabled() != null) {
            habit.setReminderEnabled(request.getReminderEnabled());
        } else {
            habit.setReminderEnabled(reminderTime != null);
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

    private User getCurrentUser() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userDetails.getId()));
    }
}
