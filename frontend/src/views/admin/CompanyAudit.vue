<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-semibold text-neutral-900">企业审核</h1>
      <a-radio-group v-model:value="statusFilter" button-style="solid">
        <a-radio-button value="all">全部企业</a-radio-button>
        <a-radio-button value="PENDING">待审核</a-radio-button>
        <a-radio-button value="APPROVED">已通过</a-radio-button>
        <a-radio-button value="REJECTED">已拒绝</a-radio-button>
      </a-radio-group>
    </div>
    
    <div class="grid grid-cols-2 gap-6">
      <div 
        v-for="company in filteredCompanies" 
        :key="company.id"
        class="bg-white rounded-xl border border-neutral-100 hover:border-neutral-300 transition-colors"
      >
        <div class="p-6">
          <div class="flex justify-between items-start mb-4">
            <div class="flex items-center gap-4">
              <div class="w-14 h-14 bg-neutral-100 rounded-xl flex items-center justify-center overflow-hidden">
                <img v-if="company.logoUrl" :src="resolveMediaUrl(company.logoUrl)" class="w-full h-full object-cover" />
                <bank-outlined v-else class="text-2xl text-neutral-400" />
              </div>
              <div>
                <h3 class="text-lg font-semibold text-neutral-900">{{ company.name }}</h3>
                <p class="text-neutral-500 text-sm">{{ company.industry }}</p>
              </div>
            </div>
            <a-tag :color="getStatusColor(company.status)">
              {{ getStatusText(company.status) }}
            </a-tag>
          </div>
          
          <div class="space-y-2 text-sm">
            <div class="flex items-center text-neutral-600">
              <environment-outlined class="mr-2 text-neutral-400" />
              {{ company.address }}
            </div>
            <div class="flex items-center text-neutral-600">
              <team-outlined class="mr-2 text-neutral-400" />
              公司规模: {{ company.scale }}
            </div>
            <div class="flex items-center text-neutral-600">
              <phone-outlined class="mr-2 text-neutral-400" />
              {{ company.contactPerson }} · {{ company.contactPhone }}
            </div>
          </div>
          
          <div class="mt-4 pt-4 border-t border-neutral-100">
            <div class="text-neutral-500 text-sm mb-2">企业简介</div>
            <p class="text-neutral-600 text-sm line-clamp-2">{{ company.description }}</p>
          </div>
        </div>
        
        <div class="px-6 py-4 bg-neutral-50 rounded-b-xl flex justify-between items-center">
          <span class="text-neutral-400 text-sm">申请时间: {{ company.createdAt }}</span>
          <div class="flex gap-2">
            <a-button 
              v-if="company.status === 'PENDING'"
              type="primary"
              class="!bg-green-600"
              @click="approveCompany(company)"
            >
              通过
            </a-button>
            <a-button 
              v-if="company.status === 'PENDING'"
              danger
              @click="rejectCompany(company)"
            >
              拒绝
            </a-button>
            <a-button type="link" @click="viewDetail(company)">
              查看详情
            </a-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { 
  BankOutlined, 
  EnvironmentOutlined, 
  TeamOutlined, 
  PhoneOutlined 
} from '@ant-design/icons-vue'
import { companyApi } from '@/api'
import { resolveMediaUrl } from '@/utils/media'

const statusFilter = ref('all')
const router = useRouter()
const companies = ref<any[]>([])

const filteredCompanies = computed(() => {
  if (statusFilter.value === 'all') return companies.value
  return companies.value.filter(c => c.status === statusFilter.value)
})

const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'error'
  }
  return colors[status] || 'default'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已拒绝'
  }
  return texts[status] || status
}

const approveCompany = (company: any) => {
  Modal.confirm({
    title: '确认通过',
    content: `确定要通过企业 "${company.name}" 的审核吗？`,
    okText: '确认',
    cancelText: '取消',
    onOk() {
      company.status = 'APPROVED'
      message.success('审核状态已更新')
    }
  })
}

const rejectCompany = (company: any) => {
  Modal.confirm({
    title: '确认拒绝',
    content: `确定要拒绝企业 "${company.name}" 的申请吗？`,
    okText: '确认',
    okType: 'danger',
    cancelText: '取消',
    onOk() {
      company.status = 'REJECTED'
      message.success('审核状态已更新')
    }
  })
}

const viewDetail = (company: any) => {
  router.push(`/admin/companies/${company.id}`)
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleDateString('zh-CN')
}

const fetchCompanies = async () => {
  try {
    const res = await companyApi.getAll()
    companies.value = (res.data || []).map((item: any) => ({
      ...item,
      createdAt: formatDate(item.createdAt)
    }))
  } catch (error) {
    message.error('获取企业列表失败')
  }
}

onMounted(() => {
  fetchCompanies()
})
</script>
