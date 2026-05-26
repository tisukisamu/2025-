<template>
  <div class="my-products-page">
    <div class="back-nav">
      <a-button type="text" @click="router.back()">
        <left-outlined /> 返回
      </a-button>
    </div>
    <div class="page-header">
      <h1>我的商品</h1>
      <a-button type="primary" @click="router.push('/publish')">
        <plus-outlined />
        发布商品
      </a-button>
    </div>

    <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
      <a-tab-pane key="all" tab="全部" />
      <a-tab-pane key="ON_SALE" tab="在售" />
      <a-tab-pane key="PENDING" tab="审核中" />
      <a-tab-pane key="OFF_SHELF" tab="已下架" />
      <a-tab-pane key="SOLD" tab="已售" />
    </a-tabs>

    <a-spin :spinning="loading">
      <div class="products-grid" v-if="products.length > 0">
        <div v-for="product in products" :key="product.id" class="product-item">
          <product-card :product="product" @click="router.push(`/products/${product.id}`)" />
          <div class="product-actions">
            <a-space>
              <a-button
                v-if="product.status === 'ON_SALE'"
                size="small"
                @click.stop="handleOffShelf(product.id)"
              >
                下架
              </a-button>
              <a-button
                v-if="product.status === 'OFF_SHELF'"
                type="primary"
                size="small"
                @click.stop="handleOnSale(product.id)"
              >
                上架
              </a-button>
              <a-button
                v-if="product.status !== 'SOLD'"
                size="small"
                @click.stop="router.push(`/publish?id=${product.id}`)"
              >
                编辑
              </a-button>
              <a-button
                v-if="product.status !== 'SOLD'"
                danger
                size="small"
                @click.stop="handleDelete(product.id)"
              >
                删除
              </a-button>
            </a-space>
          </div>
        </div>
      </div>
      <a-empty v-else description="暂无商品" />
    </a-spin>

    <div class="pagination-wrapper" v-if="total > 0">
      <a-pagination
        v-model:current="pagination.page"
        v-model:pageSize="pagination.pageSize"
        :total="total"
        show-quick-jumper
        :show-total="(total: number) => `共 ${total} 件商品`"
        @change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { productApi } from '@/api'
import type { Product } from '@/types'
import ProductCard from '@/components/ProductCard.vue'
import { message, Modal } from 'ant-design-vue'
import { PlusOutlined, LeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const loading = ref(false)
const products = ref<Product[]>([])
const total = ref(0)
const activeTab = ref('all')

const pagination = reactive({
  page: 1,
  pageSize: 12
})

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await productApi.getMyProducts(pagination.page, pagination.pageSize)
    let list = res.data.list
    if (activeTab.value !== 'all') {
      list = list.filter((p: Product) => p.status === activeTab.value)
    }
    products.value = list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  pagination.page = 1
  fetchProducts()
}

const handlePageChange = () => {
  fetchProducts()
}

const handleOffShelf = async (id: number) => {
  try {
    await productApi.updateProductStatus(id, 'OFF_SHELF')
    message.success('已下架')
    fetchProducts()
  } catch {
    // ignore
  }
}

const handleOnSale = async (id: number) => {
  try {
    await productApi.updateProductStatus(id, 'ON_SALE')
    message.success('已上架')
    fetchProducts()
  } catch {
    // ignore
  }
}

const handleDelete = (id: number) => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这个商品吗？删除后无法恢复。',
    okText: '删除',
    okType: 'danger',
    cancelText: '取消',
    onOk: async () => {
      await productApi.deleteProduct(id)
      message.success('删除成功')
      fetchProducts()
    }
  })
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.my-products-page {
  background: #f5f5f5;
  min-height: calc(100vh - 200px);
}

.back-nav {
  margin-bottom: 16px;
}

.back-nav :deep(.ant-btn) {
  color: #1890ff;
  padding: 4px 8px;
}

.back-nav :deep(.ant-btn:hover) {
  background: #e6f7ff;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.product-item {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.product-actions {
  padding: 12px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}
</style>
