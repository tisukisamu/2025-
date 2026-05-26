<template>
  <div class="data-monitor">
    <div class="page-header">
      <h2>数据监控</h2>
      <p class="subtitle">实时监控系统运行状态与数据统计</p>
    </div>

    <div class="stats-row">
      <a-row :gutter="16">
        <a-col :span="6">
          <div class="stat-card">
            <div class="stat-icon users">
              <TeamOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">注册用户</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card">
            <div class="stat-icon clubs">
              <HomeOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.totalClubs }}</div>
              <div class="stat-label">社团总数</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card">
            <div class="stat-icon funds">
              <AccountBookOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatMoney(stats.totalFunds) }}</div>
              <div class="stat-label">资金总额</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card">
            <div class="stat-icon applies">
              <FileTextOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.totalApplies }}</div>
              <div class="stat-label">申请总数</div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <a-card title="资金流动趋势" :bordered="false" class="chart-card">
      <template #extra>
        <a-radio-group v-model:value="chartPeriod" @change="loadChartData">
          <a-radio-button value="week">近一周</a-radio-button>
          <a-radio-button value="month">近一月</a-radio-button>
          <a-radio-button value="year">近一年</a-radio-button>
        </a-radio-group>
      </template>
      <div class="chart-container">
        <div class="mock-chart">
          <div class="chart-bars">
            <div v-for="(item, index) in chartData" :key="index" class="bar-group">
              <div class="bar income" :style="{ height: getBarHeight(item.income) }"></div>
              <div class="bar expense" :style="{ height: getBarHeight(item.expense) }"></div>
              <div class="bar-label">{{ item.label }}</div>
            </div>
          </div>
          <div class="chart-legend">
            <span class="legend-item"><span class="dot income"></span> 收入</span>
            <span class="legend-item"><span class="dot expense"></span> 支出</span>
          </div>
        </div>
      </div>
    </a-card>

    <a-row :gutter="24" style="margin-top: 24px">
      <a-col :span="12">
        <a-card title="社团资金排行" :bordered="false" class="rank-card">
          <div class="rank-list">
            <div v-for="(club, index) in clubRankList" :key="club.id" class="rank-item">
              <div class="rank-num" :class="{ top: index < 3 }">{{ index + 1 }}</div>
              <div class="rank-info">
                <div class="rank-name">{{ club.clubName }}</div>
                <div class="rank-members">{{ club.memberCount || 0 }} 成员</div>
              </div>
              <div class="rank-value">{{ formatMoney(club.balance || 0) }}</div>
            </div>
          </div>
        </a-card>
      </a-col>

      <a-col :span="12">
        <a-card title="系统资源" :bordered="false" class="resource-card">
          <div class="resource-item">
            <div class="resource-header">
              <span>CPU 使用率</span>
              <span class="resource-value">{{ resources.cpu }}%</span>
            </div>
            <a-progress :percent="resources.cpu" :show-info="false" stroke-color="#1a1a1a" />
          </div>
          <div class="resource-item">
            <div class="resource-header">
              <span>内存使用率</span>
              <span class="resource-value">{{ resources.memory }}%</span>
            </div>
            <a-progress :percent="resources.memory" :show-info="false" stroke-color="#52c41a" />
          </div>
          <div class="resource-item">
            <div class="resource-header">
              <span>磁盘使用率</span>
              <span class="resource-value">{{ resources.disk }}%</span>
            </div>
            <a-progress :percent="resources.disk" :show-info="false" stroke-color="#1890ff" />
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-card title="活跃度统计" :bordered="false" class="activity-card" style="margin-top: 24px">
      <div class="activity-stats">
        <div class="activity-item">
          <div class="activity-label">今日登录</div>
          <div class="activity-value">{{ activity.todayLogin }}</div>
        </div>
        <div class="activity-item">
          <div class="activity-label">今日申请</div>
          <div class="activity-value">{{ activity.todayApply }}</div>
        </div>
        <div class="activity-item">
          <div class="activity-label">今日审批</div>
          <div class="activity-value">{{ activity.todayApproval }}</div>
        </div>
        <div class="activity-item">
          <div class="activity-label">在线用户</div>
          <div class="activity-value online">{{ activity.onlineUsers }}</div>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import {
  TeamOutlined,
  HomeOutlined,
  AccountBookOutlined,
  FileTextOutlined
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import { clubApi } from '@/api/club'
import { fundApi } from '@/api/fund'
import { userApi } from '@/api/user'

interface ChartDataItem {
  label: string
  income: number
  expense: number
}

const chartPeriod = ref('month')

const stats = reactive({
  totalUsers: 0,
  totalClubs: 0,
  totalFunds: 0,
  totalApplies: 0
})

const resources = reactive({
  cpu: 35,
  memory: 62,
  disk: 48
})

const activity = reactive({
  todayLogin: 0,
  todayApply: 0,
  todayApproval: 0,
  onlineUsers: 0
})

const chartData = ref<ChartDataItem[]>([])
const clubRankList = ref<{ id: number; clubName: string; memberCount?: number; balance?: number }[]>([])

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
  const height = amount / maxChartValue.value * 180
  return `${Math.max(6, height)}px`
}

