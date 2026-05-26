<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">活动监管台账</h1>
      <p class="page-subtitle">查看负责社团活动进展与报名热度</p>
    </div>

    <div class="card">
      <a-form layout="inline">
        <a-form-item label="社团">
          <a-select v-model:value="clubId" style="width: 220px" @change="fetchData">
            <a-select-option v-for="club in clubList" :key="club.id" :value="club.id">
              {{ club.clubName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button @click="fetchData">刷新</a-button>
        </a-form-item>
      </a-form>
    </div>

    <div class="card table-card">
      <a-table
        :columns="columns"
        :data-source="activityList"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'time'">
            {{ formatDate(record.startTime) }} ~ {{ formatDate(record.endTime) }}
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="statusColor(record.status)">{{ statusLabel(record.status) }}</a-tag>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { activityApi, clubApi } from '@/api'
import type { Activity, Club } from '@/types'

const loading = ref(false)
const clubId = ref<number>()
const clubList = ref<Club[]>([])
const activityList = ref<Activity[]>([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true
})

const columns = [
  { title: '活动名称', dataIndex: 'activityName', key: 'activityName' },
  { title: '活动时间', key: 'time', width: 260 },
  { title: '地点', dataIndex: 'location', key: 'location', width: 180 },
  { title: '状态', key: 'status', width: 120 },
  { title: '报名人数', dataIndex: 'signupCount', key: 'signupCount', width: 120 }
]

const statusLabel = (status?: string) => {
  const map: Record<string, string> = {
    DRAFT: '草稿',
    SUBMITTED: '待审核',
    PUBLISHED: '已发布',
    REJECTED: '已驳回'
  }
  return map[status || ''] || status
}

const statusColor = (status?: string) => {
  const map: Record<string, string> = {
    DRAFT: 'default',
    SUBMITTED: 'processing',
    PUBLISHED: 'success',
    REJECTED: 'error'
  }
  return map[status || ''] || 'default'
}

const formatDate = (value?: string) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '-')

const loadClubs = async () => {
  const res = await clubApi.getMyClubs()
  clubList.value = res.data || []
  if (!clubId.value && clubList.value.length) {
    clubId.value = clubList.value[0].id
  }
}

const fetchData = async () => {
  if (!clubId.value) return
  loading.value = true
  try {
    const res = await activityApi.getList({
      clubId: clubId.value,
      page: pagination.current - 1,
      size: pagination.pageSize
    })
    activityList.value = res.data.list || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchData()
}

onMounted(async () => {
  await loadClubs()
  fetchData()
})
</script>

<style scoped>
.table-card {
  margin-top: 16px;
}
</style>
