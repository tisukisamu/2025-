<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-#111111 text-white p-5 flex items-center justify-between">
      <div>
        <h1 class="text-22px m-0">统计看板</h1>
        <p class="m-0 mt-2 text-#9ca3af">查看整体完成率、连击与近 14 天趋势</p>
      </div>
      <a-space>
        <a-button class="bg-white/10 text-white border-white/20" :loading="loading" @click="fetchData">刷新数据</a-button>
        <a-button @click="router.push('/statistics/heatmap')">热力图</a-button>
        <a-button type="primary" class="bg-black border-black text-white" @click="router.push('/statistics/weekly')">周报分析</a-button>
      </a-space>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <a-card v-for="item in overviewCards" :key="item.label" :bordered="false" class="border border-#e5e7eb rounded-xl">
        <div class="text-#6b7280 text-13px">{{ item.label }}</div>
        <div class="text-28px font-700 text-#111827 mt-2">{{ item.value }}</div>
      </a-card>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <template #title>功能总览</template>
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="p-4 rounded-xl border border-#e5e7eb bg-white">
          <div class="text-12px text-#6b7280">模板库数量</div>
          <div class="text-24px font-700 mt-2">{{ moduleStats.templateCount }}</div>
          <a-button type="link" class="p-0 mt-2" @click="router.push('/templates/habits')">去模板中心</a-button>
        </div>
        <div class="p-4 rounded-xl border border-#e5e7eb bg-white">
          <div class="text-12px text-#6b7280">今日提醒待处理</div>
          <div class="text-24px font-700 mt-2">{{ moduleStats.reminderPending }}</div>
          <div class="text-12px text-#9ca3af mt-1">总计 {{ moduleStats.reminderTotal }} 条</div>
        </div>
        <div class="p-4 rounded-xl border border-#e5e7eb bg-white">
          <div class="text-12px text-#6b7280">已解锁成就</div>
          <div class="text-24px font-700 mt-2">{{ moduleStats.achievementEarned }}</div>
          <div class="text-12px text-#9ca3af mt-1">共 {{ moduleStats.achievementTotal }} 项</div>
        </div>
        <div class="p-4 rounded-xl border border-#e5e7eb bg-white">
          <div class="text-12px text-#6b7280">成就解锁率</div>
          <div class="text-24px font-700 mt-2">{{ moduleStats.achievementRate }}%</div>
          <a-button type="link" class="p-0 mt-2" @click="router.push('/achievements')">查看成就详情</a-button>
        </div>
      </div>
    </a-card>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <template #title>近 14 天完成趋势</template>
      <div class="space-y-3">
        <div v-for="item in trendRows" :key="item.date" class="grid grid-cols-[120px_1fr_52px] gap-3 items-center">
          <div class="text-#6b7280 text-12px">{{ item.date }}</div>
          <div class="h-3 rounded bg-#f3f4f6 overflow-hidden">
            <div class="h-3 bg-#111111" :style="{ width: `${Math.min(item.completeRate, 100)}%` }"></div>
          </div>
          <div class="text-right text-12px text-#374151">{{ item.completeRate }}%</div>
        </div>
      </div>
    </a-card>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <template #title>打卡激励排行榜</template>
      <a-empty v-if="rankingRows.length === 0" description="暂无排行榜数据" />
      <div v-else class="space-y-2">
        <div
          v-for="item in rankingRows"
          :key="item.userId"
          :class="[
            'rounded-xl border p-3 flex items-center justify-between gap-3',
            item.currentUser ? 'border-#111111 bg-#f9fafb' : 'border-#e5e7eb bg-white'
          ]"
        >
          <div class="flex items-center gap-3 min-w-0">
            <div class="w-7 h-7 rounded-full bg-#111111 text-white text-12px flex items-center justify-center">{{ item.rank }}</div>
            <a-avatar :src="item.userAvatar || undefined">
              {{ item.userName?.slice(0, 1) || 'U' }}
            </a-avatar>
            <div class="min-w-0">
              <div class="text-14px font-700 text-#111827 truncate">
                {{ item.userName }} <span v-if="item.currentUser" class="text-#6b7280 text-12px">(我)</span>
              </div>
              <div class="text-12px text-#6b7280">累计打卡 {{ item.totalCheckDays }} 天 · 最高连击 {{ item.maxStreak }} 天</div>
            </div>
          </div>
          <div class="text-right">
            <div class="text-13px font-700 text-#111827">{{ item.score }} 分</div>
            <div class="text-12px text-#6b7280">平均完成率 {{ item.avgCompleteRate }}%</div>
          </div>
        </div>
      </div>
    </a-card>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <template #title>习惯完成明细</template>
      <a-table :loading="loading" row-key="id" :data-source="statistics" :pagination="{ pageSize: 8, showSizeChanger: false }">
        <a-table-column title="习惯" data-index="habitName" key="habitName" />
        <a-table-column title="总天数" data-index="totalDays" key="totalDays" />
        <a-table-column title="当前连击" data-index="streakDays" key="streakDays" />
        <a-table-column title="最高连击" data-index="maxStreak" key="maxStreak" />
        <a-table-column title="完成率" key="completeRate">
          <template #default="{ record }">
            <div class="flex items-center gap-2">
              <a-progress :percent="record.completeRate" :show-info="false" stroke-color="#111111" />
              <span class="text-#374151 text-12px">{{ record.completeRate }}%</span>
            </div>
          </template>
        </a-table-column>
        <a-table-column title="详情" key="action" width="120">
          <template #default="{ record }">
            <a-button type="link" @click="router.push(`/habits/${record.habitId}`)">查看</a-button>
          </template>
        </a-table-column>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import dayjs from 'dayjs'
