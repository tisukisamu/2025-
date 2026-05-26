<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">财务查看</h1>
      <p class="page-subtitle">查看社团财务公开信息</p>
    </div>

    <div class="card">
      <a-form layout="inline" class="filter-form">
        <a-form-item label="社团">
          <a-select
            v-model:value="selectedClubId"
            style="width: 200px"
            placeholder="选择社团"
            @change="fetchData"
          >
            <a-select-option v-for="club in clubs" :key="club.id" :value="club.id">
              {{ club.clubName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="时间范围">
          <a-range-picker
            v-model:value="dateRange"
            @change="fetchData"
          />
        </a-form-item>
      </a-form>
    </div>

    <a-row :gutter="24" style="margin-top: 24px">
      <a-col :span="8">
        <div class="stat-card">
          <div class="stat-label">账户余额</div>
          <div class="stat-value">¥{{ balance.toFixed(2) }}</div>
        </div>
      </a-col>
      <a-col :span="8">
        <div class="stat-card">
          <div class="stat-label">本期收入</div>
          <div class="stat-value income">+¥{{ totalIncome.toFixed(2) }}</div>
        </div>
      </a-col>
      <a-col :span="8">
        <div class="stat-card">
          <div class="stat-label">本期支出</div>
          <div class="stat-value expense">-¥{{ totalExpense.toFixed(2) }}</div>
        </div>
      </a-col>
    </a-row>

    <div class="card" style="margin-top: 24px">
      <div class="card-header">
        <h3>资金流水</h3>
      </div>
      <a-table
        :dataSource="flowList"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'flowType'">
            <a-tag :color="record.flowType === 'INCOME' ? 'success' : 'error'">
              {{ record.flowType === 'INCOME' ? '收入' : '支出' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'amount'">
            <span :class="record.flowType === 'INCOME' ? 'income' : 'expense'">
              {{ record.flowType === 'INCOME' ? '+' : '-' }}¥{{ record.amount.toFixed(2) }}
            </span>
          </template>
          <template v-if="column.key === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { clubApi, fundApi } from '@/api'
import type { Club, FundFlow } from '@/types'
import dayjs, { Dayjs } from 'dayjs'

const clubs = ref<Club[]>([])
const selectedClubId = ref<number>()
const dateRange = ref<[Dayjs, Dayjs]>()
const flowList = ref<FundFlow[]>([])
const loading = ref(false)
const balance = ref(0)
const totalIncome = ref(0)
const totalExpense = ref(0)

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

const columns = [
  { title: '流水号', dataIndex: 'flowNo', key: 'flowNo' },
  { title: '类型', dataIndex: 'flowType', key: 'flowType' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '余额', dataIndex: 'balanceAfter', key: 'balanceAfter' },
  { title: '描述', dataIndex: 'description', key: 'description' },
  { title: '时间', dataIndex: 'createTime', key: 'createTime' }
]

const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const fetchClubs = async () => {
  try {
    const res = await clubApi.getMyClubs()
    clubs.value = res.data || []
    if (clubs.value.length > 0) {
      selectedClubId.value = clubs.value[0].id
      fetchData()
    }
  } catch (error) {
    console.error('获取社团列表失败', error)
  }
}

const fetchData = async () => {
  if (!selectedClubId.value) return
  
  loading.value = true
  try {
    const params: any = {
      clubId: selectedClubId.value,
      page: pagination.value.current - 1,
      size: pagination.value.pageSize
    }

    if (dateRange.value) {
      params.startTime = dateRange.value[0].format('YYYY-MM-DD HH:mm:ss')
      params.endTime = dateRange.value[1].format('YYYY-MM-DD HH:mm:ss')
    }

    const res = await fundApi.getFlowList(params)
    flowList.value = res.data.list || res.data.content || []
    pagination.value.total = res.data.total || res.data.totalElements || 0

    const club = clubs.value.find(c => c.id === selectedClubId.value)
    if (club) {
      balance.value = club.balance
    }

    let income = 0
    let expense = 0
    flowList.value.forEach(item => {
      if (item.flowType === 'INCOME') {
        income += item.amount
      } else {
        expense += item.amount
      }
    })
    totalIncome.value = income
    totalExpense.value = expense
  } catch (error) {
    console.error('获取数据失败', error)
  } finally {
    loading.value = false
  }
}

const handleTableChange = (pag: any) => {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchData()
}

onMounted(() => {
  fetchClubs()
})
</script>

<style scoped>
.filter-form {
  margin-bottom: 0;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  border: 1px solid #f0f0f0;
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
}

.stat-value.income {
  color: #52c41a;
}

.stat-value.expense {
  color: #f5222d;
}

.income {
  color: #52c41a;
  font-weight: 600;
}

.expense {
  color: #f5222d;
  font-weight: 600;
}
</style>
