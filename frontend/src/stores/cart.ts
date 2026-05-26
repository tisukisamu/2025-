import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { CartItem, Product } from '../api'

interface CartItemWithInfo extends CartItem {
  productName: string
  price: number
  imageUrl?: string
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItemWithInfo[]>(JSON.parse(localStorage.getItem('cart') || '[]'))
  const loaded = ref(true)
  
  const totalItems = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))
  const totalPrice = computed(() => items.value.reduce((sum, item) => sum + item.price * item.quantity, 0))

  function saveToStorage() {
    localStorage.setItem('cart', JSON.stringify(items.value))
  }

  function addItem(item: CartItemWithInfo) {
    const existing = items.value.find(i => i.productId === item.productId)
    if (existing) {
      existing.quantity += item.quantity
    } else {
      items.value.push(item)
    }
    saveToStorage()
  }

  function addProduct(product: Product, quantity: number = 1) {
    const existing = items.value.find(i => i.productId === product.id)
    if (existing) {
      existing.quantity += quantity
    } else {
      items.value.push({
        productId: product.id,
        productName: product.name,
        price: product.price,
        quantity,
        imageUrl: product.imageUrl
      })
    }
    saveToStorage()
  }

  function removeItem(productId: number) {
    items.value = items.value.filter(i => i.productId !== productId)
    saveToStorage()
  }

  function updateQuantity(productId: number, quantity: number) {
    const item = items.value.find(i => i.productId === productId)
    if (item) {
      item.quantity = quantity
      if (quantity <= 0) {
        removeItem(productId)
      } else {
        saveToStorage()
      }
    }
  }

  function clearCart() {
    items.value = []
    saveToStorage()
  }

  function getItemsForOrder(): { productId: number; quantity: number }[] {
    return items.value.map(item => ({
      productId: item.productId,
      quantity: item.quantity
    }))
  }

  return {
    items,
    loaded,
    totalItems,
    totalPrice,
    addItem,
    addProduct,
    removeItem,
    updateQuantity,
    clearCart,
    getItemsForOrder
  }
})
