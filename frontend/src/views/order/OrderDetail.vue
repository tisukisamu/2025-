<template>
  <div class="order-detail-page">
    <a-spin :spinning="loading">
      <template v-if="order">
        <a-page-header
          :title="`订单 ${order.orderNo}`"
          @back="router.back()"
        >
          <template #extra>
            <a-tag :color="getStatusColor(order.status)">{{ getStatusText(order.status) }}</a-tag>
          </template>
        </a-page-header>

        <a-row :gutter="[24, 24]">
          <a-col :xs="24" :lg="16">
            <a-card title="订单信息" class="info-card">
              <a-descriptions :column="2" bordered>
                <a-descriptions-item label="商品名称">{{ order.productTitle }}</a-descriptions-item>
                <a-descriptions-item label="订单金额">
                  <span class="price">¥{{ order.amount }}</span>
                </a-descriptions-item>
                <a-descriptions-item label="交易方式">{{ getTradeTypeText(order.tradeType) }}</a-descriptions-item>
                <a-descriptions-item label="订单状态">{{ getStatusText(order.status) }}</a-descriptions-item>
                <a-descriptions-item label="创建时间">{{ formatTime(order.createTime) }}</a-descriptions-item>
                <a-descriptions-item label="收货地址" v-if="order.address">{{ order.address }}</a-descriptions-item>
                <a-descriptions-item label="快递单号" v-if="order.expressNo">{{ order.expressNo }}</a-descriptions-item>
                <a-descriptions-item label="发货时间" v-if="order.shipTime">{{ formatTime(order.shipTime) }}</a-descriptions-item>
                <a-descriptions-item label="完成时间" v-if="order.completeTime">{{ formatTime(order.completeTime) }}</a-descriptions-item>
                <a-descriptions-item label="取消原因" v-if="order.cancelReason">{{ order.cancelReason }}</a-descriptions-item>
              </a-descriptions>
            </a-card>

            <a-card title="订单进度" class="timeline-card">
              <a-steps :current="getCurrentStep()" :status="getStepsStatus()">
                <a-step title="创建订单" :description="formatTime(order.createTime)" />
                <a-step title="卖家发货" :description="order.shipTime ? formatTime(order.shipTime) : '等待发货'" />
                <a-step title="确认收货" :description="order.completeTime ? formatTime(order.completeTime) : '等待收货'" />
                <a-step title="交易完成" />
              </a-steps>
            </a-card>
          </a-col>

          <a-col :xs="24" :lg="8">
            <a-card title="买家信息" class="user-card">
              <div class="user-info">
                <a-avatar :src="order.buyerAvatar" :size="48">
                  {{ order.buyerName?.charAt(0) }}
                </a-avatar>
                <div class="user-detail">
                  <div class="user-name">{{ order.buyerName }}</div>
                  <div class="user-role">买家</div>
                </div>
              </div>
              <a-button block @click="router.push(`/chat/${order.buyerId}`)">
                <message-outlined />
                联系买家
              </a-button>
            </a-card>

            <a-card title="卖家信息" class="user-card">
              <div class="user-info">
                <a-avatar :src="order.sellerAvatar" :size="48">
                  {{ order.sellerName?.charAt(0) }}
                </a-avatar>
                <div class="user-detail">
                  <div class="user-name">{{ order.sellerName }}</div>
                  <div class="user-role">卖家</div>
                </div>
              </div>
              <a-button block @click="router.push(`/chat/${order.sellerId}`)">
                <message-outlined />
                联系卖家
              </a-button>
            </a-card>

            <a-card title="操作" class="action-card">
              <a-space direction="vertical" style="width: 100%">
                <a-button
                  v-if="order.status === 'PENDING' && order.sellerId === userStore.userInfo?.id"
                  type="primary"
                  block
                  @click="showShipModal = true"
                >
                  发货
                </a-button>
                <a-button
                  v-if="order.status === 'SHIPPED' && order.buyerId === userStore.userInfo?.id"
                  type="primary"
                  block
                  @click="handleConfirm"
                >
                  确认收货
                </a-button>
                <a-button
                  v-if="order.status === 'PENDING'"
                  danger
                  block
                  @click="showCancelModal = true"
                >
                  取消订单
                </a-button>
              </a-space>
            </a-card>
          </a-col>
        </a-row>
      </template>
    </a-spin>

    <a-modal v-model:open="showShipModal" title="发货" @ok="handleShip" :confirm-loading="shipLoading">
      <a-form layout="vertical">
        <a-form-item label="快递单号" required>
          <a-input v-model:value="expressNo" placeholder="请输入快递单号" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal v-model:open="showCancelModal" title="取消订单" @ok="handleCancel" :confirm-loading="cancelLoading">
      <a-form layout="vertical">
        <a-form-item label="取消原因">
          <a-textarea v-model:value="cancelReason" placeholder="请输入取消原因" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { orderApi } from '@/api'
import type { Order } from '@/types'
import { message, Modal } from 'ant-design-vue'
import { MessageOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const order = ref<Order | null>(null)

const showShipModal = ref(false)
const shipLoading = ref(false)
const expressNo = ref('')

const showCancelModal = ref(false)
const cancelLoading = ref(false)
const cancelReason = ref('')

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

const getCurrentStep = () => {
  if (!order.value) return 0
  const steps: Record<string, number> = {
    PENDING: 0,
    SHIPPED: 1,
    COMPLETED: 2,
    CANCELLED: 0
  }
  return steps[order.value.status] || 0
}

const getStepsStatus = () => {
  if (!order.value) return 'process'
  return order.value.status === 'CANCELLED' ? 'error' : 'process'
}

const fetchOrder = async () => {
  const id = Number(route.params.id)
  if (!id || isNaN(id)) {
    message.error('订单ID无效')
    router.push('/orders')
    return
  }
  loading.value = true
  try {
    const res = await orderApi.getOrderDetail(id)
    order.value = res.data
  } catch {
    message.error('获取订单详情失败')
    router.push('/orders')
  } finally {
    loading.value = false
  }
}

const handleShip = async () => {
  if (!expressNo.value) {
    message.warning('请输入快递单号')
    return
  }
  shipLoading.value = true
  try {
    await orderApi.shipOrder(order.value!.id, expressNo.value)
    message.success('发货成功')
    showShipModal.value = false
    fetchOrder()
  } finally {
    shipLoading.value = false
  }
}

const handleConfirm = () => {
  Modal.confirm({
    title: '确认收货',
    content: '确定已收到商品吗？',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      await orderApi.confirmOrder(order.value!.id)
      message.success('确认收货成功')
      fetchOrder()
    }
  })
}

const handleCancel = async () => {
  cancelLoading.value = true
  try {
    await orderApi.cancelOrder(order.value!.id, cancelReason.value)
    message.success('订单已取消')
    showCancelModal.value = false
    fetchOrder()
  } finally {
    cancelLoading.value = false
  }
}

onMounted(() => {
  fetchOrder()
})
</script>

<style scoped>
.order-detail-page {
  background: #f5f5f5;
  min-height: calc(100vh - 200px);
}

.info-card,
.timeline-card,
.user-card,
.action-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.price {
  color: #f5222d;
  font-size: 18px;
  font-weight: 600;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.user-name {
  font-weight: 500;
}

.user-role {
  font-size: 12px;
  color: #999;
}
</style>
