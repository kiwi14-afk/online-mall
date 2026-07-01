<template>
  <div class="home">
    <!-- Hero -->
    <section class="hero">
      <div class="hero-content">
        <span class="hero-eyebrow">Spring 2026 Collection</span>
        <h1 class="hero-title">发现<br>生活之美</h1>
        <p class="hero-desc">精选好物，品质生活。探索我们的甄选商品系列。</p>
        <div class="hero-actions">
          <router-link to="/products" class="btn">探索商品</router-link>
          <router-link to="/products" class="btn btn-outline">查看分类</router-link>
        </div>
      </div>
      <div class="hero-visual">
        <div class="hero-shape"></div>
      </div>
    </section>

    <!-- Categories -->
    <section class="section">
      <div class="container">
        <div class="section-head">
          <h2 class="section-title">商品分类</h2>
          <router-link to="/products" class="section-link">查看全部 →</router-link>
        </div>
        <div class="category-grid">
          <div
            v-for="cat in categories"
            :key="cat.id"
            class="category-card"
            @click="$router.push({ path: '/products', query: { categoryId: cat.id } })"
          >
            <span class="category-index">{{ String(cat.id).padStart(2, '0') }}</span>
            <span class="category-name">{{ cat.name }}</span>
          </div>
          <div v-if="categories.length === 0" class="category-card muted">
            <span class="category-name">加载中…</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Search -->
    <section class="section search-section">
      <div class="container">
        <div class="search-box">
          <el-input
            v-model="keyword"
            placeholder="搜索你想要的..."
            size="large"
            class="search-input"
            @keyup.enter="search"
          >
            <template #append>
              <el-button @click="search">搜索</el-button>
            </template>
          </el-input>
        </div>
      </div>
    </section>

    <!-- Products -->
    <section class="section">
      <div class="container">
        <div class="section-head">
          <h2 class="section-title">热门商品</h2>
          <router-link to="/products" class="section-link">查看全部 →</router-link>
        </div>
        <div class="product-grid">
          <article
            v-for="product in products"
            :key="product.id"
            class="product-card"
            @click="$router.push(`/product/${product.id}`)"
          >
            <div class="product-image-wrap">
              <img
                :src="product.imageUrl || 'https://placehold.co/600x600/FAF8F5/1A1A1A?text=' + encodeURIComponent(product.name?.charAt(0) || '?')"
                :alt="product.name"
                loading="lazy"
              />
              <div class="product-overlay">
                <span class="overlay-text">查看详情</span>
              </div>
            </div>
            <div class="product-meta">
              <div class="product-name-row">
                <h3>{{ product.name }}</h3>
                <span class="product-category">{{ product.categoryName }}</span>
              </div>
              <span class="product-price">¥{{ product.price }}</span>
            </div>
          </article>
        </div>
      </div>
    </section>

    <!-- Empty state placeholder -->
    <section v-if="products.length === 0" class="section">
      <div class="container" style="text-align:center;padding:60px 0;">
        <p style="color:var(--color-text-sub)">暂无商品，请先启动 product-service</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProductList, getCategories } from '../api/product'

const router = useRouter()
const keyword = ref('')
const products = ref([])
const categories = ref([])

function search() {
  if (keyword.value.trim()) {
    router.push({ path: '/products', query: { keyword: keyword.value } })
  }
}

onMounted(async () => {
  try {
    const [prodRes, catRes] = await Promise.all([
      getProductList({ page: 0, size: 8 }),
      getCategories()
    ])
    products.value = prodRes.data.content || []
    categories.value = catRes.data || []
  } catch (e) {
    console.warn('Backend not available, showing empty state')
  }
})
</script>

