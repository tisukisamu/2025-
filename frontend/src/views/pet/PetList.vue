<template>
  <div class="pet-page">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">我的宠物</h1>
        <p class="page-subtitle">管理您的宠物信息</p>
      </div>
      <div class="header-right">
        <a-button type="primary" @click="showModal()">
          <template #icon><plus-outlined /></template>
          添加宠物
        </a-button>
      </div>
    </div>

    <div class="pet-grid" v-if="pets.length > 0">
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :sm="12" :lg="8" :xl="6" v-for="pet in pets" :key="pet.id">
          <div class="pet-card" @click="showPetDetail(pet)">
            <div class="pet-avatar">
              <img v-if="pet.photo" :src="getImageUrl(pet.photo)" :alt="pet.name" />
              <div v-else class="avatar-placeholder">
                <bug-outlined />
              </div>
            </div>
            <div class="pet-info">
              <h3 class="pet-name">{{ pet.name }}</h3>
              <div class="pet-meta">
                <span class="pet-type">{{ getPetTypeLabel(pet.type) }}</span>
                <span v-if="pet.breed" class="pet-breed">{{ pet.breed }}</span>
              </div>
              <div class="pet-detail" v-if="pet.birthday">
                <calendar-outlined />
                <span>{{ pet.birthday }}</span>
              </div>
              <div class="pet-detail" v-if="pet.passDate">
                <heart-outlined />
                <span>离开时间：{{ pet.passDate }}</span>
              </div>
            </div>
            <div class="pet-actions" @click.stop>
              <a-button type="link" size="small" @click="showModal(pet)">编辑</a-button>
              <a-divider type="vertical" />
              <a-button type="link" size="small" @click="viewMemorial(pet)">纪念</a-button>
              <a-divider type="vertical" />
              <a-popconfirm
                title="确定要删除这个宠物信息吗？"
                @confirm="handleDelete(pet.id)"
              >
                <a-button type="link" size="small" danger>删除</a-button>
              </a-popconfirm>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">
        <inbox-outlined />
      </div>
      <p class="empty-text">还没有添加宠物信息</p>
      <a-button type="primary" @click="showModal()">
        <template #icon><plus-outlined /></template>
        添加第一个宠物
      </a-button>
    </div>

    <a-modal
      v-model:open="modalVisible"
      :title="editingPet ? '编辑宠物' : '添加宠物'"
      @ok="handleSubmit"
      @cancel="handleCancel"
      width="600px"
      :confirmLoading="loading"
    >
      <a-form
        ref="formRef"
        :model="formState"
        :rules="rules"
        layout="vertical"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="宠物名称" name="name">
              <a-input v-model:value="formState.name" placeholder="请输入宠物名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="宠物类型" name="type">
              <a-select v-model:value="formState.type" placeholder="请选择宠物类型">
                <a-select-option value="dog">狗</a-select-option>
                <a-select-option value="cat">猫</a-select-option>
                <a-select-option value="bird">鸟类</a-select-option>
                <a-select-option value="fish">鱼类</a-select-option>
                <a-select-option value="other">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="品种" name="breed">
              <a-input v-model:value="formState.breed" placeholder="请输入品种" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="性别" name="gender">
              <a-select v-model:value="formState.gender" placeholder="请选择性别">
                <a-select-option value="male">雄性</a-select-option>
                <a-select-option value="female">雌性</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="出生日期" name="birthday">
              <a-date-picker 
                v-model:value="formState.birthday" 
                style="width: 100%"
                placeholder="请选择出生日期"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="离开日期" name="passDate">
              <a-date-picker 
                v-model:value="formState.passDate" 
                style="width: 100%"
                placeholder="请选择离开日期"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="毛色" name="color">
          <a-input v-model:value="formState.color" placeholder="请输入毛色" />
        </a-form-item>

        <a-form-item label="体重(kg)" name="weight">
          <a-input-number 
            v-model:value="formState.weight" 
            :min="0" 
            :precision="2"
            style="width: 100%"
            placeholder="请输入体重"
          />
        </a-form-item>

        <a-form-item label="宠物照片" name="photo">
          <a-upload
            list-type="picture-card"
            :show-upload-list="false"
            :before-upload="beforeUpload"
            @change="handleUpload"
          >
            <img v-if="formState.photo" :src="getImageUrl(formState.photo)" alt="avatar" style="width: 100%" />
            <div v-else>
              <plus-outlined />
              <div style="margin-top: 8px">上传照片</div>
            </div>
          </a-upload>
        </a-form-item>

        <a-form-item label="描述" name="description">
          <a-textarea 
            v-model:value="formState.description" 
            :rows="4"
            placeholder="请输入宠物描述"
          />
        </a-form-item>

        <a-form-item label="纪念文字" name="memorialText">
          <a-textarea 
            v-model:value="formState.memorialText" 
            :rows="4"
            placeholder="写下您想对它说的话..."
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailVisible"
      :title="currentPet?.name"
      :footer="null"
      width="500px"
    >
      <div class="pet-detail-content" v-if="currentPet">
        <div class="detail-avatar">
          <img v-if="currentPet.photo" :src="getImageUrl(currentPet.photo)" :alt="currentPet.name" />
          <div v-else class="detail-avatar-placeholder">
            <bug-outlined />
          </div>
        </div>
        <div class="detail-info">
          <div class="detail-row">
            <span class="detail-label">类型</span>
            <span class="detail-value">{{ getPetTypeLabel(currentPet.type) }}</span>
          </div>
          <div class="detail-row" v-if="currentPet.breed">
            <span class="detail-label">品种</span>
            <span class="detail-value">{{ currentPet.breed }}</span>
          </div>
          <div class="detail-row" v-if="currentPet.gender">
            <span class="detail-label">性别</span>
            <span class="detail-value">{{ currentPet.gender === 'male' ? '雄性' : '雌性' }}</span>
          </div>
          <div class="detail-row" v-if="currentPet.birthday">
            <span class="detail-label">出生日期</span>
            <span class="detail-value">{{ currentPet.birthday }}</span>
          </div>
          <div class="detail-row" v-if="currentPet.passDate">
            <span class="detail-label">离开日期</span>
            <span class="detail-value">{{ currentPet.passDate }}</span>
          </div>
          <div class="detail-row" v-if="currentPet.color">
            <span class="detail-label">毛色</span>
            <span class="detail-value">{{ currentPet.color }}</span>
          </div>
          <div class="detail-row" v-if="currentPet.weight">
            <span class="detail-label">体重</span>
            <span class="detail-value">{{ currentPet.weight }} kg</span>
          </div>
          <div class="detail-row" v-if="currentPet.description">
            <span class="detail-label">描述</span>
            <span class="detail-value">{{ currentPet.description }}</span>
          </div>
          <div class="detail-row" v-if="currentPet.memorialText">
            <span class="detail-label">纪念文字</span>
            <span class="detail-value memorial-text">{{ currentPet.memorialText }}</span>
          </div>
        </div>
        <div class="detail-actions">
          <a-button @click="detailVisible = false">关闭</a-button>
          <a-button type="primary" @click="showModal(currentPet); detailVisible = false">编辑</a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  PlusOutlined, 
  BugOutlined, 
  CalendarOutlined,
  HeartOutlined,
  InboxOutlined 
} from '@ant-design/icons-vue'
import { getPetList, createPet, updatePet, deletePet } from '../../api/pet'
import { uploadFile } from '../../api/file'
import { getImageUrl } from '../../utils'
import dayjs from 'dayjs'

