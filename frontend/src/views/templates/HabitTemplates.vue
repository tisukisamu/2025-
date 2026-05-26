<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-gradient-to-r from-#111111 to-#374151 text-white p-6">
      <div class="flex flex-col lg:flex-row lg:items-start justify-between gap-4">
        <div>
          <h1 class="text-22px m-0 font-700">习惯模板中心</h1>
          <p class="m-0 mt-2 text-white/70">从成熟的习惯库快速创建，减少从 0 到 1 的成本</p>
        </div>
        <div class="w-full lg:max-w-700px flex flex-col gap-3">
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-[1.4fr_160px_140px] gap-2">
            <a-input-search
              v-model:value="keyword"
              allow-clear
              placeholder="搜索模板名称/标签"
              class="w-full"
              @search="fetchTemplates"
            />
            <a-select
              v-model:value="selectedCategoryName"
              allow-clear
              placeholder="模板分类"
              class="w-full"
              @change="fetchTemplates"
            >
              <a-select-option v-for="c in templateCategoryOptions" :key="c" :value="c">{{ c }}</a-select-option>
            </a-select>
            <a-select
              v-model:value="selectedRepeatType"
              allow-clear
              placeholder="重复类型"
              class="w-full"
              @change="fetchTemplates"
            >
              <a-select-option value="DAILY">每天</a-select-option>
              <a-select-option value="WEEKLY">每周</a-select-option>
            </a-select>
          </div>
          <div class="flex flex-wrap lg:justify-end gap-2">
            <a-button class="bg-white/10 text-white border-white/20" @click="fetchTemplates">刷新</a-button>
            <a-button v-if="hasActiveFilters" class="bg-white/10 text-white border-white/20" @click="clearFilters">清空筛选</a-button>
          </div>
        </div>
      </div>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <div class="mb-4 flex items-center justify-between gap-4">
        <div class="text-13px text-#6b7280">
          共找到 <span class="font-700 text-#111111">{{ templates.length }}</span> 个模板
        </div>
        <div class="flex flex-wrap gap-2">
          <a-tag v-for="f in activeFilters" :key="f" color="default">{{ f }}</a-tag>
        </div>
      </div>
      <a-spin :spinning="loading">
        <a-empty v-if="templates.length === 0" description="暂无模板" />

        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div
            v-for="t in templates"
            :key="t.id"
            class="p-4 rounded-xl border border-#e5e7eb bg-white hover:shadow-md transition-shadow"
          >
            <div class="flex items-start justify-between gap-4">
              <div class="flex items-center gap-3 min-w-0">
                <div class="w-10 h-10 rounded-full flex items-center justify-center text-white text-lg shrink-0" :style="{ backgroundColor: t.color || '#111111' }">
                  {{ resolveTemplateIcon(t.icon) }}
                </div>
                <div class="min-w-0">
                  <div class="font-700 text-#111827 truncate">{{ t.name }}</div>
                  <div v-if="t.description" class="text-12px text-#6b7280 mt-1">{{ t.description }}</div>
                </div>
              </div>
              <a-button type="primary" class="bg-black border-black text-white" @click="openCreate(t)">使用</a-button>
            </div>

            <div class="mt-3 flex flex-wrap gap-2">
              <a-tag v-if="t.categoryName" :color="t.categoryColor || '#111111'">{{ t.categoryName }}</a-tag>
              <a-tag v-for="tag in parseTags(t.tags)" :key="`${t.id}-${tag}`">{{ tag }}</a-tag>
              <a-tag v-if="t.repeatType === 'WEEKLY'">每周</a-tag>
              <a-tag v-else>每天</a-tag>
              <a-tag v-if="t.reminderTime">默认提醒 {{ formatTime(t.reminderTime) }}</a-tag>
            </div>
          </div>
        </div>
      </a-spin>
    </a-card>

    <a-modal
      v-model:open="createOpen"
      title="使用模板创建习惯"
      :confirm-loading="creating"
      ok-text="创建"
      cancel-text="取消"
      @ok="submitCreate"
    >
      <a-form layout="vertical">
        <a-form-item label="习惯名称">
          <a-input v-model:value="createForm.name" placeholder="不填则使用模板名称" />
        </a-form-item>
        <a-form-item label="分类">
          <a-select v-model:value="createForm.categoryId" allow-clear placeholder="可选">
            <a-select-option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-space class="w-full justify-between">
          <div class="flex items-center gap-2">
            <span class="text-#111827">开启提醒</span>
            <a-switch v-model:checked="createForm.reminderEnabled" />
          </div>
          <a-time-picker
            v-model:value="createTimeValue"
            format="HH:mm"
            :disabled="!createForm.reminderEnabled"
            style="width: 180px"
          />
        </a-space>
        <div class="mt-3 text-12px text-#6b7280">创建后可在「习惯编辑」里继续调整规则</div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import { createHabitFromTemplate, getCategories, getHabitTemplatesWithFilter } from '../../api/habit'
