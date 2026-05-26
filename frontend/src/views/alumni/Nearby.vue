<template>
  <div class="space-y-4">
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
      <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
        <template #title>附近校友</template>
        <a-empty v-if="!hasGeoLocation()" description="请先在资料页填写并保存经纬度，才能查看附近校友" />
        <a-empty v-else-if="nearbyAlumni.length === 0" description="暂无附近校友，试试扩大半径或邀请同学完善资料" />
        <div v-else class="space-y-3">
          <div v-for="item in nearbyAlumni" :key="item.userId" class="rounded-xl border border-#e5e7eb p-3 flex items-start justify-between gap-3">
            <div class="flex items-start gap-3 min-w-0">
              <a-avatar :src="item.userAvatar || undefined" />
              <div class="min-w-0">
                <div class="font-700 text-#111827">{{ item.userName }}</div>
                <div class="text-12px text-#6b7280 mt-1">{{ item.school || '未填写学校' }} · {{ item.major || '未填写专业' }} · {{ item.city || '未知城市' }}</div>
                <div class="text-12px text-#9ca3af mt-1">{{ item.bio || '这个校友还没写简介' }}</div>
              </div>
            </div>
            <a-tag color="blue">{{ item.distanceKm }}km</a-tag>
          </div>
        </div>
      </a-card>

      <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
        <template #title>附近互督小队</template>
        <a-empty v-if="!hasGeoLocation()" description="请先在资料页填写并保存经纬度，才能查看附近小队" />
        <a-empty v-else-if="nearbyTeams.length === 0" description="附近暂无可加入小队" />
        <div v-else class="space-y-3">
          <div v-for="team in nearbyTeams" :key="team.id" class="rounded-xl border border-#e5e7eb p-3">
            <div class="flex items-start justify-between gap-3">
              <div>
                <div class="font-700 text-#111827">{{ team.name }}</div>
                <div class="text-12px text-#6b7280 mt-1">{{ team.city || '未知城市' }} · {{ team.distanceKm }}km</div>
                <div class="text-12px text-#9ca3af mt-1">{{ team.slogan || '这个小队还没有宣言' }}</div>
                <div class="text-12px text-#6b7280 mt-1">成员 {{ team.currentMembers }}/{{ team.maxMembers }} · 队长 {{ team.ownerName }}</div>
              </div>
              <a-space>
                <a-button class="app-btn-secondary" @click="viewTeam(team.id, team.joined)">查看</a-button>
                <a-button
                  v-if="!team.joined"
                  class="app-btn-primary"
                  :disabled="team.currentMembers >= team.maxMembers"
                  @click="joinAndOpen(team.id)"
                >
                  加入
                </a-button>
                <a-tag v-else color="success">已加入</a-tag>
              </a-space>
            </div>
          </div>
        </div>
      </a-card>
    </div>

    <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
      <template #title>附近校友排行榜</template>
      <a-empty v-if="!hasGeoLocation()" description="请先完善经纬度后查看排行榜" />
      <a-empty v-else-if="rankingRows.length === 0" description="暂无可展示的排行数据" />
      <div v-else class="space-y-2">
        <div
          v-for="item in rankingRows"
          :key="item.userId"
          class="rounded-lg border border-#e5e7eb bg-white px-3 py-2 flex items-center justify-between gap-3"
        >
          <div class="flex items-center gap-3 min-w-0">
            <div class="w-7 h-7 rounded-full bg-#111111 text-white text-12px flex items-center justify-center">{{ item.rank }}</div>
            <a-avatar :src="item.userAvatar || undefined" size="small" />
            <div class="min-w-0">
              <div class="text-13px font-700 text-#111827 truncate">{{ item.userName }}</div>
              <div class="text-12px text-#6b7280">{{ item.school || '未填写学校' }} · {{ item.city || '未知城市' }} · {{ item.distanceKm }}km</div>
            </div>
          </div>
          <div class="text-right">
            <div class="text-13px font-700 text-#111827">打卡 {{ item.totalChecks }}</div>
            <div class="text-12px text-#6b7280">完成率 {{ item.avgRate }}%</div>
          </div>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useAlumni } from './useAlumni'

const router = useRouter()
const { hasGeoLocation, nearbyAlumni, nearbyTeams, rankingRows, loadProfile, loadNearby, openTeam, joinTeam } = useAlumni()

const viewTeam = async (teamId: number, joined: boolean) => {
  if (!joined) {
    message.info('请先加入小队后再查看详情')
    return
  }
  await openTeam(teamId)
  await router.push({
    path: '/alumni/teams',
    query: { teamId: String(teamId) }
  })
}

const joinAndOpen = async (teamId: number) => {
  await joinTeam(teamId)
  await router.push({
    path: '/alumni/teams',
    query: { teamId: String(teamId) }
  })
}

onMounted(async () => {
  await loadProfile()
  await loadNearby()
})
</script>
