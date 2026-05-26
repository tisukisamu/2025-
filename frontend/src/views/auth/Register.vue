<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <div class="logo" @click="$router.push('/')">
            <heart-outlined class="logo-icon" />
            <span class="logo-text">宠物纪念</span>
          </div>
          <h1 class="auth-title">创建账户</h1>
          <p class="auth-subtitle">注册一个新账户</p>
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
          
          <a-form-item label="姓名" name="name">
            <a-input 
              v-model:value="formState.name" 
              size="large"
              placeholder="请输入真实姓名"
            >
              <template #prefix>
                <idcard-outlined />
              </template>
            </a-input>
          </a-form-item>
          
          <a-form-item label="年龄" name="age">
            <a-input-number 
              v-model:value="formState.age" 
              size="large"
              placeholder="请输入年龄"
              :min="1"
              :max="120"
              style="width: 100%"
            >
              <template #prefix>
                <calendar-outlined />
              </template>
            </a-input-number>
          </a-form-item>
          
          <a-form-item label="邮箱" name="email">
            <a-input 
              v-model:value="formState.email" 
              size="large"
              placeholder="请输入邮箱"
            >
              <template #prefix>
                <mail-outlined />
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
          
          <a-form-item label="确认密码" name="confirmPassword">
            <a-input-password 
              v-model:value="formState.confirmPassword" 
              size="large"
              placeholder="请再次输入密码"
            >
              <template #prefix>
                <lock-outlined />
              </template>
            </a-input-password>
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
        </a-form>
        
        <div class="auth-footer">
          <span>已有账户？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined, MailOutlined, HeartOutlined, IdcardOutlined, CalendarOutlined } from '@ant-design/icons-vue'
import { register } from '../../api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const formState = reactive({
  username: '',
  name: '',
  age: null,
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = async (_rule, value) => {
  if (value !== formState.password) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

const rules = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 3, message: '用户名至少3个字符' }
  ],
  name: [
    { required: true, message: '请输入姓名' }
  ],
  age: [
    { required: true, message: '请输入年龄' }
  ],
  email: [
    { required: true, message: '请输入邮箱' },
    { type: 'email', message: '请输入正确的邮箱格式' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码至少6个字符' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码' },
    { validator: validateConfirmPassword }
  ]
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    const res = await register({
      username: formState.username,
      name: formState.name,
      age: formState.age,
      email: formState.email,
      password: formState.password
    })
    
    if (res.code === 200) {
      message.success('注册成功，请登录')
      router.push('/login')
    }
  } catch (error) {
    console.error('注册失败:', error)
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
