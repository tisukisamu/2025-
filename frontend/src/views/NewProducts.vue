<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { productApi } from '../api'
import type { Product } from '../api'
import { message } from 'ant-design-vue'
import {
  ArrowLeftOutlined,
  GiftOutlined,
  ShoppingCartOutlined,
  ThunderboltOutlined,
  StarOutlined,
  SearchOutlined
} from '@ant-design/icons-vue'
import { useCartStore } from '../stores/cart'
import FlyToCart from '../components/FlyToCart.vue'

type SortKey = 'rank' | 'sales' | 'priceAsc' | 'priceDesc' | 'stock'

const router = useRouter()
const cartStore = useCartStore()
const flyRef = ref<InstanceType<typeof FlyToCart> | null>(null)

// ✅ badge 弹跳
const badgeBump = ref(false)
const bumpBadge = () => {
  badgeBump.value = false
  requestAnimationFrame(() => {
    badgeBump.value = true
    window.setTimeout(() => (badgeBump.value = false), 320)
  })
}

const products = ref<Product[]>([])
const loading = ref(false)

const keyword = ref('')
const onlyInStock = ref(true)
const minPrice = ref<number | null>(null)
const maxPrice = ref<number | null>(null)
const sortKey = ref<SortKey>('rank')

onMounted(async () => {
  await loadNewProducts()
})

const loadNewProducts = async () => {
  loading.value = true
  try {
    products.value = await productApi.getNew()
  } catch (error) {
    message.error('加载新品失败')
  } finally {
    loading.value = false
  }
}

const filtered = computed(() => {
  const q = keyword.value.trim().toLowerCase()

  let arr = products.value.filter((p) => {
    if (onlyInStock.value && (p.stock ?? 0) <= 0) return false
    if (q && !p.name?.toLowerCase().includes(q)) return false

    const price = Number(p.price ?? 0)
    if (minPrice.value != null && price < minPrice.value) return false
    if (maxPrice.value != null && price > maxPrice.value) return false

    return true
  })

  arr = [...arr].sort((a, b) => {
    switch (sortKey.value) {
      case 'rank':
        // 新品按创建时间倒序（最新的在前）
        return new Date(b.createTime || 0).getTime() - new Date(a.createTime || 0).getTime()
      case 'sales':
        return (b.sales ?? 0) - (a.sales ?? 0)
      case 'stock':
        return (b.stock ?? 0) - (a.stock ?? 0)
      case 'priceAsc':
        return (a.price ?? 0) - (b.price ?? 0)
      case 'priceDesc':
        return (b.price ?? 0) - (a.price ?? 0)
      default:
        return 0
    }
  })

  // 添加序号
  return arr.map((p, i) => ({ ...p, rank: i + 1 }))
})

const goToProductDetail = (id: number) => router.push(`/product/${id}`)
const formatPrice = (price: number) => `￥${price?.toFixed(2) || '0.00'}`

const addToCart = (p: Product, ev?: MouseEvent) => {
  if ((p.stock ?? 0) <= 0) {
    message.warning('该商品已售罄')
    return
  }

  const target = document.querySelector('.cart-fly-target') as HTMLElement | null

  if (flyRef.value && ev && target) {
    const rect = target.getBoundingClientRect()

    flyRef.value.fly({
      imageUrl: p.imageUrl,
      startX: ev.clientX,
      startY: ev.clientY,
      endX: rect.left + rect.width / 2,
      endY: rect.top + rect.height / 2,
      duration: 650
    })

    window.setTimeout(() => {
      cartStore.addProduct(p, 1)
      bumpBadge()
      message.success('已加入购物车')
    }, 650)

    return
  }

  cartStore.addProduct(p, 1)
  bumpBadge()
  message.success('已加入购物车')
}

const resetFilters = () => {
  keyword.value = ''
  onlyInStock.value = true
  minPrice.value = null
  maxPrice.value = null
  sortKey.value = 'rank'
}
</script>

