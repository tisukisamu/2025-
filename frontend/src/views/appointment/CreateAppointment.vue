<template>
  <div class="create-appointment-page">
    <div class="page-header">
      <a-page-header
        title="创建预约"
        sub-title="为您的爱宠预约服务"
        @back="$router.back()"
      />
    </div>

    <div class="form-container">
      <a-steps :current="currentStep" class="steps-nav">
        <a-step title="选择宠物" />
        <a-step title="选择套餐" />
        <a-step title="填写信息" />
        <a-step title="确认预约" />
      </a-steps>

      <div class="step-content">
        <div v-if="currentStep === 0" class="step-panel">
          <h3 class="step-title">选择宠物</h3>
          <div class="pet-grid">
            <div
              v-for="pet in pets"
              :key="pet.id"
              class="pet-card"
              :class="{ 'pet-card-selected': selectedPet?.id === pet.id }"
              @click="selectPet(pet)"
            >
              <div class="pet-avatar">
                <img v-if="pet.photo" :src="getImageUrl(pet.photo)" :alt="pet.name" />
                <div v-else class="avatar-placeholder">
                  <bug-outlined />
                </div>
              </div>
              <div class="pet-info">
                <h4 class="pet-name">{{ pet.name }}</h4>
                <p class="pet-type">{{ getPetTypeLabel(pet.type) }}</p>
              </div>
              <check-circle-filled v-if="selectedPet?.id === pet.id" class="check-icon" />
            </div>
          </div>
          <div class="empty-state" v-if="pets.length === 0">
            <p>还没有添加宠物信息</p>
            <a-button type="primary" @click="$router.push('/pets')">添加宠物</a-button>
          </div>
        </div>

        <div v-if="currentStep === 1" class="step-panel">
          <h3 class="step-title">选择服务套餐</h3>
          <div class="package-grid">
            <div
              v-for="pkg in packages"
              :key="pkg.id"
              class="package-card"
              :class="{ 'package-card-selected': selectedPackage?.id === pkg.id }"
              @click="selectPackage(pkg)"
            >
              <div class="package-header">
                <h4 class="package-name">{{ pkg.name }}</h4>
                <div class="package-price">¥{{ pkg.price }}</div>
              </div>
              <p class="package-desc">{{ pkg.description }}</p>
              <check-circle-filled v-if="selectedPackage?.id === pkg.id" class="check-icon" />
            </div>
          </div>
        </div>

        <div v-if="currentStep === 2" class="step-panel">
          <h3 class="step-title">填写预约信息</h3>
          <a-form
            ref="formRef"
            :model="formState"
            :rules="rules"
            layout="vertical"
          >
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="预约时间" name="appointmentTime">
                  <a-date-picker
                    v-model:value="formState.appointmentTime"
                    show-time
                    format="YYYY-MM-DD HH:mm"
                    style="width: 100%"
                    placeholder="请选择预约时间"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="联系人" name="contactName">
                  <a-input v-model:value="formState.contactName" placeholder="请输入联系人姓名" />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="联系电话" name="contactPhone">
                  <a-input v-model:value="formState.contactPhone" placeholder="请输入联系电话" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="服务地址" name="address">
                  <a-input v-model:value="formState.address" placeholder="请输入服务地址" />
                </a-form-item>
              </a-col>
            </a-row>
            <a-form-item label="备注" name="remark">
              <a-textarea v-model:value="formState.remark" :rows="4" placeholder="请输入备注信息" />
            </a-form-item>
          </a-form>
        </div>

        <div v-if="currentStep === 3" class="step-panel">
          <h3 class="step-title">确认预约信息</h3>
          <div class="confirm-info">
            <div class="info-section">
              <h4 class="section-title">宠物信息</h4>
              <div class="info-item">
                <span class="label">宠物名称</span>
                <span class="value">{{ selectedPet?.name }}</span>
              </div>
              <div class="info-item">
                <span class="label">宠物类型</span>
                <span class="value">{{ getPetTypeLabel(selectedPet?.type) }}</span>
              </div>
            </div>
            <div class="info-section">
              <h4 class="section-title">服务信息</h4>
              <div class="info-item">
                <span class="label">服务套餐</span>
                <span class="value">{{ selectedPackage?.name }}</span>
              </div>
              <div class="info-item">
                <span class="label">服务费用</span>
                <span class="value price">¥{{ selectedPackage?.price }}</span>
              </div>
            </div>
            <div class="info-section">
              <h4 class="section-title">联系信息</h4>
              <div class="info-item">
                <span class="label">预约时间</span>
                <span class="value">{{ formatDateTime(formState.appointmentTime) }}</span>
              </div>
              <div class="info-item">
                <span class="label">联系人</span>
                <span class="value">{{ formState.contactName }}</span>
              </div>
              <div class="info-item">
                <span class="label">联系电话</span>
                <span class="value">{{ formState.contactPhone }}</span>
              </div>
              <div class="info-item" v-if="formState.address">
                <span class="label">服务地址</span>
                <span class="value">{{ formState.address }}</span>
              </div>
              <div class="info-item" v-if="formState.remark">
                <span class="label">备注</span>
                <span class="value">{{ formState.remark }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="step-actions">
        <a-button v-if="currentStep > 0" @click="prevStep">上一步</a-button>
        <a-button
          v-if="currentStep < 3"
          type="primary"
          :disabled="!canNextStep"
          @click="nextStep"
        >
          下一步
        </a-button>
        <a-button
          v-if="currentStep === 3"
          type="primary"
          :loading="loading"
          @click="handleSubmit"
        >
          确认预约
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { BugOutlined, CheckCircleFilled } from '@ant-design/icons-vue'
import { getPetList } from '../../api/pet'
import { getServiceList } from '../../api/service'
import { createAppointment } from '../../api/appointment'
import { getImageUrl } from '../../utils'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const currentStep = ref(0)
const loading = ref(false)
const pets = ref([])
const packages = ref([])
const selectedPet = ref(null)
const selectedPackage = ref(null)

const formState = reactive({
  appointmentTime: null,
  contactName: '',
  contactPhone: '',
  address: '',
  remark: ''
})

const rules = {
  appointmentTime: [{ required: true, message: '请选择预约时间' }],
  contactName: [{ required: true, message: '请输入联系人姓名' }],
  contactPhone: [{ required: true, message: '请输入联系电话' }]
}

const petTypeLabels = {
  dog: '狗',
  cat: '猫',
  bird: '鸟类',
  fish: '鱼类',
  other: '其他'
}

const getPetTypeLabel = (type) => petTypeLabels[type] || type
const formatDateTime = (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : ''

const canNextStep = computed(() => {
  if (currentStep.value === 0) return !!selectedPet.value
  if (currentStep.value === 1) return !!selectedPackage.value
  if (currentStep.value === 2) {
    return formState.appointmentTime && formState.contactName && formState.contactPhone
  }
  return true
})

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

const loadPackages = async () => {
  try {
    const res = await getServiceList()
    if (res.code === 200) {
      packages.value = res.data || []
    }
  } catch (error) {
    console.error('加载服务套餐失败:', error)
  }
}

const selectPet = (pet) => {
  selectedPet.value = pet
}

const selectPackage = (pkg) => {
  selectedPackage.value = pkg
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const nextStep = async () => {
  if (currentStep.value === 2) {
    try {
      await formRef.value?.validate()
    } catch {
      return
    }
  }
  if (currentStep.value < 3) {
    currentStep.value++
  }
}

const handleSubmit = async () => {
  loading.value = true
  try {
    const res = await createAppointment({
      petId: selectedPet.value.id,
      packageId: selectedPackage.value.id,
      appointmentTime: formState.appointmentTime.format('YYYY-MM-DD HH:mm:ss'),
      contactName: formState.contactName,
      contactPhone: formState.contactPhone,
      address: formState.address,
      remark: formState.remark
    })
    
    if (res.code === 200) {
      message.success('预约成功')
      router.push('/appointments')
    }
  } catch (error) {
    console.error('预约失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadPets()
  loadPackages()
  
  const packageId = route.query.packageId
  if (packageId) {
    loadPackages().then(() => {
      selectedPackage.value = packages.value.find(p => p.id === Number(packageId))
      if (selectedPackage.value) {
        currentStep.value = 0
      }
    })
  }
})
</script>

<style scoped>
.create-appointment-page {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
}

.page-header {
  margin-bottom: 24px;
}

.form-container {
  max-width: 900px;
  margin: 0 auto;
}

.steps-nav {
  margin-bottom: 32px;
}

.step-panel {
  min-height: 400px;
}

.step-title {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 24px;
}

.pet-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.pet-card {
  position: relative;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.pet-card:hover {
  border-color: #d9d9d9;
}

.pet-card-selected {
  border-color: #262626;
  background: #fafafa;
}

.pet-avatar {
  width: 80px;
  height: 80px;
  margin: 0 auto 12px;
  border-radius: 50%;
  overflow: hidden;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pet-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  font-size: 40px;
  color: #d9d9d9;
}

.pet-name {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 4px;
  text-align: center;
}

.pet-type {
  font-size: 13px;
  color: #8c8c8c;
  margin: 0;
  text-align: center;
}

.check-icon {
  position: absolute;
  top: 12px;
  right: 12px;
  font-size: 20px;
  color: #262626;
}

.package-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.package-card {
  position: relative;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.package-card:hover {
  border-color: #d9d9d9;
}

.package-card-selected {
  border-color: #262626;
  background: #fafafa;
}

.package-name {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 8px;
}

.package-price {
  font-size: 24px;
  font-weight: 700;
  color: #262626;
}

.package-desc {
  font-size: 13px;
  color: #8c8c8c;
  margin: 12px 0 0;
}

.confirm-info {
  background: #fafafa;
  border-radius: 12px;
  padding: 24px;
}

.info-section {
  margin-bottom: 24px;
}

.info-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e8e8e8;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.info-item .label {
  color: #8c8c8c;
  font-size: 14px;
}

.info-item .value {
  color: #262626;
  font-size: 14px;
  font-weight: 500;
}

.info-item .price {
  font-size: 18px;
  font-weight: 700;
}

.step-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.empty-state {
  text-align: center;
  padding: 48px;
  color: #8c8c8c;
}
</style>
