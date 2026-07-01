<template>
  <div class="page">
    <div class="container container-narrow">
      <h2 class="page-title">购物车</h2>

      <div v-if="cart.length > 0">
        <div class="cart-list">
          <div v-for="(item, index) in cart" :key="item.id" class="cart-item">
            <div class="cart-item-img" @click="$router.push(`/product/${item.id}`)">
              <img :src="item.imageUrl || 'https://placehold.co/120x120/FAF8F5/1A1A1A?text=?'" :alt="item.name" />
            </div>
            <div class="cart-item-body" @click="$router.push(`/product/${item.id}`)">
              <h4>{{ item.name }}</h4>
              <span class="cart-item-price">¥{{ item.price }}</span>
            </div>
            <div class="cart-item-qty">
              <el-input-number v-model="item.quantity" :min="1" :max="item.stock" size="small" @change="saveCart" />
            </div>
            <div class="cart-item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
            <button class="cart-item-remove" @click="removeItem(index)" title="移除">
              <el-icon><Close /></el-icon>
            </button>
          </div>
        </div>

        <div class="cart-footer">
          <span class="cart-total">合计 <strong>¥{{ totalPrice.toFixed(2) }}</strong></span>
          <button class="btn" @click="checkout">结算</button>
        </div>
      </div>

      <div v-else class="empty-state">
        <p class="empty-text">购物车是空的</p>
        <router-link to="/products" class="btn">去逛逛</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { isLoggedIn } from '../utils/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const cart = ref([])

const totalPrice = computed(() => cart.value.reduce((sum, item) => sum + item.price * item.quantity, 0))

function saveCart() {
  localStorage.setItem('mall_cart', JSON.stringify(cart.value))
  window.dispatchEvent(new Event('cart-updated'))
}

function removeItem(index) {
  cart.value.splice(index, 1)
  saveCart()
}

function checkout() {
  if (!isLoggedIn()) { ElMessage.warning('请先登录'); router.push('/login'); return }
  router.push('/order-confirm')
}

onMounted(() => { cart.value = JSON.parse(localStorage.getItem('mall_cart') || '[]') })
</script>

<style scoped>
.page { padding: 48px 0; }
.container-narrow { max-width: 800px; }
.page-title { font-family: var(--font-display); font-size: 24px; font-weight: 600; margin-bottom: 32px; }

.cart-list { border-top: 1px solid var(--color-border); }
.cart-item {
  display: flex; align-items: center; gap: 16px;
  padding: 20px 0; border-bottom: 1px solid var(--color-border-light);
}
.cart-item-img { width: 80px; height: 80px; border-radius: var(--radius-sm); overflow: hidden; cursor: pointer; background: #EEECE8; flex-shrink: 0; }
.cart-item-img img { width: 100%; height: 100%; object-fit: cover; }
.cart-item-body { flex: 1; cursor: pointer; min-width: 0; }
.cart-item-body h4 { font-size: 14px; font-weight: 500; margin-bottom: 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.cart-item-price { font-size: 13px; color: var(--color-text-sub); }
.cart-item-qty { flex-shrink: 0; }
.cart-item-subtotal { font-size: 14px; font-weight: 600; width: 80px; text-align: right; flex-shrink: 0; }
.cart-item-remove {
  width: 32px; height: 32px; display: flex; align-items: center; justify-content: center;
  border: none; background: none; color: var(--color-text-sub); border-radius: var(--radius-sm);
  transition: all var(--transition); flex-shrink: 0;
}
.cart-item-remove:hover { color: var(--color-highlight); background: rgba(200,85,61,0.06); }

.cart-footer { display: flex; justify-content: flex-end; align-items: center; gap: 24px; margin-top: 24px; }
.cart-total { font-size: 16px; }
.cart-total strong { font-size: 22px; font-weight: 700; color: var(--color-highlight); }

.empty-state { text-align: center; padding: 80px 0; }
.empty-text { font-size: 15px; color: var(--color-text-sub); margin-bottom: 20px; }
</style>
