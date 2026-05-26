<template>
  <div class="home-wrap">
    <section class="hero">
      <div class="hero-panel">
        <h1>简洁高效的招聘平台</h1>
        <p>黑白灰现代化界面，聚焦真实职位与高质量投递体验</p>
        <div class="hero-search">
          <a-input
            v-model:value="searchKeyword"
            placeholder="搜索职位关键词，如：前端 / Java / 产品"
            size="large"
            @pressEnter="handleSearch"
          />
          <a-button type="primary" class="hero-btn" @click="handleSearch">搜索职位</a-button>
        </div>
      </div>
      <div class="stats-grid">
        <a-card v-for="item in statistics" :key="item.title" :bordered="false" class="stat-card">
          <div class="stat-title">{{ item.title }}</div>
          <div class="stat-value">{{ item.value }}</div>
        </a-card>
      </div>
    </section>

    <section class="section">
      <div class="section-head">
        <h2>热门职位</h2>
        <a-button type="link" @click="router.push('/jobs')">查看全部</a-button>
      </div>
      <div class="job-grid">
        <a-card v-for="job in hotJobs" :key="job.id" class="job-card" :bordered="false" @click="openHotJob(job)">
          <div class="job-top">
            <a-avatar :src="resolveMediaUrl(job.coverUrl)" shape="square" :size="46" class="job-avatar">
              {{ job.title?.charAt(0) || '职' }}
            </a-avatar>
            <div>
              <div class="job-title">{{ job.title }}</div>
              <div class="job-company">{{ job.companyName }}</div>
            </div>
          </div>
          <div class="job-salary">{{ job.salary }}</div>
          <div class="job-meta">{{ job.location }} · {{ job.experience }} · {{ job.education }}</div>
          <div class="job-tags">
            <a-tag v-for="tag in job.tags" :key="tag">{{ tag }}</a-tag>
          </div>
          <div class="job-actions">
            <a-button size="small" @click.stop="goJobDetail(job.id)">查看详情</a-button>
            <a-button type="primary" size="small" class="hero-btn" @click.stop="goJobsWithKeyword(job.title)">找同类职位</a-button>
          </div>
        </a-card>
      </div>
    </section>

    <section class="section">
      <div class="section-head">
        <h2>最新公告</h2>
      </div>
      <a-list :data-source="announcements" class="notice-list">
        <template #renderItem="{ item }">
          <a-list-item>
            <a-list-item-meta :title="item.title" :description="item.desc" />
            <span class="notice-date">{{ item.date }}</span>
          </a-list-item>
        </template>
      </a-list>
    </section>

    <a-modal
      v-model:open="jobModalOpen"
      :title="selectedJob?.title || '职位详情'"
      width="680px"
      ok-text="查看职位详情"
      cancel-text="关闭"
      @ok="selectedJob && goJobDetail(selectedJob.id)"
    >
      <div v-if="selectedJob" class="modal-body">
        <div class="modal-row"><span>企业：</span>{{ selectedJob.companyName }}</div>
        <div class="modal-row"><span>薪资：</span>{{ selectedJob.salary }}</div>
        <div class="modal-row"><span>地点：</span>{{ selectedJob.location }}</div>
        <div class="modal-row"><span>经验：</span>{{ selectedJob.experience }}</div>
        <div class="modal-row"><span>学历：</span>{{ selectedJob.education }}</div>
        <div class="modal-row"><span>发布时间：</span>{{ selectedJob.daysAgo }} 天前</div>
        <div class="modal-row">
          <span>标签：</span>
          <a-tag v-for="tag in selectedJob.tags" :key="tag">{{ tag }}</a-tag>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { announcementApi, companyApi, jobApi } from '@/api'
import { resolveMediaUrl } from '@/utils/media'

const router = useRouter()
const searchKeyword = ref('')
const jobModalOpen = ref(false)
const selectedJob = ref<any | null>(null)
const hotJobs = ref<any[]>([])
const announcements = ref<any[]>([])
const statistics = ref([
  { title: '在线职位', value: '0' },
  { title: '入驻企业', value: '0' },
  { title: '投递记录', value: '0' },
  { title: '平台消息', value: '0' }
])

