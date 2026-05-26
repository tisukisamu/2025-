import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import Antd, { message, notification } from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import 'virtual:uno.css'
import '@unocss/reset/tailwind.css'
import './assets/button-fix.css'

const app = createApp(App)
const pinia = createPinia()

app.config.errorHandler = (err, vm, info) => {
  console.error('[Vue Error]', err)
  console.error('[Component]', vm?.$options?.name || 'AnonymousComponent')
  console.error('[Info]', info)
  
  notification.error({
    message: '应用错误',
    description: (err as Error).message || '发生了未知错误',
    duration: 5,
    placement: 'topRight'
  })
}

app.config.warnHandler = (msg, vm, trace) => {
  console.warn('[Vue Warning]', msg)
  console.warn('[Trace]', trace)
}

window.addEventListener('unhandledrejection', (event) => {
  event.preventDefault()
  console.error('[Unhandled Promise Rejection]', event.reason)
  
  if ((event.reason as Error)?.message) {
    message.error(`异步错误: ${(event.reason as Error).message}`)
  }
})

window.addEventListener('error', (event) => {
  console.error('[Global Error]', event.error)
  
  if (event.target && (event.target as HTMLElement).tagName === 'IMG' || (event.target as HTMLElement).tagName === 'SCRIPT' || (event.target as HTMLElement).tagName === 'LINK') {
    message.error('资源加载失败，请刷新页面重试')
    return
  }
  
  message.error('程序发生错误，请刷新页面重试')
})

app.use(pinia)
app.use(router)
app.use(Antd, {
  theme: {
    token: {
      colorPrimary: '#111111',
      colorInfo: '#6b7280',
      colorSuccess: '#22c55e',
      colorWarning: '#f59e0b',
      colorError: '#ef4444',
      borderRadius: 8,
      colorText: '#111827',
      colorTextSecondary: '#6b7280',
      colorBgContainer: '#ffffff',
      colorBorder: '#e5e7eb',
    },
    components: {
      Button: {
        primaryColor: '#111111',
        primaryTextColor: '#ffffff',
        defaultColor: '#374151',
        defaultBg: '#ffffff',
        defaultBorderColor: '#e5e7eb',
      }
    }
  }
})
app.mount('#app')
