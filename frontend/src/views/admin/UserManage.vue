<template>
  <div class="admin-page">
    <div class="back-nav">
      <a-button type="text" @click="router.back()">
        <left-outlined /> 返回
      </a-button>
    </div>
    <div class="page-header">
      <h1>用户管理</h1>
    </div>

    <a-card class="table-card">
      <a-table
        :columns="columns"
        :data-source="users"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'role'">
            <a-tag :color="record.role === 'ADMIN' ? 'red' : 'blue'">
              {{ record.role === 'ADMIN' ? '管理员' : '普通用户' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button
                v-if="record.status === 'ACTIVE'"
                type="link"
                danger
                size="small"
                @click="handleUpdateStatus(record, 'BANNED')"
              >
                禁用
              </a-button>
              <a-button
                v-else
                type="link"
                size="small"
                @click="handleUpdateStatus(record, 'ACTIVE')"
              >
                启用
              </a-button>
              <a-button
                v-if="record.role !== 'ADMIN'"
                type="link"
                size="small"
                @click="handleUpdateRole(record, 'ADMIN')"
              >
                设为管理员
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '@/api'
import type { User } from '@/types'
import { message } from 'ant-design-vue'
import { LeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const loading = ref(false)
const users = ref<User[]>([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  onChange: (page: number) => {
    pagination.current = page
    fetchUsers()
  }
})

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '学号', dataIndex: 'studentId', key: 'studentId' },
  { title: '真实姓名', dataIndex: 'realName', key: 'realName' },
  { title: '手机号', dataIndex: 'phone', key: 'phone' },
  { title: '角色', dataIndex: 'role', key: 'role', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '信誉分', dataIndex: 'creditScore', key: 'creditScore', width: 80 },
  { title: '操作', key: 'action', width: 200 }
]

const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    ACTIVE: 'green',
    INACTIVE: 'orange',
    BANNED: 'red'
  }
  return colors[status] || 'default'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    ACTIVE: '正常',
    INACTIVE: '未激活',
    BANNED: '已禁用'
  }
  return texts[status] || status
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await adminApi.getUsers(pagination.current, pagination.pageSize)
    users.value = res.data.list
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleUpdateStatus = async (user: User, status: User['status']) => {
  try {
    await adminApi.updateUserStatus(user.id, status)
    message.success('状态更新成功')
    fetchUsers()
  } catch {
    // ignore
  }
}

const handleUpdateRole = async (user: User, role: User['role']) => {
  try {
    await adminApi.updateUserRole(user.id, role)
    message.success('角色更新成功')
    fetchUsers()
  } catch {
    // ignore
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.admin-page {
  min-height: calc(100vh - 200px);
}

.back-nav {
  margin-bottom: 16px;
}

.back-nav :deep(.ant-btn) {
  color: #71717a;
  padding: 4px 8px;
}

.back-nav :deep(.ant-btn:hover) {
  background: #f4f4f5;
  color: #18181b;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #18181b;
}

.table-card {
  border-radius: 12px;
}
</style>
