<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-gradient-to-r from-#111111 to-#374151 text-white p-6">
      <div class="flex items-center justify-between gap-4">
        <div>
          <h1 class="text-22px m-0 font-700">打卡社区</h1>
          <p class="m-0 mt-2 text-white/70">把动态、发布、我的内容拆分为独立页面，互动更清晰</p>
        </div>
        <a-button class="community-hero-btn" :loading="loading" @click="fetchPosts">刷新数据</a-button>
      </div>
      <div class="mt-4 flex flex-wrap gap-2">
        <a-button :class="activeTab === 'feed' ? 'app-btn-primary' : 'app-btn-secondary'" @click="go('/community/feed')">社区动态</a-button>
        <a-button :class="activeTab === 'publish' ? 'app-btn-primary' : 'app-btn-secondary'" @click="go('/community/publish')">发布动态</a-button>
        <a-button :class="activeTab === 'mine' ? 'app-btn-primary' : 'app-btn-secondary'" @click="go('/community/mine')">我的动态</a-button>
      </div>
    </div>
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCommunity } from './useCommunity'

const route = useRoute()
const router = useRouter()
const { loading, fetchPosts } = useCommunity()

const activeTab = computed(() => {
  if (route.path.includes('/community/publish')) return 'publish'
  if (route.path.includes('/community/mine')) return 'mine'
  return 'feed'
})

const go = (path: string) => {
  router.push(path)
}

onMounted(fetchPosts)
</script>

<style scoped>
.community-hero-btn {
  background: rgba(255, 255, 255, 0.14) !important;
  border-color: rgba(255, 255, 255, 0.24) !important;
  color: #ffffff !important;
  height: 36px;
  border-radius: 10px;
}

.community-hero-btn:hover,
.community-hero-btn:focus {
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.34) !important;
  color: #ffffff !important;
}
</style>
