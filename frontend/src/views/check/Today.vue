<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-gradient-to-r from-#111111 to-#374151 text-white p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-24px m-0 font-bold">今日打卡</h1>
          <p class="m-0 mt-2 text-white/70">{{ todayDate }}</p>
        </div>
        <div class="text-right">
          <div class="text-36px font-bold">{{ completedCount }}/{{ totalCount }}</div>
          <div class="text-white/60 text-sm">今日完成</div>
        </div>
      </div>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <template #title>
        <div class="flex items-center justify-between">
          <span class="text-16px font-600">待打卡习惯</span>
          <a-button type="primary" class="bg-black border-black text-white" @click="router.push('/habits/create')">
            <plus-outlined class="mr-1" />添加习惯
          </a-button>
        </div>
      </template>
      
      <a-spin :spinning="loading">
        <a-empty v-if="habits.length === 0" description="暂无习惯，快去创建吧">
          <a-button type="primary" class="bg-black border-black text-white" @click="router.push('/habits/create')">
            创建第一个习惯
          </a-button>
        </a-empty>
        
        <div v-else class="space-y-3">
          <div
            v-for="habit in habits"
            :key="habit.id"
            class="p-4 rounded-xl border transition-all hover:shadow-md"
            :class="habit.checked ? 'border-green-300 bg-green-50' : 'border-#e5e7eb bg-white'"
          >
            <div class="flex items-center gap-4">
              <div 
                class="w-12 h-12 rounded-xl flex items-center justify-center text-white text-xl"
                :style="{ backgroundColor: habit.color || '#111111' }"
              >
                {{ (habit.icon || '✓').slice(0, 1) }}
              </div>
              
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2">
                  <span class="font-600 text-#111827 text-lg">{{ habit.name }}</span>
                  <a-tag v-if="habit.categoryName" color="default">{{ habit.categoryName }}</a-tag>
                </div>
                <div class="flex items-center gap-4 mt-1 text-#6b7280 text-sm">
                  <span>
                    <fire-outlined class="mr-1 text-orange-500" />
                    连续 {{ habit.streakDays }} 天
                  </span>
                  <span v-if="habit.reminderTime">
                    <clock-circle-outlined class="mr-1" />
                    {{ habit.reminderTime }}
                  </span>
                </div>
              </div>
              
              <div class="flex items-center gap-2">
                <a-button
                  v-if="!habit.checked"
                  type="primary"
                  size="large"
                  class="bg-black border-black text-white"
                  @click="handleCheckIn(habit)"
                >
                  <check-outlined class="mr-1" />打卡
                </a-button>
                <a-tag v-else color="success" class="text-base px-3 py-1">
                  <check-outlined class="mr-1" />已打卡
                </a-tag>
              </div>
            </div>
          </div>
        </div>
      </a-spin>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getTodayOverview, checkIn } from '@/api/check'
import { useUserStore } from '@/stores/user'
import {
  PlusOutlined,
  CheckOutlined,
  FireOutlined,
  ClockCircleOutlined
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

const habits = computed(() => overview.value.habits || [])
const totalCount = computed(() => overview.value.totalHabits)
const completedCount = computed(() => overview.value.completedHabits)

const todayDate = computed(() => {
  const now = new Date()
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 ${weekDays[now.getDay()]}`
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTodayOverview()
    if (res.code === 200) {
      overview.value = res.data
    }
  } finally {
    loading.value = false
  }
}

const handleCheckIn = async (habit: any) => {
  try {
    await checkIn({ habitId: habit.id })
    habit.checked = true
    overview.value.completedHabits++
    message.success('打卡成功！')
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
