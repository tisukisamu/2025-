/**
 * 图片加载工具函数
 * 实现双保底策略：优先使用相对路径，失败时降级到绝对路径
 */

import { ref, computed, type Ref } from 'vue'

/**
 * 图片路径类型
 */
export type ImagePathType = 'relative' | 'absolute' | 'fallback' | 'none'

/**
 * 产品图片信息接口
 */
export interface ProductImageInfo {
  imageUrl?: string
  imageUrls?: string
  primaryImageUrl?: string
  primaryImageType?: string
  fallbackImageUrl?: string
  allImageUrls?: string[]
  imagePathType?: string
  imagePathTypeDesc?: string
}

/**
 * 获取产品主图URL（应用双保底策略）
 * @param product 产品对象
 * @returns 主图URL
 */
export function getPrimaryImageUrl(product: ProductImageInfo | null | undefined): string {
  if (!product) return ''

  // 1. 优先使用后端处理好的 primaryImageUrl
  if (product.primaryImageUrl) {
    return product.primaryImageUrl
  }

  // 2. 解析 imageUrls（相对路径）
  if (product.imageUrls) {
    const urls = parseImageUrls(product.imageUrls)
    if (urls.length > 0) {
      const url = urls[0]
      // 确保相对路径以 / 开头
      if (!url.startsWith('http') && !url.startsWith('/')) {
        return '/' + url
      }
      return url
    }
  }

  // 3. 降级使用 imageUrl（绝对路径）
  if (product.imageUrl) {
    return product.imageUrl
  }

  return ''
}

/**
 * 获取产品所有图片URL列表
 * @param product 产品对象
 * @returns 图片URL列表
 */
export function getAllImageUrls(product: ProductImageInfo | null | undefined): string[] {
  if (!product) return []

  // 1. 优先使用后端处理好的 allImageUrls
  if (product.allImageUrls && product.allImageUrls.length > 0) {
    return product.allImageUrls
  }

  // 2. 解析 imageUrls
  if (product.imageUrls) {
    const urls = parseImageUrls(product.imageUrls)
    if (urls.length > 0) {
      return urls.map(url => {
        if (!url.startsWith('http') && !url.startsWith('/')) {
          return '/' + url
        }
        return url
      })
    }
  }

  // 3. 降级使用 imageUrl
  if (product.imageUrl) {
    return [product.imageUrl]
  }

  return []
}

/**
 * 获取降级图片URL（用于加载失败时）
 * @param product 产品对象
 * @returns 降级URL
 */
export function getFallbackImageUrl(product: ProductImageInfo | null | undefined): string {
  if (!product) return ''

  // 1. 优先使用后端处理好的 fallbackImageUrl
  if (product.fallbackImageUrl) {
    return product.fallbackImageUrl
  }

  // 2. 使用 imageUrl
  return product.imageUrl || ''
}

/**
 * 解析逗号分隔的图片URL字符串
 * @param imageUrls 逗号分隔的URL字符串
 * @returns URL数组
 */
export function parseImageUrls(imageUrls: string | null | undefined): string[] {
  if (!imageUrls) return []
  return imageUrls
    .split(',')
    .map(url => url.trim())
    .filter(url => url.length > 0)
}

/**
 * 判断是否为相对路径
 * @param url 图片URL
 * @returns 是否为相对路径
 */
export function isRelativePath(url: string): boolean {
  return !url.startsWith('http://') && !url.startsWith('https://')
}

/**
 * 确保URL以 / 开头（用于相对路径）
 * @param url 图片URL
 * @returns 处理后的URL
 */
export function ensureLeadingSlash(url: string): string {
  if (!url) return url
  if (url.startsWith('http')) return url
  return url.startsWith('/') ? url : '/' + url
}

/**
 * 使用组合式函数管理图片加载状态
 * @param product 产品对象
 * @returns 图片加载状态和URL
 */
export function useProductImage(product: Ref<ProductImageInfo | null | undefined>) {
  const loadError = ref(false)
  const isLoading = ref(true)
  const useFallback = ref(false)

  // 计算当前应该使用的图片URL
  const currentImageUrl = computed(() => {
    const p = product.value
    if (!p) return ''

    // 如果已经启用降级，使用绝对路径
    if (useFallback.value) {
      return getFallbackImageUrl(p)
    }

    // 否则使用主图（相对路径优先）
    return getPrimaryImageUrl(p)
  })

  // 获取降级URL
  const fallbackUrl = computed(() => {
    return getFallbackImageUrl(product.value)
  })

  // 处理图片加载错误
  const handleError = () => {
    if (!useFallback.value && fallbackUrl.value) {
      // 第一次失败，尝试降级
      useFallback.value = true
      loadError.value = false
    } else {
      // 降级也失败
      loadError.value = true
    }
    isLoading.value = false
  }

  // 处理图片加载成功
  const handleLoad = () => {
    isLoading.value = false
    loadError.value = false
  }

  // 重置状态
  const reset = () => {
    loadError.value = false
    isLoading.value = true
    useFallback.value = false
  }

  return {
    currentImageUrl,
    fallbackUrl,
    loadError,
    isLoading,
    useFallback,
    handleError,
    handleLoad,
    reset
  }
}

/**
 * 创建图片加载器（用于非响应式场景）
 * @param primaryUrl 主图URL
 * @param fallbackUrl 降级URL
 * @returns 图片加载器
 */
export function createImageLoader(primaryUrl: string, fallbackUrl?: string) {
  let currentUrl = primaryUrl
  let hasTriedFallback = false

  const getCurrentUrl = () => currentUrl

  const onError = () => {
    if (!hasTriedFallback && fallbackUrl && fallbackUrl !== primaryUrl) {
      currentUrl = fallbackUrl
      hasTriedFallback = true
      return true // 已切换，需要重试
    }
    return false // 没有更多降级选项
  }

  const reset = () => {
    currentUrl = primaryUrl
    hasTriedFallback = false
  }

  return {
    getCurrentUrl,
    onError,
    reset,
    hasTriedFallback: () => hasTriedFallback
  }
}
