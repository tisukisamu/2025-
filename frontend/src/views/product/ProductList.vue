<template>
  <div class="products-page">
    <div class="page-header">
      <h1>商品列表</h1>
      <div class="view-toggle" v-if="!isMobile">
        <a-segmented v-model:value="viewMode" :options="viewOptions" />
      </div>
    </div>

    <div class="search-section">
      <div class="search-box">
        <a-input-search
          v-model:value="filters.keyword"
          placeholder="搜索商品名称、描述..."
          enter-button="搜索"
          size="large"
          @search="handleSearch"
        />
      </div>
      
      <div class="search-suggestions" v-if="showSuggestions">
        <div class="suggestion-row" v-if="searchHistory.length > 0">
          <span class="suggestion-label">搜索历史：</span>
          <div class="suggestion-tags">
            <a-tag 
              v-for="(item, index) in searchHistory" 
              :key="index"
              closable
              @click="handleHistoryClick(item)"
              @close.stop="removeHistory(index)"
            >
              {{ item }}
            </a-tag>
            <a-button type="link" size="small" @click="clearHistory">清空</a-button>
          </div>
        </div>
        <div class="suggestion-row" v-if="hotKeywords.length > 0">
          <span class="suggestion-label">热门搜索：</span>
          <div class="suggestion-tags">
            <a-tag 
              v-for="(item, index) in hotKeywords" 
              :key="index"
              :color="index < 3 ? 'red' : 'default'"
              @click="handleKeywordClick(item)"
            >
              {{ item }}
            </a-tag>
          </div>
        </div>
      </div>
    </div>

    <div class="filter-section">
      <div class="filter-row">
        <div class="filter-group">
          <span class="filter-label">分类：</span>
          <div class="filter-options">
            <a-tag 
              :color="!filters.categoryId ? '#18181b' : 'default'" 
              @click="filters.categoryId = undefined"
            >
              全部
            </a-tag>
            <a-tag 
              v-for="cat in categories" 
              :key="cat.id"
              :color="filters.categoryId === cat.id ? '#18181b' : 'default'"
              @click="filters.categoryId = cat.id"
            >
              {{ cat.name }}
            </a-tag>
          </div>
        </div>
      </div>

      <div class="filter-row">
        <div class="filter-group">
          <span class="filter-label">价格：</span>
          <div class="filter-options">
            <a-tag 
              :color="!filters.minPrice && !filters.maxPrice ? '#18181b' : 'default'" 
              @click="clearPriceFilter"
            >
              不限
            </a-tag>
            <a-tag 
              v-for="range in priceRanges" 
              :key="range.label"
              :color="isPriceRangeSelected(range) ? '#18181b' : 'default'"
              @click="setPriceRange(range)"
            >
              {{ range.label }}
            </a-tag>
          </div>
        </div>
      </div>

      <div class="filter-row">
        <div class="filter-group">
          <span class="filter-label">交易方式：</span>
          <div class="filter-options">
            <a-tag 
              :color="!filters.tradeType ? '#18181b' : 'default'" 
              @click="filters.tradeType = undefined"
            >
              不限
            </a-tag>
            <a-tag 
              :color="filters.tradeType === 'FACE_TO_FACE' ? '#18181b' : 'default'"
              @click="filters.tradeType = 'FACE_TO_FACE'"
            >
              面交
            </a-tag>
            <a-tag 
              :color="filters.tradeType === 'EXPRESS' ? '#18181b' : 'default'"
              @click="filters.tradeType = 'EXPRESS'"
            >
              快递
            </a-tag>
            <a-tag 
              :color="filters.tradeType === 'BOTH' ? '#18181b' : 'default'"
              @click="filters.tradeType = 'BOTH'"
            >
              都可以
            </a-tag>
          </div>
        </div>
      </div>

      <div class="filter-row">
        <div class="filter-group">
          <span class="filter-label">排序：</span>
          <div class="filter-options">
            <a-tag 
              :color="filters.sortBy === 'createTime' ? '#18181b' : 'default'" 
              @click="setSort('createTime')"
            >
              最新发布
            </a-tag>
            <a-tag 
              :color="filters.sortBy === 'price_asc' ? '#18181b' : 'default'" 
              @click="setSort('price_asc')"
            >
              价格从低到高
            </a-tag>
            <a-tag 
              :color="filters.sortBy === 'price_desc' ? '#18181b' : 'default'" 
              @click="setSort('price_desc')"
            >
              价格从高到低
            </a-tag>
            <a-tag 
              :color="filters.sortBy === 'viewCount' ? '#18181b' : 'default'" 
              @click="setSort('viewCount')"
            >
              浏览最多
            </a-tag>
            <a-tag 
              :color="filters.sortBy === 'favoriteCount' ? '#18181b' : 'default'" 
              @click="setSort('favoriteCount')"
            >
              收藏最多
            </a-tag>
          </div>
        </div>
      </div>

      <div class="filter-row custom-price" v-if="showCustomPrice">
        <a-input-number
          v-model:value="filters.minPrice"
          placeholder="最低价"
          :min="0"
          style="width: 120px"
        />
        <span class="price-separator">-</span>
        <a-input-number
          v-model:value="filters.maxPrice"
          placeholder="最高价"
          :min="0"
          style="width: 120px"
        />
        <a-button type="primary" size="small" @click="handleSearch">确定</a-button>
      </div>
    </div>

    <div class="products-content">
      <div class="result-info">
        <span>共找到 <strong>{{ total }}</strong> 件商品</span>
        <a-button v-if="hasFilter" type="link" @click="clearAllFilters">清除筛选</a-button>
      </div>

      <a-spin :spinning="loading">
        <div v-if="products.length > 0">
          <div v-if="viewMode === 'grid'" class="products-grid">
            <product-card
              v-for="product in products"
              :key="product.id"
              :product="product"
              @click="handleProductClick(product.id)"
            />
          </div>
          <div v-else class="products-list">
            <div 
              v-for="product in products" 
              :key="product.id" 
              class="product-list-item"
              @click="handleProductClick(product.id)"
            >
              <div class="product-image">
                <img :src="product.coverImage || defaultImage" :alt="product.title" />
              </div>
              <div class="product-info">
                <h3 class="product-title">{{ product.title }}</h3>
                <p class="product-desc">{{ product.description }}</p>
                <div class="product-meta">
                  <span class="product-price">¥{{ product.price }}</span>
                  <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
                  <a-tag v-if="product.tradeType === 'FACE_TO_FACE'" color="green">面交</a-tag>
                  <a-tag v-else-if="product.tradeType === 'EXPRESS'" color="blue">快递</a-tag>
                </div>
                <div class="product-stats">
                  <span><eye-outlined /> {{ product.viewCount }}</span>
                  <span><heart-outlined /> {{ product.favoriteCount }}</span>
                  <span><clock-circle-outlined /> {{ formatTime(product.createTime) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <a-empty v-else description="暂无符合条件的商品" />
      </a-spin>

      <div class="pagination-wrapper" v-if="total > 0">
        <a-pagination
          v-model:current="pagination.page"
          v-model:pageSize="pagination.pageSize"
          :total="total"
          show-quick-jumper
          show-size-changer
          :pageSizeOptions="['12', '24', '48']"
          :show-total="(total: number) => `共 ${total} 件商品`"
          @change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { productApi, categoryApi } from '@/api'
import { searchHistoryApi } from '@/types/extra'
import type { Product, Category } from '@/types'
import ProductCard from '@/components/ProductCard.vue'
import { EyeOutlined, HeartOutlined, ClockCircleOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

const defaultImage = '/no-image.svg'

const loading = ref(false)
const products = ref<Product[]>([])
const categories = ref<Category[]>([])
const total = ref(0)
const searchHistory = ref<string[]>([])
const hotKeywords = ref(['iPhone', 'MacBook', 'iPad', 'AirPods', '相机', '显示器', '键盘', '耳机'])
const showCustomPrice = ref(false)
const windowWidth = ref(window.innerWidth)

const isMobile = computed(() => windowWidth.value < 768)
const showSuggestions = computed(() => searchHistory.value.length > 0 || hotKeywords.value.length > 0)

const viewMode = ref<'grid' | 'list'>('grid')
const viewOptions = [
  { label: '网格', value: 'grid' },
  { label: '列表', value: 'list' }
]

const filters = reactive({
  keyword: '',
  categoryId: undefined as number | undefined,
  minPrice: undefined as number | undefined,
  maxPrice: undefined as number | undefined,
  tradeType: undefined as string | undefined,
  sortBy: 'createTime'
})

const priceRanges = [
  { label: '0-50元', min: 0, max: 50 },
  { label: '50-100元', min: 50, max: 100 },
  { label: '100-500元', min: 100, max: 500 },
  { label: '500-1000元', min: 500, max: 1000 },
  { label: '1000元以上', min: 1000, max: undefined },
  { label: '自定义', min: undefined, max: undefined, custom: true }
]

const hasFilter = computed(() => {
  return filters.keyword || filters.categoryId || filters.minPrice || filters.maxPrice || filters.tradeType
})

const pagination = reactive({
  page: 1,
  pageSize: 12
})

const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

const isPriceRangeSelected = (range: typeof priceRanges[0]) => {
  if (range.custom) return showCustomPrice.value
  return filters.minPrice === range.min && filters.maxPrice === range.max
}

const setPriceRange = (range: typeof priceRanges[0]) => {
  if (range.custom) {
    showCustomPrice.value = true
  } else {
    showCustomPrice.value = false
    filters.minPrice = range.min
    filters.maxPrice = range.max
    handleSearch()
  }
}

const clearPriceFilter = () => {
  filters.minPrice = undefined
  filters.maxPrice = undefined
  showCustomPrice.value = false
  handleSearch()
}

const setSort = (sort: string) => {
  filters.sortBy = sort
  handleSearch()
}

const clearAllFilters = () => {
  filters.keyword = ''
  filters.categoryId = undefined
  filters.minPrice = undefined
  filters.maxPrice = undefined
  filters.tradeType = undefined
  filters.sortBy = 'createTime'
  showCustomPrice.value = false
  pagination.page = 1
  fetchProducts()
}

const fetchCategories = async () => {
  try {
    const res = await categoryApi.getAllCategories()
    categories.value = res.data
  } catch {
    // ignore
  }
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const [sortBy, sortOrder] = filters.sortBy.includes('_') 
      ? filters.sortBy.split('_') 
      : [filters.sortBy, 'desc']
    
    const res = await productApi.getProducts({
      keyword: filters.keyword || undefined,
      categoryId: filters.categoryId,
      minPrice: filters.minPrice,
      maxPrice: filters.maxPrice,
      tradeType: filters.tradeType,
      sortBy,
      sortOrder: sortOrder as 'asc' | 'desc',
      page: pagination.page,
      size: pagination.pageSize
    })
    products.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const fetchSearchHistory = async () => {
  try {
    const res = await searchHistoryApi.getRecent(10)
    searchHistory.value = res.data
  } catch {
    // ignore
  }
}

const saveSearchHistory = async (keyword: string) => {
  if (!keyword.trim()) return
  try {
    await searchHistoryApi.save(keyword)
  } catch {
    // ignore
  }
}

const handleSearch = () => {
  pagination.page = 1
  if (filters.keyword) {
    saveSearchHistory(filters.keyword)
  }
  fetchProducts()
}

const handleHistoryClick = (keyword: string) => {
  filters.keyword = keyword
  handleSearch()
}

const handleKeywordClick = (keyword: string) => {
  filters.keyword = keyword
  handleSearch()
}

const removeHistory = (index: number) => {
  const keyword = searchHistory.value[index]
  searchHistory.value.splice(index, 1)
  searchHistoryApi.delete(keyword).catch(() => {})
}

const clearHistory = async () => {
  searchHistory.value = []
  try {
    await searchHistoryApi.clear()
  } catch {
    // ignore
  }
}

const handlePageChange = () => {
  fetchProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleProductClick = (productId: number) => {
  router.push(`/products/${productId}`)
}

const handleResize = () => {
  windowWidth.value = window.innerWidth
}

watch(
  () => route.query,
  (query) => {
    if (query.keyword) {
      filters.keyword = query.keyword as string
    }
    if (query.categoryId) {
      filters.categoryId = Number(query.categoryId)
    }
    if (query.sort) {
      filters.sortBy = query.sort as string
    }
    fetchProducts()
  },
  { immediate: true }
)

onMounted(() => {
  fetchCategories()
  fetchSearchHistory()
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.products-page {
  min-height: calc(100vh - 200px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #18181b;
}

.search-section {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 16px;
  border: 1px solid #e4e4e7;
}

.search-box {
  max-width: 600px;
  margin: 0 auto 16px;
}

.search-suggestions {
  max-width: 600px;
  margin: 0 auto;
}

.suggestion-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 8px;
}

.suggestion-label {
  font-size: 13px;
  color: #71717a;
  flex-shrink: 0;
  line-height: 28px;
}

.suggestion-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.suggestion-tags .ant-tag {
  cursor: pointer;
}

.filter-section {
  background: #fff;
  padding: 16px 20px;
  border-radius: 12px;
  margin-bottom: 16px;
  border: 1px solid #e4e4e7;
}

.filter-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
}

.filter-row:last-child {
  margin-bottom: 0;
}

.filter-group {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #71717a;
  flex-shrink: 0;
  line-height: 28px;
  width: 70px;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-options .ant-tag {
  cursor: pointer;
  margin: 0;
}

.custom-price {
  margin-top: 12px;
  padding-left: 78px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-separator {
  color: #71717a;
}

.products-content {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e4e4e7;
}

.result-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  color: #71717a;
}

.result-info strong {
  color: #18181b;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.products-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-list-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f4f4f5;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.product-list-item:hover {
  background: #e4e4e7;
}

.product-image {
  width: 160px;
  height: 160px;
  flex-shrink: 0;
  border-radius: 8px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-title {
  font-size: 16px;
  font-weight: 500;
  color: #18181b;
  margin: 0 0 8px;
}

.product-desc {
  font-size: 14px;
  color: #71717a;
  margin: 0 0 12px;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.product-price {
  font-size: 20px;
  font-weight: 600;
  color: #ef4444;
}

.original-price {
  font-size: 14px;
  color: #71717a;
  text-decoration: line-through;
}

.product-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #71717a;
}

.product-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .search-section {
    padding: 16px;
  }

  .filter-section {
    padding: 12px 16px;
  }

  .filter-group {
    flex-direction: column;
    gap: 8px;
  }

  .filter-label {
    width: auto;
    line-height: 1;
  }

  .custom-price {
    padding-left: 0;
    flex-wrap: wrap;
  }

  .products-content {
    padding: 16px;
  }

  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .product-list-item {
    flex-direction: column;
  }

  .product-image {
    width: 100%;
    height: 150px;
  }
}
</style>
