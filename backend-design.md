# 高校社团资金管控平台 - 后端设计文档

## 一、技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.x | 后端框架 |
| Spring Security | 6.x | 安全框架 |
| Hibernate | 6.x | ORM框架 |
| MySQL | 8.x | 关系型数据库 |
| JWT | latest | 令牌认证 |
| Lombok | latest | 代码简化 |
| Maven | 3.x | 项目构建 |

## 二、项目结构

```
backend/
├── src/main/java/com/club/fund/
│   ├── ClubFundApplication.java      # 启动类
│   ├── config/                       # 配置类
│   │   ├── SecurityConfig.java       # 安全配置
│   │   ├── JwtConfig.java            # JWT配置
│   │   ├── CorsConfig.java           # 跨域配置
│   │   ├── WebMvcConfig.java         # Web配置
│   │   └── UploadConfig.java         # 文件上传配置
│   ├── controller/                   # 控制器层
│   │   ├── AuthController.java       # 认证控制器
│   │   ├── UserController.java       # 用户控制器
│   │   ├── ClubController.java       # 社团控制器
│   │   ├── FundController.java       # 资金控制器
│   │   ├── ApprovalController.java   # 审批控制器
│   │   ├── ActivityController.java   # 活动控制器
│   │   ├── NotificationController.java # 通知控制器
│   │   └── UploadController.java     # 文件上传控制器
│   ├── service/                      # 服务层
│   │   ├── AuthService.java          # 认证服务
│   │   ├── UserService.java          # 用户服务
│   │   ├── ClubService.java          # 社团服务
│   │   ├── FundService.java          # 资金服务
│   │   ├── ApprovalService.java      # 审批服务
│   │   ├── ActivityService.java      # 活动服务
│   │   ├── NotificationService.java  # 通知服务
│   │   └── FileService.java          # 文件服务
│   ├── repository/                   # 数据访问层
│   │   ├── UserRepository.java       # 用户仓储
│   │   ├── ClubRepository.java       # 社团仓储
│   │   ├── FundRepository.java       # 资金仓储
│   │   ├── ApprovalRepository.java   # 审批仓储
│   │   ├── ActivityRepository.java   # 活动仓储
│   │   └── NotificationRepository.java # 通知仓储
│   ├── entity/                       # 实体类
│   │   ├── User.java                 # 用户实体
│   │   ├── Role.java                 # 角色实体
│   │   ├── Club.java                 # 社团实体
│   │   ├── ClubMember.java           # 社团成员实体
│   │   ├── Fund.java                 # 资金实体
│   │   ├── FundFlow.java             # 资金流水实体
│   │   ├── Approval.java             # 审批实体
│   │   ├── ApprovalRecord.java       # 审批记录实体
│   │   ├── Activity.java             # 活动实体
│   │   └── Notification.java         # 通知实体
│   ├── dto/                          # 数据传输对象
│   │   ├── request/                  # 请求DTO
│   │   │   ├── LoginRequest.java
│   │   │   ├── UserCreateRequest.java
│   │   │   ├── ClubCreateRequest.java
│   │   │   ├── FundApplyRequest.java
│   │   │   └── ApprovalRequest.java
│   │   └── response/                 # 响应DTO
│   │       ├── UserResponse.java
│   │       ├── ClubResponse.java
│   │       ├── FundResponse.java
│   │       └── ApprovalResponse.java
│   ├── vo/                           # 视图对象
│   │   ├── FundFlowVO.java           # 资金流水VO
│   │   ├── ApprovalDetailVO.java     # 审批详情VO
│   │   └── StatisticsVO.java         # 统计数据VO
│   ├── common/                       # 公共模块
│   │   ├── Result.java               # 统一响应结果
│   │   ├── PageResult.java           # 分页结果
│   │   ├── ErrorCode.java            # 错误码枚举
│   │   └── Constants.java            # 常量定义
│   ├── exception/                    # 异常处理
│   │   ├── BusinessException.java    # 业务异常
│   │   ├── AuthException.java        # 认证异常
│   │   └── GlobalExceptionHandler.java # 全局异常处理
│   ├── util/                         # 工具类
│   │   ├── JwtUtil.java              # JWT工具
│   │   ├── SecurityUtil.java         # 安全工具
│   │   ├── DateUtil.java             # 日期工具
│   │   └── FileUtil.java             # 文件工具
│   ├── filter/                       # 过滤器
│   │   └── JwtAuthenticationFilter.java # JWT认证过滤器
│   └── aspect/                       # 切面
│       ├── LogAspect.java            # 日志切面
│       └── PermissionAspect.java     # 权限切面
├── src/main/resources/
│   ├── application.yml               # 主配置文件
│   ├── application-dev.yml           # 开发环境配置
│   ├── application-prod.yml          # 生产环境配置
│   └── logback-spring.xml            # 日志配置
├── upload/                           # 文件上传目录
│   ├── avatar/                       # 头像目录
│   ├── voucher/                      # 凭证目录
│   └── document/                     # 文档目录
├── pom.xml                           # Maven配置
└── README.md                         # 项目说明
```

