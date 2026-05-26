<template>
  <div class="page-container">
    <div class="page-header flex-between">
      <div>
        <h1 class="page-title">资金审核</h1>
        <p class="page-subtitle">审核社团资金申请</p>
      </div>
    </div>

    <div class="card">
      <div class="filter-bar" style="margin-bottom: 16px">
        <a-select
          v-if="allClubs.length > 1"
          v-model:value="selectedClubId"
          style="width: 250px"
          placeholder="全部社团"
          allow-clear
          @change="handleClubFilter"
        >
          <a-select-option v-for="club in allClubs" :key="club.id" :value="club.id">
            {{ club.clubName }}
          </a-select-option>
        </a-select>
      </div>
      <a-tabs v-model:activeKey="activeTab" @change="fetchData">
        <a-tab-pane key="PENDING" tab="待审核" />
        <a-tab-pane key="PRESIDENT_APPROVED" tab="已审核" />
        <a-tab-pane key="COMPLETED" tab="已完成" />
        <a-tab-pane key="REJECTED" tab="已驳回" />
      </a-tabs>

      <a-table
        :dataSource="applyList"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'amount'">
            <span class="amount">¥{{ record.amount.toFixed(2) }}</span>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="showDetail(record)">
                查看
              </a-button>
              <a-button
                v-if="activeTab === 'PENDING'"
                type="link"
                size="small"
                @click="handleApprove(record)"
              >
                审批
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>

    <a-modal
      v-model:open="detailVisible"
      title="申请详情"
      :footer="null"
      width="700px"
    >
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="申请编号">{{ currentApply?.applyNo }}</a-descriptions-item>
        <a-descriptions-item label="申请类型">{{ currentApply?.applyType }}</a-descriptions-item>
        <a-descriptions-item label="申请金额">
          <span class="amount">¥{{ currentApply?.amount.toFixed(2) }}</span>
        </a-descriptions-item>
        <a-descriptions-item label="申请状态">
          <a-tag :color="getStatusColor(currentApply?.status || '')">
            {{ getStatusText(currentApply?.status || '') }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="申请人">{{ currentApply?.applicant?.realName }}</a-descriptions-item>
        <a-descriptions-item label="申请时间">{{ formatTime(currentApply?.createTime || '') }}</a-descriptions-item>
        <a-descriptions-item label="申请理由" :span="2">
          {{ currentApply?.reason }}
        </a-descriptions-item>
      </a-descriptions>

      <div v-if="currentApply?.vouchers?.length" style="margin-top: 16px">
        <h4>凭证图片</h4>
        <a-image-preview-group>
          <a-image
            v-for="(url, index) in currentApply.vouchers"
            :key="index"
            :src="url"
            :width="100"
            style="margin-right: 8px"
          />
        </a-image-preview-group>
      </div>

      <div v-if="currentApply?.approvalRecords?.length" style="margin-top: 16px">
        <h4>审批记录</h4>
        <a-timeline>
          <a-timeline-item
            v-for="record in currentApply.approvalRecords"
            :key="record.id"
            :color="record.action === 'APPROVE' ? 'green' : 'red'"
          >
            <p><strong>{{ record.approverName }}</strong> - {{ record.action === 'APPROVE' ? '通过' : '驳回' }}</p>
            <p v-if="record.comment">意见: {{ record.comment }}</p>
            <p class="time-text">{{ formatTime(record.createTime) }}</p>
          </a-timeline-item>
        </a-timeline>
      </div>
    </a-modal>

    <a-modal
      v-model:open="approveVisible"
      title="审批"
      @ok="handleApproveSubmit"
    >
      <a-form layout="vertical">
        <a-form-item label="审批意见">
          <a-textarea v-model:value="approveComment" :rows="4" placeholder="请输入审批意见" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { fundApi, approvalApi, clubApi } from '@/api'
import { useUserStore } from '@/stores/user'
import type { FundApply, Club } from '@/types'
import dayjs from 'dayjs'

const userStore = useUserStore()
const activeTab = ref('PENDING')
const applyList = ref<FundApply[]>([])
const loading = ref(false)
const detailVisible = ref(false)
const approveVisible = ref(false)
const currentApply = ref<FundApply | null>(null)
const approveComment = ref('')
const allClubs = ref<Club[]>([])
const selectedClubId = ref<number | undefined>(undefined)

const isAdmin = computed(() => userStore.user?.role?.roleCode === 'admin')

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const columns = [
  { title: '申请编号', dataIndex: 'applyNo', key: 'applyNo' },
  { title: '申请类型', dataIndex: 'applyType', key: 'applyType' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '申请人', dataIndex: ['applicant', 'realName'], key: 'applicant' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '申请时间', dataIndex: 'createTime', key: 'createTime' },
  { title: '操作', key: 'action' }
]

const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    PENDING: 'default',
    PRESIDENT_APPROVED: 'processing',
    TEACHER_APPROVED: 'success',
    COMPLETED: 'success',
    REJECTED: 'error'
  }
  return colors[status] || 'default'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    PENDING: '待审批',
    PRESIDENT_APPROVED: '社长已审',
    TEACHER_APPROVED: '老师已审',
    COMPLETED: '已完成',
    REJECTED: '已驳回'
  }
  return texts[status] || status
}

const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const fetchData = async () => {
  loading.value = true
  try {
    const params: any = {
      status: activeTab.value,
      page: pagination.current - 1,
      size: pagination.pageSize
    }
    if (selectedClubId.value) {
      params.clubId = selectedClubId.value
    }
    const res = await fundApi.getApplyList(params)
    applyList.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取数据失败', error)
  } finally {
    loading.value = false
  }
}

const loadClubs = async () => {
  try {
    const res = await clubApi.getList(0, 100)
    allClubs.value = res.data?.content || []
  } catch (error) {
    console.error('加载社团列表失败', error)
  }
}

const handleClubFilter = () => {
  pagination.current = 1
  fetchData()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchData()
}

const showDetail = (record: FundApply) => {
  currentApply.value = record
  detailVisible.value = true
}

const handleApprove = (record: FundApply) => {
  currentApply.value = record
  approveComment.value = ''
  approveVisible.value = true
}

const handleApproveSubmit = async () => {
  if (!currentApply.value) return
  
  try {
    await approvalApi.approve(currentApply.value.id, { comment: approveComment.value })
    message.success('审批成功')
    approveVisible.value = false
    fetchData()
  } catch (error: any) {
    message.error(error.message || '审批失败')
  }
}

onMounted(async () => {
  await loadClubs()
  fetchData()
})
</script>

<style scoped>
.amount {
  font-weight: 600;
  color: #1a1a1a;
}

.time-text {
  font-size: 12px;
  color: #999;
}
</style>
