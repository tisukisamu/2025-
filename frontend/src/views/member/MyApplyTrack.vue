<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">我的申请跟踪</h1>
      <p class="page-subtitle">查看个人资金申请进度与审批状态</p>
    </div>

    <div class="card table-card">
      <a-table
        :columns="columns"
        :data-source="applyList"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'club'">
            {{ record.club?.clubName || '-' }}
          </template>
          <template v-else-if="column.key === 'amount'">
            ¥{{ (record.amount || 0).toFixed(2) }}
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="statusColor(record.status)">{{ statusLabel(record.status) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'time'">
            {{ formatTime(record.createTime) }}
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { fundApi } from '@/api'
import type { FundApply } from '@/types'

const loading = ref(false)
const applyList = ref<FundApply[]>([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true
})

const columns = [
  { title: '申请单号', dataIndex: 'applyNo', key: 'applyNo' },
  { title: '社团', key: 'club', width: 140 },
  { title: '类型', dataIndex: 'applyType', key: 'applyType', width: 120 },
  { title: '金额', key: 'amount', width: 120 },
  { title: '状态', key: 'status', width: 140 },
  { title: '提交时间', key: 'time', width: 180 }
]

const statusLabel = (status?: string) => {
  const map: Record<string, string> = {
    PENDING: '待审批',
    PRESIDENT_APPROVED: '社长已审',
    TEACHER_APPROVED: '老师已审',
    COMPLETED: '已完成',
    REJECTED: '已驳回',
    CANCELLED: '已撤销'
  }
  return map[status || ''] || status
}

const statusColor = (status?: string) => {
  const map: Record<string, string> = {
    PENDING: 'processing',
    PRESIDENT_APPROVED: 'blue',
    TEACHER_APPROVED: 'success',
    COMPLETED: 'success',
    REJECTED: 'error',
    CANCELLED: 'default'
  }
  return map[status || ''] || 'default'
}

const formatTime = (value?: string) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '-')

const fetchData = async () => {
  loading.value = true
  try {
    const res = await fundApi.getMyApplies(pagination.current - 1, pagination.pageSize)
    applyList.value = res.data.list || []
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

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.table-card {
  margin-top: 8px;
}
</style>