## 三、数据库设计

### 3.1 用户表 (sys_user)

| 字段名 | 类型 | 长度 | 约束 | 说明 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 主键ID |
| username | VARCHAR | 50 | UNIQUE, NOT NULL | 用户名 |
| password | VARCHAR | 255 | NOT NULL | 密码(加密) |
| real_name | VARCHAR | 50 | NOT NULL | 真实姓名 |
| student_id | VARCHAR | 20 | UNIQUE | 学号/工号 |
| phone | VARCHAR | 20 | - | 手机号 |
| email | VARCHAR | 100 | - | 邮箱 |
| avatar | VARCHAR | 255 | - | 头像路径 |
| role_id | BIGINT | - | FK | 角色ID |
| status | TINYINT | - | DEFAULT 1 | 状态(0禁用/1启用) |
| create_time | DATETIME | - | DEFAULT NOW() | 创建时间 |
| update_time | DATETIME | - | ON UPDATE NOW() | 更新时间 |
| deleted | TINYINT | - | DEFAULT 0 | 删除标记 |

### 3.2 角色表 (sys_role)

| 字段名 | 类型 | 长度 | 约束 | 说明 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 主键ID |
| role_name | VARCHAR | 50 | UNIQUE, NOT NULL | 角色名称 |
| role_code | VARCHAR | 50 | UNIQUE, NOT NULL | 角色编码 |
| description | VARCHAR | 255 | - | 角色描述 |
| permissions | TEXT | - | - | 权限列表(JSON) |
| create_time | DATETIME | - | DEFAULT NOW() | 创建时间 |
| update_time | DATETIME | - | ON UPDATE NOW() | 更新时间 |

**预置角色数据:**
- member: 社团成员
- president: 社长
- teacher: 指导老师
- admin: 校级管理员

### 3.3 社团表 (club)

| 字段名 | 类型 | 长度 | 约束 | 说明 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 主键ID |
| club_name | VARCHAR | 100 | NOT NULL | 社团名称 |
| club_code | VARCHAR | 20 | UNIQUE, NOT NULL | 社团编码 |
| description | TEXT | - | - | 社团简介 |
| logo | VARCHAR | 255 | - | 社团Logo |
| category | VARCHAR | 50 | - | 社团类别 |
| president_id | BIGINT | - | FK | 社长ID |
| teacher_id | BIGINT | - | FK | 指导老师ID |
| balance | DECIMAL | 12,2 | DEFAULT 0 | 账户余额 |
| status | TINYINT | - | DEFAULT 1 | 状态(0停用/1正常) |
| create_time | DATETIME | - | DEFAULT NOW() | 创建时间 |
| update_time | DATETIME | - | ON UPDATE NOW() | 更新时间 |
| deleted | TINYINT | - | DEFAULT 0 | 删除标记 |

### 3.4 社团成员表 (club_member)

| 字段名 | 类型 | 长度 | 约束 | 说明 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 主键ID |
| club_id | BIGINT | - | FK, NOT NULL | 社团ID |
| user_id | BIGINT | - | FK, NOT NULL | 用户ID |
| position | VARCHAR | 50 | - | 职位 |
| join_time | DATETIME | - | DEFAULT NOW() | 加入时间 |
| status | TINYINT | - | DEFAULT 1 | 状态(0退出/1正常) |
| create_time | DATETIME | - | DEFAULT NOW() | 创建时间 |

### 3.5 资金申请表 (fund_apply)

