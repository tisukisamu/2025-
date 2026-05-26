<template>
  <a-layout class="main-layout">
    <a-layout-header class="header">
      <div class="header-content">
        <router-link to="/" class="logo">
          <swap-outlined class="logo-icon" />
          <span class="logo-text">校园置换</span>
        </router-link>

        <div class="search-bar" v-if="!isMobile">
          <a-input-search
            v-model:value="searchKeyword"
            placeholder="搜索商品..."
            enter-button
            @search="handleSearch"
            class="search-input"
          />
        </div>

        <a-menu
          v-if="!isMobile"
          mode="horizontal"
          :selectedKeys="[currentRoute]"
          class="nav-menu"
        >
          <a-menu-item key="/">
            <router-link to="/">
              <home-outlined />
              <span>首页</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="/products">
            <router-link to="/products">
              <appstore-outlined />
              <span>商品</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="/buy-requests">
            <router-link to="/buy-requests">
              <shopping-cart-outlined />
              <span>求购</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="/topics">
            <router-link to="/topics">
              <comment-outlined />
              <span>话题</span>
            </router-link>
          </a-menu-item>
          <a-menu-item v-if="userStore.isLoggedIn" key="/publish">
            <router-link to="/publish">
              <plus-outlined />
              <span>发布</span>
            </router-link>
          </a-menu-item>
          <a-menu-item v-if="userStore.isLoggedIn" key="/orders">
            <router-link to="/orders">
              <shopping-outlined />
              <span>订单</span>
            </router-link>
          </a-menu-item>
          <a-menu-item v-if="userStore.isAdmin" key="/admin">
            <router-link to="/admin">
              <setting-outlined />
              <span>管理</span>
            </router-link>
          </a-menu-item>
        </a-menu>

        <div class="header-actions">
          <template v-if="userStore.isLoggedIn">
            <a-badge :count="unreadCount" :offset="[-5, 5]" v-if="!isMobile">
              <router-link to="/messages">
                <a-button type="text" class="action-btn">
                  <message-outlined />
                </a-button>
              </router-link>
            </a-badge>
            
            <a-dropdown v-if="!isMobile">
              <div class="user-info">
                <a-avatar :src="userStore.avatar" :style="{ backgroundColor: userStore.isAdmin ? '#f5222d' : '#1890ff' }">
                  {{ userStore.username.charAt(0).toUpperCase() }}
                </a-avatar>
                <span class="username">{{ userStore.username }}</span>
                <a-tag v-if="userStore.isAdmin" color="red" class="admin-tag">管理员</a-tag>
              </div>
              <template #overlay>
                <a-menu>
                  <a-menu-item key="profile">
                    <router-link to="/profile">
                      <user-outlined />
                      个人中心
                    </router-link>
                  </a-menu-item>
                  <a-menu-item key="credit">
                    <router-link to="/credit">
                      <trophy-outlined />
                      信用中心
                    </router-link>
                  </a-menu-item>
                  <a-menu-item key="favorites">
                    <router-link to="/favorites">
                      <heart-outlined />
                      我的收藏
                    </router-link>
                  </a-menu-item>
                  <a-menu-item key="following">
                    <router-link to="/following">
                      <team-outlined />
                      我的关注
                    </router-link>
                  </a-menu-item>
                  <a-menu-item key="feedback">
                    <router-link to="/feedback">
                      <comment-outlined />
                      意见反馈
                    </router-link>
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="logout" @click="handleLogout">
                    <logout-outlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>

            <a-button v-if="isMobile" type="text" @click="showMobileMenu = true">
              <menu-outlined />
            </a-button>
          </template>
          
          <template v-else>
            <router-link to="/login" v-if="!isMobile">
              <a-button type="primary">登录</a-button>
            </router-link>
            <router-link to="/register" v-if="!isMobile">
              <a-button>注册</a-button>
            </router-link>
            <a-button v-if="isMobile" type="text" @click="showMobileMenu = true">
              <menu-outlined />
            </a-button>
          </template>
        </div>
      </div>
    </a-layout-header>

    <a-drawer
      v-model:open="showMobileMenu"
      placement="right"
      title="菜单"
      class="mobile-drawer"
      @close="showMobileMenu = false"
    >
      <a-menu mode="vertical" :selectedKeys="[currentRoute]">
        <a-menu-item key="/" @click="handleMobileNav('/')">
          <router-link to="/">
            <home-outlined />
            <span>首页</span>
          </router-link>
        </a-menu-item>
        <a-menu-item key="/products" @click="handleMobileNav('/products')">
          <router-link to="/products">
            <appstore-outlined />
            <span>商品列表</span>
          </router-link>
        </a-menu-item>
        <a-menu-item key="/announcements" @click="handleMobileNav('/announcements')">
          <router-link to="/announcements">
            <notification-outlined />
            <span>系统公告</span>
          </router-link>
        </a-menu-item>
        <a-menu-item key="/buy-requests" @click="handleMobileNav('/buy-requests')">
          <router-link to="/buy-requests">
            <shopping-cart-outlined />
            <span>求购专区</span>
          </router-link>
        </a-menu-item>
        <a-menu-item key="/topics" @click="handleMobileNav('/topics')">
          <router-link to="/topics">
            <comment-outlined />
            <span>话题讨论</span>
          </router-link>
        </a-menu-item>
        <template v-if="userStore.isLoggedIn">
          <a-menu-divider />
          <a-menu-item key="/publish" @click="handleMobileNav('/publish')">
            <router-link to="/publish">
              <plus-outlined />
              <span>发布商品</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="/orders" @click="handleMobileNav('/orders')">
            <router-link to="/orders">
              <shopping-outlined />
              <span>我的订单</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="/messages" @click="handleMobileNav('/messages')">
            <router-link to="/messages">
              <message-outlined />
              <span>消息中心</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="/favorites" @click="handleMobileNav('/favorites')">
            <router-link to="/favorites">
              <heart-outlined />
              <span>我的收藏</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="/following" @click="handleMobileNav('/following')">
            <router-link to="/following">
              <team-outlined />
              <span>我的关注</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="/profile" @click="handleMobileNav('/profile')">
            <router-link to="/profile">
              <user-outlined />
              <span>个人中心</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="/credit" @click="handleMobileNav('/credit')">
            <router-link to="/credit">
              <trophy-outlined />
              <span>信用中心</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="/feedback" @click="handleMobileNav('/feedback')">
            <router-link to="/feedback">
              <comment-outlined />
              <span>意见反馈</span>
            </router-link>
          </a-menu-item>
          <a-menu-divider />
          <a-menu-item v-if="userStore.isAdmin" key="/admin" @click="handleMobileNav('/admin')">
            <router-link to="/admin">
              <setting-outlined />
              <span>管理后台</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="logout" @click="handleLogout">
            <logout-outlined />
            <span>退出登录</span>
          </a-menu-item>
        </template>
        <template v-else>
          <a-menu-divider />
          <a-menu-item key="/login" @click="handleMobileNav('/login')">
            <router-link to="/login">
              <login-outlined />
              <span>登录</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="/register" @click="handleMobileNav('/register')">
            <router-link to="/register">
              <user-add-outlined />
              <span>注册</span>
            </router-link>
          </a-menu-item>
        </template>
      </a-menu>
    </a-drawer>

    <a-layout-content class="content">
      <div class="content-wrapper" :class="{ 'no-padding': noPadding }">
        <router-view />
      </div>
    </a-layout-content>

    <a-layout-footer class="footer">
      <div class="footer-content">
        <div class="footer-links">
          <router-link to="/announcements">系统公告</router-link>
          <a-divider type="vertical" />
          <router-link to="/feedback">意见反馈</router-link>
          <a-divider type="vertical" />
          <a href="javascript:;">关于我们</a>
        </div>
        <div class="copyright">
          校园闲置电子产品置换系统 ©{{ new Date().getFullYear() }}
        </div>
      </div>
    </a-layout-footer>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  HomeOutlined,
  AppstoreOutlined,
  PlusOutlined,
  ShoppingOutlined,
  ShoppingCartOutlined,
  MessageOutlined,
  UserOutlined,
  HeartOutlined,
  SettingOutlined,
  LogoutOutlined,
  MenuOutlined,
  SwapOutlined,
  LoginOutlined,
  UserAddOutlined,
  TeamOutlined,
  CommentOutlined,
  NotificationOutlined,
  TrophyOutlined
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const showMobileMenu = ref(false)
const searchKeyword = ref('')
const windowWidth = ref(window.innerWidth)
const unreadCount = ref(0)

