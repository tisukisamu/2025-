<template>
  <div class="member-manage">
    <div class="page-header">
      <h2>成员管理</h2>
      <p class="subtitle">管理社团成员与权限分配</p>
    </div>

    <div class="toolbar">
      <div class="search-box">
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索成员姓名或学号"
          style="width: 300px"
          @search="handleSearch"
        />
      </div>
      <a-button type="primary" @click="showAddModal">
        <PlusOutlined /> 添加成员
      </a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="memberList"
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
          <a-tag :color="getRoleColor(record.position)">
            {{ record.position || '成员' }}
          </a-tag>
        </template>
        <template v-if="column.key === 'status'">
          <span class="status-dot" :class="record.status === 1 ? 'active' : 'inactive'"></span>
          {{ record.status === 1 ? '正常' : '停用' }}
        </template>
        <template v-if="column.key === 'joinTime'">
          {{ formatDate(record.joinTime) }}
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="showEditPosition(record)">
              设置职位
            </a-button>
            <a-popconfirm
              title="确定要移除该成员吗？"
              @confirm="handleRemove(record)"
              ok-text="确定"
              cancel-text="取消"
            >
              <a-button type="link" size="small" danger>移除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="addVisible"
      title="添加成员"
      @ok="handleAddMember"
      @cancel="addVisible = false"
      :confirmLoading="submitting"
    >
      <a-form :model="addForm" layout="vertical">
        <a-form-item label="用户学号" required>
          <a-input v-model:value="addForm.studentId" placeholder="请输入学号" />
        </a-form-item>
        <a-form-item label="职位">
          <a-select v-model:value="addForm.position" placeholder="选择职位（可选）" allowClear>
            <a-select-option value="副社长">副社长</a-select-option>
            <a-select-option value="财务">财务</a-select-option>
            <a-select-option value="宣传">宣传</a-select-option>
            <a-select-option value="组织">组织</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="editVisible"
      title="设置职位"
      @ok="handleUpdatePosition"
      @cancel="editVisible = false"
      :confirmLoading="submitting"
    >
      <a-form layout="vertical">
        <a-form-item label="成员">
          <a-input :value="currentMember?.realName" disabled />
        </a-form-item>
        <a-form-item label="职位">
          <a-select v-model:value="editPosition" placeholder="选择职位">
            <a-select-option value="副社长">副社长</a-select-option>
            <a-select-option value="财务">财务</a-select-option>
            <a-select-option value="宣传">宣传</a-select-option>
            <a-select-option value="组织">组织</a-select-option>
            <a-select-option value="">普通成员</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { clubApi } from '@/api/club'
import type { User, Club } from '@/types'
import { normalizeMediaUrl } from '@/utils/media'

const loading = ref(false)
const submitting = ref(false)
const addVisible = ref(false)
const editVisible = ref(false)
const memberList = ref<User[]>([])
const currentClub = ref<Club | null>(null)
const currentMember = ref<User | null>(null)
const searchKeyword = ref('')
const editPosition = ref('')

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`
})

const addForm = reactive({
  studentId: '',
  position: ''
})

const columns = [
  { title: '', key: 'avatar', width: 60 },
  { title: '姓名', dataIndex: 'realName', key: 'realName' },
  { title: '学号', dataIndex: 'studentId', key: 'studentId' },
  { title: '职位', key: 'role', width: 100 },
  { title: '状态', key: 'status', width: 100 },
  { title: '加入时间', key: 'joinTime', width: 120 },
  { title: '操作', key: 'action', width: 160 }
]

const getRoleColor = (position?: string) => {
  const colors: Record<string, string> = {
    '社长': 'red',
    '副社长': 'orange',
    '财务': 'green',
    '宣传': 'blue',
    '组织': 'purple'
  }
  return colors[position || ''] || 'default'
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const loadClub = async () => {
  try {
    const res = await clubApi.getMyClubs()
    if (res.data && res.data.length > 0) {
      currentClub.value = res.data[0]
    }
  } catch (error) {
    console.error('加载社团失败:', error)
  }
}

const loadMembers = async () => {
  if (!currentClub.value) return
  
  loading.value = true
  try {
    const res = await clubApi.getMembers(
      currentClub.value.id,
      pagination.current - 1,
      pagination.pageSize
    )
    memberList.value = (res.data?.list || res.data?.content || []).map((item: User) => ({
      ...item,
      avatar: normalizeMediaUrl(item.avatar)
    }))
    pagination.total = res.data?.total || res.data?.totalElements || 0
  } catch (error) {
    console.error('加载成员列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadMembers()
}

const handleSearch = () => {
  pagination.current = 1
  loadMembers()
}

const showAddModal = () => {
  addForm.studentId = ''
  addForm.position = ''
  addVisible.value = true
}

const handleAddMember = async () => {
  if (!addForm.studentId) {
    message.warning('请输入学号')
    return
  }
  if (!currentClub.value) return
  
  submitting.value = true
  try {
    await clubApi.addMember(
      currentClub.value.id,
      parseInt(addForm.studentId),
      addForm.position || undefined
    )
    message.success('添加成员成功')
    addVisible.value = false
    loadMembers()
  } catch (error: any) {
    message.error(error.response?.data?.message || '添加失败')
  } finally {
    submitting.value = false
  }
}

const showEditPosition = (member: User) => {
  currentMember.value = member
  editPosition.value = member.position || ''
  editVisible.value = true
}

const handleUpdatePosition = async () => {
  if (!currentClub.value || !currentMember.value) return
  
  submitting.value = true
  try {
    await clubApi.updateMemberPosition(
      currentClub.value.id,
      currentMember.value.id,
      editPosition.value || ''
    )
    message.success('职位更新成功')
    editVisible.value = false
    loadMembers()
  } catch (error: any) {
    message.error(error.response?.data?.message || '更新失败')
  } finally {
    submitting.value = false
  }
}

const handleRemove = async (member: User) => {
  if (!currentClub.value) return
  
  try {
    await clubApi.removeMember(currentClub.value.id, member.id)
    message.success('移除成员成功')
    loadMembers()
  } catch (error: any) {
    message.error(error.response?.data?.message || '移除失败')
  }
}

onMounted(async () => {
  await loadClub()
  loadMembers()
})
</script>

<style scoped>
.member-manage {
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

.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 8px;
}

.status-dot.active {
  background: #52c41a;
}

.status-dot.inactive {
  background: #d9d9d9;
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
