<template>
  <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
    <template #title>社区动态广场</template>
    <a-empty v-if="rows.length === 0 && !loading" description="还没有动态，快来发布第一条吧" />
    <a-spin :spinning="loading">
      <div class="space-y-4">
        <div v-for="post in rows" :key="post.id" class="rounded-xl border border-#e5e7eb p-4 bg-white">
          <div class="flex items-start justify-between gap-3">
            <div class="flex items-center gap-3">
              <a-avatar :src="post.userAvatar || undefined">
                <template #icon>
                  <user-outlined />
                </template>
              </a-avatar>
              <div>
                <div class="font-700 text-#111827">{{ post.userName }}</div>
                <div class="text-12px text-#9ca3af">{{ formatDate(post.createdAt) }}</div>
              </div>
            </div>
            <a-popconfirm
              v-if="canDeletePost(post)"
              title="确认删除这条动态？"
              ok-text="删除"
              cancel-text="取消"
              @confirm="removePost(post.id)"
            >
              <a-button class="app-btn-danger" size="small">删除</a-button>
            </a-popconfirm>
          </div>
          <div class="mt-3 whitespace-pre-wrap text-#111827 leading-6">{{ post.content }}</div>
          <div v-if="post.imagePath" class="mt-3">
            <img :src="post.imagePath" alt="post-image" class="max-h-320px rounded-lg border border-#e5e7eb" />
          </div>
          <div class="mt-4 pt-3 border-t border-#f3f4f6">
            <div class="text-13px text-#6b7280 mb-2">评论 {{ post.comments?.length || 0 }} 条</div>
            <div v-if="post.comments?.length" class="space-y-2 mb-3">
              <div
                v-for="comment in post.comments"
                :key="comment.id"
                class="rounded-lg bg-#f9fafb px-3 py-2 flex items-start justify-between gap-3"
              >
                <div class="min-w-0">
                  <div class="text-13px">
                    <span class="font-700 text-#111827 mr-2">{{ comment.userName }}</span>
                    <span class="text-#6b7280">{{ comment.content }}</span>
                  </div>
                  <div class="text-12px text-#9ca3af mt-1">{{ formatDate(comment.createdAt) }}</div>
                </div>
                <a-button
                  v-if="canDeleteComment(comment)"
                  class="app-btn-danger shrink-0"
                  size="small"
                  @click="removeComment(comment.id)"
                >
                  删除
                </a-button>
              </div>
            </div>
            <div class="send-row">
              <a-input v-model:value="commentDrafts[post.id]" placeholder="写下你的评论..." :maxlength="300" />
              <a-button class="app-btn-primary min-w-78px" @click="submitComment(post.id)">发送</a-button>
            </div>
          </div>
        </div>
      </div>
    </a-spin>
  </a-card>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { UserOutlined } from '@ant-design/icons-vue'
import { useCommunity } from './useCommunity'

const {
  loading,
  rows,
  commentDrafts,
  formatDate,
  fetchPosts,
  submitComment,
  canDeletePost,
  canDeleteComment,
  removePost,
  removeComment
} = useCommunity()

onMounted(fetchPosts)
</script>

<style scoped>
.send-row {
  display: grid;
  grid-template-columns: 1fr auto;
  align-items: center;
  gap: 10px;
}
</style>
