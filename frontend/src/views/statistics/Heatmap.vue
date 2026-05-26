<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-gradient-to-r from-#111111 to-#374151 text-white p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-24px m-0 font-bold">热力图</h1>
          <p class="m-0 mt-2 text-white/70">可视化展示您的打卡活动</p>
        </div>
        <div class="flex items-center gap-2">
          <a-button @click="changeYear(-1)">
            <left-outlined />
          </a-button>
          <span class="text-20px font-bold px-4">{{ currentYear }}</span>
          <a-button @click="changeYear(1)">
            <right-outlined />
          </a-button>
        </div>
      </div>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <template #title>
        <span class="text-16px font-600">年度打卡热力图</span>
      </template>
      
      <a-spin :spinning="loading">
        <div class="heatmap-container">
          <div class="flex gap-1">
            <div class="w-10 text-12px text-#6b7280 text-right pr-2">
              <div v-for="month in months" :key="month" class="h-4 leading-4">{{ month }}</div>
            </div>
            <div class="flex-1 overflow-x-auto">
              <div class="flex flex-col gap-1">
                <div class="flex gap-1 mb-2">
                  <div v-for="day in weekDays" :key="day" class="w-4 text-center text-12px text-#6b7280">
                    {{ day }}
                  </div>
                </div>
                <div v-for="week in heatmapWeeks" :key="week.weekNum" class="flex gap-1">
                  <div
                    v-for="day in week.days"
                    :key="day.date"
                    class="w-4 h-4 rounded-sm cursor-pointer transition-all hover:ring-2 hover:ring-#111827"
                    :class="getLevelClass(day.level)"
                    :title="`${day.date} - ${day.count}次打卡`"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="flex items-center justify-end gap-2 mt-4 text-12px text-#6b7280">
          <span>少</span>
          <div class="w-4 h-4 rounded-sm bg-#ebedf0" />
          <div class="w-4 h-4 rounded-sm bg-#c6e48b" />
          <div class="w-4 h-4 rounded-sm bg-#7bc96f" />
          <div class="w-4 h-4 rounded-sm bg-#239a3b" />
          <div class="w-4 h-4 rounded-sm bg-#196127" />
          <span>多</span>
        </div>
      </a-spin>
    </a-card>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <a-card :bordered="false" class="border border-#e5e7eb rounded-xl text-center">
        <div class="text-36px font-bold text-#111827">{{ stats.totalDays }}</div>
        <div class="text-#6b7280 mt-2">总打卡天数</div>
      </a-card>
      <a-card :bordered="false" class="border border-#e5e7eb rounded-xl text-center">
        <div class="text-36px font-bold text-#22c55e">{{ stats.maxStreak }}</div>
        <div class="text-#6b7280 mt-2">最长连续天数</div>
      </a-card>
      <a-card :bordered="false" class="border border-#e5e7eb rounded-xl text-center">
        <div class="text-36px font-bold text-#f59e0b">{{ stats.currentStreak }}</div>
        <div class="text-#6b7280 mt-2">当前连续天数</div>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useUserStore } from '../../stores/user'
import { getHeatmap } from '../../api/statistics'
import { LeftOutlined, RightOutlined } from '@ant-design/icons-vue'

const userStore = useUserStore()
const loading = ref(false)
const currentYear = ref(new Date().getFullYear())
const heatmapData = ref<any[]>([])
const stats = ref({
  totalDays: 0,
  maxStreak: 0,
  currentStreak: 0
})

const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
const weekDays = ['日', '一', '二', '三', '四', '五', '六']

const heatmapWeeks = computed(() => {
  const weeks: any[] = []
  const year = currentYear.value
  const startDate = new Date(year, 0, 1)
  const endDate = new Date(year, 11, 31)
  
  let currentWeek: any = { weekNum: 0, days: [] }
  let weekNum = 0
  
  for (let d = new Date(startDate); d <= endDate; d.setDate(d.getDate() + 1)) {
    const dateStr = d.toISOString().split('T')[0]
    const dayData = heatmapData.value.find(h => h.date === dateStr) || { date: dateStr, count: 0, level: 0 }
    
    currentWeek.days.push(dayData)
    
    if (d.getDay() === 6 || d.getTime() === endDate.getTime()) {
      currentWeek.weekNum = weekNum++
      weeks.push({ ...currentWeek })
      currentWeek = { weekNum: 0, days: [] }
    }
  }
  
  return weeks
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getHeatmap(currentYear.value)
    if (res.code === 200) {
      heatmapData.value = res.data.days || []
      stats.value = {
        totalDays: res.data.days.filter((d: any) => d.count > 0).length,
        maxStreak: calculateMaxStreak(res.data.days),
        currentStreak: calculateCurrentStreak(res.data.days)
      }
    }
  } catch (error) {
    console.error('获取热力图数据失败', error)
  } finally {
    loading.value = false
  }
}

const calculateMaxStreak = (days: any[]) => {
  let maxStreak = 0
  let currentStreak = 0
  
  days.forEach(day => {
    if (day.count > 0) {
      currentStreak++
      maxStreak = Math.max(maxStreak, currentStreak)
    } else {
      currentStreak = 0
    }
  })
  
  return maxStreak
}

const calculateCurrentStreak = (days: any[]) => {
  const sortedDays = [...days].sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
  let streak = 0
  
  for (const day of sortedDays) {
    if (day.count > 0) {
      streak++
    } else {
      break
    }
  }
  
  return streak
}

const getLevelClass = (level: number) => {
  const classes = [
    'bg-#ebedf0',
    'bg-#c6e48b',
    'bg-#7bc96f',
    'bg-#239a3b',
    'bg-#196127'
  ]
  return classes[level] || classes[0]
}

const changeYear = (delta: number) => {
  currentYear.value += delta
}

watch(currentYear, () => {
  fetchData()
})

onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchData()
  }
})
</script>

<style scoped>
.heatmap-container {
  overflow-x: auto;
}
</style>
