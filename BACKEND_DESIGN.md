# 后端详细设计说明书

## 1. 项目结构
```
backend/src/main/java/com/agri/store/
├── config/
│   ├── SecurityConfig.java           # Spring Security 配置
│   └── GlobalExceptionHandler.java   # 全局异常处理
├── controller/
│   ├── AuthController.java           # 认证接口
│   ├── UserController.java           # 用户接口
│   ├── ProductController.java        # 商品接口
│   ├── OrderController.java          # 订单接口
│   ├── CategoryController.java       # 分类接口
│   ├── FavoriteController.java       # 收藏接口
│   ├── CommentController.java        # 评论接口
│   ├── StoreController.java          # 店铺接口
│   ├── StoreProductController.java   # 店铺商品接口
│   ├── StoreOrderController.java     # 店铺订单接口
│   ├── AdminController.java          # 管理后台接口
│   └── HealthController.java         # 健康检查接口
├── dto/
│   ├── LoginRequest.java             # 登录请求DTO
│   ├── RegisterRequest.java          # 注册请求DTO
│   ├── JwtAuthenticationResponse.java # JWT响应DTO
│   ├── ProductDTO.java               # 商品DTO（含图片双保底策略）
│   ├── OrderDTO.java                 # 订单DTO
│   ├── OrderResponse.java            # 订单响应DTO
│   ├── StoreRegisterRequest.java     # 店铺注册请求DTO
│   ├── StoreProductRequest.java      # 店铺商品请求DTO
│   ├── ShipRequest.java              # 发货请求DTO
│   └── AuditRequest.java             # 审核请求DTO
├── util/
│   └── ImageUrlUtil.java             # 图片路径工具类（双保底策略）
├── entity/
│   ├── User.java                     # 用户实体
│   ├── Product.java                  # 商品实体
│   ├── Order.java                    # 订单实体
│   ├── OrderItem.java                # 订单项实体
│   ├── Store.java                    # 店铺实体
│   ├── Category.java                 # 分类实体
│   ├── Favorite.java                 # 收藏实体
│   ├── Comment.java                  # 评论实体
│   ├── OperationLog.java             # 操作日志实体
│   └── StockLog.java                 # 库存日志实体
├── repository/
│   ├── UserRepository.java           # 用户仓库
│   ├── ProductRepository.java        # 商品仓库
│   ├── OrderRepository.java          # 订单仓库
│   ├── StoreRepository.java          # 店铺仓库
│   ├── CategoryRepository.java       # 分类仓库
│   ├── FavoriteRepository.java       # 收藏仓库
│   ├── CommentRepository.java        # 评论仓库
│   ├── OperationLogRepository.java   # 操作日志仓库
│   └── StockLogRepository.java       # 库存日志仓库
├── security/
│   ├── JwtTokenProvider.java         # JWT令牌提供者
│   ├── JwtAuthenticationFilter.java  # JWT认证过滤器
│   └── CustomUserDetailsService.java # 用户详情服务
└── service/
    └── OrderService.java             # 订单业务服务
```

## 2. 数据库建模 (Entity)

详见 [ENTITY_DOCUMENTATION.md](./ENTITY_DOCUMENTATION.md)

## 3. 核心逻辑实现

### 3.1 鉴权流程
1. **登录**: `AuthController.authenticateUser()`
   - 验证用户名密码
   - 生成 JWT Token
   - 返回 Token + 用户信息

2. **JWT 配置**: `JwtTokenProvider`
   - 生成 Token: `generateToken(Authentication)`
   - 验证 Token: `validateToken(String)`
   - 解析用户名: `getUsernameFromJWT(String)`

3. **过滤器**: `JwtAuthenticationFilter`
   - 拦截所有请求
   - 从 Header 提取 Token
   - 验证并设置 SecurityContext

4. **安全配置**: `SecurityConfig`
   - 放行 `/api/auth/**`
   - 放行 GET `/api/products/**`, `/api/category/**`, `/api/comments/**`
   - 保护 `/api/admin/**` (需要 ROLE_ADMIN)
   - 保护 `/api/store/**` (需要 ROLE_STORE 或 ROLE_ADMIN)

### 3.2 订单状态流转
```
待发货 (1) -> 已发货 (2) -> 已完成 (3)
```

**状态说明**:
- **1 - 待发货**: 订单已创建，等待商家发货
- **2 - 已发货**: 商家已发货，设置物流单号
- **3 - 已完成**: 用户确认收货或系统自动完成

**状态转换**:
- **创建订单**: 状态 = 1 (待发货)
- **商家发货**: `OrderService.shipOrder(orderId, trackingNo)` - 状态 1 -> 2
- **确认收货**: 状态 2 -> 3

### 3.3 店铺审核流程
```
待审核 (0) -> 已通过 (1) / 已驳回 (2) / 已禁用 (3)
```

