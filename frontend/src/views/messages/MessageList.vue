<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-semibold text-neutral-900">消息中心</h1>
      <a-button type="primary" class="!bg-neutral-900" @click="markAllRead">
        全部已读
      </a-button>
    </div>

    <a-card :bordered="false" class="rounded-xl">
      <a-list :data-source="messages" item-layout="horizontal">
        <template #renderItem="{ item }">
          <a-list-item class="!px-0">
            <a-list-item-meta>
              <template #title>
                <div class="flex items-center gap-2">
                  <span class="font-medium text-neutral-900">{{ item.title }}</span>
                  <a-tag v-if="!item.read" color="processing">未读</a-tag>
                </div>
              </template>
              <template #description>
                <div class="text-neutral-500">{{ item.content }}</div>
              </template>
            </a-list-item-meta>
            <div class="text-sm text-neutral-400">{{ item.time }}</div>
          </a-list-item>
        </template>
      </a-list>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { messageApi } from '@/api'

const messages = ref<any[]>([])

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleString('zh-CN', { hour12: false })
}

const fetchMessages = async () => {
  try {
    const res = await messageApi.getList(0, 50)
    messages.value = (res.data?.content || []).map((item: any) => ({
      id: item.id,
      title: item.title,
      content: item.content,
      time: formatDate(item.createdAt),
      read: item.isRead
    }))
  } catch (error) {
    message.error('获取消息失败')
  }
}

const markAllRead = async () => {
  try {
    await messageApi.markAllRead()
    messages.value = messages.value.map((item) => ({ ...item, read: true }))
    message.success('已全部标记为已读')
  } catch (error) {
    message.error('标记失败')
  }
}

onMounted(() => {
  fetchMessages()
})
</script>
