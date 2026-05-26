<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">通知管理</h1>
      <p class="text-gray-600 mt-1">管理系统通知和消息推送</p>
    </div>

    <a-card class="mb-6">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-statistic title="今日通知" :value="todayNotifications" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="未读通知" :value="unreadNotifications" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="已发送" :value="sentNotifications" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="发送成功率" :value="successRate" suffix="%" />
        </a-col>
      </a-row>
    </a-card>

    <a-tabs v-model:activeKey="activeTab">
      <a-tab-pane key="list" tab="通知列表">
        <a-card>
          <a-form layout="inline" class="mb-4">
            <a-form-item label="通知类型">
              <a-select v-model:value="searchForm.type" placeholder="选择类型" style="width: 150px" allow-clear>
                <a-select-option value="SYSTEM">系统通知</a-select-option>
                <a-select-option value="COURSE">课程通知</a-select-option>
                <a-select-option value="PAYMENT">支付通知</a-select-option>
                <a-select-option value="PROMOTION">活动推广</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="接收对象">
              <a-select v-model:value="searchForm.target" placeholder="选择对象" style="width: 150px" allow-clear>
                <a-select-option value="ALL">全部用户</a-select-option>
                <a-select-option value="STUDENT">学员</a-select-option>
                <a-select-option value="TEACHER">教师</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="发送状态">
              <a-select v-model:value="searchForm.status" placeholder="选择状态" style="width: 120px" allow-clear>
                <a-select-option value="PENDING">待发送</a-select-option>
                <a-select-option value="SENT">已发送</a-select-option>
                <a-select-option value="FAILED">发送失败</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item>
              <a-space>
                <a-button type="primary" @click="handleSearch">搜索</a-button>
                <a-button @click="handleReset">重置</a-button>
                <a-button type="primary" @click="showSendModal">
                  <template #icon><SendOutlined /></template>
                  发送通知
                </a-button>
              </a-space>
            </a-form-item>
          </a-form>

          <a-table
            :columns="columns"
            :data-source="notifications"
            :pagination="pagination"
            :loading="loading"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'type'">
                <a-tag :color="getTypeColor(record.type)">
                  {{ getTypeText(record.type) }}
                </a-tag>
              </template>
              <template v-if="column.key === 'target'">
                <a-tag>{{ getTargetText(record.target) }}</a-tag>
              </template>
              <template v-if="column.key === 'status'">
                <a-tag :color="getStatusColor(record.status)">
                  {{ getStatusText(record.status) }}
                </a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-space>
                  <a-button type="link" size="small" @click="viewDetail(record)">详情</a-button>
                  <a-button 
                    type="link" 
                    size="small" 
                    v-if="record.status === 'PENDING'"
                    @click="sendNow(record)"
                  >
                    立即发送
                  </a-button>
                  <a-popconfirm
                    title="确定要删除这条通知吗？"
                    @confirm="deleteNotification(record)"
                    ok-text="确定"
                    cancel-text="取消"
                  >
                    <a-button type="link" size="small" danger>删除</a-button>
                  </a-popconfirm>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-tab-pane>

      <a-tab-pane key="templates" tab="通知模板">
        <a-card>
          <a-button type="primary" class="mb-4" @click="showTemplateModal">
            <template #icon><PlusOutlined /></template>
            新建模板
          </a-button>

          <a-table :columns="templateColumns" :data-source="templates">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'type'">
                {{ getTypeText(record.type) }}
              </template>
              <template v-if="column.key === 'action'">
                <a-space>
                  <a-button type="link" size="small" @click="editTemplate(record)">编辑</a-button>
                  <a-button type="link" size="small" @click="useTemplate(record)">使用</a-button>
                  <a-popconfirm
                    title="确定要删除这个模板吗？"
                    @confirm="deleteTemplate(record)"
                    ok-text="确定"
                    cancel-text="取消"
                  >
                    <a-button type="link" size="small" danger>删除</a-button>
                  </a-popconfirm>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-tab-pane>

      <a-tab-pane key="history" tab="发送历史">
        <a-card>
          <a-table :columns="historyColumns" :data-source="historyList">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="record.success ? 'green' : 'red'">
                  {{ record.success ? '成功' : '失败' }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-tab-pane>
    </a-tabs>

    <a-modal
      v-model:open="sendModalVisible"
      title="发送通知"
      width="600px"
      @ok="handleSend"
    >
      <a-form
        :model="notificationForm"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="通知标题" required>
          <a-input v-model:value="notificationForm.title" placeholder="请输入通知标题" />
        </a-form-item>
        <a-form-item label="通知类型" required>
          <a-select v-model:value="notificationForm.type" placeholder="请选择通知类型">
            <a-select-option value="SYSTEM">系统通知</a-select-option>
            <a-select-option value="COURSE">课程通知</a-select-option>
            <a-select-option value="PAYMENT">支付通知</a-select-option>
            <a-select-option value="PROMOTION">活动推广</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="接收对象" required>
          <a-select v-model:value="notificationForm.target" placeholder="请选择接收对象">
            <a-select-option value="ALL">全部用户</a-select-option>
            <a-select-option value="STUDENT">学员</a-select-option>
            <a-select-option value="TEACHER">教师</a-select-option>
            <a-select-option value="SPECIFIC">指定用户</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="选择用户" v-if="notificationForm.target === 'SPECIFIC'">
          <a-select
            v-model:value="notificationForm.userIds"
            mode="multiple"
            placeholder="请选择用户"
            :options="userOptions"
          />
        </a-form-item>
        <a-form-item label="通知内容" required>
          <a-textarea v-model:value="notificationForm.content" :rows="4" placeholder="请输入通知内容" />
        </a-form-item>
        <a-form-item label="发送方式">
          <a-checkbox-group v-model:value="notificationForm.methods">
            <a-checkbox value="SMS">短信</a-checkbox>
            <a-checkbox value="EMAIL">邮件</a-checkbox>
            <a-checkbox value="WECHAT">微信</a-checkbox>
            <a-checkbox value="APP">APP推送</a-checkbox>
          </a-checkbox-group>
        </a-form-item>
        <a-form-item label="发送时间">
          <a-radio-group v-model:value="notificationForm.sendType">
            <a-radio value="NOW">立即发送</a-radio>
            <a-radio value="SCHEDULE">定时发送</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="定时时间" v-if="notificationForm.sendType === 'SCHEDULE'">
          <a-date-picker
            v-model:value="notificationForm.scheduledTime"
            show-time
            format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="templateModalVisible"
      :title="editingTemplate ? '编辑模板' : '新建模板'"
      width="600px"
      @ok="handleTemplateSubmit"
    >
      <a-form
        :model="templateForm"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="模板名称" required>
          <a-input v-model:value="templateForm.name" placeholder="请输入模板名称" />
        </a-form-item>
        <a-form-item label="通知类型" required>
          <a-select v-model:value="templateForm.type" placeholder="请选择通知类型">
            <a-select-option value="SYSTEM">系统通知</a-select-option>
            <a-select-option value="COURSE">课程通知</a-select-option>
            <a-select-option value="PAYMENT">支付通知</a-select-option>
            <a-select-option value="PROMOTION">活动推广</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="模板标题" required>
          <a-input v-model:value="templateForm.title" placeholder="请输入模板标题" />
        </a-form-item>
        <a-form-item label="模板内容" required>
          <a-textarea v-model:value="templateForm.content" :rows="4" placeholder="请输入模板内容，可使用{姓名}等变量" />
        </a-form-item>
        <a-form-item label="可用变量">
          <div class="text-sm text-gray-500">
            <div>• {'{姓名}'} - 用户姓名</div>
            <div>• {'{课程名}'} - 课程名称</div>
            <div>• {'{时间}'} - 时间信息</div>
            <div>• {'{金额}'} - 金额信息</div>
          </div>
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailModalVisible"
      title="通知详情"
      width="700px"
      :footer="null"
    >
      <a-descriptions :column="2" bordered v-if="currentNotification">
        <a-descriptions-item label="通知标题" :span="2">{{ currentNotification.title }}</a-descriptions-item>
        <a-descriptions-item label="通知类型">
          <a-tag :color="getTypeColor(currentNotification.type)">
            {{ getTypeText(currentNotification.type) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="接收对象">
          <a-tag>{{ getTargetText(currentNotification.target) }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="发送状态">
          <a-tag :color="getStatusColor(currentNotification.status)">
            {{ getStatusText(currentNotification.status) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="发送时间">{{ currentNotification.sentAt }}</a-descriptions-item>
        <a-descriptions-item label="通知内容" :span="2">{{ currentNotification.content }}</a-descriptions-item>
        <a-descriptions-item label="发送方式" :span="2">
          <a-tag v-for="method in currentNotification.methods" :key="method">{{ method }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="发送统计" :span="2">
          <a-row :gutter="16">
            <a-col :span="8">
              <a-statistic title="发送总数" :value="currentNotification.totalCount" />
            </a-col>
            <a-col :span="8">
              <a-statistic title="成功数" :value="currentNotification.successCount" />
            </a-col>
            <a-col :span="8">
              <a-statistic title="失败数" :value="currentNotification.failCount" />
            </a-col>
          </a-row>
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { SendOutlined, PlusOutlined } from '@ant-design/icons-vue'

interface Notification {
  id: number
  title: string
  type: string
  target: string
  content: string
  status: string
  sentAt: string
  methods: string[]
  totalCount: number
  successCount: number
  failCount: number
}

const activeTab = ref('list')
const searchForm = reactive({
  type: undefined,
  target: undefined,
  status: undefined
})

const notificationForm = reactive({
  title: '',
  type: undefined,
  target: undefined,
  userIds: [],
  content: '',
  methods: ['APP'],
  sendType: 'NOW',
  scheduledTime: null
})

const templateForm = reactive({
  name: '',
  type: undefined,
  title: '',
  content: ''
})

const notifications = ref<Notification[]>([])
const allNotifications = ref<Notification[]>([])
const templates = ref<any[]>([])
const allTemplates = ref<any[]>([])
const historyList = ref<any[]>([])
const loading = ref(false)
const sendModalVisible = ref(false)
const templateModalVisible = ref(false)
const detailModalVisible = ref(false)
const editingTemplate = ref<any>(null)
const currentNotification = ref<Notification | null>(null)

const todayNotifications = ref(15)
const unreadNotifications = ref(32)
const sentNotifications = ref(1280)
const successRate = ref(98)

const userOptions = ref([
  { label: '张三', value: 1 },
  { label: '李四', value: 2 },
  { label: '王五', value: 3 }
])

const columns = [
  { title: '通知标题', dataIndex: 'title', key: 'title' },
  { title: '通知类型', key: 'type' },
  { title: '接收对象', key: 'target' },
  { title: '发送状态', key: 'status' },
  { title: '发送时间', dataIndex: 'sentAt', key: 'sentAt' },
  { title: '操作', key: 'action', fixed: 'right' }
]

const templateColumns = [
  { title: '模板名称', dataIndex: 'name', key: 'name' },
  { title: '通知类型', dataIndex: 'type', key: 'type' },
  { title: '模板标题', dataIndex: 'title', key: 'title' },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt' },
  { title: '操作', key: 'action' }
]

const historyColumns = [
  { title: '通知标题', dataIndex: 'title', key: 'title' },
  { title: '接收人', dataIndex: 'receiver', key: 'receiver' },
  { title: '发送方式', dataIndex: 'method', key: 'method' },
  { title: '发送时间', dataIndex: 'sentAt', key: 'sentAt' },
  { title: '状态', key: 'status' }
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const getTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    SYSTEM: 'blue',
    COURSE: 'green',
    PAYMENT: 'orange',
    PROMOTION: 'purple'
  }
  return colorMap[type] || 'default'
}

const getTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    SYSTEM: '系统通知',
    COURSE: '课程通知',
    PAYMENT: '支付通知',
    PROMOTION: '活动推广'
  }
  return textMap[type] || type
}

const getTargetText = (target: string) => {
  const textMap: Record<string, string> = {
    ALL: '全部用户',
    STUDENT: '学员',
    TEACHER: '教师',
    SPECIFIC: '指定用户'
  }
  return textMap[target] || target
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    PENDING: 'orange',
    SENT: 'green',
    FAILED: 'red'
  }
  return colorMap[status] || 'default'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    PENDING: '待发送',
    SENT: '已发送',
    FAILED: '发送失败'
  }
  return textMap[status] || status
}

const loadNotifications = async () => {
  loading.value = true
  try {
    const mockNotifications: Notification[] = [
      {
        id: 1,
        title: '课程开课提醒',
        type: 'COURSE',
        target: 'STUDENT',
        content: '您报名的Hip-Hop基础班将于明天18:00开课，请准时参加。',
        status: 'SENT',
        sentAt: '2024-02-19 10:00:00',
        methods: ['SMS', 'APP'],
        totalCount: 18,
        successCount: 18,
        failCount: 0
      },
      {
        id: 2,
        title: '系统维护通知',
        type: 'SYSTEM',
        target: 'ALL',
        content: '系统将于今晚22:00-23:00进行维护升级，届时系统将暂停服务。',
        status: 'PENDING',
        sentAt: '',
        methods: ['APP', 'EMAIL'],
        totalCount: 0,
        successCount: 0,
        failCount: 0
      }
    ]
    allNotifications.value = mockNotifications
    applyNotificationFilters()
  } catch (error) {
    message.error('加载通知列表失败')
  } finally {
    loading.value = false
  }
}

const applyNotificationFilters = () => {
  let list = [...allNotifications.value]
  if (searchForm.type) {
    list = list.filter(item => item.type === searchForm.type)
  }
  if (searchForm.target) {
    list = list.filter(item => item.target === searchForm.target)
  }
  if (searchForm.status) {
    list = list.filter(item => item.status === searchForm.status)
  }
  notifications.value = list
  pagination.total = list.length
}

const loadTemplates = async () => {
  if (!allTemplates.value.length) {
    allTemplates.value = [
      {
        id: 1,
        name: '开课提醒模板',
        type: 'COURSE',
        title: '课程开课提醒',
        content: '尊敬的{姓名}，您报名的{课程名}将于{时间}开课，请准时参加。',
        createdAt: '2024-02-15 10:00:00'
      },
      {
        id: 2,
        name: '支付成功模板',
        type: 'PAYMENT',
        title: '支付成功通知',
        content: '尊敬的{姓名}，您已成功支付{金额}元，感谢您的支持。',
        createdAt: '2024-02-15 10:00:00'
      }
    ]
  }
  templates.value = [...allTemplates.value]
}

const loadHistory = async () => {
  historyList.value = [
    {
      id: 1,
      title: '课程开课提醒',
      receiver: '张三',
      method: '短信',
      sentAt: '2024-02-19 10:00:00',
      success: true
    },
    {
      id: 2,
      title: '课程开课提醒',
      receiver: '李四',
      method: 'APP推送',
      sentAt: '2024-02-19 10:00:00',
      success: true
    }
  ]
}

const handleSearch = () => {
  pagination.current = 1
  applyNotificationFilters()
}

const handleReset = () => {
  Object.assign(searchForm, {
    type: undefined,
    target: undefined,
    status: undefined
  })
  pagination.current = 1
  applyNotificationFilters()
}

const showSendModal = () => {
  Object.assign(notificationForm, {
    title: '',
    type: undefined,
    target: undefined,
    userIds: [],
    content: '',
    methods: ['APP'],
    sendType: 'NOW',
    scheduledTime: null
  })
  sendModalVisible.value = true
}

const showTemplateModal = () => {
  editingTemplate.value = null
  Object.assign(templateForm, {
    name: '',
    type: undefined,
    title: '',
    content: ''
  })
  templateModalVisible.value = true
}

const viewDetail = (notification: Notification) => {
  currentNotification.value = notification
  detailModalVisible.value = true
}

const sendNow = (notification: Notification) => {
  message.loading('正在发送通知...', 1).then(() => {
    const target = allNotifications.value.find(item => item.id === notification.id)
    if (target) {
      target.status = 'SENT'
      target.sentAt = new Date().toLocaleString('zh-CN', { hour12: false }).replace(/\//g, '-')
    }
    message.success('通知发送成功')
    applyNotificationFilters()
  })
}

const deleteNotification = (notification: Notification) => {
  allNotifications.value = allNotifications.value.filter(item => item.id !== notification.id)
  applyNotificationFilters()
  message.success(`已删除通知：${notification.title}`)
}

const handleSend = () => {
  if (!notificationForm.title || !notificationForm.type || !notificationForm.target || !notificationForm.content) {
    message.warning('请填写完整的通知信息')
    return
  }
  const nextId = allNotifications.value.length ? Math.max(...allNotifications.value.map(item => item.id)) + 1 : 1
  allNotifications.value.unshift({
    id: nextId,
    title: notificationForm.title,
    type: notificationForm.type as string,
    target: notificationForm.target as string,
    content: notificationForm.content,
    status: notificationForm.sendType === 'NOW' ? 'SENT' : 'PENDING',
    sentAt: notificationForm.sendType === 'NOW'
      ? new Date().toLocaleString('zh-CN', { hour12: false }).replace(/\//g, '-')
      : '',
    methods: [...notificationForm.methods],
    totalCount: notificationForm.target === 'SPECIFIC' ? notificationForm.userIds.length : userOptions.value.length,
    successCount: notificationForm.sendType === 'NOW'
      ? (notificationForm.target === 'SPECIFIC' ? notificationForm.userIds.length : userOptions.value.length)
      : 0,
    failCount: 0
  })
  message.success('通知发送成功')
  sendModalVisible.value = false
  applyNotificationFilters()
}

const editTemplate = (template: any) => {
  editingTemplate.value = template
  Object.assign(templateForm, {
    name: template.name,
    type: template.type,
    title: template.title,
    content: template.content
  })
  templateModalVisible.value = true
}

const useTemplate = (template: any) => {
  notificationForm.title = template.title
  notificationForm.content = template.content
  sendModalVisible.value = true
}

const deleteTemplate = (template: any) => {
  allTemplates.value = allTemplates.value.filter(item => item.id !== template.id)
  templates.value = [...allTemplates.value]
  message.success(`已删除模板：${template.name}`)
}

const handleTemplateSubmit = () => {
  if (!templateForm.name || !templateForm.type || !templateForm.title || !templateForm.content) {
    message.warning('请填写完整的模板信息')
    return
  }
  if (editingTemplate.value) {
    const target = allTemplates.value.find(item => item.id === editingTemplate.value.id)
    if (target) {
      target.name = templateForm.name
      target.type = templateForm.type
      target.title = templateForm.title
      target.content = templateForm.content
    }
  } else {
    const now = new Date()
    const createdAt = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`
    const nextId = allTemplates.value.length ? Math.max(...allTemplates.value.map(item => item.id)) + 1 : 1
    allTemplates.value.unshift({
      id: nextId,
      name: templateForm.name,
      type: templateForm.type,
      title: templateForm.title,
      content: templateForm.content,
      createdAt
    })
  }
  templates.value = [...allTemplates.value]
  message.success(editingTemplate.value ? '模板修改成功' : '模板创建成功')
  templateModalVisible.value = false
}

onMounted(() => {
  loadNotifications()
  loadTemplates()
  loadHistory()
})
</script>
