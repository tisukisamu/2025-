<template>
  <div class="login-container">
    <div class="login-left">
      <div class="brand-section">
        <div class="brand-logo">
          <check-square-outlined />
        </div>
        <h1 class="brand-title">HabitFlow</h1>
        <p class="brand-subtitle">养成好习惯，成就更好的自己</p>
      </div>
      
      <div class="features-section">
        <div class="feature-item">
          <div class="feature-icon">
            <rocket-outlined />
          </div>
          <div class="feature-content">
            <h3>简单易用</h3>
            <p>一键打卡，轻松记录每日习惯</p>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">
            <bar-chart-outlined />
          </div>
          <div class="feature-content">
            <h3>数据可视化</h3>
            <p>直观图表展示习惯完成趋势</p>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">
            <trophy-outlined />
          </div>
          <div class="feature-content">
            <h3>成就激励</h3>
            <p>连续打卡徽章，激励持续进步</p>
          </div>
        </div>
      </div>
    </div>
    
    <div class="login-right">
      <div class="login-form-container">
        <div class="form-header">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">请登录您的账号继续使用</p>
        </div>
        
        <a-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          layout="vertical"
          class="login-form"
          @finish="handleSubmit"
        >
          <a-form-item name="username" label="用户名">
            <a-input
              v-model:value="formData.username"
              placeholder="请输入用户名"
              size="large"
              class="form-input"
            >
              <template #prefix>
                <user-outlined class="input-icon" />
              </template>
            </a-input>
          </a-form-item>
          
          <a-form-item name="password" label="密码">
            <a-input-password
              v-model:value="formData.password"
              placeholder="请输入密码"
              size="large"
              class="form-input"
            >
              <template #prefix>
                <lock-outlined class="input-icon" />
              </template>
            </a-input-password>
          </a-form-item>
          
          <a-form-item>
            <div class="form-options">
              <a-checkbox v-model:checked="rememberMe">记住我</a-checkbox>
              <a class="forgot-link">忘记密码？</a>
            </div>
          </a-form-item>
          
          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              size="large"
              block
              :loading="loading"
              class="submit-btn"
            >
              登 录
            </a-button>
          </a-form-item>
        </a-form>
        
        <div class="form-footer">
          <span class="footer-text">还没有账号？</span>
          <router-link to="/register" class="register-link">立即注册</router-link>
        </div>
        
        <a-divider class="divider">
          <span class="divider-text">其他登录方式</span>
        </a-divider>
        
        <div class="social-login">
          <a-button class="social-btn" shape="circle" size="large">
            <wechat-outlined />
          </a-button>
          <a-button class="social-btn" shape="circle" size="large">
            <qq-outlined />
          </a-button>
          <a-button class="social-btn" shape="circle" size="large">
            <github-outlined />
          </a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { useUserStore } from '@/stores/user'
import {
  UserOutlined,
  LockOutlined,
  CheckSquareOutlined,
  RocketOutlined,
  BarChartOutlined,
  TrophyOutlined,
  WechatOutlined,
  GithubOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const rememberMe = ref(false)

const formData = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  loading.value = true
  try {
    const success = await userStore.loginAction({
      username: formData.username,
      password: formData.password
    })
    
    if (success) {
      const redirect = route.query.redirect as string
      router.push(redirect || '/')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  background: #f9fafb;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #111111 0%, #1f2937 50%, #374151 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 60px;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(255,255,255,0.05) 0%, transparent 70%);
}

.brand-section {
  position: relative;
  z-index: 1;
}

.brand-logo {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: #fff;
  margin-bottom: 24px;
}

.brand-title {
  font-size: 48px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 12px 0;
  letter-spacing: -1px;
}

.brand-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0;
}

.features-section {
  margin-top: 60px;
  position: relative;
  z-index: 1;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 32px;
}

.feature-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  flex-shrink: 0;
}

.feature-content h3 {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 4px 0;
}

.feature-content p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

.login-right {
  width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #fff;
}

.login-form-container {
  width: 100%;
  max-width: 380px;
}

.form-header {
  margin-bottom: 32px;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 8px 0;
}

.form-subtitle {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
}

.login-form :deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: #374151;
}

.form-input :deep(.ant-input),
.form-input :deep(.ant-input-password) {
  height: 48px;
  border-radius: 10px;
  border-color: #e5e7eb;
}

.form-input :deep(.ant-input:hover),
.form-input :deep(.ant-input-password:hover) {
  border-color: #111827;
}

.form-input :deep(.ant-input:focus),
.form-input :deep(.ant-input-password:focus) {
  border-color: #111827;
  box-shadow: 0 0 0 2px rgba(17, 24, 39, 0.1);
}

.input-icon {
  color: #9ca3af;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.forgot-link {
  color: #6b7280;
  font-size: 14px;
}

.forgot-link:hover {
  color: #111827;
}

.submit-btn {
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  background: #111111 !important;
  border-color: #111111 !important;
}

.submit-btn:hover {
  background: #374151 !important;
  border-color: #374151 !important;
}

.form-footer {
  text-align: center;
  margin-top: 24px;
}

.footer-text {
  color: #6b7280;
  font-size: 14px;
}

.register-link {
  color: #111827;
  font-weight: 600;
  font-size: 14px;
  margin-left: 4px;
}

.register-link:hover {
  color: #374151;
}

.divider {
  margin: 32px 0;
}

.divider-text {
  color: #9ca3af;
  font-size: 13px;
  padding: 0 16px;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.social-btn {
  width: 48px;
  height: 48px;
  border-color: #e5e7eb;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.social-btn:hover {
  border-color: #111827;
  color: #111827;
}

@media (max-width: 1024px) {
  .login-left {
    display: none;
  }
  
  .login-right {
    width: 100%;
  }
}
</style>