**状态说明**:
- **0 - 待审核**: 店铺申请提交，等待管理员审核
- **1 - 已通过**: 审核通过，店铺可以正常运营
- **2 - 已驳回**: 审核未通过，可查看驳回原因后重新申请
- **3 - 已禁用**: 店铺被禁用，无法继续运营

### 3.4 商品审核流程
```
待审核 (0) -> 已通过 (1) / 已驳回 (2)
```

**状态说明**:
- **0 - 待审核**: 商品提交，等待管理员审核
- **1 - 已通过**: 审核通过，商品可以上架销售
- **2 - 已驳回**: 审核未通过，可查看驳回原因后修改

## 4. API 接口设计

### 4.1 认证接口
| 方法 | 路径 | 描述 | 参数 |
|------|------|------|------|
| POST | /api/auth/login | 登录 | username, password |
| POST | /api/auth/register | 注册 | username, password, nickname |

**响应示例**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "user123",
  "role": "ROLE_USER"
}
```

### 4.2 用户接口
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/user/profile | 获取用户信息 | 用户 |
| PUT | /api/user/profile | 更新用户信息 | 用户 |
| PUT | /api/user/password | 修改密码 | 用户 |

### 4.3 商品接口
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/products | 获取所有商品 | 公开 |
| GET | /api/products/{id} | 获取商品详情 | 公开 |
| GET | /api/products/new | 获取新品商品 | 公开 |
| GET | /api/products/hot | 获取热销商品 | 公开 |
| GET | /api/products/search | 搜索商品（分页） | 公开 |
| POST | /api/products | 创建商品 | Admin |
| PUT | /api/products/{id} | 更新商品 | Admin |
| DELETE | /api/products/{id} | 删除商品 | Admin |
| PATCH | /api/products/{id}/active | 切换上下架 | Admin |

**搜索商品接口**:
```
GET /api/products/search?keyword=苹果&page=0&size=12
```

**响应示例**:
```json
{
  "content": [
    {
      "id": 1,
      "name": "红富士苹果",
      "description": "新鲜红富士苹果",
      "price": 9.99,
      "stock": 100,
      "imageUrl": "http://example.com/apple.jpg",
      "category": "fruits",
      "active": true,
      "isNew": false,
      "isHot": true,
      "sales": 50
    }
  ],
  "totalElements": 25,
  "totalPages": 3,
  "size": 12,
  "number": 0,
  "first": true,
  "last": false,
  "empty": false
}
```

### 4.4 分类接口
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/category/list | 获取所有分类 | 公开 |
| GET | /api/category/tree | 获取分类树 | 公开 |
| GET | /api/category/children/{parentId} | 获取子分类 | 公开 |
| POST | /api/category/create | 创建分类 | Admin |
| PUT | /api/category/update/{id} | 更新分类 | Admin |
| DELETE | /api/category/delete/{id} | 删除分类 | Admin |

### 4.5 收藏接口
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/favorites/user/{userId} | 获取用户收藏 | 用户 |
| GET | /api/favorites/check/{userId}/{productId} | 检查是否收藏 | 用户 |
| POST | /api/favorites | 添加收藏 | 用户 |
| DELETE | /api/favorites/{userId}/{productId} | 取消收藏 | 用户 |

### 4.6 评论接口
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/comments/product/{productId} | 获取商品评论 | 公开 |
| GET | /api/comments/user/{userId} | 获取用户评论 | 用户 |
| POST | /api/comments | 创建评论 | 用户 |
| PUT | /api/comments/{id} | 更新评论 | 用户 |
| DELETE | /api/comments/{id} | 删除评论 | 用户 |

### 4.7 订单接口
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/orders | 获取我的订单 | 用户 |
| POST | /api/orders | 创建订单 | 用户 |
| GET | /api/orders/{id} | 获取订单详情 | 用户 |
| POST | /api/orders/{id}/cancel | 取消订单 | 用户 |
| POST | /api/orders/{id}/confirm | 确认收货 | 用户 |
| GET | /api/orders/tracking/{orderNo} | 追踪订单 | 公开 |

**创建订单请求示例**:
```json
{
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ],
  "address": "北京市朝阳区建国路88号",
  "contact": "张三",
  "phone": "13800138000"
}
```

### 4.8 店铺接口
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /api/store/register | 注册店铺 | 用户 |
| GET | /api/store/my | 获取我的店铺 | 店铺/管理员 |
| PUT | /api/store/my | 更新店铺信息 | 店铺/管理员 |

### 4.9 店铺商品接口
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/store/product/list | 获取店铺商品列表 | 店铺/管理员 |
| POST | /api/store/product/create | 创建商品 | 店铺/管理员 |
| PUT | /api/store/product/{id} | 更新商品 | 店铺/管理员 |
| DELETE | /api/store/product/{id} | 删除商品 | 店铺/管理员 |
| PUT | /api/store/product/{id}/active | 切换上下架 | 店铺/管理员 |
| GET | /api/store/product/stock-logs/{productId} | 获取库存日志 | 店铺/管理员 |
| GET | /api/store/product/warning | 获取库存预警 | 店铺/管理员 |
| POST | /api/store/product/upload | 上传图片 | 店铺/管理员 |

### 4.10 店铺订单接口
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/store/order/list | 获取店铺订单 | 店铺/管理员 |
| POST | /api/store/order/{id}/ship | 订单发货 | 店铺/管理员 |

### 4.11 管理后台接口

#### 4.11.1 统计与报表
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/admin/stats | 统计数据 | Admin |
| GET | /api/admin/reports/sales | 销售报表 | Admin |
| GET | /api/admin/reports/categories | 分类统计 | Admin |

#### 4.11.2 用户管理
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/admin/users | 获取所有用户 | Admin |
| POST | /api/admin/users | 创建用户 | Admin |
| PUT | /api/admin/users/{id} | 更新用户信息 | Admin |
| DELETE | /api/admin/users/{id} | 删除用户 | Admin |
| PATCH | /api/admin/users/{id}/status | 启用/禁用用户 | Admin |
| POST | /api/admin/users/{id}/reset-password | 重置密码 | Admin |

#### 4.11.3 订单管理
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/admin/orders | 所有订单 | Admin |
| POST | /api/admin/orders/{id}/ship | 订单发货 | Admin |
| GET | /api/admin/orders/{id} | 订单详情 | Admin |
| PATCH | /api/admin/orders/{id}/status | 更新订单状态 | Admin |

#### 4.11.4 店铺审核
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/admin/stores | 获取店铺列表 | Admin |
| PUT | /api/admin/store/{id}/audit | 审核店铺 | Admin |

#### 4.11.5 商品审核
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/admin/products | 获取商品列表 | Admin |
| PUT | /api/admin/product/{id}/audit | 审核商品 | Admin |
| POST | /api/admin/product | 创建商品 | Admin |
| PUT | /api/admin/product/{id} | 更新商品 | Admin |
| DELETE | /api/admin/product/{id} | 删除商品 | Admin |
| PATCH | /api/admin/product/{id}/active | 切换上下架 | Admin |

#### 4.11.6 操作日志
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/admin/logs | 获取操作日志 | Admin |

**操作日志响应示例**:
```json
[
  {
    "id": 1,
    "userId": 1,
    "username": "admin",
    "module": "USER",
    "action": "CREATE",
    "description": "创建用户: newuser",
    "success": true,
    "createTime": "2024-01-15T10:30:00"
  }
]
```

### 4.12 商品图片双保底策略

#### 4.12.1 设计目标
- **兼容性**: 保持现有 `imageUrl` 和 `imageUrls` 字段不变，确保历史数据可用
- **优先级**: 优先使用 `imageUrls`（相对路径），失败时降级到 `imageUrl`（绝对路径）
- **透明性**: 前端无需关心底层存储方式，统一使用处理后的图片URL

#### 4.12.2 响应字段说明
商品API响应新增以下字段：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `imageUrl` | string | 原始绝对路径（保留兼容） |
| `imageUrls` | string | 原始相对路径，逗号分隔（保留兼容） |
| `primaryImageUrl` | string | 主图URL（双保底策略处理后的URL） |
| `primaryImageType` | string | 主图类型：`relative`/`absolute`/`fallback`/`none` |
| `fallbackImageUrl` | string | 降级URL（绝对路径） |
| `allImageUrls` | string[] | 所有图片URL列表（已处理） |
| `imagePathType` | string | 当前使用的路径类型代码 |
| `imagePathTypeDesc` | string | 当前使用的路径类型描述 |

#### 4.12.3 优先级逻辑
```java
1. 优先使用 imageUrls（相对路径）
   - 解析逗号分隔的字符串
   - 确保相对路径以 / 开头
   - type = "relative"

