<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">支付历史</h1>
      <p class="text-gray-600 mt-1">查看所有支付记录和账单详情</p>
    </div>

    <a-card class="mb-6">
      <a-row :gutter="16">
        <a-col :xs="24" :sm="12" :lg="6">
          <a-statistic
            title="本月收入"
            :value="monthlyIncome"
            prefix="¥"
            :precision="2"
            :value-style="{ color: '#3f8600' }"
          />
        </a-col>
        <a-col :xs="24" :sm="12" :lg="6">
          <a-statistic
            title="本月支出"
            :value="monthlyExpense"
            prefix="¥"
            :precision="2"
            :value-style="{ color: '#cf1322' }"
          />
        </a-col>
        <a-col :xs="24" :sm="12" :lg="6">
          <a-statistic title="待支付订单" :value="pendingPayments" suffix="笔" />
        </a-col>
        <a-col :xs="24" :sm="12" :lg="6">
          <a-statistic title="本月交易笔数" :value="monthlyTransactions" suffix="笔" />
        </a-col>
      </a-row>
    </a-card>

    <a-card class="mb-6">
      <a-form layout="inline">
        <a-form-item label="交易类型">
          <a-select v-model:value="searchForm.type" placeholder="选择类型" style="width: 150px" allow-clear>
            <a-select-option value="INCOME">收入</a-select-option>
            <a-select-option value="EXPENSE">支出</a-select-option>
            <a-select-option value="REFUND">退款</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="支付方式">
          <a-select v-model:value="searchForm.method" placeholder="选择方式" style="width: 150px" allow-clear>
            <a-select-option value="CASH">现金</a-select-option>
            <a-select-option value="WECHAT">微信支付</a-select-option>
            <a-select-option value="ALIPAY">支付宝</a-select-option>
            <a-select-option value="BANK">银行转账</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="交易状态">
          <a-select v-model:value="searchForm.status" placeholder="选择状态" style="width: 120px" allow-clear>
            <a-select-option value="SUCCESS">成功</a-select-option>
            <a-select-option value="PENDING">待支付</a-select-option>
            <a-select-option value="FAILED">失败</a-select-option>
            <a-select-option value="REFUNDED">已退款</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="交易时间">
          <a-range-picker v-model:value="searchForm.dateRange" />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch">
              <template #icon><SearchOutlined /></template>
              搜索
            </a-button>
            <a-button @click="handleReset">重置</a-button>
            <a-button @click="exportRecords">
              <template #icon><DownloadOutlined /></template>
              导出
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card>
      <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
        <a-tab-pane key="all" tab="全部交易">
          <a-table
            :columns="columns"
            :data-source="payments"
            :pagination="pagination"
            :loading="loading"
            @change="handleTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'type'">
                <a-tag :color="getTypeColor(record.type)">
                  {{ getTypeText(record.type) }}
                </a-tag>
              </template>
              <template v-if="column.key === 'amount'">
                <span :style="{ color: record.type === 'INCOME' ? '#3f8600' : '#cf1322' }">
                  {{ record.type === 'INCOME' ? '+' : '-' }}¥{{ record.amount }}
                </span>
              </template>
              <template v-if="column.key === 'method'">
                <a-tag>{{ getMethodText(record.method) }}</a-tag>
              </template>
              <template v-if="column.key === 'status'">
                <a-tag :color="getStatusColor(record.status)">
                  {{ getStatusText(record.status) }}
                </a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-space>
                  <a-button type="link" size="small" @click="viewDetail(record)">详情</a-button>
                  <a-button 
                    type="link" 
                    size="small" 
                    v-if="record.status === 'PENDING'"
                    @click="confirmPayment(record)"
                  >
                    确认支付
                  </a-button>
                  <a-button 
                    type="link" 
                    size="small" 
                    v-if="record.status === 'SUCCESS' && record.type === 'INCOME'"
                    @click="refundPayment(record)"
                  >
                    退款
                  </a-button>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="income" tab="收入">
          <a-table
            :columns="columns"
            :data-source="incomePayments"
            :pagination="pagination"
            :loading="loading"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'amount'">
                <span style="color: #3f8600">+¥{{ record.amount }}</span>
              </template>
              <template v-if="column.key === 'status'">
                <a-tag :color="getStatusColor(record.status)">
                  {{ getStatusText(record.status) }}
                </a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-button type="link" size="small" @click="viewDetail(record)">详情</a-button>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="expense" tab="支出">
          <a-table
            :columns="columns"
            :data-source="expensePayments"
            :pagination="pagination"
            :loading="loading"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'amount'">
                <span style="color: #cf1322">-¥{{ record.amount }}</span>
              </template>
              <template v-if="column.key === 'status'">
                <a-tag :color="getStatusColor(record.status)">
                  {{ getStatusText(record.status) }}
                </a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-button type="link" size="small" @click="viewDetail(record)">详情</a-button>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="pending" tab="待支付">
          <a-table
            :columns="columns"
            :data-source="pendingPaymentsList"
            :pagination="pagination"
            :loading="loading"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'amount'">
                <span style="color: #fa8c16">¥{{ record.amount }}</span>
              </template>
              <template v-if="column.key === 'status'">
                <a-tag color="orange">待支付</a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-space>
                  <a-button type="link" size="small" @click="viewDetail(record)">详情</a-button>
                  <a-button type="primary" size="small" @click="confirmPayment(record)">
                    确认支付
                  </a-button>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <a-modal
      v-model:open="detailModalVisible"
      title="交易详情"
      width="700px"
      :footer="null"
    >
      <a-descriptions :column="2" bordered v-if="currentPayment">
        <a-descriptions-item label="交易单号">{{ currentPayment.transactionId }}</a-descriptions-item>
        <a-descriptions-item label="交易类型">
          <a-tag :color="getTypeColor(currentPayment.type)">
            {{ getTypeText(currentPayment.type) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="交易金额">
          <span :style="{ color: currentPayment.type === 'INCOME' ? '#3f8600' : '#cf1322', fontSize: '18px', fontWeight: 'bold' }">
            {{ currentPayment.type === 'INCOME' ? '+' : '-' }}¥{{ currentPayment.amount }}
          </span>
        </a-descriptions-item>
        <a-descriptions-item label="支付方式">
          <a-tag>{{ getMethodText(currentPayment.method) }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="交易状态">
          <a-tag :color="getStatusColor(currentPayment.status)">
            {{ getStatusText(currentPayment.status) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="交易时间">{{ currentPayment.paidAt }}</a-descriptions-item>
        <a-descriptions-item label="关联课程">{{ currentPayment.courseName }}</a-descriptions-item>
        <a-descriptions-item label="关联学员">{{ currentPayment.studentName }}</a-descriptions-item>
        <a-descriptions-item label="备注" :span="2">{{ currentPayment.note || '暂无备注' }}</a-descriptions-item>
      </a-descriptions>

      <a-divider>交易流水</a-divider>

      <a-timeline>
        <a-timeline-item v-for="(log, index) in paymentLogs" :key="index">
          <div>
            <div class="font-bold">{{ log.action }}</div>
            <div class="text-sm text-gray-500">{{ log.time }}</div>
            <div class="text-sm text-gray-600 mt-1">{{ log.description }}</div>
          </div>
        </a-timeline-item>
      </a-timeline>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { SearchOutlined, DownloadOutlined } from '@ant-design/icons-vue'

interface Payment {
  id: number
  transactionId: string
  type: string
  amount: number
  method: string
  status: string
  courseName: string
  studentName: string
  paidAt: string
  note: string
}

const searchForm = reactive({
  type: undefined,
  method: undefined,
  status: undefined,
  dateRange: null
})

const payments = ref<Payment[]>([])
const allPayments = ref<Payment[]>([])
const loading = ref(false)
const activeTab = ref('all')
const detailModalVisible = ref(false)
const currentPayment = ref<Payment | null>(null)

const monthlyIncome = computed(() =>
  allPayments.value
    .filter(p => p.type === 'INCOME' && p.status !== 'FAILED')
    .reduce((sum, p) => sum + p.amount, 0)
)
const monthlyExpense = computed(() =>
  allPayments.value
    .filter(p => p.type === 'EXPENSE' && p.status !== 'FAILED')
    .reduce((sum, p) => sum + p.amount, 0)
)
const pendingPayments = computed(() => allPayments.value.filter(p => p.status === 'PENDING').length)
const monthlyTransactions = computed(() => allPayments.value.length)

const incomePayments = computed(() => payments.value.filter(p => p.type === 'INCOME'))
const expensePayments = computed(() => payments.value.filter(p => p.type === 'EXPENSE'))
const pendingPaymentsList = computed(() => payments.value.filter(p => p.status === 'PENDING'))

const paymentLogs = ref([
  { action: '创建订单', time: '2024-02-20 10:00:00', description: '学员张三报名Hip-Hop基础班' },
  { action: '支付成功', time: '2024-02-20 10:05:00', description: '微信支付1200元' },
  { action: '订单完成', time: '2024-02-20 10:05:01', description: '系统自动确认订单' }
])

const columns = [
  { title: '交易单号', dataIndex: 'transactionId', key: 'transactionId' },
  { title: '交易类型', key: 'type' },
  { title: '交易金额', key: 'amount' },
  { title: '支付方式', key: 'method' },
  { title: '关联课程', dataIndex: 'courseName', key: 'courseName' },
  { title: '关联学员', dataIndex: 'studentName', key: 'studentName' },
  { title: '交易时间', dataIndex: 'paidAt', key: 'paidAt' },
  { title: '状态', key: 'status' },
  { title: '操作', key: 'action', fixed: 'right' }
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const getTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    INCOME: 'green',
    EXPENSE: 'red',
    REFUND: 'orange'
  }
  return colorMap[type] || 'default'
}

const getTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    INCOME: '收入',
    EXPENSE: '支出',
    REFUND: '退款'
  }
  return textMap[type] || type
}

const getMethodText = (method: string) => {
  const textMap: Record<string, string> = {
    CASH: '现金',
    WECHAT: '微信支付',
    ALIPAY: '支付宝',
    BANK: '银行转账'
  }
  return textMap[method] || method
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    SUCCESS: 'green',
    PENDING: 'orange',
    FAILED: 'red',
    REFUNDED: 'default'
  }
  return colorMap[status] || 'default'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    SUCCESS: '成功',
    PENDING: '待支付',
    FAILED: '失败',
    REFUNDED: '已退款'
  }
  return textMap[status] || status
}

