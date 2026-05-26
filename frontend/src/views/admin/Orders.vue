<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { adminApi } from '../../api'
import type { Order } from '../../api'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  EyeOutlined,
  FileTextOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  CarOutlined,
  CloseCircleOutlined,
  FilterOutlined,
  CalendarOutlined,
  DollarOutlined,
  UserOutlined,
  PhoneOutlined,
  EnvironmentOutlined,
  ShoppingCartOutlined
} from '@ant-design/icons-vue'

const orders = ref<Order[]>([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({
  page: 1,
  size: 10,
  status: 'all' as number | 'all',
  search: ''
})

const detailModalVisible = ref(false)
const selectedOrder = ref<Order | null>(null)

// 状态映射
const statusMap: Record<number, { text: string; color: string; bg: string; icon: any }> = {
  0: { text: '待支付', color: '#d97706', bg: '#fef3c7', icon: ClockCircleOutlined },
  1: { text: '待发货', color: '#3b82f6', bg: '#dbeafe', icon: FileTextOutlined },
  2: { text: '已发货', color: '#8b5cf6', bg: '#ede9fe', icon: CarOutlined },
  3: { text: '已完成', color: '#10b981', bg: '#d1fae5', icon: CheckCircleOutlined },
  4: { text: '已取消', color: '#ef4444', bg: '#fee2e2', icon: CloseCircleOutlined }
}

const statusOptions = [
  { value: 'all', label: '全部状态' },
  { value: 0, label: '待支付' },
  { value: 1, label: '待发货' },
  { value: 2, label: '已发货' },
  { value: 3, label: '已完成' },
  { value: 4, label: '已取消' }
]

onMounted(async () => {
  await loadOrders()
})

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await adminApi.getOrders(queryParams.value.page - 1, queryParams.value.size)
    orders.value = res.content
    total.value = res.totalElements
  } catch (error) {
    message.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const handleTableChange = (pagination: any) => {
  queryParams.value.page = pagination.current
  queryParams.value.size = pagination.pageSize
  loadOrders()
}

const filteredOrders = computed(() => {
  let res = orders.value
  if (queryParams.value.status !== 'all') {
    res = res.filter(o => o.status === queryParams.value.status)
  }
  if (queryParams.value.search) {
    const q = queryParams.value.search.toLowerCase()
    res = res.filter(o => 
      o.orderNo?.toLowerCase().includes(q) ||
      o.userId?.toString().includes(q) ||
      o.contact?.toLowerCase().includes(q)
    )
  }
  return res
})

const formatPrice = (price: number) => `¥${price?.toFixed(2) || '0.00'}`
const formatDate = (date: string) => {
  if (!date) return '-'
  const d = new Date(date)
  return `${d.getFullYear()}/${(d.getMonth() + 1).toString().padStart(2, '0')}/${d.getDate().toString().padStart(2, '0')} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

const getStatusInfo = (status: number) => statusMap[status] || statusMap[0]

const viewOrderDetail = (order: Order) => {
  selectedOrder.value = order
  detailModalVisible.value = true
}

const getStatusCount = (status: number) => {
  return orders.value.filter(o => o.status === status).length
}

const closeDetailModal = () => {
  detailModalVisible.value = false
  selectedOrder.value = null
}
</script>

<template>
  <div class="orders-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">订单管理</h1>
        <p class="page-subtitle">
          共 <span class="highlight">{{ total }}</span> 个订单
        </p>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <div class="search-box">
          <SearchOutlined class="search-icon" />
          <input 
            v-model="queryParams.search" 
            type="text"
            class="search-input"
            placeholder="搜索订单号、联系人..."
          />
        </div>
        
        <div class="filter-group">
          <FilterOutlined class="filter-icon" />
          <select v-model="queryParams.status" class="filter-select">
            <option v-for="opt in statusOptions" :key="opt.value" :value="opt.value">
              {{ opt.label }}
            </option>
          </select>
        </div>
      </div>

      <!-- 统计标签 -->
      <div class="stats-tags">
        <div class="stat-tag stat-tag-pending">
          <ClockCircleOutlined />
          <span>待支付 {{ getStatusCount(0) }}</span>
        </div>
        <div class="stat-tag stat-tag-processing">
          <FileTextOutlined />
          <span>待发货 {{ getStatusCount(1) }}</span>
        </div>
        <div class="stat-tag stat-tag-shipping">
          <CarOutlined />
          <span>已发货 {{ getStatusCount(2) }}</span>
        </div>
        <div class="stat-tag stat-tag-completed">
          <CheckCircleOutlined />
          <span>已完成 {{ getStatusCount(3) }}</span>
        </div>
      </div>
    </div>

    <!-- 订单表格 -->
    <div class="table-container">
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      
      <div v-else-if="filteredOrders.length === 0" class="empty-state">
        <FileTextOutlined class="empty-icon" />
        <h3 class="empty-title">暂无订单数据</h3>
        <p class="empty-desc">没有找到符合条件的订单</p>
      </div>

      <div v-else class="table-wrapper">
        <table class="data-table">
          <thead>
            <tr>
              <th style="width: 15%;">订单信息</th>
              <th style="width: 15%;">客户信息</th>
              <th style="width: 15%;">下单时间</th>
              <th style="width: 12%;">订单金额</th>
              <th style="width: 13%;">订单状态</th>
              <th style="width: 15%;">收货地址</th>
              <th style="width: 15%;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in filteredOrders" :key="order.id" class="table-row">
              <td>
                <div class="order-info">
                  <div class="order-no">{{ order.orderNo }}</div>
                  <div class="order-id">ID: #{{ order.id }}</div>
                </div>
              </td>
              <td>
                <div class="customer-info">
                  <div class="customer-name">{{ order.contact || '-' }}</div>
                  <div class="customer-phone">{{ order.phone || '-' }}</div>
                </div>
              </td>
              <td>
                <div class="time-info">
                  <CalendarOutlined class="time-icon" />
                  <span>{{ formatDate(order.createTime) }}</span>
                </div>
              </td>
              <td class="amount-cell">
                <div class="amount-main">{{ formatPrice(order.totalAmount) }}</div>
              </td>
              <td class="status-cell">
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
              </td>
              <td>
                <div class="address-cell" :title="order.address">
                  <span class="address-text">{{ order.address || '-' }}</span>
                </div>
              </td>
              <td class="action-cell">
                <button class="btn-view" @click="viewOrderDetail(order)">
                  <EyeOutlined />
                  <span>查看</span>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <a-pagination
          v-model:current="queryParams.page"
          v-model:pageSize="queryParams.size"
          :total="total"
          :show-total="(t: number) => `共 ${t} 条`"
          show-size-changer
          @change="(page: number, pageSize: number) => handleTableChange({ current: page, pageSize })"
        />
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <a-modal
      v-model:open="detailModalVisible"
      :title="`订单详情 - ${selectedOrder?.orderNo || ''}`"
      @cancel="closeDetailModal"
      :footer="null"
      width="700px"
    >
      <div v-if="selectedOrder" class="order-detail">
        <!-- 订单基本信息 -->
        <div class="detail-section">
          <h4 class="detail-title">订单信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">订单号</span>
              <span class="detail-value">{{ selectedOrder.orderNo }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">订单金额</span>
              <span class="detail-value price-value">{{ formatPrice(selectedOrder.totalAmount) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">下单时间</span>
              <span class="detail-value">{{ formatDate(selectedOrder.createTime) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">订单状态</span>
              <div
                class="status-badge"
                :style="{
                  backgroundColor: getStatusInfo(selectedOrder.status).bg,
                  color: getStatusInfo(selectedOrder.status).color
                }"
              >
                <component :is="getStatusInfo(selectedOrder.status).icon" />
                <span>{{ getStatusInfo(selectedOrder.status).text }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 客户信息 -->
        <div class="detail-section">
          <h4 class="detail-title">客户信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">联系人</span>
              <span class="detail-value">{{ selectedOrder.contact || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">联系电话</span>
              <span class="detail-value">{{ selectedOrder.phone || '-' }}</span>
            </div>
          </div>
        </div>

        <!-- 收货信息 -->
        <div class="detail-section">
          <h4 class="detail-title">收货地址</h4>
          <div class="detail-grid">
            <div class="detail-item full-width">
              <span class="detail-label">收货地址</span>
              <span class="detail-value">{{ selectedOrder.address || '-' }}</span>
            </div>
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="detail-section">
          <h4 class="detail-title">商品信息</h4>
          <div v-if="selectedOrder.items && selectedOrder.items.length > 0" class="order-items">
            <div v-for="(item, idx) in selectedOrder.items" :key="idx" class="order-item">
              <div class="item-image">
                <ShoppingCartOutlined v-if="!item.imageUrl" class="placeholder-icon" />
                <img v-else :src="item.imageUrl" :alt="item.productName" />
              </div>
              <div class="item-info">
                <div class="item-name">{{ item.productName }}</div>
                <div class="item-spec">{{ item.spec || '默认规格' }}</div>
              </div>
              <div class="item-quantity">x{{ item.quantity }}</div>
              <div class="item-price">{{ formatPrice(item.price * item.quantity) }}</div>
            </div>
          </div>
          <div v-else class="empty-items">
            <ShoppingCartOutlined class="empty-icon" />
            <p>暂无商品信息</p>
          </div>
        </div>

        <!-- 关闭按钮 -->
        <div class="detail-footer">
          <button class="btn-close" @click="closeDetailModal">
            关闭
          </button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<style scoped>
/* 页面容器 */
.orders-page {
  padding: 24px;
  max-width: 1440px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  margin-bottom: 24px;
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

.highlight {
  color: #10b981;
  font-weight: 600;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.filter-left {
  display: flex;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

/* 搜索框 */
.search-box {
  position: relative;
  flex: 1;
  max-width: 360px;
}

.search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  font-size: 16px;
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: 12px 14px 12px 44px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  font-size: 14px;
  background: white;
  transition: all 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

/* 筛选组 */
.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 16px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
}

.filter-icon {
  color: #9ca3af;
  font-size: 14px;
}

.filter-select {
  border: none;
  background: transparent;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  cursor: pointer;
  padding-right: 8px;
}

.filter-select:focus {
  outline: none;
}

/* 统计标签 */
.stats-tags {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.stat-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.stat-tag-pending {
  background: #fef3c7;
  color: #d97706;
}

.stat-tag-processing {
  background: #dbeafe;
  color: #3b82f6;
}

.stat-tag-shipping {
  background: #ede9fe;
  color: #8b5cf6;
}

.stat-tag-completed {
  background: #d1fae5;
  color: #10b981;
}

/* 表格容器 */
.table-container {
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.table-wrapper {
  overflow-x: auto;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding: 0 16px 16px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  min-width: 1000px;
}

.data-table thead {
  background: #f9fafb;
}

.data-table th {
  text-align: left;
  padding: 16px 20px;
  font-weight: 600;
  color: #374151;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 1px solid #e5e7eb;
  white-space: nowrap;
}

.data-table td {
  padding: 16px 20px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
  color: #374151;
}

.table-row {
  transition: background 0.2s;
}

.table-row:hover {
  background: #f9fafb;
}

.table-row:last-child td {
  border-bottom: none;
}

/* 订单信息 */
.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-no {
  font-weight: 600;
  color: #1f2937;
  font-size: 14px;
}

.order-id {
  font-size: 12px;
  color: #9ca3af;
}

/* 客户信息 */
.customer-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.customer-name {
  font-weight: 500;
  color: #374151;
  font-size: 14px;
}

.customer-phone {
  font-size: 13px;
  color: #6b7280;
}

/* 时间信息 */
.time-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-icon {
  color: #9ca3af;
  font-size: 14px;
}

.time-info span {
  font-size: 13px;
  color: #6b7280;
}

/* 金额 */
.amount-cell {
  text-align: right;
}

.amount-main {
  font-size: 16px;
  font-weight: 700;
  color: #10b981;
}

/* 状态 */
.status-cell {
  text-align: center;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

/* 地址 */
.address-cell {
  max-width: 200px;
}

.address-text {
  color: #6b7280;
  font-size: 13px;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 操作 */
.action-cell {
  text-align: right;
}

.btn-view {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  background: white;
  color: #374151;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-view:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
  color: #10b981;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 16px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #10b981;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-state p {
  color: #6b7280;
  font-size: 14px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
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
}

/* 订单详情弹窗样式 */
.order-detail {
  max-height: 60vh;
  overflow-y: auto;
}

.detail-section {
  padding: 16px 0;
  border-bottom: 1px solid #f3f4f6;
}

.detail-section:last-of-type {
  border-bottom: none;
}

.detail-title {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 16px 0;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

.detail-label {
  font-size: 12px;
  color: #9ca3af;
}

.detail-value {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
}

.detail-value.price-value {
  font-size: 16px;
  font-weight: 700;
  color: #10b981;
}

/* 订单商品列表 */
.order-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  display: grid;
  grid-template-columns: auto 1fr auto auto;
  grid-template-rows: auto auto;
  gap: 8px 16px;
  align-items: center;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
}

.order-item .item-image {
  grid-row: 1 / -1;
  width: 56px;
  height: 56px;
  border-radius: 8px;
  overflow: hidden;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
}

.order-item .item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.order-item .placeholder-icon {
  font-size: 24px;
  color: #d1d5db;
}

.order-item .item-info {
  grid-row: 1 / -1;
  grid-column: 2;
}

.order-item .item-name {
  font-weight: 500;
  color: #1f2937;
  font-size: 14px;
}

.order-item .item-spec {
  font-size: 12px;
  color: #9ca3af;
}

.order-item .item-quantity {
  grid-row: 1;
  grid-column: 3;
  font-size: 14px;
  color: #6b7280;
}

.order-item .item-price {
  grid-row: 1;
  grid-column: 4;
  font-size: 16px;
  font-weight: 700;
  color: #10b981;
  text-align: right;
}

.empty-items {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  gap: 16px;
}

.empty-items .empty-icon {
  font-size: 48px;
  color: #d1d5db;
  opacity: 0.5;
}

.empty-items p {
  font-size: 14px;
  color: #9ca3af;
  margin: 0;
}

.detail-footer {
  padding-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.btn-close {
  padding: 10px 32px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  background: white;
  color: #374151;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-close:hover {
  background: #f9fafb;
  border-color: #d1d5db;
  color: #10b981;
}

/* 响应式 */
@media (max-width: 1200px) {
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-left {
    flex-direction: column;
  }
  
  .search-box {
    max-width: none;
  }
  
  .stats-tags {
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .orders-page {
    padding: 16px;
  }
  
  .page-title {
    font-size: 22px;
  }
  
  .stats-tags {
    flex-wrap: wrap;
  }
  
  .data-table th,
  .data-table td {
    padding: 12px 16px;
    font-size: 13px;
  }
  
  .address-cell {
    max-width: 150px;
  }
}
</style>
