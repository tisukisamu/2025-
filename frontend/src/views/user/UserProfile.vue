<template>
  <div class="user-profile-page">
    <a-spin :spinning="loading">
      <template v-if="userInfo">
        <div class="back-nav">
          <a-button type="text" @click="router.back()">
            <left-outlined /> 返回
          </a-button>
        </div>
        <div class="profile-header">
          <div class="cover-image"></div>
          <div class="profile-info">
            <a-avatar :src="userInfo.avatar" :size="100">
              {{ userInfo.username?.charAt(0)?.toUpperCase() }}
            </a-avatar>
            <div class="user-details">
              <h1>
                {{ userInfo.nickname || userInfo.username }}
                <a-tag v-if="userInfo.role === 'ADMIN'" color="red">管理员</a-tag>
              </h1>
              <p class="bio">{{ userInfo.bio || '这个人很懒，什么都没写' }}</p>
              <div class="user-meta">
                <span><environment-outlined /> {{ userInfo.school || '未设置学校' }}</span>
                <span><calendar-outlined /> 加入于 {{ formatTime(userInfo.createTime) }}</span>
              </div>
            </div>
            <div class="profile-actions" v-if="userStore.isLoggedIn && userInfo.id !== userStore.userInfo?.id">
              <a-button 
                :type="isFollowing ? 'default' : 'primary'"
                @click="handleFollow"
                :loading="followLoading"
              >
                {{ isFollowing ? '已关注' : '关注' }}
              </a-button>
              <a-button @click="router.push(`/chat/${userInfo.id}`)">
                <message-outlined /> 私信
              </a-button>
            </div>
          </div>
        </div>

        <div class="stats-section">
          <div class="stat-item" @click="activeTab = 'products'">
            <div class="stat-value">{{ stats.productCount }}</div>
            <div class="stat-label">发布商品</div>
          </div>
          <div class="stat-item" @click="activeTab = 'sold'">
            <div class="stat-value">{{ stats.soldCount }}</div>
            <div class="stat-label">已售出</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ userInfo.creditScore || 100 }}</div>
            <div class="stat-label">信用分</div>
          </div>
          <div class="stat-item" @click="showFollowModal = true">
            <div class="stat-value">{{ stats.followingCount }}</div>
            <div class="stat-label">关注</div>
          </div>
          <div class="stat-item" @click="showFollowModal = true">
            <div class="stat-value">{{ stats.followerCount }}</div>
            <div class="stat-label">粉丝</div>
          </div>
        </div>

        <a-tabs v-model:activeKey="activeTab" class="content-tabs">
          <a-tab-pane key="products" tab="在售商品">
            <a-spin :spinning="productsLoading">
              <div class="products-grid" v-if="products.length > 0">
                <product-card 
                  v-for="product in products" 
                  :key="product.id" 
                  :product="product"
                  @click="router.push(`/products/${product.id}`)"
                />
              </div>
              <a-empty v-else description="暂无在售商品" />
            </a-spin>
          </a-tab-pane>
          
          <a-tab-pane key="sold" tab="已售商品">
            <a-spin :spinning="soldLoading">
              <div class="products-grid" v-if="soldProducts.length > 0">
                <product-card 
                  v-for="product in soldProducts" 
                  :key="product.id" 
                  :product="product"
                  @click="router.push(`/products/${product.id}`)"
                />
              </div>
              <a-empty v-else description="暂无已售商品" />
            </a-spin>
          </a-tab-pane>
          
          <a-tab-pane key="reviews" tab="收到的评价">
            <a-spin :spinning="reviewsLoading">
              <div class="reviews-list" v-if="reviews.length > 0">
                <div v-for="review in reviews" :key="review.id" class="review-item">
                  <div class="review-header">
                    <a-avatar>{{ review.reviewerName?.charAt(0) || 'U' }}</a-avatar>
                    <div class="review-meta">
                      <span class="reviewer-name">{{ review.isAnonymous ? '匿名用户' : review.reviewerName }}</span>
                      <a-rate :value="review.rating" disabled />
                    </div>
                    <span class="review-time">{{ formatTime(review.createTime) }}</span>
                  </div>
                  <div class="review-content">{{ review.content }}</div>
                </div>
              </div>
              <a-empty v-else description="暂无评价" />
            </a-spin>
          </a-tab-pane>
        </a-tabs>
      </template>
    </a-spin>

    <a-modal v-model:open="showFollowModal" title="关注列表" :footer="null" width="400px">
      <a-tabs>
        <a-tab-pane key="following" tab="关注">
          <a-list :data-source="followingList" :loading="followListLoading">
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #avatar>
                    <a-avatar :src="item.avatar">{{ item.username?.charAt(0) }}</a-avatar>
                  </template>
                  <template #title>
                    <router-link :to="`/user/${item.id}`">{{ item.nickname || item.username }}</router-link>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>
        </a-tab-pane>
        <a-tab-pane key="followers" tab="粉丝">
          <a-list :data-source="followerList" :loading="followListLoading">
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #avatar>
                    <a-avatar :src="item.avatar">{{ item.username?.charAt(0) }}</a-avatar>
                  </template>
                  <template #title>
                    <router-link :to="`/user/${item.id}`">{{ item.nickname || item.username }}</router-link>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>
        </a-tab-pane>
      </a-tabs>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { userApi, productApi, reviewApi } from '@/api'
