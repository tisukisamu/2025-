<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { userApi, type User } from '@/api'
import { message } from 'ant-design-vue'

const loading = ref(false)
const saving = ref(false)
const changingPwd = ref(false)

const profile = ref<User | null>(null)

const editForm = reactive({
  nickname: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const loadProfile = async () => {
  loading.value = true
  try {
    const data = await userApi.getProfile()
    profile.value = data
    editForm.nickname = data.nickname ?? ''
  } finally {
    loading.value = false
  }
}

const saveProfile = async () => {
  if (!profile.value) return
  saving.value = true
  try {
    const updated = await userApi.updateProfile({ nickname: editForm.nickname })
    profile.value = updated
    message.success('个人资料已保存')
  } catch (e: any) {
    message.error(e?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const changePassword = async () => {
  if (!pwdForm.oldPassword || !pwdForm.newPassword) {
    message.warning('请输入原密码和新密码')
    return
  }
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    message.warning('两次输入的新密码不一致')
    return
  }
  if (pwdForm.newPassword.length < 6) {
    message.warning('新密码至少 6 位')
    return
  }

  changingPwd.value = true
  try {
    await userApi.changePassword(pwdForm.oldPassword, pwdForm.newPassword)
    message.success('密码修改成功，请牢记新密码')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } catch (e: any) {
    message.error(e?.message || '修改失败')
  } finally {
    changingPwd.value = false
  }
}

onMounted(loadProfile)
</script>

<template>
  <div class="page">
    <h2 class="title">个人中心</h2>

    <div class="card" v-if="profile">
      <h3 class="card-title">基础信息</h3>

      <div class="grid">
        <div class="field">
          <div class="label">用户名</div>
          <div class="value">{{ profile.username }}</div>
        </div>

        <div class="field">
          <div class="label">角色</div>
          <div class="value">{{ profile.role }}</div>
        </div>

        <div class="field">
          <div class="label">昵称</div>
          <input v-model="editForm.nickname" class="input" placeholder="请输入昵称" />
        </div>
      </div>

      <button class="btn" :disabled="saving" @click="saveProfile">
        {{ saving ? '保存中...' : '保存资料' }}
      </button>
    </div>

    <div class="card" v-if="profile">
      <h3 class="card-title">修改密码</h3>

      <div class="grid">
        <div class="field">
          <div class="label">原密码</div>
          <input v-model="pwdForm.oldPassword" type="password" class="input" placeholder="请输入原密码" />
        </div>

        <div class="field">
          <div class="label">新密码</div>
          <input v-model="pwdForm.newPassword" type="password" class="input" placeholder="请输入新密码" />
        </div>

        <div class="field">
          <div class="label">确认新密码</div>
          <input
            v-model="pwdForm.confirmPassword"
            type="password"
            class="input"
            placeholder="请再次输入新密码"
          />
        </div>
      </div>

      <button class="btn danger" :disabled="changingPwd" @click="changePassword">
        {{ changingPwd ? '提交中...' : '修改密码' }}
      </button>
    </div>

    <div v-if="loading" class="hint">加载中...</div>
  </div>
</template>

<style scoped>
.page { max-width: 980px; margin: 0 auto; }
.title { font-size: 20px; font-weight: 800; margin-bottom: 16px; }
.card {
  background: #fff;
  border: 1px solid var(--gray-200);
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 16px;
}
.card-title { font-size: 15px; font-weight: 800; margin: 0 0 12px; }
.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 12px;
}
.field { display: flex; flex-direction: column; gap: 6px; }
.label { font-size: 12px; color: var(--gray-500); }
.value { font-size: 14px; color: var(--gray-900); font-weight: 700; }
.input {
  height: 38px;
  border: 1px solid var(--gray-200);
  border-radius: 10px;
  padding: 0 12px;
  outline: none;
}
.input:focus {
  border-color: var(--primary-500);
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.12);
}
.btn {
  height: 38px;
  padding: 0 14px;
  border-radius: 10px;
  border: 1px solid var(--gray-200);
  background: #111827;
  color: #fff;
  cursor: pointer;
  font-weight: 700;
}
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
.btn.danger { background: #b91c1c; border-color: rgba(185, 28, 28, 0.4); }
.hint { color: var(--gray-500); font-size: 13px; }
@media (max-width: 768px) {
  .grid { grid-template-columns: 1fr; }
}
</style>
