<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-semibold text-gray-800">用户管理</h1>
          <p class="text-gray-500 mt-1">管理系统用户账户</p>
        </div>
        <a-button type="primary" @click="showCreateModal">
          <template #icon><PlusOutlined /></template>
          添加用户
        </a-button>
      </div>
    </div>

    <a-card class="mb-4">
      <a-form layout="inline">
        <a-form-item label="搜索">
          <a-input
            v-model:value="searchText"
            placeholder="搜索用户名、邮箱"
            style="width: 250px"
            allow-clear
          >
            <template #prefix>
              <SearchOutlined />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item label="角色">
          <a-select
            v-model:value="filterRole"
            placeholder="选择角色"
            style="width: 150px"
            allow-clear
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="ADMIN">管理员</a-select-option>
            <a-select-option value="TEACHER">教师</a-select-option>
            <a-select-option value="STUDENT">学员</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="filterStatus"
            placeholder="选择状态"
            style="width: 150px"
            allow-clear
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="ACTIVE">活跃</a-select-option>
            <a-select-option value="INACTIVE">未激活</a-select-option>
            <a-select-option value="BANNED">已封禁</a-select-option>
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
        :data-source="users"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'role'">
            <a-tag :color="getRoleColor(record.role)">
              {{ getRoleText(record.role) }}
            </a-tag>
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
              <a-divider type="vertical" />
              <a-popconfirm
                title="确定要删除此用户吗？"
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
      width="600px"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        layout="vertical"
      >
        <a-form-item label="用户名" name="username">
          <a-input v-model:value="formData.username" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item label="真实姓名" name="realName">
          <a-input v-model:value="formData.realName" placeholder="请输入真实姓名" />
        </a-form-item>
        <a-form-item v-if="isCreateMode" label="密码" name="password">
          <a-input-password v-model:value="formData.password" placeholder="请输入密码（至少6位）" />
        </a-form-item>
        <a-form-item label="邮箱" name="email">
          <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
        </a-form-item>
        <a-form-item label="手机号" name="phone">
          <a-input v-model:value="formData.phone" placeholder="请输入手机号" />
        </a-form-item>
        <a-form-item label="角色" name="role">
          <a-select v-model:value="formData.role" placeholder="请选择角色">
            <a-select-option value="ADMIN">管理员</a-select-option>
            <a-select-option value="TEACHER">教师</a-select-option>
            <a-select-option value="STUDENT">学员</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-select v-model:value="formData.status" placeholder="请选择状态">
            <a-select-option value="ACTIVE">活跃</a-select-option>
            <a-select-option value="INACTIVE">未激活</a-select-option>
            <a-select-option value="BANNED">已封禁</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined, SearchOutlined } from '@ant-design/icons-vue'
import type { User } from '@/types'
import { getUsers, createUser, updateUser, deleteUser } from '@/api/user'

const loading = ref(false)
const users = ref<User[]>([])
const searchText = ref('')
const filterRole = ref('')
const filterStatus = ref('')
const modalVisible = ref(false)
const modalTitle = ref('添加用户')
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
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  role: 'STUDENT',
  status: 'ACTIVE'
})

const isCreateMode = computed(() => formData.id === 0)

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度3-50个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    {
      validator: (_rule: any, value: string) => {
        if (!isCreateMode.value) {
          return Promise.resolve()
        }
        if (!value) {
          return Promise.reject('请输入密码')
        }
        if (value.length < 6) {
          return Promise.reject('密码至少6个字符')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
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
    title: '用户名',
    dataIndex: 'username',
    key: 'username'
  },
  {
    title: '真实姓名',
    dataIndex: 'realName',
    key: 'realName'
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email'
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    key: 'phone'
  },
  {
    title: '角色',
    dataIndex: 'role',
    key: 'role'
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status'
  },
  {
    title: '操作',
    key: 'action',
    width: 150
  }
]

const getRoleColor = (role: string) => {
  const colors: Record<string, string> = {
    ADMIN: 'red',
    TEACHER: 'blue',
    STUDENT: 'green'
  }
  return colors[role] || 'default'
}

const getRoleText = (role: string) => {
  const texts: Record<string, string> = {
    ADMIN: '管理员',
    TEACHER: '教师',
    STUDENT: '学员'
  }
  return texts[role] || role
}

const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    ACTIVE: 'success',
    INACTIVE: 'warning',
    BANNED: 'error'
  }
  return colors[status] || 'default'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    ACTIVE: '活跃',
    INACTIVE: '未激活',
    BANNED: '已封禁'
  }
  return texts[status] || status
}

const loadUsers = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.current - 1,
      size: pagination.pageSize
    }
    if (searchText.value) {
      params.username = searchText.value
    }
    if (filterRole.value) {
      params.role = filterRole.value
    }
    if (filterStatus.value) {
      params.status = filterStatus.value
    }
    const res = await getUsers(params)
    if (res.code === 200 && res.data) {
      users.value = res.data.content
      pagination.total = res.data.totalElements
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadUsers()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadUsers()
}

const showCreateModal = () => {
  modalTitle.value = '添加用户'
  Object.assign(formData, {
    id: 0,
    username: '',
    password: '',
    realName: '',
    email: '',
    phone: '',
    role: 'STUDENT',
    status: 'ACTIVE'
  })
  modalVisible.value = true
}

const handleEdit = (record: User) => {
  modalTitle.value = '编辑用户'
  Object.assign(formData, {
    id: record.id,
    username: record.username,
    password: '',
    realName: record.realName,
    email: record.email,
    phone: record.phone,
    role: record.role,
    status: record.status
  })
  modalVisible.value = true
}

const handleDelete = async (id: number) => {
  try {
    const res = await deleteUser(id)
    if (res.code === 200) {
      message.success('删除成功')
      loadUsers()
    }
  } catch (error) {
    console.error('删除用户失败:', error)
  }
}

const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    const payload = {
      username: formData.username,
      password: formData.password,
      realName: formData.realName,
      email: formData.email,
      phone: formData.phone,
      role: formData.role,
      status: formData.status
    }
    const res = isCreateMode.value
      ? await createUser(payload)
      : await updateUser(formData.id, payload)
    if (res.code === 200) {
      message.success('保存成功')
      modalVisible.value = false
      loadUsers()
    }
  } catch (error) {
    console.error('保存用户失败:', error)
  }
}

const handleModalCancel = () => {
  modalVisible.value = false
  formRef.value?.resetFields()
}

onMounted(() => {
  loadUsers()
})
</script>
