<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">活动统计</h1>
      <p class="page-subtitle">活动数量、报名人数与预算概览</p>
    </div>

    <a-row :gutter="16">
      <a-col :span="6"><div class="card metric"><div class="label">活动总数</div><div class="value">{{ stats.total }}</div></div></a-col>
      <a-col :span="6"><div class="card metric"><div class="label">已发布</div><div class="value">{{ stats.published }}</div></div></a-col>
      <a-col :span="6"><div class="card metric"><div class="label">待审核</div><div class="value">{{ stats.submitted }}</div></div></a-col>
      <a-col :span="6"><div class="card metric"><div class="label">累计预算</div><div class="value">¥{{ stats.budget.toFixed(2) }}</div></div></a-col>
    </a-row>

    <div class="card table-card">
      <a-table :columns="columns" :data-source="activityList" row-key="id" :pagination="false" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { activityApi } from '@/api'
import type { Activity } from '@/types'

const activityList = ref<Activity[]>([])

const stats = reactive({
  total: 0,
  published: 0,
  submitted: 0,
  budget: 0
})

const columns = [
  { title: '活动名称', dataIndex: 'activityName', key: 'activityName' },
  {
    title: '活动时间',
    key: 'time',
    customRender: ({ record }: any) => `${dayjs(record.startTime).format('YYYY-MM-DD')} ~ ${dayjs(record.endTime).format('YYYY-MM-DD')}`
  },
  { title: '预算', dataIndex: 'budget', key: 'budget' },
  {
    title: '状态',
    key: 'status',
    customRender: ({ record }: any) => ({ DRAFT: '草稿', SUBMITTED: '待审核', PUBLISHED: '已发布', REJECTED: '已驳回' }[record.status] || record.status)
  },
  { title: '报名人数', dataIndex: 'signupCount', key: 'signupCount' }
]

const fetchData = async () => {
  const res = await activityApi.getMyCreated(0, 200)
  const list = res.data.list || []
  activityList.value = list
  stats.total = list.length
  stats.published = list.filter(item => item.status === 'PUBLISHED').length
  stats.submitted = list.filter(item => item.status === 'SUBMITTED').length
  stats.budget = list.reduce((sum, item) => sum + (item.budget || 0), 0)
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.metric .label {
  color: #6e6e6e;
}

.metric .value {
  margin-top: 8px;
  font-size: 26px;
  font-weight: 700;
  color: #111111;
}

.table-card {
  margin-top: 16px;
}
</style>
