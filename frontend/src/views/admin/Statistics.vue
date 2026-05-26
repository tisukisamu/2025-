<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-gray-800">统计报表</h1>
      <p class="text-gray-500 mt-1">查看系统各项统计数据</p>
    </div>

    <a-tabs v-model:activeKey="activeTab">
      <a-tab-pane key="overview" tab="概览">
        <a-row :gutter="16" class="mb-6">
          <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
            <a-card>
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-gray-500 text-sm mb-1">总课程数</p>
                  <p class="text-2xl font-semibold text-gray-800">{{ courseStats.totalCourses || 0 }}</p>
                </div>
                <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
                  <BookOutlined class="text-xl text-gray-600" />
                </div>
              </div>
            </a-card>
          </a-col>
          
          <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
            <a-card>
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-gray-500 text-sm mb-1">已发布课程</p>
                  <p class="text-2xl font-semibold text-gray-800">{{ courseStats.publishedCourses || 0 }}</p>
                </div>
                <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
                  <CheckCircleOutlined class="text-xl text-gray-600" />
                </div>
              </div>
            </a-card>
          </a-col>
          
          <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
            <a-card>
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-gray-500 text-sm mb-1">总报名数</p>
                  <p class="text-2xl font-semibold text-gray-800">{{ courseStats.totalEnrollments || 0 }}</p>
                </div>
                <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
                  <UserOutlined class="text-xl text-gray-600" />
                </div>
              </div>
            </a-card>
          </a-col>
          
          <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
            <a-card>
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-gray-500 text-sm mb-1">总收入</p>
                  <p class="text-2xl font-semibold text-gray-800">¥{{ financeStats.totalIncome || 0 }}</p>
                </div>
                <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
                  <AccountBookOutlined class="text-xl text-gray-600" />
                </div>
              </div>
            </a-card>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :xs="24" :lg="12" class="mb-4">
            <a-card title="学员统计">
              <div class="space-y-4">
                <div class="flex justify-between items-center">
                  <span class="text-gray-600">总学员数</span>
                  <span class="text-xl font-semibold text-gray-800">{{ studentStats.totalStudents || 0 }}</span>
                </div>
                <a-divider class="my-2" />
                <div class="flex justify-between items-center">
                  <span class="text-gray-600">活跃学员</span>
                  <span class="text-gray-800">{{ studentStats.activeStudents || 0 }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-gray-600">休学学员</span>
                  <span class="text-gray-800">{{ studentStats.inactiveStudents || 0 }}</span>
                </div>
              </div>
            </a-card>
          </a-col>
          
          <a-col :xs="24" :lg="12" class="mb-4">
            <a-card title="财务统计">
              <div class="space-y-4">
                <div class="flex justify-between items-center">
                  <span class="text-gray-600">成功支付</span>
                  <span class="text-gray-800">{{ financeStats.successfulPayments || 0 }} 笔</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-gray-600">待支付</span>
                  <span class="text-gray-800">{{ financeStats.pendingPayments || 0 }} 笔</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-gray-600">支付失败</span>
                  <span class="text-gray-800">{{ financeStats.failedPayments || 0 }} 笔</span>
                </div>
              </div>
            </a-card>
          </a-col>
        </a-row>
      </a-tab-pane>

      <a-tab-pane key="courses" tab="课程统计">
        <a-card>
          <a-table
            :columns="courseColumns"
            :data-source="courseData"
            :loading="courseLoading"
            row-key="id"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'enrollments'">
                <a-progress
                  :percent="record.enrollments / record.capacity * 100"
                  :format="() => `${record.enrollments}/${record.capacity}`"
                />
              </template>
            </template>
          </a-table>
        </a-card>
      </a-tab-pane>

      <a-tab-pane key="students" tab="学员统计">
        <a-card>
          <a-row :gutter="16" class="mb-4">
            <a-col :span="12">
              <div class="text-center p-4 bg-gray-50 rounded">
                <p class="text-gray-500 text-sm mb-2">男女比例</p>
                <div class="flex justify-center space-x-4">
                  <div>
                    <p class="text-xl font-semibold text-gray-800">{{ studentStats.studentsByGender?.MALE || 0 }}</p>
                    <p class="text-gray-500 text-sm">男</p>
                  </div>
                  <div>
                    <p class="text-xl font-semibold text-gray-800">{{ studentStats.studentsByGender?.FEMALE || 0 }}</p>
                    <p class="text-gray-500 text-sm">女</p>
                  </div>
                </div>
              </div>
            </a-col>
            <a-col :span="12">
              <div class="text-center p-4 bg-gray-50 rounded">
                <p class="text-gray-500 text-sm mb-2">报名状态分布</p>
                <div class="space-y-2">
                  <div class="flex justify-between">
                    <span class="text-gray-600">待确认</span>
                    <span class="text-gray-800">{{ studentStats.enrollmentsByStatus?.PENDING || 0 }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-gray-600">已确认</span>
                    <span class="text-gray-800">{{ studentStats.enrollmentsByStatus?.CONFIRMED || 0 }}</span>
                  </div>
                </div>
              </div>
            </a-col>
          </a-row>
        </a-card>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  BookOutlined,
  CheckCircleOutlined,
  UserOutlined,
  AccountBookOutlined
} from '@ant-design/icons-vue'
import { getCourseStatistics, getFinanceStatistics, getStudentStatistics } from '@/api/statistics'
import { getCourses } from '@/api/course'

const activeTab = ref('overview')
const courseLoading = ref(false)

const courseStats = ref({
  totalCourses: 0,
  publishedCourses: 0,
  draftCourses: 0,
  closedCourses: 0,
  totalEnrollments: 0
})

const financeStats = ref({
  totalIncome: 0,
  successfulPayments: 0,
  pendingPayments: 0,
  failedPayments: 0
})

const studentStats = ref({
  totalStudents: 0,
  activeStudents: 0,
  inactiveStudents: 0,
  studentsByGender: {},
  enrollmentsByStatus: {}
})

const courseData = ref([])

const courseColumns = [
  { title: '课程名称', dataIndex: 'name', key: 'name' },
  { title: '教师', dataIndex: 'teacher', key: 'teacher' },
  { title: '报名情况', key: 'enrollments' },
  { title: '价格', dataIndex: 'price', key: 'price' }
]

const loadStatistics = async () => {
  try {
    const [courseRes, financeRes, studentRes] = await Promise.all([
      getCourseStatistics(),
      getFinanceStatistics(),
      getStudentStatistics()
    ])
    
    if (courseRes.code === 200 && courseRes.data) {
      courseStats.value = courseRes.data
    }
    
    if (financeRes.code === 200 && financeRes.data) {
      financeStats.value = financeRes.data
    }
    
    if (studentRes.code === 200 && studentRes.data) {
      studentStats.value = studentRes.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadCourseData = async () => {
  courseLoading.value = true
  try {
    const res = await getCourses({ page: 0, size: 100 })
    if (res.code === 200 && res.data?.content) {
      courseData.value = res.data.content.map((item: any) => ({
        id: item.id,
        name: item.name,
        teacher: item.teacher?.name || '-',
        enrollments: item.enrolledCount || 0,
        capacity: item.capacity || 0,
        price: item.price || 0
      }))
    }
  } catch (error) {
    console.error('加载课程统计列表失败:', error)
  } finally {
    courseLoading.value = false
  }
}

onMounted(() => {
  loadStatistics()
  loadCourseData()
})
</script>
