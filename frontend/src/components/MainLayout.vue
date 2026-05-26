<template>
  <a-layout class="main-layout">
    <a-layout-header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/')">
          <heart-outlined class="logo-icon" />
          <span class="logo-text">宠物纪念</span>
        </div>
        
        <a-menu
          mode="horizontal"
          :selectedKeys="[activeMenu]"
          @click="handleMenuClick"
          class="nav-menu"
        >
          <a-menu-item key="/">
            <span class="menu-link">
              <home-outlined />
              <span>首页</span>
            </span>
          </a-menu-item>
          
          <a-menu-item key="/services">
            <span class="menu-link">
              <appstore-outlined />
              <span>服务套餐</span>
            </span>
          </a-menu-item>
          
          <a-menu-item v-if="userStore.isLoggedIn" key="/pets">
            <span class="menu-link">
              <bug-outlined />
              <span>我的宠物</span>
            </span>
          </a-menu-item>
          
          <a-menu-item v-if="userStore.isLoggedIn" key="/appointments">
            <span class="menu-link">
              <calendar-outlined />
              <span>我的预约</span>
            </span>
          </a-menu-item>
          
          <a-menu-item key="/memorials">
            <span class="menu-link">
              <picture-outlined />
              <span>纪念相册</span>
            </span>
          </a-menu-item>
          
          <a-menu-item v-if="userStore.isAdmin" key="/admin">
            <span class="menu-link">
              <setting-outlined />
              <span>管理后台</span>
            </span>
          </a-menu-item>
          <a-menu-item
            v-if="userStore.userRole === 'SERVICE' || userStore.isAdmin"
            key="/service"
          >
            <span class="menu-link">
              <cluster-outlined />
              <span>服务工作台</span>
            </span>
          </a-menu-item>
        </a-menu>
        
        <div class="header-right">
          <template v-if="userStore.isLoggedIn">
            <a-dropdown>
              <div class="user-info">
                <a-avatar :size="32" :src="userAvatar" class="user-avatar">
                  {{ userStore.username?.charAt(0)?.toUpperCase() }}
                </a-avatar>
                <span class="user-name">{{ userStore.username }}</span>
                <down-outlined class="dropdown-icon" />
              </div>
              <template #overlay>
                <a-menu class="user-menu">
                  <a-menu-item key="profile" @click="$router.push('/profile')">
                    <user-outlined />
                    <span>个人中心</span>
                  </a-menu-item>
                  <a-menu-item v-if="userStore.isAdmin" key="admin-center" @click="$router.push('/admin')">
                    <setting-outlined />
                    <span>管理中心</span>
                  </a-menu-item>
                  <a-menu-item
                    v-if="userStore.userRole === 'SERVICE' || userStore.isAdmin"
                    key="service-center"
                    @click="$router.push('/service/tasks')"
                  >
                    <appstore-outlined />
                    <span>服务工作台</span>
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="logout" @click="handleLogout">
                    <logout-outlined />
                    <span>退出登录</span>
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>
          <template v-else>
            <a-space :size="12">
              <a-button type="default" @click="$router.push('/login')">
                登录
              </a-button>
              <a-button type="primary" @click="$router.push('/register')">
                注册
              </a-button>
            </a-space>
          </template>
        </div>
      </div>
    </a-layout-header>
    
    <a-layout-content class="content">
      <div class="content-wrapper" :class="{ 'admin-section': isAdminRoute }">
        <router-view />
      </div>
    </a-layout-content>

  </a-layout>
</template>

<script setup>
import { computed } from 'vue'
import { RouterView, useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getImageUrl } from '../utils'
import {
  HomeOutlined,
  AppstoreOutlined,
  BugOutlined,
  ClusterOutlined,
  CalendarOutlined,
  PictureOutlined,
  SettingOutlined,
  UserOutlined,
  LogoutOutlined,
  DownOutlined,
  HeartOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const userAvatar = computed(() => {
  const avatar = userStore.userInfo?.avatar
  return avatar ? getImageUrl(avatar) : null
})

const activeMenu = computed(() => {
  if (route.path.startsWith('/admin')) {
    return '/admin'
  }
  if (route.path.startsWith('/services')) {
    return '/services'
  }
  if (route.path.startsWith('/service')) {
    return '/service'
  }
  if (route.path.startsWith('/appointments')) {
    return '/appointments'
  }
  if (route.path.startsWith('/memorials')) {
    return '/memorials'
  }
  if (route.path.startsWith('/pets')) {
    return '/pets'
  }
  return '/'
})

const isAdminRoute = computed(() => route.path.startsWith('/admin'))

const handleMenuClick = ({ key }) => {
  if (key === '/service') {
    router.push('/service/tasks')
    return
  }
  router.push(key)
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  background: linear-gradient(180deg, #fcfcfc 0%, #f7f7f7 100%);
}

.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
  padding: 0;
  height: 64px;
  line-height: 64px;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  height: 64px;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 40px;
}

.logo-icon {
  font-size: 28px;
  color: #262626;
  margin-right: 8px;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  letter-spacing: 1px;
}

.nav-menu {
  flex: 1;
  border: none;
  background: transparent;
  line-height: 62px;
}

.nav-menu :deep(.ant-menu-item) {
  color: #595959;
  font-size: 14px;
  padding: 0 20px;
  margin: 0 4px;
}

.menu-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: inherit;
  width: 100%;
  height: 100%;
}

.nav-menu :deep(.ant-menu-item:hover) {
  color: #262626;
}

.nav-menu :deep(.ant-menu-item-selected) {
  color: #262626;
  font-weight: 500;
  border-bottom: 2px solid #262626;
}

.nav-menu :deep(.ant-menu-item-selected::after) {
  border-bottom: none;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background 0.3s;
}

.user-info:hover {
  background: #fafafa;
}

.user-avatar {
  background: #262626;
  color: #fff;
}

.user-name {
  margin: 0 8px;
  color: #262626;
  font-size: 14px;
}

.dropdown-icon {
  font-size: 10px;
  color: #8c8c8c;
}

.user-menu {
  min-width: 160px;
  padding: 8px 0;
}

.user-menu :deep(.ant-menu-item) {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  margin: 0;
  height: auto;
  line-height: 1.5;
}

.user-menu :deep(.ant-menu-item .anticon) {
  font-size: 14px;
  margin: 0;
}

.user-menu :deep(.ant-menu-item span:not(.anticon)) {
  flex: 1;
}

.user-menu :deep(.ant-menu-divider) {
  margin: 4px 0;
}

.content {
  padding: 20px 24px 28px;
  min-height: calc(100vh - 64px - 80px);
}

.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
}

.content-wrapper.admin-section :deep(.ant-card),
.content-wrapper.admin-section :deep(.ant-card:hover) {
  box-shadow: none;
}

.footer {
  background: #fff;
  border-top: 1px solid #f0f0f0;
  padding: 24px;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  text-align: center;
}

.footer-info p {
  margin: 0;
  color: #262626;
  font-size: 14px;
}

.copyright {
  color: #8c8c8c !important;
  font-size: 12px !important;
  margin-top: 8px !important;
}

.footer-links {
  margin-top: 16px;
}

.footer-links a {
  color: #595959;
  font-size: 12px;
}

.footer-links a:hover {
  color: #262626;
}

.footer-links :deep(.ant-divider) {
  background: #d9d9d9;
}
</style>
