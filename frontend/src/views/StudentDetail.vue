<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <a-page-header
      :title="student.name"
      :sub-title="student.phone"
      @back="() => $router.back()"
    >
      <template #extra>
        <a-space>
          <a-button @click="editStudent">编辑信息</a-button>
          <a-button type="primary" @click="enrollCourse">报名课程</a-button>
        </a-space>
      </template>
    </a-page-header>

    <a-row :gutter="16" class="mt-6">
      <a-col :xs="24" :lg="8">
        <a-card class="mb-4">
          <div class="text-center">
            <a-avatar :size="120" :src="student.avatar">
              <template #icon><UserOutlined /></template>
            </a-avatar>
            <h2 class="text-2xl font-bold mt-4 mb-2">{{ student.name }}</h2>
            <p class="text-gray-600 mb-2">{{ student.phone }}</p>
            <a-tag :color="student.status === 'ACTIVE' ? 'green' : 'red'">
              {{ student.status === 'ACTIVE' ? '在读' : '已结业' }}
            </a-tag>
          </div>
        </a-card>

        <a-card title="基本信息" class="mb-4">
          <a-descriptions :column="1" size="small">
            <a-descriptions-item label="学员编号">{{ student.studentId }}</a-descriptions-item>
            <a-descriptions-item label="性别">{{ student.gender === 'MALE' ? '男' : '女' }}</a-descriptions-item>
            <a-descriptions-item label="年龄">{{ student.age }}岁</a-descriptions-item>
            <a-descriptions-item label="电子邮箱">{{ student.email }}</a-descriptions-item>
            <a-descriptions-item label="注册日期">{{ student.joinDate }}</a-descriptions-item>
            <a-descriptions-item label="会员等级">
              <a-tag color="gold">{{ student.memberLevel }}</a-tag>
            </a-descriptions-item>
          </a-descriptions>
        </a-card>

        <a-card title="学习统计" class="mb-4">
          <a-row :gutter="16">
            <a-col :span="12">
              <a-statistic title="已学课程" :value="student.completedCourses" />
            </a-col>
            <a-col :span="12">
              <a-statistic title="在学课程" :value="student.ongoingCourses" />
            </a-col>
            <a-col :span="12" class="mt-4">
              <a-statistic title="学习时长" :value="student.learningHours" suffix="小时" />
            </a-col>
            <a-col :span="12" class="mt-4">
              <a-statistic title="出勤率" :value="student.attendanceRate" suffix="%" />
            </a-col>
          </a-row>
        </a-card>

        <a-card title="紧急联系人">
          <a-descriptions :column="1" size="small">
            <a-descriptions-item label="姓名">{{ student.emergencyContact.name }}</a-descriptions-item>
            <a-descriptions-item label="关系">{{ student.emergencyContact.relation }}</a-descriptions-item>
            <a-descriptions-item label="电话">{{ student.emergencyContact.phone }}</a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="16">
        <a-card title="学习进度" class="mb-4">
          <a-table :columns="progressColumns" :data-source="learningProgress" :pagination="false">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'progress'">
                <a-progress :percent="record.progress" :size="'small'" />
              </template>
              <template v-if="column.key === 'status'">
                <a-tag :color="record.status === 'ONGOING' ? 'blue' : 'green'">
                  {{ record.status === 'ONGOING' ? '进行中' : '已完成' }}
                </a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-button type="link" size="small" @click="viewCourse(record)">查看详情</a-button>
              </template>
            </template>
          </a-table>
        </a-card>

        <a-card title="上课记录" class="mb-4">
          <a-table :columns="attendanceColumns" :data-source="attendanceRecords" :pagination="false">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="record.status === 'PRESENT' ? 'green' : 'red'">
                  {{ record.status === 'PRESENT' ? '出勤' : '缺勤' }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-card>

        <a-card title="支付记录" class="mb-4">
          <a-table :columns="paymentColumns" :data-source="paymentRecords" :pagination="false">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="record.status === 'PAID' ? 'green' : 'orange'">
                  {{ record.status === 'PAID' ? '已支付' : '待支付' }}
                </a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-button type="link" size="small" @click="viewPayment(record)">查看</a-button>
              </template>
            </template>
          </a-table>
        </a-card>

        <a-card title="舞蹈经历" class="mb-4">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="舞蹈基础">{{ student.danceExperience.level }}</a-descriptions-item>
            <a-descriptions-item label="学习舞种">
              <a-space>
                <a-tag v-for="style in student.danceExperience.styles" :key="style">{{ style }}</a-tag>
              </a-space>
            </a-descriptions-item>
            <a-descriptions-item label="学习目标" :span="2">
              {{ student.danceExperience.goal }}
            </a-descriptions-item>
          </a-descriptions>
        </a-card>

        <a-card title="获得证书">
          <a-list :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 3, xl: 4, xxl: 4 }" :data-source="certificates">
            <template #renderItem="{ item }">
              <a-list-item>
                <a-card hoverable>
                  <template #cover>
                    <div class="certificate-cover">
                      <TrophyOutlined style="font-size: 48px; color: #faad14" />
                    </div>
                  </template>
                  <a-card-meta :title="item.name" :description="item.date" />
                </a-card>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, TrophyOutlined } from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()

