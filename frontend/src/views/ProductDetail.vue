<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi, commentApi, favoriteApi } from '../api'
import type { Product, Comment } from '../api'
import { useCartStore } from '../stores/cart'
import { useUserStore } from '../stores/user'
import { message } from 'ant-design-vue'
import ProductImage from '../components/ProductImage.vue'
import { getAllImageUrls, getPrimaryImageUrl } from '../utils/imageLoader'
import {
  ShoppingCartOutlined,
  HeartOutlined,
  HeartFilled,
  LeftOutlined,
  RightOutlined,
  StarFilled,
  SafetyCertificateFilled,
  CarFilled,
  CheckCircleFilled,
  DeleteOutlined,
  ShareAltOutlined
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const product = ref<Product | null>(null)
const loading = ref(false)

const quantity = ref<number>(1)
const activeImage = ref(0)

const isLiked = ref(false)
const selectedSpec = ref('默认规格')

const comments = ref<Comment[]>([])
const commentContent = ref('')
const commentRating = ref(5)
const submittingComment = ref(false)

const images = computed(() => {
  if (!product.value) return []
  // 使用双保底策略获取所有图片
  const urls = getAllImageUrls(product.value)
  return urls.length > 0 ? urls : [getPrimaryImageUrl(product.value)]
})

const specs = computed(() => ['默认规格', '500g装', '1kg装', '2kg装'])

const stock = computed(() => Number(product.value?.stock ?? 0))
const canBuy = computed(() => !!product.value && stock.value > 0)

const averageRating = computed(() => {
  if (comments.value.length === 0) return '0.0'
  const sum = comments.value.reduce((acc, c) => acc + (c.rating ?? 0), 0)
  return (sum / comments.value.length).toFixed(1)
})

onMounted(async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    if (id) {
      product.value = await productApi.getById(id)
      await loadComments()
      await checkFavorite()
      quantity.value = 1
      activeImage.value = 0
    }
  } catch {
    message.error('加载商品失败')
  } finally {
    loading.value = false
  }
})

const loadComments = async () => {
  if (!product.value) return
  try {
    comments.value = await commentApi.getByProduct(product.value.id)
  } catch (error) {
    console.error('加载评论失败', error)
  }
}

const checkFavorite = async () => {
  if (!product.value || !userStore.isLoggedIn) return
  try {
    isLiked.value = await favoriteApi.checkFavorite(userStore.id, product.value.id)
  } catch (error) {
    console.error('检查收藏失败', error)
  }
}

const addToCart = () => {
  if (!product.value) return
  if (!canBuy.value) {
    message.warning('该商品已售罄')
    return
  }
  cartStore.addProduct(product.value, quantity.value)
  message.success('已加入购物车')
}

const buyNow = () => {
  if (!product.value) return
  if (!canBuy.value) {
    message.warning('该商品已售罄')
    return
  }
  cartStore.addProduct(product.value, quantity.value)
  router.push('/cart')
}

const toggleLike = async () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  if (!product.value) return

  try {
    if (isLiked.value) {
      await favoriteApi.remove(userStore.id, product.value.id)
      isLiked.value = false
      message.success('已取消收藏')
    } else {
      await favoriteApi.add({ productId: product.value.id, userId: userStore.id })
      isLiked.value = true
      message.success('已收藏')
    }
  } catch {
    message.error('操作失败')
  }
}

const submitComment = async () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  if (!product.value) return

  if (!commentContent.value.trim()) {
    message.warning('请输入评论内容')
    return
  }

  submittingComment.value = true
  try {
    await commentApi.create({
      productId: product.value.id,
      userId: userStore.id,
      content: commentContent.value,
      rating: commentRating.value,
      nickname: userStore.nickname || userStore.username
    })
    commentContent.value = ''
    commentRating.value = 5
    await loadComments()
    message.success('评论发表成功')
  } catch {
    message.error('发表评论失败')
  } finally {
    submittingComment.value = false
  }
}

const deleteComment = async (id: number) => {
  try {
    await commentApi.delete(id)
    await loadComments()
    message.success('评论已删除')
  } catch {
    message.error('删除评论失败')
  }
}

