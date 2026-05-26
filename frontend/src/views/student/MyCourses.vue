<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">我的课程</h1>
      <p class="text-gray-600 mt-1">查看已报名的课程学习进度</p>
    </div>

    <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
      <a-tab-pane key="ongoing" tab="进行中">
        <a-row :gutter="[16, 16]">
          <a-col :xs="24" :sm="12" :lg="8" v-for="course in ongoingCourses" :key="course.id">
            <a-card hoverable>
              <template #cover>
                <div class="course-cover">
                  <img v-if="getCourseImage(course)" :src="getCourseImage(course)" alt="课程封面" />
                  <div v-else class="course-cover-placeholder">
                    <PictureOutlined style="font-size: 48px; color: #d9d9d9" />
                  </div>
                </div>
              </template>

              <a-card-meta>
                <template #title>
                  <div class="course-title">{{ course.name }}</div>
                </template>
                <template #description>
                  <div class="course-info">
                    <div class="info-item">
                      <UserOutlined class="mr-1" />
                      <span>{{ course.teacherName }}</span>
                    </div>
                    <div class="info-item">
                      <CalendarOutlined class="mr-1" />
                      <span>{{ course.startDate }} ~ {{ course.endDate }}</span>
                    </div>
                  </div>
                </template>
              </a-card-meta>

              <div class="mt-4">
                <div class="flex justify-between mb-2">
                  <span class="text-sm text-gray-600">学习进度</span>
                  <span class="text-sm font-bold">{{ course.progress }}%</span>
                </div>
                <a-progress :percent="course.progress" :show-info="false" />
              </div>

              <div class="mt-4">
                <a-space direction="vertical" style="width: 100%">
                  <div class="flex justify-between text-sm">
                    <span class="text-gray-600">已完成课时</span>
                    <span class="font-bold">{{ course.completedLessons }}/{{ course.totalLessons }}</span>
                  </div>
                  <div class="flex justify-between text-sm">
                    <span class="text-gray-600">下次上课</span>
                    <span class="font-bold text-blue-600">{{ course.nextClass }}</span>
                  </div>
                </a-space>
              </div>

              <div class="course-footer">
                <a-button type="primary" block @click="continueLearning(course)">
                  继续学习
                </a-button>
              </div>
            </a-card>
          </a-col>
        </a-row>
      </a-tab-pane>

      <a-tab-pane key="completed" tab="已完成">
        <a-row :gutter="[16, 16]">
          <a-col :xs="24" :sm="12" :lg="8" v-for="course in completedCourses" :key="course.id">
            <a-card hoverable>
              <template #cover>
                <div class="course-cover">
                  <img v-if="getCourseImage(course)" :src="getCourseImage(course)" alt="课程封面" />
                  <div v-else class="course-cover-placeholder">
                    <PictureOutlined style="font-size: 48px; color: #d9d9d9" />
                  </div>
                  <div class="completed-badge">
                    <CheckCircleOutlined />
                  </div>
                </div>
              </template>

              <a-card-meta>
                <template #title>
                  <div class="course-title">{{ course.name }}</div>
                </template>
                <template #description>
                  <div class="course-info">
                    <div class="info-item">
                      <UserOutlined class="mr-1" />
                      <span>{{ course.teacherName }}</span>
                    </div>
                    <div class="info-item">
                      <CalendarOutlined class="mr-1" />
                      <span>{{ course.startDate }} ~ {{ course.endDate }}</span>
                    </div>
                  </div>
                </template>
              </a-card-meta>

              <div class="mt-4">
                <div class="flex justify-between mb-2">
                  <span class="text-sm text-gray-600">学习进度</span>
                  <span class="text-sm font-bold text-green-600">100%</span>
                </div>
                <a-progress :percent="100" :show-info="false" status="success" />
              </div>

              <div class="mt-4">
                <a-space direction="vertical" style="width: 100%">
                  <div class="flex justify-between text-sm">
                    <span class="text-gray-600">完成时间</span>
                    <span class="font-bold">{{ course.completedDate }}</span>
                  </div>
                  <div class="flex justify-between text-sm">
                    <span class="text-gray-600">学习评分</span>
                    <a-rate v-model:value="course.rating" disabled />
                  </div>
                </a-space>
              </div>

              <div class="course-footer">
                <a-space style="width: 100%">
                  <a-button style="flex: 1">查看证书</a-button>
                  <a-button type="primary" style="flex: 1">评价课程</a-button>
                </a-space>
              </div>
            </a-card>
          </a-col>
        </a-row>
      </a-tab-pane>

      <a-tab-pane key="all" tab="全部">
        <a-table :columns="columns" :data-source="allCourses" :pagination="pagination">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="record.status === 'ongoing' ? 'blue' : 'green'">
                {{ record.status === 'ongoing' ? '进行中' : '已完成' }}
              </a-tag>
            </template>
            <template v-if="column.key === 'progress'">
              <a-progress :percent="record.progress" :size="'small'" />
            </template>
            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="link" size="small" @click="viewDetail(record)">详情</a-button>
                <a-button type="link" size="small" v-if="record.status === 'ongoing'" @click="continueLearning(record)">
                  学习
                </a-button>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  PictureOutlined,
  UserOutlined,
  CalendarOutlined,
  CheckCircleOutlined
} from '@ant-design/icons-vue'
import { getMyCourses, type StudentCourseEnrollment } from '@/api/student'

