<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  BarChartOutlined,
  LineChartOutlined,
  PieChartOutlined
} from '@ant-design/icons-vue'

const statsData = ref({
  salesTrend: [],
  categoryStats: [],
  hotProducts: []
})
const loading = ref(false)
const dateRange = ref('7days')

onMounted(() => {
  loadStats()
})

const loadStats = async () => {
  loading.value = true
  try {
    // TODO: 调用 API 获取统计数据
    statsData.value = {
      salesTrend: [],
      categoryStats: [],
      hotProducts: []
    }
  } catch (error) {
    message.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="store-stats">
    <h2>数据统计</h2>
    
    <a-card class="stats-filter">
      <a-radio-group v-model:value="dateRange" @change="loadStats">
        <a-radio-button value="7days">近7天</a-radio-button>
        <a-radio-button value="30days">近30天</a-radio-button>
        <a-radio-button value="90days">近90天</a-radio-button>
      </a-radio-group>
    </a-card>

    <a-row :gutter="16" class="stats-content">
      <a-col :span="12">
        <a-card title="销售趋势" :loading="loading">
          <div class="chart-placeholder">
            <LineChartOutlined style="font-size: 48px; color: #ccc;" />
            <p>销售趋势图表</p>
          </div>
        </a-card>
      </a-col>
      
      <a-col :span="12">
        <a-card title="分类占比" :loading="loading">
          <div class="chart-placeholder">
            <PieChartOutlined style="font-size: 48px; color: #ccc;" />
            <p>分类占比图表</p>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-card title="热销商品TOP10" class="hot-products" :loading="loading">
      <a-table :dataSource="statsData.hotProducts" :pagination="false">
        <a-table-column title="排名" dataIndex="rank" key="rank" />
        <a-table-column title="商品名称" dataIndex="name" key="name" />
        <a-table-column title="销量" dataIndex="sales" key="sales" />
        <a-table-column title="销售额" dataIndex="revenue" key="revenue" />
      </a-table>
    </a-card>
  </div>
</template>

<style scoped>
.store-stats {
  padding: 24px;
}

.store-stats h2 {
  margin-bottom: 24px;
}

.stats-filter {
  margin-bottom: 24px;
}

.stats-content {
  margin-bottom: 24px;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 8px;
}

.chart-placeholder p {
  margin-top: 16px;
  color: #999;
}

.hot-products {
  margin-top: 24px;
}
</style>
