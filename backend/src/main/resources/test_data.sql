-- 宠物殡葬系统测试数据
-- 数据库名: gp13

-- 清空现有数据 (可选，按需取消注释)
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE message;
-- TRUNCATE TABLE service_process;
-- TRUNCATE TABLE memorial_album;
-- TRUNCATE TABLE appointment;
-- TRUNCATE TABLE pet_info;
-- TRUNCATE TABLE service_package;
-- TRUNCATE TABLE users;
-- SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 1. 用户表 (users)
-- 密码都是: 123456 (BCrypt加密后的值)
-- ============================================
INSERT INTO users (username, password, name, email, age, role, status, created_at, updated_at) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin@petfuneral.com', 30, 'ADMIN', 'ACTIVE', NOW(), NOW()),
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三', 'zhangsan@example.com', 28, 'USER', 'ACTIVE', NOW(), NOW()),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李四', 'lisi@example.com', 35, 'USER', 'ACTIVE', NOW(), NOW()),
('wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王五', 'wangwu@example.com', 42, 'USER', 'ACTIVE', NOW(), NOW()),
('zhaoliu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵六', 'zhaoliu@example.com', 25, 'USER', 'DISABLED', NOW(), NOW());

-- ============================================
-- 2. 服务套餐表 (service_package)
-- ============================================
INSERT INTO service_package (name, type, description, price, original_price, includes, image, sort_order, status, created_at, updated_at, deleted) VALUES
('基础火化套餐', 'cremation', '为您的爱宠提供体面、庄重的火化服务，包含独立火化和骨灰盒。', 1280.00, 1580.00, '独立火化服务,精美骨灰盒,火化证书,专车接送', NULL, 1, 1, NOW(), NOW(), 0),
('标准告别套餐', 'farewell', '包含告别仪式、鲜花布置、火化服务及纪念品，让告别更有仪式感。', 2980.00, 3580.00, '告别仪式场地,鲜花布置,独立火化,精美骨灰盒,纪念相册,火化证书,专车接送', NULL, 2, 1, NOW(), NOW(), 0),
('尊享纪念套餐', 'memorial', '全方位的纪念服务，包含告别仪式、火化、骨灰寄存及定制纪念品。', 5680.00, 6880.00, '专属告别厅,鲜花花坛布置,独立火化,高档骨灰盒,骨灰寄存一年,定制纪念品,纪念视频,火化证书,专车接送,后续关怀服务', NULL, 3, 1, NOW(), NOW(), 0),
('集体火化服务', 'cremation', '经济实惠的集体火化服务，适合预算有限的家庭。', 380.00, NULL, '集体火化服务,基础骨灰袋', NULL, 4, 1, NOW(), NOW(), 0),
('宠物安葬服务', 'burial', '提供宠物墓地安葬服务，让爱宠有一个安静的归宿。', 3980.00, 4980.00, '宠物墓地(10年),墓碑刻字,鲜花祭品,安葬仪式,后续祭扫服务', NULL, 5, 1, NOW(), NOW(), 0),
('骨灰寄存服务', 'memorial', '专业的骨灰寄存服务，环境清幽，可随时祭拜。', 680.00, NULL, '骨灰寄存一年,寄存证书,定期祭拜提醒', NULL, 6, 1, NOW(), NOW(), 0),
('上门接宠服务', 'other', '24小时上门接宠服务，温柔对待每一位小天使。', 200.00, NULL, '24小时响应,专车上门,温柔处理', NULL, 7, 1, NOW(), NOW(), 0),
('纪念视频制作', 'memorial', '专业团队制作纪念视频，永久保存美好回忆。', 880.00, 1280.00, '专业拍摄,视频剪辑,背景音乐,电子相册', NULL, 8, 1, NOW(), NOW(), 0);

-- ============================================
-- 3. 宠物信息表 (pet_info)
-- ============================================
INSERT INTO pet_info (user_id, name, type, breed, gender, birthday, pass_date, photo, color, weight, description, memorial_text, created_at, updated_at, deleted) VALUES
(2, '小白', 'dog', '萨摩耶', 'male', '2018-03-15', '2024-01-10', NULL, '白色', 28.50, '活泼可爱的小萨摩耶，陪伴了我们6年。', '亲爱的小白，谢谢你这些年的陪伴，愿你在汪星快乐奔跑。', NOW(), NOW(), 0),
(2, '咪咪', 'cat', '英短蓝猫', 'female', '2020-06-20', NULL, NULL, '蓝灰色', 4.20, '温顺的英短，喜欢晒太阳。', NULL, NOW(), NOW(), 0),
(3, '大黄', 'dog', '金毛', 'male', '2015-08-10', '2023-12-25', NULL, '金黄色', 32.00, '忠诚的大金毛，陪伴了8年多。', '大黄，你是我最好的朋友，永远怀念你。', NOW(), NOW(), 0),
(3, '小花', 'cat', '三花猫', 'female', '2019-02-14', '2024-02-01', NULL, '三花色', 3.80, '流浪猫收养，性格亲人。', '小花，虽然你离开了，但你的温暖永远留在我们心中。', NOW(), NOW(), 0),
(4, '豆豆', 'dog', '柯基', 'male', '2021-04-05', NULL, NULL, '黄白相间', 12.50, '小短腿柯基，非常可爱。', NULL, NOW(), NOW(), 0),
(4, '雪球', 'cat', '布偶猫', 'female', '2022-01-01', NULL, NULL, '白色', 5.00, '漂亮的布偶猫，像小公主一样。', NULL, NOW(), NOW(), 0),
(4, '小黑', 'dog', '拉布拉多', 'male', '2016-11-20', '2024-01-20', NULL, '黑色', 30.00, '忠诚的黑色拉布拉多。', '小黑，谢谢你守护我们7年多。', NOW(), NOW(), 0);

