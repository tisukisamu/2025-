<script setup lang="ts">
import { reactive, watch } from 'vue'
import { message } from 'ant-design-vue'
import {
  loadAdminSettings,
  saveAdminSettings,
  defaultAdminSettings,
  type AdminSettings
} from '@/utils/adminSettings'

const settings = reactive<AdminSettings>(loadAdminSettings())

watch(
  () => ({ ...settings }),
  (val) => {
    saveAdminSettings(val as AdminSettings)
  },
  { deep: true }
)

const reset = () => {
  Object.assign(settings, defaultAdminSettings)
  saveAdminSettings(settings)
  message.success('已恢复默认设置')
}
</script>

<template>
  <div class="page">
    <h2 class="title">系统设置</h2>

    <div class="card">
      <h3 class="card-title">外观与布局</h3>

      <div class="row">
        <div class="left">
          <div class="label">侧边栏默认收起</div>
          <div class="desc">进入管理后台时默认使用收起状态</div>
        </div>
        <input type="checkbox" v-model="settings.defaultCollapsed" />
      </div>

      <div class="row">
        <div class="left">
          <div class="label">显示面包屑</div>
          <div class="desc">顶部导航显示当前位置路径</div>
        </div>
        <input type="checkbox" v-model="settings.showBreadcrumb" />
      </div>

      <div class="row">
        <div class="left">
          <div class="label">显示全局搜索</div>
          <div class="desc">顶部显示全局搜索输入框（目前仅 UI）</div>
        </div>
        <input type="checkbox" v-model="settings.showGlobalSearch" />
      </div>

      <div class="row">
        <div class="left">
          <div class="label">主题模式</div>
          <div class="desc">可先保存偏好，后续接入真正 dark 样式</div>
        </div>
        <select v-model="settings.themeMode" class="select">
          <option value="light">浅色</option>
          <option value="dark">深色</option>
          <option value="system">跟随系统</option>
        </select>
      </div>

      <button class="btn" @click="reset">恢复默认</button>
    </div>

    <div class="hint">
      提示：设置会自动保存到本地（localStorage），刷新页面仍然生效。
    </div>
  </div>
</template>

<style scoped>
.page { max-width: 980px; margin: 0 auto; }
.title { font-size: 20px; font-weight: 800; margin-bottom: 16px; }
.card {
  background: #fff;
  border: 1px solid var(--gray-200);
  border-radius: 14px;
  padding: 16px;
}
.card-title { font-size: 15px; font-weight: 800; margin: 0 0 12px; }
.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--gray-100);
}
.row:last-of-type { border-bottom: none; }
.left { display: flex; flex-direction: column; gap: 4px; }
.label { font-weight: 800; color: var(--gray-900); }
.desc { font-size: 12px; color: var(--gray-500); }
.select {
  height: 36px;
  border-radius: 10px;
  border: 1px solid var(--gray-200);
  padding: 0 10px;
}
.btn {
  margin-top: 12px;
  height: 38px;
  padding: 0 14px;
  border-radius: 10px;
  border: 1px solid var(--gray-200);
  background: #111827;
  color: #fff;
  cursor: pointer;
  font-weight: 700;
}
.hint { margin-top: 12px; font-size: 13px; color: var(--gray-500); }
</style>
