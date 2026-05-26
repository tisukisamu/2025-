<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-semibold text-neutral-900">数据统计</h1>
      <a-space>
        <span class="text-neutral-500 text-sm">更新时间：{{ lastUpdated }}</span>
        <a-button @click="fetchData">刷新</a-button>
      </a-space>
    </div>

    <a-row :gutter="16">
      <a-col :span="8" v-for="item in cards" :key="item.key">
        <a-card :bordered="false" class="rounded-xl">
          <a-statistic :title="item.title" :value="item.value" />
          <div class="mt-3 text-xs text-neutral-500">
            <span>占比 {{ item.percent }}%</span>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16">
      <a-col :span="12">
        <a-card :bordered="false" class="rounded-xl" title="核心健康度">
          <div class="grid grid-cols-2 gap-4">
            <div class="metric-panel">
              <div class="text-neutral-500 text-xs">职位活跃率</div>
              <div class="metric-value">{{ activeRate }}%</div>
              <a-progress :percent="activeRate" :show-info="false" stroke-color="#2f4f75" />
            </div>
            <div class="metric-panel">
              <div class="text-neutral-500 text-xs">投递活跃度</div>
              <div class="metric-value">{{ applicationPerJob }}</div>
              <a-progress :percent="applicationHeatPercent" :show-info="false" stroke-color="#2f6a5a" />
            </div>
          </div>
          <a-divider class="!my-4" />
          <div class="space-y-2">
            <div v-for="item in insights" :key="item.title" class="insight-item">
              <div class="font-medium text-neutral-800">{{ item.title }}</div>
              <div class="text-neutral-500 text-xs">{{ item.desc }}</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card :bordered="false" class="rounded-xl" title="结构占比">
          <div class="space-y-4">
            <div v-for="item in composition" :key="item.key">
              <div class="flex items-center justify-between text-sm mb-2">
                <span class="text-neutral-700">{{ item.title }}</span>
                <span class="text-neutral-500">{{ item.value }}</span>
              </div>
              <a-progress :percent="item.percent" :show-info="false" :stroke-color="item.color" />
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-card :bordered="false" class="rounded-xl" title="关键指标对比">
      <a-table :columns="compareColumns" :data-source="compareRows" :pagination="false" size="small" :row-key="(r: any) => r.key">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'trend'">
            <a-tag :color="record.delta > 0 ? 'success' : record.delta < 0 ? 'warning' : 'default'">
              {{ record.deltaText }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { statisticsApi } from '@/api'

const overview = ref<Record<string, any>>({})
const adminStats = ref<Record<string, any>>({})
const lastUpdated = ref('-')

const toNumber = (value: any) => {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

const totalJobs = computed(() => toNumber(overview.value.totalJobs ?? adminStats.value.totalJobs))
const totalCompanies = computed(() => toNumber(overview.value.totalCompanies ?? adminStats.value.totalCompanies))
const totalUsers = computed(() => toNumber(overview.value.totalUsers ?? adminStats.value.totalUsers))
const totalApplications = computed(() => toNumber(overview.value.totalApplications ?? adminStats.value.totalApplications))
const activeJobs = computed(() => toNumber(overview.value.activeJobs ?? adminStats.value.activeJobs))

const totalVolume = computed(() => totalJobs.value + totalCompanies.value + totalUsers.value)
const activeRate = computed(() => totalJobs.value > 0 ? Number(((activeJobs.value / totalJobs.value) * 100).toFixed(1)) : 0)
const applicationPerJob = computed(() => totalJobs.value > 0 ? Number((totalApplications.value / totalJobs.value).toFixed(2)) : 0)
const applicationHeatPercent = computed(() => Math.min(100, Math.round(applicationPerJob.value * 20)))

const cards = computed(() => [
  {
    key: 'job',
    title: '职位总数',
    value: totalJobs.value,
    percent: totalVolume.value > 0 ? ((totalJobs.value / totalVolume.value) * 100).toFixed(1) : '0.0'
  },
  {
    key: 'company',
    title: '企业总数',
    value: totalCompanies.value,
    percent: totalVolume.value > 0 ? ((totalCompanies.value / totalVolume.value) * 100).toFixed(1) : '0.0'
  },
  {
    key: 'user',
    title: '用户总数',
    value: totalUsers.value,
    percent: totalVolume.value > 0 ? ((totalUsers.value / totalVolume.value) * 100).toFixed(1) : '0.0'
  }
])

const composition = computed(() => [
  {
    key: 'users',
    title: '用户规模',
    value: totalUsers.value,
    percent: totalVolume.value > 0 ? Math.round((totalUsers.value / totalVolume.value) * 100) : 0,
    color: '#2f4f75'
  },
  {
    key: 'companies',
    title: '企业规模',
    value: totalCompanies.value,
    percent: totalVolume.value > 0 ? Math.round((totalCompanies.value / totalVolume.value) * 100) : 0,
    color: '#4d5f7f'
  },
  {
    key: 'jobs',
    title: '职位规模',
    value: totalJobs.value,
    percent: totalVolume.value > 0 ? Math.round((totalJobs.value / totalVolume.value) * 100) : 0,
    color: '#2f6a5a'
  }
])

const insights = computed(() => [
  {
    title: `当前活跃职位 ${activeJobs.value} 个`,
    desc: `职位活跃率 ${activeRate.value}%`
  },
  {
    title: `累计投递 ${totalApplications.value} 份`,
    desc: `平均每个职位收到 ${applicationPerJob.value} 份投递`
  },
  {
    title: `企业用户总量 ${totalCompanies.value}`,
    desc: `当前平台用户规模 ${totalUsers.value}，供需持续增长`
  }
])

const compareColumns = [
  { title: '指标', dataIndex: 'name', key: 'name' },
  { title: '概览值', dataIndex: 'overview', key: 'overview' },
  { title: '管理端值', dataIndex: 'admin', key: 'admin' },
  { title: '趋势', dataIndex: 'trend', key: 'trend' }
]

const buildDeltaText = (delta: number) => {
  if (delta > 0) return `+${delta}`
  if (delta < 0) return `${delta}`
  return '持平'
}

const compareRows = computed(() => {
  const rows = [
    { key: 'users', name: '用户总数', overview: totalUsers.value, admin: toNumber(adminStats.value.totalUsers) },
    { key: 'companies', name: '企业总数', overview: totalCompanies.value, admin: toNumber(adminStats.value.totalCompanies) },
    { key: 'jobs', name: '职位总数', overview: totalJobs.value, admin: toNumber(adminStats.value.totalJobs) },
    { key: 'activeJobs', name: '活跃职位', overview: activeJobs.value, admin: toNumber(adminStats.value.activeJobs) },
    { key: 'applications', name: '投递总数', overview: totalApplications.value, admin: toNumber(adminStats.value.totalApplications) }
  ]
  return rows.map(item => {
    const delta = item.admin - item.overview
    return {
      ...item,
      delta,
      deltaText: buildDeltaText(delta)
    }
  })
})

const fetchData = async () => {
  try {
    const [overviewRes, adminRes] = await Promise.all([
      statisticsApi.getOverview(),
      statisticsApi.getAdminSystemStats()
    ])
    overview.value = overviewRes.data || {}
    adminStats.value = adminRes.data || {}
    lastUpdated.value = new Date().toLocaleString('zh-CN')
  } catch (error) {
    message.error('统计数据加载失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.metric-panel {
  background: #f8fafc;
  border: 1px solid #e9edf3;
  border-radius: 12px;
  padding: 12px;
}

.metric-value {
  color: #1f2a44;
  font-size: 26px;
  font-weight: 700;
  line-height: 1.2;
  margin: 6px 0 10px;
}

.insight-item {
  background: #f9fafb;
  border: 1px solid #edf0f3;
  border-radius: 10px;
  padding: 10px 12px;
}
</style>