-- ============================================
-- 4. 预约表 (appointment)
-- ============================================
INSERT INTO appointment (order_no, user_id, pet_id, package_id, appointment_time, contact_name, contact_phone, address, remark, status, operator_id, created_at, updated_at, deleted) VALUES
('APT20240110001', 2, 1, 2, '2024-01-12 10:00:00', '张三', '13800138001', '北京市朝阳区幸福路88号', '请安排上午的时间', 'completed', 1, NOW(), NOW(), 0),
('APT20240125001', 3, 3, 1, '2024-01-28 14:00:00', '李四', '13900139002', '上海市浦东新区花园路66号', '狗狗体型较大，请安排大车', 'completed', 1, NOW(), NOW(), 0),
('APT20240201001', 3, 4, 3, '2024-02-03 09:00:00', '李四', '13900139002', '上海市浦东新区花园路66号', NULL, 'completed', 1, NOW(), NOW(), 0),
('APT20240120001', 4, 7, 2, '2024-01-22 15:00:00', '王五', '13700137003', '广州市天河区阳光大道128号', '希望能安排周末', 'processing', 1, NOW(), NOW(), 0),
('APT20240305001', 2, 2, 4, '2024-03-08 11:00:00', '张三', '13800138001', '北京市朝阳区幸福路88号', '猫咪比较胆小，请温柔对待', 'confirmed', NULL, NOW(), NOW(), 0),
('APT20240310001', 4, 5, 1, '2024-03-12 16:00:00', '王五', '13700137003', '广州市天河区阳光大道128号', NULL, 'pending', NULL, NOW(), NOW(), 0),
('APT20240315001', 4, 6, 6, '2024-03-18 10:00:00', '王五', '13700137003', '广州市天河区阳光大道128号', '需要上门服务', 'pending', NULL, NOW(), NOW(), 0);

