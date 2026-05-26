<template>
  <div class="process-page">
    <div class="page-header">
      <a-page-header
        title="服务进度"
        sub-title="实时追踪服务进度"
        @back="$router.back()"
      />
    </div>

    <div class="process-container">
      <div class="appointment-info">
        <div class="info-card">
          <div class="card-header">
            <h3>预约信息</h3>
            <a-tag :color="getStatusColor(appointment.status)">
              {{ getStatusText(appointment.status) }}
            </a-tag>
          </div>
          <div class="card-body">
            <div class="info-row">
              <span class="label">订单号</span>
              <span class="value">{{ appointment.orderNo }}</span>
            </div>
            <div class="info-row">
              <span class="label">宠物名称</span>
              <span class="value">{{ appointment.petName }}</span>
            </div>
            <div class="info-row">
              <span class="label">服务套餐</span>
              <span class="value">{{ appointment.packageName }}</span>
            </div>
            <div class="info-row">
              <span class="label">预约时间</span>
              <span class="value">{{ formatDateTime(appointment.appointmentTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="process-timeline">
        <h3 class="section-title">服务流程</h3>
        <a-timeline mode="left">
          <a-timeline-item
            v-for="stage in processStages"
            :key="stage.id"
            :color="getStageColor(stage.status)"
          >
            <template #dot>
              <div class="timeline-dot" :class="'status-' + stage.status">
                <check-outlined v-if="stage.status === 'completed'" />
                <loading-outlined v-else-if="stage.status === 'processing'" />
                <clock-circle-outlined v-else />
              </div>
            </template>
            <div class="stage-card">
              <div class="stage-header">
                <h4 class="stage-name">{{ getStageLabel(stage.stage) }}</h4>
                <a-tag :color="getStageTagColor(stage.status)" size="small">
                  {{ getStageStatusText(stage.status) }}
                </a-tag>
              </div>
              <p class="stage-time" v-if="stage.startTime">
                开始时间：{{ formatDateTime(stage.startTime) }}
              </p>
              <p class="stage-time" v-if="stage.endTime">
                完成时间：{{ formatDateTime(stage.endTime) }}
              </p>
              <p class="stage-desc" v-if="stage.description">{{ stage.description }}</p>
              <div class="stage-photos" v-if="stage.photos && stage.photos.length > 0">
                <img
                  v-for="(photo, index) in stage.photos"
                  :key="index"
                  :src="getImageUrl(photo)"
                  class="stage-photo"
                  @click="previewPhoto(getImageUrl(photo))"
                />
              </div>
            </div>
          </a-timeline-item>
        </a-timeline>
      </div>
    </div>

    <a-modal :open="previewVisible" :footer="null" @cancel="previewVisible = false">
      <img :src="previewImage" alt="preview" style="width: 100%" />
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import {
  CheckOutlined,
  LoadingOutlined,
  ClockCircleOutlined
} from '@ant-design/icons-vue'
import { getAppointmentById } from '../../api/appointment'
import { getProcessByAppointment } from '../../api/process'
import { getImageUrl } from '../../utils'
import dayjs from 'dayjs'

const route = useRoute()
const appointment = ref({})
const processStages = ref([])
const previewVisible = ref(false)
const previewImage = ref('')

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

const stageLabels = {
  confirmed: '确认预约',
  pickup: '接送宠物',
  farewell: '告别仪式',
  cremation: '火化服务',
  processing: '骨灰处理',
  memorial: '纪念品制作',
  completed: '服务完成'
}

const stageStatusTexts = {
  pending: '待处理',
  processing: '进行中',
  completed: '已完成'
}

const getStatusColor = (status) => statusColors[status] || 'default'
const getStatusText = (status) => statusTexts[status] || status
const getStageLabel = (stage) => stageLabels[stage] || stage
const getStageStatusText = (status) => stageStatusTexts[status] || status
const formatDateTime = (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : ''

const getStageColor = (status) => {
  const colors = {
    completed: 'success',
    processing: 'processing',
    pending: 'gray'
  }
  return colors[status] || 'gray'
}

const getStageTagColor = (status) => {
  const colors = {
    completed: 'success',
    processing: 'processing',
    pending: 'default'
  }
  return colors[status] || 'default'
}

const previewPhoto = (url) => {
  previewImage.value = url
  previewVisible.value = true
}

const loadAppointment = async () => {
  try {
    const id = route.params.id
    const res = await getAppointmentById(id)
    if (res.code === 200) {
      appointment.value = res.data
    }
  } catch (error) {
    console.error('加载预约信息失败:', error)
  }
}

const loadProcess = async () => {
  try {
    const id = route.params.id
    const res = await getProcessByAppointment(id)
    if (res.code === 200) {
      processStages.value = res.data || []
    }
  } catch (error) {
    console.error('加载流程信息失败:', error)
  }
}

onMounted(() => {
  loadAppointment()
  loadProcess()
})
</script>

<style scoped>
.process-page {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
}

.page-header {
  margin-bottom: 24px;
}

.process-container {
  max-width: 900px;
  margin: 0 auto;
}

.appointment-info {
  margin-bottom: 32px;
}

.info-card {
  background: #fafafa;
  border-radius: 12px;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.card-body {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.info-row {
  display: flex;
  gap: 12px;
}

.info-row .label {
  color: #8c8c8c;
  font-size: 14px;
  min-width: 70px;
}

.info-row .value {
  color: #262626;
  font-size: 14px;
  font-weight: 500;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 24px;
}

.process-timeline {
  padding: 24px;
  background: #fafafa;
  border-radius: 12px;
}

.timeline-dot {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.timeline-dot.status-completed {
  background: #52c41a;
  color: #fff;
}

.timeline-dot.status-processing {
  background: #1890ff;
  color: #fff;
}

.timeline-dot.status-pending {
  background: #d9d9d9;
  color: #fff;
}

.stage-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 8px;
}

.stage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.stage-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.stage-time {
  margin: 0 0 8px;
  font-size: 13px;
  color: #8c8c8c;
}

.stage-desc {
  margin: 8px 0;
  font-size: 14px;
  color: #595959;
}

.stage-photos {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.stage-photo {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s;
}

.stage-photo:hover {
  transform: scale(1.1);
}
</style>
