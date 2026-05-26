<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { productApi } from '../api'
import type { Product } from '../api'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  AppstoreOutlined,
  BarsOutlined,
  FireOutlined,
  StarOutlined,
  HomeOutlined,
  ShoppingCartOutlined,
  FilterOutlined,
  ReloadOutlined,
  TagOutlined
} from '@ant-design/icons-vue'

type SortKey = 'default' | 'priceAsc' | 'priceDesc' | 'newest'

const router = useRouter()
const route = useRoute()

const products = ref<Product[]>([])
const loading = ref(false)

const searchQuery = ref('')
const selectedCategory = ref('all')
const viewMode = ref<'grid' | 'list'>('grid')
const sortBy = ref<SortKey>('default')

/** 筛选 */
const onlyInStock = ref(true)
const minPrice = ref<number | null>(null)
const maxPrice = ref<number | null>(null)
const quickLowStock = ref(false) // <=10

/** 分页 */
const page = ref(1)
const pageSize = ref(12)
const pageSizeOptions = ['12', '24', '48']

const categories = [
  { key: 'all', label: '全部商品' },
  { key: 'vegetables', label: '新鲜蔬菜' },
  { key: 'fruits', label: '时令水果' },
  { key: 'grains', label: '粮油米面' },
  { key: 'meat', label: '肉禽蛋品' },
  { key: 'seafood', label: '水产海鲜' }
]

const sortOptions = [
  { value: 'default', label: '默认排序' },
  { value: 'newest', label: '最新上架' },
  { value: 'priceAsc', label: '价格从低到高' },
  { value: 'priceDesc', label: '价格从高到低' }
]

onMounted(async () => {
  const categoryFromQuery = route.query.category as string
  if (categoryFromQuery && categories.some(c => c.key === categoryFromQuery)) {
    selectedCategory.value = categoryFromQuery
  }
  await loadProducts()
})

watch(selectedCategory, (newVal) => {
  if (newVal === 'all') router.replace({ query: {} })
  else router.replace({ query: { category: newVal } })
})

/** 筛选变化 -> 回到第一页 */
watch([searchQuery, selectedCategory, sortBy, onlyInStock, minPrice, maxPrice, quickLowStock, pageSize], () => {
  page.value = 1
})

const loadProducts = async () => {
  loading.value = true
  try {
    products.value = await productApi.getAll()
  } catch {
    message.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

const filteredAll = computed(() => {
  let result = products.value

  // 搜索
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.trim().toLowerCase()
    result = result.filter(p => p.name?.toLowerCase().includes(q))
  }

  // 分类
  if (selectedCategory.value !== 'all') {
    result = result.filter(p => p.category === selectedCategory.value)
  }

  // 有货
  if (onlyInStock.value) {
    result = result.filter(p => (p.stock ?? 0) > 0)
  }

  // 低库存快捷筛选
  if (quickLowStock.value) {
    result = result.filter(p => (p.stock ?? 0) > 0 && (p.stock ?? 0) <= 10)
  }

  // 价格区间
  if (minPrice.value != null) result = result.filter(p => (p.price ?? 0) >= minPrice.value!)
  if (maxPrice.value != null) result = result.filter(p => (p.price ?? 0) <= maxPrice.value!)

  // 排序
  switch (sortBy.value) {
    case 'priceAsc':
      result = [...result].sort((a, b) => (a.price ?? 0) - (b.price ?? 0))
      break
    case 'priceDesc':
      result = [...result].sort((a, b) => (b.price ?? 0) - (a.price ?? 0))
      break
    case 'newest':
      result = [...result].sort((a, b) => (b.id ?? 0) - (a.id ?? 0))
      break
  }

  return result
})

const paged = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return filteredAll.value.slice(start, start + pageSize.value)
})

const resetFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = 'all'
  sortBy.value = 'default'
  onlyInStock.value = true
  quickLowStock.value = false
  minPrice.value = null
  maxPrice.value = null
  viewMode.value = 'grid'
}

const formatPrice = (price: number) => `￥${price?.toFixed(2) || '0.00'}`
</script>