<template>
  <div class="page">
    <header class="topbar">
      <div class="topbar-inner">
        <div class="topbar-left">
          <a-button type="text" @click="router.push('/')">
            <ArrowLeftOutlined /> 返回
          </a-button>
        </div>

        <div class="topbar-center">
          <div class="title">
            <GiftOutlined class="title-icon" />
            <span>新品上市</span>
          </div>
        </div>

        <div class="topbar-right">
          <!-- ✅ 购物车按钮 + 红点徽章 + 动画终点锚点 -->
          <a-button class="cart-fly-target cart-btn" type="text" @click="router.push('/cart')">
            <ShoppingCartOutlined />
            <span
              v-if="cartStore.totalItems > 0"
              class="cart-badge"
              :class="{ 'badge-bump': badgeBump }"
            >
              {{ cartStore.totalItems > 99 ? '99+' : cartStore.totalItems }}
            </span>
          </a-button>
        </div>
      </div>
    </header>

    <main class="container">
      <a-card :bordered="false" class="head-card">
        <div class="head-row" >
          <div>
            <div class="sub">发现最新上架的优质商品</div>
          </div>
          <a-tag color="blue"><ThunderboltOutlined /> NEW</a-tag>
        </div>
      </a-card>

      <a-card :bordered="false" class="filter-card">
        <a-row :gutter="[12, 12]" align="middle">
          <a-col :xs="24" :sm="12" :md="8" :lg="8">
            <a-input v-model:value="keyword" allow-clear placeholder="搜索商品名称...">
              <template #prefix><SearchOutlined /></template>
            </a-input>
          </a-col>

          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-select v-model:value="sortKey" style="width: 100%" placeholder="排序方式">
              <a-select-option value="rank">默认（最新上架）</a-select-option>
              <a-select-option value="sales">按销量</a-select-option>
              <a-select-option value="stock">按库存</a-select-option>
              <a-select-option value="priceAsc">价格从低到高</a-select-option>
              <a-select-option value="priceDesc">价格从高到低</a-select-option>
            </a-select>
          </a-col>

          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <div class="price-range">
              <a-input-number v-model:value="minPrice" :min="0" placeholder="最低价" style="width: 100%" />
              <span class="price-sep">-</span>
              <a-input-number v-model:value="maxPrice" :min="0" placeholder="最高价" style="width: 100%" />
            </div>
          </a-col>

          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <div class="filter-right">
              <a-switch v-model:checked="onlyInStock" checked-children="有货" un-checked-children="全部" />
              <a-button @click="resetFilters">重置</a-button>
            </div>
          </a-col>
        </a-row>
      </a-card>

      <a-card :bordered="false" class="list-card">
        <a-spin :spinning="loading" size="large">
          <a-empty v-if="filtered.length === 0 && !loading" description="没有符合条件的商品" />

          <div v-else class="grid">
            <a-card
              v-for="p in filtered"
              :key="p.id"
              :bordered="false"
              class="item"
              @click="goToProductDetail(p.id)"
            >
              <div class="rank">
                <span class="rank-num">{{ (p as any).rank }}</span>
              </div>

              <div class="cover">
                <img v-if="p.imageUrl" :src="p.imageUrl" :alt="p.name" />
                <div v-else class="cover-empty">无图</div>
                <div v-if="(p.stock ?? 0) <= 0" class="soldout">售罄</div>
              </div>

              <div class="info">
                <div class="name" :title="p.name">{{ p.name }}</div>
                <div class="desc">{{ p.description || '暂无描述' }}</div>

                <div class="bottom">
                  <div class="left">
                    <div class="price">{{ formatPrice(p.price) }}</div>
                    <div class="meta">
                      <span>销量 {{ p.sales || 0 }}</span>
                      <span class="dot">·</span>
                      <span>库存 {{ p.stock }}</span>
                    </div>
                  </div>

                  <div class="right">
                    <div class="rate">
                      <StarOutlined />
                      <span>4.9</span>
                    </div>
                    <a-button
                      type="primary"
                      size="small"
                      class="add"
                      :disabled="(p.stock ?? 0) <= 0"
                      @click.stop="addToCart(p, $event)"
                    >
                      加购
                    </a-button>
                  </div>
                </div>
              </div>
            </a-card>
          </div>
        </a-spin>
      </a-card>
    </main>

    <FlyToCart ref="flyRef" />
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: #f8fafc;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 20;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #e5e7eb;
}
.topbar-inner {
  max-width: 1440px;
  margin: 0 auto;
  padding: 10px 16px;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
}
.topbar-left { justify-self: start; }
.topbar-center { justify-self: center; }
.topbar-right { justify-self: end; }

