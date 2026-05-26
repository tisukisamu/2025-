<template>
  <div class="risk-warning">
    <div class="page-header">
      <h2>风险预警</h2>
      <p class="subtitle">监控社团财务异常与风险提示</p>
    </div>

    <div class="warning-stats">
      <a-row :gutter="16">
        <a-col :span="6">
          <div class="stat-card danger">
            <div class="stat-icon">
              <AlertOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ highRiskCount }}</div>
              <div class="stat-label">高风险</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card warning">
            <div class="stat-icon">
              <ExclamationCircleOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ mediumRiskCount }}</div>
              <div class="stat-label">中风险</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card info">
            <div class="stat-icon">
              <InfoCircleOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ lowRiskCount }}</div>
              <div class="stat-label">低风险</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card normal">
            <div class="stat-icon">
              <CheckCircleOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ normalCount }}</div>
              <div class="stat-label">正常社团</div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <a-card title="预警列表" :bordered="false" class="warning-list-card">
      <template #extra>
        <a-space>
          <a-select v-model:value="filterLevel" style="width: 120px" placeholder="风险等级" allowClear>
            <a-select-option value="HIGH">高风险</a-select-option>
            <a-select-option value="MEDIUM">中风险</a-select-option>
            <a-select-option value="LOW">低风险</a-select-option>
          </a-select>
          <a-button @click="refreshData">
            <ReloadOutlined /> 刷新
          </a-button>
        </a-space>
      </template>

      <a-table
        :columns="columns"
        :data-source="warningList"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'clubName'">
            <div class="club-cell">
              <a-avatar :size="32" :src="record.clubLogo" :style="{ backgroundColor: '#1a1a1a' }">
                {{ record.clubName?.charAt(0) || '社' }}
              </a-avatar>
              <span>{{ record.clubName }}</span>
            </div>
          </template>
          <template v-if="column.key === 'level'">
            <a-tag :color="getLevelColor(record.level)">
              {{ levelMap[record.level] || record.level }}
            </a-tag>
          </template>
          <template v-if="column.key === 'type'">
            <span class="warning-type">
              <WarningOutlined :style="{ color: getLevelColor(record.level) }" />
              {{ typeMap[record.type] || record.type }}
            </span>
          </template>
          <template v-if="column.key === 'createTime'">
            {{ formatDateTime(record.createTime) }}
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewDetail(record)">
                查看详情
              </a-button>
              <a-button type="link" size="small" @click="handleIgnore(record)">
                忽略
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-card title="风险规则说明" :bordered="false" class="rules-card">
      <div class="rules-list">
        <div class="rule-item">
          <div class="rule-icon high"><AlertOutlined /></div>
          <div class="rule-content">
            <div class="rule-title">高风险规则</div>
            <ul>
              <li>账户余额低于 ¥100</li>
              <li>单笔支出超过 ¥5000</li>
              <li>连续3个月无收入记录</li>
            </ul>
          </div>
        </div>
        <div class="rule-item">
          <div class="rule-icon medium"><ExclamationCircleOutlined /></div>
          <div class="rule-content">
            <div class="rule-title">中风险规则</div>
            <ul>
              <li>账户余额低于 ¥500</li>
              <li>单笔支出超过 ¥2000</li>
              <li>月支出超过月收入的200%</li>
            </ul>
          </div>
        </div>
        <div class="rule-item">
          <div class="rule-icon low"><InfoCircleOutlined /></div>
          <div class="rule-content">
            <div class="rule-title">低风险规则</div>
            <ul>
              <li>账户余额低于 ¥1000</li>
              <li>申请审批超过7天未处理</li>
              <li>社团成员少于5人</li>
            </ul>
          </div>
        </div>
      </div>
    </a-card>

    <a-modal
      v-model:open="detailVisible"
      title="预警详情"
      :footer="null"
      width="640px"
    >
      <template v-if="currentWarning">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="社团名称">{{ currentWarning.clubName }}</a-descriptions-item>
          <a-descriptions-item label="风险等级">
            <a-tag :color="getLevelColor(currentWarning.level)">{{ levelMap[currentWarning.level] || currentWarning.level }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="预警类型">{{ typeMap[currentWarning.type] || currentWarning.type }}</a-descriptions-item>
          <a-descriptions-item label="触发时间">{{ formatDateTime(currentWarning.createTime) }}</a-descriptions-item>
          <a-descriptions-item label="详细描述" :span="2">{{ currentWarning.description }}</a-descriptions-item>
        </a-descriptions>
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  AlertOutlined,
  ExclamationCircleOutlined,
  InfoCircleOutlined,
  CheckCircleOutlined,
  WarningOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import { clubApi } from '@/api/club'
import { normalizeMediaUrl } from '@/utils/media'

interface WarningItem {
  id: number
  clubId: number
  clubName: string
  clubLogo?: string
  level: string
  type: string
  description: string
  createTime: string
}

const loading = ref(false)
const filterLevel = ref<string>()
const warningList = ref<WarningItem[]>([])
const detailVisible = ref(false)
const currentWarning = ref<WarningItem | null>(null)

const highRiskCount = computed(() => warningList.value.filter(w => w.level === 'HIGH').length)
const mediumRiskCount = computed(() => warningList.value.filter(w => w.level === 'MEDIUM').length)
const lowRiskCount = computed(() => warningList.value.filter(w => w.level === 'LOW').length)
const normalCount = ref(0)

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`
})

const columns = [
  { title: '社团', key: 'clubName', width: 180 },
  { title: '风险等级', key: 'level', width: 100 },
  { title: '预警类型', key: 'type', width: 150 },
  { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
  { title: '时间', key: 'createTime', width: 160 },
  { title: '操作', key: 'action', width: 140 }
]

const levelMap: Record<string, string> = {
  HIGH: '高风险',
  MEDIUM: '中风险',
  LOW: '低风险'
}

const typeMap: Record<string, string> = {
  LOW_BALANCE: '余额不足',
  LARGE_EXPENSE: '大额支出',
  NO_INCOME: '无收入记录',
  OVERSPENDING: '超支预警',
  PENDING_TIMEOUT: '审批超时',
  LOW_MEMBERS: '成员不足'
}

const getLevelColor = (level: string) => {
  const colors: Record<string, string> = {
    HIGH: 'red',
    MEDIUM: 'orange',
    LOW: 'blue'
  }
  return colors[level] || 'default'
}

const formatDateTime = (dateStr?: string) => {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

const loadWarningData = async () => {
  loading.value = true
  try {
    const clubsRes = await clubApi.getList(0, 100)
    const clubs = clubsRes.data?.list || clubsRes.data?.content || []
    
    const warnings: WarningItem[] = []
    let warningId = 1
    
    clubs.forEach((club: any) => {
      const balance = Number(club.balance) || 0
      const memberCount = Number(club.memberCount) || 0
      const monthIncome = Number(club.monthIncome) || 0
      const monthExpense = Number(club.monthExpense) || 0
      const pendingApprovalCount = Number(club.pendingApprovalCount) || 0
      
      if (balance < 100) {
        warnings.push({
          id: warningId++,
          clubId: club.id,
          clubName: club.clubName,
          clubLogo: normalizeMediaUrl(club.logo),
          level: 'HIGH',
          type: 'LOW_BALANCE',
          description: `账户余额仅剩 ¥${balance.toFixed(2)}，低于安全阈值 ¥100`,
          createTime: new Date().toISOString()
        })
      } else if (balance < 500) {
        warnings.push({
          id: warningId++,
          clubId: club.id,
          clubName: club.clubName,
          clubLogo: normalizeMediaUrl(club.logo),
          level: 'MEDIUM',
          type: 'LOW_BALANCE',
          description: `账户余额 ¥${balance.toFixed(2)}，低于安全阈值 ¥500`,
          createTime: new Date().toISOString()
        })
      } else if (balance < 1000) {
        warnings.push({
          id: warningId++,
          clubId: club.id,
          clubName: club.clubName,
          clubLogo: normalizeMediaUrl(club.logo),
          level: 'LOW',
          type: 'LOW_BALANCE',
          description: `账户余额 ¥${balance.toFixed(2)}，低于安全阈值 ¥1000`,
          createTime: new Date().toISOString()
        })
      }
      
      if (monthIncome > 0 && monthExpense > monthIncome * 2) {
        warnings.push({
          id: warningId++,
          clubId: club.id,
          clubName: club.clubName,
          clubLogo: normalizeMediaUrl(club.logo),
          level: 'MEDIUM',
          type: 'OVERSPENDING',
          description: `本月支出 ¥${monthExpense.toFixed(2)}，超过本月收入 ¥${monthIncome.toFixed(2)} 的 200%`,
          createTime: new Date().toISOString()
        })
      }
      
      if (pendingApprovalCount > 0) {
        warnings.push({
          id: warningId++,
          clubId: club.id,
          clubName: club.clubName,
          clubLogo: normalizeMediaUrl(club.logo),
          level: 'LOW',
          type: 'PENDING_TIMEOUT',
          description: `有 ${pendingApprovalCount} 笔申请待审批`,
          createTime: new Date().toISOString()
        })
      }
      
      if (memberCount < 5 && memberCount > 0) {
        warnings.push({
          id: warningId++,
          clubId: club.id,
          clubName: club.clubName,
          clubLogo: normalizeMediaUrl(club.logo),
          level: 'LOW',
          type: 'LOW_MEMBERS',
          description: `社团成员仅剩 ${memberCount} 人，低于最低要求 5 人`,
          createTime: new Date().toISOString()
        })
      }
    })
    
    warningList.value = warnings
    pagination.total = warnings.length
    
    const warningClubIds = new Set(warnings.map(w => w.clubId))
    normalCount.value = clubs.filter((c: any) => !warningClubIds.has(c.id)).length
  } catch (error) {
    console.error('加载预警数据失败:', error)
    warningList.value = []
    normalCount.value = 0
  } finally {
    loading.value = false
  }
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
}

const refreshData = () => {
  loadWarningData()
  message.success('数据已刷新')
}

const viewDetail = (record: WarningItem) => {
  currentWarning.value = record
  detailVisible.value = true
}

const handleIgnore = (record: WarningItem) => {
  const index = warningList.value.findIndex(w => w.id === record.id)
  if (index > -1) {
    warningList.value.splice(index, 1)
    message.success('已忽略该预警')
  }
}

onMounted(() => {
  loadWarningData()
})
</script>

<style scoped>
.risk-warning {
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

.warning-stats {
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

.stat-card.danger .stat-icon {
  background: #fff1f0;
  color: #ff4d4f;
}

.stat-card.warning .stat-icon {
  background: #fff7e6;
  color: #fa8c16;
}

.stat-card.info .stat-icon {
  background: #e6f7ff;
  color: #1890ff;
}

.stat-card.normal .stat-icon {
  background: #f6ffed;
  color: #52c41a;
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

.warning-list-card {
  margin-bottom: 24px;
  border-radius: 8px;
}

.warning-list-card :deep(.ant-card-head-title) {
  font-weight: 600;
  color: #1a1a1a;
}

.club-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.warning-type {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rules-card {
  border-radius: 8px;
}

.rules-card :deep(.ant-card-head-title) {
  font-weight: 600;
  color: #1a1a1a;
}

.rules-list {
  display: flex;
  gap: 24px;
}

.rule-item {
  flex: 1;
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.rule-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.rule-icon.high {
  background: #fff1f0;
  color: #ff4d4f;
}

.rule-icon.medium {
  background: #fff7e6;
  color: #fa8c16;
}

.rule-icon.low {
  background: #e6f7ff;
  color: #1890ff;
}

.rule-title {
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.rule-content ul {
  margin: 0;
  padding-left: 16px;
  color: #666;
  font-size: 13px;
}

.rule-content li {
  margin-bottom: 4px;
}

:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  color: #1a1a1a;
}
</style>
