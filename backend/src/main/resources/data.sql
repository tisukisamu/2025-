-- 测试数据初始化脚本
-- 执行前请先确保数据库已创建

-- 清空现有数据（可选，如需保留数据请注释掉）
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE order_items;
-- TRUNCATE TABLE orders;
-- TRUNCATE TABLE products;
-- TRUNCATE TABLE users;
-- SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 1. 用户数据
-- ============================================
-- 密码都是 '123456' 经过 BCrypt 加密
-- 普通用户: user1/user2  管理员: admin

INSERT INTO users (username, password, nickname, role) VALUES
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '张三', 'ROLE_USER'),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '李四', 'ROLE_USER'),
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '管理员', 'ROLE_ADMIN');

-- ============================================
-- 2. 商品数据
-- ============================================

-- 新鲜蔬菜
INSERT INTO products (name, description, price, stock, image_url, active, category, is_new, is_hot, sales) VALUES
('有机西红柿', '新鲜采摘的有机西红柿，自然成熟，口感酸甜适中，富含维生素C', 8.90, 100, 'https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=400', true, 'vegetables', true, false, 156),
('新鲜黄瓜', '清脆爽口的新鲜黄瓜，适合凉拌或生吃，美容养颜', 5.50, 150, 'https://images.unsplash.com/photo-1449300079323-02e209d9d3a6?w=400', true, 'vegetables', false, true, 234),
('有机菠菜', '绿色有机菠菜，叶片肥厚，营养丰富，含铁量高', 6.80, 80, 'https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400', true, 'vegetables', true, false, 89),
('紫甘蓝', '新鲜紫甘蓝，颜色鲜艳，富含花青素，抗氧化', 7.20, 60, 'https://images.unsplash.com/photo-1594282486552-05b4d80fbb9f?w=400', true, 'vegetables', false, false, 67),
('西兰花', '绿色西兰花，营养丰富，抗癌蔬菜之王', 9.50, 120, 'https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?w=400', true, 'vegetables', true, true, 312),
('胡萝卜', '新鲜胡萝卜，口感脆甜，富含胡萝卜素', 4.50, 200, 'https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=400', true, 'vegetables', false, false, 145);

