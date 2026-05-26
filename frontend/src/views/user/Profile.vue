<template>
  <div class="profile-page">
    <a-row :gutter="[24, 24]">
      <a-col :xs="24" :md="8">
        <a-card class="profile-card">
          <div class="profile-header">
            <a-upload
              name="avatar"
              :showUploadList="false"
              :customRequest="handleAvatarUpload"
            >
              <a-badge :dot="true" :offset="[-10, 60]">
                <a-avatar :size="80" :src="userStore.avatar" class="avatar">
                  {{ userStore.username.charAt(0).toUpperCase() }}
                </a-avatar>
              </a-badge>
            </a-upload>
            <h2>{{ userStore.userInfo?.nickname || userStore.username }}</h2>
            <div class="user-tags">
              <a-tag :color="userStore.isAdmin ? 'red' : 'blue'">
                {{ userStore.isAdmin ? '管理员' : '普通用户' }}
              </a-tag>
              <a-tag v-if="userStore.isActive" color="green">已认证</a-tag>
            </div>
            <p class="user-bio">{{ userStore.userInfo?.bio || '这个人很懒，什么都没写' }}</p>
          </div>
          <a-divider />
          <div class="profile-stats">
            <div class="stat-item" @click="router.push('/following')">
              <div class="stat-value">{{ stats.following }}</div>
              <div class="stat-label">关注</div>
            </div>
            <div class="stat-item" @click="router.push('/following')">
              <div class="stat-value">{{ stats.followers }}</div>
              <div class="stat-label">粉丝</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userStore.creditScore }}</div>
              <div class="stat-label">信誉分</div>
            </div>
          </div>
          <div class="credit-section">
            <div class="credit-label">
              <safety-certificate-outlined /> 信用等级
            </div>
            <a-progress 
              :percent="userStore.creditScore" 
              :strokeColor="creditColor"
              :showInfo="false"
            />
            <span class="credit-text" :style="{ color: creditColor }">{{ creditLevel }}</span>
          </div>
        </a-card>

        <a-card class="quick-actions-card" title="快捷入口">
          <div class="quick-actions">
            <div class="action-item" @click="router.push('/products/mine')">
              <appstore-outlined class="action-icon" />
              <span>我的商品</span>
              <span class="action-count">{{ stats.published }}</span>
            </div>
            <div class="action-item" @click="router.push('/orders')">
              <shopping-outlined class="action-icon" />
              <span>我的订单</span>
              <span class="action-count">{{ stats.orders }}</span>
            </div>
            <div class="action-item" @click="router.push('/favorites')">
              <heart-outlined class="action-icon" />
              <span>我的收藏</span>
              <span class="action-count">{{ stats.favorites }}</span>
            </div>
            <div class="action-item" @click="router.push('/messages')">
              <message-outlined class="action-icon" />
              <span>消息中心</span>
              <a-badge :count="stats.unread" :offset="[10, 0]" />
            </div>
            <div class="action-item" @click="router.push('/feedback')">
              <comment-outlined class="action-icon" />
              <span>意见反馈</span>
            </div>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :md="16">
        <a-card class="main-content-card">
          <a-tabs v-model:activeKey="currentMenu">
            <a-tab-pane key="info" tab="个人信息">
              <a-form
                :model="infoForm"
                @finish="handleUpdateInfo"
                layout="vertical"
                class="info-form"
              >
                <a-row :gutter="24">
                  <a-col :span="12">
                    <a-form-item label="用户名">
                      <a-input :value="userStore.username" disabled />
                    </a-form-item>
                  </a-col>
                  <a-col :span="12">
                    <a-form-item label="学号">
                      <a-input :value="userStore.userInfo?.studentId" disabled />
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-row :gutter="24">
                  <a-col :span="12">
                    <a-form-item name="nickname" label="昵称">
                      <a-input v-model:value="infoForm.nickname" placeholder="请输入昵称" />
                    </a-form-item>
                  </a-col>
                  <a-col :span="12">
                    <a-form-item name="phone" label="手机号">
                      <a-input v-model:value="infoForm.phone" placeholder="请输入手机号" />
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-form-item name="realName" label="真实姓名">
                  <a-input v-model:value="infoForm.realName" placeholder="请输入真实姓名" />
                </a-form-item>
                <a-form-item name="bio" label="个人简介">
                  <a-textarea v-model:value="infoForm.bio" placeholder="介绍一下自己吧" :rows="3" />
                </a-form-item>
                <a-form-item name="school" label="学校">
                  <a-input v-model:value="infoForm.school" placeholder="请输入学校名称" />
                </a-form-item>
                <a-form-item>
                  <a-button type="primary" html-type="submit" :loading="infoLoading">
                    保存修改
                  </a-button>
                </a-form-item>
              </a-form>
            </a-tab-pane>

            <a-tab-pane key="security" tab="账户安全">
              <div class="security-section">
                <div class="security-item">
                  <div class="security-info">
                    <lock-outlined class="security-icon" />
                    <div>
                      <h4>登录密码</h4>
                      <p>定期更换密码可以提高账户安全性</p>
                    </div>
                  </div>
                  <a-button type="link" @click="showPasswordModal = true">修改</a-button>
                </div>
                
                <div class="security-item">
                  <div class="security-info">
                    <mail-outlined class="security-icon" />
                    <div>
                      <h4>邮箱绑定</h4>
                      <p>{{ userStore.userInfo?.email || '未绑定邮箱' }}</p>
                    </div>
                  </div>
                  <a-button type="link" @click="showEmailModal = true">
                    {{ userStore.userInfo?.email ? '更换' : '绑定' }}
                  </a-button>
                </div>

                <div class="security-item">
                  <div class="security-info">
                    <phone-outlined class="security-icon" />
                    <div>
                      <h4>手机绑定</h4>
                      <p>{{ userStore.userInfo?.phone || '未绑定手机' }}</p>
                    </div>
                  </div>
                  <a-button type="link" @click="showPhoneModal = true">
                    {{ userStore.userInfo?.phone ? '更换' : '绑定' }}
                  </a-button>
                </div>
              </div>
            </a-tab-pane>

            <a-tab-pane key="address" tab="收货地址">
              <div class="address-section">
                <div class="address-header">
                  <a-button type="primary" @click="showAddressModal = true">
                    <plus-outlined /> 新增地址
                  </a-button>
                </div>
                <div class="address-list">
                  <div v-for="addr in addresses" :key="addr.id" class="address-item">
                    <div class="address-content">
                      <div class="address-name">
                        {{ addr.receiverName }} {{ addr.receiverPhone }}
                        <a-tag v-if="addr.isDefault" color="blue">默认</a-tag>
                      </div>
                      <div class="address-detail">{{ addr.detail }}</div>
                    </div>
                    <div class="address-actions">
                      <a-button type="link" size="small" @click="editAddress(addr)">编辑</a-button>
                      <a-popconfirm title="确定删除？" @confirm="deleteAddress(addr.id)">
                        <a-button type="link" size="small" danger>删除</a-button>
                      </a-popconfirm>
                    </div>
                  </div>
                </div>
              </div>
            </a-tab-pane>

            <a-tab-pane key="notification" tab="消息通知">
              <div class="notification-settings">
                <div class="setting-item">
                  <div class="setting-info">
                    <span>系统通知</span>
                    <span class="setting-desc">接收系统公告、审核结果等通知</span>
                  </div>
                  <a-switch v-model:checked="notificationSettings.system" />
                </div>
                <div class="setting-item">
                  <div class="setting-info">
                    <span>交易通知</span>
                    <span class="setting-desc">接收订单状态变更、评价提醒等通知</span>
                  </div>
                  <a-switch v-model:checked="notificationSettings.trade" />
                </div>
                <div class="setting-item">
                  <div class="setting-info">
                    <span>私信通知</span>
                    <span class="setting-desc">接收新私信提醒</span>
                  </div>
                  <a-switch v-model:checked="notificationSettings.message" />
                </div>
              </div>
            </a-tab-pane>
          </a-tabs>
        </a-card>
      </a-col>
    </a-row>

    <a-modal
      v-model:open="showPasswordModal"
      title="修改密码"
      @ok="handleChangePassword"
      :confirm-loading="passwordLoading"
    >
      <a-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" layout="vertical">
        <a-form-item name="oldPassword" label="当前密码">
          <a-input-password v-model:value="passwordForm.oldPassword" placeholder="请输入当前密码" />
        </a-form-item>
        <a-form-item name="newPassword" label="新密码">
          <a-input-password v-model:value="passwordForm.newPassword" placeholder="请输入新密码" />
        </a-form-item>
        <a-form-item name="confirmPassword" label="确认新密码">
          <a-input-password v-model:value="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="showAddressModal"
      :title="editingAddress ? '编辑地址' : '新增地址'"
      @ok="handleSaveAddress"
      :confirm-loading="addressLoading"
    >
      <a-form :model="addressForm" layout="vertical">
        <a-form-item label="收货人" required>
          <a-input v-model:value="addressForm.receiverName" placeholder="请输入收货人姓名" />
        </a-form-item>
        <a-form-item label="手机号" required>
          <a-input v-model:value="addressForm.receiverPhone" placeholder="请输入手机号" />
        </a-form-item>
        <a-form-item label="详细地址" required>
          <a-textarea v-model:value="addressForm.detail" placeholder="请输入详细地址" :rows="2" />
        </a-form-item>
        <a-form-item>
          <a-checkbox v-model:checked="addressForm.isDefault">设为默认地址</a-checkbox>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { userApi, uploadApi } from '@/api'
