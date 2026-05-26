# 后端实体类说明

本文档说明所有 JPA 实体类的结构，使用 Lombok `@Data` 注解自动生成 getter/setter。

## 实体类列表

### 1. User.java - 用户实体
```java
package com.agri.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 255)
    private String username;          // 登录名
    
    @Column(nullable = false, length = 255)
    private String password;          // 加密密码 (BCrypt)
    
    @Column(length = 255)
    private String nickname;          // 昵称
    
    @Column(length = 50)
    private String role = "ROLE_USER"; // 角色 (ROLE_USER, ROLE_ADMIN, ROLE_STORE)
    
    @Column(length = 20)
    private String phone;             // 手机号
    
    @Column(length = 100)
    private String email;             // 邮箱
    
    @Column(nullable = false)
    private Boolean active = true;    // 账号状态
    
    @Column(name = "create_time")
    private LocalDateTime createTime; // 创建时间
    
    @Column(name = "update_time")
    private LocalDateTime updateTime; // 更新时间
}
```

**数据库表**: `users`
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| username | VARCHAR(255) | 登录名，唯一 |
| password | VARCHAR(255) | BCrypt加密后的密码 |
| nickname | VARCHAR(255) | 用户昵称 |
| role | VARCHAR(50) | 用户角色 (ROLE_USER/ROLE_ADMIN/ROLE_STORE) |
| phone | VARCHAR(20) | 手机号 |
| email | VARCHAR(100) | 邮箱地址 |
| active | BOOLEAN | 账号是否启用，默认true |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

---

### 2. Product.java - 商品实体
```java
package com.agri.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String name;              // 商品名称
    
    @Column(columnDefinition = "TEXT")
    private String description;       // 商品描述
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;         // 价格
    
    @Column(nullable = false)
    private Integer stock;            // 库存
    
    @Column(name = "stock_warning", nullable = false)
    private Integer stockWarning = 10; // 库存预警值
    
    @Column(length = 500)
    private String imageUrl;          // 主图URL
    
    @Column(length = 2000)
    private String imageUrls;         // 多图，逗号分隔
    
    @Column(nullable = false)
    private Boolean active = true;    // 是否上架
    
    @Column(length = 100)
    private String category;          // 分类名称（兼容字段）
    
    @Column
    private Long categoryId;          // 分类ID关联
    
    @Column(nullable = false)
    private Boolean isNew = false;    // 是否新品
    
    @Column(nullable = false)
    private Boolean isHot = false;    // 是否热销
    
    @Column(nullable = false)
    private Integer sales = 0;        // 销量
    
    // 店家相关字段
    @Column
    private Long storeId;             // 所属店铺ID，null表示平台自营
    
    @Column(nullable = false)
    private Integer status = 1;       // 0-待审核, 1-已通过, 2-已驳回
    
    @Column(length = 500)
    private String rejectReason;      // 驳回原因
    
    @Column(name = "audit_time")
    private LocalDateTime auditTime;  // 审核时间
    
    @Column(name = "audit_by")
    private Long auditBy;             // 审核人ID
    
    @Column(name = "create_time")
    private LocalDateTime createTime; // 创建时间
    
    @Column(name = "update_time")
    private LocalDateTime updateTime; // 更新时间
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
```

**数据库表**: `products`
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| name | VARCHAR(255) | 商品名称 |
| description | TEXT | 商品详情描述 |
| price | DECIMAL(19,2) | 商品价格 |
| stock | INT | 库存数量 |
| stock_warning | INT | 库存预警值，默认10 |
| image_url | VARCHAR(500) | 商品主图URL |
| image_urls | VARCHAR(2000) | 多图URL，逗号分隔 |
| active | BOOLEAN | 是否上架，默认true |
| category | VARCHAR(100) | 商品分类名称 |
| category_id | BIGINT | 分类ID |
| is_new | BOOLEAN | 是否新品，默认false |
| is_hot | BOOLEAN | 是否热销，默认false |
| sales | INT | 销量，默认0 |
| store_id | BIGINT | 所属店铺ID |
| status | INT | 审核状态：0-待审核, 1-已通过, 2-已驳回 |
| reject_reason | VARCHAR(500) | 驳回原因 |
| audit_time | DATETIME | 审核时间 |
| audit_by | BIGINT | 审核人ID |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

---

