<template>
  <a-layout class="layout-shell">
    <a-layout-sider
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      width="252"
      class="layout-sider"
    >
      <div class="brand-wrap">
        <span class="brand-mark">街</span>
        <div v-if="!collapsed" class="brand-text">
          <div class="brand-title">街舞工作室</div>
          <div class="brand-subtitle">Studio Console</div>
        </div>
      </div>

      <a-menu
        v-model:selectedKeys="selectedKeys"
        v-model:openKeys="openKeys"
        mode="inline"
        class="menu-panel"
        @click="handleMenuClick"
      >
        <template v-if="userStore.isAdmin">
          <a-menu-item key="/admin">
            <DashboardOutlined />
            <span>运营总览</span>
          </a-menu-item>

          <a-sub-menu key="admin-members">
            <template #icon><TeamOutlined /></template>
            <template #title>成员管理</template>
            <a-menu-item key="/admin/users">用户管理</a-menu-item>
            <a-menu-item key="/admin/teachers">教师管理</a-menu-item>
            <a-menu-item key="/admin/students">学员管理</a-menu-item>
          </a-sub-menu>

          <a-sub-menu key="admin-teaching">
            <template #icon><BookOutlined /></template>
            <template #title>教学中心</template>
            <a-menu-item key="/admin/courses">课程管理</a-menu-item>
            <a-menu-item key="/admin/schedules">排课管理</a-menu-item>
            <a-menu-item key="/admin/calendar">排课日历</a-menu-item>
            <a-menu-item key="/admin/enrollments">报名管理</a-menu-item>
          </a-sub-menu>

          <a-sub-menu key="admin-finance">
            <template #icon><AccountBookOutlined /></template>
            <template #title>财务中心</template>
            <a-menu-item key="/admin/payments">支付管理</a-menu-item>
            <a-menu-item key="/admin/bills">账单管理</a-menu-item>
            <a-menu-item key="/admin/payment-history">支付历史</a-menu-item>
          </a-sub-menu>

          <a-menu-item key="/admin/statistics">
            <BarChartOutlined />
            <span>统计报表</span>
          </a-menu-item>

          <a-menu-item key="/admin/notifications">
            <BellOutlined />
            <span>通知管理</span>
          </a-menu-item>

          <a-menu-item key="/admin/settings">
            <SettingOutlined />
            <span>系统设置</span>
          </a-menu-item>

          <a-sub-menu key="admin-role-features">
            <template #icon><ReadOutlined /></template>
            <template #title>角色功能</template>
            <a-menu-item key="/teacher">教师工作台</a-menu-item>
            <a-menu-item key="/teacher/courses">教师我的课程</a-menu-item>
            <a-menu-item key="/teacher/schedules">教师我的排课</a-menu-item>
            <a-menu-item key="/teacher/students">教师学员管理</a-menu-item>
            <a-menu-item key="/student">学员学习首页</a-menu-item>
            <a-menu-item key="/student/courses">学员课程浏览</a-menu-item>
            <a-menu-item key="/student/my-courses">学员我的课程</a-menu-item>
            <a-menu-item key="/student/finance">学员支付与账单</a-menu-item>
            <a-menu-item key="/student/profile">学员个人中心</a-menu-item>
          </a-sub-menu>
        </template>

        <template v-else-if="userStore.isTeacher">
          <a-menu-item key="/teacher">
            <DashboardOutlined />
            <span>工作台</span>
          </a-menu-item>
          <a-menu-item key="/teacher/courses">
            <BookOutlined />
            <span>我的课程</span>
          </a-menu-item>
          <a-menu-item key="/teacher/schedules">
            <CalendarOutlined />
            <span>我的排课</span>
          </a-menu-item>
          <a-menu-item key="/teacher/students">
            <ReadOutlined />
            <span>学员管理</span>
          </a-menu-item>
        </template>

        <template v-else>
          <a-menu-item key="/student">
            <DashboardOutlined />
            <span>学习首页</span>
          </a-menu-item>
          <a-menu-item key="/student/courses">
            <BookOutlined />
            <span>课程浏览</span>
          </a-menu-item>
          <a-menu-item key="/student/my-courses">
            <SolutionOutlined />
            <span>我的课程</span>
          </a-menu-item>
          <a-menu-item key="/student/finance">
            <AccountBookOutlined />
            <span>支付与账单</span>
          </a-menu-item>
          <a-menu-item key="/student/profile">
            <UserOutlined />
            <span>个人中心</span>
          </a-menu-item>
        </template>
      </a-menu>
    </a-layout-sider>

    <a-layout>
      <a-layout-header class="layout-header">
        <div class="header-left">
          <MenuUnfoldOutlined v-if="collapsed" class="trigger-icon" @click="collapsed = !collapsed" />
          <MenuFoldOutlined v-else class="trigger-icon" @click="collapsed = !collapsed" />
          <span class="header-title">{{ currentTitle }}</span>
        </div>

        <div class="header-right">
          <a-badge :count="notificationCount" :number-style="{ backgroundColor: '#111111' }">
            <BellOutlined class="header-action" @click="openNotificationCenter" />
          </a-badge>

          <a-dropdown>
            <div class="user-entry">
              <a-avatar :src="userStore.userInfo?.avatar" class="user-avatar">
                {{ userStore.realName?.charAt(0) || userStore.username?.charAt(0) }}
              </a-avatar>
              <span class="user-name">{{ userStore.realName || userStore.username || '访客' }}</span>
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile" @click="goProfile">
                  <UserOutlined />
                  个人中心
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>

      <a-layout-content class="layout-content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  TeamOutlined,
  UserOutlined,
  BookOutlined,
  SolutionOutlined,
  ReadOutlined,
  AccountBookOutlined,
  BarChartOutlined,
  SettingOutlined,
  BellOutlined,
  LogoutOutlined,
  CalendarOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const collapsed = ref(false)