const loadPayments = async () => {
  loading.value = true
  try {
    const mockPayments: Payment[] = [
      {
        id: 1,
        transactionId: 'TXN202402200001',
        type: 'INCOME',
        amount: 1200,
        method: 'WECHAT',
        status: 'SUCCESS',
        courseName: 'Hip-Hop基础班',
        studentName: '张三',
        paidAt: '2024-02-20 10:05:00',
        note: ''
      },
      {
        id: 2,
        transactionId: 'TXN202402200002',
        type: 'INCOME',
        amount: 1500,
        method: 'ALIPAY',
        status: 'PENDING',
        courseName: '爵士舞进阶班',
        studentName: '李四',
        paidAt: '2024-02-20 14:20:00',
        note: '待确认支付'
      },
      {
        id: 3,
        transactionId: 'TXN202402190001',
        type: 'EXPENSE',
        amount: 5000,
        method: 'BANK',
        status: 'SUCCESS',
        courseName: '教师工资',
        studentName: '-',
        paidAt: '2024-02-19 18:00:00',
        note: '张老师2月份工资'
      }
    ]
    allPayments.value = mockPayments
    applyFilters()
  } catch (error) {
    message.error('加载支付记录失败')
  } finally {
    loading.value = false
  }
}

const applyFilters = () => {
  let list = [...allPayments.value]

  if (searchForm.type) {
    list = list.filter(item => item.type === searchForm.type)
  }
  if (searchForm.method) {
    list = list.filter(item => item.method === searchForm.method)
  }
  if (searchForm.status) {
    list = list.filter(item => item.status === searchForm.status)
  }
  if (searchForm.dateRange && Array.isArray(searchForm.dateRange) && searchForm.dateRange.length === 2) {
    const [start, end]: any[] = searchForm.dateRange as any[]
    const startTime = new Date(start.format('YYYY-MM-DD 00:00:00')).getTime()
    const endTime = new Date(end.format('YYYY-MM-DD 23:59:59')).getTime()
    list = list.filter(item => {
      const value = new Date(item.paidAt).getTime()
      return value >= startTime && value <= endTime
    })
  }

  if (activeTab.value === 'income') {
    list = list.filter(item => item.type === 'INCOME')
  } else if (activeTab.value === 'expense') {
    list = list.filter(item => item.type === 'EXPENSE')
  } else if (activeTab.value === 'pending') {
    list = list.filter(item => item.status === 'PENDING')
  }

  payments.value = list
  pagination.total = list.length
}

