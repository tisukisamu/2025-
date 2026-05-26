<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100 p-4">
    <div class="w-full max-w-md">
      <div class="bg-white rounded-lg shadow-sm p-8">
        <div class="text-center mb-8">
          <div class="w-16 h-16 bg-gray-800 rounded-full flex items-center justify-center mx-auto mb-4">
            <span class="text-white text-2xl font-bold">街舞</span>
          </div>
          <h2 class="text-2xl font-semibold text-gray-800 mb-2">欢迎回来</h2>
          <p class="text-gray-500">请登录您的账户</p>
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
          
          <a-form-item>
            <div class="flex justify-between items-center">
              <a-checkbox v-model:checked="rememberMe">
                记住我
              </a-checkbox>
              <a class="text-gray-600 hover:text-gray-800" href="#">
                忘记密码？
              </a>
            </div>
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
              登录
            </a-button>
          </a-form-item>
          
          <div class="text-center text-gray-500">
            <span>还没有账户？</span>
            <a class="text-gray-800 hover:text-gray-600 ml-1 font-medium" @click="goToRegister">
              立即注册
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
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const rememberMe = ref(false)

const formData = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度3-50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  const success = await userStore.loginAction(formData)
  if (success) {
    if (userStore.isAdmin) {
      router.push('/admin')
    } else if (userStore.isTeacher) {
      router.push('/teacher')
    } else {
      router.push('/student')
    }
  }
}

const goToRegister = () => {
  router.push('/register')
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
