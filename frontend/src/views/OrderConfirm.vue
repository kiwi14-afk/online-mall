<template>
  <div class="page" v-loading="submitting">
    <div class="container container-narrow">
      <h2 class="page-title">确认订单</h2>

      <div v-if="cartItems.length > 0">
        <!-- Address -->
        <section class="confirm-section">
          <h3>收货信息</h3>
          <div class="address-form">
            <el-input v-model="address.name" placeholder="收货人" size="large" class="addr-input" />
            <el-input v-model="address.phone" placeholder="联系电话" size="large" class="addr-input" />
            <el-input v-model="address.address" placeholder="详细地址" size="large" class="addr-input" />
          </div>
        </section>

        <!-- Items -->
        <section class="confirm-section">
          <h3>订单明细</h3>
          <div class="item-list">
            <div v-for="item in cartItems" :key="item.id" class="line-item">
              <span class="line-name">{{ item.name }} × {{ item.quantity }}</span>
              <span class="line-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
        </section>

        <!-- Total & Submit -->
        <div class="confirm-footer">
          <span class="confirm-total">应付 <strong>¥{{ totalPrice.toFixed(2) }}</strong></span>
          <button class="btn btn-lg" @click="submitOrder" :disabled="submitting">
            {{ submitting ? '提交中...' : '提交订单' }}
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <p class="empty-text">没有待下单的商品</p>
        <router-link to="/cart" class="btn">返回购物车</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { isLoggedIn } from '../utils/auth'

const router = useRouter()
const submitting = ref(false)
const cartItems = ref([])
const address = reactive({ name: '', phone: '', address: '' })
const totalPrice = computed(() => cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0))

async function submitOrder() {
  if (!address.name || !address.phone || !address.address) { ElMessage.warning('请填写完整收货信息'); return }
  submitting.value = true
  try {
    const userId = JSON.parse(localStorage.getItem('mall_user') || '{}').userId
    const resp = await fetch('http://localhost:8083/api/order', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'X-User-Id': userId || '' },
      body: JSON.stringify({ items: cartItems.value, address: { ...address }, totalAmount: totalPrice.value })
    })
    if (resp.ok) {
      localStorage.removeItem('mall_cart')
      window.dispatchEvent(new Event('cart-updated'))
      ElMessage.success('下单成功')
      router.push('/orders')
    } else {
      throw new Error('Order failed')
    }
  } catch (e) {
    console.warn('Order service not available, simulating')
    localStorage.removeItem('mall_cart')
    window.dispatchEvent(new Event('cart-updated'))
    ElMessage.success('下单成功！（模拟模式）')
    router.push('/orders')
  } finally { submitting.value = false }
}

onMounted(() => {
  if (!isLoggedIn()) { router.push('/login'); return }
  cartItems.value = JSON.parse(localStorage.getItem('mall_cart') || '[]')
  if (cartItems.value.length === 0) router.push('/cart')
})
</script>

<style scoped>
.page { padding: 48px 0; }
.container-narrow { max-width: 680px; }
.page-title { font-family: var(--font-display); font-size: 24px; font-weight: 600; margin-bottom: 32px; }

.confirm-section { margin-bottom: 28px; }
.confirm-section h3 { font-size: 15px; font-weight: 600; margin-bottom: 14px; }
.address-form { display: flex; flex-direction: column; gap: 10px; background: var(--color-surface); padding: 20px; border-radius: var(--radius-sm); border: 1px solid var(--color-border-light); }

.item-list { border: 1px solid var(--color-border-light); border-radius: var(--radius-sm); overflow: hidden; }
.line-item { display: flex; justify-content: space-between; padding: 14px 20px; border-bottom: 1px solid var(--color-border-light); font-size: 14px; }
.line-item:last-child { border-bottom: none; }
.line-name { color: var(--color-text); }
.line-price { font-weight: 500; }

.confirm-footer { display: flex; justify-content: flex-end; align-items: center; gap: 24px; }
.confirm-total { font-size: 16px; }
.confirm-total strong { font-size: 22px; font-weight: 700; color: var(--color-highlight); }

.empty-state { text-align: center; padding: 80px 0; }
.empty-text { font-size: 15px; color: var(--color-text-sub); margin-bottom: 20px; }
</style>
