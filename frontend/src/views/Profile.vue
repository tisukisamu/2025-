<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-#111111 text-white p-5 flex items-center justify-between">
      <div>
        <h1 class="text-22px m-0">个人中心</h1>
        <p class="m-0 mt-2 text-#9ca3af">管理您的个人信息和账户设置</p>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
        <div class="text-center py-4">
          <a-upload
            name="file"
            :show-upload-list="false"
            :custom-request="handleAvatarUpload"
          >
            <div class="cursor-pointer">
              <a-avatar :size="100" class="bg-#111111">
                <template #icon>
                  <user-outlined v-if="!userStore.userInfo?.avatar" class="text-40px" />
                  <img v-else :src="userStore.userInfo.avatar" alt="avatar" class="w-full h-full object-cover" />
                </template>
              </a-avatar>
              <div class="mt-2 text-#6b7280 text-sm hover:text-#111111 transition-colors">
                <camera-outlined class="mr-1" />更换头像
              </div>
            </div>
          </a-upload>
          <h3 class="mt-4 mb-2 text-xl font-600 text-#111827">{{ userInfo?.name }}</h3>
          <p class="text-#6b7280 mb-3">@{{ userInfo?.username }}</p>
          <div class="flex justify-center gap-2">
            <a-tag :color="userStore.isAdmin ? 'red' : 'blue'">
              {{ userStore.isAdmin ? '管理员' : '普通用户' }}
            </a-tag>
            <a-tag :color="userStore.isActive ? 'green' : 'orange'">
              {{ userStore.isActive ? '正常' : '已禁用' }}
            </a-tag>
          </div>
        </div>
        
        <a-divider />
        
        <div class="space-y-3">
          <div class="flex justify-between py-2">
            <span class="text-#6b7280">邮箱</span>
            <span class="text-#111827">{{ userInfo?.email || '未设置' }}</span>
          </div>
          <div class="flex justify-between py-2">
            <span class="text-#6b7280">年龄</span>
            <span class="text-#111827">{{ userInfo?.age }} 岁</span>
          </div>
          <div class="flex justify-between py-2">
            <span class="text-#6b7280">注册时间</span>
            <span class="text-#111827">{{ formatDate(userInfo?.createdAt) }}</span>
          </div>
        </div>
      </a-card>

      <div class="md:col-span-2 space-y-4">
        <a-card title="编辑资料" :bordered="false" class="border border-#e5e7eb rounded-xl">
          <a-form ref="formRef" layout="vertical" :model="formData" :rules="rules">
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="姓名" name="name">
                  <a-input v-model:value="formData.name" placeholder="请输入姓名" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="年龄" name="age">
                  <a-input-number v-model:value="formData.age" :min="1" :max="150" style="width: 100%" />
                </a-form-item>
              </a-col>
            </a-row>
            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" class="bg-black border-black text-white" :loading="loading" @click="handleSubmit">
                保存修改
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>

        <a-card title="修改密码" :bordered="false" class="border border-#e5e7eb rounded-xl">
          <a-form ref="passwordFormRef" layout="vertical" :model="passwordForm" :rules="passwordRules">
            <a-form-item label="当前密码" name="currentPassword">
              <a-input-password v-model:value="passwordForm.currentPassword" placeholder="请输入当前密码" />
            </a-form-item>
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="新密码" name="newPassword">
                  <a-input-password v-model:value="passwordForm.newPassword" placeholder="请输入新密码" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="确认密码" name="confirmPassword">
                  <a-input-password v-model:value="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
                </a-form-item>
              </a-col>
            </a-row>
            <a-form-item>
              <a-button type="primary" class="bg-black border-black text-white" :loading="passwordLoading" @click="handlePasswordSubmit">
                修改密码
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { UploadRequestOption } from 'ant-design-vue'
import { useUserStore } from '../stores/user'
import { getCurrentUser } from '../api/auth'
import { uploadAvatar } from '../api/upload'
import { UserOutlined, CameraOutlined } from '@ant-design/icons-vue'

const userStore = useUserStore()
const loading = ref(false)
const passwordLoading = ref(false)
const userInfo = ref<any>(null)
const formRef = ref()
const passwordFormRef = ref()

const formData = reactive({
  name: '',
  email: '',
  age: null as number | null
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

const validateConfirmPassword = async (_rule: any, value: string) => {
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
      userInfo.value = res.data
      formData.name = res.data.name
      formData.email = res.data.email
      formData.age = res.data.age
    }
  } catch (error) {
    message.error('获取用户信息失败')
  }
}

const handleAvatarUpload = async (options: UploadRequestOption) => {
  const { file } = options
  try {
    const res = await uploadAvatar(file as File)
    if (res.code === 200) {
      userInfo.value.avatar = res.data.url
      userStore.userInfo.avatar = res.data.url
      message.success('头像上传成功')
    }
  } catch (error) {
    message.error('头像上传失败')
  }
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  loading.value = true
  try {
    message.success('资料更新成功')
  } finally {
    loading.value = false
  }
}

const handlePasswordSubmit = async () => {
  await passwordFormRef.value?.validate()
  passwordLoading.value = true
  try {
    message.success('密码修改成功')
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } finally {
    passwordLoading.value = false
  }
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchUserInfo()
})
</script>
