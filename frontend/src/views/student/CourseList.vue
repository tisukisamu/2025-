<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">课程列表</h1>
      <p class="text-gray-600 mt-1">浏览和报名街舞课程</p>
    </div>

    <a-card class="mb-6">
      <a-form layout="inline">
        <a-form-item label="课程名称">
          <a-input v-model:value="searchForm.keyword" placeholder="搜索课程名称" style="width: 200px" allow-clear />
        </a-form-item>
        <a-form-item label="课程类型">
          <a-select v-model:value="searchForm.type" placeholder="选择类型" style="width: 150px" allow-clear>
            <a-select-option value="HIPHOP">Hip-Hop</a-select-option>
            <a-select-option value="JAZZ">爵士</a-select-option>
            <a-select-option value="BREAKING">Breaking</a-select-option>
            <a-select-option value="POPPING">Popping</a-select-option>
            <a-select-option value="LOCKING">Locking</a-select-option>
            <a-select-option value="URBAN">Urban</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="难度等级">
          <a-select v-model:value="searchForm.level" placeholder="选择等级" style="width: 150px" allow-clear>
            <a-select-option value="BEGINNER">初级</a-select-option>
            <a-select-option value="INTERMEDIATE">中级</a-select-option>
            <a-select-option value="ADVANCED">高级</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="价格范围">
          <a-input-number v-model:value="searchForm.minPrice" placeholder="最低价" style="width: 100px" :min="0" />
          <span class="mx-2">-</span>
          <a-input-number v-model:value="searchForm.maxPrice" placeholder="最高价" style="width: 100px" :min="0" />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch">
              <template #icon><SearchOutlined /></template>
              搜索
            </a-button>
            <a-button @click="handleReset">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :sm="12" :lg="8" :xl="6" v-for="course in courses" :key="course.id">
        <a-card hoverable class="course-card">
          <template #cover>
            <div class="course-cover">
              <img v-if="course.coverImage" :src="course.coverImage" alt="课程封面" />
              <img v-else-if="getCourseImage(course)" :src="getCourseImage(course)" alt="课程封面" />
              <div v-else class="course-cover-placeholder">
                <PictureOutlined style="font-size: 48px; color: #d9d9d9" />
              </div>
              <div class="course-type-tag">{{ getCourseTypeText(course.category) }}</div>
            </div>
          </template>
          
          <a-card-meta>
            <template #title>
              <div class="course-title">{{ course.name }}</div>
            </template>
            <template #description>
              <div class="course-info">
                <div class="info-item">
                  <UserOutlined class="mr-1" />
                  <span>{{ getTeacherName(course) }}</span>
                </div>
                <div class="info-item">
                  <ClockCircleOutlined class="mr-1" />
                  <span>{{ course.duration || 0 }}课时</span>
                </div>
                <div class="info-item">
                  <TeamOutlined class="mr-1" />
                  <span>{{ course.enrolledCount }}/{{ course.capacity }}</span>
                </div>
              </div>
              <div class="course-level">
                <a-tag :color="getLevelColor(course.level)">
                  {{ getLevelText(course.level || '') }}
                </a-tag>
              </div>
            </template>
          </a-card-meta>

          <div class="course-footer">
            <div class="course-price">¥{{ course.price }}</div>
            <a-space>
              <a-button size="small" @click="viewDetail(course)">详情</a-button>
              <a-button 
                v-if="!enrolledCourseIds.includes(course.id)"
                type="primary" 
                size="small"
                :disabled="course.enrolledCount >= course.capacity"
                :loading="enrollingCourseIds.includes(course.id)"
                @click="enrollCourse(course)"
              >
                {{ course.enrolledCount >= course.capacity ? '已满' : '报名' }}
              </a-button>
              <a-tag v-else color="green">已报名</a-tag>
            </a-space>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <div class="mt-6 text-center" v-if="total > searchForm.pageSize">
      <a-pagination
        v-model:current="searchForm.page"
        v-model:pageSize="searchForm.pageSize"
        :total="total"
        show-size-changer
        show-quick-jumper
        :show-total="total => `共 ${total} 个课程`"
        @change="handlePageChange"
      />
    </div>

    <a-modal
      v-model:open="detailVisible"
      :title="currentCourse?.name"
      width="800px"
      :footer="null"
    >
      <div v-if="currentCourse" class="course-detail">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="课程类型">{{ getCourseTypeText(currentCourse.category) }}</a-descriptions-item>
          <a-descriptions-item label="难度等级">
            <a-tag :color="getLevelColor(currentCourse.level || '')">{{ getLevelText(currentCourse.level || '') }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="授课教师">{{ getTeacherName(currentCourse) }}</a-descriptions-item>
          <a-descriptions-item label="课程时长">{{ currentCourse.duration }}课时</a-descriptions-item>
          <a-descriptions-item label="课程价格">¥{{ currentCourse.price }}</a-descriptions-item>
          <a-descriptions-item label="报名人数">{{ currentCourse.enrolledCount }}/{{ currentCourse.capacity }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ formatDate(currentCourse.createdAt) }}</a-descriptions-item>
          <a-descriptions-item label="更新时间">{{ formatDate(currentCourse.updatedAt) }}</a-descriptions-item>
          <a-descriptions-item label="课程描述" :span="2">
            {{ currentCourse.description || '暂无描述' }}
          </a-descriptions-item>
        </a-descriptions>
        
        <div class="mt-4">
          <h3 class="text-lg font-bold mb-2">课程大纲</h3>
          <a-timeline>
            <a-timeline-item v-for="(item, index) in getOutline(currentCourse)" :key="index">
              {{ item }}
            </a-timeline-item>
          </a-timeline>
        </div>

        <div class="mt-4 text-right">
          <a-button 
            v-if="!enrolledCourseIds.includes(currentCourse.id)"
            type="primary" 
            size="large"
            :disabled="currentCourse.enrolledCount >= currentCourse.capacity"
            :loading="enrollingCourseIds.includes(currentCourse.id)"
            @click="enrollCourse(currentCourse)"
          >
            立即报名
          </a-button>
          <a-tag v-else color="green" class="text-base px-4 py-2">已报名</a-tag>
        </div>
      </div>
    </a-modal>

    <a-modal
      v-model:open="paymentVisible"
      title="课程支付（模拟）"
      :confirm-loading="paying"
      ok-text="确认支付"
      cancel-text="取消"
      @ok="confirmPayment"
    >
      <a-form layout="vertical">
        <a-form-item label="课程名称">
          <a-input :value="pendingCourse?.name" disabled />
        </a-form-item>
        <a-form-item label="支付金额">
          <a-input :value="`¥${pendingCourse?.price ?? 0}`" disabled />
        </a-form-item>
        <a-form-item label="支付方式">
          <a-select v-model:value="paymentMethod">
            <a-select-option value="WECHAT">微信支付</a-select-option>
            <a-select-option value="ALIPAY">支付宝</a-select-option>
            <a-select-option value="CARD">银行卡</a-select-option>
            <a-select-option value="CASH">现金</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      <div class="text-gray-500 text-sm">模拟支付：点击确认后将默认支付成功。</div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { 
  SearchOutlined, 
  PictureOutlined,
  UserOutlined,
  ClockCircleOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'
import type { Course } from '@/types/course'
import { getCourses, enrollCourse as enrollCourseApi } from '@/api/course'
import { createPayment, processPayment } from '@/api/finance'
import { PaymentMethod } from '@/types'
import { useUserStore } from '@/stores/user'
import { getMyCourses } from '@/api/student'

const DEFAULT_COURSE_IMAGE = 'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=1200&q=80'
const userStore = useUserStore()

const searchForm = reactive({
  keyword: '',
  type: undefined,
  level: undefined,
  minPrice: undefined,
  maxPrice: undefined,
  page: 1,
  pageSize: 12
})

const courses = ref<Course[]>([])
const total = ref(0)
const detailVisible = ref(false)
const currentCourse = ref<Course | null>(null)
const enrollingCourseIds = ref<number[]>([])
const enrolledCourseIds = ref<number[]>([])
const paymentVisible = ref(false)
const paying = ref(false)
const paymentMethod = ref<PaymentMethod>(PaymentMethod.WECHAT)
const pendingCourse = ref<Course | null>(null)
const pendingEnrollmentId = ref<number | null>(null)

const getCourseTypeText = (type?: string) => {
  const typeMap: Record<string, string> = {
    HIPHOP: 'Hip-Hop',
    JAZZ: '爵士',
    BREAKING: 'Breaking',
    POPPING: 'Popping',
    LOCKING: 'Locking',
    URBAN: 'Urban'
  }
  return typeMap[type] || type
}

const getTeacherName = (course: Course) => {
  return course.teacher?.name || '待分配'
}

const formatDate = (value?: string) => {
  if (!value) {
    return '-'
  }
  return value.replace('T', ' ').slice(0, 19)
}

const getLevelText = (level: string) => {
  const levelMap: Record<string, string> = {
    BEGINNER: '初级',
    INTERMEDIATE: '中级',
    ADVANCED: '高级'
  }
  return levelMap[level] || level
}

const getLevelColor = (level: string) => {
  const colorMap: Record<string, string> = {
    BEGINNER: 'green',
    INTERMEDIATE: 'blue',
    ADVANCED: 'red'
  }
  return colorMap[level] || 'default'
}

const getOutline = (course: Course) => {
  if (course.description) {
    return [course.description]
  }
  return ['课程内容以实际上课安排为准']
}

const getCourseImage = (course: any) => {
  return course.coverImage || course.image || DEFAULT_COURSE_IMAGE
}

const loadCourses = async () => {
  try {
    const params: any = {
      page: searchForm.page - 1,
      size: searchForm.pageSize,
      status: 'PUBLISHED'
    }
    if (searchForm.keyword) {
      params.name = searchForm.keyword
    }
    const res = await getCourses(params)
    if (res.code === 200 && res.data) {
      let list = [...res.data.content]
      if (searchForm.type) {
        list = list.filter(item => item.category === searchForm.type)
      }
      if (searchForm.level) {
        list = list.filter(item => item.level === searchForm.level)
      }
      if (searchForm.minPrice !== undefined && searchForm.minPrice !== null) {
        list = list.filter(item => Number(item.price) >= Number(searchForm.minPrice))
      }
      if (searchForm.maxPrice !== undefined && searchForm.maxPrice !== null) {
        list = list.filter(item => Number(item.price) <= Number(searchForm.maxPrice))
      }
      courses.value = list
      total.value = res.data.totalElements
    }
  } catch (error) {
    message.error('加载课程失败')
  }
}

const handleSearch = () => {
  searchForm.page = 1
  loadCourses()
}

const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    type: undefined,
    level: undefined,
    minPrice: undefined,
    maxPrice: undefined,
    page: 1,
    pageSize: 12
  })
  loadCourses()
}

