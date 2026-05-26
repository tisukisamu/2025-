<template>
  <div class="buy-request-detail-page">
    <a-spin :spinning="loading">
      <template v-if="request">
        <div class="back-nav">
          <a-button type="text" @click="router.back()">
            <left-outlined /> 返回
          </a-button>
        </div>

        <a-card class="detail-card">
          <div class="detail-header">
            <h1>{{ request.title }}</h1>
            <a-tag :color="getStatusColor(request.status)">
              {{ getStatusText(request.status) }}
            </a-tag>
          </div>

          <div class="author-info">
            <a-avatar :src="request.userAvatar" :style="{ backgroundColor: '#1890ff' }">
              {{ request.userName?.charAt(0) }}
            </a-avatar>
            <div class="author-detail">
              <span class="author-name">{{ request.userName }}</span>
              <span class="publish-time">发布于 {{ formatTime(request.createTime) }}</span>
            </div>
          </div>

          <div class="detail-content">
            <p>{{ request.description || '暂无详细描述' }}</p>
          </div>

          <div class="detail-meta">
            <div class="meta-item" v-if="request.category">
              <span class="label">商品类别</span>
              <span class="value">{{ request.category }}</span>
            </div>
            <div class="meta-item" v-if="request.expectedCondition">
              <span class="label">期望成色</span>
              <span class="value">{{ request.expectedCondition }}</span>
            </div>
            <div class="meta-item">
              <span class="label">预算范围</span>
              <span class="value">
                {{ request.budgetMin ? '¥' + request.budgetMin : '不限' }} - 
                {{ request.budgetMax ? '¥' + request.budgetMax : '不限' }}
              </span>
            </div>
            <div class="meta-item" v-if="request.contactInfo">
              <span class="label">联系方式</span>
              <span class="value">{{ request.contactInfo }}</span>
            </div>
            <div class="meta-item" v-if="request.expireTime">
              <span class="label">有效期至</span>
              <span class="value">{{ new Date(request.expireTime).toLocaleDateString('zh-CN') }}</span>
            </div>
          </div>

          <div class="detail-stats">
            <span><eye-outlined /> {{ request.viewCount }} 浏览</span>
            <span><message-outlined /> {{ request.responseCount }} 响应</span>
          </div>

          <div class="detail-actions" v-if="request.status === 'OPEN'">
            <a-button type="primary" @click="showResponseModal = true" v-if="!isOwner">
              我有此商品
            </a-button>
            <a-button @click="handleClose" v-if="isOwner">关闭求购</a-button>
          </div>
        </a-card>

        <a-card title="响应列表" class="responses-card">
          <div class="response-list">
            <div v-for="response in responses" :key="response.id" class="response-item">
              <div class="response-header">
                <a-avatar :src="response.responderAvatar" :style="{ backgroundColor: '#52c41a' }">
                  {{ response.responderName?.charAt(0) }}
                </a-avatar>
                <div class="responder-info">
                  <span class="responder-name">{{ response.responderName }}</span>
                  <span class="response-time">{{ formatTime(response.createTime) }}</span>
                </div>
                <a-tag :color="getResponseStatusColor(response.status)">
                  {{ getResponseStatusText(response.status) }}
                </a-tag>
              </div>
              <div class="response-content" v-if="response.message">
                {{ response.message }}
              </div>
              <div class="response-product" v-if="response.productId" @click="router.push(`/products/${response.productId}`)">
                <img :src="response.productImage || defaultImage" class="product-image" />
                <div class="product-info">
                  <span class="product-title">{{ response.productTitle }}</span>
                  <span class="product-price" v-if="response.offeredPrice">¥{{ response.offeredPrice }}</span>
                </div>
              </div>
              <div class="response-actions" v-if="isOwner && response.status === 'PENDING'">
                <a-button type="primary" size="small" @click="handleAcceptResponse(response.id)">
                  接受
                </a-button>
                <a-button size="small" @click="handleRejectResponse(response.id)">
                  拒绝
                </a-button>
              </div>
            </div>
          </div>
          <a-empty v-if="responses.length === 0" description="暂无响应" />
        </a-card>
      </template>
    </a-spin>

    <a-modal
      v-model:open="showResponseModal"
      title="响应求购"
      @ok="handleResponse"
      :confirmLoading="submitting"
    >
      <a-form layout="vertical">
        <a-form-item label="关联商品（可选）">
          <a-select v-model:value="responseForm.productId" placeholder="选择您的商品" allowClear>
            <a-select-option v-for="p in myProducts" :key="p.id" :value="p.id">
              {{ p.title }} - ¥{{ p.price }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="留言">
          <a-textarea v-model:value="responseForm.message" placeholder="给买家留言" :rows="3" />
        </a-form-item>
        <a-form-item label="报价">
          <a-input-number v-model:value="responseForm.offeredPrice" :min="0" :precision="2" style="width: 100%" placeholder="您的报价" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { buyRequestApi } from '@/api/extra'
import { productApi } from '@/api'
import { message } from 'ant-design-vue'
import { LeftOutlined, EyeOutlined, MessageOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const request = ref<any>(null)
const responses = ref<any[]>([])
const showResponseModal = ref(false)
const myProducts = ref<any[]>([])

const defaultImage = '/no-image.svg'

const responseForm = reactive({
  productId: null as number | null,
  message: '',
  offeredPrice: null as number | null
})

const isOwner = computed(() => request.value?.userId === userStore.userInfo?.id)

const formatTime = (time: string) => {
  return new Date(time).toLocaleString('zh-CN')
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'OPEN': '进行中', 'IN_PROGRESS': '洽谈中',
    'CLOSED': '已关闭', 'FULFILLED': '已完成'
  }
  return map[status] || status
}

const getStatusColor = (status: string) => {
  const map: Record<string, string> = {
    'OPEN': 'green', 'IN_PROGRESS': 'blue',
    'CLOSED': 'default', 'FULFILLED': 'success'
  }
  return map[status] || 'default'
}

const getResponseStatusText = (status: string) => {
  const map: Record<string, string> = {
    'PENDING': '待处理', 'ACCEPTED': '已接受',
    'REJECTED': '已拒绝', 'WITHDRAWN': '已撤回'
  }
  return map[status] || status
}

const getResponseStatusColor = (status: string) => {
  const map: Record<string, string> = {
    'PENDING': 'orange', 'ACCEPTED': 'green',
    'REJECTED': 'red', 'WITHDRAWN': 'default'
  }
  return map[status] || 'default'
}

const fetchRequest = async () => {
  const id = Number(route.params.id)
  loading.value = true
  try {
    const res = await buyRequestApi.getBuyRequestById(id)
    request.value = res.data
    fetchResponses()
  } finally {
    loading.value = false
  }
}

const fetchResponses = async () => {
  const id = Number(route.params.id)
  try {
    const res = await buyRequestApi.getResponses(id, { page: 1, size: 100 })
    responses.value = res.data.list
  } catch {
    // ignore
  }
}

const fetchMyProducts = async () => {
  try {
    const res = await productApi.getMyProducts({ page: 1, size: 100 })
    myProducts.value = res.data.list
  } catch {
    // ignore
  }
}

const handleClose = async () => {
  try {
    await buyRequestApi.closeBuyRequest(request.value.id)
    message.success('已关闭')
    fetchRequest()
  } catch {
    // ignore
  }
}

const handleResponse = async () => {
  submitting.value = true
  try {
    await buyRequestApi.createResponse(request.value.id, responseForm)
    message.success('响应成功')
    showResponseModal.value = false
    Object.assign(responseForm, { productId: null, message: '', offeredPrice: null })
    fetchResponses()
  } finally {
    submitting.value = false
  }
}

const handleAcceptResponse = async (responseId: number) => {
  try {
    await buyRequestApi.acceptResponse(responseId)
    message.success('已接受')
    fetchRequest()
  } catch {
    // ignore
  }
}

const handleRejectResponse = async (responseId: number) => {
  try {
    await buyRequestApi.rejectResponse(responseId)
    message.success('已拒绝')
    fetchResponses()
  } catch {
    // ignore
  }
}

onMounted(() => {
  fetchRequest()
  if (userStore.isLoggedIn) {
    fetchMyProducts()
  }
})
</script>

<style scoped>
.buy-request-detail-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.back-nav {
  margin-bottom: 16px;
}

.back-nav :deep(.ant-btn) {
  color: #1890ff;
}

.detail-card, .responses-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.detail-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.author-detail {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 500;
}

.publish-time {
  font-size: 13px;
  color: #999;
}

.detail-content {
  padding: 16px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.detail-content p {
  margin: 0;
  color: #666;
  line-height: 1.8;
}

.detail-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-item .label {
  font-size: 13px;
  color: #999;
}

.meta-item .value {
  font-weight: 500;
}

.detail-stats {
  display: flex;
  gap: 24px;
  color: #999;
  font-size: 14px;
  margin-bottom: 16px;
}

.detail-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-actions {
  display: flex;
  gap: 12px;
}

.response-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.response-item {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.response-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.responder-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.responder-name {
  font-weight: 500;
}

.response-time {
  font-size: 12px;
  color: #999;
}

.response-content {
  color: #666;
  margin-bottom: 12px;
}

.response-product {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  cursor: pointer;
}

.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.product-title {
  font-weight: 500;
  margin-bottom: 4px;
}

.product-price {
  color: #f5222d;
  font-weight: 600;
}

.response-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}
</style>
