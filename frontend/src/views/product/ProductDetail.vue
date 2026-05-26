<template>
  <div class="product-detail-page">
    <a-spin :spinning="loading">
      <template v-if="product">
        <div class="back-nav">
          <a-button type="text" @click="router.back()">
            <left-outlined /> 返回
          </a-button>
          <div class="action-buttons">
            <a-button type="text" @click="handleShare">
              <share-alt-outlined /> 分享
            </a-button>
            <a-dropdown v-if="userStore.isLoggedIn && product.sellerId !== userStore.userInfo?.id">
              <a-button type="text">
                <more-outlined />
              </a-button>
              <template #overlay>
                <a-menu>
                  <a-menu-item key="report" @click="showReportModal = true">
                    <warning-outlined /> 举报商品
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
        </div>

        <a-row :gutter="[24, 24]">
          <a-col :xs="24" :md="12">
            <div class="image-section">
              <a-carousel arrows dots-class="slick-dots slick-thumb" class="product-carousel">
                <template #customPaging="props">
                  <a>
                    <img :src="product.imageUrls?.[props.i]" v-if="product.imageUrls?.[props.i]" />
                  </a>
                </template>
                <div v-for="(img, index) in (product.imageUrls?.length ? product.imageUrls : [defaultImage])" :key="index">
                  <img :src="img" :alt="product.title" class="main-image" />
                </div>
              </a-carousel>
            </div>
          </a-col>

          <a-col :xs="24" :md="12">
            <div class="info-section">
              <div class="title-row">
                <h1 class="product-title">{{ product.title }}</h1>
                <a-tag :color="statusColor">{{ statusText }}</a-tag>
              </div>
              
              <div class="price-section">
                <span class="price">¥{{ product.price }}</span>
                <span class="original-price" v-if="product.originalPrice">
                  原价 ¥{{ product.originalPrice }}
                </span>
                <span class="discount" v-if="product.originalPrice">
                  {{ Math.round((1 - product.price / product.originalPrice) * 100) }}% OFF
                </span>
              </div>

              <div class="meta-section">
                <a-space :size="16">
                  <span><eye-outlined /> {{ product.viewCount }} 浏览</span>
                  <span><heart-outlined /> {{ product.favoriteCount }} 收藏</span>
                  <span><clock-circle-outlined /> {{ formatRelativeTime(product.createTime) }}</span>
                </a-space>
              </div>

              <a-divider />

              <div class="detail-row">
                <span class="label">分类：</span>
                <a-tag>{{ product.categoryName || '未分类' }}</a-tag>
              </div>
              <div class="detail-row">
                <span class="label">交易方式：</span>
                <span>{{ tradeTypeText }}</span>
              </div>
              <div class="detail-row" v-if="product.location">
                <span class="label">交易地点：</span>
                <span><environment-outlined /> {{ product.location }}</span>
              </div>
              <div class="detail-row">
                <span class="label">商品成色：</span>
                <span>{{ product.condition || '良好' }}</span>
              </div>

              <a-divider />

              <div class="seller-section">
                <div class="seller-main" @click="router.push(`/user/${product.sellerId}`)">
                  <a-avatar :src="product.sellerAvatar" :size="56">
                    {{ product.sellerName?.charAt(0) }}
                  </a-avatar>
                  <div class="seller-info">
                    <div class="seller-name">
                      {{ product.sellerName }}
                      <a-tag v-if="sellerInfo?.role === 'ADMIN'" color="red" size="small">管理员</a-tag>
                    </div>
                    <div class="seller-stats">
                      <span><star-outlined /> {{ sellerInfo?.creditScore || 100 }} 信用分</span>
                      <span><shopping-outlined /> {{ sellerSoldCount }} 已售</span>
                    </div>
                  </div>
                </div>
                <div class="seller-actions">
                  <a-button 
                    v-if="userStore.isLoggedIn && product.sellerId !== userStore.userInfo?.id"
                    :type="isFollowing ? 'default' : 'primary'"
                    ghost
                    @click="handleFollow"
                    :loading="followLoading"
                  >
                    {{ isFollowing ? '已关注' : '关注' }}
                  </a-button>
                </div>
              </div>

              <div class="action-section" v-if="userStore.isLoggedIn && product.sellerId !== userStore.userInfo?.id">
                <a-button
                  type="primary"
                  size="large"
                  :disabled="!canBuy"
                  @click="handleBuy"
                >
                  <shopping-outlined />
                  立即购买
                </a-button>
                <a-space>
                  <a-button size="large" :type="isFavorited ? 'primary' : 'default'" @click="handleFavorite">
                    <heart-filled v-if="isFavorited" />
                    <heart-outlined v-else />
                    {{ isFavorited ? '已收藏' : '收藏' }}
                  </a-button>
                  <a-button size="large" @click="handleChat">
                    <message-outlined />
                    联系卖家
                  </a-button>
                </a-space>
              </div>

              <div class="action-section" v-else-if="!userStore.isLoggedIn">
                <a-button type="primary" size="large" block @click="router.push('/login')">
                  登录后购买
                </a-button>
              </div>

              <div class="owner-actions" v-else-if="product.sellerId === userStore.userInfo?.id">
                <a-button @click="router.push(`/products/mine?edit=${product.id}`)">
                  <edit-outlined /> 编辑商品
                </a-button>
                <a-button @click="handleToggleStatus">
                  <poweroff-outlined /> {{ product.status === 'ON_SALE' ? '下架' : '上架' }}
                </a-button>
              </div>
            </div>
          </a-col>
        </a-row>

        <a-row :gutter="[24, 24]" class="bottom-section">
          <a-col :xs="24" :md="16">
            <a-card title="商品描述" class="description-card">
              <div class="description-content">
                {{ product.description || '暂无描述' }}
              </div>
            </a-card>

            <a-card title="商品评价" class="reviews-card">
              <template #extra>
                <span v-if="reviews.length > 0">平均评分: {{ averageRating }}</span>
              </template>
              <a-list
                :data-source="reviews"
                :loading="reviewsLoading"
              >
                <template #renderItem="{ item }">
                  <a-list-item>
                    <a-list-item-meta
                      :title="item.isAnonymous ? '匿名用户' : item.reviewerName"
                      :description="item.content"
                    >
                      <template #avatar>
                        <a-avatar>{{ item.isAnonymous ? '匿' : (item.reviewerName?.charAt(0) || 'U') }}</a-avatar>
                      </template>
                    </a-list-item-meta>
                    <template #actions>
                      <a-rate :value="item.rating" disabled />
                      <span class="review-time">{{ formatTime(item.createTime) }}</span>
                    </template>
                  </a-list-item>
                </template>
                <template #empty>
                  <a-empty description="暂无评价" />
                </template>
              </a-list>
            </a-card>
          </a-col>

          <a-col :xs="24" :md="8">
            <a-card title="卖家其他商品" class="other-products-card">
              <a-spin :spinning="otherProductsLoading">
                <div class="other-products" v-if="otherProducts.length > 0">
                  <div 
                    v-for="item in otherProducts" 
                    :key="item.id" 
                    class="other-product-item"
                    @click="router.push(`/products/${item.id}`)"
                  >
                    <img :src="item.coverImage || defaultImage" class="other-product-image" />
                    <div class="other-product-info">
                      <div class="other-product-title">{{ item.title }}</div>
                      <div class="other-product-price">¥{{ item.price }}</div>
                    </div>
                  </div>
                </div>
                <a-empty v-else description="暂无其他商品" />
              </a-spin>
            </a-card>

            <a-card title="相关推荐" class="recommend-card">
              <a-spin :spinning="recommendLoading">
                <div class="recommend-products" v-if="recommendProducts.length > 0">
                  <div 
                    v-for="item in recommendProducts" 
                    :key="item.id" 
                    class="recommend-item"
                    @click="router.push(`/products/${item.id}`)"
                  >
                    <img :src="item.coverImage || defaultImage" class="recommend-image" />
                    <div class="recommend-info">
                      <div class="recommend-title">{{ item.title }}</div>
                      <div class="recommend-price">¥{{ item.price }}</div>
                    </div>
                  </div>
                </div>
                <a-empty v-else description="暂无推荐" />
              </a-spin>
            </a-card>
          </a-col>
        </a-row>
      </template>
    </a-spin>

    <a-modal
      v-model:open="showReportModal"
      title="举报商品"
      @ok="handleReport"
      :confirm-loading="reportLoading"
    >
      <a-form layout="vertical">
        <a-form-item label="举报类型" required>
          <a-select v-model:value="reportForm.type" placeholder="请选择举报类型">
            <a-select-option value="FRAUD">欺诈行为</a-select-option>
            <a-select-option value="INAPPROPRIATE">不当内容</a-select-option>
            <a-select-option value="PROHIBITED">违禁商品</a-select-option>
            <a-select-option value="OTHER">其他</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="举报原因">
          <a-textarea v-model:value="reportForm.reason" placeholder="请详细描述举报原因" :rows="4" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="showShareModal"
      title="分享商品"
      :footer="null"
    >
      <div class="share-options">
        <div class="share-item" @click="copyLink">
          <link-outlined class="share-icon" />
          <span>复制链接</span>
        </div>
        <div class="share-item">
          <wechat-outlined class="share-icon wechat" />
          <span>微信</span>
        </div>
        <div class="share-item">
          <qq-outlined class="share-icon qq" />
          <span>QQ</span>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { productApi, reviewApi, userApi } from '@/api'
