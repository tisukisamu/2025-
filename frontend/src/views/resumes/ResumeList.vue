<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-semibold text-neutral-900">我的简历</h1>
      <a-button type="primary" class="!bg-neutral-900 !rounded-lg" @click="showCreateModal">
        <plus-outlined /> 新建简历
      </a-button>
    </div>
    
    <div class="grid grid-cols-2 gap-6">
      <div 
        v-for="resume in resumes" 
        :key="resume.id"
        class="bg-white rounded-xl border border-neutral-100 hover:border-neutral-300 hover:shadow-md transition-all"
      >
        <div class="p-6">
          <div class="flex justify-between items-start mb-4">
            <div>
              <h3 class="text-lg font-semibold text-neutral-900">{{ resume.name }}</h3>
              <p class="text-neutral-500 text-sm mt-1">
                {{ resume.gender }} · {{ resume.age }}岁 · {{ resume.education }}
              </p>
            </div>
            <a-dropdown>
              <a-button type="text" class="!text-neutral-400">
                <more-outlined />
              </a-button>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="editResume(resume)">
                    <edit-outlined class="mr-2" /> 编辑
                  </a-menu-item>
                  <a-menu-item @click="previewResume(resume)">
                    <eye-outlined class="mr-2" /> 预览
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item class="!text-red-500" @click="deleteResume(resume.id)">
                    <delete-outlined class="mr-2" /> 删除
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          
          <div class="space-y-3 text-sm">
            <div class="flex items-center text-neutral-600">
              <phone-outlined class="mr-2 text-neutral-400" />
              {{ resume.phone }}
            </div>
            <div class="flex items-center text-neutral-600">
              <mail-outlined class="mr-2 text-neutral-400" />
              {{ resume.email }}
            </div>
            <div class="flex items-center text-neutral-600">
              <trophy-outlined class="mr-2 text-neutral-400" />
              {{ resume.experience }}工作经验
            </div>
          </div>
          
          <div class="mt-4 pt-4 border-t border-neutral-100">
            <div class="text-neutral-500 text-sm mb-2">技能标签</div>
            <div class="flex flex-wrap gap-2">
              <a-tag 
                v-for="skill in resume.skills.slice(0, 4)" 
                :key="skill"
                class="!bg-neutral-100 !text-neutral-600 !border-0"
              >
                {{ skill }}
              </a-tag>
              <a-tag v-if="resume.skills.length > 4" class="!bg-neutral-100 !text-neutral-400 !border-0">
                +{{ resume.skills.length - 4 }}
              </a-tag>
            </div>
          </div>
        </div>
        
        <div class="px-6 py-4 bg-neutral-50 rounded-b-xl flex justify-between items-center">
          <span class="text-neutral-400 text-sm">更新于 {{ resume.updatedAt }}</span>
          <a-button type="primary" ghost class="!rounded-lg" @click="useResume(resume)">
            使用此简历
          </a-button>
        </div>
      </div>
    </div>
    
    <a-modal
      v-model:open="modalVisible"
      :title="editingResume ? '编辑简历' : '新建简历'"
      width="800px"
      :footer="null"
    >
      <a-form
        :model="formData"
        :rules="rules"
        layout="vertical"
        @finish="handleSubmit"
        class="mt-4"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="姓名" name="name">
              <a-input v-model:value="formData.name" placeholder="请输入姓名" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="性别" name="gender">
              <a-radio-group v-model:value="formData.gender">
                <a-radio value="男">男</a-radio>
                <a-radio value="女">女</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="年龄" name="age">
              <a-input-number v-model:value="formData.age" :min="18" :max="65" class="w-full" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="电话" name="phone">
              <a-input v-model:value="formData.phone" placeholder="请输入电话" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="学历" name="education">
              <a-select v-model:value="formData.education" placeholder="请选择学历">
                <a-select-option value="大专">大专</a-select-option>
                <a-select-option value="本科">本科</a-select-option>
                <a-select-option value="硕士">硕士</a-select-option>
                <a-select-option value="博士">博士</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="工作经验" name="experience">
              <a-select v-model:value="formData.experience" placeholder="请选择工作经验">
                <a-select-option value="应届生">应届生</a-select-option>
                <a-select-option value="1-3年">1-3年</a-select-option>
                <a-select-option value="3-5年">3-5年</a-select-option>
                <a-select-option value="5-10年">5-10年</a-select-option>
                <a-select-option value="10年以上">10年以上</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item label="技能标签" name="skills">
          <a-select
            v-model:value="formData.skills"
            mode="tags"
            placeholder="输入技能后按回车添加"
          />
        </a-form-item>
        
        <a-form-item label="工作经历" name="workExperience">
          <a-textarea
            v-model:value="formData.workExperience"
            placeholder="请描述您的工作经历"
            :rows="4"
          />
        </a-form-item>
        
        <a-form-item label="项目经验" name="projectExperience">
          <a-textarea
            v-model:value="formData.projectExperience"
            placeholder="请描述您的项目经验"
            :rows="4"
          />
        </a-form-item>
        
        <a-form-item label="教育经历" name="educationExperience">
          <a-textarea
            v-model:value="formData.educationExperience"
            placeholder="请描述您的教育经历"
            :rows="4"
          />
        </a-form-item>
        
        <div class="flex justify-end gap-3">
          <a-button @click="modalVisible = false">取消</a-button>
          <a-button type="primary" html-type="submit" class="!bg-neutral-900">
            {{ editingResume ? '保存' : '创建' }}
          </a-button>
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  PlusOutlined,
  MoreOutlined,
  EditOutlined,
  EyeOutlined,
  DeleteOutlined,
  PhoneOutlined,
  MailOutlined,
  TrophyOutlined
} from '@ant-design/icons-vue'
import { resumeApi } from '@/api'
import type { Resume } from '@/types/resume'

