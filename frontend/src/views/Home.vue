<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useCartStore } from '../stores/cart'
import { productApi } from '../api'
import type { Product } from '../api'
import ProductImage from '../components/ProductImage.vue'
import { getPrimaryImageUrl } from '../utils/imageLoader'
import {
  ShoppingCartOutlined,
  DownOutlined,
  SearchOutlined,
  FireOutlined,
  StarOutlined,
  AppstoreOutlined,
  HomeOutlined,
  FileTextOutlined,
  DashboardOutlined,
  LogoutOutlined,
  PlusOutlined,
  HeartOutlined,
  ShopOutlined,
  RightOutlined
} from '@ant-design/icons-vue'

const flyingItem = ref<{ show: boolean; x: number; y: number; imageUrl: string }>({
  show: false,
  x: 0,
  y: 0,
  imageUrl: ''
})

const categories = [
  { key: 'all', name: '全部商品', icon: '🛍️' },
  { key: 'vegetables', name: '新鲜蔬菜', icon: '🥬' },
  { key: 'fruits', name: '时令水果', icon: '🍎' },
  { key: 'grains', name: '粮油米面', icon: '🌾' },
  { key: 'meat', name: '肉禽蛋品', icon: '🥩' },
  { key: 'seafood', name: '水产海鲜', icon: '🦐' },
  { key: 'snacks', name: '休闲零食', icon: '🍪' },
  { key: 'drinks', name: '酒水饮料', icon: '🥤' }
]

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const products = ref<Product[]>([])
const loading = ref(false)
const searchQuery = ref('')

onMounted(async () => {
  loading.value = true
  try {
    products.value = await productApi.getAll()
  } finally {
    loading.value = false
  }
})

const filteredProducts = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  const list = q
    ? products.value.filter((p) => p.name.toLowerCase().includes(q))
    : products.value
  return list.slice(0, 12)
})

const addToCart = async (product: Product, event: MouseEvent) => {
  const startX = event.clientX
  const startY = event.clientY

  const cartIcon = document.querySelector('.cart-icon-badge')
  if (!cartIcon) {
    cartStore.addProduct(product, 1)
    return
  }

  const cartRect = cartIcon.getBoundingClientRect()
  const endX = cartRect.left + cartRect.width / 2
  const endY = cartRect.top + cartRect.height / 2

  flyingItem.value = { show: true, x: startX, y: startY, imageUrl: getPrimaryImageUrl(product) }
  await nextTick()

  const flyingEl = document.querySelector('.flying-item') as HTMLElement
  if (flyingEl) {
    flyingEl.style.left = `${startX}px`
    flyingEl.style.top = `${startY}px`
    flyingEl.offsetHeight
    flyingEl.style.transition = 'all 0.6s cubic-bezier(0.2, 0.8, 0.2, 1)'
    flyingEl.style.left = `${endX}px`
    flyingEl.style.top = `${endY}px`
    flyingEl.style.transform = 'scale(0.2)'
    flyingEl.style.opacity = '0'
  }

  setTimeout(() => {
    cartStore.addProduct(product, 1)
    flyingItem.value.show = false
  }, 600)
}

const goToCart = () => router.push('/cart')
const goToOrders = () => router.push('/orders')
const goToFavorites = () => router.push('/favorites')
const goToLogin = () => router.push('/login')
const goToAdmin = () => router.push('/admin/dashboard')
const goToStore = () => router.push('/store/dashboard')
const goToStoreRegister = () => router.push('/store/register')

const goToProductList = (category?: string) => {
  if (category && category !== 'all') router.push(`/product/list?category=${category}`)
  else router.push('/product/list')
}
const goToNewProducts = () => router.push('/product/new')
const goToHotProducts = () => router.push('/product/hot')

const logout = () => {
  userStore.logout()
  router.push('/login')
}

const onSearchEnter = () => {
  // 你原来是 goToProductList()，我保留逻辑
  goToProductList()
}

const formatPrice = (price: number) => `￥${price?.toFixed(2) || '0.00'}`
</script>

