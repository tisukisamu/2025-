<template>
  <div class="home-page">
    <div class="hero-section">
      <a-carousel autoplay :dots="true" class="hero-carousel">
        <div class="carousel-item carousel-item-1">
          <div class="carousel-content">
            <h1>校园闲置电子产品置换平台</h1>
            <p>安全、便捷、可靠的校园二手交易</p>
            <div class="hero-actions">
              <a-button type="primary" size="large" @click="router.push('/products')">
                浏览商品
              </a-button>
              <a-button size="large" ghost @click="router.push('/publish')" v-if="userStore.isLoggedIn">
                发布商品
              </a-button>
            </div>
          </div>
        </div>
        <div class="carousel-item carousel-item-2">
          <div class="carousel-content">
            <h1>闲置物品变废为宝</h1>
            <p>让你的闲置电子产品找到新主人</p>
            <div class="hero-actions">
              <a-button type="primary" size="large" @click="router.push('/products')">
                立即探索
              </a-button>
            </div>
          </div>
        </div>
        <div class="carousel-item carousel-item-3">
          <div class="carousel-content">
            <h1>校园专属交易平台</h1>
            <p>学号认证，交易更放心</p>
            <div class="hero-actions">
              <a-button type="primary" size="large" @click="router.push('/register')" v-if="!userStore.isLoggedIn">
                立即注册
              </a-button>
            </div>
          </div>
        </div>
      </a-carousel>
    </div>

    <div class="quick-actions" v-if="userStore.isLoggedIn">
      <a-row :gutter="[16, 16]">
        <a-col :xs="12" :sm="6">
          <div class="action-card" @click="router.push('/publish')">
            <plus-circle-outlined class="action-icon" />
            <span>发布商品</span>
          </div>
        </a-col>
        <a-col :xs="12" :sm="6">
          <div class="action-card" @click="router.push('/orders')">
            <shopping-outlined class="action-icon" />
            <span>我的订单</span>
          </div>
        </a-col>
        <a-col :xs="12" :sm="6">
          <div class="action-card" @click="router.push('/favorites')">
            <heart-outlined class="action-icon" />
            <span>我的收藏</span>
          </div>
        </a-col>
        <a-col :xs="12" :sm="6">
          <div class="action-card" @click="router.push('/messages')">
            <message-outlined class="action-icon" />
            <span>消息中心</span>
          </div>
        </a-col>
      </a-row>
    </div>

    <div class="announcement-section" v-if="announcements.length > 0">
      <div class="section-header">
        <h2><notification-outlined /> 系统公告</h2>
        <router-link to="/announcements">查看全部</router-link>
      </div>
      <div class="announcement-list">
        <div 
          v-for="item in announcements" 
          :key="item.id" 
          class="announcement-item"
          @click="router.push(`/announcements/${item.id}`)"
        >
          <a-tag :color="getAnnouncementColor(item.type)">{{ getAnnouncementType(item.type) }}</a-tag>
          <span class="announcement-title">{{ item.title }}</span>
          <span class="announcement-time">{{ formatDate(item.createTime) }}</span>
        </div>
      </div>
    </div>

    <div class="category-section">
      <h2>商品分类</h2>
      <a-row :gutter="[16, 16]">
        <a-col :xs="8" :sm="6" :md="4" :lg="3" v-for="category in categories" :key="category.id">
          <div class="category-card" @click="handleCategoryClick(category.id)">
            <div class="category-icon" :style="{ background: category.color }">
              <component :is="category.icon" />
            </div>
            <span>{{ category.name }}</span>
          </div>
        </a-col>
      </a-row>
    </div>

    <div class="products-section">
      <div class="section-header">
        <h2>热门推荐</h2>
        <router-link to="/products?sort=hot">
          查看更多 <right-outlined />
        </router-link>
      </div>
      
      <a-spin :spinning="loading">
        <a-row :gutter="[16, 16]" v-if="hotProducts.length > 0">
          <a-col :xs="12" :sm="8" :md="6" :lg="4" v-for="product in hotProducts" :key="product.id">
            <product-card :product="product" @click="router.push(`/products/${product.id}`)" />
          </a-col>
        </a-row>
        <a-empty v-else description="暂无商品" />
      </a-spin>
    </div>

    <div class="products-section">
      <div class="section-header">
        <h2>最新发布</h2>
        <router-link to="/products?sort=new">
          查看更多 <right-outlined />
        </router-link>
      </div>
      
      <a-spin :spinning="loading">
        <a-row :gutter="[16, 16]" v-if="newProducts.length > 0">
          <a-col :xs="12" :sm="8" :md="6" :lg="4" v-for="product in newProducts" :key="product.id">
            <product-card :product="product" @click="router.push(`/products/${product.id}`)" />
          </a-col>
        </a-row>
        <a-empty v-else description="暂无商品" />
      </a-spin>
    </div>

    <div class="features-section">
      <h2>平台特色</h2>
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :sm="12" :md="6" v-for="feature in features" :key="feature.title">
          <a-card class="feature-card" hoverable>
            <div class="feature-icon">
              <component :is="feature.icon" />
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <div class="stats-section">
      <a-row :gutter="[24, 24]">
        <a-col :xs="12" :sm="6" v-for="stat in stats" :key="stat.label">
          <div class="stat-item">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { productApi } from '@/api'
