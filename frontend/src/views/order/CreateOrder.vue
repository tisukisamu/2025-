<template>
  <div class="create-order-page">
    <a-spin :spinning="loading">
      <template v-if="product">
        <a-page-header
          title="确认订单"
          @back="router.back()"
        />

        <a-row :gutter="[24, 24]">
          <a-col :xs="24" :lg="16">
            <a-card title="商品信息" class="product-card">
              <div class="product-content">
                <img :src="product.coverImage || defaultImage" class="product-image" />
                <div class="product-info">
                  <h3>{{ product.title }}</h3>
                  <p class="product-desc">{{ product.description }}</p>
                  <div class="product-price">
                    <span class="price">¥{{ product.price }}</span>
                    <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
                  </div>
                </div>
              </div>
            </a-card>

            <a-card title="交易方式" class="trade-card">
              <a-radio-group v-model:value="tradeType">
                <a-radio value="FACE_TO_FACE">当面交易</a-radio>
                <a-radio value="EXPRESS">快递邮寄</a-radio>
              </a-radio-group>
              
              <div class="trade-info" v-if="tradeType === 'FACE_TO_FACE'">
                <info-circle-outlined />
                <span>当面交易：买卖双方约定时间地点进行交易</span>
              </div>
              
              <a-form layout="vertical" v-if="tradeType === 'EXPRESS'" class="address-form">
                <a-form-item label="收货地址" required>
                  <a-textarea v-model:value="address" placeholder="请输入详细收货地址" :rows="2" />
                </a-form-item>
              </a-form>
            </a-card>
          </a-col>

          <a-col :xs="24" :lg="8">
            <a-card title="订单金额" class="amount-card">
              <div class="amount-row">
                <span>商品金额</span>
                <span>¥{{ product.price }}</span>
              </div>
              <div class="amount-row" v-if="tradeType === 'EXPRESS'">
                <span>运费</span>
                <span>包邮</span>
              </div>
              <a-divider />
              <div class="amount-total">
                <span>合计</span>
                <span class="total-price">¥{{ product.price }}</span>
              </div>
            </a-card>

            <a-card title="卖家信息" class="seller-card">
              <div class="seller-info">
                <a-avatar :src="product.sellerAvatar" :size="48">
                  {{ product.sellerName?.charAt(0) }}
                </a-avatar>
                <div class="seller-detail">
                  <div class="seller-name">{{ product.sellerName }}</div>
                  <div class="seller-credit">
                    <star-outlined />
                    {{ product.sellerCredit || 100 }} 信用分
                  </div>
                </div>
              </div>
            </a-card>

            <a-button type="primary" size="large" block :loading="submitting" @click="handleSubmit">
              提交订单
            </a-button>
          </a-col>
        </a-row>
      </template>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { productApi, orderApi } from '@/api'
import type { Product } from '@/types'
import { message, Modal } from 'ant-design-vue'
import { InfoCircleOutlined, StarOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const product = ref<Product | null>(null)
const tradeType = ref<'FACE_TO_FACE' | 'EXPRESS'>('FACE_TO_FACE')
const address = ref('')

const defaultImage = '/no-image.svg'

const fetchProduct = async () => {
  const productId = Number(route.query.productId)
  if (!productId || isNaN(productId)) {
    message.error('商品ID无效')
    router.push('/products')
    return
  }

  loading.value = true
  try {
    const res = await productApi.getProductDetail(productId)
    product.value = res.data
    
    if (product.value.sellerId === userStore.userInfo?.id) {
      message.error('不能购买自己的商品')
      router.push('/products')
    }
  } catch {
    message.error('获取商品信息失败')
    router.push('/products')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!product.value) return
  
  if (tradeType.value === 'EXPRESS' && !address.value.trim()) {
    message.warning('请填写收货地址')
    return
  }

  Modal.confirm({
    title: '确认提交',
    content: '确定要提交订单吗？',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      submitting.value = true
      try {
        const res = await orderApi.createOrder({
          productId: product.value!.id,
          tradeType: tradeType.value,
          address: tradeType.value === 'EXPRESS' ? address.value : undefined
        })
        message.success('订单创建成功')
        router.push(`/orders/${res.data.id}`)
      } catch {
        // error handled by request
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  fetchProduct()
})
</script>

<style scoped>
.create-order-page {
  background: #f5f5f5;
  min-height: calc(100vh - 200px);
}

.product-card,
.trade-card,
.amount-card,
.seller-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.product-content {
  display: flex;
  gap: 16px;
}

.product-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
}

.product-info h3 {
  font-size: 18px;
  font-weight: 500;
  margin: 0 0 8px;
}

.product-desc {
  color: #999;
  font-size: 14px;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price .price {
  font-size: 20px;
  font-weight: 600;
  color: #f5222d;
}

.product-price .original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
  margin-left: 8px;
}

.trade-info {
  margin-top: 12px;
  padding: 12px;
  background: #e6f7ff;
  border-radius: 8px;
  font-size: 13px;
  color: #1890ff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.address-form {
  margin-top: 16px;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  color: #666;
}

.amount-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
}

.total-price {
  font-size: 24px;
  color: #f5222d;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.seller-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.seller-credit {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

@media (max-width: 768px) {
  .product-content {
    flex-direction: column;
  }

  .product-image {
    width: 100%;
    height: 150px;
  }
}
</style>
