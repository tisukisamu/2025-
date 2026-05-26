<template>
  <div class="admin-page">
    <div class="back-nav">
      <a-button type="text" @click="router.back()">
        <left-outlined /> 返回
      </a-button>
    </div>
    <div class="page-header">
      <h1>商品管理</h1>
    </div>

    <a-card class="table-card">
      <template #extra>
        <a-space>
          <a-select v-model:value="filterStatus" style="width: 120px" @change="fetchProducts">
            <a-select-option value="">全部状态</a-select-option>
            <a-select-option value="PENDING">待审核</a-select-option>
            <a-select-option value="APPROVED">已通过</a-select-option>
            <a-select-option value="REJECTED">已拒绝</a-select-option>
          </a-select>
        </a-space>
      </template>
      <a-table
        :columns="columns"
        :data-source="products"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'title'">
            <div class="product-cell">
              <img :src="record.coverImage || defaultImage" class="product-thumb" />
              <span>{{ record.title }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'price'">
            <span class="price">¥{{ record.price }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'auditStatus'">
            <a-tag :color="getAuditColor(record.auditStatus)">
              {{ getAuditText(record.auditStatus) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button
                v-if="record.auditStatus === 'PENDING'"
                type="link"
                size="small"
                @click="handleAudit(record, 'APPROVED')"
              >
                通过
              </a-button>
              <a-button
                v-if="record.auditStatus === 'PENDING'"
                type="link"
                danger
                size="small"
                @click="handleAudit(record, 'REJECTED')"
              >
                拒绝
              </a-button>
              <a-button
                type="link"
                size="small"
                @click="router.push(`/products/${record.id}`)"
              >
                查看
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '@/api'
import type { Product } from '@/types'
import { message } from 'ant-design-vue'
import { LeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const loading = ref(false)
const products = ref<Product[]>([])
const filterStatus = ref('')

const defaultImage = '/no-image.svg'

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  onChange: (page: number) => {
    pagination.current = page
    fetchProducts()
  }
})

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '商品', dataIndex: 'title', key: 'title' },
  { title: '价格', dataIndex: 'price', key: 'price', width: 100 },
  { title: '卖家', dataIndex: 'sellerName', key: 'sellerName', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '审核状态', dataIndex: 'auditStatus', key: 'auditStatus', width: 100 },
  { title: '操作', key: 'action', width: 150 }
]

const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    DRAFT: 'default',
    PENDING: 'orange',
    ON_SALE: 'green',
    OFF_SHELF: 'red',
    SOLD: 'gray'
  }
  return colors[status] || 'default'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    DRAFT: '草稿',
    PENDING: '待审核',
    ON_SALE: '在售',
    OFF_SHELF: '已下架',
    SOLD: '已售'
  }
  return texts[status] || status
}

const getAuditColor = (status: string) => {
  const colors: Record<string, string> = {
    PENDING: 'orange',
    APPROVED: 'green',
    REJECTED: 'red'
  }
  return colors[status] || 'default'
}

const getAuditText = (status: string) => {
  const texts: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已拒绝'
  }
  return texts[status] || status
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await adminApi.getProducts(
      pagination.current,
      pagination.pageSize,
      filterStatus.value as Product['auditStatus']
    )
    products.value = res.data.list
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleAudit = async (product: Product, status: Product['auditStatus']) => {
  try {
    await adminApi.auditProduct(product.id, status)
    message.success('审核完成')
    fetchProducts()
  } catch {
    // ignore
  }
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.admin-page {
  min-height: calc(100vh - 200px);
}

.back-nav {
  margin-bottom: 16px;
}

.back-nav :deep(.ant-btn) {
  color: #71717a;
  padding: 4px 8px;
}

.back-nav :deep(.ant-btn:hover) {
  background: #f4f4f5;
  color: #18181b;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #18181b;
}

.table-card {
  border-radius: 12px;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-thumb {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 8px;
}

.price {
  color: #f5222d;
  font-weight: 500;
}
</style>
