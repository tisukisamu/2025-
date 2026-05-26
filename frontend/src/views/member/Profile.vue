<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">个人信息</h1>
    </div>

    <a-row :gutter="24">
      <a-col :span="8">
        <div class="card profile-card">
          <div class="avatar-section">
            <a-avatar :size="100" :src="userStore.user?.avatar">
              {{ userStore.user?.realName?.charAt(0) }}
            </a-avatar>
            <a-upload
              :show-upload-list="false"
              :before-upload="beforeUpload"
              :custom-request="handleAvatarUpload"
            >
              <a-button type="link" :loading="avatarLoading">更换头像</a-button>
            </a-upload>
          </div>
          <div class="user-info">
            <h2>{{ userStore.user?.realName }}</h2>
            <p class="role-tag">
              <a-tag color="default">{{ userStore.user?.role?.roleName }}</a-tag>
            </p>
            <p class="info-item">
              <span class="label">用户名：</span>
              <span>{{ userStore.user?.username }}</span>
            </p>
            <p class="info-item">
              <span class="label">学号：</span>
              <span>{{ userStore.user?.studentId || '-' }}</span>
            </p>
            <p class="info-item">
              <span class="label">手机：</span>
              <span>{{ userStore.user?.phone || '-' }}</span>
            </p>
            <p class="info-item">
              <span class="label">邮箱：</span>
              <span>{{ userStore.user?.email || '-' }}</span>
            </p>
          </div>
        </div>
      </a-col>

      <a-col :span="16">
        <div class="card">
          <a-tabs v-model:activeKey="activeTab">
            <a-tab-pane key="info" tab="基本信息">
              <a-form
                ref="formRef"
                :model="formState"
                :rules="rules"
                layout="vertical"
                class="profile-form"
              >
                <a-row :gutter="16">
                  <a-col :span="12">
                    <a-form-item label="真实姓名" name="realName">
                      <a-input v-model:value="formState.realName" />
                    </a-form-item>
                  </a-col>
                  <a-col :span="12">
                    <a-form-item label="学号" name="studentId">
                      <a-input v-model:value="formState.studentId" disabled />
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-row :gutter="16">
                  <a-col :span="12">
                    <a-form-item label="手机号" name="phone">
                      <a-input v-model:value="formState.phone" />
                    </a-form-item>
                  </a-col>
                  <a-col :span="12">
                    <a-form-item label="邮箱" name="email">
                      <a-input v-model:value="formState.email" />
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-form-item>
                  <a-button type="primary" :loading="loading" @click="handleSave">
                    保存修改
                  </a-button>
                </a-form-item>
              </a-form>
            </a-tab-pane>

            <a-tab-pane key="password" tab="修改密码">
              <a-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                layout="vertical"
                class="profile-form"
              >
                <a-form-item label="原密码" name="oldPassword">
                  <a-input-password v-model:value="passwordForm.oldPassword" />
                </a-form-item>
                <a-form-item label="新密码" name="newPassword">
                  <a-input-password v-model:value="passwordForm.newPassword" />
                </a-form-item>
                <a-form-item label="确认密码" name="confirmPassword">
                  <a-input-password v-model:value="passwordForm.confirmPassword" />
                </a-form-item>
                <a-form-item>
                  <a-button type="primary" :loading="passwordLoading" @click="handleChangePassword">
                    修改密码
                  </a-button>
                </a-form-item>
              </a-form>
            </a-tab-pane>
          </a-tabs>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message, type FormInstance, type Rule } from 'ant-design-vue'
import { useUserStore } from '@/stores'
import { userApi, uploadApi } from '@/api'

const userStore = useUserStore()

const activeTab = ref('info')
const loading = ref(false)
const passwordLoading = ref(false)
const avatarLoading = ref(false)
const formRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

const formState = reactive({
  realName: '',
  studentId: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules: Record<string, Rule[]> = {
  realName: [{ required: true, message: '请输入真实姓名' }]
}

const validateConfirmPassword = async (_rule: Rule, value: string) => {
  if (value !== passwordForm.newPassword) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

const passwordRules: Record<string, Rule[]> = {
  oldPassword: [{ required: true, message: '请输入原密码' }],
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 6, message: '密码长度不能少于6位' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    { validator: validateConfirmPassword }
  ]
}

const initForm = () => {
  if (userStore.user) {
    formState.realName = userStore.user.realName || ''
    formState.studentId = userStore.user.studentId || ''
    formState.phone = userStore.user.phone || ''
    formState.email = userStore.user.email || ''
  }
}

const handleSave = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true
    await userApi.updateProfile(formState)
    await userStore.fetchUserInfo()
    message.success('保存成功')
  } catch (error: any) {
    message.error(error.message || '保存失败')
  } finally {
    loading.value = false
  }
}

const handleChangePassword = async () => {
  try {
    await passwordFormRef.value?.validate()
    passwordLoading.value = true
    await userApi.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    message.success('密码修改成功')
    passwordFormRef.value?.resetFields()
  } catch (error: any) {
    message.error(error.message || '修改失败')
  } finally {
    passwordLoading.value = false
  }
}

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    message.error('图片大小不能超过5MB')
    return false
  }
  return true
}

const handleAvatarUpload = async (options: any) => {
  const file = options.file as File
  avatarLoading.value = true
  try {
    const uploadRes = await uploadApi.uploadAvatar(file)
    const uploadedUrl = uploadRes.data.url
    const avatar = uploadedUrl.startsWith('/upload/') ? uploadedUrl : `/upload${uploadedUrl}`
    await userApi.updateProfile({ avatar })
    await userStore.fetchUserInfo()
    options.onSuccess?.(uploadRes, file)
    message.success('头像更新成功')
  } catch (error: any) {
    options.onError?.(error)
    message.error(error.message || '头像上传失败')
  } finally {
    avatarLoading.value = false
  }
}

onMounted(() => {
  initForm()
})
</script>

<style scoped>
.profile-card {
  text-align: center;
}

.avatar-section {
  padding: 24px 0;
  border-bottom: 1px solid #f0f0f0;
}

.user-info {
  padding: 24px 0;
}

.user-info h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.role-tag {
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  color: #666;
}

.profile-form {
  max-width: 500px;
  padding: 24px 0;
}
</style>