import { announcementApi } from '@/types/extra'
import type { Product } from '@/types'
import type { Announcement } from '@/types/extra'
import ProductCard from '@/components/ProductCard.vue'
import {
  RightOutlined,
  MobileOutlined,
  LaptopOutlined,
  TabletOutlined,
  CustomerServiceOutlined,
  CameraOutlined,
  SafetyCertificateOutlined,
  ThunderboltOutlined,
  StarOutlined,
  TeamOutlined,
  ShoppingOutlined,
  HeartOutlined,
  PlusCircleOutlined,
  MessageOutlined,
  NotificationOutlined,
  DesktopOutlined,
  GiftOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const hotProducts = ref<Product[]>([])
const newProducts = ref<Product[]>([])
const announcements = ref<Announcement[]>([])

const categories = [
  { id: 1, name: '手机', icon: MobileOutlined, color: '#1890ff' },
  { id: 2, name: '电脑', icon: LaptopOutlined, color: '#52c41a' },
  { id: 3, name: '平板', icon: TabletOutlined, color: '#faad14' },
  { id: 4, name: '耳机', icon: CustomerServiceOutlined, color: '#eb2f96' },
  { id: 5, name: '相机', icon: CameraOutlined, color: '#722ed1' },
  { id: 6, name: '显示器', icon: DesktopOutlined, color: '#13c2c2' },
  { id: 7, name: '智能手表', icon: DesktopOutlined, color: '#fa8c16' },
  { id: 8, name: '配件', icon: GiftOutlined, color: '#2f54eb' }
]

const features = [
  {
    title: '安全可靠',
    description: '学号认证，确保用户身份真实可信',
    icon: SafetyCertificateOutlined
  },
  {
    title: '快速交易',
    description: '支持面交和快递，灵活便捷',
    icon: ThunderboltOutlined
  },
  {
    title: '信誉体系',
    description: '评价透明，交易更放心',
    icon: StarOutlined
  },
  {
    title: '校园专属',
    description: '专为校园用户打造的交易平台',
    icon: TeamOutlined
  }
]

const stats = [
  { value: '1000+', label: '注册用户' },
  { value: '500+', label: '在售商品' },
  { value: '200+', label: '成功交易' },
  { value: '99%', label: '好评率' }
]

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

const getAnnouncementType = (type: Announcement['type']) => {
  const map: Record<Announcement['type'], string> = {
    NORMAL: '公告',
    IMPORTANT: '重要',
    URGENT: '紧急'
  }
  return map[type]
}

const getAnnouncementColor = (type: Announcement['type']) => {
  const map: Record<Announcement['type'], string> = {
    NORMAL: 'blue',
    IMPORTANT: 'orange',
    URGENT: 'red'
  }
  return map[type]
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const [hotRes, newRes] = await Promise.all([
      productApi.getProducts({ page: 1, size: 8, sort: 'hot' }),
      productApi.getProducts({ page: 1, size: 8, sort: 'new' })
    ])
    hotProducts.value = hotRes.data.list
    newProducts.value = newRes.data.list
  } finally {
    loading.value = false
  }
}