const isMobile = computed(() => windowWidth.value < 768)
const currentRoute = computed(() => route.path)
const noPadding = computed(() => route.meta.noPadding as boolean)

const handleSearch = (value: string) => {
  if (value.trim()) {
    router.push({ path: '/products', query: { keyword: value } })
  }
}

const handleMobileNav = (path: string) => {
  showMobileMenu.value = false
}

const handleLogout = () => {
  userStore.logout()
  showMobileMenu.value = false
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
.main-layout {
  min-height: 100vh;
  background: transparent;
}

.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid #e4e4e7;
  padding: 0;
  height: auto;
  line-height: normal;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
  text-decoration: none;
}

.logo-icon {
  font-size: 26px;
  color: #18181b;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #18181b;
  letter-spacing: -0.5px;
}

.search-bar {
  flex: 1;
  max-width: 400px;
}

.search-input {
  border-radius: 8px;
}

.search-input :deep(.ant-input) {
  border-radius: 8px 0 0 8px;
  background: #f4f4f5;
  border-color: transparent;
}

.search-input :deep(.ant-input:focus) {
  background: #ffffff;
  border-color: #18181b;
}

.search-input :deep(.ant-input-search-button) {
  border-radius: 0 8px 8px 0;
  background: #18181b;
  border-color: #18181b;
}

