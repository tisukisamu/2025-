<template>
  <div class="space-y-6">
    <a-page-header title="企业详情" @back="router.back()" />

    <a-card :bordered="false" class="rounded-xl">
      <div class="flex items-center justify-between mb-5">
        <div class="flex items-center gap-3">
          <a-avatar :size="56" :src="resolveMediaUrl(company.logoUrl)" class="!bg-neutral-200 text-neutral-700">
            {{ company.name?.charAt(0) || '企' }}
          </a-avatar>
          <div>
            <div class="text-2xl font-semibold text-neutral-900">{{ company.name || '-' }}</div>
            <div class="text-neutral-500 mt-1">{{ company.industry || '未填写行业' }}</div>
          </div>
        </div>
        <a-tag :color="statusColor">{{ statusText }}</a-tag>
      </div>

      <a-descriptions :column="2" size="middle" bordered>
        <a-descriptions-item label="企业规模">{{ company.scale || '-' }}</a-descriptions-item>
        <a-descriptions-item label="企业地址">{{ company.address || '-' }}</a-descriptions-item>
        <a-descriptions-item label="联系人">{{ company.contactPerson || '-' }}</a-descriptions-item>
        <a-descriptions-item label="联系电话">{{ company.contactPhone || '-' }}</a-descriptions-item>
        <a-descriptions-item label="联系邮箱">{{ company.contactEmail || '-' }}</a-descriptions-item>
        <a-descriptions-item label="申请时间">{{ formatDate(company.createdAt) }}</a-descriptions-item>
        <a-descriptions-item label="企业简介" :span="2">
          <div class="whitespace-pre-wrap text-neutral-700">{{ company.description || '暂无企业简介' }}</div>
        </a-descriptions-item>
      </a-descriptions>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { companyApi } from '@/api'
import { resolveMediaUrl } from '@/utils/media'

const route = useRoute()
const router = useRouter()
const company = reactive<any>({})

const statusColor = computed(() => {
  if (company.status === 'APPROVED') return 'success'
  if (company.status === 'REJECTED') return 'error'
  return 'warning'
})

const statusText = computed(() => {
  if (company.status === 'APPROVED') return '已通过'
  if (company.status === 'REJECTED') return '已拒绝'
  return '待审核'
})

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleString('zh-CN')
}

const fetchCompany = async () => {
  try {
    const id = Number(route.params.id)
    const res = await companyApi.getById(id)
    Object.assign(company, res.data || {})
  } catch (error) {
    message.error('获取企业详情失败')
  }
}

onMounted(() => {
  fetchCompany()
})
</script>
