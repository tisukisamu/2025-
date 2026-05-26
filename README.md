# 农产品售卖系统 (Agri-Store)

基于 Spring Boot + Vue 3 的全栈农产品电商平台。

## 1. 项目简介
本项目分为前台购物端、后台管理端以及商家端，旨在为农产品提供线上的展示、销售、订单追踪及多商户入驻的一体化解决方案。

**核心功能**:
- 用户注册/登录、商品浏览、购物车、订单管理
- 商品收藏、评论、评分
- 多级商品分类
- 店铺入驻申请、店铺商品管理
- 管理员审核（店铺审核、商品审核）
- 库存预警、操作日志

## 2. 技术栈
### 后端 (Backend)
- **框架**: Spring Boot 3.2
- **持久层**: Spring Data JPA (Hibernate)
- **数据库**: MySQL 8.0
- **鉴权**: Spring Security + JWT
- **构建工具**: Maven
- **JDK**: Java 17

### 前端 (Frontend)
- **框架**: Vue 3 (Composition API)
- **构建**: Vite 5
- **UI 组件**: Ant Design Vue 4
- **样式**: UnoCSS (原子化 CSS)
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP 客户端**: Axios

## 3. IDEA 配置说明

### 3.1 必需插件
在 IntelliJ IDEA 中打开项目前，请确保已安装以下插件：
- **Lombok** - 必需，用于处理 `@Data` 等注解
- **Spring Boot** - 推荐，提供更好的 Spring Boot 支持

### 3.2 启用注解处理器
1. 打开 IDEA 设置 (File -> Settings)
2. 搜索 "Annotation Processors"
3. 勾选 "Enable annotation processing"
4. 选择 "Obtain processors from project classpath"

### 3.3 项目导入步骤
1. 打开 IDEA
2. 选择 "Open" 或 "Import Project"
3. 选择 `backend/pom.xml` 文件
4. 等待 Maven 自动导入依赖
5. 如果提示 Lombok 问题，请安装 Lombok 插件并重启 IDEA

### 3.4 运行配置
1. 找到 `AgristoreBackendApplication.java`
2. 右键选择 "Run" 或 "Debug"
3. 或者使用 Maven 工具栏运行 `spring-boot:run`

