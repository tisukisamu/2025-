<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../api'
import { message } from 'ant-design-vue'
import {
  FileTextOutlined,
  ArrowLeftOutlined,
  ShoppingOutlined,
  CarOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  EnvironmentOutlined,
  PhoneOutlined,
  UserOutlined,
  InboxOutlined,
  EyeOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const orders = ref([])
const loading = ref(false)
const activeTab = ref('all')

const statusMap: Record<OrderStatus, { label: string; color: string; icon: any; bgColor: string }> = {
  0: {
    label: '待支付',
    color: '#f59e0b',
    icon: ClockCircleOutlined,
    bgColor: '#fef3c7'
  },
  1: {
    label: '待发货',
    color: '#3b82f6',
    icon: InboxOutlined,
    bgColor: '#dbeafe'
  },
  2: {
    label: '已发货',
    color: '#8b5cf6',
    icon: CarOutlined,
    bgColor: '#ede9fe'
  },
  3: {
    label: '已完成',
    color: '#10b981',
    icon: CheckCircleOutlined,
    bgColor: '#d1fae5'
  }
}

const tabs = [
  { key: 'all', label: '全部订单' },
  { key: '0', label: '待支付' },
  { key: '1', label: '待发货' },
  { key: '2', label: '已发货' },
  { key: '3', label: '已完成' }
]

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orders.value
  return orders.value.filter((order: any) => order.status === parseInt(activeTab.value))
})

