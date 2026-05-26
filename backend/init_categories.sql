-- 初始化分类数据
-- 已存在分类则忽略插入

INSERT INTO categories (name, description, parent_id, level, sort_order, icon_url, active, create_time, update_time)
SELECT 
  '蔬菜', '新鲜蔬菜瓜果', 0, 1, 1, '🥬', true, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM categories WHERE name = '蔬菜'
);

INSERT INTO categories (name, description, parent_id, level, sort_order, icon_url, active, create_time, update_time)
SELECT 
  '水果', '时令水果', 0, 1, 2, '🍎', true, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM categories WHERE name = '水果'
);

INSERT INTO categories (name, description, parent_id, level, sort_order, icon_url, active, create_time, update_time)
SELECT 
  '粮油米面', '粮油米面', 0, 1, 3, '🌾', true, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM categories WHERE name = '粮油米面'
);

INSERT INTO categories (name, description, parent_id, level, sort_order, icon_url, active, create_time, update_time)
SELECT 
  '肉禽蛋品', '新鲜肉禽蛋品', 0, 1, 4, '🥩', true, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM categories WHERE name = '肉禽蛋品'
);

INSERT INTO categories (name, description, parent_id, level, sort_order, icon_url, active, create_time, update_time)
SELECT 
  '水产海鲜', '鲜活水产', 0, 1, 5, '🦐', true, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM categories WHERE name = '水产海鲜'
);

INSERT INTO categories (name, description, parent_id, level, sort_order, icon_url, active, create_time, update_time)
SELECT 
  '休闲零食', '休闲零食', 0, 1, 6, '🍪', true, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM categories WHERE name = '休闲零食'
);
