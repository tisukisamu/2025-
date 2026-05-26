<template>
  <a-layout class="min-h-100vh">
    <a-layout-header class="flex items-center px-6 sticky top-0 z-100">
      <div class="text-white text-xl font-bold mr-10">
        <span>BaseWeb</span>
      </div>
      <a-menu
        theme="dark"
        mode="horizontal"
        :selectedKeys="[$route.path]"
        class="flex-1 bg-transparent"
      >
        <a-menu-item key="/" @click="$router.push('/')">
          <home-outlined />
          首页
        </a-menu-item>
        
        <a-menu-item v-if="userStore.isLoggedIn" key="/users" @click="$router.push('/users')">
          <team-outlined />
          用户管理
        </a-menu-item>
        
        <a-menu-item v-if="userStore.isAdmin" key="/admin" @click="$router.push('/admin')">
          <setting-outlined />
          管理后台
        </a-menu-item>
        
        <a-menu-item key="/about" @click="$router.push('/about')">
          <info-circle-outlined />
          关于
        </a-menu-item>
      </a-menu>
      
      <div class="ml-auto">
        <template v-if="userStore.isLoggedIn">
          <a-dropdown>
            <a class="text-white flex items-center gap-2" @click.prevent>
              <a-avatar :style="{ backgroundColor: userStore.isAdmin ? '#f5222d' : '#1890ff' }">
                {{ userStore.username.charAt(0).toUpperCase() }}
              </a-avatar>
              <span class="mx-1">{{ userStore.username }}</span>
              <span v-if="userStore.isAdmin" class="bg-red-500 text-white text-10px px-1.5 rounded">管理员</span>
              <down-outlined />
            </a>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile" @click="$router.push('/profile')">
                  <user-outlined />
                  个人中心
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout">
                  <logout-outlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </template>
        <template v-else>
          <a-space>
            <a-button type="primary" @click="$router.push('/login')">登录</a-button>
            <a-button @click="$router.push('/register')">注册</a-button>
          </a-space>
        </template>
      </div>
    </a-layout-header>
    
    <a-layout-content class="p-6 bg-gray-100">
      <div class="bg-white p-6 rounded-lg min-h-[calc(100vh-200px)]">
        <slot />
      </div>
    </a-layout-content>
    
    <a-layout-footer class="text-center bg-white">
      BaseWeb ©2024 Created by Your Name
    </a-layout-footer>
  </a-layout>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import {
  HomeOutlined,
  TeamOutlined,
  SettingOutlined,
  InfoCircleOutlined,
  UserOutlined,
  LogoutOutlined,
  DownOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>