const handlePageChange = () => {
  loadCourses()
}

const viewDetail = (course: Course) => {
  currentCourse.value = course
  detailVisible.value = true
}

const enrollCourse = async (course: Course) => {
  if (!userStore.isStudent && !userStore.isAdmin) {
    message.warning('当前账号无报名权限')
    return
  }
  if (enrollingCourseIds.value.includes(course.id)) {
    return
  }
  if (enrolledCourseIds.value.includes(course.id)) {
    message.warning('您已报名该课程')
    return
  }
  enrollingCourseIds.value.push(course.id)
  try {
    const res = await enrollCourseApi(course.id, { courseId: course.id })
    if (res.code === 200) {
      message.success(`报名成功，请完成支付：${course.name}`)
      pendingCourse.value = course
      pendingEnrollmentId.value = (res as any).data?.billId ?? null
      paymentMethod.value = PaymentMethod.WECHAT
      paymentVisible.value = true
      courses.value = courses.value.map(item =>
        item.id === course.id
          ? { ...item, enrolledCount: Number(item.enrolledCount || 0) + 1 }
          : item
      )
      if (currentCourse.value?.id === course.id) {
        currentCourse.value = {
          ...currentCourse.value,
          enrolledCount: Number(currentCourse.value.enrolledCount || 0) + 1
        }
      }
      enrolledCourseIds.value.push(course.id)
    }
  } catch (error) {
    console.error('报名失败:', error)
  } finally {
    enrollingCourseIds.value = enrollingCourseIds.value.filter(id => id !== course.id)
  }
}

