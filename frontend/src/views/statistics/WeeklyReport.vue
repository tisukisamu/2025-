<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-gradient-to-r from-#111111 to-#374151 text-white p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-24px m-0 font-bold">周报分析</h1>
          <p class="m-0 mt-2 text-white/70">{{ weekRange }}</p>
        </div>
        <div class="flex items-center gap-2">
          <a-button @click="changeWeek(-1)">
            <left-outlined />
          </a-button>
          <a-button type="primary" class="bg-white! text-#111!" @click="changeWeek(0)">本周</a-button>
          <a-button @click="changeWeek(1)">
            <right-outlined />
          </a-button>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <a-card v-for="item in statsCards" :key="item.key" :bordered="false" class="border border-#e5e7eb rounded-xl">
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
        <span class="text-16px font-600">每日完成趋势</span>
      </template>
      
      <div class="h-64 flex items-end gap-2">
        <div v-for="(day, index) in weekData" :key="index" class="flex-1 flex flex-col items-center gap-2">
          <div class="text-12px text-#6b7280">{{ day.rate }}%</div>
          <div 
            class="w-full rounded-t-lg transition-all"
            :class="day.isToday ? 'bg-#111111' : 'bg-#d1d5db'"
            :style="{ height: `${day.rate}%` }"
          />
          <div class="text-12px text-#6b7280">{{ day.label }}</div>
        </div>
      </div>
    </a-card>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
        <template #title>
          <span class="text-16px font-600">习惯完成排行</span>
        </template>
        
        <div class="space-y-3">
          <div v-for="(habit, index) in habitRanking" :key="habit.id" class="flex items-center gap-3">
            <div class="w-6 h-6 rounded-full flex items-center justify-center text-12px font-bold" :class="index < 3 ? 'bg-#111111 text-white' : 'bg-#f3f4f6 text-#6b7280'">
              {{ index + 1 }}
            </div>
            <div class="flex-1">
              <div class="flex items-center justify-between mb-1">
                <span class="font-500 text-#111827">{{ habit.name }}</span>
                <span class="text-#6b7280 text-sm">{{ habit.completed }}/{{ habit.total }}</span>
              </div>
              <a-progress :percent="habit.rate" :show-info="false" stroke-color="#111111" />
            </div>
          </div>
        </div>
      </a-card>

      <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
        <template #title>
          <span class="text-16px font-600">本周总结</span>
        </template>
        
        <div class="space-y-4">
          <div class="p-4 rounded-xl bg-#f9fafb">
            <div class="flex items-center gap-2 mb-2">
              <trophy-outlined class="text-yellow-500" />
              <span class="font-600 text-#111827">最佳表现</span>
            </div>
            <div class="text-#6b7280 text-sm">{{ bestDay }}</div>
          </div>
          
          <div class="p-4 rounded-xl bg-#f9fafb">
            <div class="flex items-center gap-2 mb-2">
              <fire-outlined class="text-orange-500" />
              <span class="font-600 text-#111827">连续打卡</span>
            </div>
            <div class="text-#6b7280 text-sm">当前连续 {{ currentStreak }} 天</div>
          </div>
          
          <div class="p-4 rounded-xl bg-#f9fafb">
            <div class="flex items-center gap-2 mb-2">
              <rise-outlined class="text-green-500" />
              <span class="font-600 text-#111827">进步建议</span>
            </div>
            <div class="text-#6b7280 text-sm">{{ suggestion }}</div>
          </div>
        </div>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useUserStore } from '../../stores/user'
import { getTrend, getAllStatistics } from '../../api/statistics'
import {
  LeftOutlined,
  RightOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  RiseOutlined,
  TrophyOutlined,
  FireOutlined
} from '@ant-design/icons-vue'

const userStore = useUserStore()
const loading = ref(false)
const weekOffset = ref(0)
const trendData = ref<any[]>([])
const statisticsData = ref<any[]>([])

