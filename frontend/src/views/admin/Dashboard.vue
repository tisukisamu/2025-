<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-semibold text-neutral-900">控制台</h1>
    
    <div class="grid grid-cols-4 gap-6">
      <div 
        v-for="stat in statistics" 
        :key="stat.title"
        class="bg-white rounded-xl p-6 border border-neutral-100"
      >
        <div class="flex items-center justify-between mb-4">
          <div 
            class="w-12 h-12 rounded-xl flex items-center justify-center"
            :class="stat.bgColor"
          >
            <component :is="stat.icon" class="text-xl" :class="stat.iconColor" />
          </div>
          <div 
            class="text-sm px-2 py-1 rounded-full"
            :class="stat.trendClass"
          >
            {{ stat.trend }}
          </div>
        </div>
        <div class="text-2xl font-bold text-neutral-900 mb-1">{{ stat.value }}</div>
        <div class="text-neutral-500 text-sm">{{ stat.title }}</div>
      </div>
    </div>
    
    <div class="grid grid-cols-3 gap-6">
      <div class="col-span-2 bg-white rounded-xl border border-neutral-100 p-6">
        <h3 class="font-semibold text-neutral-900 mb-4">招聘趋势</h3>
        <div class="h-64 flex items-end justify-between gap-2">
          <div 
            v-for="(item, index) in normalizedTrendData" 
            :key="index"
            class="flex-1 flex flex-col items-center"
          >
            <div 
              class="w-full bg-neutral-900 rounded-t transition-all hover:bg-neutral-700"
              :style="{ height: `${item.barHeight}px` }"
            />
            <span class="text-xs text-neutral-400 mt-2">{{ item.label }}</span>
          </div>
        </div>
      </div>
      
      <div class="bg-white rounded-xl border border-neutral-100 p-6">
        <h3 class="font-semibold text-neutral-900 mb-4">待处理事项</h3>
        <div class="space-y-3">
          <div 
            v-for="item in pendingItems" 
            :key="item.title"
            class="flex items-center justify-between p-3 bg-neutral-50 rounded-lg cursor-pointer hover:bg-neutral-100 transition-colors"
            @click="handlePendingItem(item)"
          >
            <div class="flex items-center gap-3">
              <a-badge :count="item.count" :number-style="{ backgroundColor: '#171717' }" />
              <span class="text-neutral-700">{{ item.title }}</span>
            </div>
            <right-outlined class="text-neutral-400" />
          </div>
        </div>
      </div>
    </div>
    
    <div class="grid grid-cols-2 gap-6">
      <div class="bg-white rounded-xl border border-neutral-100 p-6">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-semibold text-neutral-900">最新注册企业</h3>
          <a-button type="link" size="small">查看全部</a-button>
        </div>
        <a-list :data-source="recentCompanies" :split="false">
          <template #renderItem="{ item }">
            <a-list-item class="!py-3">
              <a-list-item-meta>
                <template #avatar>
                  <a-avatar class="!bg-neutral-200">
                    <span class="text-neutral-600">{{ item.name.charAt(0) }}</span>
                  </a-avatar>
                </template>
                <template #title>
                  <span class="text-neutral-900">{{ item.name }}</span>
                </template>
                <template #description>
                  <span class="text-neutral-400 text-sm">{{ item.industry }} · {{ item.location }}</span>
                </template>
              </a-list-item-meta>
              <template #actions>
                <a-tag :color="item.status === 'PENDING' ? 'warning' : 'success'">
                  {{ item.status === 'PENDING' ? '待审核' : '已通过' }}
                </a-tag>
              </template>
            </a-list-item>
          </template>
        </a-list>
      </div>
      
      <div class="bg-white rounded-xl border border-neutral-100 p-6">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-semibold text-neutral-900">最新职位发布</h3>
          <a-button type="link" size="small">查看全部</a-button>
        </div>
        <a-list :data-source="recentJobs" :split="false">
          <template #renderItem="{ item }">
            <a-list-item class="!py-3">
              <a-list-item-meta>
                <template #title>
                  <span class="text-neutral-900">{{ item.title }}</span>
                </template>
                <template #description>
                  <span class="text-neutral-400 text-sm">{{ item.company }} · {{ item.salary }}</span>
                </template>
              </a-list-item-meta>
              <template #actions>
                <span class="text-neutral-400 text-sm">{{ item.time }}</span>
              </template>
            </a-list-item>
          </template>
        </a-list>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  UserOutlined, 
  BankOutlined, 
  FileTextOutlined, 
  SendOutlined,
  RightOutlined 
} from '@ant-design/icons-vue'
import { adminApi, companyApi, jobApi } from '@/api'

const router = useRouter()

