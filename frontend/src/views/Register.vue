<template>
  <div class="auth-page">
    <div class="auth-wrapper">
      <div class="auth-header">
        <span class="logo-mark">M</span>
        <h2>创建账户</h2>
        <p>加入我们，探索品质好物</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" class="auth-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名（3-50位）" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码（至少6位）" size="large" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button @click="handleRegister" :loading="loading" class="submit-btn" size="large">注 册</el-button>
        </el-form-item>
      </el-form>
      <p class="auth-switch">已有账号？<router-link to="/login">立即登录 →</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api/user'
import { setToken, setUser } from '../utils/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', password: '', confirmPassword: '', email: '', phone: '' })

const validateConfirm = (rule, value, callback) => {
  if (value !== form.password) callback(new Error('两次密码输入不一致'))
  else callback()
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度3-50位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await register({ username: form.username, password: form.password, email: form.email, phone: form.phone })
    setToken(res.data.token)
    setUser({ userId: res.data.userId, username: res.data.username })
    ElMessage.success('注册成功')
    router.push('/')
  } catch (e) { /* error handled by interceptor */ }
  finally { loading.value = false }
}
</script>

<style scoped>
.auth-page {
  display: flex; align-items: center; justify-content: center;
  min-height: calc(100vh - 200px); padding: 40px 20px;
}
.auth-wrapper { width: 100%; max-width: 400px; }
.auth-header { text-align: center; margin-bottom: 36px; }
.auth-header .logo-mark { margin-bottom: 16px; display: inline-block; }
.auth-header h2 { font-family: var(--font-display); font-size: 24px; font-weight: 600; margin-bottom: 6px; }
.auth-header p { font-size: 14px; color: var(--color-text-sub); }
.auth-form { margin-bottom: 20px; }
.submit-btn { width: 100%; background: var(--color-accent); border: none; color: #fff; font-weight: 500; letter-spacing: 0.04em; }
.submit-btn:hover { background: var(--color-accent-hover); }
.auth-switch { text-align: center; font-size: 14px; color: var(--color-text-sub); }
.auth-switch a { color: var(--color-text); font-weight: 500; }
.auth-switch a:hover { text-decoration: underline; }
</style>
