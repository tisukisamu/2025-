<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-gradient-to-r from-#111111 to-#374151 text-white p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-24px m-0 font-bold">打卡历史</h1>
          <p class="m-0 mt-2 text-white/70">查看您的所有打卡记录</p>
        </div>
      </div>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <template #title>
        <div class="flex items-center justify-between">
          <span class="text-16px font-600">打卡记录</span>
          <a-select v-model:value="selectedHabit" style="width: 200px" placeholder="筛选习惯" allowClear>
            <a-select-option value="">全部习惯</a-select-option>
          </a-select>
        </div>
      </template>
      
      <a-table
        :columns="columns"
        :data-source="records"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'habitName'">
            <div class="flex items-center gap-2">
              <div 
                class="w-8 h-8 rounded-lg flex items-center justify-center text-white text-sm"
                :style="{ backgroundColor: record.color || '#111111' }"
              >
                {{ (record.icon || '✓').slice(0, 1) }}
              </div>
              <span>{{ record.habitName }}</span>
            </div>
          </template>
          <template v-if="column.key === 'checkDate'">
            {{ formatDate(record.checkDate) }}
          </template>
          <template v-if="column.key === 'checkTime'">
            {{ record.checkTime || '-' }}
          </template>
          <template v-if="column.key === 'note'">
            <span class="text-#6b7280">{{ record.note || '-' }}</span>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { getHistory } from '@/api/check'

const userStore = useUserStore()
const loading = ref(false)
const selectedHabit = ref('')
const records = ref([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const columns = [
  { title: '习惯名称', key: 'habitName', dataIndex: 'habitName' },
  { title: '打卡日期', key: 'checkDate', dataIndex: 'checkDate' },
  { title: '打卡时间', key: 'checkTime', dataIndex: 'checkTime' },
  { title: '备注', key: 'note', dataIndex: 'note' }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getHistory({
      page: pagination.current - 1,
      size: pagination.pageSize,
      habitId: selectedHabit.value || undefined
    })
    if (res.code === 200) {
      records.value = res.data
      pagination.total = res.totalElements
    }
  } finally {
    loading.value = false
  }
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchData()
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
}

watch(selectedHabit, () => {
  pagination.current = 1
  fetchData()
})

onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchData()
  }
})
</script>
