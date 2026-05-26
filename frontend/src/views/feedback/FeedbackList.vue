<template>
  <div class="feedback-list-page">
    <div class="page-header">
      <h1>我的反馈</h1>
      <a-button type="primary" @click="router.push('/feedback/create')">
        提交反馈
      </a-button>
    </div>

    <a-spin :spinning="loading">
      <div class="feedback-list" v-if="feedbacks.length > 0">
        <div
          v-for="item in feedbacks"
          :key="item.id"
          class="feedback-item"
        >
          <div class="feedback-header">
            <a-tag :color="getTypeColor(item.type)">{{ getTypeText(item.type) }}</a-tag>
            <a-tag :color="getStatusColor(item.status)">{{ getStatusText(item.status) }}</a-tag>
          </div>
          <div class="feedback-title">{{ item.title }}</div>
          <div class="feedback-content">{{ item.content }}</div>
          <div class="feedback-footer">
            <span>{{ formatTime(item.createTime) }}</span>
          </div>
          <div v-if="item.replyContent" class="feedback-reply">
            <div class="reply-label">官方回复：</div>
            <div class="reply-content">{{ item.replyContent }}</div>
          </div>
        </div>
      </div>
      <a-empty v-else description="暂无反馈" />
    </a-spin>

    <div class="pagination-wrapper" v-if="total > 0">
      <a-pagination
        v-model:current="pagination.page"
        v-model:pageSize="pagination.pageSize"
        :total="total"
        simple
        @change="fetchFeedbacks"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { feedbackApi } from '@/types/extra'
import type { Feedback } from '@/types/extra'

const router = useRouter()

const loading = ref(false)
const feedbacks = ref<Feedback[]>([])
const total = ref(0)

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const formatTime = (time: string) => {
  return new Date(time).toLocaleDateString('zh-CN')
}

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
    const res = await feedbackApi.getMine(pagination.page, pagination.pageSize)
    feedbacks.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchFeedbacks()
})
</script>

<style scoped>
.feedback-list-page {
  background: #f5f5f5;
  min-height: calc(100vh - 200px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feedback-item {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}

.feedback-header {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.feedback-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.feedback-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}

.feedback-footer {
  font-size: 13px;
  color: #999;
}

.feedback-reply {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #e8e8e8;
  background: #f9f9f9;
  padding: 12px;
  border-radius: 8px;
}

.reply-label {
  font-size: 13px;
  color: #1890ff;
  margin-bottom: 4px;
}

.reply-content {
  font-size: 14px;
  color: #333;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .page-header h1 {
    font-size: 20px;
  }

  .feedback-item {
    padding: 12px;
  }
}
</style>