const formatPrice = (price: number) => `￥${price?.toFixed(2) || '0.00'}`
const formatDate = (date: string) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}/${String(d.getMonth() + 1).padStart(2, '0')}/${String(d.getDate()).padStart(2, '0')}
  ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const onShare = async () => {
  try {
    await navigator.clipboard?.writeText(window.location.href)
    message.success('链接已复制')
  } catch {
    message.info('请手动复制浏览器地址栏链接')
  }
}
</script>

<template>
  <div class="page">
    <!-- 顶部栏 -->
    <a-affix :offset-top="0">
      <div class="topbar">
        <div class="topbar-inner">
          <a-button type="text" class="topbar-left" @click="router.back()">
            <LeftOutlined /> 返回
          </a-button>

          <div class="topbar-center">
            <div class="topbar-title">商品详情</div>
          </div>

          <div class="topbar-right">
            <a-space>
              <a-button type="text" @click="toggleLike">
                <component :is="isLiked ? HeartFilled : HeartOutlined" :style="{ color: isLiked ? '#ef4444' : undefined }" />
              </a-button>
              <a-button type="text" @click="onShare">
                <ShareAltOutlined />
              </a-button>
              <a-button type="text" @click="router.push('/cart')">
                <ShoppingCartOutlined />
              </a-button>
            </a-space>
          </div>
        </div>
      </div>
    </a-affix>

    <main class="container">
      <a-spin :spinning="loading" size="large">
        <a-empty v-if="!product && !loading" description="商品不存在或已下架" />

        <div v-else-if="product" class="layout">
          <!-- 左侧图片区 -->
          <a-card :bordered="false" class="card">
            <div class="image-wrap">
              <!-- 主图显示区域 -->
              <div class="main-image-container">
                <img
                  v-if="images[activeImage]"
                  :src="images[activeImage]"
                  :alt="product.name"
                  class="main-image"
                  @error="$event.target.style.display='none'"
                />
                <div v-else class="no-image-placeholder">
                  <ShoppingOutlined class="no-image-icon" />
                  <span>暂无图片</span>
                </div>
                <div class="image-indicator">{{ activeImage + 1 }} / {{ images.length }}</div>
                <div v-if="stock <= 0" class="soldout-mask">已售罄</div>

                <!-- 左右切换按钮 -->
                <button
                  v-if="images.length > 1"
                  class="nav-btn prev-btn"
                  @click="activeImage = (activeImage - 1 + images.length) % images.length"
                  type="button"
                >
                  <LeftOutlined />
                </button>
                <button
                  v-if="images.length > 1"
                  class="nav-btn next-btn"
                  @click="activeImage = (activeImage + 1) % images.length"
                  type="button"
                >
                  <RightOutlined />
                </button>
              </div>
            </div>

            <div class="thumbs">
              <button
                v-for="(img, idx) in images"
                :key="idx"
                class="thumb"
                :class="{ active: activeImage === idx }"
                @mouseenter="activeImage = idx"
                @click="activeImage = idx"
                type="button"
              >
                <img :src="img" alt="thumb" @error="$event.target.style.display='none'" />
              </button>
            </div>
          </a-card>

          <!-- 右侧信息区 -->
          <a-card :bordered="false" class="card">
            <div class="meta-row">
              <a-tag color="green">{{ product.category }}</a-tag>
              <div class="rating">
                <StarFilled />
                <span>{{ averageRating }}（{{ comments.length }}）</span>
              </div>
              <a-tag v-if="stock <= 10 && stock > 0" color="orange">库存紧张</a-tag>
              <a-tag v-if="stock <= 0" color="red">售罄</a-tag>
            </div>

            <div class="title">{{ product.name }}</div>
            <div class="desc">{{ product.description || '暂无描述' }}</div>

            <div class="price-card">
              <div class="price-row">
                <div class="price">{{ formatPrice(product.price) }}</div>
                <div class="subprice">参考价 {{ formatPrice((product.price ?? 0) * 1.2) }}</div>
              </div>

              <div class="spec-row">
                <div class="label">规格</div>
                <a-segmented v-model:value="selectedSpec" :options="specs" />
              </div>

              <div class="qty-row">
                <div class="label">数量</div>
                <a-input-number
                  v-model:value="quantity"
                  :min="1"
                  :max="Math.max(stock, 1)"
                  :disabled="!canBuy"
                />
                <span class="stock">库存 {{ stock }}</span>
              </div>

              <div class="actions">
                <a-button size="large" @click="addToCart" :disabled="!canBuy">
                  加入购物车
                </a-button>
                <a-button type="primary" size="large" @click="buyNow" :disabled="!canBuy">
                  立即购买
                </a-button>
              </div>
            </div>

            <div class="services">
              <div class="service-item"><SafetyCertificateFilled /> 正品保证</div>
              <div class="service-item"><CarFilled /> 极速发货</div>
              <div class="service-item"><CheckCircleFilled /> 品质保障</div>
            </div>
          </a-card>
        </div>

        <!-- 评论区 -->
        <a-card v-if="product" :bordered="false" class="card" style="margin-top: 12px;">
          <template #title>
            <div class="card-title">商品评价（{{ comments.length }}）</div>
          </template>

          <div class="comment-form">
            <a-form layout="vertical">
              <a-form-item label="评分">
                <a-rate v-model:value="commentRating" />
              </a-form-item>

              <a-form-item label="评价内容">
                <a-textarea v-model:value="commentContent" :rows="4" placeholder="分享你的使用体验..." />
              </a-form-item>

              <a-button
                type="primary"
                :loading="submittingComment"
                :disabled="!commentContent.trim()"
                @click="submitComment"
              >
                发表评论
              </a-button>
            </a-form>
          </div>

          <a-divider />

          <a-empty v-if="comments.length === 0" description="暂无评价，快来抢沙发" />

          <a-list v-else :data-source="comments" item-layout="horizontal">
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #title>
                    <div class="c-title">
                      <div class="c-user">
                        <span class="avatar">{{ (item.nickname || 'U').charAt(0).toUpperCase() }}</span>
                        <span class="name">{{ item.nickname || '匿名用户' }}</span>
                      </div>
                      <div class="c-meta">
                        <a-rate :value="item.rating" disabled />
                        <span class="time">{{ formatDate(item.createTime) }}</span>
                      </div>
                    </div>
                  </template>
                  <template #description>
                    <div class="c-content">{{ item.content }}</div>
                    <div class="c-actions">
                      <span class="likes">❤️ {{ item.likes || 0 }}</span>
                      <a-button
                        v-if="userStore.user?.id === item.userId"
                        type="text"
                        danger
                        @click="deleteComment(item.id)"
                      >
                        <DeleteOutlined /> 删除
                      </a-button>
                    </div>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-spin>
    </main>

    <!-- ✅ 移动端底部悬浮购买栏（更像电商） -->
    <a-affix :offset-bottom="0">
      <div v-if="product" class="buybar">
        <div class="buybar-left">
          <div class="buybar-price">{{ formatPrice(product.price) }}</div>
          <div class="buybar-stock">库存 {{ stock }}</div>
        </div>
        <div class="buybar-right">
          <a-button @click="addToCart" :disabled="!canBuy">加购</a-button>
          <a-button type="primary" @click="buyNow" :disabled="!canBuy">购买</a-button>
        </div>
      </div>
    </a-affix>

    <a-back-top />
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: #f8fafc;
}

