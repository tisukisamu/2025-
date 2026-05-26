<template>
  <div class="job-list-page">
    <div class="page-header">
      <div class="header-bg"></div>
      <div class="header-content">
        <h1>职位搜索</h1>
        <p>发现最适合你的职业机会</p>
        
        <div class="search-section">
          <div class="search-box">
            <a-input
              v-model:value="searchParams.keyword"
              placeholder="搜索职位、公司或关键词..."
              size="large"
              class="search-input"
              @pressEnter="handleSearch"
            >
              <template #prefix>
                <SearchOutlined class="search-icon" />
              </template>
            </a-input>
            <a-select
              v-model:value="searchParams.city"
              placeholder="选择城市"
              size="large"
              class="city-select"
              allowClear
            >
              <a-select-option value="北京">北京</a-select-option>
              <a-select-option value="上海">上海</a-select-option>
              <a-select-option value="深圳">深圳</a-select-option>
              <a-select-option value="杭州">杭州</a-select-option>
              <a-select-option value="广州">广州</a-select-option>
              <a-select-option value="成都">成都</a-select-option>
            </a-select>
            <a-button type="primary" size="large" class="search-btn" @click="handleSearch">
              搜索
            </a-button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="page-content">
      <div class="container">
        <div class="filter-section">
          <div class="filter-group">
            <span class="filter-label">工作经验:</span>
            <div class="filter-options">
              <span 
                v-for="exp in experienceOptions" 
                :key="exp.value"
                :class="['filter-option', { active: searchParams.experience === exp.value }]"
                @click="selectExperience(exp.value)"
              >
                {{ exp.label }}
              </span>
            </div>
          </div>
          
          <div class="filter-group">
            <span class="filter-label">学历要求:</span>
            <div class="filter-options">
              <span 
                v-for="edu in educationOptions" 
                :key="edu.value"
                :class="['filter-option', { active: searchParams.education === edu.value }]"
                @click="selectEducation(edu.value)"
              >
                {{ edu.label }}
              </span>
            </div>
          </div>
          
          <div class="filter-group">
            <span class="filter-label">薪资范围:</span>
            <div class="filter-options">
              <span 
                v-for="salary in salaryOptions" 
                :key="salary.value"
                :class="['filter-option', { active: searchParams.salary === salary.value }]"
                @click="selectSalary(salary.value)"
              >
                {{ salary.label }}
              </span>
            </div>
          </div>
        </div>
        
        <div class="result-header">
          <div class="result-count">
            共找到 <span class="count">{{ jobs.length }}</span> 个职位
          </div>
          <div class="sort-options">
            <span class="sort-label">排序:</span>
            <a-select v-model:value="sortBy" size="small" class="sort-select">
              <a-select-option value="latest">最新发布</a-select-option>
              <a-select-option value="salary">薪资最高</a-select-option>
              <a-select-option value="hot">最热门</a-select-option>
            </a-select>
          </div>
        </div>
        
        <div class="jobs-list">
          <div 
            v-for="(job, index) in jobs" 
            :key="job.id"
            class="job-item"
            :style="{ animationDelay: `${index * 0.05}s` }"
            @click="viewJob(job.id)"
          >
            <div class="job-main">
              <div class="job-logo" :style="{ background: job.logoColor }">
                <img v-if="job.coverUrl" :src="resolveMediaUrl(job.coverUrl)" class="w-full h-full object-cover" style="border-radius: 14px;" />
                <template v-else>{{ job.companyName.charAt(0) }}</template>
              </div>
              
              <div class="job-content">
                <div class="job-header">
                  <h3 class="job-title">{{ job.title }}</h3>
                  <div class="job-salary">{{ job.salary }}</div>
                </div>
                
                <div class="job-company">
                  <span class="company-name">{{ job.companyName }}</span>
                  <span class="company-divider">|</span>
                  <span class="company-industry">{{ job.industry }}</span>
                </div>
                
                <div class="job-tags">
                  <span v-for="tag in job.tags" :key="tag" class="job-tag">{{ tag }}</span>
                </div>
                
                <div class="job-meta">
                  <span><EnvironmentOutlined /> {{ job.location }}</span>
                  <span><ClockCircleOutlined /> {{ job.experience }}</span>
                  <span><BookOutlined /> {{ job.education }}</span>
                  <span><HistoryOutlined /> {{ job.daysAgo }}天前发布</span>
                </div>
              </div>
              
              <div class="job-actions">
                <a-button type="primary" class="apply-btn" @click.stop="applyJob(job)">
                  立即投递
                </a-button>
                <a-button :type="job.collected ? 'primary' : 'default'" class="collect-btn" @click.stop="collectJob(job)">
                  <StarOutlined />
                  {{ job.collected ? '已收藏' : '收藏' }}
                </a-button>
              </div>
            </div>
            
            <div v-if="job.urgent" class="urgent-tag">
              <FireOutlined /> 急聘
            </div>
            <div v-if="job.hot" class="hot-tag">
              <FireOutlined /> 热门
            </div>
          </div>
        </div>
        
        <div class="pagination-section">
          <a-pagination
            v-model:current="pagination.current"
            :total="pagination.total"
            :page-size="pagination.pageSize"
            show-quick-jumper
            @change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  SearchOutlined, 
  EnvironmentOutlined, 
  ClockCircleOutlined,
  BookOutlined,
  HistoryOutlined,
  StarOutlined,
  FireOutlined
} from '@ant-design/icons-vue'
import { applicationApi, companyApi, favoriteApi, jobApi, resumeApi } from '@/api'
import { resolveMediaUrl } from '@/utils/media'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const searchParams = reactive({
  keyword: '',
  city: undefined,
  experience: '',
  education: '',
  salary: ''
})

