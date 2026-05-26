<script setup lang="ts">
import { useCartStore } from '../stores/cart'
import { useUserStore } from '../stores/user'
import { useRouter } from 'vue-router'
import { computed, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  LeftOutlined,
  AlipayCircleOutlined,
  WechatOutlined,
  ShoppingCartOutlined,
  EnvironmentOutlined,
  UserOutlined,
  PhoneOutlined,
  EditOutlined
} from '@ant-design/icons-vue'
import CartItem from '../components/CartItem.vue'
import CartSummary from '../components/CartSummary.vue'
import { orderApi } from '../api'

const cartStore = useCartStore()
const userStore = useUserStore()
const router = useRouter()

// --- 状态管理 ---
const selectedItems = ref<Set<number>>(new Set())
const paymentModalVisible = ref(false)
const paymentLoading = ref(false)
const currentOrder = ref<any>(null)
const currentOrderId = ref<number | null>(null)

// 收货信息表单
const shippingInfo = ref({
  contact: '',
  phone: '',
  address: ''
})

// 表单验证状态
const formErrors = ref({
  contact: '',
  phone: '',
  address: ''
})

// --- 计算属性 ---
const isAllSelected = computed(() => {
  return cartStore.items.length > 0 &&
    cartStore.items.every(item => selectedItems.value.has(item.productId))
})

const selectedTotal = computed(() => {
  return cartStore.items
    .filter(item => selectedItems.value.has(item.productId))
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const selectedCount = computed(() => selectedItems.value.size)

// --- 事件处理 ---
const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedItems.value.clear()
  } else {
    cartStore.items.forEach(item => selectedItems.value.add(item.productId))
  }
}

const toggleSelectItem = (productId: number) => {
  if (selectedItems.value.has(productId)) {
    selectedItems.value.delete(productId)
  } else {
    selectedItems.value.add(productId)
  }
}

// --- 表单验证 ---
const validateForm = () => {
  let isValid = true
  formErrors.value = { contact: '', phone: '', address: '' }

  if (!shippingInfo.value.contact.trim()) {
    formErrors.value.contact = '请输入收货人姓名'
    isValid = false
  }

  if (!shippingInfo.value.phone.trim()) {
    formErrors.value.phone = '请输入手机号码'
    isValid = false
  } else if (!/^1[3-9]\d{9}$/.test(shippingInfo.value.phone)) {
    formErrors.value.phone = '请输入正确的手机号码'
    isValid = false
  }

  if (!shippingInfo.value.address.trim()) {
    formErrors.value.address = '请输入收货地址'
    isValid = false
  }

  return isValid
}

// --- 核心业务流程 ---
// 创建订单
const handleCheckout = async () => {
  if (selectedCount.value === 0) {
    message.warning('请先选择要结算的商品')
    return
  }

  if (!userStore.token) {
    message.warning('请先登录')
    router.push('/login')
    return
  }

  if (!validateForm()) {
    message.warning('请完善收货信息')
    return
  }

  try {
    message.loading({ content: '正在创建订单...', key: 'createOrder' })

    const orderItems = cartStore.items
      .filter(item => selectedItems.value.has(item.productId))
      .map(item => ({
        productId: item.productId,
        quantity: item.quantity
      }))

    const orderData = {
      items: orderItems,
      address: shippingInfo.value.address,
      contact: shippingInfo.value.contact,
      phone: shippingInfo.value.phone
    }

    const createdOrder = await orderApi.create(orderData)

    currentOrder.value = createdOrder
    currentOrderId.value = createdOrder.id

    message.success({ content: '订单创建成功', key: 'createOrder', duration: 1 })
    paymentModalVisible.value = true
  } catch (error: any) {
    message.error({ content: error.message || '订单创建失败，请重试', key: 'createOrder' })
  }
}

// 确认支付
const handlePaymentConfirm = async () => {
  if (!currentOrderId.value) return
  paymentLoading.value = true
  try {
    // 调用API支付订单
    await orderApi.pay(currentOrderId.value)

    message.success('支付成功！')
    paymentModalVisible.value = false

    // 从购物车移除已购买的商品
    const purchasedIds = Array.from(selectedItems.value)
    purchasedIds.forEach(id => cartStore.removeItem(id))
    selectedItems.value.clear()

    // 跳转到订单详情页
    router.push(`/orders/${currentOrderId.value}`)
  } catch (error: any) {
    message.error(error.message || '支付失败，请重试')
  } finally {
    paymentLoading.value = false
  }
}

