<template>
  <div class="approval-list">
    <div class="page-header">
      <h2>审批列表</h2>
      <p class="subtitle">审核社团资金申请</p>
    </div>

    <div class="filter-bar">
      <a-space>
        <a-select
          v-model:value="filterStatus"
          style="width: 150px"
          placeholder="审批状态"
          @change="handleFilter"
          allowClear
        >
          <a-select-option value="PENDING">待审批</a-select-option>
          <a-select-option value="PRESIDENT_APPROVED">社长已批</a-select-option>
          <a-select-option value="TEACHER_APPROVED">已通过</a-select-option>
          <a-select-option value="REJECTED">已驳回</a-select-option>
        </a-select>
        <a-select
          v-model:value="filterClub"
          style="width: 200px"
          placeholder="选择社团"
          @change="handleFilter"
          allowClear
        >
          <a-select-option v-for="club in clubs" :key="club.id" :value="club.id">
            {{ club.clubName }}
          </a-select-option>
        </a-select>
      </a-space>
    </div>

    <a-table
      :columns="columns"
      :data-source="applyList"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      row-key="id"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'clubName'">
          <div class="club-cell">
            <a-avatar :size="32" :src="resolveClubLogo(record.club?.logo, record.club?.id)" :style="{ backgroundColor: '#1a1a1a' }">
              {{ record.club?.clubName?.charAt(0) || '社' }}
            </a-avatar>
            <span>{{ record.club?.clubName || '-' }}</span>
          </div>
        </template>
        <template v-if="column.key === 'amount'">
          <span class="amount">{{ formatMoney(record.amount) }}</span>
        </template>
        <template v-if="column.key === 'status'">
          <a-tag :color="getStatusColor(record.status)">
            {{ statusMap[record.status] || record.status }}
          </a-tag>
        </template>
        <template v-if="column.key === 'createTime'">
          {{ formatDateTime(record.createTime) }}
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="viewDetail(record)">
              详情
            </a-button>
            <template v-if="record.status === 'PRESIDENT_APPROVED'">
              <a-button type="link" size="small" @click="showApproveModal(record)">
                <CheckOutlined /> 通过
              </a-button>
              <a-button type="link" size="small" danger @click="showRejectModal(record)">
                <CloseOutlined /> 驳回
              </a-button>
            </template>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="approveVisible"
      title="审批通过"
      @ok="handleApprove"
      :confirmLoading="submitting"
    >
      <a-form layout="vertical">
        <a-form-item label="申请信息">
          <p><strong>社团：</strong>{{ currentApply?.club?.clubName }}</p>
          <p><strong>金额：</strong>{{ formatMoney(currentApply?.amount || 0) }}</p>
          <p><strong>用途：</strong>{{ currentApply?.purpose }}</p>
        </a-form-item>
        <a-form-item label="审批意见">
          <a-textarea
            v-model:value="approveComment"
            placeholder="请输入审批意见（可选）"
            :rows="3"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="rejectVisible"
      title="驳回申请"
      @ok="handleReject"
      :confirmLoading="submitting"
    >
      <a-form layout="vertical">
        <a-form-item label="申请信息">
          <p><strong>社团：</strong>{{ currentApply?.club?.clubName }}</p>
          <p><strong>金额：</strong>{{ formatMoney(currentApply?.amount || 0) }}</p>
          <p><strong>用途：</strong>{{ currentApply?.purpose }}</p>
        </a-form-item>
        <a-form-item label="驳回原因" required>
          <a-textarea
            v-model:value="rejectReason"
            placeholder="请输入驳回原因"
            :rows="3"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { CheckOutlined, CloseOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import { clubApi } from '@/api/club'
import { fundApi } from '@/api/fund'
import { approvalApi } from '@/api/approval'
import type { FundApply, Club } from '@/types'
import { normalizeMediaUrl } from '@/utils/media'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const submitting = ref(false)
const applyList = ref<FundApply[]>([])
const clubs = ref<Club[]>([])
const filterStatus = ref<string>()
const filterClub = ref<number>()
const approveVisible = ref(false)
const rejectVisible = ref(false)
const currentApply = ref<FundApply | null>(null)
const approveComment = ref('')
const rejectReason = ref('')

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`
})

const columns = [
  { title: '社团', key: 'clubName', width: 180 },
  { title: '申请金额', key: 'amount', width: 120 },
  { title: '用途', dataIndex: 'purpose', key: 'purpose', ellipsis: true },
  { title: '状态', key: 'status', width: 100 },
  { title: '申请时间', key: 'createTime', width: 160 },
  { title: '操作', key: 'action', width: 200 }
]

const statusMap: Record<string, string> = {
  PENDING: '待审批',
  PRESIDENT_APPROVED: '社长已批',
  TEACHER_APPROVED: '已通过',
  COMPLETED: '已完成',
  REJECTED: '已驳回'
}

const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    PENDING: 'default',
    PRESIDENT_APPROVED: 'processing',
    TEACHER_APPROVED: 'success',
    COMPLETED: 'cyan',
    REJECTED: 'error'
  }
  return colors[status] || 'default'
}

const formatMoney = (amount: number) => {
  return '¥' + (amount || 0).toFixed(2)
}

const formatDateTime = (dateStr?: string) => {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

const resolveClubLogo = (url?: string, clubId?: number) => {
  const directUrl = normalizeMediaUrl(url)
  if (directUrl) return directUrl
  const club = clubs.value.find(item => item.id === clubId)
  return normalizeMediaUrl(club?.logo)
}

const loadClubs = async () => {
  try {
    const res = await clubApi.getMyClubs()
    clubs.value = (res.data || []).map(item => ({
      ...item,
      logo: normalizeMediaUrl(item.logo)
    }))
    
    if (route.query.clubId) {
      filterClub.value = parseInt(route.query.clubId as string)
    }
  } catch (error) {
    console.error('加载社团列表失败:', error)
  }
}

const loadApplyList = async () => {
  loading.value = true
  try {
    const res = await fundApi.getApplyList({
      status: filterStatus.value,
      clubId: filterClub.value,
      page: pagination.current - 1,
      size: pagination.pageSize
    })
    applyList.value = res.data?.list || res.data?.content || []
    pagination.total = res.data?.total || res.data?.totalElements || 0
  } catch (error) {
    console.error('加载申请列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  pagination.current = 1
  loadApplyList()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadApplyList()
}

const viewDetail = (apply: FundApply) => {
  router.push({ name: 'ApprovalDetail', params: { id: apply.id } })
}

const showApproveModal = (apply: FundApply) => {
  currentApply.value = apply
  approveComment.value = ''
  approveVisible.value = true
}

const showRejectModal = (apply: FundApply) => {
  currentApply.value = apply
  rejectReason.value = ''
  rejectVisible.value = true
}

const handleApprove = async () => {
  if (!currentApply.value) return
  
  submitting.value = true
  try {
    await approvalApi.approve(currentApply.value.id, {
      comment: approveComment.value
    })
    message.success('审批通过')
    approveVisible.value = false
    loadApplyList()
  } catch (error: any) {
    message.error(error.response?.data?.message || '审批失败')
  } finally {
    submitting.value = false
  }
}

const handleReject = async () => {
  if (!currentApply.value) return
  if (!rejectReason.value) {
    message.warning('请输入驳回原因')
    return
  }
  
  submitting.value = true
  try {
    await approvalApi.reject(currentApply.value.id, {
      comment: rejectReason.value
    })
    message.success('已驳回申请')
    rejectVisible.value = false
    loadApplyList()
  } catch (error: any) {
    message.error(error.response?.data?.message || '驳回失败')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await loadClubs()
  loadApplyList()
})
</script>

<style scoped>
.approval-list {
  padding: 24px;
  background: #fff;
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

.filter-bar {
  margin-bottom: 20px;
}

.club-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.amount {
  font-weight: 600;
  color: #1a1a1a;
}

:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  color: #1a1a1a;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: #f5f5f5;
}
</style>
