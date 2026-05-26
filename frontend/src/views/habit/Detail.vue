<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-#111111 text-white p-5 flex items-center justify-between">
      <div>
        <h1 class="text-22px m-0">{{ habit?.name || '习惯详情' }}</h1>
        <p class="m-0 mt-2 text-#9ca3af">{{ habit?.description || '查看习惯规则、统计表现与最近记录' }}</p>
      </div>
      <a-space>
        <a-button @click="router.push('/habits')">返回列表</a-button>
        <a-button type="primary" class="bg-black border-black text-white" @click="router.push(`/habits/${habitId}/edit`)">编辑习惯</a-button>
      </a-space>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
        <div class="text-#6b7280 text-13px">完成率</div>
        <div class="text-28px font-700 text-#111827 mt-2">{{ statistics?.completeRate || 0 }}%</div>
      </a-card>
      <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
        <div class="text-#6b7280 text-13px">当前连击</div>
        <div class="text-28px font-700 text-#111827 mt-2">{{ statistics?.streakDays || 0 }}</div>
      </a-card>
      <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
        <div class="text-#6b7280 text-13px">最高连击</div>
        <div class="text-28px font-700 text-#111827 mt-2">{{ statistics?.maxStreak || 0 }}</div>
      </a-card>
      <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
        <div class="text-#6b7280 text-13px">总执行天数</div>
        <div class="text-28px font-700 text-#111827 mt-2">{{ statistics?.totalDays || 0 }}</div>
      </a-card>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl" title="基础信息">
      <a-descriptions :column="2" size="middle">
        <a-descriptions-item label="分类">{{ habit?.categoryName || '未分类' }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="habit?.status === 'ACTIVE' ? 'success' : 'default'">
            {{ habit?.status === 'ACTIVE' ? '进行中' : '已暂停' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="重复规则">{{ repeatText }}</a-descriptions-item>
        <a-descriptions-item label="提醒时间">{{ habit?.reminderTime || '未设置' }}</a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ formatDateTime(habit?.createdAt) }}</a-descriptions-item>
        <a-descriptions-item label="更新时间">{{ formatDateTime(habit?.updatedAt) }}</a-descriptions-item>
      </a-descriptions>
    </a-card>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl" title="最近打卡记录">
      <a-table :loading="loadingHistory" row-key="id" :data-source="historyList" :pagination="false">
        <a-table-column title="日期" data-index="checkDate" key="checkDate" />
        <a-table-column title="时间" data-index="checkTime" key="checkTime" />
        <a-table-column title="备注" key="note">
          <template #default="{ record }">{{ record.note || '-' }}</template>
        </a-table-column>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import dayjs from 'dayjs'
import { useRoute, useRouter } from 'vue-router'
import { getHabitById } from '../../api/habit'
import { getHabitStatistics } from '../../api/statistics'
import { getHistory } from '../../api/check'

const route = useRoute()
const router = useRouter()
const habitId = Number(route.params.id)
const habit = ref(null)
const statistics = ref(null)
const historyList = ref([])
const loadingHistory = ref(false)

const repeatText = computed(() => {
  if (!habit.value) return '-'
  if (habit.value.repeatType === 'DAILY') return '每天'
  if (!habit.value.repeatDays) return '每周'
  const labels = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return String(habit.value.repeatDays)
    .split(',')
    .map((d) => labels[Number(d)] || `周${d}`)
    .join('、')
})

const formatDateTime = (value) => {
  if (!value) return '-'
  return dayjs(value).format('YYYY-MM-DD HH:mm')
}

const fetchDetail = async () => {
  const res = await getHabitById(habitId)
  habit.value = res.data
}

const fetchStatistics = async () => {
  const res = await getHabitStatistics(habitId)
  statistics.value = res.data
}

const fetchHistory = async () => {
  loadingHistory.value = true
  try {
    const res = await getHistory({ habitId, page: 0, size: 10 })
    historyList.value = res.data || []
  } finally {
    loadingHistory.value = false
  }
}

onMounted(async () => {
  await Promise.all([fetchDetail(), fetchStatistics(), fetchHistory()])
})
</script>
