<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-semibold text-neutral-900">面试管理</h1>
      <a-button type="primary" class="!bg-neutral-900" :disabled="!selectedApplicationId" @click="openCreateModal">
        安排面试
      </a-button>
    </div>

    <a-card :bordered="false" class="rounded-xl">
      <a-table
        :columns="applicationColumns"
        :data-source="applications"
        :pagination="false"
        row-key="id"
        :row-selection="rowSelection"
      />
    </a-card>

    <a-card :bordered="false" class="rounded-xl" title="面试记录">
      <a-empty v-if="!selectedApplicationId" description="请先在上方选择一个投递记录" />
      <a-list v-else :data-source="interviews" item-layout="vertical">
        <template #renderItem="{ item }">
          <a-list-item>
            <div class="flex items-center justify-between">
              <div class="text-sm text-neutral-700">
                <div>面试时间：{{ formatDate(item.interviewTime) }}</div>
                <div>地点/方式：{{ item.location || '未填写' }}</div>
                <div>面试官：{{ item.interviewer || '未填写' }}</div>
              </div>
              <div class="flex items-center gap-2">
                <a-select
                  :value="item.result || 'PENDING'"
                  style="width: 130px"
                  @change="(val) => updateResult(item.id, val)"
                >
                  <a-select-option value="PENDING">待面试</a-select-option>
                  <a-select-option value="PASSED">通过</a-select-option>
                  <a-select-option value="FAILED">未通过</a-select-option>
                </a-select>
              </div>
            </div>
          </a-list-item>
        </template>
      </a-list>
    </a-card>

    <a-modal v-model:open="modalOpen" title="安排面试" ok-text="确认" cancel-text="取消" @ok="createInterview">
      <a-form layout="vertical">
        <a-form-item label="面试时间">
          <a-date-picker v-model:value="formData.date" show-time value-format="YYYY-MM-DDTHH:mm:ss" class="w-full" />
        </a-form-item>
        <a-form-item label="地点/方式">
          <a-input v-model:value="formData.location" placeholder="如：线上腾讯会议 / 线下公司会议室" />
        </a-form-item>
        <a-form-item label="面试官">
          <a-input v-model:value="formData.interviewer" placeholder="请输入面试官姓名" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { applicationApi, interviewApi } from '@/api'

const applications = ref<any[]>([])
const interviews = ref<any[]>([])
const selectedApplicationId = ref<number | null>(null)
const modalOpen = ref(false)
const formData = reactive({
  date: '',
  location: '',
  interviewer: ''
})

const applicationColumns = [
  { title: '投递ID', dataIndex: 'id' },
  { title: '职位ID', dataIndex: 'jobId' },
  { title: '简历ID', dataIndex: 'resumeId' },
  { title: '状态', dataIndex: 'status' },
  { title: '投递时间', dataIndex: 'appliedAt' }
]

const rowSelection = computed(() => ({
  type: 'radio' as const,
  selectedRowKeys: selectedApplicationId.value ? [selectedApplicationId.value] : [],
  onChange: (keys: (number | string)[]) => {
    const key = Number(keys[0] || 0)
    selectedApplicationId.value = key || null
  }
}))

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleString('zh-CN', { hour12: false })
}

const fetchApplications = async () => {
  try {
    const res = await applicationApi.getReceived()
    applications.value = (res.data || []).map((item: any) => ({
      ...item,
      appliedAt: formatDate(item.appliedAt)
    }))
    if (!selectedApplicationId.value && applications.value.length > 0) {
      selectedApplicationId.value = applications.value[0].id
    }
  } catch (error) {
    message.error('获取投递记录失败')
  }
}

const fetchInterviews = async () => {
  if (!selectedApplicationId.value) {
    interviews.value = []
    return
  }
  try {
    const res = await interviewApi.getByApplication(selectedApplicationId.value)
    interviews.value = res.data || []
  } catch (error) {
    interviews.value = []
  }
}

const openCreateModal = () => {
  formData.date = ''
  formData.location = ''
  formData.interviewer = ''
  modalOpen.value = true
}

const createInterview = async () => {
  if (!selectedApplicationId.value) {
    message.warning('请先选择投递记录')
    return
  }
  if (!formData.date) {
    message.warning('请选择面试时间')
    return
  }
  try {
    await interviewApi.create({
      applicationId: selectedApplicationId.value,
      interviewTime: formData.date,
      location: formData.location,
      interviewer: formData.interviewer
    })
    modalOpen.value = false
    message.success('面试安排成功')
    await fetchInterviews()
  } catch (error) {
    message.error('面试安排失败')
  }
}

const updateResult = async (id: number, result: string) => {
  try {
    await interviewApi.updateResult(id, result)
    message.success('面试结果已更新')
    await fetchInterviews()
  } catch (error) {
    message.error('更新失败')
  }
}

watch(selectedApplicationId, () => {
  fetchInterviews()
})

onMounted(() => {
  fetchApplications()
})
</script>