const selectedKeys = ref([route.path])
const openKeys = ref<string[]>([])

const routeTitleMap: Record<string, string> = {
  '/admin': '运营总览',
  '/admin/users': '用户管理',
  '/admin/teachers': '教师管理',
  '/admin/students': '学员管理',
  '/admin/courses': '课程管理',
  '/admin/schedules': '排课管理',
  '/admin/calendar': '排课日历',
  '/admin/enrollments': '报名管理',
  '/admin/payments': '支付管理',
  '/admin/bills': '账单管理',
  '/admin/payment-history': '支付历史',
  '/admin/statistics': '统计报表',
  '/admin/notifications': '通知管理',
  '/admin/settings': '系统设置',
  '/teacher': '工作台',
  '/teacher/courses': '我的课程',
  '/teacher/schedules': '我的排课',
  '/teacher/students': '学员管理',
  '/student': '学习首页',
  '/student/courses': '课程浏览',
  '/student/my-courses': '我的课程',
  '/student/finance': '支付与账单',
  '/student/profile': '个人中心'
}

const notificationCount = computed(() => (userStore.isAdmin ? 8 : userStore.isTeacher ? 3 : 2))
const currentTitle = computed(() => routeTitleMap[route.path] || '街舞工作室')

const syncMenuState = (path: string) => {
  let activeKey = path
  if (/^\/admin\/courses\/\d+/.test(path)) activeKey = '/admin/courses'
  if (/^\/admin\/teachers\/\d+/.test(path)) activeKey = '/admin/teachers'
  if (/^\/admin\/students\/\d+/.test(path)) activeKey = '/admin/students'
  selectedKeys.value = [activeKey]
  if (path.startsWith('/admin/users') || path.startsWith('/admin/teachers') || path.startsWith('/admin/students')) {
    openKeys.value = ['admin-members']
    return
  }
  if (
    path.startsWith('/admin/courses') ||
    path.startsWith('/admin/schedules') ||
    path.startsWith('/admin/calendar') ||
    path.startsWith('/admin/enrollments')
  ) {
    openKeys.value = ['admin-teaching']
    return
  }
  if (path.startsWith('/admin/payments') || path.startsWith('/admin/bills') || path.startsWith('/admin/payment-history')) {
    openKeys.value = ['admin-finance']
    return
  }
  if (path.startsWith('/teacher') || path.startsWith('/student')) {
    if (userStore.isAdmin) {
      openKeys.value = ['admin-role-features']
      return
    }
  }
  openKeys.value = []
}

watch(
  () => route.path,
  (path) => syncMenuState(path),
  { immediate: true }
)

const handleMenuClick = ({ key }: { key: string }) => {
  if (key !== route.path) {
    router.push(key)
  }
}

const goProfile = () => {
  if (userStore.isStudent) {
    router.push('/student/profile')
    return
  }
  router.push('/profile')
}

const openNotificationCenter = () => {
  if (userStore.isAdmin) {
    router.push('/admin/notifications')
    return
  }
  message.info('当前角色暂无专属通知中心')
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-shell {
  min-height: 100vh;
}

.layout-sider {
  border-right: 1px solid #e8e8e8;
  background: #ffffff;
}

.brand-wrap {
  height: 68px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 12px;
}

.brand-mark {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: #111111;
  color: #ffffff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.brand-title {
  font-size: 16px;
  font-weight: 600;
  line-height: 1.2;
}

.brand-subtitle {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 2px;
}

.menu-panel {
  border-right: 0;
  padding-top: 8px;
}

.layout-header {
  height: 68px;
  padding: 0 20px;
  border-bottom: 1px solid #e8e8e8;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 14px;
}

.trigger-icon {
  font-size: 18px;
  color: #262626;
  cursor: pointer;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
}

.header-action {
  font-size: 18px;
  color: #262626;
  cursor: pointer;
}

.user-entry {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-avatar {
  background: #111111;
}

.user-name {
  color: #262626;
  font-weight: 500;
}

.layout-content {
  padding: 16px;
  background: #f5f5f5;
}

:deep(.ant-layout-sider-trigger) {
  background: #111111;
}
</style>
