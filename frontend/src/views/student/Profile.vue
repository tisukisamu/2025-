<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">个人中心</h1>
      <p class="text-gray-600 mt-1">管理个人信息和账户设置</p>
    </div>

    <a-row :gutter="16">
      <a-col :xs="24" :lg="8">
        <a-card>
          <div class="text-center">
            <a-avatar :size="100" :src="userInfo.avatar">
              <template #icon><UserOutlined /></template>
            </a-avatar>
            <h2 class="text-xl font-bold mt-4 mb-2">{{ userInfo.realName }}</h2>
            <p class="text-gray-600">{{ userInfo.phone }}</p>
            <a-button type="link" @click="showAvatarModal">
              <UploadOutlined /> 更换头像
            </a-button>
          </div>

          <a-divider />

          <div class="user-stats">
            <a-row :gutter="16">
              <a-col :span="8">
                <a-statistic title="已学课程" :value="userInfo.learnedCourses" />
              </a-col>
              <a-col :span="8">
                <a-statistic title="学习时长" :value="userInfo.learningHours" suffix="小时" />
              </a-col>
              <a-col :span="8">
                <a-statistic title="获得证书" :value="userInfo.certificates" />
              </a-col>
            </a-row>
          </div>
        </a-card>

        <a-card class="mt-4" title="账户安全">
          <a-space direction="vertical" style="width: 100%">
            <div class="flex justify-between items-center">
              <span>登录密码</span>
              <a-button type="link" @click="showPasswordModal">修改</a-button>
            </div>
            <a-divider style="margin: 8px 0" />
            <div class="flex justify-between items-center">
              <span>手机绑定</span>
              <span>{{ userInfo.phone }}</span>
            </div>
            <a-divider style="margin: 8px 0" />
            <div class="flex justify-between items-center">
              <span>邮箱绑定</span>
              <span>{{ userInfo.email || '未绑定' }}</span>
            </div>
          </a-space>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="16">
        <a-card title="基本信息">
          <a-form
            :model="userInfo"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 16 }"
          >
            <a-form-item label="姓名">
              <a-input v-model:value="userInfo.realName" placeholder="请输入姓名" />
            </a-form-item>
            <a-form-item label="性别">
              <a-radio-group v-model:value="userInfo.gender">
                <a-radio value="MALE">男</a-radio>
                <a-radio value="FEMALE">女</a-radio>
              </a-radio-group>
            </a-form-item>
            <a-form-item label="出生日期">
              <a-date-picker v-model:value="userInfo.birthday" style="width: 100%" />
            </a-form-item>
            <a-form-item label="手机号码">
              <a-input v-model:value="userInfo.phone" placeholder="请输入手机号码" />
            </a-form-item>
            <a-form-item label="电子邮箱">
              <a-input v-model:value="userInfo.email" placeholder="请输入电子邮箱" />
            </a-form-item>
            <a-form-item label="所在地区">
              <a-cascader
                v-model:value="userInfo.region"
                :options="regionOptions"
                placeholder="请选择地区"
              />
            </a-form-item>
            <a-form-item label="详细地址">
              <a-textarea
                v-model:value="userInfo.address"
                placeholder="请输入详细地址"
                :rows="2"
              />
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
              <a-button type="primary" @click="saveUserInfo">保存修改</a-button>
            </a-form-item>
          </a-form>
        </a-card>

        <a-card class="mt-4" title="紧急联系人">
          <a-form
            :model="emergencyContact"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 16 }"
          >
            <a-form-item label="联系人姓名">
              <a-input v-model:value="emergencyContact.name" placeholder="请输入联系人姓名" />
            </a-form-item>
            <a-form-item label="关系">
              <a-select v-model:value="emergencyContact.relation" placeholder="请选择关系">
                <a-select-option value="父母">父母</a-select-option>
                <a-select-option value="配偶">配偶</a-select-option>
                <a-select-option value="子女">子女</a-select-option>
                <a-select-option value="其他">其他</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="联系电话">
              <a-input v-model:value="emergencyContact.phone" placeholder="请输入联系电话" />
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
              <a-button type="primary" @click="saveEmergencyContact">保存联系人</a-button>
            </a-form-item>
          </a-form>
        </a-card>

        <a-card class="mt-4" title="舞蹈经历">
          <a-form
            :model="danceExperience"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 16 }"
          >
            <a-form-item label="舞蹈基础">
              <a-select v-model:value="danceExperience.level" placeholder="请选择舞蹈基础">
                <a-select-option value="零基础">零基础</a-select-option>
                <a-select-option value="初级">初级</a-select-option>
                <a-select-option value="中级">中级</a-select-option>
                <a-select-option value="高级">高级</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="学习舞种">
              <a-checkbox-group v-model:value="danceExperience.styles">
                <a-checkbox value="HIPHOP">Hip-Hop</a-checkbox>
                <a-checkbox value="JAZZ">爵士</a-checkbox>
                <a-checkbox value="BREAKING">Breaking</a-checkbox>
                <a-checkbox value="POPPING">Popping</a-checkbox>
                <a-checkbox value="LOCKING">Locking</a-checkbox>
                <a-checkbox value="URBAN">Urban</a-checkbox>
              </a-checkbox-group>
            </a-form-item>
            <a-form-item label="学习目标">
              <a-textarea
                v-model:value="danceExperience.goal"
                placeholder="请描述您的学习目标"
                :rows="3"
              />
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
              <a-button type="primary" @click="saveDanceExperience">保存经历</a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
    </a-row>

    <a-modal
      v-model:open="avatarModalVisible"
      title="更换头像"
      @ok="handleAvatarUpload"
    >
      <a-upload
        list-type="picture-card"
        :file-list="fileList"
        :before-upload="beforeUpload"
        @preview="handlePreview"
        @change="handleFileChange"
      >
        <div v-if="fileList.length < 1" class="text-center">
          <PlusOutlined />
          <div style="margin-top: 8px">上传头像</div>
        </div>
      </a-upload>
      <p class="text-gray-500 text-sm mt-2">支持 JPG、PNG 格式，文件大小不超过 2MB</p>
    </a-modal>

    <a-modal
      v-model:open="passwordModalVisible"
      title="修改密码"
      @ok="handlePasswordChange"
    >
      <a-form :model="passwordForm" :label-col="{ span: 6 }">
        <a-form-item label="原密码">
          <a-input-password v-model:value="passwordForm.oldPassword" placeholder="请输入原密码" />
        </a-form-item>
        <a-form-item label="新密码">
          <a-input-password v-model:value="passwordForm.newPassword" placeholder="请输入新密码" />
        </a-form-item>
        <a-form-item label="确认密码">
          <a-input-password v-model:value="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { 
  UserOutlined, 
  UploadOutlined,
  PlusOutlined 
} from '@ant-design/icons-vue'
import type { UploadFile } from 'ant-design-vue'
import { useUserStore } from '@/stores/user'
import { getCurrentUser } from '@/api/auth'
import { uploadAvatar, updateProfile } from '@/api/user'

