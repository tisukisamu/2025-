<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-semibold text-neutral-900">职位监管</h1>
      <a-input-search
        v-model:value="keyword"
        placeholder="按职位名称筛选"
        style="width: 280px"
      />
    </div>

    <a-card :bordered="false" class="rounded-xl">
      <a-table :columns="columns" :data-source="filteredJobs" :pagination="{ pageSize: 10 }" row-key="id">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === 'ACTIVE' ? 'green' : 'red'">
              {{ record.status }}
            </a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <div class="flex items-center gap-2">
              <a-button size="small" @click="viewDetail(record)">详情</a-button>
              <a-button
                size="small"
                danger
                :disabled="record.status !== 'ACTIVE'"
                @click="closeJob(record)"
              >
                下架
              </a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { companyApi, jobApi } from '@/api'

const keyword = ref('')
const jobs = ref<any[]>([])

const columns = [
  { title: '职位ID', dataIndex: 'id', width: 90 },
  { title: '职位名称', dataIndex: 'title' },
  { title: '企业', dataIndex: 'companyName' },
  { title: '地点', dataIndex: 'location' },
  { title: '薪资', dataIndex: 'salary' },
  { title: '状态', dataIndex: 'status', width: 120 },
  { title: '发布时间', dataIndex: 'createdAt', width: 130 },
  { title: '操作', dataIndex: 'action', width: 170 }
]

const filteredJobs = computed(() => {
  if (!keyword.value) return jobs.value
  return jobs.value.filter(item => item.title.includes(keyword.value))
})

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleDateString('zh-CN')
}

const fetchJobs = async () => {
  try {
    const [jobsRes, companyRes] = await Promise.all([
      jobApi.getAll(0, 200),
      companyApi.getAll()
    ])
    const companyMap = new Map((companyRes.data || []).map((item: any) => [item.id, item]))
    jobs.value = (jobsRes.data?.content || []).map((item: any) => ({
      ...item,
      companyName: companyMap.get(item.companyId)?.name || `企业 #${item.companyId || '-'}`,
      salary: `${item.salaryMin || 0}-${item.salaryMax || 0}K`,
      createdAt: formatDate(item.createdAt)
    }))
  } catch (error) {
    message.error('获取职位列表失败')
  }
}

const viewDetail = (job: any) => {
  Modal.info({
    title: `职位 #${job.id}`,
    content: `${job.title} ｜ ${job.companyName} ｜ ${job.location}`
  })
}

const closeJob = (job: any) => {
  Modal.confirm({
    title: '确认下架该职位？',
    content: `职位：${job.title}`,
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        await jobApi.close(job.id)
        message.success('职位已下架')
        await fetchJobs()
      } catch (error) {
        message.error('操作失败')
      }
    }
  })
}

onMounted(() => {
  fetchJobs()
})
</script>
