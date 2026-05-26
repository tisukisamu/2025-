<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <a-page-header
      :title="course.name"
      :sub-title="getCourseTypeText(course.type)"
      @back="() => $router.back()"
    >
      <template #extra>
        <a-space>
          <a-button @click="editCourse" v-if="canEdit">编辑课程</a-button>
          <a-button type="primary" @click="enrollCourse" v-if="canEnroll && !hasEnrolled">立即报名</a-button>
          <a-tag v-if="hasEnrolled" color="green">已报名</a-tag>
        </a-space>
      </template>

      <a-descriptions :column="3" class="mt-4">
        <a-descriptions-item label="授课教师">{{ course.teacherName }}</a-descriptions-item>
        <a-descriptions-item label="课程价格">
          <span class="text-xl font-bold text-gray-800">¥{{ course.price }}</span>
        </a-descriptions-item>
        <a-descriptions-item label="课程时长">{{ course.duration }}课时</a-descriptions-item>
        <a-descriptions-item label="难度等级">
          <a-tag :color="getLevelColor(course.level)">{{ getLevelText(course.level) }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="报名人数">
          <a-progress 
            :percent="(course.enrolledCount / course.maxStudents) * 100" 
            :format="() => `${course.enrolledCount}/${course.maxStudents}`"
          />
        </a-descriptions-item>
        <a-descriptions-item label="课程状态">
          <a-tag :color="getStatusColor(course.status)">{{ getStatusText(course.status) }}</a-tag>
        </a-descriptions-item>
      </a-descriptions>
    </a-page-header>

    <a-row :gutter="16" class="mt-6">
      <a-col :xs="24" :lg="16">
        <a-card title="课程信息" class="mb-4">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="开课日期">{{ course.startDate }}</a-descriptions-item>
            <a-descriptions-item label="结课日期">{{ course.endDate }}</a-descriptions-item>
            <a-descriptions-item label="上课时间">{{ course.classTime }}</a-descriptions-item>
            <a-descriptions-item label="上课地点">{{ course.location }}</a-descriptions-item>
            <a-descriptions-item label="课程描述" :span="2">
              {{ course.description || '暂无描述' }}
            </a-descriptions-item>
          </a-descriptions>
        </a-card>

        <a-card title="课程大纲" class="mb-4">
          <a-timeline>
            <a-timeline-item v-for="(item, index) in course.outline" :key="index" :color="item.completed ? 'green' : 'gray'">
              <div class="flex justify-between items-start">
                <div>
                  <div class="font-bold">{{ item.title }}</div>
                  <div class="text-sm text-gray-500 mt-1">{{ item.content }}</div>
                </div>
                <a-tag v-if="item.completed" color="green">已完成</a-tag>
              </div>
            </a-timeline-item>
          </a-timeline>
        </a-card>

        <a-card title="课程评价" class="mb-4">
          <div class="mb-4">
            <a-row :gutter="16">
              <a-col :span="8">
                <a-statistic title="综合评分" :value="course.rating" suffix="/ 5.0">
                  <template #formatter>
                    <span class="text-3xl font-bold text-yellow-500">{{ course.rating }}</span>
                  </template>
                </a-statistic>
              </a-col>
              <a-col :span="16">
                <div v-for="(count, star) in ratingDistribution" :key="star" class="mb-2">
                  <span class="mr-2">{{ star }}星</span>
                  <a-progress :percent="count" :show-info="false" style="width: 200px" />
                  <span class="ml-2 text-gray-500">{{ count }}%</span>
                </div>
              </a-col>
            </a-row>
          </div>

          <a-list :data-source="reviews" item-layout="horizontal">
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta :description="item.content">
                  <template #title>
                    <div class="flex justify-between">
                      <span>{{ item.userName }}</span>
                      <a-rate :value="item.rating" disabled />
                    </div>
                  </template>
                  <template #avatar>
                    <a-avatar>{{ item.userName.charAt(0) }}</a-avatar>
                  </template>
                </a-list-item-meta>
                <template #actions>
                  <span class="text-gray-500 text-sm">{{ item.createdAt }}</span>
                </template>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="8">
        <a-card title="授课教师" class="mb-4">
          <div class="text-center">
            <a-avatar :size="80">{{ course.teacherName.charAt(0) }}</a-avatar>
            <h3 class="text-lg font-bold mt-2">{{ course.teacherName }}</h3>
            <p class="text-gray-600">{{ course.teacherTitle }}</p>
            <a-button type="link" @click="viewTeacher">查看详情</a-button>
          </div>
          <a-divider />
          <a-descriptions :column="1" size="small">
            <a-descriptions-item label="教学经验">{{ course.teacherExperience }}年</a-descriptions-item>
            <a-descriptions-item label="授课数量">{{ course.teacherCourses }}门</a-descriptions-item>
            <a-descriptions-item label="学员评价">{{ course.teacherRating }}分</a-descriptions-item>
          </a-descriptions>
        </a-card>

        <a-card title="已报名学员" class="mb-4">
          <a-list :data-source="enrolledStudents" size="small">
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #title>{{ item.name }}</template>
                  <template #avatar>
                    <a-avatar :size="32">{{ item.name.charAt(0) }}</a-avatar>
                  </template>
                </a-list-item-meta>
                <template #actions>
                  <a-progress :percent="item.progress" :size="'small'" style="width: 80px" />
                </template>
              </a-list-item>
            </template>
          </a-list>
          <div class="text-center mt-4">
            <a-button type="link" @click="viewAllStudents">查看全部学员</a-button>
          </div>
        </a-card>

        <a-card title="课程动态">
          <a-timeline>
            <a-timeline-item v-for="(activity, index) in activities" :key="index">
              <div class="text-sm">
                <div class="font-bold">{{ activity.title }}</div>
                <div class="text-gray-500 mt-1">{{ activity.time }}</div>
              </div>
            </a-timeline-item>
          </a-timeline>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useUserStore } from '@/stores/user'
