<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { message, Empty } from 'ant-design-vue'
import {
  PlusOutlined,
  SearchOutlined,
  EditOutlined,
  DeleteOutlined,
  FilterOutlined,
  ReloadOutlined,
  AppstoreOutlined,
  DollarOutlined,
  InboxOutlined,
  TagOutlined,
  FireOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  ShoppingCartOutlined,
  UploadOutlined
} from '@ant-design/icons-vue'
import type { UploadProps } from 'ant-design-vue'
import { auditApi, categoryApi } from '../../api'
import type { Product } from '../../api'

// 查询参数
const products = ref<Product[]>([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({
  page: 1,
  size: 10,
  keyword: '',
  status: undefined as number | undefined,
  categoryId: undefined as number | undefined
})

// 表单和弹窗
const modalVisible = ref(false)
const deleteModalVisible = ref(false)
const productToDelete = ref<number | null>(null)
const editingProduct = ref<Partial<Product>>({})
const formRef = ref()

// 多图上传文件列表
const imageFileList = ref<any[]>([])
const uploadHeaders = ref({
  Authorization: `Bearer ${localStorage.getItem('token') || ''}`
})

// 分类数据
const categories = ref([])

// 审核状态选项
const statusOptions = [
  { label: '全部状态', value: undefined },
  { label: '待审核', value: 0 },
  { label: '已通过', value: 1 },
  { label: '已驳回', value: 2 }
]

// 状态映射
const statusMap = {
  0: { text: '待审核', color: 'warning', icon: ExclamationCircleOutlined },
  1: { text: '已通过', color: 'success', icon: CheckCircleOutlined },
  2: { text: '已驳回', color: 'error', icon: ExclamationCircleOutlined }
}

// 搜索防抖
let searchTimer: number | undefined
watch(
  () => queryParams.value.keyword,
  (val) => {
    clearTimeout(searchTimer)
    searchTimer = setTimeout(() => {
      handleSearch()
    }, 300)
  }
)

onMounted(async () => {
  await Promise.all([loadProducts(), loadCategories()])
})

// 加载商品列表（使用后端分页）
const loadProducts = async () => {
  loading.value = true
  try {
    const params: any = {
      page: queryParams.value.page - 1,
      size: queryParams.value.size
    }

    // 添加筛选参数
    if (queryParams.value.keyword) {
      params.keyword = queryParams.value.keyword
    }
    if (queryParams.value.status !== undefined) {
      params.status = queryParams.value.status
    }
    if (queryParams.value.categoryId !== undefined) {
      params.categoryId = queryParams.value.categoryId
    }

    const res = await auditApi.getProducts(params)
    products.value = res.content || []
    total.value = res.totalElements || 0
  } catch (error: any) {
    console.error('加载商品失败:', error)
    message.error('加载商品失败: ' + (error.response?.data?.message || error.message || '未知错误'))
    products.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 加载分类
const loadCategories = async () => {
  try {
    const res = await categoryApi.getAll()
    categories.value = res || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

// 搜索（重置到第一页）
const handleSearch = () => {
  queryParams.value.page = 1
  loadProducts()
}

// 重置筛选
const handleReset = () => {
  queryParams.value = {
    page: 1,
    size: queryParams.value.size,
    keyword: '',
    status: undefined,
    categoryId: undefined
  }
  loadProducts()
}

// 刷新列表
const handleRefresh = () => {
  loadProducts()
}

// 分页变化
const handleTableChange = (pagination: any) => {
  queryParams.value.page = pagination.current
  queryParams.value.size = pagination.pageSize
  loadProducts()
}

// 状态筛选变化
const handleStatusChange = (value: number) => {
  queryParams.value.page = 1
  queryParams.value.status = value
  loadProducts()
}

// 分类筛选变化
const handleCategoryChange = (value: number) => {
  queryParams.value.page = 1
  queryParams.value.categoryId = value
  loadProducts()
}

// 将相对路径转换为完整URL
const getFullImageUrl = (url: string): string => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  return url.startsWith('/') ? url : '/' + url
}

// 打开编辑弹窗
const handleEdit = (product: Product) => {
  // 处理 imageUrls：如果是字符串则转换为数组
  let imageUrlsArray: string[] = []
  if (product.imageUrls) {
    if (typeof product.imageUrls === 'string') {
      imageUrlsArray = product.imageUrls.split(',').filter((url: string) => url.trim() !== '')
    } else if (Array.isArray(product.imageUrls)) {
      imageUrlsArray = product.imageUrls
    }
  }

  editingProduct.value = {
    ...product,
    imageUrls: imageUrlsArray,
    imageUrl: imageUrlsArray.length > 0 ? imageUrlsArray[0] : product.imageUrl || ''
  }

  // 设置多图上传文件列表（最多4张）
  imageFileList.value = imageUrlsArray.slice(0, 4).map((url: string, index: number) => ({
    uid: `-${index + 1}`,
    name: `image-${index + 1}.jpg`,
    status: 'done',
    url: getFullImageUrl(url),
    response: url
  }))

  modalVisible.value = true
}

// 打开新增弹窗
const handleAdd = () => {
  editingProduct.value = {
    name: '',
    description: '',
    price: 0,
    stock: 0,
    categoryId: undefined,
    imageUrl: '',
    imageUrls: [],
    active: true,
    isNew: false,
    isHot: false
  }
  imageFileList.value = []
  modalVisible.value = true
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
  
  return ''
}

// 确认删除
const confirmDelete = (id: number) => {
  productToDelete.value = id
  deleteModalVisible.value = true
}

// 执行删除
const handleDelete = async () => {
  if (!productToDelete.value) return
  try {
    await auditApi.deleteProduct(productToDelete.value)
    message.success('删除成功')
    deleteModalVisible.value = false
    productToDelete.value = null
    await loadProducts()
  } catch (error: any) {
    message.error('删除失败: ' + (error.message || '未知错误'))
  }
}

// 切换上下架
const toggleActive = async (product: Product) => {
  try {
    const newActive = !product.active
    await auditApi.toggleActive(product.id, newActive)
    message.success(newActive ? '已上架' : '已下架')
    await loadProducts()
  } catch (error: any) {
    message.error('操作失败: ' + (error.message || '未知错误'))
  }
}

// 保存商品
const handleSave = async () => {
  try {
    await formRef.value.validate()

    // 准备提交数据
    const submitData = { ...editingProduct.value }
    
    // 处理图片数据：imageUrls 是数组，需要转换为逗号分隔的字符串
    if (Array.isArray(submitData.imageUrls)) {
      // 只取前4张
      submitData.imageUrls = submitData.imageUrls.slice(0, 4).join(',')
    }
    
    // 第一张图片作为主图
    if (submitData.imageUrls && typeof submitData.imageUrls === 'string') {
      const urls = submitData.imageUrls.split(',')
      if (urls.length > 0) {
        submitData.imageUrl = urls[0]
      }
    }

    if (editingProduct.value.id) {
      await auditApi.updateProduct(editingProduct.value.id, submitData as any)
      message.success('更新成功')
    } else {
      await auditApi.createProduct(submitData as any)
      message.success('创建成功')
    }

    modalVisible.value = false
    editingProduct.value = {}
    imageFileList.value = []
    await loadProducts()
  } catch (error: any) {
    message.error('保存失败: ' + (error.message || '未知错误'))
  }
}

// 自定义上传请求
const customUpload: UploadProps['customRequest'] = async (options) => {
  const { file, onSuccess, onError } = options
  try {
    // 使用 storeApi 上传图片（管理员也使用同一个上传接口）
    const result = await auditApi.uploadImage(file as File)
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
  
  // 更新 editingProduct
  editingProduct.value.imageUrls = uploadedUrls
  // 第一张作为主图
  editingProduct.value.imageUrl = uploadedUrls.length > 0 ? uploadedUrls[0] : ''
}

// 表格列配置
const columns = [
  {
    title: '商品图片',
    dataIndex: 'imageUrl',
    key: 'image',
    width: 80,
    align: 'center'
  },
  {
    title: '商品名称',
    dataIndex: 'name',
    key: 'name',
    ellipsis: true,
    width: 200
  },
  {
    title: '价格',
    dataIndex: 'price',
    key: 'price',
    width: 120,
    align: 'right',
    sorter: true
  },
  {
    title: '库存',
    dataIndex: 'stock',
    key: 'stock',
    width: 100,
    align: 'right',
    sorter: true
  },
  {
    title: '销量',
    dataIndex: 'sales',
    key: 'sales',
    width: 100,
    align: 'right',
    sorter: true,
    defaultSortOrder: 'descend'
  },
  {
    title: '分类',
    dataIndex: 'category',
    key: 'category',
    width: 120,
    align: 'center'
  },
  {
    title: '审核状态',
    dataIndex: 'status',
    key: 'status',
    width: 120,
    align: 'center'
  },
  {
    title: '上下架',
    dataIndex: 'active',
    key: 'active',
    width: 100,
    align: 'center'
  },
  {
    title: '操作',
    key: 'action',
    width: 180,
    align: 'center',
    fixed: 'right'
  }
]
</script>

<template>
  <div class="admin-products-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">商品管理</h1>
        <p class="page-subtitle">
          共 <span class="highlight">{{ total }}</span> 个商品
        </p>
      </div>
      <div class="header-actions">
        <a-button type="primary" size="large" @click="handleAdd">
          <template #icon><PlusOutlined /></template>
          新建商品
        </a-button>
        <a-button @click="handleRefresh">
          <template #icon><ReloadOutlined /></template>
          刷新
        </a-button>
      </div>
    </div>

    <!-- 筛选栏 -->
    <a-card class="filter-card" :bordered="false">
      <a-row :gutter="16">
        <a-col :xs="24" :sm="12" :md="8" :lg="6">
          <div class="filter-item">
            <label class="filter-label"><SearchOutlined /> 商品搜索</label>
            <a-input
              v-model:value="queryParams.keyword"
              placeholder="搜索商品名称"
              allow-clear
              size="large"
              @pressEnter="handleSearch"
            >
              <template #prefix>
                <SearchOutlined class="search-icon" />
              </template>
            </a-input>
          </div>
        </a-col>

        <a-col :xs="24" :sm="12" :md="8" :lg="4">
          <div class="filter-item">
            <label class="filter-label"><TagOutlined /> 商品分类</label>
            <a-select
              v-model:value="queryParams.categoryId"
              placeholder="全部分类"
              allow-clear
              style="width: 100%"
              size="large"
              @change="handleCategoryChange"
            >
              <a-select-option
                v-for="cat in categories"
                :key="cat.id"
                :value="cat.id"
              >
                {{ cat.name }}
              </a-select-option>
            </a-select>
          </div>
        </a-col>

        <a-col :xs="24" :sm="12" :md="8" :lg="4">
          <div class="filter-item">
            <label class="filter-label"><CheckCircleOutlined /> 审核状态</label>
            <a-select
              v-model:value="queryParams.status"
              placeholder="全部状态"
              allow-clear
              style="width: 100%"
              size="large"
              @change="handleStatusChange"
            >
              <a-select-option
                v-for="opt in statusOptions"
                :key="opt.value"
                :value="opt.value"
              >
                <a-badge
                  v-if="opt.value !== undefined"
                  :count="undefined"
                  :offset="[0, 0]"
                  :number-style="{ backgroundColor: '#52c41a' }"
                >
                  {{ opt.label }}
                </a-badge>
                <span v-else>{{ opt.label }}</span>
              </a-select-option>
            </a-select>
          </div>
        </a-col>

        <a-col :xs="24" :sm="24" :md="24" :lg="6">
          <div class="filter-actions">
            <a-space>
              <a-button @click="handleReset">
                <template #icon><FilterOutlined /></template>
                重置筛选
              </a-button>
              <a-tag v-if="queryParams.keyword || queryParams.status !== undefined || queryParams.categoryId !== undefined">
                共 {{ total }} 条
              </a-tag>
            </a-space>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 商品列表 -->
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
          showTotal: (total: number) => `共 ${total} 条`,
          pageSizeOptions: ['10', '20', '50', '100'],
          defaultPageSize: 10
        }"
        :scroll="{ x: 1400, y: 600 }"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'image'">
            <a-image
              v-if="getFirstImageUrl(record)"
              :src="getFirstImageUrl(record)"
              :alt="record.name"
              :width="60"
              :height="60"
              style="border-radius: 8px; object-fit: cover;"
            />
            <div v-else class="no-image">
              <ShoppingCartOutlined class="placeholder-icon" />
            </div>
          </template>

          <template v-if="column.key === 'price'">
            <span class="price-text">¥{{ record.price?.toFixed(2) || '0.00' }}</span>
          </template>

          <template v-if="column.key === 'stock'">
            <a-tag
              :color="record.stock <= 10 ? 'orange' : 'blue'"
              style="font-weight: 500;"
            >
              {{ record.stock }}
            </a-tag>
          </template>

          <template v-if="column.key === 'category'">
            <a-tag color="blue">
              {{ record.category || '-' }}
            </a-tag>
          </template>

          <template v-if="column.key === 'status'">
            <a-tag :color="statusMap[record.status]?.color || 'default'">
              <template #icon>
                <component :is="statusMap[record.status]?.icon" />
              </template>
              {{ statusMap[record.status]?.text || '未知' }}
            </a-tag>
          </template>

          <template v-if="column.key === 'active'">
            <a-switch
              :checked="record.active"
              :checked-children="'已上架'"
              :un-checked-children="'已下架'"
              @change="() => toggleActive(record)"
            />
          </template>

          <template v-if="column.key === 'action'">
            <a-space>
              <a-tooltip title="编辑商品">
                <a-button type="link" size="small" @click="handleEdit(record)">
                  <template #icon><EditOutlined /></template>
                  编辑
                </a-button>
              </a-tooltip>
              <a-popconfirm
                title="确定要删除这个商品吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="confirmDelete(record.id)"
              >
                <a-tooltip title="删除商品">
                  <a-button type="link" danger size="small">
                    <template #icon><DeleteOutlined /></template>
                    删除
                  </a-button>
                </a-tooltip>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>

      <!-- 空状态 -->
      <a-empty
        v-if="!loading && products.length === 0"
        description="暂无商品数据"
        :image="Empty.PRESENTED_IMAGE_SIMPLE"
      >
        <a-button type="primary" @click="handleAdd">
          <template #icon><PlusOutlined /></template>
          添加第一个商品
        </a-button>
      </a-empty>
    </a-card>

    <!-- 编辑/新增弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="editingProduct.id ? '编辑商品' : '新建商品'"
      width="700px"
      :confirm-loading="loading"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleSave"
      @cancel="() => { modalVisible = false; editingProduct = {}; imageFileList = [] }"
    >
      <a-form
        ref="formRef"
        :model="editingProduct"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 20 }"
        layout="horizontal"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="商品名称"
              name="name"
              :rules="[{ required: true, message: '请输入商品名称' }]"
            >
              <a-input
                v-model:value="editingProduct.name"
                placeholder="请输入商品名称"
                size="large"
              />
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item
              label="商品分类"
              name="categoryId"
              :rules="[{ required: true, message: '请选择商品分类' }]"
            >
              <a-select
                v-model:value="editingProduct.categoryId"
                placeholder="请选择商品分类"
                size="large"
              >
                <a-select-option
                  v-for="cat in categories"
                  :key="cat.id"
                  :value="cat.id"
                >
                  {{ cat.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="商品价格"
              name="price"
              :rules="[{ required: true, message: '请输入商品价格' }]"
            >
              <a-input-number
                v-model:value="editingProduct.price"
                :min="0"
                :precision="2"
                :formatter="(value: number) => `¥ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :parser="(value: string) => value!.replace(/¥\s?|(,*)/g, '')"
                style="width: 100%"
                size="large"
                placeholder="0.00"
              >
                <template #prefix>
                  <DollarOutlined />
                </template>
              </a-input-number>
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item
              label="商品库存"
              name="stock"
              :rules="[{ required: true, message: '请输入商品库存' }]"
            >
              <a-input-number
                v-model:value="editingProduct.stock"
                :min="0"
                style="width: 100%"
                size="large"
                placeholder="请输入商品库存"
              >
                <template #prefix>
                  <InboxOutlined />
                </template>
              </a-input-number>
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item
          label="商品描述"
          name="description"
        >
          <a-textarea
            v-model:value="editingProduct.description"
            :rows="4"
            placeholder="请输入商品描述"
            show-count
            :maxlength="500"
          />
        </a-form-item>

        <a-form-item
          label="商品图片"
          name="imageUrls"
        >
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

        <a-form-item label="商品状态" class="status-row">
          <a-space size="large">
            <a-switch v-model:checked="editingProduct.active" checked-children="上架" un-checked-children="下架" />
            <a-checkbox v-model:checked="editingProduct.isNew">新品</a-checkbox>
            <a-checkbox v-model:checked="editingProduct.isHot">热销</a-checkbox>
          </a-space>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 删除确认弹窗 -->
    <a-modal
      v-model:open="deleteModalVisible"
      title="确认删除"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleDelete"
      @cancel="() => { deleteModalVisible = false; productToDelete = null }"
    >
      <div class="confirm-content">
        <ExclamationCircleOutlined class="confirm-icon" style="font-size: 48px; color: #fa8c16;" />
        <div class="confirm-text">
          <p class="confirm-title">确定要删除这个商品吗？</p>
          <p class="confirm-desc">此操作不可恢复，请谨慎操作。</p>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<style scoped>
.admin-products-page {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-left {
  flex: 1;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
}

.highlight {
  color: #10b981;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.filter-item {
  margin-bottom: 0;
}

.filter-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #595959;
}

.filter-label :deep(.anticon) {
  margin-right: 6px;
  color: #9ca3af;
}

.search-icon {
  color: #9ca3af;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 100%;
}

/* 表格卡片 */
.table-card {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 表格样式优化 */
:deep(.ant-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  color: #262626;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: #f9fafb;
}

.no-image {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #e5e7eb 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
}

.placeholder-icon {
  color: #b0b8c5;
  font-size: 24px;
}

.price-text {
  font-weight: 600;
  color: #10b981;
  font-size: 16px;
}

/* 删除确认 */
.confirm-content {
  display: flex;
  gap: 16px;
  padding: 16px 0;
}

.confirm-icon {
  flex-shrink: 0;
}

.confirm-text {
  flex: 1;
}

.confirm-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.confirm-desc {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
}

/* 响应式 */
@media (max-width: 768px) {
  .admin-products-page {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
