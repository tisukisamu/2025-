<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { storeApi } from '../../api'
import {
  ShopOutlined,
  CheckCircleOutlined,
  ArrowLeftOutlined,
  UploadOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const loading = ref(false)
const formState = ref({
  storeName: '',
  description: '',
  phone: '',
  address: '',
  logoUrl: ''
})

const handleSubmit = async () => {
  if (!formState.value.storeName) {
    message.warning('请输入店铺名称')
    return
  }
  
  loading.value = true
  try {
    await storeApi.register(formState.value)
    message.success('店铺注册成功，请等待审核')
    router.push('/store/dashboard')
  } catch (error: any) {
    message.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}

const handleLogoUpload = (info: any) => {
  if (info.file.status === 'done') {
    formState.value.logoUrl = info.file.response
    message.success('上传成功')
  } else if (info.file.status === 'error') {
    message.error('上传失败')
  }
}

const goBack = () => router.back()
</script>

<template>
  <div class="store-register">
    <div class="register-header">
      <a-button type="link" @click="goBack">
        <ArrowLeftOutlined /> 返回
      </a-button>
      <h1>注册店铺</h1>
      <div style="width: 60px;"></div>
    </div>

    <div class="register-content">
      <a-card class="register-card">
        <div class="register-icon">
          <ShopOutlined />
        </div>
        <h2>开启您的店铺之旅</h2>
        <p class="subtitle">填写店铺信息，提交审核后即可开始销售商品</p>

        <a-form
          :model="formState"
          layout="vertical"
          class="register-form"
        >
          <a-form-item label="店铺Logo">
            <a-upload
              name="file"
              list-type="picture-card"
              :show-upload-list="false"
              action="/api/store/product/upload"
              @change="handleLogoUpload"
            >
              <img
                v-if="formState.logoUrl"
                :src="formState.logoUrl"
                alt="店铺Logo"
                style="width: 100%; height: 100%; object-fit: cover;"
              />
              <div v-else>
                <UploadOutlined />
                <div style="margin-top: 8px;">上传Logo</div>
              </div>
            </a-upload>
          </a-form-item>

          <a-form-item label="店铺名称" required>
            <a-input
              v-model:value="formState.storeName"
              placeholder="请输入店铺名称"
              size="large"
            />
          </a-form-item>

          <a-form-item label="店铺描述">
            <a-textarea
              v-model:value="formState.description"
              :rows="4"
              placeholder="请输入店铺描述，让顾客更好地了解您的店铺"
            />
          </a-form-item>

          <a-form-item label="联系电话">
            <a-input
              v-model:value="formState.phone"
              placeholder="请输入联系电话"
            />
          </a-form-item>

          <a-form-item label="店铺地址">
            <a-input
              v-model:value="formState.address"
              placeholder="请输入店铺地址"
            />
          </a-form-item>

          <a-form-item>
            <a-button
              type="primary"
              size="large"
              block
              :loading="loading"
              @click="handleSubmit"
            >
              <CheckCircleOutlined />
              提交审核
            </a-button>
          </a-form-item>
        </a-form>

        <div class="register-tips">
          <h4>温馨提示：</h4>
          <ul>
            <li>店铺名称提交后不可随意修改</li>
            <li>审核通常需要 1-3 个工作日</li>
            <li>请确保填写的联系方式准确无误</li>
            <li>审核通过后即可发布商品</li>
          </ul>
        </div>
      </a-card>
    </div>
  </div>
</template>

<style scoped>
.store-register {
  min-height: 100vh;
  background: #f5f5f5;
}

.register-header {
  background: white;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e8e8e8;
}

.register-header h1 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.register-content {
  max-width: 600px;
  margin: 40px auto;
  padding: 0 24px;
}

.register-card {
  padding: 40px;
}

.register-icon {
  width: 80px;
  height: 80px;
  background: #1890ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
}

.register-icon :deep(.anticon) {
  font-size: 40px;
  color: white;
}

.register-card h2 {
  text-align: center;
  margin-bottom: 8px;
}

.subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 32px;
}

.register-form {
  margin-top: 32px;
}

.register-tips {
  margin-top: 32px;
  padding: 16px;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 4px;
}

.register-tips h4 {
  margin: 0 0 8px 0;
  color: #52c41a;
}

.register-tips ul {
  margin: 0;
  padding-left: 20px;
  color: #666;
}

.register-tips li {
  margin-bottom: 4px;
}
</style>
