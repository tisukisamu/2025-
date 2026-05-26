<template>
  <div>
    <a-card title="关于 BaseWeb" :bordered="false">
      <p>这是一个基于 Vue3 + Spring Boot 的全栈项目模板，包含完整的用户认证和权限管理系统。</p>
      <a-divider />
      
      <h3>项目结构</h3>
      <pre class="bg-gray-50 p-4 rounded-lg overflow-x-auto">
baseweb/
├── frontend/              # 前端项目 (Vue3 + Vite)
│   ├── src/
│   │   ├── api/           # API接口
│   │   ├── components/    # 组件
│   │   ├── router/        # 路由配置
│   │   ├── stores/        # Pinia 状态管理
│   │   ├── utils/         # 工具函数
│   │   └── views/         # 页面视图
│   │       ├── auth/      # 登录注册
│   │       └── admin/     # 管理后台
│   └── package.json
├── backend/               # 后端项目 (Spring Boot)
│   ├── src/main/java/
│   │   ├── config/        # 配置类
│   │   ├── controller/    # 控制器
│   │   ├── dto/           # 数据传输对象
│   │   ├── entity/        # 实体类
│   │   ├── exception/     # 异常类
│   │   ├── repository/    # 数据仓库
│   │   ├── security/      # 安全配置
│   │   └── service/       # 服务层
│   └── pom.xml
      </pre>
      
      <a-divider />
      
      <h3>权限系统说明</h3>
      <a-descriptions bordered :column="1">
        <a-descriptions-item label="管理员 (ADMIN)">
          拥有所有权限，可以管理用户、修改用户状态和角色
        </a-descriptions-item>
        <a-descriptions-item label="普通用户 (USER)">
          只能访问自己的数据和普通功能
        </a-descriptions-item>
        <a-descriptions-item label="账户状态">
          <a-badge status="success" text="ACTIVE - 正常访问" />
          <br />
          <a-badge status="error" text="DISABLED - 账户被禁用" />
        </a-descriptions-item>
      </a-descriptions>
    </a-card>
  </div>
</template>

<script setup>
</script>
