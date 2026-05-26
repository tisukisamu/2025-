<template>
  <a-layout class="layout-shell">
    <a-layout-header class="layout-header">
      <div class="layout-header-inner">
        <div class="brand" @click="navigateTo('/')">
          <div class="brand-logo">
            <span>R</span>
          </div>
          <div class="brand-text">
            <span class="brand-title">招聘管理系统</span>
            <span class="brand-subtitle">Recruitment Platform</span>
          </div>
        </div>

        <a-menu mode="horizontal" :selectedKeys="[selectedKey]" class="main-nav">
          <a-menu-item key="home" @click="navigateTo('/')">
            <home-outlined />
            <span>首页</span>
          </a-menu-item>
          <a-menu-item key="jobs" @click="navigateTo('/jobs')">
            <search-outlined />
            <span>职位搜索</span>
          </a-menu-item>
          <a-menu-item v-if="userStore.isLoggedIn && !userStore.isAdmin" key="resumes" @click="navigateTo('/resumes')">
            <file-text-outlined />
            <span>我的简历</span>
          </a-menu-item>
          <a-menu-item v-if="userStore.isLoggedIn && !userStore.isAdmin" key="applications" @click="navigateTo('/applications')">
            <send-outlined />
            <span>投递记录</span>
          </a-menu-item>
          <a-menu-item v-if="userStore.isLoggedIn" key="favorites" @click="navigateTo('/favorites')">
            <star-outlined />
            <span>我的收藏</span>
          </a-menu-item>
          <a-menu-item v-if="canUseCompanyModules" key="company" @click="navigateTo('/company')">
            <bank-outlined />
            <span>企业管理</span>
          </a-menu-item>
          <a-menu-item v-if="userStore.isLoggedIn && !userStore.isAdmin && !canUseCompanyModules" key="company-apply" @click="navigateTo('/company/apply')">
            <bank-outlined />
            <span>{{ hasCompany ? '入驻进度' : '企业入驻' }}</span>
          </a-menu-item>
          <a-sub-menu v-if="canUseCompanyModules" key="company-module">
            <template #icon>
              <team-outlined />
            </template>
            <template #title>企业模块</template>
            <a-menu-item key="company-profile" @click="navigateTo('/company/profile')">企业资料</a-menu-item>
            <a-menu-item key="company-resumes" @click="navigateTo('/company/resumes')">简历管理</a-menu-item>
            <a-menu-item key="company-interviews" @click="navigateTo('/company/interviews')">面试管理</a-menu-item>
            <a-menu-item key="company-talents" @click="navigateTo('/company/talents')">人才库</a-menu-item>
          </a-sub-menu>
          <a-menu-item v-if="userStore.isAdmin" key="admin" @click="navigateTo('/admin')">
            <setting-outlined />
            <span>系统管理</span>
          </a-menu-item>
          <a-sub-menu v-if="userStore.isAdmin" key="admin-module">
            <template #icon>
              <notification-outlined />
            </template>
            <template #title>管理模块</template>
            <a-menu-item key="admin-users" @click="navigateTo('/admin/users')">用户管理</a-menu-item>
            <a-menu-item key="admin-companies" @click="navigateTo('/admin/companies')">企业审核</a-menu-item>
            <a-menu-item key="admin-jobs" @click="navigateTo('/admin/jobs')">职位监管</a-menu-item>
            <a-menu-item key="admin-announcements" @click="navigateTo('/admin/announcements')">公告管理</a-menu-item>
            <a-menu-item key="admin-statistics" @click="navigateTo('/admin/statistics')">数据统计</a-menu-item>
            <a-menu-item key="admin-complaints" @click="navigateTo('/admin/complaints')">投诉处理</a-menu-item>
          </a-sub-menu>
        </a-menu>

        <div class="header-actions">
          <a-badge v-if="userStore.isLoggedIn" :count="unreadCount" :offset="[-3, 4]">
            <a-button type="text" class="icon-btn" @click="navigateTo('/messages')">
              <bell-outlined />
            </a-button>
          </a-badge>

          <template v-if="userStore.isLoggedIn">
            <a-dropdown>
              <div class="user-trigger">
                <a-avatar :size="30" :src="resolveAvatarUrl(userStore.userInfo?.avatarUrl)" class="user-avatar">
                  {{ userStore.userInfo?.name?.charAt(0) || 'U' }}
                </a-avatar>
              </div>
              <template #overlay>
                <a-menu class="user-menu">
                  <a-menu-item key="profile" @click="navigateTo('/profile')">
                    <user-outlined class="mr-2" />
                    个人中心
                  </a-menu-item>
                  <a-menu-item key="favorites" @click="navigateTo('/favorites')">
                    <star-outlined class="mr-2" />
                    我的收藏
                  </a-menu-item>
                  <a-menu-item key="my-applications" @click="navigateTo('/my-applications')">
                    <send-outlined class="mr-2" />
                    我的投递
                  </a-menu-item>
                  <a-menu-item v-if="!userStore.isAdmin" key="company-apply" @click="navigateTo('/company/apply')">
                    <bank-outlined class="mr-2" />
                    {{ hasCompany ? '企业入驻进度' : '申请企业入驻' }}
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="logout" @click="handleLogout">
                    <logout-outlined class="mr-2" />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>
          <template v-else>
            <a-button class="auth-btn ghost-btn" @click="navigateTo('/login')">
              登录
            </a-button>
            <a-button type="primary" class="auth-btn solid-btn" @click="navigateTo('/register')">
              注册
            </a-button>
          </template>
        </div>
      </div>
    </a-layout-header>

    <a-layout-content class="layout-content">
      <div class="layout-content-inner">
        <router-view />
      </div>
    </a-layout-content>

    <a-layout-footer class="layout-footer">
      <div>© 2024 招聘管理系统</div>
    </a-layout-footer>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { companyApi, messageApi } from '../api'
