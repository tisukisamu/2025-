<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-gradient-to-r from-#111111 to-#374151 text-white p-6">
      <div class="flex items-center justify-between gap-4">
        <div>
          <h1 class="text-22px m-0 font-700">校友互督</h1>
          <p class="m-0 mt-2 text-white/70">拆分为资料、附近、小队三类页面，互督流程更清晰</p>
        </div>
        <a-space>
          <a-input-number v-model:value="radiusKm" :min="1" :max="100" :step="1" addon-after="km" />
          <a-button class="community-hero-btn" :loading="loading" @click="refreshAll">刷新</a-button>
        </a-space>
      </div>
      <div class="mt-4 flex flex-wrap gap-2">
        <a-button :class="activeTab === 'nearby' ? 'app-btn-primary' : 'app-btn-secondary'" @click="go('/alumni/nearby')">附近发现</a-button>
        <a-button :class="activeTab === 'teams' ? 'app-btn-primary' : 'app-btn-secondary'" @click="go('/alumni/teams')">小队互督</a-button>
        <a-button :class="activeTab === 'profile' ? 'app-btn-primary' : 'app-btn-secondary'" @click="go('/alumni/profile')">我的资料</a-button>
      </div>
    </div>
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAlumni } from './useAlumni'

const route = useRoute()
const router = useRouter()
const { loading, radiusKm, refreshAll } = useAlumni()

const activeTab = computed(() => {
  if (route.path.includes('/alumni/teams')) return 'teams'
  if (route.path.includes('/alumni/profile')) return 'profile'
  return 'nearby'
})

const go = (path: string) => {
  router.push(path)
}

onMounted(refreshAll)
</script>

<style scoped>
.community-hero-btn {
  background: rgba(255, 255, 255, 0.14) !important;
  border-color: rgba(255, 255, 255, 0.24) !important;
  color: #ffffff !important;
}

.community-hero-btn:hover,
.community-hero-btn:focus {
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.34) !important;
  color: #ffffff !important;
}
</style>