-- ============================================
-- 5. 服务流程表 (service_process)
-- ============================================
INSERT INTO service_process (appointment_id, stage, status, operator_id, description, photos, videos, start_time, end_time, created_at, updated_at) VALUES
(1, 'confirmed', 'completed', 1, '已与客户确认预约信息', NULL, NULL, '2024-01-10 09:00:00', '2024-01-10 09:30:00', NOW(), NOW()),
(1, 'pickup', 'completed', 1, '已从客户家中接走宠物', NULL, NULL, '2024-01-12 08:00:00', '2024-01-12 09:00:00', NOW(), NOW()),
(1, 'farewell', 'completed', 1, '告别仪式已完成', NULL, NULL, '2024-01-12 10:00:00', '2024-01-12 11:30:00', NOW(), NOW()),
(1, 'cremation', 'completed', 1, '火化服务已完成', NULL, NULL, '2024-01-12 12:00:00', '2024-01-12 14:00:00', NOW(), NOW()),
(1, 'processing', 'completed', 1, '骨灰处理完成，已装入骨灰盒', NULL, NULL, '2024-01-12 14:30:00', '2024-01-12 15:00:00', NOW(), NOW()),
(1, 'memorial', 'completed', 1, '纪念品制作完成', NULL, NULL, '2024-01-12 15:30:00', '2024-01-12 16:30:00', NOW(), NOW()),
(1, 'completed', 'completed', 1, '服务已全部完成，骨灰已交付客户', NULL, NULL, '2024-01-12 17:00:00', '2024-01-12 17:30:00', NOW(), NOW()),
(2, 'confirmed', 'completed', 1, '已确认预约', NULL, NULL, '2024-01-25 10:00:00', '2024-01-25 10:30:00', NOW(), NOW()),
(2, 'pickup', 'completed', 1, '已接走宠物', NULL, NULL, '2024-01-28 13:00:00', '2024-01-28 14:00:00', NOW(), NOW()),
(2, 'cremation', 'completed', 1, '火化完成', NULL, NULL, '2024-01-28 14:30:00', '2024-01-28 16:00:00', NOW(), NOW()),
(2, 'completed', 'completed', 1, '服务完成', NULL, NULL, '2024-01-28 16:30:00', '2024-01-28 17:00:00', NOW(), NOW()),
(3, 'confirmed', 'completed', 1, '已确认预约', NULL, NULL, '2024-02-01 10:00:00', '2024-02-01 10:30:00', NOW(), NOW()),
(3, 'pickup', 'completed', 1, '已接走宠物', NULL, NULL, '2024-02-03 08:00:00', '2024-02-03 09:00:00', NOW(), NOW()),
(3, 'farewell', 'completed', 1, '告别仪式完成', NULL, NULL, '2024-02-03 09:00:00', '2024-02-03 10:30:00', NOW(), NOW()),
(3, 'cremation', 'completed', 1, '火化完成', NULL, NULL, '2024-02-03 11:00:00', '2024-02-03 12:30:00', NOW(), NOW()),
(3, 'processing', 'completed', 1, '骨灰处理完成', NULL, NULL, '2024-02-03 13:00:00', '2024-02-03 13:30:00', NOW(), NOW()),
(3, 'memorial', 'completed', 1, '纪念品制作完成', NULL, NULL, '2024-02-03 14:00:00', '2024-02-03 15:30:00', NOW(), NOW()),
(3, 'completed', 'completed', 1, '服务完成', NULL, NULL, '2024-02-03 16:00:00', '2024-02-03 16:30:00', NOW(), NOW()),
(4, 'confirmed', 'completed', 1, '已确认预约', NULL, NULL, '2024-01-20 11:00:00', '2024-01-20 11:30:00', NOW(), NOW()),
(4, 'pickup', 'completed', 1, '已接走宠物', NULL, NULL, '2024-01-22 14:00:00', '2024-01-22 15:00:00', NOW(), NOW()),
(4, 'farewell', 'processing', 1, '告别仪式进行中', NULL, NULL, '2024-01-22 15:00:00', NULL, NOW(), NOW());

-- ============================================
-- 6. 纪念相册表 (memorial_album)
-- ============================================
INSERT INTO memorial_album (pet_id, user_id, title, description, photos, is_public, view_count, created_at, updated_at, deleted) VALUES
(1, 2, '小白的美好时光', '记录小白陪伴我们的点点滴滴，永远怀念你。', NULL, 1, 156, NOW(), NOW(), 0),
(3, 3, '大黄的八年', '忠诚的大黄，谢谢你八年的陪伴。', NULL, 1, 89, NOW(), NOW(), 0),
(4, 3, '小花的故事', '从流浪到家人，感谢你选择我们。', NULL, 1, 67, NOW(), NOW(), 0),
(7, 4, '小黑的守护', '七年守护，一生怀念。', NULL, 1, 45, NOW(), NOW(), 0);

-- ============================================
-- 7. 留言表 (message)
-- ============================================
INSERT INTO message (album_id, user_id, author_name, content, created_at, deleted) VALUES
(1, 3, '李四', '小白看起来真的很可爱，愿它在汪星快乐。', NOW(), 0),
(1, 4, '王五', '萨摩耶的笑容最治愈了，节哀。', NOW(), 0),
(1, NULL, '匿名用户', '看着照片就想起了我家的狗狗，它们一定在汪星成为了好朋友。', NOW(), 0),
(2, 2, '张三', '金毛是最忠诚的伙伴，大黄一定很爱你。', NOW(), 0),
(2, 4, '王五', '八年的陪伴，是多么珍贵的缘分。', NOW(), 0),
(3, 2, '张三', '流浪猫也能拥有温暖的家，感谢你们的爱心。', NOW(), 0),
(4, 2, '张三', '黑色拉布拉多真的很帅气，愿它安息。', NOW(), 0),
(4, 3, '李四', '七年的守护，永远的怀念。', NOW(), 0);

-- ============================================
-- 查询验证数据
-- ============================================
SELECT '--- 用户数据 ---' AS info;
SELECT id, username, name, role, status FROM users;

SELECT '--- 服务套餐数据 ---' AS info;
SELECT id, name, type, price, status FROM service_package;

SELECT '--- 宠物数据 ---' AS info;
SELECT id, name, type, breed, user_id FROM pet_info;

SELECT '--- 预约数据 ---' AS info;
SELECT id, order_no, status, user_id, pet_id, package_id FROM appointment;

SELECT '--- 服务流程数据 ---' AS info;
SELECT id, appointment_id, stage, status FROM service_process;

SELECT '--- 纪念相册数据 ---' AS info;
SELECT id, title, pet_id, user_id, view_count FROM memorial_album;

SELECT '--- 留言数据 ---' AS info;
SELECT id, album_id, author_name, LEFT(content, 30) AS content_preview FROM message;
