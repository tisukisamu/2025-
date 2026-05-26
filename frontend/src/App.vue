<template>
  <a-config-provider :theme="themeConfig">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </a-config-provider>
</template>

<script setup>
const themeConfig = {
  token: {
    colorPrimary: '#1f2a44',
    colorInfo: '#2f4f75',
    colorSuccess: '#2f6a5a',
    colorWarning: '#8a6d3b',
    colorError: '#8a3f3f',
    colorTextBase: '#111111',
    colorBgBase: '#ffffff',
    colorBorder: '#d9d9d9',
    colorPrimaryBorder: '#d9d9d9',
    colorPrimaryHover: '#2a3a5f',
    controlOutline: 'rgba(47, 79, 117, 0.12)',
    borderRadius: 10,
    fontSize: 14
  },
  components: {
    Layout: {
      bodyBg: '#f5f5f5',
      headerBg: '#ffffff',
      footerBg: '#f5f5f5'
    },
    Menu: {
      itemBg: 'transparent',
      itemColor: '#595959',
      itemHoverColor: '#111111',
      itemSelectedColor: '#ffffff',
      itemSelectedBg: '#1f2a44'
    },
    Card: {
      borderRadiusLG: 14,
      colorBorderSecondary: '#ebebeb'
    },
    Button: {
      borderRadius: 10,
      defaultBorderColor: '#d9d9d9',
      defaultColor: '#262626',
      defaultHoverColor: '#111111',
      defaultHoverBorderColor: '#bfbfbf',
      primaryColor: '#ffffff'
    },
    Input: {
      hoverBorderColor: '#cfcfcf',
      activeBorderColor: '#cfcfcf',
      activeShadow: '0 0 0 2px rgba(0, 0, 0, 0.04)'
    },
    Select: {
      hoverBorderColor: '#cfcfcf',
      activeBorderColor: '#cfcfcf',
      activeOutlineColor: 'rgba(0, 0, 0, 0.04)'
    }
  }
}
</script>

<style>
html,
body {
  background: linear-gradient(180deg, #f5f7fb 0%, #f3f5f8 100%);
  color: #111111;
}

#app {
  min-height: 100vh;
}

.ant-card {
  border-color: #ededed;
  box-shadow: 0 4px 18px rgba(20, 30, 48, 0.06);
}

.ant-card-head {
  border-bottom-color: #edf1f6 !important;
}

.ant-card-head-title {
  color: #1f2a44;
}

.ant-btn {
  font-weight: 500;
}

.ant-btn-primary {
  box-shadow: none;
  color: #ffffff !important;
  background: #1f2a44 !important;
  border-color: #1f2a44 !important;
}

.ant-btn-primary:hover,
.ant-btn-primary:focus {
  color: #ffffff !important;
  background: #2a3a5f !important;
  border-color: #2a3a5f !important;
}

.ant-btn-primary.ant-btn-dangerous {
  background: #8a3f3f !important;
  border-color: #8a3f3f !important;
}

.ant-btn-default,
.ant-btn-dashed {
  color: #262626 !important;
  background: #ffffff !important;
  border-color: #d9d9d9 !important;
}

.ant-btn-default:hover,
.ant-btn-default:focus,
.ant-btn-dashed:hover,
.ant-btn-dashed:focus {
  color: #111111 !important;
  border-color: #bfbfbf !important;
}

.ant-btn-link,
.ant-btn-text {
  color: #2f4f75 !important;
}

.ant-btn-link:hover,
.ant-btn-link:focus,
.ant-btn-text:hover,
.ant-btn-text:focus {
  color: #1f2a44 !important;
}

.ant-btn[disabled],
.ant-btn-disabled {
  color: #a6a6a6 !important;
  background: #f5f5f5 !important;
  border-color: #e8e8e8 !important;
}

.ant-input:focus,
.ant-input-focused,
.ant-select-focused .ant-select-selector,
.ant-picker-focused {
  border-color: #b8c7da !important;
  box-shadow: 0 0 0 2px rgba(47, 79, 117, 0.12) !important;
}

.ant-menu-horizontal > .ant-menu-item-selected::after,
.ant-menu-horizontal > .ant-menu-submenu-selected::after {
  border-bottom-color: #4d5f7f !important;
}

.ant-table-thead > tr > th {
  background: #f7f9fc !important;
  color: #334155 !important;
}

.ant-table-tbody > tr:hover > td {
  background: #f9fbff !important;
}

.hero-bg,
.header-bg,
.login-left,
.register-left {
  background: linear-gradient(135deg, #1f2a44 0%, #2a3a5f 48%, #4d5f7f 100%) !important;
}

.brand-logo,
.job-logo {
  background: linear-gradient(135deg, #1f2a44 0%, #4d5f7f 100%) !important;
}

.search-btn,
.apply-btn,
.cta-primary,
.register-btn,
.social-btn.active {
  background: #1f2a44 !important;
  border-color: #1f2a44 !important;
  color: #ffffff !important;
}

.filter-option.active,
.hot-keyword:hover,
.announcement-tag {
  background: #1f2a44 !important;
  color: #ffffff !important;
}

.ant-tag-blue,
.ant-tag-geekblue,
.ant-tag-cyan {
  color: #2f4f75 !important;
  border-color: #c8d5e8 !important;
  background: #f3f7fd !important;
}

.ant-tag-green {
  color: #2f6a5a !important;
  border-color: #cfe7e0 !important;
  background: #f1faf7 !important;
}

.ant-tag-orange,
.ant-tag-gold {
  color: #8a6d3b !important;
  border-color: #ecdcb8 !important;
  background: #fff9ef !important;
}

.ant-tag-processing,
.ant-badge-status-processing {
  color: #2f4f75 !important;
}

.ant-tag-success {
  color: #2f6a5a !important;
}

.ant-tag-warning {
  color: #8a6d3b !important;
}

.ant-statistic-content-value {
  background: linear-gradient(135deg, #1f2a44 0%, #4d5f7f 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
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
