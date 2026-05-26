-- ============================================
-- 商品数据插入脚本（标准格式）
-- ============================================

-- 清空现有数据（谨慎使用！）
-- TRUNCATE TABLE products;

-- 或者删除特定ID的数据
DELETE FROM products WHERE id IN (1, 2, 123, 222);

-- 插入标准格式的商品数据
INSERT INTO products (
    id, name, description, price, stock, stock_warning,
    image_url, image_urls, active, category, category_id,
    is_new, is_hot, sales, store_id, status,
    reject_reason, audit_time, audit_by, create_time, update_time
) VALUES
-- 平台自营商品（store_id = NULL）
(1, '有机西红柿', '新鲜采摘的有机西红柿，自然成熟，口感酸甜适中，富含维生素C', 
 8.90, 94, 10,
 'https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=400',
 '/uploads/tomato.jpg',
 true, 'vegetables', 1, false, true, 156, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(2, '新鲜黄瓜', '清脆爽口的新鲜黄瓜，适合凉拌或生吃，美容养颜', 
 5.50, 150, 10,
 'https://images.unsplash.com/photo-1449300079323-02e209d9d3a6?w=400',
 '/uploads/cucumber.jpg',
 true, 'vegetables', 1, true, false, 234, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(3, '有机菠菜', '绿色有机菠菜，叶片肥厚，营养丰富，含铁量高', 
 6.80, 80, 10,
 'https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400',
 '/uploads/spinach.jpg',
 true, 'vegetables', 1, false, true, 89, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(4, '紫甘蓝', '新鲜紫甘蓝，颜色鲜艳，富含花青素，抗氧化', 
 7.20, 60, 10,
 'https://images.unsplash.com/photo-1594282486552-05b4d80fbb9f?w=400',
 '/uploads/cabbage.jpg',
 true, 'vegetables', 1, false, false, 67, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(5, '西兰花', '绿色西兰花，营养丰富，抗癌蔬菜之王', 
 9.50, 120, 10,
 'https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?w=400',
 '/uploads/broccoli.jpg',
 true, 'vegetables', 1, true, true, 312, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(6, '胡萝卜', '新鲜胡萝卜，口感脆甜，富含胡萝卜素', 
 4.50, 200, 10,
 'https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=400',
 '/uploads/carrot.jpg',
 true, 'vegetables', 1, false, false, 145, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(7, '红富士苹果', '山东红富士苹果，脆甜多汁，果肉细腻', 
 12.80, 200, 10,
 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400',
 '/uploads/apple.jpg',
 true, 'fruits', 2, true, false, 456, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(8, '进口香蕉', '菲律宾进口香蕉，果肉软糯香甜', 
 6.90, 150, 10,
 'https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=400',
 '/uploads/banana.jpg',
 true, 'fruits', 2, false, true, 234, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(9, '新鲜橙子', '赣南脐橙，汁多味甜，维生素C含量高', 
 8.50, 180, 10,
 'https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=400',
 '/uploads/orange.jpg',
 true, 'fruits', 2, false, false, 178, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(10, '巨峰葡萄', '新鲜巨峰葡萄，颗粒饱满，甜度高', 
 15.80, 100, 10,
 'https://images.unsplash.com/photo-1537640538965-1756cd58090e?w=400',
 '/uploads/grape.jpg',
 true, 'fruits', 2, true, true, 389, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(11, '水蜜桃', '阳山水蜜桃，皮薄肉厚，汁水丰富', 
 18.90, 80, 10,
 'https://images.unsplash.com/photo-1629753250291-979952613877?w=400',
 '/uploads/peach.jpg',
 true, 'fruits', 2, false, true, 267, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(12, '西瓜', '麒麟西瓜，皮薄瓤红，甜度超高', 
 3.98, 50, 10,
 'https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=400',
 '/uploads/watermelon.jpg',
 true, 'fruits', 2, true, false, 523, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(13, '五常大米', '黑龙江五常大米，粒粒饱满，香气扑鼻', 
 68.00, 100, 10,
 'https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400',
 '/uploads/rice.jpg',
 true, 'grains', 3, true, false, 345, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(14, '有机燕麦', '澳洲进口有机燕麦，营养丰富，健康早餐首选', 
 28.50, 80, 10,
 'https://images.unsplash.com/photo-1517093725432-a9ac7b9e45f7?w=400',
 '/uploads/oat.jpg',
 true, 'grains', 3, false, true, 198, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(15, '花生油', '压榨一级花生油，香味浓郁，炒菜必备', 
 89.90, 60, 10,
 'https://images.unsplash.com/photo-1474979266404-7caddbed64a5?w=400',
 '/uploads/oil.jpg',
 true, 'grains', 3, false, false, 234, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(16, '全麦面粉', '石磨全麦面粉，保留麦麸，营养更全面', 
 25.80, 120, 10,
 'https://images.unsplash.com/photo-1627485937980-221c88ac04f9?w=400',
 '/uploads/flour.jpg',
 true, 'grains', 3, false, true, 167, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(17, '东北小米', '东北黄小米，熬粥香浓，养胃佳品', 
 19.90, 150, 10,
 'https://images.unsplash.com/photo-1610725664285-7c57e6eeac3f?w=400',
 '/uploads/millet.jpg',
 true, 'grains', 3, false, false, 189, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(18, '黑芝麻', '农家黑芝麻，炒熟即食，乌发养颜', 
 32.00, 90, 10,
 'https://images.unsplash.com/photo-1559598467-f8b76c8155d0?w=400',
 '/uploads/sesame.jpg',
 true, 'grains', 3, false, true, 123, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(19, '土鸡蛋', '散养土鸡蛋，蛋黄饱满，营养丰富', 
 25.80, 200, 10,
 'https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?w=400',
 '/uploads/egg.jpg',
 true, 'meat', 4, true, false, 567, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(20, '五花肉', '精选五花肉，肥瘦相间，适合红烧', 
 35.80, 80, 10,
 'https://images.unsplash.com/photo-1602470520998-f4a52199a3d6?w=400',
 '/uploads/pork.jpg',
 true, 'meat', 4, false, false, 234, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(21, '鸡胸肉', '新鲜鸡胸肉，低脂高蛋白，健身首选', 
 22.50, 120, 10,
 'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=400',
 '/uploads/chicken.jpg',
 true, 'meat', 4, false, true, 345, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(22, '牛排', '澳洲进口牛排，肉质鲜嫩，纹理清晰', 
 128.00, 50, 10,
 'https://images.unsplash.com/photo-1600891964092-4316c288032e?w=400',
 '/uploads/steak.jpg',
 true, 'meat', 4, true, true, 456, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(23, '鸡翅中', '新鲜鸡翅中，肉质细嫩，烧烤必备', 
 38.90, 100, 10,
 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=400',
 '/uploads/chicken-wing.jpg',
 true, 'meat', 4, false, false, 189, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(24, '鸭腿', '新鲜鸭腿，肉质紧实，适合卤制', 
 28.00, 80, 10,
 'https://images.unsplash.com/photo-1518492104633-130d0cc84637?w=400',
 '/uploads/duck.jpg',
 true, 'meat', 4, false, false, 145, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(25, '基围虾', '鲜活基围虾，肉质鲜甜，营养丰富', 
 58.00, 60, 10,
 'https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=400',
 '/uploads/shrimp.jpg',
 true, 'seafood', 5, true, true, 423, NULL, 1,
 NULL, NULL, NULL, NOW(), NOW()),

(26, '三文鱼', '挪威进口三文鱼，肉质细腻，刺身级', 
 88.00, 40, 10,
 'https://images.unsplash.com/photo-1599084993091-979952613877?w=400',
 '/uploads/salmon