import { getCourseById } from '@/api/course'
import { getMyCourses } from '@/api/student'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const currentCourseId = computed(() => Number(route.params.id) || course.id)

const course = reactive({
  id: 1,
  name: 'Hip-Hop基础班',
  type: 'HIPHOP',
  teacherId: 1,
  teacherName: '张老师',
  teacherTitle: '资深Hip-Hop教练',
  teacherExperience: 8,
  teacherCourses: 12,
  teacherRating: 4.9,
  price: 1200,
  duration: 24,
  level: 'BEGINNER',
  maxStudents: 20,
  enrolledCount: 18,
  startDate: '2024-01-15',
  endDate: '2024-03-15',
  classTime: '周一、周三 18:00-19:30',
  location: 'A教室',
  description: '适合零基础学员，从基础动作开始学习，循序渐进掌握Hip-Hop舞蹈技巧',
  status: 'ONGOING',
  rating: 4.8,
  outline: [
    { title: '第一课：基础律动', content: '学习身体律动和基础节奏感', completed: true },
    { title: '第二课：基础步伐', content: '掌握Hip-Hop基础步伐组合', completed: true },
    { title: '第三课：身体协调', content: '提升身体协调性和控制力', completed: false },
    { title: '第四课：简单编舞', content: '学习简单的编舞组合', completed: false }
  ]
})

const ratingDistribution = ref({
  '5': 75,
  '4': 20,
  '3': 4,
  '2': 1,
  '1': 0
})

const reviews = ref([
  {
    id: 1,
    userName: '学员小明',
    rating: 5,
    content: '老师讲解很细致，课程内容丰富，学到了很多！',
    createdAt: '2024-02-15'
  },
  {
    id: 2,
    userName: '学员小红',
    rating: 4,
    content: '课程不错，就是时间有点短，希望能延长课时。',
    createdAt: '2024-02-10'
  }
])

const enrolledStudents = ref([
  { id: 1, name: '张三', progress: 75 },
  { id: 2, name: '李四', progress: 60 },
  { id: 3, name: '王五', progress: 90 }
])

