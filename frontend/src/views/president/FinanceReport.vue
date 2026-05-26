<template>
  <div class="finance-report">
    <div class="page-header">
      <h2>财务报表</h2>
      <p class="subtitle">查看社团财务流水与统计报表</p>
    </div>

    <div class="filter-bar">
      <a-space>
        <a-select
          v-if="allClubs.length > 1"
          v-model:value="selectedClubId"
          style="width: 250px"
          placeholder="选择社团"
          @change="handleClubChange"
        >
          <a-select-option v-for="club in allClubs" :key="club.id" :value="club.id">
            {{ club.clubName }}
          </a-select-option>
        </a-select>
        <a-range-picker
          v-model:value="dateRange"
          @change="handleDateChange"
          :placeholder="['开始日期', '结束日期']"
          allow-clear
        />
      </a-space>
      <a-button type="primary" @click="exportReport">
        <ExportOutlined /> 导出报表
      </a-button>
    </div>

    <div class="summary-cards">
      <a-row :gutter="24">
        <a-col :span="6">
          <div class="summary-card">
            <div class="summary-icon balance">
              <WalletOutlined />
            </div>
            <div class="summary-content">
              <div class="summary-value">{{ formatMoney(summary.balance) }}</div>
              <div class="summary-label">当前余额</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="summary-card">
            <div class="summary-icon income">
              <ArrowUpOutlined />
            </div>
            <div class="summary-content">
              <div class="summary-value income">+{{ formatMoney(summary.totalIncome) }}</div>
              <div class="summary-label">总收入</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="summary-card">
            <div class="summary-icon expense">
              <ArrowDownOutlined />
            </div>
            <div class="summary-content">
              <div class="summary-value expense">-{{ formatMoney(summary.totalExpense) }}</div>
              <div class="summary-label">总支出</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="summary-card">
            <div class="summary-icon count">
              <UnorderedListOutlined />
            </div>
            <div class="summary-content">
              <div class="summary-value">{{ summary.transactionCount }}</div>
              <div class="summary-label">交易笔数</div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <div class="chart-section">
      <a-card title="收支趋势" :bordered="false">
        <div class="chart-placeholder">
          <div class="mock-chart">
            <div class="chart-bars">
              <div v-for="(item, index) in chartData" :key="index" class="bar-group">
                <div class="bar income" :style="{ height: getBarHeight(item.income) }">
                  <span v-if="item.income > 0" class="bar-value">{{ formatMoney(item.income) }}</span>
                </div>
                <div class="bar expense" :style="{ height: getBarHeight(item.expense) }">
                  <span v-if="item.expense > 0" class="bar-value">{{ formatMoney(item.expense) }}</span>
                </div>
                <div class="bar-label">{{ item.month }}</div>
              </div>
            </div>
            <div class="chart-legend">
              <span class="legend-item"><span class="dot income"></span> 收入</span>
              <span class="legend-item"><span class="dot expense"></span> 支出</span>
            </div>
          </div>
        </div>
      </a-card>
    </div>

    <a-card title="流水明细" :bordered="false" class="flow-table">
      <a-table
        :columns="flowColumns"
        :data-source="flowList"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'flowType'">
            <a-tag :color="record.flowType === 'INCOME' ? 'green' : 'red'">
              {{ record.flowType === 'INCOME' ? '收入' : '支出' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'amount'">
            <span :class="record.flowType === 'INCOME' ? 'amount-income' : 'amount-expense'">
              {{ record.flowType === 'INCOME' ? '+' : '-' }}{{ formatMoney(record.amount) }}
            </span>
          </template>
          <template v-if="column.key === 'createTime'">
            {{ formatDateTime(record.createTime) }}
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  ExportOutlined,
  WalletOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  UnorderedListOutlined
} from '@ant-design/icons-vue'
import type { Dayjs } from 'dayjs'
import dayjs from 'dayjs'
import { clubApi } from '@/api/club'
import { fundApi } from '@/api/fund'
import { useUserStore } from '@/stores/user'
import type { FundFlow, Club } from '@/types'

const userStore = useUserStore()
const loading = ref(false)
const flowList = ref<FundFlow[]>([])
const currentClub = ref<Club | null>(null)
const allClubs = ref<Club[]>([])
const selectedClubId = ref<number | undefined>(undefined)
const dateRange = ref<[Dayjs, Dayjs] | null>(null)

const isAdmin = computed(() => userStore.user?.role?.roleCode === 'admin')

const summary = reactive({
  balance: 0,
  totalIncome: 0,
  totalExpense: 0,
  transactionCount: 0
})

const chartData = ref<{ month: string; income: number; expense: number }[]>([])

const maxChartValue = computed(() => {
  if (chartData.value.length === 0) return 1
  return Math.max(
    ...chartData.value.map(d => Math.max(d.income, d.expense)),
    1
  )
})

const getBarHeight = (value: number) => {
  const amount = Number(value || 0)
  if (amount <= 0) return '2px'
  const height = amount / maxChartValue.value * 150
  return `${Math.max(8, height)}px`
}

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`
})

const flowColumns = [
  { title: '类型', key: 'flowType', width: 80 },
  { title: '金额', key: 'amount', width: 120 },
  { title: '描述', dataIndex: 'description', key: 'description' },
  { title: '关联申请', dataIndex: 'applyId', key: 'applyId', width: 100 },
  { title: '时间', key: 'createTime', width: 160 }
]

const formatMoney = (amount: number) => {
  return '¥' + (amount || 0).toFixed(2)
}

const toAmount = (value: any) => Number(value || 0)

const resolveFlowDirection = (flow: FundFlow): 'income' | 'expense' | 'unknown' => {
  const before = toAmount((flow as any).balanceBefore)
  const after = toAmount((flow as any).balanceAfter)
  if (after > before) return 'income'
  if (after < before) return 'expense'

  const type = String(flow.flowType || '').toUpperCase()
  if (type.includes('INCOME') || type.includes('REFUND') || type === 'IN') return 'income'
  if (type.includes('EXPENSE') || type.includes('ADJUST') || type === 'OUT') return 'expense'

  const description = String(flow.description || '')
  if (description.includes('收入') || description.includes('入账')) return 'income'
  if (description.includes('支出') || description.includes('扣减')) return 'expense'
  return 'unknown'
}

const formatDateTime = (dateStr?: string) => {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

const loadClub = async () => {
  try {
    const res = await clubApi.getList(0, 100)
    allClubs.value = res.data?.content || []
    if (allClubs.value.length > 0) {
      selectedClubId.value = allClubs.value[0].id
      currentClub.value = allClubs.value[0]
      summary.balance = currentClub.value.balance || 0
    }
  } catch (error) {
    console.error('加载社团失败:', error)
  }
}

const handleClubChange = (clubId: number) => {
  const club = allClubs.value.find(c => c.id === clubId)
  if (club) {
    currentClub.value = club
    summary.balance = club.balance || 0
    pagination.current = 1
    loadFlowData()
  }
}

const loadFlowData = async () => {
  if (!currentClub.value) return
  
  loading.value = true
  try {
    const baseParams: any = {
      clubId: currentClub.value.id,
      page: pagination.current - 1,
      size: pagination.pageSize
    }
    if (dateRange.value?.[0] && dateRange.value?.[1]) {
      baseParams.startTime = dateRange.value[0].toISOString()
      baseParams.endTime = dateRange.value[1].toISOString()
    }
    const res = await fundApi.getFlowList({
      ...baseParams
    })
    
    flowList.value = res.data?.list || res.data?.content || []
    pagination.total = res.data?.total || res.data?.totalElements || 0
    
    const summaryRes = await fundApi.getFlowList({
      clubId: currentClub.value.id,
      page: 0,
      size: 1000
    })
    const flows = summaryRes.data?.list || summaryRes.data?.content || []
    summary.totalIncome = flows
      .filter((f: FundFlow) => resolveFlowDirection(f) === 'income')
      .reduce((sum: number, f: FundFlow) => sum + toAmount(f.amount), 0)
    summary.totalExpense = flows
      .filter((f: FundFlow) => resolveFlowDirection(f) === 'expense')
      .reduce((sum: number, f: FundFlow) => sum + toAmount(f.amount), 0)
    summary.transactionCount = flows.length
    
    generateChartData(flows)
  } catch (error) {
    console.error('加载流水数据失败:', error)
  } finally {
    loading.value = false
  }
}

const generateChartData = (flows: FundFlow[]) => {
  const latestTime = flows
    .map(item => dayjs(item.createTime))
    .filter(item => item.isValid())
    .sort((a, b) => a.valueOf() - b.valueOf())
    .pop()
  const end = latestTime || dayjs()
  const buckets = Array.from({ length: 6 }, (_, index) => {
    const time = end.subtract(5 - index, 'month')
    return {
      key: time.format('YYYY-MM'),
      month: time.format('M月'),
      income: 0,
      expense: 0
    }
  })
  const bucketMap = new Map(buckets.map(item => [item.key, item]))

  flows.forEach((flow: FundFlow) => {
    const flowTime = dayjs(flow.createTime)
    if (!flowTime.isValid()) return
    const key = flowTime.format('YYYY-MM')
    const bucket = bucketMap.get(key)
    if (!bucket) return
    const amount = toAmount(flow.amount)
    const direction = resolveFlowDirection(flow)
    if (direction === 'income') {
      bucket.income += amount
    } else if (direction === 'expense') {
      bucket.expense += amount
    }
  })

  chartData.value = buckets.map(({ month, income, expense }) => ({ month, income, expense }))
}

const handleDateChange = () => {
  pagination.current = 1
  loadFlowData()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadFlowData()
}

const exportReport = () => {
  if (flowList.value.length === 0) {
    message.warning('暂无可导出的数据')
    return
  }
  const header = ['交易时间', '类型', '金额', '描述', '关联申请']
  const rows = flowList.value.map(item => [
    formatDateTime(item.createTime),
    item.flowType === 'INCOME' ? '收入' : '支出',
    (item.amount || 0).toFixed(2),
    item.description || '',
    item.applyNo || '-'
  ])

  const csv = [header, ...rows]
    .map(line => line.map(value => `"${String(value).replaceAll('"', '""')}"`).join(','))
    .join('\n')

  const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `财务流水报表_${dayjs().format('YYYYMMDD_HHmmss')}.csv`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
  message.success('报表导出成功')
}

