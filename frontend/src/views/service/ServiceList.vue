<template>
  <div class="services-page">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">服务套餐</h1>
        <p class="page-subtitle">为您的爱宠选择最合适的告别方式</p>
      </div>
    </div>

    <div class="service-tabs">
      <a-radio-group v-model:value="activeType" button-style="solid">
        <a-radio-button value="all">全部</a-radio-button>
        <a-radio-button value="cremation">火化服务</a-radio-button>
        <a-radio-button value="burial">安葬服务</a-radio-button>
        <a-radio-button value="memorial">纪念服务</a-radio-button>
      </a-radio-group>
    </div>

    <div class="services-grid" v-if="filteredPackages.length > 0">
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :sm="12" :lg="6" v-for="pkg in filteredPackages" :key="pkg.id" class="service-col">
          <div class="service-card" :class="{ 'service-card-featured': pkg.featured }" @click="showDetail(pkg)">
            <div v-if="pkg.featured" class="featured-badge">推荐</div>
            <div class="service-image">
              <img v-if="pkg.image" :src="getImageUrl(pkg.image)" :alt="pkg.name" />
              <div v-else class="image-placeholder">
                <appstore-outlined />
              </div>
            </div>
            <div class="service-content">
              <h3 class="service-name">{{ pkg.name }}</h3>
              <div class="service-price">
                <span class="price-current">¥{{ pkg.price }}</span>
                <span v-if="pkg.originalPrice" class="price-original">¥{{ pkg.originalPrice }}</span>
              </div>
              <p class="service-description">{{ pkg.description }}</p>
              <div class="service-features" v-if="pkg.includes">
                <div class="feature-item" v-for="(item, index) in parseIncludes(pkg.includes).slice(0, 3)" :key="index">
                  <check-circle-outlined class="feature-icon" />
                  <span>{{ item }}</span>
                </div>
                <div v-if="parseIncludes(pkg.includes).length > 3" class="feature-more">
                  还有{{ parseIncludes(pkg.includes).length - 3 }}项服务...
                </div>
              </div>
              <div class="service-actions">
                <a-button type="primary" block @click.stop="handleBook(pkg)">
                  立即预约
                </a-button>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">
        <inbox-outlined />
      </div>
      <p class="empty-text">暂无服务套餐</p>
    </div>

    <a-modal
      v-model:open="detailVisible"
      :title="currentPackage?.name"
      :footer="null"
      width="600px"
      class="service-detail-modal"
    >
      <div class="detail-content" v-if="currentPackage">
        <div class="detail-image" v-if="currentPackage.image">
          <img :src="getImageUrl(currentPackage.image)" :alt="currentPackage.name" />
        </div>
        <div class="detail-price-section">
          <span class="detail-price">¥{{ currentPackage.price }}</span>
          <span v-if="currentPackage.originalPrice" class="detail-original-price">
            ¥{{ currentPackage.originalPrice }}
          </span>
          <a-tag v-if="currentPackage.featured" color="red" style="margin-left: 12px">推荐</a-tag>
        </div>
        <div class="detail-type">
          <a-tag :color="getTypeColor(currentPackage.type)">
            {{ getTypeText(currentPackage.type) }}
          </a-tag>
        </div>
        <div class="detail-description">
          <h4>服务介绍</h4>
          <p>{{ currentPackage.description || '暂无介绍' }}</p>
        </div>
        <div class="detail-includes" v-if="currentPackage.includes">
          <h4>服务内容</h4>
          <div class="includes-list">
            <div class="include-item" v-for="(item, index) in parseIncludes(currentPackage.includes)" :key="index">
              <check-circle-outlined class="include-icon" />
              <span>{{ item }}</span>
            </div>
          </div>
        </div>
        <div class="detail-actions">
          <a-button size="large" @click="detailVisible = false">取消</a-button>
          <a-button type="primary" size="large" @click="handleBookFromDetail">
            立即预约
          </a-button>
        </div>
      </div>
    </a-modal>

    <div class="service-info-section">
      <h2 class="section-title">服务流程</h2>
      <a-row :gutter="[24, 24]" class="process-grid">
        <a-col :xs="24" :sm="12" :lg="6" v-for="(step, index) in processSteps" :key="index">
          <div class="process-card">
            <div class="process-number">{{ index + 1 }}</div>
            <h4 class="process-title">{{ step.title }}</h4>
            <p class="process-desc">{{ step.description }}</p>
          </div>
        </a-col>
      </a-row>
    </div>

    <div class="faq-section">
      <h2 class="section-title">常见问题</h2>
      <a-collapse accordion>
        <a-collapse-panel v-for="(faq, index) in faqs" :key="index" :header="faq.question">
          <p>{{ faq.answer }}</p>
        </a-collapse-panel>
      </a-collapse>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { message } from 'ant-design-vue'
import { 
  AppstoreOutlined, 
  CheckCircleOutlined,
  InboxOutlined 
} from '@ant-design/icons-vue'
import { getServiceList } from '../../api/service'
import { getImageUrl } from '../../utils'

const router = useRouter()
const userStore = useUserStore()
const activeType = ref('all')
const packages = ref([])
const detailVisible = ref(false)
const currentPackage = ref(null)

const filteredPackages = computed(() => {
  if (activeType.value === 'all') {
    return packages.value
  }
  return packages.value.filter(pkg => pkg.type === activeType.value)
})

const typeColors = {
  cremation: 'orange',
  burial: 'green',
  memorial: 'purple',
  other: 'default'
}

const typeTexts = {
  cremation: '火化服务',
  burial: '安葬服务',
  memorial: '纪念服务',
  other: '其他服务'
}

const getTypeColor = (type) => typeColors[type] || 'default'
const getTypeText = (type) => typeTexts[type] || type

