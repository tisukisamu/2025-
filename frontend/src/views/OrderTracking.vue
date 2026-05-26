<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderApi } from '../api'
import type { Order } from '../api'
import { message } from 'ant-design-vue'
import {
  LeftOutlined,
  EnvironmentOutlined,
  CarOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  InboxOutlined,
  PhoneOutlined,
  UserOutlined,
  ShoppingOutlined
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const order = ref<Order | null>(null)

// 状态映射
const statusMap: Record<number, { text: string; icon: any; color: string; bg: string }> = {
  0: { text: '待支付', icon: ClockCircleOutlined, color: '#d97706', bg: '#fef3c7' },
  1: { text: '待发货', icon: InboxOutlined, color: '#3b82f6', bg: '#dbeafe' },
  2: { text: '配送中', icon: CarOutlined, color: '#8b5cf6', bg: '#ede9fe' },
  3: { text: '已完成', icon: CheckCircleOutlined, color: '#10b981', bg: '#d1fae5' },
  4: { text: '已取消', icon: ClockCircleOutlined, color: '#ef4444', bg: '#fee2e2' }
}

// 物流步骤
const trackingSteps = computed(() => {
  if (!order.value) return []
  const steps = [
    { title: '订单已提交', desc: order.value.createTime || '', completed: true },
    { title: '等待付款', desc: '', completed: order.value.status === 0 },
    { title: '商家已接单', desc: '', completed: [1, 2, 3].includes(order.value.status) },
    { title: '已发货', desc: '', completed: [2, 3].includes(order.value.status) },
    { title: '配送中', desc: '', completed: order.value.status === 2 },
    { title: '已签收', desc: '', completed: order.value.status === 3 }
  ]
  return steps
})

onMounted(async () => {
  loading.value = true
  try {
    const orderNo = route.query.orderNo as string
    const id = Number(route.params.id)

    if (orderNo) {
      // 根据订单号查询
      order.value = await orderApi.getByOrderNo(orderNo)
    } else if (id) {
      // 根据订单ID查询
      order.value = await orderApi.getById(id)
    } else {
      message.error('订单参数错误')
      router.push('/orders')
      return
    }

    if (!order.value) {
      message.error('订单不存在')
      router.push('/orders')
    }
  } catch (error: any) {
    message.error(error.message || '加载订单失败')
    router.push('/orders')
  } finally {
    loading.value = false
  }
})

const formatPrice = (price: number) => `¥${price?.toFixed(2) || '0.00'}`

const formatDate = (date: string) => {
  if (!date) return '-'
  const d = new Date(date)
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

const getStatusInfo = (status: number) => statusMap[status] || statusMap[0]
</script>

<template>
  <div class="order-tracking-page">
    <!-- 顶部导航 -->
    <nav class="top-nav">
      <button class="nav-back-btn" @click="router.back()">
        <LeftOutlined />
        <span>返回</span>
      </button>
      <h1 class="nav-title">订单追踪</h1>
    </nav>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 订单内容 -->
    <div v-else-if="order" class="content">
      <!-- 订单状态卡片 -->
      <div class="status-card">
        <div class="status-left">
          <div
            class="status-icon"
            :style="{ backgroundColor: getStatusInfo(order.status).bg, color: getStatusInfo(order.status).color }"
          >
            <component :is="getStatusInfo(order.status).icon" class="icon-lg" />
          </div>
          <div class="status-info">
            <h2 class="status-title">{{ getStatusInfo(order.status).text }}</h2>
            <p class="status-desc">订单号：{{ order.orderNo }}</p>
          </div>
        </div>
        <div class="status-right">
          <div class="amount-info">
            <span class="amount-label">订单金额</span>
            <span class="amount-value">{{ formatPrice(order.totalAmount) }}</span>
          </div>
        </div>
      </div>

      <!-- 物流时间轴 -->
      <div class="tracking-section">
        <h3 class="section-title">物流进度</h3>
        <div class="timeline">
          <div
            v-for="(step, index) in trackingSteps"
            :key="index"
            class="timeline-item"
            :class="{ 'timeline-item-active': step.completed, 'timeline-item-pending': !step.completed }"
          >
            <div class="timeline-marker">
              <CheckCircleOutlined v-if="step.completed" class="marker-icon" />
              <ClockCircleOutlined v-else class="marker-icon pending" />
            </div>
            <div class="timeline-content">
              <div class="timeline-title">{{ step.title }}</div>
              <div v-if="step.desc" class="timeline-desc">{{ step.desc }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 订单信息 -->
      <div class="info-section">
        <h3 class="section-title">订单信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <div class="info-label">下单时间</div>
            <div class="info-value">{{ formatDate(order.createTime) }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">订单金额</div>
            <div class="info-value price-value">{{ formatPrice(order.totalAmount) }}</div>
          </div>
        </div>
      </div>

      <!-- 收货信息 -->
      <div class="info-section">
        <h3 class="section-title">收货信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <UserOutlined class="info-icon" />
            <div>
              <div class="info-label">收货人</div>
              <div class="info-value">{{ order.contact || '-' }}</div>
            </div>
          </div>
          <div class="info-item">
            <PhoneOutlined class="info-icon" />
            <div>
              <div class="info-label">联系电话</div>
              <div class="info-value">{{ order.phone || '-' }}</div>
            </div>
          </div>
          <div class="info-item full-width">
            <EnvironmentOutlined class="info-icon" />
            <div>
              <div class="info-label">收货地址</div>
              <div class="info-value">{{ order.address || '-' }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <ShoppingOutlined class="empty-icon" />
      <h3 class="empty-title">订单不存在</h3>
      <p class="empty-desc">请检查订单号是否正确</p>
      <button class="btn-back" @click="router.push('/')">
        返回首页
      </button>
    </div>
  </div>
</template>

<style scoped>
.order-tracking-page {
  min-height: 100vh;
  background: #f9fafb;
}

/* 顶部导航 */
.top-nav {
  position: sticky;
  top: 0;
  z-index: 50;
  background: white;
  border-bottom: 1px solid #e5e7eb;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.nav-back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  background: white;
  color: #374151;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.nav-back-btn:hover {
  background: #f3f4f6;
  border-color: #10b981;
  color: #10b981;
}

.nav-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

/* 内容区域 */
.content {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}

/* 状态卡片 */
.status-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.status-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-icon .icon-lg {
  font-size: 28px;
}

.status-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.status-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.status-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.status-right {
  flex-shrink: 0;
}

.amount-info {
  text-align: right;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.amount-label {
  font-size: 13px;
  color: #9ca3af;
}

.amount-value {
  font-size: 24px;
  font-weight: 700;
  color: #10b981;
}

/* 追踪区域 */
.tracking-section,
.info-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 20px 0;
}

/* 时间轴 */
.timeline {
  position: relative;
  padding-left: 20px;
}

.timeline-item {
  position: relative;
  padding-left: 40px;
  padding-bottom: 24px;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-item::before {
  content: '';
  position: absolute;
  left: 20px;
  top: 28px;
  bottom: 0;
  width: 2px;
  background: #e5e7eb;
}

.timeline-item:last-child::before {
  display: none;
}

.timeline-item-active .timeline-marker {
  background: #10b981;
}

.timeline-item-pending .timeline-marker {
  background: #e5e7eb;
}

.timeline-marker {
  position: absolute;
  left: 0;
  top: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

.marker-icon {
  font-size: 20px;
  color: white;
}

.marker-icon.pending {
  color: #9ca3af;
}

.timeline-content {
  padding-top: 4px;
}

.timeline-title {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
}

.timeline-desc {
  font-size: 13px;
  color: #9ca3af;
  margin-top: 4px;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 10px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-icon {
  font-size: 20px;
  color: #10b981;
  flex-shrink: 0;
}

.info-label {
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 4px;
}

.info-value {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
}

.info-value.price-value {
  font-size: 18px;
  font-weight: 700;
  color: #10b981;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  gap: 16px;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e5e7eb;
  border-top-color: #10b981;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-container p {
  color: #6b7280;
  font-size: 14px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  color: #d1d5db;
  margin-bottom: 24px;
  opacity: 0.5;
}

.empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 12px 0;
}

.empty-desc {
  font-size: 14px;
  color: #9ca3af;
  margin: 0 0 24px 0;
}

.btn-back {
  padding: 12px 32px;
  border-radius: 8px;
  border: 1px solid #10b981;
  background: #10b981;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-back:hover {
  background: #059669;
}

/* 响应式 */
@media (max-width: 768px) {
  .content {
    padding: 16px;
  }

  .status-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .status-right {
    width: 100%;
  }

  .amount-info {
    text-align: left;
  }
}
</style>
