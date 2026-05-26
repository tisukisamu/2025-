<template>
  <div class="admin-dashboard">
    <div class="page-header">
      <h1>管理后台</h1>
    </div>

    <a-row :gutter="[16, 16]">
      <a-col :xs="12" :sm="6">
        <a-card class="stat-card">
          <a-statistic title="用户总数" :value="stats.totalUsers">
            <template #prefix>
              <team-outlined class="stat-icon" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card class="stat-card">
          <a-statistic title="商品总数" :value="stats.totalProducts">
            <template #prefix>
              <appstore-outlined class="stat-icon" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card class="stat-card">
          <a-statistic title="订单总数" :value="stats.totalOrders">
            <template #prefix>
              <shopping-outlined class="stat-icon" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card class="stat-card">
          <a-statistic title="待审核商品" :value="stats.pendingProducts" :value-style="{ color: '#faad14' }">
            <template #prefix>
              <clock-circle-outlined class="stat-icon warning" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[16, 16]" class="mt-16">
      <a-col :xs="24" :lg="12">
        <a-card title="快捷操作" class="action-card">
          <a-row :gutter="[16, 16]">
            <a-col :span="12">
              <a-button block size="large" @click="router.push('/admin/users')">
                <user-outlined />
                用户管理
              </a-button>
            </a-col>
            <a-col :span="12">
              <a-button block size="large" @click="router.push('/admin/products')">
                <appstore-outlined />
                商品管理
              </a-button>
            </a-col>
            <a-col :span="12">
              <a-button block size="large" @click="router.push('/admin/orders')">
                <shopping-outlined />
                订单管理
              </a-button>
            </a-col>
            <a-col :span="12">
              <a-button block size="large" @click="fetchStats">
                <reload-outlined />
                刷新数据
              </a-button>
            </a-col>
          </a-row>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="12">
        <a-card title="待处理事项" class="todo-card">
          <a-list :data-source="todoItems">
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta :title="item.title" :description="item.description" />
                <template #actions>
                  <a-button type="link" @click="handleTodo(item)">处理</a-button>
                </template>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '@/api'
import type { DashboardStats } from '@/types'
import {
  TeamOutlined,
  AppstoreOutlined,
  ShoppingOutlined,
  ClockCircleOutlined,
  UserOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'

const router = useRouter()

const stats = reactive<DashboardStats>({
  totalUsers: 0,
  totalProducts: 0,
  pendingProducts: 0,
  totalOrders: 0,
  pendingOrders: 0,
  completedOrders: 0
})

const todoItems = ref([
  { title: '待审核商品', description: '有新商品等待审核', action: 'products' },
  { title: '待处理订单', description: '有订单需要处理', action: 'orders' }
])

const fetchStats = async () => {
  try {
    const res = await adminApi.getDashboardStats()
    Object.assign(stats, res.data)
  } catch {
    // ignore
  }
}

const handleTodo = (item: any) => {
  router.push(`/admin/${item.action}`)
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.admin-dashboard {
  min-height: calc(100vh - 200px);
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

.stat-card {
  border-radius: 12px;
}

.stat-icon {
  font-size: 24px;
  color: #18181b;
}

.stat-icon.warning {
  color: #f59e0b;
}

.action-card,
.todo-card {
  border-radius: 12px;
}

.mt-16 {
  margin-top: 16px;
}

@media (max-width: 768px) {
  .stat-card :deep(.ant-statistic-title) {
    font-size: 12px;
  }

  .stat-card :deep(.ant-statistic-content-value) {
    font-size: 20px;
  }
}
</style>