<template>
  <div class="page">
    <a-affix :offset-top="0">
      <div class="nav">
        <div class="nav-inner">
          <div class="nav-left">
            <a-button type="text" @click="router.push('/')">
              <HomeOutlined /> 首页
            </a-button>
            <a-button type="text" @click="router.push('/product/new')">
              <StarOutlined /> 新品
            </a-button>
            <a-button type="text" @click="router.push('/product/hot')">
              <FireOutlined /> 热销
            </a-button>
          </div>

          <div class="nav-center">
            <div class="nav-title">
              <AppstoreOutlined />
              <span>{{ categories.find(c => c.key === selectedCategory)?.label || '商品列表' }}</span>
            </div>
          </div>

          <div class="nav-right">
            <a-button type="text" @click="router.push('/cart')">
              <ShoppingCartOutlined />
            </a-button>
          </div>
        </div>
      </div>
    </a-affix>

    <main class="container">
      <a-card :bordered="false" class="card">
        <a-row :gutter="[12, 12]" align="middle">
          <a-col :xs="24" :md="10" :lg="8">
            <a-input v-model:value="searchQuery" allow-clear placeholder="搜索商品名称...">
              <template #prefix><SearchOutlined /></template>
            </a-input>
          </a-col>

          <a-col :xs="12" :md="7" :lg="6">
            <a-select v-model:value="selectedCategory" style="width:100%" placeholder="选择分类">
              <a-select-option v-for="c in categories" :key="c.key" :value="c.key">{{ c.label }}</a-select-option>
            </a-select>
          </a-col>

          <a-col :xs="12" :md="7" :lg="5">
            <a-select v-model:value="sortBy" style="width:100%" :options="sortOptions" />
          </a-col>

          <a-col :xs="24" :md="24" :lg="5">
            <div class="filter-actions">
              <a-space wrap>
                <a-switch v-model:checked="onlyInStock" checked-children="有货" un-checked-children="全部" />
                <a-button @click="resetFilters">
                  <ReloadOutlined /> 重置
                </a-button>
              </a-space>
            </div>
          </a-col>

          <a-col :xs="24" :lg="12">
            <div class="price-range">
              <span class="label"><FilterOutlined /> 价格区间</span>
              <a-input-number v-model:value="minPrice" :min="0" placeholder="最低价" style="width: 140px" />
              <span class="sep">-</span>
              <a-input-number v-model:value="maxPrice" :min="0" placeholder="最高价" style="width: 140px" />
              <a-checkable-tag v-model:checked="quickLowStock">
                <TagOutlined /> 低库存(≤10)
              </a-checkable-tag>
            </div>
          </a-col>

          <a-col :xs="24" :lg="12">
            <div class="viewbar">
              <div class="stat">
                共 <b>{{ products.length }}</b> 件 / 筛选后 <b>{{ filteredAll.length }}</b> 件
              </div>
              <a-segmented
                v-model:value="viewMode"
                :options="[
                  { label: '网格', value: 'grid', icon: AppstoreOutlined },
                  { label: '列表', value: 'list', icon: BarsOutlined }
                ]"
              />
            </div>
          </a-col>
        </a-row>
      </a-card>

      <a-card :bordered="false" class="card" style="margin-top: 12px;">
        <a-spin :spinning="loading">
          <a-empty v-if="paged.length === 0 && !loading" description="暂无符合条件的商品" />

          <div v-else-if="viewMode === 'grid'" class="grid">
            <a-card
              v-for="p in paged"
              :key="p.id"
              class="product-card"
              :bordered="false"
              hoverable
              @click="$router.push(`/product/${p.id}`)"
            >
              <template #cover>
                <div class="cover">
                  <img v-if="p.imageUrl" :src="p.imageUrl" :alt="p.name" />
                  <div v-else class="cover-empty">无图</div>
                  <div v-if="(p.stock ?? 0) <= 0" class="soldout">售罄</div>
                </div>
              </template>

              <div class="pname" :title="p.name">{{ p.name }}</div>
              <div class="pdesc">{{ p.description || '暂无描述' }}</div>

              <div class="row">
                <div class="price">{{ formatPrice(p.price) }}</div>
                <a-tag :color="(p.stock ?? 0) <= 10 ? 'orange' : 'blue'">库存 {{ p.stock }}</a-tag>
              </div>
            </a-card>
          </div>

          <div v-else class="list">
            <a-card
              v-for="p in paged"
              :key="p.id"
              class="list-item"
              :bordered="false"
              hoverable
              @click="$router.push(`/product/${p.id}`)"
            >
              <div class="thumb">
                <img v-if="p.imageUrl" :src="p.imageUrl" :alt="p.name" />
                <div v-else class="cover-empty">无图</div>
                <div v-if="(p.stock ?? 0) <= 0" class="soldout">售罄</div>
              </div>

              <div class="list-info">
                <div class="pname">{{ p.name }}</div>
                <div class="pdesc">{{ p.description || '暂无描述' }}</div>
                <div class="row">
                  <div class="price">{{ formatPrice(p.price) }}</div>
                  <a-space wrap>
                    <a-tag :color="(p.stock ?? 0) <= 10 ? 'orange' : 'blue'">库存 {{ p.stock }}</a-tag>
                    <a-tag v-if="p.isNew" color="green">新品</a-tag>
                    <a-tag v-if="p.isHot" color="red">热销</a-tag>
                  </a-space>
                </div>
              </div>
            </a-card>
          </div>

          <div v-if="filteredAll.length > 0" class="pager">
            <a-pagination
              v-model:current="page"
              v-model:pageSize="pageSize"
              :total="filteredAll.length"
              :pageSizeOptions="pageSizeOptions"
              show-size-changer
              show-quick-jumper
              :show-total="(t:number) => `共 ${t} 条`"
            />
          </div>
        </a-spin>
      </a-card>

      <a-back-top />
    </main>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: #f8fafc;
}

