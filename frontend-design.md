# 高校社团资金管控平台 - 前端设计文档

## 一、技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Vite | 5.x | 构建工具 |
| UnoCSS | latest | 原子化CSS引擎 |
| Ant Design Vue | 4.x | UI组件库 |
| Vue Router | 4.x | 路由管理 |
| Pinia | 2.x | 状态管理 |
| Axios | 1.x | HTTP请求库 |
| Day.js | latest | 日期处理 |

## 二、项目结构

```
frontend/
├── public/                     # 静态资源
│   └── favicon.ico
├── src/
│   ├── api/                    # API接口模块
│   │   ├── user.js             # 用户相关接口
│   │   ├── club.js             # 社团相关接口
│   │   ├── fund.js             # 资金相关接口
│   │   ├── activity.js         # 活动相关接口
│   │   ├── approval.js         # 审批相关接口
│   │   └── notification.js     # 通知相关接口
│   ├── assets/                 # 静态资源
│   │   ├── images/             # 图片资源
│   │   └── styles/             # 全局样式
│   ├── components/             # 公共组件
│   │   ├── common/             # 通用组件
│   │   │   ├── Header.vue      # 头部组件
│   │   │   ├── Sidebar.vue     # 侧边栏组件
│   │   │   ├── Footer.vue      # 底部组件
│   │   │   └── Breadcrumb.vue  # 面包屑组件
│   │   ├── fund/               # 资金相关组件
│   │   │   ├── FundCard.vue    # 资金卡片
│   │   │   ├── FundFlow.vue    # 资金流水
│   │   │   └── FundChart.vue   # 资金图表
│   │   ├── approval/           # 审批相关组件
│   │   │   ├── ApprovalStep.vue    # 审批步骤
│   │   │   └── ApprovalTimeline.vue # 审批时间线
│   │   └── upload/             # 上传组件
│   │       └── ImageUpload.vue # 图片上传组件
│   ├── composables/            # 组合式函数
│   │   ├── useAuth.js          # 认证相关
│   │   ├── useUpload.js        # 上传相关
│   │   └── useNotification.js  # 通知相关
│   ├── layouts/                # 布局组件
│   │   ├── DefaultLayout.vue   # 默认布局
│   │   ├── AdminLayout.vue     # 管理员布局
│   │   └── BlankLayout.vue     # 空白布局
│   ├── router/                 # 路由配置
│   │   ├── index.js            # 路由入口
│   │   ├── routes.js           # 路由定义
│   │   └── guards.js           # 路由守卫
│   ├── stores/                 # 状态管理
│   │   ├── user.js             # 用户状态
│   │   ├── club.js             # 社团状态
│   │   └── app.js              # 应用状态
│   ├── utils/                  # 工具函数
│   │   ├── request.js          # Axios封装
│   │   ├── auth.js             # 认证工具
│   │   ├── format.js           # 格式化工具
│   │   └── validate.js         # 验证工具
│   ├── views/                  # 页面视图
│   │   ├── login/              # 登录模块
│   │   │   └── index.vue
│   │   ├── dashboard/          # 仪表盘
│   │   │   └── index.vue
│   │   ├── member/             # 社团成员模块
│   │   │   ├── Profile.vue     # 个人中心
│   │   │   ├── FundView.vue    # 财务查看
│   │   │   ├── ActivityList.vue # 活动列表
│   │   │   └── Notification.vue # 通知中心
│   │   ├── president/          # 社长模块
│   │   │   ├── ClubManage.vue  # 社团管理
│   │   │   ├── MemberManage.vue # 成员管理
│   │   │   ├── FundApply.vue   # 资金申请
│   │   │   ├── FundReview.vue  # 资金审核
│   │   │   └── FinanceReport.vue # 财务报表
│   │   ├── teacher/            # 指导老师模块
│   │   │   ├── ClubMonitor.vue # 社团监管
│   │   │   ├── ApprovalList.vue # 审批列表
│   │   │   ├── ApprovalDetail.vue # 审批详情
│   │   │   └── RiskWarning.vue # 风险预警
│   │   ├── admin/              # 校级管理员模块
│   │   │   ├── SystemConfig.vue # 系统配置
│   │   │   ├── UserManage.vue  # 用户管理
│   │   │   ├── ClubManage.vue  # 社团管理
│   │   │   ├── DataMonitor.vue # 数据监控
│   │   │   └── LogManage.vue   # 日志管理
│   │   └── error/              # 错误页面
│   │       ├── 404.vue
│   │       └── 403.vue
│   ├── App.vue                 # 根组件
│   └── main.js                 # 入口文件
├── .env                        # 环境变量
├── .env.development            # 开发环境变量
├── .env.production             # 生产环境变量
├── index.html                  # HTML模板
├── package.json                # 项目配置
├── vite.config.js              # Vite配置
└── uno.config.js               # UnoCSS配置
```

