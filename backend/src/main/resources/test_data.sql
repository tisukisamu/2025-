-- =============================================
-- 个人习惯养成打卡系统 - 测试数据
-- 数据库: MySQL 8.0+
-- =============================================
use gp6;
-- 清空现有数据（按外键依赖顺序删除）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE check_records;
TRUNCATE TABLE habits;
TRUNCATE TABLE categories;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 1. 用户数据 (密码均为: 123456)
-- BCrypt加密后的哈希值
-- =============================================
INSERT INTO users (id, username, password, name, email, age, role, status, avatar, created_at, updated_at) VALUES
(1, 'admin', '$2a$10$EqKcp1WFKVQISheBxmXJGePJwJbvHfEFvEqJjGWQv2Mb6AqPQvWIi', '系统管理员', 'admin@habitflow.com', 28, 'ADMIN', 'ACTIVE', NULL, NOW(), NOW()),
(2, 'zhangsan', '$2a$10$EqKcp1WFKVQISheBxmXJGePJwJbvHfEFvEqJjGWQv2Mb6AqPQvWIi', '张三', 'zhangsan@example.com', 25, 'USER', 'ACTIVE', NULL, NOW(), NOW()),
(3, 'lisi', '$2a$10$EqKcp1WFKVQISheBxmXJGePJwJbvHfEFvEqJjGWQv2Mb6AqPQvWIi', '李四', 'lisi@example.com', 30, 'USER', 'ACTIVE', NULL, NOW(), NOW()),
(4, 'wangwu', '$2a$10$EqKcp1WFKVQISheBxmXJGePJwJbvHfEFvEqJjGWQv2Mb6AqPQvWIi', '王五', 'wangwu@example.com', 22, 'USER', 'DISABLED', NULL, NOW(), NOW());

-- =============================================
-- 2. 分类数据
-- =============================================
INSERT INTO categories (id, name, icon, color, sort_order, user_id, created_at) VALUES
-- 张三的分类
(1, '健康运动', '🏃', '#10B981', 1, 2, NOW()),
(2, '学习成长', '📚', '#3B82F6', 2, 2, NOW()),
(3, '生活作息', '🌙', '#8B5CF6', 3, 2, NOW()),
(4, '工作效率', '💼', '#F59E0B', 4, 2, NOW()),
-- 李四的分类
(5, '健身', '💪', '#EF4444', 1, 3, NOW()),
(6, '阅读', '📖', '#06B6D4', 2, 3, NOW()),
-- 管理员测试分类
(7, '默认分类', '⭐', '#111111', 0, 1, NOW());

-- =============================================
-- 3. 习惯数据
-- =============================================
INSERT INTO habits (id, name, description, icon, color, repeat_type, repeat_days, reminder_time, status, user_id, category_id, created_at, updated_at) VALUES
-- 张三的习惯
(1, '晨跑30分钟', '每天早上6点起床跑步，保持身体健康', '🏃', '#10B981', 'DAILY', NULL, '06:00:00', 'ACTIVE', 2, 1, NOW(), NOW()),
(2, '阅读1小时', '每天阅读一小时，拓展知识面', '📚', '#3B82F6', 'DAILY', NULL, '21:00:00', 'ACTIVE', 2, 2, NOW(), NOW()),
(3, '早睡早起', '晚上11点前睡觉，早上6点起床', '🌙', '#8B5CF6', 'DAILY', NULL, '23:00:00', 'ACTIVE', 2, 3, NOW(), NOW()),
(4, '冥想15分钟', '每天冥想放松心情', '🧘', '#F59E0B', 'DAILY', NULL, '07:00:00', 'ACTIVE', 2, 3, NOW(), NOW()),
(5, '学习英语', '每天学习英语30分钟', '🔤', '#3B82F6', 'DAILY', NULL, '12:30:00', 'ACTIVE', 2, 2, NOW(), NOW()),
(6, '健身训练', '每周一三五去健身房', '💪', '#10B981', 'WEEKLY', '1,3,5', '18:00:00', 'ACTIVE', 2, 1, NOW(), NOW()),
(7, '写日记', '每周日写周记总结', '📝', '#8B5CF6', 'WEEKLY', '0', '20:00:00', 'ACTIVE', 2, 3, NOW(), NOW()),
(8, '整理工作台', '每周五整理工作区域', '🧹', '#F59E0B', 'WEEKLY', '5', '17:00:00', 'PAUSED', 2, 4, NOW(), NOW()),
-- 李四的习惯
(9, '俯卧撑50个', '每天做50个俯卧撑', '💪', '#EF4444', 'DAILY', NULL, '07:30:00', 'ACTIVE', 3, 5, NOW(), NOW()),
(10, '深蹲100个', '每天做100个深蹲', '🦵', '#EF4444', 'DAILY', NULL, '07:45:00', 'ACTIVE', 3, 5, NOW(), NOW()),
(11, '读书打卡', '每天读书至少30页', '📖', '#06B6D4', 'DAILY', NULL, '22:00:00', 'ACTIVE', 3, 6, NOW(), NOW()),
(12, '周末骑行', '每周六骑行20公里', '🚴', '#EF4444', 'WEEKLY', '6', '08:00:00', 'ACTIVE', 3, 5, NOW(), NOW()),
-- 管理员的测试习惯
(13, '每日复盘', '每天晚上复盘当天工作', '📋', '#111111', 'DAILY', NULL, '22:00:00', 'ACTIVE', 1, 7, NOW(), NOW());

