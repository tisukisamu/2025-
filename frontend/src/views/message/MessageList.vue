<template>
  <div class="messages-page">
    <a-row :gutter="[24, 24]">
      <a-col :xs="24" :md="8">
        <a-card title="消息列表" class="message-list-card">
          <template #extra>
            <a-badge :count="unreadCount" :offset="[10, 0]">
              <a-button type="link" size="small" @click="markAllRead">
                全部已读
              </a-button>
            </a-badge>
          </template>
          <a-list
            :data-source="conversations"
            :loading="loading"
          >
            <template #renderItem="{ item }">
              <a-list-item
                :class="{ active: activeChat === item.userId }"
                @click="selectConversation(item)"
              >
                <a-list-item-meta :description="item.lastMessage">
                  <template #title>
                    <div class="conversation-title">
                      <span>{{ item.userName }}</span>
                      <span class="time">{{ formatTime(item.lastTime) }}</span>
                    </div>
                  </template>
                  <template #avatar>
                    <a-badge :dot="item.unread > 0">
                      <a-avatar>{{ item.userName.charAt(0) }}</a-avatar>
                    </a-badge>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </template>
            <template #empty>
              <a-empty description="暂无消息" />
            </template>
          </a-list>
        </a-card>
      </a-col>

      <a-col :xs="24" :md="16">
        <a-card v-if="activeChat" class="chat-card">
          <template #title>
            <div class="chat-header">
              <a-avatar>{{ activeConversation?.userName.charAt(0) }}</a-avatar>
              <span class="chat-title">{{ activeConversation?.userName }}</span>
            </div>
          </template>
          <div class="chat-content" ref="chatContainer">
            <div
              v-for="msg in messages"
              :key="msg.id"
              :class="['message-item', { self: msg.senderId === userStore.userInfo?.id }]"
            >
              <div class="message-bubble">
                <img v-if="msg.type === 'IMAGE'" :src="msg.content" class="message-image" />
                <span v-else>{{ msg.content }}</span>
              </div>
              <div class="message-time">{{ formatTime(msg.createTime) }}</div>
            </div>
          </div>
          <div class="chat-input">
            <a-input
              v-model:value="inputMessage"
              placeholder="输入消息..."
              @pressEnter="sendMessage"
            >
              <template #suffix>
                <a-space>
                  <a-upload
                    :showUploadList="false"
                    :customRequest="handleImageUpload"
                    accept="image/*"
                  >
                    <picture-outlined class="upload-icon" />
                  </a-upload>
                  <a-button type="primary" size="small" @click="sendMessage">
                    发送
                  </a-button>
                </a-space>
              </template>
            </a-input>
          </div>
        </a-card>
        <a-card v-else class="empty-chat">
          <a-empty description="选择一个对话开始聊天" />
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'
import { messageApi, uploadApi } from '@/api'
import type { Message } from '@/types'
import { PictureOutlined } from '@ant-design/icons-vue'

const userStore = useUserStore()

const loading = ref(false)
const conversations = ref<any[]>([])
const activeChat = ref<number | null>(null)
const activeConversation = ref<any>(null)
const messages = ref<Message[]>([])
const inputMessage = ref('')
const unreadCount = ref(0)
const chatContainer = ref<HTMLElement | null>(null)

const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return date.toLocaleDateString('zh-CN')
}

const fetchConversations = async () => {
  loading.value = true
  try {
    const res = await messageApi.getMessages()
    conversations.value = res.data.list || []
    unreadCount.value = await messageApi.getUnreadCount().then(r => r.data)
  } finally {
    loading.value = false
  }
}

const selectConversation = async (conv: any) => {
  activeChat.value = conv.userId
  activeConversation.value = conv
  try {
    const res = await messageApi.getConversation(conv.userId)
    messages.value = res.data
    await messageApi.markAsRead(conv.userId)
    conv.unread = 0
    nextTick(() => {
      scrollToBottom()
    })
  } catch {
    // ignore
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || !activeChat.value) return
  
  try {
    await messageApi.sendMessage({
      receiverId: activeChat.value,
      content: inputMessage.value,
      type: 'TEXT'
    })
    inputMessage.value = ''
    const res = await messageApi.getConversation(activeChat.value)
    messages.value = res.data
    nextTick(() => {
      scrollToBottom()
    })
  } catch {
    // ignore
  }
}

const handleImageUpload = async (options: any) => {
  try {
    const res = await uploadApi.uploadFile(options.file)
    await messageApi.sendMessage({
      receiverId: activeChat.value!,
      content: res.data,
      type: 'IMAGE'
    })
    const msgRes = await messageApi.getConversation(activeChat.value!)
    messages.value = msgRes.data
    nextTick(() => {
      scrollToBottom()
    })
  } catch {
    // ignore
  }
}

const markAllRead = async () => {
  await messageApi.markAsRead(activeChat.value || 0)
  fetchConversations()
}

const scrollToBottom = () => {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

onMounted(() => {
  fetchConversations()
})
</script>

<style scoped>
.messages-page {
  background: #f5f5f5;
  min-height: calc(100vh - 200px);
}

.message-list-card {
  border-radius: 12px;
  height: calc(100vh - 250px);
  overflow: hidden;
}

.message-list-card :deep(.ant-card-body) {
  padding: 0;
  height: calc(100% - 57px);
  overflow-y: auto;
}

.message-list-card :deep(.ant-list-item) {
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.message-list-card :deep(.ant-list-item:hover),
.message-list-card :deep(.ant-list-item.active) {
  background: #e6f7ff;
}

.conversation-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.conversation-title .time {
  font-size: 12px;
  color: #999;
}

.chat-card {
  border-radius: 12px;
  height: calc(100vh - 250px);
  display: flex;
  flex-direction: column;
}

.chat-card :deep(.ant-card-body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0;
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-title {
  font-weight: 500;
}

.chat-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f5f5f5;
}

.message-item {
  margin-bottom: 16px;
  text-align: left;
}

.message-item.self {
  text-align: right;
}

.message-bubble {
  display: inline-block;
  padding: 10px 16px;
  background: #fff;
  border-radius: 12px;
  max-width: 70%;
  word-break: break-word;
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
}

.chat-input {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  background: #fff;
}

.upload-icon {
  cursor: pointer;
  font-size: 18px;
  color: #999;
}

.upload-icon:hover {
  color: #1890ff;
}

.empty-chat {
  border-radius: 12px;
  height: calc(100vh - 250px);
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .message-list-card,
  .chat-card,
  .empty-chat {
    height: auto;
    min-height: 400px;
  }
}
</style>
