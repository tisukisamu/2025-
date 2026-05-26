<template>
  <div class="register-page">
    <div class="register-left">
      <div class="register-left-content">
        <div class="brand">
          <div class="brand-logo">R</div>
          <span class="brand-name">招聘管理系统</span>
        </div>
        
        <h1 class="register-title">加入我们</h1>
        <p class="register-desc">创建账户，开启你的职业新旅程</p>
        
        <div class="benefits">
          <div class="benefit-item">
            <CheckCircleOutlined class="benefit-icon" />
            <span>海量优质职位等你发现</span>
          </div>
          <div class="benefit-item">
            <CheckCircleOutlined class="benefit-icon" />
            <span>智能匹配推荐最适合的工作</span>
          </div>
          <div class="benefit-item">
            <CheckCircleOutlined class="benefit-icon" />
            <span>一键投递，快速获得反馈</span>
          </div>
          <div class="benefit-item">
            <CheckCircleOutlined class="benefit-icon" />
            <span>在线面试，高效便捷</span>
          </div>
        </div>
        
        <div class="testimonial">
          <div class="testimonial-content">
            "这个平台帮我找到了理想的工作，整个求职过程非常顺畅！"
          </div>
          <div class="testimonial-author">
            <a-avatar :size="40" class="author-avatar">张</a-avatar>
            <div class="author-info">
              <span class="author-name">张先生</span>
              <span class="author-title">高级前端工程师</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="register-left-bg"></div>
    </div>
    
    <div class="register-right">
      <div class="register-form-container">
        <div class="register-header">
          <h2>创建账户</h2>
          <p>填写以下信息完成注册</p>
        </div>
        
        <a-form
          :model="formData"
          :rules="rules"
          @finish="handleSubmit"
          layout="vertical"
          class="register-form"
        >
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="用户名" name="username">
                <a-input
                  v-model:value="formData.username"
                  placeholder="请输入用户名"
                  size="large"
                >
                  <template #prefix>
                    <UserOutlined class="input-icon" />
                  </template>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="姓名" name="name">
                <a-input
                  v-model:value="formData.name"
                  placeholder="请输入真实姓名"
                  size="large"
                >
                  <template #prefix>
                    <IdcardOutlined class="input-icon" />
                  </template>
                </a-input>
              </a-form-item>
            </a-col>
          </a-row>
          
          <a-form-item label="邮箱" name="email">
            <a-input
              v-model:value="formData.email"
              placeholder="请输入邮箱"
              size="large"
            >
              <template #prefix>
                <MailOutlined class="input-icon" />
              </template>
            </a-input>
          </a-form-item>
          
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="年龄" name="age">
                <a-input-number
                  v-model:value="formData.age"
                  placeholder="年龄"
                  size="large"
                  class="w-full"
                  :min="18"
                  :max="65"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="账户类型" name="role">
                <a-select
                  v-model:value="formData.role"
                  placeholder="选择类型"
                  size="large"
                >
                  <a-select-option value="USER">
                    <div class="role-option">
                      <UserOutlined />
                      <span>求职者</span>
                    </div>
                  </a-select-option>
                  <a-select-option value="COMPANY">
                    <div class="role-option">
                      <BankOutlined />
                      <span>企业用户</span>
                    </div>
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
          
          <a-form-item label="密码" name="password">
            <a-input-password
              v-model:value="formData.password"
              placeholder="请输入密码（至少6位）"
              size="large"
            >
              <template #prefix>
                <LockOutlined class="input-icon" />
              </template>
            </a-input-password>
          </a-form-item>
          
          <a-form-item label="确认密码" name="confirmPassword">
            <a-input-password
              v-model:value="formData.confirmPassword"
              placeholder="请再次输入密码"
              size="large"
            >
              <template #prefix>
                <LockOutlined class="input-icon" />
              </template>
            </a-input-password>
          </a-form-item>
          
          <a-form-item>
            <a-checkbox v-model:checked="agreeTerms">
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
              :loading="loading"
              :disabled="!agreeTerms"
              block
              class="register-btn"
            >
              立即注册
            </a-button>
          </a-form-item>
        </a-form>
        
        <div class="login-link">
          <span>已有账户?</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  UserOutlined, 
  LockOutlined, 
  MailOutlined, 
  IdcardOutlined,
  BankOutlined,
  CheckCircleOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const agreeTerms = ref(false)

