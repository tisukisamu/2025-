<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { favoriteApi, productApi } from '../api'
import type { Favorite, Product } from '../api'
import { useUserStore } from '../stores/user'
import { message } from 'ant-design-vue'
import {
  HeartOutlined,
  ShoppingOutlined,
  ArrowLeftOutlined,
  DeleteOutlined,
  ShoppingCartOutlined,
  StarFilled
} from '@ant-design/icons-vue'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const favorites = ref<(Favorite & { product?: Product })[]>([])
const loading = ref(false)

onMounted(async () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  await loadFavorites()
})

const loadFavorites = async () => {
  loading.value = true
  try {
    const favList = await favoriteApi.getUserFavorites(userStore.id)
    const favWithProducts = await Promise.all(
      favList.map(async (fav) => {
        try {
          const product = await productApi.getById(fav.productId)
          return { ...fav, product }
        } catch (error) {
          return { ...fav, product: undefined }
        }
      })
    )
    favorites.value = favWithProducts
  } catch (error) {
    message.error('加载收藏列表失败')
  } finally {
    loading.value = false
  }
}

const removeFavorite = async (productId: number) => {
  try {
    await favoriteApi.remove(userStore.id, productId)
    favorites.value = favorites.value.filter((f) => f.productId !== productId)
    message.success('已取消收藏')
  } catch (error) {
    message.error('取消收藏失败')
  }
}

const addToCart = (product: Product) => {
  cartStore.addProduct(product, 1)
  message.success('已加入购物车')
}

const goToProduct = (productId: number) => {
  router.push(`/product/${productId}`)
}

const formatPrice = (price: number) => `￥${price?.toFixed(2) || '0.00'}`
</script>

<template>
  <div class="favorites-page">
    <header class="page-header">
      <button class="back-btn" @click="router.back()">
        <ArrowLeftOutlined />
      </button>
      <h1 class="page-title">我的收藏</h1>
      <span class="fav-count">{{ favorites.length }} 件商品</span>
    </header>

    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
    </div>

    <div v-else-if="favorites.length === 0" class="empty-state">
      <div class="empty-content">
        <HeartOutlined class="empty-icon" />
        <h2 class="empty-title">暂无收藏</h2>
        <p class="empty-text">快去发现喜欢的商品吧</p>
        <button class="shop-btn" @click="router.push('/product/list')">去逛逛</button>
      </div>
    </div>

    <div v-else class="favorites-list">
      <div v-for="item in favorites" :key="item.id" class="favorite-card">
        <div class="product-image" @click="goToProduct(item.productId)">
          <img v-if="item.product?.imageUrl" :src="item.product.imageUrl" :alt="item.product?.name" />
          <div v-else class="image-placeholder"><ShoppingOutlined /></div>
        </div>

        <div class="product-info" @click="goToProduct(item.productId)">
          <h3 class="product-name">{{ item.product?.name || '商品已下架' }}</h3>
          <p class="product-desc">{{ item.product?.description || '暂无描述' }}</p>
          <div class="product-meta">
            <span class="product-price">{{ formatPrice(item.product?.price || 0) }}</span>
            <div class="product-rating" v-if="item.product">
              <StarFilled />
              <span>4.9</span>
            </div>
          </div>
        </div>

        <div class="product-actions">
          <button
            class="action-btn cart-btn"
            @click="item.product && addToCart(item.product)"
            :disabled="!item.product"
          >
            <ShoppingCartOutlined />
            <span class="btn-text">加入购物车</span>
          </button>
          <button class="action-btn remove-btn" @click="removeFavorite(item.productId)">
            <DeleteOutlined />
            <span class="btn-text">取消收藏</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.favorites-page {
  min-height: 100vh;
  background: #f8fafc;
}

.page-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: white;
  border-bottom: 1px solid #e2e8f0;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.back-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: #f1f5f9;
  color: #475569;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}
.back-btn:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  flex: 1;
}

.fav-count {
  font-size: 14px;
  color: #64748b;
  background: #f1f5f9;
  padding: 4px 12px;
  border-radius: 20px;
}

/* loading/empty 省略（保持你原样） */
.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}
.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e2e8f0;
  border-top-color: #10b981;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 80px);
}
.empty-content {
  text-align: center;
  max-width: 400px;
}
.empty-icon {
  font-size: 64px;
  color: #d1d5db;
  margin-bottom: 24px;
}
.empty-title {
  font-size: 20px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px 0;
}
.empty-text {
  font-size: 14px;
  color: #999;
  margin: 0 0 32px 0;
}
.shop-btn {
  padding: 12px 32px;
  border-radius: 8px;
  border: none;
  background: #1a1a1a;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}
.shop-btn:hover {
  background: #333;
}

.favorites-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 16px;
}
@media (min-width: 640px) {
  .favorites-list {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (min-width: 1024px) {
  .favorites-list {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (min-width: 1280px) {
  .favorites-list {
    grid-template-columns: repeat(4, 1fr);
  }
}

.favorite-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  transition: all 0.3s;
}
.favorite-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.product-image {
  position: relative;
  aspect-ratio: 1;
  overflow: hidden;
  background: #f8fafc;
  cursor: pointer;
}
.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.favorite-card:hover .product-image img {
  transform: scale(1.05);
}
.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #d1d5db;
  font-size: 48px;
}

.product-info {
  padding: 16px;
  cursor: pointer;
}
.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
  margin: 0 0 12px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 40px;
}
.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.product-price {
  font-size: 18px;
  font-weight: 700;
  color: #f43f5e;
}
.product-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #f59e0b;
  font-size: 13px;
}
.product-rating span {
  color: #64748b;
}

/* ✅ 关键修复：按钮区域不重叠 */
.product-actions {
  padding: 0 16px 16px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;          /* 允许换行，避免挤压重叠 */
}

.action-btn {
  flex: 1 1 140px;          /* 有最小宽度，空间不够就换行 */
  min-width: 140px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 14px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  white-space: nowrap;      /* 不让文字拆开导致高度异常 */
}

.btn-text {
  line-height: 1;
}

.cart-btn {
  background: #10b981;
  color: white;
}
.cart-btn:hover:not(:disabled) {
  background: #059669;
}
.cart-btn:disabled {
  background: #e2e8f0;
  cursor: not-allowed;
  color: #9ca3af;
}

.remove-btn {
  background: white;
  color: #ef4444;
  border: 1px solid #fee2e2;
}
.remove-btn:hover {
  background: #fef2f2;
}

@media (max-width: 420px) {
  .action-btn {
    flex: 1 1 100%;
    min-width: 100%;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 12px 16px;
  }
  .favorites-list {
    padding: 16px;
  }
}
</style>
