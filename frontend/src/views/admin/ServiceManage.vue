<template>
  <div class="service-manage-page">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">服务管理</h1>
        <p class="page-subtitle">管理服务套餐内容</p>
      </div>
      <div class="header-right">
        <a-space>
          <a-button @click="goBack">返回后台</a-button>
          <a-button type="primary" @click="showCreateModal">
            <template #icon><PlusOutlined /></template>
            新增服务
          </a-button>
        </a-space>
      </div>
    </div>

    <div class="filter-bar">
      <a-space>
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索服务名称"
          style="width: 250px"
          @search="handleSearch"
        />
        <a-select v-model:value="typeFilter" placeholder="类型筛选" style="width: 140px" allowClear @change="handleFilter">
          <a-select-option value="farewell">告别仪式</a-select-option>
          <a-select-option value="cremation">火化服务</a-select-option>
          <a-select-option value="memorial">纪念服务</a-select-option>
          <a-select-option value="other">其他服务</a-select-option>
        </a-select>
        <a-select v-model:value="statusFilter" placeholder="状态筛选" style="width: 120px" allowClear @change="handleFilter">
          <a-select-option :value="1">启用</a-select-option>
          <a-select-option :value="0">禁用</a-select-option>
        </a-select>
      </a-space>
    </div>

    <a-table
      :columns="columns"
      :data-source="filteredServices"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      row-key="id"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'name'">
          <div class="service-name">
            <a-image
              v-if="record.image"
              :src="getImageUrl(record.image)"
              :width="36"
              :height="36"
              style="border-radius: 8px; object-fit: cover; margin-right: 8px"
            />
            <span>{{ record.name }}</span>
          </div>
        </template>
        <template v-if="column.key === 'type'">
          <a-tag :color="getTypeColor(record.type)">
            {{ getTypeText(record.type) }}
          </a-tag>
        </template>
        <template v-if="column.key === 'price'">
          <div class="price-info">
            <span class="current-price">¥{{ record.price }}</span>
            <span v-if="record.originalPrice" class="original-price">¥{{ record.originalPrice }}</span>
          </div>
        </template>
        <template v-if="column.key === 'description'">
          <a-tooltip :title="record.description" v-if="record.description">
            <span class="description-text">{{ record.description }}</span>
          </a-tooltip>
          <span v-else class="no-data">-</span>
        </template>
        <template v-if="column.key === 'status'">
          <a-switch
            :checked="record.status === 1"
            @change="(checked) => handleStatusChange(record, checked)"
          />
        </template>
        <template v-if="column.key === 'sortOrder'">
          <a-input-number
            :value="record.sortOrder"
            :min="0"
            size="small"
            style="width: 70px"
            @change="(value) => handleSortChange(record, value)"
          />
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="showEditModal(record)">
              编辑
            </a-button>
            <a-popconfirm
              title="确定要删除此服务吗？"
              @confirm="handleDelete(record.id)"
            >
              <a-button type="link" size="small" danger>删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="editModalVisible"
      :title="isEdit ? '编辑服务' : '新增服务'"
      @ok="handleSubmit"
      :confirmLoading="submitLoading"
      width="600px"
    >
      <a-form :model="editForm" layout="vertical" :rules="rules" ref="formRef">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="服务名称" name="name">
              <a-input v-model:value="editForm.name" placeholder="请输入服务名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="服务类型" name="type">
              <a-select v-model:value="editForm.type" placeholder="请选择服务类型">
                <a-select-option value="farewell">告别仪式</a-select-option>
                <a-select-option value="cremation">火化服务</a-select-option>
                <a-select-option value="memorial">纪念服务</a-select-option>
                <a-select-option value="other">其他服务</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="服务价格" name="price">
              <a-input-number
                v-model:value="editForm.price"
                :min="0"
                :precision="2"
                style="width: 100%"
                placeholder="请输入价格"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="原价">
              <a-input-number
                v-model:value="editForm.originalPrice"
                :min="0"
                :precision="2"
                style="width: 100%"
                placeholder="可选"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="服务描述">
          <a-textarea
            v-model:value="editForm.description"
            :rows="3"
            placeholder="请输入服务描述"
          />
        </a-form-item>
        <a-form-item label="服务内容">
          <a-textarea
            v-model:value="editForm.includes"
            :rows="3"
            placeholder="请输入包含的服务内容，用换行分隔"
          />
        </a-form-item>
        <a-form-item label="服务图片">
          <a-upload
            list-type="picture-card"
            :show-upload-list="false"
            :before-upload="beforeUpload"
            @change="handleUpload"
          >
            <img v-if="editForm.image" :src="getImageUrl(editForm.image)" alt="service" style="width: 100%; height: 100%; object-fit: cover; border-radius: 8px" />
            <div v-else>
              <PlusOutlined />
              <div style="margin-top: 8px">上传图片</div>
            </div>
          </a-upload>
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="排序">
              <a-input-number
                v-model:value="editForm.sortOrder"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态">
              <a-switch
                :checked="editForm.status === 1"
                @change="(checked) => editForm.status = checked ? 1 : 0"
              />
              <span class="status-text">{{ editForm.status === 1 ? '启用' : '禁用' }}</span>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { getServiceList, createService, updateService, deleteService, updateServiceStatus } from '../../api/service'
import { uploadFile } from '../../api/file'
import { getImageUrl } from '../../utils'