const activities = ref([
  { title: '新学员报名', time: '2024-02-20 14:30' },
  { title: '完成第3课教学', time: '2024-02-19 19:30' },
  { title: '发布课程作业', time: '2024-02-18 20:00' }
])

const canEnroll = ref(true)
const hasEnrolled = ref(false)

const canEdit = computed(() => {
  return userStore.isAdmin || userStore.isTeacher
})

const getCourseTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    HIPHOP: 'Hip-Hop',
    JAZZ: '爵士',
    BREAKING: 'Breaking',
    POPPING: 'Popping',
    LOCKING: 'Locking',
    URBAN: 'Urban'
  }
  return typeMap[type] || type
}

const getLevelText = (level: string) => {
  const levelMap: Record<string, string> = {
    BEGINNER: '初级',
    INTERMEDIATE: '中级',
    ADVANCED: '高级'
  }
  return levelMap[level] || level
}

const getLevelColor = (level: string) => {
  const colorMap: Record<string, string> = {
    BEGINNER: 'green',
    INTERMEDIATE: 'blue',
    ADVANCED: 'red'
  }
  return colorMap[level] || 'default'
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    UPCOMING: 'blue',
    ONGOING: 'green',
    COMPLETED: 'default',
    CANCELLED: 'red'
  }
  return colorMap[status] || 'default'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    UPCOMING: '即将开课',
    ONGOING: '进行中',
    COMPLETED: '已结束',
    CANCELLED: '已取消'
  }
  return textMap[status] || status
}

const editCourse = () => {
  if (userStore.isTeacher && !userStore.isAdmin) {
    router.push(`/teacher/courses/${currentCourseId.value}/edit`)
    return
  }
  router.push(`/admin/courses/${currentCourseId.value}/edit`)
}

const enrollCourse = () => {
  message.success('报名成功')
}

const viewTeacher = () => {
  router.push(`/teachers/${course.teacherId}`)
}

const viewAllStudents = () => {
  message.info('查看全部学员')
}

const loadCourse = async () => {
  try {
    const res = await getCourseById(currentCourseId.value)
    if (res.code === 200 && res.data) {
      Object.assign(course, {
        id: res.data.id,
        name: res.data.name,
        type: res.data.category || 'HIPHOP',
        teacherId: res.data.teacher?.id || 1,
        teacherName: res.data.teacher?.name || '待分配',
        teacherTitle: '资深教练',
        teacherExperience: res.data.teacher?.experience || 5,
        teacherCourses: 12,
        teacherRating: 4.9,
        price: res.data.price,
        duration: res.data.duration || 24,
        level: res.data.level || 'BEGINNER',
        maxStudents: res.data.capacity,
        enrolledCount: res.data.enrolledCount,
        startDate: '2024-01-15',
        endDate: '2024-03-15',
        classTime: '周一、周三 18:00-19:30',
        location: 'A教室',
        description: res.data.description || '暂无描述',
        status: res.data.status || 'ONGOING',
        rating: 4.8,
        outline: [
          { title: '第一课：基础律动', content: '学习身体律动和基础节奏感', completed: true },
          { title: '第二课：基础步伐', content: '掌握Hip-Hop基础步伐组合', completed: true },
          { title: '第三课：身体协调', content: '提升身体协调性和控制力', completed: false },
          { title: '第四课：简单编舞', content: '学习简单的编舞组合', completed: false }
        ]
      })
    }
  } catch (error) {
    console.error('加载课程失败:', error)
  }
}

const checkEnrollment = async () => {
  if (!userStore.isStudent && !userStore.isAdmin) {
    return
  }
  try {
    const res = await getMyCourses()
    if (res.code === 200 && res.data) {
      hasEnrolled.value = res.data.some(
        item => item.course.id === currentCourseId.value && item.status !== 'CANCELLED'
      )
    }
  } catch (error) {
    console.error('检查报名状态失败:', error)
  }
}

onMounted(() => {
  loadCourse()
  checkEnrollment()
})
</script>

<style scoped>
:deep(.ant-page-header) {
  background: white;
  padding: 16px 24px;
  border-radius: 8px;
}
</style>