const sortBy = ref('latest')

const experienceOptions = [
  { label: '不限', value: '' },
  { label: '应届生', value: '应届生' },
  { label: '1-3年', value: '1-3年' },
  { label: '3-5年', value: '3-5年' },
  { label: '5-10年', value: '5-10年' },
  { label: '10年以上', value: '10年以上' }
]

const educationOptions = [
  { label: '不限', value: '' },
  { label: '大专', value: '大专' },
  { label: '本科', value: '本科' },
  { label: '硕士', value: '硕士' },
  { label: '博士', value: '博士' }
]

const salaryOptions = [
  { label: '不限', value: '' },
  { label: '5K以下', value: '0-5' },
  { label: '5-10K', value: '5-10' },
  { label: '10-20K', value: '10-20' },
  { label: '20-30K', value: '20-30' },
  { label: '30-50K', value: '30-50' },
  { label: '50K以上', value: '50-999' }
]

const jobs = ref<any[]>([])
const companyMap = ref<Map<number, any>>(new Map())
const favoriteJobIds = ref<Set<number>>(new Set())

const pagination = reactive({
  current: 1,
  total: 0,
  pageSize: 10
})

const formatDaysAgo = (createdAt?: string) => {
  if (!createdAt) return 0
  const createTime = new Date(createdAt).getTime()
  if (Number.isNaN(createTime)) return 0
  const days = Math.floor((Date.now() - createTime) / (1000 * 60 * 60 * 24))
  return Math.max(days, 0)
}

const mapJobItem = (item: any) => {
  const company = companyMap.value.get(item.companyId)
  return {
    id: item.id,
    title: item.title,
    companyName: company?.name || `企业 #${item.companyId || '-'}`,
    industry: company?.industry || '行业待完善',
    salary: `${item.salaryMin || 0}-${item.salaryMax || 0}K`,
    tags: [item.jobType, item.education, item.experience].filter(Boolean),
    location: item.location || '地点待补充',
    experience: item.experience || '不限',
    education: item.education || '不限',
    daysAgo: formatDaysAgo(item.createdAt),
    urgent: item.status === 'ACTIVE',
    hot: false,
    collected: favoriteJobIds.value.has(item.id),
    logoColor: 'linear-gradient(135deg, #1f1f1f 0%, #4a4a4a 100%)',
    coverUrl: item.coverUrl
  }
}

