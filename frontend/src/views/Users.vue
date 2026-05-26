<template>
  <div>
    <a-page-header title="用户列表" />
    
    <a-card :bordered="false">
      <a-space class="mb-4">
        <a-button type="primary" @click="showAddModal">
          <template #icon><plus-outlined /></template>
          新增用户
        </a-button>
        <a-button @click="fetchUsers">
          <template #icon><reload-outlined /></template>
          刷新
        </a-button>
      </a-space>
      
      <a-table
        :columns="columns"
        :data-source="users"
        :loading="loading"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'role'">
            <a-tag :color="record.role === 'ADMIN' ? 'red' : 'blue'">
              {{ record.role === 'ADMIN' ? '管理员' : '普通用户' }}
            </a-tag>
          </template>
          
          <template v-if="column.key === 'status'">
            <a-badge
              :status="record.status === 'ACTIVE' ? 'success' : 'error'"
              :text="record.status === 'ACTIVE' ? '正常' : '已禁用'"
            />
          </template>
          
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="editUser(record)">编辑</a-button>
              <a-popconfirm
                title="确定要删除该用户吗？"
                @confirm="deleteUser(record.id)"
              >
                <a-button type="link" danger size="small">删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      @ok="handleSubmit"
    >
      <a-form :model="formData" :label-col="{ span: 4 }">
        <a-form-item label="用户名">
          <a-input v-model:value="formData.username" placeholder="请输入用户名" :disabled="isEdit" />
        </a-form-item>
        <a-form-item label="姓名">
          <a-input v-model:value="formData.name" placeholder="请输入姓名" />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
        </a-form-item>
        <a-form-item label="年龄">
          <a-input-number v-model:value="formData.age" :min="1" :max="150" style="width: 100%" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { getUsers, createUser, updateUser, deleteUserById } from '../api/user'

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '年龄', dataIndex: 'age', key: 'age', width: 80 },
  { title: '角色', key: 'role', width: 100 },
  { title: '状态', key: 'status', width: 100 },
  { title: '操作', key: 'action', width: 150 }
]

const users = ref([])
const loading = ref(false)
const modalVisible = ref(false)
const modalTitle = ref('新增用户')
const isEdit = ref(false)
const formData = reactive({
  id: null,
  username: '',
  name: '',
  email: '',
  age: 18
})

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await getUsers()
    if (res.code === 200) {
      users.value = res.data
    }
  } catch (error) {
    message.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const showAddModal = () => {
  modalTitle.value = '新增用户'
  isEdit.value = false
  formData.id = null
  formData.username = ''
  formData.name = ''
  formData.email = ''
  formData.age = 18
  modalVisible.value = true
}

const editUser = (record) => {
  modalTitle.value = '编辑用户'
  isEdit.value = true
  formData.id = record.id
  formData.username = record.username
  formData.name = record.name
  formData.email = record.email
  formData.age = record.age
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await updateUser(formData.id, formData)
      message.success('更新成功')
    } else {
      await createUser(formData)
      message.success('创建成功')
    }
    modalVisible.value = false
    fetchUsers()
  } catch (error) {
    message.error(isEdit.value ? '更新失败' : '创建失败')
  }
}

const deleteUser = async (id) => {
  try {
    await deleteUserById(id)
    message.success('删除成功')
    fetchUsers()
  } catch (error) {
    message.error('删除失败')
  }
}

onMounted(() => {
  fetchUsers()
})
</script>
