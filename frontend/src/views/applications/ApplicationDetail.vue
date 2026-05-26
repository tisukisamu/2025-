<template>
  <div class="space-y-6">
    <a-page-header title="投递详情" @back="router.back()" />

    <a-card :bordered="false" class="rounded-xl">
      <div class="flex items-start justify-between gap-4">
        <div>
          <div class="text-2xl font-semibold text-neutral-900">{{ job.title || `职位 #${application.jobId || '-'}` }}</div>
          <div class="text-neutral-500 mt-1">{{ company.name || `企业 #${job.companyId || '-'}` }}</div>
          <div class="text-sm text-neutral-500 mt-2">
            {{ job.location || '地点待补充' }} · {{ salaryText }} · {{ job.education || '不限' }} · {{ job.experience || '不限' }}
          </div>
        </div>
        <a-tag :color="statusColor">{{ statusText }}</a-tag>
      </div>
      <div class="mt-4 text-sm text-neutral-500">
        投递时间：{{ formatDateTime(application.appliedAt) }}
      </div>
    </a-card>

    <a-card :bordered="false" class="rounded-xl" title="投递信息">
      <a-descriptions :column="2" size="small">
        <a-descriptions-item label="投递ID">{{ application.id || '-' }}</a-descriptions-item>
        <a-descriptions-item label="简历名称">{{ resume.name || `简历 #${application.resumeId || '-'}` }}</a-descriptions-item>
        <a-descriptions-item label="当前状态">{{ statusText }}</a-descriptions-item>
        <a-descriptions-item label="最后更新">{{ formatDateTime(application.updatedAt) }}</a-descriptions-item>
      </a-descriptions>
      <div class="mt-4">
        <a-button @click="router.push('/resumes')">去管理简历</a-button>
      </div>
    </a-card>

    <a-card id="interviews" :bordered="false" class="rounded-xl" title="面试安排">
      <a-empty v-if="interviews.length === 0" description="暂无面试安排" />
      <a-timeline v-else>
        <a-timeline-item v-for="item in interviews" :key="item.id" :color="interviewColor(item.result)">
          <div class="text-sm text-neutral-800">时间：{{ formatDateTime(item.interviewTime) }}</div>
          <div class="text-sm text-neutral-600">地点/方式：{{ item.location || '-' }}</div>
          <div class="text-sm text-neutral-600">面试官：{{ item.interviewer || '-' }}</div>
          <div class="text-sm text-neutral-600">结果：{{ interviewText(item.result) }}</div>
        </a-timeline-item>
      </a-timeline>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { applicationApi, companyApi, interviewApi, jobApi, resumeApi } from '@/api'

const route = useRoute()
const router = useRouter()
const application = reactive<any>({})
const job = reactive<any>({})
const company = reactive<any>({})
const resume = reactive<any>({})
const interviews = ref<any[]>([])

const salaryText = computed(() => `${job.salaryMin || 0}-${job.salaryMax || 0}K`)

const statusText = computed(() => {
  const map: Record<string, string> = {
    PENDING: '待处理',
    REVIEWING: '审核中',
    INTERVIEWED: '面试中',
    ACCEPTED: '已录用',
    REJECTED: '已拒绝'
  }
  return map[String(application.status || '').toUpperCase()] || '未知'
})

const statusColor = computed(() => {
  const map: Record<string, string> = {
    PENDING: 'default',
    REVIEWING: 'processing',
    INTERVIEWED: 'warning',
    ACCEPTED: 'success',
    REJECTED: 'error'
  }
  return map[String(application.status || '').toUpperCase()] || 'default'
})

const formatDateTime = (value?: string) => {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('zh-CN')
}

const interviewText = (value?: string) => {
  const map: Record<string, string> = {
    PENDING: '待面试',
    PASSED: '通过',
    FAILED: '未通过'
  }
  return map[String(value || '').toUpperCase()] || '待更新'
}

const interviewColor = (value?: string) => {
  const map: Record<string, string> = {
    PENDING: 'gray',
    PASSED: 'green',
    FAILED: 'red'
  }
  return map[String(value || '').toUpperCase()] || 'gray'
}

const fetchDetail = async () => {
  try {
    const id = Number(route.params.id)
    const res = await applicationApi.getById(id)
    Object.assign(application, res.data || {})
    if (application.jobId) {
      const jobRes = await jobApi.getById(application.jobId)
      Object.assign(job, jobRes.data || {})
      if (job.companyId) {
        const companyRes = await companyApi.getById(job.companyId)
        Object.assign(company, companyRes.data || {})
      }
    }
    if (application.resumeId) {
      const resumeRes = await resumeApi.getById(application.resumeId)
      Object.assign(resume, resumeRes.data || {})
    }
    const interviewsRes = await interviewApi.getByApplication(id)
    interviews.value = interviewsRes.data || []
  } catch (error) {
    message.error('投递详情加载失败')
  }
}

onMounted(() => {
  fetchDetail()
})
</script>
