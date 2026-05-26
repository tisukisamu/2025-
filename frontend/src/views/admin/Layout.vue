<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../stores/user'
import {
  DashboardOutlined,
  ShoppingOutlined,
  FileTextOutlined,
  TeamOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  BellOutlined,
  SettingOutlined,
  SearchOutlined,
  DownOutlined,
  HomeOutlined,
  ShopOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const collapsed = ref(false)
const mobileMenuOpen = ref(false)
const screenWidth = ref(window.innerWidth)
const userDropdownOpen = ref(false)

// 菜单配置
const menuItems = [
  { key: '/admin/dashboard', icon: DashboardOutlined, label: '仪表盘', badge: null },
  { key: '/admin/products', icon: ShoppingOutlined, label: '商品管理', badge: null },
  { key: '/admin/store-audit', icon: ShopOutlined, label: '店铺审核', badge: null },
  { key: '/admin/orders', icon: FileTextOutlined, label: '订单中心', badge: 5 },
  { key: '/admin/users', icon: TeamOutlined, label: '客户管理', badge: null }
]

const activeMenu = computed(() => route.path)

const handleResize = () => {
  screenWidth.value = window.innerWidth
  if (screenWidth.value < 1024) collapsed.value = true
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
  handleResize()
})

onUnmounted(() => window.removeEventListener('resize', handleResize))

const logout = () => {
  userStore.logout()
  router.push('/login')
}

const goHome = () => router.push('/')

const breadcrumbs = computed(() => {
  const paths = route.path.split('/').filter(Boolean)
  const items = [{ label: '首页', path: '/admin/dashboard' }]
  if (paths[1]) {
    const menuItem = menuItems.find((m) => m.key === route.path)
    if (menuItem) items.push({ label: menuItem.label, path: route.path })
  }
  return items
})
</script>

<template>
  <div class="admin-layout">
    <div v-if="mobileMenuOpen" class="mobile-overlay" @click="mobileMenuOpen = false"></div>

    <!-- ✅ 侧边栏：白色主题 -->
    <aside
      class="sidebar sidebar-light"
      :class="{
        'sidebar-collapsed': collapsed,
        'sidebar-mobile-open': mobileMenuOpen
      }"
    >
      <!-- Logo -->
      <div class="sidebar-header">
        <div v-if="!collapsed" class="logo" :class="{ 'logo-collapsed': collapsed }">
          <div class="logo-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path
                d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>

          <!-- ✅ 收缩时：名字隐藏 -->
          <span  class="logo-text">AgriAdmin</span>
        </div>

        <button
          class="collapse-btn"
          @click="collapsed = !collapsed"
          :title="collapsed ? '展开菜单' : '收起菜单'"
        >
          <MenuFoldOutlined v-if="!collapsed" />
          <MenuUnfoldOutlined v-else />
        </button>
      </div>

      <!-- 菜单 -->
      <nav class="sidebar-nav">
        <router-link
          v-for="item in menuItems"
          :key="item.key"
          :to="item.key"
          class="nav-item"
          :class="{
            'nav-item-active': activeMenu === item.key,
            'nav-item-collapsed': collapsed
          }"
          @click="mobileMenuOpen = false"
        >
          <div class="nav-icon">
            <component :is="item.icon" />
            <span v-if="item.badge" class="nav-badge">{{ item.badge }}</span>
          </div>

          <!-- ✅ 收缩时：标签隐藏 -->
          <span v-if="!collapsed" class="nav-label">{{ item.label }}</span>
        </router-link>
      </nav>

      <!-- 底部用户区 -->
      <div class="sidebar-footer">
        <div class="user-menu" :class="{ 'user-menu-collapsed': collapsed }">
          <div class="user-info" @click="!collapsed && (userDropdownOpen = !userDropdownOpen)">
            <div class="user-avatar">
              <span>{{ userStore.username?.charAt(0).toUpperCase() || 'U' }}</span>
            </div>

            <div v-if="!collapsed" class="user-details">
              <span class="user-name">{{ userStore.username || '用户' }}</span>
              <span class="user-role">管理员</span>
            </div>

            <DownOutlined
              v-if="!collapsed"
              class="dropdown-icon"
              :class="{ 'dropdown-open': userDropdownOpen }"
            />
          </div>

          <div v-if="!collapsed && userDropdownOpen" class="user-dropdown">
            <button class="dropdown-item" @click="router.push('/admin/profile'); userDropdownOpen = false">
              <UserOutlined />
              <span>个人中心</span>
            </button>
            <button class="dropdown-item" @click="router.push('/admin/settings'); userDropdownOpen = false">
              <SettingOutlined />
              <span>系统设置</span>
            </button>
            <div class="dropdown-divider"></div>
            <button class="dropdown-item dropdown-item-danger" @click="logout">
              <LogoutOutlined />
              <span>退出登录</span>
            </button>
          </div>
        </div>

        <button v-if="collapsed" class="logout-btn-collapsed" @click="logout" title="退出登录">
          <LogoutOutlined />
        </button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="main-wrapper">
      <header class="top-header">
        <div class="header-left">
          <button class="back-home-btn" @click="goHome" title="返回主页">
            <HomeOutlined />
            <span>返回主页</span>
          </button>

          <button class="mobile-menu-btn" @click="mobileMenuOpen = true">
            <MenuUnfoldOutlined />
          </button>

          <nav class="breadcrumb">
            <span v-for="(item, index) in breadcrumbs" :key="index" class="breadcrumb-item">
              <router-link v-if="index < breadcrumbs.length - 1" :to="item.path">
                {{ item.label }}
              </router-link>
              <span v-else class="breadcrumb-current">{{ item.label }}</span>
              <span v-if="index < breadcrumbs.length - 1" class="breadcrumb-separator">/</span>
            </span>
          </nav>
        </div>

        <div class="header-right">
          <div class="header-search">
            <SearchOutlined class="search-icon" />
            <input type="text" placeholder="全局搜索..." class="search-input" />
          </div>

          <button class="header-btn" title="通知">
            <BellOutlined />
            <span class="notification-dot"></span>
          </button>

          <button class="header-btn" title="设置">
            <SettingOutlined />
          </button>
        </div>
      </header>

      <main class="main-content">
        <div class="content-wrapper">
          <router-view />
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
@import '../../styles/design-system.css';

