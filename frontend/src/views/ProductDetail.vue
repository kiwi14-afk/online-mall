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
.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 60px; align-items: start; }
.detail-gallery { position: sticky; top: 80px; }
.gallery-main { aspect-ratio: 1; border-radius: var(--radius-sm); overflow: hidden; background: #EEECE8; }
.gallery-main img { width: 100%; height: 100%; object-fit: cover; }

.breadcrumb { font-size: 13px; color: var(--color-text-sub); margin-bottom: 16px; display: flex; gap: 6px; align-items: center; }
.breadcrumb a:hover { color: var(--color-text); }
.breadcrumb .current { color: var(--color-text-dim); }

.product-tag { display: inline-block; font-size: 11px; text-transform: uppercase; letter-spacing: 0.08em; color: var(--color-text-sub); border: 1px solid var(--color-border); padding: 4px 10px; border-radius: 2px; margin-bottom: 16px; }
.product-title { font-family: var(--font-display); font-size: 28px; font-weight: 600; line-height: 1.3; margin-bottom: 20px; }

.price-block { padding: 20px; background: var(--color-bg); border-radius: var(--radius-sm); margin-bottom: 20px; display: flex; align-items: baseline; gap: 10px; }
.price-amount { font-size: 28px; font-weight: 700; color: var(--color-highlight); }
.price-note { font-size: 13px; color: var(--color-text-sub); }

.product-desc { font-size: 14px; color: var(--color-text-dim); line-height: 1.8; margin-bottom: 20px; }

.stock-info { display: flex; align-items: center; gap: 8px; font-size: 13px; color: var(--color-text-dim); margin-bottom: 24px; }
.stock-dot { width: 8px; height: 8px; border-radius: 50%; }
.stock-dot.in { background: var(--color-success); }
.stock-dot.out { background: var(--color-text-sub); }

.purchase-row { display: flex; gap: 12px; }
.qty-input { width: 100px; }
.btn-lg { padding: 12px 32px; font-size: 15px; }

@media (max-width: 768px) {
  .detail-grid { grid-template-columns: 1fr; gap: 32px; }
  .detail-gallery { position: static; }
}
</style>
