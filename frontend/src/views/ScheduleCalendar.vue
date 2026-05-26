<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6 flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">排课日历</h1>
        <p class="text-gray-600 mt-1">可视化查看和管理课程排期</p>
      </div>
      <a-space>
        <a-radio-group v-model:value="viewMode" button-style="solid" @change="handleViewModeChange">
          <a-radio-button value="month">月视图</a-radio-button>
          <a-radio-button value="week">周视图</a-radio-button>
          <a-radio-button value="day">日视图</a-radio-button>
        </a-radio-group>
        <a-button type="primary" @click="showAddModal">
          <template #icon><PlusOutlined /></template>
          新增排课
        </a-button>
      </a-space>
    </div>

    <a-row :gutter="16" class="mb-6">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card>
          <a-statistic title="今日课程" :value="todaySchedules" suffix="节">
            <template #prefix>
              <CalendarOutlined style="color: #1890ff" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card>
          <a-statistic title="本周课程" :value="weekSchedules" suffix="节">
            <template #prefix>
              <ScheduleOutlined style="color: #52c41a" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card>
          <a-statistic title="可用教室" :value="availableRooms" suffix="间">
            <template #prefix>
              <HomeOutlined style="color: #faad14" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card>
          <a-statistic title="待确认课程" :value="pendingSchedules" suffix="节">
            <template #prefix>
              <ClockCircleOutlined style="color: #ff4d4f" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <a-card class="mb-6">
      <a-form layout="inline">
        <a-form-item label="课程名称">
          <a-input v-model:value="filterForm.keyword" placeholder="搜索课程" style="width: 200px" allow-clear />
        </a-form-item>
        <a-form-item label="授课教师">
          <a-select v-model:value="filterForm.teacherId" placeholder="选择教师" style="width: 150px" allow-clear>
            <a-select-option v-for="teacher in teachers" :key="teacher.id" :value="teacher.id">
              {{ teacher.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="教室">
          <a-select v-model:value="filterForm.roomId" placeholder="选择教室" style="width: 150px" allow-clear>
            <a-select-option v-for="room in rooms" :key="room.id" :value="room.id">
              {{ room.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="课程类型">
          <a-select v-model:value="filterForm.courseType" placeholder="选择类型" style="width: 150px" allow-clear>
            <a-select-option value="HIPHOP">Hip-Hop</a-select-option>
            <a-select-option value="JAZZ">爵士</a-select-option>
            <a-select-option value="BREAKING">Breaking</a-select-option>
            <a-select-option value="POPPING">Popping</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleFilter">筛选</a-button>
            <a-button @click="handleResetFilter">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card>
      <div class="calendar-header">
        <a-space>
          <a-button @click="goToToday">今天</a-button>
          <a-button-group>
            <a-button @click="goToPrev">
              <LeftOutlined />
            </a-button>
            <a-button @click="goToNext">
              <RightOutlined />
            </a-button>
          </a-button-group>
          <span class="text-xl font-bold">{{ currentPeriod }}</span>
        </a-space>
      </div>

      <div v-if="viewMode === 'month'" class="calendar-month">
        <div class="calendar-weekdays">
          <div v-for="day in weekdays" :key="day" class="weekday">{{ day }}</div>
        </div>
        <div class="calendar-days">
          <div
            v-for="(day, index) in calendarDays"
            :key="index"
            :class="['calendar-day', { 'other-month': !day.currentMonth, 'today': day.isToday }]"
            @click="handleDayClick(day)"
          >
            <div class="day-number">{{ day.date }}</div>
            <div class="day-schedules">
              <div
                v-for="schedule in day.schedules"
                :key="schedule.id"
                :class="['schedule-item', `schedule-${schedule.status}`]"
                @click.stop="viewScheduleDetail(schedule)"
              >
                <div class="schedule-time">{{ schedule.time }}</div>
                <div class="schedule-name">{{ schedule.courseName }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="viewMode === 'week'" class="calendar-week">
        <div class="week-header">
          <div class="time-column"></div>
          <div v-for="day in weekDays" :key="day.date" class="day-column-header">
            <div class="weekday-name">{{ day.weekday }}</div>
            <div :class="['date-number', { 'today': day.isToday }]">{{ day.date }}</div>
          </div>
        </div>
        <div class="week-body">
          <div class="time-slots">
            <div v-for="hour in hours" :key="hour" class="time-slot">
              {{ hour }}:00
            </div>
          </div>
          <div class="day-columns">
            <div v-for="day in weekDays" :key="day.date" class="day-column">
              <div
                v-for="schedule in day.schedules"
                :key="schedule.id"
                :class="['week-schedule-item', `schedule-${schedule.status}`]"
                :style="getScheduleStyle(schedule)"
                @click="viewScheduleDetail(schedule)"
              >
                <div class="schedule-title">{{ schedule.courseName }}</div>
                <div class="schedule-info">{{ schedule.time }} | {{ schedule.room }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="viewMode === 'day'" class="calendar-day-view">
        <div class="day-view-header">
          <div class="current-date">{{ currentDateDisplay }}</div>
        </div>
        <div class="day-view-body">
          <div class="time-slots">
            <div v-for="hour in hours" :key="hour" class="time-slot">
              {{ hour }}:00
            </div>
          </div>
          <div class="day-schedule-list">
            <div
              v-for="schedule in daySchedules"
              :key="schedule.id"
              :class="['day-schedule-item', `schedule-${schedule.status}`]"
              :style="getScheduleStyle(schedule)"
              @click="viewScheduleDetail(schedule)"
            >
              <div class="schedule-time">{{ schedule.time }}</div>
              <div class="schedule-content">
                <div class="schedule-title">{{ schedule.courseName }}</div>
                <div class="schedule-info">
                  <span>{{ schedule.teacherName }}</span>
                  <a-divider type="vertical" />
                  <span>{{ schedule.room }}</span>
                  <a-divider type="vertical" />
                  <span>{{ schedule.enrolledCount }}/{{ schedule.maxStudents }}人</span>
                </div>
              </div>
              <a-tag :color="getStatusColor(schedule.status)">
                {{ getStatusText(schedule.status) }}
              </a-tag>
            </div>
          </div>
        </div>
      </div>
    </a-card>

    <a-modal
      v-model:open="addModalVisible"
      title="新增排课"
      width="600px"
      @ok="handleSubmit"
    >
      <a-form
        :model="scheduleForm"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="选择课程" required>
          <a-select v-model:value="scheduleForm.courseId" placeholder="请选择课程">
            <a-select-option v-for="course in courses" :key="course.id" :value="course.id">
              {{ course.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="上课日期" required>
          <a-date-picker v-model:value="scheduleForm.date" style="width: 100%" />
        </a-form-item>
        <a-form-item label="上课时间" required>
          <a-time-range-picker v-model:value="scheduleForm.timeRange" style="width: 100%" format="HH:mm" />
        </a-form-item>
        <a-form-item label="教室" required>
          <a-select v-model:value="scheduleForm.roomId" placeholder="请选择教室">
            <a-select-option v-for="room in rooms" :key="room.id" :value="room.id">
              {{ room.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailModalVisible"
      title="课程详情"
      width="600px"
      :footer="null"
    >
      <a-descriptions :column="2" bordered v-if="currentSchedule">
        <a-descriptions-item label="课程名称">{{ currentSchedule.courseName }}</a-descriptions-item>
        <a-descriptions-item label="授课教师">{{ currentSchedule.teacherName }}</a-descriptions-item>
        <a-descriptions-item label="上课时间">{{ currentSchedule.time }}</a-descriptions-item>
        <a-descriptions-item label="教室">{{ currentSchedule.room }}</a-descriptions-item>
        <a-descriptions-item label="报名人数">{{ currentSchedule.enrolledCount }}/{{ currentSchedule.maxStudents }}</a-descriptions-item>
        <a-descriptions-item label="课程状态">
          <a-tag :color="getStatusColor(currentSchedule.status)">
            {{ getStatusText(currentSchedule.status) }}
          </a-tag>
        </a-descriptions-item>
      </a-descriptions>
      <div class="mt-4 text-right">
        <a-space>
          <a-button @click="editSchedule">编辑</a-button>
          <a-button type="primary" @click="viewCourseDetail">查看课程</a-button>
        </a-space>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { 
  PlusOutlined, 
  LeftOutlined, 
  RightOutlined,
  CalendarOutlined,
  ScheduleOutlined,
  HomeOutlined,
  ClockCircleOutlined
} from '@ant-design/icons-vue'
import dayjs, { Dayjs } from 'dayjs'

const viewMode = ref('month')
const currentDate = ref(dayjs())
const addModalVisible = ref(false)
const detailModalVisible = ref(false)
const currentSchedule = ref<any>(null)

const todaySchedules = ref(8)
const weekSchedules = ref(32)
const availableRooms = ref(5)
const pendingSchedules = ref(3)

const filterForm = reactive({
  keyword: '',
  teacherId: undefined,
  roomId: undefined,
  courseType: undefined
})

const scheduleForm = reactive({
  courseId: undefined,
  date: null,
  timeRange: null,
  roomId: undefined
})

const teachers = ref([
  { id: 1, name: '张老师' },
  { id: 2, name: '李老师' },
  { id: 3, name: '王老师' }
])

const rooms = ref([
  { id: 1, name: 'A教室' },
  { id: 2, name: 'B教室' },
  { id: 3, name: 'C教室' }
])

const courses = ref([
  { id: 1, name: 'Hip-Hop基础班' },
  { id: 2, name: '爵士舞进阶班' },
  { id: 3, name: 'Popping入门班' }
])

const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
const hours = Array.from({ length: 15 }, (_, i) => i + 8)

const currentPeriod = computed(() => {
  if (viewMode.value === 'month') {
    return currentDate.value.format('YYYY年MM月')
  } else if (viewMode.value === 'week') {
    const start = currentDate.value.startOf('week')
    const end = currentDate.value.endOf('week')
    return `${start.format('MM月DD日')} - ${end.format('MM月DD日')}`
  } else {
    return currentDate.value.format('YYYY年MM月DD日')
  }
})

const currentDateDisplay = computed(() => {
  return currentDate.value.format('YYYY年MM月DD日 dddd')
})

const calendarDays = computed(() => {
  const start = currentDate.value.startOf('month').startOf('week')
  const end = currentDate.value.endOf('month').endOf('week')
  const days = []
  
  let current = start
  while (current.isBefore(end) || current.isSame(end, 'day')) {
    const daySchedules = getSchedulesForDate(current)
    days.push({
      date: current.date(),
      fullDate: current.format('YYYY-MM-DD'),
      currentMonth: current.month() === currentDate.value.month(),
      isToday: current.isSame(dayjs(), 'day'),
      schedules: daySchedules
    })
    current = current.add(1, 'day')
  }
  
  return days
})

const weekDays = computed(() => {
  const start = currentDate.value.startOf('week')
  const days = []
  
  for (let i = 0; i < 7; i++) {
    const current = start.add(i, 'day')
    days.push({
      date: current.date(),
      fullDate: current.format('YYYY-MM-DD'),
      weekday: weekdays[current.day()],
      isToday: current.isSame(dayjs(), 'day'),
      schedules: getSchedulesForDate(current)
    })
  }
  
  return days
})

const daySchedules = computed(() => {
  return getSchedulesForDate(currentDate.value)
})

const getSchedulesForDate = (date: Dayjs) => {
  const mockSchedules = [
    {
      id: 1,
      courseName: 'Hip-Hop基础班',
      teacherName: '张老师',
      time: '18:00-19:30',
      room: 'A教室',
      enrolledCount: 18,
      maxStudents: 20,
      status: 'SCHEDULED'
    },
    {
      id: 2,
      courseName: '爵士舞进阶班',
      teacherName: '李老师',
      time: '19:30-21:00',
      room: 'B教室',
      enrolledCount: 15,
      maxStudents: 18,
      status: 'ONGOING'
    }
  ]
  
  if (date.date() === 20 || date.date() === 21 || date.date() === 22) {
    return mockSchedules
  }
  
  return []
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    SCHEDULED: 'blue',
    ONGOING: 'green',
    COMPLETED: 'default',
    CANCELLED: 'red'
  }
  return colorMap[status] || 'default'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    SCHEDULED: '已排课',
    ONGOING: '进行中',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return textMap[status] || status
}

const getScheduleStyle = (schedule: any) => {
  const [startTime] = schedule.time.split('-')
  const [hour] = startTime.split(':')
  const top = (parseInt(hour) - 8) * 60
  return {
    top: `${top}px`,
    height: '90px'
  }
}

const handleViewModeChange = () => {
  console.log('View mode changed to:', viewMode.value)
}

const goToToday = () => {
  currentDate.value = dayjs()
}

const goToPrev = () => {
  if (viewMode.value === 'month') {
    currentDate.value = currentDate.value.subtract(1, 'month')
  } else if (viewMode.value === 'week') {
    currentDate.value = currentDate.value.subtract(1, 'week')
  } else {
    currentDate.value = currentDate.value.subtract(1, 'day')
  }
}

const goToNext = () => {
  if (viewMode.value === 'month') {
    currentDate.value = currentDate.value.add(1, 'month')
  } else if (viewMode.value === 'week') {
    currentDate.value = currentDate.value.add(1, 'week')
  } else {
    currentDate.value = currentDate.value.add(1, 'day')
  }
}

const handleDayClick = (day: any) => {
  currentDate.value = dayjs(day.fullDate)
  viewMode.value = 'day'
}

const handleFilter = () => {
  message.info('筛选功能')
}

const handleResetFilter = () => {
  Object.assign(filterForm, {
    keyword: '',
    teacherId: undefined,
    roomId: undefined,
    courseType: undefined
  })
}

const showAddModal = () => {
  addModalVisible.value = true
}

const viewScheduleDetail = (schedule: any) => {
  currentSchedule.value = schedule
  detailModalVisible.value = true
}

const editSchedule = () => {
  message.info('编辑排课')
}

const viewCourseDetail = () => {
  message.info('查看课程详情')
}

const handleSubmit = () => {
  if (!scheduleForm.courseId || !scheduleForm.date || !scheduleForm.timeRange || !scheduleForm.roomId) {
    message.warning('请填写完整的排课信息')
    return
  }
  message.success('排课创建成功')
  addModalVisible.value = false
}

onMounted(() => {
  console.log('ScheduleCalendar mounted')
})
</script>

<style scoped>
.calendar-header {
  margin-bottom: 16px;
}

.calendar-month {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;
}

.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.weekday {
  padding: 12px;
  text-align: center;
  font-weight: 600;
  color: #1f2937;
}

.calendar-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
}

.calendar-day {
  min-height: 120px;
  padding: 8px;
  border-right: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.3s;
}

.calendar-day:hover {
  background: #fafafa;
}

.calendar-day.other-month {
  background: #fafafa;
  color: #d9d9d9;
}

.calendar-day.today {
  background: #e6f7ff;
}

.day-number {
  font-weight: 600;
  margin-bottom: 4px;
}

.day-schedules {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.schedule-item {
  padding: 2px 4px;
  border-radius: 2px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.schedule-item:hover {
  transform: translateX(2px);
}

.schedule-SCHEDULED {
  background: #e6f7ff;
  color: #1890ff;
}

.schedule-ONGOING {
  background: #f6ffed;
  color: #52c41a;
}

.schedule-COMPLETED {
  background: #f5f5f5;
  color: #8c8c8c;
}

.schedule-CANCELLED {
  background: #fff2f0;
  color: #ff4d4f;
}

.schedule-time {
  font-size: 11px;
  opacity: 0.8;
}

.schedule-name {
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.calendar-week {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;
}

.week-header {
  display: grid;
  grid-template-columns: 60px repeat(7, 1fr);
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.time-column {
  padding: 12px 8px;
  text-align: center;
}

.day-column-header {
  padding: 12px;
  text-align: center;
  border-left: 1px solid #f0f0f0;
}

.weekday-name {
  font-size: 12px;
  color: #6b7280;
}

.date-number {
  font-size: 18px;
  font-weight: 600;
  margin-top: 4px;
}

.date-number.today {
  color: #1890ff;
}

.week-body {
  display: grid;
  grid-template-columns: 60px 1fr;
  position: relative;
}

.time-slots {
  border-right: 1px solid #f0f0f0;
}

.time-slot {
  height: 60px;
  padding: 4px 8px;
  font-size: 12px;
  color: #6b7280;
  text-align: right;
  border-bottom: 1px solid #f0f0f0;
}

.day-columns {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  position: relative;
}

.day-column {
  border-right: 1px solid #f0f0f0;
  position: relative;
  min-height: 900px;
}

.week-schedule-item {
  position: absolute;
  left: 4px;
  right: 4px;
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  overflow: hidden;
}

.week-schedule-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.schedule-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.schedule-info {
  font-size: 12px;
  opacity: 0.8;
}

.calendar-day-view {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;
}

.day-view-header {
  padding: 16px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.current-date {
  font-size: 18px;
  font-weight: 600;
}

.day-view-body {
  display: grid;
  grid-template-columns: 60px 1fr;
}

.day-schedule-list {
  position: relative;
  min-height: 900px;
  padding: 8px;
}

.day-schedule-item {
  position: absolute;
  left: 8px;
  right: 8px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.day-schedule-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transform: translateX(4px);
}

.day-schedule-item .schedule-time {
  font-size: 14px;
  font-weight: 600;
  width: 100px;
}

.day-schedule-item .schedule-content {
  flex: 1;
  margin: 0 16px;
}

.day-schedule-item .schedule-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.day-schedule-item .schedule-info {
  font-size: 13px;
  opacity: 0.8;
}
</style>
