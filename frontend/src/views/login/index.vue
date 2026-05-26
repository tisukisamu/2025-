<template>
  <div class="login-container">
    <div class="login-shell">
      <div class="brand-panel">
        <div class="brand-badge">Campus Club Fund</div>
        <h1>高校社团资金管控平台</h1>
        <p>资金流程标准化、审批透明化、监管可追溯</p>
        <div class="brand-stats">
          <div class="stat-box">
            <div class="stat-value">4</div>
            <div class="stat-label">角色协同</div>
          </div>
          <div class="stat-box">
            <div class="stat-value">24h</div>
            <div class="stat-label">实时监管</div>
          </div>
          <div class="stat-box">
            <div class="stat-value">100%</div>
            <div class="stat-label">流程留痕</div>
          </div>
        </div>
      </div>
      <div class="login-card">
        <div class="login-header">
          <h2>{{ activeKey === 'login' ? '欢迎登录' : '创建账号' }}</h2>
          <p>{{ activeKey === 'login' ? '输入账号密码进入系统' : '注册后即可提交申请与参与审批流程' }}</p>
        </div>

        <a-tabs v-model:activeKey="activeKey">
          <a-tab-pane key="login" tab="登录">
            <a-form
              ref="loginFormRef"
              :model="loginForm"
              :rules="loginRules"
              class="login-form"
            >
              <a-form-item name="username">
                <a-input
                  v-model:value="loginForm.username"
                  size="large"
                  placeholder="用户名"
                >
                  <template #prefix>
                    <UserOutlined />
                  </template>
                </a-input>
              </a-form-item>
              <a-form-item name="password">
                <a-input-password
                  v-model:value="loginForm.password"
                  size="large"
                  placeholder="密码"
                >
                  <template #prefix>
                    <LockOutlined />
                  </template>
                </a-input-password>
              </a-form-item>
              <a-form-item>
                <a-button
                  type="primary"
                  size="large"
                  block
                  :loading="loading"
                  @click="handleLogin"
                >
                  登录
                </a-button>
              </a-form-item>
            </a-form>
          </a-tab-pane>

          <a-tab-pane key="register" tab="注册">
            <a-form
              ref="registerFormRef"
              :model="registerForm"
              :rules="registerRules"
              class="login-form"
            >
              <a-form-item name="username">
                <a-input
                  v-model:value="registerForm.username"
                  size="large"
                  placeholder="用户名"
                >
                  <template #prefix>
                    <UserOutlined />
                  </template>
                </a-input>
              </a-form-item>
              <a-form-item name="realName">
                <a-input
                  v-model:value="registerForm.realName"
                  size="large"
                  placeholder="真实姓名"
                >
                  <template #prefix>
                    <IdcardOutlined />
                  </template>
                </a-input>
              </a-form-item>
              <a-form-item name="password">
                <a-input-password
                  v-model:value="registerForm.password"
                  size="large"
                  placeholder="密码"
                >
                  <template #prefix>
                    <LockOutlined />
                  </template>
                </a-input-password>
              </a-form-item>
              <a-form-item name="confirmPassword">
                <a-input-password
                  v-model:value="registerForm.confirmPassword"
                  size="large"
                  placeholder="确认密码"
                >
                  <template #prefix>
                    <LockOutlined />
                  </template>
                </a-input-password>
              </a-form-item>
              <a-form-item name="studentId">
                <a-input
                  v-model:value="registerForm.studentId"
                  size="large"
                  placeholder="学号"
                >
                  <template #prefix>
                    <NumberOutlined />
                  </template>
                </a-input>
              </a-form-item>
              <a-form-item>
                <a-button
                  type="primary"
                  size="large"
                  block
                  :loading="registerLoading"
                  @click="handleRegister"
                >
                  注册
                </a-button>
              </a-form-item>
            </a-form>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message, type FormInstance, type Rule } from 'ant-design-vue'
import { useUserStore } from '@/stores'
import {
  UserOutlined,
  LockOutlined,
  IdcardOutlined,
  NumberOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeKey = ref('login')
const loading = ref(false)
const registerLoading = ref(false)
const loginFormRef = ref<FormInstance>()
const registerFormRef = ref<FormInstance>()

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  realName: '',
  password: '',
  confirmPassword: '',
  studentId: ''
})

const loginRules: Record<string, Rule[]> = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }]
}

const validateConfirmPassword = async (_rule: Rule, value: string) => {
  if (value !== registerForm.password) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

const registerRules: Record<string, Rule[]> = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 3, max: 50, message: '用户名长度为3-50个字符' }
  ],
  realName: [{ required: true, message: '请输入真实姓名' }],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码长度不能少于6位' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    { validator: validateConfirmPassword }
  ]
}

const handleLogin = async () => {
  try {
    await loginFormRef.value?.validate()
    loading.value = true
    await userStore.login(loginForm)
    message.success('登录成功')
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.push(redirect)
  } catch (error: any) {
    message.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  try {
    await registerFormRef.value?.validate()
    registerLoading.value = true
    await userStore.register({
      username: registerForm.username,
      password: registerForm.password,
      realName: registerForm.realName,
      studentId: registerForm.studentId
    })
    message.success('注册成功，请登录')
    activeKey.value = 'login'
    loginForm.username = registerForm.username
  } catch (error: any) {
    message.error(error.message || '注册失败')
  } finally {
    registerLoading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at 12% 20%, #2d2d2d 0%, #181818 40%, #0f0f0f 100%);
  padding: 24px;
}

.login-shell {
  width: min(1080px, 100%);
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  border: 1px solid #2c2c2c;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 32px 60px rgba(0, 0, 0, 0.4);
  background: #111111;
}

.brand-panel {
  padding: 56px 52px;
  color: #f3f3f3;
  background: linear-gradient(160deg, #1d1d1d 0%, #121212 100%);
}

.brand-badge {
  display: inline-block;
  padding: 5px 12px;
  background: #2a2a2a;
  color: #d8d8d8;
  border-radius: 999px;
  font-size: 12px;
  letter-spacing: 0.6px;
  margin-bottom: 20px;
}

.brand-panel h1 {
  margin: 0;
  color: #ffffff;
  font-size: 34px;
  line-height: 1.25;
}

.brand-panel p {
  margin: 16px 0 0;
  color: #b8b8b8;
  font-size: 15px;
}

.brand-stats {
  margin-top: 44px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.stat-box {
  border: 1px solid #2f2f2f;
  background: #181818;
  border-radius: 12px;
  padding: 14px 12px;
}

.stat-value {
  color: #ffffff;
  font-size: 22px;
  font-weight: 700;
}

.stat-label {
  margin-top: 4px;
  font-size: 12px;
  color: #a3a3a3;
}

.login-card {
  padding: 40px 36px;
  background: #ffffff;
}

.login-header {
  margin-bottom: 24px;
}

.login-header h2 {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  color: #111111;
}

.login-header p {
  margin-top: 8px;
  font-size: 14px;
  color: #666666;
}

.login-form {
  margin-top: 24px;
}

:deep(.ant-tabs-tab) {
  color: #666666;
}

:deep(.ant-tabs-tab-active .ant-tabs-tab-btn) {
  color: #111111;
}

:deep(.ant-tabs-ink-bar) {
  background: #111111;
}

@media (max-width: 960px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .brand-panel {
    display: none;
  }
}
</style>