const router = useRouter()
const loading = ref(false)
const editModalVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const searchKeyword = ref('')
const typeFilter = ref(undefined)
const statusFilter = ref(undefined)
const services = ref([])
const formRef = ref(null)

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条`
})

const editForm = reactive({
  id: null,
  name: '',
  type: undefined,
  description: '',
  price: 0,
  originalPrice: undefined,
  includes: '',
  image: '',
  sortOrder: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入服务名称' }],
  type: [{ required: true, message: '请选择服务类型' }],
  price: [{ required: true, message: '请输入服务价格' }]
}

const columns = [
  { title: '服务名称', dataIndex: 'name', key: 'name', width: 180 },
  { title: '类型', dataIndex: 'type', key: 'type', width: 100 },
  { title: '价格', key: 'price', width: 120 },
  { title: '描述', key: 'description', ellipsis: true },
  { title: '排序', dataIndex: 'sortOrder', key: 'sortOrder', width: 90 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 80 },
  { title: '操作', key: 'action', width: 140 }
]

const filteredServices = computed(() => {
  let result = services.value
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(item => 
      item.name?.toLowerCase().includes(keyword)
    )
  }
  
  if (typeFilter.value) {
    result = result.filter(item => item.type === typeFilter.value)
  }
  
  if (statusFilter.value !== undefined) {
    result = result.filter(item => item.status === statusFilter.value)
  }
  
  return result
})

const getTypeColor = (type) => {
  const colors = {
    farewell: 'purple',
    cremation: 'orange',
    memorial: 'cyan',
    other: 'default'
  }
  return colors[type] || 'default'
}

const getTypeText = (type) => {
  const texts = {
    farewell: '告别仪式',
    cremation: '火化服务',
    memorial: '纪念服务',
    other: '其他服务'
  }
  return texts[type] || type
}

const loadServices = async () => {
  loading.value = true
  try {
    const res = await getServiceList()
    if (res.code === 200) {
      const payload = res.data
      if (Array.isArray(payload)) {
        services.value = payload
      } else if (Array.isArray(payload?.records)) {
        services.value = payload.records
      } else if (Array.isArray(payload?.list)) {
        services.value = payload.list
      } else {
        services.value = []
      }
      pagination.total = services.value.length
    }
  } catch (error) {
    console.error('加载服务列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
}

const handleFilter = () => {
  pagination.current = 1
}

const goBack = () => {
  router.push('/admin')
}

const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
}

const resetForm = () => {
  editForm.id = null
  editForm.name = ''
  editForm.type = undefined
  editForm.description = ''
  editForm.price = 0
  editForm.originalPrice = undefined
  editForm.includes = ''
  editForm.image = ''
  editForm.sortOrder = 0
  editForm.status = 1
}

const showCreateModal = () => {
  isEdit.value = false
  resetForm()
  editModalVisible.value = true
}

const showEditModal = (record) => {
  isEdit.value = true
  editForm.id = record.id
  editForm.name = record.name
  editForm.type = record.type
  editForm.description = record.description || ''
  editForm.price = record.price
  editForm.originalPrice = record.originalPrice
  editForm.includes = record.includes || ''
  editForm.image = record.image || ''
  editForm.sortOrder = record.sortOrder || 0
  editForm.status = record.status
  editModalVisible.value = true
}

const beforeUpload = (file) => {
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
  return false
}

const handleUpload = async (info) => {
  const rawFile = info.file?.originFileObj || info.file
  if (!rawFile) {
    return
  }
  try {
    const res = await uploadFile(rawFile, 'services')
    if (res.code === 200) {
      editForm.image = res.data.url
      message.success('上传成功')
    }
  } catch (error) {
    console.error('服务图片上传失败:', error)
    message.error('上传失败')
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }

  submitLoading.value = true
  try {
    const data = {
      name: editForm.name,
      type: editForm.type,
      description: editForm.description,
      price: editForm.price,
      originalPrice: editForm.originalPrice,
      includes: editForm.includes,
      image: editForm.image,
      sortOrder: editForm.sortOrder,
      status: editForm.status
    }

    if (isEdit.value) {
      await updateService(editForm.id, data)
      message.success('更新成功')
    } else {
      await createService(data)
      message.success('创建成功')
    }
    
    editModalVisible.value = false
    loadServices()
  } catch (error) {
    console.error('提交失败:', error)
    message.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleStatusChange = async (record, checked) => {
  try {
    await updateServiceStatus(record.id, checked ? 1 : 0)
    message.success('状态更新成功')
    loadServices()
  } catch (error) {
    console.error('更新状态失败:', error)
  }
}

const handleSortChange = async (record, value) => {
  try {
    await updateService(record.id, { sortOrder: value })
    message.success('排序更新成功')
    loadServices()
  } catch (error) {
    console.error('更新排序失败:', error)
  }
}

const handleDelete = async (id) => {
  try {
    await deleteService(id)
    message.success('删除成功')
    loadServices()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

onMounted(() => {
  loadServices()
})
</script>

<style scoped>
.service-manage-page {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #262626;
  margin: 0;
}

.page-subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 4px 0 0;
}

.filter-bar {
  margin-bottom: 24px;
}

.service-name {
  display: flex;
  align-items: center;
  font-weight: 500;
  color: #262626;
}

.price-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.current-price {
  color: #ff4d4f;
  font-weight: 600;
}

.original-price {
  color: #bfbfbf;
  text-decoration: line-through;
  font-size: 12px;
}

.description-text {
  color: #595959;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.no-data {
  color: #bfbfbf;
}

.status-text {
  margin-left: 8px;
  color: #8c8c8c;
}
</style>