import { followApi, reportApi } from '@/types/extra'
import type { Product, Review, User } from '@/types'
import { message, Modal } from 'ant-design-vue'
import {
  LeftOutlined,
  EyeOutlined,
  HeartOutlined,
  HeartFilled,
  ShoppingOutlined,
  MessageOutlined,
  StarOutlined,
  ClockCircleOutlined,
  EnvironmentOutlined,
  ShareAltOutlined,
  MoreOutlined,
  WarningOutlined,
  EditOutlined,
  PoweroffOutlined,
  LinkOutlined,
  WechatOutlined,
  QqOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const reviewsLoading = ref(false)
const otherProductsLoading = ref(false)
const recommendLoading = ref(false)
const product = ref<Product | null>(null)
const reviews = ref<Review[]>([])
const otherProducts = ref<Product[]>([])
const recommendProducts = ref<Product[]>([])
const isFavorited = ref(false)
const isFollowing = ref(false)
const followLoading = ref(false)
const sellerInfo = ref<User | null>(null)
const sellerSoldCount = ref(0)

const showReportModal = ref(false)
const reportLoading = ref(false)
const reportForm = reactive({
  type: '' as '' | 'FRAUD' | 'INAPPROPRIATE' | 'PROHIBITED' | 'OTHER',
  reason: ''
})

const showShareModal = ref(false)

const defaultImage = '/no-image.svg'

const statusColor = computed(() => {
  const colors: Record<string, string> = {
    ON_SALE: 'green',
    OFF_SHELF: 'red',
    SOLD: 'gray'
  }
  return colors[product.value?.status || 'ON_SALE'] || 'default'
})

const statusText = computed(() => {
  const texts: Record<string, string> = {
    ON_SALE: '在售',
    OFF_SHELF: '已下架',
    SOLD: '已售'
  }
  return texts[product.value?.status || 'ON_SALE'] || ''
})

const tradeTypeText = computed(() => {
  const texts: Record<string, string> = {
    FACE_TO_FACE: '当面交易',
    EXPRESS: '快递邮寄',
    BOTH: '当面交易 / 快递邮寄'
  }
  return texts[product.value?.tradeType || 'BOTH'] || ''
})

const canBuy = computed(() => {
  return product.value?.status === 'ON_SALE' && 
         product.value?.sellerId !== userStore.userInfo?.id &&
         userStore.isLoggedIn
})

const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0
  const sum = reviews.value.reduce((acc, r) => acc + r.rating, 0)
  return (sum / reviews.value.length).toFixed(1)
})

