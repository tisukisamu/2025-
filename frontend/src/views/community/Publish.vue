<template>
  <a-card :bordered="false" class="rounded-xl border border-#e5e7eb">
    <template #title>发布动态</template>
    <a-form layout="vertical">
      <a-form-item label="分享内容">
        <a-textarea
          v-model:value="postForm.content"
          :maxlength="500"
          :auto-size="{ minRows: 4, maxRows: 6 }"
          placeholder="今天完成了什么？有哪些心得？"
        />
      </a-form-item>
      <a-space class="w-full justify-between">
        <div class="flex items-center gap-3">
          <a-upload
            :show-upload-list="false"
            accept=".jpg,.jpeg,.png,.gif,.webp"
            :custom-request="handleUpload"
          >
            <a-button class="app-btn-secondary" :loading="uploading">上传图片</a-button>
          </a-upload>
          <a-button v-if="postForm.imagePath" class="app-btn-danger" size="small" @click="removeImage">移除图片</a-button>
        </div>
        <a-button class="app-btn-primary min-w-88px" :loading="posting" @click="submitPost">发布</a-button>
      </a-space>
      <div v-if="postForm.imagePath" class="mt-3">
        <img :src="postForm.imagePath" alt="post-image" class="max-h-260px rounded-lg border border-#e5e7eb" />
      </div>
    </a-form>
  </a-card>
</template>

<script setup lang="ts">
import { useCommunity } from './useCommunity'

const { posting, uploading, postForm, handleUpload, removeImage, submitPost } = useCommunity()
</script>