2. 降级使用 imageUrl（绝对路径）
   - 当 imageUrls 为空或无效时
   - type = "absolute"

3. 无图片
   - 当两者都为空时
   - type = "none"
```

#### 4.12.4 使用示例
**商品响应示例**:
```json
{
  "id": 1,
  "name": "红富士苹果",
  "price": 9.99,
  "imageUrl": "http://example.com/uploads/apple.jpg",
  "imageUrls": "/uploads/apple.jpg,/uploads/apple2.jpg",
  "primaryImageUrl": "/uploads/apple.jpg",
  "primaryImageType": "relative",
  "fallbackImageUrl": "http://example.com/uploads/apple.jpg",
  "allImageUrls": ["/uploads/apple.jpg", "/uploads/apple2.jpg"],
  "imagePathType": "relative",
  "imagePathTypeDesc": "相对路径"
}
```

#### 4.12.5 工具类使用
```java
// 获取主图（优先相对路径）
ImagePathResult result = ImageUrlUtil.getPrimaryImage(product);
String url = result.getUrl();           // 图片URL
ImagePathType type = result.getType();  // 路径类型

// 获取所有图片
List<ImagePathResult> allImages = ImageUrlUtil.getAllImages(product);

// 获取降级URL（用于前端加载失败）
String fallbackUrl = ImageUrlUtil.getFallbackUrl(product);