## 4. 项目结构
```
project-root/
├── backend/                          # Spring Boot 后端
│   ├── src/main/java/com/agri/store/
│   │   ├── config/                   # 配置类
│   │   │   ├── SecurityConfig.java   # Spring Security + JWT 配置
│   │   │   └── GlobalExceptionHandler.java # 全局异常处理
│   │   ├── controller/               # REST API 控制器
│   │   │   ├── AuthController.java   # 认证接口
│   │   │   ├── UserController.java   # 用户接口
│   │   │   ├── ProductController.java# 商品接口
│   │   │   ├── OrderController.java  # 订单接口
│   │   │   ├── CategoryController.java # 分类接口
│   │   │   ├── FavoriteController.java # 收藏接口
│   │   │   ├── CommentController.java  # 评论接口
│   │   │   ├── StoreController.java    # 店铺接口
│   │   │   ├── StoreProductController.java # 店铺商品接口
│   │   │   ├── StoreOrderController.java   # 店铺订单接口
│   │   │   └── AdminController.java  # 管理后台接口
│   │   ├── dto/                      # 数据传输对象
│   │   ├── entity/                   # JPA 实体类
│   │   │   ├── User.java             # 用户实体
│   │   │   ├── Product.java          # 商品实体
│   │   │   ├── Order.java            # 订单实体
│   │   │   ├── OrderItem.java        # 订单项实体
│   │   │   ├── Store.java            # 店铺实体
│   │   │   ├── Category.java         # 分类实体
│   │   │   ├── Favorite.java         # 收藏实体
│   │   │   ├── Comment.java          # 评论实体
│   │   │   ├── OperationLog.java     # 操作日志实体
│   │   │   └── StockLog.java         # 库存日志实体
│   │   ├── repository/               # JPA 仓库接口
│   │   ├── security/                 # 安全相关
│   │   └── service/                  # 业务逻辑层
│   └── src/main/resources/
│       └── application.yml           # 应用配置
├── frontend/                         # Vue 3 前端
│   ├── src/
│   │   ├── api/                      # API 服务
│   │   │   └── index.ts              # 封装所有后端接口
│   │   ├── components/               # 公共组件
│   │   ├── router/                   # 路由配置
│   │   ├── stores/                   # Pinia 状态管理
│   │   ├── views/                    # 页面视图
│   │   │   ├── Home.vue              # 首页
│   │   │   ├── Login.vue             # 登录/注册
│   │   │   ├── ProductList.vue       # 商品列表
│   │   │   ├── ProductDetail.vue     # 商品详情
│   │   │   ├── Cart.vue              # 购物车
│   │   │   ├── Orders.vue            # 我的订单
│   │   │   ├── MyFavorites.vue       # 我的收藏
│   │   │   ├── admin/                # 管理后台
│   │   │   │   ├── Layout.vue        # 后台布局
│   │   │   │   ├── Dashboard.vue     # 数据大屏
│   │   │   │   ├── Products.vue      # 商品管理
│   │   │   │   ├── Orders.vue        # 订单管理
│   │   │   │   ├── Users.vue         # 用户管理
│   │   │   │   ├── StoreAudit.vue    # 店铺审核
│   │   │   │   └── ProductAudit.vue  # 商品审核
│   │   │   └── store/                # 商家端
│   │   │       ├── StoreLayout.vue   # 店铺布局
│   │   │       ├── StoreDashboard.vue # 店铺概览
│   │   │       ├── StoreProducts.vue # 商品管理
│   │   │       ├── StoreOrders.vue   # 订单管理
│   │   │       ├── StoreStats.vue    # 数据统计
│   │   │       └── StoreSettings.vue # 店铺设置
│   │   ├── App.vue                   # 根组件
│   │   └── main.ts                   # 入口文件
│   ├── package.json
│   ├── vite.config.ts                # Vite 配置
│   └── uno.config.ts                 # UnoCSS 配置
├── BACKEND_DESIGN.md                 # 后端详细设计
├── FRONTEND_DESIGN.md                # 前端详细设计
├── ENTITY_DOCUMENTATION.md           # 实体类文档
└── README.md                         # 项目说明
```

