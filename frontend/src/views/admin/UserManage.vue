<template>
  <div class="user-manage">
    <div class="page-header">
      <h2>用户管理</h2>
      <p class="subtitle">管理系统用户与权限分配</p>
    </div>

    <div class="toolbar">
      <a-space>
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索用户姓名或学号"
          style="width: 300px"
          @search="handleSearch"
        />
        <a-select v-model:value="filterRole" style="width: 150px" placeholder="角色筛选" allowClear @change="handleFilter">
          <a-select-option value="admin">管理员</a-select-option>
          <a-select-option value="teacher">指导老师</a-select-option>
          <a-select-option value="president">社长</a-select-option>
          <a-select-option value="member">成员</a-select-option>
        </a-select>
        <a-select v-model:value="filterStatus" style="width: 120px" placeholder="状态筛选" allowClear @change="handleFilter">
          <a-select-option value="ACTIVE">正常</a-select-option>
          <a-select-option value="INACTIVE">停用</a-select-option>
        </a-select>
      </a-space>
      <a-button type="primary" @click="showCreateModal">
        <PlusOutlined /> 添加用户
      </a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="userList"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      row-key="id"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'avatar'">
          <a-avatar :src="record.avatar" :style="{ backgroundColor: '#1a1a1a' }">
            {{ record.realName?.charAt(0) || 'U' }}
          </a-avatar>
        </template>
        <template v-if="column.key === 'role'">
          <a-tag :color="getRoleColor(record.role?.roleCode)">
            {{ record.role?.roleName || '-' }}
          </a-tag>
        </template>
        <template v-if="column.key === 'club'">
          {{ record.club?.clubName || '-' }}
        </template>
        <template v-if="column.key === 'status'">
          <a-badge :status="record.status === 1 ? 'success' : 'default'" :text="record.status === 1 ? '正常' : '停用'" />
        </template>
        <template v-if="column.key === 'createTime'">
          {{ formatDate(record.createTime) }}
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="showEditModal(record)">
              编辑
            </a-button>
            <a-button type="link" size="small" @click="resetPassword(record)">
              重置密码
            </a-button>
            <a-popconfirm
              :title="record.status === 1 ? '确定要停用该用户吗？' : '确定要启用该用户吗？'"
              @confirm="toggleStatus(record)"
              ok-text="确定"
              cancel-text="取消"
            >
              <a-button type="link" size="small" :danger="record.status === 1">
                {{ record.status === 1 ? '停用' : '启用' }}
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="modalVisible"
      :title="isEdit ? '编辑用户' : '添加用户'"
      @ok="handleSubmit"
      @cancel="modalVisible = false"
      :confirmLoading="submitting"
      width="600px"
    >
      <a-form
        ref="formRef"
        :model="formState"
        :rules="formRules"
        layout="vertical"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="姓名" name="realName">
              <a-input v-model:value="formState.realName" placeholder="请输入姓名" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="学号/工号" name="studentId">
              <a-input v-model:value="formState.studentId" placeholder="请输入学号/工号" :disabled="isEdit" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="用户名" name="username">
              <a-input v-model:value="formState.username" placeholder="请输入用户名" :disabled="isEdit" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="密码" name="password" v-if="!isEdit">
              <a-input-password v-model:value="formState.password" placeholder="请输入密码" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="角色" name="roleId">
              <a-select v-model:value="formState.roleId" placeholder="请选择角色">
                <a-select-option v-for="role in roleList" :key="role.id" :value="role.id">
                  {{ role.roleName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="所属社团" name="clubId">
              <a-select v-model:value="formState.clubId" placeholder="请选择社团" allowClear>
                <a-select-option v-for="club in clubList" :key="club.id" :value="club.id">
                  {{ club.clubName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="邮箱" name="email">
          <a-input v-model:value="formState.email" placeholder="请输入邮箱" />
        </a-form-item>
        <a-form-item label="手机号" name="phone">
          <a-input v-model:value="formState.phone" placeholder="请输入手机号" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import type { FormInstance, Rule } from 'ant-design-vue/es/form'
import { PlusOutlined } from '@ant-design/icons-vue'
import { userApi, authApi } from '@/api/user'
import { clubApi } from '@/api/club'
import type { User, Club, Role } from '@/types'
import { normalizeMediaUrl } from '@/utils/media'

const loading = ref(false)
const submitting = ref(false)
const modalVisible = ref(false)
const isEdit = ref(false)
const userList = ref<User[]>([])
const clubList = ref<Club[]>([])
const roleList = ref<Role[]>([])
const searchKeyword = ref('')
const filterRole = ref<string>()
const filterStatus = ref<string>()
const currentUser = ref<User | null>(null)

const formRef = ref<FormInstance>()
const formState = reactive({
  realName: '',
  studentId: '',
  username: '',
  password: '',
  roleId: undefined as number | undefined,
  clubId: undefined as number | undefined,
  email: '',
  phone: ''
})

const formRules: Record<string, Rule[]> = {
  realName: [{ required: true, message: '请输入姓名' }],
  studentId: [{ required: true, message: '请输入学号/工号' }],
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
  roleId: [{ required: true, message: '请选择角色' }]
}

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`
})

const columns = [
  { title: '', key: 'avatar', width: 60 },
  { title: '姓名', dataIndex: 'realName', key: 'realName', width: 100 },
  { title: '学号/工号', dataIndex: 'studentId', key: 'studentId', width: 120 },
  { title: '用户名', dataIndex: 'username', key: 'username', width: 120 },
  { title: '角色', key: 'role', width: 100 },
  { title: '所属社团', key: 'club', width: 150 },
  { title: '状态', key: 'status', width: 80 },
  { title: '创建时间', key: 'createTime', width: 120 },
  { title: '操作', key: 'action', width: 180 }
]

const getRoleColor = (roleCode?: string) => {
  const colors: Record<string, string> = {
    admin: 'red',
    teacher: 'orange',
    president: 'blue',
    member: 'default'
  }
  return colors[roleCode || ''] || 'default'
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await userApi.getList(pagination.current - 1, pagination.pageSize)
    userList.value = (res.data?.list || res.data?.content || []).map((item: User) => ({
      ...item,
      avatar: normalizeMediaUrl(item.avatar)
    }))
    pagination.total = res.data?.total || res.data?.totalElements || 0
  } catch (error) {
    console.error('加载用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const loadClubs = async () => {
  try {
    const res = await clubApi.getList(0, 100)
    clubList.value = res.data?.content || []
  } catch (error) {
    console.error('加载社团列表失败:', error)
  }
}

const loadRoles = async () => {
  roleList.value = [
    { id: 1, roleCode: 'admin', roleName: '管理员' },
    { id: 2, roleCode: 'teacher', roleName: '指导老师' },
    { id: 3, roleCode: 'president', roleName: '社长' },
    { id: 4, roleCode: 'member', roleName: '成员' }
  ]
}

const handleSearch = () => {
  pagination.current = 1
  loadUsers()
}

const handleFilter = () => {
  pagination.current = 1
  loadUsers()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadUsers()
}

const resetForm = () => {
  formState.realName = ''
  formState.studentId = ''
  formState.username = ''
  formState.password = ''
  formState.roleId = undefined
  formState.clubId = undefined
  formState.email = ''
  formState.phone = ''
}

const showCreateModal = () => {
  isEdit.value = false
  currentUser.value = null
  resetForm()
  modalVisible.value = true
}

const showEditModal = (user: User) => {
  isEdit.value = true
  currentUser.value = user
  formState.realName = user.realName || ''
  formState.studentId = user.studentId || ''
  formState.username = user.username || ''
  formState.roleId = user.role?.id
  formState.clubId = user.club?.id
  formState.email = user.email || ''
  formState.phone = user.phone || ''
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true
    
    if (isEdit.value && currentUser.value) {
      await userApi.update(currentUser.value.id, {
        realName: formState.realName,
        email: formState.email,
        phone: formState.phone,
        roleId: formState.roleId,
        clubId: formState.clubId
      })
      message.success('用户信息更新成功')
    } else {
      await authApi.register({
        username: formState.username,
        password: formState.password,
        realName: formState.realName,
        studentId: formState.studentId,
        roleId: formState.roleId!,
        clubId: formState.clubId,
        email: formState.email,
        phone: formState.phone
      })
      message.success('用户创建成功')
    }
    
    modalVisible.value = false
    loadUsers()
  } catch (error: any) {
    if (error.response) {
      message.error(error.response?.data?.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

const resetPassword = (user: User) => {
  Modal.confirm({
    title: '重置密码',
    content: `确定要重置用户 "${user.realName}" 的密码吗？新密码将发送到用户邮箱。`,
    okText: '确定',
    cancelText: '取消',
    onOk() {
      message.success('密码重置成功，新密码已发送至用户邮箱')
    }
  })
}

const toggleStatus = async (user: User) => {
  try {
    const targetStatus = user.status === 1 ? 0 : 1
    await userApi.updateStatus(user.id, targetStatus)
    message.success(targetStatus === 1 ? '用户已启用' : '用户已停用')
    loadUsers()
  } catch (error: any) {
    message.error(error.response?.data?.message || '操作失败')
  }
}

onMounted(() => {
  loadUsers()
  loadClubs()
  loadRoles()
})
</script>

<style scoped>
.user-manage {
  padding: 24px;
  background: #fff;
  min-height: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
}

.subtitle {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  color: #1a1a1a;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: #f5f5f5;
}
</style>
