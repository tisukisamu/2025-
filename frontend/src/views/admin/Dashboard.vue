<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '../../api'
import type { AdminStats, Order } from '../../api'
import { message } from 'ant-design-vue'
import {
  DollarOutlined,
  ShoppingOutlined,
  UserOutlined,
  FileTextOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  EyeOutlined,
  ReloadOutlined,
  DownloadOutlined,
  PlusOutlined,
  CalendarOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  CarOutlined,
  CloseCircleOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const stats = ref<AdminStats | null>(null)
const loading = ref(false)
const refreshing = ref(false)

// 趋势数据
const trendData = ref([
  { day: '周一', value: 4500, orders: 32 },
  { day: '周二', value: 5200, orders: 38 },
  { day: '周三', value: 4800, orders: 35 },
  { day: '周四', value: 6100, orders: 45 },
  { day: '周五', value: 5500, orders: 41 },
  { day: '周六', value: 7200, orders: 56 },
  { day: '周日', value: 6900, orders: 52 }
])

// 快速操作
const quickActions = [
  { icon: PlusOutlined, label: '添加商品', color: '#10b981', action: () => router.push('/admin/products') },
  { icon: EyeOutlined, label: '查看订单', color: '#3b82f6', action: () => router.push('/admin/orders') },
  { icon: UserOutlined, label: '客户管理', color: '#8b5cf6', action: () => router.push('/admin/users') },
  { icon: DownloadOutlined, label: '导出报表', color: '#f59e0b', action: () => message.info('导出功能开发中') }
]

// 状态映射
const statusMap: Record<number, { text: string; color: string; bg: string; icon: any }> = {
  0: { text: '待支付', color: '#d97706', bg: '#fef3c7', icon: ClockCircleOutlined },
  1: { text: '待发货', color: '#3b82f6', bg: '#dbeafe', icon: FileTextOutlined },
  2: { text: '已发货', color: '#8b5cf6', bg: '#ede9fe', icon: CarOutlined },
  3: { text: '已完成', color: '#10b981', bg: '#d1fae5', icon: CheckCircleOutlined },
  4: { text: '已取消', color: '#ef4444', bg: '#fee2e2', icon: CloseCircleOutlined }
}

onMounted(async () => {
  await loadStats()
})

const loadStats = async () => {
  loading.value = true
  try {
    stats.value = await adminApi.getStats()
    
    // 获取真实的趋势数据
    const today = new Date()
    const lastWeek = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000)
    const res = await adminApi.getSalesReport({
      type: 'day',
      startDate: lastWeek.toISOString().split('T')[0],
      endDate: today.toISOString().split('T')[0]
    })
    
    if (res && res.labels) {
      trendData.value = res.labels.map((label: string, index: number) => ({
        day: label,
        value: res.sales[index],
        orders: res.orders[index]
      }))
    }
  } finally {
    loading.value = false
  }
}

const refreshStats = async () => {
  refreshing.value = true
  try {
    await loadStats()
    message.success('数据已更新')
  } finally {
    refreshing.value = false
  }
}

// KPI 卡片数据
const kpiCards = computed(() => [
  {
    title: '总销售额',
    value: stats.value?.todaySales || 0,
    prefix: '¥',
    trend: 12.5,
    trendLabel: '较昨日',
    icon: DollarOutlined,
    color: 'emerald'
  },
  {
    title: '订单总量',
    value: stats.value?.totalOrders || 0,
    trend: 8.2,
    trendLabel: '较昨日',
    icon: FileTextOutlined,
    color: 'blue'
  },
  {
    title: '活跃用户',
    value: stats.value?.totalUsers || 0,
    trend: 24.5,
    trendLabel: '较上周',
    icon: UserOutlined,
    color: 'violet'
  },
  {
    title: '商品总数',
    value: stats.value?.totalProducts || 156,
    trend: -2.4,
    trendLabel: '库存预警',
    icon: ShoppingOutlined,
    color: 'amber'
  }
])

const formatPrice = (price: number) => `¥${price?.toFixed(2) || '0.00'}`

const getStatusInfo = (status: number) => statusMap[status] || statusMap[0]
</script>

