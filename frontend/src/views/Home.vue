<template>
  <div class="home">
    <!-- ===== HERO (dark) ===== -->
    <section class="hero">
      <div class="container hero-grid">
        <div class="hero-content">
          <span class="hero-eyebrow">Spring / Summer 2026</span>
          <h1 class="hero-title">发现<br>生活之美</h1>
          <p class="hero-desc">甄选全球好物，每一件都为品质生活而来</p>
          <div class="hero-actions">
            <router-link to="/products" class="btn btn-light">探索商品</router-link>
            <router-link to="/products" class="btn btn-outline-light">查看分类</router-link>
          </div>
        </div>
        <div class="hero-visual">
          <div class="hero-cards">
            <div class="hero-card hc-1"></div>
            <div class="hero-card hc-2"></div>
            <div class="hero-card hc-3"></div>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== CATEGORIES ===== -->
    <section class="section section-alt">
      <div class="container">
        <div class="section-head">
          <div>
            <span class="section-kicker">目录</span>
            <h2 class="section-title">商品分类</h2>
          </div>
          <router-link to="/products" class="section-link">查看全部 →</router-link>
        </div>
        <div class="category-grid">
          <div v-for="(cat, i) in categories" :key="cat.id" class="category-card"
               @click="$router.push({ path: '/products', query: { categoryId: cat.id } })">
            <span class="category-icon" :style="{ background: catColors[i] || '#999' }">
              <el-icon :size="20" color="#fff"><component :is="catIcons[i]" /></el-icon>
            </span>
            <span class="category-name">{{ cat.name }}</span>
            <span class="category-count">{{ catCounts[i] || '浏览' }}</span>
          </div>
        </div>
      </div>
    </section>

    <hr class="section-divider" />

    <!-- ===== SEARCH ===== -->
    <section class="section">
      <div class="container">
        <div class="search-box">
          <el-input v-model="keyword" placeholder="搜索你想要的..." size="large" class="search-input" @keyup.enter="search">
            <template #prefix><el-icon><Search /></el-icon></template>
            <template #append><el-button @click="search" class="search-btn">搜索</el-button></template>
          </el-input>
        </div>
      </div>
    </section>

    <hr class="section-divider" />

    <!-- ===== PRODUCTS ===== -->
    <section class="section">
      <div class="container">
        <div class="section-head">
          <div>
            <span class="section-kicker">精选</span>
            <h2 class="section-title">热门商品</h2>
          </div>
          <router-link to="/products" class="section-link">查看全部 →</router-link>
        </div>
        <div class="product-grid" v-if="products.length > 0">
          <article v-for="product in products" :key="product.id" class="product-card"
                   @click="$router.push(`/product/${product.id}`)">
            <div class="product-badge" v-if="product.stock > 0 && product.stock < 20">热卖</div>
            <div class="product-image-wrap" :style="{ background: imageBg(product) }">
              <span class="placeholder-char">{{ product.name?.charAt(0) || '?' }}</span>
              <el-icon class="placeholder-icon" :size="48" color="rgba(255,255,255,0.25)"><component :is="imageIcon(product)" /></el-icon>
              <div class="product-overlay"><span class="overlay-text">查看详情 →</span></div>
            </div>
            <div class="product-meta">
              <span class="product-category">{{ product.categoryName }}</span>
              <h3>{{ product.name }}</h3>
              <span class="product-price">¥{{ product.price }}</span>
            </div>
          </article>
        </div>
        <div v-else class="empty-note"><p>暂无商品，请确保 product-service 已启动</p></div>
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
const catIcons = ['Monitor', 'Reading', 'Present', 'ForkSpoon', 'Headset', 'MagicStick']
const catColors = ['#4A90D9', '#5B8C5A', '#C8553D', '#E8953A', '#7B68EE', '#D4728A']
const catCounts = ['1,200+ 件', '890+ 件', '650+ 件', '430+ 件', '320+ 件', '280+ 件']
const productGradients = [
  'linear-gradient(135deg, #E8E0D8 0%, #D5CCC0 100%)',
  'linear-gradient(135deg, #D8E4E0 0%, #C5D2CC 100%)',
  'linear-gradient(135deg, #E8DBD5 0%, #D8C8C0 100%)',
  'linear-gradient(135deg, #E0D8E4 0%, #D0C8D8 100%)',
  'linear-gradient(135deg, #D8E0E8 0%, #C8D0DC 100%)',
  'linear-gradient(135deg, #E4D8E0 0%, #D4C8D0 100%)',
]

function imageBg(product) { return productGradients[(product.categoryId || 1) % productGradients.length] }
function imageIcon(product) {
  const icons = ['Monitor', 'Reading', 'Present', 'ForkSpoon', 'Headset', 'MagicStick']
  return icons[(product.categoryId || 1) % icons.length]
}

function search() {
  if (keyword.value.trim()) router.push({ path: '/products', query: { keyword: keyword.value } })
}

onMounted(async () => {
  try {
    const [prodRes, catRes] = await Promise.all([
      getProductList({ page: 0, size: 8 }),
      getCategories()
    ])
    products.value = prodRes.data.content || []
    categories.value = catRes.data || []
  } catch (e) { console.warn('Backend not available') }
})
</script>

