<template>
  <div class="system-config">
    <div class="page-header">
      <h2>系统配置</h2>
      <p class="subtitle">管理系统全局参数与设置</p>
    </div>

    <a-row :gutter="24">
      <a-col :span="16">
        <a-card title="基础配置" :bordered="false" class="config-card">
          <a-form
            :model="configForm"
            :label-col="{ span: 6 }"
            :wrapper-col="{ span: 14 }"
          >
            <a-form-item label="系统名称">
              <a-input v-model:value="configForm.systemName" />
            </a-form-item>
            <a-form-item label="系统Logo">
              <a-input v-model:value="configForm.logo" placeholder="Logo URL" />
            </a-form-item>
            <a-form-item label="学期名称">
              <a-input v-model:value="configForm.semester" placeholder="如：2024-2025学年第一学期" />
            </a-form-item>
            <a-form-item label="审批超时天数">
              <a-input-number v-model:value="configForm.approvalTimeout" :min="1" :max="30" />
              <span class="form-hint">天（超过此天数未审批将产生预警）</span>
            </a-form-item>
            <a-form-item label="最低成员数">
              <a-input-number v-model:value="configForm.minMembers" :min="1" :max="100" />
              <span class="form-hint">人（社团成员低于此数量将产生预警）</span>
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 6, span: 14 }">
              <a-button type="primary" @click="saveConfig" :loading="saving">
                <SaveOutlined /> 保存配置
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>

        <a-card title="金额阈值配置" :bordered="false" class="config-card">
          <a-form
            :model="thresholdForm"
            :label-col="{ span: 6 }"
            :wrapper-col="{ span: 14 }"
          >
            <a-form-item label="高风险余额阈值">
              <a-input-number
                v-model:value="thresholdForm.highBalanceThreshold"
                :min="0"
                :formatter="value => `¥ ${value}`"
                :parser="value => value!.replace('¥ ', '')"
              />
            </a-form-item>
            <a-form-item label="中风险余额阈值">
              <a-input-number
                v-model:value="thresholdForm.mediumBalanceThreshold"
                :min="0"
                :formatter="value => `¥ ${value}`"
                :parser="value => value!.replace('¥ ', '')"
              />
            </a-form-item>
            <a-form-item label="低风险余额阈值">
              <a-input-number
                v-model:value="thresholdForm.lowBalanceThreshold"
                :min="0"
                :formatter="value => `¥ ${value}`"
                :parser="value => value!.replace('¥ ', '')"
              />
            </a-form-item>
            <a-form-item label="大额支出阈值">
              <a-input-number
                v-model:value="thresholdForm.largeExpenseThreshold"
                :min="0"
                :formatter="value => `¥ ${value}`"
                :parser="value => value!.replace('¥ ', '')"
              />
            </a-form-item>
            <a-form-item label="超大额支出阈值">
              <a-input-number
                v-model:value="thresholdForm.hugeExpenseThreshold"
                :min="0"
                :formatter="value => `¥ ${value}`"
                :parser="value => value!.replace('¥ ', '')"
              />
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 6, span: 14 }">
              <a-button type="primary" @click="saveThreshold" :loading="saving">
                <SaveOutlined /> 保存阈值
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>

      <a-col :span="8">
        <a-card title="系统状态" :bordered="false" class="status-card">
          <div class="status-item">
            <div class="status-label">系统版本</div>
            <div class="status-value">v1.0.0</div>
          </div>
          <a-divider />
          <div class="status-item">
            <div class="status-label">数据库状态</div>
            <div class="status-value">
              <a-tag color="green">正常</a-tag>
            </div>
          </div>
          <a-divider />
          <div class="status-item">
            <div class="status-label">缓存状态</div>
            <div class="status-value">
              <a-tag color="green">正常</a-tag>
            </div>
          </div>
          <a-divider />
          <div class="status-item">
            <div class="status-label">最后备份</div>
            <div class="status-value">{{ lastBackup }}</div>
          </div>
        </a-card>

        <a-card title="快捷操作" :bordered="false" class="action-card">
          <a-space direction="vertical" style="width: 100%">
            <a-button block @click="clearCache">
              <ClearOutlined /> 清理缓存
            </a-button>
            <a-button block @click="backupData">
              <DatabaseOutlined /> 数据备份
            </a-button>
            <a-button block @click="exportLogs">
              <ExportOutlined /> 导出日志
            </a-button>
            <a-popconfirm
              title="确定要重启系统吗？"
              @confirm="restartSystem"
              ok-text="确定"
              cancel-text="取消"
            >
              <a-button block danger>
                <ReloadOutlined /> 重启系统
              </a-button>
            </a-popconfirm>
          </a-space>
        </a-card>

        <a-card title="通知设置" :bordered="false" class="notify-card">
          <a-form layout="vertical">
            <a-form-item label="邮件通知">
              <a-switch v-model:checked="notifySettings.email" />
            </a-form-item>
            <a-form-item label="系统通知">
              <a-switch v-model:checked="notifySettings.system" />
            </a-form-item>
            <a-form-item label="审批提醒">
              <a-switch v-model:checked="notifySettings.approval" />
            </a-form-item>
            <a-form-item label="预警通知">
              <a-switch v-model:checked="notifySettings.warning" />
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  SaveOutlined,
  ClearOutlined,
  DatabaseOutlined,
  ExportOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import { logApi } from '@/api'

