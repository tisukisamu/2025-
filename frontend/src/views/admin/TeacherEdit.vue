<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <a-page-header
      :title="isEdit ? '编辑教师' : '添加教师'"
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
          <a-col :span="8">
            <a-form-item label="头像" name="avatar">
              <a-upload
                list-type="picture-card"
                :file-list="fileList"
                :before-upload="beforeUpload"
                @change="handleFileChange"
                :max-count="1"
              >
                <div v-if="fileList.length < 1" class="text-center">
                <PlusOutlined />
                <div style="margin-top: 8px">上传头像</div>
              </div>
              </a-upload>
            </a-form-item>
          </a-col>
          <a-col :span="16">
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="姓名" name="name">
                  <a-input v-model:value="formData.name" placeholder="请输入姓名" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="状态" name="status">
                  <a-select v-model:value="formData.status" placeholder="请选择状态">
                    <a-select-option value="ACTIVE">在职</a-select-option>
                    <a-select-option value="INACTIVE">离职</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="手机号" name="phone">
                  <a-input v-model:value="formData.phone" placeholder="请输入手机号" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="邮箱" name="email">
                  <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
                </a-form-item>
              </a-col>
            </a-row>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="授课科目" name="subjects">
              <a-input v-model:value="formData.subjects" placeholder="请输入授课科目，多个用逗号分隔" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="教学经验" name="experience">
              <a-input-number
                v-model:value="formData.experience"
                :min="0"
                style="width: 100%"
                placeholder="请输入教学经验（年）"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="个人简介" name="bio">
          <a-textarea
            v-model:value="formData.bio"
            placeholder="请输入个人简介"
            :rows="4"
          />
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
import type { UploadFile } from 'ant-design-vue'
import { getTeacherById, createTeacher, updateTeacher } from '@/api/teacher'
import { uploadImage } from '@/api/upload'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const formRef = ref()
const fileList = ref<UploadFile[]>([])
const uploadLoading = ref(false)

const teacherId = computed(() => route.params.id as string)
const isEdit = computed(() => !!teacherId.value && teacherId.value !== 'new')

const formData = reactive({
  name: '',
  phone: '',
  email: '',
  subjects: '',
  experience: 0,
  bio: '',
  avatar: '',
  status: 'ACTIVE'
})

const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const loadTeacher = async () => {
  if (!isEdit.value) return
  
  try {
    const res = await getTeacherById(Number(teacherId.value))
    if (res.code === 200 && res.data) {
      const teacher = res.data
      formData.name = teacher.name
      formData.phone = teacher.phone || ''
      formData.email = teacher.email || ''
      formData.subjects = teacher.subjects || ''
      formData.experience = teacher.experience || 0
      formData.bio = teacher.bio || ''
      formData.avatar = teacher.avatar || ''
      formData.status = teacher.status
      
      if (teacher.avatar) {
        fileList.value = [{
          uid: '-1',
          name: 'avatar.png',
          status: 'done',
          url: teacher.avatar
        }]
      }
    }
  } catch (error) {
    message.error('加载教师信息失败')
    console.error('加载教师失败:', error)
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
    const res = await uploadImage(file, 'avatars')
    if (res.code === 200 && res.data) {
      formData.avatar = res.data.url
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
    formData.avatar = ''
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    const submitData = {
      name: formData.name,
      phone: formData.phone,
      email: formData.email,
      subjects: formData.subjects,
      experience: formData.experience,
      bio: formData.bio,
      avatar: formData.avatar,
      status: formData.status
    }
    
    const res = isEdit.value 
      ? await updateTeacher(Number(teacherId.value), submitData)
      : await createTeacher(submitData)
    
    if (res.code === 200) {
      message.success(isEdit.value ? '教师更新成功' : '教师创建成功')
      router.push('/admin/teachers')
    }
  } catch (error) {
    console.error('保存教师失败:', error)
    message.error('保存失败，请重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTeacher()
})
</script>