import type { Category, HabitTemplate } from '../../types'

const loading = ref(false)
const creating = ref(false)
const keyword = ref('')
const selectedCategoryName = ref<string | undefined>(undefined)
const selectedRepeatType = ref<'DAILY' | 'WEEKLY' | undefined>(undefined)
const templates = ref<HabitTemplate[]>([])
const categories = ref<Category[]>([])
const baseCategoryOptions = ['健康', '成长', '运动', '学习', '情绪']
const templateCategoryOptions = computed(() => {
  const dynamic = templates.value.map((item) => item.categoryName).filter(Boolean) as string[]
  return Array.from(new Set([...baseCategoryOptions, ...dynamic]))
})
const hasActiveFilters = computed(() => Boolean(keyword.value || selectedCategoryName.value || selectedRepeatType.value))
const activeFilters = computed(() => {
  const list: string[] = []
  if (keyword.value) list.push(`关键词：${keyword.value}`)
  if (selectedCategoryName.value) list.push(`分类：${selectedCategoryName.value}`)
  if (selectedRepeatType.value) list.push(`重复：${selectedRepeatType.value === 'DAILY' ? '每天' : '每周'}`)
  return list
})

const createOpen = ref(false)
const currentTemplate = ref<HabitTemplate | null>(null)
const createForm = reactive({
  name: '',
  categoryId: undefined as number | undefined,
  reminderEnabled: true,
  reminderTime: null as string | null
})

const createTimeValue = computed({
  get: () => (createForm.reminderTime ? dayjs(`2026-01-01 ${createForm.reminderTime}`) : null),
  set: (v) => {
    createForm.reminderTime = v ? v.format('HH:mm:ss') : null
    if (v) createForm.reminderEnabled = true
  }
})

const parseTags = (raw: string | null) => {
  if (!raw) return []
  return String(raw)
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean)
    .slice(0, 4)
}

const formatTime = (t: string) => {
  return t.slice(0, 5)
}

const resolveTemplateIcon = (icon: string | null) => {
  const value = String(icon || '✓').trim()
  if (!value) return '✓'
  return Array.from(value)[0] || '✓'
}

const fetchCategories = async () => {
  const res = await getCategories()
  categories.value = res.data || []
}

const fetchTemplates = async () => {
  loading.value = true
  try {
    const res = await getHabitTemplatesWithFilter({
      keyword: keyword.value || undefined,
      categoryName: selectedCategoryName.value || undefined,
      repeatType: selectedRepeatType.value || undefined
    })
    templates.value = res.data || []
  } finally {
    loading.value = false
  }
}

const clearFilters = async () => {
  keyword.value = ''
  selectedCategoryName.value = undefined
  selectedRepeatType.value = undefined
  await fetchTemplates()
}

const openCreate = (t: HabitTemplate) => {
  currentTemplate.value = t
  createForm.name = ''
  createForm.categoryId = undefined
  createForm.reminderEnabled = Boolean(t.reminderTime)
  createForm.reminderTime = t.reminderTime || null
  createOpen.value = true
}

const submitCreate = async () => {
  if (!currentTemplate.value) return
  creating.value = true
  try {
    await createHabitFromTemplate(currentTemplate.value.id, {
      name: createForm.name || null,
      categoryId: createForm.categoryId || null,
      reminderEnabled: createForm.reminderEnabled,
      reminderTime: createForm.reminderEnabled ? createForm.reminderTime : null
    })
    message.success('已创建习惯')
    createOpen.value = false
  } finally {
    creating.value = false
  }
}

onMounted(async () => {
  await fetchCategories()
  await fetchTemplates()
})
</script>
