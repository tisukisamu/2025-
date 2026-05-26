<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-left">
        <div class="auth-brand">
          <swap-outlined class="brand-icon" />
          <h1>校园置换</h1>
          <p>校园闲置电子产品交易平台</p>
        </div>
        <div class="auth-features">
          <div class="feature-item">
            <safety-certificate-outlined class="feature-icon" />
            <div class="feature-text">
              <h3>安全可靠</h3>
              <p>学号认证，真实可信</p>
            </div>
          </div>
          <div class="feature-item">
            <thunderbolt-outlined class="feature-icon" />
            <div class="feature-text">
              <h3>快速交易</h3>
              <p>面交快递，灵活选择</p>
            </div>
          </div>
          <div class="feature-item">
            <star-outlined class="feature-icon" />
            <div class="feature-text">
              <h3>信誉体系</h3>
              <p>评价透明，交易放心</p>
            </div>
          </div>
        </div>
      </div>

      <div class="auth-right">
        <div class="auth-form-container">
          <h2>创建账户</h2>
          <p class="auth-subtitle">注册账户，开启交易之旅</p>

          <a-form
            :model="formState"
            :rules="rules"
            @finish="handleSubmit"
            layout="vertical"
            class="auth-form"
          >
            <a-form-item name="username" label="用户名">
              <a-input
                v-model:value="formState.username"
                placeholder="请输入用户名"
                size="large"
              >
                <template #prefix>
                  <user-outlined />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item name="studentId" label="学号">
              <a-input
                v-model:value="formState.studentId"
                placeholder="请输入学号"
                size="large"
              >
                <template #prefix>
                  <idcard-outlined />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item name="realName" label="真实姓名">
              <a-input
                v-model:value="formState.realName"
                placeholder="请输入真实姓名（选填）"
                size="large"
              >
                <template #prefix>
                  <solution-outlined />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item name="phone" label="手机号">
              <a-input
                v-model:value="formState.phone"
                placeholder="请输入手机号（选填）"
                size="large"
              >
                <template #prefix>
                  <phone-outlined />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item name="password" label="密码">
              <a-input-password
                v-model:value="formState.password"
                placeholder="请输入密码"
                size="large"
              >
                <template #prefix>
                  <lock-outlined />
                </template>
              </a-input-password>
            </a-form-item>

            <a-form-item name="confirmPassword" label="确认密码">
              <a-input-password
                v-model:value="formState.confirmPassword"
                placeholder="请再次输入密码"
                size="large"
              >
                <template #prefix>
                  <lock-outlined />
                </template>
              </a-input-password>
            </a-form-item>

            <a-form-item name="agreement">
              <a-checkbox v-model:checked="formState.agreement">
                我已阅读并同意
                <a @click.stop>《用户协议》</a>
                和
                <a @click.stop>《隐私政策》</a>
              </a-checkbox>
            </a-form-item>

            <a-form-item>
              <a-button
                type="primary"
                html-type="submit"
                size="large"
                block
                :loading="loading"
              >
                注册
              </a-button>
            </a-form-item>

            <div class="auth-footer">
              <span>已有账户？</span>
              <a @click="router.push('/login')">立即登录</a>
            </div>
          </a-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import type { Rule } from 'ant-design-vue/es/form'
import {
  UserOutlined,
  LockOutlined,
  IdcardOutlined,
  SolutionOutlined,
  PhoneOutlined,
  SwapOutlined,
  SafetyCertificateOutlined,
  ThunderboltOutlined,
  StarOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)

const formState = reactive({
  username: '',
  studentId: '',
  realName: '',
  phone: '',
  password: '',
  confirmPassword: '',
  agreement: false
})

const validateConfirmPassword = async (_rule: Rule, value: string) => {
  if (value !== formState.password) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

const validateAgreement = async (_rule: Rule, value: boolean) => {
  if (!value) {
    return Promise.reject('请阅读并同意用户协议')
  }
  return Promise.resolve()
}

const rules: Record<string, Rule[]> = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 3, max: 50, message: '用户名长度为3-50个字符' }
  ],
  studentId: [
    { required: true, message: '请输入学号' },
    { max: 20, message: '学号长度不能超过20个字符' }
  ],
  realName: [
    { max: 50, message: '姓名长度不能超过50个字符' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码长度不能少于6个字符' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码' },
    { validator: validateConfirmPassword }
  ],
  agreement: [
    { validator: validateAgreement }
  ]
}

const handleSubmit = async () => {
  loading.value = true
  try {
    const success = await userStore.registerAction({
      username: formState.username,
      studentId: formState.studentId,
      realName: formState.realName,
      phone: formState.phone,
      password: formState.password
    })
    if (success) {
      router.push('/')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f4f4f5;
  padding: 24px;
}

.auth-container {
  display: flex;
  background: #ffffff;
  border: 1px solid #e4e4e7;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  max-width: 980px;
  width: 100%;
}

.auth-left {
  flex: 1;
  background: #18181b;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #ffffff;
}

.auth-brand {
  text-align: center;
  margin-bottom: 60px;
}

.brand-icon {
  font-size: 60px;
  margin-bottom: 20px;
  color: #ffffff;
}

.auth-brand h1 {
  font-size: 32px;
  font-weight: 600;
  margin: 0 0 10px;
  color: #ffffff;
}

.auth-brand p {
  font-size: 16px;
  opacity: 0.8;
  margin: 0;
  color: #a1a1aa;
}

.auth-features {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.feature-icon {
  font-size: 32px;
  opacity: 0.9;
  color: #ffffff;
}

.feature-text h3 {
  font-size: 18px;
  font-weight: 500;
  margin: 0 0 4px;
  color: #ffffff;
}

.feature-text p {
  font-size: 14px;
  opacity: 0.7;
  margin: 0;
  color: #a1a1aa;
}

.auth-right {
  flex: 1;
  padding: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  max-height: 100vh;
  overflow-y: auto;
}

.auth-form-container {
  width: 100%;
  max-width: 360px;
}

.auth-form-container h2 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px;
  color: #18181b;
}

.auth-subtitle {
  color: #71717a;
  margin-bottom: 24px;
}

.auth-form {
  margin-top: 16px;
}

.auth-footer {
  text-align: center;
  color: #71717a;
}

.auth-footer a {
  color: #18181b;
  margin-left: 4px;
  cursor: pointer;
  font-weight: 500;
}

@media (max-width: 768px) {
  .auth-container {
    flex-direction: column;
  }

  .auth-left {
    padding: 40px 24px;
  }

  .auth-brand {
    margin-bottom: 30px;
  }

  .brand-icon {
    font-size: 40px;
  }

  .auth-brand h1 {
    font-size: 24px;
  }

  .auth-features {
    gap: 20px;
  }

  .feature-icon {
    font-size: 24px;
  }

  .feature-text h3 {
    font-size: 16px;
  }

  .auth-right {
    padding: 24px;
  }

  .auth-form-container h2 {
    font-size: 24px;
  }
}
</style>
