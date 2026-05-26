<template>
  <div class="following-page">
    <div class="back-nav">
      <a-button type="text" @click="router.back()">
        <left-outlined /> 返回
      </a-button>
    </div>
    <div class="page-header">
      <h1>我的关注</h1>
    </div>

    <a-tabs v-model:activeKey="activeTab">
      <a-tab-pane key="following" tab="关注">
        <a-spin :spinning="loading">
          <div class="user-list" v-if="followingList.length > 0">
            <div v-for="user in followingList" :key="user.id" class="user-item">
              <div class="user-info" @click="router.push(`/profile/${user.id}`)">
                <a-avatar :size="48" :src="user.avatar">
                  {{ user.nickname?.charAt(0) || user.username?.charAt(0) }}
                </a-avatar>
                <div class="user-detail">
                  <div class="user-name">{{ user.nickname || user.username }}</div>
                  <div class="user-desc">{{ user.bio || '这个人很懒，什么都没写' }}</div>
                </div>
              </div>
              <a-button type="primary" ghost size="small" @click="handleUnfollow(user.id)">
                取消关注
              </a-button>
            </div>
          </div>
          <a-empty v-else description="暂无关注" />
        </a-spin>

        <div class="pagination-wrapper" v-if="followingTotal > 0">
          <a-pagination
            v-model:current="followingPage"
            :pageSize="pageSize"
            :total="followingTotal"
            simple
            @change="fetchFollowing"
          />
        </div>
      </a-tab-pane>

      <a-tab-pane key="followers" tab="粉丝">
        <a-spin :spinning="loading">
          <div class="user-list" v-if="followerList.length > 0">
            <div v-for="user in followerList" :key="user.id" class="user-item">
              <div class="user-info" @click="router.push(`/profile/${user.id}`)">
                <a-avatar :size="48" :src="user.avatar">
                  {{ user.nickname?.charAt(0) || user.username?.charAt(0) }}
                </a-avatar>
                <div class="user-detail">
                  <div class="user-name">{{ user.nickname || user.username }}</div>
                  <div class="user-desc">{{ user.bio || '这个人很懒，什么都没写' }}</div>
                </div>
              </div>
              <a-button
                v-if="!user.isFollowing"
                type="primary"
                size="small"
                @click="handleFollow(user.id)"
              >
                关注
              </a-button>
              <a-button v-else type="default" size="small" @click="handleUnfollow(user.id)">
                已关注
              </a-button>
            </div>
          </div>
          <a-empty v-else description="暂无粉丝" />
        </a-spin>

        <div class="pagination-wrapper" v-if="followerTotal > 0">
          <a-pagination
            v-model:current="followerPage"
            :pageSize="pageSize"
            :total="followerTotal"
            simple
            @change="fetchFollowers"
          />
        </div>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { followApi } from '@/types/extra'
import type { User } from '@/types'
import { message } from 'ant-design-vue'
import { LeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const activeTab = ref('following')
const loading = ref(false)
const followingList = ref<User[]>([])
const followerList = ref<User[]>([])
const followingTotal = ref(0)
const followerTotal = ref(0)
const followingPage = ref(1)
const followerPage = ref(1)
const pageSize = 10

const fetchFollowing = async () => {
  loading.value = true
  try {
    const res = await followApi.getFollowing(followingPage.value, pageSize)
    followingList.value = res.data.list
    followingTotal.value = res.data.total
  } finally {
    loading.value = false
  }
}

const fetchFollowers = async () => {
  loading.value = true
  try {
    const res = await followApi.getFollowers(followerPage.value, pageSize)
    followerList.value = res.data.list
    followerTotal.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleFollow = async (userId: number) => {
  try {
    await followApi.follow(userId)
    message.success('关注成功')
    fetchFollowers()
  } catch {
    // ignore
  }
}

const handleUnfollow = async (userId: number) => {
  try {
    await followApi.unfollow(userId)
    message.success('已取消关注')
    if (activeTab.value === 'following') {
      fetchFollowing()
    } else {
      fetchFollowers()
    }
  } catch {
    // ignore
  }
}

onMounted(() => {
  fetchFollowing()
})
</script>

<style scoped>
.following-page {
  background: #f5f5f5;
  min-height: calc(100vh - 200px);
}

.back-nav {
  margin-bottom: 16px;
}

.back-nav :deep(.ant-btn) {
  color: #1890ff;
  padding: 4px 8px;
}

.back-nav :deep(.ant-btn:hover) {
  background: #e6f7ff;
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

.user-list {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.user-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.user-item:last-child {
  border-bottom: none;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  flex: 1;
}

.user-detail {
  flex: 1;
}

.user-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.user-desc {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .user-item {
    padding: 12px;
  }

  .user-name {
    font-size: 14px;
  }

  .user-desc {
    font-size: 12px;
  }
}
</style>