const statistics = ref([
  {
    title: '注册用户',
    value: '58,234',
    icon: UserOutlined,
    iconColor: 'text-blue-500',
    bgColor: 'bg-blue-50',
    trend: '+12%',
    trendClass: 'text-green-500 bg-green-50'
  },
  {
    title: '入驻企业',
    value: '3,256',
    icon: BankOutlined,
    iconColor: 'text-purple-500',
    bgColor: 'bg-purple-50',
    trend: '+8%',
    trendClass: 'text-green-500 bg-green-50'
  },
  {
    title: '在线职位',
    value: '12,580',
    icon: FileTextOutlined,
    iconColor: 'text-orange-500',
    bgColor: 'bg-orange-50',
    trend: '+15%',
    trendClass: 'text-green-500 bg-green-50'
  },
  {
    title: '今日投递',
    value: '1,234',
    icon: SendOutlined,
    iconColor: 'text-green-500',
    bgColor: 'bg-green-50',
    trend: '+23%',
    trendClass: 'text-green-500 bg-green-50'
  }
])

const trendData = ref([
  { label: '周一', value: 120 },
  { label: '周二', value: 180 },
  { label: '周三', value: 150 },
  { label: '周四', value: 220 },
  { label: '周五', value: 280 },
  { label: '周六', value: 160 },
  { label: '周日', value: 100 }
])

const normalizedTrendData = computed(() => {
  const values = trendData.value.map(item => Number(item.value) || 0)
  const maxValue = Math.max(...values, 1)
  return trendData.value.map(item => ({
    ...item,
    barHeight: Math.max(Math.round(((Number(item.value) || 0) / maxValue) * 210), 10)
  }))
})

const pendingItems = ref([
  { title: '待审核企业', count: 12, path: '/admin/companies' },
  { title: '待审核职位', count: 28, path: '/admin/jobs' },
  { title: '待处理投诉', count: 5, path: '/admin/complaints' },
  { title: '待发布公告', count: 3, path: '/admin/announcements' }
])

const recentCompanies = ref([
  { id: 1, name: '科技有限公司', industry: '互联网', location: '北京', status: 'PENDING' },
  { id: 2, name: '创新科技', industry: '软件开发', location: '上海', status: 'APPROVED' },
  { id: 3, name: '智能科技', industry: '人工智能', location: '深圳', status: 'PENDING' },
  { id: 4, name: '数据科技', industry: '大数据', location: '杭州', status: 'APPROVED' }
])

const recentJobs = ref([
  { id: 1, title: '高级前端工程师', company: '科技有限公司', salary: '25-40K', time: '2小时前' },
  { id: 2, title: 'Java开发工程师', company: '互联网科技', salary: '20-35K', time: '3小时前' },
  { id: 3, title: '产品经理', company: '创新科技', salary: '30-50K', time: '5小时前' },
  { id: 4, title: 'UI设计师', company: '设计工作室', salary: '15-25K', time: '6小时前' }
])

const handlePendingItem = (item: any) => {
  router.push(item.path)
}

const fetchDashboard = async () => {
  try {
    const [dashboardRes, usersRes, companiesRes, jobsRes] = await Promise.all([
      adminApi.getDashboard(),
      adminApi.getUsers(),
      companyApi.getAll(),
      jobApi.getAll(0, 50)
    ])
    const dashboard = dashboardRes.data || {}
    const users = usersRes.data || []
    const companies = companiesRes.data || []
    const jobs = jobsRes.data?.content || []
    statistics.value = [
      {
        ...statistics.value[0],
        value: String(dashboard.totalUsers || users.length)
      },
      {
        ...statistics.value[1],
        value: String(companies.length)
      },
      {
        ...statistics.value[2],
        value: String(jobs.length)
      },
      {
        ...statistics.value[3],
        value: String(dashboard.activeUsers || 0)
      }
    ]
    pendingItems.value = [
      { title: '待审核企业', count: companies.filter((item: any) => item.status === 'PENDING').length, path: '/admin/companies' },
      { title: '待审核职位', count: jobs.filter((item: any) => item.status === 'DRAFT').length, path: '/admin/jobs' },
      { title: '待处理投诉', count: Number(dashboard.pendingComplaints || 0), path: '/admin/complaints' },
      { title: '待发布公告', count: Number(dashboard.draftAnnouncements || 0), path: '/admin/announcements' }
    ]
    recentCompanies.value = companies.slice(0, 4).map((item: any) => ({
      id: item.id,
      name: item.name,
      industry: item.industry || '未分类',
      location: item.address || '未填写',
      status: item.status || 'PENDING'
    }))
    recentJobs.value = jobs.slice(0, 4).map((item: any) => ({
      id: item.id,
      title: item.title,
      company: `企业 #${item.companyId || '-'}`,
      salary: `${item.salaryMin || 0}-${item.salaryMax || 0}K`,
      time: item.createdAt ? new Date(item.createdAt).toLocaleDateString('zh-CN') : '-'
    }))
    const baseCount = Math.max(Math.floor(jobs.length / 7), 1)
    trendData.value = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'].map((label, idx) => ({
      label,
      value: baseCount + (idx % 3) * Math.max(Math.floor(baseCount / 2), 1) + idx
    }))
  } catch (error) {
    message.error('控制台数据加载失败')
  }
}

onMounted(() => {
  fetchDashboard()
})
</script>