import { followApi } from '@/types/extra'
import type { Rule } from 'ant-design-vue/es/form'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  LockOutlined,
  AppstoreOutlined,
  ShoppingOutlined,
  HeartOutlined,
  MessageOutlined,
  SafetyCertificateOutlined,
  CommentOutlined,
  PlusOutlined,
  MailOutlined,
  PhoneOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const currentMenu = ref('info')
const infoLoading = ref(false)
const passwordLoading = ref(false)
const addressLoading = ref(false)
const showPasswordModal = ref(false)
const showEmailModal = ref(false)
const showPhoneModal = ref(false)
const showAddressModal = ref(false)
const editingAddress = ref<any>(null)
const passwordFormRef = ref()

const stats = reactive({
  published: 0,
  sold: 0,
  bought: 0,
  orders: 0,
  favorites: 0,
  following: 0,
  followers: 0,
  unread: 0
})

const infoForm = reactive({
  nickname: userStore.userInfo?.nickname || '',
  realName: userStore.userInfo?.realName || '',
  phone: userStore.userInfo?.phone || '',
  bio: userStore.userInfo?.bio || '',
  school: userStore.userInfo?.school || ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  detail: '',
  isDefault: false
})

const addresses = ref<any[]>([])

const notificationSettings = reactive({
  system: true,
  trade: true,
  message: true
})