import { resolveAvatarUrl } from '../utils/media'
import {
  HomeOutlined,
  SearchOutlined,
  FileTextOutlined,
  SendOutlined,
  StarOutlined,
  BankOutlined,
  TeamOutlined,
  SettingOutlined,
  NotificationOutlined,
  BellOutlined,
  UserOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const unreadCount = ref(0)
const hasCompany = ref(false)
const companyStatus = ref('')

const canUseCompanyModules = computed(() => {
  if (userStore.userRole === 'COMPANY') return true
  return hasCompany.value && companyStatus.value === 'APPROVED'
})

const selectedKey = computed(() => {
  const path = route.path
  if (path === '/') return 'home'
  if (path.startsWith('/jobs')) return 'jobs'
  if (path.startsWith('/resumes')) return 'resumes'
  if (path.startsWith('/applications')) return 'applications'
  if (path.startsWith('/my-applications')) return 'applications'
  if (path.startsWith('/favorites')) return 'favorites'
  if (path.startsWith('/messages')) return 'messages'
  if (path.startsWith('/company/apply')) return 'company-apply'
  if (path.startsWith('/company/profile')) return 'company-profile'
  if (path.startsWith('/company/resumes')) return 'company-resumes'
  if (path.startsWith('/company/interviews')) return 'company-interviews'
  if (path.startsWith('/company/talents')) return 'company-talents'
  if (path.startsWith('/company')) return 'company'
  if (path.startsWith('/admin/users')) return 'admin-users'
  if (path.startsWith('/admin/companies')) return 'admin-companies'
  if (path.startsWith('/admin/jobs')) return 'admin-jobs'
  if (path.startsWith('/admin/announcements')) return 'admin-announcements'
  if (path.startsWith('/admin/statistics')) return 'admin-statistics'
  if (path.startsWith('/admin/complaints')) return 'admin-complaints'
  if (path.startsWith('/admin')) return 'admin'
  return 'home'
})

const navigateTo = (path: string) => {
  router.push(path)
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const fetchUnreadCount = async () => {
  if (userStore.isLoggedIn) {
    try {
      const res = await messageApi.getUnreadCount()
      unreadCount.value = res.data
    } catch (error) {
      console.error('获取未读消息数失败', error)
    }
  }
}

const fetchCompanyStatus = async () => {
  if (!userStore.isLoggedIn || userStore.isAdmin) return
  try {
    const res = await companyApi.getMy()
    hasCompany.value = Boolean(res.data?.id)
    companyStatus.value = String(res.data?.status || '')
  } catch (error) {
    hasCompany.value = false
    companyStatus.value = ''
  }
}

onMounted(() => {
  fetchUnreadCount()
  fetchCompanyStatus()
})
</script>

<style scoped>
.layout-shell {
  min-height: 100vh;
  background: linear-gradient(180deg, #ffffff 0%, #f5f5f5 100%);
}

.layout-header {
  height: 78px;
  background: rgba(255, 255, 255, 0.95) !important;
  border-bottom: 1px solid #e5e5e5;
  backdrop-filter: blur(10px);
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.layout-header-inner {
  max-width: 1280px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  gap: 24px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  min-width: 230px;
}

.brand-logo {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: #111111;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 700;
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-title {
  font-size: 15px;
  font-weight: 600;
  color: #111111;
}

.brand-subtitle {
  font-size: 11px;
  color: #9a9a9a;
}

.main-nav {
  flex: 1;
  border-bottom: none;
  background: transparent !important;
  color: #595959;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon-btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  color: #595959;
}

.icon-btn:hover {
  background: #f1f1f1 !important;
  color: #111111 !important;
}

.user-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 999px;
  cursor: pointer;
  background: transparent;
}

.user-trigger:hover {
  background: #f3f3f3;
}

.user-avatar {
  background: #1f1f1f !important;
}

.auth-btn {
  height: 36px;
  border-radius: 10px;
  font-size: 13px;
  padding: 0 14px;
}

.ghost-btn {
  border-color: #d9d9d9;
  color: #262626;
}

.ghost-btn:hover {
  border-color: #111111;
  color: #111111;
}

.solid-btn {
  background: #111111;
  border-color: #111111;
}

.solid-btn:hover {
  background: #262626 !important;
  border-color: #262626 !important;
}

.layout-content {
  padding: 28px 24px 20px;
}

.layout-content-inner {
  max-width: 1280px;
  margin: 0 auto;
}

.layout-footer {
  background: transparent !important;
  color: #8c8c8c !important;
  text-align: center;
  padding: 24px;
  border-top: 1px solid #ececec;
  font-size: 12px;
}

:deep(.main-nav .ant-menu-item) {
  border-radius: 10px;
  margin-inline: 4px !important;
  padding-inline: 12px !important;
}

:deep(.main-nav .ant-menu-item-selected) {
  background: #111111 !important;
  color: #ffffff !important;
}

:deep(.main-nav .ant-menu-item:hover) {
  color: #111111 !important;
}

:deep(.main-nav.ant-menu-horizontal > .ant-menu-item::after) {
  border-bottom: none !important;
}

:deep(.user-menu .ant-dropdown-menu-item) {
  border-radius: 8px;
}
</style>
