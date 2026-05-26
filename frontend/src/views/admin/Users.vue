<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { adminApi } from '../../api'
import type { User } from '../../api'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  PlusOutlined,
  EditOutlined,
  CheckCircleOutlined,
  StopOutlined,
  UserOutlined,
  PhoneOutlined,
  MailOutlined
} from '@ant-design/icons-vue'

const users = ref<User[]>([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({
  page: 1,
  size: 10,
  search: ''
})

/** 搜索防抖：输入更顺 */
const debouncedSearch = ref('')
let searchTimer: number | undefined
watch(
  () => queryParams.value.search,
  (val) => {
    window.clearTimeout(searchTimer)
    searchTimer = window.setTimeout(() => {
      debouncedSearch.value = (val ?? '').trim()
    }, 250)
  },
  { immediate: true }
)

const modalVisible = ref(false)
const editingUser = ref<Partial<User>>({})

const roleOptions = [
  { value: 'ROLE_USER', label: '普通用户' },
  { value: 'ROLE_ADMIN', label: '管理员' }
] as const

onMounted(() => {
  loadUsers()
})

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await adminApi.getUsers(queryParams.value.page - 1, queryParams.value.size)
    users.value = res.content
    total.value = res.totalElements
  } catch (error) {
    message.error('加载用户失败')
  } finally {
    loading.value = false
  }
}

const handleTableChange = (pagination: any) => {
  queryParams.value.page = pagination.current
  queryParams.value.size = pagination.pageSize
  loadUsers()
}

const filteredUsers = computed(() => {
  if (!debouncedSearch.value) return users.value
  const q = debouncedSearch.value.toLowerCase()
  return users.value.filter(
    (u) =>
      u.username?.toLowerCase().includes(q) ||
      u.email?.toLowerCase().includes(q) ||
      u.phone?.includes(q)
  )
})

const handleAdd = () => {
  editingUser.value = { role: 'ROLE_USER', active: true }
  modalVisible.value = true
}

const handleEdit = (user: User) => {
  editingUser.value = { ...user }
  modalVisible.value = true
}

const handleSave = async () => {
  try {
    if (editingUser.value.id) {
      await adminApi.updateUser(editingUser.value.id, editingUser.value)
      message.success('更新成功')
    } else {
      await adminApi.createUser(editingUser.value)
      message.success('创建成功')
    }
    modalVisible.value = false
    loadUsers()
  } catch (error) {
    message.error('保存失败')
  }
}

const toggleStatus = async (user: User) => {
  try {
    await adminApi.updateUserStatus(user.id, !user.active)
    user.active = !user.active
    message.success(user.active ? '已禁用' : '已启用')
  } catch (error) {
    message.error('操作失败')
  }
}

const formatDate = (date: string) => {
  if (!date) return '-'
  const d = new Date(date)
  return `${d.getFullYear()}/${(d.getMonth() + 1).toString().padStart(2, '0')}/${d
    .getDate()
    .toString()
    .padStart(2, '0')}`
}
</script>

