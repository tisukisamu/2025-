-- 插入默认管理员用户
INSERT INTO users (username, password, email, phone, real_name, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@dancestudio.com', '13800138000', '系统管理员', 'ADMIN', 'ACTIVE');

-- 插入默认教师
INSERT INTO teachers (name, phone, email, subjects, experience, bio, status) VALUES
('张老师', '13800138001', 'zhanglaoshi@dancestudio.com', '街舞,嘻哈', 5, '资深街舞教师,擅长嘻哈和锁舞', 'ACTIVE'),
('李老师', '13800138002', 'lilaoshi@dancestudio.com', '爵士,现代舞', 8, '专业爵士舞教师,教学经验丰富', 'ACTIVE'),
('王老师', '13800138003', 'wanglaoshi@dancestudio.com', 'Breaking,Popping', 6, 'Breaking和Popping专业教师', 'ACTIVE');

-- 插入默认学员
INSERT INTO students (name, phone, email, gender, status) VALUES
('张三', '13900139001', 'zhangsan@example.com', 'MALE', 'ACTIVE'),
('李四', '13900139002', 'lisi@example.com', 'FEMALE', 'ACTIVE'),
('王五', '13900139003', 'wangwu@example.com', 'MALE', 'ACTIVE');

-- 插入默认课程
INSERT INTO courses (name, description, teacher_id, category, level, duration, price, capacity, status) VALUES
('街舞基础班', '适合零基础学员,学习街舞基本动作和节奏感', 1, '街舞', 'BEGINNER', 60, 199.00, 20, 'PUBLISHED'),
('嘻哈进阶班', '适合有一定基础的学员,学习更复杂的嘻哈动作组合', 1, '嘻哈', 'INTERMEDIATE', 90, 299.00, 15, 'PUBLISHED'),
('爵士舞入门', '爵士舞基础课程,培养身体协调性和舞蹈感觉', 2, '爵士', 'BEGINNER', 60, 249.00, 18, 'PUBLISHED'),
('Breaking基础', 'Breaking基础动作教学,包括Toprock、Footwork等', 3, 'Breaking', 'BEGINNER', 90, 279.00, 12, 'PUBLISHED'),
('Popping专修班', 'Popping技巧深度学习,适合中高级学员', 3, 'Popping', 'ADVANCED', 120, 399.00, 10, 'PUBLISHED');

-- 插入系统设置
INSERT INTO settings (setting_key, setting_value, description) VALUES
('studio_name', '街舞工作室', '工作室名称'),
('studio_address', '北京市朝阳区xxx街道xxx号', '工作室地址'),
('studio_phone', '400-123-4567', '联系电话'),
('studio_email', 'info@dancestudio.com', '联系邮箱'),
('business_hours', '09:00-22:00', '营业时间'),
('cancel_deadline', '24', '取消报名截止时间(小时)');
