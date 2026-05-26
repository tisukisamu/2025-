<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <a-page-header
      :title="isEdit ? '编辑课程' : '添加课程'"
      @back="() => router.back()"
    >
      <template #extra>
        <a-space>
          <a-button @click="router.back()">取消</a-button>
          <a-button type="primary" :loading="loading" @click="handleSubmit">
            保存
          </a-button>
        </a-space>
      </template>
    </a-page-header>

    <a-card class="mt-4">
      <a-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        layout="vertical"
      >
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="课程名称" name="name">
              <a-input v-model:value="formData.name" placeholder="请输入课程名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="授课教师" name="teacherId">
              <a-select v-model:value="formData.teacherId" placeholder="请选择教师" allow-clear>
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
        
        <a-row :gutter="24">
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

        <a-row :gutter="24">
          <a-col :span="8">
            <a-form-item label="课程类别" name="category">
              <a-select v-model:value="formData.category" placeholder="请选择类别" allow-clear>
                <a-select-option value="HIPHOP">Hip-Hop</a-select-option>
                <a-select-option value="JAZZ">爵士</a-select-option>
                <a-select-option value="BREAKING">Breaking</a-select-option>
                <a-select-option value="POPPING">Popping</a-select-option>
                <a-select-option value="LOCKING">Locking</a-select-option>
                <a-select-option value="URBAN">Urban</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="课程等级" name="level">
              <a-select v-model:value="formData.level" placeholder="请选择等级">
                <a-select-option value="BEGINNER">初级</a-select-option>
                <a-select-option value="INTERMEDIATE">中级</a-select-option>
                <a-select-option value="ADVANCED">高级</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
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
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import type { Teacher } from '@/types'
import type { UploadFile } from 'ant-design-vue'
import { getCourseById, createCourse, updateCourse } from '@/api/course'
import { getActiveTeachers } from '@/api/teacher'
import { uploadImage } from '@/api/upload'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const formRef = ref()
const teachers = ref<Teacher[]>([])
const fileList = ref<UploadFile[]>([])
const uploadLoading = ref(false)

const courseId = computed(() => route.params.id as string)
const isEdit = computed(() => !!courseId.value && courseId.value !== 'new')

const formData = reactive({
  name: '',
  description: '',
  teacherId: undefined as number | undefined,
  category: undefined as string | undefined,
  level: 'BEGINNER' as string,
  price: 0,
  capacity: 20,
  duration: 60,
  image: '',
  status: 'DRAFT'
})

const rules = {
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ],
  capacity: [
    { required: true, message: '请输入容量', trigger: 'blur' }
  ]
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

const loadCourse = async () => {
  if (!isEdit.value) return
  
  try {
    const res = await getCourseById(Number(courseId.value))
    if (res.code === 200 && res.data) {
      const course = res.data
      formData.name = course.name
      formData.description = course.description || ''
      formData.teacherId = course.teacher?.id
      formData.category = course.category
      formData.level = course.level
      formData.price = course.price
      formData.capacity = course.capacity
      formData.duration = course.duration
      formData.image = course.image || ''
      formData.status = course.status
      
      if (course.image) {
        fileList.value = [{
          uid: '-1',
          name: 'image.png',
          status: 'done',
          url: course.image
        }]
      }
    }
  } catch (error) {
    message.error('加载课程信息失败')
    console.error('加载课程失败:', error)
  }
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

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    const submitData = {
      name: formData.name,
      description: formData.description,
      teacherId: formData.teacherId,
      category: formData.category,
      level: formData.level,
      price: formData.price,
      capacity: formData.capacity,
      duration: formData.duration,
      image: formData.image,
      status: formData.status
    }
    
    const res = isEdit.value 
      ? await updateCourse(Number(courseId.value), submitData)
      : await createCourse(submitData)
    
    if (res.code === 200) {
      message.success(isEdit.value ? '课程更新成功' : '课程创建成功')
      if (route.path.startsWith('/teacher/')) {
        router.push('/teacher/courses')
      } else {
        router.push('/admin/courses')
      }
    }
  } catch (error) {
    console.error('保存课程失败:', error)
    message.error('保存失败，请重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTeachers()
  loadCourse()
})
</script>
