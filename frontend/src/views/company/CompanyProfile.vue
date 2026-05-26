<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-semibold text-neutral-900">企业资料管理</h1>
      <a-tag :color="statusColorMap[companyStatus]">{{ statusTextMap[companyStatus] }}</a-tag>
    </div>

    <a-card :bordered="false" class="rounded-xl">
      <a-form layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="企业名称">
              <a-input v-model:value="formData.name" placeholder="请输入企业名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="所属行业">
              <a-input v-model:value="formData.industry" placeholder="如：互联网 / 金融 / 制造" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="企业规模">
              <a-input v-model:value="formData.scale" placeholder="如：100-499人" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="企业地址">
              <a-input v-model:value="formData.address" placeholder="请输入企业地址" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="联系人">
              <a-input v-model:value="formData.contactPerson" placeholder="请输入联系人姓名" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="联系电话">
              <a-input v-model:value="formData.contactPhone" placeholder="请输入联系电话" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="联系邮箱">
              <a-input v-model:value="formData.contactEmail" placeholder="请输入联系邮箱" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="企业Logo">
          <div class="flex items-center gap-4">
            <a-avatar :size="56" :src="resolveMediaUrl(formData.logoUrl)" class="!bg-neutral-200 text-neutral-700">
              {{ formData.name?.charAt(0) || '企' }}
            </a-avatar>
            <a-upload :show-upload-list="false" :before-upload="handleLogoUpload">
              <a-button :loading="uploading">上传图片</a-button>
            </a-upload>
            <span class="text-neutral-400 text-xs">建议上传 1:1 图片，系统仅保存相对地址</span>
          </div>
        </a-form-item>

        <a-form-item label="企业介绍">
          <a-textarea
            v-model:value="formData.description"
            :rows="5"
            placeholder="请输入企业简介、业务方向、团队亮点等信息"
          />
        </a-form-item>

        <div class="flex justify-end">
          <a-button type="primary" class="!bg-neutral-900" :loading="saving" @click="saveCompany">
            保存企业资料
          </a-button>
        </div>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { companyApi, fileApi } from '@/api'
import type { CompanyStatus } from '@/types/company'
import { resolveMediaUrl } from '@/utils/media'

const saving = ref(false)
const uploading = ref(false)
const companyId = ref<number | null>(null)
const companyStatus = ref<CompanyStatus>('PENDING' as CompanyStatus)

const formData = reactive({
  name: '',
  description: '',
  industry: '',
  scale: '',
  address: '',
  contactPerson: '',
  contactPhone: '',
  contactEmail: '',
  logoUrl: ''
})

const statusColorMap: Record<string, string> = {
  PENDING: 'gold',
  APPROVED: 'green',
  REJECTED: 'red'
}

const statusTextMap: Record<string, string> = {
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已拒绝'
}

const fillForm = (data: any) => {
  companyId.value = data.id || null
  companyStatus.value = data.status || 'PENDING'
  formData.name = data.name || ''
  formData.description = data.description || ''
  formData.industry = data.industry || ''
  formData.scale = data.scale || ''
  formData.address = data.address || ''
  formData.contactPerson = data.contactPerson || ''
  formData.contactPhone = data.contactPhone || ''
  formData.contactEmail = data.contactEmail || ''
  formData.logoUrl = data.logoUrl || ''
}

const fetchMyCompany = async () => {
  try {
    const res = await companyApi.getMy()
    if (res.data) {
      fillForm(res.data)
    }
  } catch (error) {
    companyId.value = null
  }
}

const saveCompany = async () => {
  try {
    saving.value = true
    const payload = {
      name: formData.name,
      description: formData.description,
      industry: formData.industry,
      scale: formData.scale,
      address: formData.address,
      contactPerson: formData.contactPerson,
      contactPhone: formData.contactPhone,
      contactEmail: formData.contactEmail,
      logoUrl: formData.logoUrl
    }
    if (companyId.value) {
      const res = await companyApi.update(companyId.value, payload)
      if (res.data) fillForm(res.data)
      message.success('企业资料已更新')
    } else {
      const res = await companyApi.create(payload)
      if (res.data) fillForm(res.data)
      message.success('企业资料已创建，等待审核')
    }
  } catch (error) {
    message.error('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

const handleLogoUpload = async (file: File) => {
  try {
    uploading.value = true
    const res = await fileApi.uploadImage(file, 'company')
    formData.logoUrl = res.data.path
    message.success('Logo 上传成功')
  } catch (error) {
    message.error('Logo 上传失败')
  } finally {
    uploading.value = false
  }
  return false
}

onMounted(() => {
  fetchMyCompany()
})
</script>
