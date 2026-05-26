<template>
  <div class="club-manage">
    <div class="page-header">
      <h2>社团管理</h2>
      <p class="subtitle">管理社团基本信息与设置</p>
    </div>

    <a-spin :spinning="loading">
      <div class="club-selector" v-if="allClubs.length > 1">
        <span class="selector-label">选择社团：</span>
        <a-select
          v-model:value="selectedClubId"
          style="width: 300px"
          @change="handleClubChange"
        >
          <a-select-option v-for="c in allClubs" :key="c.id" :value="c.id">
            {{ c.clubName }}
          </a-select-option>
        </a-select>
      </div>
      
      <div class="club-card" v-if="club">
        <div class="club-header">
          <div class="club-avatar">
            <img v-if="club.logo" :src="resolveLogoUrl(club.logo)" alt="社团Logo" />
            <div v-else class="avatar-placeholder">
              {{ club.clubName?.charAt(0) || '社' }}
            </div>
          </div>
          <div class="club-info">
            <h3>{{ club.clubName }}</h3>
            <p class="club-meta">
              <span class="status-tag" :class="getStatusClass(club.status)">
                {{ statusMap[club.status] || club.status }}
              </span>
              <span class="member-count">
                <UserOutlined /> {{ memberCount }} 成员
              </span>
            </p>
          </div>
          <a-button type="primary" @click="showEditModal">
            <EditOutlined /> 编辑信息
          </a-button>
        </div>

        <a-divider />

        <div class="club-details">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="社团ID">{{ club.id }}</a-descriptions-item>
            <a-descriptions-item label="社团编码">{{ club.clubCode || '-' }}</a-descriptions-item>
            <a-descriptions-item label="创建时间">{{ formatDate(club.createTime) }}</a-descriptions-item>
            <a-descriptions-item label="指导老师">
              {{ club.teacher?.realName || '未指定' }}
            </a-descriptions-item>
            <a-descriptions-item label="社团账号">
              {{ club.account || '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="社团简介" :span="2">
              {{ club.description || '暂无简介' }}
            </a-descriptions-item>
          </a-descriptions>
        </div>

        <a-divider />

        <div class="finance-summary">
          <h4><AccountBookOutlined /> 财务概览</h4>
          <a-row :gutter="24">
            <a-col :span="8">
              <div class="stat-card">
                <div class="stat-value">{{ formatMoney(club.balance || 0) }}</div>
                <div class="stat-label">当前余额</div>
              </div>
            </a-col>
            <a-col :span="8">
              <div class="stat-card">
                <div class="stat-value income">+{{ formatMoney(totalIncome) }}</div>
                <div class="stat-label">本学期收入</div>
              </div>
            </a-col>
            <a-col :span="8">
              <div class="stat-card">
                <div class="stat-value expense">-{{ formatMoney(totalExpense) }}</div>
                <div class="stat-label">本学期支出</div>
              </div>
            </a-col>
          </a-row>
        </div>
      </div>

      <div class="empty-state" v-else-if="!loading">
        <a-empty description="暂无社团信息">
          <a-button type="primary" @click="showCreateModal">
            <PlusOutlined /> 创建社团
          </a-button>
        </a-empty>
      </div>
    </a-spin>

    <a-modal
      v-model:open="editVisible"
      :title="isCreate ? '创建社团' : '编辑社团信息'"
      @ok="handleSubmit"
      @cancel="editVisible = false"
      ok-text="确定"
      cancel-text="取消"
      :confirmLoading="submitting"
      width="600px"
    >
      <a-form
        ref="formRef"
        :model="formState"
        :rules="formRules"
        layout="vertical"
      >
        <a-form-item label="社团名称" name="clubName">
          <a-input v-model:value="formState.clubName" placeholder="请输入社团名称" />
        </a-form-item>
        <a-form-item label="社团编码" name="clubCode">
          <a-input
            v-model:value="formState.clubCode"
            :disabled="!isCreate"
            placeholder="请输入社团编码（如 ACM001）"
          />
        </a-form-item>
        <a-form-item label="社团简介" name="description">
          <a-textarea
            v-model:value="formState.description"
            placeholder="请输入社团简介"
            :rows="4"
          />
        </a-form-item>
        <a-form-item label="社团Logo" name="logo">
          <div class="logo-uploader">
            <img v-if="formState.logo" :src="resolveLogoUrl(formState.logo)" class="logo-preview" alt="logo" />
            <a-upload
              :show-upload-list="false"
              :before-upload="beforeLogoUpload"
              :custom-request="handleLogoUpload"
            >
              <a-button :loading="logoUploading">上传Logo</a-button>
            </a-upload>
            <a-button v-if="formState.logo" type="link" danger @click="formState.logo = ''">移除图片</a-button>
          </div>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule } from 'ant-design-vue/es/form'
import { UserOutlined, EditOutlined, PlusOutlined, AccountBookOutlined } from '@ant-design/icons-vue'
import { clubApi } from '@/api/club'
import { fundApi } from '@/api/fund'
import { uploadApi } from '@/api/upload'
import { useUserStore } from '@/stores/user'
import type { Club, FundFlow } from '@/types'
import { normalizeMediaUrl } from '@/utils/media'

const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const editVisible = ref(false)
const isCreate = ref(false)
const logoUploading = ref(false)
const club = ref<Club | null>(null)
const allClubs = ref<Club[]>([])
const selectedClubId = ref<number | undefined>(undefined)
const memberCount = ref(0)
const totalIncome = ref(0)
const totalExpense = ref(0)

const isAdmin = computed(() => userStore.user?.role?.roleCode === 'admin')

const formRef = ref<FormInstance>()
const formState = reactive({
  clubName: '',
  clubCode: '',
  description: '',
  logo: '',
  category: '综合社团'
})

const formRules: Record<string, Rule[]> = {
  clubName: [{ required: true, message: '请输入社团名称' }],
  clubCode: [{ required: true, message: '请输入社团编码' }]
}

const statusMap: Record<string, string> = {
  0: '停用',
  1: '活跃',
  2: '待审核',
  ACTIVE: '活跃',
  INACTIVE: '停用',
  PENDING: '待审核'
}

const getStatusClass = (status: number | string) => {
  if (status === 1 || status === 'ACTIVE') return 'ACTIVE'
  if (status === 2 || status === 'PENDING') return 'PENDING'
  return 'INACTIVE'
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const formatMoney = (amount: number) => {
  return '¥' + amount.toFixed(2)
}

const resolveLogoUrl = (url?: string) => {
  return normalizeMediaUrl(url)
}

const loadClubData = async () => {
  loading.value = true
  try {
    const res = await clubApi.getList(0, 100)
    allClubs.value = (res.data?.content || []).map(item => ({
      ...item,
      logo: resolveLogoUrl(item.logo)
    }))
    if (allClubs.value.length > 0) {
      if (selectedClubId.value) {
        club.value = allClubs.value.find(c => c.id === selectedClubId.value) || allClubs.value[0]
      } else {
        club.value = allClubs.value[0]
      }
      selectedClubId.value = club.value.id
      loadMemberCount()
      loadFinanceData()
    }
  } catch (error) {
    console.error('加载社团数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleClubChange = (clubId: number) => {
  const found = allClubs.value.find(c => c.id === clubId)
  if (found) {
    club.value = found
    selectedClubId.value = clubId
    loadMemberCount()
    loadFinanceData()
  }
}

const loadMemberCount = async () => {
  if (!club.value) return
  try {
    const res = await clubApi.getMembers(club.value.id, 0, 1)
    memberCount.value = res.data?.totalElements || 0
  } catch (error) {
    console.error('加载成员数量失败:', error)
  }
}

const loadFinanceData = async () => {
  if (!club.value) return
  try {
    const now = new Date()
    const startTime = new Date(now.getFullYear(), now.getMonth() >= 8 ? 8 : 0, 1)
    const res = await fundApi.getFlowList({
      clubId: club.value.id,
      startTime: startTime.toISOString(),
      endTime: now.toISOString(),
      size: 1000
    })
    const flows = res.data?.list || res.data?.content || []
    totalIncome.value = flows
      .filter((f: FundFlow) => f.flowType === 'INCOME')
      .reduce((sum: number, f: FundFlow) => sum + (f.amount || 0), 0)
    totalExpense.value = flows
      .filter((f: FundFlow) => f.flowType === 'EXPENSE')
      .reduce((sum: number, f: FundFlow) => sum + (f.amount || 0), 0)
  } catch (error) {
    console.error('加载财务数据失败:', error)
  }
}

const showEditModal = () => {
  isCreate.value = false
  if (club.value) {
    formState.clubName = club.value.clubName || ''
    formState.clubCode = club.value.clubCode || ''
    formState.description = club.value.description || ''
    formState.logo = resolveLogoUrl(club.value.logo) || ''
    formState.category = club.value.category || '综合社团'
  }
  editVisible.value = true
}

const showCreateModal = () => {
  isCreate.value = true
  formState.clubName = ''
  formState.clubCode = ''
  formState.description = ''
  formState.logo = ''
  formState.category = '综合社团'
  editVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true
    
    if (isCreate.value) {
      await clubApi.create({
        clubName: formState.clubName,
        clubCode: formState.clubCode,
        description: formState.description,
        logo: formState.logo,
        category: formState.category
      })
      message.success('社团创建成功')
    } else if (club.value) {
      await clubApi.update(club.value.id, {
        clubName: formState.clubName,
        clubCode: formState.clubCode || club.value.clubCode || '',
        description: formState.description,
        logo: formState.logo,
        category: formState.category
      })
      message.success('社团信息更新成功')
    }
    
    editVisible.value = false
    loadClubData()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitting.value = false
  }
}

const beforeLogoUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    message.error('图片大小不能超过5MB')
    return false
  }
  return true
}

const handleLogoUpload = async (options: any) => {
  const file = options.file as File
  logoUploading.value = true
  try {
    const uploadRes = await uploadApi.uploadImage(file, 'logo')
    const uploadedUrl = uploadRes.data.url
    formState.logo = uploadedUrl.startsWith('/upload/') ? uploadedUrl : `/upload${uploadedUrl}`
    options.onSuccess?.(uploadRes, file)
    message.success('Logo上传成功')
  } catch (error: any) {
    options.onError?.(error)
    message.error(error.message || 'Logo上传失败')
  } finally {
    logoUploading.value = false
  }
}

onMounted(() => {
  loadClubData()
})
</script>

<style scoped>
.club-manage {
  padding: 24px;
  background: #fff;
  min-height: 100%;
}

.club-selector {
  margin-bottom: 20px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.selector-label {
  font-weight: 500;
  color: #333;
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

.club-card {
  background: #fafafa;
  border-radius: 8px;
  padding: 24px;
}

.club-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.club-avatar {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  background: #e8e8e8;
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
  font-size: 32px;
  font-weight: 600;
  color: #666;
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
}

.logo-uploader {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-preview {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid #d9d9d9;
}

.club-info {
  flex: 1;
}

.club-info h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
}

.club-meta {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.ACTIVE,
.status-tag.ACTIVE {
  background: #f6ffed;
  color: #52c41a;
}

.status-tag.INACTIVE,
.status-tag.INACTIVE {
  background: #fff7e6;
  color: #fa8c16;
}

.status-tag.PENDING,
.status-tag.PENDING {
  background: #f0f0f0;
  color: #666;
}

.member-count {
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.club-details {
  margin-top: 16px;
}

.finance-summary h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  border: 1px solid #e8e8e8;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.stat-value.income {
  color: #52c41a;
}

.stat-value.expense {
  color: #ff4d4f;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

:deep(.ant-descriptions-item-label) {
  background: #fafafa;
  font-weight: 500;
}
</style>
