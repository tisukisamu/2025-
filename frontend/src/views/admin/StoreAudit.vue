<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { auditApi } from '../../api'
import { CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons-vue'

const loading = ref(false)
const stores = ref([])
const total = ref(0)
const queryParams = ref({
  page: 1,
  size: 10
})
const activeTab = ref('all') // all, pending, passed, rejected

const columns = [
  {
    title: '店铺名称',
    dataIndex: 'storeName',
    key: 'storeName',
  },
  {
    title: '联系人',
    dataIndex: 'userId', // 暂时显示userId，后续可以关联查询用户昵称
    key: 'userId',
  },
  {
    title: '电话',
    dataIndex: 'phone',
    key: 'phone',
  },
  {
    title: '地址',
    dataIndex: 'address',
    key: 'address',
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
  },
  {
    title: '申请时间',
    dataIndex: 'createTime',
    key: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

const loadStores = async () => {
  loading.value = true
  try {
    let status = undefined
    if (activeTab.value === 'pending') status = 0
    else if (activeTab.value === 'passed') status = 1
    else if (activeTab.value === 'rejected') status = 2
    
    const res = await auditApi.getStores(status, queryParams.value.page - 1, queryParams.value.size)
    stores.value = res.content
    total.value = res.totalElements
  } catch (error) {
    message.error('加载店铺列表失败')
  } finally {
    loading.value = false
  }
}

const handleTableChange = (pagination: any) => {
  queryParams.value.page = pagination.current
  queryParams.value.size = pagination.pageSize
  loadStores()
}

const handleTabChange = (key: string) => {
  activeTab.value = key
  queryParams.value.page = 1
  loadStores()
}

// 审核通过
const handleApprove = (store: any) => {
  Modal.confirm({
    title: '确认通过审核?',
    content: `确定要批准店铺 "${store.storeName}" 入驻吗？`,
    onOk: async () => {
      try {
        await auditApi.auditStore(store.id, true)
        message.success('审核通过')
        loadStores()
      } catch (error) {
        message.error('操作失败')
      }
    }
  })
}

// 驳回弹窗
const rejectModalVisible = ref(false)
const rejectReason = ref('')
const currentStoreId = ref<number | null>(null)

const showRejectModal = (storeId: number) => {
  currentStoreId.value = storeId
  rejectReason.value = ''
  rejectModalVisible.value = true
}

const handleReject = async () => {
  if (!rejectReason.value) {
    message.warning('请输入驳回原因')
    return
  }
  
  try {
    await auditApi.auditStore(currentStoreId.value!, false, rejectReason.value)
    message.success('已驳回申请')
    rejectModalVisible.value = false
    loadStores()
  } catch (error) {
    message.error('操作失败')
  }
}

const getStatusText = (status: number) => {
  switch (status) {
    case 0: return '待审核'
    case 1: return '已通过'
    case 2: return '已驳回'
    case 3: return '已禁用'
    default: return '未知'
  }
}

const getStatusColor = (status: number) => {
  switch (status) {
    case 0: return 'orange'
    case 1: return 'green'
    case 2: return 'red'
    case 3: return 'default'
    default: return 'default'
  }
}

onMounted(() => {
  loadStores()
})
</script>

<template>
  <div class="store-audit-page">
    <div class="page-header">
      <h2 class="page-title">店铺入驻审核</h2>
    </div>

    <a-card :bordered="false" class="main-card">
      <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
        <a-tab-pane key="all" tab="全部" />
        <a-tab-pane key="pending" tab="待审核" />
        <a-tab-pane key="passed" tab="已通过" />
        <a-tab-pane key="rejected" tab="已驳回" />
      </a-tabs>

      <a-table
        :columns="columns"
        :data-source="stores"
        :loading="loading"
        row-key="id"
        :pagination="{
          current: queryParams.page,
          pageSize: queryParams.size,
          total: total,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`
        }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          
          <template v-if="column.key === 'createTime'">
            {{ new Date(record.createTime).toLocaleString() }}
          </template>

          <template v-if="column.key === 'action'">
            <div v-if="record.status === 0" class="actions">
              <a-button type="link" class="success-text" @click="handleApprove(record)">
                通过
              </a-button>
              <a-divider type="vertical" />
              <a-button type="link" danger @click="showRejectModal(record.id)">
                驳回
              </a-button>
            </div>
            <div v-else-if="record.status === 2">
              <span class="reason-text">驳回原因: {{ record.rejectReason }}</span>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 驳回弹窗 -->
    <a-modal
      v-model:visible="rejectModalVisible"
      title="驳回申请"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleReject"
    >
      <a-form layout="vertical">
        <a-form-item label="驳回原因" required>
          <a-textarea
            v-model:value="rejectReason"
            :rows="4"
            placeholder="请输入具体的驳回原因，以便商家修改"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.main-card {
  border-radius: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.actions {
  display: flex;
  align-items: center;
}

.success-text {
  color: #10b981;
}

.reason-text {
  color: #ef4444;
  font-size: 12px;
}
</style>