<template>
  <div>
    <a-card title="欢迎使用 BaseWeb 系统" :bordered="false">
      <p>这是一个包含完整登录、权限管理的前后端分离项目框架</p>
      <a-space>
        <a-button type="primary" @click="goToUsers" v-if="userStore.isLoggedIn">用户管理</a-button>
        <a-button type="primary" @click="goToAdmin" v-if="userStore.isAdmin" danger>管理后台</a-button>
        <a-button @click="goToAbout">关于</a-button>
      </a-space>
    </a-card>
    
    <a-row :gutter="16" class="mt-4">
      <a-col :span="8">
        <a-card title="前端技术栈">
          <ul class="list-disc pl-5">
            <li>Vue 3 - 渐进式框架</li>
            <li>Vite - 快速构建工具</li>
            <li>Ant Design Vue - UI组件库</li>
            <li>Pinia - 状态管理</li>
            <li>Axios - HTTP客户端</li>
          </ul>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card title="后端技术栈">
          <ul class="list-disc pl-5">
            <li>Spring Boot 3</li>
            <li>Spring Security</li>
            <li>JWT 认证</li>
            <li>Hibernate/JPA</li>
            <li>MySQL 数据库</li>
          </ul>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card title="系统功能">
          <ul class="list-disc pl-5">
            <li>✅ 用户注册/登录</li>
            <li>✅ JWT Token 认证</li>
            <li>✅ 角色权限管理</li>
            <li>✅ 账户状态控制</li>
            <li>✅ 全局错误处理</li>
          </ul>
        </a-card>
      </a-col>
    </a-row>
    
    <a-alert
      v-if="!userStore.isLoggedIn"
      message="您尚未登录"
      description="请登录以访问更多功能"
      type="info"
      show-icon
      class="mt-4"
    >
      <template #action>
        <a-button type="primary" @click="goToLogin">立即登录</a-button>
      </template>
    </a-alert>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const goToUsers = () => {
  router.push('/users')
}

const goToAdmin = () => {
  router.push('/admin')
}

const goToAbout = () => {
  router.push('/about')
}

const goToLogin = () => {
  router.push('/login')
}
</script>
