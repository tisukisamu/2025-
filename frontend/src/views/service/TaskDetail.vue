<template>
  <div class="module-page">
    <a-page-header title="任务详情" sub-title="查看服务任务与流程节点" @back="$router.back()" />

    <a-row :gutter="[16, 16]" class="content-row">
      <a-col :xs="24" :lg="10">
        <a-card :bordered="false" title="订单信息">
          <a-descriptions :column="1" size="small">
            <a-descriptions-item label="订单号">{{ detail.orderNo || '-' }}</a-descriptions-item>
            <a-descriptions-item label="宠物">{{ detail.petName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="服务">{{ detail.packageName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="预约时间">{{ detail.appointmentTime || '-' }}</a-descriptions-item>
            <a-descriptions-item label="联系人">{{ detail.contactName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="电话">{{ detail.contactPhone || '-' }}</a-descriptions-item>
            <a-descriptions-item label="地址">{{ detail.address || '-' }}</a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="14">
        <a-card :bordered="false" title="流程进度">
          <a-timeline>
            <a-timeline-item v-for="item in processList" :key="item.id" :color="statusColor(item.status)">
              <div class="timeline-title">
                <span>{{ stageText(item.stage) }}</span>
                <a-tag :color="statusColor(item.status)">{{ statusText(item.status) }}</a-tag>
              </div>
              <div class="timeline-desc">{{ item.description || '暂无说明' }}</div>
            </a-timeline-item>
          </a-timeline>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getAppointmentById } from '../../api/appointment'
import { getProcessByAppointment } from '../../api/process'

const route = useRoute()
const detail = ref({})
const processList = ref([])

const stageMap = {
  confirmed: '确认预约',
  pickup: '接送宠物',
  farewell: '告别仪式',
  cremation: '火化服务',
  processing: '骨灰处理',
  memorial: '纪念品制作',
  completed: '服务完成'
}

const statusMap = {
  pending: '待处理',
  processing: '进行中',
  completed: '已完成'
}

const stageText = (value) => stageMap[value] || value
const statusText = (value) => statusMap[value] || value

const statusColor = (value) => {
  if (value === 'completed') return 'success'
  if (value === 'processing') return 'processing'
  return 'default'
}

const loadData = async () => {
  const id = Number(route.params.id)
  const [detailRes, processRes] = await Promise.all([getAppointmentById(id), getProcessByAppointment(id)])
  if (detailRes.code === 200) {
    detail.value = detailRes.data || {}
  }
  if (processRes.code === 200) {
    processList.value = processRes.data || []
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.module-page {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
}

.content-row {
  margin-top: 8px;
}

.timeline-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.timeline-desc {
  margin-top: 4px;
  color: #595959;
}
</style>
