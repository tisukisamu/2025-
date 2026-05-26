<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-gradient-to-r from-#111111 to-#374151 text-white p-6">
      <div class="flex items-center justify-between gap-4">
        <div>
          <h1 class="text-22px m-0 font-700">成就多人排行榜</h1>
          <p class="m-0 mt-2 text-white/70">完成并解锁成就即可进入榜单，与更多人一起冲榜</p>
        </div>
        <a-space>
          <a-input-number v-model:value="limit" :min="5" :max="100" :step="5" addon-after="名" />
          <a-button class="bg-white/10 text-white border-white/20" :loading="loading" @click="fetchRanking">刷新排行</a-button>
          <a-button class="bg-white/10 text-white border-white/20" @click="goAchievements">返回成就</a-button>
        </a-space>
      </div>
    </div>

    <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
      <a-empty v-if="rows.length === 0 && !loading" description="暂无排行榜数据" />
      <a-spin :spinning="loading">
        <div class="space-y-2">
          <div
            v-for="item in rows"
            :key="`${item.userId}-${item.rank}`"
            class="rounded-lg border px-3 py-2 flex items-center justify-between gap-3"
            :class="item.currentUser ? 'bg-#fffbeb border-amber-200' : 'bg-white border-#e5e7eb'"
          >
            <div class="flex items-center gap-3 min-w-0">
              <div class="w-8 h-8 rounded-full bg-#111111 text-white text-12px flex items-center justify-center">{{ item.rank }}</div>
              <a-avatar :src="item.userAvatar || undefined" />
              <div class="min-w-0">
                <div class="text-14px font-700 text-#111827 truncate">
                  {{ item.userName }}
                  <span v-if="item.currentUser" class="text-12px text-amber-700 ml-1">(我)</span>
                </div>
                <div class="text-12px text-#6b7280">已解锁 {{ item.earnedCount }}/{{ item.totalAchievements }} · 最近解锁 {{ formatDate(item.latestEarnedAt) || '暂无' }}</div>
              </div>
            </div>
            <div class="text-right">
              <div class="text-14px font-700 text-#111827">{{ item.completionRate }}%</div>
              <div class="text-12px text-#6b7280">解锁率</div>
            </div>
          </div>
        </div>
      </a-spin>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getAchievementRanking } from '../../api/statistics'
import type { AchievementRankingItem } from '../../types'

const router = useRouter()
const loading = ref(false)
const limit = ref(20)
const rows = ref<AchievementRankingItem[]>([])

const formatDate = (v: string | null) => {
  if (!v) return ''
  return String(v).replace('T', ' ').slice(0, 16)
}

const fetchRanking = async () => {
  loading.value = true
  try {
    const res = await getAchievementRanking(limit.value)
    rows.value = res.data || []
  } finally {
    loading.value = false
  }
}

const goAchievements = () => {
  router.push('/achievements')
}

onMounted(fetchRanking)
</script>