const creditColor = computed(() => {
  const score = userStore.creditScore
  if (score >= 90) return '#52c41a'
  if (score >= 70) return '#1890ff'
  if (score >= 50) return '#faad14'
  return '#f5222d'
})

const creditLevel = computed(() => {
  const score = userStore.creditScore
  if (score >= 90) return '优秀'
  if (score >= 70) return '良好'
  if (score >= 50) return '一般'
  return '较差'
})

const validateConfirmPassword = async (_rule: Rule, value: string) => {
  if (value !== passwordForm.newPassword) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

const passwordRules: Record<string, Rule[]> = {
  oldPassword: [{ required: true, message: '请输入当前密码' }],
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 6, message: '密码长度不能少于6个字符' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码' },
    { validator: validateConfirmPassword }
  ]
}

const fetchStats = async () => {
  try {
    const res = await followApi.getFollowStats(userStore.userInfo!.id)
    stats.following = res.data.followingCount
    stats.followers = res.data.followerCount
  } catch {
    // ignore
  }
}

const handleAvatarUpload = async (options: any) => {
  try {
    const res = await uploadApi.uploadFile(options.file)
    await userApi.updateCurrentUser({ avatar: res.data })
    await userStore.fetchUserInfo()
    message.success('头像更新成功')
  } catch {
    // error handled
  }
}