onMounted(async () => {
  await loadClub()
  loadFlowData()
})
</script>

<style scoped>
.finance-report {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
}

.subtitle {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: #fff;
  padding: 16px 24px;
  border-radius: 8px;
}

.summary-cards {
  margin-bottom: 24px;
}

.summary-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.summary-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.summary-icon.balance {
  background: #f0f0f0;
  color: #1a1a1a;
}

.summary-icon.income {
  background: #f6ffed;
  color: #52c41a;
}

.summary-icon.expense {
  background: #fff1f0;
  color: #ff4d4f;
}

.summary-icon.count {
  background: #e6f7ff;
  color: #1890ff;
}

.summary-content {
  flex: 1;
}

.summary-value {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.2;
}

.summary-value.income {
  color: #52c41a;
}

.summary-value.expense {
  color: #ff4d4f;
}

.summary-label {
  color: #666;
  font-size: 14px;
  margin-top: 4px;
}

.chart-section {
  margin-bottom: 24px;
}

.chart-section :deep(.ant-card) {
  border-radius: 8px;
}

.chart-section :deep(.ant-card-head) {
  border-bottom: 1px solid #f0f0f0;
}

.chart-section :deep(.ant-card-head-title) {
  font-weight: 600;
  color: #1a1a1a;
}

