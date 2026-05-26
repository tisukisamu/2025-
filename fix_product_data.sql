-- ============================================
-- 商品数据修复脚本
-- ============================================

-- 1. 首先查看当前表结构
DESCRIBE products;

-- 2. 查看当前数据状态
SELECT id, name, image_url, image_urls, store_id, status, active FROM products ORDER BY id;

-- ============================================
-- 修复方案 A：修复 ID 1 和 2 的数据（如果这些是测试数据）
-- ============================================

-- 删除混乱的测试数据（如果不需要）
-- DELETE FROM products WHERE id IN (1, 2, 123, 222);

-- 或者更新 ID 1 的数据
UPDATE products 
SET 
    name = '测试商品1',
    description = '测试商品描述',
    price = 10.00,
    stock = 100,
    image_url = '/api/uploads/0f906b1f-a224-47a0-8ff4-5cf47be436d8.jpg',
    image_urls = '/api/uploads/0f906b1f-a224-47a0-8ff4-5cf47be436d8.jpg',
    active = true,
    category = 'vegetables',
    category_id = 1,
    is_new = false,
    is_hot = false,
    sales = 0,
    store_id = NULL,
    status = 1,
    reject_reason = NULL,
    audit_time = NULL,
    audit_by = NULL,
    create_time = NOW(),
    update_time = NOW()
WHERE id = 1;

-- 更新 ID 2 的数据
UPDATE products 
SET 
    name = '测试商品2',
    description = '测试商品描述',
    price = 20.00,
    stock = 100,
    image_url = '/api/uploads/0f906b1f-a224-47a0-8ff4-5cf47be436d8.jpg',
    image_urls = '/api/uploads/0f906b1f-a224-47a0-8ff4-5cf47be436d8.jpg',
    active = true,
    category = 'vegetables',
    category_id = 1,
    is_new = false,
    is_hot = false,
    sales = 0,
    store_id = NULL,
    status = 1,
    reject_reason = NULL,
    audit_time = NULL,
    audit_by = NULL,
    create_time = NOW(),
    update_time = NOW()
WHERE id = 2;

-- ============================================
-- 修复方案 B：修复 ID 123 和 222 的数据
-- ============================================

-- 更新 ID 123 的数据
UPDATE products 
SET 
    name = '店铺商品123',
    description = '店铺商品描述',
    price = 50.00,
    stock = 100,
    image_url = '/api/uploads/0f906b1f-a224-47a0-8ff4-5cf47be436d8.jpg',
    image_urls = '/api/uploads/0f906b1f-a224-47a0-8ff4-5cf47be436d8.jpg',
    active = true,
    category = 'fruits',
    category_id = 2,
    is_new = true,
    is_hot = false,
    sales = 5,
    store_id = 123,
    status = 1,
    reject_reason = NULL,
    audit_time = '2026-02-11 03:24:27',
    audit_by = 5,
    create_time = '2026-02-11 03:21:10',
    update_time = '2026-02-11 02:52:54'
WHERE id = 123;

-- 更新 ID 222 的数据
UPDATE products 
SET 
    name = '店铺商品222',
    description = '店铺商品描述',
    price = 60.00,
    stock = 80,
    image_url = '/api/uploads/0f906b1f-a224-47a0-8ff4-5cf47be436d8.jpg',
    image_urls = '/api/uploads/0f906b1f-a224-47a0-8ff4-5cf47be436d8.jpg',
    active = true,
    category = 'meat',
    category_id = 4,
    is_new = false,
    is_hot = true,
    sales = 3,
    store_id = 222,
    status = 1,
    reject_reason = NULL,
    audit_time = '2026-02-11 16:34:42',
    audit_by = NULL,
    create_time = '2026-02-11 03:21:10',
    update_time = '2026-02-11 16:37:25'
WHERE id = 222;

-- ============================================
-- 修复方案 C：标准化图片路径（应用双保底策略）
-- ============================================

-- 将所有商品的 image_url 转换为相对路径格式存入 image_urls
-- 这样前端可以使用双保底策略
UPDATE products 
SET 
    image_urls = CASE 
        WHEN image_url LIKE 'http%' THEN 
            -- 将绝对路径转换为相对路径
            REPLACE(REPLACE(image_url, 'https://images.unsplash.com/', '/images/'), 'http://localhost:8080/', '/')
        WHEN image_url IS NOT NULL THEN 
            image_url
        ELSE 
            NULL
    END
WHERE image_urls IS NULL OR image_urls = '';

-- ============================================
-- 修复方案 D：修复缺失的必要字段
-- ============================================

-- 修复缺失的 category_id
UPDATE products 
SET category_id = CASE category
    WHEN 'vegetables' THEN 1
    WHEN 'fruits' THEN 2
    WHEN 'grains' THEN 3
    WHEN 'meat' THEN 4
    WHEN 'seafood' THEN 5
    WHEN 'snacks' THEN 6
    WHEN 'drinks' THEN 7
    ELSE 1
END
WHERE category_id IS NULL OR category_id = 0;

-- 修复缺失的布尔字段（确保不为 NULL）
UPDATE products SET is_new = false WHERE is_new IS NULL;
UPDATE products SET is_hot = false WHERE is_hot IS NULL;
UPDATE products SET active = true WHERE active IS NULL;
UPDATE products SET sales = 0 WHERE sales IS NULL;
UPDATE products SET status = 1 WHERE status IS NULL;
UPDATE products SET stock_warning = 10 WHERE stock_warning IS NULL;

-- ============================================
-- 验证修复结果
-- ============================================

-- 查看修复后的数据
SELECT 
    id, 
    name, 
    LEFT(description, 30) as description_preview,
    price, 
    stock, 
    LEFT(image_url, 50) as image_url_preview,
    LEFT(image_urls, 50) as image_urls_preview,
    category,
    category_id,
    store_id,
    status,
    active,
    is_new,
    is_hot,
    sales
FROM products 
ORDER BY id;

-- 统计各状态商品数量
SELECT 
    status,
    COUNT(*) as count,
    SUM(CASE WHEN image_url IS NOT NULL THEN 1 ELSE 0 END) as has_image_url,
    SUM(CASE WHEN image_urls IS NOT NULL AND image_urls != '' THEN 1 ELSE 0 END) as has_image_urls
FROM products
GROUP BY status;

-- 检查是否有 store_id 为 NULL 但应该属于店铺的商品
SELECT id, name, store_id, status 
FROM products 
WHERE store_id IS NULL 
  AND id > 100;