const handleUpdateInfo = async () => {
  infoLoading.value = true
  try {
    await userApi.updateCurrentUser(infoForm)
    await userStore.fetchUserInfo()
    message.success('信息更新成功')
  } finally {
    infoLoading.value = false
  }
}

const handleChangePassword = async () => {
  try {
    await passwordFormRef.value?.validate()
  } catch {
    return
  }
  
  passwordLoading.value = true
  try {
    await userApi.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    message.success('密码修改成功')
    showPasswordModal.value = false
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } finally {
    passwordLoading.value = false
  }
}

const editAddress = (addr: any) => {
  editingAddress.value = addr
  addressForm.receiverName = addr.receiverName
  addressForm.receiverPhone = addr.receiverPhone
  addressForm.detail = addr.detail
  addressForm.isDefault = addr.isDefault
  showAddressModal.value = true
}

const handleSaveAddress = async () => {
  addressLoading.value = true
  try {
    if (editingAddress.value) {
      message.success('地址更新成功')
    } else {
      message.success('地址添加成功')
    }
    showAddressModal.value = false
    editingAddress.value = null
    addressForm.receiverName = ''
    addressForm.receiverPhone = ''
    addressForm.detail = ''
    addressForm.isDefault = false
  } finally {
    addressLoading.value = false
  }
}

const deleteAddress = async (id: number) => {
  message.success('地址删除成功')
}

onMounted(() => {
  infoForm.nickname = userStore.userInfo?.nickname || ''
  infoForm.realName = userStore.userInfo?.realName || ''
  infoForm.phone = userStore.userInfo?.phone || ''
  infoForm.bio = userStore.userInfo?.bio || ''
  infoForm.school = userStore.userInfo?.school || ''
  fetchStats()
})
</script>

<style scoped>
.profile-page {
  background: #f5f5f5;
  min-height: calc(100vh - 200px);
}

.profile-card {
  border-radius: 12px;
  text-align: center;
}

.profile-header {
  padding: 24px 0;
}

.avatar {
  cursor: pointer;
  margin-bottom: 16px;
}

.profile-header h2 {
  margin: 0 0 8px;
  font-size: 20px;
}

.user-tags {
  margin-bottom: 8px;
}

.user-bio {
  color: #999;
  font-size: 14px;
  margin: 0;
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.stat-item {
  text-align: center;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: background 0.2s;
}

.stat-item:hover {
  background: #f5f5f5;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #1890ff;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.credit-section {
  text-align: left;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.credit-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.credit-text {
  font-size: 14px;
  font-weight: 500;
  margin-top: 4px;
}

.quick-actions-card {
  border-radius: 12px;
  margin-top: 16px;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.action-item:hover {
  background: #f5f5f5;
}

.action-icon {
  font-size: 18px;
  color: #1890ff;
}

.action-item span {
  flex: 1;
}

.action-count {
  color: #999;
  font-size: 14px;
}

.main-content-card {
  border-radius: 12px;
}

.info-form {
  max-width: 600px;
}

.security-section {
  max-width: 600px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.security-item:last-child {
  border-bottom: none;
}

.security-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.security-icon {
  font-size: 24px;
  color: #1890ff;
}

.security-info h4 {
  margin: 0 0 4px;
  font-size: 14px;
}

.security-info p {
  margin: 0;
  font-size: 13px;
  color: #999;
}

.address-section {
  max-width: 600px;
}

.address-header {
  margin-bottom: 16px;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.address-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.address-detail {
  font-size: 13px;
  color: #666;
}

.notification-settings {
  max-width: 600px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.setting-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.setting-desc {
  font-size: 13px;
  color: #999;
}

@media (max-width: 768px) {
  .profile-stats {
    grid-template-columns: repeat(3, 1fr);
  }

  .stat-value {
    font-size: 20px;
  }

  .info-form :deep(.ant-row) {
    flex-direction: column;
  }

  .info-form :deep(.ant-col) {
    width: 100%;
  }
}
</style>
