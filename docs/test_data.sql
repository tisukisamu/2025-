-- =============================================
-- 招聘管理系统测试数据
-- 数据库: MySQL 8.0+
-- 生成时间: 2024
-- =============================================

-- 清空现有数据
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE complaints_suggestions;
TRUNCATE TABLE company_ratings;
TRUNCATE TABLE candidate_feedback;
TRUNCATE TABLE interview_evaluations;
TRUNCATE TABLE evaluation_templates;
TRUNCATE TABLE talent_pool;
TRUNCATE TABLE talent_groups;
TRUNCATE TABLE exam_records;
TRUNCATE TABLE exam_papers;
TRUNCATE TABLE question_bank;
TRUNCATE TABLE interview_rooms;
TRUNCATE TABLE reports;
TRUNCATE TABLE statistics;
TRUNCATE TABLE message_templates;
TRUNCATE TABLE messages;
TRUNCATE TABLE announcements;
TRUNCATE TABLE interviews;
TRUNCATE TABLE applications;
TRUNCATE TABLE resumes;
TRUNCATE TABLE jobs;
TRUNCATE TABLE companies;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 用户表测试数据 (users)
-- 密码统一为: 123456 (BCrypt加密)
-- =============================================
INSERT INTO users (username, password, name, email, age, role, status, created_at, updated_at) VALUES
-- 管理员账户
('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '系统管理员', 'admin@recruit.com', 35, 'ADMIN', 'ACTIVE', NOW(), NOW()),

-- 求职者账户
('zhangsan', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '张三', 'zhangsan@example.com', 28, 'USER', 'ACTIVE', NOW(), NOW()),
('lisi', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '李四', 'lisi@example.com', 25, 'USER', 'ACTIVE', NOW(), NOW()),
('wangwu', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '王五', 'wangwu@example.com', 30, 'USER', 'ACTIVE', NOW(), NOW()),
('zhaoliu', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '赵六', 'zhaoliu@example.com', 26, 'USER', 'ACTIVE', NOW(), NOW()),
('sunqi', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '孙七', 'sunqi@example.com', 32, 'USER', 'ACTIVE', NOW(), NOW()),
('zhouba', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '周八', 'zhouba@example.com', 27, 'USER', 'ACTIVE', NOW(), NOW()),
('wujiu', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '吴九', 'wujiu@example.com', 29, 'USER', 'ACTIVE', NOW(), NOW()),
('zhengshi', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '郑十', 'zhengshi@example.com', 31, 'USER', 'ACTIVE', NOW(), NOW()),
('qianyi', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '钱一', 'qianyi@example.com', 24, 'USER', 'ACTIVE', NOW(), NOW()),

-- 企业HR账户
('hr_tech', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '科技有限公司HR', 'hr@techcompany.com', 35, 'USER', 'ACTIVE', NOW(), NOW()),
('hr_internet', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '互联网科技HR', 'hr@internettech.com', 32, 'USER', 'ACTIVE', NOW(), NOW()),
('hr_innovation', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '创新科技HR', 'hr@innovation.com', 30, 'USER', 'ACTIVE', NOW(), NOW()),
('hr_ai', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '智能科技HR', 'hr@aitech.com', 33, 'USER', 'ACTIVE', NOW(), NOW()),
('hr_data', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '数据科技HR', 'hr@datatech.com', 28, 'USER', 'ACTIVE', NOW(), NOW()),

-- 禁用账户测试
('disabled_user', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '已禁用用户', 'disabled@example.com', 28, 'USER', 'DISABLED', NOW(), NOW());

-- =============================================
-- 企业表测试数据 (companies)
-- =============================================
INSERT INTO companies (name, description, industry, scale, address, contact_person, contact_phone, contact_email, user_id, status, created_at, updated_at) VALUES
('科技有限公司', '一家专注于互联网技术开发的科技公司，致力于为企业提供优质的软件解决方案。公司拥有专业的技术团队，在Web开发、移动应用、云计算等领域具有丰富经验。', '互联网/电子商务', '100-499人', '北京市朝阳区建国路88号SOHO现代城', '张经理', '13800138001', 'hr@techcompany.com', 11, 'APPROVED', NOW(), NOW()),
('互联网科技', '专注于移动互联网产品研发的高新技术企业，主要业务包括社交应用、电商平台、在线教育等领域。', '移动互联网', '50-99人', '上海市浦东新区张江高科技园区', '李经理', '13800138002', 'hr@internettech.com', 12, 'APPROVED', NOW(), NOW()),
('创新科技', '一家以人工智能和大数据为核心的技术公司，为企业提供智能化解决方案。', '人工智能', '20-49人', '深圳市南山区科技园', '王经理', '13800138003', 'hr@innovation.com', 13, 'APPROVED', NOW(), NOW()),
('智能科技', '专注于智能制造和工业互联网领域，为制造业企业提供数字化转型解决方案。', '智能制造', '100-499人', '杭州市滨江区网商路', '赵经理', '13800138004', 'hr@aitech.com', 14, 'APPROVED', NOW(), NOW()),
('数据科技', '大数据分析与应用服务提供商，帮助企业实现数据驱动的业务决策。', '大数据', '50-99人', '广州市天河区珠江新城', '孙经理', '13800138005', 'hr@datatech.com', 15, 'APPROVED', NOW(), NOW()),
('金融科技', '专注于金融科技创新，为银行、保险、证券等金融机构提供技术解决方案。', '金融科技', '100-499人', '北京市西城区金融街', '周经理', '13800138006', 'hr@fintech.com', NULL, 'PENDING', NOW(), NOW()),
('医疗健康', '互联网医疗健康服务平台，提供在线问诊、健康管理等服务。', '医疗健康', '50-99人', '上海市静安区南京西路', '吴经理', '13800138007', 'hr@healthtech.com', NULL, 'PENDING', NOW(), NOW()),
('教育培训', '在线教育平台，提供编程、设计、语言等多种课程。', '在线教育', '20-49人', '杭州市西湖区', '郑经理', '13800138008', 'hr@edutech.com', NULL, 'REJECTED', NOW(), NOW());

-- =============================================
-- 职位表测试数据 (jobs)
-- =============================================
INSERT INTO jobs (company_id, title, description, requirements, salary_min, salary_max, location, job_type, education, experience, status, created_at, updated_at) VALUES
-- 科技有限公司的职位
(1, '高级前端工程师', '负责公司核心产品的前端架构设计和开发工作，参与技术选型和团队建设。', '精通Vue/React，熟悉TypeScript，有大型项目经验优先。', 25000, 40000, '北京·朝阳区', '全职', '本科', '3-5年', 'ACTIVE', NOW(), NOW()),
(1, 'Java开发工程师', '负责后端服务开发，参与系统架构设计和技术难题攻关。', '精通Java，熟悉Spring Boot、MySQL、Redis等技术栈。', 20000, 35000, '北京·朝阳区', '全职', '本科', '3-5年', 'ACTIVE', NOW(), NOW()),
(1, '产品经理', '负责产品规划、需求分析和项目推进，协调各部门完成产品落地。', '3年以上产品经验，有B端产品经验优先。', 18000, 30000, '北京·朝阳区', '全职', '本科', '3-5年', 'ACTIVE', NOW(), NOW()),
(1, 'UI设计师', '负责产品UI设计，制定设计规范，提升用户体验。', '精通Figma/Sketch，有优秀的设计作品集。', 15000, 25000, '北京·朝阳区', '全职', '本科', '1-3年', 'ACTIVE', NOW(), NOW()),

-- 互联网科技的职位
(2, '移动端开发工程师', '负责iOS/Android应用开发，优化应用性能和用户体验。', '熟悉Flutter或React Native，有移动端开发经验。', 20000, 35000, '上海·浦东新区', '全职', '本科', '3-5年', 'ACTIVE', NOW(), NOW()),
(2, '测试工程师', '负责产品质量保障，制定测试计划，执行功能测试和自动化测试。', '熟悉测试理论，有自动化测试经验优先。', 12000, 20000, '上海·浦东新区', '全职', '本科', '1-3年', 'ACTIVE', NOW(), NOW()),
(2, '运营专员', '负责用户运营和活动策划，提升用户活跃度和留存率。', '有互联网运营经验，数据敏感度高。', 10000, 18000, '上海·浦东新区', '全职', '本科', '1-3年', 'ACTIVE', NOW(), NOW()),

-- 创新科技的职位
(3, '算法工程师', '负责机器学习算法研发，优化模型性能。', '熟悉深度学习框架，有算法竞赛经验优先。', 30000, 50000, '深圳·南山区', '全职', '硕士', '3-5年', 'ACTIVE', NOW(), NOW()),
(3, '数据分析师', '负责数据分析和报表制作，为业务决策提供数据支持。', '熟练使用SQL、Python，有数据分析经验。', 15000, 25000, '深圳·南山区', '全职', '本科', '1-3年', 'ACTIVE', NOW(), NOW()),

-- 智能科技的职位
(4, '嵌入式工程师', '负责嵌入式系统开发和硬件驱动编写。', '熟悉C/C++，有嵌入式开发经验。', 18000, 30000, '杭州·滨江区', '全职', '本科', '3-5年', 'ACTIVE', NOW(), NOW()),
(4, '项目经理', '负责项目管理和团队协调，确保项目按时交付。', '有PMP证书，3年以上项目管理经验。', 20000, 35000, '杭州·滨江区', '全职', '本科', '5-10年', 'ACTIVE', NOW(), NOW()),

-- 数据科技的职位
(5, '大数据开发工程师', '负责大数据平台搭建和数据仓库开发。', '熟悉Hadoop、Spark等技术栈。', 25000, 40000, '广州·天河区', '全职', '本科', '3-5年', 'ACTIVE', NOW(), NOW()),
(5, '数据产品经理', '负责数据产品规划，推动数据产品落地。', '有数据产品经验，了解数据技术。', 20000, 35000, '广州·天河区', '全职', '本科', '3-5年', 'ACTIVE', NOW(), NOW()),

-- 草稿和已关闭的职位
(1, '实习生', '协助开发团队完成日常开发任务。', '计算机相关专业在校生。', 3000, 5000, '北京·朝阳区', '实习', '本科', '应届生', 'CLOSED', DATE_SUB(NOW(), INTERVAL 30 DAY), NOW()),
(2, '前端实习生', '参与前端开发工作。', '熟悉HTML/CSS/JavaScript。', 3000, 5000, '上海·浦东新区', '实习', '本科', '应届生', 'DRAFT', NOW(), NOW());

-- =============================================
-- 简历表测试数据 (resumes)
-- =============================================
INSERT INTO resumes (user_id, name, gender, age, phone, email, education, experience, skills, work_experience, project_experience, education_experience, created_at, updated_at) VALUES
(2, '张三', '男', 28, '13900139001', 'zhangsan@example.com', '本科', '3-5年', 'Vue,React,TypeScript,Node.js,Webpack', '2020-2024：科技有限公司 前端开发工程师\n2018-2020：互联网公司 前端开发工程师', '电商平台前端开发\n- 负责商品管理模块开发\n- 优化页面性能，提升用户体验\n\n在线教育平台开发\n- 使用Vue3重构前端架构\n- 实现视频播放和互动功能', '2014-2018：某某大学 计算机科学与技术 本科', NOW(), NOW()),
(3, '李四', '男', 25, '13900139002', 'lisi@example.com', '本科', '1-3年', 'Java,Spring Boot,MySQL,Redis,MyBatis', '2022-2024：科技有限公司 Java开发工程师', '用户管理系统\n- 设计并实现用户认证模块\n- 集成Redis缓存提升系统性能', '2018-2022：某某大学 软件工程 本科', NOW(), NOW()),
(4, '王五', '男', 30, '13900139003', 'wangwu@example.com', '硕士', '5-10年', 'Python,机器学习,深度学习,TensorFlow,PyTorch', '2019-2024：AI科技公司 算法工程师\n2016-2019：互联网公司 数据分析师', '智能推荐系统\n- 设计并实现个性化推荐算法\n- 提升用户点击率30%', '2013-2016：某某大学 人工智能 硕士\n2009-2013：某某大学 数学 本科', NOW(), NOW()),
(5, '赵六', '女', 26, '13900139004', 'zhaoliu@example.com', '本科', '1-3年', 'UI设计,Figma,Sketch,Photoshop,用户体验', '2022-2024：设计工作室 UI设计师', '移动应用UI设计\n- 负责多款APP的UI设计\n- 建立设计规范体系', '2018-2022：某某大学 视觉传达设计 本科', NOW(), NOW()),
(6, '孙七', '男', 32, '13900139005', 'sunqi@example.com', '本科', '5-10年', '项目管理,敏捷开发,团队管理,需求分析', '2019-2024：科技有限公司 项目经理\n2015-2019：互联网公司 高级开发工程师', '企业级项目管理系统\n- 带领10人团队完成项目交付\n- 项目按时交付率100%', '2009-2013：某某大学 信息管理 本科', NOW(), NOW()),
(7, '周八', '男', 27, '13900139006', 'zhouba@example.com', '本科', '3-5年', 'iOS,Swift,Objective-C,Xcode', '2021-2024：移动科技公司 iOS开发工程师\n2019-2021：创业公司 移动端开发', '社交APP开发\n- 独立完成iOS端开发\n- APP Store评分4.8分', '2015-2019：某某大学 计算机科学 本科', NOW(), NOW()),
(8, '吴九', '男', 29, '13900139007', 'wujiu@example.com', '本科', '3-5年', 'Android,Kotlin,Java,Jetpack', '2021-2024：互联网公司 Android开发工程师\n2019-2021：科技公司 移动开发', '电商APP开发\n- 负责核心模块开发\n- 日活用户100万+', '2015-2019：某某大学 软件工程 本科', NOW(), NOW()),
(9, '郑十', '女', 31, '13900139008', 'zhengshi@example.com', '硕士', '5-10年', '数据分析,Python,SQL,Tableau,统计学', '2020-2024：数据科技公司 数据分析师\n2016-2020：咨询公司 数据顾问', '用户行为分析项目\n- 建立用户画像体系\n- 为业务决策提供数据支持', '2014-2016：某某大学 统计学 硕士\n2010-2014：某某大学 数学 本科', NOW(), NOW()),
(10, '钱一', '男', 24, '13900139009', 'qianyi@example.com', '本科', '应届生', 'Python,Java,MySQL,Linux', '2024：科技公司 实习生', '毕业设计项目\n- 基于Spring Boot的电商系统\n- 实现用户注册登录、商品管理等功能', '2020-2024：某某大学 计算机科学 本科', NOW(), NOW());

-- =============================================
-- 投递记录表测试数据 (applications)
-- =============================================
INSERT INTO applications (user_id, job_id, resume_id, status, applied_at, updated_at) VALUES
-- 张三的投递
(2, 1, 1, 'REVIEWING', NOW(), NOW()),
(2, 5, 1, 'INTERVIEWED', DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(2, 9, 1, 'ACCEPTED', DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),

-- 李四的投递
(3, 2, 2, 'PENDING', NOW(), NOW()),
(3, 6, 2, 'REVIEWING', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),

-- 王五的投递
(4, 9, 3, 'INTERVIEWED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(4, 13, 3, 'PENDING', NOW(), NOW()),

-- 赵六的投递
(5, 4, 4, 'REVIEWING', NOW(), NOW()),

-- 孙七的投递
(6, 12, 5, 'INTERVIEWED', DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),
(6, 3, 5, 'REJECTED', DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),

-- 周八的投递
(7, 5, 6, 'PENDING', NOW(), NOW()),

-- 吴九的投递
(8, 5, 7, 'REVIEWING', NOW(), NOW()),

-- 郑十的投递
(9, 10, 8, 'INTERVIEWED', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(9, 14, 8, 'PENDING', NOW(), NOW()),

-- 钱一的投递
(10, 2, 9, 'PENDING', NOW(), NOW()),
(10, 6, 9, 'PENDING', NOW(), NOW());

-- =============================================
-- 面试记录表测试数据 (interviews)
-- =============================================
INSERT INTO interviews (application_id, interview_time, location, interviewer, result, feedback, created_at, updated_at) VALUES
(2, DATE_ADD(NOW(), INTERVAL 2 DAY), '线上面试 - 腾讯会议', '技术总监 张总', 'PENDING', NULL, NOW(), NOW()),
(3, DATE_SUB(NOW(), INTERVAL 1 DAY), '北京市朝阳区建国路88号 3楼会议室', 'HR 李经理', 'PASSED', '技术能力扎实，沟通能力强，建议录用。', NOW(), NOW()),
(6, DATE_ADD(NOW(), INTERVAL 3 DAY), '深圳市南山区科技园 A座', '算法组长 王工', 'PENDING', NULL, NOW(), NOW()),
(9, DATE_SUB(NOW(), INTERVAL 2 DAY), '杭州市滨江区网商路', '项目经理 赵经理', 'PASSED', '项目管理经验丰富，团队协作能力强。', NOW(), NOW()),
(14, DATE_ADD(NOW(), INTERVAL 1 DAY), '线上面试', '数据分析主管', 'PENDING', NULL, NOW(), NOW()),
(3, DATE_SUB(NOW(), INTERVAL 5 DAY), '线上面试 - 钉钉', '技术面试官', 'PASSED', '前端基础扎实，Vue熟练度高。', NOW(), NOW()),
(9, DATE_SUB(NOW(), INTERVAL 7 DAY), '深圳市南山区科技园 B座', 'HR经理', 'PASSED', '算法能力强，项目经验丰富。', NOW(), NOW());

-- =============================================
-- 面试房间表测试数据 (interview_rooms)
-- =============================================
INSERT INTO interview_rooms (interview_id, room_id, status, started_at, ended_at, recording_url, created_at) VALUES
(1, CONCAT('room_001_', UNIX_TIMESTAMP()), 'WAITING', NULL, NULL, NULL, NOW()),
(2, CONCAT('room_002_', UNIX_TIMESTAMP()), 'ENDED', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), '/recordings/interview_2.mp4', NOW()),
(3, CONCAT('room_003_', UNIX_TIMESTAMP()), 'WAITING', NULL, NULL, NULL, NOW()),
(4, CONCAT('room_004_', UNIX_TIMESTAMP()), 'ENDED', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), '/recordings/interview_4.mp4', NOW()),
(5, CONCAT('room_005_', UNIX_TIMESTAMP()), 'WAITING', NULL, NULL, NULL, NOW());

-- =============================================
-- 系统公告表测试数据 (announcements)
-- =============================================
INSERT INTO announcements (title, content, type, status, created_by, created_at, updated_at) VALUES
('系统升级公告', '尊敬的用户：\n\n为了给您提供更好的服务体验，我们将于2024年1月20日进行系统升级。升级期间系统将暂停服务，预计维护时间为凌晨2:00-6:00。\n\n给您带来的不便，敬请谅解。', 'NOTICE', 'PUBLISHED', 1, NOW(), NOW()),
('春节招聘季活动开启', '新年新机遇！春节招聘季正式开启，海量优质职位等你来投递。活动期间投递简历，还有机会获得精美礼品！', 'NOTICE', 'PUBLISHED', 1, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
('平台服务协议更新通知', '尊敬的用户：\n\n我们已更新《平台服务协议》，新增了用户隐私保护相关条款。请您仔细阅读更新后的协议内容。', 'POLICY', 'PUBLISHED', 1, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
('新功能上线：在线面试', '在线面试功能正式上线！企业可以通过平台直接发起视频面试，求职者可在线参加面试，让招聘更高效。', 'UPDATE', 'PUBLISHED', 1, DATE_SUB(NOW(), INTERVAL 15 DAY), NOW()),
('寒假实习专场招聘会', '寒假将至，我们特别推出实习专场招聘会，汇集数百家企业，提供上千个实习岗位，欢迎在校生参与！', 'NOTICE', 'DRAFT', 1, NOW(), NOW());

-- =============================================
-- 消息表测试数据 (messages)
-- =============================================
INSERT INTO messages (sender_id, receiver_id, title, content, type, is_read, related_id, created_at) VALUES
-- 系统消息
(1, 2, '欢迎加入招聘管理系统', '尊敬的张三，欢迎您注册成为我们的用户！请完善您的简历，开启求职之旅。', 'SYSTEM', 0, NULL, NOW()),
(1, 3, '欢迎加入招聘管理系统', '尊敬的李四，欢迎您注册成为我们的用户！请完善您的简历，开启求职之旅。', 'SYSTEM', 1, NULL, NOW()),

-- 面试邀请消息
(11, 2, '面试邀请', '您好，感谢您投递【高级前端工程师】职位。我们邀请您参加面试，时间：2024年1月18日 14:00，地点：线上面试。', 'INTERVIEW', 0, 1, NOW()),
(13, 4, '面试邀请', '您好，感谢您投递【算法工程师】职位。我们邀请您参加面试，时间：2024年1月19日 10:00，地点：深圳市南山区科技园。', 'INTERVIEW', 0, 6, NOW()),

-- 投递状态消息
(11, 3, '简历已查看', '您投递的【Java开发工程师】职位，企业已查看您的简历。', 'APPLICATION', 1, 4, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(12, 2, '面试通过', '恭喜！您投递的【移动端开发工程师】职位，面试已通过。', 'APPLICATION', 0, 3, NOW()),

-- 公告通知
(1, 2, '系统升级公告', '系统将于2024年1月20日进行升级维护，届时系统将暂停服务。', 'ANNOUNCEMENT', 0, 1, NOW()),
(1, 3, '系统升级公告', '系统将于2024年1月20日进行升级维护，届时系统将暂停服务。', 'ANNOUNCEMENT', 0, 1, NOW()),
(1, 4, '春节招聘季活动开启', '新年新机遇！春节招聘季正式开启，海量优质职位等你来投递。', 'ANNOUNCEMENT', 1, 2, DATE_SUB(NOW(), INTERVAL 5 DAY));

-- =============================================
-- 消息模板表测试数据 (message_templates)
-- =============================================
INSERT INTO message_templates (name, code, type, subject, content, variables, status, created_at, updated_at) VALUES
('面试邀请模板', 'INTERVIEW_INVITE', 'EMAIL', '【招聘系统】面试邀请通知', '尊敬的{userName}：\n\n您好！感谢您投递{jobTitle}职位。我们诚挚邀请您参加面试。\n\n面试时间：{interviewTime}\n面试地点：{interviewLocation}\n\n请准时参加，如有问题请联系我们。', 'userName,jobTitle,interviewTime,interviewLocation', 'ACTIVE', NOW(), NOW()),
('简历投递成功模板', 'APPLICATION_SUCCESS', 'EMAIL', '【招聘系统】简历投递成功', '尊敬的{userName}：\n\n您已成功投递{jobTitle}职位，企业将尽快审核您的简历。', 'userName,jobTitle,companyName', 'ACTIVE', NOW(), NOW()),
('面试结果通知模板', 'INTERVIEW_RESULT', 'EMAIL', '【招聘系统】面试结果通知', '尊敬的{userName}：\n\n您投递的{jobTitle}职位，面试结果：{result}。\n\n{feedback}', 'userName,jobTitle,result,feedback', 'ACTIVE', NOW(), NOW()),
('账户审核通过模板', 'ACCOUNT_APPROVED', 'EMAIL', '【招聘系统】账户审核通过', '尊敬的{companyName}：\n\n恭喜！您的企业账户已审核通过，现在可以开始发布职位了。', 'companyName', 'ACTIVE', NOW(), NOW()),
('系统通知模板', 'SYSTEM_NOTICE', 'IN_APP', '系统通知', '{content}', 'content', 'ACTIVE', NOW(), NOW()),
('短信验证码模板', 'SMS_CODE', 'SMS', NULL, '您的验证码是：{code}，有效期5分钟，请勿泄露给他人。', 'code', 'ACTIVE', NOW(), NOW()),
('面试提醒模板', 'INTERVIEW_REMINDER', 'SMS', NULL, '尊敬的{userName}，您预约的{jobTitle}职位面试将于{interviewTime}开始，请准时参加。', 'userName,jobTitle,interviewTime', 'ACTIVE', NOW(), NOW());

-- =============================================
-- 人才分组表测试数据 (talent_groups)
-- =============================================
INSERT INTO talent_groups (company_id, name, description, color, created_at, updated_at) VALUES
(1, '前端开发', '前端开发相关人才', '#1890ff', NOW(), NOW()),
(1, '后端开发', '后端开发相关人才', '#52c41a', NOW(), NOW()),
(1, '产品运营', '产品和运营人才', '#faad14', NOW(), NOW()),
(2, '移动开发', '移动端开发人才', '#eb2f96', NOW(), NOW()),
(3, '算法人才', '算法和AI人才', '#722ed1', NOW(), NOW()),
(4, '项目管理', '项目管理人才', '#13c2c2', NOW(), NOW()),
(5, '数据分析', '数据分析人才', '#fa541c', NOW(), NOW());

-- =============================================
-- 人才库表测试数据 (talent_pool)
-- =============================================
INSERT INTO talent_pool (company_id, resume_id, user_id, tags, group_id, status, notes, added_at, updated_at) VALUES
(1, 1, 2, '前端,Vue,React', 1, 'COLLECTED', '技术能力优秀，沟通能力强', NOW(), NOW()),
(1, 2, 3, 'Java,后端', 2, 'CONTACTED', '已电话沟通，意向明确', NOW(), NOW()),
(1, 4, 5, 'UI设计', 3, 'INTERVIEWED', '面试表现良好', NOW(), NOW()),
(2, 6, 7, 'iOS,移动端', 4, 'COLLECTED', '有独立开发经验', NOW(), NOW()),
(3, 3, 4, '算法,机器学习', 5, 'COLLECTED', '算法能力强', NOW(), NOW()),
(4, 5, 6, '项目管理,PMP', 6, 'CONTACTED', '项目管理经验丰富', NOW(), NOW()),
(5, 8, 9, '数据分析,Python', 7, 'COLLECTED', '数据分析能力强', NOW(), NOW());

-- =============================================
-- 评价模板表测试数据 (evaluation_templates)
-- =============================================
INSERT INTO evaluation_templates (name, description, dimensions, total_score, created_by, created_at, updated_at) VALUES
('技术岗位评价模板', '适用于技术类岗位的面试评价', '[{"name":"技术能力","weight":40},{"name":"沟通能力","weight":20},{"name":"学习能力","weight":20},{"name":"团队协作","weight":20}]', 100, 1, NOW(), NOW()),
('产品岗位评价模板', '适用于产品类岗位的面试评价', '[{"name":"产品思维","weight":30},{"name":"沟通能力","weight":25},{"name":"数据分析","weight":25},{"name":"项目管理","weight":20}]', 100, 1, NOW(), NOW()),
('通用岗位评价模板', '适用于所有岗位的通用评价', '[{"name":"专业能力","weight":30},{"name":"沟通表达","weight":25},{"name":"学习能力","weight":25},{"name":"团队协作","weight":20}]', 100, 1, NOW(), NOW()),
('管理岗位评价模板', '适用于管理类岗位的面试评价', '[{"name":"领导力","weight":30},{"name":"战略思维","weight":25},{"name":"团队管理","weight":25},{"name":"沟通协调","weight":20}]', 100, 1, NOW(), NOW());

-- =============================================
-- 面试评价表测试数据 (interview_evaluations)
-- =============================================
INSERT INTO interview_evaluations (interview_id, interviewer_id, evaluation_template_id, scores, comments, overall_score, recommendation, created_at) VALUES
(2, 11, 1, '{"技术能力":85,"沟通能力":90,"学习能力":88,"团队协作":85}', '技术基础扎实，沟通表达清晰，学习能力较强。建议录用。', 87, 'RECOMMEND', NOW()),
(4, 13, 1, '{"技术能力":92,"沟通能力":88,"学习能力":95,"团队协作":90}', '算法能力突出，有丰富的项目经验。强烈推荐录用。', 91, 'STRONGLY_RECOMMEND', NOW()),
(5, 14, 3, '{"专业能力":88,"沟通表达":85,"学习能力":82,"团队协作":90}', '项目管理经验丰富，团队协作能力强。建议录用。', 86, 'RECOMMEND', NOW()),
(6, 11, 1, '{"技术能力":80,"沟通能力":85,"学习能力":78,"团队协作":82}', '前端基础扎实，Vue框架熟练。可以考虑录用。', 81, 'NEUTRAL', NOW()),
(7, 13, 1, '{"技术能力":95,"沟通能力":90,"学习能力":92,"团队协作":88}', '算法能力非常突出，解决问题能力强。强烈推荐录用。', 91, 'STRONGLY_RECOMMEND', NOW());

-- =============================================
-- 候选人反馈表测试数据 (candidate_feedback)
-- =============================================
INSERT INTO candidate_feedback (interview_id, user_id, rating, feedback, created_at) VALUES
(2, 2, 5, '面试官很专业，公司环境很好，期待加入！', NOW()),
(4, 4, 4, '面试流程规范，技术问题有深度。', NOW()),
(5, 6, 5, '面试体验很好，HR很热情。', NOW()),
(6, 2, 4, '面试官技术能力强，沟通顺畅。', NOW()),
(7, 4, 5, '公司技术氛围很好，面试官很专业。', NOW());

-- =============================================
-- 企业评分表测试数据 (company_ratings)
-- =============================================
INSERT INTO company_ratings (company_id, user_id, rating, comment, created_at) VALUES
(1, 2, 5, '公司很正规，面试流程规范，HR很专业。', NOW()),
(1, 3, 4, '面试体验不错，期待加入。', NOW()),
(2, 4, 5, '技术氛围很好，面试官很专业。', NOW()),
(3, 6, 4, '公司发展前景好，团队氛围不错。', NOW()),
(4, 7, 5, '面试体验很好，公司环境优美。', NOW()),
(5, 9, 4, '数据分析岗位很有挑战性，期待加入。', NOW());

-- =============================================
-- 投诉建议表测试数据 (complaints_suggestions)
-- =============================================
INSERT INTO complaints_suggestions (user_id, type, title, content, status, handler_id, handling_result, created_at, updated_at) VALUES
(2, 'SUGGESTION', '建议增加职位收藏功能', '希望能够增加职位收藏功能，方便后续查看。', 'RESOLVED', 1, '感谢您的建议，我们将在下一版本中增加此功能。', NOW(), NOW()),
(3, 'COMPLAINT', '某企业长期不处理简历', '投递简历后，企业长期不查看也不处理，建议平台介入。', 'PROCESSING', 1, NULL, NOW(), NOW()),
(5, 'SUGGESTION', '建议优化搜索功能', '职位搜索可以增加更多筛选条件，如公司规模等。', 'PENDING', NULL, NULL, NOW(), NOW()),
(7, 'COMPLAINT', '面试时间冲突', '企业安排的面试时间与我其他面试冲突，希望能协调调整。', 'RESOLVED', 1, '已与企业沟通，重新安排面试时间。', NOW(), NOW()),
(9, 'SUGGESTION', '建议增加薪资范围筛选', '希望能按薪资范围筛选职位，提高求职效率。', 'REJECTED', 1, '该功能已在开发计划中，预计下个版本上线。', NOW(), NOW());

-- =============================================
-- 题库表测试数据 (question_bank)
-- =============================================
INSERT INTO question_bank (category, type, question, options, answer, difficulty, tags, created_by, created_at, updated_at) VALUES
('前端开发', 'SINGLE_CHOICE', 'Vue3中，以下哪个是正确的组合式API导入方式？', '["import { ref } from ''vue''","import ref from ''vue''","import * as ref from ''vue''","require(''vue'').ref"]', 'import { ref } from ''vue''', 'EASY', 'Vue,Vue3', 1, NOW(), NOW()),
('前端开发', 'SINGLE_CHOICE', '以下哪个不是JavaScript的数据类型？', '["string","number","array","boolean"]', 'array', 'EASY', 'JavaScript', 1, NOW(), NOW()),
('前端开发', 'SINGLE_CHOICE', 'React中，用于管理组件状态的Hook是？', '["useEffect","useState","useContext","useRef"]', 'useState', 'EASY', 'React', 1, NOW(), NOW()),
('前端开发', 'MULTIPLE_CHOICE', '以下哪些是CSS预处理器？', '["Sass","Less","Stylus","PostCSS"]', 'Sass,Less,Stylus', 'EASY', 'CSS', 1, NOW(), NOW()),
('后端开发', 'SINGLE_CHOICE', 'Spring Boot中，@Autowired注解的作用是？', '["定义Bean","自动装配","配置文件","路由映射"]', '自动装配', 'EASY', 'Spring Boot,Java', 1, NOW(), NOW()),
('后端开发', 'SINGLE_CHOICE', 'MySQL中，以下哪个存储引擎支持事务？', '["MyISAM","Memory","InnoDB","Archive"]', 'InnoDB', 'EASY', 'MySQL,数据库', 1, NOW(), NOW()),
('后端开发', 'SINGLE_CHOICE', 'Redis默认端口号是多少？', '["3306","6379","8080","27017"]', '6379', 'EASY', 'Redis', 1, NOW(), NOW()),
('后端开发', 'MULTIPLE_CHOICE', '以下哪些是Java的垃圾回收算法？', '["标记-清除","复制算法","标记-整理","分代收集"]', '标记-清除,复制算法,标记-整理,分代收集', 'MEDIUM', 'Java,JVM', 1, NOW(), NOW()),
('算法', 'CODING', '实现一个函数，判断一个字符串是否为回文字符串', NULL, 'function isPalindrome(str) { return str === str.split('''').reverse().join(''''); }', 'EASY', '算法,字符串', 1, NOW(), NOW()),
('算法', 'CODING', '实现一个函数，找出数组中的最大值', NULL, 'function findMax(arr) { return Math.max(...arr); }', 'EASY', '算法,数组', 1, NOW(), NOW()),
('算法', 'CODING', '实现一个函数，将字符串反转', NULL, 'function reverseString(str) { return str.split('''').reverse().join(''''); }', 'EASY', '算法,字符串', 1, NOW(), NOW()),
('算法', 'SINGLE_CHOICE', '快速排序的平均时间复杂度是？', '["O(n)","O(nlogn)","O(n^2)","O(logn)"]', 'O(nlogn)', 'MEDIUM', '算法,排序', 1, NOW(), NOW()),
('数据库', 'SINGLE_CHOICE', 'SQL中，用于去重的关键字是？', '["DISTINCT","UNIQUE","DIFFERENT","GROUP BY"]', 'DISTINCT', 'EASY', 'SQL,数据库', 1, NOW(), NOW()),
('数据库', 'SINGLE_CHOICE', '以下哪个不是SQL的聚合函数？', '["COUNT","SUM","AVG","CONCAT"]', 'CONCAT', 'EASY', 'SQL,数据库', 1, NOW(), NOW()),
('系统设计', 'ESSAY', '请描述如何设计一个高并发的秒杀系统', NULL, '需要考虑：1.限流 2.缓存 3.消息队列 4.分布式锁 5.数据库优化', 'HARD', '系统设计,高并发', 1, NOW(), NOW());

-- =============================================
-- 试卷表测试数据 (exam_papers)
-- =============================================
INSERT INTO exam_papers (name, description, duration, total_score, pass_score, question_ids, created_by, created_at, updated_at) VALUES
('前端开发基础测试', '测试前端开发基础知识，包含Vue、React、JavaScript等内容', 30, 100, 60, '1,2,3,4', 1, NOW(), NOW()),
('后端开发基础测试', '测试后端开发基础知识，包含Spring Boot、MySQL、Redis等内容', 30, 100, 60, '5,6,7,8', 1, NOW(), NOW()),
('算法基础测试', '测试算法基础能力，包含字符串、数组、排序等内容', 45, 100, 60, '9,10,11,12', 1, NOW(), NOW()),
('数据库基础测试', '测试数据库基础知识，包含SQL语法、聚合函数等内容', 20, 100, 60, '13,14', 1, NOW(), NOW()),
('综合能力测试', '综合测试前端、后端、算法能力', 60, 100, 70, '1,5,9,13', 1, NOW(), NOW());

-- =============================================
-- 考试记录表测试数据 (exam_records)
-- =============================================
INSERT INTO exam_records (paper_id, user_id, application_id, score, is_passed, answers, started_at, submitted_at, created_at) VALUES
(1, 2, 1, 85, 1, '{"1":"A","2":"C","3":"B","4":"A,B,C"}', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(2, 3, 4, 72, 1, '{"5":"B","6":"C","7":"B","8":"A,B,C,D"}', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(3, 4, 6, 90, 1, '{"9":"pass","10":"pass","11":"pass","12":"B"}', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(1, 5, 8, 55, 0, '{"1":"B","2":"A","3":"C","4":"A,B"}', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(5, 6, 9, 78, 1, '{"1":"A","5":"B","9":"pass","13":"A"}', NOW(), NULL, NOW());

-- =============================================
-- 统计数据表测试数据 (statistics)
-- =============================================
INSERT INTO statistics (type, date, metrics, created_at) VALUES
('DAILY_APPLICATION', CURDATE(), '{"total":156,"pending":45,"reviewing":38,"interviewed":28,"accepted":25,"rejected":20}', NOW()),
('DAILY_APPLICATION', DATE_SUB(CURDATE(), INTERVAL 1 DAY), '{"total":142,"pending":42,"reviewing":35,"interviewed":25,"accepted":22,"rejected":18}', NOW()),
('DAILY_APPLICATION', DATE_SUB(CURDATE(), INTERVAL 2 DAY), '{"total":138,"pending":40,"reviewing":33,"interviewed":23,"accepted":20,"rejected":22}', NOW()),
('DAILY_REGISTRATION', CURDATE(), '{"users":28,"companies":5}', NOW()),
('DAILY_REGISTRATION', DATE_SUB(CURDATE(), INTERVAL 1 DAY), '{"users":25,"companies":3}', NOW()),
('DAILY_REGISTRATION', DATE_SUB(CURDATE(), INTERVAL 2 DAY), '{"users":30,"companies":4}', NOW()),
('DAILY_JOB', CURDATE(), '{"published":15,"closed":3,"total_active":1258}', NOW()),
('DAILY_JOB', DATE_SUB(CURDATE(), INTERVAL 1 DAY), '{"published":12,"closed":2,"total_active":1246}', NOW()),
('DAILY_JOB', DATE_SUB(CURDATE(), INTERVAL 2 DAY), '{"published":18,"closed":5,"total_active":1236}', NOW()),
('MONTHLY_SUMMARY', DATE_FORMAT(CURDATE(), '%Y-%m-01'), '{"total_users":1580,"total_companies":125,"total_jobs":1258,"total_applications":4560}', NOW());

-- =============================================
-- 报表表测试数据 (reports)
-- =============================================
INSERT INTO reports (name, type, parameters, file_path, status, created_by, created_at) VALUES
('2024年1月招聘报告', 'RECRUITMENT', '{"month":"2024-01","company_id":1}', '/reports/2024_01_recruitment_company_1.pdf', 'COMPLETED', 11, NOW()),
('人才分析报告', 'TALENT', '{"industry":"互联网","education":"本科"}', '/reports/talent_analysis_internet.pdf', 'COMPLETED', 1, NOW()),
('企业招聘效率报告', 'COMPANY', '{"company_id":2,"period":"2024-01"}', '/reports/company_2_efficiency.pdf', 'COMPLETED', 12, NOW()),
('系统整体招聘报告', 'RECRUITMENT', '{"period":"2024-01"}', NULL, 'PENDING', 1, NOW()),
('人才流动分析报告', 'TALENT', '{"period":"2024-01"}', NULL, 'PENDING', 1, NOW());

-- =============================================
-- 完成提示
-- =============================================
SELECT '=============================================' as '';
SELECT '测试数据插入完成！' as message;
SELECT '=============================================' as '';
SELECT '默认密码: 123456' as password_info;
SELECT '管理员账户: admin' as admin_account;
SELECT '=============================================' as '';
SELECT '用户统计:' as '';
SELECT COUNT(*) as total_users FROM users;
SELECT '企业统计:' as '';
SELECT COUNT(*) as total_companies FROM companies;
SELECT '职位统计:' as '';
SELECT COUNT(*) as total_jobs FROM jobs;
SELECT '简历统计:' as '';
SELECT COUNT(*) as total_resumes FROM resumes;
SELECT '投递统计:' as '';
SELECT COUNT(*) as total_applications FROM applications;
SELECT '=============================================' as '';