import { useRouter } from 'vue-router'
import { getTodayReminderSummary } from '../../api/check'
import { getHabitTemplatesWithFilter } from '../../api/habit'
import { getAchievementSummary, getAllStatistics, getCheckinRanking, getTrend } from '../../api/statistics'

const router = useRouter()
const loading = ref(false)
const statistics = ref([])
const trendRows = ref([])
const rankingRows = ref([])
const moduleStats = ref({
  templateCount: 0,
  reminderPending: 0,
  reminderTotal: 0,
  achievementEarned: 0,
  achievementTotal: 0,
  achievementRate: 0
})

const overviewCards = computed(() => {
  const totalHabits = statistics.value.length
  const avgRate = totalHabits
    ? Math.round(statistics.value.reduce((sum, item) => sum + (item.completeRate || 0), 0) / totalHabits)
    : 0
  const maxStreak = totalHabits
    ? Math.max(...statistics.value.map((item) => item.maxStreak || 0))
    : 0
  const activeStreak = totalHabits
    ? statistics.value.reduce((sum, item) => sum + (item.streakDays || 0), 0)
    : 0
  return [
    { label: '习惯总数', value: totalHabits },
    { label: '平均完成率', value: `${avgRate}%` },
    { label: '总连击天数', value: activeStreak },
    { label: '最高连击', value: maxStreak }
  ]
})

const fetchData = async () => {
  loading.value = true
  try {
    const end = dayjs()
    const start = end.subtract(13, 'day')
    const [statsRes, trendRes, reminderRes, templateRes, achievementRes, rankingRes] = await Promise.all([
      getAllStatistics(),
      getTrend(start.format('YYYY-MM-DD'), end.format('YYYY-MM-DD')),
      getTodayReminderSummary(),
      getHabitTemplatesWithFilter(),
      getAchievementSummary(),
      getCheckinRanking(10)
    ])
    statistics.value = statsRes.data || []
    trendRows.value = (trendRes.data?.dailyData || []).map((item) => ({
      ...item,
      date: dayjs(item.date).format('MM-DD')
    }))
    moduleStats.value = {
      templateCount: (templateRes.data || []).length,
      reminderPending: reminderRes.data?.pending || 0,
      reminderTotal: reminderRes.data?.total || 0,
      achievementEarned: achievementRes.data?.earned || 0,
      achievementTotal: achievementRes.data?.total || 0,
      achievementRate: achievementRes.data?.completionRate || 0
    }
    rankingRows.value = rankingRes.data || []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>
