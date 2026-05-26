<template>
  <div>
    <a-page-header title="个人中心" />
    
    <a-row :gutter="24">
      <a-col :span="8">
        <a-card class="text-center">
          <div class="py-5">
            <a-upload
              name="file"
              :show-upload-list="false"
              :before-upload="beforeUpload"
              :customRequest="handleAvatarUpload"
              accept="image/jpeg,image/png"
            >
              <div class="avatar-upload-wrapper">
                <a-avatar :size="100" :src="userInfo?.avatar" :style="{ backgroundColor: userStore.isAdmin ? '#f5222d' : '#1890ff' }">
                  {{ userInfo?.avatar ? '' : (userStore.username?.charAt(0)?.toUpperCase() || 'U') }}
                </a-avatar>
                <div class="avatar-upload-overlay">
                  <CameraOutlined class="text-white text-xl" />
                  <span class="text-white text-xs mt-1">更换头像</span>
                </div>
              </div>
            </a-upload>
            <h3 class="mt-4 mb-2">{{ userInfo?.realName || userInfo?.username }}</h3>
            <p class="mb-0">
              <a-tag :color="userStore.isAdmin ? 'red' : 'blue'">
                {{ userStore.isAdmin ? '管理员' : '普通用户' }}
              </a-tag>
              <a-tag :color="userStore.isActive ? 'green' : 'red'">
                {{ userStore.isActive ? '正常' : '已禁用' }}
              </a-tag>
            </p>
          </div>
          
          <a-divider />
          
          <div class="text-left">
            <div class="flex justify-between py-3 border-b border-gray-100">
              <span class="text-gray-500">用户名</span>
              <span class="text-gray-800 font-medium">{{ userInfo?.username }}</span>
            </div>
            <div class="flex justify-between py-3 border-b border-gray-100">
              <span class="text-gray-500">邮箱</span>
              <span class="text-gray-800 font-medium">{{ userInfo?.email || '未设置' }}</span>
            </div>
            <div class="flex justify-between py-3 border-b border-gray-100">
              <span class="text-gray-500">手机号</span>
              <span class="text-gray-800 font-medium">{{ userInfo?.phone || '未设置' }}</span>
            </div>
            <div class="flex justify-between py-3">
              <span class="text-gray-500">注册时间</span>
              <span class="text-gray-800 font-medium">{{ formatDate(userInfo?.createdAt) }}</span>
            </div>
          </div>
        </a-card>
      </a-col>
      
      <a-col :span="16">
        <a-card title="编辑资料">
          <a-form
            :model="formData"
            :rules="rules"
            @finish="handleSubmit"
            layout="vertical"
          >
            <a-form-item label="姓名" name="realName">
              <a-input v-model:value="formData.realName" placeholder="请输入姓名" />
            </a-form-item>
            
            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
            </a-form-item>
            
            <a-form-item label="手机号" name="phone">
              <a-input v-model:value="formData.phone" placeholder="请输入手机号" />
            </a-form-item>
            
            <a-form-item>
              <a-button type="primary" html-type="submit" :loading="loading">
                保存修改
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>
        
        <a-card title="修改密码" class="mt-6">
          <a-form
            :model="passwordForm"
            :rules="passwordRules"
            @finish="handlePasswordSubmit"
            layout="vertical"
          >
            <a-form-item label="当前密码" name="currentPassword">
              <a-input-password v-model:value="passwordForm.currentPassword" placeholder="请输入当前密码" />
            </a-form-item>
            
            <a-form-item label="新密码" name="newPassword">
              <a-input-password v-model:value="passwordForm.newPassword" placeholder="请输入新密码（至少6位）" />
            </a-form-item>
            
            <a-form-item label="确认新密码" name="confirmPassword">
              <a-input-password v-model:value="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
            </a-form-item>
            
            <a-form-item>
              <a-button type="primary" html-type="submit" :loading="passwordLoading">
                修改密码
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { CameraOutlined } from '@ant-design/icons-vue'
import { useUserStore } from '../stores/user'
import { getCurrentUser } from '../api/auth'
import { updateProfile, uploadAvatar } from '../api/user'

const userStore = useUserStore()
const loading = ref(false)
const passwordLoading = ref(false)
const uploadLoading = ref(false)
const userInfo = ref(null)

const formData = reactive({
  realName: '',
  email: '',
  phone: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const validateConfirmPassword = async (rule, value) => {
  if (value !== passwordForm.newPassword) {
    throw new Error('两次输入的密码不一致')
  }
}

const passwordRules = {
  currentPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const fetchUserInfo = async () => {
  try {
    const res = await getCurrentUser()
    if (res.code === 200 && res.data) {
      const userData = res.data.user || res.data
      userInfo.value = userData
      formData.realName = userData.realName || ''
      formData.email = userData.email || ''
      formData.phone = userData.phone || ''
    }
  } catch (error) {
    message.error('获取用户信息失败')
  }
}

const beforeUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('只能上传 JPG/PNG 格式的图片！')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB！')
    return false
  }
  return true
}

const handleAvatarUpload = async (options) => {
  const { file } = options
  uploadLoading.value = true
  
  try {
    const res = await uploadAvatar(file)
    if (res.code === 200) {
      message.success('头像上传成功')
      userInfo.value = { ...userInfo.value, avatar: res.data.url }
      userStore.setAvatar(res.data.url)
    }
  } catch (error) {
    message.error('头像上传失败')
    console.error('上传失败:', error)
  } finally {
    uploadLoading.value = false
  }
}

const handleSubmit = async () => {
  loading.value = true
  try {
    const res = await updateProfile({
      realName: formData.realName,
      email: formData.email,
      phone: formData.phone
    })
    if (res.code === 200) {
      message.success('资料更新成功')
      userInfo.value = res.data
    }
  } catch (error) {
    message.error('资料更新失败')
    console.error('更新失败:', error)
  } finally {
    loading.value = false
  }
}

const handlePasswordSubmit = async () => {
  passwordLoading.value = true
  message.success('密码修改成功（演示）')
  passwordLoading.value = false
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.avatar-upload-wrapper {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.avatar-upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-upload-wrapper:hover .avatar-upload-overlay {
  opacity: 1;
}
</style>
