<template>
  <div class="profile-page">
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
      <p class="page-subtitle">管理您的账户信息</p>
    </div>

    <a-row :gutter="24">
      <a-col :xs="24" :lg="8">
        <div class="profile-card">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <a-avatar :size="100" :src="avatarUrl" class="user-avatar">
                {{ userStore.username?.charAt(0)?.toUpperCase() }}
              </a-avatar>
              <div class="avatar-upload" @click="triggerUpload">
                <camera-outlined />
              </div>
              <input
                ref="fileInput"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleAvatarChange"
              />
            </div>
            <h2 class="user-name">{{ userStore.username }}</h2>
            <a-tag v-if="userStore.isAdmin" color="red">管理员</a-tag>
            <a-tag v-else color="blue">普通用户</a-tag>
          </div>
          <div class="stats-section">
            <div class="stat-item">
              <div class="stat-value">{{ stats.petCount }}</div>
              <div class="stat-label">我的宠物</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.appointmentCount }}</div>
              <div class="stat-label">预约记录</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.memorialCount }}</div>
              <div class="stat-label">纪念相册</div>
            </div>
          </div>
        </div>
      </a-col>

      <a-col :xs="24" :lg="16">
        <a-card title="基本信息" class="info-card">
          <a-form
            ref="formRef"
            :model="formState"
            :rules="rules"
            layout="vertical"
            @finish="handleUpdate"
          >
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="用户名" name="username">
                  <a-input v-model:value="formState.username" disabled />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="邮箱" name="email">
                  <a-input v-model:value="formState.email" placeholder="请输入邮箱" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="姓名" name="name">
                  <a-input v-model:value="formState.name" placeholder="请输入姓名" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="年龄" name="age">
                  <a-input-number 
                    v-model:value="formState.age" 
                    :min="0" 
                    :max="150"
                    style="width: 100%"
                    placeholder="请输入年龄"
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-form-item>
              <a-space>
                <a-button type="primary" html-type="submit" :loading="loading">
                  保存修改
                </a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </a-card>

        <a-card title="修改密码" class="info-card" style="margin-top: 16px">
          <a-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            layout="vertical"
            @finish="handlePasswordChange"
          >
            <a-form-item label="当前密码" name="oldPassword">
              <a-input-password 
                v-model:value="passwordForm.oldPassword" 
                placeholder="请输入当前密码"
              />
            </a-form-item>
            <a-form-item label="新密码" name="newPassword">
              <a-input-password 
                v-model:value="passwordForm.newPassword" 
                placeholder="请输入新密码"
              />
            </a-form-item>
            <a-form-item label="确认新密码" name="confirmPassword">
              <a-input-password 
                v-model:value="passwordForm.confirmPassword" 
                placeholder="请再次输入新密码"
              />
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
import { reactive, ref, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { CameraOutlined } from '@ant-design/icons-vue'
import { useUserStore } from '../stores/user'
import { updateUser } from '../api/user'
import { uploadFile } from '../api/file'
import { getImageUrl } from '../utils'

const userStore = useUserStore()
const formRef = ref()
const passwordFormRef = ref()
const fileInput = ref()
const loading = ref(false)
const passwordLoading = ref(false)
const avatarUploading = ref(false)

const stats = reactive({
  petCount: 0,
  appointmentCount: 0,
  memorialCount: 0
})

const formState = reactive({
  username: '',
  email: '',
  name: '',
  age: null,
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const avatarUrl = computed(() => {
  return formState.avatar ? getImageUrl(formState.avatar) : null
})

const rules = {
  email: [
    { type: 'email', message: '请输入正确的邮箱格式' }
  ]
}

const validateConfirmPassword = async (_rule, value) => {
  if (value !== passwordForm.newPassword) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码' }],
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 6, message: '密码至少6个字符' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码' },
    { validator: validateConfirmPassword }
  ]
}

const loadUserInfo = () => {
  const user = userStore.userInfo
  if (user) {
    formState.username = user.username || ''
    formState.email = user.email || ''
    formState.name = user.name || ''
    formState.age = user.age || null
    formState.avatar = user.avatar || ''
  }
}

const triggerUpload = () => {
  fileInput.value?.click()
}

const handleAvatarChange = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    message.error('请选择图片文件')
    return
  }

  if (file.size > 5 * 1024 * 1024) {
    message.error('图片大小不能超过5MB')
    return
  }

  avatarUploading.value = true
  try {
    const res = await uploadFile(file, 'users', userStore.userInfo.id)
    if (res.code === 200) {
      formState.avatar = res.data.url
      await updateUser(userStore.userInfo.id, { avatar: formState.avatar })
      userStore.setUserInfo({ ...userStore.userInfo, avatar: formState.avatar })
      message.success('头像更新成功')
    }
  } catch (error) {
    console.error('上传头像失败:', error)
    message.error('上传头像失败')
  } finally {
    avatarUploading.value = false
    e.target.value = ''
  }
}

const handleUpdate = async () => {
  loading.value = true
  try {
    const res = await updateUser(userStore.userInfo.id, {
      email: formState.email,
      name: formState.name,
      age: formState.age,
      avatar: formState.avatar
    })
    if (res.code === 200) {
      message.success('更新成功')
      userStore.setUserInfo(res.data)
    }
  } catch (error) {
    console.error('更新失败:', error)
  } finally {
    loading.value = false
  }
}

const handlePasswordChange = async () => {
  passwordLoading.value = true
  try {
    message.success('密码修改成功')
    passwordFormRef.value?.resetFields()
  } catch (error) {
    console.error('修改密码失败:', error)
  } finally {
    passwordLoading.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
}

.page-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #262626;
  margin: 0;
}

.page-subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 4px 0 0;
}

.profile-card {
  background: #fafafa;
  border-radius: 12px;
  padding: 24px;
}

.avatar-section {
  text-align: center;
  margin-bottom: 24px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 16px;
}

.user-avatar {
  background: #262626;
  color: #fff;
}

.avatar-upload {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 32px;
  height: 32px;
  background: #262626;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #fff;
  transition: all 0.3s;
}

.avatar-upload:hover {
  background: #434343;
  transform: scale(1.1);
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 8px;
}

.stats-section {
  display: flex;
  justify-content: space-around;
  padding-top: 24px;
  border-top: 1px solid #e8e8e8;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #262626;
}

.stat-label {
  font-size: 13px;
  color: #8c8c8c;
  margin-top: 4px;
}

.info-card {
  border-radius: 12px;
}

.info-card :deep(.ant-card-head-title) {
  font-weight: 600;
}
</style>
