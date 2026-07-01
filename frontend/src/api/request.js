import axios from 'axios'
import { getToken, logout } from '../utils/auth'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: 'http://localhost:8080',  // Gateway address (dev: direct to service)
  timeout: 10000
})

// Request interceptor: attach JWT token
request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    const user = JSON.parse(localStorage.getItem('mall_user') || '{}')
    if (user.userId) {
      config.headers['X-User-Id'] = user.userId
    }
    return config
  },
  error => Promise.reject(error)
)

// Response interceptor: handle errors
request.interceptors.response.use(
  response => {
    const data = response.data
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message))
    }
    return data
  },
  error => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        logout()
        window.location.href = '/login'
        ElMessage.error('登录已过期，请重新登录')
      } else {
        ElMessage.error(`请求失败: ${status}`)
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default request
