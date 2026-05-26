<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { storeApi } from '../../api'
import { useUserStore } from '../../stores/user'
import {
  ShopOutlined,
  SaveOutlined,
  UploadOutlined
} from '@ant-design/icons-vue'

const userStore = useUserStore()
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

const storeInfo = ref({
  storeName: '',
  description: '',
  phone: '',
  address: '',
  logoUrl: ''
})
const loading = ref(false)
const saving = ref(false)

onMounted(() => {
  loadStoreInfo()
})

const loadStoreInfo = async () => {
  loading.value = true
  try {
    const res = await storeApi.getMyStore()
    if (res) {
      storeInfo.value = res
    }
  } catch (error) {
    message.error('加载店铺信息失败')
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  saving.value = true
  try {
    await storeApi.updateStore(storeInfo.value)
    message.success('保存成功')
  } catch (error) {
    message.error('保存失败')
  } finally {
    saving.value = false
  }
}

const handleLogoUpload = (info: any) => {
  if (info.file.status === 'done') {
    storeInfo.value.logoUrl = info.file.response
    message.success('上传成功')
  } else if (info.file.status === 'error') {
    message.error('上传失败')
  }
}
</script>

<template>
  <div class="store-settings">
    <h2>店铺设置</h2>
    
    <a-card :loading="loading">
      <a-form
        :model="storeInfo"
        layout="vertical"
        style="max-width: 600px;"
      >
        <a-form-item label="店铺Logo">
          <a-upload
            name="file"
            list-type="picture-card"
            :show-upload-list="false"
            action="/api/store/product/upload"
            :headers="uploadHeaders"
            @change="handleLogoUpload"
          >
            <img
              v-if="storeInfo.logoUrl"
              :src="storeInfo.logoUrl"
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
            v-model:value="storeInfo.storeName"
            placeholder="请输入店铺名称"
          />
        </a-form-item>

        <a-form-item label="店铺描述">
          <a-textarea
            v-model:value="storeInfo.description"
            :rows="4"
            placeholder="请输入店铺描述"
          />
        </a-form-item>

        <a-form-item label="联系电话">
          <a-input
            v-model:value="storeInfo.phone"
            placeholder="请输入联系电话"
          />
        </a-form-item>

        <a-form-item label="店铺地址">
          <a-input
            v-model:value="storeInfo.address"
            placeholder="请输入店铺地址"
          />
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            :loading="saving"
            @click="handleSave"
          >
            <SaveOutlined /> 保存设置
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<style scoped>
.store-settings {
  padding: 24px;
}

.store-settings h2 {
  margin-bottom: 24px;
}
</style>
