<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-gray-800">教师控制台</h1>
      <p class="text-gray-500 mt-1">欢迎回来，{{ userStore.realName || userStore.username }}</p>
    </div>

    <a-row :gutter="16" class="mb-6">
      <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
        <a-card class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm mb-1">我的课程</p>
              <p class="text-2xl font-semibold text-gray-800">{{ stats.totalCourses || 0 }}</p>
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
              <p class="text-gray-500 text-sm mb-1">总学员数</p>
              <p class="text-2xl font-semibold text-gray-800">{{ stats.totalStudents || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
              <UserOutlined class="text-xl text-gray-600" />
            </div>
          </div>
        </a-card>
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
        <a-card class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm mb-1">本周课时</p>
              <p class="text-2xl font-semibold text-gray-800">{{ stats.weeklyHours || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
              <ClockCircleOutlined class="text-xl text-gray-600" />
            </div>
          </div>
        </a-card>
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
        <a-card class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm mb-1">待上课</p>
              <p class="text-2xl font-semibold text-gray-800">{{ stats.upcomingClasses || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
              <CalendarOutlined class="text-xl text-gray-600" />
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16">
      <a-col :xs="24" :lg="12" class="mb-4">
        <a-card title="今日课程" class="h-full">
          <div v-if="todayCourses.length === 0" class="text-center py-8 text-gray-400">
            今日暂无课程安排
          </div>
          <div v-else class="space-y-3">
            <div
              v-for="course in todayCourses"
              :key="course.id"
              class="flex items-center justify-between p-3 bg-gray-50 rounded"
            >
              <div>
                <p class="font-medium text-gray-800">{{ course.name }}</p>
                <p class="text-sm text-gray-500">{{ course.time }}</p>
              </div>
              <a-tag :color="course.status === 'ongoing' ? 'success' : 'default'">
                {{ course.status === 'ongoing' ? '进行中' : '待上课' }}
              </a-tag>
            </div>
          </div>
        </a-card>
      </a-col>
      
      <a-col :xs="24" :lg="12" class="mb-4">
        <a-card title="快速操作" class="h-full">
          <a-row :gutter="16">
            <a-col :span="12" class="mb-3">
              <a-button type="default" block @click="$router.push('/teacher/courses')">
                <BookOutlined class="mr-2" />
                我的课程
              </a-button>
            </a-col>
            <a-col :span="12" class="mb-3">
              <a-button type="default" block @click="$router.push('/teacher/schedules')">
                <CalendarOutlined class="mr-2" />
                我的排班
              </a-button>
            </a-col>
            <a-col :span="12" class="mb-3">
              <a-button type="default" block @click="$router.push('/teacher/students')">
                <UserOutlined class="mr-2" />
                学员管理
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
import { 
  BookOutlined, 
  UserOutlined, 
  ClockCircleOutlined,
  CalendarOutlined 
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const stats = ref({
  totalCourses: 0,
  totalStudents: 0,
  weeklyHours: 0,
  upcomingClasses: 0
})

const todayCourses = ref([
  { id: 1, name: '街舞基础班', time: '14:00-15:30', status: 'ongoing' },
  { id: 2, name: '嘻哈进阶班', time: '16:00-17:30', status: 'upcoming' }
])

onMounted(() => {
  // 加载统计数据
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
