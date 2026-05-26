<template>
  <a-layout class="layout">
    <a-layout-sider
      v-model:collapsed="appStore.collapsed"
      :trigger="null"
      collapsible
      :width="220"
      :collapsed-width="60"
      class="sider"
    >
      <div class="logo">
        <span v-if="!appStore.collapsed">社团资金管控</span>
        <span v-else>社</span>
      </div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        v-model:openKeys="openKeys"
        mode="inline"
        :items="menuItems"
        @click="handleMenuClick"
      />
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="header">
        <div class="header-left">
          <MenuFoldOutlined
            v-if="!appStore.collapsed"
            class="trigger"
            @click="appStore.toggleSidebar"
          />
          <MenuUnfoldOutlined
            v-else
            class="trigger"
            @click="appStore.toggleSidebar"
          />
          <a-breadcrumb class="breadcrumb">
            <a-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.title }}
            </a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        <div class="header-right">
          <a-badge :count="appStore.unreadCount" :offset="[-5, 5]">
            <BellOutlined class="header-icon" @click="router.push('/member/notification')" />
          </a-badge>
          <a-dropdown>
            <div class="user-info">
              <a-avatar :size="32" :src="userStore.user?.avatar">
                {{ userStore.user?.realName?.charAt(0) }}
              </a-avatar>
              <span class="username">{{ userStore.user?.realName }}</span>
              <DownOutlined />
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile" @click="router.push('/member/profile')">
                  <UserOutlined /> 个人中心
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout">
                  <LogoutOutlined /> 退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, watch, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore, useAppStore } from '@/stores'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  UserOutlined,
  TeamOutlined,
  WalletOutlined,
  AuditOutlined,
  SettingOutlined,
  BellOutlined,
  DownOutlined,
  LogoutOutlined,
  FileTextOutlined,
  AlertOutlined,
  BarChartOutlined,
  FundOutlined,
  UnorderedListOutlined
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const selectedKeys = ref<string[]>([])
const openKeys = ref<string[]>([])