onMounted(async () => {
  loading.value = true
  try {
    orders.value = await orderApi.getMyOrders()
  } catch (error) {
    message.error('加载订单失败')
  } finally {
    loading.value = false
  }
})

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatPrice = (price: number) => {
  return `￥${price?.toFixed(2) || '0.00'}`
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-gray-100">
    <!-- 导航栏 -->
    <nav class="sticky top-0 z-50 glass-effect shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          <div class="flex items-center gap-3">
            <a-button type="text" class="flex items-center gap-2 !border-0 !shadow-none" @click="router.push('/')">
              <ArrowLeftOutlined class="text-gray-600" />
              <span class="hidden sm:inline text-gray-700 font-medium">返回首页</span>
            </a-button>
          </div>
          <div class="flex items-center gap-2">
            <FileTextOutlined class="text-xl text-emerald-600" />
            <span class="text-lg font-semibold text-gray-800">我的订单</span>
          </div>
          <div class="w-20"></div>
        </div>
      </div>
    </nav>

    <!-- 主要内容 -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <!-- 标签页 -->
      <div class="bg-white rounded-xl shadow-sm p-2 mb-6 border border-gray-100">
        <div class="flex flex-wrap gap-1">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            class="flex-1 sm:flex-none px-4 py-2.5 rounded-lg font-medium transition-all duration-300 text-sm border-0 outline-none focus:outline-none"
            :class="activeTab === tab.key
              ? 'bg-emerald-500 text-white shadow-md'
              : 'text-gray-600 hover:bg-gray-50'"
            @click="activeTab = tab.key"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>

      <!-- 订单列表 -->
      <a-spin :spinning="loading" size="large">
        <!-- 空状态 -->
        <div v-if="filteredOrders.length === 0" class="flex flex-col items-center justify-center py-20">
          <div class="w-32 h-32 rounded-full bg-gradient-to-br from-gray-50 to-gray-100 flex items-center justify-center mb-6 shadow-inner">
            <ShoppingOutlined class="text-6xl text-gray-300" />
          </div>
          <h3 class="text-xl font-semibold text-gray-700 mb-2">暂无订单</h3>
          <p class="text-gray-500 mb-6">您还没有相关订单，快去选购心仪的商品吧</p>
          <a-button type="primary" size="large" class="!border-0 !shadow-none !rounded-lg px-8" @click="router.push('/')">
            去购物
          </a-button>
        </div>

        <!-- 订单卡片列表 -->
        <div v-else class="space-y-5">
          <div
            v-for="order in filteredOrders"
            :key="order.id"
            class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow duration-300"
          >
            <!-- 订单头部 -->
            <div class="bg-gradient-to-r from-gray-50 to-slate-50 px-5 py-4 border-b border-gray-100">
              <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                <div class="flex items-center gap-4">
                  <span class="text-gray-600 text-sm font-medium">订单号: <span class="text-gray-800">{{ order.orderNo }}</span></span>
                  <span class="hidden sm:block text-gray-300">|</span>
                  <span class="text-gray-500 text-sm">{{ formatDate(order.createTime) }}</span>
                </div>
                <div
                  class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full"
                  :style="{ backgroundColor: statusMap[order.status]?.bgColor }"
                >
                  <component
                    :is="statusMap[order.status]?.icon"
                    class="text-base"
                    :style="{ color: statusMap[order.status]?.color }"
                  />
                  <span
                    class="font-semibold text-sm"
                    :style="{ color: statusMap[order.status]?.color }"
                  >
                    {{ statusMap[order.status]?.label }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 订单商品 -->
            <div class="p-5">
              <div
                v-for="(item, index) in order.items"
                :key="item.id"
                class="flex items-start gap-4 py-4"
                :class="{ 'border-b border-gray-50': index < order.items.length - 1 }"
              >
                <div class="w-24 h-24 rounded-lg bg-gray-50 flex-shrink-0 overflow-hidden border border-gray-100">
                  <img
                    v-if="item.imageUrl"
                    :src="item.imageUrl"
                    :alt="item.productName"
                    class="w-full h-full object-cover"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
                    <ShoppingOutlined class="text-2xl" />
                  </div>
                </div>
                <div class="flex-1 min-w-0 flex flex-col justify-between">
                  <div>
                    <h4 class="font-semibold text-gray-800 mb-1 line-clamp-2">{{ item.productName }}</h4>
                    <p class="text-gray-500 text-sm">单价: <span class="text-gray-700 font-medium">{{ formatPrice(item.price) }}</span></p>
                  </div>
                  <div class="flex items-center justify-between mt-2">
                    <span class="text-gray-600 text-sm">数量: <span class="font-semibold">{{ item.quantity }}</span></span>
                    <span class="text-lg font-bold text-rose-500">{{ formatPrice(item.price * item.quantity) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 订单底部 -->
            <div class="px-5 py-4 bg-gradient-to-r from-gray-50/50 to-slate-50/50 border-t border-gray-100">
              <div class="flex flex-col gap-4">
                <!-- 收货信息 -->
                <div v-if="order.contact || order.phone || order.address" class="flex flex-wrap items-center gap-4 text-sm">
                  <span v-if="order.contact" class="flex items-center gap-1.5 text-gray-600 bg-white px-3 py-1.5 rounded-lg border border-gray-100">
                    <UserOutlined class="text-emerald-500" />
                    {{ order.contact }}
                  </span>
                  <span v-if="order.phone" class="flex items-center gap-1.5 text-gray-600 bg-white px-3 py-1.5 rounded-lg border border-gray-100">
                    <PhoneOutlined class="text-emerald-500" />
                    {{ order.phone }}
                  </span>
                  <span v-if="order.address" class="flex items-center gap-1.5 text-gray-600 bg-white px-3 py-1.5 rounded-lg border border-gray-100">
                    <EnvironmentOutlined class="text-emerald-500" />
                    {{ order.address }}
                  </span>
                </div>

                <!-- 物流信息 -->
                <div v-if="order.trackingNo" class="flex items-center gap-2 text-sm text-gray-600 bg-blue-50 px-3 py-2 rounded-lg border border-blue-100">
                  <CarOutlined class="text-blue-500" />
                  <span class="font-medium">物流单号:</span>
                  <span class="text-gray-800">{{ order.trackingNo }}</span>
                </div>

                <!-- 金额和操作 -->
                <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 pt-3 border-t border-gray-200">
                  <div class="flex items-center gap-2">
                    <span class="text-gray-500 text-sm">共 <span class="font-semibold text-gray-700">{{ order.items?.length || 0 }}</span> 件商品</span>
                    <span class="text-gray-400">|</span>
                    <span class="text-gray-500 text-sm">合计:</span>
                    <span class="text-2xl font-bold text-rose-500">{{ formatPrice(order.totalAmount) }}</span>
                  </div>
                  <a-button
                    type="primary"
                    size="large"
                    class="!border-0 !shadow-none !rounded-lg !px-6"
                    @click="$router.push(`/order/tracking?orderNo=${order.orderNo}`)"
                  >
                    <template #icon>
                      <EyeOutlined />
                    </template>
                    查看详情
                  </a-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </a-spin>
    </main>
  </div>
</template>
