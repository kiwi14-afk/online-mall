<template>
  <div class="page">
    <div class="container container-narrow">
      <h2 class="page-title">我的订单</h2>

      <div v-if="orders.length > 0" class="order-list">
        <div v-for="order in orders" :key="order.id" class="order-card" @click="$router.push(`/order/${order.id}`)">
          <div class="order-head">
            <span class="order-no">{{ order.orderNo || order.id }}</span>
            <span class="order-status" :class="statusClass(order.status)">{{ order.status || '待处理' }}</span>
          </div>
          <div class="order-body">
            <span class="order-items">
              <template v-for="(item, idx) in (order.items || [])" :key="idx">
                {{ item.name }} × {{ item.quantity }}<span v-if="idx < order.items.length - 1">、</span>
              </template>
            </span>
          </div>
          <div class="order-foot">
            <span class="order-date">{{ order.createTime }}</span>
            <span class="order-amount">¥{{ order.totalAmount }}</span>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <p class="empty-text">还没有订单</p>
        <router-link to="/products" class="btn">去购物</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { isLoggedIn } from '../utils/auth'

const router = useRouter()
const orders = ref([])

function statusClass(status) {
  const map = { '待支付': 'status-pending', '已支付': 'status-paid', '已取消': 'status-cancel', '已完成': 'status-done' }
  return map[status] || ''
}

onMounted(async () => {
  if (!isLoggedIn()) { router.push('/login'); return }
  try {
    const userId = JSON.parse(localStorage.getItem('mall_user') || '{}').userId
    const resp = await fetch(`http://localhost:8083/api/order/list?userId=${userId}`)
    if (resp.ok) { const result = await resp.json(); orders.value = result.data || [] }
  } catch (e) { console.warn('Order service not available') }
})
</script>

<style scoped>
.page { padding: 48px 0; }
.container-narrow { max-width: 680px; }
.page-title { font-family: var(--font-display); font-size: 24px; font-weight: 600; margin-bottom: 32px; }

.order-list { display: flex; flex-direction: column; gap: 12px; }
.order-card {
  padding: 20px 24px; background: var(--color-surface); border: 1px solid var(--color-border-light);
  border-radius: var(--radius-sm); cursor: pointer; transition: all var(--transition);
}
.order-card:hover { border-color: var(--color-border); box-shadow: var(--shadow-sm); }

.order-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.order-no { font-size: 13px; color: var(--color-text-sub); font-family: var(--font-mono); }
.order-status { font-size: 12px; padding: 2px 10px; border-radius: 2px; font-weight: 500; }
.status-pending { background: #FFF5E6; color: #B87A14; }
.status-paid { background: #E8F4E8; color: #3D7A3D; }
.status-cancel { background: #F0F0F0; color: #999; }
.status-done { background: #E8EEF4; color: #4A6A8A; }

.order-body { margin-bottom: 10px; }
.order-items { font-size: 14px; color: var(--color-text); }

.order-foot { display: flex; justify-content: space-between; align-items: center; }
.order-date { font-size: 12px; color: var(--color-text-sub); }
.order-amount { font-size: 16px; font-weight: 600; }

.empty-state { text-align: center; padding: 80px 0; }
.empty-text { font-size: 15px; color: var(--color-text-sub); margin-bottom: 20px; }
</style>
