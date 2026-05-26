<template>
  <div class="announcement-list-page">
    <div class="page-header">
      <h1>系统公告</h1>
    </div>

    <a-spin :spinning="loading">
      <div class="announcement-list" v-if="announcements.length > 0">
        <div
          v-for="item in announcements"
          :key="item.id"
          class="announcement-item"
          @click="router.push(`/announcements/${item.id}`)"
        >
          <div class="announcement-header">
            <a-tag v-if="item.type === 'URGENT'" color="red">紧急</a-tag>
            <a-tag v-else-if="item.type === 'IMPORTANT'" color="orange">重要</a-tag>
            <span class="announcement-title">{{ item.title }}</span>
            <a-tag v-if="item.isTop" color="blue" size="small">置顶</a-tag>
          </div>
          <div class="announcement-meta">
            <span>{{ formatTime(item.createTime) }}</span>
            <span>阅读 {{ item.viewCount }}</span>
          </div>
        </div>
      </div>
      <a-empty v-else description="暂无公告" />
    </a-spin>

    <div class="pagination-wrapper" v-if="total > 0">
      <a-pagination
        v-model:current="pagination.page"
        v-model:pageSize="pagination.pageSize"
        :total="total"
        show-quick-jumper
        :show-total="(total: number) => `共 ${total} 条公告`"
        @change="fetchAnnouncements"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { announcementApi } from '@/types/extra'
import type { Announcement } from '@/types/extra'

const router = useRouter()

const loading = ref(false)
const announcements = ref<Announcement[]>([])
const total = ref(0)

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const formatTime = (time: string) => {
  return new Date(time).toLocaleDateString('zh-CN')
}

const fetchAnnouncements = async () => {
  loading.value = true
  try {
    const res = await announcementApi.getList(pagination.page, pagination.pageSize)
    announcements.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchAnnouncements()
})
</script>

<style scoped>
.announcement-list-page {
  background: #f5f5f5;
  min-height: calc(100vh - 200px);
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.announcement-list {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.announcement-item {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}

.announcement-item:hover {
  background: #fafafa;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.announcement-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  flex: 1;
}

.announcement-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .announcement-item {
    padding: 12px 16px;
  }

  .announcement-title {
    font-size: 14px;
  }
}
</style>
