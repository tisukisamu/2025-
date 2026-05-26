<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-gradient-to-r from-#111111 to-#374151 text-white p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-22px m-0 font-700">提醒中心</h1>
          <p class="m-0 mt-2 text-white/70">展示今天需要提醒的习惯，按提醒时间排序</p>
        </div>
        <a-space>
          <div class="flex items-center gap-2 text-white/90 text-13px">
            <span>仅看待打卡</span>
            <a-switch v-model:checked="onlyPending" @change="fetchData" />
          </div>
          <a-button class="bg-white/10 text-white border-white/20" :loading="loading" @click="fetchData">刷新</a-button>
        </a-space>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
        <div class="text-12px text-#6b7280">提醒总数</div>
        <div class="text-24px font-700 mt-2">{{ summary.total }}</div>
      </a-card>
      <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
        <div class="text-12px text-#6b7280">待打卡</div>
        <div class="text-24px font-700 mt-2 text-#111111">{{ summary.pending }}</div>
      </a-card>
      <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
        <div class="text-12px text-#6b7280">已完成</div>
        <div class="text-24px font-700 mt-2 text-#059669">{{ summary.completed }}</div>
      </a-card>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <div class="mb-4 grid grid-cols-1 md:grid-cols-[1fr_160px_auto] gap-3">
        <a-input-search
          v-model:value="keyword"
          allow-clear
          placeholder="搜索提醒习惯"
        />
        <a-select v-model:value="statusFilter">
          <a-select-option value="ALL">全部状态</a-select-option>
          <a-select-option value="PENDING">待打卡</a-select-option>
          <a-select-option value="DONE">已打卡</a-select-option>
        </a-select>
        <a-button @click="router.push('/today')">去今日打卡</a-button>
      </div>
      <a-spin :spinning="loading">
        <a-empty v-if="filteredRows.length === 0" description="当前筛选下没有提醒习惯">
          <a-button type="primary" class="bg-black border-black text-white" @click="goHabits">去设置提醒</a-button>
        </a-empty>

        <a-list v-else :data-source="filteredRows" :split="false">
          <template #renderItem="{ item }">
            <a-list-item class="rounded-xl border border-#e5e7eb p-4 mb-3 bg-white">
              <div class="w-full flex items-center justify-between gap-4">
                <div class="flex items-center gap-3 min-w-0">
                  <div class="w-10 h-10 rounded-full flex items-center justify-center text-white text-lg" :style="{ backgroundColor: item.color || '#111111' }">
                    {{ (item.icon || '✓').slice(0, 1) }}
                  </div>
                  <div class="min-w-0">
                    <div class="font-700 text-#111827 truncate">{{ item.habitName }}</div>
                    <div class="text-12px text-#6b7280 mt-1">提醒时间：{{ item.reminderTime.slice(0, 5) }}</div>
                  </div>
                </div>

                <div class="shrink-0">
                  <a-tag v-if="item.checked" color="success">已打卡</a-tag>
                  <a-tag v-else>待打卡</a-tag>
                </div>
              </div>
            </a-list-item>
          </template>
        </a-list>
      </a-spin>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getTodayReminderSummary, getTodayReminders } from '../../api/check'
import type { ReminderItem, ReminderSummary } from '../../types'

const router = useRouter()
const loading = ref(false)
const rows = ref<ReminderItem[]>([])
const onlyPending = ref(false)
const keyword = ref('')
const statusFilter = ref<'ALL' | 'PENDING' | 'DONE'>('ALL')
const summary = ref<ReminderSummary>({
  total: 0,
  completed: 0,
  pending: 0
})
const filteredRows = computed(() => {
  return rows.value.filter((item) => {
    const k = keyword.value.trim().toLowerCase()
    const matchKeyword = !k || item.habitName.toLowerCase().includes(k)
    const matchStatus = statusFilter.value === 'ALL'
      || (statusFilter.value === 'PENDING' && !item.checked)
      || (statusFilter.value === 'DONE' && item.checked)
    return matchKeyword && matchStatus
  })
})

const goHabits = () => router.push('/habits')

const fetchData = async () => {
  loading.value = true
  try {
    const [res, sum] = await Promise.all([
      getTodayReminders(onlyPending.value),
      getTodayReminderSummary()
    ])
    rows.value = res.data || []
    summary.value = sum.data || summary.value
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>