-- 时令水果
INSERT INTO products (name, description, price, stock, image_url, active, category, is_new, is_hot, sales) VALUES
('红富士苹果', '山东红富士苹果，脆甜多汁，果肉细腻', 12.80, 200, 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400', true, 'fruits', false, true, 456),
('进口香蕉', '菲律宾进口香蕉，果肉软糯香甜', 6.90, 150, 'https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=400', true, 'fruits', true, false, 234),
('新鲜橙子', '赣南脐橙，汁多味甜，维生素C含量高', 8.50, 180, 'https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=400', true, 'fruits', false, false, 178),
('巨峰葡萄', '新鲜巨峰葡萄，颗粒饱满，甜度高', 15.80, 100, 'https://images.unsplash.com/photo-1537640538965-1756cd58090e?w=400', true, 'fruits', true, true, 389),
('水蜜桃', '阳山水蜜桃，皮薄肉厚，汁水丰富', 18.90, 80, 'https://images.unsplash.com/photo-1629753250291-979952613877?w=400', true, 'fruits', true, false, 267),
('西瓜', '麒麟西瓜，皮薄瓤红，甜度超高', 3.98, 50, 'https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=400', true, 'fruits', false, true, 523);

-- 粮油米面
INSERT INTO products (name, description, price, stock, image_url, active, category, is_new, is_hot, sales) VALUES
('五常大米', '黑龙江五常大米，粒粒饱满，香气扑鼻', 68.00, 100, 'https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400', true, 'grains', false, true, 345),
('有机燕麦', '澳洲进口有机燕麦，营养丰富，健康早餐首选', 28.50, 80, 'https://images.unsplash.com/photo-1517093725432-a9ac7b9e45f7?w=400', true, 'grains', true, false, 198),
('花生油', '压榨一级花生油，香味浓郁，炒菜必备', 89.90, 60, 'https://images.unsplash.com/photo-1474979266404-7caddbed64a5?w=400', true, 'grains', false, false, 234),
('全麦面粉', '石磨全麦面粉，保留麦麸，营养更全面', 25.80, 120, 'https://images.unsplash.com/photo-1627485937980-221c88ac04f9?w=400', true, 'grains', true, false, 167),
('东北小米', '东北黄小米，熬粥香浓，养胃佳品', 19.90, 150, 'https://images.unsplash.com/photo-1610725664285-7c57e6eeac3f?w=400', true, 'grains', false, false, 189),
('黑芝麻', '农家黑芝麻，炒熟即食，乌发养颜', 32.00, 90, 'https://images.unsplash.com/photo-1559598467-f8b76c8155d0?w=400', true, 'grains', true, false, 123);

-- 肉禽蛋品
INSERT INTO products (name, description, price, stock, image_url, active, category, is_new, is_hot, sales) VALUES
('土鸡蛋', '散养土鸡蛋，蛋黄饱满，营养丰富', 25.80, 200, 'https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?w=400', true, 'meat', false, true, 567),
('五花肉', '精选五花肉，肥瘦相间，适合红烧', 35.80, 80, 'https://images.unsplash.com/photo-1602470520998-f4a52199a3d6?w=400', true, 'meat', false, false, 234),
('鸡胸肉', '新鲜鸡胸肉，低脂高蛋白，健身首选', 22.50, 120, 'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=400', true, 'meat', true, false, 345),
('牛排', '澳洲进口牛排，肉质鲜嫩，纹理清晰', 128.00, 50, 'https://images.unsplash.com/photo-1600891964092-4316c288032e?w=400', true, 'meat', true, true, 456),
('鸡翅中', '新鲜鸡翅中，肉质细嫩，烧烤必备', 38.90, 100, 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=400', true, 'meat', false, false, 189),
('鸭腿', '新鲜鸭腿，肉质紧实，适合卤制', 28.00, 80, 'https://images.unsplash.com/photo-1518492104633-130d0cc84637?w=400', true, 'meat', false, false, 145);

-- 水产海鲜
INSERT INTO products (name, description, price, stock, image_url, active, category, is_new, is_hot, sales) VALUES
('基围虾', '鲜活基围虾，肉质鲜甜，营养丰富', 58.00, 60, 'https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=400', true, 'seafood', true, true, 423),
('三文鱼', '挪威进口三文鱼，肉质细腻，刺身级', 88.00, 40, 'https://images.unsplash.com/photo-1599084993091-1cb5c0721cc6?w=400', true, 'seafood', false, true, 378),
('鲈鱼', '新鲜海鲈鱼，肉质细嫩，刺少肉多', 42.80, 50, 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=400', true, 'seafood', false, false, 234),
('扇贝', '鲜活扇贝，贝柱肥大，口感鲜甜', 35.00, 80, 'https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=400', true, 'seafood', true, false, 289),
('花蛤', '新鲜花蛤，肉质鲜美，吐沙干净', 15.80, 100, 'https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=400', true, 'seafood', false, false, 167),
('鱿鱼', '新鲜鱿鱼，肉质Q弹，烧烤爆炒皆宜', 32.50, 70, 'https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=400', true, 'seafood', false, false, 198);

-- 下架商品（测试用）
INSERT INTO products (name, description, price, stock, image_url, active, category, is_new, is_hot, sales) VALUES
('季节性草莓', '冬季草莓，暂时下架', 38.00, 0, 'https://images.unsplash.com/photo-1464965911861-746a04b4bca6?w=400', false, 'fruits', false, false, 0),
('限量松茸', '野生松茸，已售罄', 288.00, 0, 'https://images.unsplash.com/photo-1597715508708-60312c520f7a?w=400', false, 'vegetables', false, false, 0);

-- ============================================
-- 3. 订单数据
-- ============================================

-- 用户1的订单
INSERT INTO orders (order_no, user_id, total_amount, status, address, contact, phone, tracking_no, create_time) VALUES
('ORD202401150001', 1, 45.60, 3, '北京市朝阳区建国路88号', '张三', '13800138001', 'SF1234567890', DATE_SUB(NOW(), INTERVAL 5 DAY)),
('ORD202401160002', 1, 128.50, 2, '北京市朝阳区建国路88号', '张三', '13800138001', 'SF1234567891', DATE_SUB(NOW(), INTERVAL 3 DAY)),
('ORD202401170003', 1, 68.00, 1, '北京市朝阳区建国路88号', '张三', '13800138001', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('ORD202401180004', 1, 256.80, 0, '北京市朝阳区建国路88号', '张三', '13800138001', NULL, NOW());

-- 用户2的订单
INSERT INTO orders (order_no, user_id, total_amount, status, address, contact, phone, tracking_no, create_time) VALUES
('ORD202401150005', 2, 89.90, 3, '上海市浦东新区陆家嘴环路1000号', '李四', '13800138002', 'SF1234567892', DATE_SUB(NOW(), INTERVAL 4 DAY)),
('ORD202401160006', 2, 156.00, 2, '上海市浦东新区陆家嘴环路1000号', '李四', '13800138002', 'SF1234567893', DATE_SUB(NOW(), INTERVAL 2 DAY)),
('ORD202401170007', 2, 45.50, 1, '上海市浦东新区陆家嘴环路1000号', '李四', '13800138002', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ============================================
-- 4. 订单项数据
-- ============================================

-- 订单1的订单项
INSERT INTO order_items (order_id, product_id, product_name, price, quantity) VALUES
(1, 1, '有机西红柿', 8.90, 2),
(1, 2, '新鲜黄瓜', 5.50, 3),
(1, 9, '红富士苹果', 12.80, 2);

-- 订单2的订单项
INSERT INTO order_items (order_id, product_id, product_name, price, quantity) VALUES
(2, 19, '牛排', 128.00, 1);

-- 订单3的订单项
INSERT INTO order_items (order_id, product_id, product_name, price, quantity) VALUES
(3, 13, '五常大米', 68.00, 1);

-- 订单4的订单项
INSERT INTO order_items (order_id, product_id, product_name, price, quantity) VALUES
(4, 19, '牛排', 128.00, 2);

-- 订单5的订单项
INSERT INTO order_items (order_id, product_id, product_name, price, quantity) VALUES
(5, 7, '有机菠菜', 6.80, 5),
(5, 8, '紫甘蓝', 7.20, 5);

-- 订单6的订单项
INSERT INTO order_items (order_id, product_id, product_name, price, quantity) VALUES
(6, 25, '基围虾', 58.00, 2),
(6, 26, '三文鱼', 88.00, 1);

-- 订单7的订单项
INSERT INTO order_items (order_id, product_id, product_name, price, quantity) VALUES
(7, 20, '土鸡蛋', 25.80, 1),
(7, 21, '五花肉', 35.80, 1);

-- ============================================
-- 数据插入完成
-- ============================================

-- 验证数据
SELECT '用户数量' as 统计项, COUNT(*) as 数量 FROM users
UNION ALL
SELECT '商品数量', COUNT(*) FROM products
UNION ALL
SELECT '订单数量', COUNT(*) FROM orders
UNION ALL
SELECT '订单项数量', COUNT(*) FROM order_items;
