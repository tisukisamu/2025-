<template>
  <div class="admin-dashboard">
    <div class="page-header">
      <div>
        <h1 class="page-title">管理后台</h1>
        <p class="page-subtitle">系统数据概览</p>
      </div>
      <a-button @click="$router.push('/')" ghost class="header-btn">返回首页</a-button>
    </div>

    <a-row :gutter="[24, 24]" class="stats-row">
      <a-col :xs="24" :sm="12" :lg="6" class="stretch-col">
        <div class="stat-card">
          <div class="stat-icon" style="background: #f0f0f0">
            <user-outlined />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.userCount }}</div>
            <div class="stat-label">注册用户</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6" class="stretch-col">
        <div class="stat-card">
          <div class="stat-icon" style="background: #f5f5f5">
            <bug-outlined />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.petCount }}</div>
            <div class="stat-label">宠物档案</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6" class="stretch-col">
        <div class="stat-card">
          <div class="stat-icon" style="background: #f5f5f5">
            <calendar-outlined />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.appointmentCount }}</div>
            <div class="stat-label">预约订单</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6" class="stretch-col">
        <div class="stat-card">
          <div class="stat-icon" style="background: #f5f5f5">
            <dollar-outlined />
          </div>
          <div class="stat-content">
            <div class="stat-value">¥{{ stats.totalRevenue }}</div>
            <div class="stat-label">总收入</div>
          </div>
        </div>
      </a-col>
    </a-row>

    <a-row :gutter="[24, 24]">
      <a-col :xs="24" :lg="16" class="stretch-col">
        <a-card title="最近预约" class="recent-card">
          <template #extra>
            <a-button type="link" @click="$router.push('/admin/appointments')">
              查看全部
            </a-button>
          </template>
          <a-table
            :columns="columns"
            :data-source="recentAppointments"
            :pagination="false"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="getStatusColor(record.status)">
                  {{ getStatusText(record.status) }}
                </a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-button type="link" size="small" @click="viewAppointment(record)">
                  查看
                </a-button>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="8" class="stretch-col">
        <a-card title="快捷操作" class="quick-card">
          <div class="quick-actions">
            <div class="action-item" @click="$router.push('/admin/users')">
              <user-outlined class="action-icon" />
              <span>用户管理</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/appointments')">
              <calendar-outlined class="action-icon" />
              <span>预约管理</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/services')">
              <appstore-outlined class="action-icon" />
              <span>服务管理</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/pets')">
              <bug-outlined class="action-icon" />
              <span>宠物管理</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/process')">
              <picture-outlined class="action-icon" />
              <span>流程管理</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/statistics')">
              <dollar-outlined class="action-icon" />
              <span>统计分析</span>
            </div>
          </div>
        </a-card>

        <a-card title="服务状态" class="status-card">
          <div class="status-list">
            <div class="status-item">
              <span class="status-label">待确认预约</span>
              <span class="status-value">{{ statusCounts.pending }}</span>
            </div>
            <div class="status-item">
              <span class="status-label">进行中服务</span>
              <span class="status-value">{{ statusCounts.processing }}</span>
            </div>
            <div class="status-item">
              <span class="status-label">今日完成</span>
              <span class="status-value">{{ statusCounts.completed }}</span>
            </div>
            <div class="status-item">
              <span class="status-label">已取消</span>
              <span class="status-value">{{ statusCounts.cancelled }}</span>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  UserOutlined,
  BugOutlined,
  CalendarOutlined,
  DollarOutlined,
  AppstoreOutlined,
  PictureOutlined
} from '@ant-design/icons-vue'
import { getAppointmentList } from '../../api/appointment'

const router = useRouter()

const stats = reactive({
  userCount: 128,
  petCount: 256,
  appointmentCount: 89,
  totalRevenue: '45,680'
})

const statusCounts = reactive({
  pending: 12,
  processing: 8,
  completed: 65,
  cancelled: 4
})

const columns = [
  { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 140 },
  { title: '宠物', dataIndex: 'petName', key: 'petName' },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '操作', key: 'action', width: 80 }
]

const recentAppointments = ref([])

const statusColors = {
  pending: 'orange',
  confirmed: 'blue',
  processing: 'processing',
  completed: 'success',
  cancelled: 'default'
}

const statusTexts = {
  pending: '待确认',
  confirmed: '已确认',
  processing: '进行中',
  completed: '已完成',
  cancelled: '已取消'
}

const getStatusColor = (status) => statusColors[status] || 'default'
const getStatusText = (status) => statusTexts[status] || status

const viewAppointment = (record) => {
  router.push(`/appointments/${record.id}/process`)
}

const loadRecentAppointments = async () => {
  try {
    const res = await getAppointmentList({ pageNum: 1, pageSize: 5 })
    if (res.code === 200) {
      const payload = res.data
      if (Array.isArray(payload)) {
        recentAppointments.value = payload
      } else if (Array.isArray(payload?.records)) {
        recentAppointments.value = payload.records
      } else if (Array.isArray(payload?.list)) {
        recentAppointments.value = payload.list
      } else {
        recentAppointments.value = []
      }
    }
  } catch (error) {
    console.error('加载预约列表失败:', error)
  }
}

onMounted(() => {
  loadRecentAppointments()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 8px 0;
}

.page-header {
  margin-bottom: 24px;
  background: linear-gradient(135deg, #1f1f1f 0%, #434343 100%);
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-btn {
  border-color: rgba(255, 255, 255, 0.45);
}

.stretch-col {
  display: flex;
}

.stats-row {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
}

.page-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin: 4px 0 0;
}

.stat-card {
  background: #fafafa;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
  min-height: 116px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #262626;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #262626;
}

.stat-label {
  font-size: 14px;
  color: #8c8c8c;
}

.recent-card,
.quick-card,
.status-card {
  border-radius: 12px;
  width: 100%;
  border: 1px solid #f0f0f0;
}

.quick-card {
  margin-bottom: 16px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  background: #f0f0f0;
}

.action-icon {
  font-size: 24px;
  color: #262626;
  margin-bottom: 8px;
}

.action-item span {
  font-size: 13px;
  color: #595959;
}

.status-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
}

.status-label {
  font-size: 14px;
  color: #595959;
}

.status-value {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
}
</style>
