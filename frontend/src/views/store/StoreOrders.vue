<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { message } from 'ant-design-vue'
import { CarOutlined, EyeOutlined, SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { storeApi } from '../../api'

type AnyOrder = any

const orders = ref<AnyOrder[]>([])
const loading = ref(false)

const shipVisible = ref(false)
const currentOrder = ref<AnyOrder | null>(null)
const trackingNo = ref('')

/** 筛选 */
const filters = ref({
  status: undefined as number | undefined,
  keyword: ''
})

/** 关键字防抖 */
const debouncedKeyword = ref('')
let kwTimer: number | undefined
watch(
  () => filters.value.keyword,
  (val) => {
    window.clearTimeout(kwTimer)
    kwTimer = window.setTimeout(() => {
      debouncedKeyword.value = (val ?? '').trim()
    }, 250)
  },
  { immediate: true }
)

onMounted(() => {
  loadOrders()
})

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await storeApi.getOrders()
    orders.value = Array.isArray(res) ? res : []
  } catch (error) {
    message.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.value.status = undefined
  filters.value.keyword = ''
}

const showShipModal = (order: AnyOrder) => {
  currentOrder.value = order
  trackingNo.value = ''
  shipVisible.value = true
}

const handleShip = async () => {
  if (!currentOrder.value) return
  if (!trackingNo.value) {
    message.warning('请输入物流单号')
    return
  }
  try {
    await storeApi.shipOrder(currentOrder.value.id, trackingNo.value)
    message.success('发货成功')
    shipVisible.value = false
    loadOrders()
  } catch (error) {
    message.error('发货失败')
  }
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待支付',
    1: '待发货',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return map[status] || '未知'
}

const getStatusColor = (status: number) => {
  const map: Record<number, string> = {
    0: 'orange',
    1: 'processing',
    2: 'green',
    3: 'default',
    4: 'red'
  }
  return map[status] || 'default'
}

const filteredOrders = computed(() => {
  let res = orders.value

  if (filters.value.status !== undefined) {
    res = res.filter((o) => o.status === filters.value.status)
  }

  if (debouncedKeyword.value) {
    const k = debouncedKeyword.value.toLowerCase()
    res = res.filter((o) => {
      const orderNo = String(o.orderNo ?? '').toLowerCase()
      const contact = String(o.contact ?? '').toLowerCase()
      const phone = String(o.phone ?? '').toLowerCase()
      return orderNo.includes(k) || contact.includes(k) || phone.includes(k)
    })
  }

  return res
})

const columns = [
  { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 220 },
  { title: '收货人', dataIndex: 'contact', key: 'contact', width: 120 },
  { title: '电话', dataIndex: 'phone', key: 'phone', width: 140 },
  { title: '金额', dataIndex: 'totalAmount', key: 'totalAmount', width: 120, align: 'right' },
  { title: '状态', dataIndex: 'status', key: 'status', width: 120, align: 'center' },
  { title: '下单时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 180, fixed: 'right' }
]
</script>

<template>
  <div class="store-orders">
    <div class="page-header">
      <h2 class="page-title">订单管理</h2>
      <a-button @click="loadOrders">
        <template #icon><ReloadOutlined /></template>
        刷新
      </a-button>
    </div>

    <!-- 筛选栏（补全） -->
    <a-card class="filter-card" :bordered="false">
      <a-row :gutter="12">
        <a-col :xs="24" :md="10" :lg="8">
          <a-input v-model:value="filters.keyword" allow-clear placeholder="搜索订单号/收货人/电话">
            <template #prefix><SearchOutlined /></template>
          </a-input>
        </a-col>

        <a-col :xs="24" :md="6" :lg="5">
          <a-select
            v-model:value="filters.status"
            allow-clear
            placeholder="订单状态"
            style="width: 100%"
          >
            <a-select-option :value="0">待支付</a-select-option>
            <a-select-option :value="1">待发货</a-select-option>
            <a-select-option :value="2">已发货</a-select-option>
            <a-select-option :value="3">已完成</a-select-option>
            <a-select-option :value="4">已取消</a-select-option>
          </a-select>
        </a-col>

        <a-col :xs="24" :md="8" :lg="6">
          <a-space>
            <a-button @click="resetFilters">重置</a-button>
          </a-space>
        </a-col>
      </a-row>
    </a-card>

    <a-card class="table-card" :bordered="false">
      <a-table
        :columns="columns"
        :data-source="filteredOrders"
        :loading="loading"
        row-key="id"
        :scroll="{ x: 1100 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>

          <template v-else-if="column.key === 'totalAmount'">
            ￥{{ Number(record.totalAmount || 0).toFixed(2) }}
          </template>

          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button
                v-if="record.status === 1"
                type="primary"
                size="small"
                @click="showShipModal(record)"
              >
                <CarOutlined /> 发货
              </a-button>
              <a-button size="small" disabled>
                <EyeOutlined /> 详情
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="shipVisible" title="订单发货" ok-text="确定" cancel-text="取消" @ok="handleShip">
      <a-form layout="vertical">
        <a-form-item label="物流单号" required>
          <a-input v-model:value="trackingNo" placeholder="请输入快递单号" allow-clear />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.store-orders {
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

.filter-card,
.table-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.filter-card {
  margin-bottom: 16px;
}
</style>
