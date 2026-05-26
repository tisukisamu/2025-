<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-semibold text-neutral-900">我的收藏</h1>
      <a-button @click="fetchFavorites">刷新</a-button>
    </div>

    <a-empty v-if="favorites.length === 0" description="暂无收藏职位，去职位搜索页收藏心仪职位吧" />

    <div v-else class="grid gap-4">
      <a-card v-for="job in favorites" :key="job.id" :bordered="false" class="rounded-xl border border-neutral-200">
        <div class="flex items-start justify-between gap-4">
          <div class="flex items-center gap-3 min-w-0">
            <a-avatar :size="50" shape="square" :src="resolveMediaUrl(job.coverUrl)" class="!bg-neutral-200">
              {{ job.title?.charAt(0) || '职' }}
            </a-avatar>
            <div class="min-w-0">
              <div class="text-lg font-semibold text-neutral-900 truncate">{{ job.title }}</div>
              <div class="text-sm text-neutral-500 truncate">{{ companyNameMap.get(job.companyId) || `企业 #${job.companyId}` }}</div>
              <div class="text-sm text-neutral-500 mt-1">{{ job.location || '地点待完善' }} · {{ job.education || '不限' }} · {{ job.experience || '不限' }}</div>
            </div>
          </div>

          <div class="text-right shrink-0">
            <div class="text-xl font-bold text-neutral-900">{{ job.salaryMin || 0 }}-{{ job.salaryMax || 0 }}K</div>
            <div class="flex gap-2 mt-3">
              <a-button size="small" @click="viewDetail(job.id)">查看详情</a-button>
              <a-button size="small" danger @click="removeFavorite(job.id)">取消收藏</a-button>
            </div>
          </div>
        </div>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { companyApi, favoriteApi } from '@/api'
import { resolveMediaUrl } from '@/utils/media'

const router = useRouter()
const favorites = ref<any[]>([])
const companyNameMap = ref<Map<number, string>>(new Map())

const fetchFavorites = async () => {
  try {
    const [favRes, companyRes] = await Promise.all([
      favoriteApi.getMyJobs(),
      companyApi.getAll()
    ])
    favorites.value = favRes.data || []
    companyNameMap.value = new Map((companyRes.data || []).map((item: any) => [item.id, item.name]))
  } catch (error) {
    message.error('获取收藏列表失败')
  }
}

const removeFavorite = async (jobId: number) => {
  try {
    await favoriteApi.remove(jobId)
    favorites.value = favorites.value.filter(item => item.id !== jobId)
    message.success('已取消收藏')
  } catch (error) {
    message.error('取消收藏失败')
  }
}

const viewDetail = (jobId: number) => {
  router.push(`/jobs/${jobId}`)
}

onMounted(() => {
  fetchFavorites()
})
</script>