interface MyCourse {
  id: number
  courseId: number
  name: string
  teacherName: string
  startDate: string
  endDate: string
  progress: number
  completedLessons: number
  totalLessons: number
  nextClass: string
  coverImage: string
  image?: string
  status: string
  completedDate?: string
  rating?: number
}

const router = useRouter()
const activeTab = ref('ongoing')
const ongoingCourses = ref<MyCourse[]>([])
const completedCourses = ref<MyCourse[]>([])
const allCourses = ref<MyCourse[]>([])

const columns = [
  { title: '课程名称', dataIndex: 'name', key: 'name' },
  { title: '授课教师', dataIndex: 'teacherName', key: 'teacherName' },
  { title: '开课日期', dataIndex: 'startDate', key: 'startDate' },
  { title: '结课日期', dataIndex: 'endDate', key: 'endDate' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '学习进度', dataIndex: 'progress', key: 'progress' },
  { title: '操作', key: 'action' }
]

const pagination = {
  pageSize: 10,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条记录`
}

const DEFAULT_COURSE_IMAGE = 'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=1200&q=80'

const getCourseImage = (course: MyCourse) => {
  return course.coverImage || course.image || DEFAULT_COURSE_IMAGE
}

const formatDate = (value?: string) => {
  if (!value) {
    return '-'
  }
  return value.replace('T', ' ').slice(0, 10)
}

const buildMyCourse = (item: StudentCourseEnrollment): MyCourse => {
  const isCompleted = item.status === 'COMPLETED'
  const progress = isCompleted ? 100 : item.status === 'CONFIRMED' ? 60 : 20
  const totalLessons = Number(item.course.duration || 0)
  const completedLessons = Math.floor((totalLessons * progress) / 100)
  return {
    id: item.enrollmentId,
    courseId: item.course.id,
    name: item.course.name,
    teacherName: item.course.teacherName || '待分配',
    startDate: formatDate(item.course.createdAt),
    endDate: formatDate(item.course.updatedAt),
    progress,
    completedLessons,
    totalLessons,
    nextClass: '-',
    coverImage: item.course.image || '',
    image: item.course.image || '',
    status: isCompleted ? 'completed' : 'ongoing',
    completedDate: isCompleted ? formatDate(item.course.updatedAt) : '',
    rating: isCompleted ? 5 : 0
  }
}

const loadCourses = async () => {
  try {
    const res = await getMyCourses()
    if (res.code === 200 && res.data) {
      const mappedCourses = res.data.map(buildMyCourse)
      allCourses.value = mappedCourses
      ongoingCourses.value = mappedCourses.filter(item => item.status === 'ongoing')
      completedCourses.value = mappedCourses.filter(item => item.status === 'completed')
    }
  } catch (error) {
    message.error('加载课程失败')
  }
}

const handleTabChange = (key: string) => {
  console.log('Tab changed to:', key)
}

const continueLearning = (course: MyCourse) => {
  router.push(`/courses/${course.courseId}`)
}

const viewDetail = (course: MyCourse) => {
  router.push(`/courses/${course.courseId}`)
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.course-cover {
  position: relative;
  height: 180px;
  overflow: hidden;
  background: #f5f5f5;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.completed-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #52c41a;
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-info {
  margin-top: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  font-size: 13px;
  color: #6b7280;
}

.course-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}
</style>