## 5. 快速开始
### 后端启动
1. 创建 MySQL 数据库 `agri_store`。
2. 修改 `backend/src/main/resources/application.yml` 中的数据库配置：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/agri_store?useSSL=false&serverTimezone=UTC
       username: root
       password: your_password
   ```
3. 运行 `cd backend && mvn spring-boot:run`。

### 前端启动
1. `cd frontend`
2. `npm install`
3. `npm run dev`

## 6. API 接口文档

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 用户接口
- `GET /api/user/profile` - 获取用户信息
- `PUT /api/user/profile` - 更新用户信息
- `PUT /api/user/password` - 修改密码

### 商品接口
- `GET /api/products` - 获取所有商品
- `GET /api/products/{id}` - 获取商品详情
- `GET /api/products/new` - 获取新品商品
- `GET /api/products/hot` - 获取热销商品
- `GET /api/products/search` - 搜索商品（分页）

### 分类接口
- `GET /api/category/list` - 获取所有分类
- `GET /api/category/tree` - 获取分类树
- `GET /api/category/children/{parentId}` - 获取子分类

### 收藏接口
- `GET /api/favorites/user/{userId}` - 获取用户收藏
- `POST /api/favorites` - 添加收藏
- `DELETE /api/favorites/{userId}/{productId}` - 取消收藏

### 评论接口
- `GET /api/comments/product/{productId}` - 获取商品评论
- `POST /api/comments` - 创建评论

### 订单接口
- `GET /api/orders` - 获取我的订单
- `POST /api/orders` - 创建订单
- `GET /api/orders/{id}` - 获取订单详情
- `POST /api/orders/{id}/cancel` - 取消订单
- `POST /api/orders/{id}/confirm` - 确认收货
- `GET /api/orders/tracking/{orderNo}` - 追踪订单

### 店铺接口
- `POST /api/store/register` - 注册店铺
- `GET /api/store/my` - 获取我的店铺
- `PUT /api/store/my` - 更新店铺信息

### 店铺商品接口
- `GET /api/store/product/list` - 获取店铺商品列表
- `POST /api/store/product/create` - 创建商品
- `PUT /api/store/product/{id}` - 更新商品
- `DELETE /api/store/product/{id}` - 删除商品
- `POST /api/store/product/upload` - 上传图片

### 店铺订单接口
- `GET /api/store/order/list` - 获取店铺订单
- `POST /api/store/order/{id}/ship` - 订单发货

### 管理后台接口
- `GET /api/admin/stats` - 获取统计数据
- `GET /api/admin/users` - 获取用户列表
- `GET /api/admin/orders` - 获取所有订单
- `GET /api/admin/stores` - 获取店铺列表
- `PUT /api/admin/store/{id}/audit` - 审核店铺
- `GET /api/admin/products` - 获取商品列表
- `PUT /api/admin/product/{id}/audit` - 审核商品
- `GET /api/admin/logs` - 获取操作日志

## 7. 核心功能说明

### 7.1 鉴权流程
1. 用户登录成功后，后端返回 JWT Token
2. 前端将 Token 存储在 localStorage
3. 后续请求在 Header 中携带 `Authorization: Bearer <token>`
4. 后端通过 `JwtAuthenticationFilter` 验证 Token

### 7.2 订单状态流转
```
待发货 (1) -> 已发货 (2) -> 已完成 (3)
```

### 7.3 店铺审核流程
```
待审核 (0) -> 已通过 (1) / 已驳回 (2) / 已禁用 (3)
```

### 7.4 商品审核流程
```
待审核 (0) -> 已通过 (1) / 已驳回 (2)
```

### 7.5 购物车实现
- 使用 Pinia 管理购物车状态
- 购物车数据持久化到 localStorage
- 支持添加、删除、修改数量

### 7.6 收藏功能
- 商品详情页可添加/取消收藏
- 我的收藏页面展示所有收藏商品
- 支持快速加入购物车

### 7.7 评论功能
- 商品详情页展示评论列表
- 登录用户可发表评论
- 支持评分（1-5星）

## 8. 环境变量

### 后端配置 (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/agri_store
    username: root
    password: root
  
jwt:
  secret: your-secret-key
  expiration: 86400000  # 24小时

cors:
  allowed-origins: http://localhost:5174

upload:
  path: ./uploads
  max-size: 10MB
```

### 前端代理配置 (vite.config.ts)
```typescript
server: {
  port: 5174,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 9. 数据库表结构

### 核心表
| 表名 | 说明 |
|------|------|
| users | 用户表 |
| products | 商品表 |
| orders | 订单表 |
| order_items | 订单项表 |
| stores | 店铺表 |
| categories | 分类表 |
| favorites | 收藏表 |
| comments | 评论表 |
| operation_logs | 操作日志表 |
| stock_logs | 库存日志表 |

详见 [ENTITY_DOCUMENTATION.md](./ENTITY_DOCUMENTATION.md)

## 10. 部署说明

### 后端部署
```bash
cd backend
mvn clean package
java -jar target/agristore-backend-1.0.0.jar
```

### 前端部署
```bash
cd frontend
npm run build
# 将 dist 目录部署到 Nginx 或其他静态服务器
```

## 11. 开发计划
- [x] 基础架构搭建
- [x] 用户认证系统
- [x] 商品管理
- [x] 购物车功能
- [x] 订单管理
- [x] 管理后台
- [x] 收藏功能
- [x] 评论功能
- [x] 分类管理
- [x] 店铺入驻
- [x] 店铺审核
- [x] 商品审核
- [x] 库存预警
- [x] 操作日志
- [ ] 支付集成
- [ ] 数据报表
- [ ] 消息通知
