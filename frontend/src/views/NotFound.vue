<template>
  <div class="not-found-page">
    <div class="content">
      <div class="error-code">404</div>
      <div class="error-title">页面未找到</div>
      <div class="error-description">
        抱歉，您访问的页面不存在或已被移除
      </div>
      <div class="actions">
        <a-button type="primary" size="large" @click="goHome">
          返回首页
        </a-button>
        <a-button size="large" @click="goBack">
          返回上页
        </a-button>
      </div>
    </div>
    <div class="illustration">
      <div class="paw-prints">
        <span class="paw paw-1">🐾</span>
        <span class="paw paw-2">🐾</span>
        <span class="paw paw-3">🐾</span>
        <span class="paw paw-4">🐾</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  position: relative;
  overflow: hidden;
}

.content {
  text-align: center;
  z-index: 1;
}

.error-code {
  font-size: 160px;
  font-weight: 700;
  color: #262626;
  line-height: 1;
  letter-spacing: -8px;
  text-shadow: 4px 4px 0 #d9d9d9;
  margin-bottom: 16px;
}

.error-title {
  font-size: 32px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 12px;
}

.error-description {
  font-size: 16px;
  color: #8c8c8c;
  margin-bottom: 32px;
}

.actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.illustration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.paw-prints {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 400px;
  height: 400px;
}

.paw {
  position: absolute;
  font-size: 48px;
  opacity: 0.1;
  animation: float 3s ease-in-out infinite;
}

.paw-1 {
  top: 0;
  left: 20%;
  animation-delay: 0s;
}

.paw-2 {
  top: 30%;
  right: 10%;
  animation-delay: 0.5s;
}

.paw-3 {
  bottom: 20%;
  left: 10%;
  animation-delay: 1s;
}

.paw-4 {
  bottom: 0;
  right: 20%;
  animation-delay: 1.5s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

@media (max-width: 768px) {
  .error-code {
    font-size: 100px;
    letter-spacing: -4px;
  }

  .error-title {
    font-size: 24px;
  }

  .error-description {
    font-size: 14px;
  }

  .actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>
