<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">资金申请</h1>
    </div>

    <div class="card">
      <a-form
        ref="formRef"
        :model="formState"
        :rules="rules"
        layout="vertical"
      >
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="申请社团" name="clubId">
              <a-select v-model:value="formState.clubId" placeholder="选择社团">
                <a-select-option v-for="club in clubs" :key="club.id" :value="club.id">
                  {{ club.clubName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="申请类型" name="applyType">
              <a-select v-model:value="formState.applyType" placeholder="选择类型">
                <a-select-option value="ACTIVITY_FUND">活动经费</a-select-option>
                <a-select-option value="MATERIAL">物资采购</a-select-option>
                <a-select-option value="REIMBURSEMENT">报销申请</a-select-option>
                <a-select-option value="OTHER">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="申请金额" name="amount">
              <a-input-number
                v-model:value="formState.amount"
                :min="0.01"
                :precision="2"
                style="width: 100%"
                placeholder="请输入金额"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="关联活动">
              <a-select v-model:value="formState.activityId" placeholder="选择活动(可选)" allow-clear>
                <a-select-option v-for="activity in activities" :key="activity.id" :value="activity.id">
                  {{ activity.activityName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="申请理由" name="reason">
          <a-textarea
            v-model:value="formState.reason"
            :rows="4"
            placeholder="请详细说明申请理由(不少于20字)"
            :maxlength="500"
            show-count
          />
        </a-form-item>

        <a-form-item label="上传凭证">
          <a-upload
            v-model:file-list="fileList"
            list-type="picture-card"
            :action="uploadUrl"
            :headers="uploadHeaders"
            @preview="handlePreview"
          >
            <div v-if="fileList.length < 5">
              <PlusOutlined />
              <div style="margin-top: 8px">上传图片</div>
            </div>
          </a-upload>
          <a-modal :open="previewVisible" :footer="null" @cancel="previewVisible = false">
            <img alt="preview" style="width: 100%" :src="previewImage" />
          </a-modal>
        </a-form-item>

        <a-form-item>
          <a-space>
            <a-button type="primary" :loading="loading" @click="handleSubmit">
              提交申请
            </a-button>
            <a-button @click="handleReset">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message, type FormInstance, type Rule, type UploadFile } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores'
import { clubApi, activityApi, fundApi } from '@/api'
import type { Club, Activity } from '@/types'
import { PlusOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const clubs = ref<Club[]>([])
const activities = ref<Activity[]>([])
const fileList = ref<UploadFile[]>([])
const previewVisible = ref(false)
const previewImage = ref('')

const formState = reactive({
  clubId: undefined as number | undefined,
  applyType: undefined as string | undefined,
  amount: undefined as number | undefined,
  activityId: undefined as number | undefined,
  reason: '',
  vouchers: [] as string[]
})

const rules: Record<string, Rule[]> = {
  clubId: [{ required: true, message: '请选择社团' }],
  applyType: [{ required: true, message: '请选择申请类型' }],
  amount: [{ required: true, message: '请输入申请金额' }],
  reason: [
    { required: true, message: '请输入申请理由' },
    { min: 20, message: '申请理由不能少于20字' }
  ]
}

const uploadUrl = computed(() => {
  return import.meta.env.VITE_API_BASE_URL
    ? `${import.meta.env.VITE_API_BASE_URL}/upload/image?type=voucher`
    : '/api/upload/image?type=voucher'
})

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

const handlePreview = async (file: UploadFile) => {
  if (!file.url && !file.preview) {
    file.preview = (file.originFileObj as any)?.url
  }
  previewImage.value = (file.url || file.preview) as string
  previewVisible.value = true
}

const fetchClubs = async () => {
  try {
    const res = await clubApi.getList(0, 100)
    clubs.value = res.data?.content || []
    if (clubs.value.length > 0) {
      formState.clubId = clubs.value[0].id
      fetchActivities(formState.clubId)
    }
  } catch (error) {
    console.error('获取社团列表失败', error)
  }
}

const fetchActivities = async (clubId: number) => {
  try {
    const res = await activityApi.getList({ clubId, page: 0, size: 100 })
    activities.value = res.data.list
  } catch (error) {
    console.error('获取活动列表失败', error)
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    
    const vouchers = fileList.value
      .filter(f => f.status === 'done')
      .map(f => f.response?.data?.url || f.url)
      .filter(Boolean)

    loading.value = true
    await fundApi.createApply({
      ...formState,
      vouchers
    } as any)
    
    message.success('申请提交成功')
    router.push('/president/fund/review')
  } catch (error: any) {
    message.error(error.message || '提交失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  formRef.value?.resetFields()
  fileList.value = []
}

onMounted(() => {
  fetchClubs()
})
</script>

<style scoped>
:deep(.ant-upload-picture-card-wrapper) {
  width: auto;
}
</style>
