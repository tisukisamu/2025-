<template>
  <div class="chat-page">
    <a-card class="chat-card">
      <template #title>
        <div class="chat-header">
          <a-button type="text" @click="router.back()" class="back-btn">
            <arrow-left-outlined />
          </a-button>
          <a-avatar :src="targetUser?.avatar" :style="{ backgroundColor: '#1890ff' }">
            {{ targetUser?.username?.charAt(0) }}
          </a-avatar>
          <span class="chat-title">{{ targetUser?.username || '聊天' }}</span>
        </div>
      </template>
      <div class="chat-content" ref="chatContainer">
        <div
          v-for="msg in messages"
          :key="msg.id"
          :class="['message-item', { self: msg.senderId === userStore.userInfo?.id }]"
        >
          <a-avatar
            v-if="msg.senderId !== userStore.userInfo?.id"
            class="message-avatar"
            :src="targetUser?.avatar"
            :style="{ backgroundColor: '#52c41a' }"
          >
            {{ targetUser?.username?.charAt(0) }}
          </a-avatar>
          <div class="message-body">
            <div class="message-bubble">
              <img v-if="msg.type === 'IMAGE'" :src="msg.content" class="message-image" />
              <span v-else>{{ msg.content }}</span>
            </div>
            <div class="message-time">{{ formatTime(msg.createTime) }}</div>
          </div>
          <a-avatar
            v-if="msg.senderId === userStore.userInfo?.id"
            class="message-avatar"
            :src="userStore.avatar"
            :style="{ backgroundColor: '#1890ff' }"
          >
            {{ userStore.username?.charAt(0) }}
          </a-avatar>
        </div>
        <div v-if="messages.length === 0" class="empty-chat">
          <p>暂无消息，发送第一条消息开始聊天吧</p>
        </div>
      </div>
      <div class="chat-input">
        <a-input-group compact>
          <a-input
            v-model:value="inputMessage"
            placeholder="输入消息..."
            style="width: calc(100% - 120px)"
            @pressEnter="sendMessage"
          />
          <a-upload
            :showUploadList="false"
            :customRequest="handleImageUpload"
            accept="image/*"
          >
            <a-button>
              <picture-outlined />
            </a-button>
          </a-upload>
          <a-button type="primary" @click="sendMessage">
            发送
          </a-button>
        </a-input-group>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { messageApi, uploadApi, userApi } from '@/api'
import type { Message, User } from '@/types'
import { ArrowLeftOutlined, PictureOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const targetUser = ref<User | null>(null)
const messages = ref<Message[]>([])
const inputMessage = ref('')
const chatContainer = ref<HTMLElement | null>(null)

const formatTime = (time: string) => {
  return new Date(time).toLocaleString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const fetchTargetUser = async () => {
  const userId = Number(route.params.userId)
  try {
    const res = await userApi.getUserById(userId)
    targetUser.value = res.data
  } catch {
    // ignore
  }
}

const fetchMessages = async () => {
  const userId = Number(route.params.userId)
  try {
    const res = await messageApi.getConversation(userId)
    messages.value = res.data
    await messageApi.markAsRead(userId)
    nextTick(() => {
      scrollToBottom()
    })
  } catch {
    // ignore
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return
  
  try {
    await messageApi.sendMessage({
      receiverId: Number(route.params.userId),
      content: inputMessage.value,
      type: 'TEXT'
    })
    inputMessage.value = ''
    await fetchMessages()
  } catch {
    // ignore
  }
}

const handleImageUpload = async (options: any) => {
  try {
    const res = await uploadApi.uploadFile(options.file)
    await messageApi.sendMessage({
      receiverId: Number(route.params.userId),
      content: res.data,
      type: 'IMAGE'
    })
    await fetchMessages()
  } catch {
    // ignore
  }
}

const scrollToBottom = () => {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

onMounted(() => {
  fetchTargetUser()
  fetchMessages()
})
</script>

<style scoped>
.chat-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 64px);
}

.chat-card {
  border-radius: 12px;
  height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chat-card :deep(.ant-card-head) {
  border-bottom: 1px solid #f0f0f0;
  background: #fff;
}

.chat-card :deep(.ant-card-body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0;
  overflow: hidden;
  background: #fff;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-btn {
  color: #1890ff;
}

.back-btn:hover {
  color: #40a9ff;
  background: #e6f7ff;
}

.chat-title {
  font-weight: 500;
  font-size: 16px;
}

.chat-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.empty-chat {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.message-item.self {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-body {
  max-width: 70%;
}

.message-bubble {
  padding: 12px 16px;
  background: #fff;
  border-radius: 12px;
  word-break: break-word;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  color: #333;
}

.message-item.self .message-bubble {
  background: #1890ff;
  color: #fff;
}

.message-image {
  max-width: 200px;
  border-radius: 8px;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  text-align: right;
}

.message-item:not(.self) .message-time {
  text-align: left;
}

.chat-input {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  background: #fff;
  border-radius: 0 0 12px 12px;
}

@media (max-width: 768px) {
  .chat-page {
    padding: 12px;
  }
  
  .chat-card {
    height: calc(100vh - 150px);
  }
  
  .message-body {
    max-width: 80%;
  }
}
</style>
