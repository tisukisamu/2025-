<template>
  <div class="appointment-manage-page">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">预约管理</h1>
        <p class="page-subtitle">管理所有服务预约订单</p>
      </div>
      <a-button @click="goBack">返回后台</a-button>
    </div>

    <div class="filter-bar">
      <a-space>
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索订单号/联系人"
          style="width: 250px"
          @search="handleSearch"
        />
        <a-select v-model:value="statusFilter" placeholder="状态筛选" style="width: 140px" allowClear @change="handleFilter">
          <a-select-option value="pending">待确认</a-select-option>
          <a-select-option value="confirmed">已确认</a-select-option>
          <a-select-option value="processing">进行中</a-select-option>
          <a-select-option value="completed">已完成</a-select-option>
          <a-select-option value="cancelled">已取消</a-select-option>
        </a-select>
        <a-range-picker v-model:value="dateRange" @change="handleFilter" />
      </a-space>
    </div>

    <a-table
      :columns="columns"
      :data-source="filteredAppointments"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      row-key="id"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'orderNo'">
          <span class="order-no">{{ record.orderNo }}</span>
        </template>
        <template v-if="column.key === 'petInfo'">
          <div class="pet-info">
            <span class="pet-name">{{ record.petName || '-' }}</span>
            <span class="pet-type">{{ record.petType || '' }}</span>
          </div>
        </template>
        <template v-if="column.key === 'packageInfo'">
          <div class="package-info">
            <span class="package-name">{{ record.packageName || '-' }}</span>
            <span class="package-price">¥{{ record.packagePrice || 0 }}</span>
          </div>
        </template>
        <template v-if="column.key === 'contactInfo'">
          <div class="contact-info">
            <div>{{ record.contactName }}</div>
            <div class="phone">{{ record.contactPhone }}</div>
          </div>
        </template>
        <template v-if="column.key === 'appointmentTime'">
          {{ formatDateTime(record.appointmentTime) }}
        </template>
        <template v-if="column.key === 'status'">
          <a-tag :color="getStatusColor(record.status)">
            {{ getStatusText(record.status) }}
          </a-tag>
        </template>
        <template v-if="column.key === 'createdAt'">
          {{ formatDate(record.createdAt) }}
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="showDetailModal(record)">
              详情
            </a-button>
            <a-dropdown v-if="record.status !== 'completed' && record.status !== 'cancelled'">
              <a-button type="link" size="small">
                状态 <DownOutlined />
              </a-button>
              <template #overlay>
                <a-menu @click="({ key }) => handleStatusChange(record, key)">
                  <a-menu-item key="confirmed" v-if="record.status === 'pending'">确认预约</a-menu-item>
                  <a-menu-item key="processing" v-if="record.status === 'confirmed'">开始服务</a-menu-item>
                  <a-menu-item key="completed" v-if="record.status === 'processing'">完成服务</a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="cancelled" danger>取消预约</a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="detailModalVisible"
      title="预约详情"
      :footer="null"
      width="600px"
    >
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="订单号" :span="2">
          {{ currentAppointment?.orderNo }}
        </a-descriptions-item>
        <a-descriptions-item label="宠物名称">
          {{ currentAppointment?.petName || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="宠物类型">
          {{ currentAppointment?.petType || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="服务套餐">
          {{ currentAppointment?.packageName || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="套餐价格">
          ¥{{ currentAppointment?.packagePrice || 0 }}
        </a-descriptions-item>
        <a-descriptions-item label="联系人">
          {{ currentAppointment?.contactName }}
        </a-descriptions-item>
        <a-descriptions-item label="联系电话">
          {{ currentAppointment?.contactPhone }}
        </a-descriptions-item>
        <a-descriptions-item label="预约时间" :span="2">
          {{ formatDateTime(currentAppointment?.appointmentTime) }}
        </a-descriptions-item>
        <a-descriptions-item label="服务地址" :span="2">
          {{ currentAppointment?.address || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="订单状态">
          <a-tag :color="getStatusColor(currentAppointment?.status)">
            {{ getStatusText(currentAppointment?.status) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">
          {{ formatDateTime(currentAppointment?.createdAt) }}
        </a-descriptions-item>
        <a-descriptions-item label="备注" :span="2">
          {{ currentAppointment?.remark || '无' }}
        </a-descriptions-item>
      </a-descriptions>

      <div class="modal-actions" v-if="currentAppointment?.status !== 'completed' && currentAppointment?.status !== 'cancelled'">
        <a-button @click="handleStatusChange(currentAppointment, 'cancelled')" danger>
          取消预约
        </a-button>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { DownOutlined } from '@ant-design/icons-vue'
import { getAllAppointments, updateAppointmentStatus, cancelAppointment } from '../../api/appointment'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const detailModalVisible = ref(false)
const searchKeyword = ref('')
const statusFilter = ref(undefined)
const dateRange = ref([])
const appointments = ref([])
const currentAppointment = ref(null)

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条`
})

const columns = [
  { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 160 },
  { title: '宠物信息', key: 'petInfo', width: 120 },
  { title: '服务套餐', key: 'packageInfo', width: 150 },
  { title: '联系信息', key: 'contactInfo', width: 120 },
  { title: '预约时间', dataIndex: 'appointmentTime', key: 'appointmentTime', width: 150 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 120 },
  { title: '操作', key: 'action', width: 140 }
]

const filteredAppointments = computed(() => {
  let result = appointments.value
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(item => 
      item.orderNo?.toLowerCase().includes(keyword) ||
      item.contactName?.toLowerCase().includes(keyword)
    )
  }
  
  if (statusFilter.value) {
    result = result.filter(item => item.status === statusFilter.value)
  }
  
  if (dateRange.value && dateRange.value.length === 2) {
    const [start, end] = dateRange.value
    result = result.filter(item => {
      const date = dayjs(item.appointmentTime)
      return date.isAfter(start) && date.isBefore(end.add(1, 'day'))
    })
  }
  
  return result
})

const formatDate = (date) => date ? dayjs(date).format('YYYY-MM-DD') : '-'
const formatDateTime = (date) => date ? dayjs(date).format('YYYY-MM-DD HH:mm') : '-'

const goBack = () => {
  router.push('/admin')
}

const getStatusColor = (status) => {
  const colors = {
    pending: 'orange',
    confirmed: 'blue',
    processing: 'cyan',
    completed: 'green',
    cancelled: 'red'
  }
  return colors[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    pending: '待确认',
    confirmed: '已确认',
    processing: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return texts[status] || status
}

const loadAppointments = async () => {
  console.log('loadAppointments called, statusFilter:', statusFilter.value)
  loading.value = true
  try {
    const params = { status: statusFilter.value }
    console.log('Calling getAllAppointments with params:', params)
    const res = await getAllAppointments(params)
    console.log('getAllAppointments response:', res)
    if (res.code === 200) {
      const payload = res.data
      console.log('Response payload:', payload)
      if (payload && Array.isArray(payload.list)) {
        appointments.value = payload.list
        pagination.total = payload.total || payload.list.length
      } else if (Array.isArray(payload)) {
        appointments.value = payload
        pagination.total = payload.length
      } else {
        appointments.value = []
        pagination.total = 0
      }
      console.log('Appointments loaded:', appointments.value.length, 'total:', pagination.total)
    }
  } catch (error) {
    console.error('加载预约列表失败:', error)
    message.error('加载预约列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadAppointments()
}

const handleFilter = () => {
  pagination.current = 1
  loadAppointments()
}

const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
}

const showDetailModal = (record) => {
  currentAppointment.value = record
  detailModalVisible.value = true
}

const handleStatusChange = async (record, status) => {
  try {
    if (status === 'cancelled') {
      await cancelAppointment(record.id)
    } else {
      await updateAppointmentStatus(record.id, status)
    }
    message.success('状态更新成功')
    detailModalVisible.value = false
    loadAppointments()
  } catch (error) {
    console.error('更新状态失败:', error)
    message.error('更新失败')
  }
}

onMounted(() => {
  console.log('AppointmentManage mounted')
  loadAppointments()
})
</script>

<style scoped>
.appointment-manage-page {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
}

.page-header {
  margin-bottom: 24px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #262626;
  margin: 0;
}

.page-subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 4px 0 0;
}

.filter-bar {
  margin-bottom: 24px;
}

.order-no {
  font-family: monospace;
  color: #262626;
  font-weight: 500;
}

.pet-info,
.package-info,
.contact-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.pet-name,
.package-name {
  color: #262626;
  font-weight: 500;
}

.pet-type {
  font-size: 12px;
  color: #8c8c8c;
}

.package-price {
  font-size: 12px;
  color: #ff4d4f;
}

.phone {
  font-size: 12px;
  color: #8c8c8c;
}

.modal-actions {
  margin-top: 24px;
  text-align: right;
}
</style>
