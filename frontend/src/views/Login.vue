<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api'
import { useUserStore } from '../stores/user'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  LockOutlined,
  MailOutlined,
  PhoneOutlined,
  ArrowRightOutlined,
  ShopOutlined,
  CloseOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)

/** 登录表单（主卡片） */
const loginForm = reactive({
  username: '',
  password: ''
})

/** 注册弹窗 */
const registerOpen = ref(false)
const registerLoading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: ''
})

const canSubmitLogin = computed(() => !!loginForm.username && !!loginForm.password)

const handleLogin = async () => {
  if (!canSubmitLogin.value) {
    message.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    const res = await authApi.login(loginForm)
    userStore.setUser(res.accessToken, res.username, res.role, res.username, res.userId, res.hasStore, res.storeStatus)
    message.success('登录成功')
    router.push('/')
  } catch (error: any) {
    message.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const openRegister = () => {
  registerOpen.value = true
}

const closeRegister = () => {
  registerOpen.value = false
}

const handleRegister = async () => {
  if (!registerForm.username || !registerForm.password || !registerForm.email) {
    message.warning('请填写完整信息（用户名/邮箱/密码）')
    return
  }
  if (registerForm.password !== registerForm.confirmPassword) {
    message.warning('两次密码不一致')
    return
  }

  registerLoading.value = true
  try {
    await authApi.register({
      username: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.username
    })
    message.success('注册成功，请登录')
    registerOpen.value = false
  } catch (error: any) {
    message.error(error.message || '注册失败')
  } finally {
    registerLoading.value = false
  }
}

const goStoreRegister = () => router.push('/store/register')
</script>

<template>
  <div class="login-page">
    <!-- 顶部简洁栏 -->
    <div class="topbar">
      <div class="brand" @click="$router.push('/')">
        <span class="brand-logo">
          <ShopOutlined />
        </span>
        <span class="brand-title">AgriStore</span>
      </div>

      <div class="top-actions">
        <a-button type="link" @click="goStoreRegister">
          <ShopOutlined /> 我要开店
        </a-button>
      </div>
    </div>

    <div class="content">
      <a-card class="login-card" :bordered="false">
        <div class="card-head">
          <h1 class="title">登录</h1>
          <p class="subtitle">欢迎回来，登录后继续购物</p>
        </div>

        <a-form layout="vertical" @submit.prevent="handleLogin">
          <a-form-item label="用户名" required>
            <a-input
              v-model:value="loginForm.username"
              size="large"
              placeholder="请输入用户名"
              autocomplete="username"
            >
              <template #prefix><UserOutlined /></template>
            </a-input>
          </a-form-item>

          <a-form-item label="密码" required>
            <a-input-password
              v-model:value="loginForm.password"
              size="large"
              placeholder="请输入密码"
              autocomplete="current-password"
            >
              <template #prefix><LockOutlined /></template>
            </a-input-password>
          </a-form-item>

          <div class="row-between">
            <a-checkbox>记住我</a-checkbox>
            <a-button type="link" class="link-btn">忘记密码？</a-button>
          </div>

          <a-button
            type="primary"
            size="large"
            block
            :loading="loading"
            :disabled="!canSubmitLogin"
            @click="handleLogin"
          >
            登录 <ArrowRightOutlined />
          </a-button>

          <!-- 这里改成“明确的提示行”，不再用 divider（避免被按钮视觉压住） -->
          <div class="register-hint">
            <span class="hint-text">还没有账号？</span>
            <a-button type="link" class="hint-link" @click="openRegister">立即注册</a-button>
          </div>
        </a-form>

        <div class="store-entry">
          <a-divider plain>或者</a-divider>
          <a-button type="dashed" size="large" block @click="goStoreRegister">
            <ShopOutlined /> 我要开店成为店家
          </a-button>
        </div>
      </a-card>
    </div>

    <!-- 注册弹窗：小且紧凑 -->
    <a-modal
      v-model:open="registerOpen"
      title="注册账号"
      ok-text="确定"
      cancel-text="取消"
      :confirm-loading="registerLoading"
      width="420px"
      :bodyStyle="{ padding: '16px 20px' }"
      @ok="handleRegister"
      @cancel="closeRegister"
    >
      <template #closeIcon>
        <CloseOutlined />
      </template>

      <a-form layout="vertical">
        <a-form-item label="用户名" required>
          <a-input v-model:value="registerForm.username" placeholder="输入用户名" autocomplete="username">
            <template #prefix><UserOutlined /></template>
          </a-input>
        </a-form-item>

        <a-form-item label="邮箱" required>
          <a-input v-model:value="registerForm.email" placeholder="example@email.com" autocomplete="email">
            <template #prefix><MailOutlined /></template>
          </a-input>
        </a-form-item>

        <a-form-item label="手机号">
          <a-input v-model:value="registerForm.phone" placeholder="输入手机号" autocomplete="tel">
            <template #prefix><PhoneOutlined /></template>
          </a-input>
        </a-form-item>

        <a-form-item label="密码" required>
          <a-input-password v-model:value="registerForm.password" placeholder="设置密码" autocomplete="new-password">
            <template #prefix><LockOutlined /></template>
          </a-input-password>
        </a-form-item>

        <a-form-item label="确认密码" required>
          <a-input-password v-model:value="registerForm.confirmPassword" placeholder="再次输入密码" autocomplete="new-password">
            <template #prefix><LockOutlined /></template>
          </a-input-password>
        </a-form-item>

        <div class="register-tip">注册成功后将自动返回登录页面。</div>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  display: flex;
  flex-direction: column;
}

.topbar {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #e5e7eb;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}

.brand-logo {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-title {
  font-weight: 800;
  color: #111827;
  letter-spacing: -0.2px;
}

.top-actions :deep(.ant-btn-link) {
  font-weight: 600;
}

.content {
  flex: 1;
  display: grid;
  place-items: center;
  padding: 24px 16px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  border-radius: 16px;
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.08);
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.card-head {
  text-align: left;
  margin-bottom: 10px;
}

.title {
  margin: 0;
  font-size: 26px;
  font-weight: 900;
  color: #111827;
}

.subtitle {
  margin: 8px 0 0;
  font-size: 13px;
  color: #6b7280;
}

.row-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 6px 0 16px;
}

.link-btn {
  padding: 0;
}

/* 关键修复：把“未注册提示”做成独立行，并增加上下间距，绝不会被按钮视觉遮挡 */
.register-hint {
  margin-top: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.hint-text {
  color: #6b7280;
  font-size: 13px;
}

.hint-link {
  padding: 0;
  height: auto;
  font-weight: 700;
}

/* “注册新账号”按钮弱化一点，避免抢提示行注意力 */
.register-btn {
  margin-top: 8px;
}

.store-entry {
  margin-top: 14px;
}

.register-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #9ca3af;
}

:deep(.ant-modal-title) {
  font-weight: 800;
}
:deep(.ant-modal-body) {
  padding-top: 14px !important;
}
</style>
