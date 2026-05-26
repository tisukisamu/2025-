<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">资金入账</h1>
      <p class="page-subtitle">登记社团收入并实时更新账户流水</p>
    </div>

    <div class="card">
      <a-form layout="inline">
        <a-form-item v-if="allClubs.length > 1" label="选择社团">
          <a-select
            v-model:value="selectedClubId"
            style="width: 200px"
            placeholder="选择社团"
            @change="handleClubChange"
          >
            <a-select-option v-for="club in allClubs" :key="club.id" :value="club.id">
              {{ club.clubName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="入账金额">
          <a-input-number v-model:value="amount" :min="0.01" :precision="2" style="width: 180px" />
        </a-form-item>
        <a-form-item label="说明">
          <a-input v-model:value="description" placeholder="如：赞助到账" style="width: 260px" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" :loading="submitting" @click="handleSubmit">确认入账</a-button>
        </a-form-item>
      </a-form>
    </div>

    <div class="card table-card">
      <a-table
        :columns="columns"
        :data-source="flowList"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'amount'">
            <span :class="record.flowType === 'INCOME' ? 'income' : 'expense'">
              {{ record.flowType === 'INCOME' ? '+' : '-' }}¥{{ (record.amount || 0).toFixed(2) }}
            </span>
          </template>
          <template v-else-if="column.key === 'time'">
            {{ formatTime(record.createTime) }}
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import { clubApi, fundApi } from '@/api'
import { useUserStore } from '@/stores/user'
import type { FundFlow, Club } from '@/types'

const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const amount = ref<number>()
const description = ref('')
const clubId = ref<number>()
const allClubs = ref<Club[]>([])
const selectedClubId = ref<number | undefined>(undefined)
const flowList = ref<FundFlow[]>([])

const isAdmin = computed(() => userStore.user?.role?.roleCode === 'admin')

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true
})

const columns = [
  { title: '流水号', dataIndex: 'flowNo', key: 'flowNo' },
  { title: '类型', dataIndex: 'flowType', key: 'flowType', width: 100 },
  { title: '金额', key: 'amount', width: 140 },
  { title: '余额变动', key: 'balance', width: 220, customRender: ({ record }: any) => `${record.balanceBefore || 0} → ${record.balanceAfter || 0}` },
  { title: '说明', dataIndex: 'description', key: 'description' },
  { title: '时间', key: 'time', width: 180 }
]

const formatTime = (value?: string) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '-')

const loadClubId = async () => {
  const res = await clubApi.getList(0, 100)
  allClubs.value = res.data?.content || []
  if (allClubs.value.length > 0) {
    selectedClubId.value = allClubs.value[0].id
    clubId.value = allClubs.value[0].id
  }
}

const handleClubChange = (id: number) => {
  clubId.value = id
  pagination.current = 1
  fetchData()
}

const fetchData = async () => {
  if (!clubId.value) return
  loading.value = true
  try {
    const res = await fundApi.getFlowList({
      clubId: clubId.value,
      page: pagination.current - 1,
      size: pagination.pageSize
    })
    flowList.value = res.data.list || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!clubId.value) {
    message.warning('未找到社团信息')
    return
  }
  if (!amount.value || amount.value <= 0 || !description.value) {
    message.warning('请填写完整入账信息')
    return
  }
  submitting.value = true
  try {
    await fundApi.addIncome(clubId.value, amount.value, description.value)
    message.success('入账成功')
    amount.value = undefined
    description.value = ''
    fetchData()
  } catch (error: any) {
    message.error(error.message || '入账失败')
  } finally {
    submitting.value = false
  }
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchData()
}

onMounted(async () => {
  await loadClubId()
  fetchData()
})
</script>

<style scoped>
.table-card {
  margin-top: 16px;
}

.income {
  color: #237804;
}

.expense {
  color: #a8071a;
}
</style>
