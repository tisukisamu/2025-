<template>
  <div class="memorial-page">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">纪念相册</h1>
        <p class="page-subtitle">永恒的记忆，永远的爱</p>
      </div>
      <div class="header-right" v-if="userStore.isLoggedIn">
        <a-button type="primary" @click="showCreateModal">
          <plus-outlined />
          创建相册
        </a-button>
      </div>
    </div>

    <div class="memorial-grid" v-if="memorials.length > 0">
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :sm="12" :lg="8" :xl="6" v-for="memorial in memorials" :key="memorial.id">
          <div class="memorial-card" @click="viewMemorial(memorial)">
            <div class="memorial-cover">
              <img v-if="memorial.photos && memorial.photos.length > 0" :src="getImageUrl(memorial.photos[0])" :alt="memorial.title" />
              <div v-else class="cover-placeholder">
                <picture-outlined />
              </div>
              <div class="photo-count" v-if="memorial.photos && memorial.photos.length > 1">
                <picture-outlined />
                {{ memorial.photos.length }}
              </div>
            </div>
            <div class="memorial-info">
              <h3 class="memorial-title">{{ memorial.title || memorial.petName }}</h3>
              <p class="memorial-desc" v-if="memorial.description">{{ memorial.description }}</p>
              <div class="memorial-meta">
                <span class="pet-name" v-if="memorial.petName">
                  <heart-outlined />
                  {{ memorial.petName }}
                </span>
                <span class="view-count">
                  <eye-outlined />
                  {{ memorial.viewCount }}
                </span>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">
        <picture-outlined />
      </div>
      <p class="empty-text">暂无纪念相册</p>
      <a-button type="primary" v-if="userStore.isLoggedIn" @click="showCreateModal">
        创建第一个相册
      </a-button>
    </div>

    <div class="pagination-wrapper" v-if="total > pageSize">
      <a-pagination
        v-model:current="pageNum"
        :total="total"
        :pageSize="pageSize"
        show-less-items
        @change="handlePageChange"
      />
    </div>

    <a-modal
      v-model:open="createModalVisible"
      title="创建纪念相册"
      @ok="handleCreate"
      width="700px"
      :confirmLoading="loading"
    >
      <a-form :model="formState" layout="vertical">
        <a-form-item label="选择宠物" name="petId" :rules="[{ required: true, message: '请选择宠物' }]">
          <a-select v-model:value="formState.petId" placeholder="请选择宠物">
            <a-select-option v-for="pet in pets" :key="pet.id" :value="pet.id">
              {{ pet.name }} ({{ getPetTypeLabel(pet.type) }})
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="相册标题" name="title">
          <a-input v-model:value="formState.title" placeholder="请输入相册标题" />
        </a-form-item>

        <a-form-item label="相册描述" name="description">
          <a-textarea v-model:value="formState.description" :rows="3" placeholder="写下您想记录的故事..." />
        </a-form-item>

        <a-form-item label="上传照片" name="photos">
          <a-upload
            list-type="picture-card"
            v-model:fileList="fileList"
            :before-upload="beforeUpload"
            @preview="handlePreview"
            multiple
          >
            <div v-if="fileList.length < 9">
              <plus-outlined />
              <div style="margin-top: 8px">上传</div>
            </div>
          </a-upload>
        </a-form-item>

        <a-form-item label="公开设置" name="isPublic">
          <a-switch v-model:checked="formState.isPublic" checked-children="公开" un-checked-children="私密" />
          <span class="form-tip">公开后所有人都可以看到这个相册</span>
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal :open="previewVisible" :footer="null" @cancel="previewVisible = false">
      <img alt="preview" style="width: 100%" :src="previewImage" />
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { message } from 'ant-design-vue'
import { 
  PlusOutlined, 
  PictureOutlined, 
  HeartOutlined,
  EyeOutlined 
} from '@ant-design/icons-vue'
import { getPublicMemorials, createMemorial } from '../../api/memorial'
import { getPetList } from '../../api/pet'
import { uploadFile } from '../../api/file'
import { getImageUrl } from '../../utils'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const memorials = ref([])
const pets = ref([])
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const createModalVisible = ref(false)
const loading = ref(false)
const fileList = ref([])
const previewVisible = ref(false)
const previewImage = ref('')

const formState = reactive({
  petId: undefined,
  title: '',
  description: '',
  photos: [],
  isPublic: true
})

const petTypeLabels = {
  dog: '狗',
  cat: '猫',
  bird: '鸟类',
  fish: '鱼类',
  other: '其他'
}

const getPetTypeLabel = (type) => petTypeLabels[type] || type

const loadMemorials = async () => {
  try {
    const res = await getPublicMemorials({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    if (res.code === 200) {
      memorials.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载纪念相册失败:', error)
  }
}

const loadPets = async () => {
  try {
    const res = await getPetList()
    if (res.code === 200) {
      pets.value = res.data || []
    }
  } catch (error) {
    console.error('加载宠物列表失败:', error)
  }
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadMemorials()
}

const showCreateModal = () => {
  loadPets()
  formState.petId = undefined
  formState.title = ''
  formState.description = ''
  formState.photos = []
  formState.isPublic = true
  fileList.value = []
  createModalVisible.value = true
}

const beforeUpload = async (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片')
    return false
  }
  
  try {
    const res = await uploadFile(file, 'memorials', null)
    if (res.code === 200) {
      formState.photos.push(res.data.url)
    }
  } catch (error) {
    message.error('上传失败')
  }
  
  return false
}

const handlePreview = (file) => {
  previewImage.value = file.url || file.thumbUrl
  previewVisible.value = true
}

const handleCreate = async () => {
  if (!formState.petId) {
    message.warning('请选择宠物')
    return
  }
  
  loading.value = true
  try {
    await createMemorial({
      ...formState,
      isPublic: formState.isPublic ? 1 : 0
    })
    message.success('创建成功')
    createModalVisible.value = false
    loadMemorials()
  } catch (error) {
    console.error('创建失败:', error)
  } finally {
    loading.value = false
  }
}

const viewMemorial = (memorial) => {
  router.push(`/memorials/${memorial.id}`)
}

onMounted(() => {
  loadMemorials()
  
  const petId = route.query.petId
  if (petId) {
    showCreateModal()
    formState.petId = Number(petId)
  }
})
</script>

<style scoped>
.memorial-page {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
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

.memorial-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.memorial-card:hover {
  border-color: #d9d9d9;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  transform: translateY(-4px);
}

.memorial-cover {
  position: relative;
  width: 100%;
  height: 200px;
  background: #f5f5f5;
  overflow: hidden;
}

.memorial-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.memorial-card:hover .memorial-cover img {
  transform: scale(1.05);
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  color: #d9d9d9;
}

.photo-count {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.memorial-info {
  padding: 16px;
}

.memorial-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.memorial-desc {
  font-size: 13px;
  color: #8c8c8c;
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.memorial-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #8c8c8c;
}

.pet-name {
  display: flex;
  align-items: center;
  gap: 4px;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.empty-state {
  text-align: center;
  padding: 80px 24px;
}

.empty-icon {
  font-size: 64px;
  color: #d9d9d9;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: #8c8c8c;
  margin-bottom: 24px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.form-tip {
  margin-left: 12px;
  font-size: 12px;
  color: #8c8c8c;
}
</style>
