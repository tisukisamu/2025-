<template>
  <div class="announcement-detail-page">
    <a-spin :spinning="loading">
      <div class="detail-content" v-if="announcement">
        <div class="back-btn">
          <a-button type="text" @click="router.back()">
            <template #icon><LeftOutlined /></template>
            返回
          </a-button>
        </div>

        <div class="announcement-header">
          <div class="title-row">
            <a-tag v-if="announcement.type === 'URGENT'" color="red">紧急</a-tag>
            <a-tag v-else-if="announcement.type === 'IMPORTANT'" color="orange">重要</a-tag>
            <h1>{{ announcement.title }}</h1>
          </div>
          <div class="meta-info">
            <span>{{ formatTime(announcement.createTime) }}</span>
            <span>阅读 {{ announcement.viewCount }}</span>
          </div>
        </div>

        <a-divider />

        <div class="announcement-content" v-html="announcement.content"></div>
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { announcementApi } from '@/types/extra'
import type { Announcement } from '@/types/extra'
import { LeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const announcement = ref<Announcement | null>(null)

const formatTime = (time: string) => {
  return new Date(time).toLocaleString('zh-CN')
}

const fetchDetail = async () => {
  const id = Number(route.params.id)
  if (!id) return

  loading.value = true
  try {
    const res = await announcementApi.getDetail(id)
    announcement.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.announcement-detail-page {
  background: #f5f5f5;
  min-height: calc(100vh - 200px);
}

.detail-content {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
}

.back-btn {
  margin-bottom: 16px;
}

.announcement-header {
  margin-bottom: 16px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.title-row h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.meta-info {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #999;
}

.announcement-content {
  font-size: 15px;
  line-height: 1.8;
  color: #333;
}

@media (max-width: 768px) {
  .detail-content {
    padding: 16px;
  }

  .title-row h1 {
    font-size: 20px;
  }
}
</style>
