<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <div class="logo" @click="$router.push('/')">
            <heart-outlined class="logo-icon" />
            <span class="logo-text">宠物纪念</span>
          </div>
          <h1 class="auth-title">欢迎回来</h1>
          <p class="auth-subtitle">登录您的账户</p>
        </div>
        
        <a-form
          ref="formRef"
          :model="formState"
          :rules="rules"
          layout="vertical"
          @finish="handleSubmit"
        >
          <a-form-item label="用户名" name="username">
            <a-input 
              v-model:value="formState.username" 
              size="large"
              placeholder="请输入用户名"
            >
              <template #prefix>
                <user-outlined />
              </template>
            </a-input>
          </a-form-item>
          
          <a-form-item label="密码" name="password">
            <a-input-password 
              v-model:value="formState.password" 
              size="large"
              placeholder="请输入密码"
            >
              <template #prefix>
                <lock-outlined />
              </template>
            </a-input-password>
          </a-form-item>
          
          <div class="auth-options">
            <a-checkbox v-model:checked="formState.remember">记住我</a-checkbox>
            <a href="javascript:;" class="forgot-link">忘记密码？</a>
          </div>
          
          <a-form-item>
            <a-button 
              type="primary" 
              html-type="submit" 
              size="large" 
              block
              :loading="loading"
            >
              登录
            </a-button>
          </a-form-item>
        </a-form>
        
        <div class="auth-footer">
          <span>还没有账户？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined, HeartOutlined } from '@ant-design/icons-vue'
import { login } from '../../api/auth'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const formState = reactive({
  username: '',
  password: '',
  remember: false
})

const rules = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 3, message: '用户名至少3个字符' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码至少6个字符' }
  ]
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    const res = await login({
      username: formState.username,
      password: formState.password
    })
    
    if (res.code === 200) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data.user))
      userStore.setUserInfo(res.data.user)
      userStore.setToken(res.data.token)
      
      message.success('登录成功')
      router.push('/')
    }
  } catch (error) {
    console.error('登录失败:', error)
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
  background: #fafafa;
  padding: 24px;
}

.auth-container {
  width: 100%;
  max-width: 440px;
}

.auth-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  margin-bottom: 24px;
}

.logo-icon {
  font-size: 32px;
  color: #262626;
  margin-right: 8px;
}

.logo-text {
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.auth-title {
  font-size: 24px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 8px;
}

.auth-subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.auth-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.forgot-link {
  font-size: 14px;
  color: #262626;
}

.forgot-link:hover {
  color: #595959;
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
  font-size: 14px;
  color: #8c8c8c;
}

.auth-footer a {
  color: #262626;
  font-weight: 500;
  margin-left: 4px;
}

.auth-footer a:hover {
  color: #595959;
}
</style>
