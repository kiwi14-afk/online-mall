import request from './request'

// Direct calls to user-service (port 8081) during dev, via gateway in production
const BASE = 'http://localhost:8081/api/user'

export function login(data) {
  return request.post(`${BASE}/login`, data)
}

export function register(data) {
  return request.post(`${BASE}/register`, data)
}

export function getProfile() {
  return request.get(`${BASE}/profile`)
}

export function updateProfile(data) {
  return request.put(`${BASE}/profile`, data)
}
