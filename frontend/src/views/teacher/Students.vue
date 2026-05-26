<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-gray-800">学员管理</h1>
      <p class="text-gray-500 mt-1">查看和管理您的学员</p>
    </div>

    <a-card class="mb-4">
      <a-form layout="inline">
        <a-form-item label="课程">
          <a-select
            v-model:value="filterCourse"
            placeholder="选择课程"
            style="width: 200px"
            allow-clear
          >
            <a-select-option value="">全部课程</a-select-option>
            <a-select-option v-for="course in courses" :key="course.id" :value="course.id">
              {{ course.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="搜索">
          <a-input
            v-model:value="searchText"
            placeholder="搜索学员姓名"
            style="width: 200px"
            allow-clear
          >
            <template #prefix>
              <SearchOutlined />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            搜索
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card>
      <a-table
        :columns="columns"
        :data-source="students"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'avatar'">
            <a-avatar :src="record.avatar">
              {{ record.name?.charAt(0) }}
            </a-avatar>
          </template>
          <template v-else-if="column.key === 'gender'">
            {{ record.gender === 'MALE' ? '男' : record.gender === 'FEMALE' ? '女' : '其他' }}
          </template>
          <template v-else-if="column.key === 'enrollmentDate'">
            {{ formatDate(record.enrollmentDate) }}
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewDetail(record)">
                查看详情
              </a-button>
              <a-button type="link" size="small" @click="viewProgress(record)">
                学习进度
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="detailModalVisible"
      title="学员详情"
      width="640px"
      :footer="null"
    >
      <div v-if="currentStudent">
        <div class="flex items-center mb-4">
          <a-avatar :src="currentStudent.avatar" :size="56" class="mr-3">
            {{ currentStudent.name?.charAt(0) }}
          </a-avatar>
          <div>
            <p class="text-lg font-semibold text-gray-800">{{ currentStudent.name }}</p>
            <p class="text-sm text-gray-500">{{ currentStudent.phone || '-' }}</p>
          </div>
        </div>
        <a-descriptions bordered :column="2">
          <a-descriptions-item label="姓名">{{ currentStudent.name || '-' }}</a-descriptions-item>
          <a-descriptions-item label="手机号">{{ currentStudent.phone || '-' }}</a-descriptions-item>
          <a-descriptions-item label="性别">
            {{ currentStudent.gender === 'MALE' ? '男' : currentStudent.gender === 'FEMALE' ? '女' : '其他' }}
          </a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="getStatusColor(currentStudent.status)">{{ getStatusText(currentStudent.status) }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="报名日期" :span="2">{{ formatDate(currentStudent.enrollmentDate) }}</a-descriptions-item>
        </a-descriptions>
      </div>
    </a-modal>

    <a-modal
      v-model:open="progressModalVisible"
      title="学习进度"
      width="700px"
      :footer="null"
    >
      <div v-if="currentStudent">
        <div class="mb-4">
          <div class="flex items-center mb-2">
            <a-avatar :src="currentStudent.avatar" class="mr-3">
              {{ currentStudent.name?.charAt(0) }}
            </a-avatar>
            <div>
              <p class="font-medium text-gray-800">{{ currentStudent.name }}</p>
              <p class="text-sm text-gray-500">{{ currentStudent.phone }}</p>
            </div>
          </div>
        </div>
        
        <a-divider />
        
        <div class="space-y-4">
          <div>
            <p class="text-gray-600 mb-2">课程完成进度</p>
            <a-progress :percent="75" />
          </div>
          
          <div>
            <p class="text-gray-600 mb-2">出勤率</p>
            <a-progress :percent="90" status="success" />
          </div>
          
          <div>
            <p class="text-gray-600 mb-2">作业完成情况</p>
            <a-progress :percent="80" />
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { SearchOutlined } from '@ant-design/icons-vue'
import type { Student, Course } from '@/types'
import dayjs from 'dayjs'

const loading = ref(false)
const students = ref<Student[]>([])
const courses = ref<Course[]>([])
const searchText = ref('')
const filterCourse = ref('')
const detailModalVisible = ref(false)
const progressModalVisible = ref(false)
const currentStudent = ref<Student | null>(null)

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total: number) => `共 ${total} 条`
})

const columns = [
  { title: '头像', key: 'avatar', width: 80 },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '性别', dataIndex: 'gender', key: 'gender' },
  { title: '手机号', dataIndex: 'phone', key: 'phone' },
  { title: '报名日期', dataIndex: 'enrollmentDate', key: 'enrollmentDate' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action', width: 200 }
]

const getStatusColor = (status: string) => {
  return status === 'ACTIVE' ? 'success' : 'warning'
}

const getStatusText = (status: string) => {
  return status === 'ACTIVE' ? '在读' : '休学'
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD')
}

const handleSearch = () => {
  pagination.current = 1
  loadStudents()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadStudents()
}

const viewDetail = (record: Student) => {
  currentStudent.value = record
  detailModalVisible.value = true
}

const viewProgress = (record: Student) => {
  currentStudent.value = record
  progressModalVisible.value = true
}

const loadStudents = async () => {
  loading.value = true
  try {
    // 模拟数据
    students.value = [
      {
        id: 1,
        name: '张三',
        phone: '13800138001',
        gender: 'MALE',
        avatar: '',
        status: 'ACTIVE',
        enrollmentDate: '2024-01-01'
      }
    ]
    pagination.total = 1
  } catch (error) {
    console.error('加载学员列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStudents()
})
</script>