const formatTime = (time: string) => {
  return new Date(time).toLocaleDateString('zh-CN')
}

const formatRelativeTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  return date.toLocaleDateString('zh-CN')
}

const fetchProduct = async () => {
  const id = Number(route.params.id)
  loading.value = true
  try {
    const res = await productApi.getProductDetail(id)
    product.value = res.data
    if (userStore.isLoggedIn) {
      isFavorited.value = await checkFavorite()
      if (product.value.sellerId !== userStore.userInfo?.id) {
        checkFollowing()
      }
    }
    fetchSellerInfo()
    fetchOtherProducts()
    fetchRecommendProducts()
  } finally {
    loading.value = false
  }
}

const fetchSellerInfo = async () => {
  if (!product.value) return
  try {
    const res = await userApi.getUserById(product.value.sellerId)
    sellerInfo.value = res.data as User
  } catch {
    // ignore
  }
}

const fetchReviews = async () => {
  if (!product.value) return
  reviewsLoading.value = true
  try {
    const res = await reviewApi.getProductReviews(product.value.id)
    reviews.value = res.data.list
  } finally {
    reviewsLoading.value = false
  }
}

const fetchOtherProducts = async () => {
  if (!product.value) return
  otherProductsLoading.value = true
  try {
    const res = await productApi.getProducts({
      sellerId: product.value.sellerId,
      page: 1,
      size: 5
    })
    otherProducts.value = res.data.list.filter((p: Product) => p.id !== product.value!.id)
  } finally {
    otherProductsLoading.value = false
  }
}