import { followApi } from '@/types/extra'
import type { User, Product, Review } from '@/types'
import ProductCard from '@/components/ProductCard.vue'
import { EnvironmentOutlined, CalendarOutlined, MessageOutlined, LeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const productsLoading = ref(false)
const soldLoading = ref(false)
const reviewsLoading = ref(false)
const followLoading = ref(false)
const followListLoading = ref(false)

const userInfo = ref<User | null>(null)
const products = ref<Product[]>([])
const soldProducts = ref<Product[]>([])
const reviews = ref<Review[]>([])
const followingList = ref<User[]>([])
const followerList = ref<User[]>([])
const isFollowing = ref(false)
const showFollowModal = ref(false)

const activeTab = ref('products')

const stats = reactive({
  productCount: 0,
  soldCount: 0,
  followingCount: 0,
  followerCount: 0
})

const formatTime = (time: string) => {
  return new Date(time).toLocaleDateString('zh-CN')
}

const fetchUserInfo = async () => {
  const userId = Number(route.params.id)
  loading.value = true
  try {
    const res = await userApi.getUserById(userId)
    userInfo.value = res.data as User
    fetchStats()
    fetchProducts()
    if (userStore.isLoggedIn && userInfo.value.id !== userStore.userInfo?.id) {
      checkFollowing()
    }
  } finally {
    loading.value = false
  }
}

const fetchStats = async () => {
  if (!userInfo.value) return
  try {
    const res = await followApi.getFollowStats(userInfo.value.id)
    stats.followingCount = res.data.followingCount
    stats.followerCount = res.data.followerCount
  } catch {
    // ignore
  }
}

const fetchProducts = async () => {
  if (!userInfo.value) return
  productsLoading.value = true
  try {
    const res = await productApi.getProducts({
      sellerId: userInfo.value.id,
      status: 'ON_SALE',
      page: 1,
      size: 20
    })
    products.value = res.data.list
    stats.productCount = res.data.total
  } finally {
    productsLoading.value = false
  }
}

const fetchSoldProducts = async () => {
  if (!userInfo.value) return
  soldLoading.value = true
  try {
    const res = await productApi.getProducts({
      sellerId: userInfo.value.id,
      status: 'SOLD',
      page: 1,
      size: 20
    })
    soldProducts.value = res.data.list
    stats.soldCount = res.data.total
  } finally {
    soldLoading.value = false
  }
}

const fetchReviews = async () => {
  if (!userInfo.value) return
  reviewsLoading.value = true
  try {
    const res = await reviewApi.getUserReviews(userInfo.value.id)
    reviews.value = res.data.list
  } finally {
    reviewsLoading.value = false
  }
}

const checkFollowing = async () => {
  if (!userInfo.value) return
  try {
    const res = await followApi.checkFollow(userInfo.value.id)
    isFollowing.value = res.data
  } catch {
    // ignore
  }
}

const handleFollow = async () => {
  if (!userInfo.value) return
  followLoading.value = true
  try {
    if (isFollowing.value) {
      await followApi.unfollow(userInfo.value.id)
      stats.followerCount--
    } else {
      await followApi.follow(userInfo.value.id)
      stats.followerCount++
    }
    isFollowing.value = !isFollowing.value
  } finally {
    followLoading.value = false
  }
}

watch(activeTab, (tab) => {
  if (tab === 'sold' && soldProducts.value.length === 0) {
    fetchSoldProducts()
  } else if (tab === 'reviews' && reviews.value.length === 0) {
    fetchReviews()
  }
})

watch(showFollowModal, async (show) => {
  if (show && followingList.value.length === 0 && followerList.value.length === 0) {
    followListLoading.value = true
    try {
      const [followingRes, followerRes] = await Promise.all([
        followApi.getFollowing(1, 20),
        followApi.getFollowers(1, 20)
      ])
      followingList.value = followingRes.data.list
      followerList.value = followerRes.data.list
    } finally {
      followListLoading.value = false
    }
  }
})

watch(() => route.params.id, () => {
  fetchUserInfo()
})

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.user-profile-page {
  background: #f5f5f5;
}

.back-nav {
  margin-bottom: 16px;
}

.back-nav :deep(.ant-btn) {
  color: #1890ff;
  padding: 4px 8px;
}

.back-nav :deep(.ant-btn:hover) {
  background: #e6f7ff;
}

.profile-header {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 16px;
}

.cover-image {
  height: 120px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
}

.profile-info {
  display: flex;
  align-items: flex-start;
  gap: 24px;
  padding: 0 24px 24px;
  margin-top: -50px;
}

.profile-info .ant-avatar {
  border: 4px solid #fff;
  flex-shrink: 0;
}

.user-details {
  flex: 1;
  padding-top: 60px;
}

.user-details h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.bio {
  color: #666;
  margin-bottom: 8px;
}

.user-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #999;
}

.user-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.profile-actions {
  display: flex;
  gap: 12px;
  padding-top: 60px;
}

.stats-section {
  display: flex;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 16px;
}

.stat-item {
  flex: 1;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.stat-item:hover {
  background: #f5f5f5;
  border-radius: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}

.content-tabs {
  background: #fff;
  border-radius: 12px;
  padding: 16px 24px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.review-meta {
  flex: 1;
}

.reviewer-name {
  font-weight: 500;
  margin-right: 8px;
}

.review-time {
  font-size: 12px;
  color: #999;
}

.review-content {
  color: #666;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .profile-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 0 16px 16px;
  }

  .user-details {
    padding-top: 16px;
  }

  .user-meta {
    flex-direction: column;
    gap: 8px;
  }

  .profile-actions {
    padding-top: 16px;
    width: 100%;
    justify-content: center;
  }

  .stats-section {
    padding: 16px;
  }

  .stat-value {
    font-size: 20px;
  }

  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}
</style>