const weekRange = computed(() => {
  const now = new Date()
  const startOfWeek = new Date(now)
  startOfWeek.setDate(now.getDate() - now.getDay() + 1 + (weekOffset.value * 7))
  const endOfWeek = new Date(startOfWeek)
  endOfWeek.setDate(startOfWeek.getDate() + 6)
  
  return `${startOfWeek.getMonth() + 1}月${startOfWeek.getDate()}日 - ${endOfWeek.getMonth() + 1}月${endOfWeek.getDate()}日`
})

const weekData = computed(() => {
  const labels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const today = new Date().getDay()
  
  return labels.map((label, index) => {
    const dayData = trendData.value[index] || { completedHabits: 0, totalHabits: 0 }
    const rate = dayData.totalHabits > 0 ? Math.round((dayData.completedHabits / dayData.totalHabits) * 100) : 0
    
    return {
      label,
      rate,
      completed: dayData.completedHabits,
      total: dayData.totalHabits,
      isToday: index === (today === 0 ? 6 : today - 1)
    }
  })
})

const statsCards = computed(() => {
  const totalCompleted = weekData.value.reduce((sum, d) => sum + d.completed, 0)
  const totalTasks = weekData.value.reduce((sum, d) => sum + d.total, 0)
  const avgRate = totalTasks > 0 ? Math.round((totalCompleted / totalTasks) * 100) : 0
  
  return [
    {
      key: 'total',
      label: '本周总打卡',
      value: totalCompleted,
      icon: CheckCircleOutlined,
      bgClass: 'bg-blue-100',
      iconClass: 'text-blue-600'
    },
    {
      key: 'tasks',
      label: '总任务数',
      value: totalTasks,
      icon: ClockCircleOutlined,
      bgClass: 'bg-orange-100',
      iconClass: 'text-orange-600'
    },
    {
      key: 'rate',
      label: '平均完成率',
      value: `${avgRate}%`,
      icon: RiseOutlined,
      bgClass: 'bg-green-100',
      iconClass: 'text-green-600'
    },
    {
      key: 'streak',
      label: '连续天数',
      value: currentStreak.value,
      icon: FireOutlined,
      bgClass: 'bg-red-100',
      iconClass: 'text-red-600'
    }
  ]
})

const habitRanking = computed(() => {
  return statisticsData.value
    .map(s => ({
      id: s.habitId,
      name: s.habitName,
      completed: s.totalDays,
      total: 7,
      rate: Math.round(s.completeRate)
    }))
    .sort((a, b) => b.rate - a.rate)
    .slice(0, 5)
})

const bestDay = computed(() => {
  const best = weekData.value.reduce((max, day) => day.rate > max.rate ? day : max, weekData.value[0])
  return best ? `${best.label} (${best.rate}%完成率)` : '-'
})

const currentStreak = ref(0)

const suggestion = computed(() => {
  const avgRate = weekData.value.reduce((sum, d) => sum + d.rate, 0) / 7
  if (avgRate >= 80) return '表现优秀！继续保持！'
  if (avgRate >= 60) return '做得不错，可以尝试提高周末的完成率'
  return '加油！建议从小目标开始，逐步提高完成率'
})

const fetchData = async () => {
  loading.value = true
  try {
    const now = new Date()
    const startOfWeek = new Date(now)
    startOfWeek.setDate(now.getDate() - now.getDay() + 1 + (weekOffset.value * 7))
    const endOfWeek = new Date(startOfWeek)
    endOfWeek.setDate(startOfWeek.getDate() + 6)
    
    const [trendRes, statsRes] = await Promise.all([
      getTrend(startOfWeek.toISOString().split('T')[0], endOfWeek.toISOString().split('T')[0]),
      getAllStatistics()
    ])
    
    if (trendRes.code === 200) {
      trendData.value = trendRes.data.dailyData || []
    }
    
    if (statsRes.code === 200) {
      statisticsData.value = statsRes.data || []
    }
  } finally {
    loading.value = false
  }
}

const changeWeek = (delta: number) => {
  if (delta === 0) {
    weekOffset.value = 0
  } else {
    weekOffset.value += delta
  }
}

watch(weekOffset, () => {
  fetchData()
})

onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchData()
  }
})
</script>
