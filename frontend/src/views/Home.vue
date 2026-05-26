<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="section-inner">
        <div class="hero-content">
          <h1 class="hero-title">宠物纪念</h1>
          <p class="hero-subtitle">让爱与记忆永恒</p>
          <p class="hero-description">
            我们提供专业、温馨的宠物殡葬服务，用心守护每一个生命的尊严，让您的爱宠得到最好的告别。
          </p>
          <div class="hero-actions">
            <a-button type="primary" size="large" @click="$router.push('/services')">
              查看服务套餐
            </a-button>
            <a-button size="large" @click="$router.push('/memorials')">
              浏览纪念相册
            </a-button>
          </div>
        </div>
        <div class="hero-image">
          <div class="image-placeholder">
            <heart-outlined class="placeholder-icon" />
          </div>
        </div>
      </div>
    </section>

    <section class="features-section">
      <div class="section-inner">
        <h2 class="section-title">我们的服务</h2>
        <p class="section-subtitle">专业、温馨、有尊严的告别仪式</p>
        
        <a-row :gutter="[24, 24]" class="features-grid">
          <a-col :xs="24" :sm="12" :lg="6" v-for="feature in features" :key="feature.title" class="grid-col">
            <div class="feature-card">
              <div class="feature-icon">
                <component :is="feature.icon" />
              </div>
              <h3 class="feature-title">{{ feature.title }}</h3>
              <p class="feature-description">{{ feature.description }}</p>
            </div>
          </a-col>
        </a-row>
      </div>
    </section>

    <section class="services-section">
      <div class="section-inner">
        <h2 class="section-title">服务套餐</h2>
        <p class="section-subtitle">为您的爱宠选择最合适的告别方式</p>
        
        <a-row :gutter="[24, 24]" class="services-grid">
          <a-col :xs="24" :sm="12" :lg="8" v-for="pkg in packages" :key="pkg.id" class="grid-col">
            <div class="service-card" :class="{ 'service-card-featured': pkg.featured }">
              <div v-if="pkg.featured" class="featured-badge">推荐</div>
              <h3 class="service-name">{{ pkg.name }}</h3>
              <div class="service-price">
                <span class="price-amount">¥{{ pkg.price }}</span>
                <span class="price-unit">起</span>
              </div>
              <p class="service-description">{{ pkg.description }}</p>
              <ul class="service-features">
                <li v-for="(item, index) in pkg.features" :key="index">
                  <check-outlined class="check-icon" />
                  {{ item }}
                </li>
              </ul>
              <a-button 
                :type="pkg.featured ? 'primary' : 'default'" 
                block 
                @click="$router.push('/services')"
              >
                了解详情
              </a-button>
            </div>
          </a-col>
        </a-row>
      </div>
    </section>

    <section class="stats-section">
      <div class="section-inner">
        <a-row :gutter="[24, 24]" class="stats-grid">
          <a-col :xs="12" :sm="6" v-for="stat in stats" :key="stat.label">
            <div class="stat-card">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </a-col>
        </a-row>
      </div>
    </section>

    <section class="cta-section">
      <div class="section-inner">
        <div class="cta-content">
          <h2 class="cta-title">让我们陪伴您度过这段时光</h2>
          <p class="cta-description">
            如果您正在经历失去爱宠的痛苦，我们愿意为您提供帮助和支持。
          </p>
          <a-button type="primary" size="large" @click="$router.push('/register')">
            立即预约
          </a-button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { 
  HeartOutlined, 
  CheckOutlined,
  CustomerServiceOutlined,
  SafetyCertificateOutlined,
  EnvironmentOutlined,
  ClockCircleOutlined
} from '@ant-design/icons-vue'

const features = ref([
  {
    icon: CustomerServiceOutlined,
    title: '专业服务',
    description: '经验丰富的专业团队，提供全程贴心服务'
  },
  {
    icon: SafetyCertificateOutlined,
    title: '品质保障',
    description: '严格的服务流程，确保每一个细节的完美'
  },
  {
    icon: EnvironmentOutlined,
    title: '温馨环境',
    description: '安静优雅的告别场所，让告别更有尊严'
  },
  {
    icon: ClockCircleOutlined,
    title: '透明流程',
    description: '实时追踪服务进度，让您安心放心'
  }
])

const packages = ref([
  {
    id: 1,
    name: '基础套餐',
    price: '999',
    description: '简约而庄重的告别仪式',
    features: ['专车接送', '告别仪式', '火化服务', '骨灰盒'],
    featured: false
  },
  {
    id: 2,
    name: '标准套餐',
    price: '1999',
    description: '完整的告别服务流程',
    features: ['专车接送', '告别仪式', '火化服务', '精美骨灰盒', '纪念证书', '追思相册'],
    featured: true
  },
  {
    id: 3,
    name: '尊享套餐',
    price: '3999',
    description: '全方位的纪念服务',
    features: ['专车接送', '私人告别厅', '火化服务', '定制骨灰盒', '纪念证书', '追思相册', '永久纪念馆'],
    featured: false
  }
])