| 字段名 | 类型 | 长度 | 约束 | 说明 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 主键ID |
| apply_no | VARCHAR | 30 | UNIQUE, NOT NULL | 申请编号 |
| club_id | BIGINT | - | FK, NOT NULL | 社团ID |
| applicant_id | BIGINT | - | FK, NOT NULL | 申请人ID |
| apply_type | VARCHAR | 20 | NOT NULL | 申请类型 |
| amount | DECIMAL | 12,2 | NOT NULL | 申请金额 |
| reason | TEXT | NOT NULL | - | 申请理由 |
| vouchers | TEXT | - | - | 凭证图片(JSON) |
| activity_id | BIGINT | - | FK | 关联活动ID |
| status | VARCHAR | 20 | DEFAULT 'PENDING' | 审批状态 |
| current_step | INT | DEFAULT 1 | 当前审批步骤 |
| create_time | DATETIME | - | DEFAULT NOW() | 创建时间 |
| update_time | DATETIME | - | ON UPDATE NOW() | 更新时间 |
| deleted | TINYINT | - | DEFAULT 0 | 删除标记 |

**申请类型:**
- ACTIVITY_FUND: 活动经费
- MATERIAL: 物资采购
- REIMBURSEMENT: 报销申请
- OTHER: 其他

**审批状态:**
- PENDING: 待审批
- PRESIDENT_APPROVED: 社长已审
- TEACHER_APPROVED: 老师已审
- COMPLETED: 已完成
- REJECTED: 已驳回
- CANCELLED: 已撤销

### 3.6 审批记录表 (approval_record)

| 字段名 | 类型 | 长度 | 约束 | 说明 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 主键ID |
| fund_apply_id | BIGINT | - | FK, NOT NULL | 资金申请ID |
| approver_id | BIGINT | - | FK, NOT NULL | 审批人ID |
| step | INT | NOT NULL | 审批步骤 |
| action | VARCHAR | 20 | NOT NULL | 审批动作 |
| comment | TEXT | - | - | 审批意见 |
| create_time | DATETIME | - | DEFAULT NOW() | 审批时间 |

**审批动作:**
- APPROVE: 通过
- REJECT: 驳回
- RETURN: 退回修改

### 3.7 资金流水表 (fund_flow)

| 字段名 | 类型 | 长度 | 约束 | 说明 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 主键ID |
| flow_no | VARCHAR | 30 | UNIQUE, NOT NULL | 流水号 |
| club_id | BIGINT | - | FK, NOT NULL | 社团ID |
| fund_apply_id | BIGINT | - | FK | 关联申请ID |
| flow_type | VARCHAR | 20 | NOT NULL | 流水类型 |
| amount | DECIMAL | 12,2 | NOT NULL | 交易金额 |
| balance_before | DECIMAL | 12,2 | NOT NULL | 交易前余额 |
| balance_after | DECIMAL | 12,2 | NOT NULL | 交易后余额 |
| description | VARCHAR | 255 | - | 交易描述 |
| operator_id | BIGINT | - | FK | 操作人ID |
| create_time | DATETIME | - | DEFAULT NOW() | 交易时间 |

**流水类型:**
- INCOME: 收入
- EXPENSE: 支出
- REFUND: 退款
- ADJUST: 调整

### 3.8 活动表 (activity)

| 字段名 | 类型 | 长度 | 约束 | 说明 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 主键ID |
| club_id | BIGINT | - | FK, NOT NULL | 社团ID |
| activity_name | VARCHAR | 100 | NOT NULL | 活动名称 |
| description | TEXT | - | - | 活动描述 |
| start_time | DATETIME | NOT NULL | 开始时间 |
| end_time | DATETIME | NOT NULL | 结束时间 |
| location | VARCHAR | 200 | - | 活动地点 |
| budget | DECIMAL | 12,2 | - | 活动预算 |
| status | VARCHAR | 20 | DEFAULT 'DRAFT' | 活动状态 |
| cover_image | VARCHAR | 255 | - | 封面图片 |
| create_time | DATETIME | - | DEFAULT NOW() | 创建时间 |
| update_time | DATETIME | - | ON UPDATE NOW() | 更新时间 |
| deleted | TINYINT | - | DEFAULT 0 | 删除标记 |

### 3.9 活动报名表 (activity_signup)

