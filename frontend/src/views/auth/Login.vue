<template>
  <div class="login-page">
    <div class="login-left">
      <div class="login-left-content">
        <div class="brand">
          <div class="brand-logo">R</div>
          <span class="brand-name">招聘管理系统</span>
        </div>
        
        <h1 class="login-title">开启你的职业新篇章</h1>
        <p class="login-desc">连接优质企业，发现理想工作，让求职更简单</p>
        
        <div class="features">
          <div class="feature-item">
            <div class="feature-icon">
              <RocketOutlined />
            </div>
            <div class="feature-text">
              <h4>智能匹配</h4>
              <p>AI精准推荐最适合你的职位</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <SafetyOutlined />
            </div>
            <div class="feature-text">
              <h4>安全保障</h4>
              <p>企业资质严格审核认证</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <ThunderboltOutlined />
            </div>
            <div class="feature-text">
              <h4>高效沟通</h4>
              <p>在线即时沟通快速反馈</p>
            </div>
          </div>
        </div>
        
        <div class="stats">
          <div class="stat-item">
            <span class="stat-value">12,580+</span>
            <span class="stat-label">在线职位</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">3,200+</span>
            <span class="stat-label">入驻企业</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">58,000+</span>
            <span class="stat-label">注册用户</span>
          </div>
        </div>
      </div>
      
      <div class="login-left-bg"></div>
    </div>
    
    <div class="login-right">
      <div class="login-form-container">
        <div class="login-header">
          <h2>欢迎回来</h2>
          <p>登录您的账户继续使用</p>
        </div>
        
        <a-form
          :model="formData"
          :rules="rules"
          @finish="handleSubmit"
          layout="vertical"
          class="login-form"
        >
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
          
          <a-form-item label="密码" name="password">
            <a-input-password
              v-model:value="formData.password"
              placeholder="请输入密码"
              size="large"
            >
              <template #prefix>
                <LockOutlined class="input-icon" />
              </template>
            </a-input-password>
          </a-form-item>
          
          <div class="form-options">
            <a-checkbox v-model:checked="rememberMe">记住我</a-checkbox>
            <a class="forgot-link">忘记密码?</a>
          </div>
          
          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              size="large"
              :loading="loading"
              block
              class="login-btn"
            >
              登录
            </a-button>
          </a-form-item>
        </a-form>
        
        <div class="divider">
          <span>或</span>
        </div>
        
        <div class="social-login">
          <button class="social-btn wechat">
            <WechatOutlined />
            微信登录
          </button>
          <button class="social-btn qq">
            <QqOutlined />
            QQ登录
          </button>
        </div>
        
        <div class="register-link">
          <span>还没有账户?</span>
          <router-link to="/register">立即注册</router-link>
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
  RocketOutlined,
  SafetyOutlined,
  ThunderboltOutlined,
  WechatOutlined,
  QqOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const rememberMe = ref(false)

const formData = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  loading.value = true
  try {
    await userStore.loginAction(formData)
    message.success('登录成功')
    if (userStore.isAdmin) {
      router.push('/admin')
    } else if (userStore.userRole === 'COMPANY') {
      router.push('/company')
    } else {
      router.push('/')
    }
  } catch (error: any) {
    message.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  position: relative;
  display: flex;
  align-items: center;
  padding: 60px;
  overflow: hidden;
}

.login-left-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 80%, rgba(102, 126, 234, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(240, 147, 251, 0.2) 0%, transparent 50%);
  animation: bgMove 10s ease-in-out infinite alternate;
}

@keyframes bgMove {
  0% { transform: scale(1) rotate(0deg); }
  100% { transform: scale(1.1) rotate(5deg); }
}

.login-left-content {
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

.login-title {
  font-size: 48px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 16px;
  line-height: 1.2;
}

.login-desc {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 48px;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 48px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.feature-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
}

.feature-text h4 {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.feature-text p {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}

.stats {
  display: flex;
  gap: 48px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 800;
  color: #fff;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.login-right {
  width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: #fff;
}

.login-form-container {
  width: 100%;
  max-width: 380px;
}

.login-header {
  margin-bottom: 40px;
}

.login-header h2 {
  font-size: 32px;
  font-weight: 800;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.login-header p {
  font-size: 16px;
  color: #888;
}

.login-form :deep(.ant-form-item-label > label) {
  font-weight: 600;
  color: #333;
}

.login-form :deep(.ant-input-affix-wrapper),
.login-form :deep(.ant-input) {
  border-radius: 12px;
  border: 2px solid #e8e8e8;
  padding: 12px 16px;
  transition: all 0.3s;
}

.login-form :deep(.ant-input-affix-wrapper:hover),
.login-form :deep(.ant-input:hover) {
  border-color: #667eea;
}

.login-form :deep(.ant-input-affix-wrapper-focused),
.login-form :deep(.ant-input:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-icon {
  color: #aaa;
  font-size: 16px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.forgot-link {
  color: #667eea;
  font-size: 14px;
  cursor: pointer;
}

.forgot-link:hover {
  color: #764ba2;
}

.login-btn {
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.4);
}

.divider {
  display: flex;
  align-items: center;
  margin: 32px 0;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e8e8e8;
}

.divider span {
  padding: 0 16px;
  color: #aaa;
  font-size: 14px;
}

.social-login {
  display: flex;
  gap: 16px;
  margin-bottom: 32px;
}

.social-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px;
  border-radius: 12px;
  border: 2px solid #e8e8e8;
  background: #fff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.social-btn.wechat {
  color: #07c160;
}

.social-btn.wechat:hover {
  border-color: #07c160;
  background: rgba(7, 193, 96, 0.05);
}

.social-btn.qq {
  color: #12b7f5;
}

.social-btn.qq:hover {
  border-color: #12b7f5;
  background: rgba(18, 183, 245, 0.05);
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: #888;
}

.register-link a {
  color: #667eea;
  font-weight: 600;
  margin-left: 4px;
}

.register-link a:hover {
  color: #764ba2;
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
