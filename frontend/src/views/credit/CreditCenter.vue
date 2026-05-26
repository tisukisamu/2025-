<template>
  <div class="credit-page">
    <div class="back-nav">
      <a-button type="text" @click="router.back()">
        <left-outlined /> 返回
      </a-button>
    </div>

    <a-spin :spinning="loading">
      <template v-if="creditInfo">
        <a-card class="credit-card">
          <div class="credit-header">
            <div class="credit-score">
              <div class="score-circle" :style="{ background: getLevelColor(creditInfo.level) }">
                <span class="score-value">{{ creditInfo.credit }}</span>
                <span class="score-label">信用分</span>
              </div>
            </div>
            <div class="credit-info">
              <div class="level-badge" :style="{ background: getLevelColor(creditInfo.level) }">
                {{ creditInfo.level }}
              </div>
              <div class="credit-desc">
                <p>本月获得 <span class="highlight">+{{ creditInfo.monthlyPoints }}</span> 积分</p>
                <p>信用等级越高，享受权益越多</p>
              </div>
              <a-button type="primary" @click="handleDailyLogin" :loading="signingIn" :disabled="hasSignedIn">
                {{ hasSignedIn ? '今日已签到' : '每日签到 +2积分' }}
              </a-button>
            </div>
          </div>
        </a-card>

        <a-card title="等级权益" class="benefits-card">
          <div class="benefits-list">
            <div class="benefit-item" v-for="(benefit, index) in levelBenefits" :key="index" :class="{ active: creditInfo.level === benefit.level }">
              <div class="benefit-level" :style="{ background: benefit.color }">
                {{ benefit.level }}
              </div>
              <div class="benefit-detail">
                <span class="benefit-range">{{ benefit.range }}</span>
                <span class="benefit-desc">{{ benefit.desc }}</span>
              </div>
            </div>
          </div>
        </a-card>

        <a-card title="积分规则" class="rules-card">
          <div class="rules-list">
            <div class="rule-item" v-for="rule in creditRules" :key="rule.type">
              <span class="rule-name">{{ rule.name }}</span>
              <span class="rule-points" :class="{ positive: rule.points > 0, negative: rule.points < 0 }">
                {{ rule.points > 0 ? '+' : '' }}{{ rule.points }}
              </span>
            </div>
          </div>
        </a-card>

        <a-card title="积分记录" class="history-card">
          <div class="history-list">
            <div class="history-item" v-for="record in records" :key="record.id">
              <div class="history-info">
                <span class="history-type">{{ getTypeText(record.type) }}</span>
                <span class="history-desc">{{ record.description }}</span>
                <span class="history-time">{{ formatTime(record.createTime) }}</span>
              </div>
              <span class="history-points" :class="{ positive: record.points > 0, negative: record.points < 0 }">
                {{ record.points > 0 ? '+' : '' }}{{ record.points }}
              </span>
            </div>
          </div>
          <a-empty v-if="records.length === 0" description="暂无记录" />
        </a-card>
      </template>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { creditApi } from '@/api/extra'
