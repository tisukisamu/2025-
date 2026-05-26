<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-semibold text-neutral-900">公告管理</h1>
      <a-button type="primary" class="!bg-neutral-900" @click="openCreateModal">新建公告</a-button>
    </div>

    <a-card :bordered="false" class="rounded-xl">
      <a-table :columns="columns" :data-source="announcements" row-key="id" :pagination="{ pageSize: 8 }">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'type'">
            <a-tag>{{ record.type }}</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === 'PUBLISHED' ? 'green' : 'gold'">
              {{ record.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <div class="flex items-center gap-2">
              <a-button size="small" @click="openEditModal(record)">编辑</a-button>
              <a-button size="small" :disabled="record.status === 'PUBLISHED'" @click="publishAnnouncement(record.id)">
                发布
              </a-button>
              <a-button size="small" danger @click="deleteAnnouncement(record.id)">删除</a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="modalOpen"
      :title="editingId ? '编辑公告' : '新建公告'"
      ok-text="保存"
      cancel-text="取消"
      @ok="saveAnnouncement"
    >
      <a-form layout="vertical">
        <a-form-item label="公告标题">
          <a-input v-model:value="formData.title" placeholder="请输入标题" />
        </a-form-item>
        <a-form-item label="公告类型">
          <a-select v-model:value="formData.type">
            <a-select-option value="NOTICE">通知</a-select-option>
            <a-select-option value="POLICY">政策</a-select-option>
            <a-select-option value="UPDATE">更新</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="公告内容">
          <a-textarea v-model:value="formData.content" :rows="5" placeholder="请输入公告内容" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { announcementApi } from '@/api'

const announcements = ref<any[]>([])
const modalOpen = ref(false)
const editingId = ref<number | null>(null)
const formData = reactive({
  title: '',
  content: '',
  type: 'NOTICE'
})

const columns = [
  { title: 'ID', dataIndex: 'id', width: 80 },
  { title: '标题', dataIndex: 'title' },
  { title: '类型', dataIndex: 'type', width: 120 },
  { title: '状态', dataIndex: 'status', width: 120 },
  { title: '发布时间', dataIndex: 'createdAt', width: 140 },
  { title: '操作', dataIndex: 'action', width: 230 }
]

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleDateString('zh-CN')
}

const fetchAnnouncements = async () => {
  try {
    const res = await announcementApi.getAll()
    announcements.value = (res.data || []).map((item: any) => ({
      ...item,
      createdAt: formatDate(item.createdAt)
    }))
  } catch (error) {
    message.error('获取公告列表失败')
  }
}

const openCreateModal = () => {
  editingId.value = null
  formData.title = ''
  formData.content = ''
  formData.type = 'NOTICE'
  modalOpen.value = true
}

const openEditModal = (record: any) => {
  editingId.value = record.id
  formData.title = record.title
  formData.content = record.content
  formData.type = record.type || 'NOTICE'
  modalOpen.value = true
}

const saveAnnouncement = async () => {
  if (!formData.title || !formData.content) {
    message.warning('请完整填写公告信息')
    return
  }
  try {
    if (editingId.value) {
      await announcementApi.update(editingId.value, { ...formData })
      message.success('公告已更新')
    } else {
      await announcementApi.create({ ...formData })
      message.success('公告已创建')
    }
    modalOpen.value = false
    await fetchAnnouncements()
  } catch (error) {
    message.error('保存失败')
  }
}

const publishAnnouncement = async (id: number) => {
  try {
    await announcementApi.publish(id)
    message.success('公告已发布')
    await fetchAnnouncements()
  } catch (error) {
    message.error('发布失败')
  }
}

const deleteAnnouncement = (id: number) => {
  Modal.confirm({
    title: '确认删除该公告？',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        await announcementApi.delete(id)
        message.success('公告已删除')
        await fetchAnnouncements()
      } catch (error) {
        message.error('删除失败')
      }
    }
  })
}

onMounted(() => {
  fetchAnnouncements()
})
</script>