| 字段名 | 类型 | 长度 | 约束 | 说明 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 主键ID |
| activity_id | BIGINT | - | FK, NOT NULL | 活动ID |
| user_id | BIGINT | - | FK, NOT NULL | 用户ID |
| signup_time | DATETIME | - | DEFAULT NOW() | 报名时间 |
| status | TINYINT | - | DEFAULT 1 | 状态 |

### 3.10 通知表 (notification)

| 字段名 | 类型 | 长度 | 约束 | 说明 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 主键ID |
| title | VARCHAR | 100 | NOT NULL | 通知标题 |
| content | TEXT | NOT NULL | - | 通知内容 |
| type | VARCHAR | 20 | NOT NULL | 通知类型 |
| sender_id | BIGINT | - | FK | 发送者ID |
| receiver_id | BIGINT | - | FK, NOT NULL | 接收者ID |
| related_id | BIGINT | - | - | 关联业务ID |
| related_type | VARCHAR | 50 | - | 关联业务类型 |
| is_read | TINYINT | - | DEFAULT 0 | 是否已读 |
| create_time | DATETIME | - | DEFAULT NOW() | 创建时间 |

**通知类型:**
- SYSTEM: 系统通知
- APPROVAL: 审批通知
- ACTIVITY: 活动通知
- WARNING: 预警通知

### 3.11 系统日志表 (sys_log)

| 字段名 | 类型 | 长度 | 约束 | 说明 |
|--------|------|------|------|------|
| id | BIGINT | - | PK, AUTO_INCREMENT | 主键ID |
| user_id | BIGINT | - | FK | 操作用户ID |
| username | VARCHAR | 50 | - | 操作用户名 |
| operation | VARCHAR | 100 | - | 操作描述 |
| method | VARCHAR | 200 | - | 请求方法 |
| params | TEXT | - | - | 请求参数 |
| ip | VARCHAR | 50 | - | IP地址 |
| status | TINYINT | - | - | 操作状态 |
| error_msg | TEXT | - | - | 错误信息 |
| duration | BIGINT | - | - | 执行时长(ms) |
| create_time | DATETIME | - | DEFAULT NOW() | 创建时间 |

## 四、实体类设计

### 4.1 用户实体 (User.java)

```java
@Entity
@Table(name = "sys_user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String password;
    private String realName;
    private String studentId;
    private String phone;
    private String email;
    private String avatar;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;
    
    private Integer status;
    
    @CreationTimestamp
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    private LocalDateTime updateTime;
    
    private Integer deleted;
    
    @Transient
    private List<String> permissions;
}
```

### 4.2 社团实体 (Club.java)

```java
@Entity
@Table(name = "club")
@Data
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String clubName;
    private String clubCode;
    private String description;
    private String logo;
    private String category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "president_id")
    private User president;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;
    
    private BigDecimal balance;
    private Integer status;
    
    @CreationTimestamp
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    private LocalDateTime updateTime;
    
    private Integer deleted;
    
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<ClubMember> members;
}
```

### 4.3 资金申请实体 (FundApply.java)

```java
@Entity
@Table(name = "fund_apply")
@Data
public class FundApply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String applyNo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;
    
    private String applyType;
    private BigDecimal amount;
    private String reason;
    
    @Column(columnDefinition = "TEXT")
    private String vouchers;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;
    
    private String status;
    private Integer currentStep;
    
    @CreationTimestamp
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    private LocalDateTime updateTime;
    
    private Integer deleted;
    
    @OneToMany(mappedBy = "fundApply", cascade = CascadeType.ALL)
    private List<ApprovalRecord> approvalRecords;
}
```

### 4.4 资金流水实体 (FundFlow.java)

```java
@Entity
@Table(name = "fund_flow")
@Data
public class FundFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String flowNo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_apply_id")
    private FundApply fundApply;
    
    private String flowType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    private User operator;
    
    @CreationTimestamp
    private LocalDateTime createTime;
}
```

## 五、接口设计

### 5.1 认证接口

#### 登录
```
POST /api/auth/login
Request:
{
    "username": "string",
    "password": "string"
}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "jwt_token",
        "user": {
            "id": 1,
            "username": "string",
            "realName": "string",
            "role": "string",
            "permissions": []
        }
    }
}
```

#### 登出
```
POST /api/auth/logout
Headers: Authorization: Bearer {token}

Response:
{
    "code": 200,
    "message": "success"
}
```

### 5.2 用户接口

