package com.example.backend.config;

import com.example.backend.entity.Achievement;
import com.example.backend.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AchievementDataInitializer {

    private final AchievementRepository achievementRepository;

    @PostConstruct
    public void initAchievements() {
        if (achievementRepository.count() > 0) return;

        List<Achievement> list = List.of(
                new Achievement(null, "FIRST_CHECK", "初次打卡", "完成你的第一次打卡。", "🎉", Achievement.ConditionType.TOTAL_CHECKS, 1, null, null),
                new Achievement(null, "CHECK_7", "坚持一周", "累计打卡达到 7 次。", "✅", Achievement.ConditionType.TOTAL_CHECKS, 7, null, null),
                new Achievement(null, "CHECK_30", "坚持一月", "累计打卡达到 30 次。", "🏅", Achievement.ConditionType.TOTAL_CHECKS, 30, null, null),
                new Achievement(null, "STREAK_3", "连击 3 天", "单个习惯最大连击达到 3 天。", "🔥", Achievement.ConditionType.MAX_STREAK, 3, null, null),
                new Achievement(null, "STREAK_7", "连击 7 天", "单个习惯最大连击达到 7 天。", "🔥", Achievement.ConditionType.MAX_STREAK, 7, null, null),
                new Achievement(null, "STREAK_21", "连击 21 天", "单个习惯最大连击达到 21 天。", "🔥", Achievement.ConditionType.MAX_STREAK, 21, null, null),
                new Achievement(null, "HABITS_3", "三件习惯", "拥有 3 个未删除的习惯。", "🧩", Achievement.ConditionType.TOTAL_HABITS, 3, null, null),
                new Achievement(null, "HABITS_10", "十件习惯", "拥有 10 个未删除的习惯。", "🧠", Achievement.ConditionType.TOTAL_HABITS, 10, null, null)
        );

        achievementRepository.saveAll(list);
    }
}

