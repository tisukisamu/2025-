<template>
  <div class="buy-request-page">
    <div class="page-header">
      <h1>求购专区</h1>
      <a-button type="primary" @click="showCreateModal = true">
        <plus-outlined /> 发布求购
      </a-button>
    </div>

    <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
      <a-tab-pane key="all" tab="全部求购" />
      <a-tab-pane key="OPEN" tab="进行中" />
      <a-tab-pane key="IN_PROGRESS" tab="洽谈中" />
      <a-tab-pane key="CLOSED" tab="已关闭" />
    </a-tabs>

    <a-spin :spinning="loading">
      <div class="request-list">
        <div
          v-for="request in requests"
          :key="request.id"
          class="request-card"
          @click="router.push(`/buy-requests/${request.id}`)"
        >
          <div class="request-header">
            <a-avatar :src="request.userAvatar" :style="{ backgroundColor: '#1890ff' }">
              {{ request.userName?.charAt(0) }}
            </a-avatar>
            <div class="user-info">
              <span class="user-name">{{ request.userName }}</span>
              <span class="create-time">{{ formatTime(request.createTime) }}</span>
            </div>
            <a-tag :color="getStatusColor(request.status)">
              {{ getStatusText(request.status) }}
            </a-tag>
          </div>
          <h3 class="request-title">{{ request.title }}</h3>
          <p class="request-desc">{{ request.description }}</p>
          <div class="request-meta">
            <span v-if="request.category">
              <tag-outlined /> {{ request.category }}
            </span>
            <span v-if="request.budgetMin || request.budgetMax">
              <dollar-outlined /> 
              {{ request.budgetMin ? '¥' + request.budgetMin : '不限' }} - 
              {{ request.budgetMax ? '¥' + request.budgetMax : '不限' }}
            </span>
          </div>
          <div class="request-footer">
            <span><eye-outlined /> {{ request.viewCount }}</span>
            <span><message-outlined /> {{ request.responseCount }} 响应</span>
          </div>
        </div>
      </div>
      <a-empty v-if="requests.length === 0" description="暂无求购信息" />
    </a-spin>

    <div class="pagination-wrapper" v-if="total > pageSize">
      <a-pagination
        v-model:current="page"
        :total="total"
        :page-size="pageSize"
        show-quick-jumper
        @change="fetchRequests"
      />
    </div>

    <a-modal
      v-model:open="showCreateModal"
      title="发布求购"
      @ok="handleCreate"
      :confirmLoading="submitting"
      width="600px"
    >
      <a-form layout="vertical">
        <a-form-item label="求购标题" required>
          <a-input v-model:value="createForm.title" placeholder="请输入求购标题" maxlength="100" />
        </a-form-item>
        <a-form-item label="详细描述">
          <a-textarea v-model:value="createForm.description" placeholder="请描述您想要的商品" :rows="4" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="商品类别">
              <a-select v-model:value="createForm.category" placeholder="选择类别" allowClear>
                <a-select-option value="手机">手机</a-select-option>
                <a-select-option value="电脑">电脑</a-select-option>
                <a-select-option value="平板">平板</a-select-option>
                <a-select-option value="相机">相机</a-select-option>
                <a-select-option value="耳机">耳机</a-select-option>
                <a-select-option value="配件">配件</a-select-option>
                <a-select-option value="其他">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="期望成色">
              <a-select v-model:value="createForm.expectedCondition" placeholder="选择成色" allowClear>
                <a-select-option value="全新">全新</a-select-option>
                <a-select-option value="几乎全新">几乎全新</a-select-option>
                <a-select-option value="良好">良好</a-select-option>
                <a-select-option value="一般">一般</a-select-option>
                <a-select-option value="不限">不限</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="最低预算">
              <a-input-number v-model:value="createForm.budgetMin" :min="0" :precision="2" style="width: 100%" placeholder="最低价格" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="最高预算">
              <a-input-number v-model:value="createForm.budgetMax" :min="0" :precision="2" style="width: 100%" placeholder="最高价格" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="联系方式">
          <a-input v-model:value="createForm.contactInfo" placeholder="微信/QQ/手机号" />
        </a-form-item>
        <a-form-item label="有效期">
          <a-select v-model:value="createForm.expireDays" placeholder="选择有效期" allowClear>
            <a-select-option :value="7">7天</a-select-option>
            <a-select-option :value="14">14天</a-select-option>
            <a-select-option :value="30">30天</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { buyRequestApi } from '@/api/extra'
import { message } from 'ant-design-vue'
import { PlusOutlined, TagOutlined, DollarOutlined, EyeOutlined, MessageOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const requests = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const activeTab = ref('all')
const showCreateModal = ref(false)

const createForm = reactive({
  title: '',
  description: '',
  category: '',
  budgetMin: null as number | null,
  budgetMax: null as number | null,
  expectedCondition: '',
  contactInfo: '',
  expireDays: 7
})

const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'OPEN': '进行中',
    'IN_PROGRESS': '洽谈中',
    'CLOSED': '已关闭',
    'FULFILLED': '已完成'
  }
  return map[status] || status
}

const getStatusColor = (status: string) => {
  const map: Record<string, string> = {
    'OPEN': 'green',
    'IN_PROGRESS': 'blue',
    'CLOSED': 'default',
    'FULFILLED': 'success'
  }
  return map[status] || 'default'
}

const fetchRequests = async () => {
  loading.value = true
  try {
    const params: any = { page: page.value, size: pageSize.value }
    if (activeTab.value !== 'all') {
      params.status = activeTab.value
    }
    const res = await buyRequestApi.getBuyRequests(params)
    requests.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  page.value = 1
  fetchRequests()
}

const handleCreate = async () => {
  if (!createForm.title.trim()) {
    message.warning('请输入求购标题')
    return
  }
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  
  submitting.value = true
  try {
    await buyRequestApi.createBuyRequest(createForm)
    message.success('发布成功')
    showCreateModal.value = false
    Object.assign(createForm, {
      title: '', description: '', category: '',
      budgetMin: null, budgetMax: null,
      expectedCondition: '', contactInfo: '', expireDays: 7
    })
    fetchRequests()
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchRequests()
})
</script>

<style scoped>
.buy-request-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.request-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.request-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.request-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.request-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 500;
}

.create-time {
  font-size: 12px;
  color: #999;
}

.request-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px;
  color: #333;
}

.request-desc {
  color: #666;
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.request-meta {
  display: flex;
  gap: 24px;
  color: #999;
  font-size: 14px;
  margin-bottom: 12px;
}

.request-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.request-footer {
  display: flex;
  gap: 24px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  color: #999;
  font-size: 13px;
}

.request-footer span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