<template>
  <div class="users-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">用户管理</h1>
        <p class="page-subtitle">
          共 <span class="highlight">{{ total }}</span> 位注册用户
        </p>
      </div>
      <button class="btn btn-primary" @click="handleAdd">
        <PlusOutlined />
        <span>添加用户</span>
      </button>
    </div>

    <!-- 搜索栏（保持你原来的结构，仅加了防抖逻辑） -->
    <div class="search-bar">
      <div class="search-box">
        <SearchOutlined class="search-icon" />
        <input
          v-model="queryParams.search"
          type="text"
          class="search-input"
          placeholder="搜索用户名、邮箱或手机号..."
        />
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="filteredUsers.length === 0" class="empty-state">
      <UserOutlined class="empty-icon" />
      <h3 class="empty-title">未找到匹配的用户</h3>
      <p class="empty-desc">请尝试其他搜索条件</p>
    </div>

    <!-- 用户列表 -->
    <div v-else class="table-container">
      <table class="data-table">
        <thead>
        <tr>
          <th style="width: 80px;">头像</th>
          <th style="width: 20%;">用户信息</th>
          <th style="width: 20%;">联系方式</th>
          <th style="width: 15%;">角色</th>
          <th style="width: 15%;">注册时间</th>
          <th style="width: 15%;">状态</th>
          <th style="width: 15%;">操作</th>
        </tr>
        </thead>
        <tbody>
        <tr
          v-for="user in filteredUsers"
          :key="user.id"
          class="table-row"
          :class="{ 'row-inactive': !user.active }"
        >
          <td>
            <div class="user-avatar-cell">
              <span class="avatar-text">{{ user.username?.charAt(0).toUpperCase() || 'U' }}</span>
              <div class="status-indicator" :class="user.active ? 'status-active' : 'status-inactive'"></div>
            </div>
          </td>
          <td>
            <div class="user-info-cell">
              <div class="user-name">{{ user.username }}</div>
              <div class="user-id">ID: #{{ user.id }}</div>
            </div>
          </td>
          <td>
            <div class="contact-cell">
              <div v-if="user.phone" class="contact-item">
                <PhoneOutlined class="contact-icon" />
                <span>{{ user.phone }}</span>
              </div>
              <div v-if="user.email" class="contact-item">
                <MailOutlined class="contact-icon" />
                <span class="email-text">{{ user.email }}</span>
              </div>
            </div>
          </td>
          <td>
              <span class="role-badge" :class="user.role === 'ROLE_ADMIN' ? 'role-admin' : 'role-user'">
                {{ user.role === 'ROLE_ADMIN' ? '管理员' : '普通用户' }}
              </span>
          </td>
          <td>{{ formatDate(user.createTime) }}</td>
          <td>
            <div class="status-badge" :class="user.active ? 'status-active' : 'status-inactive'">
              {{ user.active ? '正常' : '已禁用' }}
            </div>
          </td>
          <td class="action-cell">
            <button class="action-btn action-btn-edit" @click="handleEdit(user)" title="编辑">
              <EditOutlined />
            </button>
            <button
              class="action-btn"
              :class="user.active ? 'action-btn-disable' : 'action-btn-enable'"
              @click="toggleStatus(user)"
              :title="user.active ? '禁用' : '启用'"
            >
              <component :is="user.active ? StopOutlined : CheckCircleOutlined" />
            </button>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <a-pagination
          v-model:current="queryParams.page"
          v-model:pageSize="queryParams.size"
          :total="total"
          :show-total="(total: number) => `共 ${total} 条`"
          show-size-changer
          @change="(page: number, pageSize: number) => handleTableChange({ current: page, pageSize })"
        />
      </div>
    </div>

    <!-- 编辑/添加弹窗（角色下拉：改用 AntD a-select 修复/优化） -->
    <a-modal
      v-model:open="modalVisible"
      :title="editingUser.id ? '编辑用户' : '添加用户'"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleSave"
      width="480px"
    >
      <div class="form-content">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input v-model="editingUser.username" class="form-input" placeholder="请输入用户名" />
        </div>

        <div class="form-row">
          <div class="form-group">
            <label class="form-label">手机号</label>
            <input v-model="editingUser.phone" class="form-input" placeholder="请输入手机号" />
          </div>
          <div class="form-group">
            <label class="form-label">邮箱</label>
            <input v-model="editingUser.email" type="email" class="form-input" placeholder="请输入邮箱" />
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">角色</label>
          <a-select
            v-model:value="editingUser.role"
            class="form-select-antd"
            :options="roleOptions as any"
            placeholder="请选择角色"
          />
        </div>

        <div class="form-group">
          <label class="form-checkbox">
            <input type="checkbox" v-model="editingUser.active" />
            <span>启用账户</span>
          </label>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<style scoped>
