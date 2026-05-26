<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">排课管理</h1>
      <p class="text-gray-600 mt-1">管理课程排期和教室安排</p>
    </div>

    <a-card class="mb-6">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-statistic title="今日课程" :value="todaySchedules" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="本周课程" :value="weekSchedules" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="可用教室" :value="availableRooms" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="待确认课程" :value="pendingSchedules" />
        </a-col>
      </a-row>
    </a-card>

    <a-card class="mb-6">
      <a-form layout="inline">
        <a-form-item label="课程名称">
          <a-input v-model:value="searchForm.courseName" placeholder="搜索课程" style="width: 200px" allow-clear />
        </a-form-item>
        <a-form-item label="授课教师">
          <a-select v-model:value="searchForm.teacherId" placeholder="选择教师" style="width: 150px" allow-clear>
            <a-select-option v-for="teacher in teachers" :key="teacher.id" :value="teacher.id">
              {{ teacher.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="日期范围">
          <a-range-picker v-model:value="searchForm.dateRange" />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="searchForm.status" placeholder="选择状态" style="width: 120px" allow-clear>
            <a-select-option value="SCHEDULED">已排课</a-select-option>
            <a-select-option value="ONGOING">进行中</a-select-option>
            <a-select-option value="COMPLETED">已完成</a-select-option>
            <a-select-option value="CANCELLED">已取消</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch">
              <template #icon><SearchOutlined /></template>
              搜索
            </a-button>
            <a-button @click="handleReset">重置</a-button>
            <a-button type="primary" @click="showAddModal">
              <template #icon><PlusOutlined /></template>
              新增排课
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card>
      <a-table
        :columns="columns"
        :data-source="schedules"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'course'">
            <div>
              <div class="font-bold">{{ record.courseName }}</div>
              <div class="text-sm text-gray-500">{{ record.courseType }}</div>
            </div>
          </template>
          <template v-if="column.key === 'time'">
            <div>
              <div>{{ record.date }}</div>
              <div class="text-sm text-gray-500">{{ record.startTime }} - {{ record.endTime }}</div>
            </div>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'attendance'">
            <a-progress :percent="record.attendanceRate" :size="'small'" />
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewDetail(record)">详情</a-button>
              <a-button type="link" size="small" @click="editSchedule(record)">编辑</a-button>
              <a-popconfirm
                title="确定要取消这个排课吗？"
                @confirm="cancelSchedule(record)"
                ok-text="确定"
                cancel-text="取消"
              >
                <a-button type="link" size="small" danger>取消</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="addModalVisible"
      :title="editingSchedule ? '编辑排课' : '新增排课'"
      width="600px"
      @ok="handleSubmit"
    >
      <a-form
        :model="scheduleForm"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="选择课程" required>
          <a-select
            v-model:value="scheduleForm.courseId"
            placeholder="请选择课程"
            show-search
            :filter-option="filterOption"
          >
            <a-select-option v-for="course in courses" :key="course.id" :value="course.id">
              {{ course.name }} - {{ course.teacherName }}
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
              {{ room.name }} (容量: {{ room.capacity }})
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="最大人数">
          <a-input-number v-model:value="scheduleForm.maxStudents" :min="1" style="width: 100%" />
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="scheduleForm.note" :rows="3" placeholder="请输入备注信息" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailModalVisible"
      title="排课详情"
      width="700px"
      :footer="null"
    >
      <a-descriptions :column="2" bordered v-if="currentSchedule">
        <a-descriptions-item label="课程名称">{{ currentSchedule.courseName }}</a-descriptions-item>
        <a-descriptions-item label="课程类型">{{ currentSchedule.courseType }}</a-descriptions-item>
        <a-descriptions-item label="授课教师">{{ currentSchedule.teacherName }}</a-descriptions-item>
        <a-descriptions-item label="教室">{{ currentSchedule.roomName }}</a-descriptions-item>
        <a-descriptions-item label="上课日期">{{ currentSchedule.date }}</a-descriptions-item>
        <a-descriptions-item label="上课时间">{{ currentSchedule.startTime }} - {{ currentSchedule.endTime }}</a-descriptions-item>
        <a-descriptions-item label="报名人数">{{ currentSchedule.enrolledCount }}/{{ currentSchedule.maxStudents }}</a-descriptions-item>
        <a-descriptions-item label="出勤率">
          <a-progress :percent="currentSchedule.attendanceRate" :size="'small'" />
        </a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="getStatusColor(currentSchedule.status)">
            {{ getStatusText(currentSchedule.status) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ currentSchedule.createdAt }}</a-descriptions-item>
        <a-descriptions-item label="备注" :span="2">{{ currentSchedule.note || '暂无备注' }}</a-descriptions-item>
      </a-descriptions>

      <a-divider>学员列表</a-divider>

      <a-table
        :columns="studentColumns"
        :data-source="currentSchedule?.students || []"
        :pagination="false"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'attendance'">
            <a-tag :color="record.attended ? 'green' : 'red'">
              {{ record.attended ? '已签到' : '未签到' }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { SearchOutlined, PlusOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

interface Schedule {
  id: number
  courseName: string
  courseType: string
  teacherName: string
  date: string
  startTime: string
  endTime: string
  roomName: string
  enrolledCount: number
  maxStudents: number
  attendanceRate: number
  status: string
  note: string
  createdAt: string
  students?: any[]
}

const searchForm = reactive({
  courseName: '',
  teacherId: undefined,
  dateRange: null,
  status: undefined
})

const scheduleForm = reactive({
  courseId: undefined,
  date: null,
  timeRange: null,
  roomId: undefined,
  maxStudents: 20,
  note: ''
})

const schedules = ref<Schedule[]>([])
const allSchedules = ref<Schedule[]>([])
const teachers = ref<any[]>([])
const courses = ref<any[]>([])
const rooms = ref<any[]>([])
const loading = ref(false)
const addModalVisible = ref(false)
const detailModalVisible = ref(false)
const editingSchedule = ref<Schedule | null>(null)
const currentSchedule = ref<Schedule | null>(null)

const todaySchedules = ref(8)
const weekSchedules = ref(32)
const availableRooms = ref(5)
const pendingSchedules = ref(3)

const columns = [
  { title: '课程信息', key: 'course' },
  { title: '授课教师', dataIndex: 'teacherName', key: 'teacherName' },
  { title: '上课时间', key: 'time' },
  { title: '教室', dataIndex: 'roomName', key: 'roomName' },
  { title: '报名人数', dataIndex: 'enrolledCount', key: 'enrolledCount' },
  { title: '出勤率', key: 'attendance' },
  { title: '状态', key: 'status' },
  { title: '操作', key: 'action', fixed: 'right' }
]

const studentColumns = [
  { title: '学员姓名', dataIndex: 'name', key: 'name' },
  { title: '联系电话', dataIndex: 'phone', key: 'phone' },
  { title: '签到状态', key: 'attendance' }
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

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

const filterOption = (input: string, option: any) => {
  return String(option?.children ?? '').toLowerCase().includes(input.toLowerCase())
}

const loadSchedules = async () => {
  loading.value = true
  try {
    const mockSchedules: Schedule[] = [
      {
        id: 1,
        courseName: 'Hip-Hop基础班',
        courseType: 'Hip-Hop',
        teacherName: '张老师',
        date: '2024-02-20',
        startTime: '18:00',
        endTime: '19:30',
        roomName: 'A教室',
        enrolledCount: 18,
        maxStudents: 20,
        attendanceRate: 90,
        status: 'SCHEDULED',
        note: '',
        createdAt: '2024-02-15 10:00:00',
        students: [
          { name: '学员1', phone: '138****0001', attended: true },
          { name: '学员2', phone: '138****0002', attended: false }
        ]
      },
      {
        id: 2,
        courseName: '爵士舞进阶班',
        courseType: '爵士',
        teacherName: '李老师',
        date: '2024-02-20',
        startTime: '19:30',
        endTime: '21:00',
        roomName: 'B教室',
        enrolledCount: 15,
        maxStudents: 18,
        attendanceRate: 85,
        status: 'ONGOING',
        note: '',
        createdAt: '2024-02-15 10:00:00'
      }
    ]
    if (!allSchedules.value.length) {
      allSchedules.value = mockSchedules
    }
    applyFilters()
  } catch (error) {
    message.error('加载排课列表失败')
  } finally {
    loading.value = false
  }
}

const applyFilters = () => {
  let list = [...allSchedules.value]
  if (searchForm.courseName) {
    list = list.filter(item => item.courseName.includes(searchForm.courseName))
  }
  if (searchForm.teacherId) {
    const teacher = teachers.value.find(item => item.id === searchForm.teacherId)
    if (teacher) {
      list = list.filter(item => item.teacherName === teacher.name)
    }
  }
  if (searchForm.status) {
    list = list.filter(item => item.status === searchForm.status)
  }
  schedules.value = list
  pagination.total = list.length
}

const loadTeachers = async () => {
  teachers.value = [
    { id: 1, name: '张老师' },
    { id: 2, name: '李老师' },
    { id: 3, name: '王老师' }
  ]
}

const loadCourses = async () => {
  courses.value = [
    { id: 1, name: 'Hip-Hop基础班', teacherName: '张老师' },
    { id: 2, name: '爵士舞进阶班', teacherName: '李老师' },
    { id: 3, name: 'Popping入门班', teacherName: '王老师' }
  ]
}

const loadRooms = async () => {
  rooms.value = [
    { id: 1, name: 'A教室', capacity: 30 },
    { id: 2, name: 'B教室', capacity: 25 },
    { id: 3, name: 'C教室', capacity: 20 }
  ]
}

const handleSearch = () => {
  pagination.current = 1
  applyFilters()
}

const handleReset = () => {
  Object.assign(searchForm, {
    courseName: '',
    teacherId: undefined,
    dateRange: null,
    status: undefined
  })
  applyFilters()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  applyFilters()
}

const showAddModal = () => {
  editingSchedule.value = null
  Object.assign(scheduleForm, {
    courseId: undefined,
    date: null,
    timeRange: null,
    roomId: undefined,
    maxStudents: 20,
    note: ''
  })
  addModalVisible.value = true
}

const editSchedule = (schedule: Schedule) => {
  editingSchedule.value = schedule
  const matchedCourse = courses.value.find(item => item.name === schedule.courseName)
  const matchedRoom = rooms.value.find(item => item.name === schedule.roomName)
  Object.assign(scheduleForm, {
    courseId: matchedCourse?.id,
    date: dayjs(schedule.date),
    timeRange: [dayjs(schedule.startTime, 'HH:mm'), dayjs(schedule.endTime, 'HH:mm')],
    roomId: matchedRoom?.id,
    maxStudents: schedule.maxStudents,
    note: schedule.note || ''
  })
  addModalVisible.value = true
}

const viewDetail = (schedule: Schedule) => {
  currentSchedule.value = schedule
  detailModalVisible.value = true
}

const cancelSchedule = (schedule: Schedule) => {
  const target = allSchedules.value.find(item => item.id === schedule.id)
  if (target) {
    target.status = 'CANCELLED'
  }
  message.success(`已取消排课：${schedule.courseName}`)
  applyFilters()
}

const handleSubmit = () => {
  if (!scheduleForm.courseId || !scheduleForm.date || !scheduleForm.timeRange || !scheduleForm.roomId) {
    message.warning('请填写完整的排课信息')
    return
  }
  const selectedCourse = courses.value.find(item => item.id === scheduleForm.courseId)
  const selectedRoom = rooms.value.find(item => item.id === scheduleForm.roomId)
  const selectedTeacher = teachers.value.find(item => item.name === selectedCourse?.teacherName)
  const dateText = dayjs(scheduleForm.date as any).format('YYYY-MM-DD')
  const timeRange = scheduleForm.timeRange as any[]
  const startTime = dayjs(timeRange[0]).format('HH:mm')
  const endTime = dayjs(timeRange[1]).format('HH:mm')
  if (!selectedCourse || !selectedRoom) {
    message.warning('课程或教室数据无效，请重新选择')
    return
  }
  if (editingSchedule.value) {
    const target = allSchedules.value.find(item => item.id === editingSchedule.value?.id)
    if (target) {
      target.courseName = selectedCourse.name
      target.courseType = selectedCourse.type || target.courseType
      target.teacherName = selectedTeacher?.name || selectedCourse.teacherName || target.teacherName
      target.date = dateText
      target.startTime = startTime
      target.endTime = endTime
      target.roomName = selectedRoom.name
      target.maxStudents = scheduleForm.maxStudents
      target.note = scheduleForm.note
      target.status = target.status === 'CANCELLED' ? 'SCHEDULED' : target.status
    }
    message.success('排课修改成功')
  } else {
    const nextId = allSchedules.value.length ? Math.max(...allSchedules.value.map(item => item.id)) + 1 : 1
    allSchedules.value.unshift({
      id: nextId,
      courseName: selectedCourse.name,
      courseType: selectedCourse.type || '综合',
      teacherName: selectedTeacher?.name || selectedCourse.teacherName || '待分配',
      date: dateText,
      startTime,
      endTime,
      roomName: selectedRoom.name,
      enrolledCount: 0,
      maxStudents: scheduleForm.maxStudents,
      attendanceRate: 0,
      status: 'SCHEDULED',
      note: scheduleForm.note,
      createdAt: dayjs().format('YYYY-MM-DD HH:mm:ss'),
      students: []
    })
    message.success('排课创建成功')
  }
  addModalVisible.value = false
  applyFilters()
}

onMounted(() => {
  loadSchedules()
  loadTeachers()
  loadCourses()
  loadRooms()
})
</script>
