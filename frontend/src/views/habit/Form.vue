<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-#111111 text-white p-5 flex items-center justify-between">
      <div>
        <h1 class="text-22px m-0">{{ isEdit ? '编辑习惯' : '新建习惯' }}</h1>
        <p class="m-0 mt-2 text-#9ca3af">设置习惯规则、提醒时间与分类</p>
      </div>
      <a-space>
        <a-button @click="router.push('/habits')">返回列表</a-button>
        <a-button type="primary" class="bg-black border-black text-white" :loading="saving" @click="submitForm">保存</a-button>
      </a-space>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <a-form ref="formRef" layout="vertical" :model="formData" :rules="rules">
        <a-row :gutter="20">
          <a-col :span="12">
            <a-form-item label="习惯名称" name="name">
              <a-input v-model:value="formData.name" placeholder="例如：晨跑 30 分钟" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="分类" name="categoryId">
              <a-select v-model:value="formData.categoryId" allow-clear placeholder="选择分类">
                <a-select-option v-for="item in categories" :key="item.id" :value="item.id">
                  {{ item.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="描述" name="description">
          <a-textarea v-model:value="formData.description" :rows="3" placeholder="给这个习惯补充说明" />
        </a-form-item>

        <a-row :gutter="20">
          <a-col :span="8">
            <a-form-item label="重复类型" name="repeatType">
              <a-radio-group v-model:value="formData.repeatType">
                <a-radio value="DAILY">每天</a-radio>
                <a-radio value="WEEKLY">每周</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="开启提醒">
              <a-switch v-model:checked="formData.reminderEnabled" />
            </a-form-item>
          </a-col>
          <a-col :span="10">
            <a-form-item label="提醒时间">
              <a-time-picker
                v-model:value="reminderTimeValue"
                format="HH:mm"
                style="width: 100%"
                :disabled="!formData.reminderEnabled"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item v-if="formData.repeatType === 'WEEKLY'" label="每周执行日" name="repeatDays">
          <a-checkbox-group v-model:value="weeklyDays" :options="weekOptions" />
        </a-form-item>

        <a-row :gutter="20">
          <a-col :span="12">
            <a-form-item label="图标">
              <a-input v-model:value="formData.icon" placeholder="例如：🏃" maxlength="2" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="主题色">
              <a-input v-model:value="formData.color" placeholder="#111111" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { createHabit, getCategories, getHabitById, updateHabit } from '../../api/habit'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const saving = ref(false)
const categories = ref([])
const weekOptions = [
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 },
  { label: '周六', value: 6 },
  { label: '周日', value: 0 }
]
const formData = reactive({
  name: '',
  description: '',
  categoryId: undefined,
  repeatType: 'DAILY',
  repeatDays: '',
  reminderTime: null,
  reminderEnabled: false,
  icon: '',
  color: '#111111'
})
const weeklyDays = ref([])
const isEdit = computed(() => !!route.params.id)
const reminderTimeValue = computed({
  get: () => (formData.reminderTime ? dayjs(`2026-01-01 ${formData.reminderTime}`) : null),
  set: (value) => {
    formData.reminderTime = value ? value.format('HH:mm:ss') : null
    if (value) {
      formData.reminderEnabled = true
    }
  }
})
const rules = {
  name: [{ required: true, message: '请输入习惯名称', trigger: 'blur' }],
  repeatType: [{ required: true, message: '请选择重复类型', trigger: 'change' }],
  repeatDays: [{
    validator: async () => {
      if (formData.repeatType === 'WEEKLY' && weeklyDays.value.length === 0) {
        throw new Error('请至少选择一个执行日')
      }
    },
    trigger: 'change'
  }]
}

const fetchCategories = async () => {
  const res = await getCategories()
  categories.value = res.data || []
}

const fetchDetail = async () => {
  if (!isEdit.value) return
  const id = Number(route.params.id)
  const res = await getHabitById(id)
  const item = res.data
  formData.name = item.name
  formData.description = item.description || ''
  formData.categoryId = item.categoryId || undefined
  formData.repeatType = item.repeatType
  formData.reminderTime = item.reminderTime
  formData.reminderEnabled = item.reminderEnabled ?? Boolean(item.reminderTime)
  formData.icon = item.icon || ''
  formData.color = item.color || '#111111'
  weeklyDays.value = item.repeatDays ? String(item.repeatDays).split(',').map((d) => Number(d)) : []
}

const submitForm = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    formData.repeatDays = formData.repeatType === 'WEEKLY' ? weeklyDays.value.join(',') : null
    const payload = {
      name: formData.name,
      description: formData.description || null,
      categoryId: formData.categoryId || null,
      repeatType: formData.repeatType,
      repeatDays: formData.repeatDays,
      reminderTime: formData.reminderTime,
      reminderEnabled: formData.reminderEnabled,
      icon: formData.icon || null,
      color: formData.color || null
    }
    if (isEdit.value) {
      await updateHabit(Number(route.params.id), payload)
      message.success('习惯已更新')
    } else {
      await createHabit(payload)
      message.success('习惯已创建')
    }
    router.push('/habits')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await fetchCategories()
  await fetchDetail()
})
</script>
