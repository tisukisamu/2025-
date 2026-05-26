<template>
  <div class="page-container">
    <div class="hero card">
      <div>
        <h1 class="page-title">仪表盘</h1>
        <p class="page-subtitle">欢迎回来，{{ userStore.user?.realName }}，这是当前社团资金与审批概览</p>
      </div>
      <div class="hero-meta">
        <div class="hero-label">当前角色</div>
        <div class="hero-value">{{ userStore.user?.role?.roleName || '-' }}</div>
      </div>
    </div>

    <a-row :gutter="16" class="stat-row">
      <a-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card">
          <div class="metric-icon">
            <WalletOutlined />
          </div>
          <div class="metric-content">
            <div class="metric-label">总资金余额</div>
            <div class="metric-value">¥{{ stats.totalBalance.toFixed(2) }}</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card">
          <div class="metric-icon">
            <FileTextOutlined />
          </div>
          <div class="metric-content">
            <div class="metric-label">待审批申请</div>
            <div class="metric-value">{{ stats.pendingCount }}</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card">
          <div class="metric-icon">
            <TeamOutlined />
          </div>
          <div class="metric-content">
            <div class="metric-label">社团成员</div>
            <div class="metric-value">{{ stats.memberCount }}</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card">
          <div class="metric-icon">
            <CalendarOutlined />
          </div>
          <div class="metric-content">
            <div class="metric-label">进行中活动</div>
            <div class="metric-value">{{ stats.activityCount }}</div>
          </div>
        </div>
      </a-col>
    </a-row>

    <a-row :gutter="16">
      <a-col :xs="24" :lg="16">
        <div class="card panel">
          <div class="card-header">
            <h3>最近资金申请</h3>
            <a-button type="default" @click="router.push('/president/fund/review')">
              查看全部
            </a-button>
          </div>
          <a-table
            :dataSource="recentApplies"
            :columns="applyColumns"
            :pagination="false"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'amount'">
                <span class="amount">¥{{ record.amount.toFixed(2) }}</span>
              </template>
              <template v-if="column.key === 'status'">
                <a-tag :color="getStatusColor(record.status)">
                  {{ getStatusText(record.status) }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </div>
      </a-col>
      <a-col :xs="24" :lg="8">
        <div class="card panel">
          <div class="card-header">
            <h3>快捷操作</h3>
          </div>
          <div class="quick-actions">
            <div
              v-for="action in quickActions"
              :key="action.title"
              class="action-item"
              @click="router.push(action.path)"
            >
              <component :is="action.icon" class="action-icon" />
              <span>{{ action.title }}</span>
            </div>
          </div>
        </div>

        <div class="card panel">
          <div class="card-header">
            <h3>最新通知</h3>
          </div>
          <div class="notification-list">
            <div
              v-for="item in notifications"
              :key="item.id"
              class="notification-item"
            >
              <div class="notification-title">{{ item.title }}</div>
              <div class="notification-time">{{ formatTime(item.createTime) }}</div>
            </div>
            <a-empty v-if="notifications.length === 0" description="暂无通知" />
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores'
import { fundApi, notificationApi, clubApi, activityApi } from '@/api'
import type { FundApply, Notification } from '@/types'
import dayjs from 'dayjs'
import {
  WalletOutlined,
  FileTextOutlined,
  TeamOutlined,
  CalendarOutlined,
  PlusCircleOutlined,
  BarChartOutlined,
  BellOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const stats = ref({
  totalBalance: 0,
  pendingCount: 0,
  memberCount: 0,
  activityCount: 0
})

const recentApplies = ref<FundApply[]>([])
const notifications = ref<Notification[]>([])

const quickActions = computed(() => {
  const role = userStore.userRole
  if (role === 'president') {
    return [
      { title: '发起申请', path: '/president/fund/apply', icon: PlusCircleOutlined },
      { title: '财务报表', path: '/president/finance', icon: BarChartOutlined },
      { title: '活动管理', path: '/member/activity', icon: CalendarOutlined },
      { title: '通知中心', path: '/member/notification', icon: BellOutlined }
    ]
  }
  return [
    { title: '活动管理', path: '/member/activity', icon: CalendarOutlined },
    { title: '通知中心', path: '/member/notification', icon: BellOutlined },
    { title: '财务查看', path: '/member/fund', icon: WalletOutlined },
    { title: '个人资料', path: '/member/profile', icon: TeamOutlined }
  ]
})

const applyColumns = [
  { title: '申请编号', dataIndex: 'applyNo', key: 'applyNo' },
  { title: '申请类型', dataIndex: 'applyType', key: 'applyType' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '状态', dataIndex: 'status', key: 'status' }
]

const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    PENDING: 'default',
    PRESIDENT_APPROVED: 'processing',
    TEACHER_APPROVED: 'success',
    COMPLETED: 'success',
    REJECTED: 'error'
  }
  return colors[status] || 'default'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    PENDING: '待审批',
    PRESIDENT_APPROVED: '社长已审',
    TEACHER_APPROVED: '老师已审',
    COMPLETED: '已完成',
    REJECTED: '已驳回'
  }
  return texts[status] || status
}

