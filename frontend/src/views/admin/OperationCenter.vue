<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">运营总览</h1>
      <p class="page-subtitle">从用户、社团、资金、活动四维度快速掌握系统运行状态</p>
    </div>

    <a-row :gutter="16">
      <a-col :span="6"><div class="card metric"><div class="label">用户总数</div><div class="value">{{ stats.users }}</div></div></a-col>
      <a-col :span="6"><div class="card metric"><div class="label">社团总数</div><div class="value">{{ stats.clubs }}</div></div></a-col>
      <a-col :span="6"><div class="card metric"><div class="label">资金申请</div><div class="value">{{ stats.applies }}</div></div></a-col>
      <a-col :span="6"><div class="card metric"><div class="label">活动总数</div><div class="value">{{ stats.activities }}</div></div></a-col>
    </a-row>

    <div class="card table-card">
      <div class="table-title">待审核活动</div>
      <a-table :columns="columns" :data-source="reviewList" row-key="id" :pagination="false">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'time'">
            {{ formatTime(record.createTime) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button size="small" type="primary" @click="handleApprove(record.id)" :disabled="record.status !== 'SUBMITTED'">通过</a-button>
              <a-button size="small" danger @click="handleReject(record.id)" :disabled="record.status !== 'SUBMITTED'">驳回</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import { activityApi, clubApi, fundApi, userApi } from '@/api'
import type { Activity } from '@/types'

const reviewList = ref<Activity[]>([])
const stats = reactive({
  users: 0,
  clubs: 0,
  applies: 0,
  activities: 0
})

const columns = [
  { title: '活动名称', dataIndex: 'activityName', key: 'activityName' },
  { title: '社团', key: 'club', customRender: ({ record }: any) => record.club?.clubName || '-' },
  { title: '状态', key: 'status', width: 120, customRender: ({ record }: any) => ({ DRAFT: '草稿', SUBMITTED: '待审核', PUBLISHED: '已发布', REJECTED: '已驳回' }[record.status] || record.status) },
  { title: '提交时间', key: 'time', width: 180 },
  { title: '操作', key: 'action', width: 160 }
]

const formatTime = (value?: string) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '-')

const loadStats = async () => {
  const [usersRes, clubsRes, applyRes, activityRes] = await Promise.all([
    userApi.getList(0, 1),
    clubApi.getList(0, 1),
    fundApi.getApplyList({ page: 0, size: 1 }),
    activityApi.getList({ page: 0, size: 1 })
  ])
  stats.users = usersRes.data.total || 0
  stats.clubs = clubsRes.data.total || 0
  stats.applies = applyRes.data.total || 0
  stats.activities = activityRes.data.total || 0
}

const loadReviewList = async () => {
  const res = await activityApi.getReviewList(0, 8)
  reviewList.value = res.data.list || []
  if (reviewList.value.length === 0) {
    const latest = await activityApi.getList({ page: 0, size: 8 })
    reviewList.value = latest.data.list || []
  }
}

const handleApprove = async (id: number) => {
  await activityApi.approve(id)
  message.success('审核通过')
  loadReviewList()
}

const handleReject = async (id: number) => {
  await activityApi.reject(id)
  message.success('已驳回')
  loadReviewList()
}

onMounted(async () => {
  await Promise.all([loadStats(), loadReviewList()])
})
</script>

<style scoped>
.metric .label {
  color: #6f6f6f;
}

.metric .value {
  margin-top: 8px;
  color: #111111;
  font-size: 28px;
  font-weight: 700;
}

.table-card {
  margin-top: 16px;
}

.table-title {
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 700;
}
</style>
