<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-gray-800">管理员控制台</h1>
      <p class="text-gray-500 mt-1">欢迎回来，{{ userStore.realName || userStore.username }}</p>
    </div>

    <a-row :gutter="16" class="mb-6">
      <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
        <a-card class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm mb-1">总用户数</p>
              <p class="text-2xl font-semibold text-gray-800">{{ stats.totalUsers || 0 }}</p>
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
              <p class="text-gray-500 text-sm mb-1">活跃用户</p>
              <p class="text-2xl font-semibold text-gray-800">{{ stats.activeUsers || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
              <TeamOutlined class="text-xl text-gray-600" />
            </div>
          </div>
        </a-card>
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
        <a-card class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm mb-1">教师数量</p>
              <p class="text-2xl font-semibold text-gray-800">{{ stats.teacherCount || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
              <SolutionOutlined class="text-xl text-gray-600" />
            </div>
          </div>
        </a-card>
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6" class="mb-4">
        <a-card class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm mb-1">学员数量</p>
              <p class="text-2xl font-semibold text-gray-800">{{ stats.studentCount || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
              <ReadOutlined class="text-xl text-gray-600" />
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16">
      <a-col :xs="24" :lg="12" class="mb-4">
        <a-card title="快速操作" class="h-full">
          <a-row :gutter="16">
            <a-col :span="12" class="mb-3">
              <a-button type="default" block @click="$router.push('/admin/users')">
                <UserOutlined class="mr-2" />
                用户管理
              </a-button>
            </a-col>
            <a-col :span="12" class="mb-3">
              <a-button type="default" block @click="$router.push('/admin/courses')">
                <BookOutlined class="mr-2" />
                课程管理
              </a-button>
            </a-col>
            <a-col :span="12" class="mb-3">
              <a-button type="default" block @click="$router.push('/admin/teachers')">
                <SolutionOutlined class="mr-2" />
                教师管理
              </a-button>
            </a-col>
            <a-col :span="12" class="mb-3">
              <a-button type="default" block @click="$router.push('/admin/students')">
                <ReadOutlined class="mr-2" />
                学员管理
              </a-button>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
      
      <a-col :xs="24" :lg="12" class="mb-4">
        <a-card title="系统信息" class="h-full">
          <div class="space-y-3">
            <div class="flex justify-between items-center py-2 border-b border-gray-100">
              <span class="text-gray-600">系统版本</span>
              <span class="text-gray-800">v1.0.0</span>
            </div>
            <div class="flex justify-between items-center py-2 border-b border-gray-100">
              <span class="text-gray-600">数据库状态</span>
              <a-tag color="success">正常</a-tag>
            </div>
            <div class="flex justify-between items-center py-2 border-b border-gray-100">
              <span class="text-gray-600">服务器状态</span>
              <a-tag color="success">正常</a-tag>
            </div>
            <div class="flex justify-between items-center py-2">
              <span class="text-gray-600">最后更新</span>
              <span class="text-gray-800">{{ new Date().toLocaleDateString() }}</span>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { 
  UserOutlined, 
  TeamOutlined, 
  SolutionOutlined, 
  ReadOutlined,
  BookOutlined 
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'
import { getDashboardStats } from '@/api/admin'

const userStore = useUserStore()
const stats = ref({
  totalUsers: 0,
  activeUsers: 0,
  teacherCount: 0,
  studentCount: 0
})

const loadStats = async () => {
  try {
    const res = await getDashboardStats()
    if (res.code === 200 && res.data) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
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