const fetchRecommendProducts = async () => {
  if (!product.value) return
  recommendLoading.value = true
  try {
    const res = await productApi.getProducts({
      categoryId: product.value.categoryId,
      page: 1,
      size: 5
    })
    recommendProducts.value = res.data.list.filter((p: Product) => p.id !== product.value!.id)
  } finally {
    recommendLoading.value = false
  }
}

const checkFavorite = async () => {
  return false
}

const checkFollowing = async () => {
  if (!product.value) return
  try {
    const res = await followApi.checkFollow(product.value.sellerId)
    isFollowing.value = res.data
  } catch {
    // ignore
  }
}

const handleBuy = () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  
  Modal.confirm({
    title: '确认购买',
    content: `确定要购买「${product.value?.title}」吗？`,
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      router.push({
        name: 'CreateOrder',
        query: { productId: String(product.value?.id) }
      })
    }
  })
}

const handleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    return
  }
  
  try {
    if (isFavorited.value) {
      await userApi.removeFavorite(product.value!.id)
      message.success('已取消收藏')
    } else {
      await userApi.addFavorite(product.value!.id)
      message.success('收藏成功')
    }
    isFavorited.value = !isFavorited.value
  } catch {
    // error handled by request
  }
}

const handleFollow = async () => {
  if (!product.value) return
  followLoading.value = true
  try {
    if (isFollowing.value) {
      await followApi.unfollow(product.value.sellerId)
      message.success('已取消关注')
    } else {
      await followApi.follow(product.value.sellerId)
      message.success('关注成功')
    }
    isFollowing.value = !isFollowing.value
  } finally {
    followLoading.value = false
  }
}

const handleChat = () => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    return
  }
  router.push(`/chat/${product.value?.sellerId}`)
}

const handleShare = () => {
  showShareModal.value = true
}

