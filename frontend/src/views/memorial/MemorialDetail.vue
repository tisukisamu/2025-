<template>
  <div class="memorial-detail-page">
    <div class="memorial-header">
      <div class="header-content">
        <h1 class="memorial-title">{{ memorial.title || memorial.petName }}</h1>
        <p class="memorial-subtitle" v-if="memorial.description">{{ memorial.description }}</p>
        <div class="memorial-meta">
          <span class="meta-item" v-if="memorial.petName">
            <heart-outlined />
            {{ memorial.petName }}
          </span>
          <span class="meta-item">
            <eye-outlined />
            {{ memorial.viewCount }} 次浏览
          </span>
          <span class="meta-item">
            <calendar-outlined />
            {{ formatDate(memorial.createdAt) }}
          </span>
        </div>
      </div>
    </div>

    <div class="memorial-content">
      <div class="photo-gallery">
        <div class="main-photo" v-if="memorial.photos && memorial.photos.length > 0">
          <img :src="getImageUrl(currentPhoto)" :alt="memorial.title" @click="previewPhoto(getImageUrl(currentPhoto))" />
        </div>
        <div class="photo-thumbnails" v-if="memorial.photos && memorial.photos.length > 1">
          <div
            v-for="(photo, index) in memorial.photos"
            :key="index"
            class="thumbnail"
            :class="{ active: currentIndex === index }"
            @click="currentIndex = index"
          >
            <img :src="getImageUrl(photo)" :alt="`Photo ${index + 1}`" />
          </div>
        </div>
      </div>

      <div class="memorial-text" v-if="memorial.description">
        <h3>纪念文字</h3>
        <p>{{ memorial.description }}</p>
      </div>

      <div class="message-section">
        <h3>追思留言</h3>
        
        <div class="message-form" v-if="userStore.isLoggedIn">
          <a-textarea
            v-model:value="newMessage"
            placeholder="写下您的追思..."
            :rows="3"
          />
          <a-button type="primary" @click="handleSendMessage" :loading="sending">
            发表留言
          </a-button>
        </div>
        <div class="login-tip" v-else>
          <a-button type="link" @click="$router.push('/login')">登录</a-button>
          后发表留言
        </div>

        <div class="message-list">
          <div class="message-item" v-for="msg in messages" :key="msg.id">
            <div class="message-header">
              <span class="author">{{ msg.authorName }}</span>
              <span class="time">{{ formatDate(msg.createdAt) }}</span>
            </div>
            <p class="message-content">{{ msg.content }}</p>
          </div>
          <div class="empty-message" v-if="messages.length === 0">
            暂无留言，成为第一个留言的人吧
          </div>
        </div>
      </div>
    </div>

    <a-modal :open="previewVisible" :footer="null" @cancel="previewVisible = false">
      <img :src="previewImage" alt="preview" style="width: 100%" />
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { message } from 'ant-design-vue'
import { HeartOutlined, EyeOutlined, CalendarOutlined } from '@ant-design/icons-vue'
import { getMemorialById } from '../../api/memorial'
import { getAlbumMessages, createMessage } from '../../api/message'
import { getImageUrl } from '../../utils'
import dayjs from 'dayjs'

const route = useRoute()
const userStore = useUserStore()
const memorial = ref({})
const messages = ref([])
const currentIndex = ref(0)
const newMessage = ref('')
const sending = ref(false)
const previewVisible = ref(false)
const previewImage = ref('')

const currentPhoto = computed(() => {
  if (memorial.value.photos && memorial.value.photos.length > 0) {
    return memorial.value.photos[currentIndex.value]
  }
  return ''
})

const formatDate = (date) => dayjs(date).format('YYYY-MM-DD')

const previewPhoto = (url) => {
  previewImage.value = url
  previewVisible.value = true
}

const loadMemorial = async () => {
  try {
    const id = route.params.id
    const res = await getMemorialById(id)
    if (res.code === 200) {
      memorial.value = res.data
    }
  } catch (error) {
    console.error('加载纪念相册失败:', error)
  }
}

const loadMessages = async () => {
  try {
    const id = route.params.id
    const res = await getAlbumMessages(id)
    if (res.code === 200) {
      messages.value = res.data || []
    }
  } catch (error) {
    console.error('加载留言失败:', error)
  }
}

const handleSendMessage = async () => {
  if (!newMessage.value.trim()) {
    message.warning('请输入留言内容')
    return
  }
  
  sending.value = true
  try {
    const res = await createMessage({
      albumId: route.params.id,
      userId: userStore.userInfo?.id,
      authorName: userStore.username,
      content: newMessage.value.trim()
    })
    
    if (res.code === 200) {
      message.success('留言成功')
      newMessage.value = ''
      loadMessages()
    }
  } catch (error) {
    console.error('发送留言失败:', error)
  } finally {
    sending.value = false
  }
}

onMounted(() => {
  loadMemorial()
  loadMessages()
})
</script>

<style scoped>
.memorial-detail-page {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.memorial-header {
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  padding: 48px 24px;
  text-align: center;
}

.memorial-title {
  font-size: 32px;
  font-weight: 700;
  color: #262626;
  margin: 0 0 12px;
}

.memorial-subtitle {
  font-size: 16px;
  color: #595959;
  margin: 0 0 16px;
}

.memorial-meta {
  display: flex;
  justify-content: center;
  gap: 24px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #8c8c8c;
}

.memorial-content {
  padding: 32px 24px;
  max-width: 900px;
  margin: 0 auto;
}

.photo-gallery {
  margin-bottom: 32px;
}

.main-photo {
  width: 100%;
  max-height: 600px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 16px;
}

.main-photo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  cursor: pointer;
}

.photo-thumbnails {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.thumbnail {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color 0.3s;
}

.thumbnail.active {
  border-color: #262626;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.memorial-text {
  background: #fafafa;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 32px;
}

.memorial-text h3 {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 16px;
}

.memorial-text p {
  font-size: 15px;
  color: #595959;
  line-height: 1.8;
  margin: 0;
}

.message-section {
  border-top: 1px solid #f0f0f0;
  padding-top: 32px;
}

.message-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 24px;
}

.message-form {
  margin-bottom: 24px;
}

.message-form .ant-input {
  margin-bottom: 12px;
}

.login-tip {
  text-align: center;
  padding: 24px;
  background: #fafafa;
  border-radius: 8px;
  margin-bottom: 24px;
  color: #8c8c8c;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.author {
  font-weight: 600;
  color: #262626;
}

.time {
  font-size: 12px;
  color: #8c8c8c;
}

.message-content {
  margin: 0;
  font-size: 14px;
  color: #595959;
  line-height: 1.6;
}

.empty-message {
  text-align: center;
  padding: 32px;
  color: #8c8c8c;
}
</style>
