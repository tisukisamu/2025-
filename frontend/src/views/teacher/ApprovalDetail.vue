<template>
  <div class="approval-detail">
    <div class="page-header">
      <a-button type="text" @click="goBack">
        <ArrowLeftOutlined /> 返回
      </a-button>
      <h2>审批详情</h2>
    </div>

    <a-spin :spinning="loading">
      <template v-if="apply">
        <a-row :gutter="24">
          <a-col :span="16">
            <a-card title="申请信息" :bordered="false" class="info-card">
              <a-descriptions :column="2" bordered>
                <a-descriptions-item label="申请社团">
                  {{ apply.club?.clubName || '-' }}
                </a-descriptions-item>
                <a-descriptions-item label="申请金额">
                  <span class="amount">{{ formatMoney(apply.amount) }}</span>
                </a-descriptions-item>
                <a-descriptions-item label="申请状态">
                  <a-tag :color="getStatusColor(apply.status)">
                    {{ statusMap[apply.status] || apply.status }}
                  </a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="申请时间">
                  {{ formatDateTime(apply.createTime) }}
                </a-descriptions-item>
                <a-descriptions-item label="资金用途" :span="2">
                  {{ apply.purpose || '-' }}
                </a-descriptions-item>
                <a-descriptions-item label="详细说明" :span="2">
                  {{ apply.description || '-' }}
                </a-descriptions-item>
              </a-descriptions>
            </a-card>

            <a-card title="附件材料" :bordered="false" class="info-card" v-if="apply.attachments">
              <div class="attachments">
                <div v-for="(file, index) in attachmentList" :key="index" class="attachment-item">
                  <FileOutlined />
                  <span class="file-name">{{ file }}</span>
                  <a-button type="link" size="small">下载</a-button>
                </div>
              </div>
            </a-card>

            <a-card title="审批流程" :bordered="false" class="info-card">
              <a-timeline>
                <a-timeline-item
                  v-for="(record, index) in approvalRecords"
                  :key="index"
                  :color="getTimelineColor(record.action)"
                >
                  <div class="timeline-content">
                    <div class="timeline-header">
                      <span class="step-name">{{ getStepName(record.step) }}</span>
                      <a-tag :color="getActionColor(record.action)" size="small">
                        {{ actionMap[record.action] || record.action }}
                      </a-tag>
                    </div>
                    <div class="timeline-info">
                      <span class="approver">{{ record.approverName }}</span>
                      <span class="time">{{ formatDateTime(record.createTime) }}</span>
                    </div>
                    <div class="timeline-comment" v-if="record.comment">
                      "{{ record.comment }}"
                    </div>
                  </div>
                </a-timeline-item>
              </a-timeline>
            </a-card>
          </a-col>

          <a-col :span="8">
            <a-card title="审批操作" :bordered="false" class="action-card">
              <div class="current-status">
                <div class="status-label">当前状态</div>
                <div class="status-value">
                  <a-tag :color="getStatusColor(apply.status)" style="font-size: 14px; padding: 4px 12px;">
                    {{ statusMap[apply.status] || apply.status }}
                  </a-tag>
                </div>
              </div>

              <a-divider />

              <template v-if="apply.status === 'PRESIDENT_APPROVED'">
                <a-form layout="vertical">
                  <a-form-item label="审批意见">
                    <a-textarea
                      v-model:value="comment"
                      placeholder="请输入审批意见（可选）"
                      :rows="4"
                    />
                  </a-form-item>
                </a-form>
                <div class="action-buttons">
                  <a-button
                    type="primary"
                    block
                    @click="handleApprove"
                    :loading="submitting"
                  >
                    <CheckOutlined /> 审批通过
                  </a-button>
                  <a-button
                    danger
                    block
                    @click="handleReject"
                    :loading="submitting"
                    style="margin-top: 12px"
                  >
                    <CloseOutlined /> 驳回申请
                  </a-button>
                </div>
              </template>

              <template v-else>
                <a-alert
                  :type="apply.status === 'TEACHER_APPROVED' ? 'success' : 'warning'"
                  :message="apply.status === 'TEACHER_APPROVED' ? '该申请已审批通过' : '该申请当前状态无法审批'"
                  show-icon
                />
              </template>
            </a-card>

            <a-card title="申请人信息" :bordered="false" class="info-card">
              <div class="applicant-info">
                <a-avatar :size="48" :src="resolveAvatar(apply.applicant?.avatar)" :style="{ backgroundColor: '#1a1a1a' }">
                  {{ apply.applicant?.realName?.charAt(0) || 'U' }}
                </a-avatar>
                <div class="applicant-detail">
                  <div class="name">{{ apply.applicant?.realName || '-' }}</div>
                  <div class="meta">{{ apply.applicant?.studentId || '-' }}</div>
                </div>
              </div>
            </a-card>
          </a-col>
        </a-row>
      </template>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { ArrowLeftOutlined, CheckOutlined, CloseOutlined, FileOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import { fundApi } from '@/api/fund'
import { approvalApi } from '@/api/approval'
import type { FundApply } from '@/types'
import { normalizeMediaUrl } from '@/utils/media'

interface ApprovalRecord {
  step: number
  approverName: string
  action: string
  comment?: string
  createTime: string
}

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const submitting = ref(false)
const apply = ref<FundApply | null>(null)
const comment = ref('')
const approvalRecords = ref<ApprovalRecord[]>([])

const statusMap: Record<string, string> = {
  PENDING: '待审批',
  PRESIDENT_APPROVED: '社长已批',
  TEACHER_APPROVED: '已通过',
  COMPLETED: '已完成',
  REJECTED: '已驳回'
}

const actionMap: Record<string, string> = {
  SUBMIT: '提交申请',
  APPROVE: '通过',
  REJECT: '驳回'
}

const attachmentList = computed(() => {
  if (!apply.value?.attachments) return []
  try {
    return JSON.parse(apply.value.attachments)
  } catch {
    return apply.value.attachments.split(',')
  }
})

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

const getActionColor = (action: string) => {
  const colors: Record<string, string> = {
    SUBMIT: 'default',
    APPROVE: 'success',
    REJECT: 'error'
  }
  return colors[action] || 'default'
}

const getTimelineColor = (action: string) => {
  const colors: Record<string, string> = {
    SUBMIT: 'gray',
    APPROVE: 'green',
    REJECT: 'red'
  }
  return colors[action] || 'gray'
}

const getStepName = (step: number) => {
  const names: Record<number, string> = {
    1: '社长审批',
    2: '指导老师审批',
    3: '财务执行'
  }
  return names[step] || `第${step}步`
}

const formatMoney = (amount: number) => {
  return '¥' + (amount || 0).toFixed(2)
}

const formatDateTime = (dateStr?: string) => {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

const resolveAvatar = (url?: string) => normalizeMediaUrl(url)

const goBack = () => {
  router.back()
}

const loadApplyDetail = async () => {
  const id = route.params.id
  if (!id) return
  
  loading.value = true
  try {
    const res = await fundApi.getApplyById(parseInt(id as string))
    apply.value = res.data
    
    approvalRecords.value = [
      {
        step: 1,
        approverName: apply.value.applicant?.realName || '申请人',
        action: 'SUBMIT',
        comment: apply.value.purpose,
        createTime: apply.value.createTime || ''
      }
    ]
    
    if (apply.value.status !== 'PENDING') {
      approvalRecords.value.push({
        step: 1,
        approverName: '社长',
        action: apply.value.status === 'REJECTED' ? 'REJECT' : 'APPROVE',
        comment: '同意申请',
        createTime: apply.value.updateTime || ''
      })
    }
    
    if (apply.value.status === 'TEACHER_APPROVED' || apply.value.status === 'COMPLETED') {
      approvalRecords.value.push({
        step: 2,
        approverName: '指导老师',
        action: 'APPROVE',
        comment: '审批通过',
        createTime: apply.value.updateTime || ''
      })
    }
  } catch (error) {
    console.error('加载申请详情失败:', error)
    message.error('加载申请详情失败')
  } finally {
    loading.value = false
  }
}

const handleApprove = async () => {
  if (!apply.value) return
  
  submitting.value = true
  try {
    await approvalApi.approve(apply.value.id, { comment: comment.value })
    message.success('审批通过')
    loadApplyDetail()
  } catch (error: any) {
    message.error(error.response?.data?.message || '审批失败')
  } finally {
    submitting.value = false
  }
}

const handleReject = () => {
  Modal.confirm({
    title: '驳回申请',
    content: '确定要驳回该申请吗？',
    okText: '确定驳回',
    okType: 'danger',
    cancelText: '取消',
    async onOk() {
      if (!apply.value) return
      
      submitting.value = true
      try {
        await approvalApi.reject(apply.value.id, { comment: comment.value || '申请被驳回' })
        message.success('已驳回申请')
        loadApplyDetail()
      } catch (error: any) {
        message.error(error.response?.data?.message || '驳回失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  loadApplyDetail()
})
</script>

<style scoped>
.approval-detail {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100%;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
}

.info-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.info-card :deep(.ant-card-head) {
  border-bottom: 1px solid #f0f0f0;
}

.info-card :deep(.ant-card-head-title) {
  font-weight: 600;
  color: #1a1a1a;
}

.amount {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.attachments {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
}

.file-name {
  flex: 1;
  color: #333;
}

.timeline-content {
  padding-bottom: 8px;
}

.timeline-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.step-name {
  font-weight: 600;
  color: #1a1a1a;
}

.timeline-info {
  display: flex;
  gap: 16px;
  color: #666;
  font-size: 13px;
}

.timeline-comment {
  margin-top: 8px;
  padding: 8px 12px;
  background: #fafafa;
  border-radius: 4px;
  color: #666;
  font-size: 13px;
}

.action-card {
  border-radius: 8px;
  margin-bottom: 16px;
}

.action-card :deep(.ant-card-head-title) {
  font-weight: 600;
  color: #1a1a1a;
}

.current-status {
  text-align: center;
  padding: 16px 0;
}

.status-label {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.action-buttons {
  margin-top: 16px;
}

.applicant-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.applicant-detail .name {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.applicant-detail .meta {
  color: #666;
  font-size: 13px;
  margin-top: 4px;
}

:deep(.ant-descriptions-item-label) {
  background: #fafafa;
  font-weight: 500;
}
</style>