const copyLink = () => {
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    message.success('链接已复制')
    showShareModal.value = false
  })
}

const handleReport = async () => {
  if (!reportForm.type) {
    message.warning('请选择举报类型')
    return
  }
  
  reportLoading.value = true
  try {
    await reportApi.createReport(product.value!.id, reportForm.type, reportForm.reason)
    message.success('举报成功，我们会尽快处理')
    showReportModal.value = false
    reportForm.type = ''
    reportForm.reason = ''
  } finally {
    reportLoading.value = false
  }
}

const handleToggleStatus = async () => {
  if (!product.value) return
  try {
    if (product.value.status === 'ON_SALE') {
      await productApi.offShelfProduct(product.value.id)
      product.value.status = 'OFF_SHELF'
      message.success('商品已下架')
    } else {
      await productApi.onShelfProduct(product.value.id)
      product.value.status = 'ON_SALE'
      message.success('商品已上架')
    }
  } catch {
    // ignore
  }
}

onMounted(() => {
  fetchProduct().then(() => {
    fetchReviews()
  })
})
</script>

<style scoped>
.product-detail-page {
  min-height: calc(100vh - 64px);
}

.back-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.image-section {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e4e4e7;
}

.product-carousel {
  aspect-ratio: 1;
}

.main-image {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
}

.info-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e4e4e7;
}

.title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.product-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #18181b;
  flex: 1;
}

.price-section {
  margin-bottom: 16px;
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.price {
  font-size: 32px;
  font-weight: 600;
  color: #ef4444;
}

.original-price {
  font-size: 16px;
  color: #71717a;
  text-decoration: line-through;
}

.discount {
  font-size: 12px;
  color: #fff;
  background: #ef4444;
  padding: 2px 8px;
  border-radius: 4px;
}

.meta-section {
  color: #71717a;
  font-size: 14px;
}

.detail-row {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
}

.detail-row .label {
  color: #71717a;
  width: 80px;
  flex-shrink: 0;
}

.seller-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  padding: 16px;
  background: #f4f4f5;
  border-radius: 12px;
}

.seller-main {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.seller-name {
  font-weight: 500;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #18181b;
}

.seller-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #71717a;
}

.action-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.owner-actions {
  display: flex;
  gap: 12px;
}

.bottom-section {
  margin-top: 24px;
}

.description-card,
.reviews-card,
.other-products-card,
.recommend-card {
  margin-bottom: 24px;
  border-radius: 12px;
  border: 1px solid #e4e4e7;
}

.description-content {
  white-space: pre-wrap;
  line-height: 1.8;
  color: #52525b;
}

.review-time {
  font-size: 12px;
  color: #71717a;
}

.other-products {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.other-product-item {
  display: flex;
  gap: 12px;
  padding: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.other-product-item:hover {
  background: #f4f4f5;
}

.other-product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.other-product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.other-product-title {
  font-size: 14px;
  color: #18181b;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.other-product-price {
  font-size: 16px;
  font-weight: 600;
  color: #ef4444;
}

.recommend-products {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommend-item {
  display: flex;
  gap: 12px;
  padding: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.recommend-item:hover {
  background: #f4f4f5;
}

.recommend-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
}

.recommend-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.recommend-title {
  font-size: 13px;
  color: #18181b;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recommend-price {
  font-size: 14px;
  font-weight: 600;
  color: #ef4444;
}

.share-options {
  display: flex;
  justify-content: center;
  gap: 40px;
  padding: 20px;
}

.share-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.share-icon {
  font-size: 36px;
  color: #18181b;
}

.share-icon.wechat {
  color: #07c160;
}

.share-icon.qq {
  color: #12b7f5;
}

@media (max-width: 768px) {
  .product-title {
    font-size: 20px;
  }

  .price {
    font-size: 24px;
  }

  .info-section {
    padding: 16px;
  }

  .seller-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
