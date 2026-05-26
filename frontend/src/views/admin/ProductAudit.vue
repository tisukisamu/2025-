<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { auditApi } from '../../api'
import {
  CheckOutlined,
  CloseOutlined,
  EyeOutlined
} from '@ant-design/icons-vue'

const products = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({
  page: 1,
  size: 10,
  status: 0 // 默认看待审核
})
const auditVisible = ref(false)
const currentProduct = ref<any>({})
const auditForm = ref({
  status: 1,
  reason: ''
})

const statusMap = {
  0: { text: '待审核', color: 'warning' },
  1: { text: '已通过', color: 'success' },
  2: { text: '已驳回', color: 'error' }
}

onMounted(() => {
  loadProducts()
})

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await auditApi.getProducts(queryParams.value.status, queryParams.value.page - 1, queryParams.value.size)
    products.value = res.content
    total.value = res.totalElements
  } catch (error) {
    message.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleTableChange = (pagination: any) => {
  queryParams.value.page = pagination.current
  queryParams.value.size = pagination.pageSize
  loadProducts()
}

const handleAudit = (record: any) => {
  currentProduct.value = record
  auditForm.value = { status: 1, reason: '' }
  auditVisible.value = true
}

const submitAudit = async () => {
  try {
    const pass = auditForm.value.status === 1
    await auditApi.auditProduct(currentProduct.value.id, pass, auditForm.value.reason)
    message.success('审核完成')
    auditVisible.value = false
    loadProducts()
  } catch (error) {
    message.error('审核失败')
  }
}

const formatPrice = (price: number) => `￥${price?.toFixed(2) || '0.00'}`

const columns = [
  { title: '商品图片', dataIndex: 'mainImageUrl', key: 'image', width: 100 },
  { title: '商品名称', dataIndex: 'name', key: 'name' },
  { title: '价格', dataIndex: 'price', key: 'price' },
  { title: '库存', dataIndex: 'stock', key: 'stock' },
  { title: '店铺ID', dataIndex: 'storeId', key: 'storeId' },
  { title: '申请时间', dataIndex: 'createTime', key: 'createTime' },
  { title: '操作', key: 'action', width: 150 }
]
</script>

<template>
  <div class="product-audit">
    <h2>商品审核</h2>

    <a-tabs v-model:activeKey="queryParams.status" @change="() => { queryParams.page = 1; loadProducts() }">
      <a-tab-pane :key="0" tab="待审核" />
      <a-tab-pane :key="1" tab="已通过" />
      <a-tab-pane :key="2" tab="已驳回" />
    </a-tabs>
    
    <a-table
      :columns="columns"
      :data-source="products"
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
        <template v-if="column.key === 'image'">
          <img
            v-if="record.mainImageUrl"
            :src="record.mainImageUrl"
            alt="商品图片"
            style="width: 60px; height: 60px; object-fit: cover; border-radius: 4px;"
          />
          <div v-else class="no-image">暂无图片</div>
        </template>
        
        <template v-if="column.key === 'price'">
          {{ formatPrice(record.price) }}
        </template>
        
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button v-if="record.status === 0" type="primary" size="small" @click="handleAudit(record)">
              <CheckOutlined /> 审核
            </a-button>
            <span v-else-if="record.status === 2" style="color: #ff4d4f; font-size: 12px;">
              驳回原因: {{ record.rejectReason }}
            </span>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 审核弹窗 -->
    <a-modal
      v-model:open="auditVisible"
      title="商品审核"
      ok-text="确定"
      cancel-text="取消"
      @ok="submitAudit"
    >
      <a-form :model="auditForm" layout="vertical">
        <a-form-item label="商品名称">
          <span>{{ currentProduct.name }}</span>
        </a-form-item>
        
        <a-form-item label="商品描述">
          <span>{{ currentProduct.description }}</span>
        </a-form-item>
        
        <a-form-item label="审核结果" required>
          <a-radio-group v-model:value="auditForm.status">
            <a-radio :value="1">通过</a-radio>
            <a-radio :value="2">驳回</a-radio>
          </a-radio-group>
        </a-form-item>
        
        <a-form-item label="审核意见" v-if="auditForm.status === 2">
          <a-textarea
            v-model:value="auditForm.reason"
            :rows="4"
            placeholder="请输入驳回原因"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.product-audit {
  padding: 24px;
}

.product-audit h2 {
  margin-bottom: 24px;
}

.no-image {
  width: 60px;
  height: 60px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 12px;
  border-radius: 4px;
}
</style>