### 3. Order.java - 订单实体
```java
package com.agri.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String orderNo;           // 订单号
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;                // 下单用户
    
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;              // 用户ID（冗余字段）
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;   // 订单总金额
    
    @Column(nullable = false)
    private Integer status = 1;       // 1: 待发货, 2: 已发货, 3: 已完成
    
    @Column(length = 500)
    private String address;           // 收货地址
    
    @Column(length = 100)
    private String contact;           // 联系人
    
    @Column(length = 20)
    private String phone;             // 联系电话
    
    @Column(length = 100)
    private String trackingNo;        // 物流单号
    
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime = LocalDateTime.now(); // 创建时间
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items;    // 订单商品列表
}
```

**数据库表**: `orders`
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| order_no | VARCHAR(100) | 订单号，唯一 |
| user_id | BIGINT | 用户ID，外键 |
| total_amount | DECIMAL(19,2) | 订单总金额 |
| status | INT | 订单状态：1-待发货, 2-已发货, 3-已完成 |
| address | VARCHAR(500) | 收货地址 |
| contact | VARCHAR(100) | 联系人姓名 |
| phone | VARCHAR(20) | 联系电话 |
| tracking_no | VARCHAR(100) | 物流追踪号 |
| create_time | DATETIME | 订单创建时间 |

**订单状态说明**:
- `1`: 待发货 - 订单已支付，等待发货
- `2`: 已发货 - 商家已发货，设置物流单号
- `3`: 已完成 - 用户确认收货或系统自动完成

**订单状态流转**:
```
待发货 (1) -> 已发货 (2) -> 已完成 (3)
```

---

### 4. OrderItem.java - 订单项实体
```java
package com.agri.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;           // 商品ID
    
    @Column(name = "store_id")
    private Long storeId;             // 店铺ID
    
    @Column(length = 255)
    private String productName;       // 商品名称（快照）
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;         // 单价（快照）
    
    @Column(nullable = false)
    private Integer quantity;         // 购买数量
    
    @Column(length = 500)
    private String imageUrl;          // 商品图片URL（快照）
}
```

**数据库表**: `order_items`
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| order_id | BIGINT | 所属订单ID，外键 |
| product_id | BIGINT | 商品ID |
| store_id | BIGINT | 店铺ID |
| product_name | VARCHAR(255) | 商品名称（下单时快照） |
| price | DECIMAL(19,2) | 单价（下单时快照） |
| quantity | INT | 购买数量 |
| image_url | VARCHAR(500) | 商品图片URL（下单时快照） |

---

### 5. Store.java - 店铺实体
```java
package com.agri.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Long userId;              // 店主用户ID
    
    @Column(nullable = false, length = 100)
    private String storeName;         // 店铺名称
    
    @Column(length = 500)
    private String description;       // 店铺描述
    
    @Column(length = 20)
    private String phone;             // 联系电话
    
    @Column(length = 200)
    private String address;           // 店铺地址
    
    @Column(length = 255)
    private String logoUrl;           // 店铺Logo URL
    
    @Column(nullable = false)
    private Integer status = 0;       // 0-待审核, 1-已通过, 2-已驳回, 3-已禁用
    
    @Column(length = 500)
    private String rejectReason;      // 驳回原因
    
    @Column(name = "create_time")
    private LocalDateTime createTime; // 创建时间
    
    @Column(name = "update_time")
    private LocalDateTime updateTime; // 更新时间
    
    @Column(name = "audit_time")
    private LocalDateTime auditTime;  // 审核时间
    
    @Column(name = "audit_by")
    private Long auditBy;             // 审核人ID
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
```

**数据库表**: `stores`
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 店主用户ID，唯一 |
| store_name | VARCHAR(100) | 店铺名称 |
| description | VARCHAR(500) | 店铺描述 |
| phone | VARCHAR(20) | 联系电话 |
| address | VARCHAR(200) | 店铺地址 |
| logo_url | VARCHAR(255) | 店铺Logo URL |
| status | INT | 状态：0-待审核, 1-已通过, 2-已驳回, 3-已禁用 |
| reject_reason | VARCHAR(500) | 驳回原因 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| audit_time | DATETIME | 审核时间 |
| audit_by | BIGINT | 审核人ID |

---

### 6. Category.java - 分类实体
```java
package com.agri.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;              // 分类名称
    
    @Column(length = 500)
    private String description;       // 分类描述
    
    @Column
    private Long parentId = 0L;       // 父分类ID，0表示顶级分类
    
    @Column(nullable = false)
    private Integer level = 1;        // 分类层级
    
    @Column(nullable = false)
    private Integer sortOrder = 0;    // 排序
    
    @Column(length = 255)
    private String iconUrl;           // 分类图标URL
    
    @Column(nullable = false)
    private Boolean active = true;    // 是否启用
    
    @Column(name = "create_time")
    private LocalDateTime createTime; // 创建时间
    
    @Column(name = "update_time")
    private LocalDateTime updateTime; // 更新时间
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
```

