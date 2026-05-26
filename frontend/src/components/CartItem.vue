<script setup lang="ts">
import { computed } from 'vue'
import { MinusOutlined, PlusOutlined, DeleteOutlined, ShoppingOutlined } from '@ant-design/icons-vue'

interface Props {
  item: {
    productId: number
    productName: string
    price: number
    quantity: number
    imageUrl?: string
    stock?: number
  }
  selected: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'toggle-select': [id: number]
  'update-quantity': [id: number, quantity: number]
  'remove': [id: number]
}>()

const formatPrice = (price: number) => `¥${price?.toFixed(2) || '0.00'}`

const canDecrease = computed(() => props.item.quantity > 1)
const canIncrease = computed(() => {
  const maxStock = props.item.stock ?? 999
  return props.item.quantity < maxStock
})
const displayStock = computed(() => props.item.stock ?? 999)
</script>

<template>
  <div class="cart-item" :class="{ 'item-selected': selected }">
    <!-- 选择框 -->
    <label class="item-checkbox">
      <input
        type="checkbox"
        :checked="selected"
        @change="$emit('toggle-select', item.productId)"
        class="checkbox"
      />
      <span class="checkmark"></span>
    </label>

    <!-- 商品图片 -->
    <div class="product-image">
      <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.productName" />
      <div v-else class="image-placeholder">
        <ShoppingOutlined />
      </div>
    </div>

    <!-- 商品信息 -->
    <div class="product-info">
      <h3 class="product-name">{{ item.productName }}</h3>
      <span class="stock-text">库存: {{ displayStock }}件</span>
    </div>

    <!-- 价格 -->
    <div class="product-price">
      <span class="price-value">{{ formatPrice(item.price) }}</span>
    </div>

    <!-- 数量调整 -->
    <div class="quantity-control">
      <button
        class="qty-btn qty-btn-decrease"
        :class="{ 'qty-btn-disabled': !canDecrease }"
        :disabled="!canDecrease"
        @click="$emit('update-quantity', item.productId, item.quantity - 1)"
      >
        <MinusOutlined />
      </button>
      <input
        type="number"
        class="qty-input"
        :value="item.quantity"
        readonly
      />
      <button
        class="qty-btn qty-btn-increase"
        :class="{ 'qty-btn-disabled': !canIncrease }"
        :disabled="!canIncrease"
        @click="$emit('update-quantity', item.productId, item.quantity + 1)"
      >
        <PlusOutlined />
      </button>
    </div>

    <!-- 小计 -->
    <div class="product-subtotal">
      <span class="subtotal-value">{{ formatPrice(item.price * item.quantity) }}</span>
    </div>

    <!-- 删除按钮 -->
    <button
      class="delete-btn"
      @click="$emit('remove', item.productId)"
      title="删除"
    >
      <DeleteOutlined />
    </button>
  </div>
</template>

<style scoped>
.cart-item {
  display: grid;
  grid-template-columns: 24px 88px 1fr 100px 140px 100px 48px;
  gap: 20px;
  padding: 20px 24px;
  align-items: center;
  background: #ffffff;
  transition: all 0.25s ease;
}

.cart-item:hover {
  background: #f8fafc;
}

.cart-item.item-selected {
  background: #f0fdfa;
}

.cart-item.item-selected:hover {
  background: #f0fdfa;
}

.item-checkbox {
  display: flex;
  align-items: center;
  cursor: pointer;
  position: relative;
}

.checkbox {
  width: 18px;
  height: 18px;
  cursor: pointer;
  appearance: none;
  border: 2px solid #cbd5e1;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.checkbox:checked {
  background: #0d9488;
  border-color: #0d9488;
}

.checkbox:checked::after {
  content: '';
  position: absolute;
  left: 4px;
  top: 1px;
  width: 4px;
  height: 8px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 10px;
  overflow: hidden;
  background: #f1f5f9;
  flex-shrink: 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.cart-item:hover .product-image img {
  transform: scale(1.05);
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  font-size: 28px;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding-right: 12px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
  margin: 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.stock-text {
  font-size: 12px;
  color: #64748b;
}

.product-price {
  display: flex;
  align-items: center;
}

.price-value {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.quantity-control {
  display: flex;
  align-items: center;
  background: #f8fafc;
  border-radius: 8px;
  padding: 2px;
  width: fit-content;
}

.qty-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: white;
  color: #64748b;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  border-radius: 6px;
  font-size: 14px;
}

.qty-btn:hover:not(.qty-btn-disabled) {
  background: #0d9488;
  color: white;
  transform: scale(1.05);
}

.qty-btn-disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.qty-input {
  width: 44px;
  height: 32px;
  text-align: center;
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
  border: none;
  background: transparent;
  outline: none;
}

.qty-input::-webkit-inner-spin-button,
.qty-input::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.product-subtotal {
  display: flex;
  align-items: center;
}

.subtotal-value {
  font-size: 16px;
  font-weight: 600;
  color: #0d9488;
}

.delete-btn {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  border: none;
  background: #fee2e2;
  color: #ef4444;
  font-size: 15px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.delete-btn:hover {
  background: #ef4444;
  color: white;
  transform: scale(1.1);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .cart-item {
    grid-template-columns: 24px 72px 1fr 120px 44px;
    grid-template-rows: auto auto;
    gap: 12px;
    padding: 16px;
  }

  .product-price {
    grid-column: 4;
    grid-row: 1;
  }

  .product-subtotal {
    display: none;
  }

  .delete-btn {
    grid-column: 5;
    grid-row: 1 / span 2;
    height: auto;
  }

  .quantity-control {
    grid-column: 4;
    grid-row: 2;
  }
}

@media (max-width: 640px) {
  .cart-item {
    grid-template-columns: 24px 64px 1fr 36px;
    grid-template-rows: auto auto auto;
    gap: 10px;
    padding: 14px;
  }

  .product-name {
    font-size: 13px;
  }

  .product-price {
    grid-column: 2 / span 2;
    grid-row: 2;
  }

  .quantity-control {
    grid-column: 2 / span 3;
    grid-row: 3;
  }

  .delete-btn {
    grid-column: 4;
    grid-row: 1 / span 2;
  }
}
</style>
