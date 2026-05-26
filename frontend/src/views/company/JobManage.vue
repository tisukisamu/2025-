<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-semibold text-neutral-900">职位管理</h1>
      <a-button type="primary" class="!bg-neutral-900 !rounded-lg" @click="showCreateModal">
        <plus-outlined /> 发布职位
      </a-button>
    </div>
    
    <div class="bg-white rounded-xl border border-neutral-100">
      <div class="p-4 border-b border-neutral-100">
        <a-radio-group v-model:value="statusFilter" button-style="solid">
          <a-radio-button value="all">全部职位</a-radio-button>
          <a-radio-button value="active">招聘中</a-radio-button>
          <a-radio-button value="closed">已关闭</a-radio-button>
        </a-radio-group>
      </div>
      
      <a-table :columns="columns" :data-source="filteredJobs" :pagination="false">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'title'">
            <div class="flex items-center gap-3">
              <a-avatar shape="square" :size="40" :src="resolveMediaUrl(record.coverUrl)" class="!bg-neutral-200">
                {{ record.title?.charAt(0) || '职' }}
              </a-avatar>
              <div class="font-medium text-neutral-900">{{ record.title }}</div>
              <div class="text-sm text-neutral-400">{{ record.location }}</div>
            </div>
          </template>
          <template v-if="column.key === 'salary'">
            <span class="text-neutral-900 font-medium">{{ record.salaryMin }}-{{ record.salaryMax }}K</span>
          </template>
          <template v-if="column.key === 'applications'">
            <a-badge :count="record.applicationCount" :number-style="{ backgroundColor: '#171717' }">
              <a-button type="link" size="small" @click="viewApplications(record)">
                查看简历
              </a-button>
            </a-badge>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 'ACTIVE' ? 'success' : 'default'">
              {{ record.status === 'ACTIVE' ? '招聘中' : '已关闭' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="editJob(record)">编辑</a-button>
              <a-button 
                type="link" 
                size="small" 
                @click="toggleJobStatus(record)"
                :class="record.status === 'ACTIVE' ? '!text-red-500' : '!text-green-500'"
              >
                {{ record.status === 'ACTIVE' ? '关闭' : '开启' }}
              </a-button>
              <a-popconfirm
                title="确定要删除这个职位吗？"
                @confirm="deleteJob(record.id)"
              >
                <a-button type="link" size="small" danger>删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>
    
    <a-modal
      v-model:open="modalVisible"
      :title="editingJob ? '编辑职位' : '发布职位'"
      width="800px"
      :footer="null"
    >
      <a-form
        :model="formData"
        :rules="rules"
        layout="vertical"
        @finish="handleSubmit"
        class="mt-4"
      >
        <a-form-item label="职位名称" name="title">
          <a-input v-model:value="formData.title" placeholder="请输入职位名称" />
        </a-form-item>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="薪资范围" name="salary">
              <a-row :gutter="8">
                <a-col :span="11">
                  <a-input-number v-model:value="formData.salaryMin" placeholder="最低" class="w-full" addon-after="K" />
                </a-col>
                <a-col :span="2" class="text-center pt-2">-</a-col>
                <a-col :span="11">
                  <a-input-number v-model:value="formData.salaryMax" placeholder="最高" class="w-full" addon-after="K" />
                </a-col>
              </a-row>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="工作地点" name="location">
              <a-input v-model:value="formData.location" placeholder="请输入工作地点" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="工作类型" name="jobType">
              <a-select v-model:value="formData.jobType" placeholder="请选择">
                <a-select-option value="全职">全职</a-select-option>
                <a-select-option value="兼职">兼职</a-select-option>
                <a-select-option value="实习">实习</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="学历要求" name="education">
              <a-select v-model:value="formData.education" placeholder="请选择">
                <a-select-option value="大专">大专</a-select-option>
                <a-select-option value="本科">本科</a-select-option>
                <a-select-option value="硕士">硕士</a-select-option>
                <a-select-option value="博士">博士</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="经验要求" name="experience">
              <a-select v-model:value="formData.experience" placeholder="请选择">
                <a-select-option value="应届生">应届生</a-select-option>
                <a-select-option value="1-3年">1-3年</a-select-option>
                <a-select-option value="3-5年">3-5年</a-select-option>
                <a-select-option value="5-10年">5-10年</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="职位封面">
          <div class="flex items-center gap-4">
            <a-avatar shape="square" :size="56" :src="resolveMediaUrl(formData.coverUrl)" class="!bg-neutral-200">
              {{ formData.title?.charAt(0) || '职' }}
            </a-avatar>
            <a-upload :show-upload-list="false" :before-upload="handleCoverUpload">
              <a-button :loading="coverUploading">上传封面</a-button>
            </a-upload>
            <span class="text-neutral-400 text-xs">数据库仅保存相对地址</span>
          </div>
        </a-form-item>
        
        <a-form-item label="职位描述" name="description">
          <a-textarea v-model:value="formData.description" :rows="4" placeholder="请输入职位描述" />
        </a-form-item>
        
        <a-form-item label="任职要求" name="requirements">
          <a-textarea v-model:value="formData.requirements" :rows="4" placeholder="请输入任职要求" />
        </a-form-item>
        
        <div class="flex justify-end gap-3">
          <a-button @click="modalVisible = false">取消</a-button>
          <a-button type="primary" html-type="submit" class="!bg-neutral-900">
            {{ editingJob ? '保存' : '发布' }}
          </a-button>
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { fileApi, jobApi } from '@/api'
import type { Job, JobDTO } from '@/types/job'
import { resolveMediaUrl } from '@/utils/media'

const router = useRouter()
const statusFilter = ref('all')
const modalVisible = ref(false)
const coverUploading = ref(false)
const editingJob = ref<Job | null>(null)

const columns = [
  { title: '职位名称', key: 'title' },
  { title: '薪资', key: 'salary', width: 120 },
  { title: '投递数', key: 'applications', width: 120 },
  { title: '状态', key: 'status', width: 100 },
  { title: '发布时间', dataIndex: 'createdAt', width: 120 },
  { title: '操作', key: 'action', width: 180 }
]

const jobs = ref<any[]>([])

const filteredJobs = computed(() => {
  if (statusFilter.value === 'all') return jobs.value
  if (statusFilter.value === 'active') return jobs.value.filter(j => j.status === 'ACTIVE')
  return jobs.value.filter(j => j.status === 'CLOSED')
})

const formData = reactive({
  title: '',
  salaryMin: undefined as number | undefined,
  salaryMax: undefined as number | undefined,
  location: '',
  jobType: undefined,
  education: undefined,
  experience: undefined,
  description: '',
  requirements: '',
  coverUrl: ''
})

const rules = {
  title: [{ required: true, message: '请输入职位名称' }],
  location: [{ required: true, message: '请输入工作地点' }]
}

const showCreateModal = () => {
  editingJob.value = null
  Object.assign(formData, {
    title: '',
    salaryMin: undefined,
    salaryMax: undefined,
    location: '',
    jobType: undefined,
    education: undefined,
    experience: undefined,
    description: '',
    requirements: '',
    coverUrl: ''
  })
  modalVisible.value = true
}

const editJob = (job: Job) => {
  editingJob.value = job
  Object.assign(formData, job)
  modalVisible.value = true
}

const toggleJobStatus = (job: any) => {
  if (job.status === 'ACTIVE') {
    closeJob(job.id)
    return
  }
  updateJobStatus(job)
}

const deleteJob = async (id: number) => {
  await jobApi.delete(id)
  await fetchJobs()
  message.success('删除成功')
}

const viewApplications = () => {
  router.push('/company/resumes')
}

const handleSubmit = async () => {
  try {
    const payload: JobDTO = {
      title: formData.title,
      salaryMin: formData.salaryMin,
      salaryMax: formData.salaryMax,
      location: formData.location,
      jobType: formData.jobType,
      education: formData.education,
      experience: formData.experience,
      description: formData.description,
      requirements: formData.requirements,
      coverUrl: formData.coverUrl,
      status: 'ACTIVE'
    }
    if (editingJob.value) {
      await jobApi.update(editingJob.value.id, payload)
      message.success('职位更新成功')
    } else {
      await jobApi.create(payload)
      message.success('职位发布成功')
    }
    await fetchJobs()
    modalVisible.value = false
  } catch (error) {
    message.error('操作失败')
  }
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleDateString('zh-CN')
}

const fetchJobs = async () => {
  try {
    const res = await jobApi.getMy()
    jobs.value = (res.data || []).map((job: any) => ({
      ...job,
      applicationCount: 0,
      createdAt: formatDate(job.createdAt)
    }))
  } catch (error) {
    message.error('获取职位列表失败')
  }
}

const closeJob = async (id: number) => {
  try {
    await jobApi.close(id)
    await fetchJobs()
    message.success('职位已关闭')
  } catch (error) {
    message.error('关闭职位失败')
  }
}

const updateJobStatus = async (job: Job) => {
  try {
    await jobApi.update(job.id, {
      title: job.title,
      description: job.description,
      requirements: job.requirements,
      salaryMin: job.salaryMin,
      salaryMax: job.salaryMax,
      location: job.location,
      jobType: job.jobType,
      education: job.education,
      experience: job.experience,
      coverUrl: job.coverUrl,
      status: 'ACTIVE'
    })
    await fetchJobs()
    message.success('职位已开启')
  } catch (error) {
    message.error('开启职位失败')
  }
}

const handleCoverUpload = async (file: File) => {
  try {
    coverUploading.value = true
    const res = await fileApi.uploadImage(file, 'job')
    formData.coverUrl = res.data.path
    message.success('职位封面上传成功')
  } catch (error) {
    message.error('职位封面上传失败')
  } finally {
    coverUploading.value = false
  }
  return false
}

onMounted(() => {
  fetchJobs()
})
</script>
