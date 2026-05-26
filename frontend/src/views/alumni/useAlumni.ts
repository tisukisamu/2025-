import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  createAlumniTeam,
  createAlumniTeamMessage,
  getAlumniRanking,
  getAlumniTeamDetail,
  getMyAlumniProfile,
  getMyAlumniTeams,
  getNearbyAlumni,
  getNearbyAlumniTeams,
  joinAlumniTeam,
  quitAlumniTeam,
  updateMyAlumniProfile
} from '../../api/alumni'
import type { AlumniProfile, AlumniRankingItem, AlumniTeam, AlumniTeamDetail } from '../../types'

const loading = ref(false)
const savingProfile = ref(false)
const creatingTeam = ref(false)
const radiusKm = ref<number>(10)
const nearbyAlumni = ref<AlumniProfile[]>([])
const nearbyTeams = ref<AlumniTeam[]>([])
const rankingRows = ref<AlumniRankingItem[]>([])
const myTeams = ref<AlumniTeam[]>([])
const activeTeamId = ref<number | null>(null)
const activeTeamDetail = ref<AlumniTeamDetail | null>(null)
const teamMessage = ref('')

const profileForm = reactive({
  school: '',
  major: '',
  graduationYear: undefined as number | undefined,
  city: '',
  latitude: undefined as number | undefined,
  longitude: undefined as number | undefined,
  bio: '',
  openNearby: true
})

const teamForm = reactive({
  name: '',
  slogan: '',
  city: '',
  maxMembers: 6
})

const formatDate = (v: string) => {
  if (!v) return ''
  return String(v).replace('T', ' ').slice(0, 16)
}

const fillProfile = (profile: AlumniProfile) => {
  profileForm.school = profile.school || ''
  profileForm.major = profile.major || ''
  profileForm.graduationYear = profile.graduationYear || undefined
  profileForm.city = profile.city || ''
  profileForm.latitude = profile.latitude || undefined
  profileForm.longitude = profile.longitude || undefined
  profileForm.bio = profile.bio || ''
  profileForm.openNearby = Boolean(profile.openNearby)
}

const hasGeoLocation = () => {
  return profileForm.latitude !== undefined && profileForm.longitude !== undefined
}

const loadProfile = async () => {
  const res = await getMyAlumniProfile()
  if (res.data) {
    fillProfile(res.data)
  }
}

const saveProfile = async () => {
  savingProfile.value = true
  try {
    await updateMyAlumniProfile({
      school: profileForm.school || null,
      major: profileForm.major || null,
      graduationYear: profileForm.graduationYear || null,
      city: profileForm.city || null,
      latitude: profileForm.latitude || null,
      longitude: profileForm.longitude || null,
      bio: profileForm.bio || null,
      openNearby: profileForm.openNearby
    })
    message.success('资料已保存')
    await refreshAll()
  } finally {
    savingProfile.value = false
  }
}

const useCurrentLocation = async () => {
  if (!navigator.geolocation) {
    message.warning('当前浏览器不支持定位')
    return
  }
  await new Promise<void>((resolve) => {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        profileForm.latitude = Number(position.coords.latitude.toFixed(6))
        profileForm.longitude = Number(position.coords.longitude.toFixed(6))
        message.success('已读取当前经纬度')
        resolve()
      },
      () => {
        message.warning('定位失败，请检查浏览器定位权限')
        resolve()
      },
      { enableHighAccuracy: true, timeout: 10000 }
    )
  })
}

const loadNearby = async () => {
  if (!hasGeoLocation()) {
    nearbyAlumni.value = []
    nearbyTeams.value = []
    rankingRows.value = []
    return
  }
  const [alumniRes, teamRes, rankRes] = await Promise.all([
    getNearbyAlumni(radiusKm.value),
    getNearbyAlumniTeams(radiusKm.value),
    getAlumniRanking(radiusKm.value, 20)
  ])
  nearbyAlumni.value = alumniRes.data || []
  nearbyTeams.value = teamRes.data || []
  rankingRows.value = rankRes.data || []
}

const loadMyTeams = async () => {
  const res = await getMyAlumniTeams()
  myTeams.value = res.data || []
}

const submitCreateTeam = async () => {
  if (!teamForm.name.trim()) {
    message.warning('请输入小队名称')
    return
  }
  creatingTeam.value = true
  try {
    const res = await createAlumniTeam({
      name: teamForm.name.trim(),
      slogan: teamForm.slogan || null,
      city: teamForm.city || profileForm.city || null,
      maxMembers: teamForm.maxMembers
    })
    teamForm.name = ''
    teamForm.slogan = ''
    teamForm.city = ''
    teamForm.maxMembers = 6
    message.success('小队创建成功')
    await refreshAll()
    if (res.data?.id) {
      await openTeam(res.data.id)
    }
  } finally {
    creatingTeam.value = false
  }
}

const joinTeam = async (teamId: number) => {
  await joinAlumniTeam(teamId)
  message.success('加入成功')
  await refreshAll()
  await openTeam(teamId)
}

const openTeam = async (teamId: number) => {
  activeTeamId.value = teamId
  const res = await getAlumniTeamDetail(teamId)
  activeTeamDetail.value = res.data || null
}

const quitTeam = async (teamId: number) => {
  await quitAlumniTeam(teamId)
  message.success('已退出小队')
  if (activeTeamId.value === teamId) {
    activeTeamId.value = null
    activeTeamDetail.value = null
  }
  await refreshAll()
}

const sendTeamMessage = async () => {
  if (!activeTeamId.value) return
  const content = teamMessage.value.trim()
  if (!content) {
    message.warning('请输入督促消息')
    return
  }
  await createAlumniTeamMessage(activeTeamId.value, content)
  teamMessage.value = ''
  await openTeam(activeTeamId.value)
}

const refreshAll = async () => {
  loading.value = true
  try {
    await loadProfile()
    await Promise.all([loadMyTeams(), loadNearby()])
    if (activeTeamId.value) {
      await openTeam(activeTeamId.value)
    }
  } finally {
    loading.value = false
  }
}

export function useAlumni() {
  return {
    loading,
    savingProfile,
    creatingTeam,
    radiusKm,
    nearbyAlumni,
    nearbyTeams,
    rankingRows,
    myTeams,
    activeTeamId,
    activeTeamDetail,
    teamMessage,
    profileForm,
    teamForm,
    formatDate,
    hasGeoLocation,
    loadProfile,
    saveProfile,
    useCurrentLocation,
    loadNearby,
    loadMyTeams,
    submitCreateTeam,
    joinTeam,
    openTeam,
    quitTeam,
    sendTeamMessage,
    refreshAll
  }
}
