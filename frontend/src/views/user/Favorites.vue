<template>
  <div class="favorites-page">
    <div class="back-nav">
      <a-button type="text" @click="router.back()">
        <left-outlined /> 返回
      </a-button>
    </div>
    <div class="page-header">
      <h1>我的收藏</h1>
      <span class="total-count" v-if="total > 0">共 {{ total }} 件收藏</span>
    </div>

    <a-spin :spinning="loading">
      <div class="favorites-grid" v-if="favorites.length > 0">
        <div
          v-for="fav in favorites"
          :key="fav.id"
          class="favorite-card"
          @click="router.push(`/products/${fav.productId}`)"
        >
          <div class="card-image">
            <img :src="fav.productCoverImage || defaultImage" :alt="fav.productTitle" />
            <div class="status-tag" :class="getStatusClass(fav.productStatus)">
              {{ getStatusText(fav.productStatus) }}
            </div>
          </div>
          <div class="card-content">
            <h3 class="card-title">{{ fav.productTitle }}</h3>
            <div class="card-price">
              <span class="price">¥{{ fav.productPrice }}</span>
            </div>
            <div class="card-footer">
              <span class="favorite-time">
                <clock-circle-outlined />
                {{ formatTime(fav.createTime) }}
              </span>
              <a-button 
                type="text" 
                danger 
                size="small" 
                @click.stop="handleRemove(fav.productId)"
                class="remove-btn"
              >
                <delete-outlined /> 移除
              </a-button>
            </div>
          </div>
        </div>
      </div>
      <a-empty v-else description="暂无收藏">
        <template #image>
          <heart-outlined style="font-size: 64px; color: #d9d9d9;" />
        </template>
        <a-button type="primary" @click="router.push('/products')">
          去逛逛
        </a-button>
      </a-empty>
    </a-spin>

    <div class="pagination-wrapper" v-if="total > pagination.pageSize">
      <a-pagination
        v-model:current="pagination.page"
        v-model:pageSize="pagination.pageSize"
        :total="total"
        show-quick-jumper
        :show-total="(total: number) => `共 ${total} 件收藏`"
        @change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '@/api'
import type { Favorite } from '@/types'
import { message } from 'ant-design-vue'
import { LeftOutlined, DeleteOutlined, HeartOutlined, ClockCircleOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const loading = ref(false)
const favorites = ref<Favorite[]>([])
const total = ref(0)

const defaultImage = '/no-image.svg'

const pagination = reactive({
  page: 1,
  pageSize: 12
})

const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  return date.toLocaleDateString('zh-CN')
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'ON_SALE': '在售',
    'PENDING': '审核中',
    'OFF_SHELF': '已下架',
    'SOLD': '已售出'
  }
  return statusMap[status] || status
}

const getStatusClass = (status: string) => {
  const classMap: Record<string, string> = {
    'ON_SALE': 'status-sale',
    'PENDING': 'status-pending',
    'OFF_SHELF': 'status-off',
    'SOLD': 'status-sold'
  }
  return classMap[status] || ''
}

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res = await userApi.getMyFavorites(pagination.page, pagination.pageSize)
    favorites.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleRemove = async (productId: number) => {
  try {
    await userApi.removeFavorite(productId)
    message.success('已取消收藏')
    fetchFavorites()
  } catch {
    // ignore
  }
}

const handlePageChange = () => {
  fetchFavorites()
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
.favorites-page {
  background: #f5f5f5;
  min-height: calc(100vh - 200px);
  padding: 24px;
}

.back-nav {
  margin-bottom: 16px;
}

.back-nav :deep(.ant-btn) {
  color: #1890ff;
  padding: 4px 8px;
}

.back-nav :deep(.ant-btn:hover) {
  background: #e6f7ff;
}

.page-header {
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.total-count {
  color: #999;
  font-size: 14px;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}

.favorite-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.favorite-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-image {
  position: relative;
  width: 100%;
  padding-top: 100%;
  overflow: hidden;
}

.card-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.favorite-card:hover .card-image img {
  transform: scale(1.05);
}

.status-tag {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
}

.status-sale {
  background: #52c41a;
}

.status-pending {
  background: #faad14;
}

.status-off {
  background: #999;
}

.status-sold {
  background: #f5222d;
}

.card-content {
  padding: 12px 16px 16px;
}

.card-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-price {
  margin-bottom: 12px;
}

.card-price .price {
  font-size: 18px;
  font-weight: 600;
  color: #f5222d;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.favorite-time {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

.remove-btn {
  padding: 0 8px;
  height: auto;
  font-size: 12px;
}

.remove-btn:hover {
  background: #fff1f0;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .favorites-page {
    padding: 12px;
  }
  
  .favorites-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .card-content {
    padding: 10px 12px 12px;
  }
  
  .card-title {
    font-size: 13px;
  }
  
  .card-price .price {
    font-size: 16px;
  }
}
</style>
