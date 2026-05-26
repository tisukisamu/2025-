<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <a-page-header
      :title="teacher.name"
      :sub-title="teacher.subjects"
      @back="() => $router.back()"
    >
      <template #extra>
        <a-space>
          <a-button @click="editTeacher">编辑信息</a-button>
          <a-button type="primary" @click="showAssignCourseModal">分配课程</a-button>
        </a-space>
      </template>
    </a-page-header>

    <a-row :gutter="16" class="mt-6">
      <a-col :xs="24" :lg="8">
        <a-card class="mb-4">
          <div class="text-center">
            <a-avatar :size="120" :src="teacher.avatar">
              <template #icon><UserOutlined /></template>
              {{ teacher.name?.charAt(0) }}
            </a-avatar>
            <h2 class="text-2xl font-bold mt-4 mb-2">{{ teacher.name }}</h2>
            <p class="text-gray-600 mb-2">{{ teacher.subjects || '暂无授课科目' }}</p>
            <a-tag :color="teacher.status === 'ACTIVE' ? 'green' : 'red'">
              {{ teacher.status === 'ACTIVE' ? '在职' : '离职' }}
            </a-tag>
          </div>
        </a-card>

        <a-card title="基本信息" class="mb-4">
          <a-descriptions :column="1" size="small">
            <a-descriptions-item label="联系电话">{{ teacher.phone || '未设置' }}</a-descriptions-item>
            <a-descriptions-item label="电子邮箱">{{ teacher.email || '未设置' }}</a-descriptions-item>
            <a-descriptions-item label="教学经验">{{ teacher.experience ? teacher.experience + '年' : '未设置' }}</a-descriptions-item>
          </a-descriptions>
        </a-card>

        <a-card title="个人简介">
          <p class="text-gray-700 leading-relaxed">{{ teacher.bio || '暂无简介' }}</p>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="16">
        <a-card title="授课课程" class="mb-4">
          <a-table 
            :columns="courseColumns" 
            :data-source="teacherCourses" 
            :pagination="false"
            :loading="coursesLoading"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="getCourseStatusColor(record.status)">
                  {{ getCourseStatusText(record.status) }}
                </a-tag>
              </template>
              <template v-if="column.key === 'price'">
                ¥{{ record.price }}
              </template>
              <template v-if="column.key === 'capacity'">
                {{ record.enrolledCount || 0 }} / {{ record.capacity }}
              </template>
              <template v-if="column.key === 'action'">
                <a-button type="link" size="small" @click="viewCourse(record)">查看</a-button>
              </template>
            </template>
          </a-table>
          <a-empty v-if="!coursesLoading && teacherCourses.length === 0" description="暂无授课课程" />
        </a-card>
      </a-col>
    </a-row>

    <a-modal
      v-model:open="assignModalVisible"
      title="分配课程"
      @ok="handleAssignCourse"
      :confirm-loading="assignLoading"
      width="800px"
    >
      <a-table
        :columns="assignColumns"
        :data-source="availableCourses"
        :row-selection="rowSelection"
        :pagination="false"
        :loading="assignModalLoading"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getCourseStatusColor(record.status)">
              {{ getCourseStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'price'">
            ¥{{ record.price }}
          </template>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined } from '@ant-design/icons-vue'
import type { Teacher, Course } from '@/types'
import { getTeacherById, getTeacherCourses } from '@/api/teacher'
import { getCourses, updateCourse } from '@/api/course'

const route = useRoute()
const router = useRouter()

const teacher = reactive<Partial<Teacher>>({
  id: 0,
  name: '',
  phone: '',
  email: '',
  subjects: '',
  experience: 0,
  bio: '',
  avatar: '',
  status: 'ACTIVE'
})

const teacherCourses = ref<Course[]>([])
const coursesLoading = ref(false)
const assignModalVisible = ref(false)
const assignLoading = ref(false)
const assignModalLoading = ref(false)
const availableCourses = ref<Course[]>([])
const selectedCourseIds = ref<number[]>([])

const courseColumns = [
  { title: '课程名称', dataIndex: 'name', key: 'name' },
  { title: '类别', dataIndex: 'category', key: 'category' },
  { title: '价格', key: 'price' },
  { title: '容量', key: 'capacity' },
  { title: '状态', key: 'status' },
  { title: '操作', key: 'action' }
]

const assignColumns = [
  { title: '课程名称', dataIndex: 'name', key: 'name' },
  { title: '类别', dataIndex: 'category', key: 'category' },
  { title: '价格', key: 'price' },
  { title: '状态', key: 'status' }
]

const rowSelection = computed(() => ({
  selectedRowKeys: selectedCourseIds.value,
  onChange: (keys: number[]) => {
    selectedCourseIds.value = keys
  }
}))

const getCourseStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    PUBLISHED: 'green',
    DRAFT: 'default',
    CLOSED: 'red'
  }
  return colorMap[status] || 'default'
}

const getCourseStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    PUBLISHED: '已发布',
    DRAFT: '草稿',
    CLOSED: '已关闭'
  }
  return textMap[status] || status
}

const loadTeacher = async () => {
  const teacherId = route.params.id
  if (!teacherId) return
  
  try {
    const res = await getTeacherById(Number(teacherId))
    if (res.code === 200 && res.data) {
      Object.assign(teacher, res.data)
    }
  } catch (error) {
    message.error('加载教师信息失败')
    console.error('加载教师失败:', error)
  }
}

const loadTeacherCourses = async () => {
  const teacherId = route.params.id
  if (!teacherId) return
  
  coursesLoading.value = true
  try {
    const res = await getTeacherCourses(Number(teacherId))
    if (res.code === 200 && res.data) {
      teacherCourses.value = res.data
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
  } finally {
    coursesLoading.value = false
  }
}

const showAssignCourseModal = async () => {
  assignModalVisible.value = true
  assignModalLoading.value = true
  
  try {
    const res = await getCourses({ page: 0, size: 100 })
    if (res.code === 200 && res.data) {
      const currentCourseIds = teacherCourses.value.map(c => c.id)
      availableCourses.value = res.data.content.filter(
        (c: Course) => !currentCourseIds.includes(c.id)
      )
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
  } finally {
    assignModalLoading.value = false
  }
}

const handleAssignCourse = async () => {
  if (selectedCourseIds.value.length === 0) {
    message.warning('请选择要分配的课程')
    return
  }
  
  assignLoading.value = true
  try {
    const promises = selectedCourseIds.value.map(async courseId => {
      const course = availableCourses.value.find(c => c.id === courseId)
      if (course) {
        return updateCourse(courseId, {
          name: course.name,
          description: course.description,
          teacherId: teacher.id,
          category: course.category,
          level: course.level,
          price: course.price,
          capacity: course.capacity,
          duration: course.duration,
          image: course.image,
          status: course.status
        })
      }
    })
    
    await Promise.all(promises)
    message.success('课程分配成功')
    assignModalVisible.value = false
    selectedCourseIds.value = []
    loadTeacherCourses()
  } catch (error) {
    message.error('课程分配失败')
    console.error('分配失败:', error)
  } finally {
    assignLoading.value = false
  }
}

const editTeacher = () => {
  router.push(`/admin/teachers/${teacher.id}/edit`)
}

const viewCourse = (course: Course) => {
  router.push(`/admin/courses/${course.id}`)
}

onMounted(() => {
  loadTeacher()
  loadTeacherCourses()
})
</script>

<style scoped>
:deep(.ant-page-header) {
  background: white;
  padding: 16px 24px;
  border-radius: 8px;
}
</style>
