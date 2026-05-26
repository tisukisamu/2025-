<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-gradient-to-r from-#111111 to-#374151 text-white p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-24px m-0 font-bold">欢迎回来，{{ userStore.userInfo?.name || '用户' }}</h1>
          <p class="m-0 mt-2 text-white/70">今天是 {{ todayDate }}，让我们开始今天的习惯打卡吧</p>
        </div>
        <div class="text-right">
          <div class="text-36px font-bold">{{ overview.completedHabits }}/{{ overview.totalHabits }}</div>
          <div class="text-white/60 text-sm">今日完成</div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <a-card v-for="item in statsCards" :key="item.key" :bordered="false" class="border border-#e5e7eb rounded-xl hover:shadow-md transition-shadow">
        <div class="flex items-center gap-3">
          <div class="w-12 h-12 rounded-xl flex items-center justify-center" :class="item.bgClass">
            <component :is="item.icon" class="text-xl" :class="item.iconClass" />
          </div>
          <div>
            <div class="text-#6b7280 text-sm">{{ item.label }}</div>
            <div class="text-24px font-bold text-#111827">{{ item.value }}</div>
          </div>
        </div>
      </a-card>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <template #title>
        <div class="flex items-center justify-between">
          <span class="text-16px font-600">今日待打卡习惯</span>
          <a-button type="link" @click="router.push('/habits')">管理习惯</a-button>
        </div>
      </template>
      
      <a-spin :spinning="loading">
        <a-empty v-if="todayHabits.length === 0" description="暂无待打卡习惯">
          <template #image>
            <check-square-outlined class="text-48px text-#d1d5db" />
          </template>
          <a-button type="primary" class="bg-black border-black text-white" @click="router.push('/habits/create')">创建第一个习惯</a-button>
        </a-empty>
        
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div
            v-for="habit in todayHabits"
            :key="habit.id"
            class="p-4 rounded-xl border transition-all cursor-pointer hover:shadow-md"
            :class="habit.checked ? 'border-green-300 bg-green-50' : 'border-#e5e7eb bg-white'"
            @click="handleCheckIn(habit)"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-full flex items-center justify-center text-white text-lg" :style="{ backgroundColor: habit.color || '#111111' }">
                {{ (habit.icon || '✓').slice(0, 1) }}
              </div>
              <div class="flex-1 min-w-0">
                <div class="font-600 text-#111827 truncate">{{ habit.name }}</div>
                <div class="text-12px text-#9ca3af mt-1">
                  <fire-outlined class="mr-1" />
                  连续 {{ habit.streakDays }} 天
                </div>
              </div>
              <div>
                <a-tag v-if="habit.checked" color="success">已打卡</a-tag>
                <a-tag v-else>待打卡</a-tag>
              </div>
            </div>
          </div>
        </div>
      </a-spin>
    </a-card>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
        <template #title>
          <div class="flex items-center gap-2">
            <calendar-outlined class="text-#111111" />
            <span>本周概览</span>
          </div>
        </template>
        <div class="flex justify-between items-end h-32">
          <div v-for="(day, index) in weekDays" :key="index" class="flex flex-col items-center gap-2">
            <div class="w-8 h-8 rounded flex items-center justify-center text-xs" :class="day.isToday ? 'bg-black text-white' : 'bg-#f3f4f6 text-#6b7280'">
              {{ day.label }}
            </div>
            <div class="w-6 rounded-sm transition-all" :class="day.checked ? 'bg-green-500' : 'bg-#e5e7eb'" :style="{ height: day.checked ? '24px' : '8px' }" />
          </div>
        </div>
      </a-card>

      <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
        <template #title>
          <div class="flex items-center gap-2">
            <trophy-outlined class="text-#111111" />
            <span>成就徽章</span>
          </div>
        </template>
        <div class="flex gap-4">
          <div v-for="badge in badges" :key="badge.name" class="flex flex-col items-center">
            <div class="w-12 h-12 rounded-full flex items-center justify-center text-xl" :class="badge.earned ? 'bg-yellow-100 text-yellow-600' : 'bg-#f3f4f6 text-#d1d5db'">
              {{ badge.icon }}
            </div>
            <div class="text-11px text-#6b7280 mt-1">{{ badge.name }}</div>
          </div>
        </div>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getTodayOverview, checkIn as checkInApi } from '../api/check'
import { getMyAchievements } from '../api/statistics'
import {
  CheckSquareOutlined,
  FireOutlined,
  CalendarOutlined,
  TrophyOutlined,
  RocketOutlined,
  ThunderboltOutlined,
  CrownOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const overview = ref({
  totalHabits: 0,
  completedHabits: 0,
  pendingHabits: 0,
  completeRate: 0,
  habits: []
})

const todayDate = computed(() => {
  const now = new Date()
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 ${weekDays[now.getDay()]}`
})

const todayHabits = computed(() => overview.value.habits || [])

const statsCards = computed(() => [
  {
    key: 'total',
    label: '习惯总数',
    value: overview.value.totalHabits,
    icon: CheckSquareOutlined,
    bgClass: 'bg-blue-100',
    iconClass: 'text-blue-600'
  },
  {
    key: 'completed',
    label: '今日完成',
    value: overview.value.completedHabits,
    icon: RocketOutlined,
    bgClass: 'bg-green-100',
    iconClass: 'text-green-600'
  },
  {
    key: 'rate',
    label: '完成率',
    value: `${overview.value.completeRate}%`,
    icon: ThunderboltOutlined,
    bgClass: 'bg-orange-100',
    iconClass: 'text-orange-600'
  },
  {
    key: 'streak',
    label: '总连击',
    value: totalStreak.value,
    icon: FireOutlined,
    bgClass: 'bg-red-100',
    iconClass: 'text-red-600'
  }
])

const totalStreak = computed(() => {
  return (overview.value.habits || []).reduce((sum, h) => sum + (h.streakDays || 0), 0)
})

const weekDays = computed(() => {
  const now = new Date()
  const day = now.getDay()
  const labels = ['日', '一', '二', '三', '四', '五', '六']
  const days = []
  for (let i = 0; i < 7; i++) {
    days.push({
      label: labels[i],
      isToday: i === day,
      checked: Math.random() > 0.3
    })
  }
  return days
})

const badges = ref([
  { name: '初次打卡', icon: '🎉', earned: false },
  { name: '连击 7 天', icon: '🔥', earned: false },
  { name: '坚持一周', icon: '✅', earned: false },
  { name: '三件习惯', icon: '🧩', earned: false }
])

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTodayOverview()
    if (res.code === 200) {
      overview.value = res.data
    }

    const ach = await getMyAchievements()
    const list = (ach.data || [])
      .slice()
      .sort((a, b) => Number(b.earned) - Number(a.earned))
      .slice(0, 4)
    if (list.length > 0) {
      badges.value = list.map((x) => ({
        name: x.name,
        icon: (x.icon || '🏅').slice(0, 2),
        earned: Boolean(x.earned)
      }))
    }
  } finally {
    loading.value = false
  }
}

const handleCheckIn = async (habit: any) => {
  if (habit.checked) return
  try {
    await checkInApi({ habitId: habit.id })
    habit.checked = true
    overview.value.completedHabits++
  } catch (error) {
    console.error('打卡失败', error)
  }
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchData()
  }
})
</script>
