<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-semibold text-gray-800">我的课程</h1>
          <p class="text-gray-500 mt-1">管理您教授的所有课程</p>
        </div>
        <a-button type="primary" @click="showCreateModal">
          <template #icon><PlusOutlined /></template>
          添加课程
        </a-button>
      </div>
    </div>

    <a-row :gutter="16">
      <a-col
        :xs="24"
        :sm="12"
        :lg="8"
        :xl="6"
        v-for="course in courses"
        :key="course.id"
        class="mb-4"
      >
        <a-card hoverable class="h-full">
          <template #cover>
            <div class="h-40 bg-gray-200 flex items-center justify-center overflow-hidden">
              <img
                v-if="course.image || DEFAULT_COURSE_IMAGE"
                :src="course.image || DEFAULT_COURSE_IMAGE"
                alt="课程封面"
                class="w-full h-full object-cover"
              />
              <BookOutlined v-else class="text-4xl text-gray-400" />
            </div>
          </template>
          
          <template #actions>
            <a-button type="link" size="small" @click="viewDetail(course)">
              查看详情
            </a-button>
            <a-button type="link" size="small" @click="viewStudents(course)">
              查看学员
            </a-button>
          </template>
          
          <a-card-meta :title="course.name" class="mb-3">
            <template #description>
              <div class="text-gray-600 text-sm line-clamp-2">
                {{ course.description || '暂无描述' }}
              </div>
            </template>
          </a-card-meta>
          
          <div class="space-y-2">
            <div class="flex justify-between items-center text-sm">
              <span class="text-gray-500">学员数</span>
              <span class="text-gray-800">{{ course.enrolledCount }}/{{ course.capacity }}</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-gray-500 text-sm">状态</span>
              <a-tag :color="getStatusColor(course.status)">
                {{ getStatusText(course.status) }}
              </a-tag>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-modal
      v-model:open="createModalVisible"
      title="添加课程"
      :confirm-loading="submitting"
      @ok="handleSubmit"
      width="600px"
    >
      <a-form
        ref="formRef"
        :model="formState"
        :rules="formRules"
        layout="vertical"
      >
        <a-form-item label="课程名称" name="name">
          <a-input v-model:value="formState.name" placeholder="请输入课程名称" />
        </a-form-item>
        
        <a-form-item label="课程描述" name="description">
          <a-textarea
            v-model:value="formState.description"
            placeholder="请输入课程描述"
            :rows="4"
          />
        </a-form-item>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="课程类别" name="category">
              <a-select v-model:value="formState.category" placeholder="请选择课程类别">
                <a-select-option value="HIPHOP">Hip-Hop</a-select-option>
                <a-select-option value="JAZZ">爵士</a-select-option>
                <a-select-option value="BREAKING">Breaking</a-select-option>
                <a-select-option value="POPPING">Popping</a-select-option>
                <a-select-option value="LOCKING">Locking</a-select-option>
                <a-select-option value="URBAN">Urban</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="难度等级" name="level">
              <a-select v-model:value="formState.level" placeholder="请选择难度等级">
                <a-select-option value="BEGINNER">初级</a-select-option>
                <a-select-option value="INTERMEDIATE">中级</a-select-option>
                <a-select-option value="ADVANCED">高级</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="课程价格" name="price">
              <a-input-number
                v-model:value="formState.price"
                :min="0"
                :precision="2"
                style="width: 100%"
                placeholder="请输入课程价格"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="课程容量" name="capacity">
              <a-input-number
                v-model:value="formState.capacity"
                :min="1"
                style="width: 100%"
                placeholder="请输入课程容量"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item label="课程时长（课时）" name="duration">
          <a-input-number
            v-model:value="formState.duration"
            :min="1"
            style="width: 100%"
            placeholder="请输入课程时长"
          />
        </a-form-item>
        
        <a-form-item label="课程状态" name="status">
          <a-radio-group v-model:value="formState.status">
            <a-radio value="DRAFT">草稿</a-radio>
            <a-radio value="PUBLISHED">发布</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { BookOutlined, PlusOutlined } from '@ant-design/icons-vue'
import type { Course } from '@/types'
import type { CourseRequest } from '@/types/course'
import { getTeacherCourses, getCurrentTeacher } from '@/api/teacher'
import { createCourse } from '@/api/course'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const courses = ref<Course[]>([])
const DEFAULT_COURSE_IMAGE = 'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=1200&q=80'

const createModalVisible = ref(false)
const submitting = ref(false)
const formRef = ref()

const formState = reactive<CourseRequest>({
  name: '',
  description: '',
  category: 'HIPHOP',
  level: 'BEGINNER',
  price: 0,
  capacity: 20,
  duration: 24,
  status: 'DRAFT'
})

const formRules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择课程类别', trigger: 'change' }],
  level: [{ required: true, message: '请选择难度等级', trigger: 'change' }],
  price: [{ required: true, message: '请输入课程价格', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入课程容量', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入课程时长', trigger: 'blur' }],
  status: [{ required: true, message: '请选择课程状态', trigger: 'change' }]
}

const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    DRAFT: 'default',
    PUBLISHED: 'success',
    CLOSED: 'error'
  }
  return colors[status] || 'default'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    DRAFT: '草稿',
    PUBLISHED: '已发布',
    CLOSED: '已关闭'
  }
  return texts[status] || status
}

const loadCourses = async () => {
  try {
    if (userStore.isTeacher) {
      const teacherRes = await getCurrentTeacher()
      if (teacherRes.code === 200 && teacherRes.data) {
        const res = await getTeacherCourses(teacherRes.data.id)
        if (res.code === 200 && res.data) {
          courses.value = res.data
        }
      }
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
  }
}

const viewDetail = (course: Course) => {
  router.push(`/courses/${course.id}`)
}

const viewStudents = (course: Course) => {
  router.push(`/teacher/students?courseId=${course.id}`)
}

const showCreateModal = () => {
  Object.assign(formState, {
    name: '',
    description: '',
    category: 'HIPHOP',
    level: 'BEGINNER',
    price: 0,
    capacity: 20,
    duration: 24,
    status: 'DRAFT'
  })
  createModalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    let teacherId = undefined
    if (userStore.isTeacher) {
      const teacherRes = await getCurrentTeacher()
      if (teacherRes.code === 200 && teacherRes.data) {
        teacherId = teacherRes.data.id
      }
    }
    
    const courseData = {
      ...formState,
      teacherId
    }
    
    const res = await createCourse(courseData)
    if (res.code === 200) {
      message.success('课程创建成功')
      createModalVisible.value = false
      loadCourses()
    } else {
      message.error(res.message || '创建失败')
    }
  } catch (error) {
    console.error('创建课程失败:', error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
