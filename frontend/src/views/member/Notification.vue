<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">通知中心</h1>
    </div>

    <div class="card">
      <div class="card-header">
        <a-radio-group v-model:value="filterType" @change="fetchData">
          <a-radio-button value="">全部</a-radio-button>
          <a-radio-button value="SYSTEM">系统通知</a-radio-button>
          <a-radio-button value="APPROVAL">审批通知</a-radio-button>
          <a-radio-button value="ACTIVITY">活动通知</a-radio-button>
        </a-radio-group>
        <a-button type="link" @click="handleMarkAllRead">全部已读</a-button>
      </div>

      <a-list
        :loading="loading"
        :data-source="notificationList"
        item-layout="horizontal"
      >
        <template #renderItem="{ item }">
          <a-list-item>
            <a-list-item-meta :description="item.content">
              <template #title>
                <span :class="{ 'unread-title': !item.isRead }">
                  {{ item.title }}
                </span>
                <a-tag v-if="!item.isRead" color="processing" style="margin-left: 8px">新</a-tag>
              </template>
              <template #avatar>
                <a-avatar :style="{ backgroundColor: getTypeColor(item.type) }">
                  <template #icon>
                    <BellOutlined />
                  </template>
                </a-avatar>
              </template>
            </a-list-item-meta>
            <template #actions>
              <span class="time-text">{{ formatTime(item.createTime) }}</span>
              <a @click="handleMarkRead(item)">标记已读</a>
            </template>
          </a-list-item>
        </template>
      </a-list>

      <div class="pagination-wrapper">
        <a-pagination
          v-model:current="pagination.current"
          :total="pagination.total"
          :pageSize="pagination.pageSize"
          @change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { notificationApi } from '@/api'
import type { Notification } from '@/types'
import dayjs from 'dayjs'
import { BellOutlined } from '@ant-design/icons-vue'

const filterType = ref('')
const notificationList = ref<Notification[]>([])
const loading = ref(false)

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const getTypeColor = (type: string) => {
  const colors: Record<string, string> = {
    SYSTEM: '#1a1a1a',
    APPROVAL: '#1890ff',
    ACTIVITY: '#52c41a',
    WARNING: '#faad14'
  }
  return colors[type] || '#666'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await notificationApi.getList({
      type: filterType.value || undefined,
      page: pagination.current - 1,
      size: pagination.pageSize
    })
    notificationList.value = res.data.content || res.data.list || []
    pagination.total = res.data.totalElements || res.data.total || 0
  } catch (error) {
    console.error('获取通知列表失败', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page: number) => {
  pagination.current = page
  fetchData()
}

const handleMarkRead = async (item: Notification) => {
  try {
    await notificationApi.markAsRead(item.id)
    item.isRead = 1
    message.success('已标记为已读')
  } catch (error: any) {
    message.error(error.message || '操作失败')
  }
}

const handleMarkAllRead = async () => {
  try {
    await notificationApi.markAllAsRead()
    notificationList.value.forEach(item => {
      item.isRead = 1
    })
    message.success('已全部标记为已读')
  } catch (error: any) {
    message.error(error.message || '操作失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.unread-title {
  font-weight: 600;
  color: #1a1a1a;
}

.time-text {
  color: #999;
  font-size: 12px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
