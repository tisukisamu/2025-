<template>
  <div class="topic-detail-page">
    <a-spin :spinning="loading">
      <template v-if="topic">
        <div class="back-nav">
          <a-button type="text" @click="router.back()">
            <left-outlined /> 返回
          </a-button>
        </div>

        <a-card class="topic-card">
          <div class="topic-header">
            <div class="topic-tags">
              <a-tag v-if="topic.isPinned" color="red">置顶</a-tag>
              <a-tag v-if="topic.isHot" color="orange">热门</a-tag>
              <a-tag>{{ getCategoryText(topic.category) }}</a-tag>
            </div>
            <h1>{{ topic.title }}</h1>
            <div class="author-info">
              <a-avatar :src="topic.authorAvatar" :size="40">
                {{ topic.authorName?.charAt(0) }}
              </a-avatar>
              <div class="author-detail">
                <span class="author-name">{{ topic.authorName }}</span>
                <span class="publish-time">{{ formatTime(topic.createTime) }}</span>
              </div>
            </div>
          </div>

          <div class="topic-cover" v-if="topic.coverImage">
            <img :src="topic.coverImage" />
          </div>

          <div class="topic-content" v-html="formatContent(topic.content)"></div>

          <div class="topic-stats">
            <span><eye-outlined /> {{ topic.viewCount }} 浏览</span>
            <span><like-outlined /> {{ topic.likeCount }} 点赞</span>
            <span><message-outlined /> {{ topic.commentCount }} 评论</span>
          </div>

          <div class="topic-actions">
            <a-button :type="topic.isLiked ? 'primary' : 'default'" @click="handleLike">
              <like-outlined /> {{ topic.isLiked ? '已赞' : '点赞' }}
            </a-button>
            <a-button @click="showCommentInput = !showCommentInput">
              <message-outlined /> 评论
            </a-button>
            <a-button v-if="isOwner" @click="handleDelete" danger>
              <delete-outlined /> 删除
            </a-button>
          </div>
        </a-card>

        <a-card title="评论区" class="comments-card">
          <div class="comment-input" v-if="showCommentInput">
            <a-textarea v-model:value="commentContent" placeholder="写下你的评论..." :rows="3" />
            <div class="comment-actions">
              <a-button type="primary" @click="handleComment" :loading="commenting">
                发表评论
              </a-button>
            </div>
          </div>

          <div class="comment-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <a-avatar :src="comment.userAvatar" :size="36">
                {{ comment.userName?.charAt(0) }}
              </a-avatar>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-user">{{ comment.userName }}</span>
                  <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-footer">
                  <a-button type="text" size="small" @click="handleLikeComment(comment.id)">
                    <like-outlined /> {{ comment.likeCount }}
                  </a-button>
                  <a-button type="text" size="small" @click="replyTo = comment.id; replyContent = ''">
                    回复
                  </a-button>
                  <a-button type="text" size="small" danger v-if="comment.userId === userStore.userInfo?.id" @click="handleDeleteComment(comment.id)">
                    删除
                  </a-button>
                </div>
                <div class="reply-input" v-if="replyTo === comment.id">
                  <a-input v-model:value="replyContent" placeholder="回复..." @pressEnter="handleReply(comment.id)" />
                  <a-button type="primary" size="small" @click="handleReply(comment.id)" :loading="replying">
                    发送
                  </a-button>
                  <a-button size="small" @click="replyTo = null">取消</a-button>
                </div>
                <div class="replies" v-if="comment.replies && comment.replies.length > 0">
                  <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                    <span class="reply-user">{{ reply.userName }}</span>
                    <span v-if="reply.parentUserName"> 回复 <span class="reply-to">{{ reply.parentUserName }}</span></span>：
                    <span class="reply-text">{{ reply.content }}</span>
                    <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <a-empty v-if="comments.length === 0" description="暂无评论，快来抢沙发吧" />
        </a-card>
      </template>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { topicApi } from '@/api/extra'