const showDetail = (pkg) => {
  currentPackage.value = pkg
  detailVisible.value = true
}

const handleBookFromDetail = () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    detailVisible.value = false
    router.push('/login')
    return
  }
  router.push(`/appointments/create?packageId=${currentPackage.value.id}`)
}

const processSteps = ref([
  { title: '在线预约', description: '选择服务套餐，填写预约信息' },
  { title: '上门接送', description: '专车上门，温柔接送您的爱宠' },
  { title: '告别仪式', description: '庄重的告别仪式，送别爱宠' },
  { title: '后续服务', description: '骨灰交付，纪念品制作' }
])

const faqs = ref([
  {
    question: '服务需要提前多久预约？',
    answer: '建议提前1-2天预约，我们也可以提供紧急服务，24小时内响应。'
  },
  {
    question: '可以指定告别仪式的时间吗？',
    answer: '可以的，我们会根据您的时间安排来协调告别仪式的具体时间。'
  },
  {
    question: '骨灰如何处理？',
    answer: '我们会将骨灰装入您选择的骨灰盒中，您可以带回家或选择我们的安葬服务。'
  },
  {
    question: '服务过程中可以拍照吗？',
    answer: '可以，我们会为您记录整个服务过程，并提供电子相册。'
  }
])

const loadPackages = async () => {
  try {
    const res = await getServiceList()
    if (res.code === 200) {
      const payload = res.data
      if (Array.isArray(payload)) {
        packages.value = payload
      } else if (Array.isArray(payload?.records)) {
        packages.value = payload.records
      } else if (Array.isArray(payload?.list)) {
        packages.value = payload.list
      } else {
        packages.value = []
      }
    }
  } catch (error) {
    console.error('加载服务套餐失败:', error)
  }
}

const parseIncludes = (includes) => {
  if (!includes) {
    return []
  }
  if (Array.isArray(includes)) {
    return includes
  }
  try {
    return JSON.parse(includes)
  } catch {
    return String(includes).split(',').map(item => item.trim()).filter(Boolean)
  }
}

const handleBook = (pkg) => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  router.push(`/appointments/create?packageId=${pkg.id}`)
}

onMounted(() => {
  loadPackages()
})
</script>

<style scoped>
.services-page {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
}

.page-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #262626;
  margin: 0;
}

.page-subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 4px 0 0;
}

.service-tabs {
  margin-bottom: 32px;
}

.service-tabs :deep(.ant-radio-button-wrapper) {
  border-radius: 8px;
  margin-right: 8px;
}

.service-tabs :deep(.ant-radio-button-wrapper-checked) {
  background: #262626;
  border-color: #262626;
}

.service-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.service-card:hover {
  border-color: #d9d9d9;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.service-card-featured {
  border-color: #262626;
  border-width: 2px;
}

.featured-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  background: #262626;
  color: #fff;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  z-index: 1;
}

.service-image {
  width: 100%;
  height: 200px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.service-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  font-size: 48px;
  color: #d9d9d9;
}

.service-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.service-name {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 12px;
}

.service-price {
  margin-bottom: 12px;
}

.price-current {
  font-size: 28px;
  font-weight: 700;
  color: #262626;
}

.price-original {
  font-size: 14px;
  color: #8c8c8c;
  text-decoration: line-through;
  margin-left: 8px;
}

.service-description {
  font-size: 14px;
  color: #8c8c8c;
  line-height: 1.6;
  margin-bottom: 16px;
}

.service-features {
  margin-bottom: 16px;
  flex: 1;
}

.service-actions {
  margin-top: auto;
}

.service-col {
  display: flex;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #595959;
  margin-bottom: 8px;
}

.feature-icon {
  color: #52c41a;
  font-size: 14px;
}

.empty-state {
  text-align: center;
  padding: 80px 24px;
}

.empty-icon {
  font-size: 64px;
  color: #d9d9d9;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: #8c8c8c;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 24px;
}

.service-info-section {
  margin-top: 48px;
  padding-top: 48px;
  border-top: 1px solid #f0f0f0;
}

.process-card {
  text-align: center;
  padding: 24px;
}

.process-number {
  width: 48px;
  height: 48px;
  background: #262626;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
  margin: 0 auto 16px;
}

.process-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 8px;
}

.process-desc {
  font-size: 13px;
  color: #8c8c8c;
}

.faq-section {
  margin-top: 48px;
  padding-top: 48px;
  border-top: 1px solid #f0f0f0;
}

.faq-section :deep(.ant-collapse-item) {
  border: 1px solid #f0f0f0;
  border-radius: 8px !important;
  margin-bottom: 12px;
  overflow: hidden;
}

.faq-section :deep(.ant-collapse-header) {
  background: #fafafa;
  font-weight: 500;
}

.faq-section :deep(.ant-collapse-content) {
  border-top: 1px solid #f0f0f0;
}

.feature-more {
  font-size: 12px;
  color: #8c8c8c;
  padding: 4px 0;
}

.detail-content {
  padding: 0;
}

.detail-image {
  width: 100%;
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}

.detail-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-price-section {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.detail-price {
  font-size: 32px;
  font-weight: 700;
  color: #262626;
}

.detail-original-price {
  font-size: 16px;
  color: #8c8c8c;
  text-decoration: line-through;
  margin-left: 12px;
}

.detail-type {
  margin-bottom: 20px;
}

.detail-description,
.detail-includes {
  margin-bottom: 24px;
}

.detail-description h4,
.detail-includes h4 {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 12px;
}

.detail-description p {
  font-size: 14px;
  color: #595959;
  line-height: 1.8;
}

.includes-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.include-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
  font-size: 14px;
  color: #262626;
}

.include-icon {
  color: #52c41a;
  font-size: 16px;
}

.detail-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}
</style>