const fetchFavoriteIds = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await favoriteApi.getMyJobIds()
    favoriteJobIds.value = new Set((res.data || []).map((id: any) => Number(id)))
  } catch (error) {
    favoriteJobIds.value = new Set()
  }
}

const fetchJobs = async () => {
  try {
    const query = {
      title: searchParams.keyword || undefined,
      location: searchParams.city || undefined,
      education: searchParams.education || undefined,
      experience: searchParams.experience || undefined,
      page: pagination.current - 1,
      size: pagination.pageSize
    }
    const [jobRes, companyRes] = await Promise.all([
      jobApi.search(query),
      companyApi.getAll()
    ])
    companyMap.value = new Map((companyRes.data || []).map((item: any) => [item.id, item]))
    const pageData = jobRes.data
    jobs.value = (pageData?.content || []).map(mapJobItem)
    pagination.total = pageData?.totalElements || 0
  } catch (error) {
    message.error('获取职位列表失败')
  }
}

const handleSearch = async () => {
  pagination.current = 1
  await fetchJobs()
}

const selectExperience = (value: string) => {
  searchParams.experience = value
}

const selectEducation = (value: string) => {
  searchParams.education = value
}

const selectSalary = (value: string) => {
  searchParams.salary = value
}

const handlePageChange = (page: number) => {
  pagination.current = page
  fetchJobs()
}

const viewJob = (id: number) => {
  router.push(`/jobs/${id}`)
}

const applyJob = async (job: any) => {
  try {
    const resumeRes = await resumeApi.getMy()
    const firstResume = resumeRes.data?.[0]
    if (!firstResume) {
      message.warning('请先创建简历后再投递')
      router.push('/resumes')
      return
    }
    await applicationApi.create({
      jobId: job.id,
      resumeId: firstResume.id
    })
    message.success(`已投递 ${job.title}`)
  } catch (error) {
    message.error('投递失败')
  }
}

const collectJob = (job: any) => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录后再收藏')
    router.push('/login')
    return
  }
  if (job.collected) {
    favoriteApi.remove(job.id).then(() => {
      job.collected = false
      favoriteJobIds.value.delete(job.id)
      message.success(`已取消收藏 ${job.title}`)
    }).catch(() => {
      message.error('取消收藏失败')
    })
    return
  }
  favoriteApi.add(job.id).then(() => {
    job.collected = true
    favoriteJobIds.value.add(job.id)
    message.success(`已收藏 ${job.title} 职位`)
  }).catch(() => {
    message.error('收藏失败')
  })
}

onMounted(async () => {
  if (route.query.keyword) {
    searchParams.keyword = route.query.keyword as string
  }
  await fetchFavoriteIds()
  fetchJobs()
})
</script>

<style scoped>
.job-list-page {
  min-height: 100vh;
  background: #f3f3f3;
}

.page-header {
  position: relative;
  padding: 52px 24px 84px;
  overflow: hidden;
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #111111 0%, #222222 55%, #404040 100%);
}

.header-bg::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 78% 16%, rgba(255, 255, 255, 0.14) 0%, transparent 52%);
}

.header-content {
  position: relative;
  z-index: 1;
  max-width: 980px;
  margin: 0 auto;
  text-align: center;
}

.header-content h1 {
  font-size: 34px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
  letter-spacing: 0.5px;
}

.header-content p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.75);
  margin-bottom: 28px;
}

.search-section {
  margin-top: 20px;
}

.search-box {
  display: flex;
  gap: 12px;
  background: rgba(255, 255, 255, 0.94);
  padding: 12px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 16px 44px rgba(0, 0, 0, 0.2);
}

.search-box :deep(.ant-input-affix-wrapper-lg),
.search-box :deep(.ant-select-single.ant-select-lg .ant-select-selector),
.search-box :deep(.ant-btn-lg) {
  height: 46px !important;
  min-height: 46px !important;
  border-radius: 10px !important;
}

.search-box :deep(.ant-input-affix-wrapper) {
  display: flex;
  align-items: center;
}

