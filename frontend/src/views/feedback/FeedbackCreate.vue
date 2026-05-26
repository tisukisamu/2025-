<template>
  <div class="feedback-create-page">
    <div class="page-header">
      <h1>提交反馈</h1>
    </div>

    <a-form
      :model="form"
      :rules="rules"
      layout="vertical"
      @finish="handleSubmit"
    >
      <a-form-item label="反馈类型" name="type">
        <a-select v-model:value="form.type" placeholder="请选择反馈类型">
          <a-select-option value="BUG">Bug反馈</a-select-option>
          <a-select-option value="SUGGESTION">功能建议</a-select-option>
          <a-select-option value="COMPLAINT">投诉</a-select-option>
          <a-select-option value="OTHER">其他</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="标题" name="title">
        <a-input v-model:value="form.title" placeholder="请输入标题" maxlength="50" show-count />
      </a-form-item>

      <a-form-item label="内容" name="content">
        <a-textarea
          v-model:value="form.content"
          placeholder="请详细描述您的问题或建议"
          :rows="6"
          maxlength="500"
          show-count
        />
      </a-form-item>

      <a-form-item label="联系方式（选填）" name="contactInfo">
        <a-input v-model:value="form.contactInfo" placeholder="手机号或邮箱，方便我们联系您" />
      </a-form-item>

      <a-form-item>
        <a-space>
          <a-button type="primary" html-type="submit" :loading="submitting">
            提交
          </a-button>
          <a-button @click="router.back()">取消</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { feedbackApi } from '@/types/extra'
import type { Feedback } from '@/types/extra'
import { message } from 'ant-design-vue'

const router = useRouter()

const submitting = ref(false)

const form = reactive({
  type: '' as Feedback['type'] | '',
  title: '',
  content: '',
  contactInfo: ''
})

const rules = {
  type: [{ required: true, message: '请选择反馈类型' }],
  title: [{ required: true, message: '请输入标题' }],
  content: [{ required: true, message: '请输入内容' }]
}

const handleSubmit = async () => {
  if (!form.type) return

  submitting.value = true
  try {
    await feedbackApi.create({
      type: form.type,
      title: form.title,
      content: form.content,
      contactInfo: form.contactInfo || undefined
    })
    message.success('提交成功，感谢您的反馈')
    router.push('/feedback')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.feedback-create-page {
  background: #f5f5f5;
  min-height: calc(100vh - 200px);
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

:deep(.ant-form) {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  max-width: 600px;
}

@media (max-width: 768px) {
  :deep(.ant-form) {
    padding: 16px;
  }
}
</style>