<template>
  <div class="dashboard-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">数据仪表盘</h1>
        <p class="page-subtitle">欢迎回来，这是今日的业务概览</p>
      </div>
      <button 
        class="refresh-btn" 
        @click="refreshStats"
        :disabled="refreshing"
      >
        <ReloadOutlined :spin="refreshing" />
        <span>刷新数据</span>
      </button>
    </div>

    <!-- KPI 卡片 -->
    <div class="kpi-grid">
      <div 
        v-for="card in kpiCards" 
        :key="card.title" 
        class="kpi-card"
        :class="`kpi-card-${card.color}`"
      >
        <div class="kpi-header">
          <div class="kpi-icon" :class="`kpi-icon-${card.color}`">
            <component :is="card.icon" />
          </div>
          <div 
            class="kpi-trend"
            :class="card.trend >= 0 ? 'trend-up' : 'trend-down'"
          >
            <component :is="card.trend >= 0 ? ArrowUpOutlined : ArrowDownOutlined" />
            <span>{{ Math.abs(card.trend) }}%</span>
          </div>
        </div>
        <div class="kpi-body">
          <p class="kpi-label">{{ card.title }}</p>
          <p class="kpi-value">{{ card.prefix }}{{ card.value }}</p>
          <p class="kpi-hint">{{ card.trendLabel }}</p>
        </div>
      </div>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions">
      <button 
        v-for="action in quickActions" 
        :key="action.label"
        class="action-card"
        @click="action.action"
      >
        <div class="action-icon" :style="{ background: action.color }">
          <component :is="action.icon" />
        </div>
        <span>{{ action.label }}</span>
      </button>
    </div>

    <!-- 主内容区 -->
    <div class="content-grid">
      <!-- 销售趋势 -->
      <div class="chart-card">
        <div class="card-header">
          <h3 class="card-title">销售趋势分析</h3>
          <div class="card-actions">
            <button class="icon-btn" title="查看详情">
              <EyeOutlined />
            </button>
          </div>
        </div>
        <div class="chart-container">
          <div v-for="item in trendData" :key="item.day" class="chart-bar-wrapper">
            <div class="chart-bar-bg">
              <div 
                class="chart-bar" 
                :style="{ height: (item.value / 8000 * 100) + '%' }"
              ></div>
            </div>
            <div class="chart-info">
              <span class="chart-label">{{ item.day }}</span>
              <span class="chart-value">¥{{ item.value }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 最新订单 -->
      <div class="orders-card">
        <div class="card-header">
          <h3 class="card-title">最新订单</h3>
          <router-link to="/admin/orders" class="view-all-link">
            查看全部
            <FileTextOutlined />
          </router-link>
        </div>
        <div class="orders-list">
          <div v-if="stats?.recentOrders && stats.recentOrders.length > 0" class="orders-wrapper">
            <div 
              v-for="order in stats.recentOrders.slice(0, 5)" 
              :key="order.id"
              class="order-item"
            >
              <div class="order-main">
                <div class="order-no">{{ order.orderNo }}</div>
                <div class="order-amount">{{ formatPrice(order.totalAmount) }}</div>
              </div>
              <div class="order-status">
                <div 
                  class="status-badge"
                  :style="{ 
                    backgroundColor: getStatusInfo(order.status).bg,
                    color: getStatusInfo(order.status).color
                  }"
                >
                  <component :is="getStatusInfo(order.status).icon" />
                  <span>{{ getStatusInfo(order.status).text }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <FileTextOutlined class="empty-icon" />
            <p class="empty-title">暂无订单数据</p>
            <p class="empty-desc">订单创建后会显示在这里</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 页面容器 */
.dashboard-page {
  padding: 24px;
  max-width: 1440px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  gap: 16px;
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.page-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.refresh-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  color: #374151;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.refresh-btn:hover:not(:disabled) {
  background: #f9fafb;
  border-color: #d1d5db;
}

.refresh-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* KPI 卡片网格 */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.kpi-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
}

.kpi-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #d1d5db;
  transform: translateY(-2px);
}

.kpi-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.kpi-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.kpi-icon-emerald {
  background: #ecfdf5;
  color: #10b981;
}

.kpi-icon-blue {
  background: #eff6ff;
  color: #3b82f6;
}

.kpi-icon-violet {
  background: #f5f3ff;
  color: #8b5cf6;
}

.kpi-icon-amber {
  background: #fffbeb;
  color: #f59e0b;
}

.kpi-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 20px;
}

.trend-up {
  background: #d1fae5;
  color: #059669;
}

.trend-down {
  background: #fee2e2;
  color: #dc2626;
}

.kpi-label {
  font-size: 13px;
  color: #6b7280;
  margin: 0 0 4px 0;
}

.kpi-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 4px 0;
}

.kpi-hint {
  font-size: 12px;
  color: #9ca3af;
  margin: 0;
}

/* 快速操作 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.action-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-card:hover {
  border-color: #10b981;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.1);
  transform: translateY(-2px);
}

.action-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.action-card span {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

/* 内容网格 */
.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.chart-card,
.orders-card {
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f3f4f6;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.icon-btn {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  border: none;
  background: transparent;
  color: #9ca3af;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.icon-btn:hover {
  background: #f3f4f6;
  color: #6b7280;
}

.view-all-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #10b981;
  text-decoration: none;
  font-weight: 500;
}

.view-all-link:hover {
  color: #059669;
}

/* 图表容器 */
.chart-container {
  padding: 24px;
  height: 320px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
}

.chart-bar-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  height: 100%;
}

.chart-bar-bg {
  flex: 1;
  width: 100%;
  background: #f3f4f6;
  border-radius: 6px 6px 0 0;
  position: relative;
  overflow: hidden;
}

.chart-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(180deg, #10b981 0%, #059669 100%);
  border-radius: 6px 6px 0 0;
  transition: height 0.5s ease;
}

.chart-bar:hover {
  background: linear-gradient(180deg, #059669 0%, #047857 100%);
}

.chart-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.chart-label {
  font-size: 12px;
  color: #9ca3af;
}

.chart-value {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}

/* 订单列表 */
.orders-list {
  padding: 0;
}

.orders-wrapper {
  display: flex;
  flex-direction: column;
}

.order-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #f3f4f6;
  transition: background 0.2s;
}

.order-item:last-child {
  border-bottom: none;
}

.order-item:hover {
  background: #f9fafb;
}

.order-main {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16px;
}

.order-no {
  font-weight: 500;
  color: #1f2937;
  font-size: 14px;
}

.order-amount {
  font-weight: 600;
  color: #1f2937;
  font-size: 15px;
}

.order-status {
  flex-shrink: 0;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

/* 空状态 */
.empty-state {
  padding: 48px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: #d1d5db;
}

.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 8px 0;
}

.empty-desc {
  font-size: 13px;
  color: #9ca3af;
  margin: 0;
}

/* 响应式 */
@media (max-width: 1200px) {
  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .content-grid {
    grid-template-columns: 1fr;
  }
  
  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard-page {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
  }
  
  .page-title {
    font-size: 22px;
  }
  
  .kpi-grid {
    grid-template-columns: 1fr;
  }
  
  .kpi-value {
    font-size: 24px;
  }
  
  .chart-container {
    height: 240px;
    padding: 16px;
    gap: 8px;
  }
}
</style>