.admin-layout {
  display: flex;
  height: 100vh;
  background: var(--gray-50);
  font-family: var(--font-sans);
}

/* ✅ 侧边栏：白色背景 */
.sidebar {
  width: 260px;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  transition: width 0.3s ease, transform 0.3s ease;
  z-index: var(--z-sticky);
  position: relative;
}

.sidebar-light {
  background: #ffffff;
  border-right: 1px solid var(--gray-200);
}

.sidebar-collapsed {
  width: 72px;
}

.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 14px;
  border-bottom: 1px solid var(--gray-200);
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  overflow: hidden;
  min-width: 0;
}

.logo-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--primary-500), var(--primary-600));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo-icon svg {
  width: 20px;
  height: 20px;
}

.logo-text {
  color: var(--gray-900);
  font-size: 16px;
  font-weight: 900;
  letter-spacing: -0.2px;
  white-space: nowrap;
}

.collapse-btn {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  border: 1px solid var(--gray-200);
  background: #fff;
  color: var(--gray-700);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s;
}

.collapse-btn:hover {
  background: var(--gray-50);
  border-color: var(--gray-300);
}

/* 菜单 */
.sidebar-nav {
  flex: 1;
  padding: 12px 10px;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 12px;
  margin-bottom: 6px;
  border-radius: 12px;
  text-decoration: none;
  color: var(--gray-700);
  transition: background 0.15s ease, color 0.15s ease;
  position: relative;
}

/* ✅ hover：浅灰 */
.nav-item:hover {
  background: var(--gray-100);
  color: var(--gray-900);
}

/* ✅ 选中：黑底白字 */
.nav-item-active {
  background: #111827;
  color: #ffffff;
}

.nav-item-active:hover {
  background: #0b1220;
  color: #ffffff;
}

/* ✅ 收缩时：居中图标，减少左右 padding */
.nav-item-collapsed {
  justify-content: center;
  padding: 11px 0;
}

.nav-icon {
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.nav-badge {
  position: absolute;
  top: -6px;
  right: -8px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: var(--error-500);
  color: #fff;
  font-size: 11px;
  font-weight: 800;
  border-radius: 999px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-label {
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
}

/* 底部用户区（白色侧边栏适配） */
.sidebar-footer {
  padding: 12px 10px;
  border-top: 1px solid var(--gray-200);
}

.user-menu {
  position: relative;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.15s;
}

.user-info:hover {
  background: var(--gray-100);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--primary-500), var(--primary-600));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  flex-shrink: 0;
}

