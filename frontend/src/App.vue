<template>
  <a-config-provider :theme="themeConfig" :locale="locale">
    <router-view />
  </a-config-provider>
</template>

<script setup lang="ts">
import { theme } from 'ant-design-vue'
import zhCN from 'ant-design-vue/es/locale/zh_CN'

const locale = zhCN

const themeConfig = {
  algorithm: theme.defaultAlgorithm,
  token: {
    colorPrimary: '#111111',
    colorSuccess: '#52c41a',
    colorWarning: '#faad14',
    colorError: '#f5222d',
    borderRadius: 10,
    borderRadiusLG: 12,
    colorBgLayout: '#f2f2f2',
    colorBgContainer: '#ffffff',
    colorBorder: '#d8d8d8',
    colorText: '#1f1f1f',
    colorTextSecondary: '#6b6b6b'
  },
  components: {
    Card: {
      paddingLG: 20
    },
    Table: {
      headerBg: '#f7f7f7',
      headerColor: '#1f1f1f'
    },
    Menu: {
      darkItemBg: '#141414',
      darkSubMenuItemBg: '#1b1b1b',
      darkItemSelectedBg: '#303030'
    }
  }
}
</script>

<style>
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>