const modalVisible = ref(false)
const editingResume = ref<Resume | null>(null)
const resumes = ref<any[]>([])

const formData = reactive({
  name: '',
  gender: '男',
  age: 25,
  phone: '',
  email: '',
  education: undefined,
  experience: undefined,
  skills: [] as string[],
  workExperience: '',
  projectExperience: '',
  educationExperience: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名' }],
  phone: [{ required: true, message: '请输入电话' }],
  email: [
    { required: true, message: '请输入邮箱' },
    { type: 'email', message: '请输入正确的邮箱格式' }
  ],
  education: [{ required: true, message: '请选择学历' }],
  experience: [{ required: true, message: '请选择工作经验' }]
}

const showCreateModal = () => {
  editingResume.value = null
  Object.assign(formData, {
    name: '',
    gender: '男',
    age: 25,
    phone: '',
    email: '',
    education: undefined,
    experience: undefined,
    skills: [],
    workExperience: '',
    projectExperience: '',
    educationExperience: ''
  })
  modalVisible.value = true
}

const editResume = (resume: Resume) => {
  editingResume.value = resume
  Object.assign(formData, {
    ...resume,
    skills: Array.isArray((resume as any).skills)
      ? (resume as any).skills
      : String((resume as any).skills || '')
          .split(',')
          .map(item => item.trim())
          .filter(Boolean)
  })
  modalVisible.value = true
}

const previewResume = (resume: Resume) => {
  message.info('预览功能开发中')
}

const deleteResume = (id: number) => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这份简历吗？删除后无法恢复。',
    okText: '删除',
    okType: 'danger',
    cancelText: '取消',
    async onOk() {
      await resumeApi.delete(id)
      await fetchResumes()
      message.success('删除成功')
    }
  })
}

const useResume = (resume: Resume) => {
  message.success(`已选择简历: ${resume.name}`)
}

const handleSubmit = async () => {
  try {
    const payload = {
      ...formData,
      skills: formData.skills.join(',')
    }
    if (editingResume.value) {
      await resumeApi.update(editingResume.value.id, payload)
      message.success('简历更新成功')
    } else {
      await resumeApi.create(payload)
      message.success('简历创建成功')
    }
    await fetchResumes()
    modalVisible.value = false
  } catch (error) {
    message.error('操作失败')
  }
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleDateString('zh-CN')
}

const fetchResumes = async () => {
  try {
    const res = await resumeApi.getMy()
    resumes.value = (res.data || []).map((item: any) => ({
      ...item,
      skills: String(item.skills || '')
        .split(',')
        .map((skill: string) => skill.trim())
        .filter(Boolean),
      updatedAt: formatDate(item.updatedAt || item.createdAt)
    }))
  } catch (error) {
    message.error('获取简历列表失败')
  }
}

onMounted(() => {
  fetchResumes()
})
</script>
