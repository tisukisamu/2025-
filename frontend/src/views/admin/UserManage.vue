<template>
  <div class="user-manage-page">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">用户管理</h1>
        <p class="page-subtitle">管理系统用户账户</p>
      </div>
      <a-button @click="goBack">返回后台</a-button>
    </div>

    <div class="filter-bar">
      <a-space>
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索用户名/邮箱"
          style="width: 250px"
          @search="handleSearch"
        />
        <a-select v-model:value="roleFilter" placeholder="角色筛选" style="width: 120px" allowClear @change="handleFilter">
          <a-select-option value="USER">普通用户</a-select-option>
          <a-select-option value="ADMIN">管理员</a-select-option>
        </a-select>
        <a-select v-model:value="statusFilter" placeholder="状态筛选" style="width: 120px" allowClear @change="handleFilter">
          <a-select-option :value="1">启用</a-select-option>
          <a-select-option :value="0">禁用</a-select-option>
        </a-select>
      </a-space>
    </div>

    <a-table
      :columns="columns"
      :data-source="users"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      row-key="id"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'avatar'">
          <a-avatar :style="{ backgroundColor: record.role === 'ADMIN' ? '#ff4d4f' : '#262626' }">
            {{ record.username?.charAt(0)?.toUpperCase() }}
          </a-avatar>
        </template>
        <template v-if="column.key === 'role'">
          <a-tag :color="record.role === 'ADMIN' ? 'red' : 'blue'">
            {{ record.role === 'ADMIN' ? '管理员' : '普通用户' }}
          </a-tag>
        </template>
        <template v-if="column.key === 'status'">
          <a-switch
            :checked="record.status === 1"
            @change="(checked) => handleStatusChange(record, checked)"
          />
        </template>
        <template v-if="column.key === 'createdAt'">
          {{ formatDate(record.createdAt) }}
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="showEditModal(record)">
              编辑
            </a-button>
            <a-popconfirm
              title="确定要删除此用户吗？"
              @confirm="handleDelete(record.id)"
            >
              <a-button type="link" size="small" danger>删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="editModalVisible"
      title="编辑用户"
      @ok="handleEditSubmit"
      :confirmLoading="editLoading"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="用户名">
          <a-input v-model:value="editForm.username" disabled />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-model:value="editForm.email" />
        </a-form-item>
        <a-form-item label="姓名">
          <a-input v-model:value="editForm.name" />
        </a-form-item>
        <a-form-item label="角色">
          <a-select v-model:value="editForm.role">
            <a-select-option value="USER">普通用户</a-select-option>
            <a-select-option value="ADMIN">管理员</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getUserList, updateUser, deleteUser } from '../../api/user'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const editModalVisible = ref(false)
const editLoading = ref(false)
const searchKeyword = ref('')
const roleFilter = ref(undefined)
const statusFilter = ref(undefined)
const users = ref([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条`
})

const editForm = reactive({
  id: null,
  username: '',
  email: '',
  name: '',
  role: 'USER'
})

const columns = [
  { title: '头像', key: 'avatar', width: 60 },
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '角色', dataIndex: 'role', key: 'role', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 80 },
  { title: '注册时间', dataIndex: 'createdAt', key: 'createdAt', width: 120 },
  { title: '操作', key: 'action', width: 140 }
]

const formatDate = (date) => dayjs(date).format('YYYY-MM-DD')

const goBack = () => {
  router.push('/admin')
}

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getUserList()
    if (res.code === 200) {
      const payload = res.data
      if (Array.isArray(payload)) {
        users.value = payload
      } else if (Array.isArray(payload?.records)) {
        users.value = payload.records
      } else if (Array.isArray(payload?.list)) {
        users.value = payload.list
      } else {
        users.value = []
      }
      pagination.total = users.value.length
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

const handleFilter = () => {
  pagination.current = 1
  loadUsers()
}

const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadUsers()
}

const handleStatusChange = async (record, checked) => {
  try {
    await updateUser(record.id, { status: checked ? 1 : 0 })
    message.success('状态更新成功')
    loadUsers()
  } catch (error) {
    console.error('更新状态失败:', error)
  }
}

const showEditModal = (record) => {
  editForm.id = record.id
  editForm.username = record.username
  editForm.email = record.email
  editForm.name = record.name
  editForm.role = record.role
  editModalVisible.value = true
}

const handleEditSubmit = async () => {
  editLoading.value = true
  try {
    await updateUser(editForm.id, {
      email: editForm.email,
      name: editForm.name,
      role: editForm.role
    })
    message.success('更新成功')
    editModalVisible.value = false
    loadUsers()
  } catch (error) {
    console.error('更新失败:', error)
  } finally {
    editLoading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await deleteUser(id)
    message.success('删除成功')
    loadUsers()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-manage-page {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
}

.page-header {
  margin-bottom: 24px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #262626;
  margin: 0;
}

.page-subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 4px 0 0;
}

.filter-bar {
  margin-bottom: 24px;
}
</style>
