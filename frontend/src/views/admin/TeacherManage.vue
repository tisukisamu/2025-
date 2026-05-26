<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-semibold text-gray-800">教师管理</h1>
          <p class="text-gray-500 mt-1">管理所有教师信息</p>
        </div>
        <a-button type="primary" @click="showCreateModal">
          <template #icon><PlusOutlined /></template>
          添加教师
        </a-button>
      </div>
    </div>

    <a-card class="mb-4">
      <a-form layout="inline">
        <a-form-item label="搜索">
          <a-input
            v-model:value="searchText"
            placeholder="搜索教师姓名"
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
            <a-select-option value="ACTIVE">在职</a-select-option>
            <a-select-option value="INACTIVE">离职</a-select-option>
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
        v-for="teacher in teachers"
        :key="teacher.id"
        class="mb-4"
      >
        <a-card hoverable class="h-full">
          <div class="text-center mb-4">
            <a-avatar :size="80" :src="teacher.avatar">
              {{ teacher.name?.charAt(0) }}
            </a-avatar>
            <h3 class="text-lg font-semibold text-gray-800 mt-3 mb-1">
              {{ teacher.name }}
            </h3>
            <a-tag :color="getStatusColor(teacher.status)">
              {{ getStatusText(teacher.status) }}
            </a-tag>
          </div>
          
          <div class="space-y-2 text-sm">
            <div class="flex items-center text-gray-600">
              <PhoneOutlined class="mr-2" />
              {{ teacher.phone || '未填写' }}
            </div>
            <div class="flex items-center text-gray-600">
              <MailOutlined class="mr-2" />
              {{ teacher.email || '未填写' }}
            </div>
            <div class="flex items-center text-gray-600">
              <BookOutlined class="mr-2" />
              {{ teacher.subjects || '未填写' }}
            </div>
            <div class="flex items-center text-gray-600">
              <CalendarOutlined class="mr-2" />
              {{ teacher.experience || 0 }} 年经验
            </div>
          </div>
          
          <template #actions>
            <a-button type="link" size="small" @click="handleEdit(teacher)">
              编辑
            </a-button>
            <a-button type="link" size="small" @click="handleView(teacher)">
              查看
            </a-button>
            <a-popconfirm
              title="确定要删除此教师吗？"
              @confirm="handleDelete(teacher.id)"
            >
              <a-button type="link" size="small" danger>
                删除
              </a-button>
            </a-popconfirm>
          </template>
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
            <a-form-item label="姓名" name="name">
              <a-input v-model:value="formData.name" placeholder="请输入姓名" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="手机号" name="phone">
              <a-input v-model:value="formData.phone" placeholder="请输入手机号" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="教学经验" name="experience">
              <a-input-number
                v-model:value="formData.experience"
                :min="0"
                style="width: 100%"
                placeholder="请输入教学经验"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item label="授课科目" name="subjects">
          <a-input v-model:value="formData.subjects" placeholder="请输入授课科目" />
        </a-form-item>
        
        <a-form-item label="个人简介" name="bio">
          <a-textarea
            v-model:value="formData.bio"
            placeholder="请输入个人简介"
            :rows="4"
          />
        </a-form-item>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="状态" name="status">
              <a-select v-model:value="formData.status" placeholder="请选择状态">
                <a-select-option value="ACTIVE">在职</a-select-option>
                <a-select-option value="INACTIVE">离职</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  SearchOutlined,
  PhoneOutlined,
  MailOutlined,
  BookOutlined,
  CalendarOutlined
} from '@ant-design/icons-vue'
import type { Teacher } from '@/types'
import { getTeachers, createTeacher, updateTeacher, deleteTeacher } from '@/api/teacher'

const router = useRouter()
const loading = ref(false)
const teachers = ref<Teacher[]>([])
const searchText = ref('')
const filterStatus = ref('')
const modalVisible = ref(false)
const modalTitle = ref('添加教师')
const formRef = ref()

const pagination = reactive({
  current: 1,
  pageSize: 12,
  total: 0
})

const formData = reactive({
  id: 0,
  name: '',
  phone: '',
  email: '',
  subjects: '',
  experience: 0,
  bio: '',
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

const getStatusColor = (status: string) => {
  return status === 'ACTIVE' ? 'success' : 'default'
}

const getStatusText = (status: string) => {
  return status === 'ACTIVE' ? '在职' : '离职'
}

const loadTeachers = async () => {
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
    const res = await getTeachers(params)
    if (res.code === 200 && res.data) {
      teachers.value = res.data.content
      pagination.total = res.data.totalElements
    }
  } catch (error) {
    console.error('加载教师列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadTeachers()
}

const showCreateModal = () => {
  modalTitle.value = '添加教师'
  Object.assign(formData, {
    id: 0,
    name: '',
    phone: '',
    email: '',
    subjects: '',
    experience: 0,
    bio: '',
    status: 'ACTIVE'
  })
  modalVisible.value = true
}

const handleEdit = (record: Teacher) => {
  modalTitle.value = '编辑教师'
  Object.assign(formData, {
    id: record.id,
    name: record.name,
    phone: record.phone,
    email: record.email,
    subjects: record.subjects,
    experience: record.experience,
    bio: record.bio,
    status: record.status
  })
  modalVisible.value = true
}

const handleView = (record: Teacher) => {
  router.push(`/admin/teachers/${record.id}`)
}

const handleDelete = async (id: number) => {
  try {
    const res = await deleteTeacher(id)
    if (res.code === 200) {
      message.success('删除成功')
      loadTeachers()
    }
  } catch (error) {
    console.error('删除教师失败:', error)
  }
}

const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    const payload = {
      name: formData.name,
      phone: formData.phone,
      email: formData.email,
      subjects: formData.subjects,
      experience: formData.experience,
      bio: formData.bio,
      status: formData.status
    }
    const res = formData.id
      ? await updateTeacher(formData.id, payload)
      : await createTeacher(payload)
    if (res.code === 200) {
      message.success('保存成功')
      modalVisible.value = false
      loadTeachers()
    }
  } catch (error) {
    console.error('保存教师失败:', error)
  }
}

const handleModalCancel = () => {
  modalVisible.value = false
  formRef.value?.resetFields()
}

onMounted(() => {
  loadTeachers()
})
</script>
