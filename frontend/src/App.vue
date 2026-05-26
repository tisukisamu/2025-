<template>
  <a-config-provider :theme="themeConfig" :locale="zhCN">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </a-config-provider>
</template>

<script setup>
import zhCN from 'ant-design-vue/es/locale/zh_CN'

const themeConfig = {
  token: {
    colorPrimary: '#111111',
    colorInfo: '#111111',
    colorSuccess: '#3f3f3f',
    colorWarning: '#595959',
    colorError: '#1f1f1f',
    colorTextBase: '#1f1f1f',
    colorBgBase: '#ffffff',
    colorBorder: '#d9d9d9',
    borderRadius: 10,
    wireframe: false
  },
  components: {
    Button: {
      colorPrimary: '#111111',
      colorPrimaryHover: '#2b2b2b',
      colorPrimaryActive: '#000000',
      primaryColor: '#ffffff',
      colorTextLightSolid: '#ffffff',
      defaultColor: '#1f1f1f',
      defaultBg: '#ffffff',
      defaultBorderColor: '#d0d0d0',
      defaultHoverColor: '#111111',
      defaultHoverBorderColor: '#111111',
      defaultActiveColor: '#000000',
      defaultActiveBorderColor: '#000000',
      contentFontSize: 14
    }
  }
}
</script>

<style>
body {
  margin: 0;
  background: #f5f5f5;
  color: #1f1f1f;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

#app {
  min-height: 100vh;
}

.ant-btn {
  font-weight: 500;
}

.ant-btn .anticon {
  color: inherit;
}

.ant-btn.ant-btn-color-default.ant-btn-variant-solid,
.ant-btn:not(.ant-btn-primary):not(.ant-btn-link):not(.ant-btn-text):not(.ant-btn-dashed) {
  color: #1f1f1f;
  background: #ffffff;
  border-color: #d0d0d0;
}

.ant-btn.ant-btn-color-default.ant-btn-variant-solid:hover,
.ant-btn.ant-btn-color-default.ant-btn-variant-solid:focus {
  color: #111111;
  background: #ffffff;
  border-color: #111111;
}

.ant-btn-primary,
.ant-btn.ant-btn-color-primary.ant-btn-variant-solid {
  color: #ffffff;
  background: #111111;
  border-color: #111111;
}

.ant-btn-primary:hover,
.ant-btn-primary:focus {
  color: #ffffff;
  background: #2b2b2b;
  border-color: #2b2b2b;
}

.ant-btn-primary.ant-btn-background-ghost {
  color: #111111;
  background: #ffffff;
  border-color: #111111;
}

.ant-btn.ant-btn-dangerous.ant-btn-variant-solid,
.ant-btn.ant-btn-dangerous.ant-btn-variant-filled {
  color: #ffffff;
}

.ant-btn-link:not(.ant-btn-dangerous),
.ant-btn-text:not(.ant-btn-dangerous) {
  color: #111111;
}

.ant-btn-link:not(.ant-btn-dangerous):hover,
.ant-btn-text:not(.ant-btn-dangerous):hover {
  color: #2b2b2b;
}

.ant-btn.ant-btn-disabled,
.ant-btn[disabled] {
  color: rgba(0, 0, 0, 0.35);
  background: #f5f5f5;
  border-color: #d9d9d9;
}

img,
.ant-image-img {
  display: block;
  margin-left: auto;
  margin-right: auto;
  max-width: 100%;
  object-position: center center;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