.search-input :deep(.ant-input-search-button:hover) {
  background: #3f3f46;
  border-color: #3f3f46;
}

.nav-menu {
  border: none;
  background: transparent;
  flex-shrink: 0;
}

.nav-menu :deep(.ant-menu-item) {
  padding: 0 16px;
  margin-inline: 4px;
  color: #71717a;
  font-weight: 500;
}

.nav-menu :deep(.ant-menu-item a) {
  display: flex;
  align-items: center;
  gap: 6px;
  color: inherit;
  text-decoration: none;
}

.nav-menu :deep(.ant-menu-item a:hover) {
  color: #18181b;
}

.nav-menu :deep(.ant-menu-item-selected) {
  background: #f4f4f5;
  color: #18181b;
}

.nav-menu :deep(.ant-menu-item::after) {
  display: none;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.header-actions a {
  text-decoration: none;
}

.action-btn {
  color: #71717a;
  font-size: 20px;
}

.action-btn:hover {
  color: #18181b;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 12px 4px 4px;
  border-radius: 999px;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.user-info:hover {
  background: #f4f4f5;
}

.username {
  font-weight: 500;
  color: #18181b;
  font-size: 14px;
}

.admin-tag {
  font-size: 10px;
  padding: 0 6px;
  line-height: 18px;
  background: #18181b;
  border-color: #18181b;
}

.content {
  background: transparent;
}

.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 24px;
  min-height: calc(100vh - 180px);
}

.content-wrapper.no-padding {
  padding: 0;
}

.footer {
  background: #ffffff;
  padding: 40px 24px;
  border-top: 1px solid #e4e4e7;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  text-align: center;
  padding: 0;
  background: transparent;
  border: none;
}

.footer-links {
  margin-bottom: 16px;
}

.footer-links a {
  color: #71717a;
  cursor: pointer;
  transition: color 0.2s;
  text-decoration: none;
  font-weight: 500;
  margin: 0 12px;
}

.footer-links a:hover {
  color: #18181b;
}

.copyright {
  color: #a1a1aa;
  font-size: 13px;
}

.mobile-drawer :deep(.ant-menu-item a) {
  display: flex;
  align-items: center;
  gap: 12px;
  color: inherit;
  text-decoration: none;
  width: 100%;
}

@media (max-width: 768px) {
  .header {
    top: 0;
    margin: 0;
    border-radius: 0;
    border-bottom: 1px solid #e4e4e7;
  }

  .header-content {
    padding: 12px 16px;
    gap: 12px;
  }

  .logo-text {
    font-size: 16px;
  }

  .content-wrapper {
    padding: 24px 16px;
  }

  .footer {
    padding: 24px 16px;
  }
}
</style>