const formData = reactive({
  username: '',
  name: '',
  email: '',
  age: 18,
  role: 'USER',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = async (_rule: any, value: string) => {
  if (value !== formData.password) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
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
  ]
}

const handleSubmit = async () => {
  if (!agreeTerms.value) {
    message.warning('请先同意用户协议和隐私政策')
    return
  }
  
  loading.value = true
  try {
    await userStore.registerAction({
      username: formData.username,
      name: formData.name,
      email: formData.email,
      age: formData.age,
      password: formData.password
    })
    message.success('注册成功')
    router.push('/')
  } catch (error: any) {
    message.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
}

.register-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  display: flex;
  align-items: center;
  padding: 60px;
  overflow: hidden;
}

.register-left-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 10% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 40%),
    radial-gradient(circle at 90% 80%, rgba(255, 255, 255, 0.15) 0%, transparent 40%);
}

.register-left-content {
  position: relative;
  z-index: 1;
  max-width: 500px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 48px;
}

.brand-logo {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
  font-weight: 800;
}

.brand-name {
  color: #fff;
  font-size: 20px;
  font-weight: 600;
}

.register-title {
  font-size: 48px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 16px;
  line-height: 1.2;
}

.register-desc {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 48px;
}

.benefits {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 48px;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #fff;
  font-size: 16px;
}

.benefit-icon {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.9);
}

.testimonial {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
}

.testimonial-content {
  font-size: 16px;
  color: #fff;
  font-style: italic;
  margin-bottom: 16px;
  line-height: 1.6;
}

.testimonial-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  background: rgba(255, 255, 255, 0.3);
  color: #fff;
}

.author-info {
  display: flex;
  flex-direction: column;
}

.author-name {
  color: #fff;
  font-weight: 600;
}

.author-title {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

.register-right {
  width: 560px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: #fff;
  overflow-y: auto;
}

.register-form-container {
  width: 100%;
  max-width: 420px;
}

.register-header {
  margin-bottom: 32px;
}

.register-header h2 {
  font-size: 32px;
  font-weight: 800;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.register-header p {
  font-size: 16px;
  color: #888;
}

.register-form :deep(.ant-form-item-label > label) {
  font-weight: 600;
  color: #333;
}

.register-form :deep(.ant-input-affix-wrapper),
.register-form :deep(.ant-input),
.register-form :deep(.ant-select-selector),
.register-form :deep(.ant-input-number) {
  border-radius: 12px;
  border: 2px solid #e8e8e8;
  transition: all 0.3s;
}

.register-form :deep(.ant-input-affix-wrapper:hover),
.register-form :deep(.ant-input:hover),
.register-form :deep(.ant-select-selector:hover),
.register-form :deep(.ant-input-number:hover) {
  border-color: #667eea;
}

.register-form :deep(.ant-input-affix-wrapper-focused),
.register-form :deep(.ant-input:focus),
.register-form :deep(.ant-select-focused .ant-select-selector),
.register-form :deep(.ant-input-number-focused) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-icon {
  color: #aaa;
  font-size: 16px;
}

.role-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.link {
  color: #667eea;
}

.link:hover {
  color: #764ba2;
}

.register-btn {
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
  transition: all 0.3s;
}

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.4);
}

.register-btn:disabled {
  opacity: 0.6;
}

.login-link {
  text-align: center;
  font-size: 14px;
  color: #888;
  margin-top: 24px;
}

.login-link a {
  color: #667eea;
  font-weight: 600;
  margin-left: 4px;
}

.login-link a:hover {
  color: #764ba2;
}

.w-full {
  width: 100%;
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
