<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">活动发布</h1>
      <p class="page-subtitle">创建活动草稿、提交审核并追踪发布状态</p>
    </div>

    <div class="card">
      <a-space>
        <a-button type="primary" @click="openCreateModal">新建活动</a-button>
        <a-button @click="fetchData">刷新</a-button>
      </a-space>
    </div>

    <div class="card table-card">
      <a-table
        :columns="columns"
        :data-source="activityList"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="statusColor(record.status)">{{ statusLabel(record.status) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'time'">
            {{ formatDate(record.startTime) }} ~ {{ formatDate(record.endTime) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button size="small" @click="openEditModal(record)">编辑</a-button>
              <a-button
                size="small"
                type="primary"
                v-if="record.status === 'DRAFT' || record.status === 'REJECTED'"
                @click="handleSubmitReview(record.id)"
              >
                提交审核
              </a-button>
              <a-button size="small" danger @click="handleDelete(record.id)">删除</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>

    <a-modal
      v-model:open="modalVisible"
      :title="editingId ? '编辑活动' : '新建活动'"
      @ok="handleSave"
      @cancel="modalVisible = false"
      ok-text="确定"
      cancel-text="取消"
      :confirmLoading="saving"
      width="720px"
    >
      <a-form :model="formState" layout="vertical">
        <a-form-item label="活动名称">
          <a-input v-model:value="formState.activityName" placeholder="请输入活动名称" />
        </a-form-item>
        <a-form-item label="活动描述">
          <a-textarea v-model:value="formState.description" :rows="3" placeholder="请输入活动内容" />
        </a-form-item>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item label="开始时间">
              <a-date-picker v-model:value="formState.startTime" show-time style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="结束时间">
              <a-date-picker v-model:value="formState.endTime" show-time style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item label="活动地点">
              <a-input v-model:value="formState.location" placeholder="如：学生活动中心" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="预算金额">
              <a-input-number v-model:value="formState.budget" :min="0" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="活动封面">
          <div class="cover-box">
            <img v-if="formState.coverImage" :src="formState.coverImage" class="cover-preview" alt="cover" />
            <a-upload :show-upload-list="false" :before-upload="beforeUpload" :custom-request="handleUpload">
              <a-button :loading="uploading">上传封面</a-button>
            </a-upload>
          </div>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import dayjs, { type Dayjs } from 'dayjs'
import { message } from 'ant-design-vue'
import { activityApi, clubApi, uploadApi } from '@/api'
import type { Activity } from '@/types'

const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const modalVisible = ref(false)
const editingId = ref<number>()
const clubId = ref<number>()
const activityList = ref<Activity[]>([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true
})

const formState = reactive({
  activityName: '',
  description: '',
  startTime: undefined as Dayjs | undefined,
  endTime: undefined as Dayjs | undefined,
  location: '',
  budget: 0,
  coverImage: ''
})

const columns = [
  { title: '活动名称', dataIndex: 'activityName', key: 'activityName' },
  { title: '时间', key: 'time', width: 240 },
  { title: '地点', dataIndex: 'location', key: 'location', width: 160 },
  { title: '预算', dataIndex: 'budget', key: 'budget', width: 120 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 120 },
  { title: '操作', key: 'action', width: 260 }
]

const statusLabel = (status?: string) => {
  const map: Record<string, string> = {
    DRAFT: '草稿',
    SUBMITTED: '待审核',
    PUBLISHED: '已发布',
    REJECTED: '已驳回'
  }
  return map[status || ''] || status
}

const statusColor = (status?: string) => {
  const map: Record<string, string> = {
    DRAFT: 'default',
    SUBMITTED: 'processing',
    PUBLISHED: 'success',
    REJECTED: 'error'
  }
  return map[status || ''] || 'default'
}

const formatDate = (time?: string) => (time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-')

const loadClubId = async () => {
  const res = await clubApi.getMyClubs()
  if (res.data?.length) {
    clubId.value = res.data[0].id
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await activityApi.getMyCreated(pagination.current - 1, pagination.pageSize)
    activityList.value = res.data.list || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  formState.activityName = ''
  formState.description = ''
  formState.startTime = undefined
  formState.endTime = undefined
  formState.location = ''
  formState.budget = 0
  formState.coverImage = ''
}

const openCreateModal = () => {
  editingId.value = undefined
  resetForm()
  modalVisible.value = true
}

const openEditModal = (record: Activity) => {
  editingId.value = record.id
  formState.activityName = record.activityName
  formState.description = record.description || ''
  formState.startTime = record.startTime ? dayjs(record.startTime) : undefined
  formState.endTime = record.endTime ? dayjs(record.endTime) : undefined
  formState.location = record.location || ''
  formState.budget = record.budget || 0
  formState.coverImage = record.coverImage || ''
  modalVisible.value = true
}

const handleSave = async () => {
  if (!clubId.value) {
    message.error('未找到社团信息')
    return
  }
  if (!formState.activityName || !formState.startTime || !formState.endTime) {
    message.warning('请填写完整活动信息')
    return
  }
  saving.value = true
  try {
    const payload = {
      clubId: clubId.value,
      activityName: formState.activityName,
      description: formState.description,
      startTime: formState.startTime.format('YYYY-MM-DDTHH:mm:ss'),
      endTime: formState.endTime.format('YYYY-MM-DDTHH:mm:ss'),
      location: formState.location,
      budget: formState.budget,
      coverImage: formState.coverImage
    }
    if (editingId.value) {
      await activityApi.update(editingId.value, payload)
      message.success('活动已更新')
    } else {
      await activityApi.create(payload)
      message.success('活动草稿创建成功')
    }
    modalVisible.value = false
    fetchData()
  } catch (error: any) {
    message.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const handleSubmitReview = async (id: number) => {
  await activityApi.submit(id)
  message.success('已提交审核')
  fetchData()
}

const handleDelete = async (id: number) => {
  await activityApi.delete(id)
  message.success('删除成功')
  fetchData()
}

const beforeUpload = (file: File) => {
  if (!file.type.startsWith('image/')) {
    message.error('请上传图片文件')
    return false
  }
  return file.size / 1024 / 1024 < 5
}

const handleUpload = async (options: any) => {
  uploading.value = true
  try {
    const res = await uploadApi.uploadImage(options.file, 'activity')
    const url = res.data.url
    formState.coverImage = url.startsWith('/upload/') ? url : `/upload${url}`
    options.onSuccess?.(res)
  } catch (error: any) {
    options.onError?.(error)
    message.error(error.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchData()
}

onMounted(async () => {
  await loadClubId()
  fetchData()
})
</script>

<style scoped>
.table-card {
  margin-top: 16px;
}

.cover-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cover-preview {
  width: 88px;
  height: 56px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
}
</style>
