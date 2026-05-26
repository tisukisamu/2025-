<template>
  <div>
    <a-page-header title="个人中心" />
    
    <a-row :gutter="24">
      <a-col :span="8">
        <a-card class="text-center">
          <div class="py-5">
            <a-avatar :size="100" :src="resolveAvatarUrl(formData.avatarUrl)" :style="{ backgroundColor: userStore.isAdmin ? '#262626' : '#111111' }">
              {{ userStore.username.charAt(0).toUpperCase() }}
            </a-avatar>
            <div class="mt-3">
              <a-upload :show-upload-list="false" :before-upload="handleAvatarUpload">
                <a-button size="small" :loading="uploading">上传头像</a-button>
              </a-upload>
            </div>
            <h3 class="mt-4 mb-2">{{ userInfo?.name }}</h3>
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
              <span class="text-gray-500">年龄</span>
              <span class="text-gray-800 font-medium">{{ userInfo?.age }}</span>
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
            <a-form-item label="姓名" name="name">
              <a-input v-model:value="formData.name" placeholder="请输入姓名" />
            </a-form-item>
            
            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
            </a-form-item>
            
            <a-form-item label="年龄" name="age">
              <a-input-number v-model:value="formData.age" :min="1" :max="150" style="width: 100%" />
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
import { useUserStore } from '../stores/user'
import { getCurrentUser } from '../api/auth'
import { getUserById, updateMyProfile } from '../api/user'
import { fileApi } from '../api'
import { resolveAvatarUrl } from '../utils/media'

const userStore = useUserStore()
const loading = ref(false)
const passwordLoading = ref(false)
const uploading = ref(false)
const userInfo = ref(null)

const formData = reactive({
  name: '',
  email: '',
  age: null,
  avatarUrl: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }]
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
    if (res.code === 200) {
      let profile = res.data
      if (profile.age == null && profile.id) {
        const detailRes = await getUserById(profile.id)
        profile = {
          ...profile,
          ...detailRes.data
        }
      }
      userInfo.value = profile
      formData.name = profile.name
      formData.email = profile.email
      formData.age = profile.age
      formData.avatarUrl = profile.avatarUrl || ''
      userStore.setUserInfo(profile)
    }
  } catch (error) {
    message.error('获取用户信息失败')
  }
}

const saveProfile = async () => {
  const payload = {
    name: formData.name,
    email: formData.email,
    age: formData.age,
    avatarUrl: formData.avatarUrl
  }
  const res = await updateMyProfile(payload)
  userInfo.value = res.data
  userStore.setUserInfo({
    ...userStore.userInfo,
    ...res.data
  })
}

const handleSubmit = async () => {
  loading.value = true
  try {
    await saveProfile()
    message.success('资料更新成功')
  } catch (error) {
    message.error('资料更新失败')
  } finally {
    loading.value = false
  }
}

const handlePasswordSubmit = async () => {
  passwordLoading.value = true
  message.success('密码修改成功（演示）')
  passwordLoading.value = false
}

const handleAvatarUpload = async (file) => {
  try {
    uploading.value = true
    const res = await fileApi.uploadImage(file, 'avatar')
    formData.avatarUrl = res.data.path
    if (!formData.name || !formData.email || formData.age == null) {
      await fetchUserInfo()
    }
    await saveProfile()
    message.success('头像上传并保存成功')
  } catch (error) {
    message.error('头像上传成功，但保存资料失败，请点击“保存修改”')
  } finally {
    uploading.value = false
  }
  return false
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchUserInfo()
})
</script>
