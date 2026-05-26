import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import Antd, { message, notification } from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import 'virtual:uno.css'
import '@unocss/reset/tailwind.css'

const app = createApp(App)
const pinia = createPinia()

app.config.errorHandler = (err, vm, info) => {
  console.error('[Vue Error]', err)
  console.error('[Component]', vm?.$options?.name || 'AnonymousComponent')
  console.error('[Info]', info)
  
  notification.error({
    message: '应用错误',
    description: err.message || '发生了未知错误',
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
  
  if (event.reason?.message) {
    message.error(`异步错误: ${event.reason.message}`)
  }
})

window.addEventListener('error', (event) => {
  console.error('[Global Error]', event.error)
  
  if (event.target && (event.target.tagName === 'IMG' || event.target.tagName === 'SCRIPT' || event.target.tagName === 'LINK')) {
    message.error('资源加载失败，请刷新页面重试')
    return
  }
  
  message.error('程序发生错误，请刷新页面重试')
})

app.use(pinia)
app.use(router)
app.use(Antd)
app.mount('#app')