const router = useRouter()
const formRef = ref()
const modalVisible = ref(false)
const detailVisible = ref(false)
const loading = ref(false)
const editingPet = ref(null)
const currentPet = ref(null)
const pets = ref([])

const formState = reactive({
  name: '',
  type: undefined,
  breed: '',
  gender: undefined,
  birthday: null,
  passDate: null,
  color: '',
  weight: null,
  photo: '',
  description: '',
  memorialText: ''
})

const rules = {
  name: [{ required: true, message: '请输入宠物名称' }],
  type: [{ required: true, message: '请选择宠物类型' }]
}

const petTypeLabels = {
  dog: '狗',
  cat: '猫',
  bird: '鸟类',
  fish: '鱼类',
  other: '其他'
}

const getPetTypeLabel = (type) => {
  return petTypeLabels[type] || type
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

const showPetDetail = (pet) => {
  currentPet.value = pet
  detailVisible.value = true
}

const showModal = (pet = null) => {
  editingPet.value = pet
  if (pet) {
    Object.assign(formState, {
      name: pet.name,
      type: pet.type,
      breed: pet.breed || '',
      gender: pet.gender,
      birthday: pet.birthday ? dayjs(pet.birthday) : null,
      passDate: pet.passDate ? dayjs(pet.passDate) : null,
      color: pet.color || '',
      weight: pet.weight,
      photo: pet.photo || '',
      description: pet.description || '',
      memorialText: pet.memorialText || ''
    })
  } else {
    Object.assign(formState, {
      name: '',
      type: undefined,
      breed: '',
      gender: undefined,
      birthday: null,
      passDate: null,
      color: '',
      weight: null,
      photo: '',
      description: '',
      memorialText: ''
    })
  }
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    const data = {
      ...formState,
      birthday: formState.birthday ? formState.birthday.format('YYYY-MM-DD') : null,
      passDate: formState.passDate ? formState.passDate.format('YYYY-MM-DD') : null
    }

    if (editingPet.value) {
      await updatePet(editingPet.value.id, data)
      message.success('更新成功')
    } else {
      await createPet(data)
      message.success('添加成功')
    }

    modalVisible.value = false
    loadPets()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  modalVisible.value = false
  formRef.value?.resetFields()
}

const handleDelete = async (id) => {
  try {
    await deletePet(id)
    message.success('删除成功')
    loadPets()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

const viewMemorial = (pet) => {
  router.push(`/memorials?petId=${pet.id}`)
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
  if (rawFile) {
    try {
      const res = await uploadFile(rawFile, 'pets')
      if (res.code === 200) {
        formState.photo = res.data.url
        message.success('上传成功')
      }
    } catch (error) {
      message.error('上传失败')
    }
  }
}

onMounted(() => {
  loadPets()
})
</script>

<style scoped>
.pet-page {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  min-height: calc(100vh - 200px);
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

.pet-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.pet-card:hover {
  border-color: #d9d9d9;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.pet-avatar {
  width: 100%;
  height: 200px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.pet-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  font-size: 64px;
  color: #d9d9d9;
}

.pet-info {
  padding: 16px;
}

.pet-name {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 8px;
}

.pet-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.pet-type,
.pet-breed {
  font-size: 12px;
  padding: 2px 8px;
  background: #f5f5f5;
  border-radius: 4px;
  color: #595959;
}

.pet-detail {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #8c8c8c;
  margin-top: 8px;
}

.pet-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
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

.pet-detail-content {
  padding: 0;
}

.detail-avatar {
  width: 100%;
  height: 250px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 20px;
  background: #f5f5f5;
}

.detail-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 80px;
  color: #d9d9d9;
}

.detail-info {
  margin-bottom: 20px;
}

.detail-row {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-label {
  width: 80px;
  flex-shrink: 0;
  color: #8c8c8c;
  font-size: 14px;
}

.detail-value {
  flex: 1;
  color: #262626;
  font-size: 14px;
}

.memorial-text {
  white-space: pre-wrap;
  line-height: 1.6;
}

.detail-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}
</style>
