<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">支付与账单</h1>
      <p class="text-gray-600 mt-1">查看个人支付历史与账单信息</p>
    </div>

    <a-tabs v-model:activeKey="activeTab">
      <a-tab-pane key="payments" tab="支付历史">
        <a-card class="mb-4">
          <a-form layout="inline">
            <a-form-item label="交易号">
              <a-input v-model:value="paymentKeyword" placeholder="搜索交易号" allow-clear style="width: 220px" />
            </a-form-item>
            <a-form-item label="状态">
              <a-select v-model:value="paymentStatus" placeholder="全部状态" allow-clear style="width: 160px">
                <a-select-option value="PENDING">待支付</a-select-option>
                <a-select-option value="SUCCESS">已支付</a-select-option>
                <a-select-option value="FAILED">失败</a-select-option>
                <a-select-option value="REFUNDED">已退款</a-select-option>
              </a-select>
            </a-form-item>
          </a-form>
        </a-card>
        <a-card>
          <a-table :columns="paymentColumns" :data-source="filteredPayments" row-key="id">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'amount'">
                ¥{{ record.amount }}
              </template>
              <template v-if="column.key === 'paymentMethod'">
                {{ getPaymentMethodText(record.paymentMethod) }}
              </template>
              <template v-if="column.key === 'status'">
                <a-tag :color="getPaymentStatusColor(record.status)">
                  {{ getPaymentStatusText(record.status) }}
                </a-tag>
              </template>
              <template v-if="column.key === 'paymentTime'">
                {{ formatDateTime(record.paymentTime || record.createdAt) }}
              </template>
            </template>
          </a-table>
        </a-card>
      </a-tab-pane>

      <a-tab-pane key="bills" tab="账单信息">
        <a-card class="mb-4">
          <a-form layout="inline">
            <a-form-item label="账单类型">
              <a-select v-model:value="billType" placeholder="全部类型" allow-clear style="width: 160px">
                <a-select-option value="TUITION">学费</a-select-option>
                <a-select-option value="REGISTRATION">报名费</a-select-option>
                <a-select-option value="OTHER">其他</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="账单状态">
              <a-select v-model:value="billStatus" placeholder="全部状态" allow-clear style="width: 160px">
                <a-select-option value="UNPAID">未支付</a-select-option>
                <a-select-option value="PAID">已支付</a-select-option>
                <a-select-option value="OVERDUE">已逾期</a-select-option>
              </a-select>
            </a-form-item>
          </a-form>
        </a-card>
        <a-card>
          <a-table :columns="billColumns" :data-source="filteredBills" row-key="id">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'amount'">
                ¥{{ record.amount }}
              </template>
              <template v-if="column.key === 'billType'">
                {{ getBillTypeText(record.billType) }}
              </template>
              <template v-if="column.key === 'status'">
                <a-tag :color="getBillStatusColor(record.status)">
                  {{ getBillStatusText(record.status) }}
                </a-tag>
              </template>
              <template v-if="column.key === 'dueDate'">
                {{ formatDateTime(record.dueDate) }}
              </template>
              <template v-if="column.key === 'action'">
                <a-button
                  v-if="record.status === 'UNPAID'"
                  type="primary"
                  size="small"
                  @click="handlePayBill(record)"
                >
                  立即支付
                </a-button>
                <span v-else class="text-gray-400">-</span>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-tab-pane>
    </a-tabs>

    <PaymentModal
      v-model:open="paymentModalVisible"
      :bill="selectedBill"
      @success="handlePaymentSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { getBills, getMyBills, getMyPayments, getPayments } from '@/api/finance'
import type { Bill, Payment } from '@/types'
import { useUserStore } from '@/stores/user'
import PaymentModal from '@/components/PaymentModal.vue'

