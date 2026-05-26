<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-semibold text-gray-800">课程管理</h1>
          <p class="text-gray-500 mt-1">管理所有课程信息</p>
        </div>
        <a-button type="primary" @click="showCreateModal">
          <template #icon><PlusOutlined /></template>
          添加课程
        </a-button>
      </div>
    </div>

    <a-card class="mb-4">
      <a-form layout="inline">
        <a-form-item label="搜索">
          <a-input
            v-model:value="searchText"
            placeholder="搜索课程名称"
            style="width: 250px"
            allow-clear
          >
            <template #prefix>
              <SearchOutlined />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="filterStatus"
            placeholder="选择状态"
            style="width: 150px"
            allow-clear
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="DRAFT">草稿</a-select-option>
            <a-select-option value="PUBLISHED">已发布</a-select-option>
            <a-select-option value="CLOSED">已关闭</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            搜索
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>

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
            <div class="h-40 bg-gray-200 flex items-center justify-center">
              <img
                v-if="course.image"
                :src="course.image"
                :alt="course.name"
                class="w-full h-full object-cover"
              />
              <BookOutlined v-else class="text-4xl text-gray-400" />
            </div>
          </template>
          
          <template #actions>
            <a-button type="link" size="small" @click="handleEdit(course)">
              编辑
            </a-button>
            <a-button type="link" size="small" @click="handleView(course)">
              查看
            </a-button>
            <a-popconfirm
              title="确定要删除此课程吗？"
              @confirm="handleDelete(course.id)"
            >
              <a-button type="link" size="small" danger>
                删除
              </a-button>
            </a-popconfirm>
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
              <span class="text-gray-500">价格</span>
              <span class="text-gray-800 font-medium">¥{{ course.price }}</span>
            </div>
            <div class="flex justify-between items-center text-sm">
              <span class="text-gray-500">容量</span>
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
      v-model:open="modalVisible"
      :title="modalTitle"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
      width="700px"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        layout="vertical"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="课程名称" name="name">
              <a-input v-model:value="formData.name" placeholder="请输入课程名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="授课教师" name="teacherId">
              <a-select v-model:value="formData.teacherId" placeholder="请选择教师">
                <a-select-option
                  v-for="teacher in teachers"
                  :key="teacher.id"
                  :value="teacher.id"
                >
                  {{ teacher.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item label="课程描述" name="description">
          <a-textarea
            v-model:value="formData.description"
            placeholder="请输入课程描述"
            :rows="4"
          />
        </a-form-item>
        
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="价格" name="price">
              <a-input-number
                v-model:value="formData.price"
                :min="0"
                :precision="2"
                style="width: 100%"
                placeholder="请输入价格"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="容量" name="capacity">
              <a-input-number
                v-model:value="formData.capacity"
                :min="1"
                style="width: 100%"
                placeholder="请输入容量"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="时长(分钟)" name="duration">
              <a-input-number
                v-model:value="formData.duration"
                :min="1"
                style="width: 100%"
                placeholder="请输入时长"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="课程等级" name="level">
              <a-select v-model:value="formData.level" placeholder="请选择等级">
                <a-select-option value="BEGINNER">初级</a-select-option>
                <a-select-option value="INTERMEDIATE">中级</a-select-option>
                <a-select-option value="ADVANCED">高级</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态" name="status">
              <a-select v-model:value="formData.status" placeholder="请选择状态">
                <a-select-option value="DRAFT">草稿</a-select-option>
                <a-select-option value="PUBLISHED">已发布</a-select-option>
                <a-select-option value="CLOSED">已关闭</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item label="课程封面" name="image">
          <a-upload
            list-type="picture-card"
            :file-list="fileList"
            :before-upload="beforeUpload"
            @change="handleFileChange"
            :max-count="1"
          >
            <div v-if="fileList.length < 1" class="text-center">
              <PlusOutlined />
              <div style="margin-top: 8px">上传封面</div>
            </div>
          </a-upload>
          <div class="text-gray-500 text-sm mt-2">支持 JPG、PNG 格式，建议尺寸 800x600，文件大小不超过 2MB</div>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { PlusOutlined, SearchOutlined, BookOutlined } from '@ant-design/icons-vue'
import type { Course, Teacher } from '@/types'
import type { UploadFile } from 'ant-design-vue'
import { getCourses, createCourse, updateCourse, deleteCourse } from '@/api/course'
import { getActiveTeachers } from '@/api/teacher'
import { uploadImage } from '@/api/upload'

const router = useRouter()
const loading = ref(false)
const courses = ref<Course[]>([])
const teachers = ref<Teacher[]>([])
const searchText = ref('')
const filterStatus = ref('')
const modalVisible = ref(false)
const modalTitle = ref('添加课程')
const formRef = ref()
const fileList = ref<UploadFile[]>([])
const uploadLoading = ref(false)

const pagination = reactive({
  current: 1,
  pageSize: 12,
  total: 0
})

const formData = reactive({
  id: 0,
  name: '',
  description: '',
  teacherId: undefined as number | undefined,
  price: 0,
  capacity: 20,
  duration: 60,
  level: 'BEGINNER',
  status: 'DRAFT',
  image: ''
})

const rules = {
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' }
  ],
  teacherId: [
    { required: true, message: '请选择授课教师', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ],
  capacity: [
    { required: true, message: '请输入容量', trigger: 'blur' }
  ]
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
  loading.value = true
  try {
    const params: any = {
      page: pagination.current - 1,
      size: pagination.pageSize
    }
    if (searchText.value) {
      params.name = searchText.value
    }
    if (filterStatus.value) {
      params.status = filterStatus.value
    }
    const res = await getCourses(params)
    if (res.code === 200 && res.data) {
      courses.value = res.data.content
      pagination.total = res.data.totalElements
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
  } finally {
    loading.value = false
  }
}

const loadTeachers = async () => {
  try {
    const res = await getActiveTeachers()
    if (res.code === 200 && res.data) {
      teachers.value = res.data
    }
  } catch (error) {
    console.error('加载教师列表失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadCourses()
}

const showCreateModal = () => {
  modalTitle.value = '添加课程'
  Object.assign(formData, {
    id: 0,
    name: '',
    description: '',
    teacherId: undefined,
    price: 0,
    capacity: 20,
    duration: 60,
    level: 'BEGINNER',
    status: 'DRAFT',
    image: ''
  })
  fileList.value = []
  modalVisible.value = true
}

const handleEdit = (record: Course) => {
  modalTitle.value = '编辑课程'
  Object.assign(formData, {
    id: record.id,
    name: record.name,
    description: record.description,
    teacherId: record.teacher?.id,
    price: record.price,
    capacity: record.capacity,
    duration: record.duration,
    level: record.level,
    status: record.status,
    image: record.image || ''
  })
  fileList.value = record.image ? [{
    uid: '-1',
    name: 'image.png',
    status: 'done',
    url: record.image
  }] : []
  modalVisible.value = true
}

const beforeUpload = async (file: File) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('只能上传 JPG/PNG 格式的图片！')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB！')
    return false
  }
  
  uploadLoading.value = true
  try {
    const res = await uploadImage(file, 'courses')
    if (res.code === 200 && res.data) {
      formData.image = res.data.url
      message.success('图片上传成功')
    }
  } catch (error) {
    message.error('图片上传失败')
    console.error('上传失败:', error)
  } finally {
    uploadLoading.value = false
  }
  
  return false
}

const handleFileChange = ({ fileList: newFileList }: { fileList: UploadFile[] }) => {
  fileList.value = newFileList
  if (newFileList.length === 0) {
    formData.image = ''
  }
}

const handleView = (record: Course) => {
  router.push(`/admin/courses/${record.id}`)
}

const handleDelete = async (id: number) => {
  try {
    const res = await deleteCourse(id)
    if (res.code === 200) {
      message.success('删除成功')
      loadCourses()
    }
  } catch (error) {
    console.error('删除课程失败:', error)
  }
}

const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    const submitData = {
      name: formData.name,
      description: formData.description,
      teacherId: formData.teacherId,
      price: formData.price,
      capacity: formData.capacity,
      duration: formData.duration,
      level: formData.level,
      status: formData.status,
      image: formData.image
    }
    const res = formData.id
      ? await updateCourse(formData.id, submitData)
      : await createCourse(submitData)
    if (res.code === 200) {
      message.success('保存成功')
      modalVisible.value = false
      fileList.value = []
      formRef.value?.resetFields()
      loadCourses()
    }
  } catch (error) {
    console.error('保存课程失败:', error)
    message.error('保存失败，请重试')
  }
}

const handleModalCancel = () => {
  modalVisible.value = false
  formRef.value?.resetFields()
}

onMounted(() => {
  loadCourses()
  loadTeachers()
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
