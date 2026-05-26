<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">报名管理</h1>
      <p class="text-gray-600 mt-1">管理学员报名和课程分配</p>
    </div>

    <a-card class="mb-6">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-statistic title="今日报名" :value="todayEnrollments" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="本月报名" :value="monthEnrollments" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="待审核" :value="pendingEnrollments" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="总报名数" :value="totalEnrollments" />
        </a-col>
      </a-row>
    </a-card>

    <a-card class="mb-6">
      <a-form layout="inline">
        <a-form-item label="学员姓名">
          <a-input v-model:value="searchForm.studentName" placeholder="搜索学员" style="width: 200px" allow-clear />
        </a-form-item>
        <a-form-item label="课程名称">
          <a-select v-model:value="searchForm.courseId" placeholder="选择课程" style="width: 200px" allow-clear>
            <a-select-option v-for="course in courses" :key="course.id" :value="course.id">
              {{ course.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="报名状态">
          <a-select v-model:value="searchForm.status" placeholder="选择状态" style="width: 120px" allow-clear>
            <a-select-option value="PENDING">待审核</a-select-option>
            <a-select-option value="APPROVED">已通过</a-select-option>
            <a-select-option value="REJECTED">已拒绝</a-select-option>
            <a-select-option value="CANCELLED">已取消</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="报名时间">
          <a-range-picker v-model:value="searchForm.dateRange" />
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
              新增报名
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card>
      <a-table
        :columns="columns"
        :data-source="enrollments"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'student'">
            <div>
              <div class="font-bold">{{ record.studentName }}</div>
              <div class="text-sm text-gray-500">{{ record.studentPhone }}</div>
            </div>
          </template>
          <template v-if="column.key === 'course'">
            <div>
              <div class="font-bold">{{ record.courseName }}</div>
              <div class="text-sm text-gray-500">{{ record.courseType }}</div>
            </div>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'payment'">
            <div>
              <div class="font-bold">¥{{ record.amount }}</div>
              <a-tag :color="record.paid ? 'green' : 'orange'" size="small">
                {{ record.paid ? '已支付' : '待支付' }}
              </a-tag>
            </div>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewDetail(record)">详情</a-button>
              <a-button type="link" size="small" @click="editEnrollment(record)">编辑</a-button>
              <a-button 
                type="link" 
                size="small" 
                v-if="record.status === 'PENDING'"
                @click="approveEnrollment(record)"
              >
                通过
              </a-button>
              <a-button 
                type="link" 
                size="small" 
                danger
                v-if="record.status === 'PENDING'"
                @click="rejectEnrollment(record)"
              >
                拒绝
              </a-button>
              <a-popconfirm
                title="确定要取消这个报名吗？"
                @confirm="cancelEnrollment(record)"
                ok-text="确定"
                cancel-text="取消"
                v-if="record.status === 'APPROVED'"
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
      :title="editingEnrollment ? '编辑报名' : '新增报名'"
      width="600px"
      @ok="handleSubmit"
    >
      <a-form
        :model="enrollmentForm"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="选择学员" required>
          <a-select
            v-model:value="enrollmentForm.studentId"
            placeholder="请选择学员"
            show-search
            :filter-option="filterOption"
          >
            <a-select-option v-for="student in students" :key="student.id" :value="student.id">
              {{ student.name }} - {{ student.phone }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="选择课程" required>
          <a-select v-model:value="enrollmentForm.courseId" placeholder="请选择课程">
            <a-select-option v-for="course in courses" :key="course.id" :value="course.id">
              {{ course.name }} (¥{{ course.price }})
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="报名费用">
          <a-input-number
            v-model:value="enrollmentForm.amount"
            :min="0"
            style="width: 100%"
            placeholder="自动获取课程价格"
          />
        </a-form-item>
        <a-form-item label="支付状态">
          <a-radio-group v-model:value="enrollmentForm.paid">
            <a-radio :value="true">已支付</a-radio>
            <a-radio :value="false">待支付</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="enrollmentForm.note" :rows="3" placeholder="请输入备注信息" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailModalVisible"
      title="报名详情"
      width="700px"
      :footer="null"
    >
      <a-descriptions :column="2" bordered v-if="currentEnrollment">
        <a-descriptions-item label="学员姓名">{{ currentEnrollment.studentName }}</a-descriptions-item>
        <a-descriptions-item label="联系电话">{{ currentEnrollment.studentPhone }}</a-descriptions-item>
        <a-descriptions-item label="课程名称">{{ currentEnrollment.courseName }}</a-descriptions-item>
        <a-descriptions-item label="课程类型">{{ currentEnrollment.courseType }}</a-descriptions-item>
        <a-descriptions-item label="授课教师">{{ currentEnrollment.teacherName }}</a-descriptions-item>
        <a-descriptions-item label="课程价格">¥{{ currentEnrollment.coursePrice }}</a-descriptions-item>
        <a-descriptions-item label="报名费用">¥{{ currentEnrollment.amount }}</a-descriptions-item>
        <a-descriptions-item label="支付状态">
          <a-tag :color="currentEnrollment.paid ? 'green' : 'orange'">
            {{ currentEnrollment.paid ? '已支付' : '待支付' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="报名状态">
          <a-tag :color="getStatusColor(currentEnrollment.status)">
            {{ getStatusText(currentEnrollment.status) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="报名时间">{{ currentEnrollment.enrolledAt }}</a-descriptions-item>
        <a-descriptions-item label="备注" :span="2">{{ currentEnrollment.note || '暂无备注' }}</a-descriptions-item>
      </a-descriptions>

      <a-divider>学习进度</a-divider>

      <a-row :gutter="16">
        <a-col :span="8">
          <a-statistic title="已完成课时" :value="currentEnrollment.completedLessons" suffix="/ {{ currentEnrollment.totalLessons }}" />
        </a-col>
        <a-col :span="8">
          <a-statistic title="出勤率" :value="currentEnrollment.attendanceRate" suffix="%" />
        </a-col>
        <a-col :span="8">
          <a-statistic title="作业完成率" :value="currentEnrollment.homeworkRate" suffix="%" />
        </a-col>
      </a-row>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { SearchOutlined, PlusOutlined } from '@ant-design/icons-vue'

interface Enrollment {
  id: number
  studentId: number
  courseId: number
  studentName: string
  studentPhone: string
  courseName: string
  courseType: string
  teacherName: string
  coursePrice: number
  amount: number
  paid: boolean
  status: string
  enrolledAt: string
  note: string
  completedLessons: number
  totalLessons: number
  attendanceRate: number
  homeworkRate: number
}

const searchForm = reactive({
  studentName: '',
  courseId: undefined,
  status: undefined,
  dateRange: null
})

const enrollmentForm = reactive({
  id: undefined as number | undefined,
  studentId: undefined,
  courseId: undefined,
  amount: 0,
  paid: false,
  note: ''
})

const enrollments = ref<Enrollment[]>([])
const allEnrollments = ref<Enrollment[]>([])
const students = ref<any[]>([])
const courses = ref<any[]>([])
const loading = ref(false)
const addModalVisible = ref(false)
const detailModalVisible = ref(false)
const editingEnrollment = ref<Enrollment | null>(null)
const currentEnrollment = ref<Enrollment | null>(null)

const todayEnrollments = ref(5)
const monthEnrollments = ref(45)
const pendingEnrollments = ref(8)
const totalEnrollments = ref(320)

const columns = [
  { title: '学员信息', key: 'student' },
  { title: '课程信息', key: 'course' },
  { title: '授课教师', dataIndex: 'teacherName', key: 'teacherName' },
  { title: '费用信息', key: 'payment' },
  { title: '报名时间', dataIndex: 'enrolledAt', key: 'enrolledAt' },
  { title: '状态', key: 'status' },
  { title: '操作', key: 'action', fixed: 'right', width: 200 }
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
    PENDING: 'orange',
    APPROVED: 'green',
    REJECTED: 'red',
    CANCELLED: 'default'
  }
  return colorMap[status] || 'default'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已拒绝',
    CANCELLED: '已取消'
  }
  return textMap[status] || status
}

const filterOption = (input: string, option: any) => {
  return String(option?.children ?? '').toLowerCase().includes(input.toLowerCase())
}

const loadEnrollments = async () => {
  loading.value = true
  try {
    const mockEnrollments: Enrollment[] = [
      {
        id: 1,
        studentId: 1,
        courseId: 1,
        studentName: '张三',
        studentPhone: '138****0001',
        courseName: 'Hip-Hop基础班',
        courseType: 'Hip-Hop',
        teacherName: '张老师',
        coursePrice: 1200,
        amount: 1200,
        paid: true,
        status: 'APPROVED',
        enrolledAt: '2024-02-15 10:30:00',
        note: '',
        completedLessons: 16,
        totalLessons: 24,
        attendanceRate: 90,
        homeworkRate: 85
      },
      {
        id: 2,
        studentId: 2,
        courseId: 2,
        studentName: '李四',
        studentPhone: '138****0002',
        courseName: '爵士舞进阶班',
        courseType: '爵士',
        teacherName: '李老师',
        coursePrice: 1500,
        amount: 1500,
        paid: false,
        status: 'PENDING',
        enrolledAt: '2024-02-18 14:20:00',
        note: '希望能尽快安排上课',
        completedLessons: 0,
        totalLessons: 30,
        attendanceRate: 0,
        homeworkRate: 0
      }
    ]
    if (!allEnrollments.value.length) {
      allEnrollments.value = mockEnrollments
    }
    applyFilters()
  } catch (error) {
    message.error('加载报名列表失败')
  } finally {
    loading.value = false
  }
}

const applyFilters = () => {
  let list = [...allEnrollments.value]
  if (searchForm.studentName) {
    list = list.filter(item => item.studentName.includes(searchForm.studentName))
  }
  if (searchForm.courseId) {
    list = list.filter(item => item.courseId === searchForm.courseId)
  }
  if (searchForm.status) {
    list = list.filter(item => item.status === searchForm.status)
  }
  enrollments.value = list
  pagination.total = list.length
}

const loadStudents = async () => {
  students.value = [
    { id: 1, name: '张三', phone: '138****0001' },
    { id: 2, name: '李四', phone: '138****0002' },
    { id: 3, name: '王五', phone: '138****0003' }
  ]
}

const loadCourses = async () => {
  courses.value = [
    { id: 1, name: 'Hip-Hop基础班', price: 1200 },
    { id: 2, name: '爵士舞进阶班', price: 1500 },
    { id: 3, name: 'Popping入门班', price: 1000 }
  ]
}

const handleSearch = () => {
  pagination.current = 1
  applyFilters()
}

const handleReset = () => {
  Object.assign(searchForm, {
    studentName: '',
    courseId: undefined,
    status: undefined,
    dateRange: null
  })
  applyFilters()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  applyFilters()
}

const showAddModal = () => {
  editingEnrollment.value = null
  Object.assign(enrollmentForm, {
    id: undefined,
    studentId: undefined,
    courseId: undefined,
    amount: 0,
    paid: false,
    note: ''
  })
  addModalVisible.value = true
}

const editEnrollment = (enrollment: Enrollment) => {
  editingEnrollment.value = enrollment
  Object.assign(enrollmentForm, {
    id: enrollment.id,
    studentId: enrollment.studentId,
    courseId: enrollment.courseId,
    amount: enrollment.amount,
    paid: enrollment.paid,
    note: enrollment.note
  })
  addModalVisible.value = true
}

const viewDetail = (enrollment: Enrollment) => {
  currentEnrollment.value = enrollment
  detailModalVisible.value = true
}

const approveEnrollment = (enrollment: Enrollment) => {
  Modal.confirm({
    title: '确认通过',
    content: `确定要通过 ${enrollment.studentName} 的报名申请吗？`,
    onOk() {
      message.success('已通过报名申请')
      const target = allEnrollments.value.find(item => item.id === enrollment.id)
      if (target) {
        target.status = 'APPROVED'
      }
      applyFilters()
    }
  })
}

const rejectEnrollment = (enrollment: Enrollment) => {
  Modal.confirm({
    title: '确认拒绝',
    content: `确定要拒绝 ${enrollment.studentName} 的报名申请吗？`,
    onOk() {
      message.success('已拒绝报名申请')
      const target = allEnrollments.value.find(item => item.id === enrollment.id)
      if (target) {
        target.status = 'REJECTED'
      }
      applyFilters()
    }
  })
}

const cancelEnrollment = (enrollment: Enrollment) => {
  const target = allEnrollments.value.find(item => item.id === enrollment.id)
  if (target) {
    target.status = 'CANCELLED'
  }
  message.success(`已取消报名：${enrollment.studentName}`)
  applyFilters()
}

const handleSubmit = () => {
  if (!enrollmentForm.studentId || !enrollmentForm.courseId) {
    message.warning('请选择学员和课程')
    return
  }
  const student = students.value.find(item => item.id === enrollmentForm.studentId)
  const course = courses.value.find(item => item.id === enrollmentForm.courseId)
  if (!student || !course) {
    message.warning('请选择有效的学员和课程')
    return
  }
  const now = new Date()
  const enrolledAt = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:00`
  if (editingEnrollment.value) {
    const target = allEnrollments.value.find(item => item.id === editingEnrollment.value?.id)
    if (target) {
      target.studentId = student.id
      target.courseId = course.id
      target.studentName = student.name
      target.studentPhone = student.phone
      target.courseName = course.name
      target.coursePrice = course.price
      target.amount = enrollmentForm.amount || course.price
      target.paid = !!enrollmentForm.paid
      target.note = enrollmentForm.note
    }
    message.success('报名修改成功')
  } else {
    const nextId = allEnrollments.value.length ? Math.max(...allEnrollments.value.map(item => item.id)) + 1 : 1
    allEnrollments.value.unshift({
      id: nextId,
      studentId: student.id,
      courseId: course.id,
      studentName: student.name,
      studentPhone: student.phone,
      courseName: course.name,
      courseType: course.type || '综合',
      teacherName: course.teacherName || '待分配',
      coursePrice: course.price,
      amount: enrollmentForm.amount || course.price,
      paid: !!enrollmentForm.paid,
      status: 'PENDING',
      enrolledAt,
      note: enrollmentForm.note,
      completedLessons: 0,
      totalLessons: course.totalLessons || 24,
      attendanceRate: 0,
      homeworkRate: 0
    })
    message.success('报名创建成功')
  }
  addModalVisible.value = false
  applyFilters()
}

onMounted(() => {
  loadEnrollments()
  loadStudents()
  loadCourses()
})
</script>