**数据库表**: `categories`
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| name | VARCHAR(100) | 分类名称 |
| description | VARCHAR(500) | 分类描述 |
| parent_id | BIGINT | 父分类ID，0表示顶级 |
| level | INT | 分类层级，默认1 |
| sort_order | INT | 排序，默认0 |
| icon_url | VARCHAR(255) | 分类图标URL |
| active | BOOLEAN | 是否启用，默认true |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

---

### 7. Favorite.java - 收藏实体
```java
package com.agri.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "favorites")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long productId;           // 商品ID
    
    @Column(nullable = false)
    private Long userId;              // 用户ID
    
    @Column(name = "create_time")
    private LocalDateTime createTime; // 创建时间
    
    @Column(name = "update_time")
    private LocalDateTime updateTime; // 更新时间
}
```

**数据库表**: `favorites`
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| product_id | BIGINT | 商品ID |
| user_id | BIGINT | 用户ID |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

---

### 8. Comment.java - 评论实体
```java
package com.agri.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long productId;           // 商品ID
    
    @Column(nullable = false)
    private Long userId;              // 用户ID
    
    @Column(nullable = false, length = 500)
    private String content;           // 评论内容
    
    @Column(nullable = false)
    private Integer rating;           // 评分 (1-5)
    
    @Column(nullable = false)
    private Integer likes = 0;        // 点赞数
    
    @Column(nullable = false, length = 255)
    private String nickname;          // 用户昵称（快照）
    
    @Column(name = "create_time")
    private LocalDateTime createTime; // 创建时间
    
    @Column(name = "update_time")
    private LocalDateTime updateTime; // 更新时间
}
```

**数据库表**: `comments`
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| product_id | BIGINT | 商品ID |
| user_id | BIGINT | 用户ID |
| content | VARCHAR(500) | 评论内容 |
| rating | INT | 评分 (1-5星) |
| likes | INT | 点赞数，默认0 |
| nickname | VARCHAR(255) | 用户昵称 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

---

### 9. OperationLog.java - 操作日志实体
```java
package com.agri.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "operation_logs")
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;              // 操作用户ID
    
    @Column(nullable = false, length = 50)
    private String username;          // 操作用户名
    
    @Column(nullable = false, length = 100)
    private String module;            // 操作模块
    
    @Column(nullable = false, length = 100)
    private String action;            // 操作类型
    
    @Column(length = 500)
    private String description;       // 操作描述
    
    @Column(length = 2000)
    private String requestData;       // 请求数据
    
    @Column(length = 2000)
    private String responseData;      // 响应数据
    
    @Column(length = 50)
    private String ipAddress;         // IP地址
    
    @Column(length = 500)
    private String userAgent;         // 浏览器信息
    
    @Column(nullable = false)
    private Boolean success = true;   // 是否成功
    
    @Column(length = 500)
    private String errorMessage;      // 错误信息
    
    @Column(name = "create_time")
    private LocalDateTime createTime; // 创建时间
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
```

**数据库表**: `operation_logs`
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 操作用户ID |
| username | VARCHAR(50) | 操作用户名 |
| module | VARCHAR(100) | 操作模块 |
| action | VARCHAR(100) | 操作类型 |
| description | VARCHAR(500) | 操作描述 |
| request_data | VARCHAR(2000) | 请求数据 |
| response_data | VARCHAR(2000) | 响应数据 |
| ip_address | VARCHAR(50) | IP地址 |
| user_agent | VARCHAR(500) | 浏览器信息 |
| success | BOOLEAN | 是否成功，默认true |
| error_message | VARCHAR(500) | 错误信息 |
| create_time | DATETIME | 创建时间 |

---

### 10. StockLog.java - 库存日志实体
```java
package com.agri.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stock_logs")
public class StockLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long productId;           // 商品ID
    
    @Column(nullable = false)
    private Long storeId;             // 店铺ID
    
    @Column(nullable = false)
    private Integer changeAmount;     // 变更数量（正数增加，负数减少）
    
    @Column(nullable = false)
    private Integer beforeStock;      // 变更前库存
    
    @Column(nullable = false)
    private Integer afterStock;       // 变更后库存
    
    @Column(length = 50)
    private String type;              // 类型：SALE-销售, RESTOCK-补货, ADJUST-调整, RETURN-退货
    
    @Column(length = 500)
    private String remark;            // 备注
    
    @Column(nullable = false)
    private Long operatorId;          // 操作人ID
    
    @Column(nullable = false)
    private String operatorName;      // 操作人名称
    
    @Column(name = "create_time")
    private LocalDateTime createTime; // 创建时间
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
```