#### 获取用户信息
```
GET /api/user/info
Headers: Authorization: Bearer {token}

Response:
{
    "code": 200,
    "data": {
        "id": 1,
        "username": "string",
        "realName": "string",
        "studentId": "string",
        "phone": "string",
        "email": "string",
        "avatar": "string",
        "role": {
            "id": 1,
            "roleName": "string",
            "roleCode": "string"
        }
    }
}
```

#### 更新个人信息
```
PUT /api/user/profile
Headers: Authorization: Bearer {token}
Request:
{
    "realName": "string",
    "phone": "string",
    "email": "string"
}

Response:
{
    "code": 200,
    "message": "更新成功"
}
```

#### 修改密码
```
PUT /api/user/password
Headers: Authorization: Bearer {token}
Request:
{
    "oldPassword": "string",
    "newPassword": "string"
}

Response:
{
    "code": 200,
    "message": "密码修改成功"
}
```

### 5.3 社团接口

#### 获取社团列表
```
GET /api/club/list?page=1&size=10&keyword=string
Headers: Authorization: Bearer {token}

Response:
{
    "code": 200,
    "data": {
        "total": 100,
        "list": [
            {
                "id": 1,
                "clubName": "string",
                "clubCode": "string",
                "category": "string",
                "memberCount": 50,
                "balance": 10000.00,
                "status": 1
            }
        ]
    }
}
```

#### 获取社团详情
```
GET /api/club/{id}
Headers: Authorization: Bearer {token}

Response:
{
    "code": 200,
    "data": {
        "id": 1,
        "clubName": "string",
        "clubCode": "string",
        "description": "string",
        "logo": "string",
        "category": "string",
        "president": {
            "id": 1,
            "realName": "string"
        },
        "teacher": {
            "id": 2,
            "realName": "string"
        },
        "balance": 10000.00,
        "memberCount": 50,
        "createTime": "2024-01-01 00:00:00"
    }
}
```

#### 获取社团成员
```
GET /api/club/{id}/members?page=1&size=10
Headers: Authorization: Bearer {token}

Response:
{
    "code": 200,
    "data": {
        "total": 50,
        "list": [
            {
                "id": 1,
                "userId": 1,
                "realName": "string",
                "position": "string",
                "joinTime": "2024-01-01"
            }
        ]
    }
}
```

### 5.4 资金接口

#### 提交资金申请
```
POST /api/fund/apply
Headers: Authorization: Bearer {token}
Request:
{
    "clubId": 1,
    "applyType": "ACTIVITY_FUND",
    "amount": 1000.00,
    "reason": "string",
    "vouchers": ["path1", "path2"],
    "activityId": 1
}

Response:
{
    "code": 200,
    "message": "申请提交成功",
    "data": {
        "applyNo": "FA202401010001"
    }
}
```

#### 获取资金申请列表
```
GET /api/fund/list?page=1&size=10&status=PENDING&clubId=1
Headers: Authorization: Bearer {token}

Response:
{
    "code": 200,
    "data": {
        "total": 20,
        "list": [
            {
                "id": 1,
                "applyNo": "string",
                "clubName": "string",
                "applicantName": "string",
                "applyType": "string",
                "amount": 1000.00,
                "status": "PENDING",
                "createTime": "2024-01-01 00:00:00"
            }
        ]
    }
}
```

#### 获取资金申请详情
```
GET /api/fund/{id}
Headers: Authorization: Bearer {token}

Response:
{
    "code": 200,
    "data": {
        "id": 1,
        "applyNo": "string",
        "club": {},
        "applicant": {},
        "applyType": "string",
        "amount": 1000.00,
        "reason": "string",
        "vouchers": [],
        "activity": {},
        "status": "string",
        "currentStep": 1,
        "approvalRecords": [
            {
                "step": 1,
                "approverName": "string",
                "action": "APPROVE",
                "comment": "string",
                "createTime": "2024-01-01 00:00:00"
            }
        ],
        "createTime": "2024-01-01 00:00:00"
    }
}
```

#### 获取资金流水
```
GET /api/fund/flow?page=1&size=10&clubId=1&flowType=EXPENSE&startDate=&endDate=
Headers: Authorization: Bearer {token}

Response:
{
    "code": 200,
    "data": {
        "total": 100,
        "list": [
            {
                "id": 1,
                "flowNo": "string",
                "flowType": "EXPENSE",
                "amount": 1000.00,
                "balanceBefore": 10000.00,
                "balanceAfter": 9000.00,
                "description": "string",
                "operatorName": "string",
                "createTime": "2024-01-01 00:00:00"
            }
        ]
    }
}
```