.chart-placeholder {
  height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mock-chart {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chart-bars {
  flex: 1;
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  padding: 20px 0;
}

.bar-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.bar-group .bar {
  width: 24px;
  border-radius: 4px 4px 0 0;
  position: relative;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  min-height: 4px;
}

.bar.income {
  background: #52c41a;
}

.bar.expense {
  background: #ff4d4f;
}

.bar-value {
  position: absolute;
  top: -20px;
  font-size: 10px;
  color: #666;
  white-space: nowrap;
}

.bar-label {
  font-size: 12px;
  color: #666;
}

.chart-legend {
  display: flex;
  justify-content: center;
  gap: 24px;
  padding: 16px 0;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.legend-item .dot {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-item .dot.income {
  background: #52c41a;
}

.legend-item .dot.expense {
  background: #ff4d4f;
}

.flow-table {
  border-radius: 8px;
}

.flow-table :deep(.ant-card-head) {
  border-bottom: 1px solid #f0f0f0;
}

.flow-table :deep(.ant-card-head-title) {
  font-weight: 600;
  color: #1a1a1a;
}

.amount-income {
  color: #52c41a;
  font-weight: 600;
}

.amount-expense {
  color: #ff4d4f;
  font-weight: 600;
}

:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  color: #1a1a1a;
}
</style>
