<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">活动审核</h1>
      <p class="page-subtitle">审核社团提交的活动发布申请</p>
    </div>

    <div class="card">
      <a-button @click="fetchData">刷新列表</a-button>
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
          <template v-if="column.key === 'club'">
            {{ record.club?.clubName || '-' }}
          </template>
          <template v-else-if="column.key === 'time'">
            {{ formatDate(record.startTime) }} ~ {{ formatDate(record.endTime) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="primary" size="small" @click="handleApprove(record.id)">通过</a-button>
              <a-button danger size="small" @click="handleReject(record.id)">驳回</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import { activityApi } from '@/api'
import type { Activity } from '@/types'

const loading = ref(false)
const activityList = ref<Activity[]>([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true
})

const columns = [
  { title: '活动名称', dataIndex: 'activityName', key: 'activityName' },
  { title: '所属社团', key: 'club', width: 180 },
  { title: '活动时间', key: 'time', width: 260 },
  { title: '地点', dataIndex: 'location', key: 'location', width: 180 },
  { title: '预算', dataIndex: 'budget', key: 'budget', width: 120 },
  { title: '操作', key: 'action', width: 160 }
]

const formatDate = (time?: string) => (time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-')

const fetchData = async () => {
  loading.value = true
  try {
    const res = await activityApi.getReviewList(pagination.current - 1, pagination.pageSize)
    activityList.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error: any) {
    message.error(error.message || '获取审核列表失败')
  } finally {
    loading.value = false
  }
}

const handleApprove = async (id: number) => {
  await activityApi.approve(id)
  message.success('审核通过')
  fetchData()
}

const handleReject = async (id: number) => {
  await activityApi.reject(id)
  message.success('已驳回')
  fetchData()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.table-card {
  margin-top: 16px;
}
</style>
