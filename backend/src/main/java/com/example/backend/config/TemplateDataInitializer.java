package com.example.backend.config;

import com.example.backend.entity.Habit;
import com.example.backend.entity.HabitTemplate;
import com.example.backend.repository.HabitTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TemplateDataInitializer {

    private final HabitTemplateRepository habitTemplateRepository;

    @PostConstruct
    public void initTemplates() {
        if (habitTemplateRepository.count() > 0) return;

        List<HabitTemplate> templates = List.of(
                new HabitTemplate(null, "喝水 8 杯", "每天分时补水，提升专注与状态。", "💧", "#3b82f6", Habit.RepeatType.DAILY, null, LocalTime.of(10, 0), "健康", "#10b981", "健康,基础,日常", null, null),
                new HabitTemplate(null, "晨间拉伸 10 分钟", "起床后做拉伸，缓解僵硬，开启一天。", "🧘", "#10b981", Habit.RepeatType.DAILY, null, LocalTime.of(7, 30), "健康", "#10b981", "健康,运动,晨间", null, null),
                new HabitTemplate(null, "阅读 20 分钟", "每天阅读 20 分钟，积累长期收益。", "📚", "#f59e0b", Habit.RepeatType.DAILY, null, LocalTime.of(21, 0), "成长", "#6366f1", "成长,阅读,专注", null, null),
                new HabitTemplate(null, "早睡（23:30 前）", "规律作息，恢复精力。", "🌙", "#8b5cf6", Habit.RepeatType.DAILY, null, LocalTime.of(23, 0), "健康", "#10b981", "健康,作息,自律", null, null),
                new HabitTemplate(null, "运动 30 分钟", "跑步/力量/有氧任选其一。", "🏃", "#ef4444", Habit.RepeatType.WEEKLY, "1,2,3,4,5,6,0", LocalTime.of(19, 0), "运动", "#ef4444", "运动,燃脂,体能", null, null),
                new HabitTemplate(null, "番茄钟学习 2 次", "每次 25 分钟，结束做 5 分钟休息。", "⏱️", "#111827", Habit.RepeatType.DAILY, null, LocalTime.of(9, 0), "学习", "#111827", "学习,效率,专注", null, null),
                new HabitTemplate(null, "复盘（今日三件事）", "写下完成的三件事与改进点。", "📝", "#6b7280", Habit.RepeatType.DAILY, null, LocalTime.of(22, 30), "成长", "#6366f1", "成长,反思,记录", null, null),
                new HabitTemplate(null, "无糖饮料日", "今天不喝含糖饮料。", "🥤", "#22c55e", Habit.RepeatType.WEEKLY, "1,3,5", LocalTime.of(12, 0), "健康", "#10b981", "健康,饮食,挑战", null, null),
                new HabitTemplate(null, "冥想 5 分钟", "随时可做，降低焦虑。", "🧠", "#0ea5e9", Habit.RepeatType.DAILY, null, LocalTime.of(13, 0), "情绪", "#0ea5e9", "情绪,放松,专注", null, null)
        );

        try {
            habitTemplateRepository.saveAll(templates);
        } catch (Exception e) {
            log.warn("初始化习惯模板失败: {}", e.getMessage());
        }
    }
}
