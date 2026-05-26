<template>
  <div class="product-card" @click="$emit('click')">
    <div class="card-image">
      <img :src="product.coverImage || defaultImage" :alt="product.title" />
      <div class="card-status" v-if="product.status !== 'ON_SALE'">
        <a-tag :color="statusColor">{{ statusText }}</a-tag>
      </div>
    </div>
    <div class="card-content">
      <h3 class="card-title">{{ product.title }}</h3>
      <div class="card-price">
        <span class="price">¥{{ product.price }}</span>
        <span class="original-price" v-if="product.originalPrice">
          ¥{{ product.originalPrice }}
        </span>
      </div>
      <div class="card-footer">
        <div class="seller-info">
          <a-avatar :size="20" :src="product.sellerAvatar">
            {{ product.sellerName?.charAt(0) }}
          </a-avatar>
          <span class="seller-name">{{ product.sellerName }}</span>
        </div>
        <div class="product-stats">
          <span><eye-outlined /> {{ product.viewCount }}</span>
          <span><heart-outlined /> {{ product.favoriteCount }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Product } from '@/types'
import { EyeOutlined, HeartOutlined } from '@ant-design/icons-vue'

const props = defineProps<{
  product: Product
}>()

defineEmits<{
  click: []
}>()

const defaultImage = '/no-image.svg'

const statusColor = computed(() => {
  const colors: Record<string, string> = {
    DRAFT: 'default',
    PENDING: 'orange',
    ON_SALE: 'green',
    OFF_SHELF: 'red',
    SOLD: 'gray'
  }
  return colors[props.product.status] || 'default'
})

const statusText = computed(() => {
  const texts: Record<string, string> = {
    DRAFT: '草稿',
    PENDING: '审核中',
    ON_SALE: '在售',
    OFF_SHELF: '已下架',
    SOLD: '已售'
  }
  return texts[props.product.status] || ''
})
</script>

<style scoped>
.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-image {
  position: relative;
  aspect-ratio: 1;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .card-image img {
  transform: scale(1.05);
}

.card-status {
  position: absolute;
  top: 8px;
  right: 8px;
}

.card-content {
  padding: 12px;
}

.card-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 8px;
}

.price {
  font-size: 18px;
  font-weight: 600;
  color: #f5222d;
}

.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.seller-name {
  font-size: 12px;
  color: #666;
  max-width: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-stats {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: #999;
}

.product-stats span {
  display: flex;
  align-items: center;
  gap: 2px;
}
</style>
