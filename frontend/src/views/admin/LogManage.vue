<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">日志管理</h1>
      <p class="page-subtitle">检索系统操作日志并快速定位异常操作</p>
    </div>

    <div class="card">
      <a-form layout="inline">
        <a-form-item label="操作人">
          <a-input v-model:value="filters.username" placeholder="用户名" allow-clear />
        </a-form-item>
        <a-form-item label="操作类型">
          <a-input v-model:value="filters.operation" placeholder="如：审批、登录" allow-clear />
        </a-form-item>
        <a-form-item label="执行状态">
          <a-select
            v-model:value="filters.status"
            placeholder="全部状态"
            style="width: 140px"
            allow-clear
          >
            <a-select-option :value="1">成功</a-select-option>
            <a-select-option :value="0">失败</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch">查询</a-button>
            <a-button @click="handleReset">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </div>

    <div class="card table-card">
      <a-table
        :columns="columns"
        :data-source="logList"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'error'">
              {{ record.status === 1 ? '成功' : '失败' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'params'">
            <span class="truncate">{{ record.params || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'duration'">
            {{ formatDuration(record.duration) }}
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ formatDate(record.createTime) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="showDetail(record)">详情</a-button>
          </template>
        </template>
      </a-table>
    </div>

    <a-modal
      v-model:open="detailVisible"
      title="日志详情"
      :footer="null"
      width="760px"
      destroy-on-close
    >
      <a-descriptions v-if="currentLog" bordered :column="2">
        <a-descriptions-item label="日志ID">{{ currentLog.id }}</a-descriptions-item>
        <a-descriptions-item label="操作人">{{ currentLog.username || '-' }}</a-descriptions-item>
        <a-descriptions-item label="操作">{{ currentLog.operation || '-' }}</a-descriptions-item>
        <a-descriptions-item label="请求方法">{{ currentLog.method || '-' }}</a-descriptions-item>
        <a-descriptions-item label="IP">{{ currentLog.ip || '-' }}</a-descriptions-item>
        <a-descriptions-item label="耗时">{{ formatDuration(currentLog.duration) }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="currentLog.status === 1 ? 'success' : 'error'">
            {{ currentLog.status === 1 ? '成功' : '失败' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="时间">{{ formatDate(currentLog.createTime) }}</a-descriptions-item>
        <a-descriptions-item label="请求参数" :span="2">
          <pre class="detail-block">{{ currentLog.params || '-' }}</pre>
        </a-descriptions-item>
        <a-descriptions-item label="错误信息" :span="2">
          <pre class="detail-block">{{ currentLog.errorMsg || '-' }}</pre>
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import { logApi } from '@/api'
import type { SysLog } from '@/types'

const loading = ref(false)
const logList = ref<SysLog[]>([])
const detailVisible = ref(false)
const currentLog = ref<SysLog>()

const filters = reactive({
  username: undefined as string | undefined,
  operation: undefined as string | undefined,
  status: undefined as number | undefined
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`
})

const columns = [
  { title: '操作人', dataIndex: 'username', key: 'username', width: 120 },
  { title: '操作', dataIndex: 'operation', key: 'operation', width: 160 },
  { title: '请求方法', dataIndex: 'method', key: 'method', ellipsis: true },
  { title: '请求参数', dataIndex: 'params', key: 'params', ellipsis: true },
  { title: 'IP', dataIndex: 'ip', key: 'ip', width: 120 },
  { title: '耗时', dataIndex: 'duration', key: 'duration', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 90 },
  { title: '时间', dataIndex: 'createTime', key: 'createTime', width: 170 },
  { title: '操作', key: 'action', width: 80, fixed: 'right' }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await logApi.getList({
      username: filters.username,
      operation: filters.operation,
      status: filters.status,
      page: pagination.current - 1,
      size: pagination.pageSize
    })
    logList.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error: any) {
    message.error(error.message || '获取日志失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  filters.username = undefined
  filters.operation = undefined
  filters.status = undefined
  pagination.current = 1
  fetchData()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchData()
}

const showDetail = (record: SysLog) => {
  currentLog.value = record
  detailVisible.value = true
}

const formatDate = (value?: string) => {
  if (!value) return '-'
  return dayjs(value).format('YYYY-MM-DD HH:mm:ss')
}

const formatDuration = (value?: number) => {
  if (value === undefined || value === null) return '-'
  return `${value}ms`
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.table-card {
  margin-top: 16px;
}

.truncate {
  display: inline-block;
  max-width: 260px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-block {
  margin: 0;
  max-height: 160px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-word;
  color: #1f1f1f;
}
</style>
