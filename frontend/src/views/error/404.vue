<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">404</h1>
      <p class="page-subtitle">页面不存在</p>
    </div>
    <div class="error-content">
      <a-result
        status="404"
        title="404"
        sub-title="抱歉，您访问的页面不存在"
      >
        <template #extra>
          <a-button type="primary" @click="router.push('/')">返回首页</a-button>
        </template>
      </a-result>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.error-content {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}
</style>
