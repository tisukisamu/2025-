<template>
  <div class="not-found">
    <div class="not-found-content">
      <h1 class="error-code">404</h1>
      <h2 class="error-title">页面未找到</h2>
      <p class="error-desc">抱歉，您访问的页面不存在或已被移除</p>
      <a-button type="primary" size="large" @click="goHome">
        返回首页
      </a-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
}

.not-found-content {
  text-align: center;
  padding: 40px;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  color: #1890ff;
  margin: 0;
  line-height: 1;
}

.error-title {
  font-size: 28px;
  color: #333;
  margin: 24px 0 16px;
}

.error-desc {
  font-size: 16px;
  color: #666;
  margin-bottom: 32px;
}
</style>