// 构建完整响应信息
ImagePathInfo info = ImageUrlUtil.buildImagePathInfo(product);
```

## 5. 配置说明

### 5.1 application.yml
```yaml
server:
  port: 8080

spring:
  application:
    name: agri-store-backend

  datasource:
    url: jdbc:mysql://localhost:3306/agri_store?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
    open-in-view: false

jwt:
  secret: agristore-secret-key-2024-spring-boot-jwt
  expiration: 86400000  # 24 hours in milliseconds

cors:
  allowed-origins: http://localhost:5174

# 文件上传配置
upload:
  path: ./uploads
  max-size: 10MB
```

### 5.2 CORS 配置
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5174"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    return source;
}
```

## 6. 依赖列表

### 6.1 Spring Boot Starters
- `spring-boot-starter-web`: Web 支持
- `spring-boot-starter-data-jpa`: JPA 支持
- `spring-boot-starter-security`: 安全认证
- `spring-boot-starter-validation`: 参数校验

### 6.2 其他依赖
- `mysql-connector-j`: MySQL 驱动
- `jjwt-api/impl/jackson`: JWT 支持 (0.11.5)
- `lombok`: 代码简化

## 7. 数据库表关系

```
users ||--o{ orders : "1:N"
users ||--o| stores : "1:1"
orders ||--o{ order_items : "1:N"
products ||--o{ order_items : "1:N (逻辑)"
stores ||--o{ products : "1:N"
categories ||--o{ products : "1:N"
users ||--o{ favorites : "1:N"
users ||--o{ comments : "1:N"
products ||--o{ favorites : "1:N"
products ||--o{ comments : "1:N"
stores ||--o{ stock_logs : "1:N"
products ||--o{ stock_logs : "1:N"
```

## 8. 安全设计

### 8.1 密码加密
- 使用 BCryptPasswordEncoder
- 单向哈希，不可解密

### 8.2 JWT 设计
- 签名算法: HS256
- 有效期: 24小时
- 载荷包含: userId, username, role

### 8.3 权限控制
- `@PreAuthorize("hasRole('ADMIN')")` 方法级权限
- `@PreAuthorize("hasRole('STORE')")` 店铺权限
- 配置类 URL 权限控制
- 角色前缀: ROLE_

## 9. 性能优化

### 9.1 数据库优化
- 字段索引: username (唯一), order_no (唯一), user_id, store_id
- 懒加载: `@ManyToOne(fetch = FetchType.LAZY)`
- 分页: 使用 Spring Data Pageable

### 9.2 事务管理
- `@Transactional` 保证订单创建原子性
- 库存扣减与订单处理在同一事务

### 9.3 DTO 模式
- 使用 DTO 进行数据传输，避免直接暴露 Entity
- 解决 Hibernate 懒加载问题
- 灵活控制返回字段

## 10. 异常处理

### 10.1 全局异常处理 (GlobalExceptionHandler)

使用 `@RestControllerAdvice` 实现全局异常处理，统一返回标准错误格式：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.UNAUTHORIZED.value());
        error.put("error", "Unauthorized");
        error.put("message", "用户名或密码错误");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.FORBIDDEN.value());
        error.put("error", "Forbidden");
        error.put("message", "没有权限执行此操作");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
```

**错误响应格式**:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "具体错误信息"
}
```

### 10.2 常见异常
| 异常类型 | HTTP 状态码 | 错误信息 | 场景 |
|----------|-------------|----------|------|
| `RuntimeException` | 400 | 业务错误信息 | 库存不足、订单状态错误等 |
| `BadCredentialsException` | 401 | 用户名或密码错误 | 登录失败 |
| `AuthenticationException` | 401 | 认证失败信息 | Token 无效或过期 |
| `AccessDeniedException` | 403 | 没有权限执行此操作 | 权限不足 |
| `Exception` | 500 | 服务器内部错误 | 系统异常 |

## 11. 文件上传

### 11.1 图片上传接口
- **端点**: `POST /api/store/product/upload`
- **Content-Type**: `multipart/form-data`
- **参数**: `file` (文件)
- **响应**: 图片访问 URL

### 11.2 上传配置
- 存储路径: `./uploads`
- 最大文件大小: 10MB
- 支持格式: jpg, jpeg, png, gif
- 文件名: UUID 生成，避免冲突