<template>
  <div class="home-page">
    <!-- Top Nav -->
    <header class="topbar">
      <div class="topbar-inner">
        <div class="brand" @click="$router.push('/')">
          <div class="brand-logo">
            <ShoppingCartOutlined />
          </div>
          <div class="brand-text">
            <div class="brand-title">农产品商城</div>
            <div class="brand-subtitle">新鲜直达 · 简洁好用</div>
          </div>
        </div>

        <div class="nav-links">
          <a-button type="text" @click="$router.push('/')"><HomeOutlined /> 首页</a-button>
          <a-dropdown>
            <a-button type="text">
              <AppstoreOutlined /> 分类 <DownOutlined style="font-size: 11px; color:#9ca3af;" />
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item v-for="cat in categories" :key="cat.key" @click="goToProductList(cat.key)">
                  <span style="margin-right: 8px;">{{ cat.icon }}</span>{{ cat.name }}
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
          <a-button type="text" @click="goToNewProducts"><StarOutlined /> 新品</a-button>
          <a-button type="text" @click="goToHotProducts"><FireOutlined /> 热销</a-button>
        </div>

        <div class="search">
          <a-input v-model:value="searchQuery" allow-clear placeholder="搜索商品名称..." @pressEnter="onSearchEnter">
            <template #prefix><SearchOutlined /></template>
          </a-input>
        </div>

        <div class="actions">
          <div class="cart-icon-badge cart" @click="goToCart" title="购物车">
            <ShoppingCartOutlined />
            <span v-if="cartStore.totalItems > 0" class="badge">{{ cartStore.totalItems }}</span>
          </div>

          <template v-if="userStore.isLoggedIn">
            <a-dropdown>
              <div class="user-chip">
                <span class="avatar">{{ userStore.username?.[0]?.toUpperCase() }}</span>
                <span class="username">{{ userStore.username }}</span>
                <DownOutlined style="font-size: 11px; color:#9ca3af;" />
              </div>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="goToOrders">
                    <template #icon><FileTextOutlined /></template>
                    我的订单
                  </a-menu-item>
                  <a-menu-item @click="goToFavorites">
                    <template #icon><HeartOutlined /></template>
                    我的收藏
                  </a-menu-item>
                  <a-menu-item v-if="userStore.isAdmin" @click="goToAdmin">
                    <template #icon><DashboardOutlined /></template>
                    管理后台
                  </a-menu-item>
                  <!-- 有店铺且审核通过或审核中 (状态0或1)，或者是管理员 -->
                  <a-menu-item v-if="userStore.isAdmin || (userStore.hasStore === true && Number(userStore.storeStatus) !== 2)" @click="goToStore">
                    <template #icon><ShopOutlined /></template>
                    店家管理
                  </a-menu-item>
                  <!-- 有店铺但被驳回 (状态2) -->
                  <a-menu-item v-else-if="userStore.hasStore === true && Number(userStore.storeStatus) === 2" @click="goToStoreRegister">
                    <template #icon><PlusOutlined /></template>
                    重新申请
                  </a-menu-item>
                  <!-- 没有店铺且不是管理员 -->
                  <a-menu-item v-else-if="!userStore.isAdmin" @click="goToStoreRegister">
                    <template #icon><PlusOutlined /></template>
                    申请开店
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item @click="logout" danger>
                    <template #icon><LogoutOutlined /></template>
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>

          <a-button v-else type="primary" @click="goToLogin">登录</a-button>
        </div>
      </div>
    </header>

    <main class="container">
      <!-- Simple Hero -->
      <a-card class="hero" :bordered="false">
        <div class="hero-inner">
          <div>
            <h1 class="hero-title">新鲜农产品，简单好买</h1>
            <p class="hero-desc">
              严选产地与品质，支持快捷配送。用更清爽的界面，买到更放心的食材。
            </p>
            <div class="hero-actions">
              <a-button type="primary" size="large" @click="goToHotProducts">
                <FireOutlined /> 去逛热销
              </a-button>
              <a-button size="large" @click="goToNewProducts">
                <StarOutlined /> 看新品
              </a-button>
            </div>
          </div>

          <div class="hero-metrics">
            <div class="metric">
              <div class="metric-value">{{ products.length }}</div>
              <div class="metric-label">在售商品</div>
            </div>
            <div class="metric">
              <div class="metric-value">{{ categories.length - 1 }}</div>
              <div class="metric-label">分类</div>
            </div>
            <div class="metric">
              <div class="metric-value">24h</div>
              <div class="metric-label">发货</div>
            </div>
          </div>
        </div>
      </a-card>

      <!-- Categories -->
      <a-card class="section" :bordered="false">
        <div class="section-head">
          <div>
            <div class="section-title">商品分类</div>
            <div class="section-subtitle">快速定位你想买的品类</div>
          </div>
          <a-button type="link" @click="goToProductList()">查看全部 <RightOutlined /></a-button>
        </div>

        <div class="cat-grid">
          <div v-for="cat in categories" :key="cat.key" class="cat-item" @click="goToProductList(cat.key)">
            <div class="cat-icon">{{ cat.icon }}</div>
            <div class="cat-name">{{ cat.name }}</div>
          </div>
        </div>
      </a-card>

      <!-- Products -->
      <a-card class="section" :bordered="false">
        <div class="section-head">
          <div>
            <div class="section-title">精选农产品</div>
            <div class="section-subtitle">严选优质产地，新鲜直达</div>
          </div>
          <a-button type="link" @click="goToProductList()">查看全部 <RightOutlined /></a-button>
        </div>

        <a-spin :spinning="loading" size="large">
          <a-empty v-if="filteredProducts.length === 0 && !loading" description="暂无商品" />

          <div v-else class="product-grid">
            <a-card
              v-for="p in filteredProducts"
              :key="p.id"
              class="product-card"
              :bordered="false"
              @click="$router.push(`/product/${p.id}`)"
            >
              <div class="cover">
                <ProductImage :product="p" :alt="p.name" height="180" border-radius="8px 8px 0 0" />
                <div v-if="p.stock <= 0" class="soldout">已售罄</div>
              </div>

              <div class="info">
                <div class="name" :title="p.name">{{ p.name }}</div>
                <div class="desc">{{ p.description || '暂无描述' }}</div>

                <div class="bottom">
                  <div>
                    <div class="price">{{ formatPrice(p.price) }}</div>
                    <div class="stock">库存 {{ p.stock }}</div>
                  </div>

                  <a-button
                    type="primary"
                    shape="circle"
                    :disabled="p.stock <= 0"
                    @click.stop="addToCart(p, $event)"
                    title="加入购物车"
                  >
                    <PlusOutlined />
                  </a-button>
                </div>
              </div>
            </a-card>
          </div>
        </a-spin>
      </a-card>
    </main>

    <footer class="footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <div class="brand-logo small"><ShoppingCartOutlined /></div>
          <div>
            <div class="footer-title">农产品商城</div>
            <div class="footer-sub">新鲜直达，品质生活</div>
          </div>
        </div>
        <div class="footer-copy">© 2026 农产品商城</div>
      </div>
    </footer>

    <!-- Flying item -->
    <Teleport to="body">
      <div
        v-if="flyingItem.show"
        class="flying-item"
        :style="{ left: `${flyingItem.x}px`, top: `${flyingItem.y}px`, transform: 'translate(-50%, -50%)' }"
      >
        <div class="flying-inner">
          <img v-if="flyingItem.imageUrl" :src="flyingItem.imageUrl" alt="商品" />
          <ShoppingCartOutlined v-else />
        </div>
      </div>
    </Teleport>
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f8fafc;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 50;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #e5e7eb;
}