-- =============================================
-- 4. 打卡记录数据 (最近30天的记录)
-- =============================================

-- 张三的打卡记录 - 晨跑 (连续打卡)
INSERT INTO check_records (habit_id, user_id, check_date, check_time, note, created_at)
SELECT 1, 2, DATE_SUB(CURDATE(), INTERVAL n DAY), '06:15:00', CONCAT('第', 30-n+1, '天晨跑完成'), DATE_SUB(NOW(), INTERVAL n DAY)
FROM (
    SELECT 0 as n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6
    UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13
    UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
    UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION SELECT 26 UNION SELECT 27
    UNION SELECT 28 UNION SELECT 29
) days;

-- 张三的打卡记录 - 阅读 (大部分打卡，有几天漏掉)
INSERT INTO check_records (habit_id, user_id, check_date, check_time, note, created_at)
SELECT 2, 2, DATE_SUB(CURDATE(), INTERVAL n DAY), '21:30:00', '阅读完成', DATE_SUB(NOW(), INTERVAL n DAY)
FROM (
    SELECT 0 as n UNION SELECT 1 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 7 UNION SELECT 8
    UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 14 UNION SELECT 15 UNION SELECT 17 UNION SELECT 18
    UNION SELECT 19 UNION SELECT 21 UNION SELECT 22 UNION SELECT 24 UNION SELECT 25 UNION SELECT 26 UNION SELECT 28 UNION SELECT 29
) days;

-- 张三的打卡记录 - 早睡早起 (部分打卡)
INSERT INTO check_records (habit_id, user_id, check_date, check_time, note, created_at)
SELECT 3, 2, DATE_SUB(CURDATE(), INTERVAL n DAY), '22:45:00', '按时睡觉', DATE_SUB(NOW(), INTERVAL n DAY)
FROM (
    SELECT 0 as n UNION SELECT 1 UNION SELECT 2 UNION SELECT 5 UNION SELECT 6 UNION SELECT 9 UNION SELECT 10
    UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 18 UNION SELECT 19 UNION SELECT 22 UNION SELECT 23
    UNION SELECT 26 UNION SELECT 27 UNION SELECT 28
) days;

-- 张三的打卡记录 - 冥想 (最近一周开始)
INSERT INTO check_records (habit_id, user_id, check_date, check_time, note, created_at)
SELECT 4, 2, DATE_SUB(CURDATE(), INTERVAL n DAY), '07:10:00', '冥想完成', DATE_SUB(NOW(), INTERVAL n DAY)
FROM (
    SELECT 0 as n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6
) days;

-- 张三的打卡记录 - 学习英语 (工作日打卡)
INSERT INTO check_records (habit_id, user_id, check_date, check_time, note, created_at)
SELECT 5, 2, DATE_SUB(CURDATE(), INTERVAL n DAY), '12:45:00', '英语学习完成', DATE_SUB(NOW(), INTERVAL n DAY)
FROM (
    SELECT 0 as n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 7 UNION SELECT 8
    UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17
    UNION SELECT 18 UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION SELECT 28
) days;

-- 张三的打卡记录 - 健身训练 (周一三五)
INSERT INTO check_records (habit_id, user_id, check_date, check_time, note, created_at)
SELECT 6, 2, DATE_SUB(CURDATE(), INTERVAL n DAY), '18:30:00', '健身训练完成', DATE_SUB(NOW(), INTERVAL n DAY)
FROM (
    SELECT 0 as n UNION SELECT 2 UNION SELECT 5 UNION SELECT 7 UNION SELECT 9 UNION SELECT 12 UNION SELECT 14
    UNION SELECT 16 UNION SELECT 19 UNION SELECT 21 UNION SELECT 23 UNION SELECT 26 UNION SELECT 28
) days;

