<script setup lang="ts">
import { ref } from 'vue'
import { CarOutlined, SafetyCertificateOutlined, SyncOutlined, CheckCircleOutlined } from '@ant-design/icons-vue'

interface Props {
  selectedTotal: number
  selectedCount: number
  disabled: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'checkout': []
}>()

const formatPrice = (price: number) => `¥${price?.toFixed(2) || '0.00'}`
const promoCode = ref('')

const applyPromo = () => {
  if (promoCode.value.trim()) {
    // 优惠码应用逻辑
  }
}
</script>

<template>
  <div class="cart-summary">
    <!-- 标题 -->
    <div class="summary-header">
      <h3 class="summary-title">订单摘要</h3>
      <span class="item-count">{{ selectedCount }}件商品</span>
    </div>

    <!-- 优惠码输入 -->
    <div class="promo-section">
      <div class="promo-input-wrapper">
        <input
          v-model="promoCode"
          type="text"
          placeholder="输入优惠码"
          class="promo-input"
        />
        <button class="promo-btn" @click="applyPromo">应用</button>
      </div>
      <p class="promo-hint">享受会员专属优惠</p>
    </div>

    <!-- 价格明细 -->
    <div class="price-details">
      <div class="price-row">
        <span class="label">商品小计</span>
        <span class="value">{{ formatPrice(selectedTotal) }}</span>
      </div>
      <div class="price-row">
        <span class="label">配送费</span>
        <span class="value free-shipping">免运费</span>
      </div>
      <div class="price-row discount">
        <span class="label">优惠减免</span>
        <span class="value">-¥0.00</span>
      </div>
    </div>

    <!-- 合计 -->
    <div class="total-section">
      <div class="total-row">
        <span class="total-label">合计</span>
        <span class="total-value">{{ formatPrice(selectedTotal) }}</span>
      </div>
      <p class="tax-hint">含税价</p>
    </div>

    <!-- 结算按钮 -->
    <button
      class="checkout-btn"
      :class="{ 'checkout-btn-disabled': disabled }"
      :disabled="disabled"
      @click="$emit('checkout')"
    >
      <span class="btn-text">去结算</span>
      <span class="btn-price">{{ formatPrice(selectedTotal) }}</span>
    </button>

    <!-- 服务承诺 -->
    <div class="service-section">
      <div class="service-item">
        <CarOutlined class="service-icon" />
        <div class="service-text">
          <span class="service-title">极速配送</span>
          <span class="service-desc">24小时内发货</span>
        </div>
      </div>
      <div class="service-item">
        <SafetyCertificateOutlined class="service-icon" />
        <div class="service-text">
          <span class="service-title">品质保障</span>
          <span class="service-desc">100%正品保证</span>
        </div>
      </div>
      <div class="service-item">
        <SyncOutlined class="service-icon" />
        <div class="service-text">
          <span class="service-title">7天退换</span>
          <span class="service-desc">无忧购物体验</span>
        </div>
      </div>
      <div class="service-item">
        <CheckCircleOutlined class="service-icon" />
        <div class="service-text">
          <span class="service-title">售后支持</span>
          <span class="service-desc">专业客服服务</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cart-summary {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  position: sticky;
  top: 100px;
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.summary-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.item-count {
  font-size: 13px;
  color: #64748b;
  background: #f1f5f9;
  padding: 4px 10px;
  border-radius: 12px;
}

.promo-section {
  margin-bottom: 20px;
}

.promo-input-wrapper {
  display: flex;
  gap: 10px;
  margin-bottom: 6px;
}

.promo-input {
  flex: 1;
  padding: 11px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  background: #f8fafc;
  transition: all 0.2s ease;
  color: #1e293b;
}

.promo-input::placeholder {
  color: #94a3b8;
}

.promo-input:focus {
  outline: none;
  border-color: #0d9488;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(13, 148, 136, 0.1);
}

.promo-btn {
  padding: 11px 18px;
  border-radius: 10px;
  border: none;
  background: #0d9488;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.promo-btn:hover {
  background: #0f766e;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(13, 148, 136, 0.2);
}

.promo-hint {
  font-size: 12px;
  color: #94a3b8;
  margin: 0;
}

.price-details {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.price-row .label {
  color: #64748b;
}

.price-row .value {
  color: #1e293b;
  font-weight: 500;
}

.price-row.discount {
  color: #0d9488;
}

.price-row.discount .value {
  color: #0d9488;
  font-weight: 600;
}

.free-shipping {
  color: #0d9488;
  font-weight: 500;
}

.total-section {
  margin-bottom: 20px;
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 6px;
}

.total-label {
  font-size: 14px;
  color: #64748b;
}

.total-value {
  font-size: 28px;
  font-weight: 700;
  color: #0d9488;
}

.tax-hint {
  font-size: 12px;
  color: #94a3b8;
  margin: 0;
}

.checkout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
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

.checkout-btn:hover:not(.checkout-btn-disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(13, 148, 136, 0.35);
}

.checkout-btn:active:not(.checkout-btn-disabled) {
  transform: translateY(0);
}

.checkout-btn-disabled {
  background: #e2e8f0;
  cursor: not-allowed;
  box-shadow: none;
}

.checkout-btn-disabled .btn-text,
.checkout-btn-disabled .btn-price {
  color: #94a3b8;
}

.service-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f1f5f9;
}

.service-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.service-icon {
  font-size: 18px;
  color: #0d9488;
  flex-shrink: 0;
  margin-top: 1px;
}

.service-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.service-title {
  font-size: 13px;
  font-weight: 500;
  color: #1e293b;
}

.service-desc {
  font-size: 11px;
  color: #94a3b8;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .cart-summary {
    position: static;
  }
}
</style>