.topbar-inner {
  max-width: 1440px;
  margin: 0 auto;
  padding: 12px 16px;
  display: grid;
  grid-template-columns: 320px 1fr 420px auto;
  gap: 12px;
  align-items: center;
}

.brand {
  display: flex;
  gap: 12px;
  align-items: center;
  cursor: pointer;
}

.brand-logo {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #10b981, #059669);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.brand-logo.small {
  width: 34px;
  height: 34px;
  border-radius: 10px;
}

.brand-title {
  font-weight: 900;
  color: #111827;
  line-height: 1.1;
}
.brand-subtitle {
  font-size: 12px;
  color: #6b7280;
}

.nav-links {
  display: none;
  gap: 4px;
  align-items: center;
}
@media (min-width: 1024px) {
  .nav-links {
    display: flex;
  }
}

.search {
  display: none;
}
@media (min-width: 768px) {
  .search {
    display: block;
  }
}

.actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
}

.cart {
  position: relative;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4b5563;
  cursor: pointer;
}
.cart:hover {
  border-color: rgba(16, 185, 129, 0.35);
  background: rgba(16, 185, 129, 0.06);
  color: #059669;
}
.badge {
  position: absolute;
  top: -6px;
  right: -6px;
  min-width: 18px;
  height: 18px;
  padding: 0 6px;
  border-radius: 999px;
  background: #ef4444;
  color: #fff;
  font-size: 11px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #fff;
}

