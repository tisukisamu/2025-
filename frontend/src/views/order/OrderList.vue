<template>
  <div class="orders-page">
    <div class="page-header">
      <h1>我的订单</h1>
    </div>

    <a-card class="orders-card">
      <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
        <a-tab-pane key="all" tab="全部订单" />
        <a-tab-pane key="PENDING" tab="待发货" />
        <a-tab-pane key="SHIPPED" tab="待收货" />
        <a-tab-pane key="COMPLETED" tab="已完成" />
        <a-tab-pane key="CANCELLED" tab="已取消" />
      </a-tabs>

      <a-spin :spinning="loading">
        <div class="orders-list" v-if="orders.length > 0">
          <div
            v-for="order in orders"
            :key="order.id"
            class="order-item"
            @click="handleOrderClick(order.id)"
          >
            <div class="order-header">
              <span class="order-no">订单号: {{ order.orderNo }}</span>
              <a-tag :color="getStatusColor(order.status)">{{ getStatusText(order.status) }}</a-tag>
            </div>
            <div class="order-content">
              <img :src="order.productImage || defaultImage" class="product-image" />
              <div class="product-info">
                <h3>{{ order.productTitle }}</h3>
                <div class="order-meta">
                  <span>买家: {{ order.buyerName }}</span>
                  <span>卖家: {{ order.sellerName }}</span>
                </div>
                <div class="order-meta">
                  <span>{{ formatTime(order.createTime) }}</span>
                  <span>{{ getTradeTypeText(order.tradeType) }}</span>
                </div>
              </div>
              <div class="order-price">
                <span class="price">¥{{ order.amount }}</span>
              </div>
            </div>
            <div class="order-footer">
              <a-space>
                <template v-if="order.status === 'PENDING' && order.sellerId === userStore.userInfo?.id">
                  <a-button type="primary" size="small" @click.stop="handleShip(order)">
                    发货
                  </a-button>
                </template>
                <template v-if="order.status === 'SHIPPED' && order.buyerId === userStore.userInfo?.id">
                  <a-button type="primary" size="small" @click.stop="handleConfirm(order)">
                    确认收货
                  </a-button>
                </template>
                <template v-if="order.status === 'PENDING'">
                  <a-button size="small" danger @click.stop="handleCancel(order)">
                    取消订单
                  </a-button>
                </template>
                <template v-if="order.status === 'COMPLETED' && order.buyerId === userStore.userInfo?.id">
                  <a-button type="primary" size="small" @click.stop="handleReview(order)">
                    评价
                  </a-button>
                </template>
              </a-space>
            </div>
          </div>
        </div>
        <a-empty v-else description="暂无订单" />
      </a-spin>

      <div class="pagination-wrapper" v-if="total > 0">
        <a-pagination
          v-model:current="pagination.page"
          v-model:pageSize="pagination.pageSize"
          :total="total"
          show-quick-jumper
          :show-total="(total: number) => `共 ${total} 个订单`"
          @change="handlePageChange"
        />
      </div>
    </a-card>

    <a-modal
      v-model:open="shipModalVisible"
      title="发货"
      @ok="confirmShip"
      :confirm-loading="shipLoading"
    >
      <a-form layout="vertical">
        <a-form-item label="快递单号">
          <a-input v-model:value="expressNo" placeholder="请输入快递单号" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="cancelModalVisible"
      title="取消订单"
      @ok="confirmCancel"
      :confirm-loading="cancelLoading"
    >
      <a-form layout="vertical">
        <a-form-item label="取消原因">
          <a-textarea v-model:value="cancelReason" placeholder="请输入取消原因" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { orderApi } from '@/api'
import type { Order } from '@/types'
import { message, Modal } from 'ant-design-vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const orders = ref<Order[]>([])
const total = ref(0)
const activeTab = ref('all')

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const shipModalVisible = ref(false)
const shipLoading = ref(false)
const expressNo = ref('')
const currentOrder = ref<Order | null>(null)

const cancelModalVisible = ref(false)
const cancelLoading = ref(false)
const cancelReason = ref('')

const defaultImage = '/no-image.svg'

const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    PENDING: 'orange',
    SHIPPED: 'blue',
    COMPLETED: 'green',
    CANCELLED: 'gray'
  }
  return colors[status] || 'default'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    PENDING: '待发货',
    SHIPPED: '待收货',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return texts[status] || status
}

const getTradeTypeText = (type: string) => {
  const texts: Record<string, string> = {
    FACE_TO_FACE: '当面交易',
    EXPRESS: '快递邮寄'
  }
  return texts[type] || type
}

const formatTime = (time: string) => {
  return new Date(time).toLocaleString('zh-CN')
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await orderApi.getOrders(pagination.page, pagination.pageSize)
    orders.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  pagination.page = 1
  fetchOrders()
}

const handlePageChange = () => {
  fetchOrders()
}

const handleOrderClick = (orderId: number) => {
  router.push(`/orders/${orderId}`)
}

const handleShip = (order: Order) => {
  currentOrder.value = order
  expressNo.value = ''
  shipModalVisible.value = true
}

const confirmShip = async () => {
  if (!expressNo.value) {
    message.warning('请输入快递单号')
    return
  }
  shipLoading.value = true
  try {
    await orderApi.shipOrder(currentOrder.value!.id, expressNo.value)
    message.success('发货成功')
    shipModalVisible.value = false
    fetchOrders()
  } finally {
    shipLoading.value = false
  }
}

const handleConfirm = (order: Order) => {
  Modal.confirm({
    title: '确认收货',
    content: '确定已收到商品吗？',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      await orderApi.confirmOrder(order.id)
      message.success('确认收货成功')
      fetchOrders()
    }
  })
}

const handleCancel = (order: Order) => {
  currentOrder.value = order
  cancelReason.value = ''
  cancelModalVisible.value = true
}

const confirmCancel = async () => {
  cancelLoading.value = true
  try {
    await orderApi.cancelOrder(currentOrder.value!.id, cancelReason.value)
    message.success('订单已取消')
    cancelModalVisible.value = false
    fetchOrders()
  } finally {
    cancelLoading.value = false
  }
}

const handleReview = (order: Order) => {
  router.push(`/reviews/create?orderId=${order.id}`)
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.orders-page {
  background: #f5f5f5;
  min-height: calc(100vh - 200px);
}

.page-header {
  margin-bottom: 16px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.orders-card {
  border-radius: 12px;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.order-item:hover {
  border-color: #1890ff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-no {
  color: #999;
  font-size: 12px;
}

.order-content {
  display: flex;
  gap: 16px;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
}

.product-info h3 {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 8px;
  color: #333;
}

.order-meta {
  display: flex;
  gap: 16px;
  color: #999;
  font-size: 12px;
  margin-bottom: 4px;
}

.order-price {
  text-align: right;
}

.price {
  font-size: 18px;
  font-weight: 600;
  color: #f5222d;
}

.order-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .order-content {
    flex-direction: column;
  }

  .product-image {
    width: 100%;
    height: 150px;
  }

  .order-price {
    text-align: left;
    margin-top: 8px;
  }
}
</style>
