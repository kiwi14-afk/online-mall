<template>
  <div class="page" v-loading="loading">
    <div class="container">
      <div class="detail-grid" v-if="product">
        <!-- Image -->
        <div class="detail-gallery">
          <div class="gallery-main">
            <img :src="product.imageUrl || 'https://placehold.co/600x600/FAF8F5/1A1A1A?text=' + product.name?.charAt(0)" :alt="product.name" />
          </div>
        </div>

        <!-- Info -->
        <div class="detail-body">
          <nav class="breadcrumb">
            <router-link to="/">首页</router-link>
            <span>/</span>
            <router-link to="/products">全部商品</router-link>
            <span>/</span>
            <span class="current">{{ product.name }}</span>
          </nav>

          <span class="product-tag">{{ product.categoryName }}</span>
          <h1 class="product-title">{{ product.name }}</h1>

          <div class="price-block">
            <span class="price-amount">¥{{ product.price }}</span>
            <span class="price-note">含税价格</span>
          </div>

          <p class="product-desc">{{ product.description || '暂无详细描述。品质保证，值得信赖。' }}</p>

          <div class="stock-info">
            <span class="stock-dot" :class="product.stock > 0 ? 'in' : 'out'"></span>
            <span>{{ product.stock > 0 ? `库存 ${product.stock} 件` : '暂时售罄' }}</span>
          </div>

          <div class="purchase-row">
            <el-input-number v-model="quantity" :min="1" :max="product.stock || 1" size="large" class="qty-input" />
            <button class="btn btn-lg" @click="addToCart" :disabled="product.stock === 0">
              {{ product.stock === 0 ? '已售罄' : '加入购物车' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProductDetail } from '../api/product'
import { ElMessage } from 'element-plus'

const route = useRoute()
const product = ref(null)
const quantity = ref(1)
const loading = ref(false)

function addToCart() {
  const cart = JSON.parse(localStorage.getItem('mall_cart') || '[]')
  const existing = cart.find(item => item.id === product.value.id)
  if (existing) {
    existing.quantity += quantity.value
  } else {
    cart.push({
      id: product.value.id, name: product.value.name,
      price: product.value.price, imageUrl: product.value.imageUrl,
      quantity: quantity.value, stock: product.value.stock
    })
  }
  localStorage.setItem('mall_cart', JSON.stringify(cart))
  window.dispatchEvent(new Event('cart-updated'))
  ElMessage.success('已加入购物车')
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data
  } catch (e) { /* error */ }
  finally { loading.value = false }
})
</script>

<style scoped>
.page { padding: 48px 0; }
.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 64px; align-items: start; }
.detail-gallery { position: sticky; top: 80px; }
.gallery-main { aspect-ratio: 1; border-radius: var(--radius-md); overflow: hidden; background: #E8E4DF; border: 1px solid var(--color-border-light); }
.gallery-main img { width: 100%; height: 100%; object-fit: cover; }

.breadcrumb { font-size: 13px; color: var(--color-text-sub); margin-bottom: 20px; display: flex; gap: 8px; align-items: center; }
.breadcrumb a:hover { color: var(--color-text); }

.product-tag { display: inline-block; font-size: 11px; text-transform: uppercase; letter-spacing: 0.1em; color: var(--color-text-dim); background: var(--color-bg-alt); padding: 6px 14px; border-radius: 3px; margin-bottom: 18px; font-weight: 600; }
.product-title { font-family: var(--font-display); font-size: 30px; font-weight: 700; line-height: 1.3; margin-bottom: 24px; }

.price-block { padding: 24px; background: var(--color-bg); border-radius: var(--radius-md); margin-bottom: 24px; display: flex; align-items: baseline; gap: 12px; border: 1px solid var(--color-border-light); }
.price-amount { font-size: 32px; font-weight: 700; color: var(--color-highlight); }
.price-note { font-size: 13px; color: var(--color-text-sub); }

.product-desc { font-size: 15px; color: var(--color-text-dim); line-height: 1.9; margin-bottom: 24px; }

.stock-info { display: flex; align-items: center; gap: 10px; font-size: 14px; color: var(--color-text-dim); margin-bottom: 28px; padding: 12px 16px; background: var(--color-bg); border-radius: var(--radius-sm); }
.stock-dot { width: 10px; height: 10px; border-radius: 50%; }
.stock-dot.in { background: var(--color-success); }
.stock-dot.out { background: var(--color-text-sub); }

.purchase-row { display: flex; gap: 14px; }
.qty-input { width: 110px; }
.btn-lg { padding: 14px 36px; font-size: 16px; }

@media (max-width: 768px) { .detail-grid { grid-template-columns: 1fr; gap: 32px; } .detail-gallery { position: static; } }
</style>
