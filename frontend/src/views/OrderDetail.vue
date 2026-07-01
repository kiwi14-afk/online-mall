<template>
  <div class="page" v-loading="loading">
    <div class="container container-narrow">
      <h2 class="page-title">订单详情</h2>

      <div v-if="order" class="detail-card">
        <div class="detail-section">
          <h3>订单信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">订单编号</span>
              <span class="info-value">{{ order.orderNo || order.id }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">状态</span>
              <span class="order-status" :class="statusClass(order.status)">{{ order.status || '待处理' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">下单时间</span>
              <span class="info-value">{{ order.createTime }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">总金额</span>
              <span class="info-value price">¥{{ order.totalAmount }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section" v-if="order.address">
          <h3>收货信息</h3>
          <p class="address-text">{{ order.address.name }} · {{ order.address.phone }}</p>
          <p class="address-text">{{ order.address.address }}</p>
        </div>

        <div class="detail-section">
          <h3>商品明细</h3>
          <div class="item-list">
            <div v-for="(item, idx) in (order.items || [])" :key="idx" class="line-item">
              <span>{{ item.name }} × {{ item.quantity }}</span>
              <span>¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const order = ref(null)
const loading = ref(false)

function statusClass(status) {
  const map = { '待支付': 'status-pending', '已支付': 'status-paid', '已取消': 'status-cancel', '已完成': 'status-done' }
  return map[status] || ''
}

onMounted(async () => {
  loading.value = true
  try {
    const resp = await fetch(`http://localhost:8083/api/order/${route.params.id}`)
    if (resp.ok) { const result = await resp.json(); order.value = result.data }
  } catch (e) { console.warn('Order service not available') }
  finally { loading.value = false }
})
</script>

<style scoped>
.page { padding: 48px 0; }
.container-narrow { max-width: 680px; }
.page-title { font-family: var(--font-display); font-size: 24px; font-weight: 600; margin-bottom: 32px; }

.detail-card { background: var(--color-surface); border: 1px solid var(--color-border-light); border-radius: var(--radius-sm); overflow: hidden; }
.detail-section { padding: 24px; border-bottom: 1px solid var(--color-border-light); }
.detail-section:last-child { border-bottom: none; }
.detail-section h3 { font-size: 14px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; color: var(--color-text-sub); margin-bottom: 14px; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.info-item { display: flex; flex-direction: column; gap: 4px; }
.info-label { font-size: 12px; color: var(--color-text-sub); }
.info-value { font-size: 14px; color: var(--color-text); }
.info-value.price { font-weight: 600; color: var(--color-highlight); }

.order-status { font-size: 12px; padding: 2px 10px; border-radius: 2px; font-weight: 500; display: inline-block; width: fit-content; }
.status-pending { background: #FFF5E6; color: #B87A14; }
.status-paid { background: #E8F4E8; color: #3D7A3D; }
.status-cancel { background: #F0F0F0; color: #999; }
.status-done { background: #E8EEF4; color: #4A6A8A; }

.address-text { font-size: 14px; color: var(--color-text); line-height: 1.8; }

.item-list { border: 1px solid var(--color-border-light); border-radius: var(--radius-sm); overflow: hidden; }
.line-item { display: flex; justify-content: space-between; padding: 12px 16px; border-bottom: 1px solid var(--color-border-light); font-size: 14px; }
.line-item:last-child { border-bottom: none; }
</style>