.topbar {
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid #e5e7eb;
  backdrop-filter: blur(10px);
}
.topbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 10px 12px;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
}
.topbar-left {
  justify-self: start;
}
.topbar-center {
  justify-self: center;
}
.topbar-right {
  justify-self: end;
}
.topbar-title {
  font-weight: 900;
  color: #111827;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
  padding-bottom: 88px; /* 给底部 buybar 留空间 */
}

.card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.06);
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}

.layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}
@media (min-width: 1024px) {
  .layout {
    grid-template-columns: 1fr 1fr;
  }
}

.image-wrap {
  position: relative;
}
.main-image-container {
  position: relative;
  width: 100%;
  height: 400px;
  border-radius: 16px;
  overflow: hidden;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
}
.main-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.3s ease;
}
.main-image:hover {
  transform: scale(1.02);
}
.no-image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  gap: 8px;
}
.no-image-icon {
  font-size: 48px;
}
.image-indicator {
  position: absolute;
  right: 10px;
  bottom: 10px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(17, 24, 39, 0.55);
  color: #fff;
  font-size: 12px;
}
.soldout-mask {
  position: absolute;
  inset: 0;
  border-radius: 16px;
  background: rgba(17, 24, 39, 0.35);
  display: grid;
  place-items: center;
  color: #fff;
  font-weight: 900;
  font-size: 18px;
}

