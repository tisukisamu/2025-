<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-semibold text-neutral-900">投诉建议处理</h1>
      <a-button @click="fetchComplaints">刷新</a-button>
    </div>

    <a-card :bordered="false" class="rounded-xl">
      <a-table :columns="columns" :data-source="complaints" row-key="id" :pagination="{ pageSize: 10 }">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="statusColor[record.status] || 'default'">{{ record.status }}</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <div class="flex items-center gap-2">
              <a-button size="small" @click="openHandleModal(record, 'PROCESSING')">处理中</a-button>
              <a-button size="small" type="primary" class="!bg-neutral-900" @click="openHandleModal(record, 'RESOLVED')">
                已解决
              </a-button>
              <a-button size="small" danger @click="openHandleModal(record, 'REJECTED')">驳回</a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="modalOpen" title="处理投诉" ok-text="提交" cancel-text="取消" @ok="submitHandle">
      <a-form layout="vertical">
        <a-form-item label="处理状态">
          <a-select v-model:value="handleStatus">
            <a-select-option value="PROCESSING">处理中</a-select-option>
            <a-select-option value="RESOLVED">已解决</a-select-option>
            <a-select-option value="REJECTED">驳回</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="处理结果">
          <a-textarea v-model:value="handleResult" :rows="4" placeholder="请输入处理说明" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { complaintApi } from '@/api'

const complaints = ref<any[]>([])
const modalOpen = ref(false)
const currentId = ref<number | null>(null)
const handleStatus = ref('PROCESSING')
const handleResult = ref('')

const statusColor: Record<string, string> = {
  PENDING: 'gold',
  PROCESSING: 'blue',
  RESOLVED: 'green',
  REJECTED: 'red'
}

const columns = [
  { title: 'ID', dataIndex: 'id', width: 80 },
  { title: '类型', dataIndex: 'type', width: 120 },
  { title: '标题', dataIndex: 'title' },
  { title: '状态', dataIndex: 'status', width: 120 },
  { title: '提交时间', dataIndex: 'createdAt', width: 140 },
  { title: '操作', dataIndex: 'action', width: 260 }
]

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleDateString('zh-CN')
}

const fetchComplaints = async () => {
  try {
    const res = await complaintApi.getAll()
    complaints.value = (res.data || []).map((item: any) => ({
      ...item,
      createdAt: formatDate(item.createdAt)
    }))
  } catch (error) {
    message.error('投诉列表加载失败')
  }
}

const openHandleModal = (record: any, status: string) => {
  currentId.value = record.id
  handleStatus.value = status
  handleResult.value = record.handlingResult || ''
  modalOpen.value = true
}

const submitHandle = async () => {
  if (!currentId.value) return
  if (!handleResult.value) {
    message.warning('请填写处理结果')
    return
  }
  try {
    await complaintApi.handle(currentId.value, handleResult.value, handleStatus.value)
    modalOpen.value = false
    message.success('处理成功')
    await fetchComplaints()
  } catch (error) {
    message.error('处理失败')
  }
}

onMounted(() => {
  fetchComplaints()
})
</script>
