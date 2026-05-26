<template>
  <a-config-provider :theme="themeConfig">
    <router-view />
  </a-config-provider>
</template>

<script setup>
import { computed } from 'vue'

const themeConfig = computed(() => ({
  token: {
    colorPrimary: '#262626',
    colorSuccess: '#52c41a',
    colorWarning: '#faad14',
    colorError: '#ff4d4f',
    colorInfo: '#1890ff',
    borderRadius: 8,
    fontSize: 14,
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", sans-serif',
    colorText: '#262626',
    colorBgContainer: '#ffffff',
  },
  components: {
    Button: {
      primaryShadow: 'none',
      defaultShadow: 'none',
      primaryColor: '#ffffff',
      defaultColor: '#262626',
      defaultBg: '#ffffff',
      defaultBorderColor: '#d9d9d9',
    },
    Card: {
      boxShadow: '0 1px 2px 0 rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02)',
    },
    Table: {
      headerBg: '#f5f5f5',
      headerColor: '#262626',
    },
    Menu: {
      darkItemBg: '#262626',
      darkItemSelectedBg: '#595959',
    }
  }
}))
</script>

<style>
@import './assets/styles/main.css';

#app {
  min-height: 100vh;
}
</style>
