<template>
  <div class="module-page">
    <div class="module-header">
      <div>
        <h1 class="module-title">流程管理</h1>
        <p class="module-subtitle">统一查看预约流程并快速更新阶段状态</p>
      </div>
      <a-space>
        <a-button @click="goBack">返回后台</a-button>
        <a-select v-model:value="statusFilter" class="filter-select" allow-clear placeholder="按状态筛选">
          <a-select-option value="pending">待处理</a-select-option>
          <a-select-option value="processing">进行中</a-select-option>
          <a-select-option value="completed">已完成</a-select-option>
        </a-select>
      </a-space>
    </div>

    <a-table
      :columns="columns"
      :data-source="filteredAppointments"
      :loading="loading"
      row-key="id"
      :pagination="{ pageSize: 8, showSizeChanger: false }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="appointmentStatusColor[record.status] || 'default'">
            {{ appointmentStatusText[record.status] || record.status }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'latestProcess'">
          <a-tag :color="getProcessTagColor(record.processStatus)">
            {{ getProcessTagText(record.processStatus) }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button type="link" @click="openDrawer(record)">管理流程</a-button>
        </template>
      </template>
    </a-table>

    <a-drawer v-model:open="drawerOpen" title="流程阶段管理" width="560">
      <template v-if="currentAppointment">
        <a-descriptions :column="1" bordered size="small" class="desc-card">
          <a-descriptions-item label="订单号">{{ currentAppointment.orderNo }}</a-descriptions-item>
          <a-descriptions-item label="宠物">{{ currentAppointment.petName || '-' }}</a-descriptions-item>
        </a-descriptions>

        <a-timeline class="timeline">
          <a-timeline-item
            v-for="item in processList"
            :key="item.id"
            :color="getProcessTagColor(item.status)"
          >
            <div class="timeline-item">
              <div class="timeline-title">
                <span>{{ stageMap[item.stage] || item.stage }}</span>
                <a-tag :color="getProcessTagColor(item.status)">
                  {{ getProcessTagText(item.status) }}
                </a-tag>
              </div>
              <p class="timeline-desc">{{ item.description || '暂无描述' }}</p>
            </div>
          </a-timeline-item>
        </a-timeline>

        <a-divider />
        <a-form layout="vertical">
          <a-form-item label="流程阶段">
            <a-select v-model:value="editForm.stage" placeholder="选择阶段">
              <a-select-option v-for="(label, value) in stageMap" :key="value" :value="value">
                {{ label }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="状态">
            <a-select v-model:value="editForm.status" placeholder="选择状态">
              <a-select-option value="pending">待处理</a-select-option>
              <a-select-option value="processing">进行中</a-select-option>
              <a-select-option value="completed">已完成</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="说明">
            <a-textarea v-model:value="editForm.description" :rows="3" placeholder="输入流程说明" />
          </a-form-item>
          <a-button type="primary" block :loading="saving" @click="saveProcess">提交更新</a-button>
        </a-form>
      </template>
    </a-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getAppointmentList } from '../../api/appointment'
import { getProcessByAppointment, updateProcess } from '../../api/process'

const router = useRouter()
const loading = ref(false)
const saving = ref(false)
const drawerOpen = ref(false)
const statusFilter = ref(undefined)
const appointments = ref([])
const processList = ref([])
const currentAppointment = ref(null)

const stageMap = {
  confirmed: '确认预约',
  pickup: '接送宠物',
  farewell: '告别仪式',
  cremation: '火化服务',
  processing: '骨灰处理',
  memorial: '纪念品制作',
  completed: '服务完成'
}

const appointmentStatusColor = {
  pending: 'orange',
  confirmed: 'blue',
  processing: 'processing',
  completed: 'success',
  cancelled: 'default'
}

const appointmentStatusText = {
  pending: '待确认',
  confirmed: '已确认',
  processing: '进行中',
  completed: '已完成',
  cancelled: '已取消'
}

const editForm = reactive({
  stage: 'confirmed',
  status: 'pending',
  description: ''
})

const columns = [
  { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 170 },
  { title: '宠物', dataIndex: 'petName', key: 'petName', width: 140 },
  { title: '预约状态', key: 'status', width: 120 },
  { title: '流程状态', key: 'latestProcess', width: 120 },
  { title: '操作', key: 'action', width: 120 }
]

const filteredAppointments = computed(() => {
  if (!statusFilter.value) {
    return appointments.value
  }
  return appointments.value.filter((item) => item.processStatus === statusFilter.value)
})

const getProcessTagColor = (status) => {
  const colorMap = {
    pending: 'default',
    processing: 'processing',
    completed: 'success'
  }
  return colorMap[status] || 'default'
}

const getProcessTagText = (status) => {
  const textMap = {
    pending: '待处理',
    processing: '进行中',
    completed: '已完成'
  }
  return textMap[status] || '-'
}

const goBack = () => {
  router.push('/admin')
}

const loadAppointments = async () => {
  loading.value = true
  try {
    const res = await getAppointmentList({ pageNum: 1, pageSize: 100 })
    if (res.code === 200) {
      const rows = res.data?.list || []
      appointments.value = rows.map((item) => ({
        ...item,
        processStatus: item.status === 'completed' ? 'completed' : item.status === 'processing' ? 'processing' : 'pending'
      }))
    }
  } finally {
    loading.value = false
  }
}

const loadProcessList = async (appointmentId) => {
  const res = await getProcessByAppointment(appointmentId)
  if (res.code === 200) {
    processList.value = res.data || []
  }
}

const openDrawer = async (record) => {
  currentAppointment.value = record
  drawerOpen.value = true
  editForm.description = ''
  editForm.status = 'pending'
  editForm.stage = 'confirmed'
  await loadProcessList(record.id)
}

const saveProcess = async () => {
  if (!currentAppointment.value) {
    return
  }
  saving.value = true
  try {
    await updateProcess(currentAppointment.value.id, {
      stage: editForm.stage,
      status: editForm.status,
      description: editForm.description
    })
    message.success('流程已更新')
    await Promise.all([loadProcessList(currentAppointment.value.id), loadAppointments()])
  } catch {
    message.error('更新失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadAppointments()
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

.filter-select {
  width: 140px;
}

.desc-card {
  margin-bottom: 16px;
}

.timeline {
  margin-top: 12px;
}

.timeline-item {
  padding: 10px 12px;
  border-radius: 8px;
  background: #fafafa;
}

.timeline-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.timeline-desc {
  margin: 8px 0 0;
  color: #595959;
}
</style>
