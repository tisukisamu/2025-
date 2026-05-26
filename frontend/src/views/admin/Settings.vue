<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">系统设置</h1>
      <p class="text-gray-600 mt-1">管理系统参数和配置</p>
    </div>

    <a-tabs v-model:activeKey="activeTab">
      <a-tab-pane key="basic" tab="基础设置">
        <a-card>
          <a-form
            :model="basicSettings"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 16 }"
          >
            <a-form-item label="系统名称">
              <a-input v-model:value="basicSettings.systemName" placeholder="请输入系统名称" />
            </a-form-item>
            <a-form-item label="系统Logo">
              <a-upload
                list-type="picture-card"
                :file-list="logoFileList"
                :before-upload="beforeUpload"
                @change="handleLogoChange"
              >
                <div v-if="logoFileList.length < 1" class="text-center">
                  <PlusOutlined />
                  <div style="margin-top: 8px">上传Logo</div>
                </div>
              </a-upload>
            </a-form-item>
            <a-form-item label="联系电话">
              <a-input v-model:value="basicSettings.phone" placeholder="请输入联系电话" />
            </a-form-item>
            <a-form-item label="联系邮箱">
              <a-input v-model:value="basicSettings.email" placeholder="请输入联系邮箱" />
            </a-form-item>
            <a-form-item label="联系地址">
              <a-textarea v-model:value="basicSettings.address" :rows="2" placeholder="请输入联系地址" />
            </a-form-item>
            <a-form-item label="营业时间">
              <a-input v-model:value="basicSettings.businessHours" placeholder="例如：周一至周日 9:00-21:00" />
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
              <a-button type="primary" @click="saveBasicSettings">保存设置</a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-tab-pane>

      <a-tab-pane key="course" tab="课程设置">
        <a-card>
          <a-form
            :model="courseSettings"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 16 }"
          >
            <a-form-item label="默认课时">
              <a-input-number v-model:value="courseSettings.defaultDuration" :min="1" />
              <span class="ml-2 text-gray-500">节</span>
            </a-form-item>
            <a-form-item label="最大报名人数">
              <a-input-number v-model:value="courseSettings.maxStudents" :min="1" />
              <span class="ml-2 text-gray-500">人</span>
            </a-form-item>
            <a-form-item label="允许试听">
              <a-switch v-model:checked="courseSettings.allowTrial" />
            </a-form-item>
            <a-form-item label="试听课时" v-if="courseSettings.allowTrial">
              <a-input-number v-model:value="courseSettings.trialLessons" :min="1" />
              <span class="ml-2 text-gray-500">节</span>
            </a-form-item>
            <a-form-item label="取消报名时限">
              <a-input-number v-model:value="courseSettings.cancelDeadline" :min="0" />
              <span class="ml-2 text-gray-500">小时（开课前）</span>
            </a-form-item>
            <a-form-item label="自动结课">
              <a-switch v-model:checked="courseSettings.autoComplete" />
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
              <a-button type="primary" @click="saveCourseSettings">保存设置</a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-tab-pane>

      <a-tab-pane key="payment" tab="支付设置">
        <a-card>
          <a-form
            :model="paymentSettings"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 16 }"
          >
            <a-form-item label="支付方式">
              <a-checkbox-group v-model:value="paymentSettings.methods">
                <a-checkbox value="CASH">现金</a-checkbox>
                <a-checkbox value="WECHAT">微信支付</a-checkbox>
                <a-checkbox value="ALIPAY">支付宝</a-checkbox>
                <a-checkbox value="BANK">银行转账</a-checkbox>
              </a-checkbox-group>
            </a-form-item>
            <a-form-item label="定金比例">
              <a-slider v-model:value="paymentSettings.depositRate" :min="0" :max="100" />
              <span class="ml-2">{{ paymentSettings.depositRate }}%</span>
            </a-form-item>
            <a-form-item label="退款政策">
              <a-textarea v-model:value="paymentSettings.refundPolicy" :rows="3" placeholder="请输入退款政策说明" />
            </a-form-item>
            <a-form-item label="发票信息">
              <a-textarea v-model:value="paymentSettings.invoiceInfo" :rows="2" placeholder="请输入发票开具信息" />
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
              <a-button type="primary" @click="savePaymentSettings">保存设置</a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-tab-pane>

      <a-tab-pane key="notification" tab="通知设置">
        <a-card>
          <a-form
            :model="notificationSettings"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 16 }"
          >
            <a-form-item label="短信通知">
              <a-switch v-model:checked="notificationSettings.smsEnabled" />
            </a-form-item>
            <a-form-item label="邮件通知">
              <a-switch v-model:checked="notificationSettings.emailEnabled" />
            </a-form-item>
            <a-form-item label="微信通知">
              <a-switch v-model:checked="notificationSettings.wechatEnabled" />
            </a-form-item>
            <a-divider>通知场景</a-divider>
            <a-form-item label="报名成功">
              <a-checkbox-group v-model:value="notificationSettings.enrollmentNotify">
                <a-checkbox value="SMS">短信</a-checkbox>
                <a-checkbox value="EMAIL">邮件</a-checkbox>
                <a-checkbox value="WECHAT">微信</a-checkbox>
              </a-checkbox-group>
            </a-form-item>
            <a-form-item label="上课提醒">
              <a-checkbox-group v-model:value="notificationSettings.classReminder">
                <a-checkbox value="SMS">短信</a-checkbox>
                <a-checkbox value="EMAIL">邮件</a-checkbox>
                <a-checkbox value="WECHAT">微信</a-checkbox>
              </a-checkbox-group>
            </a-form-item>
            <a-form-item label="提前提醒时间">
              <a-input-number v-model:value="notificationSettings.reminderHours" :min="1" />
              <span class="ml-2 text-gray-500">小时</span>
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
              <a-button type="primary" @click="saveNotificationSettings">保存设置</a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-tab-pane>

      <a-tab-pane key="security" tab="安全设置">
        <a-card>
          <a-form
            :model="securitySettings"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 16 }"
          >
            <a-form-item label="密码最小长度">
              <a-input-number v-model:value="securitySettings.minPasswordLength" :min="6" :max="20" />
              <span class="ml-2 text-gray-500">位</span>
            </a-form-item>
            <a-form-item label="密码强度要求">
              <a-checkbox-group v-model:value="securitySettings.passwordRequirements">
                <a-checkbox value="NUMBER">包含数字</a-checkbox>
                <a-checkbox value="LETTER">包含字母</a-checkbox>
                <a-checkbox value="SPECIAL">包含特殊字符</a-checkbox>
              </a-checkbox-group>
            </a-form-item>
            <a-form-item label="登录失败锁定">
              <a-input-number v-model:value="securitySettings.maxLoginAttempts" :min="3" :max="10" />
              <span class="ml-2 text-gray-500">次后锁定账户</span>
            </a-form-item>
            <a-form-item label="锁定时长">
              <a-input-number v-model:value="securitySettings.lockDuration" :min="5" />
              <span class="ml-2 text-gray-500">分钟</span>
            </a-form-item>
            <a-form-item label="强制修改密码">
              <a-switch v-model:checked="securitySettings.forcePasswordChange" />
              <span class="ml-2 text-gray-500">首次登录强制修改密码</span>
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
              <a-button type="primary" @click="saveSecuritySettings">保存设置</a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-tab-pane>

      <a-tab-pane key="backup" tab="备份恢复">
        <a-card>
          <a-alert
            message="数据备份"
            description="定期备份系统数据，确保数据安全。建议每周至少备份一次。"
            type="info"
            show-icon
            class="mb-4"
          />
          
          <a-row :gutter="16" class="mb-4">
            <a-col :span="8">
              <a-statistic title="最近备份时间" value="2024-02-20 10:30" />
            </a-col>
            <a-col :span="8">
              <a-statistic title="备份文件大小" value="256" suffix="MB" />
            </a-col>
            <a-col :span="8">
              <a-statistic title="备份数量" value="12" suffix="个" />
            </a-col>
          </a-row>

          <a-space class="mb-4">
            <a-button type="primary" @click="createBackup">
              <template #icon><CloudUploadOutlined /></template>
              立即备份
            </a-button>
            <a-button @click="showRestoreModal">
              <template #icon><CloudDownloadOutlined /></template>
              恢复数据
            </a-button>
          </a-space>

          <a-table :columns="backupColumns" :data-source="backupList" :pagination="false">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'action'">
                <a-space>
                  <a-button type="link" size="small" @click="downloadBackup(record)">下载</a-button>
                  <a-button type="link" size="small" @click="restoreBackup(record)">恢复</a-button>
                  <a-popconfirm
                    title="确定要删除这个备份吗？"
                    @confirm="deleteBackup(record)"
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
    </a-tabs>

    <a-modal
      v-model:open="restoreModalVisible"
      title="恢复数据"
      @ok="handleRestore"
    >
      <a-alert
        message="警告"
        description="恢复数据将覆盖当前系统数据，此操作不可逆，请谨慎操作！"
        type="warning"
        show-icon
        class="mb-4"
      />
      <a-upload-dragger>
        <p class="ant-upload-drag-icon">
          <InboxOutlined />
        </p>
        <p class="ant-upload-text">点击或拖拽文件到此区域上传</p>
        <p class="ant-upload-hint">支持 .sql 或 .zip 格式的备份文件</p>
      </a-upload-dragger>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { 
  PlusOutlined,
  CloudUploadOutlined,
  CloudDownloadOutlined,
  InboxOutlined
} from '@ant-design/icons-vue'
import type { UploadFile } from 'ant-design-vue'

