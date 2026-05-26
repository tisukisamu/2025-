<template>
  <div class="module-page">
    <a-page-header title="流程更新" sub-title="提交当前任务执行进度" @back="$router.back()" />

    <a-row :gutter="[16, 16]" class="content-row">
      <a-col :xs="24" :lg="8">
        <a-card :bordered="false" title="订单信息">
          <a-descriptions :column="1" size="small">
            <a-descriptions-item label="订单号">{{ appointment.orderNo || '-' }}</a-descriptions-item>
            <a-descriptions-item label="宠物">{{ appointment.petName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="服务">{{ appointment.packageName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="联系人">{{ appointment.contactName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="电话">{{ appointment.contactPhone || '-' }}</a-descriptions-item>
            <a-descriptions-item label="当前状态">
              <a-tag :color="statusColor(appointment.status)">{{ statusText(appointment.status) }}</a-tag>
            </a-descriptions-item>
          </a-descriptions>
          <a-divider />
          <a-space direction="vertical" style="width: 100%">
            <a-button 
              type="primary" 
              block 
              :loading="completing"
              :disabled="appointment.status === 'completed' || appointment.status === 'cancelled'"
              @click="showCompleteConfirm"
            >
              完成服务
            </a-button>
            <a-button 
              block 
              @click="$router.push(`/service/task/${route.params.id}`)"
            >
              查看详情
            </a-button>
          </a-space>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="8">
        <a-card :bordered="false" title="更新流程">
          <a-form layout="vertical">
            <a-form-item label="流程阶段">
              <a-select v-model:value="form.stage">
                <a-select-option v-for="(label, value) in stageMap" :key="value" :value="value">
                  {{ label }}
                </a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="执行状态">
              <a-select v-model:value="form.status">
                <a-select-option value="pending">待处理</a-select-option>
                <a-select-option value="processing">进行中</a-select-option>
                <a-select-option value="completed">已完成</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="执行说明">
              <a-textarea v-model:value="form.description" :rows="3" placeholder="请输入阶段说明" />
            </a-form-item>
            <a-button type="primary" block :loading="saving" @click="submitUpdate">提交更新</a-button>
          </a-form>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="8">
        <a-card :bordered="false" title="流程记录">
          <a-timeline>
            <a-timeline-item v-for="item in processList" :key="item.id" :color="getStageColor(item.status)">
              <div class="timeline-title">
                <span>{{ stageMap[item.stage] || item.stage }}</span>
                <a-tag :color="getStageColor(item.status)">{{ stageStatusText(item.status) }}</a-tag>
              </div>
              <p class="timeline-desc">{{ item.description || '暂无说明' }}</p>
            </a-timeline-item>
          </a-timeline>
          <a-empty v-if="!processList.length" description="暂无流程记录" />
        </a-card>
      </a-col>
    </a-row>

    <a-modal
      v-model:open="completeModalVisible"
      title="确认完成服务"
      @ok="handleComplete"
      :confirm-loading="completing"
    >
      <p>确定要将此订单标记为已完成吗？</p>
      <p>订单号：{{ appointment.orderNo }}</p>
      <a-form-item label="完成备注">
        <a-textarea v-model:value="completeRemark" :rows="3" placeholder="请输入完成备注（可选）" />
      </a-form-item>
    </a-modal>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { getProcessByAppointment, updateProcess } from '../../api/process'
import { getAppointmentById, updateServiceAppointmentStatus } from '../../api/appointment'

const route = useRoute()
const saving = ref(false)
const completing = ref(false)
const completeModalVisible = ref(false)
const completeRemark = ref('')
const processList = ref([])
const appointment = ref({})

const stageMap = {
  confirmed: '确认预约',
  pickup: '接送宠物',
  farewell: '告别仪式',
  cremation: '火化服务',
  processing: '骨灰处理',
  memorial: '纪念品制作',
  completed: '服务完成'
}

const form = reactive({
  stage: 'pickup',
  status: 'processing',
  description: ''
})

const statusText = (value) => {
  const map = {
    pending: '待确认',
    confirmed: '已确认',
    processing: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[value] || value
}

const statusColor = (value) => {
  const map = {
    pending: 'orange',
    confirmed: 'blue',
    processing: 'cyan',
    completed: 'green',
    cancelled: 'red'
  }
  return map[value] || 'default'
}

const stageStatusText = (value) => {
  if (value === 'completed') return '已完成'
  if (value === 'processing') return '进行中'
  return '待处理'
}

const getStageColor = (value) => {
  if (value === 'completed') return 'success'
  if (value === 'processing') return 'processing'
  return 'default'
}

const loadAppointment = async () => {
  const id = Number(route.params.id)
  const res = await getAppointmentById(id)
  if (res.code === 200) {
    appointment.value = res.data || {}
  }
}

const loadProcess = async () => {
  const id = Number(route.params.id)
  const res = await getProcessByAppointment(id)
  if (res.code === 200) {
    processList.value = res.data || []
  }
}

const submitUpdate = async () => {
  saving.value = true
  try {
    const id = Number(route.params.id)
    await updateProcess(id, {
      stage: form.stage,
      status: form.status,
      description: form.description
    })
    message.success('流程更新成功')
    form.description = ''
    await loadProcess()
    await loadAppointment()
  } catch {
    message.error('流程更新失败')
  } finally {
    saving.value = false
  }
}

const showCompleteConfirm = () => {
  completeRemark.value = ''
  completeModalVisible.value = true
}

const handleComplete = async () => {
  completing.value = true
  try {
    const id = Number(route.params.id)
    await updateServiceAppointmentStatus(id, 'completed')
    if (completeRemark.value) {
      await updateProcess(id, {
        stage: 'completed',
        status: 'completed',
        description: completeRemark.value
      })
    }
    message.success('服务已完成')
    completeModalVisible.value = false
    await loadAppointment()
    await loadProcess()
  } catch {
    message.error('操作失败')
  } finally {
    completing.value = false
  }
}

onMounted(() => {
  loadAppointment()
  loadProcess()
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
}

.timeline-desc {
  margin: 4px 0 0;
  color: #595959;
}
</style>
