<template>
  <div class="module-page">
    <div class="module-header">
      <div>
        <h1 class="module-title">任务列表</h1>
        <p class="module-subtitle">服务人员当日待办与执行中任务</p>
      </div>
      <a-select v-model:value="statusFilter" allow-clear placeholder="状态筛选" class="filter-select">
        <a-select-option value="confirmed">已确认</a-select-option>
        <a-select-option value="processing">进行中</a-select-option>
        <a-select-option value="completed">已完成</a-select-option>
      </a-select>
    </div>

    <a-table
      :columns="columns"
      :data-source="filteredTasks"
      :loading="loading"
      row-key="id"
      :pagination="{ pageSize: 10, showSizeChanger: false }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="statusColors[record.status] || 'default'">
            {{ statusTexts[record.status] || record.status }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" @click="$router.push(`/service/task/${record.id}`)">详情</a-button>
            <a-button type="link" @click="$router.push(`/service/process/${record.id}`)">更新流程</a-button>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getServiceAppointments } from '../../api/appointment'

const loading = ref(false)
const statusFilter = ref(undefined)
const tasks = ref([])

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

const columns = [
  { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 180 },
  { title: '宠物', dataIndex: 'petName', key: 'petName', width: 120 },
  { title: '预约时间', dataIndex: 'appointmentTime', key: 'appointmentTime', width: 180 },
  { title: '联系人', dataIndex: 'contactName', key: 'contactName', width: 120 },
  { title: '状态', key: 'status', width: 120 },
  { title: '操作', key: 'action', width: 170 }
]

const filteredTasks = computed(() => {
  if (!statusFilter.value) {
    return tasks.value
  }
  return tasks.value.filter((item) => item.status === statusFilter.value)
})

const loadTasks = async () => {
  loading.value = true
  try {
    const res = await getServiceAppointments({ pageNum: 1, pageSize: 200 })
    if (res.code === 200) {
      tasks.value = (res.data?.list || []).filter((item) => item.status !== 'pending' && item.status !== 'cancelled')
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTasks()
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
  justify-content: space-between;
  align-items: flex-start;
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

.filter-select {
  width: 140px;
}
</style>
