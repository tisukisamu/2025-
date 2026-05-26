<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-#111111 text-white p-5 flex items-center justify-between">
      <div>
        <h1 class="text-22px m-0">习惯管理</h1>
        <p class="m-0 mt-2 text-#9ca3af">统一管理你的日常习惯与执行状态</p>
      </div>
      <a-space>
        <a-button @click="router.push('/habits/categories')">分类管理</a-button>
        <a-button type="primary" class="bg-black border-black text-white" @click="goCreate">新建习惯</a-button>
      </a-space>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <a-input-search
          v-model:value="keyword"
          placeholder="搜索习惯名称"
          allow-clear
          @search="applyFilter"
          @change="applyFilter"
        />
        <a-select
          v-model:value="selectedCategory"
          placeholder="按分类筛选"
          allow-clear
          @change="applyFilter"
        >
          <a-select-option :value="0">全部分类</a-select-option>
          <a-select-option v-for="item in categoryOptions" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-select-option>
        </a-select>
        <a-select
          v-model:value="selectedStatus"
          placeholder="按状态筛选"
          @change="applyFilter"
        >
          <a-select-option value="ALL">全部状态</a-select-option>
          <a-select-option value="ACTIVE">进行中</a-select-option>
          <a-select-option value="PAUSED">已暂停</a-select-option>
          <a-select-option value="DELETED">已删除</a-select-option>
        </a-select>
      </div>
    </a-card>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <span class="text-12px text-#6b7280">已选 {{ selectedRowKeys.length }} 项</span>
        <a-button size="small" :disabled="selectedRowKeys.length === 0" @click="batchUpdate('PAUSE')">批量暂停</a-button>
        <a-button size="small" :disabled="selectedRowKeys.length === 0" @click="batchUpdate('RESUME')">批量恢复</a-button>
        <a-popconfirm
          title="确认批量删除已选习惯？"
          ok-text="删除"
          cancel-text="取消"
          @confirm="batchUpdate('DELETE')"
        >
          <a-button size="small" danger :disabled="selectedRowKeys.length === 0">批量删除</a-button>
        </a-popconfirm>
      </div>
      <a-table
        row-key="id"
        :loading="loading"
        :data-source="filteredHabits"
        :row-selection="rowSelection"
        :pagination="{ pageSize: 8, showSizeChanger: false }"
      >
        <a-table-column title="习惯" key="name">
          <template #default="{ record }">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-full flex items-center justify-center text-white" :style="{ backgroundColor: record.color || '#111111' }">
                {{ (record.icon || '习').slice(0, 1) }}
              </div>
              <div>
                <div class="font-600 text-#111827">{{ record.name }}</div>
                <div class="text-12px text-#9ca3af">{{ record.description || '暂无描述' }}</div>
              </div>
            </div>
          </template>
        </a-table-column>
        <a-table-column title="分类" data-index="categoryName" key="categoryName">
          <template #default="{ record }">
            <a-tag color="default">{{ record.categoryName || '未分类' }}</a-tag>
          </template>
        </a-table-column>
        <a-table-column title="重复规则" key="repeatType">
          <template #default="{ record }">
            <span class="text-#4b5563">{{ formatRepeat(record) }}</span>
          </template>
        </a-table-column>
        <a-table-column title="状态" key="status">
          <template #default="{ record }">
            <a-tag :color="statusColorMap[record.status]">{{ statusTextMap[record.status] }}</a-tag>
          </template>
        </a-table-column>
        <a-table-column title="操作" key="action" width="290">
          <template #default="{ record }">
            <a-space>
              <a-button size="small" @click="router.push(`/habits/${record.id}`)">详情</a-button>
              <a-button size="small" @click="goEdit(record.id)">编辑</a-button>
              <a-button
                v-if="record.status === 'ACTIVE'"
                size="small"
                @click="toggleStatus(record.id, 'pause')"
              >
                暂停
              </a-button>
              <a-button
                v-if="record.status === 'PAUSED'"
                size="small"
                @click="toggleStatus(record.id, 'resume')"
              >
                恢复
              </a-button>
              <a-popconfirm title="确认删除该习惯？" ok-text="删除" cancel-text="取消" @confirm="removeHabit(record.id)">
                <a-button size="small" danger>删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </a-table-column>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { batchUpdateHabitStatus, getHabits, getCategories, deleteHabit, pauseHabit, resumeHabit } from '../../api/habit'

const router = useRouter()
const loading = ref(false)
const habits = ref([])
const keyword = ref('')
const selectedCategory = ref(0)
const selectedStatus = ref('ALL')
const categoryOptions = ref([])
const selectedRowKeys = ref([])
const statusTextMap = {
  ACTIVE: '进行中',
  PAUSED: '已暂停',
  DELETED: '已删除'
}
const statusColorMap = {
  ACTIVE: 'success',
  PAUSED: 'default',
  DELETED: 'error'
}

const fetchData = async () => {
  loading.value = true
  try {
    const [habitRes, categoryRes] = await Promise.all([getHabits(), getCategories()])
    habits.value = habitRes.data || []
    categoryOptions.value = (categoryRes.data || []).map((item) => ({
      value: item.id,
      label: item.name
    }))
  } finally {
    loading.value = false
  }
}

const filteredHabits = computed(() => {
  return habits.value.filter((item) => {
    const matchKeyword = item.name.toLowerCase().includes(keyword.value.trim().toLowerCase())
    const matchCategory = selectedCategory.value === 0 || item.categoryId === selectedCategory.value
    const matchStatus = selectedStatus.value === 'ALL' || item.status === selectedStatus.value
    return matchKeyword && matchCategory && matchStatus
  })
})

const applyFilter = () => {
  filteredHabits.value
}

const onSelectChange = (keys) => {
  selectedRowKeys.value = keys
}

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: onSelectChange
}))

const formatRepeat = (habit) => {
  if (habit.repeatType === 'DAILY') {
    return '每天'
  }
  if (!habit.repeatDays) {
    return '每周'
  }
  const dayMap = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const days = String(habit.repeatDays).split(',').map((d) => Number(d))
  return days.map((d) => dayMap[d] || `周${d}`).join('、')
}

const goCreate = () => {
  router.push('/habits/create')
}

const goEdit = (id) => {
  router.push(`/habits/${id}/edit`)
}

const toggleStatus = async (id, mode) => {
  if (mode === 'pause') {
    await pauseHabit(id)
    message.success('已暂停该习惯')
  } else {
    await resumeHabit(id)
    message.success('已恢复该习惯')
  }
  fetchData()
}

const removeHabit = async (id) => {
  await deleteHabit(id)
  message.success('删除成功')
  selectedRowKeys.value = selectedRowKeys.value.filter((k) => k !== id)
  fetchData()
}

const batchUpdate = async (action) => {
  if (selectedRowKeys.value.length === 0) return
  await batchUpdateHabitStatus({
    ids: selectedRowKeys.value,
    action
  })
  selectedRowKeys.value = []
  message.success('批量操作成功')
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>
