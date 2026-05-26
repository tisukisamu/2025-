<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">我的报名</h1>
      <p class="page-subtitle">查看已报名活动与时间安排</p>
    </div>

    <div class="card table-card">
      <a-table :columns="columns" :data-source="activityList" :loading="loading" row-key="id" :pagination="false">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'time'">
            {{ formatDate(record.startTime) }} ~ {{ formatDate(record.endTime) }}
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag color="success">已报名</a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button size="small" danger @click="handleCancel(record.id)">取消报名</a-button>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import { activityApi } from '@/api'
import type { Activity } from '@/types'

const loading = ref(false)
const activityList = ref<Activity[]>([])

const columns = [
  { title: '活动名称', dataIndex: 'activityName', key: 'activityName' },
  { title: '时间', key: 'time', width: 260 },
  { title: '地点', dataIndex: 'location', key: 'location', width: 180 },
  { title: '状态', key: 'status', width: 100 },
  { title: '操作', key: 'action', width: 120 }
]

const formatDate = (time?: string) => (time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-')

const fetchData = async () => {
  loading.value = true
  try {
    const res = await activityApi.getMySignups()
    activityList.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleCancel = async (id: number) => {
  await activityApi.cancelSignup(id)
  message.success('已取消报名')
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.table-card {
  margin-top: 8px;
}
</style>
