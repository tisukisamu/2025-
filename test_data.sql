-- 校园闲置电子产品置换系统 - 测试数据
-- MySQL 8.x

-- 创建数据库
USE gp4;

-- ============================================
-- 1. 用户表 (users)
-- ============================================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    student_id VARCHAR(20) UNIQUE,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    avatar VARCHAR(500),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    credit_score INT DEFAULT 100,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_username (username),
    INDEX idx_student_id (student_id),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 分类表 (category)
-- ============================================
DROP TABLE IF EXISTS category;
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT,
    sort_order INT DEFAULT 0,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_parent_id (parent_id),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- ============================================
-- 3. 商品表 (product)
-- ============================================
DROP TABLE IF EXISTS product;
CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    category_id BIGINT,
    seller_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    audit_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    audit_reason VARCHAR(500),
    view_count INT DEFAULT 0,
    favorite_count INT DEFAULT 0,
    trade_type VARCHAR(20) NOT NULL DEFAULT 'BOTH',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (seller_id) REFERENCES users(id),
    INDEX idx_category_id (category_id),
    INDEX idx_seller_id (seller_id),
    INDEX idx_status (status),
    INDEX idx_audit_status (audit_status),
    INDEX idx_trade_type (trade_type),
    INDEX idx_price (price),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- ============================================
-- 4. 商品图片表 (product_image)
-- ============================================
DROP TABLE IF EXISTS product_image;
CREATE TABLE product_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    sort_order INT DEFAULT 0,
    create_time DATETIME NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    INDEX idx_product_id (product_id),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';

