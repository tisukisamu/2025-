<template>
  <a-modal
    v-model:open="visible"
    title="支付账单"
    :confirm-loading="loading"
    @ok="handlePayment"
    @cancel="handleCancel"
    width="500px"
  >
    <div class="payment-modal-content">
      <a-descriptions :column="1" bordered>
        <a-descriptions-item label="账单类型">
          <a-tag color="blue">{{ getBillTypeText(bill?.billType) }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="账单金额">
          <span class="amount-text">¥{{ bill?.amount }}</span>
        </a-descriptions-item>
        <a-descriptions-item label="账单描述">
          {{ bill?.description || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="到期时间">
          {{ formatDate(bill?.dueDate) }}
        </a-descriptions-item>
      </a-descriptions>

      <a-divider />

      <div class="payment-method-section">
        <h3 class="section-title">选择支付方式</h3>
        <a-radio-group v-model:value="paymentMethod" class="payment-method-group">
          <a-radio-button value="ALIPAY" class="payment-method-item">
            <div class="method-content">
              <AlipayOutlined class="method-icon alipay-icon" />
              <span>支付宝</span>
            </div>
          </a-radio-button>
          <a-radio-button value="WECHAT" class="payment-method-item">
            <div class="method-content">
              <WechatOutlined class="method-icon wechat-icon" />
              <span>微信支付</span>
            </div>
          </a-radio-button>
          <a-radio-button value="CASH" class="payment-method-item">
            <div class="method-content">
              <MoneyCollectOutlined class="method-icon cash-icon" />
              <span>现金支付</span>
            </div>
          </a-radio-button>
          <a-radio-button value="CARD" class="payment-method-item">
            <div class="method-content">
              <CreditCardOutlined class="method-icon card-icon" />
              <span>银行卡</span>
            </div>
          </a-radio-button>
        </a-radio-group>
      </div>

      <a-divider />

      <div class="payment-notice">
        <a-alert
          message="支付提示"
          description="此为模拟支付，点击确认后将直接完成支付。实际项目中需要对接真实支付接口。"
          type="info"
          show-icon
        />
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import {
  AlipayOutlined,
  WechatOutlined,
  MoneyCollectOutlined,
  CreditCardOutlined
} from '@ant-design/icons-vue'
import { createPayment, processPayment } from '@/api/finance'
import type { Bill } from '@/types'

interface Props {
  open: boolean
  bill: Bill | null
}

interface Emits {
  (e: 'update:open', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const visible = ref(false)
const loading = ref(false)
const paymentMethod = ref<string>('ALIPAY')

watch(
  () => props.open,
  (newVal) => {
    visible.value = newVal
  }
)

watch(visible, (newVal) => {
  emit('update:open', newVal)
})

const getBillTypeText = (type?: string) => {
  const map: Record<string, string> = {
    TUITION: '学费',
    REGISTRATION: '报名费',
    OTHER: '其他'
  }
  return type ? map[type] || type : '-'
}

const formatDate = (value?: string) => {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 10)
}

const handlePayment = async () => {
  if (!props.bill) {
    message.error('账单信息不存在')
    return
  }

  loading.value = true

  try {
    const paymentRes = await createPayment({
      billId: props.bill.id,
      amount: props.bill.amount,
      paymentMethod: paymentMethod.value
    })

    if (paymentRes.code === 200 && paymentRes.data) {
      const processRes = await processPayment(paymentRes.data.id)
      
      if (processRes.code === 200) {
        message.success('支付成功！')
        visible.value = false
        emit('success')
      } else {
        message.error(processRes.message || '支付失败')
      }
    } else {
      message.error(paymentRes.message || '创建支付记录失败')
    }
  } catch (error: any) {
    message.error(error.message || '支付失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  visible.value = false
}
</script>

<style scoped>
.payment-modal-content {
  padding: 10px 0;
}

.amount-text {
  font-size: 24px;
  font-weight: bold;
  color: #f5222d;
}

.payment-method-section {
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #262626;
}

.payment-method-group {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.payment-method-item {
  height: auto;
  padding: 16px;
  border-radius: 8px;
}

.method-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.method-icon {
  font-size: 32px;
}

.alipay-icon {
  color: #1890ff;
}

.wechat-icon {
  color: #52c41a;
}

.cash-icon {
  color: #faad14;
}

.card-icon {
  color: #722ed1;
}

.payment-notice {
  margin-top: 16px;
}
</style>
