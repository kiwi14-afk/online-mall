<template>
  <div class="auth-page">
    <div class="auth-wrapper">
      <div class="auth-header">
        <span class="logo-mark">M</span>
        <h2>欢迎回来</h2>
        <p>登录你的账户继续购物</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" class="auth-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button @click="handleLogin" :loading="loading" class="submit-btn" size="large">登 录</el-button>
        </el-form-item>
      </el-form>
      <p class="auth-switch">还没有账号？<router-link to="/register">立即注册 →</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/user'
import { setToken, setUser } from '../utils/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await login(form)
    setToken(res.data.token)
    setUser({ userId: res.data.userId, username: res.data.username })
    ElMessage.success('登录成功')
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