/* 页面容器 */
.users-page {
  padding: 24px;
  max-width: 1440px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.page-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.highlight {
  color: #10b981;
  font-weight: 600;
}

/* 按钮 */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.btn-primary {
  background: #10b981;
  color: white;
}

.btn-primary:hover {
  background: #059669;
  transform: translateY(-1px);
}

/* 搜索栏 */
.search-bar {
  margin-bottom: 24px;
}

.search-box {
  position: relative;
  max-width: 400px;
}

.search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  font-size: 16px;
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: 12px 14px 12px 44px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  font-size: 14px;
  background: white;
  transition: all 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

/* 表格容器 */
.table-container {
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.pagination-wrapper {
  padding: 16px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #f0f0f0;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  min-width: 1000px;
}

.data-table thead {
  background: #f9fafb;
}

.data-table th {
  text-align: left;
  padding: 16px 20px;
  font-weight: 600;
  color: #374151;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 1px solid #e5e7eb;
  white-space: nowrap;
}

.data-table td {
  padding: 16px 20px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
  color: #374151;
}

.table-row {
  transition: background 0.2s;
}

.table-row:hover {
  background: #f9fafb;
}

.table-row:last-child td {
  border-bottom: none;
}

.row-inactive {
  opacity: 0.5;
  background: #f9fafb;
}

/* 用户头像单元格 */
.user-avatar-cell {
  position: relative;
  width: 48px;
  height: 48px;
  border-radius: 10px;
  background: linear-gradient(135deg, #10b981, #059669);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-avatar-cell .avatar-text {
  font-size: 20px;
  font-weight: 700;
  color: white;
}

.user-avatar-cell .status-indicator {
  position: absolute;
  bottom: -3px;
  right: -3px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 2px solid white;
}

.user-avatar-cell .status-active {
  background: #10b981;
}

.user-avatar-cell .status-inactive {
  background: #ef4444;
}

/* 用户信息单元格 */
.user-info-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-weight: 500;
  color: #1f2937;
  font-size: 14px;
}

.user-id {
  font-size: 12px;
  color: #9ca3af;
}

/* 联系方式单元格 */
.contact-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #6b7280;
}

.contact-icon {
  color: #9ca3af;
  font-size: 13px;
  flex-shrink: 0;
}

.email-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}

/* 角色徽章 */
.role-badge {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.role-admin {
  background: #ede9fe;
  color: #8b5cf6;
}

.role-user {
  background: #f3f4f6;
  color: #6b7280;
}

/* 状态 */
.status-badge {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
}

.status-active {
  background: #d1fae5;
  color: #059669;
}

.status-inactive {
  background: #f3f4f6;
  color: #6b7280;
}

/* 操作单元格 */
.action-cell {
  text-align: right;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 8px;
  border: none;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn-edit {
  background: #f3f4f6;
  color: #374151;
}

.action-btn-edit:hover {
  background: #e5e7eb;
  color: #10b981;
}

.action-btn-enable {
  background: #d1fae5;
  color: #059669;
}

.action-btn-enable:hover {
  background: #a7f3d0;
}

.action-btn-disable {
  background: #fee2e2;
  color: #dc2626;
}

.action-btn-disable:hover {
  background: #fecaca;
}

/* 表单样式 */
.form-content {
  padding: 8px 0;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  transition: all 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #374151;
}

.form-checkbox input[type='checkbox'] {
  width: 18px;
  height: 18px;
  accent-color: #10b981;
}

/* AntD Select：在弹窗表单内统一高度/圆角/聚焦效果 */
.form-select-antd :deep(.ant-select-selector) {
  height: 40px !important;
  border-radius: 8px !important;
  border-color: #e5e7eb !important;
  box-shadow: none !important;
  display: flex;
  align-items: center;
}

.form-select-antd :deep(.ant-select-selection-item),
.form-select-antd :deep(.ant-select-selection-placeholder) {
  line-height: 38px;
}

.form-select-antd :deep(.ant-select-focused .ant-select-selector) {
  border-color: #10b981 !important;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1) !important;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 16px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #10b981;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  color: #6b7280;
  font-size: 14px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  color: #d1d5db;
  margin-bottom: 24px;
  opacity: 0.5;
}

.empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 12px 0;
}

.empty-desc {
  font-size: 14px;
  color: #9ca3af;
}

/* 响应式 */
@media (max-width: 768px) {
  .users-page {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .search-box {
    width: 100%;
    max-width: none;
  }

  .data-table th,
  .data-table td {
    padding: 12px 16px;
    font-size: 13px;
  }

  .action-cell {
    display: flex;
    gap: 8px;
    justify-content: flex-end;
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
