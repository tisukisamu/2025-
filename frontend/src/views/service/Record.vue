<template>
  <div class="module-page">
    <div class="module-header">
      <div>
        <h1 class="module-title">服务记录</h1>
        <p class="module-subtitle">已完成任务与过程说明归档</p>
      </div>
      <a-input-search v-model:value="keyword" placeholder="搜索订单号 / 宠物名" class="search-input" />
    </div>

    <a-table
      :columns="columns"
      :data-source="filteredRecords"
      :loading="loading"
      row-key="id"
      :pagination="{ pageSize: 10, showSizeChanger: false }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag color="success">已完成</a-tag>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getAppointmentList } from '../../api/appointment'

const loading = ref(false)
const keyword = ref('')
const records = ref([])

const columns = [
  { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 180 },
  { title: '宠物', dataIndex: 'petName', key: 'petName', width: 140 },
  { title: '服务套餐', dataIndex: 'packageName', key: 'packageName', width: 180 },
  { title: '完成时间', dataIndex: 'updatedAt', key: 'updatedAt', width: 180 },
  { title: '状态', key: 'status', width: 100 }
]

const filteredRecords = computed(() => {
  const base = records.value
  if (!keyword.value.trim()) {
    return base
  }
  const text = keyword.value.trim().toLowerCase()
  return base.filter((item) => {
    return (
      item.orderNo?.toLowerCase().includes(text) ||
      item.petName?.toLowerCase().includes(text)
    )
  })
})

const loadRecords = async () => {
  loading.value = true
  try {
    const res = await getAppointmentList({ pageNum: 1, pageSize: 200, status: 'completed' })
    if (res.code === 200) {
      records.value = res.data?.list || []
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.module-page {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
}

.module-header {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.module-title {
  margin: 0;
  font-size: 24px;
  color: #1f1f1f;
}

.module-subtitle {
  margin: 6px 0 0;
  color: #8c8c8c;
}

.search-input {
  max-width: 280px;
}
</style>
