<template>
  <a-layout class="min-h-screen bg-#f9fafb">
    <a-layout-sider
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      :width="260"
      class="sidebar-container"
    >
      <div class="logo-container">
        <div class="logo-icon">
          <check-square-outlined />
        </div>
        <transition name="fade-slide">
          <span v-if="!collapsed" class="logo-text">HabitFlow</span>
        </transition>
      </div>

      <div class="sidebar-content">
        <div class="nav-section">
          <div v-if="!collapsed" class="nav-section-title">主菜单</div>
          <a-menu
            v-model:selectedKeys="selectedKeys"
            mode="inline"
            :theme="'dark'"
            class="nav-menu"
          >
            <a-menu-item key="home" @click="navigateTo('/')">
              <template #icon>
                <home-outlined />
              </template>
              <span>首页概览</span>
            </a-menu-item>

            <a-menu-item v-if="userStore.isLoggedIn" key="today" @click="navigateTo('/today')">
              <template #icon>
                <rocket-outlined />
              </template>
              <span>今日打卡</span>
            </a-menu-item>
          </a-menu>
        </div>

        <div v-if="userStore.isLoggedIn" class="nav-section">
          <div v-if="!collapsed" class="nav-section-title">习惯管理</div>
          <a-menu
            v-model:selectedKeys="selectedKeys"
            mode="inline"
            :theme="'dark'"
            class="nav-menu"
          >
            <a-menu-item key="habits" @click="navigateTo('/habits')">
              <template #icon>
                <check-square-outlined />
              </template>
              <span>习惯列表</span>
            </a-menu-item>

            <a-menu-item key="categories" @click="navigateTo('/habits/categories')">
              <template #icon>
                <appstore-outlined />
              </template>
              <span>分类管理</span>
            </a-menu-item>

            <a-menu-item key="templates" @click="navigateTo('/templates/habits')">
              <template #icon>
                <book-outlined />
              </template>
              <span>模板中心</span>
            </a-menu-item>
          </a-menu>
        </div>

        <div v-if="userStore.isLoggedIn" class="nav-section">
          <div v-if="!collapsed" class="nav-section-title">打卡记录</div>
          <a-menu
            v-model:selectedKeys="selectedKeys"
            mode="inline"
            :theme="'dark'"
            class="nav-menu"
          >
            <a-menu-item key="calendar" @click="navigateTo('/calendar')">
              <template #icon>
                <calendar-outlined />
              </template>
              <span>打卡日历</span>
            </a-menu-item>

            <a-menu-item key="history" @click="navigateTo('/history')">
              <template #icon>
                <history-outlined />
              </template>
              <span>打卡历史</span>
            </a-menu-item>

            <a-menu-item key="reminders" @click="navigateTo('/reminders')">
              <template #icon>
                <clock-circle-outlined />
              </template>
              <span>提醒中心</span>
            </a-menu-item>

            <a-menu-item key="community" @click="navigateTo('/community/feed')">
              <template #icon>
                <message-outlined />
              </template>
              <span>打卡社区</span>
            </a-menu-item>

            <a-menu-item key="alumni" @click="navigateTo('/alumni/nearby')">
              <template #icon>
                <environment-outlined />
              </template>
              <span>校友互督</span>
            </a-menu-item>
          </a-menu>
        </div>

        <div v-if="userStore.isLoggedIn" class="nav-section">
          <div v-if="!collapsed" class="nav-section-title">数据分析</div>
          <a-menu
            v-model:selectedKeys="selectedKeys"
            mode="inline"
            :theme="'dark'"
            class="nav-menu"
          >
            <a-menu-item key="statistics" @click="navigateTo('/statistics')">
              <template #icon>
                <bar-chart-outlined />
              </template>
              <span>统计面板</span>
            </a-menu-item>

            <a-menu-item key="heatmap" @click="navigateTo('/statistics/heatmap')">
              <template #icon>
                <heat-map-outlined />
              </template>
              <span>热力图</span>
            </a-menu-item>

            <a-menu-item key="weekly" @click="navigateTo('/statistics/weekly')">
              <template #icon>
                <line-chart-outlined />
              </template>
              <span>周报分析</span>
            </a-menu-item>

            <a-menu-item key="achievements" @click="navigateTo('/achievements')">
              <template #icon>
                <trophy-outlined />
              </template>
              <span>成就系统</span>
            </a-menu-item>
          </a-menu>
        </div>

        <div v-if="userStore.isAdmin" class="nav-section">
          <div v-if="!collapsed" class="nav-section-title">系统管理</div>
          <a-menu
            v-model:selectedKeys="selectedKeys"
            mode="inline"
            :theme="'dark'"
            class="nav-menu"
          >
            <a-menu-item key="users" @click="navigateTo('/users')">
              <template #icon>
                <team-outlined />
              </template>
              <span>用户管理</span>
            </a-menu-item>

            <a-menu-item key="admin" @click="navigateTo('/admin')">
              <template #icon>
                <setting-outlined />
              </template>
              <span>管理后台</span>
            </a-menu-item>
          </a-menu>
        </div>
      </div>

      <div class="sidebar-footer">
        <a-menu
          mode="inline"
          :theme="'dark'"
          class="nav-menu"
        >
          <a-menu-item key="about" @click="navigateTo('/about')">
            <template #icon>
              <info-circle-outlined />
            </template>
            <span>关于系统</span>
          </a-menu-item>
        </a-menu>
      </div>
    </a-layout-sider>

    <a-layout class="flex-1">
      <a-layout-header class="header-container">
        <div class="header-left">
          <a-button
            type="text"
            class="collapse-btn"
            @click="collapsed = !collapsed"
          >
            <menu-fold-outlined v-if="!collapsed" />
            <menu-unfold-outlined v-else />
          </a-button>

          <a-breadcrumb class="breadcrumb-nav">
            <a-breadcrumb-item>
              <router-link to="/" class="breadcrumb-link">
                <home-outlined />
              </router-link>
            </a-breadcrumb-item>
            <a-breadcrumb-item v-if="currentRoute?.meta?.title">
              <span class="text-#6b7280">{{ currentRoute.meta.title }}</span>
            </a-breadcrumb-item>
          </a-breadcrumb>
        </div>

        <div class="header-right">
          <a-tooltip title="刷新数据" v-if="userStore.isLoggedIn">
            <a-button type="text" class="action-btn" @click="refreshData">
              <reload-outlined />
            </a-button>
          </a-tooltip>

          <a-tooltip title="消息通知" v-if="userStore.isLoggedIn">
            <a-badge :count="0" :offset="[-5, 5]">
              <a-button type="text" class="action-btn">
                <bell-outlined />
              </a-button>
            </a-badge>
          </a-tooltip>

          <a-dropdown v-if="userStore.isLoggedIn" :trigger="['click']">
            <div class="user-dropdown-trigger">
              <a-avatar :size="36" class="user-avatar">
                <template #icon>
                  <img v-if="userStore.userInfo?.avatar" :src="userStore.userInfo.avatar" alt="avatar" />
                  <user-outlined v-else />
                </template>
              </a-avatar>
              <div v-if="!collapsed" class="user-info">
                <span class="user-name">{{ userStore.userInfo?.name || userStore.userInfo?.username }}</span>
                <span class="user-role">{{ userStore.isAdmin ? '管理员' : '普通用户' }}</span>
              </div>
              <down-outlined class="dropdown-arrow" />
            </div>
            <template #overlay>
              <a-menu class="user-dropdown-menu">
                <a-menu-item key="profile" @click="navigateTo('/profile')">
                  <user-outlined />
                  <span>个人中心</span>
                </a-menu-item>
                <a-menu-item key="settings" @click="navigateTo('/settings')">
                  <setting-outlined />
                  <span>账号设置</span>
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" class="logout-item" @click="handleLogout">
                  <logout-outlined />
                  <span>退出登录</span>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>

          <div v-else class="auth-buttons">
            <a-button class="login-btn" @click="navigateTo('/login')">
              登录
            </a-button>
            <a-button type="primary" class="register-btn" @click="navigateTo('/register')">
              注册
            </a-button>
          </div>
        </div>
      </a-layout-header>

      <a-layout-content class="content-container">
        <router-view v-slot="{ Component, route }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" :key="route.path" />
          </transition>
        </router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import {
  HomeOutlined,
  CheckSquareOutlined,
  CalendarOutlined,
  BarChartOutlined,
  TeamOutlined,
  SettingOutlined,
  UserOutlined,
  LogoutOutlined,
  DownOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  RocketOutlined,
  HistoryOutlined,
  InfoCircleOutlined,
  BellOutlined,
  ReloadOutlined,
  HeatMapOutlined,
  LineChartOutlined,
  AppstoreOutlined,
  BookOutlined,
  TrophyOutlined,
  ClockCircleOutlined,
  MessageOutlined,
  EnvironmentOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const collapsed = ref(false)
const selectedKeys = ref<string[]>(['home'])

const currentRoute = computed(() => route)

watch(() => route.path, (path) => {
  const resolveKey = (p: string) => {
    if (p === '/' || p === '/home') return 'home'
    if (p === '/today') return 'today'
    if (p.startsWith('/habits')) {
      if (p.startsWith('/habits/categories')) return 'categories'
      return 'habits'
    }
    if (p.startsWith('/templates/habits')) return 'templates'
    if (p === '/calendar') return 'calendar'
    if (p === '/history') return 'history'
    if (p.startsWith('/statistics/heatmap')) return 'heatmap'
    if (p.startsWith('/statistics/weekly')) return 'weekly'
    if (p.startsWith('/statistics')) return 'statistics'
    if (p.startsWith('/achievements')) return 'achievements'
    if (p.startsWith('/reminders')) return 'reminders'
    if (p.startsWith('/community')) return 'community'
    if (p.startsWith('/alumni')) return 'alumni'
    if (p === '/users') return 'users'
    if (p === '/admin') return 'admin'
    if (p === '/about') return 'about'
    if (p === '/profile') return 'profile'
    if (p === '/settings') return 'settings'
    return 'home'
  }
  selectedKeys.value = [resolveKey(path)]
}, { immediate: true })

const navigateTo = (path: string) => {
  router.push(path)
}

const refreshData = () => {
  window.location.reload()
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.sidebar-container {
  background: linear-gradient(180deg, #111111 0%, #1f2937 100%) !important;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
}

.sidebar-container :deep(.ant-layout-sider-children) {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #374151 0%, #111827 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.5px;
}

.nav-section {
  padding: 8px 12px;
}

.sidebar-content {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding-bottom: 8px;
}

.nav-section-title {
  font-size: 11px;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 8px 12px;
  margin-bottom: 4px;
}

.nav-menu {
  background: transparent !important;
  border: none !important;
}

.nav-menu :deep(.ant-menu-item) {
  margin: 4px 0;
  border-radius: 8px;
  height: 44px;
  line-height: 44px;
  color: #9ca3af !important;
}

.nav-menu :deep(.ant-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
  color: #fff !important;
}

.nav-menu :deep(.ant-menu-item-selected) {
  background: linear-gradient(135deg, #374151 0%, #4b5563 100%) !important;
  color: #fff !important;
}

.sidebar-footer {
  margin-top: auto;
  flex-shrink: 0;
  padding: 8px 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.header-container {
  background: #fff !important;
  border-bottom: 1px solid #e5e7eb;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: #374151;
}

.collapse-btn:hover {
  background: #f3f4f6;
  color: #111827;
}

.breadcrumb-nav {
  margin-left: 8px;
}

.breadcrumb-link {
  color: #6b7280;
}

.breadcrumb-link:hover {
  color: #111827;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: #6b7280;
}

.action-btn:hover {
  background: #f3f4f6;
  color: #111827;
}

.user-dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.user-dropdown-trigger:hover {
  background: #f3f4f6;
}

.user-avatar {
  background: linear-gradient(135deg, #374151 0%, #111827 100%) !important;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  line-height: 1.2;
}

.user-role {
  font-size: 12px;
  color: #6b7280;
  line-height: 1.2;
}

.dropdown-arrow {
  color: #9ca3af;
  font-size: 10px;
}

.user-dropdown-menu {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 4px;
}

.user-dropdown-menu :deep(.ant-dropdown-menu-item) {
  border-radius: 6px;
  margin: 2px 0;
}

.logout-item {
  color: #ef4444 !important;
}

.auth-buttons {
  display: flex;
  gap: 8px;
}

.login-btn {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  height: 38px;
  padding: 0 16px;
  color: #374151 !important;
  background: #fff !important;
}

.login-btn:hover {
  border-color: #111827 !important;
  color: #111827 !important;
  background: #f9fafb !important;
}

.register-btn {
  background: #111111 !important;
  border-color: #111111 !important;
  border-radius: 8px;
  height: 38px;
  padding: 0 16px;
  color: #ffffff !important;
}

.register-btn:hover {
  background: #374151 !important;
  border-color: #374151 !important;
  color: #ffffff !important;
}

.content-container {
  padding: 24px;
  background: #f9fafb;
  min-height: calc(100vh - 64px);
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.2s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}
</style>