const activeTab = ref('basic')

const basicSettings = reactive({
  systemName: '街舞工作室管理系统',
  phone: '400-123-4567',
  email: 'contact@dancestudio.com',
  address: '北京市朝阳区xxx街道xxx号',
  businessHours: '周一至周日 9:00-21:00'
})

const courseSettings = reactive({
  defaultDuration: 24,
  maxStudents: 20,
  allowTrial: true,
  trialLessons: 1,
  cancelDeadline: 24,
  autoComplete: true
})

const paymentSettings = reactive({
  methods: ['CASH', 'WECHAT', 'ALIPAY'],
  depositRate: 30,
  refundPolicy: '开课前7天可全额退款，开课前3天退款50%，开课后不予退款',
  invoiceInfo: '可开具增值税普通发票'
})

const notificationSettings = reactive({
  smsEnabled: true,
  emailEnabled: true,
  wechatEnabled: false,
  enrollmentNotify: ['SMS', 'WECHAT'],
  classReminder: ['SMS', 'WECHAT'],
  reminderHours: 2
})

const securitySettings = reactive({
  minPasswordLength: 8,
  passwordRequirements: ['NUMBER', 'LETTER'],
  maxLoginAttempts: 5,
  lockDuration: 30,
  forcePasswordChange: true
})

