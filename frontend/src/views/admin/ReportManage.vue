<template>
  <div class="report-manage-page">
    <div class="back-nav">
      <a-button type="text" @click="router.back()">
        <left-outlined /> 返回
      </a-button>
    </div>
    <div class="page-header">
      <h1>举报管理</h1>
    </div>

    <div class="filter-bar">
      <a-select v-model:value="filterStatus" placeholder="状态筛选" style="width: 120px" allowClear @change="fetchReports">
        <a-select-option value="PENDING">待处理</a-select-option>
        <a-select-option value="PROCESSING">处理中</a-select-option>
        <a-select-option value="RESOLVED">已解决</a-select-option>
        <a-select-option value="REJECTED">已驳回</a-select-option>
      </a-select>
    </div>

    <a-table
      :columns="columns"
      :data-source="reports"
      :loading="loading"
      :pagination="pagination"
      row-key="id"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'product'">
          <div class="product-info" v-if="record.product">
            <span>{{ record.product.title }}</span>
          </div>
        </template>
        <template v-else-if="column.key === 'type'">
          <a-tag :color="getTypeColor(record.type)">{{ getTypeText(record.type) }}</a-tag>
        </template>
        <template v-else-if="column.key === 'status'">
          <a-tag :color="getStatusColor(record.status)">{{ getStatusText(record.status) }}</a-tag>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="showHandleModal(record)">
              处理
            </a-button>
            <a-button type="link" size="small" @click="router.push(`/products/${record.productId}`)">
              查看商品
            </a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="handleModalVisible"
      title="处理举报"
      @ok="handleReport"
      :confirm-loading="handleLoading"
    >
      <a-form layout="vertical">
        <a-form-item label="处理状态">
          <a-select v-model:value="handleForm.status">
            <a-select-option value="PROCESSING">处理中</a-select-option>
            <a-select-option value="RESOLVED">已解决</a-select-option>
            <a-select-option value="REJECTED">已驳回</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="处理结果">
          <a-textarea v-model:value="handleForm.result" :rows="4" placeholder="请输入处理结果" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { reportApi } from '@/types/extra'
import type { Report } from '@/types/extra'
import { message } from 'ant-design-vue'
import { LeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const loading = ref(false)
const reports = ref<Report[]>([])
const filterStatus = ref<string | undefined>()

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`
})

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '商品', key: 'product' },
  { title: '举报类型', key: 'type', width: 100 },
  { title: '原因', dataIndex: 'reason', key: 'reason', ellipsis: true },
  { title: '状态', key: 'status', width: 100 },
  { title: '举报时间', dataIndex: 'createTime', key: 'createTime', width: 120 },
  { title: '操作', key: 'action', width: 150 }
]

const handleModalVisible = ref(false)
const handleLoading = ref(false)
const currentReport = ref<Report | null>(null)
const handleForm = reactive({
  status: '' as Report['status'],
  result: ''
})

const getTypeText = (type: Report['type']) => {
  const map: Record<Report['type'], string> = {
    FRAUD: '欺诈',
    INAPPROPRIATE: '不当内容',
    PROHIBITED: '违禁品',
    OTHER: '其他'
  }
  return map[type]
}

const getTypeColor = (type: Report['type']) => {
  const map: Record<Report['type'], string> = {
    FRAUD: 'red',
    INAPPROPRIATE: 'orange',
    PROHIBITED: 'purple',
    OTHER: 'default'
  }
  return map[type]
}

const getStatusText = (status: Report['status']) => {
  const map: Record<Report['status'], string> = {
    PENDING: '待处理',
    PROCESSING: '处理中',
    RESOLVED: '已解决',
    REJECTED: '已驳回'
  }
  return map[status]
}

const getStatusColor = (status: Report['status']) => {
  const map: Record<Report['status'], string> = {
    PENDING: 'default',
    PROCESSING: 'processing',
    RESOLVED: 'success',
    REJECTED: 'error'
  }
  return map[status]
}

const fetchReports = async () => {
  loading.value = true
  try {
    const res = await reportApi.getReports(filterStatus.value, pagination.current, pagination.pageSize)
    reports.value = res.data.list
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleTableChange = (pag: { current: number; pageSize: number }) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchReports()
}

const showHandleModal = (report: Report) => {
  currentReport.value = report
  handleForm.status = report.status
  handleForm.result = report.handleResult || ''
  handleModalVisible.value = true
}

const handleReport = async () => {
  if (!currentReport.value) return

  handleLoading.value = true
  try {
    await reportApi.handleReport(currentReport.value.id, handleForm.status, handleForm.result)
    message.success('处理成功')
    handleModalVisible.value = false
    fetchReports()
  } finally {
    handleLoading.value = false
  }
}

onMounted(() => {
  fetchReports()
})
</script>

<style scoped>
.report-manage-page {
  min-height: calc(100vh - 200px);
}

.back-nav {
  margin-bottom: 16px;
}

.back-nav :deep(.ant-btn) {
  color: #71717a;
  padding: 4px 8px;
}

.back-nav :deep(.ant-btn:hover) {
  background: #f4f4f5;
  color: #18181b;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #18181b;
}

.filter-bar {
  margin-bottom: 16px;
  background: #ffffff;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e4e4e7;
}

.product-info {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