const student = reactive({
  id: 1,
  name: '张三',
  studentId: 'S001',
  gender: 'MALE',
  age: 25,
  phone: '138****0001',
  email: 'zhangsan@example.com',
  avatar: '',
  joinDate: '2023-06-15',
  status: 'ACTIVE',
  memberLevel: 'VIP会员',
  completedCourses: 5,
  ongoingCourses: 2,
  learningHours: 120,
  attendanceRate: 95,
  emergencyContact: {
    name: '李四',
    relation: '朋友',
    phone: '139****0002'
  },
  danceExperience: {
    level: '中级',
    styles: ['Hip-Hop', 'Jazz'],
    goal: '提升舞蹈技巧，参加街舞比赛'
  }
})

const learningProgress = ref([
  {
    id: 1,
    courseName: 'Hip-Hop基础班',
    teacherName: '张老师',
    progress: 75,
    completedLessons: 18,
    totalLessons: 24,
    status: 'ONGOING'
  },
  {
    id: 2,
    courseName: '爵士舞进阶班',
    teacherName: '李老师',
    progress: 30,
    completedLessons: 9,
    totalLessons: 30,
    status: 'ONGOING'
  },
  {
    id: 3,
    courseName: 'Popping入门班',
    teacherName: '王老师',
    progress: 100,
    completedLessons: 20,
    totalLessons: 20,
    status: 'COMPLETED'
  }
])

const attendanceRecords = ref([
  {
    id: 1,
    courseName: 'Hip-Hop基础班',
    date: '2024-02-19',
    time: '18:00-19:30',
    teacherName: '张老师',
    status: 'PRESENT'
  },
  {
    id: 2,
    courseName: '爵士舞进阶班',
    date: '2024-02-18',
    time: '19:30-21:00',
    teacherName: '李老师',
    status: 'PRESENT'
  },
  {
    id: 3,
    courseName: 'Hip-Hop基础班',
    date: '2024-02-17',
    time: '18:00-19:30',
    teacherName: '张老师',
    status: 'ABSENT'
  }
])

const paymentRecords = ref([
  {
    id: 1,
    courseName: 'Hip-Hop基础班',
    amount: 1200,
    paymentMethod: '微信支付',
    paidAt: '2024-01-15 10:30:00',
    status: 'PAID'
  },
  {
    id: 2,
    courseName: '爵士舞进阶班',
    amount: 1500,
    paymentMethod: '支付宝',
    paidAt: '2024-02-01 14:20:00',
    status: 'PAID'
  }
])

const certificates = ref([
  { id: 1, name: 'Hip-Hop初级证书', date: '2023-09-15' },
  { id: 2, name: 'Popping结业证书', date: '2023-12-31' },
  { id: 3, name: '优秀学员奖', date: '2024-01-15' }
])

const progressColumns = [
  { title: '课程名称', dataIndex: 'courseName', key: 'courseName' },
  { title: '授课教师', dataIndex: 'teacherName', key: 'teacherName' },
  { title: '学习进度', key: 'progress' },
  { title: '完成课时', key: 'lessons' },
  { title: '状态', key: 'status' },
  { title: '操作', key: 'action' }
]

const attendanceColumns = [
  { title: '课程名称', dataIndex: 'courseName', key: 'courseName' },
  { title: '上课日期', dataIndex: 'date', key: 'date' },
  { title: '上课时间', dataIndex: 'time', key: 'time' },
  { title: '授课教师', dataIndex: 'teacherName', key: 'teacherName' },
  { title: '出勤状态', key: 'status' }
]

const paymentColumns = [
  { title: '课程名称', dataIndex: 'courseName', key: 'courseName' },
  { title: '支付金额', dataIndex: 'amount', key: 'amount' },
  { title: '支付方式', dataIndex: 'paymentMethod', key: 'paymentMethod' },
  { title: '支付时间', dataIndex: 'paidAt', key: 'paidAt' },
  { title: '状态', key: 'status' },
  { title: '操作', key: 'action' }
]

const editStudent = () => {
  message.info('编辑学员信息')
}

const enrollCourse = () => {
  message.info('报名课程')
}

const viewCourse = (record: any) => {
  router.push(`/courses/${record.id}`)
}

const viewPayment = (record: any) => {
  message.info('查看支付详情')
}

onMounted(() => {
  const studentId = route.params.id
  console.log('Student ID:', studentId)
})
</script>

<style scoped>
.certificate-cover {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fff9e6 0%, #fff5d6 100%);
}

:deep(.ant-page-header) {
  background: white;
  padding: 16px 24px;
  border-radius: 8px;
}
</style>
