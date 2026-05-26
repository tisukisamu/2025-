<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-gradient-to-r from-#111111 to-#374151 text-white p-6">
      <div class="flex flex-col lg:flex-row lg:items-start justify-between gap-4">
        <div>
          <h1 class="text-22px m-0 font-700">成就系统</h1>
          <p class="m-0 mt-2 text-white/70">把坚持变成可见的里程碑：完成打卡、连击、习惯数都会解锁徽章</p>
        </div>
        <div class="w-full lg:max-w-720px flex flex-col gap-3">
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-[220px_170px_160px_auto] gap-2">
            <a-input-search
              v-model:value="keyword"
              allow-clear
              placeholder="搜索成就名称"
              class="w-full"
            />
            <a-select
              v-model:value="conditionType"
              allow-clear
              placeholder="成就类型"
              class="w-full"
              @change="fetchData"
            >
              <a-select-option value="TOTAL_CHECKS">打卡总数</a-select-option>
              <a-select-option value="MAX_STREAK">最长连击</a-select-option>
              <a-select-option value="TOTAL_HABITS">习惯数量</a-select-option>
            </a-select>
            <a-select
              v-model:value="sortBy"
              class="w-full"
              @change="fetchData"
            >
              <a-select-option value="DEFAULT">默认排序</a-select-option>
              <a-select-option value="PROGRESS_DESC">进度优先</a-select-option>
              <a-select-option value="LATEST_EARNED">最近解锁</a-select-option>
            </a-select>
            <div class="h-32px px-10px rounded-8px bg-white/6 flex items-center justify-between text-white/90 text-13px">
              <span>仅看已解锁</span>
              <a-switch v-model:checked="earnedOnly" @change="fetchData" />
            </div>
          </div>
          <div class="flex flex-wrap lg:justify-end gap-2">
            <a-button class="bg-white/10 text-white border-white/20" :loading="loading" @click="fetchData">刷新</a-button>
            <a-button class="bg-white/10 text-white border-white/20" @click="clearFilters">重置筛选</a-button>
            <a-button class="bg-white/10 text-white border-white/20" @click="goRanking">进入排行榜</a-button>
            <a-button type="primary" class="bg-black border-black text-white" :loading="evaluating" @click="evaluate">重新计算</a-button>
          </div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
        <div class="text-12px text-#6b7280">成就总数</div>
        <div class="text-24px font-700 mt-2">{{ summary.total }}</div>
      </a-card>
      <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
        <div class="text-12px text-#6b7280">已解锁</div>
        <div class="text-24px font-700 mt-2 text-#d97706">{{ summary.earned }}</div>
      </a-card>
      <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
        <div class="text-12px text-#6b7280">进行中</div>
        <div class="text-24px font-700 mt-2">{{ summary.inProgress }}</div>
      </a-card>
      <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
        <div class="text-12px text-#6b7280">解锁率</div>
        <div class="text-24px font-700 mt-2">{{ summary.completionRate }}%</div>
      </a-card>
    </div>

    <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
      <template #title>成就多人排行榜预览</template>
      <a-empty v-if="rankingRows.length === 0" description="暂无已解锁成就的用户" />
      <div v-else class="space-y-2">
        <div
          v-for="item in rankingRows.slice(0, 5)"
          :key="item.userId"
          class="rounded-lg border border-#e5e7eb px-3 py-2 flex items-center justify-between gap-3"
          :class="item.currentUser ? 'bg-#fffbeb border-amber-200' : 'bg-white'"
        >
          <div class="flex items-center gap-3 min-w-0">
            <div class="w-7 h-7 rounded-full bg-#111111 text-white text-12px flex items-center justify-center">{{ item.rank }}</div>
            <a-avatar :src="item.userAvatar || undefined" size="small" />
            <div class="min-w-0">
              <div class="text-13px font-700 text-#111827 truncate">{{ item.userName }}</div>
              <div class="text-12px text-#6b7280">已解锁 {{ item.earnedCount }}/{{ item.totalAchievements }}</div>
            </div>
          </div>
          <div class="text-12px text-#6b7280">{{ item.completionRate }}%</div>
        </div>
      </div>
    </a-card>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <div class="mb-4 flex items-center justify-between gap-3">
        <div class="text-13px text-#6b7280">当前显示 {{ filteredRows.length }} 项成就</div>
        <a-space>
          <a-tag v-if="keyword">关键词：{{ keyword }}</a-tag>
          <a-tag v-if="conditionType">类型：{{ conditionTextMap[conditionType] }}</a-tag>
          <a-tag v-if="earnedOnly">仅已解锁</a-tag>
        </a-space>
      </div>
      <a-spin :spinning="loading">
        <a-empty v-if="filteredRows.length === 0" description="暂无成就" />
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div
            v-for="a in filteredRows"
            :key="a.id"
            class="p-4 rounded-xl border transition-all"
            :class="a.earned ? 'border-yellow-200 bg-yellow-50' : 'border-#e5e7eb bg-white'"
          >
            <div class="flex items-start justify-between gap-4">
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 rounded-xl flex items-center justify-center text-22px" :class="a.earned ? 'bg-yellow-100 text-yellow-700' : 'bg-#f3f4f6 text-#9ca3af'">
                  {{ (a.icon || '🏅').slice(0, 2) }}
                </div>
                <div>
                  <div class="font-700 text-#111827">{{ a.name }}</div>
                  <div class="text-12px text-#6b7280 mt-1">{{ a.description || '' }}</div>
                </div>
              </div>
              <a-tag v-if="a.earned" color="gold">已解锁</a-tag>
              <a-tag v-else>进行中</a-tag>
            </div>

            <div class="mt-4">
              <div class="flex items-center justify-between text-12px text-#6b7280">
                <span>{{ a.currentValue }}/{{ a.threshold }}</span>
                <span>{{ a.progressPercent }}%</span>
              </div>
              <a-progress :percent="Number(a.progressPercent || 0)" :show-info="false" :stroke-color="a.earned ? '#d97706' : '#111111'" />
              <div v-if="a.earnedAt" class="mt-2 text-12px text-#9ca3af">解锁时间：{{ formatDate(a.earnedAt) }}</div>
            </div>
          </div>
        </div>
      </a-spin>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { evaluateAchievements, getAchievementRanking, getAchievementSummary, getMyAchievements } from '../../api/statistics'
