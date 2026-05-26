<template>
  <div>
    <a-page-header
      title="管理后台"
      sub-title="管理员专用功能"
    />
    
    <a-row :gutter="16" class="mb-6">
      <a-col :span="6">
        <a-card class="flex items-center p-4 border border-#e5e7eb rounded-xl">
          <bar-chart-outlined class="text-40px mr-4 text-#111111" />
          <div class="flex-1">
            <div class="text-28px font-bold text-gray-800">{{ stats.totalUsers }}</div>
            <div class="text-gray-500 text-sm">总用户数</div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="flex items-center p-4 border border-#e5e7eb rounded-xl">
          <user-outlined class="text-40px mr-4 text-#374151" />
          <div class="flex-1">
            <div class="text-28px font-bold text-gray-800">{{ stats.activeUsers }}</div>
            <div class="text-gray-500 text-sm">活跃用户</div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="flex items-center p-4 border border-#e5e7eb rounded-xl">
          <stop-outlined class="text-40px mr-4 text-#6b7280" />
          <div class="flex-1">
            <div class="text-28px font-bold text-gray-800">{{ stats.disabledUsers }}</div>
            <div class="text-gray-500 text-sm">已禁用</div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="flex items-center p-4 border border-#e5e7eb rounded-xl">
          <crown-outlined class="text-40px mr-4 text-#4b5563" />
          <div class="flex-1">
            <div class="text-28px font-bold text-gray-800">{{ stats.adminCount }}</div>
            <div class="text-gray-500 text-sm">管理员</div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-card title="用户管理" class="mt-4 border border-#e5e7eb rounded-xl">
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
              <a-button
                type="link"
                size="small"
                @click="toggleStatus(record)"
              >
                {{ record.status === 'ACTIVE' ? '禁用' : '启用' }}
              </a-button>
              <a-button
                type="link"
                size="small"
                @click="toggleRole(record)"
              >
                {{ record.role === 'ADMIN' ? '设为普通用户' : '设为管理员' }}
              </a-button>
              <a-popconfirm
                title="确定删除此用户吗？"
                @confirm="handleDeleteUser(record.id)"
              >
                <a-button type="link" danger size="small">删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  BarChartOutlined,
  UserOutlined,
  StopOutlined,
  CrownOutlined
} from '@ant-design/icons-vue'
import { getAllUsers, updateUserStatus, updateUserRole, deleteUser, getDashboardStats } from '../../api/admin'

const loading = ref(false)
const users = ref([])
const stats = ref({
  totalUsers: 0,
  activeUsers: 0,
  disabledUsers: 0,
  adminCount: 0
})

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '年龄', dataIndex: 'age', key: 'age', width: 80 },
  { title: '角色', key: 'role', width: 100 },
  { title: '状态', key: 'status', width: 100 },
  { title: '注册时间', dataIndex: 'createdAt', key: 'createdAt' },
  { title: '操作', key: 'action', width: 250 }
]

const fetchStats = async () => {
  try {
    const res = await getDashboardStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('获取统计失败', error)
  }
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await getAllUsers()
    if (res.code === 200) {
      users.value = res.data
    }
  } catch (error) {
    message.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const toggleStatus = async (record) => {
  const newStatus = record.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
  try {
    const res = await updateUserStatus(record.id, newStatus)
    if (res.code === 200) {
      message.success('状态更新成功')
      fetchUsers()
      fetchStats()
    }
  } catch (error) {
    message.error('状态更新失败')
  }
}

const toggleRole = async (record) => {
  const newRole = record.role === 'ADMIN' ? 'USER' : 'ADMIN'
  try {
    const res = await updateUserRole(record.id, newRole)
    if (res.code === 200) {
      message.success('角色更新成功')
      fetchUsers()
      fetchStats()
    }
  } catch (error) {
    message.error('角色更新失败')
  }
}

const handleDeleteUser = async (id) => {
  try {
    const res = await deleteUser(id)
    if (res.code === 200) {
      message.success('删除成功')
      fetchUsers()
      fetchStats()
    }
  } catch (error) {
    message.error('删除失败')
  }
}

onMounted(() => {
  fetchUsers()
  fetchStats()
})
</script>