.title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 900;
  color: #111827;
}
.title-icon { color: #3b82f6; }

.container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 16px;
}

.head-card,
.filter-card,
.list-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.06);
}
.head-card { margin-top: 14px; }

.head-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: start;
  gap: 12px;
}
.head-row > div:first-child { min-width: 0; }
.h1 { font-size: 18px; font-weight: 900; color: #111827; line-height: 1.25; }
.sub { margin-top: 6px; font-size: 13px; color: #6b7280; line-height: 1.5; }
.head-row :deep(.ant-tag) {
  justify-self: end;
  align-self: start;
  white-space: nowrap;
  margin-inline-end: 0;
}
@media (max-width: 420px) {
  .head-row { grid-template-columns: 1fr; }
  .head-row :deep(.ant-tag) { justify-self: start; }
}

.filter-card { margin-top: 14px; }
.price-range {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 8px;
  align-items: center;
}
.price-sep { color: #9ca3af; font-weight: 700; }
.filter-right {
  display: flex;
  gap: 10px;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.list-card { margin-top: 14px; }
.grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}
.item {
  border: 1px solid rgba(15, 23, 42, 0.06);
  border-radius: 16px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.06);
  overflow: hidden;
  position: relative;
}
.item:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.1);
}

.rank {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 4;
}
.rank-num {
  width: 30px;
  height: 30px;
  border-radius: 999px;
  background: #111827;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 12px;
}

.cover {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  background: #f3f4f6;
}
.cover img { width: 100%; height: 100%; object-fit: cover; }
.cover-empty {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
}
.soldout {
  position: absolute;
  inset: 0;
  z-index: 3;
  background: rgba(17, 24, 39, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 900;
}

.info { padding: 12px 14px 14px; }
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
  height: 38px;
  overflow: hidden;
  line-height: 1.6;
}
.bottom {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 12px;
}
.price { font-weight: 900; color: #ef4444; }
.meta { margin-top: 4px; font-size: 12px; color: #9ca3af; white-space: nowrap; }
.dot { margin: 0 6px; }
.right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  flex-shrink: 0;
}
.rate { display: inline-flex; align-items: center; gap: 6px; color: #f59e0b; font-weight: 800; }
.add { border-radius: 10px; }

/* ✅ 购物车红点徽章 */
.cart-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.cart-badge {
  position: absolute;
  top: -4px;
  right: -6px;
  min-width: 18px;
  height: 18px;
  padding: 0 6px;
  border-radius: 999px;
  background: linear-gradient(135deg, #fb7185, #f43f5e);
  color: #fff;
  font-size: 11px;
  font-weight: 800;
  line-height: 18px;
  text-align: center;
  box-shadow: 0 10px 20px rgba(244, 63, 94, 0.25);
  transform-origin: center;
}
.badge-bump {
  animation: badgePop 0.32s ease;
}
@keyframes badgePop {
  0% { transform: scale(1); }
  40% { transform: scale(1.35); }
  100% { transform: scale(1); }
}

@media (max-width: 1024px) {
  .grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .filter-right { justify-content: flex-start; }
}
@media (max-width: 640px) {
  .grid { grid-template-columns: 1fr; }
}
</style>
