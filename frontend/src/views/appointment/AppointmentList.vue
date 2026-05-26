<template>
  <div class="appointment-page">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">我的预约</h1>
        <p class="page-subtitle">查看和管理您的服务预约</p>
      </div>
      <div class="header-right">
        <a-button type="primary" @click="$router.push('/services')">
          <template #icon><plus-outlined /></template>
          新建预约
        </a-button>
      </div>
    </div>

    <div class="filter-bar">
      <a-radio-group v-model:value="statusFilter" button-style="solid" @change="handleFilter">
        <a-radio-button value="">全部</a-radio-button>
        <a-radio-button value="pending">待确认</a-radio-button>
        <a-radio-button value="confirmed">已确认</a-radio-button>
        <a-radio-button value="processing">进行中</a-radio-button>
        <a-radio-button value="completed">已完成</a-radio-button>
        <a-radio-button value="cancelled">已取消</a-radio-button>
      </a-radio-group>
    </div>

    <div class="appointment-list" v-if="appointments.length > 0">
      <div class="appointment-card" v-for="apt in appointments" :key="apt.id" @click="showDetail(apt)">
        <div class="card-header">
          <div class="order-info">
            <span class="order-no">订单号：{{ apt.orderNo }}</span>
            <a-tag :color="getStatusColor(apt.status)">{{ getStatusText(apt.status) }}</a-tag>
          </div>
          <div class="order-time">{{ formatTime(apt.createdAt) }}</div>
        </div>
        
        <div class="card-body">
          <div class="pet-info">
            <div class="pet-avatar">
              <img v-if="apt.petPhoto" :src="getImageUrl(apt.petPhoto)" :alt="apt.petName" />
              <div v-else class="avatar-placeholder">
                <bug-outlined />
              </div>
            </div>
            <div class="pet-detail">
              <h4 class="pet-name">{{ apt.petName }}</h4>
              <p class="pet-type">{{ getPetTypeLabel(apt.petType) }}</p>
            </div>
          </div>
          
          <div class="service-info">
            <div class="info-item">
              <span class="label">服务套餐</span>
              <span class="value">{{ apt.packageName }}</span>
            </div>
            <div class="info-item">
              <span class="label">预约时间</span>
              <span class="value">{{ formatDateTime(apt.appointmentTime) }}</span>
            </div>
            <div class="info-item">
              <span class="label">联系人</span>
              <span class="value">{{ apt.contactName }} {{ apt.contactPhone }}</span>
            </div>
            <div class="info-item" v-if="apt.address">
              <span class="label">服务地址</span>
              <span class="value">{{ apt.address }}</span>
            </div>
          </div>
          
          <div class="price-info">
            <span class="price-label">服务费用</span>
            <span class="price-value">¥{{ apt.packagePrice }}</span>
          </div>
        </div>
        
        <div class="card-footer" @click.stop>
          <a-space>
            <a-button v-if="apt.status === 'pending'" @click="handleCancel(apt)">
              取消预约
            </a-button>
            <a-button v-if="apt.status === 'confirmed' || apt.status === 'processing'" type="primary" @click="viewProcess(apt)">
              查看进度
            </a-button>
            <a-button v-if="apt.status === 'completed'" @click="viewMemorial(apt)">
              创建纪念
            </a-button>
          </a-space>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">
        <calendar-outlined />
      </div>
      <p class="empty-text">暂无预约记录</p>
      <a-button type="primary" @click="$router.push('/services')">
        立即预约
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
      v-model:open="detailVisible"
      title="预约详情"
      :footer="null"
      width="550px"
    >
      <div class="detail-content" v-if="currentAppointment">
        <div class="detail-header">
          <div class="detail-order-no">
            <span class="label">订单号</span>
            <span class="value">{{ currentAppointment.orderNo }}</span>
          </div>
          <a-tag :color="getStatusColor(currentAppointment.status)" size="large">
            {{ getStatusText(currentAppointment.status) }}
          </a-tag>
        </div>

        <a-divider />

        <div class="detail-section">
          <h4 class="section-title">宠物信息</h4>
          <div class="pet-detail-card">
            <div class="pet-avatar-large">
              <img v-if="currentAppointment.petPhoto" :src="getImageUrl(currentAppointment.petPhoto)" :alt="currentAppointment.petName" />
              <div v-else class="avatar-placeholder-large">
                <bug-outlined />
              </div>
            </div>
            <div class="pet-info-text">
              <div class="pet-name-large">{{ currentAppointment.petName }}</div>
              <div class="pet-type-text">{{ getPetTypeLabel(currentAppointment.petType) }}</div>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">服务信息</h4>
          <div class="detail-row">
            <span class="detail-label">服务套餐</span>
            <span class="detail-value">{{ currentAppointment.packageName }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">服务费用</span>
            <span class="detail-value price">¥{{ currentAppointment.packagePrice }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">预约时间</span>
            <span class="detail-value">{{ formatDateTime(currentAppointment.appointmentTime) }}</span>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">联系信息</h4>
          <div class="detail-row">
            <span class="detail-label">联系人</span>
            <span class="detail-value">{{ currentAppointment.contactName }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">联系电话</span>
            <span class="detail-value">{{ currentAppointment.contactPhone }}</span>
          </div>
          <div class="detail-row" v-if="currentAppointment.address">
            <span class="detail-label">服务地址</span>
            <span class="detail-value">{{ currentAppointment.address }}</span>
          </div>
        </div>

        <div class="detail-section" v-if="currentAppointment.remark">
          <h4 class="section-title">备注</h4>
          <div class="detail-remark">{{ currentAppointment.remark }}</div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">时间记录</h4>
          <div class="detail-row">
            <span class="detail-label">创建时间</span>
            <span class="detail-value">{{ formatDateTime(currentAppointment.createdAt) }}</span>
          </div>
          <div class="detail-row" v-if="currentAppointment.updatedAt">
            <span class="detail-label">更新时间</span>
            <span class="detail-value">{{ formatDateTime(currentAppointment.updatedAt) }}</span>
          </div>
        </div>

        <div class="detail-actions">
          <a-button @click="detailVisible = false">关闭</a-button>
          <a-button v-if="currentAppointment.status === 'pending'" @click="handleCancel(currentAppointment); detailVisible = false">
            取消预约
          </a-button>
          <a-button v-if="currentAppointment.status === 'confirmed' || currentAppointment.status === 'processing'" type="primary" @click="viewProcess(currentAppointment)">
            查看进度
          </a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { 
  PlusOutlined, 
  BugOutlined, 
  CalendarOutlined 
} from '@ant-design/icons-vue'
import { getAppointmentList, cancelAppointment } from '../../api/appointment'
import { getImageUrl } from '../../utils'
import dayjs from 'dayjs'

const router = useRouter()
const statusFilter = ref('')
const appointments = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentAppointment = ref(null)

const statusColors = {
  pending: 'orange',
  confirmed: 'blue',
  processing: 'processing',
  completed: 'success',
  cancelled: 'default'
}

const statusTexts = {
  pending: '待确认',
  confirmed: '已确认',
  processing: '进行中',
  completed: '已完成',
  cancelled: '已取消'
}

const petTypeLabels = {
  dog: '狗',
  cat: '猫',
  bird: '鸟类',
  fish: '鱼类',
  other: '其他'
}

const getStatusColor = (status) => statusColors[status] || 'default'
const getStatusText = (status) => statusTexts[status] || status
const getPetTypeLabel = (type) => petTypeLabels[type] || type
const formatTime = (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
const formatDateTime = (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'

const loadAppointments = async () => {
  try {
    const res = await getAppointmentList({
      status: statusFilter.value || undefined,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    if (res.code === 200) {
      appointments.value = res.data.list || res.data || []
      total.value = res.data.total || appointments.value.length
    }
  } catch (error) {
    console.error('加载预约列表失败:', error)
  }
}

const showDetail = (apt) => {
  currentAppointment.value = apt
  detailVisible.value = true
}

const handleFilter = () => {
  pageNum.value = 1
  loadAppointments()
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadAppointments()
}

const handleCancel = (apt) => {
  Modal.confirm({
    title: '确认取消',
    content: '确定要取消这个预约吗？',
    okText: '确认',
    cancelText: '取消',
    async onOk() {
      try {
        await cancelAppointment(apt.id)
        message.success('取消成功')
        loadAppointments()
      } catch (error) {
        console.error('取消失败:', error)
      }
    }
  })
}

const viewProcess = (apt) => {
  detailVisible.value = false
  router.push(`/appointments/${apt.id}/process`)
}

const viewMemorial = (apt) => {
  router.push(`/memorials/create?petId=${apt.petId}`)
}

onMounted(() => {
  loadAppointments()
})
</script>

<style scoped>
.appointment-page {
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

.filter-bar {
  margin-bottom: 24px;
}

.filter-bar :deep(.ant-radio-button-wrapper) {
  border-radius: 8px;
  margin-right: 8px;
}

.filter-bar :deep(.ant-radio-button-wrapper-checked) {
  background: #262626;
  border-color: #262626;
}

.appointment-card {
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  margin-bottom: 16px;
  overflow: hidden;
  transition: all 0.3s;
}

.appointment-card:hover {
  border-color: #d9d9d9;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.order-no {
  font-size: 14px;
  color: #595959;
}

.order-time {
  font-size: 13px;
  color: #8c8c8c;
}

.card-body {
  display: flex;
  padding: 20px;
  gap: 32px;
}

.pet-info {
  display: flex;
  gap: 16px;
  min-width: 200px;
}

.pet-avatar {
  width: 64px;
  height: 64px;
  border-radius: 8px;
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
  font-size: 32px;
  color: #d9d9d9;
}

.pet-name {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 4px;
}

.pet-type {
  font-size: 13px;
  color: #8c8c8c;
  margin: 0;
}

.service-info {
  flex: 1;
}

.info-item {
  display: flex;
  margin-bottom: 8px;
}

.info-item .label {
  width: 80px;
  font-size: 13px;
  color: #8c8c8c;
}

.info-item .value {
  font-size: 13px;
  color: #262626;
}

.price-info {
  text-align: right;
  min-width: 120px;
}

.price-label {
  display: block;
  font-size: 13px;
  color: #8c8c8c;
  margin-bottom: 4px;
}

.price-value {
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.card-footer {
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
  text-align: right;
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

.detail-content {
  padding: 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-order-no .label {
  font-size: 13px;
  color: #8c8c8c;
  margin-right: 8px;
}

.detail-order-no .value {
  font-size: 14px;
  color: #262626;
  font-weight: 500;
}

.detail-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 12px;
}

.pet-detail-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
}

.pet-avatar-large {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  overflow: hidden;
  background: #f0f0f0;
}

.pet-avatar-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder-large {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #d9d9d9;
}

.pet-name-large {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.pet-type-text {
  font-size: 13px;
  color: #8c8c8c;
  margin-top: 4px;
}

.detail-row {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-label {
  width: 80px;
  flex-shrink: 0;
  font-size: 13px;
  color: #8c8c8c;
}

.detail-value {
  flex: 1;
  font-size: 13px;
  color: #262626;
}

.detail-value.price {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
}

.detail-remark {
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
  font-size: 13px;
  color: #595959;
  line-height: 1.6;
}

.detail-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  margin-top: 8px;
}
</style>
