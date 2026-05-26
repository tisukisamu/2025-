<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-#111111 text-white p-5 flex items-center justify-between">
      <div>
        <h1 class="text-22px m-0">打卡日历</h1>
        <p class="m-0 mt-2 text-#9ca3af">按月查看完成率与每日打卡详情</p>
      </div>
      <a-space>
        <a-button @click="switchMonth(-1)">上个月</a-button>
        <a-button @click="switchMonth(1)">下个月</a-button>
      </a-space>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <div class="flex items-center justify-between mb-4">
        <h3 class="m-0 text-18px text-#111827">{{ currentMonthLabel }}</h3>
        <a-select v-model:value="selectedHabitId" style="width: 240px" allow-clear placeholder="全部习惯" @change="fetchCalendarData">
          <a-select-option :value="undefined">全部习惯</a-select-option>
          <a-select-option v-for="habit in habits" :key="habit.id" :value="habit.id">{{ habit.name }}</a-select-option>
        </a-select>
      </div>
      <div class="grid grid-cols-7 gap-2 text-center text-12px text-#9ca3af mb-3">
        <div v-for="label in weekLabels" :key="label">{{ label }}</div>
      </div>
      <div class="grid grid-cols-7 gap-2">
        <div
          v-for="item in calendarCells"
          :key="item.key"
          class="h-20 rounded-lg border cursor-pointer p-2 transition"
          :class="item.isCurrentMonth ? 'border-#e5e7eb bg-white hover:border-#9ca3af' : 'border-transparent bg-#f3f4f6 text-#c7c9cc'"
          @click="item.isCurrentMonth && selectDay(item)"
        >
          <div class="flex justify-between items-center">
            <span class="text-12px">{{ item.day }}</span>
            <span
              v-if="item.isCurrentMonth"
              class="text-10px px-1.5 py-0.5 rounded"
              :class="rateClass(item.completeRate)"
            >
              {{ item.completeRate }}%
            </span>
          </div>
          <div v-if="item.isCurrentMonth" class="text-11px mt-2 text-#6b7280">
            {{ item.completedHabits }}/{{ item.totalHabits }} 完成
          </div>
        </div>
      </div>
    </a-card>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <template #title>
        {{ selectedDay ? `${selectedDay.date} 详情` : '请选择日期查看详情' }}
      </template>
      <a-empty v-if="!selectedDay" description="点击上方日期查看当日打卡情况" />
      <div v-else class="space-y-3">
        <div class="text-#374151">当日完成率：{{ selectedDay.completeRate }}%</div>
        <a-list :data-source="selectedDay.habits" bordered>
          <template #renderItem="{ item }">
            <a-list-item>
              <div class="w-full flex items-center justify-between">
                <span>{{ item.habitName }}</span>
                <a-tag :color="item.checked ? 'success' : 'default'">{{ item.checked ? '已打卡' : '未打卡' }}</a-tag>
              </div>
            </a-list-item>
          </template>
        </a-list>
      </div>
    </a-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import dayjs from 'dayjs'
import { getCalendar } from '@/api/check'
import { getHabits } from '@/api/habit'

const weekLabels = ['一', '二', '三', '四', '五', '六', '日']
const habits = ref([])
const selectedHabitId = ref(undefined)
const currentMonth = ref(dayjs().format('YYYY-MM'))
const days = ref([])
const selectedDay = ref(null)

const currentMonthLabel = computed(() => dayjs(`${currentMonth.value}-01`).format('YYYY 年 MM 月'))

const calendarCells = computed(() => {
  const monthStart = dayjs(`${currentMonth.value}-01`)
  const firstWeekday = (monthStart.day() + 6) % 7
  const prefix = Array.from({ length: firstWeekday }).map((_, index) => ({
    key: `prefix-${index}`,
    day: '',
    isCurrentMonth: false,
    completeRate: 0,
    completedHabits: 0,
    totalHabits: 0
  }))
  const currentDays = days.value.map((item) => ({
    ...item,
    key: item.date,
    isCurrentMonth: true
  }))
  const totalCells = Math.ceil((prefix.length + currentDays.length) / 7) * 7
  const suffixCount = totalCells - prefix.length - currentDays.length
  const suffix = Array.from({ length: suffixCount }).map((_, index) => ({
    key: `suffix-${index}`,
    day: '',
    isCurrentMonth: false,
    completeRate: 0,
    completedHabits: 0,
    totalHabits: 0
  }))
  return [...prefix, ...currentDays, ...suffix]
})

const fetchHabits = async () => {
  const res = await getHabits()
  habits.value = res.data || []
}

const fetchCalendarData = async () => {
  const res = await getCalendar(currentMonth.value, selectedHabitId.value)
  days.value = res.data?.days || []
  selectedDay.value = null
}

const switchMonth = (step) => {
  currentMonth.value = dayjs(`${currentMonth.value}-01`).add(step, 'month').format('YYYY-MM')
  fetchCalendarData()
}

const selectDay = (day) => {
  selectedDay.value = day
}

const rateClass = (rate) => {
  if (rate >= 100) return 'bg-black text-white'
  if (rate >= 60) return 'bg-#374151 text-white'
  if (rate > 0) return 'bg-#9ca3af text-white'
  return 'bg-#e5e7eb text-#6b7280'
}

onMounted(async () => {
  await fetchHabits()
  await fetchCalendarData()
})
</script>