const menuItems = computed(() => {
  const role = userStore.userRole
  const items: any[] = [
    {
      key: '/dashboard',
      icon: () => h(DashboardOutlined),
      label: '仪表盘'
    }
  ]

  if (role === 'member') {
    items.push({
      key: '/member',
      icon: () => h(UserOutlined),
      label: '个人中心',
      children: [
        { key: '/member/profile', label: '个人信息' },
        { key: '/member/fund', label: '财务查看' },
        { key: '/member/activity', label: '活动列表' },
        { key: '/member/activity/calendar', label: '活动日历' },
        { key: '/member/activity/my-signups', label: '我的报名' },
        { key: '/member/notification', label: '通知中心' }
      ]
    })
  }

  if (role === 'president') {
    items.push({
      key: '/member',
      icon: () => h(UserOutlined),
      label: '个人中心',
      children: [
        { key: '/member/profile', label: '个人信息' },
        { key: '/member/fund', label: '财务查看' },
        { key: '/member/apply-track', label: '我的申请' },
        { key: '/member/activity', label: '活动列表' },
        { key: '/member/activity/calendar', label: '活动日历' },
        { key: '/member/activity/my-signups', label: '我的报名' },
        { key: '/member/notification', label: '通知中心' }
      ]
    })
  }

  if (role === 'teacher') {
    items.push({
      key: '/member',
      icon: () => h(UserOutlined),
      label: '个人中心',
      children: [
        { key: '/member/profile', label: '个人信息' },
        { key: '/member/notification', label: '通知中心' }
      ]
    })
  }

  if (role === 'admin') {
    items.push({
      key: '/member',
      icon: () => h(UserOutlined),
      label: '个人中心',
      children: [
        { key: '/member/profile', label: '个人信息' },
        { key: '/member/fund', label: '财务查看' },
        { key: '/member/apply-track', label: '我的申请' },
        { key: '/member/activity', label: '活动列表' },
        { key: '/member/activity/calendar', label: '活动日历' },
        { key: '/member/activity/my-signups', label: '我的报名' },
        { key: '/member/notification', label: '通知中心' }
      ]
    })
  }

  if (role === 'president') {
    items.push({
      key: '/president',
      icon: () => h(TeamOutlined),
      label: '社长管理',
      children: [
        { key: '/president/club', label: '社团管理' },
        { key: '/president/member', label: '成员管理' },
        { key: '/president/fund/apply', label: '资金申请' },
        { key: '/president/fund/review', label: '资金审核' },
        { key: '/president/finance', label: '财务报表' },
        { key: '/president/income', label: '资金入账' },
        { key: '/president/activity/publish', label: '活动发布' },
        { key: '/president/activity/stats', label: '活动统计' }
      ]
    })
  }

  if (role === 'admin') {
    items.push({
      key: '/president',
      icon: () => h(TeamOutlined),
      label: '社长管理',
      children: [
        { key: '/president/club', label: '社团管理' },
        { key: '/president/member', label: '成员管理' },
        { key: '/president/fund/apply', label: '资金申请' },
        { key: '/president/fund/review', label: '资金审核' },
        { key: '/president/finance', label: '财务报表' },
        { key: '/president/income', label: '资金入账' },
        { key: '/president/activity/publish', label: '活动发布' },
        { key: '/president/activity/stats', label: '活动统计' }
      ]
    })
  }

  if (role === 'teacher') {
    items.push({
      key: '/teacher',
      icon: () => h(AuditOutlined),
      label: '指导老师',
      children: [
        { key: '/teacher/clubs', label: '社团监管' },
        { key: '/teacher/approval', label: '审批列表' },
        { key: '/teacher/warning', label: '风险预警' },
        { key: '/teacher/activity/review', label: '活动审核' },
        { key: '/teacher/activity/ledger', label: '活动台账' }
      ]
    })
  }

  if (role === 'admin') {
    items.push({
      key: '/teacher',
      icon: () => h(AuditOutlined),
      label: '指导老师',
      children: [
        { key: '/teacher/clubs', label: '社团监管' },
        { key: '/teacher/approval', label: '审批列表' },
        { key: '/teacher/warning', label: '风险预警' },
        { key: '/teacher/activity/review', label: '活动审核' },
        { key: '/teacher/activity/ledger', label: '活动台账' }
      ]
    })
  }

  if (role === 'admin') {
    items.push({
      key: '/admin',
      icon: () => h(SettingOutlined),
      label: '系统管理',
      children: [
        { key: '/admin/system', label: '系统配置' },
        { key: '/admin/user', label: '用户管理' },
        { key: '/admin/club', label: '社团管理' },
        { key: '/admin/monitor', label: '数据监控' },
        { key: '/admin/log', label: '日志管理' },
        { key: '/admin/operation', label: '运营总览' }
      ]
    })
  }

  return items
})

const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta.title)
  return matched.map(item => ({
    path: item.path,
    title: item.meta.title as string
  }))
})

watch(
  () => route.path,
  (path) => {
    selectedKeys.value = [path]
    const parentPath = '/' + path.split('/')[1]
    if (parentPath !== path) {
      openKeys.value = [parentPath]
    }
  },
  { immediate: true }
)

const handleMenuClick = ({ key }: { key: string }) => {
  router.push(key)
}

const handleLogout = () => {
  userStore.logout()
}

appStore.fetchUnreadCount()
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.sider {
  background: #1a1a1a;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.sider :deep(.ant-menu) {
  background: transparent;
  color: rgba(255, 255, 255, 0.85);
  border: none;
}

.sider :deep(.ant-menu-item),
.sider :deep(.ant-menu-submenu-title) {
  color: rgba(255, 255, 255, 0.85);
  margin: 4px 0;
}

.sider :deep(.ant-menu-item:hover),
.sider :deep(.ant-menu-submenu-title:hover) {
  background: rgba(255, 255, 255, 0.1);
}

.sider :deep(.ant-menu-item-selected) {
  background: #333 !important;
  color: #fff;
}

.sider :deep(.ant-menu-sub) {
  background: #222;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header {
  background: #fff;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.trigger {
  font-size: 18px;
  cursor: pointer;
  color: #666;
  transition: color 0.3s;
}

.trigger:hover {
  color: #1a1a1a;
}

.breadcrumb {
  margin-left: 8px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.header-icon {
  font-size: 18px;
  color: #666;
  cursor: pointer;
  transition: color 0.3s;
}

.header-icon:hover {
  color: #1a1a1a;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.3s;
}

.user-info:hover {
  background: #f5f5f5;
}

.username {
  color: #333;
  font-size: 14px;
}

.content {
  margin: 0;
  min-height: calc(100vh - 64px);
  background: #fafafa;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
