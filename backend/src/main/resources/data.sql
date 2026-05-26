-- ============================================
-- 街舞工作室管理系统 - 测试数据
-- ============================================

-- 清空所有表（按依赖关系倒序）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE bills;
TRUNCATE TABLE payments;
TRUNCATE TABLE enrollments;
TRUNCATE TABLE schedules;
TRUNCATE TABLE courses;
TRUNCATE TABLE students;
TRUNCATE TABLE teachers;
TRUNCATE TABLE notifications;
TRUNCATE TABLE settings;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 1. 用户表 (users)
-- ============================================
INSERT INTO users (username, password, email, phone, real_name, avatar, role, status, created_at, updated_at) VALUES
-- 管理员
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@dancestudio.com', '13800000001', '系统管理员', NULL, 'ADMIN', 'ACTIVE', NOW(), NOW()),

-- 教师
('teacher001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'zhangsan@dancestudio.com', '13800000002', '张三', NULL, 'TEACHER', 'ACTIVE', NOW(), NOW()),
('teacher002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'lisi@dancestudio.com', '13800000003', '李四', NULL, 'TEACHER', 'ACTIVE', NOW(), NOW()),
('teacher003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'wangwu@dancestudio.com', '13800000004', '王五', NULL, 'TEACHER', 'ACTIVE', NOW(), NOW()),

-- 学员
('student001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xiaoming@example.com', '13900000001', '小明', NULL, 'STUDENT', 'ACTIVE', NOW(), NOW()),
('student002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xiaohong@example.com', '13900000002', '小红', NULL, 'STUDENT', 'ACTIVE', NOW(), NOW()),
('student003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xiaohua@example.com', '13900000003', '小华', NULL, 'STUDENT', 'ACTIVE', NOW(), NOW()),
('student004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xiaoli@example.com', '13900000004', '小李', NULL, 'STUDENT', 'ACTIVE', NOW(), NOW()),
('student005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'xiaowang@example.com', '13900000005', '小王', NULL, 'STUDENT', 'ACTIVE', NOW(), NOW());

-- ============================================
-- 2. 教师表 (teachers)
-- ============================================
INSERT INTO teachers (user_id, name, phone, email, subjects, experience, bio, avatar, status, created_at, updated_at) VALUES
(2, '张三', '13800000002', 'zhangsan@dancestudio.com', 'Hip-Hop,Urban,Locking', 8, '资深Hip-Hop教练，曾获得多项全国街舞大赛冠军。擅长Hip-Hop、Urban等多种舞种，教学风格轻松活泼，注重基础训练和舞蹈感觉的培养。', NULL, 'ACTIVE', NOW(), NOW()),
(3, '李四', '13800000003', 'lisi@dancestudio.com', 'Jazz,Modern', 6, '专业爵士舞教师，毕业于北京舞蹈学院。教学严谨细致，善于因材施教，帮助学员快速提升舞蹈技巧。', NULL, 'ACTIVE', NOW(), NOW()),
(4, '王五', '13800000004', 'wangwu@dancestudio.com', 'Popping,Breaking', 5, 'Popping和Breaking双修舞者，多次参加国内外街舞比赛并获奖。教学经验丰富，深受学员喜爱。', NULL, 'ACTIVE', NOW(), NOW());

-- ============================================
-- 3. 学员表 (students)
-- ============================================
INSERT INTO students (user_id, name, phone, email, gender, birth_date, emergency_contact, emergency_phone, avatar, status, created_at, updated_at) VALUES
(5, '小明', '13900000001', 'xiaoming@example.com', 'MALE', '1998-05-15', '明爸爸', '13700000001', NULL, 'ACTIVE', NOW(), NOW()),
(6, '小红', '13900000002', 'xiaohong@example.com', 'FEMALE', '2000-08-20', '红妈妈', '13700000002', NULL, 'ACTIVE', NOW(), NOW()),
(7, '小华', '13900000003', 'xiaohua@example.com', 'MALE', '1999-03-10', '华爸爸', '13700000003', NULL, 'ACTIVE', NOW(), NOW()),
(8, '小李', '13900000004', 'xiaoli@example.com', 'FEMALE', '2001-11-25', '李妈妈', '13700000004', NULL, 'ACTIVE', NOW(), NOW()),
(9, '小王', '13900000005', 'xiaowang@example.com', 'MALE', '1997-07-08', '王爸爸', '13700000005', NULL, 'ACTIVE', NOW(), NOW());

-- ============================================
-- 4. 课程表 (courses)
-- ============================================
INSERT INTO courses (name, description, teacher_id, category, level, duration, price, capacity, enrolled_count, image, status, created_at, updated_at) VALUES
('Hip-Hop基础班', '适合零基础学员，从基础动作开始学习，循序渐进掌握Hip-Hop舞蹈技巧。课程内容包括基础律动、基础步伐、身体协调性训练等。', 1, 'Hip-Hop', 'BEGINNER', 24, 1200.00, 20, 18, NULL, 'PUBLISHED', NOW(), NOW()),
('爵士舞进阶班', '需要一定舞蹈基础，学习更复杂的编舞和技巧。课程内容包括身体线条训练、情感表达、编舞技巧等。', 2, 'Jazz', 'INTERMEDIATE', 30, 1500.00, 18, 15, NULL, 'PUBLISHED', NOW(), NOW()),
('Popping入门班', '适合零基础学员，学习Popping基础技巧和肌肉震动控制。', 3, 'Popping', 'BEGINNER', 20, 1000.00, 20, 20, NULL, 'PUBLISHED', NOW(), NOW()),
('Urban编舞班', '学习Urban Dance编舞技巧，提升舞蹈表现力和创造力。适合有一定基础的学员。', 1, 'Urban', 'INTERMEDIATE', 28, 1800.00, 15, 12, NULL, 'PUBLISHED', NOW(), NOW()),
('Breaking基础班', 'Breaking基础动作教学，包括Toprock、Footwork、Freeze等基础技巧。', 3, 'Breaking', 'BEGINNER', 24, 1200.00, 18, 16, NULL, 'PUBLISHED', NOW(), NOW()),
('Locking进阶班', 'Locking进阶技巧，学习更复杂的Locking动作和组合。', 1, 'Locking', 'ADVANCED', 32, 2000.00, 12, 10, NULL, 'PUBLISHED', NOW(), NOW());

-- ============================================
-- 5. 排课表 (schedules)
-- ============================================
INSERT INTO schedules (course_id, teacher_id, start_time, end_time, location, room, is_recurring, recurrence_pattern, status, created_at, updated_at) VALUES
-- Hip-Hop基础班
(1, 1, '2024-02-20 18:00:00', '2024-02-20 19:30:00', '街舞工作室', 'A教室', TRUE, 'WEEKLY', 'SCHEDULED', NOW(), NOW()),
(1, 1, '2024-02-22 18:00:00', '2024-02-22 19:30:00', '街舞工作室', 'A教室', TRUE, 'WEEKLY', 'SCHEDULED', NOW(), NOW()),

-- 爵士舞进阶班
(2, 2, '2024-02-20 19:30:00', '2024-02-20 21:00:00', '街舞工作室', 'B教室', TRUE, 'WEEKLY', 'SCHEDULED', NOW(), NOW()),
(2, 2, '2024-02-23 19:30:00', '2024-02-23 21:00:00', '街舞工作室', 'B教室', TRUE, 'WEEKLY', 'SCHEDULED', NOW(), NOW()),

-- Popping入门班
(3, 3, '2024-02-21 18:00:00', '2024-02-21 19:30:00', '街舞工作室', 'C教室', TRUE, 'WEEKLY', 'SCHEDULED', NOW(), NOW()),
(3, 3, '2024-02-24 18:00:00', '2024-02-24 19:30:00', '街舞工作室', 'C教室', TRUE, 'WEEKLY', 'SCHEDULED', NOW(), NOW()),

-- Urban编舞班
(4, 1, '2024-02-21 19:30:00', '2024-02-21 21:00:00', '街舞工作室', 'A教室', TRUE, 'WEEKLY', 'SCHEDULED', NOW(), NOW()),
(4, 1, '2024-02-25 19:30:00', '2024-02-25 21:00:00', '街舞工作室', 'A教室', TRUE, 'WEEKLY', 'SCHEDULED', NOW(), NOW()),

-- Breaking基础班
(5, 3, '2024-02-22 19:30:00', '2024-02-22 21:00:00', '街舞工作室', 'B教室', TRUE, 'WEEKLY', 'SCHEDULED', NOW(), NOW()),

-- Locking进阶班
(6, 1, '2024-02-23 18:00:00', '2024-02-23 19:30:00', '街舞工作室', 'A教室', TRUE, 'WEEKLY', 'SCHEDULED', NOW(), NOW());

-- ============================================
-- 6. 报名表 (enrollments)
-- ============================================
INSERT INTO enrollments (student_id, course_id, schedule_id, enrollment_date, status, payment_status, cancel_reason, cancelled_at, created_at, updated_at) VALUES
-- 小明的报名
(1, 1, 1, '2024-01-15 10:00:00', 'CONFIRMED', 'PAID', NULL, NULL, NOW(), NOW()),
(1, 4, 7, '2024-02-01 14:00:00', 'CONFIRMED', 'PAID', NULL, NULL, NOW(), NOW()),

-- 小红的报名
(2, 2, 3, '2024-01-20 11:00:00', 'CONFIRMED', 'PAID', NULL, NULL, NOW(), NOW()),
(2, 1, 1, '2024-02-05 09:00:00', 'CONFIRMED', 'PAID', NULL, NULL, NOW(), NOW()),

-- 小华的报名
(3, 3, 5, '2024-01-25 15:00:00', 'CONFIRMED', 'PAID', NULL, NULL, NOW(), NOW()),
(3, 5, 9, '2024-02-10 16:00:00', 'CONFIRMED', 'PAID', NULL, NULL, NOW(), NOW()),

-- 小李的报名
(4, 2, 3, '2024-02-01 10:00:00', 'CONFIRMED', 'PAID', NULL, NULL, NOW(), NOW()),
(4, 6, 10, '2024-02-15 11:00:00', 'PENDING', 'UNPAID', NULL, NULL, NOW(), NOW()),

-- 小王的报名
(5, 1, 1, '2024-02-05 14:00:00', 'CONFIRMED', 'PAID', NULL, NULL, NOW(), NOW()),
(5, 4, 7, '2024-02-12 15:00:00', 'CONFIRMED', 'PAID', NULL, NULL, NOW(), NOW());

-- ============================================
-- 7. 支付表 (payments)
-- ============================================
INSERT INTO payments (enrollment_id, amount, payment_method, transaction_id, status, payment_time, created_at, updated_at) VALUES
-- 小明的支付
(1, 1200.00, 'WECHAT', 'WX20240115100000001', 'SUCCESS', '2024-01-15 10:05:00', NOW(), NOW()),
(2, 1800.00, 'ALIPAY', 'ALI20240201140000001', 'SUCCESS', '2024-02-01 14:10:00', NOW(), NOW()),

-- 小红的支付
(3, 1500.00, 'WECHAT', 'WX20240120110000001', 'SUCCESS', '2024-01-20 11:05:00', NOW(), NOW()),
(4, 1200.00, 'ALIPAY', 'ALI20240205090000001', 'SUCCESS', '2024-02-05 09:10:00', NOW(), NOW()),

-- 小华的支付
(5, 1000.00, 'WECHAT', 'WX20240125150000001', 'SUCCESS', '2024-01-25 15:05:00', NOW(), NOW()),
(6, 1200.00, 'CASH', NULL, 'SUCCESS', '2024-02-10 16:05:00', NOW(), NOW()),

-- 小李的支付
(7, 1500.00, 'ALIPAY', 'ALI20240201100000001', 'SUCCESS', '2024-02-01 10:05:00', NOW(), NOW()),
(8, 2000.00, 'WECHAT', 'WX20240215110000001', 'PENDING', NULL, NOW(), NOW()),

-- 小王的支付
(9, 1200.00, 'WECHAT', 'WX20240205140000001', 'SUCCESS', '2024-02-05 14:05:00', NOW(), NOW()),
(10, 1800.00, 'ALIPAY', 'ALI20240212150000001', 'SUCCESS', '2024-02-12 15:10:00', NOW(), NOW());

-- ============================================
-- 8. 账单表 (bills)
-- ============================================
INSERT INTO bills (student_id, bill_type, amount, description, due_date, status, payment_id, created_at, updated_at) VALUES
-- 小明的账单
(1, 'TUITION', 1200.00, 'Hip-Hop基础班学费', '2024-01-20', 'PAID', 1, NOW(), NOW()),
(1, 'TUITION', 1800.00, 'Urban编舞班学费', '2024-02-05', 'PAID', 2, NOW(), NOW()),

-- 小红的账单
(2, 'TUITION', 1500.00, '爵士舞进阶班学费', '2024-01-25', 'PAID', 3, NOW(), NOW()),
(2, 'TUITION', 1200.00, 'Hip-Hop基础班学费', '2024-02-10', 'PAID', 4, NOW(), NOW()),

-- 小华的账单
(3, 'TUITION', 1000.00, 'Popping入门班学费', '2024-01-30', 'PAID', 5, NOW(), NOW()),
(3, 'TUITION', 1200.00, 'Breaking基础班学费', '2024-02-15', 'PAID', 6, NOW(), NOW()),

-- 小李的账单
(4, 'TUITION', 1500.00, '爵士舞进阶班学费', '2024-02-05', 'PAID', 7, NOW(), NOW()),
(4, 'TUITION', 2000.00, 'Locking进阶班学费', '2024-02-20', 'UNPAID', NULL, NOW(), NOW()),

-- 小王的账单
(5, 'TUITION', 1200.00, 'Hip-Hop基础班学费', '2024-02-10', 'PAID', 9, NOW(), NOW()),
(5, 'TUITION', 1800.00, 'Urban编舞班学费', '2024-02-17', 'PAID', 10, NOW(), NOW()),

-- 注册费
(1, 'REGISTRATION', 100.00, '学员注册费', '2024-01-15', 'PAID', NULL, NOW(), NOW()),
(2, 'REGISTRATION', 100.00, '学员注册费', '2024-01-20', 'PAID', NULL, NOW(), NOW()),
(3, 'REGISTRATION', 100.00, '学员注册费', '2024-01-25', 'PAID', NULL, NOW(), NOW()),
(4, 'REGISTRATION', 100.00, '学员注册费', '2024-02-01', 'PAID', NULL, NOW(), NOW()),
(5, 'REGISTRATION', 100.00, '学员注册费', '2024-02-05', 'PAID', NULL, NOW(), NOW());

-- ============================================
-- 9. 通知表 (notifications)
-- ============================================
INSERT INTO notifications (title, content, type, target_role, is_published, published_at, created_by, created_at, updated_at) VALUES
('系统上线通知', '街舞工作室管理系统正式上线，欢迎使用！', 'SYSTEM', 'ALL', TRUE, NOW(), 1, NOW(), NOW()),
('新课程开放报名', 'Hip-Hop基础班、爵士舞进阶班等多门课程现已开放报名，欢迎学员报名参加！', 'COURSE', 'STUDENT', TRUE, NOW(), 1, NOW(), NOW()),
('教师排课提醒', '请各位教师及时查看自己的排课安排，确保按时上课。', 'COURSE', 'TEACHER', TRUE, NOW(), 1, NOW(), NOW()),
('缴费提醒', '部分学员还有未缴纳的费用，请及时缴费。', 'PAYMENT', 'STUDENT', TRUE, NOW(), 1, NOW(), NOW()),
('系统维护通知', '系统将于本周日凌晨2:00-4:00进行维护升级，届时系统将暂停服务。', 'SYSTEM', 'ALL', TRUE, NOW(), 1, NOW(), NOW()),
('春节放假通知', '春节期间工作室放假时间：2024年2月9日-2月17日，2月18日正常上课。', 'GENERAL', 'ALL', TRUE, NOW(), 1, NOW(), NOW());

-- ============================================
-- 10. 设置表 (settings)
-- ============================================
INSERT INTO settings (setting_key, setting_value, description, created_at, updated_at) VALUES
('system_name', '街舞工作室管理系统', '系统名称', NOW(), NOW()),
('system_logo', '/upload/logo.png', '系统Logo', NOW(), NOW()),
('contact_phone', '400-123-4567', '联系电话', NOW(), NOW()),
('contact_email', 'contact@dancestudio.com', '联系邮箱', NOW(), NOW()),
('contact_address', '北京市朝阳区xxx街道xxx号', '联系地址', NOW(), NOW()),
('business_hours', '周一至周日 9:00-21:00', '营业时间', NOW(), NOW()),
('default_course_duration', '24', '默认课程课时', NOW(), NOW()),
('max_students_per_course', '20', '每门课程最大学员数', NOW(), NOW()),
('allow_trial_class', 'true', '是否允许试听', NOW(), NOW()),
('trial_class_count', '1', '试听课时数', NOW(), NOW()),
('cancel_enrollment_deadline', '24', '取消报名时限（小时）', NOW(), NOW()),
('auto_complete_course', 'true', '是否自动结课', NOW(), NOW()),
('payment_methods', 'CASH,WECHAT,ALIPAY,CARD', '支付方式', NOW(), NOW()),
('deposit_rate', '30', '定金比例（%）', NOW(), NOW()),
('refund_policy', '开课前7天可全额退款，开课前3天退款50%，开课后不予退款', '退款政策', NOW(), NOW()),
('invoice_info', '可开具增值税普通发票', '发票信息', NOW(), NOW()),
('sms_notification_enabled', 'true', '是否启用短信通知', NOW(), NOW()),
('email_notification_enabled', 'true', '是否启用邮件通知', NOW(), NOW()),
('wechat_notification_enabled', 'false', '是否启用微信通知', NOW(), NOW()),
('reminder_hours_before_class', '2', '上课前提醒时间（小时）', NOW(), NOW()),
('min_password_length', '8', '密码最小长度', NOW(), NOW()),
('password_requirements', 'NUMBER,LETTER', '密码强度要求', NOW(), NOW()),
('max_login_attempts', '5', '登录失败锁定次数', NOW(), NOW()),
('lock_duration_minutes', '30', '账户锁定时长（分钟）', NOW(), NOW()),
('force_password_change', 'true', '首次登录强制修改密码', NOW(), NOW());

-- ============================================
-- 完成
-- ============================================
SELECT '测试数据插入完成！' AS message;
SELECT 
    (SELECT COUNT(*) FROM users) AS users_count,
    (SELECT COUNT(*) FROM teachers) AS teachers_count,
    (SELECT COUNT(*) FROM students) AS students_count,
    (SELECT COUNT(*) FROM courses) AS courses_count,
    (SELECT COUNT(*) FROM schedules) AS schedules_count,
    (SELECT COUNT(*) FROM enrollments) AS enrollments_count,
    (SELECT COUNT(*) FROM payments) AS payments_count,
    (SELECT COUNT(*) FROM bills) AS bills_count,
    (SELECT COUNT(*) FROM notifications) AS notifications_count,
    (SELECT COUNT(*) FROM settings) AS settings_count;