## 三、页面设计

### 3.1 公共页面

#### 3.1.1 登录页面
- **路径**: `/login`
- **功能**: 用户登录、记住密码、忘记密码
- **组件**: 登录表单、验证码

#### 3.1.2 仪表盘
- **路径**: `/dashboard`
- **功能**: 数据概览、快捷入口、待办事项
- **组件**: 数据卡片、图表、待办列表

### 3.2 社团成员页面

#### 3.2.1 个人中心
- **路径**: `/member/profile`
- **功能**: 个人信息查看/编辑、密码修改、头像上传
- **组件**: 个人信息表单、头像上传

#### 3.2.2 财务信息公开
- **路径**: `/member/fund`
- **功能**: 社团财务公开信息查看、资金流水查询
- **组件**: 资金列表、流水详情、筛选条件

#### 3.2.3 活动与流程参与
- **路径**: `/member/activity`
- **功能**: 活动列表查看、活动详情、活动报名
- **组件**: 活动卡片、活动详情、报名表单

#### 3.2.4 通知与提醒
- **路径**: `/member/notification`
- **功能**: 系统通知、审批通知、活动通知
- **组件**: 通知列表、通知详情、已读/未读标记

### 3.3 社长页面

#### 3.3.1 社团内部管理
- **路径**: `/president/club`
- **功能**: 社团信息编辑、成员管理、角色分配
- **组件**: 社团信息表单、成员列表、角色选择器

#### 3.3.2 资金申请
- **路径**: `/president/fund/apply`
- **功能**: 发起资金申请、上传凭证、填写用途
- **组件**: 申请表单、凭证上传、金额计算

#### 3.3.3 资金审核
- **路径**: `/president/fund/review`
- **功能**: 审核成员申请、查看详情、批准/驳回
- **组件**: 审核列表、审核详情、审批操作

#### 3.3.4 财务数据查看
- **路径**: `/president/finance`
- **功能**: 社团财务报表、收支统计、趋势分析
- **组件**: 财务报表、统计图表、数据导出

### 3.4 指导老师页面

#### 3.4.1 多社团监管
- **路径**: `/teacher/clubs`
- **功能**: 查看负责社团列表、社团财务概况
- **组件**: 社团列表、财务概览卡片

#### 3.4.2 核心审批
- **路径**: `/teacher/approval`
- **功能**: 待审批列表、审批详情、审批操作
- **组件**: 审批列表、审批详情、审批流程图

#### 3.4.3 风险预警
- **路径**: `/teacher/warning`
- **功能**: 异常资金预警、超支预警、违规提醒
- **组件**: 预警列表、预警详情、处理记录

### 3.5 校级管理员页面

#### 3.5.1 系统全局管理
- **路径**: `/admin/system`
- **功能**: 系统参数配置、审批流程配置
- **组件**: 配置表单、流程编辑器

#### 3.5.2 用户管理
- **路径**: `/admin/user`
- **功能**: 用户列表、用户审核、权限管理
- **组件**: 用户列表、用户详情、权限分配

#### 3.5.3 社团管理
- **路径**: `/admin/club`
- **功能**: 社团列表、社团审核、社团状态管理
- **组件**: 社团列表、社团详情、状态操作

#### 3.5.4 全局数据监控
- **路径**: `/admin/monitor`
- **功能**: 系统数据统计、资金流向监控、使用情况分析
- **组件**: 数据大屏、统计图表、实时监控

## 四、组件设计

### 4.1 核心组件

#### 4.1.1 资金申请表单 (FundApplyForm.vue)
```
功能描述:
- 申请类型选择(活动经费/物资采购/其他)
- 申请金额输入
- 申请理由文本域
- 凭证图片上传(支持多图)
- 关联活动选择

验证规则:
- 金额必须大于0
- 申请理由必填且不少于20字
- 至少上传一张凭证图片
```

#### 4.1.2 审批流程组件 (ApprovalFlow.vue)
```
功能描述:
- 展示审批流程节点
- 显示当前审批状态
- 审批历史记录
- 审批意见输入

流程节点:
1. 社长发起/初审
2. 指导老师审批
3. 财务确认
4. 完成
```

