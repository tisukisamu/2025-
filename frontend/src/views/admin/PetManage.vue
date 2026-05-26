<template>
  <div class="module-page">
    <div class="module-header">
      <div>
        <h1 class="module-title">宠物管理</h1>
        <p class="module-subtitle">查看与管理宠物档案信息</p>
      </div>
      <a-space>
        <a-button @click="goBack">返回后台</a-button>
        <a-input-search
          v-model:value="keyword"
          placeholder="搜索宠物名 / 品种"
          class="search-input"
        />
      </a-space>
    </div>

    <a-card :bordered="false">
      <a-table
        :columns="columns"
        :data-source="filteredList"
        :loading="loading"
        row-key="id"
        :pagination="{ pageSize: 8, showSizeChanger: false }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'name'">
            <a-space>
              <a-avatar :src="getPetAvatar(record)">{{ record.name?.[0] }}</a-avatar>
              <span class="name-cell">{{ record.name }}</span>
            </a-space>
          </template>
          <template v-else-if="column.key === 'passDate'">
            {{ formatDate(record.passDate) }}
          </template>
          <template v-else-if="column.key === 'createdAt'">
            {{ formatDate(record.createdAt) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-popconfirm title="确认删除该宠物档案？" @confirm="handleDelete(record)">
              <a-button type="link" danger>删除</a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { getPetList, deletePet } from '../../api/pet'
import { getImageUrl } from '../../utils'

const router = useRouter()
const loading = ref(false)
const keyword = ref('')
const list = ref([])

const columns = [
  { title: '宠物', key: 'name', width: 220 },
  { title: '类型', dataIndex: 'type', key: 'type', width: 120 },
  { title: '品种', dataIndex: 'breed', key: 'breed', width: 160 },
  { title: '离世日期', key: 'passDate', width: 130 },
  { title: '创建时间', key: 'createdAt', width: 130 },
  { title: '操作', key: 'action', width: 100, fixed: 'right' }
]

const filteredList = computed(() => {
  if (!keyword.value.trim()) {
    return list.value
  }
  const text = keyword.value.trim().toLowerCase()
  return list.value.filter((item) => {
    return (
      item.name?.toLowerCase().includes(text) ||
      item.breed?.toLowerCase().includes(text) ||
      item.type?.toLowerCase().includes(text)
    )
  })
})

const formatDate = (value) => {
  return value ? dayjs(value).format('YYYY-MM-DD') : '-'
}

const getPetAvatar = (record) => {
  return record.photo ? getImageUrl(record.photo) : null
}

const goBack = () => {
  router.push('/admin')
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPetList()
    if (res.code === 200) {
      list.value = res.data || []
    }
  } finally {
    loading.value = false
  }
}

const handleDelete = async (record) => {
  try {
    await deletePet(record.id)
    message.success('删除成功')
    await loadData()
  } catch {
    message.error('删除失败')
  }
}

onMounted(() => {
  loadData()
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

.name-cell {
  font-weight: 500;
  color: #1f1f1f;
}
</style>
