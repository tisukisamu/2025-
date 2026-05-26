<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-semibold text-neutral-900">用户管理</h1>
      <a-input-search
        v-model:value="searchKeyword"
        placeholder="搜索用户"
        style="width: 300px"
        class="!rounded-lg"
      />
    </div>
    
    <div class="bg-white rounded-xl border border-neutral-100">
      <div class="p-4 border-b border-neutral-100">
        <a-radio-group v-model:value="roleFilter" button-style="solid">
          <a-radio-button value="all">全部用户</a-radio-button>
          <a-radio-button value="USER">求职者</a-radio-button>
          <a-radio-button value="COMPANY">企业用户</a-radio-button>
          <a-radio-button value="ADMIN">管理员</a-radio-button>
        </a-radio-group>
      </div>
      
      <a-table :columns="columns" :data-source="filteredUsers" :pagination="pagination">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'user'">
            <div class="flex items-center gap-3">
              <a-avatar class="!bg-neutral-200">
                <span class="text-neutral-600">{{ record.name.charAt(0) }}</span>
              </a-avatar>
              <div>
                <div class="font-medium text-neutral-900">{{ record.name }}</div>
                <div class="text-sm text-neutral-400">{{ record.username }}</div>
              </div>
            </div>
          </template>
          <template v-if="column.key === 'role'">
            <a-tag :color="getRoleColor(record.role)">
              {{ getRoleText(record.role) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 'ACTIVE' ? 'success' : 'error'">
              {{ record.status === 'ACTIVE' ? '正常' : '禁用' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewUser(record)">详情</a-button>
              <a-button 
                v-if="record.status === 'ACTIVE'"
                type="link" 
                size="small" 
                danger
                @click="disableUser(record)"
              >
                禁用
              </a-button>
              <a-button 
                v-else
                type="link" 
                size="small"
                @click="enableUser(record)"
              >
                启用
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>
    
    <a-modal
      v-model:open="detailVisible"
      title="用户详情"
      width="600px"
      :footer="null"
    >
      <a-descriptions :column="2" bordered v-if="currentUser">
        <a-descriptions-item label="用户名">{{ currentUser.username }}</a-descriptions-item>
        <a-descriptions-item label="姓名">{{ currentUser.name }}</a-descriptions-item>
        <a-descriptions-item label="邮箱">{{ currentUser.email }}</a-descriptions-item>
        <a-descriptions-item label="年龄">{{ currentUser.age }}</a-descriptions-item>
        <a-descriptions-item label="角色">{{ getRoleText(currentUser.role) }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="currentUser.status === 'ACTIVE' ? 'success' : 'error'">
            {{ currentUser.status === 'ACTIVE' ? '正常' : '禁用' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="注册时间" :span="2">{{ currentUser.createdAt }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { adminApi } from '@/api'

const searchKeyword = ref('')
const roleFilter = ref('all')
const detailVisible = ref(false)
const currentUser = ref<any>(null)

const pagination = {
  pageSize: 10,
  showSizeChanger: true
}

const columns = [
  { title: '用户信息', key: 'user' },
  { title: '邮箱', dataIndex: 'email' },
  { title: '年龄', dataIndex: 'age', width: 80 },
  { title: '角色', key: 'role', width: 100 },
  { title: '状态', key: 'status', width: 80 },
  { title: '注册时间', dataIndex: 'createdAt', width: 120 },
  { title: '操作', key: 'action', width: 120 }
]

const users = ref<any[]>([])

const filteredUsers = computed(() => {
  let result = users.value
  if (roleFilter.value !== 'all') {
    result = result.filter(u => u.role === roleFilter.value)
  }
  if (searchKeyword.value) {
    result = result.filter(u => 
      u.name.includes(searchKeyword.value) || 
      u.username.includes(searchKeyword.value) ||
      u.email.includes(searchKeyword.value)
    )
  }
  return result
})

const getRoleColor = (role: string) => {
  const colors: Record<string, string> = {
    USER: 'blue',
    COMPANY: 'purple',
    ADMIN: 'gold'
  }
  return colors[role] || 'default'
}

const getRoleText = (role: string) => {
  const texts: Record<string, string> = {
    USER: '求职者',
    COMPANY: '企业用户',
    ADMIN: '管理员'
  }
  return texts[role] || role
}

const viewUser = (user: any) => {
  currentUser.value = user
  detailVisible.value = true
}

const disableUser = (user: any) => {
  Modal.confirm({
    title: '确认禁用',
    content: `确定要禁用用户 "${user.name}" 吗？`,
    okText: '确认',
    okType: 'danger',
    cancelText: '取消',
    async onOk() {
      await adminApi.updateUserStatus(user.id, 'DISABLED')
      user.status = 'DISABLED'
      message.success('用户已禁用')
    }
  })
}

const enableUser = async (user: any) => {
  await adminApi.updateUserStatus(user.id, 'ACTIVE')
  user.status = 'ACTIVE'
  message.success('用户已启用')
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleDateString('zh-CN')
}

const fetchUsers = async () => {
  try {
    const res = await adminApi.getUsers()
    users.value = (res.data || []).map((item: any) => ({
      ...item,
      createdAt: formatDate(item.createdAt)
    }))
  } catch (error) {
    message.error('获取用户列表失败')
  }
}

onMounted(() => {
  fetchUsers()
})
</script>
