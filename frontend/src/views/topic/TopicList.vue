<template>
  <div class="topic-list-page">
    <div class="page-header">
      <h1>话题讨论</h1>
      <a-button type="primary" @click="showCreateModal = true">
        <plus-outlined /> 发起话题
      </a-button>
    </div>

    <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
      <a-tab-pane key="all" tab="全部话题" />
      <a-tab-pane key="hot" tab="热门话题" />
      <a-tab-pane key="BUYING_GUIDE" tab="选购指南" />
      <a-tab-pane key="DEAL_SHARING" tab="好价分享" />
      <a-tab-pane key="TECH_TALK" tab="数码讨论" />
    </a-tabs>

    <a-spin :spinning="loading">
      <div class="topic-list">
        <div
          v-for="topic in topics"
          :key="topic.id"
          class="topic-card"
          @click="router.push(`/topics/${topic.id}`)"
        >
          <div class="topic-cover" v-if="topic.coverImage">
            <img :src="topic.coverImage" />
          </div>
          <div class="topic-content">
            <div class="topic-header">
              <a-tag v-if="topic.isPinned" color="red">置顶</a-tag>
              <a-tag v-if="topic.isHot" color="orange">热门</a-tag>
              <span class="topic-category">{{ getCategoryText(topic.category) }}</span>
            </div>
            <h3 class="topic-title">{{ topic.title }}</h3>
            <p class="topic-excerpt">{{ getExcerpt(topic.content) }}</p>
            <div class="topic-footer">
              <div class="author-info">
                <a-avatar :src="topic.authorAvatar" :size="24">
                  {{ topic.authorName?.charAt(0) }}
                </a-avatar>
                <span class="author-name">{{ topic.authorName }}</span>
                <span class="create-time">{{ formatTime(topic.createTime) }}</span>
              </div>
              <div class="topic-stats">
                <span><eye-outlined /> {{ topic.viewCount }}</span>
                <span><like-outlined /> {{ topic.likeCount }}</span>
                <span><message-outlined /> {{ topic.commentCount }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <a-empty v-if="topics.length === 0" description="暂无话题" />
    </a-spin>

    <div class="pagination-wrapper" v-if="total > pageSize">
      <a-pagination
        v-model:current="page"
        :total="total"
        :page-size="pageSize"
        show-quick-jumper
        @change="fetchTopics"
      />
    </div>

    <a-modal
      v-model:open="showCreateModal"
      title="发起话题"
      @ok="handleCreate"
      :confirmLoading="submitting"
      width="600px"
    >
      <a-form layout="vertical">
        <a-form-item label="话题标题" required>
          <a-input v-model:value="createForm.title" placeholder="请输入话题标题" maxlength="100" />
        </a-form-item>
        <a-form-item label="话题内容" required>
          <a-textarea v-model:value="createForm.content" placeholder="分享你的想法..." :rows="6" />
        </a-form-item>
        <a-form-item label="话题分类">
          <a-select v-model:value="createForm.category" placeholder="选择分类">
            <a-select-option value="BUYING_GUIDE">选购指南</a-select-option>
            <a-select-option value="DEAL_SHARING">好价分享</a-select-option>
            <a-select-option value="TECH_TALK">数码讨论</a-select-option>
            <a-select-option value="MARKET_DISCUSSION">市场讨论</a-select-option>
            <a-select-option value="CAMPUS_LIFE">校园生活</a-select-option>
            <a-select-option value="OTHER">其他</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="封面图片">
          <a-upload
            :showUploadList="false"
            :customRequest="handleCoverUpload"
            accept="image/*"
          >
            <div class="cover-upload" v-if="createForm.coverImage">
              <img :src="createForm.coverImage" />
            </div>
            <div class="cover-upload-placeholder" v-else>
              <plus-outlined />
              <span>上传封面</span>
            </div>
          </a-upload>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { topicApi } from '@/api/extra'
import { uploadApi } from '@/api'
import { message } from 'ant-design-vue'
import { PlusOutlined, EyeOutlined, LikeOutlined, MessageOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const topics = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const activeTab = ref('all')
const showCreateModal = ref(false)

const createForm = reactive({
  title: '',
  content: '',
  category: 'TECH_TALK',
  coverImage: ''
})

const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

const getCategoryText = (category: string) => {
  const map: Record<string, string> = {
    'BUYING_GUIDE': '选购指南',
    'DEAL_SHARING': '好价分享',
    'TECH_TALK': '数码讨论',
    'MARKET_DISCUSSION': '市场讨论',
    'CAMPUS_LIFE': '校园生活',
    'OTHER': '其他'
  }
  return map[category] || category
}

const getExcerpt = (content: string) => {
  if (!content) return ''
  return content.length > 100 ? content.slice(0, 100) + '...' : content
}

const fetchTopics = async () => {
  loading.value = true
  try {
    let res
    if (activeTab.value === 'hot') {
      res = await topicApi.getHotTopics({ page: page.value, size: pageSize.value })
    } else {
      const params: any = { page: page.value, size: pageSize.value }
      if (activeTab.value !== 'all') {
        params.category = activeTab.value
      }
      res = await topicApi.getTopics(params)
    }
    topics.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  page.value = 1
  fetchTopics()
}

const handleCoverUpload = async (options: any) => {
  try {
    const res = await uploadApi.uploadFile(options.file)
    createForm.coverImage = res.data
  } catch {
    // ignore
  }
}

const handleCreate = async () => {
  if (!createForm.title.trim() || !createForm.content.trim()) {
    message.warning('请填写标题和内容')
    return
  }
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }

  submitting.value = true
  try {
    await topicApi.createTopic(createForm)
    message.success('发布成功')
    showCreateModal.value = false
    Object.assign(createForm, { title: '', content: '', category: 'TECH_TALK', coverImage: '' })
    fetchTopics()
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchTopics()
})
</script>

<style scoped>
.topic-list-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
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
}

.topic-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.topic-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
}

.topic-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.topic-cover {
  width: 180px;
  flex-shrink: 0;
}

.topic-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.topic-content {
  flex: 1;
  padding: 16px;
}

.topic-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.topic-category {
  font-size: 12px;
  color: #999;
}

.topic-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px;
  color: #333;
}

.topic-excerpt {
  color: #666;
  font-size: 14px;
  margin: 0 0 12px;
  line-height: 1.6;
}

.topic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  font-size: 13px;
  color: #333;
}

.create-time {
  font-size: 12px;
  color: #999;
}

.topic-stats {
  display: flex;
  gap: 16px;
  color: #999;
  font-size: 13px;
}

.topic-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.cover-upload {
  width: 100%;
  max-height: 200px;
  overflow: hidden;
  border-radius: 8px;
}

.cover-upload img {
  width: 100%;
  height: auto;
}

.cover-upload-placeholder {
  width: 100%;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  cursor: pointer;
}

.cover-upload-placeholder:hover {
  border-color: #1890ff;
  color: #1890ff;
}

@media (max-width: 768px) {
  .topic-card {
    flex-direction: column;
  }

  .topic-cover {
    width: 100%;
    height: 150px;
  }
}
</style>