<style scoped>
/* ===== HERO ===== */
.hero {
  display: flex; align-items: center; min-height: 520px;
  background: var(--color-surface); overflow: hidden;
}
.hero-content { flex: 1; padding: 80px 0 80px 80px; }
.hero-eyebrow { font-size: 13px; text-transform: uppercase; letter-spacing: 0.12em; color: var(--color-text-sub); margin-bottom: 16px; display: block; }
.hero-title { font-family: var(--font-display); font-size: 64px; font-weight: 700; line-height: 1.1; color: var(--color-text); margin-bottom: 20px; letter-spacing: -0.02em; }
.hero-desc { font-size: 16px; color: var(--color-text-dim); line-height: 1.8; max-width: 380px; margin-bottom: 32px; }
.hero-actions { display: flex; gap: 12px; }
.hero-visual {
  flex: 1; height: 520px; background: var(--color-bg);
  display: flex; align-items: center; justify-content: center;
  position: relative;
}
.hero-shape {
  width: 240px; height: 320px;
  background: linear-gradient(135deg, #E8E4DF 0%, #D5CFC8 100%);
  border-radius: 2px;
}

/* ===== SECTIONS ===== */
.section { padding: 60px 0; }
.section-head { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 28px; }
.section-title { font-family: var(--font-display); font-size: 24px; font-weight: 600; color: var(--color-text); letter-spacing: -0.01em; }
.section-link { font-size: 14px; color: var(--color-text-dim); transition: color var(--transition); }
.section-link:hover { color: var(--color-text); }

/* ===== CATEGORIES ===== */
.category-grid { display: flex; gap: 1px; background: var(--color-border); border-radius: var(--radius-sm); overflow: hidden; }
.category-card {
  flex: 1; display: flex; flex-direction: column; align-items: center; gap: 8px;
  padding: 32px 20px; background: var(--color-surface); cursor: pointer;
  transition: all var(--transition);
}
.category-card:hover { background: #F5F2EE; }
.category-card.muted { opacity: 0.4; cursor: default; }
.category-index { font-family: var(--font-mono); font-size: 11px; color: var(--color-text-sub); letter-spacing: 0.05em; }
.category-name { font-size: 15px; font-weight: 500; color: var(--color-text); }

/* ===== SEARCH ===== */
.search-section { padding-top: 0; }
.search-box { max-width: 480px; }
.search-input :deep(.el-input__wrapper) {
  border-radius: var(--radius-sm); box-shadow: none; border: 1.5px solid var(--color-border); background: var(--color-surface);
}
.search-input :deep(.el-input__wrapper:hover) { border-color: #ccc; }
.search-input :deep(.el-input-group__append) { background: var(--color-accent); border: none; border-radius: 0 var(--radius-sm) var(--radius-sm) 0; }
.search-input :deep(.el-input-group__append .el-button) { color: #fff; }

/* ===== PRODUCT GRID ===== */
.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.product-card { cursor: pointer; }
.product-image-wrap {
  position: relative; aspect-ratio: 1; overflow: hidden; border-radius: var(--radius-sm);
  background: #EEECE8;
}
.product-image-wrap img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s ease; }
.product-card:hover .product-image-wrap img { transform: scale(1.04); }
.product-overlay {
  position: absolute; inset: 0; display: flex; align-items: center; justify-content: center;
  background: rgba(26,26,26,0.04); opacity: 0; transition: opacity var(--transition);
}
.product-card:hover .product-overlay { opacity: 1; }
.overlay-text { font-size: 13px; color: #fff; background: rgba(26,26,26,0.8); padding: 6px 16px; border-radius: 2px; }
.product-meta { padding: 14px 2px; }
.product-name-row { display: flex; justify-content: space-between; align-items: flex-start; gap: 8px; margin-bottom: 6px; }
.product-name-row h3 { font-size: 14px; font-weight: 500; color: var(--color-text); line-height: 1.4; }
.product-category { font-size: 11px; color: var(--color-text-sub); flex-shrink: 0; }
.product-price { font-size: 15px; font-weight: 600; color: var(--color-highlight); }

@media (max-width: 1024px) { .product-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) {
  .hero { flex-direction: column; min-height: auto; }
  .hero-content { padding: 48px 24px; }
  .hero-title { font-size: 40px; }
  .hero-visual { width: 100%; height: 200px; }
  .hero-shape { width: 160px; height: 200px; }
  .product-grid { grid-template-columns: repeat(2, 1fr); }
  .category-grid { flex-wrap: wrap; }
  .category-card { flex: 1 1 40%; }
}
</style>
