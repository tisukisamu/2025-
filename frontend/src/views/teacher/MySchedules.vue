<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-gray-800">我的排班</h1>
      <p class="text-gray-500 mt-1">查看和管理您的课程排班</p>
    </div>

    <a-card class="mb-4">
      <a-form layout="inline">
        <a-form-item label="时间范围">
          <a-range-picker v-model:value="dateRange" style="width: 300px" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            搜索
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-row :gutter="16">
      <a-col :xs="24" :lg="16" class="mb-4">
        <a-card title="本周排班">
          <a-calendar :fullscreen="false" @select="onSelect">
            <template #dateFullCellRender="{ current }">
              <div class="ant-picker-calendar-date">
                <div class="ant-picker-calendar-date-value">
                  {{ current.date() }}
                </div>
                <div v-if="hasSchedule(current)" class="schedule-dot"></div>
              </div>
            </template>
          </a-calendar>
        </a-card>
      </a-col>
      
      <a-col :xs="24" :lg="8" class="mb-4">
        <a-card title="今日课程">
          <div v-if="todaySchedules.length === 0" class="text-center py-8 text-gray-400">
            今日暂无课程安排
          </div>
          <div v-else class="space-y-3">
            <div
              v-for="schedule in todaySchedules"
              :key="schedule.id"
              class="p-3 bg-gray-50 rounded"
            >
              <div class="flex justify-between items-start mb-2">
                <p class="font-medium text-gray-800">{{ schedule.course?.name }}</p>
                <a-tag :color="getScheduleStatusColor(schedule.status)">
                  {{ getScheduleStatusText(schedule.status) }}
                </a-tag>
              </div>
              <div class="text-sm text-gray-600">
                <div class="flex items-center mb-1">
                  <ClockCircleOutlined class="mr-1" />
                  {{ formatTime(schedule.startTime) }} - {{ formatTime(schedule.endTime) }}
                </div>
                <div class="flex items-center">
                  <EnvironmentOutlined class="mr-1" />
                  {{ schedule.location || '待定' }} {{ schedule.room }}
                </div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-card title="排班列表">
      <a-table
        :columns="columns"
        :data-source="schedules"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'time'">
            <div>
              <div>{{ formatDate(record.startTime) }}</div>
              <div class="text-sm text-gray-500">
                {{ formatTime(record.startTime) }} - {{ formatTime(record.endTime) }}
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getScheduleStatusColor(record.status)">
              {{ getScheduleStatusText(record.status) }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ClockCircleOutlined, EnvironmentOutlined } from '@ant-design/icons-vue'
import type { Schedule } from '@/types'
import dayjs, { Dayjs } from 'dayjs'

const loading = ref(false)
const schedules = ref<Schedule[]>([])
const todaySchedules = ref<Schedule[]>([])
const dateRange = ref([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total: number) => `共 ${total} 条`
})

const columns = [
  { title: '课程', dataIndex: ['course', 'name'], key: 'course' },
  { title: '时间', key: 'time' },
  { title: '地点', dataIndex: 'location', key: 'location' },
  { title: '状态', dataIndex: 'status', key: 'status' }
]

const getScheduleStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    SCHEDULED: 'processing',
    CANCELLED: 'error',
    COMPLETED: 'success'
  }
  return colors[status] || 'default'
}

const getScheduleStatusText = (status: string) => {
  const texts: Record<string, string> = {
    SCHEDULED: '已安排',
    CANCELLED: '已取消',
    COMPLETED: '已完成'
  }
  return texts[status] || status
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD')
}

const formatTime = (date: string) => {
  return dayjs(date).format('HH:mm')
}

const hasSchedule = (date: Dayjs) => {
  return schedules.value.some(s => dayjs(s.startTime).isSame(date, 'day'))
}

const onSelect = (date: Dayjs) => {
  console.log('Selected date:', date.format('YYYY-MM-DD'))
}

const handleSearch = () => {
  pagination.current = 1
  loadSchedules()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadSchedules()
}

const loadSchedules = async () => {
  loading.value = true
  try {
    // 模拟数据
    schedules.value = [
      {
        id: 1,
        course: { id: 1, name: '街舞基础班' },
        startTime: '2024-01-15 14:00:00',
        endTime: '2024-01-15 15:30:00',
        location: 'A教室',
        room: '101',
        status: 'SCHEDULED'
      }
    ]
    pagination.total = 1
  } catch (error) {
    console.error('加载排班失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadSchedules()
})
</script>

<style scoped>
.schedule-dot {
  width: 6px;
  height: 6px;
  background-color: #1890ff;
  border-radius: 50%;
  margin: 2px auto 0;
}
</style>