.search-box :deep(.ant-input-affix-wrapper > input.ant-input) {
  height: 100%;
  line-height: 44px;
}

.search-box :deep(.ant-select-single.ant-select-lg .ant-select-selector) {
  display: flex;
  align-items: center;
  padding: 0 11px !important;
}

.search-box :deep(.ant-select-single.ant-select-lg .ant-select-selection-item),
.search-box :deep(.ant-select-single.ant-select-lg .ant-select-selection-placeholder) {
  line-height: 44px !important;
}

.search-input {
  flex: 1;
}

.search-input :deep(.ant-input) {
  border: none;
  font-size: 16px;
  padding: 12px 16px;
}

.search-input :deep(.ant-input:focus) {
  box-shadow: none;
}

.search-icon {
  color: #aaa;
  font-size: 18px;
}

.city-select {
  width: 140px;
}

.city-select :deep(.ant-select-selector) {
  border: none !important;
  border-radius: 12px !important;
  background: #f5f5f5 !important;
}

.search-btn {
  padding: 0 32px;
  font-weight: 600;
  background: #111111;
  border-color: #111111;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.page-content {
  margin-top: -54px;
  position: relative;
  z-index: 10;
  padding-bottom: 48px;
}

.filter-section {
  background: #fff;
  border: 1px solid #e9e9e9;
  border-radius: 14px;
  padding: 20px 22px;
  margin-bottom: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.03);
}

.filter-group {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.filter-group:last-child {
  border-bottom: none;
}

.filter-label {
  width: 80px;
  color: #666;
  font-size: 14px;
  flex-shrink: 0;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-option {
  padding: 6px 16px;
  border-radius: 8px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-option:hover {
  background: #f5f5f5;
  color: #333;
}

.filter-option.active {
  background: #111111;
  color: #fff;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.result-count {
  font-size: 14px;
  color: #666;
}

.count {
  font-size: 20px;
  font-weight: 700;
  color: #111111;
  margin: 0 4px;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-label {
  font-size: 14px;
  color: #666;
}

.sort-select {
  width: 120px;
}

.jobs-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.job-item {
  background: #fff;
  border: 1px solid #ebebeb;
  border-radius: 14px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  animation: fadeInUp 0.5s ease-out forwards;
  opacity: 0;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.job-item:hover {
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.06);
  border-color: #dcdcdc;
  transform: translateY(-2px);
}

.job-main {
  display: flex;
  gap: 20px;
}

.job-logo {
  width: 64px;
  height: 64px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
  font-weight: 700;
  flex-shrink: 0;
}

.job-content {
  flex: 1;
  min-width: 0;
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.job-title {
  font-size: 19px;
  font-weight: 650;
  color: #151515;
  margin: 0;
}

.job-salary {
  font-size: 21px;
  font-weight: 700;
  color: #111111;
}

.job-company {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.company-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.company-divider {
  color: #ddd;
}

.company-industry {
  font-size: 14px;
  color: #888;
}

.job-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.job-tag {
  background: #f3f3f3;
  color: #4f4f4f;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
}

.job-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #aaa;
}

.job-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.job-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  justify-content: center;
}

.apply-btn {
  border-radius: 10px;
  font-weight: 600;
  background: #111111;
  border-color: #111111;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.collect-btn {
  border-radius: 10px;
  border: 1px solid #e8e8e8;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.urgent-tag,
.hot-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

.urgent-tag {
  background: #111111;
  color: #fff;
}

.hot-tag {
  background: #f5f5f5;
  color: #4c4c4c;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 26px;
  padding: 16px;
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
}

@media (max-width: 768px) {
  .page-header {
    padding: 42px 16px 70px;
  }

  .container {
    padding: 0 14px;
  }

  .search-box {
    flex-direction: column;
  }
  
  .city-select {
    width: 100%;
  }
  
  .job-main {
    flex-direction: column;
    gap: 14px;
  }
  
  .job-logo {
    width: 48px;
    height: 48px;
    font-size: 18px;
  }
  
  .job-actions {
    flex-direction: row;
    justify-content: flex-start;
  }
}
</style>
