<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  FilterOutlined,
  UploadOutlined,
  DollarOutlined,
  InboxOutlined,
  TagOutlined,
  FireOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '../../stores/user'
import { storeApi, categoryApi } from '../../api'
import type { UploadProps } from 'ant-design-vue'

const userStore = useUserStore()

const products = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({
  page: 1,
  size: 10,
  keyword: '',
  status: undefined as number | undefined,
  categoryId: undefined as number | undefined,
  stockWarning: false
})

const visible = ref(false)
const formRef = ref<any>()
const formState = ref<any>({
  id: null,
  name: '',
  description: '',
  price: 0,
  stock: 0,
  stockWarning: 10,
  categoryId: undefined,
  mainImageUrl: '',
  imageUrls: [],
  isNew: false,
  isHot: false
})

const fileList = ref<any[]>([])
const imageFileList = ref<any[]>([]) // 多图上传文件列表
const categories = ref<any[]>([])

// 审核状态选项
const statusOptions = [
  { label: '全部状态', value: undefined },
  { label: '待审核', value: 0 },
  { label: '已通过', value: 1 },
  { label: '已驳回', value: 2 }
]

const statusMap: Record<number, { text: string; color: string }> = {
  0: { text: '待审核', color: 'orange' },
  1: { text: '已通过', color: 'green' },
  2: { text: '已驳回', color: 'red' }
}

const isAdmin = computed(() => userStore.role === 'ROLE_ADMIN')

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

/** 分类下拉：兼容 getAll 返回格式 */
const categoryOptions = computed(() => {
  const normalized = (categories.value || []).map((c: any) => {
    if (c && typeof c === 'object') {
      const value = c.id ?? c.value
      const label = c.name ?? c.label ?? String(value ?? '')
      return { value, label }
    }
    return { value: c, label: String(c) }
  })

  return [{ label: '全部分类', value: undefined }, ...normalized]
})

onMounted(() => {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    window.location.href = '/login'
    return
  }
  loadProducts()
  loadCategories()
})

