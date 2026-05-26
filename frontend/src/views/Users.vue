<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-#111111 text-white p-5 flex items-center justify-between">
      <div>
        <h1 class="text-22px m-0">用户管理</h1>
        <p class="m-0 mt-2 text-#9ca3af">管理系统用户账号与权限</p>
      </div>
      <a-button @click="fetchUsers">
        <reload-outlined class="mr-1" />刷新
      </a-button>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <a-table
        :columns="columns"
        :data-source="users"
        :loading="loading"
        row-key="id"
        :pagination="{ pageSize: 10 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'username'">
            <div class="flex items-center gap-2">
              <a-avatar :style="{ backgroundColor: '#111111' }">
                {{ (record.name || record.username || '?').slice(0, 1) }}
              </a-avatar>
              <span>{{ record.username }}</span>
            </div>
          </template>
          
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
              <a-dropdown>
                <a-button size="small">
                  操作 <down-outlined />
                </a-button>
                <template #overlay>
                  <a-menu>
                    <a-menu-item v-if="record.status === 'ACTIVE'" @click="handleStatusChange(record, 'DISABLED')">
                      禁用账号
                    </a-menu-item>
                    <a-menu-item v-else @click="handleStatusChange(record, 'ACTIVE')">
                      启用账号
                    </a-menu-item>
                    <a-menu-divider />
                    <a-menu-item v-if="record.role === 'USER'" @click="handleRoleChange(record, 'ADMIN')">
                      设为管理员
                    </a-menu-item>
                    <a-menu-item v-else @click="handleRoleChange(record, 'USER')">
                      取消管理员
                    </a-menu-item>
                    <a-menu-divider />
                    <a-menu-item danger>
                      <a-popconfirm
                        title="确定要删除该用户吗？此操作不可恢复"
                        @confirm="handleDeleteUser(record.id)"
                      >
                        删除用户
                      </a-popconfirm>
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ReloadOutlined, DownOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { getUsers, updateUserStatus, updateUserRole, deleteUser } from '../api/user'

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '用户名', key: 'username' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '年龄', dataIndex: 'age', key: 'age', width: 80 },
  { title: '角色', key: 'role', width: 100 },
  { title: '状态', key: 'status', width: 100 },
  { title: '操作', key: 'action', width: 120 }
]

const users = ref<any[]>([])
const loading = ref(false)

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

const handleStatusChange = async (record: any, status: string) => {
  try {
    await updateUserStatus(record.id, status)
    message.success('状态更新成功')
    fetchUsers()
  } catch (error) {
    message.error('状态更新失败')
  }
}

const handleRoleChange = async (record: any, role: string) => {
  try {
    await updateUserRole(record.id, role)
    message.success('角色更新成功')
    fetchUsers()
  } catch (error) {
    message.error('角色更新失败')
  }
}

const handleDeleteUser = async (id: number) => {
  try {
    await deleteUser(id)
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
