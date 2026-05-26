<template>
  <div class="feedback-manage-page">
    <div class="back-nav">
      <a-button type="text" @click="router.back()">
        <left-outlined /> 返回
      </a-button>
    </div>
    <div class="page-header">
      <h1>反馈管理</h1>
    </div>

    <div class="filter-bar">
      <a-select v-model:value="filterStatus" placeholder="状态筛选" style="width: 120px" allowClear @change="fetchFeedbacks">
        <a-select-option value="PENDING">待处理</a-select-option>
        <a-select-option value="PROCESSING">处理中</a-select-option>
        <a-select-option value="RESOLVED">已解决</a-select-option>
        <a-select-option value="CLOSED">已关闭</a-select-option>
      </a-select>
    </div>

    <a-table
      :columns="columns"
      :data-source="feedbacks"
      :loading="loading"
      :pagination="pagination"
      row-key="id"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'type'">
          <a-tag :color="getTypeColor(record.type)">{{ getTypeText(record.type) }}</a-tag>
        </template>
        <template v-else-if="column.key === 'status'">
          <a-tag :color="getStatusColor(record.status)">{{ getStatusText(record.status) }}</a-tag>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="showReplyModal(record)">
              回复
            </a-button>
            <a-dropdown>
              <a-button type="link" size="small">状态</a-button>
              <template #overlay>
                <a-menu @click="(e: { key: string }) => handleStatusChange(record.id, e.key as Feedback['status'])">
                  <a-menu-item key="PROCESSING">处理中</a-menu-item>
                  <a-menu-item key="RESOLVED">已解决</a-menu-item>
                  <a-menu-item key="CLOSED">已关闭</a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="replyModalVisible"
      title="回复反馈"
      @ok="handleReply"
      :confirm-loading="replyLoading"
    >
      <div class="feedback-detail" v-if="currentFeedback">
        <p><strong>标题：</strong>{{ currentFeedback.title }}</p>
        <p><strong>内容：</strong>{{ currentFeedback.content }}</p>
        <p v-if="currentFeedback.contactInfo">
          <strong>联系方式：</strong>{{ currentFeedback.contactInfo }}
        </p>
      </div>
      <a-divider />
      <a-form layout="vertical">
        <a-form-item label="回复内容">
          <a-textarea v-model:value="replyContent" :rows="4" placeholder="请输入回复内容" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { feedbackApi } from '@/types/extra'
import type { Feedback } from '@/types/extra'
import { message } from 'ant-design-vue'
import { LeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const loading = ref(false)
const feedbacks = ref<Feedback[]>([])
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
  { title: '类型', key: 'type', width: 100 },
  { title: '标题', dataIndex: 'title', key: 'title', ellipsis: true },
  { title: '状态', key: 'status', width: 100 },
  { title: '提交时间', dataIndex: 'createTime', key: 'createTime', width: 120 },
  { title: '操作', key: 'action', width: 120 }
]

const replyModalVisible = ref(false)
const replyLoading = ref(false)
const currentFeedback = ref<Feedback | null>(null)
const replyContent = ref('')

const getTypeText = (type: Feedback['type']) => {
  const map: Record<Feedback['type'], string> = {
    BUG: 'Bug反馈',
    SUGGESTION: '功能建议',
    COMPLAINT: '投诉',
    OTHER: '其他'
  }
  return map[type]
}

const getTypeColor = (type: Feedback['type']) => {
  const map: Record<Feedback['type'], string> = {
    BUG: 'red',
    SUGGESTION: 'green',
    COMPLAINT: 'orange',
    OTHER: 'default'
  }
  return map[type]
}

const getStatusText = (status: Feedback['status']) => {
  const map: Record<Feedback['status'], string> = {
    PENDING: '待处理',
    PROCESSING: '处理中',
    RESOLVED: '已解决',
    CLOSED: '已关闭'
  }
  return map[status]
}

const getStatusColor = (status: Feedback['status']) => {
  const map: Record<Feedback['status'], string> = {
    PENDING: 'default',
    PROCESSING: 'processing',
    RESOLVED: 'success',
    CLOSED: 'default'
  }
  return map[status]
}

const fetchFeedbacks = async () => {
  loading.value = true
  try {
    const res = await feedbackApi.getAll(filterStatus.value as Feedback['status'], pagination.current, pagination.pageSize)
    feedbacks.value = res.data.list
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleTableChange = (pag: { current: number; pageSize: number }) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchFeedbacks()
}

const showReplyModal = (feedback: Feedback) => {
  currentFeedback.value = feedback
  replyContent.value = feedback.replyContent || ''
  replyModalVisible.value = true
}

const handleReply = async () => {
  if (!currentFeedback.value || !replyContent.value) {
    message.warning('请输入回复内容')
    return
  }

  replyLoading.value = true
  try {
    await feedbackApi.reply(currentFeedback.value.id, replyContent.value)
    message.success('回复成功')
    replyModalVisible.value = false
    fetchFeedbacks()
  } finally {
    replyLoading.value = false
  }
}

const handleStatusChange = async (id: number, status: Feedback['status']) => {
  try {
    await feedbackApi.updateStatus(id, status)
    message.success('状态更新成功')
    fetchFeedbacks()
  } catch {
    // ignore
  }
}

onMounted(() => {
  fetchFeedbacks()
})
</script>

<style scoped>
.feedback-manage-page {
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

.feedback-detail {
  background: #f4f4f5;
  padding: 12px;
  border-radius: 8px;
}

.feedback-detail p {
  margin-bottom: 8px;
}

.feedback-detail p:last-child {
  margin-bottom: 0;
}
</style>
