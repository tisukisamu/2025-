<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-semibold text-neutral-900">收到的简历</h1>
      <div class="flex gap-3">
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索候选人"
          style="width: 250px"
          class="!rounded-lg"
        />
        <a-select v-model:value="jobFilter" placeholder="筛选职位" style="width: 200px" allow-clear>
          <a-select-option v-for="job in jobOptions" :key="job.value" :value="job.value">
            {{ job.label }}
          </a-select-option>
        </a-select>
      </div>
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
              <a-avatar :size="56" class="!bg-neutral-200">
                <span class="text-neutral-600 text-xl">{{ application.name.charAt(0) }}</span>
              </a-avatar>
              <div>
                <div class="flex items-center gap-3">
                  <h3 class="text-lg font-semibold text-neutral-900">{{ application.name }}</h3>
                  <a-tag :color="getStatusColor(application.status)">
                    {{ getStatusText(application.status) }}
                  </a-tag>
                </div>
                <p class="text-neutral-500 text-sm mt-1">
                  应聘: {{ application.jobTitle }}
                </p>
                <div class="flex items-center gap-4 mt-2 text-sm text-neutral-400">
                  <span><book-outlined /> {{ application.education }}</span>
                  <span><clock-circle-outlined /> {{ application.experience }}</span>
                  <span><calendar-outlined /> {{ application.age }}岁</span>
                </div>
              </div>
            </div>
            
            <div class="text-right">
              <div class="text-neutral-400 text-sm">投递时间</div>
              <div class="text-neutral-600">{{ application.appliedAt }}</div>
            </div>
          </div>
          
          <div class="mt-4 pt-4 border-t border-neutral-100">
            <div class="text-neutral-500 text-sm mb-2">技能标签</div>
            <div class="flex flex-wrap gap-2">
              <a-tag 
                v-for="skill in application.skills" 
                :key="skill"
                class="!bg-neutral-100 !text-neutral-600 !border-0"
              >
                {{ skill }}
              </a-tag>
            </div>
          </div>
        </div>
        
        <div class="px-6 py-4 bg-neutral-50 rounded-b-xl flex justify-between items-center">
          <div class="flex items-center gap-4">
            <a-button type="link" @click="viewResume(application)">查看简历</a-button>
            <a-button type="link" @click="downloadResume(application)">下载简历</a-button>
          </div>
          <div class="flex gap-2">
            <a-button 
              v-if="application.status === 'pending'"
              @click="updateStatus(application, 'reviewing')"
            >
              标记已读
            </a-button>
            <a-button 
              v-if="application.status === 'reviewing'"
              type="primary"
              class="!bg-neutral-900"
              @click="inviteInterview(application)"
            >
              邀请面试
            </a-button>
            <a-button 
              v-if="application.status === 'interviewed'"
              type="primary"
              class="!bg-green-600"
              @click="updateStatus(application, 'accepted')"
            >
              录用
            </a-button>
            <a-button 
              v-if="['pending', 'reviewing'].includes(application.status)"
              danger
              @click="updateStatus(application, 'rejected')"
            >
              拒绝
            </a-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { BookOutlined, ClockCircleOutlined, CalendarOutlined } from '@ant-design/icons-vue'
import { applicationApi, resumeApi, jobApi } from '@/api'

const searchKeyword = ref('')
const jobFilter = ref<string | undefined>(undefined)
const applications = ref<any[]>([])
const jobs = ref<any[]>([])

const jobOptions = computed(() =>
  jobs.value.map(item => ({ value: String(item.id), label: item.title }))
)

const filteredApplications = computed(() =>
  applications.value.filter(item => {
    const keywordMatched =
      !searchKeyword.value ||
      item.name.includes(searchKeyword.value) ||
      item.jobTitle.includes(searchKeyword.value)
    const jobMatched = !jobFilter.value || String(item.jobId) === String(jobFilter.value)
    return keywordMatched && jobMatched
  })
)

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
    reviewing: '已查看',
    interviewed: '面试中',
    accepted: '已录用',
    rejected: '已拒绝'
  }
  return texts[status] || status
}

const viewResume = (application: any) => {
  message.info(`查看简历 #${application.resumeId}`)
}

const downloadResume = (application: any) => {
  message.success(`简历 #${application.resumeId} 下载中`)
}

const updateStatus = async (application: any, status: string) => {
  try {
    await applicationApi.updateStatus(application.id, status.toUpperCase())
    application.status = status
    message.success('状态更新成功')
  } catch (error) {
    message.error('状态更新失败')
  }
}

const inviteInterview = async (application: any) => {
  await updateStatus(application, 'interviewed')
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleDateString('zh-CN')
}

const fetchData = async () => {
  try {
    const [applicationRes, jobsRes] = await Promise.all([
      applicationApi.getReceived(),
      jobApi.getMy()
    ])
    jobs.value = jobsRes.data || []
    const jobMap = new Map(jobs.value.map(item => [item.id, item]))
    const appList = applicationRes.data || []
    const resumePromises = appList.map(item => resumeApi.getById(item.resumeId).catch(() => null))
    const resumeResList = await Promise.all(resumePromises)
    const resumeMap = new Map<number, any>()
    resumeResList.forEach((res: any) => {
      if (res?.data?.id) {
        resumeMap.set(res.data.id, res.data)
      }
    })
    applications.value = appList.map((item: any) => {
      const resume = resumeMap.get(item.resumeId)
      const job = jobMap.get(item.jobId)
      return {
        id: item.id,
        jobId: item.jobId,
        resumeId: item.resumeId,
        name: resume?.name || `候选人 #${item.userId}`,
        jobTitle: job?.title || `职位 #${item.jobId}`,
        education: resume?.education || '未填写',
        experience: resume?.experience || '未填写',
        age: resume?.age || '-',
        skills: String(resume?.skills || '')
          .split(',')
          .map((skill: string) => skill.trim())
          .filter(Boolean),
        status: String(item.status || '').toLowerCase(),
        appliedAt: formatDate(item.appliedAt)
      }
    })
  } catch (error) {
    message.error('获取简历数据失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>