const activeTab = ref('payments')
const userStore = useUserStore()
const payments = ref<Payment[]>([])
const bills = ref<Bill[]>([])
const paymentKeyword = ref('')
const paymentStatus = ref<string | undefined>(undefined)
const billType = ref<string | undefined>(undefined)
const billStatus = ref<string | undefined>(undefined)
const paymentModalVisible = ref(false)
const selectedBill = ref<Bill | null>(null)

const paymentColumns = [
  { title: '交易号', dataIndex: 'transactionId', key: 'transactionId' },
  { title: '金额', key: 'amount' },
  { title: '支付方式', key: 'paymentMethod' },
  { title: '状态', key: 'status' },
  { title: '支付时间', key: 'paymentTime' }
]

const billColumns = [
  { title: '账单ID', dataIndex: 'id', key: 'id' },
  { title: '账单类型', key: 'billType' },
  { title: '金额', key: 'amount' },
  { title: '到期时间', key: 'dueDate' },
  { title: '状态', key: 'status' },
  { title: '操作', key: 'action' }
]

const filteredPayments = computed(() => {
  let list = [...payments.value]
  if (paymentKeyword.value) {
    list = list.filter(item => (item.transactionId || '').includes(paymentKeyword.value))
  }
  if (paymentStatus.value) {
    list = list.filter(item => item.status === paymentStatus.value)
  }
  return list
})

const filteredBills = computed(() => {
  let list = [...bills.value]
  if (billType.value) {
    list = list.filter(item => item.billType === billType.value)
  }
  if (billStatus.value) {
    list = list.filter(item => item.status === billStatus.value)
  }
  return list
})

const formatDateTime = (value?: string) => {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 19)
}

const getPaymentMethodText = (method?: string) => {
  const map: Record<string, string> = {
    ALIPAY: '支付宝',
    WECHAT: '微信支付',
    CASH: '现金',
    CARD: '银行卡'
  }
  return method ? (map[method] || method) : '-'
}

const getPaymentStatusColor = (status: string) => {
  const map: Record<string, string> = {
    PENDING: 'warning',
    SUCCESS: 'success',
    FAILED: 'error',
    REFUNDED: 'default'
  }
  return map[status] || 'default'
}

const getPaymentStatusText = (status: string) => {
  const map: Record<string, string> = {
    PENDING: '待支付',
    SUCCESS: '已支付',
    FAILED: '失败',
    REFUNDED: '已退款'
  }
  return map[status] || status
}

const getBillTypeText = (type: string) => {
  const map: Record<string, string> = {
    TUITION: '学费',
    REGISTRATION: '报名费',
    OTHER: '其他'
  }
  return map[type] || type
}

const getBillStatusColor = (status: string) => {
  const map: Record<string, string> = {
    UNPAID: 'warning',
    PAID: 'success',
    OVERDUE: 'error'
  }
  return map[status] || 'default'
}

const getBillStatusText = (status: string) => {
  const map: Record<string, string> = {
    UNPAID: '未支付',
    PAID: '已支付',
    OVERDUE: '已逾期'
  }
  return map[status] || status
}

const loadData = async () => {
  try {
    if (userStore.isAdmin) {
      const [paymentRes, billRes] = await Promise.all([
        getPayments({ page: 0, size: 200 }),
        getBills({ page: 0, size: 200 })
      ])
      if (paymentRes.code === 200 && paymentRes.data) {
        payments.value = paymentRes.data.content || []
      }
      if (billRes.code === 200 && billRes.data) {
        bills.value = billRes.data.content || []
      }
    } else {
      const [paymentRes, billRes] = await Promise.all([getMyPayments(), getMyBills()])
      if (paymentRes.code === 200 && paymentRes.data) {
        payments.value = paymentRes.data
      }
      if (billRes.code === 200 && billRes.data) {
        bills.value = billRes.data
      }
    }
  } catch (error) {
    message.error('加载支付与账单信息失败')
  }
}

const handlePayBill = (bill: Bill) => {
  selectedBill.value = bill
  paymentModalVisible.value = true
}

const handlePaymentSuccess = () => {
  loadData()
}

onMounted(() => {
  loadData()
})
</script>