.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  border: 1px solid #e5e7eb;
  border-radius: 999px;
  cursor: pointer;
  background: #fff;
}
.user-chip:hover {
  border-color: rgba(16, 185, 129, 0.35);
  background: rgba(16, 185, 129, 0.06);
}
.avatar {
  width: 26px;
  height: 26px;
  border-radius: 999px;
  background: #111827;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 800;
}
.username {
  color: #374151;
  font-weight: 700;
  font-size: 13px;
}

.container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 16px;
}

.hero {
  border-radius: 16px;
  margin-top: 14px;
  border: 1px solid rgba(15, 23, 42, 0.06);
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.06);
}

.hero-inner {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 20px;
  align-items: center;
}

.hero-title {
  margin: 0;
  font-size: 28px;
  font-weight: 900;
  color: #111827;
}

.hero-desc {
  margin: 10px 0 0;
  color: #6b7280;
  line-height: 1.7;
}

.hero-actions {
  margin-top: 14px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.hero-metrics {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}
.metric {
  border: 1px solid #e5e7eb;
  background: #fff;
  border-radius: 14px;
  padding: 12px;
}
.metric-value {
  font-weight: 900;
  font-size: 18px;
  color: #111827;
}
.metric-label {
  font-size: 12px;
  color: #6b7280;
}

.section {
  border-radius: 16px;
  margin-top: 14px;
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 12px;
  margin-bottom: 12px;
}

.section-title {
  font-weight: 900;
  color: #111827;
  font-size: 18px;
}
.section-subtitle {
  font-size: 13px;
  color: #6b7280;
  margin-top: 6px;
}

.cat-grid {
  display: grid;
  grid-template-columns: repeat(8, minmax(0, 1fr));
  gap: 10px;
}
.cat-item {
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 12px 10px;
  background: #fff;
  cursor: pointer;
  text-align: center;
  transition: all 0.2s;
}
.cat-item:hover {
  border-color: rgba(16, 185, 129, 0.35);
  background: rgba(16, 185, 129, 0.06);
  transform: translateY(-1px);
}
.cat-icon {
  font-size: 22px;
}
.cat-name {
  margin-top: 8px;
  font-size: 12px;
  color: #4b5563;
  font-weight: 700;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.product-card {
  border: 1px solid rgba(15, 23, 42, 0.06);
  border-radius: 16px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.06);
}
.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.10);
}

.cover {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  background: #f3f4f6;
  border-radius: 16px 16px 0 0;
  overflow: hidden;
}
.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.cover-empty {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  font-size: 32px;
}
.soldout {
  position: absolute;
  inset: 0;
  background: rgba(17, 24, 39, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 900;
}

.info {
  padding: 12px 14px 14px;
}
.name {
  font-weight: 900;
  color: #111827;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.desc {
  margin-top: 6px;
  color: #6b7280;
  font-size: 12px;
  line-height: 1.6;
  height: 38px;
  overflow: hidden;
}
.bottom {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}
.price {
  font-weight: 900;
  color: #ef4444;
}
.stock {
  margin-top: 4px;
  font-size: 12px;
  color: #9ca3af;
}

.footer {
  margin-top: 18px;
  border-top: 1px solid #e5e7eb;
  background: #fff;
}
.footer-inner {
  max-width: 1440px;
  margin: 0 auto;
  padding: 18px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #6b7280;
}
.footer-brand {
  display: flex;
  align-items: center;
  gap: 10px;
}
.footer-title {
  font-weight: 900;
  color: #111827;
}
.footer-sub {
  font-size: 12px;
}
.footer-copy {
  font-size: 12px;
}

/* Responsive */
@media (max-width: 1024px) {
  .topbar-inner {
    grid-template-columns: 260px 1fr auto;
  }
  .hero-inner {
    grid-template-columns: 1fr;
  }
  .hero-metrics {
    grid-template-columns: repeat(3, 1fr);
  }
  .cat-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
  .product-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
@media (max-width: 640px) {
  .search {
    display: none;
  }
  .topbar-inner {
    grid-template-columns: 1fr auto;
  }
  .brand-text {
    display: none;
  }
  .cat-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
  .product-grid {
    grid-template-columns: 1fr;
  }
}

/* Flying */
.flying-item {
  position: fixed;
  z-index: 9999;
  pointer-events: none;
  will-change: transform, left, top, opacity;
}
.flying-inner {
  width: 56px;
  height: 56px;
  border-radius: 999px;
  background: #fff;
  border: 3px solid #ef4444;
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.18);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ef4444;
}
.flying-inner img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