import type { AchievementProgress, AchievementRankingItem, AchievementSummary } from '../../types'

const router = useRouter()
const loading = ref(false)
const evaluating = ref(false)
const rows = ref<AchievementProgress[]>([])
const rankingRows = ref<AchievementRankingItem[]>([])
const keyword = ref('')
const earnedOnly = ref(false)
const conditionType = ref<'TOTAL_CHECKS' | 'MAX_STREAK' | 'TOTAL_HABITS' | undefined>(undefined)
const sortBy = ref<'DEFAULT' | 'PROGRESS_DESC' | 'LATEST_EARNED'>('DEFAULT')
const summary = ref<AchievementSummary>({
  total: 0,
  earned: 0,
  inProgress: 0,
  completionRate: 0
})
const conditionTextMap = {
  TOTAL_CHECKS: '打卡总数',
  MAX_STREAK: '最长连击',
  TOTAL_HABITS: '习惯数量'
}
const filteredRows = computed(() => {
  const k = keyword.value.trim().toLowerCase()
  return rows.value.filter((item) => {
    if (!k) return true
    return item.name.toLowerCase().includes(k) || String(item.description || '').toLowerCase().includes(k)
  })
})

const formatDate = (v: string) => {
  if (!v) return ''
  return v.replace('T', ' ').slice(0, 19)
}

const fetchData = async () => {
  loading.value = true
  try {
    const [res, sum, ranking] = await Promise.all([
      getMyAchievements({
        earnedOnly: earnedOnly.value,
        conditionType: conditionType.value,
        sortBy: sortBy.value
      }),
      getAchievementSummary(),
      getAchievementRanking(20)
    ])
    rows.value = res.data || []
    summary.value = sum.data || summary.value
    rankingRows.value = ranking.data || []
  } finally {
    loading.value = false
  }
}

const evaluate = async () => {
  evaluating.value = true
  try {
    await evaluateAchievements()
    await fetchData()
    message.success('已更新成就进度')
  } finally {
    evaluating.value = false
  }
}

const clearFilters = async () => {
  keyword.value = ''
  earnedOnly.value = false
  conditionType.value = undefined
  sortBy.value = 'DEFAULT'
  await fetchData()
}

const goRanking = () => {
  router.push('/achievements/ranking')
}

onMounted(fetchData)
</script>