// 取消支付 - 保留待支付订单，跳转到订单列表
const handleCancelPayment = () => {
  paymentModalVisible.value = false
  message.info('订单已保存，请在30分钟内完成支付')
  // 清空选中状态
  selectedItems.value.clear()
  // 跳转到订单列表
  router.push('/orders')
}
</script>

<template>
  <div class="cart-page">
    <!-- 顶部导航 -->
    <header class="cart-header">
      <button
        class="back-btn"
        @click="router.back()"
      >
        <LeftOutlined class="back-icon" />
      </button>
      <h1 class="header-title">购物车</h1>
      <button
        v-if="cartStore.items.length"
        class="clear-btn"
        @click="cartStore.clearCart"
      >
        清空
      </button>
    </header>

    <!-- 空购物车状态 -->
    <div v-if="cartStore.items.length === 0" class="empty-cart">
      <div class="empty-icon-wrapper">
        <ShoppingCartOutlined class="empty-icon" />
      </div>
      <h2 class="empty-title">您的购物车还是空的</h2>
      <p class="empty-desc">去看看我们为您精选的商品吧！</p>
      <button
        class="shop-btn"
        @click="router.push('/')"
      >
        去挑选商品
      </button>
    </div>

    <!-- 购物车列表 -->
    <div v-else class="cart-content">
      <div class="main-section">
        <!-- 收货信息表单 -->
        <div class="shipping-section">
          <div class="section-header">
            <div class="section-title">
              <EnvironmentOutlined class="section-icon" />
              <span>收货信息</span>
            </div>
          </div>
          <div class="shipping-form">
            <div class="form-row">
              <div class="form-item">
                <label class="form-label">
                  <UserOutlined class="label-icon" />
                  <span>收货人</span>
                  <span class="required">*</span>
                </label>
                <input
                  v-model="shippingInfo.contact"
                  type="text"
                  class="form-input"
                  :class="{ 'has-error': formErrors.contact }"
                  placeholder="请输入收货人姓名"
                />
                <span v-if="formErrors.contact" class="error-text">{{ formErrors.contact }}</span>
              </div>
              <div class="form-item">
                <label class="form-label">
                  <PhoneOutlined class="label-icon" />
                  <span>手机号码</span>
                  <span class="required">*</span>
                </label>
                <input
                  v-model="shippingInfo.phone"
                  type="tel"
                  class="form-input"
                  :class="{ 'has-error': formErrors.phone }"
                  placeholder="请输入手机号码"
                  maxlength="11"
                />
                <span v-if="formErrors.phone" class="error-text">{{ formErrors.phone }}</span>
              </div>
            </div>
            <div class="form-row single">
              <div class="form-item full-width">
                <label class="form-label">
                  <EnvironmentOutlined class="label-icon" />
                  <span>收货地址</span>
                  <span class="required">*</span>
                </label>
                <textarea
                  v-model="shippingInfo.address"
                  class="form-textarea"
                  :class="{ 'has-error': formErrors.address }"
                  placeholder="请输入详细收货地址（省/市/区/街道/门牌号）"
                  rows="2"
                ></textarea>
                <span v-if="formErrors.address" class="error-text">{{ formErrors.address }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 全选栏 -->
        <div class="select-all-bar">
          <label class="select-all-label">
            <input
              type="checkbox"
              :checked="isAllSelected"
              @change="toggleSelectAll"
              class="custom-checkbox"
            />
            <span class="select-all-text">全选</span>
          </label>
          <span class="selected-info">
            已选 <strong class="selected-count">{{ selectedCount }}</strong> / {{ cartStore.items.length }} 件
          </span>
        </div>

        <!-- 商品列表 -->
        <div class="cart-items-container">
          <div class="cart-items-list">
            <CartItem
              v-for="item in cartStore.items"
              :key="item.productId"
              :item="item"
              :selected="selectedItems.has(item.productId)"
              @toggle-select="toggleSelectItem"
              @update-quantity="cartStore.updateQuantity"
              @remove="cartStore.removeItem"
            />
          </div>
        </div>
      </div>

      <!-- 订单摘要 -->
      <div class="summary-section">
        <CartSummary
          :selected-total="selectedTotal"
          :selected-count="selectedCount"
          :disabled="selectedCount === 0"
          @checkout="handleCheckout"
        />
      </div>
    </div>

    <!-- 支付弹窗 -->
    <a-modal
      v-model:open="paymentModalVisible"
      :footer="null"
      :width="440"
      centered
      class="payment-modal"
      :mask-style="{ backgroundColor: 'rgba(15, 23, 42, 0.5)' }"
    >
      <div class="payment-content">
        <h3 class="payment-title">收银台</h3>

        <div class="payment-amount">
          <p class="amount-label">订单总金额</p>
          <div class="amount-value">
            <span class="currency">¥</span>{{ selectedTotal.toFixed(2) }}
          </div>
        </div>

        <div class="payment-methods">
          <div class="payment-method selected">
            <div class="method-icon">
              <AlipayCircleOutlined />
            </div>
            <span class="method-name">支付宝支付</span>
            <div class="method-check">
              <span class="check-dot"></span>
            </div>
          </div>
          <div class="payment-method">
            <div class="method-icon">
              <WechatOutlined />
            </div>
            <span class="method-name">微信支付</span>
            <div class="method-check">
              <span class="check-dot"></span>
            </div>
          </div>
        </div>

        <a-button
          type="primary"
          size="large"
          block
          class="pay-btn"
          :loading="paymentLoading"
          @click="handlePaymentConfirm"
        >
          立即支付 ¥{{ selectedTotal.toFixed(2) }}
        </a-button>

        <button
          class="cancel-btn"
          @click="handleCancelPayment"
        >
          取消支付
        </button>
      </div>
    </a-modal>
  </div>
</template>

<style scoped>
.cart-page {
  min-height: 100vh;
  background: #f8fafc;
  padding-bottom: 24px;
}

/* 顶部导航 */
.cart-header {
  position: sticky;
  top: 0;
  z-index: 50;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid #e2e8f0;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.back-btn {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  border: none;
  background: #f1f5f9;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.back-icon {
  font-size: 18px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.clear-btn {
  font-size: 14px;
  font-weight: 500;
  color: #ef4444;
  border: none;
  background: none;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.clear-btn:hover {
  background: #fef2f2;
}

/* 空购物车 */
.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
}

.empty-icon-wrapper {
  width: 160px;
  height: 160px;
  background: #f1f5f9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

.empty-icon {
  font-size: 72px;
  color: #cbd5e1;
}

.empty-title {
  font-size: 22px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 10px 0;
}

.empty-desc {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 28px 0;
}

.shop-btn {
  padding: 14px 32px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, #0d9488 0%, #0f766e 100%);
  color: white;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 14px rgba(13, 148, 136, 0.25);
}

.shop-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(13, 148, 136, 0.35);
}

/* 购物车内容 */
.cart-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 20px 20px;
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
}

.main-section {
  background: #ffffff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  margin-top: 20px;
}

.shipping-section {
  padding: 20px;
  border-bottom: 1px solid #e2e8f0;
  background: #fafbfc;
}

.section-header {
  margin-bottom: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.section-icon {
  font-size: 18px;
  color: #0d9488;
}

.shipping-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-row.single {
  grid-template-columns: 1fr;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-item.full-width {
  grid-column: 1 / -1;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  color: #475569;
}

.label-icon {
  font-size: 14px;
  color: #0d9488;
}

.required {
  color: #ef4444;
  font-size: 12px;
}

.form-input {
  height: 44px;
  padding: 0 14px;
  border: 1.5px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  color: #1e293b;
  background: #fff;
  transition: all 0.2s ease;
  outline: none;
}

.form-input:focus {
  border-color: #0d9488;
  box-shadow: 0 0 0 3px rgba(13, 148, 136, 0.1);
}

.form-input.has-error {
  border-color: #ef4444;
}

.form-input.has-error:focus {
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.form-input::placeholder {
  color: #94a3b8;
}

.form-textarea {
  padding: 12px 14px;
  border: 1.5px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  color: #1e293b;
  background: #fff;
  transition: all 0.2s ease;
  outline: none;
  resize: none;
  font-family: inherit;
  line-height: 1.5;
}

.form-textarea:focus {
  border-color: #0d9488;
  box-shadow: 0 0 0 3px rgba(13, 148, 136, 0.1);
}

.form-textarea.has-error {
  border-color: #ef4444;
}

.form-textarea::placeholder {
  color: #94a3b8;
}

.error-text {
  font-size: 12px;
  color: #ef4444;
  display: flex;
  align-items: center;
  gap: 4px;
}

.select-all-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  position: static;
  z-index: 10;
}

.select-all-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.custom-checkbox {
  width: 18px;
  height: 18px;
  cursor: pointer;
  appearance: none;
  border: 2px solid #cbd5e1;
  border-radius: 4px;
  transition: all 0.2s ease;
  position: relative;
}

.custom-checkbox:checked {
  background: #0d9488;
  border-color: #0d9488;
}

.custom-checkbox:checked::after {
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

.select-all-text {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
}

.selected-info {
  font-size: 14px;
  color: #64748b;
}

.selected-count {
  color: #0d9488;
  font-weight: 600;
}

.cart-items-list {
  display: flex;
  flex-direction: column;
}

.summary-section {
  position: sticky;
  top: 100px;
  height: fit-content;
}

/* 支付弹窗 */
.payment-content {
  padding: 24px 0 8px;
}

.payment-title {
  text-align: center;
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 24px 0;
}

.payment-amount {
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
}

.amount-label {
  font-size: 13px;
  color: #64748b;
  margin: 0 0 8px 0;
  text-align: center;
}

.amount-value {
  font-size: 36px;
  font-weight: 700;
  color: #1e293b;
  text-align: center;
}

.currency {
  font-size: 20px;
  font-weight: 600;
  margin-right: 2px;
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 24px;
}

.payment-method {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.payment-method:hover {
  border-color: #0d9488;
  background: #f0fdfa;
}

.payment-method.selected {
  border-color: #0d9488;
  background: #f0fdfa;
}

.method-icon {
  font-size: 28px;
  color: #0d9488;
}

.payment-method:not(.selected) .method-icon {
  color: #94a3b8;
}

.method-name {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
}

.method-check {
  width: 20px;
  height: 20px;
  border: 2px solid #cbd5e1;
  border-radius: 50%;
  position: relative;
}

.payment-method.selected .method-check {
  border-color: #0d9488;
  background: #0d9488;
}

.payment-method.selected .check-dot {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 8px;
  height: 8px;
  background: white;
  border-radius: 50%;
}

.pay-btn {
  height: 48px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #0d9488 0%, #0f766e 100%);
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 14px rgba(13, 148, 136, 0.25);
}

.pay-btn:hover:not(:disabled) {
  box-shadow: 0 6px 20px rgba(13, 148, 136, 0.35);
}

.cancel-btn {
  display: block;
  width: 100%;
  margin-top: 16px;
  padding: 12px;
  font-size: 14px;
  color: #94a3b8;
  border: none;
  background: none;
  cursor: pointer;
  transition: color 0.2s ease;
}

.cancel-btn:hover {
  color: #64748b;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .cart-content {
    grid-template-columns: 1fr;
  }

  .summary-section {
    position: static;
  }

  .select-all-bar {
    position: static;
  }
}

@media (max-width: 640px) {
  .cart-header {
    padding: 12px 16px;
  }

  .header-title {
    font-size: 16px;
  }

  .cart-content {
    padding: 16px;
    gap: 20px;
  }

  .shipping-section {
    padding: 16px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .form-input {
    height: 42px;
  }

  .empty-icon-wrapper {
    width: 120px;
    height: 120px;
  }

  .empty-icon {
    font-size: 56px;
  }

  .empty-title {
    font-size: 18px;
  }

  .shop-btn {
    padding: 12px 28px;
    font-size: 14px;
  }
}

/* Modal 样式覆盖 */
:global(.payment-modal .ant-modal-content) {
  border-radius: 20px;
  padding: 32px 24px 24px;
  box-shadow: 0 25px 50px -12px rgba(15, 23, 42, 0.3);
}

:global(.payment-modal .ant-modal-header) {
  display: none;
}

:global(.payment-modal .ant-modal-close) {
  top: 16px;
  right: 16px;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #f1f5f9;
  border: none;
  transition: all 0.2s ease;
}

:global(.payment-modal .ant-modal-close:hover) {
  background: #e2e8f0;
}

:global(.payment-modal .ant-modal-close-x) {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
