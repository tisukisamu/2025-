<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">活动日历</h1>
      <p class="page-subtitle">按时间查看近期活动安排</p>
    </div>

    <div class="card">
      <a-timeline>
        <a-timeline-item v-for="activity in sortedList" :key="activity.id" :color="itemColor(activity.status)">
          <div class="item-title">{{ activity.activityName }}</div>
          <div class="item-time">{{ formatDate(activity.startTime) }} - {{ formatDate(activity.endTime) }}</div>
          <div class="item-meta">{{ activity.club?.clubName || '-' }} · {{ activity.location || '地点待定' }}</div>
        </a-timeline-item>
      </a-timeline>
      <a-empty v-if="sortedList.length === 0" description="暂无活动安排" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import dayjs from 'dayjs'
import { activityApi } from '@/api'
import type { Activity } from '@/types'

const activityList = ref<Activity[]>([])

const sortedList = computed(() => {
  return [...activityList.value].sort((a, b) => dayjs(a.startTime).valueOf() - dayjs(b.startTime).valueOf())
})

const formatDate = (time?: string) => (time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-')

const itemColor = (status?: string) => {
  if (status === 'PUBLISHED') return 'green'
  if (status === 'SUBMITTED') return 'blue'
  if (status === 'REJECTED') return 'red'
  return 'gray'
}

const fetchData = async () => {
  const res = await activityApi.getList({ page: 0, size: 50 })
  activityList.value = res.data.list || []
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.item-title {
  font-weight: 700;
  color: #111111;
}

.item-time {
  margin-top: 4px;
  color: #444444;
}

.item-meta {
  margin-top: 2px;
  color: #777777;
}
</style>