const fetchAnnouncements = async () => {
  try {
    const res = await announcementApi.getList(1, 5)
    announcements.value = res.data.list
  } catch {
    // ignore
  }
}

const handleCategoryClick = (categoryId: number) => {
  router.push({ path: '/products', query: { categoryId: String(categoryId) } })
}

onMounted(() => {
  fetchProducts()
  fetchAnnouncements()
})
</script>

<style scoped>
.home-page {
  min-height: calc(100vh - 64px);
}

.hero-section {
  margin: -24px -24px 0;
}

.hero-carousel {
  height: 400px;
}

.carousel-item {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-item-1 {
  background: #18181b;
}

.carousel-item-2 {
  background: #27272a;
}

.carousel-item-3 {
  background: #3f3f46;
}

.carousel-content {
  text-align: center;
  color: #fff;
  padding: 24px;
}

.carousel-content h1 {
  font-size: 36px;
  font-weight: 600;
  margin-bottom: 16px;
}

.carousel-content p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 32px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.quick-actions {
  margin-top: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e4e4e7;
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  background: #f4f4f5;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-card:hover {
  background: #e4e4e7;
  transform: translateY(-2px);
}

.action-icon {
  font-size: 32px;
  color: #18181b;
}

.action-card span {
  font-size: 14px;
  color: #18181b;
  font-weight: 500;
}

.announcement-section {
  margin-top: 24px;
  padding: 20px 24px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e4e4e7;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h2 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #18181b;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-header a {
  color: #18181b;
  font-size: 14px;
}

.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.announcement-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.announcement-item:hover {
  background: #f4f4f5;
}

.announcement-title {
  flex: 1;
  font-size: 14px;
  color: #18181b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.announcement-time {
  font-size: 12px;
  color: #71717a;
}

.category-section,
.products-section,
.features-section {
  margin-top: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e4e4e7;
}

.category-section h2,
.products-section h2,
.features-section h2 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #18181b;
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px 12px;
  background: #f4f4f5;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-card:hover {
  transform: translateY(-4px);
  background: #e4e4e7;
}

.category-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 24px;
  color: #fff;
}

.category-card span {
  font-size: 14px;
  color: #18181b;
  font-weight: 500;
}

.feature-card {
  text-align: center;
  border-radius: 12px;
  border: 1px solid #e4e4e7;
}

.feature-card :deep(.ant-card-body) {
  padding: 32px 24px;
}

.feature-icon {
  font-size: 48px;
  color: #18181b;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #18181b;
}

.feature-card p {
  color: #71717a;
  margin: 0;
}

.stats-section {
  margin-top: 24px;
  background: #18181b;
  padding: 48px 24px;
  border-radius: 12px;
}

.stat-item {
  text-align: center;
  color: #fff;
}

.stat-value {
  font-size: 36px;
  font-weight: 600;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.7;
}

@media (max-width: 768px) {
  .hero-carousel,
  .carousel-item {
    height: 280px;
  }

  .carousel-content h1 {
    font-size: 24px;
  }

  .carousel-content p {
    font-size: 14px;
  }

  .hero-actions {
    flex-direction: column;
    align-items: center;
  }

  .quick-actions {
    padding: 16px;
  }

  .action-card {
    padding: 16px;
  }

  .action-icon {
    font-size: 24px;
  }

  .category-section,
  .products-section,
  .features-section,
  .announcement-section {
    padding: 16px;
  }

  .stat-value {
    font-size: 24px;
  }
}
</style>