#### 4.1.3 图片上传组件 (ImageUpload.vue)
```
功能描述:
- 支持拖拽上传
- 图片预览
- 图片删除
- 上传进度显示
- 图片大小/格式限制

配置参数:
- 最大上传数量
- 最大文件大小
- 允许的文件类型
- 上传地址
```

#### 4.1.4 资金流水组件 (FundFlowList.vue)
```
功能描述:
- 流水列表展示
- 筛选条件(时间/类型/金额范围)
- 导出功能
- 详情查看

显示字段:
- 流水编号
- 交易时间
- 交易类型(收入/支出)
- 交易金额
- 交易对象
- 关联申请
- 当前状态
```

### 4.2 通用组件

#### 4.2.1 页面头部 (PageHeader.vue)
```
Props:
- title: 页面标题
- subtitle: 副标题
- breadcrumb: 面包屑数据

Slots:
- extra: 额外操作区域
```

#### 4.2.2 数据表格 (DataTable.vue)
```
Props:
- columns: 列配置
- dataSource: 数据源
- loading: 加载状态
- pagination: 分页配置

Events:
- change: 分页/筛选变化
- rowClick: 行点击
```

## 五、状态管理设计

### 5.1 用户状态 (stores/user.js)
```javascript
state: {
  token: '',           // 用户令牌
  userInfo: {},        // 用户信息
  roles: [],           // 用户角色
  permissions: [],     // 权限列表
  currentClub: null    // 当前社团(社长/成员)
}

actions: {
  login()              // 登录
  logout()             // 登出
  getUserInfo()        // 获取用户信息
  updateProfile()      // 更新个人信息
  changePassword()     // 修改密码
}
```

### 5.2 应用状态 (stores/app.js)
```javascript
state: {
  theme: 'light',      // 主题
  collapsed: false,    // 侧边栏折叠状态
  notifications: [],   // 通知列表
  unreadCount: 0       // 未读消息数
}

actions: {
  toggleTheme()        // 切换主题
  toggleSidebar()      // 切换侧边栏
  fetchNotifications() // 获取通知
  markAsRead()         // 标记已读
}
```

### 5.3 社团状态 (stores/club.js)
```javascript
state: {
  clubList: [],        // 社团列表
  currentClub: {},     // 当前社团详情
  members: [],         // 成员列表
  fundOverview: {}     // 资金概览
}

actions: {
  getClubList()        // 获取社团列表
  getClubDetail()      // 获取社团详情
  getMembers()         // 获取成员列表
  getFundOverview()    // 获取资金概览
}
```

## 六、API接口设计

### 6.1 请求封装 (utils/request.js)
```javascript
基础配置:
- baseURL: API基础地址
- timeout: 请求超时时间
- headers: 请求头配置

请求拦截:
- 添加Token认证
- 添加请求时间戳

响应拦截:
- 统一错误处理
- Token过期处理
- 数据格式化
```

### 6.2 接口模块

#### 用户模块 (api/user.js)
```
POST   /api/auth/login           用户登录
POST   /api/auth/logout          用户登出
GET    /api/user/info            获取用户信息
PUT    /api/user/profile         更新个人信息
PUT    /api/user/password        修改密码
POST   /api/user/avatar          上传头像
```

#### 社团模块 (api/club.js)
```
GET    /api/club/list            获取社团列表
GET    /api/club/:id             获取社团详情
POST   /api/club                 创建社团
PUT    /api/club/:id             更新社团信息
GET    /api/club/:id/members     获取社团成员
POST   /api/club/:id/member      添加成员
DELETE /api/club/:id/member/:uid 移除成员
```

#### 资金模块 (api/fund.js)
```
GET    /api/fund/list            获取资金列表
GET    /api/fund/:id             获取资金详情
POST   /api/fund/apply           提交资金申请
PUT    /api/fund/:id             更新申请
DELETE /api/fund/:id             删除申请
GET    /api/fund/flow            获取资金流水
GET    /api/fund/export          导出财务报表
```

#### 审批模块 (api/approval.js)
```
GET    /api/approval/list        获取审批列表
GET    /api/approval/:id         获取审批详情
POST   /api/approval/:id/approve 审批通过
POST   /api/approval/:id/reject  审批驳回
GET    /api/approval/history     审批历史
```

#### 通知模块 (api/notification.js)
```
GET    /api/notification/list    获取通知列表
GET    /api/notification/:id     获取通知详情
PUT    /api/notification/:id/read 标记已读
PUT    /api/notification/read-all 全部已读
```

## 七、路由设计