const logoFileList = ref<UploadFile[]>([])
const restoreModalVisible = ref(false)

const backupColumns = [
  { title: '备份名称', dataIndex: 'name', key: 'name' },
  { title: '备份时间', dataIndex: 'time', key: 'time' },
  { title: '文件大小', dataIndex: 'size', key: 'size' },
  { title: '备份类型', dataIndex: 'type', key: 'type' },
  { title: '操作', key: 'action' }
]

const backupList = ref([
  {
    id: 1,
    name: '系统自动备份_20240220',
    time: '2024-02-20 10:30:00',
    size: '256 MB',
    type: '自动备份'
  },
  {
    id: 2,
    name: '手动备份_20240218',
    time: '2024-02-18 15:20:00',
    size: '248 MB',
    type: '手动备份'
  }
])

const beforeUpload = (file: File) => {
  return false
}

const handleLogoChange = ({ fileList: newFileList }: { fileList: UploadFile[] }) => {
  logoFileList.value = newFileList
}

const saveBasicSettings = () => {
  message.success('基础设置保存成功')
}

const saveCourseSettings = () => {
  message.success('课程设置保存成功')
}

const savePaymentSettings = () => {
  message.success('支付设置保存成功')
}

const saveNotificationSettings = () => {
  message.success('通知设置保存成功')
}

const saveSecuritySettings = () => {
  message.success('安全设置保存成功')
}

const createBackup = () => {
  message.loading('正在创建备份...', 2).then(() => {
    message.success('备份创建成功')
  })
}

const showRestoreModal = () => {
  restoreModalVisible.value = true
}

const handleRestore = () => {
  message.loading('正在恢复数据...', 2).then(() => {
    message.success('数据恢复成功')
    restoreModalVisible.value = false
  })
}

const downloadBackup = (record: any) => {
  message.info(`正在下载备份：${record.name}`)
}

const restoreBackup = (record: any) => {
  message.loading('正在恢复备份...', 2).then(() => {
    message.success('备份恢复成功')
  })
}

const deleteBackup = (record: any) => {
  message.success(`已删除备份：${record.name}`)
}
</script>
