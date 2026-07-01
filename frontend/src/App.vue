<template>
  <div id="app" :class="{ 'scrolled': scrolled }">
    <!-- Header -->
    <header class="site-header" :class="{ 'header-scrolled': scrolled }">
      <div class="header-inner">
        <router-link to="/" class="logo">
          <span class="logo-mark">M</span>
          <span class="logo-text">在线商城</span>
        </router-link>

        <nav class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/products" class="nav-link">全部商品</router-link>
        </nav>

        <div class="header-actions">
          <template v-if="isLoggedIn">
            <router-link to="/cart" class="icon-btn cart-btn">
              <el-icon><ShoppingCart /></el-icon>
              <span v-if="cartCount > 0" class="cart-dot">{{ cartCount }}</span>
            </router-link>
            <el-dropdown @command="handleCommand" trigger="click">
              <span class="user-chip">
                <span class="user-avatar">{{ username.charAt(0) }}</span>
                <span class="user-name">{{ username }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="text-link">登录</router-link>
            <router-link to="/register" class="btn btn-primary btn-sm">注册</router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- Main -->
    <main>
      <router-view />
    </main>

    <!-- Footer -->
    <footer class="site-footer">
      <div class="footer-inner">
        <div class="footer-top">
          <div class="footer-brand">
            <span class="logo-mark">M</span>
            <p>软件服务工程期末大作业<br>基于微服务架构的在线商城</p>
          </div>
          <div class="footer-links">
            <span>用户服务 · 商品服务 · 订单服务 · 支付服务</span>
            <span>Spring Cloud · Nacos · Sentinel · Docker</span>
          </div>
        </div>
        <div class="footer-bottom">
          <span>© 2026 Online Mall</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { isLoggedIn, getUser, logout as doLogout } from './utils/auth'

const router = useRouter()
const route = useRoute()
const scrolled = ref(false)
const cartCount = ref(0)

const username = computed(() => {
  const user = getUser()
  return user ? user.username : '用户'
})

function handleCommand(command) {
  if (command === 'logout') {
    doLogout()
    router.push('/login')
  } else if (command === 'orders') {
    router.push('/orders')
  }
}

function updateCartCount() {
  const cart = JSON.parse(localStorage.getItem('mall_cart') || '[]')
  cartCount.value = cart.reduce((sum, item) => sum + item.quantity, 0)
}

onMounted(() => {
  updateCartCount()
  window.addEventListener('cart-updated', updateCartCount)
  window.addEventListener('scroll', () => {
    scrolled.value = window.scrollY > 20
  })
})
</script>

<style>
/* ========== DESIGN SYSTEM : CSS Custom Properties ========== */
:root {
  --color-bg:       #FAF8F5;
  --color-surface:  #FFFFFF;
  --color-text:     #1A1A1A;
  --color-text-dim: #6B6B6B;
  --color-text-sub: #999999;
  --color-accent:   #2D2D2D;
  --color-accent-hover: #4A4A4A;
  --color-highlight: #C8553D;
  --color-border:   #E8E4DF;
  --color-border-light: #F0EDE9;
  --color-success:  #5B8C5A;
  --font-display:   'Noto Serif SC', 'SimSun', 'STSong', serif;
  --font-body:      'Inter', 'PingFang SC', 'Microsoft YaHei', 'Helvetica Neue', sans-serif;
  --font-mono:      'SF Mono', 'Fira Code', monospace;
  --radius-sm: 4px;
  --radius-md: 8px;
  --radius-lg: 16px;
  --shadow-sm: 0 1px 2px rgba(0,0,0,0.04);
  --shadow-md: 0 4px 16px rgba(0,0,0,0.06);
  --transition: 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

/* ========== RESET ========== */
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
html { -webkit-font-smoothing: antialiased; -moz-osx-font-smoothing: grayscale; }
body {
  font-family: var(--font-body);
  font-size: 15px;
  line-height: 1.6;
  color: var(--color-text);
  background: var(--color-bg);
  letter-spacing: 0.01em;
}
a { text-decoration: none; color: inherit; }
img { max-width: 100%; display: block; }
button { cursor: pointer; font-family: inherit; }

/* ========== GLOBAL BUTTONS ========== */
.btn {
  display: inline-flex; align-items: center; justify-content: center; gap: 6px;
  padding: 10px 24px; font-size: 14px; font-weight: 500;
  border: none; border-radius: var(--radius-sm);
  background: var(--color-accent); color: #fff;
  transition: all var(--transition); letter-spacing: 0.02em;
}
.btn:hover { background: var(--color-accent-hover); transform: translateY(-1px); }
.btn:active { transform: translateY(0); }
.btn-sm { padding: 6px 16px; font-size: 13px; }
.btn-outline {
  background: transparent; color: var(--color-accent);
  border: 1.5px solid var(--color-border);
}
.btn-outline:hover { border-color: var(--color-accent); background: transparent; transform: translateY(-1px); }
.btn-ghost { background: transparent; color: var(--color-accent); }
.btn-ghost:hover { background: rgba(0,0,0,0.04); transform: none; }
.btn-danger { background: var(--color-highlight); }

.text-link {
  font-size: 14px; color: var(--color-text-dim);
  transition: color var(--transition);
}
.text-link:hover { color: var(--color-text); }

/* ========== HEADER ========== */
.site-header {
  position: sticky; top: 0; z-index: 100;
  background: rgba(250,248,245,0.85); backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid transparent;
  transition: all var(--transition);
}
.header-scrolled { border-bottom-color: var(--color-border); }
.header-inner {
  max-width: 1280px; margin: 0 auto;
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 40px; height: 60px;
}
.logo { display: flex; align-items: center; gap: 8px; }
.logo-mark {
  width: 28px; height: 28px; display: flex; align-items: center; justify-content: center;
  background: var(--color-accent); color: #fff; font-size: 14px; font-weight: 700;
  border-radius: 2px; font-family: var(--font-display);
}
.logo-text { font-size: 16px; font-weight: 500; color: var(--color-text); letter-spacing: 0.03em; }
.nav-links { display: flex; gap: 32px; }
.nav-link {
  font-size: 14px; color: var(--color-text-dim); font-weight: 400;
  transition: color var(--transition); position: relative; padding: 4px 0;
}
.nav-link:hover, .nav-link.router-link-active { color: var(--color-text); }
.nav-link::after {
  content: ''; position: absolute; bottom: -2px; left: 0; width: 0; height: 1.5px;
  background: var(--color-accent); transition: width var(--transition);
}
.nav-link:hover::after, .nav-link.router-link-active::after { width: 100%; }
.header-actions { display: flex; align-items: center; gap: 16px; }
.icon-btn {
  position: relative; width: 36px; height: 36px; display: flex; align-items: center;
  justify-content: center; border-radius: var(--radius-sm); color: var(--color-text-dim);
  transition: all var(--transition);
}
.icon-btn:hover { color: var(--color-text); background: rgba(0,0,0,0.04); }
.cart-dot {
  position: absolute; top: 2px; right: 2px; min-width: 16px; height: 16px;
  padding: 0 4px; font-size: 10px; font-weight: 600; line-height: 16px;
  text-align: center; background: var(--color-highlight); color: #fff;
  border-radius: 8px;
}
.user-chip {
  display: flex; align-items: center; gap: 8px; cursor: pointer; padding: 4px 12px 4px 4px;
  border-radius: 24px; transition: background var(--transition);
}
.user-chip:hover { background: rgba(0,0,0,0.04); }
.user-avatar {
  width: 30px; height: 30px; display: flex; align-items: center; justify-content: center;
  background: var(--color-accent); color: #fff; font-size: 13px; font-weight: 600;
  border-radius: 50%;
}
.user-name { font-size: 13px; color: var(--color-text-dim); }

/* ========== FOOTER ========== */
.site-footer {
  margin-top: 80px; border-top: 1px solid var(--color-border);
  background: var(--color-surface);
}
.footer-inner { max-width: 1280px; margin: 0 auto; padding: 40px; }
.footer-top { display: flex; justify-content: space-between; align-items: flex-start; gap: 40px; flex-wrap: wrap; }
.footer-brand { display: flex; align-items: center; gap: 12px; }
.footer-brand p { font-size: 13px; color: var(--color-text-sub); line-height: 1.7; }
.footer-links { display: flex; flex-direction: column; gap: 6px; font-size: 13px; color: var(--color-text-sub); }
.footer-bottom { margin-top: 32px; padding-top: 20px; border-top: 1px solid var(--color-border-light); text-align: center; font-size: 12px; color: var(--color-text-sub); }

/* ========== UTILITY ========== */
.sr-only { position: absolute; width: 1px; height: 1px; overflow: hidden; clip: rect(0,0,0,0); }
.container { max-width: 1280px; margin: 0 auto; padding: 0 40px; }

@media (max-width: 768px) {
  .header-inner { padding: 0 20px; }
  .nav-links { display: none; }
  .container { padding: 0 20px; }
  .footer-inner { padding: 32px 20px; }
}
</style>