<style scoped>
.hero { background: var(--color-surface-dark); padding: 60px 0 0; }
.hero-grid { display: flex; align-items: center; min-height: 440px; }
.hero-content { flex: 1; padding-bottom: 60px; }
.hero-eyebrow { font-size: 12px; text-transform: uppercase; letter-spacing: 0.15em; color: rgba(255,255,255,0.5); margin-bottom: 16px; display: block; font-weight: 500; }
.hero-title { font-family: var(--font-display); font-size: 68px; font-weight: 700; line-height: 1.08; color: #fff; margin-bottom: 20px; letter-spacing: -0.02em; }
.hero-desc { font-size: 17px; color: rgba(255,255,255,0.6); line-height: 1.8; max-width: 400px; margin-bottom: 32px; }
.hero-actions { display: flex; gap: 12px; }
:deep(.btn-light) { background: #fff; color: #1A1A1A; }
:deep(.btn-light:hover) { background: #e8e8e8; }
:deep(.btn-outline-light) { background: transparent; color: rgba(255,255,255,0.85); border: 1.5px solid rgba(255,255,255,0.3); }
:deep(.btn-outline-light:hover) { border-color: #fff; color: #fff; }
.hero-visual { flex: 1; height: 440px; display: flex; align-items: center; justify-content: center; position: relative; }
.hero-cards { position: relative; width: 300px; height: 320px; }
.hero-card { position: absolute; border-radius: var(--radius-md); box-shadow: 0 20px 60px rgba(0,0,0,0.4); }
.hc-1 { width: 160px; height: 200px; background: #C8553D; top: 0; left: 0; z-index: 3; }
.hc-2 { width: 140px; height: 180px; background: #4A4A4A; top: 40px; left: 120px; z-index: 2; }
.hc-3 { width: 120px; height: 150px; background: #8B7355; top: 100px; left: 30px; z-index: 1; }

.section { padding: 72px 0; }
.section-alt { background: var(--color-bg-alt); }
.section-kicker { font-size: 11px; text-transform: uppercase; letter-spacing: 0.1em; color: var(--color-text-sub); margin-bottom: 6px; display: block; }
.section-head { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 32px; }
.section-title { font-family: var(--font-display); font-size: 28px; font-weight: 700; color: var(--color-text); }
.section-link { font-size: 14px; color: var(--color-text-dim); transition: color var(--transition); font-weight: 500; }
.section-link:hover { color: var(--color-text); }

.category-grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: 16px; }
.category-card { display: flex; flex-direction: column; align-items: center; gap: 8px; padding: 28px 16px; background: var(--color-surface); cursor: pointer; border-radius: var(--radius-md); border: 1px solid var(--color-border-light); transition: all var(--transition); }
.category-card:hover { border-color: var(--color-accent); box-shadow: var(--shadow-md); transform: translateY(-2px); }
.category-icon { width: 48px; height: 48px; display: flex; align-items: center; justify-content: center; border-radius: 12px; transition: transform var(--transition); }
.category-card:hover .category-icon { transform: scale(1.08); }
.category-name { font-size: 14px; font-weight: 600; color: var(--color-text); }
.category-count { font-size: 11px; color: var(--color-text-sub); }

.search-box { max-width: 520px; }
.search-input :deep(.el-input__wrapper) { border-radius: var(--radius-sm); box-shadow: none; border: 1.5px solid var(--color-border); background: var(--color-surface); padding: 8px 12px; }
.search-input :deep(.el-input__wrapper:hover) { border-color: #c0b8ae; }
.search-input :deep(.el-input-group__append) { background: var(--color-accent); border: none; border-radius: 0 var(--radius-sm) var(--radius-sm) 0; }
.search-btn { color: #fff !important; background: var(--color-accent) !important; border: none !important; }

.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; }
.product-card { cursor: pointer; position: relative; }
.product-badge { position: absolute; top: 10px; left: 10px; z-index: 5; background: var(--color-highlight); color: #fff; font-size: 11px; padding: 2px 10px; border-radius: 2px; font-weight: 600; }
.product-image-wrap {
  position: relative; aspect-ratio: 1; overflow: hidden;
  border-radius: var(--radius-md); border: 1px solid var(--color-border-light);
  display: flex; align-items: center; justify-content: center;
}
.placeholder-char {
  font-family: var(--font-display); font-size: 64px; font-weight: 700;
  color: rgba(255,255,255,0.7); z-index: 1; user-select: none;
}
.placeholder-icon { position: absolute; bottom: 12px; right: 12px; z-index: 1; opacity: 0.6; }
.product-card:hover .product-image-wrap { border-color: #c0b8ae; box-shadow: var(--shadow-sm); }
.product-card:hover .placeholder-char { color: rgba(255,255,255,0.9); }
.product-overlay { position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; background: rgba(0,0,0,0.3); opacity: 0; transition: opacity var(--transition); border-radius: var(--radius-md); }
.product-card:hover .product-overlay { opacity: 1; }
.overlay-text { font-size: 14px; color: #fff; font-weight: 600; letter-spacing: 0.03em; }
.product-meta { padding: 14px 4px; }
.product-meta .product-category { font-size: 11px; color: var(--color-text-sub); text-transform: uppercase; letter-spacing: 0.06em; display: block; margin-bottom: 4px; }
.product-meta h3 { font-size: 15px; font-weight: 600; color: var(--color-text); margin-bottom: 6px; }
.product-price { font-size: 16px; font-weight: 700; color: var(--color-highlight); }
.empty-note { text-align: center; padding: 60px 0; color: var(--color-text-sub); font-size: 14px; }

@media (max-width: 1024px) { .product-grid { grid-template-columns: repeat(3, 1fr); } .category-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) {
  .hero-grid { flex-direction: column; min-height: auto; }
  .hero-title { font-size: 40px; }
  .hero-visual { height: 240px; }
  .hero-cards { transform: scale(0.7); }
  .product-grid { grid-template-columns: repeat(2, 1fr); }
  .category-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
