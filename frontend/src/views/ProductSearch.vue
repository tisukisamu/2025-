<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { productApi } from '../api'
import type { Product } from '../api'
import {
  ShoppingCartOutlined,
  ArrowLeftOutlined,
  SearchOutlined,
  PlusOutlined,
  PictureOutlined,
  LoadingOutlined,
  InboxOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const searchQuery = ref(route.query.q as string || '')
const products = ref<Product[]>([])
const loading = ref(false)
const loadingMore = ref(false)
const page = ref(0)
const size = 12
const hasMore = ref(true)
const total = ref(0)

// 加载商品数据
const loadProducts = async (isLoadMore = false) => {
  if (!searchQuery.value) {
    products.value = []
    return
  }

  if (isLoadMore) {
    loadingMore.value = true
  } else {
    loading.value = true
    page.value = 0
    products.value = []
  }

  try {
    const result = await productApi.search(searchQuery.value, page.value, size)
    
    if (isLoadMore) {
      products.value = [...products.value, ...result.content]
    } else {
      products.value = result.content
    }
    
    total.value = result.totalElements
    hasMore.value = !result.last
    
    if (result.content.length === 0 && !isLoadMore) {
      message.info('未找到相关商品')
    }
  } catch (error) {
    message.error('搜索失败，请稍后重试')
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

// 加载更多
const loadMore = async () => {
  if (loadingMore.value || !hasMore.value) return
  page.value++
  await loadProducts(true)
}

// 滚动加载
const handleScroll = () => {
  const scrollHeight = document.documentElement.scrollHeight
  const scrollTop = document.documentElement.scrollTop || document.body.scrollTop
  const clientHeight = document.documentElement.clientHeight
  
  // 距离底部 100px 时加载更多
  if (scrollHeight - scrollTop - clientHeight < 100) {
    loadMore()
  }
}

onMounted(() => {
  loadProducts()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const addToCart = (product: Product) => {
  cartStore.addProduct(product, 1)
}

const goToCart = () => router.push('/cart')
const goBack = () => router.back()

const formatPrice = (price: number) => {
  return `￥${price?.toFixed(2) || '0.00'}`
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-gray-100">
    <!-- 导航栏 -->
    <nav class="sticky top-0 z-50 glass-effect shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          <div class="flex items-center gap-3">
            <a-button type="text" class="flex items-center gap-2" @click="goBack">
              <ArrowLeftOutlined />
              <span class="hidden sm:inline">返回</span>
            </a-button>
          </div>
          
          <div class="flex-1 max-w-md mx-4">
            <a-input
              v-model:value="searchQuery"
              placeholder="搜索商品..."
              class="modern-input"
              allow-clear
              @pressEnter="loadProducts()"
            >
              <template #prefix>
                <SearchOutlined class="text-gray-400" />
              </template>
              <template #suffix>
                <a-button type="primary" size="small" @click="loadProducts()">
                  搜索
                </a-button>
              </template>
            </a-input>
          </div>

          <div class="flex items-center gap-4">
            <div
              class="relative cursor-pointer p-2 rounded-xl hover:bg-gray-100 transition-colors"
              @click="goToCart"
            >
              <ShoppingCartOutlined class="text-xl text-gray-600" />
              <span
                v-if="cartStore.totalItems > 0"
                class="absolute -top-1 -right-1 w-5 h-5 bg-gradient-to-r from-rose-500 to-pink-500 text-white text-xs rounded-full flex-center font-bold"
              >
                {{ cartStore.totalItems }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- 搜索结果 -->
    <main class="container-content py-8">
      <!-- 搜索结果标题 -->
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-800">
          "{{ searchQuery }}" 的搜索结果
        </h1>
        <p class="text-gray-500 mt-1">
          共找到 {{ total }} 件商品
        </p>
      </div>

      <!-- 加载中 -->
      <div v-if="loading && products.length === 0" class="flex flex-col items-center justify-center py-20">
        <a-spin size="large" />
        <p class="mt-4 text-gray-500">正在搜索...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="products.length === 0" class="flex flex-col items-center justify-center py-20">
        <div class="w-24 h-24 rounded-full bg-gray-100 flex items-center justify-center mb-4">
          <InboxOutlined class="text-4xl text-gray-400" />
        </div>
        <h3 class="text-lg font-medium text-gray-700 mb-2">未找到相关商品</h3>
        <p class="text-gray-500">换个关键词试试</p>
      </div>

      <!-- 商品列表 -->
      <div v-else>
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <div
            v-for="product in products"
            :key="product.id"
            class="card-modern card-hover cursor-pointer group"
            @click="$router.push(`/product/${product.id}`)"
          >
            <!-- 商品图片 -->
            <div class="relative aspect-square overflow-hidden bg-gray-100">
              <img
                v-if="product.imageUrl"
                :src="product.imageUrl"
                :alt="product.name"
                class="img-cover group-hover:scale-105 transition-transform duration-500"
              />
              <div v-else class="w-full h-full flex-center text-gray-400">
                <PictureOutlined class="text-4xl" />
              </div>
              <!-- 售罄标签 -->
              <div
                v-if="product.stock <= 0"
                class="absolute inset-0 bg-black/50 flex-center"
              >
                <span class="px-4 py-2 bg-white/90 rounded-full text-gray-800 font-medium">
                  已售罄
                </span>
              </div>
              <!-- 分类标签 -->
              <div
                v-if="product.category"
                class="absolute top-3 left-3 px-3 py-1 bg-white/90 backdrop-blur rounded-full text-xs font-medium text-gray-700"
              >
                {{ product.category }}
              </div>
            </div>

            <!-- 商品信息 -->
            <div class="p-5">
              <h3 class="font-semibold text-gray-800 mb-2 line-clamp-1 group-hover:text-emerald-600 transition-colors">
                {{ product.name }}
              </h3>
              <p class="text-sm text-gray-500 mb-4 line-clamp-2 h-10">
                {{ product.description || '暂无描述' }}
              </p>
              <div class="flex-between">
                <div>
                  <span class="text-2xl font-bold text-rose-500">{{ formatPrice(product.price) }}</span>
                  <span class="text-sm text-gray-400 ml-2">库存 {{ product.stock }}</span>
                </div>
                <a-button
                  type="primary"
                  shape="circle"
                  class="shadow-lg hover:shadow-xl transition-shadow"
                  :disabled="product.stock <= 0"
                  @click.stop="addToCart(product)"
                >
                  <PlusOutlined />
                </a-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载更多 -->
        <div v-if="hasMore" class="flex justify-center mt-8">
          <a-button
            type="primary"
            size="large"
            :loading="loadingMore"
            @click="loadMore"
          >
            <template #icon>
              <LoadingOutlined v-if="loadingMore" />
            </template>
            {{ loadingMore ? '加载中...' : '加载更多' }}
          </a-button>
        </div>

        <!-- 没有更多 -->
        <div v-else-if="products.length > 0" class="text-center mt-8 text-gray-400">
          已经到底了
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
