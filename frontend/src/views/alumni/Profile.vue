<template>
  <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
    <template #title>我的校友资料</template>
    <a-form layout="vertical">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
        <a-form-item label="学校">
          <a-input v-model:value="profileForm.school" placeholder="例如：XX大学" />
        </a-form-item>
        <a-form-item label="专业">
          <a-input v-model:value="profileForm.major" placeholder="例如：计算机科学" />
        </a-form-item>
        <a-form-item label="毕业年份">
          <a-input-number v-model:value="profileForm.graduationYear" :min="1950" :max="2100" class="w-full" />
        </a-form-item>
        <a-form-item label="所在城市">
          <a-input v-model:value="profileForm.city" placeholder="例如：上海" />
        </a-form-item>
        <a-form-item label="纬度">
          <a-input-number v-model:value="profileForm.latitude" :step="0.000001" class="w-full" />
        </a-form-item>
        <a-form-item label="经度">
          <a-input-number v-model:value="profileForm.longitude" :step="0.000001" class="w-full" />
        </a-form-item>
      </div>
      <a-form-item label="简介">
        <a-textarea v-model:value="profileForm.bio" :auto-size="{ minRows: 2, maxRows: 4 }" :maxlength="200" />
      </a-form-item>
      <a-space class="w-full justify-between">
        <div class="flex items-center gap-2">
          <span class="text-13px text-#6b7280">允许被附近校友发现</span>
          <a-switch v-model:checked="profileForm.openNearby" />
        </div>
        <a-space>
          <a-button class="app-btn-secondary" @click="useCurrentLocation">读取当前位置</a-button>
          <a-button class="app-btn-primary" :loading="savingProfile" @click="saveProfile">保存资料</a-button>
        </a-space>
      </a-space>
    </a-form>
  </a-card>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAlumni } from './useAlumni'

const { savingProfile, profileForm, loadProfile, useCurrentLocation, saveProfile } = useAlumni()

onMounted(loadProfile)
</script>
