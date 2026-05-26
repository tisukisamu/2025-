import { defineStore } from 'pinia'
import { ref } from 'vue'
import { notificationApi } from '@/api'

export const useAppStore = defineStore('app', () => {
  const collapsed = ref(false)
  const notifications = ref<any[]>([])
  const unreadCount = ref(0)

  const toggleSidebar = () => {
    collapsed.value = !collapsed.value
  }

  const fetchUnreadCount = async () => {
    try {
      const res = await notificationApi.getUnreadCount()
      console.log('获取未读消息数响应:', res)
      unreadCount.value = res.data || 0
    } catch (error) {
      console.error('获取未读消息数失败', error)
      unreadCount.value = 0
    }
  }

  const fetchNotifications = async () => {
    try {
      const res = await notificationApi.getList({ page: 0, size: 10 })
      notifications.value = res.data.content || res.data.list || []
    } catch (error) {
      console.error('获取通知列表失败', error)
    }
  }

  const markAsRead = async (id: number) => {
    try {
      await notificationApi.markAsRead(id)
      await fetchUnreadCount()
    } catch (error) {
      console.error('标记已读失败', error)
    }
  }

  return {
    collapsed,
    notifications,
    unreadCount,
    toggleSidebar,
    fetchUnreadCount,
    fetchNotifications,
    markAsRead
  }
})
