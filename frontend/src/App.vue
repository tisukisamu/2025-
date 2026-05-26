<template>
  <a-config-provider :theme="themeConfig">
    <router-view />
  </a-config-provider>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const themeConfig = computed(() => ({
  token: {
    colorPrimary: '#111111',
    colorSuccess: '#22c55e',
    colorWarning: '#f59e0b',
    colorError: '#ef4444',
    colorInfo: '#6b7280',
    borderRadius: 8,
    fontSize: 14,
    colorBgContainer: '#ffffff',
    colorBgLayout: '#f9fafb',
    colorText: '#111827',
    colorTextSecondary: '#6b7280',
    colorBorder: '#e5e7eb',
  },
  components: {
    Button: {
      controlHeight: 40,
      primaryColor: '#111111',
      primaryColorHover: '#374151',
      primaryColorActive: '#000000',
      primaryTextColor: '#ffffff',
      primaryTextColorHover: '#ffffff',
      primaryTextColorActive: '#ffffff',
      defaultColor: '#374151',
      defaultBorderColor: '#e5e7eb',
      defaultBg: '#ffffff',
      defaultColorDisabled: '#d1d5db',
      defaultShadow: 'none',
      primaryShadow: 'none',
    },
    Card: {
      borderRadiusLG: 12,
    },
    Menu: {
      darkItemBg: '#111111',
      darkItemSelectedBg: '#1f2937',
      darkItemColor: '#d1d5db',
      darkItemHoverBg: '#374151',
    },
    Layout: {
      headerBg: '#111111',
      siderBg: '#111111',
      triggerBg: '#1f2937',
    },
  },
}))
</script>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f9fafb;
}

* {
  scrollbar-width: thin;
  scrollbar-color: #d1d5db transparent;
}

*::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

*::-webkit-scrollbar-track {
  background: transparent;
}

*::-webkit-scrollbar-thumb {
  background-color: #d1d5db;
  border-radius: 3px;
}

*::-webkit-scrollbar-thumb:hover {
  background-color: #9ca3af;
}
</style>
