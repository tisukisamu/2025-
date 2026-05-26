<template>
  <div class="space-y-4">
    <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
      <template #title>创建互督小队</template>
      <a-form layout="vertical">
        <a-form-item label="小队名称">
          <a-input v-model:value="teamForm.name" :maxlength="30" placeholder="例如：早起学习 30 天" />
        </a-form-item>
        <a-form-item label="督促宣言">
          <a-textarea v-model:value="teamForm.slogan" :maxlength="120" :auto-size="{ minRows: 2, maxRows: 3 }" />
        </a-form-item>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
          <a-form-item label="队伍城市">
            <a-input v-model:value="teamForm.city" placeholder="默认取你的资料城市" />
          </a-form-item>
          <a-form-item label="人数上限">
            <a-input-number v-model:value="teamForm.maxMembers" :min="2" :max="50" class="w-full" />
          </a-form-item>
        </div>
        <a-button class="app-btn-primary w-full" :loading="creatingTeam" @click="submitCreateTeam">创建并加入小队</a-button>
      </a-form>
    </a-card>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-4">
      <a-card :bordered="false" class="rounded-xl border border-#e5e7eb lg:col-span-1">
        <template #title>我的小队</template>
        <a-empty v-if="myTeams.length === 0" description="你还没有加入小队" />
        <div v-else class="space-y-2">
          <a-button
            v-for="team in myTeams"
            :key="team.id"
            block
            :class="activeTeamId === team.id ? 'app-btn-primary' : 'app-btn-secondary'"
            @click="openTeam(team.id)"
          >
            {{ team.name }}
          </a-button>
        </div>
      </a-card>

      <a-card :bordered="false" class="rounded-xl border border-#e5e7eb lg:col-span-2">
        <template #title>小队详情与互督墙</template>
        <a-empty v-if="!activeTeamDetail" description="请选择一个小队查看详情" />
        <div v-else class="space-y-4">
          <div class="rounded-xl border border-#e5e7eb p-3">
            <div class="flex items-start justify-between gap-3">
              <div>
                <div class="font-700 text-#111827 text-16px">{{ activeTeamDetail.team.name }}</div>
                <div class="text-12px text-#6b7280 mt-1">{{ activeTeamDetail.team.city || '未知城市' }} · 成员 {{ activeTeamDetail.team.currentMembers }}/{{ activeTeamDetail.team.maxMembers }}</div>
                <div class="text-12px text-#9ca3af mt-1">{{ activeTeamDetail.team.slogan || '这个小队还没有宣言' }}</div>
              </div>
              <a-button class="app-btn-danger" @click="quitTeam(activeTeamDetail.team.id)">退出小队</a-button>
            </div>
          </div>

          <div class="rounded-xl border border-#e5e7eb p-3">
            <div class="font-700 text-#111827 mb-3">成员互督榜</div>
            <div class="space-y-2">
              <div v-for="member in activeTeamDetail.members" :key="member.userId" class="flex items-center justify-between rounded-lg bg-#f9fafb px-3 py-2">
                <div class="flex items-center gap-2">
                  <a-avatar :src="member.userAvatar || undefined" size="small" />
                  <span class="text-13px text-#111827">{{ member.userName }}</span>
                  <a-tag v-if="member.role === 'OWNER'" color="blue">队长</a-tag>
                </div>
                <div class="text-12px text-#6b7280">累计打卡 {{ member.totalChecks }} · 平均完成率 {{ member.avgRate }}%</div>
              </div>
            </div>
          </div>

          <div class="rounded-xl border border-#e5e7eb p-3">
            <div class="font-700 text-#111827 mb-3">互督消息墙</div>
            <div class="space-y-2 max-h-320px overflow-y-auto pr-1">
              <div v-for="msg in activeTeamDetail.messages" :key="msg.id" class="rounded-lg bg-#f9fafb px-3 py-2">
                <div class="text-13px text-#111827">
                  <span class="font-700 mr-2">{{ msg.userName }}</span>
                  <span>{{ msg.content }}</span>
                </div>
                <div class="text-12px text-#9ca3af mt-1">{{ formatDate(msg.createdAt) }}</div>
              </div>
              <a-empty v-if="activeTeamDetail.messages.length === 0" description="还没有督促消息" />
            </div>
            <div class="send-row mt-3">
              <a-input v-model:value="teamMessage" :maxlength="200" placeholder="发一句督促：今晚 10 点前一起完成打卡！" />
              <a-button class="app-btn-primary min-w-80px" @click="sendTeamMessage">发送</a-button>
            </div>
          </div>
        </div>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAlumni } from './useAlumni'

const route = useRoute()
const {
  creatingTeam,
  myTeams,
  activeTeamId,
  activeTeamDetail,
  teamMessage,
  teamForm,
  formatDate,
  loadMyTeams,
  submitCreateTeam,
  openTeam,
  quitTeam,
  sendTeamMessage
} = useAlumni()

const syncTeamFromRoute = async () => {
  const raw = route.query.teamId
  const teamId = Number(Array.isArray(raw) ? raw[0] : raw)
  if (!teamId || Number.isNaN(teamId)) return
  await openTeam(teamId)
}

onMounted(async () => {
  await loadMyTeams()
  await syncTeamFromRoute()
})

watch(() => route.query.teamId, async () => {
  await syncTeamFromRoute()
})
</script>

<style scoped>
.send-row {
  display: grid;
  grid-template-columns: 1fr auto;
  align-items: center;
  gap: 10px;
}
</style>