**数据库表**: `stock_logs`
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| product_id | BIGINT | 商品ID |
| store_id | BIGINT | 店铺ID |
| change_amount | INT | 变更数量（正数增加，负数减少） |
| before_stock | INT | 变更前库存 |
| after_stock | INT | 变更后库存 |
| type | VARCHAR(50) | 类型：SALE/RESTOCK/ADJUST/RETURN |
| remark | VARCHAR(500) | 备注 |
| operator_id | BIGINT | 操作人ID |
| operator_name | VARCHAR(255) | 操作人名称 |
| create_time | DATETIME | 创建时间 |

---

## 数据库关系图

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    users    │       │    orders   │       │ order_items │
├─────────────┤       ├─────────────┤       ├─────────────┤
│     id      │──┐    │     id      │──┐    │     id      │
│   username  │  │    │   order_no  │  └───>│  order_id   │
│   password  │  └───>│   user_id   │       │  product_id │
│   nickname  │       │ total_amount│       │ product_name│
│    role     │       │    status   │       │    price    │
│    phone    │       │   address   │       │  quantity   │
│    email    │       │   contact   │       │  image_url  │
│   active    │       │    phone    │       └─────────────┘
│ create_time │       │  tracking_no│
│ update_time │       │ create_time │
└─────────────┘       └─────────────┘
                             │
                             │ 1:N
                             ▼
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   stores    │       │   products  │       │  categories │
├─────────────┤       ├─────────────┤       ├─────────────┤
│     id      │       │     id      │       │     id      │
│   user_id   │       │    name     │       │    name     │
│  store_name │       │ description │       │ description │
│ description │       │    price    │       │  parent_id  │
│    phone    │       │    stock    │       │    level    │
│   address   │       │  image_url  │       │  sort_order │
│   logo_url  │       │   active    │       │  icon_url   │
│   status    │       │  category   │       │   active    │
└─────────────┘       │   store_id  │       └─────────────┘
                      │   status    │
                      └─────────────┘

┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│  favorites  │       │   comments  │       │ stock_logs  │
├─────────────┤       ├─────────────┤       ├─────────────┤
│     id      │       │     id      │       │     id      │
│  product_id │       │  product_id │       │  product_id │
│   user_id   │       │   user_id   │       │  store_id   │
│ create_time │       │   content   │       │change_amount│
│ update_time │       │   rating    │       │before_stock │
└─────────────┘       │   likes     │       │after_stock  │
                      │  nickname   │       │    type     │
                      │ create_time │       │   remark    │
                      └─────────────┘       │ operator_id │
                                            │operator_name│
                                            │ create_time │
                                            └─────────────┘
```

## 关系说明

1. **User (1) ----< (N) Order**: 一个用户可以有多个订单
2. **Order (1) ----< (N) OrderItem**: 一个订单包含多个订单项
3. **OrderItem (N) >---- (1) Product**: 订单项关联商品（逻辑关联）
4. **User (1) ----< (1) Store**: 一个用户可以拥有一个店铺
5. **Store (1) ----< (N) Product**: 一个店铺可以有多个商品
6. **Category (1) ----< (N) Product**: 一个分类可以有多个商品
7. **User (N) ----< (N) Product (via Favorite)**: 多对多收藏关系
8. **User (N) ----< (N) Product (via Comment)**: 多对多评论关系

## 库存管理流程

### 库存变更类型
- `SALE`: 销售出库
- `RESTOCK`: 补货入库
- `ADJUST`: 库存调整
- `RETURN`: 退货入库

### 库存安全
- 下单时检查库存，防止超卖
- 使用数据库事务保证原子性
- 库存日志记录所有变更

## Lombok 说明

所有实体类都使用了 `@Data` 注解，Lombok 会自动生成：
- 所有字段的 getter 方法
- 所有非 final 字段的 setter 方法
- `toString()` 方法
- `equals()` 和 `hashCode()` 方法

**注意**: 在 IDEA 中需要安装 Lombok 插件并启用注解处理器。

## JPA 注解说明

- `@Entity`: 标识这是一个 JPA 实体类
- `@Table(name = "xxx")`: 指定对应的数据库表名
- `@Id`: 标识主键字段
- `@GeneratedValue`: 指定主键生成策略
- `@Column`: 指定字段映射属性
- `@ManyToOne`: 多对一关系
- `@OneToMany`: 一对多关系
- `@JoinColumn`: 指定关联字段
- `@PrePersist`: 插入前回调
- `@PreUpdate`: 更新前回调
- `@JsonIgnore`: JSON序列化时忽略该字段
