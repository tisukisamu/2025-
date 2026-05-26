<template>
  <div class="space-y-5">
    <a-page-header title="职位详情" @back="router.back()" />

    <a-card :bordered="false" class="rounded-xl">
      <div class="flex items-start justify-between gap-4">
        <div class="flex items-center gap-3">
          <a-avatar :src="resolveMediaUrl(job.coverUrl)" shape="square" :size="56" class="!bg-neutral-200">
            {{ job.title?.charAt(0) || '职' }}
          </a-avatar>
          <div>
            <div class="text-2xl font-semibold text-neutral-900">{{ job.title || '-' }}</div>
            <div class="text-neutral-500 mt-1">{{ company.name || '-' }}</div>
          </div>
        </div>
        <div class="text-right">
          <div class="text-2xl font-bold text-neutral-900">{{ salaryText }}</div>
          <div class="text-neutral-500 text-sm mt-1">{{ job.location || '地点待完善' }}</div>
        </div>
      </div>
      <div class="mt-4 flex flex-wrap gap-2">
        <a-tag>{{ job.jobType || '不限' }}</a-tag>
        <a-tag>{{ job.experience || '不限' }}</a-tag>
        <a-tag>{{ job.education || '不限' }}</a-tag>
      </div>
      <div class="mt-5 flex gap-3">
        <a-button type="primary" class="!bg-neutral-900" @click="applyJob">立即投递</a-button>
        <a-button @click="router.push('/jobs')">返回职位列表</a-button>
      </div>
    </a-card>

    <a-card :bordered="false" class="rounded-xl" title="职位描述">
      <div class="whitespace-pre-wrap text-neutral-700">{{ job.description || '暂无职位描述' }}</div>
    </a-card>

    <a-card :bordered="false" class="rounded-xl" title="任职要求">
      <div class="whitespace-pre-wrap text-neutral-700">{{ job.requirements || '暂无任职要求' }}</div>
    </a-card>

    <a-card :bordered="false" class="rounded-xl" title="企业信息">
      <a-descriptions :column="2" size="small">
        <a-descriptions-item label="企业名称">{{ company.name || '-' }}</a-descriptions-item>
        <a-descriptions-item label="所属行业">{{ company.industry || '-' }}</a-descriptions-item>
        <a-descriptions-item label="企业规模">{{ company.scale || '-' }}</a-descriptions-item>
        <a-descriptions-item label="联系方式">{{ company.contactPhone || '-' }}</a-descriptions-item>
        <a-descriptions-item label="企业地址" :span="2">{{ company.address || '-' }}</a-descriptions-item>
      </a-descriptions>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { applicationApi, companyApi, jobApi, resumeApi } from '@/api'
import { resolveMediaUrl } from '@/utils/media'

const route = useRoute()
const router = useRouter()

const job = reactive<any>({})
const company = reactive<any>({})

const salaryText = computed(() => `${job.salaryMin || 0}-${job.salaryMax || 0}K`)

const fetchDetail = async () => {
  try {
    const id = Number(route.params.id)
    const jobRes = await jobApi.getById(id)
    Object.assign(job, jobRes.data || {})
    if (job.companyId) {
      const companyRes = await companyApi.getById(job.companyId)
      Object.assign(company, companyRes.data || {})
    }
  } catch (error) {
    message.error('职位详情加载失败')
  }
}

const applyJob = async () => {
  try {
    const resumesRes = await resumeApi.getMy()
    const firstResume = resumesRes.data?.[0]
    if (!firstResume) {
      message.warning('请先创建简历后再投递')
      router.push('/resumes')
      return
    }
    await applicationApi.create({
      jobId: job.id,
      resumeId: firstResume.id
    })
    message.success('投递成功')
  } catch (error) {
    message.error('投递失败')
  }
}

onMounted(() => {
  fetchDetail()
})
</script>
