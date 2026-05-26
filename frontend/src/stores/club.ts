import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Club } from '@/types'
import { clubApi } from '@/api'

export const useClubStore = defineStore('club', () => {
  const clubList = ref<Club[]>([])
  const currentClub = ref<Club | null>(null)

  const fetchClubList = async () => {
    const res = await clubApi.getMyClubs()
    clubList.value = res.data
  }

  const fetchClubDetail = async (id: number) => {
    const res = await clubApi.getById(id)
    currentClub.value = res.data
    return res.data
  }

  const setCurrentClub = (club: Club | null) => {
    currentClub.value = club
  }

  return {
    clubList,
    currentClub,
    fetchClubList,
    fetchClubDetail,
    setCurrentClub
  }
})
