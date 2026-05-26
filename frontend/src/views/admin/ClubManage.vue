<template>
  <div class="club-manage">
    <div class="page-header">
      <h2>社团管理</h2>
      <p class="subtitle">管理全校社团信息与状态</p>
    </div>

    <div class="toolbar">
      <a-space>
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索社团名称"
          style="width: 300px"
          @search="handleSearch"
        />
        <a-select v-model:value="filterStatus" style="width: 120px" placeholder="状态筛选" allowClear @change="handleFilter">
          <a-select-option value="ACTIVE">活跃</a-select-option>
          <a-select-option value="INACTIVE">停用</a-select-option>
          <a-select-option value="PENDING">待审核</a-select-option>
        </a-select>
      </a-space>
      <a-button type="primary" @click="showCreateModal">
        <PlusOutlined /> 创建社团
      </a-button>
    </div>

    <div class="club-grid">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :sm="12" :lg="8" :xl="6" v-for="club in clubList" :key="club.id">
          <div class="club-card" @click="showDetail(club)">
            <div class="club-header">
              <div class="club-avatar">
                <img v-if="club.logo" :src="resolveLogoUrl(club.logo)" alt="" />
                <div v-else class="avatar-placeholder">
                  {{ club.clubName?.charAt(0) || '社' }}
                </div>
              </div>
              <a-tag :color="getStatusColor(club.status)" size="small">
                {{ statusMap[club.status] || club.status }}
              </a-tag>
            </div>
            <div class="club-body">
              <h4>{{ club.clubName }}</h4>
              <p class="club-desc">{{ club.description || '暂无简介' }}</p>
            </div>
            <div class="club-footer">
              <div class="stat">
                <TeamOutlined />
                <span>{{ club.memberCount || 0 }} 人</span>
              </div>
              <div class="balance">
                {{ formatMoney(club.balance || 0) }}
              </div>
            </div>
          </div>
        </a-col>
      </a-row>

      <div v-if="!loading && clubList.length === 0" class="empty-state">
        <a-empty description="暂无社团数据" />
      </div>

      <div class="pagination-wrapper" v-if="pagination.total > pagination.pageSize">
        <a-pagination
          v-model:current="pagination.current"
          :total="pagination.total"
          :pageSize="pagination.pageSize"
          show-size-changer
          :show-total="(total: number) => `共 ${total} 个社团`"
          @change="handlePageChange"
        />
      </div>
    </div>

    <a-modal
      v-model:open="detailVisible"
      :title="currentClub?.clubName"
      @cancel="detailVisible = false"
      :footer="null"
      width="700px"
    >
      <template v-if="currentClub">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="社团ID">{{ currentClub.id }}</a-descriptions-item>
          <a-descriptions-item label="社团编码">{{ currentClub.clubCode || '-' }}</a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="getStatusColor(currentClub.status)">
              {{ statusMap[currentClub.status] || currentClub.status }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="指导老师">
            {{ currentClub.teacher?.realName || '未指定' }}
          </a-descriptions-item>
          <a-descriptions-item label="社长">
            {{ currentClub.president?.realName || '未指定' }}
          </a-descriptions-item>
          <a-descriptions-item label="成员数">
            {{ currentClub.memberCount || 0 }} 人
          </a-descriptions-item>
          <a-descriptions-item label="账户余额">
            <span class="amount">{{ formatMoney(currentClub.balance || 0) }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="创建时间">
            {{ formatDate(currentClub.createTime) }}
          </a-descriptions-item>
          <a-descriptions-item label="社团账号">
            {{ currentClub.account || '-' }}
          </a-descriptions-item>
          <a-descriptions-item label="社团简介" :span="2">
            {{ currentClub.description || '暂无简介' }}
          </a-descriptions-item>
        </a-descriptions>

        <div class="detail-actions">
          <a-space>
            <a-button @click="showEditModal(currentClub)">
              <EditOutlined /> 编辑信息
            </a-button>
            <a-button @click="assignTeacher(currentClub)">
              <UserAddOutlined /> 指派老师
            </a-button>
            <a-popconfirm
              :title="currentClub.status === 1 ? '确定要停用该社团吗？' : '确定要启用该社团吗？'"
              @confirm="toggleStatus(currentClub)"
              ok-text="确定"
              cancel-text="取消"
            >
              <a-button :danger="currentClub.status === 1">
                {{ currentClub.status === 1 ? '停用社团' : '启用社团' }}
              </a-button>
            </a-popconfirm>
            <a-popconfirm
              title="确定要删除该社团吗？此操作不可恢复！"
              @confirm="handleDelete(currentClub)"
              ok-text="确定删除"
              cancel-text="取消"
              ok-type="danger"
            >
              <a-button danger>
                <DeleteOutlined /> 删除社团
              </a-button>
            </a-popconfirm>
          </a-space>
        </div>
      </template>
    </a-modal>

    <a-modal
      v-model:open="editVisible"
      :title="isCreate ? '创建社团' : '编辑社团'"
      @ok="handleSubmit"
      @cancel="editVisible = false"
      ok-text="确定"
      cancel-text="取消"
      :confirmLoading="submitting"
      width="600px"
    >
      <a-form
        ref="formRef"
        :model="formState"
        :rules="formRules"
        layout="vertical"
      >
        <a-form-item label="社团名称" name="clubName">
          <a-input v-model:value="formState.clubName" placeholder="请输入社团名称" />
        </a-form-item>
        <a-form-item label="社团编码" name="clubCode">
          <a-input
            v-model:value="formState.clubCode"
            :disabled="!isCreate"
            placeholder="请输入社团编码（如 ACM001）"
          />
        </a-form-item>
        <a-form-item label="社团简介" name="description">
          <a-textarea
            v-model:value="formState.description"
            placeholder="请输入社团简介"
            :rows="4"
          />
        </a-form-item>
        <a-form-item label="社团Logo" name="logo">
          <div class="logo-uploader">
            <img v-if="formState.logo" :src="resolveLogoUrl(formState.logo)" class="logo-preview" alt="logo" />
            <a-upload
              :show-upload-list="false"
              :before-upload="beforeLogoUpload"
              :custom-request="handleLogoUpload"
            >
              <a-button :loading="logoUploading">上传Logo</a-button>
            </a-upload>
            <a-button v-if="formState.logo" type="link" danger @click="formState.logo = ''">移除图片</a-button>
          </div>
        </a-form-item>
        <a-form-item label="指导老师" name="teacherId">
          <a-select v-model:value="formState.teacherId" placeholder="选择指导老师" allowClear show-search>
            <a-select-option v-for="teacher in teacherList" :key="teacher.id" :value="teacher.id">
              {{ teacher.realName }} ({{ teacher.username }})
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule } from 'ant-design-vue/es/form'
import { PlusOutlined, TeamOutlined, EditOutlined, UserAddOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { clubApi } from '@/api/club'
import { userApi } from '@/api/user'
import { uploadApi } from '@/api/upload'
import type { Club, User } from '@/types'
import { normalizeMediaUrl } from '@/utils/media'

const loading = ref(false)
const submitting = ref(false)
const detailVisible = ref(false)
const editVisible = ref(false)
const isCreate = ref(false)
const logoUploading = ref(false)
const clubList = ref<Club[]>([])
const currentClub = ref<Club | null>(null)
const teacherList = ref<User[]>([])
const searchKeyword = ref('')
const filterStatus = ref<string>()

const pagination = reactive({
  current: 1,
  pageSize: 12,
  total: 0
})

const formRef = ref<FormInstance>()
const formState = reactive({
  clubName: '',
  clubCode: '',
  description: '',
  logo: '',
  category: '综合社团',
  teacherId: undefined as number | undefined
})

const formRules: Record<string, Rule[]> = {
  clubName: [{ required: true, message: '请输入社团名称' }],
  clubCode: [{ required: true, message: '请输入社团编码' }]
}

const statusMap: Record<string, string> = {
  0: '停用',
  1: '活跃',
  2: '待审核',
  ACTIVE: '活跃',
  INACTIVE: '停用',
  PENDING: '待审核'
}

const getStatusColor = (status: string | number) => {
  const colors: Record<string, string> = {
    '0': 'default',
    '1': 'green',
    '2': 'orange',
    ACTIVE: 'green',
    INACTIVE: 'default',
    PENDING: 'orange'
  }
  return colors[String(status)] || 'default'
}

const formatMoney = (amount: number) => {
  return '¥' + (amount || 0).toFixed(2)
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const resolveLogoUrl = (url?: string) => {
  return normalizeMediaUrl(url)
}

const loadClubs = async () => {
  loading.value = true
  try {
    const statusMapByFilter: Record<string, number> = {
      ACTIVE: 1,
      INACTIVE: 0,
      PENDING: 2
    }
    const res = searchKeyword.value
      ? await clubApi.search(searchKeyword.value, pagination.current - 1, pagination.pageSize)
      : await clubApi.getList(pagination.current - 1, pagination.pageSize)
    const normalizedList = (res.data?.content || []).map((item: Club) => ({
      ...item,
      logo: resolveLogoUrl(item.logo)
    }))
    const filteredList = filterStatus.value
      ? normalizedList.filter(item => item.status === statusMapByFilter[filterStatus.value])
      : normalizedList
    clubList.value = filteredList
    pagination.total = filterStatus.value ? filteredList.length : res.data?.totalElements || 0
  } catch (error) {
    console.error('加载社团列表失败:', error)
  } finally {
    loading.value = false
  }
}

const loadTeachers = async () => {
  try {
    const res = await userApi.getList(0, 100)
    teacherList.value = (res.data?.content || []).filter(u => u.role?.roleCode === 'teacher')
  } catch (error) {
    console.error('加载教师列表失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadClubs()
}

const handleFilter = () => {
  pagination.current = 1
  loadClubs()
}

const handlePageChange = (page: number, pageSize: number) => {
  pagination.current = page
  pagination.pageSize = pageSize
  loadClubs()
}

const showDetail = (club: Club) => {
  currentClub.value = {
    ...club,
    logo: resolveLogoUrl(club.logo)
  }
  detailVisible.value = true
}

const resetForm = () => {
  formState.clubName = ''
  formState.clubCode = ''
  formState.description = ''
  formState.logo = ''
  formState.category = '综合社团'
  formState.teacherId = undefined
}

const showCreateModal = () => {
  isCreate.value = true
  currentClub.value = null
  resetForm()
  editVisible.value = true
}

const showEditModal = (club: Club) => {
  isCreate.value = false
  currentClub.value = club
  formState.clubName = club.clubName || ''
  formState.clubCode = club.clubCode || ''
  formState.description = club.description || ''
  formState.logo = resolveLogoUrl(club.logo) || ''
  formState.category = club.category || '综合社团'
  formState.teacherId = club.teacher?.id
  detailVisible.value = false
  editVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true
    
    if (isCreate.value) {
      await clubApi.create({
        clubName: formState.clubName,
        clubCode: formState.clubCode,
        description: formState.description,
        logo: formState.logo,
        category: formState.category,
        teacherId: formState.teacherId
      })
      message.success('社团创建成功')
    } else if (currentClub.value) {
      await clubApi.update(currentClub.value.id, {
        clubName: formState.clubName,
        clubCode: formState.clubCode || currentClub.value.clubCode || '',
        description: formState.description,
        logo: formState.logo,
        category: formState.category,
        teacherId: formState.teacherId
      })
      message.success('社团信息更新成功')
    }
    
    editVisible.value = false
    loadClubs()
  } catch (error: any) {
    message.error(error.response?.data?.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const beforeLogoUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    message.error('图片大小不能超过5MB')
    return false
  }
  return true
}

const handleLogoUpload = async (options: any) => {
  const file = options.file as File
  logoUploading.value = true
  try {
    const uploadRes = await uploadApi.uploadImage(file, 'logo')
    const uploadedUrl = uploadRes.data.url
    formState.logo = uploadedUrl.startsWith('/upload/') ? uploadedUrl : `/upload${uploadedUrl}`
    options.onSuccess?.(uploadRes, file)
    message.success('Logo上传成功')
  } catch (error: any) {
    options.onError?.(error)
    message.error(error.message || 'Logo上传失败')
  } finally {
    logoUploading.value = false
  }
}

const assignTeacher = (club: Club) => {
  showEditModal(club)
  message.info('已打开编辑窗口，请选择指导老师后保存')
}

const toggleStatus = async (club: Club) => {
  try {
    const targetStatus = club.status === 1 ? 0 : 1
    await clubApi.updateStatus(club.id, targetStatus)
    message.success(targetStatus === 1 ? '社团已启用' : '社团已停用')
    detailVisible.value = false
    loadClubs()
  } catch (error: any) {
    message.error(error.response?.data?.message || '操作失败')
  }
}

const handleDelete = async (club: Club) => {
  try {
    await clubApi.delete(club.id)
    message.success('社团已删除')
    detailVisible.value = false
    loadClubs()
  } catch (error: any) {
    message.error(error.response?.data?.message || '删除失败')
  }
}

onMounted(() => {
  loadClubs()
  loadTeachers()
})
</script>

<style scoped>
.club-manage {
  padding: 24px;
  background: #f5f5f5;
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
  margin-bottom: 24px;
}

.club-grid {
  min-height: 400px;
}

.club-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  height: 100%;
}

.club-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.club-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.club-avatar {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  overflow: hidden;
}

.club-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
  color: #666;
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
}

.club-body h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.club-desc {
  margin: 0;
  color: #666;
  font-size: 13px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.club-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.club-footer .stat {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 13px;
}

.club-footer .balance {
  font-weight: 600;
  color: #1a1a1a;
}

.empty-state {
  background: #fff;
  border-radius: 8px;
  padding: 60px 0;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.logo-uploader {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-preview {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid #d9d9d9;
}

.amount {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.detail-actions {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

:deep(.ant-descriptions-item-label) {
  background: #fafafa;
  font-weight: 500;
}
</style>
