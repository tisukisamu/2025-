<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-semibold text-gray-800">学员管理</h1>
          <p class="text-gray-500 mt-1">管理所有学员信息</p>
        </div>
        <a-button type="primary" @click="showCreateModal">
          <template #icon><PlusOutlined /></template>
          添加学员
        </a-button>
      </div>
    </div>

    <a-card class="mb-4">
      <a-form layout="inline">
        <a-form-item label="搜索">
          <a-input
            v-model:value="searchText"
            placeholder="搜索学员姓名"
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
            <a-select-option value="ACTIVE">在读</a-select-option>
            <a-select-option value="INACTIVE">休学</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            搜索
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card>
      <a-table
        :columns="columns"
        :data-source="students"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'gender'">
            {{ record.gender === 'MALE' ? '男' : record.gender === 'FEMALE' ? '女' : '其他' }}
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleEdit(record)">
                编辑
              </a-button>
              <a-button type="link" size="small" @click="handleViewCourses(record)">
                查看课程
              </a-button>
              <a-popconfirm
                title="确定要删除此学员吗？"
                @confirm="handleDelete(record.id)"
              >
                <a-button type="link" size="small" danger>
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

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
            <a-form-item label="性别" name="gender">
              <a-select v-model:value="formData.gender" placeholder="请选择性别">
                <a-select-option value="MALE">男</a-select-option>
                <a-select-option value="FEMALE">女</a-select-option>
                <a-select-option value="OTHER">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
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
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="出生日期" name="birthDate">
              <a-date-picker
                v-model:value="formData.birthDate"
                style="width: 100%"
                placeholder="请选择出生日期"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态" name="status">
              <a-select v-model:value="formData.status" placeholder="请选择状态">
                <a-select-option value="ACTIVE">在读</a-select-option>
                <a-select-option value="INACTIVE">休学</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="紧急联系人" name="emergencyContact">
              <a-input v-model:value="formData.emergencyContact" placeholder="请输入紧急联系人" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="紧急联系电话" name="emergencyPhone">
              <a-input v-model:value="formData.emergencyPhone" placeholder="请输入紧急联系电话" />
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
import { PlusOutlined, SearchOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import type { Student, StudentRequest } from '@/types'
import { getStudents, createStudent, updateStudent, deleteStudent } from '@/api/student'

const router = useRouter()
const loading = ref(false)
const students = ref<Student[]>([])
const searchText = ref('')
const filterStatus = ref('')
const modalVisible = ref(false)
const modalTitle = ref('添加学员')
const formRef = ref()

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`
})

const formData = reactive({
  id: 0,
  name: '',
  phone: '',
  email: '',
  gender: 'MALE',
  birthDate: '',
  emergencyContact: '',
  emergencyPhone: '',
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

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80
  },
  {
    title: '姓名',
    dataIndex: 'name',
    key: 'name'
  },
  {
    title: '性别',
    dataIndex: 'gender',
    key: 'gender'
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    key: 'phone'
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email'
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status'
  },
  {
    title: '操作',
    key: 'action',
    width: 200
  }
]

const getStatusColor = (status: string) => {
  return status === 'ACTIVE' ? 'success' : 'warning'
}

const getStatusText = (status: string) => {
  return status === 'ACTIVE' ? '在读' : '休学'
}

const loadStudents = async () => {
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
    const res = await getStudents(params)
    if (res.code === 200 && res.data) {
      students.value = res.data.content
      pagination.total = res.data.totalElements
    }
  } catch (error) {
    console.error('加载学员列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadStudents()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadStudents()
}

const showCreateModal = () => {
  modalTitle.value = '添加学员'
  Object.assign(formData, {
    id: 0,
    name: '',
    phone: '',
    email: '',
    gender: 'MALE',
    birthDate: '',
    emergencyContact: '',
    emergencyPhone: '',
    status: 'ACTIVE'
  })
  modalVisible.value = true
}

const handleEdit = (record: Student) => {
  modalTitle.value = '编辑学员'
  Object.assign(formData, {
    id: record.id,
    name: record.name,
    phone: record.phone,
    email: record.email,
    gender: record.gender,
    birthDate: record.birthDate ? dayjs(record.birthDate) : '',
    emergencyContact: record.emergencyContact,
    emergencyPhone: record.emergencyPhone,
    status: record.status
  })
  modalVisible.value = true
}

const handleViewCourses = (record: Student) => {
  router.push(`/admin/students/${record.id}`)
}

const handleDelete = async (id: number) => {
  try {
    const res = await deleteStudent(id)
    if (res.code === 200) {
      message.success('删除成功')
      loadStudents()
    }
  } catch (error) {
    console.error('删除学员失败:', error)
  }
}

const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    const parsedBirthDate = formData.birthDate ? dayjs(formData.birthDate as any) : null
    const payload: StudentRequest = {
      name: formData.name,
      phone: formData.phone,
      email: formData.email,
      gender: formData.gender as any,
      birthDate: parsedBirthDate && parsedBirthDate.isValid() ? parsedBirthDate.format('YYYY-MM-DD') : undefined,
      emergencyContact: formData.emergencyContact,
      emergencyPhone: formData.emergencyPhone,
      status: formData.status as any
    }
    const res = formData.id
      ? await updateStudent(formData.id, payload)
      : await createStudent(payload)
    if (res.code === 200) {
      message.success('保存成功')
      modalVisible.value = false
      loadStudents()
    }
  } catch (error) {
    console.error('保存学员失败:', error)
  }
}

const handleModalCancel = () => {
  modalVisible.value = false
  formRef.value?.resetFields()
}

onMounted(() => {
  loadStudents()
})
</script>