import { message, Modal } from 'ant-design-vue'
import { LeftOutlined, EyeOutlined, LikeOutlined, MessageOutlined, DeleteOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const commenting = ref(false)
const replying = ref(false)
const topic = ref<any>(null)
const comments = ref<any[]>([])
const showCommentInput = ref(false)
const commentContent = ref('')
const replyTo = ref<number | null>(null)
const replyContent = ref('')

const isOwner = computed(() => topic.value?.authorId === userStore.userInfo?.id)

const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / (1000 * 60))
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

const formatContent = (content: string) => {
  if (!content) return ''
  return content.replace(/\n/g, '<br>')
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

const fetchTopic = async () => {
  const id = Number(route.params.id)
  loading.value = true
  try {
    const res = await topicApi.getTopicById(id)
    topic.value = res.data
    fetchComments()
  } finally {
    loading.value = false
  }
}

const fetchComments = async () => {
  const id = Number(route.params.id)
  try {
    const res = await topicApi.getComments(id, { page: 1, size: 100 })
    comments.value = res.data.list
  } catch {
    // ignore
  }
}

const handleLike = async () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    return
  }
  try {
    if (topic.value.isLiked) {
      await topicApi.unlikeTopic(topic.value.id)
      topic.value.likeCount--
    } else {
      await topicApi.likeTopic(topic.value.id)
      topic.value.likeCount++
    }
    topic.value.isLiked = !topic.value.isLiked
  } catch {
    // ignore
  }
}

const handleDelete = () => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这个话题吗？',
    okText: '删除',
    cancelText: '取消',
    okType: 'danger',
    onOk: async () => {
      await topicApi.deleteTopic(topic.value.id)
      message.success('删除成功')
      router.back()
    }
  })
}

const handleComment = async () => {
  if (!commentContent.value.trim()) {
    message.warning('请输入评论内容')
    return
  }
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    return
  }
  commenting.value = true
  try {
    await topicApi.createComment(topic.value.id, { content: commentContent.value })
    message.success('评论成功')
    commentContent.value = ''
    fetchComments()
  } finally {
    commenting.value = false
  }
}

const handleReply = async (parentId: number) => {
  if (!replyContent.value.trim()) {
    message.warning('请输入回复内容')
    return
  }
  replying.value = true
  try {
    await topicApi.createComment(topic.value.id, { content: replyContent.value, parentId })
    message.success('回复成功')
    replyTo.value = null
    replyContent.value = ''
    fetchComments()
  } finally {
    replying.value = false
  }
}

const handleLikeComment = async (commentId: number) => {
  try {
    await topicApi.likeComment(commentId)
  } catch {
    // ignore
  }
}

const handleDeleteComment = async (commentId: number) => {
  try {
    await topicApi.deleteComment(commentId)
    message.success('删除成功')
    fetchComments()
  } catch {
    // ignore
  }
}

onMounted(() => {
  fetchTopic()
})
</script>

<style scoped>
.topic-detail-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.back-nav {
  margin-bottom: 16px;
}

.back-nav :deep(.ant-btn) {
  color: #1890ff;
}

.topic-card, .comments-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.topic-header {
  margin-bottom: 20px;
}

.topic-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.topic-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 16px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-detail {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 500;
}

.publish-time {
  font-size: 13px;
  color: #999;
}

.topic-cover {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.topic-cover img {
  width: 100%;
  height: auto;
}

.topic-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  margin-bottom: 20px;
}

.topic-stats {
  display: flex;
  gap: 24px;
  color: #999;
  font-size: 14px;
  padding: 16px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.topic-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.topic-actions {
  display: flex;
  gap: 12px;
}

.comment-input {
  margin-bottom: 20px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.comment-user {
  font-weight: 500;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-text {
  margin: 0 0 8px;
  color: #333;
  line-height: 1.6;
}

.comment-footer {
  display: flex;
  gap: 8px;
}

.reply-input {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.reply-input .ant-input {
  flex: 1;
}

.replies {
  margin-top: 12px;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
}

.reply-item {
  font-size: 14px;
  margin-bottom: 8px;
}

.reply-item:last-child {
  margin-bottom: 0;
}

.reply-user {
  color: #1890ff;
  font-weight: 500;
}

.reply-to {
  color: #1890ff;
}

.reply-text {
  color: #333;
}

.reply-time {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
}
</style>