-- ============================================
-- 5. 订单表 (order)
-- ============================================
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(32) NOT NULL UNIQUE,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    trade_type VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    address VARCHAR(500),
    express_no VARCHAR(50),
    cancel_reason VARCHAR(500),
    ship_time DATETIME,
    complete_time DATETIME,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    FOREIGN KEY (buyer_id) REFERENCES users(id),
    FOREIGN KEY (seller_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES product(id),
    INDEX idx_order_no (order_no),
    INDEX idx_buyer_id (buyer_id),
    INDEX idx_seller_id (seller_id),
    INDEX idx_product_id (product_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ============================================
-- 6. 评价表 (review)
-- ============================================
DROP TABLE IF EXISTS review;
CREATE TABLE review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL,
    content TEXT,
    images JSON,
    is_anonymous BOOLEAN DEFAULT FALSE,
    create_time DATETIME NOT NULL,
    FOREIGN KEY (order_id) REFERENCES `order`(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- ============================================
-- 7. 消息表 (message)
-- ============================================
DROP TABLE IF EXISTS message;
CREATE TABLE message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    is_read BOOLEAN DEFAULT FALSE,
    create_time DATETIME NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_is_read (is_read),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- ============================================
-- 8. 收藏表 (favorite)
-- ============================================
DROP TABLE IF EXISTS favorite;
CREATE TABLE favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL,
    UNIQUE KEY uk_user_product (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ============================================
-- 9. 关注表 (follow)
-- ============================================
DROP TABLE IF EXISTS follow;
CREATE TABLE follow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL,
    UNIQUE KEY uk_follower_following (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_follower_id (follower_id),
    INDEX idx_following_id (following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注表';

-- ============================================
-- 10. 通知表 (notification)
-- ============================================
DROP TABLE IF EXISTS notification;
CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    type VARCHAR(20) NOT NULL,
    related_id BIGINT,
    is_read BOOLEAN DEFAULT FALSE,
    create_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- ============================================
-- 11. 公告表 (announcement)
-- ============================================
DROP TABLE IF EXISTS announcement;
CREATE TABLE announcement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    type VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    is_top BOOLEAN DEFAULT FALSE,
    author_id BIGINT NOT NULL,
    view_count INT DEFAULT 0,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    FOREIGN KEY (author_id) REFERENCES users(id),
    INDEX idx_type (type),
    INDEX idx_is_top (is_top),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ============================================
-- 12. 举报表 (report)
-- ============================================
DROP TABLE IF EXISTS report;
CREATE TABLE report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reporter_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    handle_result TEXT,
    handler_id BIGINT,
    handle_time DATETIME,
    create_time DATETIME NOT NULL,
    FOREIGN KEY (reporter_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES product(id),
    FOREIGN KEY (handler_id) REFERENCES users(id),
    INDEX idx_reporter_id (reporter_id),
    INDEX idx_product_id (product_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='举报表';

-- ============================================
-- 13. 反馈表 (feedback)
-- ============================================
DROP TABLE IF EXISTS feedback;
CREATE TABLE feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    contact_info VARCHAR(100),
    images JSON,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reply_content TEXT,
    replier_id BIGINT,
    reply_time DATETIME,
    create_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (replier_id) REFERENCES users(id),
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈表';

-- ============================================
-- 14. 搜索历史表 (search_history)
-- ============================================
DROP TABLE IF EXISTS search_history;
CREATE TABLE search_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    keyword VARCHAR(100) NOT NULL,
    search_count INT DEFAULT 1,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id),
    INDEX idx_keyword (keyword),
    INDEX idx_search_count (search_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索历史表';

-- ============================================
-- 插入测试数据
-- ============================================

-- 插入分类数据
INSERT INTO category (id, name, parent_id, sort_order, create_time, update_time) VALUES
(1, '手机', NULL, 1, NOW(), NOW()),
(2, '电脑', NULL, 2, NOW(), NOW()),
(3, '平板', NULL, 3, NOW(), NOW()),
(4, '耳机', NULL, 4, NOW(), NOW()),
(5, '相机', NULL, 5, NOW(), NOW()),
(6, '显示器', NULL, 6, NOW(), NOW()),
(7, '智能手表', NULL, 7, NOW(), NOW()),
(8, '配件', NULL, 8, NOW(), NOW());

-- 插入用户数据 (密码都是123456)
INSERT INTO users (id, username, password, student_id, real_name, phone, avatar, role, status, credit_score, create_time, update_time) VALUES
(1, 'admin', '$2a$10$N9qo8uLOickgx2ZMIQ6eV5JqG9/4g1OCw4Q2X1', 'ADMIN001', '管理员', '13800138000', 'https://via.placeholder.com/100', 'ADMIN', 'ACTIVE', 100, NOW(), NOW()),
(2, 'zhangsan', '$2a$10$N9qo8uLOickgx2ZMIQ6eV5JqG9/4g1OCw4Q2X1', '2021001', '张三', '13800138001', 'https://via.placeholder.com/100', 'USER', 'ACTIVE', 95, NOW(), NOW()),
(3, 'lisi', '$2a$10$N9qo8uLOickgx2ZMIQ6eV5JqG9/4g1OCw4Q2X1', '2021002', '李四', '13800138002', 'https://via.placeholder.com/100', 'USER', 'ACTIVE', 98, NOW(), NOW()),
(4, 'wangwu', '$2a$10$N9qo8uLOickgx2ZMIQ6eV5JqG9/4g1OCw4Q2X1', '2021003', '王五', '13800138003', 'https://via.placeholder.com/100', 'USER', 'ACTIVE', 92, NOW(), NOW()),
(5, 'zhaoliu', '$2a$10$N9qo8uLOickgx2ZMIQ6eV5JqG9/4g1OCw4Q2X1', '2021004', '赵六', '13800138004', 'https://via.placeholder.com/100', 'USER', 'ACTIVE', 88, NOW(), NOW()),
(6, 'sunqi', '$2a$10$N9qo8uLOickgx2ZMIQ6eV5JqG9/4g1OCw4Q2X1', '2021005', '孙七', '13800138005', 'https://via.placeholder.com/100', 'USER', 'ACTIVE', 90, NOW(), NOW()),
(7, 'zhouba', '$2a$10$N9qo8uLOickgx2ZMIQ6eV5JqG9/4g1OCw4Q2X1', '2021006', '周八', '13800138006', 'https://via.placeholder.com/100', 'USER', 'ACTIVE', 85, NOW(), NOW()),
(8, 'wujiu', '$2a$10$N9qo8uLOickgx2ZMIQ6eV5JqG9/4g1OCw4Q2X1', '2021007', '吴九', '13800138007', 'https://via.placeholder.com/100', 'USER', 'ACTIVE', 80, NOW(), NOW()),
(9, 'zhengshi', '$2a$10$N9qo8uLOickgx2ZMIQ6eV5JqG9/4g1OCw4Q2X1', '2021008', '郑十', '13800138008', 'https://via.placeholder.com/100', 'USER', 'ACTIVE', 75, NOW(), NOW()),
(10, 'qianyi', '$2a$10$N9qo8uLOickgx2ZMIQ6eV5JqG9/4g1OCw4Q2X1', '2021009', '钱一', '13800138009', 'https://via.placeholder.com/100', 'USER', 'ACTIVE', 70, NOW(), NOW());

-- 插入商品数据
INSERT INTO product (id, title, description, price, original_price, category_id, seller_id, status, audit_status, view_count, favorite_count, trade_type, create_time, update_time) VALUES
(1, 'iPhone 14 Pro 256G', '全新未拆封，国行正品，支持验机。颜色：深空黑。', 7999.00, 8999.00, 1, 2, 'ON_SALE', 'APPROVED', 156, 23, 'BOTH', NOW(), NOW()),
(2, 'MacBook Pro 14英寸 M3芯片', '2023款，16G+512G，深空灰色，使用半年，成色99新。', 12999.00, 16999.00, 2, 2, 'ON_SALE', 'APPROVED', 203, 45, 'BOTH', NOW(), NOW()),
(3, 'iPad Air 5 WiFi版 64G', '蓝色，2024年购入，几乎全新，带原装充电器和保护壳。', 4299.00, 4799.00, 3, 3, 'ON_SALE', 'APPROVED', 89, 12, 'BOTH', NOW(), NOW()),
(4, 'AirPods Pro 2代', '主动降噪，通透模式，音质极佳。充电仓有轻微划痕，不影响使用。', 1299.00, 1899.00, 4, 3, 'ON_SALE', 'APPROVED', 312, 67, 'EXPRESS', NOW(), NOW()),
(5, 'Sony A7M3 相机', '全画幅微单，含24-70mm镜头，快门数约5000，成色95新。', 8999.00, 12999.00, 5, 4, 'ON_SALE', 'APPROVED', 78, 15, 'FACE_TO_FACE', NOW(), NOW()),
(6, 'Dell U2723QE 27英寸4K显示器', 'IPS面板，99% sRGB，Type-C接口支持65W充电。使用一年，无坏点。', 2299.00, 2999.00, 6, 5, 'ON_SALE', 'APPROVED', 145, 34, 'BOTH', NOW(), NOW()),
(7, 'Apple Watch Series 9 45mm', '星光色，GPS+蜂窝网络版，含原装表带和充电器。使用3个月。', 3299.00, 3999.00, 7, 6, 'ON_SALE', 'APPROVED', 234, 56, 'BOTH', NOW(), NOW()),
(8, '罗技MX Master 3S鼠标', '人体工学设计，静音微动，支持多设备连接。黑色，使用半年。', 599.00, 799.00, 8, 6, 'ON_SALE', 'APPROVED', 167, 28, 'EXPRESS', NOW(), NOW()),
(9, 'iPhone 13 128G 粉色', '成色95新，无维修史，电池健康92%。粉色，含原装充电器。', 4299.00, 5999.00, 1, 7, 'ON_SALE', 'APPROVED', 198, 41, 'BOTH', NOW(), NOW()),
(10, '华为MateBook X Pro 2023', 'i7-1360H 32G+1T，星空灰，触控屏，使用8个月。', 8999.00, 10999.00, 2, 7, 'ON_SALE', 'APPROVED', 123, 29, 'BOTH', NOW(), NOW()),
(11, 'Samsung Galaxy S24 Ultra', '钛灰色，512G，国行正品，带原装充电器和耳机。全新未拆封。', 7499.00, 9999.00, 1, 8, 'ON_SALE', 'APPROVED', 287, 63, 'BOTH', NOW(), NOW()),
(12, 'Lenovo ThinkPad X1 Carbon', 'i7-1165G7 16G+1T SSD，黑色，碳纤维机身，重量仅1.13kg。', 5999.00, 12999.00, 2, 8, 'ON_SALE', 'APPROVED', 156, 22, 'FACE_TO_FACE', NOW(), NOW()),
(13, 'Bose QC45降噪耳机', '主动降噪，30小时续航，支持多点连接。黑色，使用半年。', 1899.00, 2299.00, 4, 8, 'ON_SALE', 'APPROVED', 267, 51, 'EXPRESS', NOW(), NOW()),
(14, 'iPad Pro 12.9英寸 M2芯片', '256G WiFi版，深空灰，2022款，带Apple Pencil二代。', 7999.00, 9999.00, 3, 9, 'ON_SALE', 'APPROVED', 134, 18, 'BOTH', NOW(), NOW()),
(15, 'Canon EOS R6 Mark II', '全画幅无反相机，2400万像素，含24-70mm镜头。成色98新。', 25999.00, 32999.00, 5, 9, 'ON_SALE', 'APPROVED', 89, 11, 'FACE_TO_FACE', NOW(), NOW()),
(16, 'LG UltraGear 27GP850', '27英寸4K 144Hz，IPS面板，1ms响应时间，支持HDR。使用半年。', 3999.00, 4999.00, 6, 9, 'ON_SALE', 'APPROVED', 178, 35, 'BOTH', NOW(), NOW()),
(17, 'Apple Watch Ultra 2 49mm', '钛金属表壳，双频GPS，100米防水，含原装表带。使用半年。', 5999.00, 7999.00, 7, 10, 'ON_SALE', 'APPROVED', 245, 58, 'BOTH', NOW(), NOW()),
(18, '雷蛇BlackWidow V3游戏键盘', '机械轴体，RGB背光，支持蓝牙和有线连接。黑色，全新未使用。', 899.00, 1299.00, 8, 10, 'ON_SALE', 'APPROVED', 132, 19, 'EXPRESS', NOW(), NOW()),
(19, 'iPhone 15 Pro Max 256G', '原色钛金属，256G，国行正品，带原装充电器。全新未拆封。', 8999.00, 10999.00, 1, 2, 'ON_SALE', 'APPROVED', 345, 89, 'BOTH', NOW(), NOW()),
(20, 'Dell XPS 15 9530', 'i9-13900H 32G+2T SSD，4K OLED屏，银色。使用1年，成色95新。', 15999.00, 21999.00, 2, 2, 'ON_SALE', 'APPROVED', 112, 17, 'BOTH', NOW(), NOW());

-- 插入商品图片数据
INSERT INTO product_image (product_id, image_url, sort_order, create_time) VALUES
(1, 'https://via.placeholder.com/400x400?text=iPhone14', 1, NOW()),
(1, 'https://via.placeholder.com/400x400?text=iPhone14-2', 2, NOW()),
(1, 'https://via.placeholder.com/400x400?text=iPhone14-3', 3, NOW()),
(2, 'https://via.placeholder.com/400x400?text=MacBook', 1, NOW()),
(2, 'https://via.placeholder.com/400x400?text=MacBook-2', 2, NOW()),
(3, 'https://via.placeholder.com/400x400?text=iPad', 1, NOW()),
(4, 'https://via.placeholder.com/400x400?text=AirPods', 1, NOW()),
(5, 'https://via.placeholder.com/400x400?text=Sony', 1, NOW()),
(6, 'https://via.placeholder.com/400x400?text=Dell', 1, NOW()),
(7, 'https://via.placeholder.com/400x400?text=Watch', 1, NOW()),
(8, 'https://via.placeholder.com/400x400?text=Mouse', 1, NOW()),
(9, 'https://via.placeholder.com/400x400?text=iPhone13', 1, NOW()),
(10, 'https://via.placeholder.com/400x400?text=Samsung', 1, NOW()),
(11, 'https://via.placeholder.com/400x400?text=ThinkPad', 1, NOW()),
(12, 'https://via.placeholder.com/400x400?text=Bose', 1, NOW()),
(13, 'https://via.placeholder.com/400x400?text=iPadPro', 1, NOW()),
(14, 'https://via.placeholder.com/400x400?text=Canon', 1, NOW()),
(15, 'https://via.placeholder.com/400x400?text=LG', 1, NOW()),
(16, 'https://via.placeholder.com/400x400?text=WatchUltra', 1, NOW()),
(17, 'https://via.placeholder.com/400x400?text=Razer', 1, NOW()),
(18, 'https://via.placeholder.com/400x400?text=iPhone15', 1, NOW()),
(19, 'https://via.placeholder.com/400x400?text=DellXPS', 1, NOW()),
(20, 'https://via.placeholder.com/400x400?text=DellXPS-2', 2, NOW());

-- 插入订单数据
INSERT INTO `order` (id, order_no, buyer_id, seller_id, product_id, status, trade_type, amount, address, express_no, create_time, update_time) VALUES
(1, 'ORD1700000000000001', 3, 2, 1, 'COMPLETED', 'EXPRESS', 12999.00, '北京市海淀区中关村大街1号', 'SF1234567890', DATE_SUB(NOW(), INTERVAL 15 DAY), NOW()),
(2, 'ORD1700000000000002', 4, 2, 2, 'COMPLETED', 'FACE_TO_FACE', 4299.00, '上海市浦东新区张江高科园区', NULL, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
(3, 'ORD1700000000000003', 5, 2, 3, 'SHIPPED', 'EXPRESS', 1299.00, '广州市天河区珠江新城', 'YT9876543210', DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
(4, 'ORD1700000000000004', 6, 3, 4, 'PENDING', 'FACE_TO_FACE', 8999.00, '深圳市南山区科技园', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(5, 'ORD1700000000000005', 7, 2, 5, 'CANCELLED', 'EXPRESS', 2299.00, '杭州市西湖区文三路', NULL, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW());

-- 插入评价数据
INSERT INTO review (id, order_id, user_id, rating, content, is_anonymous, create_time) VALUES
(1, 1, 3, 5, '商品成色很好，和描述一致，卖家发货很快，很满意！', FALSE, DATE_SUB(NOW(), INTERVAL 14 DAY)),
(2, 2, 4, 4, '面交交易很顺利，卖家人很好，商品没问题。', FALSE, DATE_SUB(NOW(), INTERVAL 9 DAY)),
(3, 3, 5, 5, '耳机音质很好，降噪效果明显，物流也很快。', FALSE, DATE_SUB(NOW(), INTERVAL 4 DAY));

-- 插入消息数据
INSERT INTO message (id, sender_id, receiver_id, content, type, is_read, create_time) VALUES
(1, 3, 2, '你好，这个iPhone还在吗？我想买。', 'TEXT', TRUE, DATE_SUB(NOW(), INTERVAL 12 DAY)),
(2, 2, 3, '在的，什么时候方便面交？', 'TEXT', TRUE, DATE_SUB(NOW(), INTERVAL 12 DAY)),
(3, 3, 2, '明天下午3点可以吗？', 'TEXT', TRUE, DATE_SUB(NOW(), INTERVAL 11 DAY)),
(4, 4, 2, '请问MacBook可以优惠一点吗？', 'TEXT', FALSE, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(5, 2, 4, '最低12500，不能再低了。', 'TEXT', TRUE, DATE_SUB(NOW(), INTERVAL 8 DAY));

-- 插入收藏数据
INSERT INTO favorite (id, user_id, product_id, create_time) VALUES
(1, 3, 1, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(2, 3, 2, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(3, 4, 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(4, 4, 3, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(5, 5, 4, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(6, 5, 5, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(7, 6, 1, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(8, 6, 2, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(9, 7, 3, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(10, 8, 1, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 插入关注数据
INSERT INTO follow (id, follower_id, following_id, create_time) VALUES
(1, 3, 2, DATE_SUB(NOW(), INTERVAL 15 DAY)),
(2, 3, 4, DATE_SUB(NOW(), INTERVAL 12 DAY)),
(3, 3, 5, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, 4, 2, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(5, 4, 3, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(6, 5, 2, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(7, 5, 3, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(8, 6, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(9, 6, 4, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(10, 7, 2, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 插入通知数据
INSERT INTO notification (id, user_id, title, content, type, related_id, is_read, create_time) VALUES
(1, 2, '商品审核通过', '您的商品「iPhone 14 Pro 256G」已通过审核，已上架。', 'ORDER', 1, TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 3, '新订单提醒', '您有新的订单「ORD1700000000000001」，请及时处理。', 'ORDER', 1, FALSE, DATE_SUB(NOW(), INTERVAL 15 DAY)),
(3, 4, '订单已完成', '订单「ORD1700000000000002」已完成，请对商品进行评价。', 'ORDER', 2, TRUE, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, 5, '收到新评价', '您的商品「iPad Air 5」收到了一条5星好评。', 'REVIEW', 3, FALSE, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(5, 6, '系统公告', '平台将于2024年1月1日进行系统升级，届时将暂停服务2小时。', 'SYSTEM', NULL, FALSE, NOW()),
(6, 7, '商品审核通过', '您的商品「MacBook Pro 14英寸」已通过审核，已上架。', 'ORDER', 2, TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(7, 8, '订单已发货', '订单「ORD1700000000000003」已发货，快递单号：YT9876543210。', 'ORDER', 3, FALSE, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(8, 9, '新订单提醒', '您有新的订单「ORD1700000000000004」，请及时处理。', 'ORDER', 4, FALSE, NOW()),
(9, 10, '收到新评价', '您的商品「AirPods Pro 2代」收到了一条5星好评。', 'REVIEW', 4, FALSE, NOW()),
(10, 2, '订单已取消', '订单「ORD1700000000000005」已被买家取消。', 'ORDER', 5, TRUE, DATE_SUB(NOW(), INTERVAL 7 DAY));

-- 插入公告数据
INSERT INTO announcement (id, title, content, type, is_top, author_id, view_count, create_time, update_time) VALUES
(1, '平台使用规范更新', '为了维护平台秩序，保障用户权益，平台对商品发布规范进行了更新。请各位用户仔细阅读并遵守。', 'IMPORTANT', TRUE, 1, 256, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
(2, '系统升级通知', '平台将于2024年1月15日凌晨2:00-4:00进行系统升级，届时将暂停服务。', 'URGENT', TRUE, 1, 512, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(3, '新功能上线', '平台新增了搜索历史功能，方便用户快速查找历史搜索记录。', 'NORMAL', FALSE, 1, 128, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(4, '春节放假通知', '平台将于2024年2月10日至2月17日放假，放假期间客服暂停服务。', 'IMPORTANT', FALSE, 1, 89, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
(5, '安全提醒', '近期发现有不法分子冒充平台客服进行诈骗，请各位用户提高警惕，不要轻易相信陌生人。', 'URGENT', TRUE, 1, 345, NOW(), NOW()),
(6, '用户权益保障', '平台为保障用户权益，新增了举报功能和反馈渠道，遇到问题请及时反馈。', 'NORMAL', FALSE, 1, 67, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW());

-- 插入举报数据
INSERT INTO report (id, reporter_id, product_id, type, reason, status, create_time) VALUES
(1, 4, 9, 'INAPPROPRIATE', '商品描述与实际不符，涉嫌虚假宣传。', 'PENDING', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 5, 10, 'FRAUD', '卖家收钱后不发货，疑似欺诈。', 'PROCESSING', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 6, 11, 'PROHIBITED', '该商品疑似违禁品，请核实。', 'PENDING', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, 7, 12, 'OTHER', '商品信息不完整，建议完善。', 'RESOLVED', DATE_SUB(NOW(), INTERVAL 7 DAY));

-- 插入反馈数据
INSERT INTO feedback (id, user_id, type, title, content, contact_info, status, create_time) VALUES
(1, 3, 'SUGGESTION', '建议增加夜间模式', '平台建议增加夜间模式，晚上使用时太刺眼。', '13800138001', 'RESOLVED', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(2, 4, 'BUG', '搜索功能异常', '搜索时偶尔会卡住，刷新后才能继续使用。', '13800138002', 'PROCESSING', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(3, 5, 'COMPLAINT', '订单处理慢', '我的订单已经3天了还没有处理，请加快处理速度。', '13800138003', 'PENDING', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, 6, 'OTHER', '希望增加更多分类', '建议增加更多商品分类，方便查找。', '13800138004', 'PENDING', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 7, 'SUGGESTION', '建议增加消息提醒', '建议增加消息推送功能，及时提醒新消息。', '13800138005', 'PENDING', NOW());

-- 插入搜索历史数据
INSERT INTO search_history (id, user_id, keyword, search_count, create_time, update_time) VALUES
(1, 3, 'iPhone', 5, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
(2, 3, 'MacBook', 3, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
(3, 3, 'iPad', 2, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(4, 4, 'AirPods', 4, DATE_SUB(NOW(), INTERVAL 6 DAY), NOW()),
(5, 4, '相机', 2, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),
(6, 5, '显示器', 3, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
(7, 5, '键盘', 2, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(8, 6, 'Samsung', 3, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),
(9, 6, 'ThinkPad', 2, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(10, 7, '耳机', 4, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW());

-- 数据统计查询
SELECT '用户总数' as 统计项, COUNT(*) as 数量 FROM users;
SELECT '商品总数' as 统计项, COUNT(*) as 数量 FROM product WHERE status = 'ON_SALE';
SELECT '订单总数' as 统计项, COUNT(*) as 数量 FROM `order`;
SELECT '待审核商品' as 统计项, COUNT(*) as 数量 FROM product WHERE audit_status = 'PENDING';
SELECT '已完成订单' as 统计项, COUNT(*) as 数量 FROM `order` WHERE status = 'COMPLETED';
