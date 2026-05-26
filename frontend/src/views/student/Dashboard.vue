<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-gray-800">学员控制台</h1>
      <p class="text-gray-500 mt-1">欢迎回来，{{ userStore.realName || userStore.username }}</p>
    </div>

    <a-row :gutter="16" class="mb-6">
      <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
        <a-card class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm mb-1">已报名课程</p>
              <p class="text-2xl font-semibold text-gray-800">{{ stats.enrolledCourses || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
              <BookOutlined class="text-xl text-gray-600" />
            </div>
          </div>
        </a-card>
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
        <a-card class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm mb-1">已完成课程</p>
              <p class="text-2xl font-semibold text-gray-800">{{ stats.completedCourses || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
              <CheckCircleOutlined class="text-xl text-gray-600" />
            </div>
          </div>
        </a-card>
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
        <a-card class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm mb-1">待支付账单</p>
              <p class="text-2xl font-semibold text-gray-800">{{ stats.unpaidBills || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
              <AccountBookOutlined class="text-xl text-gray-600" />
            </div>
          </div>
        </a-card>
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
        <a-card class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm mb-1">学习时长</p>
              <p class="text-2xl font-semibold text-gray-800">{{ stats.studyHours || 0 }}h</p>
            </div>
            <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
              <ClockCircleOutlined class="text-xl text-gray-600" />
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16">
      <a-col :xs="24" :lg="12" class="mb-4">
        <a-card title="我的课程" class="h-full">
          <div v-if="myCourses.length === 0" class="text-center py-8 text-gray-400">
            暂无报名课程
          </div>
          <div v-else class="space-y-3">
            <div
              v-for="course in myCourses"
              :key="course.id"
              class="flex items-center justify-between p-3 bg-gray-50 rounded"
            >
              <div class="flex items-center gap-3">
                <img :src="course.coverImage || DEFAULT_COURSE_IMAGE" alt="课程封面" class="w-12 h-12 rounded object-cover" />
                <div>
                  <p class="font-medium text-gray-800">{{ course.name }}</p>
                  <p class="text-sm text-gray-500">{{ course.teacher }}</p>
                </div>
              </div>
              <a-tag :color="course.status === 'ongoing' ? 'success' : 'default'">
                {{ course.status === 'ongoing' ? '进行中' : '已完成' }}
              </a-tag>
            </div>
          </div>
          <div class="mt-4 text-center">
            <a-button type="link" @click="$router.push('/student/my-courses')">
              查看全部
            </a-button>
          </div>
        </a-card>
      </a-col>
      
      <a-col :xs="24" :lg="12" class="mb-4">
        <a-card title="快速操作" class="h-full">
          <a-row :gutter="16">
            <a-col :span="12" class="mb-3">
              <a-button type="default" block @click="$router.push('/student/courses')">
                <BookOutlined class="mr-2" />
                浏览课程
              </a-button>
            </a-col>
            <a-col :span="12" class="mb-3">
              <a-button type="default" block @click="$router.push('/student/my-courses')">
                <SolutionOutlined class="mr-2" />
                我的课程
              </a-button>
            </a-col>
            <a-col :span="12" class="mb-3">
              <a-button type="default" block @click="$router.push('/student/profile')">
                <UserOutlined class="mr-2" />
                个人中心
              </a-button>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { 
  BookOutlined, 
  UserOutlined, 
  CheckCircleOutlined,
  ClockCircleOutlined,
  AccountBookOutlined,
  SolutionOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'
import { getMyCourses, type StudentCourseEnrollment } from '@/api/student'

const userStore = useUserStore()
const DEFAULT_COURSE_IMAGE = 'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=400&q=80'
const stats = ref({
  enrolledCourses: 0,
  completedCourses: 0,
  unpaidBills: 0,
  studyHours: 0
})

const myCourses = ref<Array<{ id: number; name: string; teacher: string; status: string; coverImage: string }>>([])

const mapCourseItem = (item: StudentCourseEnrollment) => ({
  id: item.course.id,
  name: item.course.name,
  teacher: item.course.teacherName || '待分配',
  status: item.status === 'COMPLETED' ? 'completed' : 'ongoing',
  coverImage: item.course.image || ''
})

const loadDashboardData = async () => {
  try {
    const res = await getMyCourses()
    if (res.code === 200 && res.data) {
      const courseList = res.data.map(mapCourseItem)
      myCourses.value = courseList.slice(0, 5)
      stats.value.enrolledCourses = res.data.length
      stats.value.completedCourses = res.data.filter(item => item.status === 'COMPLETED').length
      stats.value.unpaidBills = res.data.filter(item => item.paymentStatus === 'UNPAID').length
      stats.value.studyHours = res.data.reduce((total, item) => {
        const duration = Number(item.course.duration || 0)
        const ratio = item.status === 'COMPLETED' ? 1 : item.status === 'CONFIRMED' ? 0.6 : 0.2
        return total + Math.round(duration * ratio)
      }, 0)
    }
  } catch (error) {
    message.error('加载学习首页数据失败')
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.stat-card {
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
</style>
