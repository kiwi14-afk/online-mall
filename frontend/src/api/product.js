import request from './request'

const BASE = 'http://localhost:8082/api/product'

export function getProductList(params) {
  return request.get(`${BASE}/list`, { params })
}

export function getProductDetail(id) {
  return request.get(`${BASE}/${id}`)
}

export function getCategories() {
  return request.get(`${BASE}/category/list`)
}
