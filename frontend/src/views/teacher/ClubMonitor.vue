<template>
  <div class="club-monitor">
    <div class="page-header">
      <h2>社团监管</h2>
      <p class="subtitle">监管指导社团的财务状况与活动情况</p>
    </div>

    <div class="stats-row">
      <a-row :gutter="16">
        <a-col :span="6">
          <div class="stat-card">
            <div class="stat-number">{{ clubs.length }}</div>
            <div class="stat-label">指导社团</div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card">
            <div class="stat-number pending">{{ pendingApprovals }}</div>
            <div class="stat-label">待审批申请</div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card">
            <div class="stat-number">{{ totalMembers }}</div>
            <div class="stat-label">总成员数</div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card">
            <div class="stat-number warning">{{ warningCount }}</div>
            <div class="stat-label">风险预警</div>
          </div>
        </a-col>
      </a-row>
    </div>

    <a-spin :spinning="loading">
      <div class="club-list">
        <div v-for="club in clubs" :key="club.id" class="club-item">
          <div class="club-header">
            <div class="club-avatar">
              <img v-if="club.logo" :src="club.logo" alt="" />
              <div v-else class="avatar-placeholder">
                {{ club.clubName?.charAt(0) || '社' }}
              </div>
            </div>
            <div class="club-info">
              <h3>{{ club.clubName }}</h3>
              <p class="club-meta">
                <span><TeamOutlined /> {{ club.memberCount || 0 }} 成员</span>
                <span><CalendarOutlined /> 创建于 {{ formatDate(club.createTime) }}</span>
              </p>
            </div>
            <div class="club-balance">
              <div class="balance-label">账户余额</div>
              <div class="balance-value">{{ formatMoney(club.balance || 0) }}</div>
            </div>
          </div>

          <a-divider style="margin: 16px 0" />

          <div class="club-stats">
            <div class="stat-item">
              <span class="label">待审批</span>
              <span class="value pending">{{ club.pendingApprovalCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="label">本月支出</span>
              <span class="value expense">{{ formatMoney(club.monthExpense || 0) }}</span>
            </div>
            <div class="stat-item">
              <span class="label">本月收入</span>
              <span class="value income">{{ formatMoney(club.monthIncome || 0) }}</span>
            </div>
            <div class="stat-item">
              <span class="label">状态</span>
              <a-tag :color="club.status === 1 ? 'green' : 'default'">
                {{ club.status === 1 ? '正常' : '停用' }}
              </a-tag>
            </div>
          </div>

          <div class="club-actions">
            <a-button type="link" @click="goToApproval(club.id)">
              <AuditOutlined /> 审批申请
            </a-button>
            <a-button type="link" @click="viewDetail(club)">
              <EyeOutlined /> 查看详情
            </a-button>
          </div>
        </div>

        <div v-if="!loading && clubs.length === 0" class="empty-state">
          <a-empty description="暂无指导的社团" />
        </div>
      </div>
    </a-spin>

    <a-modal
      v-model:open="detailVisible"
      title="社团详情"
      :footer="null"
      width="760px"
    >
      <template v-if="currentClub">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="社团名称">{{ currentClub.clubName }}</a-descriptions-item>
          <a-descriptions-item label="社团编码">{{ currentClub.clubCode || '-' }}</a-descriptions-item>
          <a-descriptions-item label="指导老师">{{ currentClub.teacher?.realName || '-' }}</a-descriptions-item>
          <a-descriptions-item label="社长">{{ currentClub.president?.realName || '-' }}</a-descriptions-item>
          <a-descriptions-item label="成员数">{{ currentClub.memberCount || 0 }} 人</a-descriptions-item>
          <a-descriptions-item label="账户余额">{{ formatMoney(currentClub.balance || 0) }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ formatDate(currentClub.createTime) }}</a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="currentClub.status === 1 ? 'green' : 'default'">
              {{ currentClub.status === 1 ? '正常' : '停用' }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="社团简介" :span="2">
            {{ currentClub.description || '暂无简介' }}
          </a-descriptions-item>
        </a-descriptions>
        <div class="detail-actions">
          <a-space>
            <a-button type="primary" @click="goToApproval(currentClub.id)">查看待审申请</a-button>
            <a-button @click="detailVisible = false">关闭</a-button>
          </a-space>
        </div>
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { TeamOutlined, CalendarOutlined, AuditOutlined, EyeOutlined } from '@ant-design/icons-vue'
import { clubApi } from '@/api/club'
import type { Club } from '@/types'
import { normalizeMediaUrl } from '@/utils/media'

const router = useRouter()
const loading = ref(false)
const clubs = ref<Club[]>([])
const pendingApprovals = ref(0)
const totalMembers = ref(0)
const warningCount = ref(0)
const detailVisible = ref(false)
const currentClub = ref<Club | null>(null)

const resolveLogoUrl = (url?: string) => {
  return normalizeMediaUrl(url)
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const formatMoney = (amount: number) => {
  return '¥' + (amount || 0).toFixed(2)
}

const loadClubs = async () => {
  loading.value = true
  try {
    const res = await clubApi.getList(0, 100)
    clubs.value = (res.data?.content || []).map(item => ({
      ...item,
      logo: resolveLogoUrl(item.logo),
      balance: Number(item.balance) || 0,
      memberCount: Number(item.memberCount) || 0,
      pendingApprovalCount: Number(item.pendingApprovalCount) || 0,
      monthIncome: Number(item.monthIncome) || 0,
      monthExpense: Number(item.monthExpense) || 0
    }))
    
    totalMembers.value = clubs.value.reduce((sum, c) => sum + (c.memberCount || 0), 0)
    pendingApprovals.value = clubs.value.reduce((sum, c) => sum + (c.pendingApprovalCount || 0), 0)
    warningCount.value = clubs.value.filter(c => c.balance !== undefined && c.balance < 1000).length
  } catch (error) {
    console.error('加载社团列表失败:', error)
  } finally {
    loading.value = false
  }
}

const goToApproval = (clubId: number) => {
  router.push({ name: 'TeacherApproval', query: { clubId } })
}

const viewDetail = (club: Club) => {
  currentClub.value = club
  detailVisible.value = true
}

onMounted(() => {
  loadClubs()
})
</script>

<style scoped>
.club-monitor {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
}

.subtitle {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: 600;
  color: #1a1a1a;
}

.stat-number.pending {
  color: #fa8c16;
}

.stat-number.warning {
  color: #ff4d4f;
}

.stat-label {
  color: #666;
  font-size: 14px;
  margin-top: 8px;
}

.club-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.club-item {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.club-header {
  display: flex;
  align-items: center;
  gap: 16px;
}

.club-avatar {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.club-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
  color: #666;
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
}

.club-info {
  flex: 1;
}

.club-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.club-meta {
  margin: 0;
  display: flex;
  gap: 16px;
  color: #666;
  font-size: 14px;
}

.club-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.club-balance {
  text-align: right;
}

.balance-label {
  color: #666;
  font-size: 12px;
  margin-bottom: 4px;
}

.balance-value {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
}

.club-stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-item .label {
  color: #666;
  font-size: 12px;
}

.stat-item .value {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.stat-item .value.pending {
  color: #fa8c16;
}

.stat-item .value.income {
  color: #52c41a;
}

.stat-item .value.expense {
  color: #ff4d4f;
}

.club-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}

.empty-state {
  background: #fff;
  border-radius: 8px;
  padding: 60px 0;
}

.detail-actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