/* 左右切换按钮 */
.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.9);
  color: #374151;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 10;
}
.nav-btn:hover {
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
.prev-btn {
  left: 12px;
}
.next-btn {
  right: 12px;
}

.thumbs {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 8px;
}
@media (max-width: 640px) {
  .thumbs {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}
.thumb {
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 12px;
  overflow: hidden;
  padding: 0;
  background: #fff;
  cursor: pointer;
  aspect-ratio: 1;
}
.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.thumb.active {
  border-color: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.15);
}

.meta-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}
.rating {
  display: inline-flex;
  gap: 6px;
  align-items: center;
  color: #f59e0b;
  font-weight: 700;
}
.title {
  margin-top: 10px;
  font-weight: 900;
  color: #111827;
  font-size: 22px;
  line-height: 1.25;
}
.desc {
  margin-top: 8px;
  color: #6b7280;
  line-height: 1.7;
}

.price-card {
  margin-top: 14px;
  background: #f8fafc;
  border: 1px solid #eef2f7;
  border-radius: 16px;
  padding: 14px;
}
.price-row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 10px;
  flex-wrap: wrap;
}
.price {
  font-size: 26px;
  font-weight: 900;
  color: #10b981;
}
.subprice {
  color: #9ca3af;
  font-size: 12px;
}

.spec-row,
.qty-row {
  margin-top: 12px;
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}
.label {
  width: 48px;
  color: #6b7280;
  font-weight: 700;
}
.stock {
  color: #9ca3af;
  font-size: 12px;
}

.actions {
  margin-top: 14px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
@media (max-width: 640px) {
  .actions {
    grid-template-columns: 1fr;
  }
}

.services {
  margin-top: 14px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}
@media (max-width: 640px) {
  .services {
    grid-template-columns: 1fr;
  }
}
.service-item {
  display: inline-flex;
  gap: 8px;
  align-items: center;
  padding: 10px 12px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #eef2f7;
  color: #374151;
  font-weight: 600;
}

.card-title {
  font-weight: 900;
  color: #111827;
}

.comment-form {
  max-width: 720px;
}

.c-title {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}
.c-user {
  display: inline-flex;
  align-items: center;
  gap: 10px;
}
.avatar {
  width: 32px;
  height: 32px;
  border-radius: 999px;
  background: linear-gradient(135deg, #10b981, #059669);
  color: #fff;
  display: grid;
  place-items: center;
  font-weight: 900;
}
.name {
  font-weight: 800;
  color: #111827;
}
.c-meta {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  color: #9ca3af;
}
.time {
  font-size: 12px;
}
.c-content {
  margin-top: 6px;
  color: #374151;
  line-height: 1.7;
}
.c-actions {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.likes {
  color: #9ca3af;
  font-size: 12px;
}

/* 底部购买栏 */
.buybar {
  background: rgba(255, 255, 255, 0.92);
  border-top: 1px solid #e5e7eb;
  backdrop-filter: blur(10px);
  padding: 10px 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
.buybar-left {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.buybar-price {
  font-weight: 900;
  color: #10b981;
  font-size: 18px;
}
.buybar-stock {
  color: #9ca3af;
  font-size: 12px;
}
.buybar-right {
  display: flex;
  gap: 10px;
}
</style>
