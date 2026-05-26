<template>
  <div class="module-page">
    <div class="module-header">
      <div>
        <h1 class="module-title">统计分析</h1>
        <p class="module-subtitle">关键业务指标与订单结构总览</p>
      </div>
      <a-space>
        <a-button @click="goBack">返回后台</a-button>
        <a-button @click="loadData" :loading="loading">刷新数据</a-button>
      </a-space>
    </div>

    <div class="metric-grid">
      <div v-for="item in metrics" :key="item.label" class="metric-col">
        <a-card :bordered="false" class="metric-card">
          <div class="metric-value">{{ item.value }}</div>
          <div class="metric-label">{{ item.label }}</div>
        </a-card>
      </div>
    </div>

    <a-row :gutter="[16, 16]" class="table-row">
      <a-col :xs="24" :lg="12" class="table-col">
        <a-card :bordered="false" title="预约状态分布" class="table-card">
          <a-table
            :columns="statusColumns"
            :data-source="statusRows"
            :pagination="false"
            size="small"
            row-key="status"
            table-layout="fixed"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'label'">
                <span class="status-cell">{{ record.label }}</span>
              </template>
              <template v-else-if="column.key === 'rate'">
                <span class="rate-cell">{{ record.rate }}</span>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12" class="table-col">
        <a-card :bordered="false" title="服务套餐分布" class="table-card">
          <a-table
            :columns="packageColumns"
            :data-source="packageRows"
            :pagination="false"
            size="small"
            row-key="name"
            table-layout="fixed"
          />
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getAppointmentList } from '../../api/appointment'
import { getPetList } from '../../api/pet'
import { getServiceList } from '../../api/service'

const router = useRouter()
const loading = ref(false)
const appointments = ref([])
const pets = ref([])
const services = ref([])

const statusText = {
  pending: '待确认',
  confirmed: '已确认',
  processing: '进行中',
  completed: '已完成',
  cancelled: '已取消'
}

const statusColumns = [
  { title: '状态', dataIndex: 'label', key: 'label', width: 140 },
  { title: '数量', dataIndex: 'count', key: 'count', width: 100, align: 'center' },
  { title: '占比', dataIndex: 'rate', key: 'rate', width: 120, align: 'right' }
]

const packageColumns = [
  { title: '套餐', dataIndex: 'name', key: 'name' },
  { title: '预约数', dataIndex: 'count', key: 'count', width: 100, align: 'right' }
]

const metrics = computed(() => {
  const doneCount = appointments.value.filter((item) => item.status === 'completed').length
  const totalRevenue = appointments.value.reduce((sum, item) => {
    return sum + Number(item.packagePrice || 0)
  }, 0)
  return [
    { label: '用户预约总数', value: appointments.value.length },
    { label: '宠物档案总数', value: pets.value.length },
    { label: '在售服务数', value: services.value.length },
    { label: '已完成订单', value: doneCount },
    { label: '累计订单金额', value: `¥${totalRevenue.toLocaleString()}` }
  ]
})

const statusRows = computed(() => {
  const total = appointments.value.length
  const counter = appointments.value.reduce((result, item) => {
    result[item.status] = (result[item.status] || 0) + 1
    return result
  }, {})
  return Object.keys(statusText).map((status) => {
    const count = counter[status] || 0
    return {
      status,
      label: statusText[status],
      count,
      rate: total > 0 ? `${((count / total) * 100).toFixed(1)}%` : '--'
    }
  })
})

const packageRows = computed(() => {
  const counter = appointments.value.reduce((result, item) => {
    const key = item.packageName || '未命名套餐'
    result[key] = (result[key] || 0) + 1
    return result
  }, {})
  return Object.entries(counter)
    .map(([name, count]) => ({ name, count }))
    .sort((a, b) => b.count - a.count)
})

const toArray = (payload) => {
  if (Array.isArray(payload)) {
    return payload
  }
  if (Array.isArray(payload?.records)) {
    return payload.records
  }
  if (Array.isArray(payload?.list)) {
    return payload.list
  }
  return []
}

const goBack = () => {
  router.push('/admin')
}

const loadData = async () => {
  loading.value = true
  try {
    const [appointmentRes, petRes, serviceRes] = await Promise.all([
      getAppointmentList({ pageNum: 1, pageSize: 200 }),
      getPetList(),
      getServiceList()
    ])
    appointments.value = appointmentRes.code === 200 ? toArray(appointmentRes.data) : []
    pets.value = petRes.code === 200 ? toArray(petRes.data) : []
    services.value = serviceRes.code === 200 ? toArray(serviceRes.data) : []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.module-page {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
}

.module-header {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.module-title {
  margin: 0;
  font-size: 24px;
  color: #1f1f1f;
}

.module-subtitle {
  margin: 6px 0 0;
  color: #8c8c8c;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
}

.metric-card {
  background: #fafafa;
  height: 100%;
  border: 1px solid #f0f0f0;
  border-radius: 10px;
}

.metric-value {
  font-size: 24px;
  font-weight: 600;
  color: #1f1f1f;
}

.metric-label {
  margin-top: 8px;
  color: #8c8c8c;
}

.table-row {
  margin-top: 16px;
}

.table-col {
  display: flex;
  min-width: 0;
}

.table-card {
  flex: 1;
  min-width: 0;
  width: 100%;
  height: 100%;
  border: 1px solid #f0f0f0;
}

.table-card :deep(.ant-card-body) {
  min-width: 0;
}

.table-card :deep(.ant-table-wrapper) {
  width: 100%;
}

.status-cell,
.rate-cell {
  white-space: nowrap;
}

.rate-cell {
  font-variant-numeric: tabular-nums;
}

.module-page :deep(.ant-card),
.module-page :deep(.ant-card:hover) {
  box-shadow: none;
}

@media (max-width: 768px) {
  .module-header {
    flex-direction: column;
  }
}
</style>