const userInfo = reactive({
  realName: '张三',
  gender: 'MALE',
  birthday: null,
  phone: '13800138000',
  email: 'zhangsan@example.com',
  region: [],
  address: '',
  avatar: '',
  learnedCourses: 5,
  learningHours: 120,
  certificates: 3
})
const userStore = useUserStore()

const emergencyContact = reactive({
  name: '',
  relation: undefined,
  phone: ''
})

const danceExperience = reactive({
  level: undefined,
  styles: [],
  goal: ''
})

const regionOptions = [
  {
    value: '北京',
    label: '北京',
    children: [
      { value: '朝阳区', label: '朝阳区' },
      { value: '海淀区', label: '海淀区' }
    ]
  },
  {
    value: '上海',
    label: '上海',
    children: [
      { value: '浦东新区', label: '浦东新区' },
      { value: '黄浦区', label: '黄浦区' }
    ]
  }
]

const avatarModalVisible = ref(false)
const passwordModalVisible = ref(false)
const fileList = ref<UploadFile[]>([])

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const showAvatarModal = () => {
  avatarModalVisible.value = true
}

const showPasswordModal = () => {
  passwordModalVisible.value = true
}

const beforeUpload = (file: File) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('只能上传 JPG/PNG 格式的图片！')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB！')
    return false
  }
  return false
}

const handlePreview = (file: UploadFile) => {
  console.log('Preview file:', file)
}

const handleFileChange = ({ fileList: newFileList }: { fileList: UploadFile[] }) => {
  fileList.value = newFileList
}

const handleAvatarUpload = () => {
  if (fileList.value.length === 0) {
    message.warning('请先选择头像图片')
    return
  }
  const file = fileList.value[0]?.originFileObj
  if (!file) {
    message.warning('未检测到有效文件，请重新选择')
    return
  }
  uploadAvatar(file as File)
    .then(res => {
      if (res.code === 200 && res.data?.url) {
        userInfo.avatar = res.data.url
        userStore.setAvatar(res.data.url)
        message.success('头像上传成功')
        avatarModalVisible.value = false
        fileList.value = []
      }
    })
    .catch(error => {
      console.error('头像上传失败:', error)
    })
}

const handlePasswordChange = () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
    message.warning('请填写完整的密码信息')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    message.error('两次输入的密码不一致')
    return
  }
  message.success('密码修改成功')
  passwordModalVisible.value = false
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
}

const saveUserInfo = async () => {
  try {
    const res = await updateProfile({
      realName: userInfo.realName,
      phone: userInfo.phone,
      email: userInfo.email
    })
    if (res.code === 200 && res.data) {
      message.success('个人信息保存成功')
      const latestUser = await getCurrentUser()
      if (latestUser.code === 200 && latestUser.data?.user) {
        userStore.setUserInfo(latestUser.data.user)
      }
    }
  } catch (error) {
    console.error('更新个人信息失败:', error)
  }
}

const saveEmergencyContact = () => {
  message.success('紧急联系人保存成功')
}

const saveDanceExperience = () => {
  message.success('舞蹈经历保存成功')
}

onMounted(() => {
  getCurrentUser()
    .then(res => {
      if (res.code === 200 && res.data?.user) {
        const profile = res.data.user
        userInfo.realName = profile.realName || profile.username || ''
        userInfo.phone = profile.phone || ''
        userInfo.email = profile.email || ''
        userInfo.avatar = profile.avatar || ''
      }
    })
    .catch(error => {
      console.error('加载个人信息失败:', error)
    })
})
</script>

<style scoped>
.user-stats {
  padding: 16px 0;
}

:deep(.ant-statistic-title) {
  font-size: 13px;
  color: #6b7280;
}

:deep(.ant-statistic-content) {
  font-size: 20px;
  font-weight: bold;
  color: #1f2937;
}
</style>