.user-details {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.user-name {
  color: var(--gray-900);
  font-size: 14px;
  font-weight: 700;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  color: var(--gray-500);
  font-size: 12px;
}

.dropdown-icon {
  color: var(--gray-500);
  font-size: 12px;
  transition: transform 0.2s;
}

.dropdown-open {
  transform: rotate(180deg);
}

.user-dropdown {
  position: absolute;
  bottom: calc(100% + 8px);
  left: 0;
  right: 0;
  background: #ffffff;
  border: 1px solid var(--gray-200);
  border-radius: 14px;
  box-shadow: var(--shadow-lg);
  padding: 8px;
  z-index: 100;
}

.dropdown-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 10px;
  border: none;
  background: transparent;
  color: var(--gray-700);
  cursor: pointer;
  transition: background 0.15s;
}

.dropdown-item:hover {
  background: var(--gray-100);
}

.dropdown-item-danger {
  color: var(--error-600);
}

.dropdown-item-danger:hover {
  background: var(--error-50);
}

.dropdown-divider {
  height: 1px;
  background: var(--gray-200);
  margin: 8px 0;
}

.user-menu-collapsed .user-info {
  justify-content: center;
  padding: 10px;
}

.logout-btn-collapsed {
  width: 100%;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid var(--gray-200);
  background: #fff;
  color: var(--gray-700);
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logout-btn-collapsed:hover {
  background: var(--error-50);
  color: var(--error-600);
  border-color: rgba(239, 68, 68, 0.25);
}

/* 主内容 */
.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
}

/* 顶栏保持白色 */
.top-header {
  height: 64px;
  background: #ffffff;
  border-bottom: 1px solid var(--gray-200);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-home-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 10px;
  border: 1px solid var(--gray-200);
  background: #fff;
  color: var(--gray-700);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s;
}

.back-home-btn:hover {
  background: var(--gray-50);
  border-color: var(--gray-300);
  color: var(--gray-900);
}

.mobile-menu-btn {
  display: none;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: 1px solid var(--gray-200);
  background: #fff;
  color: var(--gray-700);
  cursor: pointer;
  align-items: center;
  justify-content: center;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.breadcrumb-item a {
  color: var(--gray-500);
  text-decoration: none;
}
.breadcrumb-item a:hover {
  color: var(--gray-900);
}

.breadcrumb-current {
  color: var(--gray-900);
  font-weight: 600;
}

.breadcrumb-separator {
  color: var(--gray-400);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-search {
  position: relative;
  width: 280px;
}

.header-search .search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--gray-400);
  font-size: 16px;
}

.header-search .search-input {
  width: 100%;
  padding: 9px 12px 9px 40px;
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  font-size: 14px;
  background: var(--gray-50);
  transition: all 0.15s;
}

.header-search .search-input:focus {
  outline: none;
  border-color: var(--primary-500);
  background: #fff;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.12);
}

.header-btn {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  border: 1px solid var(--gray-200);
  background: #fff;
  color: var(--gray-700);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s;
  position: relative;
}

.header-btn:hover {
  background: var(--gray-50);
  border-color: var(--gray-300);
}

.notification-dot {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 8px;
  height: 8px;
  background: var(--error-500);
  border-radius: 50%;
  border: 2px solid #fff;
}

/* 主内容 */
.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.content-wrapper {
  max-width: 1440px;
  margin: 0 auto;
  animation: fadeIn 0.3s ease-out;
}

/* 移动端遮罩 */
.mobile-overlay {
  display: none;
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 150;
}

/* 响应式 */
@media (max-width: 1024px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    transform: translateX(-100%);
  }

  .sidebar-mobile-open {
    transform: translateX(0);
  }

  .mobile-overlay {
    display: block;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .header-search {
    width: 200px;
  }
}

@media (max-width: 768px) {
  .top-header {
    padding: 0 16px;
  }

  .header-search {
    display: none;
  }

  .back-home-btn span {
    display: none;
  }

  .breadcrumb {
    display: none;
  }

  .main-content {
    padding: 16px;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