import { message } from 'ant-design-vue'
import { LeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const signingIn = ref(false)
const creditInfo = ref<any>(null)
const records = ref<any[]>([])
const hasSignedIn = ref(false)

const levelBenefits = [
  { level: '新手上路', range: '0-49分', color: '#bfbfbf', desc: '基础交易权限' },
  { level: '铜牌会员', range: '50-149分', color: '#cd7f32', desc: '优先展示商品' },
  { level: '银牌会员', range: '150-299分', color: '#c0c0c0', desc: '专属标识 + 优先客服' },
  { level: '金牌会员', range: '300-499分', color: '#ffd700', desc: '免审核发布 + 专属活动' },
  { level: '钻石会员', range: '500分以上', color: '#b9f2ff', desc: '最高权益 + VIP特权' }
]

const creditRules = [
  { type: 'REGISTER', name: '注册奖励', points: 50 },
  { type: 'COMPLETE_PROFILE', name: '完善资料', points: 20 },
  { type: 'PUBLISH_PRODUCT', name: '发布商品', points: 5 },
  { type: 'SUCCESSFUL_TRADE', name: '交易成功', points: 30 },
  { type: 'POSITIVE_REVIEW', name: '获得好评', points: 10 },
  { type: 'NEGATIVE_REVIEW', name: '收到差评', points: -10 },
  { type: 'CANCEL_ORDER', name: '取消订单', points: -5 },
  { type: 'REPORT_VERIFIED', name: '举报成功', points: 15 },
  { type: 'BE_REPORTED', name: '被举报核实', points: -20 },
  { type: 'DAILY_LOGIN', name: '每日签到', points: 2 }
]

const getLevelColor = (level: string) => {
  const map: Record<string, string> = {
    '新手上路': '#bfbfbf',
    '铜牌会员': '#cd7f32',
    '银牌会员': '#c0c0c0',
    '金牌会员': '#ffd700',
    '钻石会员': '#b9f2ff'
  }
  return map[level] || '#bfbfbf'
}

const getTypeText = (type: string) => {
  const rule = creditRules.find(r => r.type === type)
  return rule ? rule.name : type
}

const formatTime = (time: string) => {
  return new Date(time).toLocaleString('zh-CN')
}

const fetchCreditInfo = async () => {
  loading.value = true
  try {
    const res = await creditApi.getCreditInfo()
    creditInfo.value = res.data
    await fetchHistory()
  } finally {
    loading.value = false
  }
}

const fetchHistory = async () => {
  try {
    const res = await creditApi.getCreditHistory({ page: 1, size: 20 })
    records.value = res.data.list || []
    checkTodaySignIn()
  } catch {
    records.value = []
  }
}

const checkTodaySignIn = () => {
  const today = new Date().toDateString()
  const todayRecord = records.value.find(r => 
    r.type === 'DAILY_LOGIN' && new Date(r.createTime).toDateString() === today
  )
  hasSignedIn.value = !!todayRecord
}

const handleDailyLogin = async () => {
  signingIn.value = true
  try {
    await creditApi.dailyLogin()
    message.success('签到成功，获得2积分')
    hasSignedIn.value = true
    fetchCreditInfo()
  } catch {
    // ignore
  } finally {
    signingIn.value = false
  }
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  fetchCreditInfo()
})
</script>

<style scoped>
.credit-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.back-nav {
  margin-bottom: 16px;
}

.back-nav :deep(.ant-btn) {
  color: #1890ff;
}

.credit-card, .benefits-card, .rules-card, .history-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.credit-header {
  display: flex;
  gap: 40px;
  align-items: center;
}

.credit-score {
  flex-shrink: 0;
}

.score-circle {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.score-value {
  font-size: 36px;
  font-weight: 600;
}

.score-label {
  font-size: 14px;
  opacity: 0.9;
}

.credit-info {
  flex: 1;
}

.level-badge {
  display: inline-block;
  padding: 4px 16px;
  border-radius: 20px;
  color: #fff;
  font-weight: 500;
  margin-bottom: 16px;
}

.credit-desc {
  margin-bottom: 16px;
}

.credit-desc p {
  margin: 0 0 8px;
  color: #666;
}

.highlight {
  color: #1890ff;
  font-weight: 600;
  font-size: 18px;
}

.benefits-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  border-radius: 8px;
  background: #fafafa;
  transition: all 0.3s;
}

.benefit-item.active {
  background: #e6f7ff;
  border: 1px solid #1890ff;
}

.benefit-level {
  padding: 4px 12px;
  border-radius: 4px;
  color: #fff;
  font-weight: 500;
  font-size: 13px;
}

.benefit-detail {
  display: flex;
  flex-direction: column;
}

.benefit-range {
  font-weight: 500;
  color: #333;
}

.benefit-desc {
  font-size: 13px;
  color: #999;
}

.rules-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.rule-item {
  display: flex;
  justify-content: space-between;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
}

.rule-name {
  color: #666;
}

.rule-points {
  font-weight: 600;
}

.rule-points.positive {
  color: #52c41a;
}

.rule-points.negative {
  color: #f5222d;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
}

.history-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.history-type {
  font-weight: 500;
  color: #333;
}

.history-desc {
  font-size: 13px;
  color: #666;
}

.history-time {
  font-size: 12px;
  color: #999;
}

.history-points {
  font-weight: 600;
  font-size: 16px;
}

.history-points.positive {
  color: #52c41a;
}

.history-points.negative {
  color: #f5222d;
}

@media (max-width: 768px) {
  .credit-header {
    flex-direction: column;
    text-align: center;
  }

  .rules-list {
    grid-template-columns: 1fr;
  }
}
</style>