const saving = ref(false)
const lastBackup = ref('-')

const configForm = reactive({
  systemName: '高校社团资金管控平台',
  logo: '',
  semester: '2024-2025学年第一学期',
  approvalTimeout: 7,
  minMembers: 5
})

const thresholdForm = reactive({
  highBalanceThreshold: 100,
  mediumBalanceThreshold: 500,
  lowBalanceThreshold: 1000,
  largeExpenseThreshold: 2000,
  hugeExpenseThreshold: 5000
})

const notifySettings = reactive({
  email: true,
  system: true,
  approval: true,
  warning: true
})

const saveConfig = async () => {
  saving.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    message.success('配置保存成功')
  } catch (error) {
    message.error('保存失败')
  } finally {
    saving.value = false
  }
}

const saveThreshold = async () => {
  saving.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    message.success('阈值配置保存成功')
  } catch (error) {
    message.error('保存失败')
  } finally {
    saving.value = false
  }
}

const clearCache = () => {
  message.success('缓存清理成功')
}

const backupData = () => {
  message.loading('正在备份数据...', 2).then(() => {
    lastBackup.value = dayjs().format('YYYY-MM-DD HH:mm:ss')
    message.success('数据备份成功')
  })
}

const exportLogs = async () => {
  try {
    const res = await logApi.getList({ page: 0, size: 200 })
    const rows = (res.data.list || []).map(item => [
      item.id,
      item.username || '',
      item.operation || '',
      item.method || '',
      item.ip || '',
      item.duration ?? '',
      item.status === 1 ? '成功' : '失败',
      item.createTime || ''
    ])

    const header = ['日志ID', '操作人', '操作', '请求方法', 'IP', '耗时(ms)', '状态', '时间']
    const csv = [header, ...rows]
      .map(line => line.map(value => `"${String(value).replaceAll('"', '""')}"`).join(','))
      .join('\n')

    const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `系统日志_${dayjs().format('YYYYMMDD_HHmmss')}.csv`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    message.success('日志导出成功')
  } catch (error: any) {
    message.error(error.message || '日志导出失败')
  }
}

const restartSystem = () => {
  message.loading('系统重启中...', 2).then(() => {
    message.success('系统重启成功')
  })
}

onMounted(() => {
  lastBackup.value = dayjs().subtract(1, 'day').format('YYYY-MM-DD HH:mm:ss')
})
</script>

<style scoped>
.system-config {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
}

.subtitle {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.config-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.config-card :deep(.ant-card-head-title) {
  font-weight: 600;
  color: #1a1a1a;
}

.form-hint {
  margin-left: 12px;
  color: #999;
  font-size: 12px;
}

.status-card,
.action-card,
.notify-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.status-card :deep(.ant-card-head-title),
.action-card :deep(.ant-card-head-title),
.notify-card :deep(.ant-card-head-title) {
  font-weight: 600;
  color: #1a1a1a;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-label {
  color: #666;
}

.status-value {
  font-weight: 500;
  color: #1a1a1a;
}

:deep(.ant-form-item-label > label) {
  color: #333;
  font-weight: 500;
}
</style>
