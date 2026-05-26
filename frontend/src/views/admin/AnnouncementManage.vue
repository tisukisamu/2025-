<template>
  <div class="announcement-manage-page">
    <div class="back-nav">
      <a-button type="text" @click="router.back()">
        <left-outlined /> 返回
      </a-button>
    </div>
    <div class="page-header">
      <h1>公告管理</h1>
      <a-button type="primary" @click="showEditModal(null)">
        发布公告
      </a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="announcements"
      :loading="loading"
      :pagination="pagination"
      row-key="id"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'type'">
          <a-tag :color="getTypeColor(record.type)">{{ getTypeText(record.type) }}</a-tag>
        </template>
        <template v-else-if="column.key === 'isTop'">
          <a-tag v-if="record.isTop" color="blue">置顶</a-tag>
          <span v-else>-</span>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="showEditModal(record)">
              编辑
            </a-button>
            <a-popconfirm title="确定删除？" @confirm="handleDelete(record.id)">
              <a-button type="link" size="small" danger>删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="editModalVisible"
      :title="editingId ? '编辑公告' : '发布公告'"
      @ok="handleSubmit"
      :confirm-loading="submitLoading"
      width="600px"
    >
      <a-form :model="form" layout="vertical">
        <a-form-item label="标题" required>
          <a-input v-model:value="form.title" placeholder="请输入标题" />
        </a-form-item>
        <a-form-item label="类型">
          <a-select v-model:value="form.type">
            <a-select-option value="NORMAL">普通</a-select-option>
            <a-select-option value="IMPORTANT">重要</a-select-option>
            <a-select-option value="URGENT">紧急</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="内容" required>
          <a-textarea v-model:value="form.content" :rows="6" placeholder="请输入公告内容" />
        </a-form-item>
        <a-form-item>
          <a-checkbox v-model:checked="form.isTop">置顶</a-checkbox>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { announcementApi } from '@/types/extra'
import type { Announcement } from '@/types/extra'
import { message } from 'ant-design-vue'
import { LeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const loading = ref(false)
const announcements = ref<Announcement[]>([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`
})

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '标题', dataIndex: 'title', key: 'title' },
  { title: '类型', key: 'type', width: 80 },
  { title: '置顶', key: 'isTop', width: 80 },
  { title: '阅读量', dataIndex: 'viewCount', key: 'viewCount', width: 80 },
  { title: '发布时间', dataIndex: 'createTime', key: 'createTime', width: 120 },
  { title: '操作', key: 'action', width: 120 }
]

const editModalVisible = ref(false)
const submitLoading = ref(false)
const editingId = ref<number | null>(null)

const form = reactive({
  title: '',
  content: '',
  type: 'NORMAL' as Announcement['type'],
  isTop: false
})

const getTypeText = (type: Announcement['type']) => {
  const map: Record<Announcement['type'], string> = {
    NORMAL: '普通',
    IMPORTANT: '重要',
    URGENT: '紧急'
  }
  return map[type]
}

const getTypeColor = (type: Announcement['type']) => {
  const map: Record<Announcement['type'], string> = {
    NORMAL: 'default',
    IMPORTANT: 'orange',
    URGENT: 'red'
  }
  return map[type]
}

const fetchAnnouncements = async () => {
  loading.value = true
  try {
    const res = await announcementApi.getList(pagination.current, pagination.pageSize)
    announcements.value = res.data.list
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleTableChange = (pag: { current: number; pageSize: number }) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchAnnouncements()
}

const showEditModal = (record: Announcement | null) => {
  if (record) {
    editingId.value = record.id
    form.title = record.title
    form.content = record.content
    form.type = record.type
    form.isTop = record.isTop
  } else {
    editingId.value = null
    form.title = ''
    form.content = ''
    form.type = 'NORMAL'
    form.isTop = false
  }
  editModalVisible.value = true
}

const handleSubmit = async () => {
  if (!form.title || !form.content) {
    message.warning('请填写完整信息')
    return
  }

  submitLoading.value = true
  try {
    if (editingId.value) {
      await announcementApi.update(editingId.value, {
        title: form.title,
        content: form.content,
        type: form.type,
        isTop: form.isTop
      })
      message.success('更新成功')
    } else {
      await announcementApi.create({
        title: form.title,
        content: form.content,
        type: form.type,
        isTop: form.isTop
      })
      message.success('发布成功')
    }
    editModalVisible.value = false
    fetchAnnouncements()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await announcementApi.delete(id)
    message.success('删除成功')
    fetchAnnouncements()
  } catch {
    // ignore
  }
}

onMounted(() => {
  fetchAnnouncements()
})
</script>

<style scoped>
.announcement-manage-page {
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #18181b;
}
</style>
