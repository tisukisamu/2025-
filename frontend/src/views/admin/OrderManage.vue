<template>
  <div class="admin-page">
    <div class="back-nav">
      <a-button type="text" @click="router.back()">
        <left-outlined /> 返回
      </a-button>
    </div>
    <div class="page-header">
      <h1>订单管理</h1>
    </div>

    <a-card class="table-card">
      <template #extra>
        <a-select v-model:value="filterStatus" style="width: 120px" @change="fetchOrders">
          <a-select-option value="">全部状态</a-select-option>
          <a-select-option value="PENDING">待发货</a-select-option>
          <a-select-option value="SHIPPED">待收货</a-select-option>
          <a-select-option value="COMPLETED">已完成</a-select-option>
          <a-select-option value="CANCELLED">已取消</a-select-option>
        </a-select>
      </template>
      <a-table
        :columns="columns"
        :data-source="orders"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'amount'">
            <span class="price">¥{{ record.amount }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="router.push(`/orders/${record.id}`)">
              查看
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '@/api'
import type { Order } from '@/types'
import { LeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const loading = ref(false)
const orders = ref<Order[]>([])
const filterStatus = ref('')

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  onChange: (page: number) => {
    pagination.current = page
    fetchOrders()
  }
})

const columns = [
  { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 180 },
  { title: '商品', dataIndex: 'productTitle', key: 'productTitle' },
  { title: '买家', dataIndex: 'buyerName', key: 'buyerName', width: 100 },
  { title: '卖家', dataIndex: 'sellerName', key: 'sellerName', width: 100 },
  { title: '金额', dataIndex: 'amount', key: 'amount', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 80 }
]

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

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await adminApi.getOrders(
      pagination.current,
      pagination.pageSize,
      filterStatus.value as Order['status']
    )
    orders.value = res.data.list
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.admin-page {
  min-height: calc(100vh - 200px);
}

.back-nav {
  margin-bottom: 16px;
}

.back-nav :deep(.ant-btn) {
  color: #71717a;
  padding: 4px 8px;
}

.back-nav :deep(.ant-btn:hover) {
  background: #f4f4f5;
  color: #18181b;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #18181b;
}

.table-card {
  border-radius: 12px;
}

.price {
  color: #f5222d;
  font-weight: 500;
}
</style>
