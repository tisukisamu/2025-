<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-semibold text-neutral-900">人才库</h1>
      <a-button @click="fetchData">刷新</a-button>
    </div>

    <a-card :bordered="false" class="rounded-xl" title="分组管理">
      <div class="flex items-center gap-2 mb-3">
        <a-input v-model:value="newGroupName" placeholder="新分组名称" style="max-width: 220px" />
        <a-button type="primary" class="!bg-neutral-900" @click="createGroup">新增分组</a-button>
      </div>
      <div class="flex flex-wrap gap-2">
        <a-tag v-for="group in groups" :key="group.id">{{ group.name }}</a-tag>
      </div>
    </a-card>

    <a-card :bordered="false" class="rounded-xl">
      <a-table :columns="columns" :data-source="talents" row-key="id" :pagination="{ pageSize: 10 }">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'tags'">
            <a-input
              :value="record.tags"
              size="small"
              @change="(e) => (record.tags = e.target.value)"
              @blur="() => updateTags(record)"
            />
          </template>
          <template v-else-if="column.dataIndex === 'groupId'">
            <a-select
              :value="record.groupId"
              allow-clear
              size="small"
              style="width: 130px"
              @change="(value) => moveGroup(record, value)"
            >
              <a-select-option v-for="group in groups" :key="group.id" :value="group.id">
                {{ group.name }}
              </a-select-option>
            </a-select>
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <a-button size="small" danger @click="removeTalent(record.id)">移除</a-button>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { talentApi } from '@/api'

const talents = ref<any[]>([])
const groups = ref<any[]>([])
const newGroupName = ref('')

const columns = [
  { title: 'ID', dataIndex: 'id', width: 80 },
  { title: '用户ID', dataIndex: 'userId', width: 90 },
  { title: '简历ID', dataIndex: 'resumeId', width: 90 },
  { title: '状态', dataIndex: 'status', width: 120 },
  { title: '标签', dataIndex: 'tags' },
  { title: '分组', dataIndex: 'groupId', width: 160 },
  { title: '操作', dataIndex: 'action', width: 100 }
]

const fetchData = async () => {
  try {
    const [poolRes, groupRes] = await Promise.all([
      talentApi.getPool(),
      talentApi.getGroups()
    ])
    talents.value = poolRes.data || []
    groups.value = groupRes.data || []
  } catch (error) {
    message.error('人才库数据加载失败')
  }
}

const createGroup = async () => {
  if (!newGroupName.value) {
    message.warning('请输入分组名称')
    return
  }
  try {
    await talentApi.createGroup(newGroupName.value)
    newGroupName.value = ''
    message.success('分组创建成功')
    await fetchData()
  } catch (error) {
    message.error('分组创建失败')
  }
}

const updateTags = async (record: any) => {
  try {
    await talentApi.updateTags(record.id, record.tags || '')
    message.success('标签已更新')
  } catch (error) {
    message.error('标签更新失败')
  }
}

const moveGroup = async (record: any, groupId: number) => {
  if (!groupId) return
  try {
    await talentApi.moveToGroup(record.id, groupId)
    message.success('分组已更新')
    await fetchData()
  } catch (error) {
    message.error('分组更新失败')
  }
}

const removeTalent = async (id: number) => {
  try {
    await talentApi.removeFromPool(id)
    message.success('已移出人才库')
    await fetchData()
  } catch (error) {
    message.error('移除失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>
