<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-#111111 text-white p-5">
      <h1 class="text-22px m-0">设置中心</h1>
      <p class="m-0 mt-2 text-#9ca3af">管理展示偏好、提醒开关与数据操作</p>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl" title="界面偏好">
      <a-form layout="vertical">
        <a-row :gutter="20">
          <a-col :span="8">
            <a-form-item label="界面主题">
              <a-select v-model:value="settings.themeMode">
                <a-select-option value="dark-gray">黑灰白简约</a-select-option>
                <a-select-option value="light">浅色高亮</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="周起始日">
              <a-select v-model:value="settings.weekStart">
                <a-select-option value="MONDAY">周一</a-select-option>
                <a-select-option value="SUNDAY">周日</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="页面密度">
              <a-select v-model:value="settings.density">
                <a-select-option value="comfortable">舒适</a-select-option>
                <a-select-option value="compact">紧凑</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl" title="提醒设置">
      <a-space direction="vertical" class="w-full">
        <div class="flex items-center justify-between">
          <span>每日打卡提醒</span>
          <a-switch v-model:checked="settings.dailyReminder" />
        </div>
        <div class="flex items-center justify-between">
          <span>周报通知</span>
          <a-switch v-model:checked="settings.weeklyReport" />
        </div>
        <div class="flex items-center justify-between">
          <span>成就提示</span>
          <a-switch v-model:checked="settings.achievementTips" />
        </div>
      </a-space>
    </a-card>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl" title="数据操作">
      <a-space>
        <a-button type="primary" class="bg-black border-black text-white" @click="saveSettings">保存设置</a-button>
        <a-button @click="exportSettings">导出设置</a-button>
        <a-button danger @click="resetSettings">恢复默认</a-button>
      </a-space>
      <div class="mt-4 text-#6b7280 text-13px">设置仅保存在当前浏览器，本地存储键：habitflow:settings</div>
    </a-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { message } from 'ant-design-vue'

const storageKey = 'habitflow:settings'
const defaultSettings = {
  themeMode: 'dark-gray',
  weekStart: 'MONDAY',
  density: 'comfortable',
  dailyReminder: true,
  weeklyReport: true,
  achievementTips: true
}

const settings = reactive(loadSettings())

function loadSettings() {
  try {
    const raw = localStorage.getItem(storageKey)
    if (!raw) return { ...defaultSettings }
    return { ...defaultSettings, ...JSON.parse(raw) }
  } catch {
    return { ...defaultSettings }
  }
}

function saveSettings() {
  localStorage.setItem(storageKey, JSON.stringify(settings))
  message.success('设置已保存')
}

function resetSettings() {
  Object.assign(settings, defaultSettings)
  localStorage.setItem(storageKey, JSON.stringify(settings))
  message.success('已恢复默认设置')
}

function exportSettings() {
  const data = JSON.stringify(settings, null, 2)
  const blob = new Blob([data], { type: 'application/json;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'habitflow-settings.json'
  a.click()
  URL.revokeObjectURL(url)
  message.success('设置导出成功')
}
</script>
