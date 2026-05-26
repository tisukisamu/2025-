<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { storeApi } from '../../api'
import { ShoppingOutlined, InboxOutlined, WarningOutlined, DollarOutlined, ReloadOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const stats = ref({
  totalProducts: 0,
  pendingOrders: 0,
  warningStock: 0,
  todaySales: 0
})
const recentProducts = ref<any[]>([])
const loading = ref(false)

// 将相对路径转换为完整URL（与商品管理保持一致）
const getFullImageUrl = (url: string): string => {
  if (!url) return ''
  // 如果已经是绝对路径，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  // 相对路径直接使用（前端代理会自动转发到后端）
  return url.startsWith('/') ? url : '/' + url
}

// 获取商品第一张图片URL：优先使用 imageUrls，为空则使用 imageUrl
const getFirstImageUrl = (item: any): string => {
  // 1. 优先使用 imageUrls
  if (item.imageUrls) {
    let urls: string[] = []
    if (typeof item.imageUrls === 'string') {
      urls = item.imageUrls.split(',').filter((url: string) => url.trim() !== '')
    } else if (Array.isArray(item.imageUrls)) {
      urls = item.imageUrls
    }
    
    if (urls.length > 0) {
      return getFullImageUrl(urls[0].trim())
    }
  }
  
  // 2. 降级使用 imageUrl
  if (item.imageUrl) {
    return getFullImageUrl(item.imageUrl)
  }
  
  // 3. 最后尝试 mainImageUrl
  if (item.mainImageUrl) {
    return getFullImageUrl(item.mainImageUrl)
  }
  
  return ''
}

onMounted(() => {
  loadDashboardData()
})

const isToday = (dateStr?: string) => {
  if (!dateStr) return false
  const d = new Date(dateStr)
  const now = new Date()
  return d.getFullYear() === now.getFullYear() && d.getMonth() === now.getMonth() && d.getDate() === now.getDate()
}

const loadDashboardData = async () => {
  loading.value = true
  try {
    // 商���列表
    const products = await storeApi.getProducts({ page: 0, size: 100 })
    const productList = products.content || []
    stats.value.totalProducts = productList.length
    recentProducts.value = productList.slice(0, 5)

    // 库存预警
    const warnings = await storeApi.getStockWarning()
    stats.value.warningStock = Array.isArray(warnings) ? warnings.length : 0

    // 订单：用于待处理/今日销售额统计（接口目前无统计专用，只能拉列表前端算）
    const orders = await storeApi.getOrders()
    const list = Array.isArray(orders) ? orders : []

    stats.value.pendingOrders = list.filter((o: any) => o.status === 1).length

    // 今日销售额：这里按 status=3（已完成）且 createTime 为今天统计
    stats.value.todaySales = list
      .filter((o: any) => o.status === 3 && isToday(o.createTime))
      .reduce((sum: number, o: any) => sum + Number(o.totalAmount || 0), 0)
  } catch (error: any) {
    console.error('加载数据失败:', error)
    message.error(error.message || '加载数据失败')
  } finally {
    loading.value = false
  }
}

const goToProducts = () => router.push('/store/products')
const goToOrders = () => router.push('/store/orders')
</script>

<template>
  <div class="store-dashboard">
    <div class="page-header">
      <h2 class="page-title">店铺概览</h2>
      <a-button :loading="loading" @click="loadDashboardData">
        <template #icon><ReloadOutlined /></template>
        刷新
      </a-button>
    </div>

    <a-row :gutter="16" class="stats-row">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card" @click="goToProducts" hoverable>
          <div class="stat-icon" style="background: #e6f7ff; color: #1890ff;">
            <ShoppingOutlined />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalProducts }}</div>
            <div class="stat-label">商品总数</div>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card" @click="goToOrders" hoverable>
          <div class="stat-icon" style="background: #f6ffed; color: #52c41a;">
            <InboxOutlined />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingOrders }}</div>
            <div class="stat-label">待发货订单</div>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card warning" @click="goToProducts" hoverable>
          <div class="stat-icon" style="background: #fff7e6; color: #fa8c16;">
            <WarningOutlined />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.warningStock }}</div>
            <div class="stat-label">库存预警</div>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card" hoverable>
          <div class="stat-icon" style="background: #f9f0ff; color: #722ed1;">
            <DollarOutlined />
          </div>
          <div class="stat-info">
            <div class="stat-value">￥{{ stats.todaySales.toFixed(2) }}</div>
            <div class="stat-label">今日销售额</div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-card title="最近添加的商品" class="recent-products" :loading="loading" :bordered="false">
      <a-list :data-source="recentProducts" item-layout="horizontal">
        <template #renderItem="{ item }">
          <a-list-item>
            <a-list-item-meta>
              <template #avatar>
                <img
                  v-if="getFirstImageUrl(item)"
                  :src="getFirstImageUrl(item)"
                  alt="商品图片"
                  style="width: 56px; height: 56px; object-fit: cover; border-radius: 8px;"
                />
                <div v-else class="no-image">暂无图片</div>
              </template>
              <template #title>
                <div style="display:flex; align-items:center; justify-content:space-between; gap:12px;">
                  <span>{{ item.name }}</span>
                  <a-tag :color="item.status === 1 ? 'green' : 'orange'">
                    {{ item.status === 1 ? '已通过' : '待审核' }}
                  </a-tag>
                </div>
              </template>
              <template #description>
                <div>价格: ￥{{ Number(item.price || 0).toFixed(2) }} | 库存: {{ item.stock }}</div>
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-card>
  </div>
</template>

<style scoped>
.store-dashboard {
  padding: 24px;
  background: #f5f5f5;
  min-height: calc(100vh - 120px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-title {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
}

.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  border-radius: 12px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 12px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.recent-products {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.no-image {
  width: 56px;
  height: 56px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 12px;
  border-radius: 8px;
}
</style>