const confirmPayment = async () => {
  if (!pendingCourse.value || !pendingEnrollmentId.value) {
    message.warning('未找到待支付账单信息')
    paymentVisible.value = false
    return
  }
  paying.value = true
  try {
    const createRes = await createPayment({
      billId: pendingEnrollmentId.value,
      amount: Number(pendingCourse.value.price || 0),
      paymentMethod: paymentMethod.value
    })
    if (createRes.code === 200 && createRes.data?.id) {
      await processPayment(createRes.data.id)
    }
    message.success('支付成功，已完成报名')
    paymentVisible.value = false
    detailVisible.value = false
    pendingCourse.value = null
    pendingEnrollmentId.value = null
  } catch (error) {
    console.error('支付失败:', error)
  } finally {
    paying.value = false
  }
}

const loadEnrolledCourses = async () => {
  if (!userStore.isStudent && !userStore.isAdmin) {
    return
  }
  try {
    const res = await getMyCourses()
    if (res.code === 200 && res.data) {
      enrolledCourseIds.value = res.data
        .filter(item => item.status !== 'CANCELLED')
        .map(item => item.course.id)
    }
  } catch (error) {
    console.error('加载已报名课程失败:', error)
  }
}

onMounted(() => {
  loadCourses()
  loadEnrolledCourses()
})
</script>

<style scoped>
.course-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.course-cover {
  position: relative;
  height: 200px;
  overflow: hidden;
  background: #f5f5f5;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.course-type-tag {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(31, 41, 55, 0.8);
  color: white;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-info {
  margin-top: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  font-size: 13px;
  color: #6b7280;
}

.course-level {
  margin-top: 8px;
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.course-price {
  font-size: 20px;
  font-weight: bold;
  color: #1f2937;
}

.course-detail {
  padding: 16px 0;
}
</style>