### 5.5 审批接口

#### 获取待审批列表
```
GET /api/approval/list?page=1&size=10&status=PENDING
Headers: Authorization: Bearer {token}

Response:
{
    "code": 200,
    "data": {
        "total": 10,
        "list": [
            {
                "id": 1,
                "applyNo": "string",
                "clubName": "string",
                "applicantName": "string",
                "applyType": "string",
                "amount": 1000.00,
                "currentStep": 2,
                "createTime": "2024-01-01 00:00:00"
            }
        ]
    }
}
```

#### 审批通过
```
POST /api/approval/{id}/approve
Headers: Authorization: Bearer {token}
Request:
{
    "comment": "string"
}

Response:
{
    "code": 200,
    "message": "审批通过"
}
```

#### 审批驳回
```
POST /api/approval/{id}/reject
Headers: Authorization: Bearer {token}
Request:
{
    "comment": "string"
}

Response:
{
    "code": 200,
    "message": "已驳回"
}
```

### 5.6 文件上传接口

#### 上传图片
```
POST /api/upload/image
Headers: Authorization: Bearer {token}
Content-Type: multipart/form-data

Request:
file: binary

Response:
{
    "code": 200,
    "data": {
        "url": "/upload/voucher/2024/01/01/xxx.jpg",
        "fileName": "xxx.jpg",
        "fileSize": 1024
    }
}
```

## 六、业务逻辑设计

### 6.1 资金申请流程

```
1. 社长发起申请
   ↓
2. 系统生成申请编号
   ↓
3. 状态: PENDING, 步骤: 1
   ↓
4. 社长初审(如需)
   ↓
5. 状态: PRESIDENT_APPROVED, 步骤: 2
   ↓
6. 指导老师审批
   ↓
7. 状态: TEACHER_APPROVED, 步骤: 3
   ↓
8. 系统自动扣款, 生成流水
   ↓
9. 状态: COMPLETED
   ↓
10. 发送通知给申请人
```

### 6.2 审批服务逻辑

```java
@Service
public class ApprovalService {
    
    public void approve(Long fundApplyId, String comment, User approver) {
        FundApply apply = fundApplyRepository.findById(fundApplyId);
        
        if (!canApprove(apply, approver)) {
            throw new BusinessException("无权审批");
        }
        
        ApprovalRecord record = new ApprovalRecord();
        record.setFundApply(apply);
        record.setApprover(approver);
        record.setStep(apply.getCurrentStep());
        record.setAction("APPROVE");
        record.setComment(comment);
        approvalRecordRepository.save(record);
        
        if (apply.getCurrentStep() == 2) {
            apply.setStatus("TEACHER_APPROVED");
            processFundDeduction(apply);
        } else {
            apply.setCurrentStep(apply.getCurrentStep() + 1);
            apply.setStatus("PRESIDENT_APPROVED");
        }
        
        fundApplyRepository.save(apply);
        sendNotification(apply);
    }
    
    private void processFundDeduction(FundApply apply) {
        Club club = apply.getClub();
        BigDecimal balance = club.getBalance();
        BigDecimal amount = apply.getAmount();
        
        if (balance.compareTo(amount) < 0) {
            throw new BusinessException("余额不足");
        }
        
        FundFlow flow = new FundFlow();
        flow.setFlowNo(generateFlowNo());
        flow.setClub(club);
        flow.setFundApply(apply);
        flow.setFlowType("EXPENSE");
        flow.setAmount(amount);
        flow.setBalanceBefore(balance);
        flow.setBalanceAfter(balance.subtract(amount));
        fundFlowRepository.save(flow);
        
        club.setBalance(balance.subtract(amount));
        clubRepository.save(club);
        
        apply.setStatus("COMPLETED");
    }
}
```

### 6.3 风险预警逻辑