-- 张三的打卡记录 - 写日记 (周日)
INSERT INTO check_records (habit_id, user_id, check_date, check_time, note, created_at)
SELECT 7, 2, DATE_SUB(CURDATE(), INTERVAL n DAY), '20:30:00', '周记完成', DATE_SUB(NOW(), INTERVAL n DAY)
FROM (
    SELECT 0 as n UNION SELECT 7 UNION SELECT 14 UNION SELECT 21 UNION SELECT 28
) days;

-- 李四的打卡记录 - 俯卧撑 (连续打卡)
INSERT INTO check_records (habit_id, user_id, check_date, check_time, note, created_at)
SELECT 9, 3, DATE_SUB(CURDATE(), INTERVAL n DAY), '07:35:00', '俯卧撑完成', DATE_SUB(NOW(), INTERVAL n DAY)
FROM (
    SELECT 0 as n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6
    UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13
    UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
    UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION SELECT 26 UNION SELECT 27
    UNION SELECT 28 UNION SELECT 29
) days;

-- 李四的打卡记录 - 深蹲 (大部分打卡)
INSERT INTO check_records (habit_id, user_id, check_date, check_time, note, created_at)
SELECT 10, 3, DATE_SUB(CURDATE(), INTERVAL n DAY), '08:00:00', '深蹲完成', DATE_SUB(NOW(), INTERVAL n DAY)
FROM (
    SELECT 0 as n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7
    UNION SELECT 8 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 15 UNION SELECT 16
    UNION SELECT 17 UNION SELECT 18 UNION SELECT 20 UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 25
    UNION SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29
) days;

-- 李四的打卡记录 - 读书打卡
INSERT INTO check_records (habit_id, user_id, check_date, check_time, note, created_at)
SELECT 11, 3, DATE_SUB(CURDATE(), INTERVAL n DAY), '22:15:00', '读书打卡', DATE_SUB(NOW(), INTERVAL n DAY)
FROM (
    SELECT 0 as n UNION SELECT 1 UNION SELECT 3 UNION SELECT 4 UNION SELECT 6 UNION SELECT 8 UNION SELECT 9
    UNION SELECT 11 UNION SELECT 12 UNION SELECT 14 UNION SELECT 15 UNION SELECT 17 UNION SELECT 18 UNION SELECT 20
    UNION SELECT 22 UNION SELECT 23 UNION SELECT 25 UNION SELECT 26 UNION SELECT 28 UNION SELECT 29
) days;

-- 李四的打卡记录 - 周末骑行 (周六)
INSERT INTO check_records (habit_id, user_id, check_date, check_time, note, created_at)
SELECT 12, 3, DATE_SUB(CURDATE(), INTERVAL n DAY), '09:00:00', '骑行完成', DATE_SUB(NOW(), INTERVAL n DAY)
FROM (
    SELECT 1 as n UNION SELECT 8 UNION SELECT 15 UNION SELECT 22 UNION SELECT 29
) days;

-- 管理员的打卡记录 - 每日复盘
INSERT INTO check_records (habit_id, user_id, check_date, check_time, note, created_at)
SELECT 13, 1, DATE_SUB(CURDATE(), INTERVAL n DAY), '22:30:00', '复盘完成', DATE_SUB(NOW(), INTERVAL n DAY)
FROM (
    SELECT 0 as n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6
    UNION SELECT 7 UNION SELECT 8 UNION SELECT 9
) days;

-- =============================================
-- 验证数据插入
-- =============================================
SELECT '用户数量' as '统计', COUNT(*) as '数量' FROM users
UNION ALL
SELECT '分类数量', COUNT(*) FROM categories
UNION ALL
SELECT '习惯数量', COUNT(*) FROM habits
UNION ALL
SELECT '打卡记录数量', COUNT(*) FROM check_records;

-- 显示用户习惯统计
SELECT 
    u.name as '用户名',
    COUNT(DISTINCT h.id) as '习惯数',
    COUNT(c.id) as '打卡记录数'
FROM users u
LEFT JOIN habits h ON u.id = h.user_id
LEFT JOIN check_records c ON h.id = c.habit_id
GROUP BY u.id, u.name
ORDER BY u.id;