const stats = ref([
  { value: '5000+', label: '服务家庭' },
  { value: '99%', label: '满意度' },
  { value: '24h', label: '响应时间' },
  { value: '7天', label: '无理由退款' }
])
</script>

<style scoped>
.home-page {
  background: #fff;
}

.section-inner {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.hero-section {
  padding: 56px 0 72px;
}

.hero-section .section-inner {
  min-height: 520px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 40px;
}

.hero-content {
  flex: 1;
  max-width: 560px;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  color: #262626;
  margin-bottom: 16px;
  letter-spacing: 2px;
}

.hero-subtitle {
  font-size: 24px;
  color: #595959;
  margin-bottom: 24px;
}

.hero-description {
  font-size: 16px;
  color: #8c8c8c;
  line-height: 1.8;
  margin-bottom: 40px;
}

.hero-actions {
  display: flex;
  gap: 16px;
}

.hero-image {
  flex: 1;
  display: flex;
  justify-content: center;
}

.image-placeholder {
  width: 400px;
  height: 400px;
  background: linear-gradient(145deg, #ffffff 0%, #f2f2f2 100%);
  border: 1px solid #f0f0f0;
  box-shadow: 0 16px 36px rgba(0, 0, 0, 0.06);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 120px;
  color: #d9d9d9;
}

.section-title {
  font-size: 32px;
  font-weight: 600;
  color: #262626;
  text-align: center;
  margin-bottom: 12px;
}

.section-subtitle {
  font-size: 16px;
  color: #8c8c8c;
  text-align: center;
  margin-bottom: 48px;
}

.features-section {
  padding: 80px 0;
  background: #fafafa;
}

.grid-col {
  display: flex;
}

.feature-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  padding: 32px;
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.feature-card:hover {
  transform: translateY(-3px);
  border-color: #d9d9d9;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.06);
}

.feature-icon {
  width: 64px;
  height: 64px;
  background: #f5f5f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 28px;
  color: #262626;
}

.feature-title {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 12px;
}

.feature-description {
  font-size: 14px;
  color: #8c8c8c;
  line-height: 1.6;
}

.services-section {
  padding: 80px 0;
}

.service-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  padding: 32px;
  transition: all 0.3s;
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.service-card:hover {
  border-color: #d9d9d9;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.06);
}

.service-card-featured {
  border-color: #262626;
}

.featured-badge {
  position: absolute;
  top: -12px;
  right: 24px;
  background: #262626;
  color: #fff;
  padding: 4px 16px;
  border-radius: 12px;
  font-size: 12px;
}

.service-name {
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 16px;
}

.service-price {
  margin-bottom: 16px;
}

.price-amount {
  font-size: 36px;
  font-weight: 700;
  color: #262626;
}

.price-unit {
  font-size: 14px;
  color: #8c8c8c;
  margin-left: 4px;
}

.service-description {
  font-size: 14px;
  color: #8c8c8c;
  margin-bottom: 24px;
  line-height: 1.6;
}

.service-features {
  list-style: none;
  padding: 0;
  margin: 0 0 24px;
  flex: 1;
}

.service-features li {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #595959;
  margin-bottom: 12px;
}

.check-icon {
  color: #595959;
  margin-right: 8px;
}

.stats-section {
  padding: 60px 0;
  background: #fafafa;
}

.stat-card {
  text-align: center;
  padding: 24px;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #262626;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #8c8c8c;
}

.cta-section {
  padding: 80px 0;
  background: linear-gradient(180deg, #ffffff 0%, #fafafa 100%);
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  text-align: center;
}

.cta-content {
  max-width: 600px;
  margin: 0 auto;
}

.cta-title {
  font-size: 32px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 16px;
}

.cta-description {
  font-size: 16px;
  color: #8c8c8c;
  margin-bottom: 32px;
}

@media (max-width: 768px) {
  .hero-section {
    padding: 40px 0;
  }

  .hero-section .section-inner {
    flex-direction: column;
    text-align: center;
    min-height: auto;
  }
  
  .hero-title {
    font-size: 32px;
  }
  
  .hero-subtitle {
    font-size: 18px;
  }
  
  .hero-actions {
    justify-content: center;
  }
  
  .image-placeholder {
    width: 280px;
    height: 280px;
    margin-top: 40px;
  }
  
  .placeholder-icon {
    font-size: 80px;
  }
}
</style>
