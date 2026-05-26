<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100 p-4">
    <div class="w-full max-w-md">
      <div class="bg-white rounded-lg shadow-sm p-8">
        <div class="text-center mb-8">
          <div class="w-16 h-16 bg-gray-800 rounded-full flex items-center justify-center mx-auto mb-4">
            <span class="text-white text-2xl font-bold">街舞</span>
          </div>
          <h2 class="text-2xl font-semibold text-gray-800 mb-2">创建账户</h2>
          <p class="text-gray-500">加入我们，开始您的舞蹈之旅</p>
        </div>
        
        <a-form
          :model="formData"
          :rules="rules"
          @finish="handleSubmit"
          layout="vertical"
        >
          <a-form-item label="用户名" name="username">
            <a-input
              v-model:value="formData.username"
              placeholder="请输入用户名"
              size="large"
            >
              <template #prefix>
                <UserOutlined class="text-gray-400" />
              </template>
            </a-input>
          </a-form-item>
          
          <a-form-item label="真实姓名" name="realName">
            <a-input
              v-model:value="formData.realName"
              placeholder="请输入真实姓名"
              size="large"
            >
              <template #prefix>
                <IdcardOutlined class="text-gray-400" />
              </template>
            </a-input>
          </a-form-item>
          
          <a-form-item label="邮箱" name="email">
            <a-input
              v-model:value="formData.email"
              placeholder="请输入邮箱"
              size="large"
            >
              <template #prefix>
                <MailOutlined class="text-gray-400" />
              </template>
            </a-input>
          </a-form-item>
          
          <a-form-item label="手机号" name="phone">
            <a-input
              v-model:value="formData.phone"
              placeholder="请输入手机号"
              size="large"
            >
              <template #prefix>
                <PhoneOutlined class="text-gray-400" />
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
                <LockOutlined class="text-gray-400" />
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
                <LockOutlined class="text-gray-400" />
              </template>
            </a-input-password>
          </a-form-item>
          
          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              size="large"
              :loading="userStore.loading"
              block
              class="bg-gray-800 hover:bg-gray-700 border-gray-800 hover:border-gray-700"
            >
              注册
            </a-button>
          </a-form-item>
          
          <div class="text-center text-gray-500">
            <span>已有账户？</span>
            <a class="text-gray-800 hover:text-gray-600 ml-1 font-medium" @click="goToLogin">
              立即登录
            </a>
          </div>
        </a-form>
      </div>
      
      <div class="text-center mt-6 text-gray-400 text-sm">
        <p>© 2024 街舞工作室管理系统. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { UserOutlined, LockOutlined, MailOutlined, PhoneOutlined, IdcardOutlined } from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formData = reactive({
  username: '',
  realName: '',
  email: '',
  phone: '',
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
    { min: 3, max: 50, message: '用户名长度3-50个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  const success = await userStore.registerAction({
    username: formData.username,
    password: formData.password,
    email: formData.email,
    phone: formData.phone,
    realName: formData.realName
  })
  if (success) {
    router.push('/student')
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.ant-btn-primary {
  background-color: #1f2937;
  border-color: #1f2937;
}

.ant-btn-primary:hover {
  background-color: #374151;
  border-color: #374151;
}
</style>