const handleSearch = () => {
  pagination.current = 1
  applyFilters()
}

const handleReset = () => {
  Object.assign(searchForm, {
    type: undefined,
    method: undefined,
    status: undefined,
    dateRange: null
  })
  pagination.current = 1
  applyFilters()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  applyFilters()
}

const handleTabChange = (key: string) => {
  activeTab.value = key
  pagination.current = 1
  applyFilters()
}

const viewDetail = (payment: Payment) => {
  currentPayment.value = payment
  detailModalVisible.value = true
}

const confirmPayment = (payment: Payment) => {
  Modal.confirm({
    title: '确认支付',
    content: `确定已收到 ${payment.studentName} 的支付款项 ¥${payment.amount} 吗？`,
    onOk() {
      const target = allPayments.value.find(item => item.id === payment.id)
      if (target) {
        target.status = 'SUCCESS'
      }
      message.success('支付确认成功')
      applyFilters()
    }
  })
}

const refundPayment = (payment: Payment) => {
  Modal.confirm({
    title: '确认退款',
    content: `确定要退款 ¥${payment.amount} 给 ${payment.studentName} 吗？`,
    onOk() {
      const target = allPayments.value.find(item => item.id === payment.id)
      if (target) {
        target.status = 'REFUNDED'
      }
      message.success('退款成功')
      applyFilters()
    }
  })
}

const exportRecords = () => {
  message.loading('正在导出支付记录...', 1).then(() => {
    message.success('导出成功')
  })
}

onMounted(() => {
  loadPayments()
})
</script>
