<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-semibold text-neutral-900">投递记录</h1>
        <p class="text-neutral-500 mt-1">查看您的职位投递状态</p>
      </div>
      <a-radio-group v-model:value="statusFilter" button-style="solid">
        <a-radio-button value="all">全部</a-radio-button>
        <a-radio-button value="pending">待处理</a-radio-button>
        <a-radio-button value="interviewed">面试中</a-radio-button>
        <a-radio-button value="accepted">已录用</a-radio-button>
        <a-radio-button value="rejected">已拒绝</a-radio-button>
      </a-radio-group>
    </div>
    
    <div class="space-y-4">
      <div 
        v-for="application in filteredApplications" 
        :key="application.id"
        class="bg-white rounded-xl border border-neutral-100 hover:border-neutral-300 transition-colors"
      >
        <div class="p-6">
          <div class="flex justify-between items-start">
            <div class="flex gap-4">
              <div class="w-12 h-12 bg-neutral-100 rounded-lg flex items-center justify-center">
                <bank-outlined class="text-xl text-neutral-400" />
              </div>
              <div>
                <h3 class="text-lg font-semibold text-neutral-900">{{ application.jobTitle }}</h3>
                <p class="text-neutral-500 text-sm">{{ application.companyName }}</p>
                <div class="flex items-center gap-4 mt-2 text-sm text-neutral-400">
                  <span><environment-outlined /> {{ application.location }}</span>
                  <span><clock-circle-outlined /> {{ application.salary }}</span>
                </div>
              </div>
            </div>
            
            <div class="text-right">
              <a-tag :color="getStatusColor(application.status)">
                {{ getStatusText(application.status) }}
              </a-tag>
              <div class="text-neutral-400 text-sm mt-2">
                投递于 {{ application.appliedAt }}
              </div>
            </div>
          </div>
        </div>
        
        <div class="px-6 py-4 bg-neutral-50 rounded-b-xl flex justify-between items-center">
          <div class="flex items-center gap-4">
            <span class="text-neutral-500 text-sm">
              使用简历: {{ application.resumeName }}
            </span>
          </div>
          <div class="flex gap-2">
            <a-button size="small" @click="viewDetail(application)">
              查看详情
            </a-button>
            <a-button 
              v-if="application.status === 'interviewed'" 
              type="primary" 
              size="small"
              class="!bg-neutral-900"
              @click="viewInterview(application)"
            >
              查看面试
            </a-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { BankOutlined, EnvironmentOutlined, ClockCircleOutlined } from '@ant-design/icons-vue'
import { applicationApi, jobApi, resumeApi, companyApi } from '@/api'
import type { ApplicationStatus } from '@/types/application'

const router = useRouter()

const statusFilter = ref('all')
const loading = ref(false)
const applications = ref<any[]>([])

const filteredApplications = computed(() => {
  if (statusFilter.value === 'all') {
    return applications.value
  }
  return applications.value.filter(app => app.status === statusFilter.value)
})

const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    pending: 'default',
    reviewing: 'processing',
    interviewed: 'warning',
    accepted: 'success',
    rejected: 'error'
  }
  return colors[status] || 'default'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '待处理',
    reviewing: '审核中',
    interviewed: '面试中',
    accepted: '已录用',
    rejected: '已拒绝'
  }
  return texts[status] || status
}

const viewDetail = (application: any) => {
  router.push(`/applications/${application.id}`)
}

const viewInterview = (application: any) => {
  router.push(`/applications/${application.id}#interviews`)
}

const normalizeStatus = (status: ApplicationStatus | string) => {
  const value = String(status).toUpperCase()
  const statusMap: Record<string, string> = {
    PENDING: 'pending',
    REVIEWING: 'reviewing',
    INTERVIEWED: 'interviewed',
    ACCEPTED: 'accepted',
    REJECTED: 'rejected'
  }
  return statusMap[value] || 'pending'
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleDateString('zh-CN')
}

const fetchApplications = async () => {
  loading.value = true
  try {
    const [applicationRes, jobRes, resumeRes, companyRes] = await Promise.all([
      applicationApi.getMy(),
      jobApi.getAll(0, 200),
      resumeApi.getMy(),
      companyApi.getAll()
    ])
    const jobs = jobRes.data?.content || []
    const resumes = resumeRes.data || []
    const companies = companyRes.data || []
    const jobMap = new Map(jobs.map((item: any) => [item.id, item]))
    const resumeMap = new Map(resumes.map((item: any) => [item.id, item]))
    const companyMap = new Map(companies.map((item: any) => [item.id, item]))
    applications.value = (applicationRes.data || []).map((item: any) => {
      const job = jobMap.get(item.jobId)
      const company = companyMap.get(job?.companyId)
      const resume = resumeMap.get(item.resumeId)
      return {
        id: item.id,
        jobTitle: job?.title || `职位 #${item.jobId}`,
        companyName: company?.name || `企业 #${job?.companyId || '-'}`,
        location: job?.location || '地点待补充',
        salary: `${job?.salaryMin || 0}-${job?.salaryMax || 0}K`,
        resumeName: resume?.name || `简历 #${item.resumeId}`,
        status: normalizeStatus(item.status),
        appliedAt: formatDate(item.appliedAt)
      }
    })
  } catch (error) {
    message.error('获取投递记录失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchApplications()
})
</script>