const formatDaysAgo = (createdAt?: string) => {
  if (!createdAt) return 0
  const created = new Date(createdAt).getTime()
  if (Number.isNaN(created)) return 0
  return Math.max(0, Math.floor((Date.now() - created) / (1000 * 60 * 60 * 24)))
}

const fetchHomeData = async () => {
  try {
    const [jobsRes, companiesRes, announcementsRes] = await Promise.all([
      jobApi.getAll(0, 8),
      companyApi.getAll(),
      announcementApi.getPublished()
    ])
    const jobPage = jobsRes.data
    const jobs = jobPage?.content || []
    const companies = companiesRes.data || []
    const companyMap = new Map(companies.map((item: any) => [item.id, item]))
    hotJobs.value = jobs.map((job: any) => {
      const company = companyMap.get(job.companyId)
      return {
        id: job.id,
        title: job.title,
        companyName: company?.name || `企业 #${job.companyId}`,
        salary: `${job.salaryMin || 0}-${job.salaryMax || 0}K`,
        location: job.location || '地点待完善',
        experience: job.experience || '不限',
        education: job.education || '不限',
        tags: [job.jobType, job.education, job.experience].filter(Boolean),
        daysAgo: formatDaysAgo(job.createdAt),
        coverUrl: job.coverUrl
      }
    })
    announcements.value = (announcementsRes.data || []).slice(0, 5).map((item: any) => ({
      id: item.id,
      title: item.title,
      desc: item.content,
      date: item.createdAt ? new Date(item.createdAt).toLocaleDateString('zh-CN') : '-'
    }))
    statistics.value = [
      { title: '在线职位', value: String(jobPage?.totalElements || jobs.length) },
      { title: '入驻企业', value: String(companies.length) },
      { title: '投递记录', value: '0' },
      { title: '平台消息', value: String((announcementsRes.data || []).length) }
    ]
  } catch (error) {
    message.error('首页数据加载失败')
  }
}

const handleSearch = () => {
  router.push({ path: '/jobs', query: { keyword: searchKeyword.value } })
}

const openHotJob = (job: any) => {
  selectedJob.value = job
  jobModalOpen.value = true
}

const goJobDetail = (id: number) => {
  router.push(`/jobs/${id}`)
}

const goJobsWithKeyword = (keyword: string) => {
  router.push({ path: '/jobs', query: { keyword } })
}

onMounted(() => {
  fetchHomeData()
})
</script>

<style scoped>
.home-wrap {
  padding: 8px 0 24px;
}
.hero {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
  margin-bottom: 18px;
}
.hero-panel {
  background: #111;
  color: #fff;
  border-radius: 16px;
  padding: 24px;
}
.hero-panel h1 {
  font-size: 30px;
  font-weight: 700;
  margin: 0 0 10px;
}
.hero-panel p {
  color: #cfcfcf;
  margin: 0 0 18px;
}
.hero-search {
  display: flex;
  gap: 10px;
}
.hero-btn {
  background: #111 !important;
  border-color: #111 !important;
}
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.stat-card {
  border-radius: 14px;
}
.stat-title {
  color: #8c8c8c;
  font-size: 13px;
}
.stat-value {
  color: #111;
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}
.section {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 16px;
}
.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.job-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}
.job-card {
  border-radius: 14px;
  border: 1px solid #f0f0f0;
}
.job-top {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 6px;
}
.job-avatar {
  border: 1px solid #ececec;
}
.job-title {
  font-size: 15px;
  font-weight: 600;
}
.job-company,
.job-meta,
.notice-date {
  color: #8c8c8c;
  font-size: 12px;
}
.job-salary {
  color: #111;
  font-size: 18px;
  font-weight: 700;
}
.job-tags {
  margin: 8px 0;
}
.job-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
.modal-body {
  display: grid;
  gap: 8px;
}
.modal-row span {
  color: #8c8c8c;
  margin-right: 4px;
}
</style>