### 7.1 路由配置
```javascript
const routes = [
  {
    path: '/login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'dashboard' }
      },
      // 社团成员路由
      {
        path: 'member',
        meta: { title: '个人中心', roles: ['member', 'president', 'teacher', 'admin'] },
        children: [
          { path: 'profile', component: () => import('@/views/member/Profile.vue') },
          { path: 'fund', component: () => import('@/views/member/FundView.vue') },
          { path: 'activity', component: () => import('@/views/member/ActivityList.vue') },
          { path: 'notification', component: () => import('@/views/member/Notification.vue') }
        ]
      },
      // 社长路由
      {
        path: 'president',
        meta: { title: '社长管理', roles: ['president'] },
        children: [
          { path: 'club', component: () => import('@/views/president/ClubManage.vue') },
          { path: 'member', component: () => import('@/views/president/MemberManage.vue') },
          { path: 'fund/apply', component: () => import('@/views/president/FundApply.vue') },
          { path: 'fund/review', component: () => import('@/views/president/FundReview.vue') },
          { path: 'finance', component: () => import('@/views/president/FinanceReport.vue') }
        ]
      },
      // 指导老师路由
      {
        path: 'teacher',
        meta: { title: '指导老师', roles: ['teacher'] },
        children: [
          { path: 'clubs', component: () => import('@/views/teacher/ClubMonitor.vue') },
          { path: 'approval', component: () => import('@/views/teacher/ApprovalList.vue') },
          { path: 'approval/:id', component: () => import('@/views/teacher/ApprovalDetail.vue') },
          { path: 'warning', component: () => import('@/views/teacher/RiskWarning.vue') }
        ]
      },
      // 校级管理员路由
      {
        path: 'admin',
        meta: { title: '系统管理', roles: ['admin'] },
        children: [
          { path: 'system', component: () => import('@/views/admin/SystemConfig.vue') },
          { path: 'user', component: () => import('@/views/admin/UserManage.vue') },
          { path: 'club', component: () => import('@/views/admin/ClubManage.vue') },
          { path: 'monitor', component: () => import('@/views/admin/DataMonitor.vue') },
          { path: 'log', component: () => import('@/views/admin/LogManage.vue') }
        ]
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    component: () => import('@/views/error/404.vue')
  }
]
```

### 7.2 路由守卫
```javascript
全局前置守卫:
- Token验证
- 权限校验
- 页面标题设置

全局后置守卫:
- 页面访问日志
- 进度条结束
```

## 八、权限设计

### 8.1 角色权限映射
```javascript
角色权限配置:
- member: ['view:fund', 'view:activity', 'apply:activity']
- president: [...member权限, 'manage:club', 'apply:fund', 'review:fund']
- teacher: ['view:all', 'approve:fund', 'monitor:club', 'warning:view']
- admin: ['manage:all', 'config:system', 'view:log']
```

### 8.2 权限指令
```javascript
v-permission 指令:
- 用于按钮级别的权限控制
- 无权限时隐藏元素

使用示例:
<a-button v-permission="['apply:fund']">申请资金</a-button>
```

## 九、样式设计

### 9.1 UnoCSS配置
```javascript
主题色:
- primary: #1890ff (主色)
- success: #52c41a (成功)
- warning: #faad14 (警告)
- error: #f5222d (错误)

常用原子类:
- 布局: flex, grid, p-*, m-*, w-*, h-*
- 文字: text-*, font-*, leading-*
- 背景: bg-*, opacity-*
- 边框: border-*, rounded-*
```

### 9.2 响应式设计
```javascript
断点配置:
- sm: 576px
- md: 768px
- lg: 992px
- xl: 1200px
- xxl: 1600px

响应式布局:
- 移动端: 单列布局, 折叠菜单
- 平板: 双列布局, 抽屉菜单
- 桌面: 多列布局, 固定侧边栏
```

## 十、构建配置

### 10.1 Vite配置
```javascript
关键配置:
- 路径别名: @ -> src/
- 代理配置: /api -> 后端服务
- 构建优化: 代码分割, 压缩
- 插件集成: Vue, UnoCSS
```

### 10.2 环境变量
```javascript
开发环境:
- VITE_API_BASE_URL: 开发环境API地址
- VITE_UPLOAD_URL: 上传服务地址

生产环境:
- VITE_API_BASE_URL: 生产环境API地址
- VITE_UPLOAD_URL: 生产上传地址
```

## 十一、性能优化

### 11.1 代码优化
- 路由懒加载
- 组件按需引入
- 图片懒加载
- 虚拟列表(大数据量)

### 11.2 构建优化
- 代码分割策略
- Tree Shaking
- 压缩优化
- CDN加速

### 11.3 缓存策略
- 静态资源缓存
- API响应缓存
- 本地存储缓存
