<template>
  <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
    <template #title>我的动态</template>
    <a-empty v-if="myPosts.length === 0 && !loading" description="你还没有发布动态" />
    <a-spin :spinning="loading">
      <div class="space-y-4">
        <div v-for="post in myPosts" :key="post.id" class="rounded-xl border border-#e5e7eb p-4 bg-white">
          <div class="flex items-start justify-between gap-3">
            <div>
              <div class="font-700 text-#111827">我发布的动态</div>
              <div class="text-12px text-#9ca3af">{{ formatDate(post.createdAt) }}</div>
            </div>
            <a-popconfirm title="确认删除这条动态？" ok-text="删除" cancel-text="取消" @confirm="removePost(post.id)">
              <a-button class="app-btn-danger" size="small">删除</a-button>
            </a-popconfirm>
          </div>
          <div class="mt-3 whitespace-pre-wrap text-#111827 leading-6">{{ post.content }}</div>
          <div v-if="post.imagePath" class="mt-3">
            <img :src="post.imagePath" alt="post-image" class="max-h-320px rounded-lg border border-#e5e7eb" />
          </div>
          <div class="mt-4 text-12px text-#6b7280">评论数：{{ post.comments?.length || 0 }}</div>
        </div>
      </div>
    </a-spin>
  </a-card>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useCommunity } from './useCommunity'

const { loading, myPosts, formatDate, fetchPosts, removePost } = useCommunity()

onMounted(fetchPosts)
</script>
