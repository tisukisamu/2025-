<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { getPrimaryImageUrl, getFallbackImageUrl, type ProductImageInfo } from '../utils/imageLoader'
import { ShoppingOutlined } from '@ant-design/icons-vue'

interface Props {
  product: ProductImageInfo | null | undefined
  alt?: string
  width?: string | number
  height?: string | number
  objectFit?: 'cover' | 'contain' | 'fill' | 'none' | 'scale-down'
  borderRadius?: string
  placeholder?: boolean
  lazy?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  alt: '',
  objectFit: 'cover',
  borderRadius: '8px',
  placeholder: true,
  lazy: true
})

const emit = defineEmits<{
  load: []
  error: []
}>()

// 加载状态
const isLoading = ref(true)
const hasError = ref(false)
const useFallback = ref(false)

// 计算当前图片URL
const currentImageUrl = computed(() => {
  if (!props.product) return ''
  
  // 如果启用了降级，使用绝对路径
  if (useFallback.value) {
    return getFallbackImageUrl(props.product)
  }
  
  // 否则使用主图（相对路径优先）
  return getPrimaryImageUrl(props.product)
})

// 是否有图片
const hasImage = computed(() => {
  return !!currentImageUrl.value
})

// 样式对象
const imageStyle = computed(() => ({
  width: typeof props.width === 'number' ? `${props.width}px` : props.width || '100%',
  height: typeof props.height === 'number' ? `${props.height}px` : props.height || '100%',
  objectFit: props.objectFit,
  borderRadius: props.borderRadius
}))

// 监听产品变化，重置状态
watch(() => props.product, () => {
  isLoading.value = true
  hasError.value = false
  useFallback.value = false
}, { deep: true })

// 处理图片加载成功
const handleLoad = () => {
  isLoading.value = false
  hasError.value = false
  emit('load')
}

// 处理图片加载失败
const handleError = () => {
  if (!useFallback.value) {
    // 第一次失败，尝试降级到绝对路径
    const fallbackUrl = getFallbackImageUrl(props.product)
    if (fallbackUrl && fallbackUrl !== currentImageUrl.value) {
      useFallback.value = true
      isLoading.value = true
      return
    }
  }
  
  // 降级也失败或没有降级选项
  isLoading.value = false
  hasError.value = true
  emit('error')
}

// 重新加载
const retry = () => {
  isLoading.value = true
  hasError.value = false
  useFallback.value = false
}
</script>

<template>
  <div class="product-image-wrapper" :style="{ borderRadius }">
    <!-- 加载中 -->
    <div v-if="isLoading && hasImage" class="image-loading">
      <div class="loading-spinner"></div>
    </div>
    
    <!-- 加载失败 -->
    <div v-else-if="hasError || !hasImage" class="image-error">
      <div class="error-content">
        <ShoppingOutlined class="error-icon" />
        <span class="error-text">暂无图片</span>
        <button v-if="hasImage" class="retry-btn" @click="retry">
          重试
        </button>
      </div>
    </div>
    
    <!-- 图片 -->
    <img
      v-if="hasImage"
      :src="currentImageUrl"
      :alt="alt"
      :style="imageStyle"
      :loading="lazy ? 'lazy' : 'eager'"
      class="product-image"
      @load="handleLoad"
      @error="handleError"
    />
    
    <!-- 降级提示（开发环境可见） -->
    <div v-if="useFallback && !hasError" class="fallback-badge" title="已降级到备用图片">
      备用
    </div>
  </div>
</template>

<style scoped>
.product-image-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: #f5f5f5;
}

.product-image {
  display: block;
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.product-image-wrapper:hover .product-image {
  transform: scale(1.05);
}

/* 加载中状态 */
.image-loading {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e5e7eb;
  border-top-color: #10b981;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 错误状态 */
.image-error {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}

.error-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #9ca3af;
}

.error-icon {
  font-size: 32px;
}

.error-text {
  font-size: 12px;
}

.retry-btn {
  padding: 4px 12px;
  font-size: 12px;
  color: #10b981;
  background: transparent;
  border: 1px solid #10b981;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.retry-btn:hover {
  color: #fff;
  background: #10b981;
}

/* 降级提示 */
.fallback-badge {
  position: absolute;
  top: 4px;
  right: 4px;
  padding: 2px 6px;
  font-size: 10px;
  color: #fff;
  background: rgba(245, 158, 11, 0.9);
  border-radius: 4px;
  pointer-events: none;
}
</style>