const formatMoney = (amount: number) => {
  return '¥' + (amount || 0).toFixed(2)
}

const formatDateTime = (dateStr?: string) => {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('MM-DD HH:mm')
}

const toAmount = (value: any) => Number(value || 0)

const resolveFlowDirection = (flow: any): 'income' | 'expense' | 'unknown' => {
  const before = toAmount(flow.balanceBefore)
  const after = toAmount(flow.balanceAfter)
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

const buildBuckets = (endBase?: dayjs.Dayjs) => {
  const now = endBase || dayjs()
  if (chartPeriod.value === 'week') {
    return Array.from({ length: 7 }, (_, index) => {
      const d = now.subtract(6 - index, 'day')
      return {
        start: d.startOf('day'),
        end: d.endOf('day'),
        label: d.format('dd'),
        income: 0,
        expense: 0
      }
    })
  }
  if (chartPeriod.value === 'month') {
    const monthStart = now.subtract(29, 'day').startOf('day')
    return Array.from({ length: 6 }, (_, index) => {
      const start = monthStart.add(index * 5, 'day').startOf('day')
      const end = index === 5 ? now.endOf('day') : monthStart.add((index + 1) * 5 - 1, 'day').endOf('day')
      return {
        start,
        end,
        label: start.format('MM/DD'),
        income: 0,
        expense: 0
      }
    })
  }
  return Array.from({ length: 12 }, (_, index) => {
    const d = now.subtract(11 - index, 'month')
    return {
      start: d.startOf('month'),
      end: d.endOf('month'),
      label: d.format('M月'),
      income: 0,
      expense: 0
    }
  })
}

const loadChartData = async () => {
  let buckets = buildBuckets()
  chartData.value = buckets.map(item => ({ label: item.label, income: item.income, expense: item.expense }))
  try {
    const clubsRes = await clubApi.getList(0, 100)
    const clubs = (clubsRes.data?.list || clubsRes.data?.content || []).slice(0, 20)
    if (clubs.length === 0) return

    const flowResult = await Promise.allSettled(
      clubs.map((club: any) =>
        fundApi.getFlowList({
          clubId: club.id,
          page: 0,
          size: 1000
        })
      )
    )

    const flowList = flowResult
      .filter((item): item is PromiseFulfilledResult<any> => item.status === 'fulfilled')
      .flatMap(item => item.value.data?.list || item.value.data?.content || [])

    const latestFlowTime = flowList
      .map((flow: any) => dayjs(flow.createTime))
      .filter((time: dayjs.Dayjs) => time.isValid())
      .sort((a: dayjs.Dayjs, b: dayjs.Dayjs) => a.valueOf() - b.valueOf())
      .pop()
    buckets = buildBuckets(latestFlowTime)

    flowList.forEach((flow: any) => {
      const time = dayjs(flow.createTime)
      if (!time.isValid()) return
      const bucket = buckets.find(item => (time.isAfter(item.start) || time.isSame(item.start)) && (time.isBefore(item.end) || time.isSame(item.end)))
      if (!bucket) return
      const amount = toAmount(flow.amount)
      const direction = resolveFlowDirection(flow)
      if (direction === 'income') {
        bucket.income += amount
      } else if (direction === 'expense') {
        bucket.expense += amount
      }
    })
    chartData.value = buckets.map(item => ({ label: item.label, income: item.income, expense: item.expense }))
  } catch (error) {
    chartData.value = buckets.map(item => ({ label: item.label, income: item.income, expense: item.expense }))
  }
}

const loadClubRank = async () => {
  try {
    const res = await clubApi.getList(0, 10)
    clubRankList.value = (res.data?.list || res.data?.content || [])
      .map((item: any) => ({
        ...item,
        balance: Number(item.balance) || 0,
        memberCount: Number(item.memberCount) || 0
      }))
      .sort((a: any, b: any) => (b.balance || 0) - (a.balance || 0))
      .slice(0, 5)
  } catch (error) {
    clubRankList.value = []
  }
}

const loadStats = async () => {
  try {
    const usersRes = await userApi.getList(0, 1)
    stats.totalUsers = usersRes.data?.total || usersRes.data?.totalElements || 0

    const clubsRes = await clubApi.getList(0, 100)
    const clubs = (clubsRes.data?.list || clubsRes.data?.content || []).map((item: any) => ({
      ...item,
      balance: Number(item.balance) || 0
    }))
    stats.totalClubs = clubs.length
    stats.totalFunds = clubs.reduce((sum: number, club: any) => sum + (club.balance || 0), 0)

    const appliesRes = await fundApi.getApplyList({ page: 0, size: 1 })
    stats.totalApplies = appliesRes.data?.total || appliesRes.data?.totalElements || 0
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

let resourceTimer: number | null = null

const updateResources = () => {
  resources.cpu = Math.floor(Math.random() * 30) + 20
  resources.memory = Math.floor(Math.random() * 20) + 50
  resources.disk = Math.floor(Math.random() * 10) + 40
}

onMounted(() => {
  loadStats()
  loadChartData()
  loadClubRank()
  
  resourceTimer = window.setInterval(updateResources, 5000)
})

onUnmounted(() => {
  if (resourceTimer) {
    clearInterval(resourceTimer)
  }
})
</script>

<style scoped>
.data-monitor {
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

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.users {
  background: #e6f7ff;
  color: #1890ff;
}

.stat-icon.clubs {
  background: #f6ffed;
  color: #52c41a;
}

.stat-icon.funds {
  background: #fff7e6;
  color: #fa8c16;
}

.stat-icon.applies {
  background: #f9f0ff;
  color: #722ed1;
}

.stat-content .stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
}

.stat-content .stat-label {
  color: #666;
  font-size: 14px;
  margin-top: 4px;
}

.chart-card,
.table-card,
.rank-card,
.resource-card,
.activity-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.chart-card :deep(.ant-card-head-title),
.table-card :deep(.ant-card-head-title),
.rank-card :deep(.ant-card-head-title),
.resource-card :deep(.ant-card-head-title),
.activity-card :deep(.ant-card-head-title) {
  font-weight: 600;
  color: #1a1a1a;
}

.chart-container {
  height: 250px;
}

.mock-chart {
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
  width: 20px;
  border-radius: 4px 4px 0 0;
}

.bar.income {
  background: #52c41a;
}

.bar.expense {
  background: #ff4d4f;
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

.amount {
  font-weight: 600;
  color: #1a1a1a;
}

.rank-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.rank-item:last-child {
  border-bottom: none;
}

.rank-num {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  background: #f0f0f0;
  color: #666;
}

.rank-num.top {
  background: #1a1a1a;
  color: #fff;
}

.rank-info {
  flex: 1;
}

.rank-name {
  font-weight: 500;
  color: #1a1a1a;
}

.rank-members {
  font-size: 12px;
  color: #999;
}

.rank-value {
  font-weight: 600;
  color: #1a1a1a;
}

.resource-item {
  margin-bottom: 16px;
}

.resource-item:last-child {
  margin-bottom: 0;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
}

.resource-value {
  font-weight: 600;
  color: #1a1a1a;
}

.activity-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.activity-item {
  text-align: center;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
}

.activity-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.activity-value {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
}

.activity-value.online {
  color: #52c41a;
}

:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  color: #1a1a1a;
}
</style>
