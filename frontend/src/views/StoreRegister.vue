<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { authApi, storeApi } from '../api'
import { useUserStore } from '../stores/user'
import {
  ShopOutlined,
  UserOutlined,
  LockOutlined,
  PhoneOutlined,
  ArrowLeftOutlined,
  CheckCircleOutlined,
  HomeOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const step = ref(1) // 1-注册账号, 2-填写店铺信息
const loading = ref(false)

const userForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  phone: ''
})

const storeForm = ref({
  storeName: '',
  description: '',
  address: '',
  logoUrl: ''
})

const canNext = computed(() => {
  return !!userForm.value.username && !!userForm.value.password && userForm.value.password === userForm.value.confirmPassword
})

const handleUserRegister = async () => {
  if (!userForm.value.username || !userForm.value.password) {
    message.warning('请填写用户名和密码')
    return
  }
  if (userForm.value.password !== userForm.value.confirmPassword) {
    message.warning('两次输入的密码不一致')
    return
  }
  step.value = 2
}

const handleStoreRegister = async () => {
  if (!storeForm.value.storeName) {
    message.warning('请输入店铺名称')
    return
  }

  loading.value = true
  try {
    await authApi.register({
      username: userForm.value.username,
      password: userForm.value.password,
      nickname: storeForm.value.storeName
    })

    const loginRes = await authApi.login({
      username: userForm.value.username,
      password: userForm.value.password
    })

    userStore.setUser(loginRes.accessToken, loginRes.username, loginRes.role, loginRes.nickname, loginRes.userId)

    await storeApi.register({
      storeName: storeForm.value.storeName,
      description: storeForm.value.description,
      phone: userForm.value.phone,
      address: storeForm.value.address
    })

    localStorage.setItem('hasStore', 'true')

    message.success('店家注册成功，请等待审核')
    router.push('/store/settings?uploadLogo=true')
  } catch (error: any) {
    message.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  if (step.value === 2) step.value = 1
  else router.push('/login')
}
</script>

<template>
  <div class="store-register-page">
    <!-- 顶部栏：更现代简朴，去掉大渐变铺满 -->
    <div class="topbar">
      <a-button type="link" class="back-btn" @click="goBack">
        <ArrowLeftOutlined /> {{ step === 2 ? '上一步' : '返回登录' }}
      </a-button>

      <div class="brand" @click="$router.push('/')">
        <span class="brand-logo"><ShopOutlined /></span>
        <span class="brand-title">店家入驻</span>
      </div>

      <a-button type="link" @click="$router.push('/')">
        <HomeOutlined /> 返回首页
      </a-button>
    </div>

    <div class="content">
      <a-card class="register-card" :bordered="false">
        <div class="card-head">
          <h1 class="title">成为店家</h1>
          <p class="subtitle">两步完成入驻，提交后等待管理员审核</p>
        </div>

        <a-steps :current="step - 1" size="small" class="steps">
          <a-step title="账号注册" />
          <a-step title="店铺信息" />
        </a-steps>

        <!-- Step 1 -->
        <div v-if="step === 1" class="form-area">
          <a-form layout="vertical">
            <a-form-item label="用户名" required>
              <a-input v-model:value="userForm.username" placeholder="请输入用户名" size="large">
                <template #prefix><UserOutlined /></template>
              </a-input>
            </a-form-item>

            <a-form-item label="密码" required>
              <a-input-password v-model:value="userForm.password" placeholder="请输入密码" size="large">
                <template #prefix><LockOutlined /></template>
              </a-input-password>
            </a-form-item>

            <a-form-item label="确认密码" required>
              <a-input-password v-model:value="userForm.confirmPassword" placeholder="请再次输入密码" size="large">
                <template #prefix><LockOutlined /></template>
              </a-input-password>
            </a-form-item>

            <a-form-item label="联系电话">
              <a-input v-model:value="userForm.phone" placeholder="请输入联系电话" size="large">
                <template #prefix><PhoneOutlined /></template>
              </a-input>
            </a-form-item>

            <a-button type="primary" size="large" block :disabled="!canNext" @click="handleUserRegister">
              下一步
            </a-button>
          </a-form>
        </div>

        <!-- Step 2 -->
        <div v-else class="form-area">

          <a-form layout="vertical">
            <a-form-item label="店铺名称" required>
              <a-input v-model:value="storeForm.storeName" placeholder="请输入店铺名称" size="large" />
            </a-form-item>

            <a-form-item label="店铺描述">
              <a-textarea v-model:value="storeForm.description" :rows="3" placeholder="请输入店铺描述" />
            </a-form-item>

            <a-form-item label="店铺地址">
              <a-input v-model:value="storeForm.address" placeholder="请输入店铺地址" size="large" />
            </a-form-item>

            <a-button type="primary" size="large" block :loading="loading" @click="handleStoreRegister">
              <CheckCircleOutlined /> 提交审核
            </a-button>
          </a-form>

          <div class="tips">
            <div class="tips-title">温馨提示</div>
            <ul>
              <li>提交后需要管理员审核，审核通过后即可上架商品</li>
              <li>所有商品都需要经过管理员审核才能展示给用户</li>
              <li>请确保填写的信息真实有效</li>
            </ul>
          </div>
        </div>
      </a-card>
    </div>
  </div>
</template>

<style scoped>
.store-register-page {
  min-height: 100vh;
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  display: flex;
  flex-direction: column;
}

.topbar {
  height: 64px;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  padding: 0 16px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #e5e7eb;
}

.back-btn {
  justify-self: start;
  padding: 0;
  font-weight: 600;
}

.brand {
  justify-self: center;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}

.brand-logo {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-title {
  font-weight: 900;
  color: #111827;
}

.content {
  flex: 1;
  display: grid;
  place-items: center;
  padding: 24px 16px;
}

.register-card {
  width: 100%;
  max-width: 520px;
  border-radius: 16px;
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.08);
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.card-head {
  margin-bottom: 10px;
}

.title {
  margin: 0;
  font-size: 24px;
  font-weight: 900;
  color: #111827;
}

.subtitle {
  margin: 8px 0 0;
  font-size: 13px;
  color: #6b7280;
}

.steps {
  margin: 12px 0 18px;
}

.form-area {
  margin-top: 4px;
}

.tips {
  margin-top: 16px;
  padding: 14px;
  border-radius: 12px;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
}

.tips-title {
  font-weight: 800;
  color: #389e0d;
  margin-bottom: 8px;
}

.tips ul {
  margin: 0;
  padding-left: 18px;
  color: #4b5563;
  font-size: 13px;
}

.tips li {
  margin-bottom: 6px;
}

/* 小屏处理 */
@media (max-width: 480px) {
  .topbar {
    grid-template-columns: 1fr auto;
    grid-template-areas:
      "back home"
      "brand brand";
    height: auto;
    gap: 8px;
    padding: 12px 16px;
  }
}
</style>