const formatTime = (time: string) => {
  return dayjs(time).format('MM-DD HH:mm')
}

const fetchDashboardData = async () => {
  try {
    const res = await clubApi.getList(0, 100)
    const clubs = (res.data?.content || []).map((item: any) => ({
      ...item,
      balance: Number(item.balance) || 0,
      memberCount: Number(item.memberCount) || 0
    }))

    const notificationsRes = await notificationApi.getList({ page: 0, size: 5 })
    notifications.value = notificationsRes.data.list || notificationsRes.data.content || []

    if (clubs.length > 0) {
      const clubIds = clubs.map((c: any) => c.id)
      
      const applyRequests = clubIds.map((clubId: number) =>
        fundApi.getApplyList({ clubId, page: 0, size: 100 })
      )
      const applyResults = await Promise.allSettled(applyRequests)
      const allApplies = applyResults
        .filter((item): item is PromiseFulfilledResult<any> => item.status === 'fulfilled')
        .flatMap(item => item.value.data.list || item.value.data.content || [])
      
      recentApplies.value = allApplies
        .sort((a: FundApply, b: FundApply) => new Date(b.createTime || 0).getTime() - new Date(a.createTime || 0).getTime())
        .slice(0, 5)

      stats.value.totalBalance = clubs.reduce(
        (sum: number, club: any) => sum + (club.balance || 0),
        0
      )
      stats.value.memberCount = clubs.reduce(
        (sum: number, club: any) => sum + (club.memberCount || 0),
        0
      )
      
      stats.value.pendingCount = allApplies.filter(
        (item: FundApply) => item.status === 'PENDING' || item.status === 'PRESIDENT_APPROVED'
      ).length

      const activityRequests = clubIds.map((clubId: number) =>
        activityApi.getList({ clubId, page: 0, size: 100 })
      )
      const activityResults = await Promise.allSettled(activityRequests)
      const allActivities = activityResults
        .filter((item): item is PromiseFulfilledResult<any> => item.status === 'fulfilled')
        .flatMap(item => item.value.data.list || item.value.data.content || [])
      
      const now = new Date()
      stats.value.activityCount = allActivities.filter((activity: any) => {
        const startTime = new Date(activity.startTime)
        const endTime = new Date(activity.endTime)
        return startTime <= now && endTime >= now
      }).length
    } else {
      recentApplies.value = []
      stats.value.pendingCount = 0
      stats.value.activityCount = 0
    }
  } catch (error) {
    console.error('获取仪表盘数据失败', error)
  }
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped>
.hero {
  margin-bottom: 16px;
  background: linear-gradient(135deg, #ffffff 0%, #f4f4f4 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.hero-meta {
  text-align: right;
}

.hero-label {
  color: #737373;
  font-size: 12px;
}

.hero-value {
  color: #111111;
  font-size: 18px;
  font-weight: 700;
  margin-top: 4px;
}

.stat-row {
  margin-bottom: 16px;
}

.metric-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  border: 1px solid #dadada;
}

.metric-icon {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  background: #efefef;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #111111;
}

.metric-content {
  flex: 1;
}

.metric-label {
  font-size: 13px;
  color: #666666;
}

.metric-value {
  margin-top: 6px;
  font-size: 26px;
  font-weight: 700;
  color: #111111;
}

.panel {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.card-header h3 {
  font-size: 16px;
  font-weight: 700;
  color: #111111;
}

.amount {
  font-weight: 700;
  color: #111111;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: flex-start;
  padding: 14px 12px;
  background: #f7f7f7;
  border-radius: 10px;
  border: 1px solid #e2e2e2;
  cursor: pointer;
  transition: all 0.2s;
}

.action-item:hover {
  background: #efefef;
}

.action-icon {
  font-size: 18px;
  color: #111111;
}

.action-item span {
  font-size: 14px;
  color: #2b2b2b;
  font-weight: 600;
}

.notification-list {
  max-height: 320px;
  overflow-y: auto;
}

.notification-item {
  padding: 10px 4px;
  border-bottom: 1px solid #ececec;
  cursor: pointer;
  border-radius: 6px;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item:hover {
  background: #f7f7f7;
}

.notification-title {
  font-size: 14px;
  color: #1f1f1f;
  margin-bottom: 4px;
  font-weight: 600;
}

.notification-time {
  font-size: 12px;
  color: #8a8a8a;
}
</style>
