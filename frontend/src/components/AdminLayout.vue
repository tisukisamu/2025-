<template>
  <a-layout class="admin-layout">
    <a-layout-sider
      v-if="!isMobile"
      width="240"
      class="admin-sider"
    >
      <div class="sider-brand" @click="router.push('/admin')">
        <control-outlined class="brand-icon" />
        <span>管理中心</span>
      </div>
      <a-menu
        mode="inline"
        :selectedKeys="[currentMenu]"
        class="admin-menu"
      >
        <a-menu-item key="/admin" @click="router.push('/admin')">
          <dashboard-outlined />
          <span>总览</span>
        </a-menu-item>
        <a-menu-item key="/admin/users" @click="router.push('/admin/users')">
          <team-outlined />
          <span>用户管理</span>
        </a-menu-item>
        <a-menu-item key="/admin/products" @click="router.push('/admin/products')">
          <appstore-outlined />
          <span>商品管理</span>
        </a-menu-item>
        <a-menu-item key="/admin/orders" @click="router.push('/admin/orders')">
          <shopping-outlined />
          <span>订单管理</span>
        </a-menu-item>
        <a-menu-item key="/admin/reports" @click="router.push('/admin/reports')">
          <alert-outlined />
          <span>举报管理</span>
        </a-menu-item>
        <a-menu-item key="/admin/announcements" @click="router.push('/admin/announcements')">
          <notification-outlined />
          <span>公告管理</span>
        </a-menu-item>
        <a-menu-item key="/admin/feedbacks" @click="router.push('/admin/feedbacks')">
          <message-outlined />
          <span>反馈管理</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>

    <a-layout class="admin-main">
      <a-layout-header class="admin-header">
        <div class="admin-header-left">
          <a-button v-if="isMobile" type="text" @click="drawerOpen = true">
            <menu-outlined />
          </a-button>
          <span class="admin-title">{{ pageTitle }}</span>
        </div>
        <a-space>
          <a-button @click="router.push('/')">返回前台</a-button>
          <a-button type="primary" @click="handleLogout">退出登录</a-button>
        </a-space>
      </a-layout-header>
      <a-layout-content class="admin-content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>

  <a-drawer v-model:open="drawerOpen" title="管理导航" placement="left" width="240">
    <a-menu mode="inline" :selectedKeys="[currentMenu]">
      <a-menu-item key="/admin" @click="go('/admin')">
        <dashboard-outlined />
        <span>总览</span>
      </a-menu-item>
      <a-menu-item key="/admin/users" @click="go('/admin/users')">
        <team-outlined />
        <span>用户管理</span>
      </a-menu-item>
      <a-menu-item key="/admin/products" @click="go('/admin/products')">
        <appstore-outlined />
        <span>商品管理</span>
      </a-menu-item>
      <a-menu-item key="/admin/orders" @click="go('/admin/orders')">
        <shopping-outlined />
        <span>订单管理</span>
      </a-menu-item>
      <a-menu-item key="/admin/reports" @click="go('/admin/reports')">
        <alert-outlined />
        <span>举报管理</span>
      </a-menu-item>
      <a-menu-item key="/admin/announcements" @click="go('/admin/announcements')">
        <notification-outlined />
        <span>公告管理</span>
      </a-menu-item>
      <a-menu-item key="/admin/feedbacks" @click="go('/admin/feedbacks')">
        <message-outlined />
        <span>反馈管理</span>
      </a-menu-item>
    </a-menu>
  </a-drawer>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  AppstoreOutlined,
  AlertOutlined,
  ControlOutlined,
  DashboardOutlined,
  MenuOutlined,
  MessageOutlined,
  NotificationOutlined,
  ShoppingOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const drawerOpen = ref(false)
const windowWidth = ref(window.innerWidth)
const isMobile = computed(() => windowWidth.value < 992)
const currentMenu = computed(() => route.path)
const pageTitle = computed(() => (route.meta.title as string) || '管理后台')

const go = (path: string) => {
  drawerOpen.value = false
  router.push(path)
}

const handleLogout = () => {
  userStore.logout()
  drawerOpen.value = false
  router.push('/login')
}

const handleResize = () => {
  windowWidth.value = window.innerWidth
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: #f4f4f5;
}

.admin-sider {
  background: #18181b;
  padding: 20px 14px;
  border-right: 1px solid #27272a;
}

.sider-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #ffffff;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 24px;
  padding: 12px 10px;
  cursor: pointer;
}

.brand-icon {
  color: #ffffff;
  font-size: 24px;
}

.admin-menu {
  border-inline-end: none;
  background: transparent;
}

.admin-menu :deep(.ant-menu-item) {
  color: #a1a1aa;
  margin-bottom: 4px;
  height: 44px;
  line-height: 44px;
  border-radius: 8px;
}

.admin-menu :deep(.ant-menu-item:hover) {
  color: #ffffff;
  background: #27272a;
}

.admin-menu :deep(.ant-menu-item-selected) {
  background: #ffffff;
  color: #18181b;
  font-weight: 500;
}

.admin-main {
  background: transparent;
}

.admin-header {
  height: 64px;
  line-height: 64px;
  padding: 0 24px;
  margin: 0;
  border-radius: 0;
  background: #ffffff;
  border-bottom: 1px solid #e4e4e7;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.admin-header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.admin-title {
  font-size: 18px;
  font-weight: 600;
  color: #18181b;
}

.admin-content {
  padding: 24px;
  max-width: 1600px;
  margin: 0 auto;
  width: 100%;
}

@media (max-width: 991px) {
  .admin-header {
    margin: 0;
    padding: 0 16px;
  }

  .admin-content {
    padding: 16px;
  }
}
</style>