.nav {
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid #e5e7eb;
  backdrop-filter: blur(10px);
}

.nav-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 10px 16px;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
}
.nav-left {
  justify-self: start;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.nav-center {
  justify-self: center;
}
.nav-right {
  justify-self: end;
}

.nav-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 900;
  color: #111827;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 16px;
}

.card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.06);
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
}
@media (max-width: 992px) {
  .filter-actions {
    justify-content: flex-start;
  }
}

.price-range {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.price-range .label {
  color: #6b7280;
  font-weight: 600;
}
.sep {
  color: #9ca3af;
  font-weight: 800;
}

.viewbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.stat {
  color: #6b7280;
}
.stat b {
  color: #111827;
}

.grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}
@media (max-width: 1024px) {
  .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
@media (max-width: 640px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

.product-card {
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.cover {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  background: #f3f4f6;
}
.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.cover-empty {
  height: 100%;
  display: grid;
  place-items: center;
  color: #9ca3af;
}
.soldout {
  position: absolute;
  inset: 0;
  background: rgba(17, 24, 39, 0.45);
  display: grid;
  place-items: center;
  color: #fff;
  font-weight: 900;
}

.pname {
  font-weight: 900;
  color: #111827;
  margin-top: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.pdesc {
  margin-top: 6px;
  color: #6b7280;
  font-size: 12px;
  height: 38px;
  overflow: hidden;
  line-height: 1.6;
}
.row {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.price {
  font-weight: 900;
  color: #10b981;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.list-item {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.06);
}
.list-item :deep(.ant-card-body) {
  display: grid;
  grid-template-columns: 140px 1fr;
  gap: 14px;
  align-items: start;
}
@media (max-width: 640px) {
  .list-item :deep(.ant-card-body) {
    grid-template-columns: 1fr;
  }
}

.thumb {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  border-radius: 14px;
  overflow: hidden;
  background: #f3f4f6;
}
.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.list-info {
  min-width: 0;
}

.pager {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}
@media (max-width: 992px) {
  .pager {
    justify-content: flex-start;
  }
}
</style>
