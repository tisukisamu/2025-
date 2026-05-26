<template>
  <div class="publish-page">
    <div class="back-nav">
      <a-button type="text" @click="router.back()">
        <left-outlined /> 返回
      </a-button>
    </div>
    <a-card :title="isEdit ? '编辑商品' : '发布商品'" class="publish-card">
      <a-spin :spinning="fetching">
        <a-form
          :model="formState"
          :rules="rules"
          @finish="handleSubmit"
          layout="vertical"
          class="publish-form"
        >
          <a-form-item name="title" label="商品标题">
            <a-input
              v-model:value="formState.title"
              placeholder="请输入商品标题"
              maxlength="100"
              show-count
            />
          </a-form-item>

          <a-form-item name="categoryId" label="商品分类">
            <a-select
              v-model:value="formState.categoryId"
              placeholder="请选择分类"
            >
              <a-select-option v-for="cat in categories" :key="cat.id" :value="cat.id">
                {{ cat.name }}
              </a-select-option>
            </a-select>
          </a-form-item>

          <a-row :gutter="16">
            <a-col :xs="24" :sm="12">
              <a-form-item name="price" label="出售价格">
                <a-input-number
                  v-model:value="formState.price"
                  placeholder="请输入价格"
                  :min="0.01"
                  :precision="2"
                  style="width: 100%"
                  addon-before="¥"
                />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12">
              <a-form-item name="originalPrice" label="原价（选填）">
                <a-input-number
                  v-model:value="formState.originalPrice"
                  placeholder="请输入原价"
                  :min="0"
                  :precision="2"
                  style="width: 100%"
                  addon-before="¥"
                />
              </a-form-item>
            </a-col>
          </a-row>

          <a-form-item name="tradeType" label="交易方式">
            <a-radio-group v-model:value="formState.tradeType">
              <a-radio value="FACE_TO_FACE">当面交易</a-radio>
              <a-radio value="EXPRESS">快递邮寄</a-radio>
              <a-radio value="BOTH">都可以</a-radio>
            </a-radio-group>
          </a-form-item>

          <a-form-item name="description" label="商品描述">
            <a-textarea
              v-model:value="formState.description"
              placeholder="请详细描述商品的新旧程度、使用情况、配件等信息"
              :rows="6"
              maxlength="2000"
              show-count
            />
          </a-form-item>

          <a-form-item name="imageUrls" label="商品图片">
            <a-upload
              v-model:file-list="fileList"
              list-type="picture-card"
              :customRequest="handleUpload"
              :beforeUpload="beforeUpload"
              @preview="handlePreview"
              @remove="handleRemove"
              accept="image/*"
              :max-count="9"
            >
              <div v-if="fileList.length < 9">
                <plus-outlined />
                <div style="margin-top: 8px">上传图片</div>
              </div>
            </a-upload>
            <div class="upload-tip">最多上传9张图片，建议尺寸800x800，支持jpg、png格式</div>
          </a-form-item>

          <a-form-item>
            <a-space>
              <a-button type="primary" html-type="submit" :loading="loading">
                {{ isEdit ? '保存修改' : '发布商品' }}
              </a-button>
              <a-button v-if="!isEdit" @click="handleSaveDraft" :loading="draftLoading">
                保存草稿
              </a-button>
              <a-button @click="router.back()">
                取消
              </a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </a-spin>
    </a-card>

    <a-modal
      :open="previewVisible"
      :footer="null"
      @cancel="previewVisible = false"
    >
      <img :src="previewImage" style="width: 100%" />
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { productApi, categoryApi, uploadApi } from '@/api'
import type { Category } from '@/types'
import type { UploadFile, UploadRequestOption } from 'ant-design-vue'
import type { Rule } from 'ant-design-vue/es/form'
import { message } from 'ant-design-vue'
import { PlusOutlined, LeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const draftLoading = ref(false)
const fetching = ref(false)
const categories = ref<Category[]>([])
const fileList = ref<UploadFile[]>([])
const previewVisible = ref(false)
const previewImage = ref('')

const editId = computed(() => route.query.id ? Number(route.query.id) : null)
const isEdit = computed(() => !!editId.value)

const formState = reactive({
  title: '',
  categoryId: undefined as number | undefined,
  price: undefined as number | undefined,
  originalPrice: undefined as number | undefined,
  tradeType: 'BOTH' as 'FACE_TO_FACE' | 'EXPRESS' | 'BOTH',
  description: '',
  imageUrls: [] as string[]
})

const rules: Record<string, Rule[]> = {
  title: [
    { required: true, message: '请输入商品标题' },
    { max: 100, message: '标题不能超过100个字符' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类' }
  ],
  price: [
    { required: true, message: '请输入出售价格' }
  ],
  tradeType: [
    { required: true, message: '请选择交易方式' }
  ]
}

const fetchCategories = async () => {
  try {
    const res = await categoryApi.getAllCategories()
    categories.value = res.data
  } catch {
    // ignore
  }
}

const fetchProduct = async () => {
  if (!editId.value) return
  fetching.value = true
  try {
    const res = await productApi.getProductDetail(editId.value)
    const p = res.data
    formState.title = p.title
    formState.categoryId = p.categoryId
    formState.price = p.price
    formState.originalPrice = p.originalPrice
    formState.tradeType = p.tradeType
    formState.description = p.description

    if (p.imageUrls && p.imageUrls.length > 0) {
      formState.imageUrls = [...p.imageUrls]
      fileList.value = p.imageUrls.map((url: string, index: number) => ({
        uid: String(-index - 1),
        name: `image-${index}`,
        status: 'done' as const,
        url: url
      }))
    }
  } catch {
    message.error('加载商品信息失败')
  } finally {
    fetching.value = false
  }
}

const beforeUpload = (file: File) => {
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

const handleUpload = async (options: UploadRequestOption) => {
  const { file, onSuccess, onError } = options
  try {
    const res = await uploadApi.uploadFile(file as File)
    const url = res.data
    formState.imageUrls.push(url)
    onSuccess?.({ url })
  } catch (error) {
    onError?.(error as Error)
  }
}

const handleRemove = (file: UploadFile) => {
  const index = fileList.value.indexOf(file)
  if (index > -1) {
    formState.imageUrls.splice(index, 1)
  }
}

const handlePreview = async (file: UploadFile) => {
  let preview = file.url || file.thumbUrl || ''
  if (!preview && file.response?.url) {
    preview = file.response.url
  }
  previewImage.value = preview
  previewVisible.value = true
}

const handleSubmit = async () => {
  loading.value = true
  try {
    const data = {
      title: formState.title,
      categoryId: formState.categoryId,
      price: formState.price!,
      originalPrice: formState.originalPrice,
      tradeType: formState.tradeType,
      description: formState.description,
      imageUrls: formState.imageUrls
    }
    if (isEdit.value) {
      await productApi.updateProduct(editId.value!, data)
      message.success('商品修改成功')
    } else {
      await productApi.createProduct(data)
      message.success('商品发布成功，等待审核')
    }
    router.push('/products/mine')
  } finally {
    loading.value = false
  }
}

const handleSaveDraft = async () => {
  draftLoading.value = true
  try {
    await productApi.createProduct({
      title: formState.title || '草稿',
      categoryId: formState.categoryId,
      price: formState.price || 0,
      originalPrice: formState.originalPrice,
      tradeType: formState.tradeType,
      description: formState.description,
      imageUrls: formState.imageUrls
    })
    message.success('草稿保存成功')
    router.push('/products/mine')
  } finally {
    draftLoading.value = false
  }
}

onMounted(() => {
  fetchCategories()
  if (isEdit.value) {
    fetchProduct()
  }
})
</script>

<style scoped>
.publish-page {
  max-width: 800px;
  margin: 0 auto;
}

.back-nav {
  margin-bottom: 16px;
}

.back-nav :deep(.ant-btn) {
  color: #1890ff;
  padding: 4px 8px;
}

.back-nav :deep(.ant-btn:hover) {
  background: #e6f7ff;
}

.publish-card {
  border-radius: 12px;
}

.publish-form {
  padding: 24px 0;
}

.upload-tip {
  color: #999;
  font-size: 12px;
  margin-top: 8px;
}

:deep(.ant-upload-picture-card-wrapper) {
  width: auto;
}

:deep(.ant-upload-select-picture-card) {
  width: 100px;
  height: 100px;
}

@media (max-width: 768px) {
  .publish-form {
    padding: 16px 0;
  }
}
</style>
