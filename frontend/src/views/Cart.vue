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
.page-title { font-family: var(--font-display); font-size: 28px; font-weight: 700; margin-bottom: 36px; }

.cart-list {
  background: var(--color-surface); border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md); overflow: hidden;
}
.cart-item {
  display: flex; align-items: center; gap: 18px;
  padding: 20px 24px; border-bottom: 1px solid var(--color-border-light);
  transition: background var(--transition);
}
.cart-item:last-child { border-bottom: none; }
.cart-item:hover { background: #FAFAF8; }
.cart-item-img { width: 90px; height: 90px; border-radius: var(--radius-sm); overflow: hidden; cursor: pointer; background: #E8E4DF; flex-shrink: 0; border: 1px solid var(--color-border-light); }
.cart-item-img img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s ease; }
.cart-item-img:hover img { transform: scale(1.06); }
.cart-item-body { flex: 1; cursor: pointer; min-width: 0; }
.cart-item-body h4 { font-size: 15px; font-weight: 600; margin-bottom: 6px; }
.cart-item-price { font-size: 13px; color: var(--color-text-sub); }
.cart-item-qty { flex-shrink: 0; }
.cart-item-subtotal { font-size: 16px; font-weight: 700; width: 90px; text-align: right; flex-shrink: 0; color: var(--color-text); }
.cart-item-remove {
  width: 36px; height: 36px; display: flex; align-items: center; justify-content: center;
  border: none; background: none; color: var(--color-text-sub); border-radius: var(--radius-sm);
  transition: all var(--transition); flex-shrink: 0; font-size: 16px;
}
.cart-item-remove:hover { color: var(--color-highlight); background: rgba(200,85,61,0.08); }

.cart-footer {
  display: flex; justify-content: flex-end; align-items: center; gap: 28px;
  margin-top: 28px; padding: 20px 24px; background: var(--color-surface);
  border: 1px solid var(--color-border-light); border-radius: var(--radius-md);
}
.cart-total { font-size: 16px; color: var(--color-text-dim); }
.cart-total strong { font-size: 24px; font-weight: 700; color: var(--color-highlight); margin-left: 4px; }

.empty-state { text-align: center; padding: 100px 0; }
.empty-text { font-size: 16px; color: var(--color-text-sub); margin-bottom: 24px; }
</style>
