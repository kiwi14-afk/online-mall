<template>
  <div class="page">
    <div class="container">
      <!-- Filter bar -->
      <div class="filter-bar">
        <div class="filter-left">
          <el-input v-model="searchKeyword" placeholder="搜索商品..." clearable @clear="doSearch" @keyup.enter="doSearch" size="large" class="search-input" />
          <el-select v-model="selectedCategory" placeholder="全部分类" clearable @change="doSearch" size="large" class="cat-select">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </div>
        <span class="result-count">{{ total }} 件商品</span>
      </div>

      <!-- Grid -->
      <div class="product-grid" v-if="products.length > 0">
        <article
          v-for="product in products"
          :key="product.id"
          class="product-card"
          @click="$router.push(`/product/${product.id}`)"
        >
          <div class="product-image-wrap">
            <img :src="product.imageUrl || 'https://placehold.co/600x600/FAF8F5/1A1A1A?text=' + product.name?.charAt(0)" :alt="product.name" loading="lazy" />
            <div class="product-overlay"><span class="overlay-text">查看详情</span></div>
          </div>
          <div class="product-meta">
            <h3>{{ product.name }}</h3>
            <div class="product-row">
              <span class="product-price">¥{{ product.price }}</span>
              <span class="product-category">{{ product.categoryName }}</span>
            </div>
          </div>
        </article>
      </div>

      <el-empty v-else description="暂无商品" />

      <div class="pagination-row" v-if="total > pageSize">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="currentPage" @current-change="doSearch" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProductList, getCategories } from '../api/product'

const route = useRoute()
const searchKeyword = ref('')
const selectedCategory = ref(null)
const products = ref([])
const categories = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 12

async function doSearch() {
  const params = {
    page: currentPage.value - 1, size: pageSize,
    keyword: searchKeyword.value || undefined,
    categoryId: selectedCategory.value || undefined
  }
  try {
    const res = await getProductList(params)
    products.value = res.data.content || []
    total.value = res.data.totalElements || 0
  } catch (e) { console.warn('Failed to load products') }
}

onMounted(async () => {
  if (route.query.keyword) searchKeyword.value = route.query.keyword
  if (route.query.categoryId) selectedCategory.value = Number(route.query.categoryId)
  try { const catRes = await getCategories(); categories.value = catRes.data || [] } catch (e) { /* empty */ }
  doSearch()
})
</script>

<style scoped>
.page { padding: 48px 0; }
.filter-bar {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 36px; gap: 16px; flex-wrap: wrap;
  padding: 20px 24px; background: var(--color-surface);
  border: 1px solid var(--color-border-light); border-radius: var(--radius-md);
}
.filter-left { display: flex; gap: 12px; }
.search-input { width: 260px; }
.cat-select { width: 160px; }
.result-count { font-size: 13px; color: var(--color-text-sub); white-space: nowrap; font-weight: 500; }

.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; }
.product-card { cursor: pointer; }
.product-image-wrap {
  position: relative; aspect-ratio: 1; overflow: hidden;
  border-radius: var(--radius-md); background: #E8E4DF;
  border: 1px solid var(--color-border-light);
}
.product-image-wrap img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s ease; }
.product-card:hover .product-image-wrap img { transform: scale(1.05); }
.product-card:hover .product-image-wrap { border-color: #ccc; box-shadow: var(--shadow-sm); }
.product-overlay {
  position: absolute; inset: 0; display: flex; align-items: center; justify-content: center;
  background: rgba(0,0,0,0.3); opacity: 0; transition: opacity var(--transition);
  border-radius: var(--radius-md);
}
.product-card:hover .product-overlay { opacity: 1; }
.overlay-text { font-size: 14px; color: #fff; font-weight: 600; letter-spacing: 0.03em; }
.product-meta { padding: 14px 4px; }
.product-meta h3 { font-size: 15px; font-weight: 600; margin-bottom: 6px; }
.product-row { display: flex; justify-content: space-between; align-items: center; }
.product-price { font-size: 16px; font-weight: 700; color: var(--color-highlight); }
.product-category { font-size: 11px; color: var(--color-text-sub); text-transform: uppercase; letter-spacing: 0.06em; }
.pagination-row { display: flex; justify-content: center; margin-top: 48px; }

@media (max-width: 1024px) { .product-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) { .product-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
