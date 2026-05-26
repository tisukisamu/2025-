<template>
  <div class="register-container">
    <div class="register-left">
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
    
    <div class="register-right">
      <div class="register-form-container">
        <div class="form-header">
          <h2 class="form-title">创建账号</h2>
          <p class="form-subtitle">开始您的好习惯养成之旅</p>
        </div>
        
        <a-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          layout="vertical"
          class="register-form"
          @finish="handleSubmit"
        >
          <a-row :gutter="16">
            <a-col :span="12">
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
            </a-col>
            <a-col :span="12">
              <a-form-item name="name" label="姓名">
                <a-input
                  v-model:value="formData.name"
                  placeholder="请输入姓名"
                  size="large"
                  class="form-input"
                >
                  <template #prefix>
                    <idcard-outlined class="input-icon" />
                  </template>
                </a-input>
              </a-form-item>
            </a-col>
          </a-row>
          
          <a-form-item name="email" label="邮箱">
            <a-input
              v-model:value="formData.email"
              placeholder="请输入邮箱"
              size="large"
              class="form-input"
            >
              <template #prefix>
                <mail-outlined class="input-icon" />
              </template>
            </a-input>
          </a-form-item>
          
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item name="age" label="年龄">
                <a-input-number
                  v-model:value="formData.age"
                  placeholder="年龄"
                  size="large"
                  :min="1"
                  :max="150"
                  class="form-input w-full"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item name="phone" label="手机号">
                <a-input
                  v-model:value="formData.phone"
                  placeholder="请输入手机号"
                  size="large"
                  class="form-input"
                >
                  <template #prefix>
                    <phone-outlined class="input-icon" />
                  </template>
                </a-input>
              </a-form-item>
            </a-col>
          </a-row>
          
          <a-form-item name="password" label="密码">
            <a-input-password
              v-model:value="formData.password"
              placeholder="请输入密码（至少6位）"
              size="large"
              class="form-input"
            >
              <template #prefix>
                <lock-outlined class="input-icon" />
              </template>
            </a-input-password>
          </a-form-item>
          
          <a-form-item name="confirmPassword" label="确认密码">
            <a-input-password
              v-model:value="formData.confirmPassword"
              placeholder="请再次输入密码"
              size="large"
              class="form-input"
            >
              <template #prefix>
                <safety-outlined class="input-icon" />
              </template>
            </a-input-password>
          </a-form-item>
          
          <a-form-item name="agreement">
            <a-checkbox v-model:checked="formData.agreement" class="agreement-checkbox">
              我已阅读并同意
              <a class="link">《用户协议》</a>
              和
              <a class="link">《隐私政策》</a>
            </a-checkbox>
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
              立即注册
            </a-button>
          </a-form-item>
        </a-form>
        
        <div class="form-footer">
          <span class="footer-text">已有账号？</span>
          <router-link to="/login" class="login-link">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useUserStore } from '@/stores/user'
import {
  UserOutlined,
  LockOutlined,
  CheckSquareOutlined,
  RocketOutlined,
  BarChartOutlined,
  TrophyOutlined,
  MailOutlined,
  PhoneOutlined,
  IdcardOutlined,
  SafetyOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)

const formData = reactive({
  username: '',
  name: '',
  email: '',
  age: null as number | null,
  phone: '',
  password: '',
  confirmPassword: '',
  agreement: false
})

const validateConfirmPassword = async (_rule: any, value: string) => {
  if (value !== formData.password) {
    throw new Error('两次输入的密码不一致')
  }
}

const validateAgreement = async (_rule: any, value: boolean) => {
  if (!value) {
    throw new Error('请阅读并同意用户协议')
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  age: [
    { required: true, message: '请输入年龄', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agreement: [
    { validator: validateAgreement, trigger: 'change' }
  ]
}

const handleSubmit = async () => {
  loading.value = true
  try {
    const success = await userStore.registerAction({
      username: formData.username,
      password: formData.password,
      name: formData.name,
      email: formData.email,
      age: formData.age!
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
.register-container {
  min-height: 100vh;
  display: flex;
  background: #f9fafb;
}

.register-left {
  flex: 1;
  background: linear-gradient(135deg, #111111 0%, #1f2937 50%, #374151 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 60px;
  position: relative;
  overflow: hidden;
}

.register-left::before {
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

.register-right {
  width: 560px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #fff;
  overflow-y: auto;
}

.register-form-container {
  width: 100%;
  max-width: 440px;
  padding: 20px 0;
}

.form-header {
  margin-bottom: 28px;
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

.register-form :deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: #374151;
}

.form-input :deep(.ant-input),
.form-input :deep(.ant-input-password),
.form-input :deep(.ant-input-number) {
  height: 44px;
  border-radius: 10px;
  border-color: #e5e7eb;
}

.form-input :deep(.ant-input:hover),
.form-input :deep(.ant-input-password:hover),
.form-input :deep(.ant-input-number:hover) {
  border-color: #111827;
}

.form-input :deep(.ant-input:focus),
.form-input :deep(.ant-input-password:focus),
.form-input :deep(.ant-input-number:focus) {
  border-color: #111827;
  box-shadow: 0 0 0 2px rgba(17, 24, 39, 0.1);
}

.w-full {
  width: 100%;
}

.input-icon {
  color: #9ca3af;
}

.agreement-checkbox {
  font-size: 13px;
  color: #6b7280;
}

.link {
  color: #111827;
}

.link:hover {
  color: #374151;
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

.login-link {
  color: #111827;
  font-weight: 600;
  font-size: 14px;
  margin-left: 4px;
}

.login-link:hover {
  color: #374151;
}

@media (max-width: 1024px) {
  .register-left {
    display: none;
  }
  
  .register-right {
    width: 100%;
  }
}
</style>
