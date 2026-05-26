<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-#111111 text-white p-5 flex items-center justify-between">
      <div>
        <h1 class="text-22px m-0">习惯分类管理</h1>
        <p class="m-0 mt-2 text-#9ca3af">管理分类名称、颜色与排序</p>
      </div>
      <a-space>
        <a-button @click="router.push('/habits')">返回习惯列表</a-button>
        <a-button type="primary" class="bg-black border-black text-white" @click="openCreate">新增分类</a-button>
      </a-space>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <a-table :loading="loading" row-key="id" :data-source="rows" :pagination="false">
        <a-table-column title="分类名" data-index="name" key="name" />
        <a-table-column title="颜色" key="color" width="120">
          <template #default="{ record }">
            <div class="w-8 h-5 rounded border border-#e5e7eb" :style="{ backgroundColor: record.color || '#111111' }"></div>
          </template>
        </a-table-column>
        <a-table-column title="图标" data-index="icon" key="icon" width="90" />
        <a-table-column title="排序" data-index="sortOrder" key="sortOrder" width="90" />
        <a-table-column title="操作" key="action" width="220">
          <template #default="{ record }">
            <a-space>
              <a-button size="small" @click="openEdit(record)">编辑</a-button>
              <a-popconfirm title="确认删除该分类？" ok-text="删除" cancel-text="取消" @confirm="remove(record.id)">
                <a-button size="small" danger>删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </a-table-column>
      </a-table>
    </a-card>

    <a-modal v-model:open="modalOpen" :title="editingId ? '编辑分类' : '新增分类'" @ok="submit" @cancel="closeModal">
      <a-form layout="vertical">
        <a-form-item label="分类名称" required>
          <a-input v-model:value="form.name" />
        </a-form-item>
        <a-form-item label="图标">
          <a-input v-model:value="form.icon" />
        </a-form-item>
        <a-form-item label="颜色">
          <a-input v-model:value="form.color" placeholder="#111111" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="form.sortOrder" :min="0" style="width: 100%" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getCategories, createCategory, updateCategory, deleteCategory } from '../../api/habit'

const router = useRouter()
const loading = ref(false)
const rows = ref([])
const modalOpen = ref(false)
const editingId = ref(null)
const form = reactive({
  name: '',
  icon: '',
  color: '#111111',
  sortOrder: 0
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCategories()
    rows.value = res.data || []
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.name = ''
  form.icon = ''
  form.color = '#111111'
  form.sortOrder = 0
}

const openCreate = () => {
  editingId.value = null
  resetForm()
  modalOpen.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  form.name = row.name
  form.icon = row.icon || ''
  form.color = row.color || '#111111'
  form.sortOrder = row.sortOrder || 0
  modalOpen.value = true
}

const closeModal = () => {
  modalOpen.value = false
}

const submit = async () => {
  if (!form.name.trim()) {
    message.warning('请填写分类名称')
    return
  }
  const payload = {
    name: form.name.trim(),
    icon: form.icon || null,
    color: form.color || null,
    sortOrder: form.sortOrder || 0
  }
  if (editingId.value) {
    await updateCategory(editingId.value, payload)
    message.success('分类已更新')
  } else {
    await createCategory(payload)
    message.success('分类已创建')
  }
  closeModal()
  fetchData()
}

const remove = async (id) => {
  await deleteCategory(id)
  message.success('分类已删除')
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>
