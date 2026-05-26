<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">活动列表</h1>
    </div>

    <div class="card">
      <a-form layout="inline">
        <a-form-item label="活动名称">
          <a-input v-model:value="keyword" placeholder="搜索活动" allow-clear />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="fetchData">搜索</a-button>
        </a-form-item>
      </a-form>
    </div>

    <a-row :gutter="24" style="margin-top: 24px">
      <a-col v-for="activity in activityList" :key="activity.id" :xs="24" :sm="12" :lg="8" :xl="6">
        <div class="activity-card" @click="showDetail(activity)">
          <div class="activity-cover">
            <img v-if="activity.coverImage" :src="activity.coverImage" alt="cover" />
            <div v-else class="no-cover">
              <CalendarOutlined />
            </div>
          </div>
          <div class="activity-content">
            <h3>{{ activity.activityName }}</h3>
            <p class="activity-time">
              <ClockCircleOutlined />
              {{ formatTime(activity.startTime) }}
            </p>
            <p class="activity-location">
              <EnvironmentOutlined />
              {{ activity.location || '待定' }}
            </p>
            <div class="activity-footer">
              <span class="signup-count">
                <UserOutlined /> {{ activity.signupCount || 0 }}人报名
              </span>
              <a-tag :color="statusColor(activity.status)">
                {{ statusLabel(activity.status) }}
              </a-tag>
            </div>
          </div>
        </div>
      </a-col>
    </a-row>

    <div class="pagination-wrapper">
      <a-pagination
        v-model:current="pagination.current"
        :total="pagination.total"
        :pageSize="pagination.pageSize"
        show-size-changer
        @change="handlePageChange"
      />
    </div>

    <a-modal
      v-model:open="detailVisible"
      :title="currentActivity?.activityName"
      :footer="null"
      width="600px"
    >
      <a-descriptions :column="1" bordered>
        <a-descriptions-item label="活动名称">{{ currentActivity?.activityName }}</a-descriptions-item>
        <a-descriptions-item label="活动时间">
          {{ formatTime(currentActivity?.startTime) }} - {{ formatTime(currentActivity?.endTime) }}
        </a-descriptions-item>
        <a-descriptions-item label="活动地点">{{ currentActivity?.location || '待定' }}</a-descriptions-item>
        <a-descriptions-item label="活动预算">¥{{ currentActivity?.budget?.toFixed(2) || '0.00' }}</a-descriptions-item>
        <a-descriptions-item label="活动描述">{{ currentActivity?.description || '暂无描述' }}</a-descriptions-item>
      </a-descriptions>
      <div style="margin-top: 24px; text-align: right">
        <a-button type="primary" :loading="signupLoading" @click="handleSignup">
          立即报名
        </a-button>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { activityApi } from '@/api'
import type { Activity } from '@/types'
import dayjs from 'dayjs'
import {
  CalendarOutlined,
  ClockCircleOutlined,
  EnvironmentOutlined,
  UserOutlined
} from '@ant-design/icons-vue'

const keyword = ref('')
const activityList = ref<Activity[]>([])
const loading = ref(false)
const detailVisible = ref(false)
const currentActivity = ref<Activity | null>(null)
const signupLoading = ref(false)

const pagination = reactive({
  current: 1,
  pageSize: 12,
  total: 0
})

const formatTime = (time?: string) => {
  if (!time) return ''
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const normalizeImageUrl = (url?: string) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  if (url.startsWith('/upload/')) return url
  if (url.startsWith('/activity/')) return `/upload${url}`
  return url
}

const statusLabel = (status?: string) => {
  const map: Record<string, string> = {
    DRAFT: '草稿',
    SUBMITTED: '待审核',
    PUBLISHED: '已发布',
    REJECTED: '已驳回'
  }
  return map[status || ''] || status
}

const statusColor = (status?: string) => {
  const map: Record<string, string> = {
    DRAFT: 'default',
    SUBMITTED: 'processing',
    PUBLISHED: 'success',
    REJECTED: 'error'
  }
  return map[status || ''] || 'default'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = keyword.value
      ? await activityApi.search(keyword.value, pagination.current - 1, pagination.pageSize)
      : await activityApi.getList({
          page: pagination.current - 1,
          size: pagination.pageSize
        })
    activityList.value = (res.data.list || []).map(item => ({
      ...item,
      coverImage: normalizeImageUrl(item.coverImage)
    }))
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取活动列表失败', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page: number, pageSize: number) => {
  pagination.current = page
  pagination.pageSize = pageSize
  fetchData()
}

const showDetail = (activity: Activity) => {
  currentActivity.value = activity
  detailVisible.value = true
}

const handleSignup = async () => {
  if (!currentActivity.value) return
  if (currentActivity.value.status !== 'PUBLISHED') {
    message.warning('仅已发布活动可报名')
    return
  }
  
  signupLoading.value = true
  try {
    await activityApi.signup(currentActivity.value.id)
    message.success('报名成功')
    detailVisible.value = false
    fetchData()
  } catch (error: any) {
    message.error(error.message || '报名失败')
  } finally {
    signupLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.activity-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 24px;
}

.activity-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.activity-cover {
  height: 160px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.activity-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-cover {
  font-size: 48px;
  color: #d9d9d9;
}

.activity-content {
  padding: 16px;
}

.activity-content h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-time,
.activity-location {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.activity-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.signup-count {
  font-size: 13px;
  color: #666;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
