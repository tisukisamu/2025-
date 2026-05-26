import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Habit, Category, TodayOverview, CalendarData } from '../types'
import * as habitApi from '../api/habit'
import * as checkApi from '../api/check'
import * as statisticsApi from '../api/statistics'

export const useHabitStore = defineStore('habit', () => {
  const habits = ref<Habit[]>([])
  const categories = ref<Category[]>([])
  const todayOverview = ref<TodayOverview | null>(null)
  const loading = ref(false)

  const activeHabits = computed(() => 
    habits.value.filter(h => h.status === 'ACTIVE')
  )

  const fetchHabits = async () => {
    loading.value = true
    try {
      const res = await habitApi.getHabits()
      habits.value = res.data || []
    } finally {
      loading.value = false
    }
  }

  const fetchCategories = async () => {
    try {
      const res = await habitApi.getCategories()
      categories.value = res.data || []
    } catch (error) {
      console.error('Failed to fetch categories:', error)
    }
  }

  const fetchTodayOverview = async () => {
    try {
      const res = await checkApi.getTodayOverview()
      todayOverview.value = res.data
    } catch (error) {
      console.error('Failed to fetch today overview:', error)
    }
  }

  const createHabit = async (data: Parameters) => {
    const res = await habitApi.createHabit(data)
    habits.value.push(res.data)
    return res.data
  }

  const updateHabit = async (id: number, data: Parameters) => {
    const res = await habitApi.updateHabit(id, data)
    const index = habits.value.findIndex(h => h.id === id)
    if (index !== -1) {
      habits.value[index] = res.data
    }
    return res.data
  }

  const deleteHabit = async (id: number) => {
    await habitApi.deleteHabit(id)
    habits.value = habits.value.filter(h => h.id !== id)
  }

  const pauseHabit = async (id: number) => {
    await habitApi.pauseHabit(id)
    const habit = habits.value.find(h => h.id === id)
    if (habit) {
      habit.status = 'PAUSED'
    }
  }

  const resumeHabit = async (id: number) => {
    await habitApi.resumeHabit(id)
    const habit = habits.value.find(h => h.id === id)
    if (habit) {
      habit.status = 'ACTIVE'
    }
  }

  return {
    habits,
    categories,
    todayOverview,
    loading,
    activeHabits,
    fetchHabits,
    fetchCategories,
    fetchTodayOverview,
    createHabit,
    updateHabit,
    deleteHabit,
    pauseHabit,
    resumeHabit
  }
})