const loadProducts = async () => {
  loading.value = true
  try {
    const params: any = {
      page: queryParams.value.page - 1,
      size: queryParams.value.size
    }

    if (queryParams.value.keyword) params.keyword = queryParams.value.keyword
    if (queryParams.value.status !== undefined) params.status = queryParams.value.status
    if (queryParams.value.categoryId !== undefined) params.categoryId = queryParams.value.categoryId
    if (queryParams.value.stockWarning === true) params.stockWarning = true

    const res = await storeApi.getProducts(params)
    products.value = res.content || []
    total.value = res.totalElements || 0
  } catch (error: any) {
    message.error('加载商品失败: ' + (error.response?.data?.message || error.message || '未知错误'))
    products.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.value.page = 1
  loadProducts()
}

const handleReset = () => {
  queryParams.value = {
    page: 1,
    size: queryParams.value.size,
    keyword: '',
    status: undefined,
    categoryId: undefined,
    stockWarning: false
  }
  loadProducts()
}

const handleTableChange = (pagination: any) => {
  queryParams.value.page = pagination.current
  queryParams.value.size = pagination.pageSize
  loadProducts()
}

const loadCategories = async () => {
  try {
    const res = await categoryApi.getAll()
    categories.value = res || []
  } catch (error) {
    message.error('加载分类失败')
  }
}

const handleAdd = () => {
  formState.value = {
    id: null,
    name: '',
    description: '',
    price: 0,
    stock: 0,
    stockWarning: 10,
    categoryId: undefined,
    mainImageUrl: '',
    imageUrls: [],
    isNew: false,
    isHot: false
  }
  imageFileList.value = [] // 清空多图上传列表
  visible.value = true
}

const handleEdit = (record: any) => {
  // 处理 imageUrls：如果是字符串则转换为数组，如果是数组则直接使用
  let imageUrlsArray: string[] = []
  if (record.imageUrls) {
    if (typeof record.imageUrls === 'string') {
      imageUrlsArray = record.imageUrls.split(',').filter((url: string) => url.trim() !== '')
    } else if (Array.isArray(record.imageUrls)) {
      imageUrlsArray = record.imageUrls
    }
  }

  formState.value = {
    ...record,
    imageUrls: imageUrlsArray,
    // 第一张图片作为主图
    mainImageUrl: imageUrlsArray.length > 0 ? imageUrlsArray[0] : record.imageUrl || ''
  }
  
  // 设置多图上传文件列表（最多4张）
  imageFileList.value = imageUrlsArray.slice(0, 4).map((url: string, index: number) => ({
    uid: `-${index + 1}`,
    name: `image-${index + 1}.jpg`,
    status: 'done',
    url: url,
    response: url
  }))
  
  visible.value = true
}

const handleDelete = async (id: number) => {
  try {
    await storeApi.deleteProduct(id)
    message.success('删除成功')
    loadProducts()
  } catch (error: any) {
    message.error(error.message || '删除失败')
  }
}

const handleToggleStatus = async (record: any) => {
  try {
    const newActive = !record.active
    await storeApi.toggleProduct(record.id, newActive)
    message.success(newActive ? '已上架' : '已下架')
    loadProducts()
  } catch (error: any) {
    message.error(error.message || '操作失败')
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    
    // 确保 imageUrls 是数组格式
    const submitData = { ...formState.value }
    if (typeof submitData.imageUrls === 'string') {
      submitData.imageUrls = submitData.imageUrls.split(',').filter((url: string) => url.trim() !== '')
    } else if (!Array.isArray(submitData.imageUrls)) {
      submitData.imageUrls = []
    }
    
    if (formState.value.id) {
      await storeApi.updateProduct(formState.value.id, submitData)
      message.success('更新成功')
    } else {
      await storeApi.createProduct(submitData)
      message.success('创建成功')
    }
    visible.value = false
    loadProducts()
  } catch (error: any) {
    message.error(error.message || '操作失败')
  }
}

const customUpload: UploadProps['customRequest'] = async (options) => {
  const { file, onSuccess, onError } = options
  try {
    const result = await storeApi.uploadImage(file as File)
    onSuccess?.(result)
    message.success('上传成功')
  } catch (error: any) {
    onError?.(error)
    message.error('上传失败: ' + (error.message || '未知错误'))
  }
}

// 处理多图上传变化
const handleImageUploadChange = (info: any) => {
  // 更新文件列表
  imageFileList.value = info.fileList
  
  // 提取所有已上传图片的URL（相对路径）
  const uploadedUrls = info.fileList
    .filter((file: any) => file.status === 'done' && file.response)
    .map((file: any) => file.response)
    .slice(0, 4) // 最多4张
  
  // 更新 formState
  formState.value.imageUrls = uploadedUrls
  // 第一张作为主图
  formState.value.mainImageUrl = uploadedUrls.length > 0 ? uploadedUrls[0] : ''
}

// 将相对路径转换为完整URL（使用前端代理，不直接拼接后端地址）
const getFullImageUrl = (url: string): string => {
  if (!url) return ''
  // 如果已经是绝对路径，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  // 相对路径直接使用（前端代理会自动转发到后端）
  return url.startsWith('/') ? url : '/' + url
}

// 获取商品第一张图片URL：优先使用 imageUrls，为空则使用 imageUrl
const getFirstImageUrl = (record: any): string => {
  // 1. 优先使用 imageUrls
  if (record.imageUrls) {
    let urls: string[] = []
    if (typeof record.imageUrls === 'string') {
      urls = record.imageUrls.split(',').filter((url: string) => url.trim() !== '')
    } else if (Array.isArray(record.imageUrls)) {
      urls = record.imageUrls
    }
    
    if (urls.length > 0) {
      return getFullImageUrl(urls[0].trim())
    }
  }
  
  // 2. 降级使用 imageUrl
  if (record.imageUrl) {
    return getFullImageUrl(record.imageUrl)
  }
  
  // 3. 最后尝试 mainImageUrl
  if (record.mainImageUrl) {
    return getFullImageUrl(record.mainImageUrl)
  }
  
  return ''
}

const columns = computed(() => [
  { title: '商品图片', dataIndex: 'mainImageUrl', key: 'image', width: 100, align: 'center' },
  { title: '商品名称', dataIndex: 'name', key: 'name', ellipsis: true },
  { title: '价格', dataIndex: 'price', key: 'price', width: 120, align: 'right' },
  { title: '库存', dataIndex: 'stock', key: 'stock', width: 100, align: 'right' },
  { title: '销量', dataIndex: 'sales', key: 'sales', width: 100, align: 'right' },
  { title: '审核状态', dataIndex: 'status', key: 'status', width: 110, align: 'center' },
  { title: '上下架', dataIndex: 'active', key: 'active', width: 120, align: 'center' },
  { title: '操作', key: 'action', width: 180, align: 'center', fixed: 'right' }
])
</script>

<template>
  <div class="store-products">
    <div class="page-header">
      <h2>{{ isAdmin ? '商品管理（管理员）' : '商品管理' }}</h2>
      <a-button type="primary" size="large" @click="handleAdd">
        <template #icon><PlusOutlined /></template>
        添加商品
      </a-button>
    </div>

    <!-- 筛选栏（对齐优化） -->
    <a-card class="filter-card" :bordered="false">
      <a-row :gutter="12">
        <a-col :xs="24" :sm="12" :md="8" :lg="7">
          <div class="filter-item">
            <label class="filter-label">商品搜索</label>
            <a-input
              v-model:value="queryParams.keyword"
              placeholder="搜索商品名称"
              allow-clear
              @pressEnter="handleSearch"
            >
              <template #prefix><SearchOutlined /></template>
              <template #suffix>
                <a-button type="text" size="small" @click="handleSearch">搜索</a-button>
              </template>
            </a-input>
          </div>
        </a-col>

        <a-col :xs="24" :sm="12" :md="8" :lg="5">
          <div class="filter-item">
            <label class="filter-label">商品分类</label>
            <a-select
              v-model:value="queryParams.categoryId"
              placeholder="全部分类"
              allow-clear
              style="width: 100%"
              @change="handleSearch"
              :options="categoryOptions"
            />
          </div>
        </a-col>

        <a-col :xs="24" :sm="12" :md="8" :lg="4">
          <div class="filter-item">
            <label class="filter-label">审核状态</label>
            <a-select
              v-model:value="queryParams.status"
              placeholder="全部状态"
              allow-clear
              style="width: 100%"
              @change="handleSearch"
              :options="statusOptions"
            />
          </div>
        </a-col>

        <a-col :xs="24" :sm="12" :md="8" :lg="4">
          <div class="filter-item">
            <label class="filter-label">库存预警</label>
            <a-select v-model:value="queryParams.stockWarning" style="width: 100%" @change="handleSearch">
              <a-select-option :value="false">全部</a-select-option>
              <a-select-option :value="true">库存不足</a-select-option>
            </a-select>
          </div>
        </a-col>

        <a-col :xs="24" :lg="4">
          <div class="filter-actions">
            <a-button @click="handleReset">
              <template #icon><FilterOutlined /></template>
              重置
            </a-button>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <a-card class="table-card" :bordered="false">
      <a-table
        :columns="columns"
        :data-source="products"
        :loading="loading"
        :pagination="{
          current: queryParams.page,
          pageSize: queryParams.size,
          total: total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (t) => `共 ${t} 条`,
          pageSizeOptions: ['10', '20', '50', '100']
        }"
        row-key="id"
        :scroll="{ x: 1200 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'image'">
            <!-- 优先使用 imageUrls（相对路径），为空则使用 imageUrl（绝对路径） -->
            <a-image
              v-if="getFirstImageUrl(record)"
              :src="getFirstImageUrl(record)"
              :width="60"
              :height="60"
              style="border-radius: 8px; object-fit: cover;"
            />
            <div v-else class="no-image">
              <InboxOutlined />
            </div>
          </template>

          <template v-else-if="column.key === 'price'">
            <span class="price-text">¥{{ record.price?.toFixed(2) || '0.00' }}</span>
          </template>

          <template v-else-if="column.key === 'stock'">
            <a-tag :color="record.stock <= record.stockWarning ? 'orange' : 'blue'">
              {{ record.stock }}
            </a-tag>
          </template>

          <template v-else-if="column.key === 'sales'">
            <span class="sales-text">{{ record.sales || 0 }}</span>
          </template>

          <template v-else-if="column.key === 'status'">
            <a-tag :color="statusMap[record.status]?.color || 'default'">
              <template #icon>
                <component
                  :is="statusMap[record.status]?.color === 'green' ? CheckCircleOutlined : ExclamationCircleOutlined"
                />
              </template>
              {{ statusMap[record.status]?.text || '未知' }}
            </a-tag>
          </template>

          <template v-else-if="column.key === 'active'">
            <a-switch
              :checked="record.active"
              :disabled="record.status !== 1"
              checked-children="已上架"
              un-checked-children="已下架"
              @change="handleToggleStatus(record)"
            />
          </template>

          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-tooltip title="编辑商品">
                <a-button type="text" size="small" @click="handleEdit(record)">
                  <template #icon><EditOutlined /></template>
                </a-button>
              </a-tooltip>

              <a-popconfirm title="确定要删除这个商品吗？" ok-text="确定" cancel-text="取消" @confirm="handleDelete(record.id)">
                <a-tooltip title="删除商品">
                  <a-button type="text" danger size="small">
                    <template #icon><DeleteOutlined /></template>
                  </a-button>
                </a-tooltip>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="visible"
      :title="formState.id ? '编辑商品' : '添加商品'"
      width="700px"
      :confirm-loading="loading"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleSubmit"
      @cancel="() => { visible = false; imageFileList = []; }"
    >
      <a-form ref="formRef" :model="formState" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }" layout="horizontal">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="商品名称" name="name" :rules="[{ required: true, message: '请输入商品名称' }]">
              <a-input v-model:value="formState.name" placeholder="请输入商品名称" size="large" />
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="商品分类" name="categoryId" :rules="[{ required: true, message: '请选择商品分类' }]">
              <a-select v-model:value="formState.categoryId" placeholder="请选择商品分类" size="large">
                <a-select-option v-for="cat in categoryOptions.slice(1)" :key="cat.value" :value="cat.value">
                  {{ cat.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="商品价格" name="price" :rules="[{ required: true, message: '请输入商品价格' }]">
              <a-input-number
                v-model:value="formState.price"
                :min="0"
                :precision="2"
                style="width: 100%"
                size="large"
                placeholder="0.00"
              >
                <template #prefix><DollarOutlined /></template>
              </a-input-number>
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="商品库存" name="stock" :rules="[{ required: true, message: '请输入商品库存' }]">
              <a-input-number v-model:value="formState.stock" :min="0" style="width: 100%" size="large" placeholder="请输入商品库存">
                <template #prefix><InboxOutlined /></template>
              </a-input-number>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="库存预警" name="stockWarning" :rules="[{ required: true, message: '请输入库存预警值' }]">
              <a-input-number v-model:value="formState.stockWarning" :min="0" style="width: 100%" size="large" placeholder="库存低于此值时预警">
                <template #prefix><TagOutlined /></template>
              </a-input-number>
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="商品标签">
          <a-space size="large">
            <a-checkbox v-model:checked="formState.isNew">
              <FireOutlined style="color: #fa8c16;" /> 新品
            </a-checkbox>
            <a-checkbox v-model:checked="formState.isHot">
              <FireOutlined style="color: #f5222d;" /> 热销
            </a-checkbox>
          </a-space>
        </a-form-item>

        <a-form-item label="商品描述" name="description">
          <a-textarea v-model:value="formState.description" :rows="4" placeholder="请输入商品描述" show-count :maxlength="500" />
        </a-form-item>

        <a-form-item label="商品图片" name="imageUrls">
          <a-upload
            v-model:file-list="imageFileList"
            list-type="picture-card"
            :custom-request="customUpload"
            @change="handleImageUploadChange"
            :max-count="4"
            :headers="uploadHeaders"
            multiple
          >
            <div v-if="imageFileList.length < 4">
              <UploadOutlined style="font-size: 32px; color: #999;" />
              <div style="margin-top: 8px; color: #999;">点击上传</div>
            </div>
          </a-upload>
          <div style="margin-top: 8px; color: #999; font-size: 12px;">支持 JPG、PNG 格式，最多上传4张，建议尺寸 800x800px</div>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.store-products {
  padding: 24px;
  background: #f5f5f5;
  min-height: calc(100vh - 120px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 18px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.page-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #111827;
}

.filter-card,
.table-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.filter-card {
  margin-bottom: 16px;
}

.filter-item {
  margin-bottom: 8px;
}

.filter-label {
  display: block;
  margin-bottom: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #4b5563;
}

.filter-actions {
  display: flex;
  align-items: end;
  height: 100%;
  padding-bottom: 8px;
}

.no-image {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ed 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-size: 24px;
  color: #b0b8c5;
}

.price-text {
  font-weight: 700;
  color: #10b981;
  font-size: 15px;
}

.sales-text {
  color: #64748b;
  font-weight: 500;
}
</style>
