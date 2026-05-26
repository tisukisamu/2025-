<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-gray-800">财务管理</h1>
      <p class="text-gray-500 mt-1">管理支付记录和账单</p>
    </div>

    <a-tabs v-model:activeKey="activeTab">
      <a-tab-pane key="payments" tab="支付记录">
        <a-card class="mb-4">
          <a-form layout="inline">
            <a-form-item label="时间范围">
              <a-range-picker
                v-model:value="dateRange"
                style="width: 300px"
              />
            </a-form-item>
            <a-form-item label="支付状态">
              <a-select
                v-model:value="filterStatus"
                placeholder="选择状态"
                style="width: 150px"
                allow-clear
              >
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="PENDING">待支付</a-select-option>
                <a-select-option value="SUCCESS">已支付</a-select-option>
                <a-select-option value="FAILED">支付失败</a-select-option>
                <a-select-option value="REFUNDED">已退款</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handleSearch">
                搜索
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>

        <a-card>
          <a-table
            :columns="paymentColumns"
            :data-source="payments"
            :loading="loading"
            :pagination="pagination"
            @change="handleTableChange"
            row-key="id"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'amount'">
                <span class="text-gray-800 font-medium">¥{{ record.amount }}</span>
              </template>
              <template v-else-if="column.key === 'paymentMethod'">
                {{ getPaymentMethodText(record.paymentMethod) }}
              </template>
              <template v-else-if="column.key === 'status'">
                <a-tag :color="getPaymentStatusColor(record.status)">
                  {{ getPaymentStatusText(record.status) }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'action'">
                <a-space>
                  <a-button
                    v-if="record.status === 'PENDING'"
                    type="link"
                    size="small"
                    @click="handleProcess(record)"
                  >
                    处理
                  </a-button>
                  <a-button
                    v-if="record.status === 'SUCCESS'"
                    type="link"
                    size="small"
                    @click="handleRefund(record)"
                  >
                    退款
                  </a-button>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-tab-pane>

      <a-tab-pane key="bills" tab="账单管理">
        <a-card class="mb-4">
          <a-form layout="inline">
            <a-form-item label="账单状态">
              <a-select
                v-model:value="billFilterStatus"
                placeholder="选择状态"
                style="width: 150px"
                allow-clear
              >
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="UNPAID">未支付</a-select-option>
                <a-select-option value="PAID">已支付</a-select-option>
                <a-select-option value="OVERDUE">已逾期</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handleBillSearch">
                搜索
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>

        <a-card>
          <a-table
            :columns="billColumns"
            :data-source="bills"
            :loading="billLoading"
            :pagination="billPagination"
            @change="handleBillTableChange"
            row-key="id"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'amount'">
                <span class="text-gray-800 font-medium">¥{{ record.amount }}</span>
              </template>
              <template v-else-if="column.key === 'billType'">
                {{ getBillTypeText(record.billType) }}
              </template>
              <template v-else-if="column.key === 'status'">
                <a-tag :color="getBillStatusColor(record.status)">
                  {{ getBillStatusText(record.status) }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-tab-pane>

      <a-tab-pane key="statistics" tab="财务统计">
        <a-row :gutter="16" class="mb-4">
          <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
            <a-card>
              <div class="text-center">
                <p class="text-gray-500 text-sm mb-1">总收入</p>
                <p class="text-2xl font-semibold text-gray-800">¥{{ statistics.totalIncome || 0 }}</p>
              </div>
            </a-card>
          </a-col>
          <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
            <a-card>
              <div class="text-center">
                <p class="text-gray-500 text-sm mb-1">成功支付</p>
                <p class="text-2xl font-semibold text-gray-800">{{ statistics.successfulPayments || 0 }}</p>
              </div>
            </a-card>
          </a-col>
          <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
            <a-card>
              <div class="text-center">
                <p class="text-gray-500 text-sm mb-1">待支付</p>
                <p class="text-2xl font-semibold text-gray-800">{{ statistics.pendingPayments || 0 }}</p>
              </div>
            </a-card>
          </a-col>
          <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
            <a-card>
              <div class="text-center">
                <p class="text-gray-500 text-sm mb-1">未支付账单</p>
                <p class="text-2xl font-semibold text-gray-800">{{ statistics.unpaidBills || 0 }}</p>
              </div>
            </a-card>
          </a-col>
        </a-row>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { Payment, Bill } from '@/types'
import { getPayments, processPayment, refundPayment, getBills, getFinanceStatistics } from '@/api/finance'

const activeTab = ref('payments')
const loading = ref(false)
const billLoading = ref(false)
const payments = ref<Payment[]>([])
const bills = ref<Bill[]>([])
const dateRange = ref([])
const filterStatus = ref('')
const billFilterStatus = ref('')

const statistics = ref({
  totalIncome: 0,
  successfulPayments: 0,
  pendingPayments: 0,
  unpaidBills: 0
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total: number) => `共 ${total} 条`
})

const billPagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total: number) => `共 ${total} 条`
})

const paymentColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '支付方式', dataIndex: 'paymentMethod', key: 'paymentMethod' },
  { title: '交易号', dataIndex: 'transactionId', key: 'transactionId' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '支付时间', dataIndex: 'paymentTime', key: 'paymentTime' },
  { title: '操作', key: 'action', width: 150 }
]

const billColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '学员', dataIndex: ['student', 'name'], key: 'student' },
  { title: '账单类型', dataIndex: 'billType', key: 'billType' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '到期日期', dataIndex: 'dueDate', key: 'dueDate' },
  { title: '状态', dataIndex: 'status', key: 'status' }
]

const getPaymentMethodText = (method: string) => {
  const texts: Record<string, string> = {
    ALIPAY: '支付宝',
    WECHAT: '微信支付',
    CASH: '现金',
    CARD: '银行卡'
  }
  return texts[method] || method
}

const getPaymentStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    PENDING: 'warning',
    SUCCESS: 'success',
    FAILED: 'error',
    REFUNDED: 'default'
  }
  return colors[status] || 'default'
}

const getPaymentStatusText = (status: string) => {
  const texts: Record<string, string> = {
    PENDING: '待支付',
    SUCCESS: '已支付',
    FAILED: '支付失败',
    REFUNDED: '已退款'
  }
  return texts[status] || status
}

const getBillTypeText = (type: string) => {
  const texts: Record<string, string> = {
    TUITION: '学费',
    REGISTRATION: '报名费',
    OTHER: '其他'
  }
  return texts[type] || type
}

const getBillStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    UNPAID: 'warning',
    PAID: 'success',
    OVERDUE: 'error'
  }
  return colors[status] || 'default'
}

const getBillStatusText = (status: string) => {
  const texts: Record<string, string> = {
    UNPAID: '未支付',
    PAID: '已支付',
    OVERDUE: '已逾期'
  }
  return texts[status] || status
}

const loadPayments = async () => {
  loading.value = true
  try {
    const res = await getPayments({
      page: pagination.current - 1,
      size: pagination.pageSize
    })
    if (res.code === 200 && res.data) {
      let list = res.data.content || []
      if (filterStatus.value) {
        list = list.filter(item => item.status === filterStatus.value)
      }
      payments.value = list
      pagination.total = filterStatus.value ? list.length : res.data.totalElements
    }
  } catch (error) {
    console.error('加载支付记录失败:', error)
  } finally {
    loading.value = false
  }
}

const loadBills = async () => {
  billLoading.value = true
  try {
    const res = await getBills({
      page: billPagination.current - 1,
      size: billPagination.pageSize
    })
    if (res.code === 200 && res.data) {
      let list = res.data.content || []
      if (billFilterStatus.value) {
        list = list.filter(item => item.status === billFilterStatus.value)
      }
      bills.value = list
      billPagination.total = billFilterStatus.value ? list.length : res.data.totalElements
    }
  } catch (error) {
    console.error('加载账单列表失败:', error)
  } finally {
    billLoading.value = false
  }
}

const loadStatistics = async () => {
  try {
    const start = dateRange.value?.[0]?.format?.('YYYY-MM-DD') || '2024-01-01'
    const end = dateRange.value?.[1]?.format?.('YYYY-MM-DD') || '2024-12-31'
    const res = await getFinanceStatistics(start, end)
    if (res.code === 200 && res.data) {
      statistics.value = res.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadPayments()
  loadStatistics()
}

const handleBillSearch = () => {
  billPagination.current = 1
  loadBills()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadPayments()
}

const handleBillTableChange = (pag: any) => {
  billPagination.current = pag.current
  billPagination.pageSize = pag.pageSize
  loadBills()
}

const handleProcess = async (record: Payment) => {
  try {
    const res = await processPayment(record.id)
    if (res.code === 200) {
      message.success('处理成功')
      loadPayments()
    }
  } catch (error) {
    console.error('处理支付失败:', error)
  }
}

const handleRefund = async (record: Payment) => {
  try {
    const res = await refundPayment(record.id)
    if (res.code === 200) {
      message.success('退款成功')
      loadPayments()
    }
  } catch (error) {
    console.error('退款失败:', error)
  }
}

onMounted(() => {
  loadPayments()
  loadBills()
  loadStatistics()
})
</script>