```java
@Service
public class RiskWarningService {
    
    public List<RiskWarning> checkWarnings(Long teacherId) {
        List<Club> clubs = clubRepository.findByTeacherId(teacherId);
        List<RiskWarning> warnings = new ArrayList<>();
        
        for (Club club : clubs) {
            if (club.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                warnings.add(createWarning(club, "BALANCE_NEGATIVE", "账户余额为负"));
            }
            
            BigDecimal monthExpense = getMonthExpense(club);
            BigDecimal threshold = getThreshold(club);
            if (monthExpense.compareTo(threshold) > 0) {
                warnings.add(createWarning(club, "OVER_BUDGET", "月支出超预算"));
            }
            
            List<FundApply> pendingApplies = getPendingApplies(club);
            if (pendingApplies.size() > 5) {
                warnings.add(createWarning(club, "TOO_MANY_PENDING", "待审批申请过多"));
            }
        }
        
        return warnings;
    }
}
```

## 七、安全设计

### 7.1 JWT认证

```java
@Component
public class JwtUtil {
    
    private static final String SECRET = "your-secret-key";
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000;
    
    public String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("userId", user.getId())
            .claim("role", user.getRole().getRoleCode())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
            .signWith(SignatureAlgorithm.HS512, SECRET)
            .compact();
    }
    
    public Claims parseToken(String token) {
        return Jwts.parser()
            .setSigningKey(SECRET)
            .parseClaimsJws(token)
            .getBody();
    }
    
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### 7.2 权限注解

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    String[] value() default {};
    Logical logical() default Logical.AND;
}

@Aspect
@Component
public class PermissionAspect {
    
    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) {
        User user = SecurityUtil.getCurrentUser();
        List<String> permissions = user.getPermissions();
        
        String[] required = requirePermission.value();
        boolean hasPermission = requirePermission.logical() == Logical.AND
            ? permissions.containsAll(Arrays.asList(required))
            : Arrays.stream(required).anyMatch(permissions::contains);
        
        if (!hasPermission) {
            throw new AuthException("权限不足");
        }
        
        return joinPoint.proceed();
    }
}
```

### 7.3 密码加密

```java
@Service
public class AuthService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public void register(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }
    
    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("密码错误");
        }
        return jwtUtil.generateToken(user);
    }
}
```

## 八、文件上传设计

### 8.1 上传配置

```java
@Configuration
public class UploadConfig {
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @Value("${upload.max-size}")
    private long maxSize;
    
    @Value("${upload.allowed-types}")
    private String[] allowedTypes;
}
```

### 8.2 文件服务

```java
@Service
public class FileService {
    
    public String uploadImage(MultipartFile file, String type) {
        String originalName = file.getOriginalFilename();
        String extension = getFileExtension(originalName);
        String newFileName = UUID.randomUUID() + "." + extension;
        
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = "/" + type + "/" + datePath + "/" + newFileName;
        String absolutePath = uploadPath + relativePath;
        
        File dest = new File(absolutePath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        
        file.transferTo(dest);
        
        return relativePath;
    }
}
```

### 8.3 静态资源映射

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
            .addResourceLocations("file:" + uploadPath + "/");
    }
}
```

## 九、配置文件

### 9.1 application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/club_fund?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
        
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

server:
  port: 8080

jwt:
  secret: your-jwt-secret-key
  expiration: 604800000

upload:
  path: ./upload
  max-size: 10485760
  allowed-types: jpg,jpeg,png,gif,pdf
```

## 十、异常处理

### 10.1 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(AuthException.class)
    public Result handleAuthException(AuthException e) {
        return Result.error(401, e.getMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return Result.error(400, message);
    }
    
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统异常");
    }
}
```

## 十一、日志设计

### 11.1 操作日志切面

```java
@Aspect
@Component
public class LogAspect {
    
    @Around("@annotation(log)")
    public Object around(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        SysLog sysLog = new SysLog();
        sysLog.setOperation(log.value());
        sysLog.setMethod(joinPoint.getSignature().toShortString());
        sysLog.setParams(JSON.toJSONString(joinPoint.getArgs()));
        sysLog.setIp(ServletUtil.getClientIP());
        sysLog.setUserId(SecurityUtil.getCurrentUserId());
        
        try {
            Object result = joinPoint.proceed();
            sysLog.setStatus(1);
            return result;
        } catch (Exception e) {
            sysLog.setStatus(0);
            sysLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            sysLog.setDuration(System.currentTimeMillis() - startTime);
            sysLogRepository.save(sysLog);
        }
    }
}
```
