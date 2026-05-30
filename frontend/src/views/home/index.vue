<template>
  <div class="home-container">
    <!-- 粒子背景 -->
    <div class="particles-bg" ref="particlesRef"></div>

    <!-- 搜索栏 -->
    <section class="search-section">
      <div class="search-wrapper">
        <el-input
          v-model="searchKeyword"
          :placeholder="$t('home.searchPlaceholder')"
          class="search-input"
          size="large"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
              {{ $t('home.search') }}
            </el-button>
          </template>
        </el-input>
      </div>
    </section>

    <!-- 轮播图区域 -->
    <section class="hero-section">
      <el-carousel height="500px" class="banner-carousel" :interval="5000" arrow="hover">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="banner-item" :style="{ background: item.gradient }">
            <div class="banner-content" :class="{ 'animate-in': activeBanner === item.id }">
              <div class="banner-badge">{{ $t(item.badgeKey) }}</div>
              <h1 class="banner-title">{{ $t(item.titleKey) }}</h1>
              <p class="banner-desc">{{ $t(item.descKey) }}</p>
              <el-button type="primary" size="large" class="banner-btn">
                {{ $t('home.shopNow') }}
                <el-icon class="el-icon--right"><ArrowRight /></el-icon>
              </el-button>
            </div>
            <!-- 浮动装饰元素 -->
            <div class="floating-shapes">
              <div class="shape shape-1"></div>
              <div class="shape shape-2"></div>
              <div class="shape shape-3"></div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 数据统计 -->
    <section class="stats-section">
      <div class="stats-grid">
        <div v-for="stat in stats" :key="stat.id" class="stat-card" @mouseenter="stat.hovered = true" @mouseleave="stat.hovered = false">
          <div class="stat-icon" :style="{ background: stat.color }">
            <el-icon :size="28"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stat.value }}</div>
            <div class="stat-label">{{ $t(stat.labelKey) }}</div>
          </div>
          <div class="stat-glow" :style="{ background: stat.color }"></div>
        </div>
      </div>
    </section>

    <!-- 商品分类 -->
    <section class="section categories-section">
      <div class="section-header">
        <div class="section-title-group">
          <h2 class="section-title">{{ $t('home.categories') }}</h2>
          <p class="section-subtitle">{{ $t('home.categoriesSubtitle') }}</p>
        </div>
        <el-button text type="primary" class="view-all-btn">
          {{ $t('home.viewAll') }}
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <!-- 骨架屏 -->
      <div v-if="loadingCategories" class="categories-grid">
        <div v-for="i in 6" :key="i" class="category-skeleton">
          <el-skeleton :rows="1" animated />
        </div>
      </div>
      <div v-else class="categories-grid">
        <div
          v-for="(category, index) in categories"
          :key="category.id"
          class="category-card"
          :style="{ animationDelay: `${index * 0.1}s` }"
          @click="goToCategory(category)"
          @mouseenter="category.active = true"
          @mouseleave="category.active = false"
        >
          <div class="category-icon-wrapper">
            <el-icon :size="48" :color="category.active ? '#fff' : '#409EFF'">
              <component :is="category.icon || 'Folder'" />
            </el-icon>
          </div>
          <h3 class="category-name">{{ category.name }}</h3>
          <p class="category-count">{{ category.count || 0 }} {{ $t('home.items') }}</p>
          <div class="category-glow"></div>
        </div>
        <el-empty v-if="categories.length === 0" :description="$t('home.noCategories')" />
      </div>
    </section>

    <!-- 热门商品 -->
    <section class="section hot-section">
      <div class="section-header">
        <div class="section-title-group">
          <h2 class="section-title">
            <span class="fire-icon">🔥</span>
            {{ $t('home.hotProducts') }}
          </h2>
          <p class="section-subtitle">{{ $t('home.hotSubtitle') }}</p>
        </div>
        <el-button text type="primary" class="view-all-btn" @click="$router.push('/products')">
          {{ $t('home.viewMore') }}
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <!-- 骨架屏 -->
      <div v-if="loadingHot" class="products-grid">
        <div v-for="i in 4" :key="i" class="product-skeleton">
          <el-skeleton :rows="3" animated />
        </div>
      </div>
      <div v-else class="products-grid">
        <div v-for="(product, index) in hotProducts" :key="product.id" class="product-card-wrapper" :style="{ animationDelay: `${index * 0.15}s` }">
          <ProductCard 
            :product="product" 
            :show-rank="true" 
            :rank="index + 1"
            @preview="showProductPreview"
            @favorite="toggleFavorite"
          />
        </div>
        <el-empty v-if="hotProducts.length === 0" :description="$t('home.noHotProducts')" />
      </div>
    </section>

    <!-- 新品推荐 -->
    <section class="section new-section">
      <div class="section-header">
        <div class="section-title-group">
          <h2 class="section-title">
            <span class="new-icon">✨</span>
            {{ $t('home.newProducts') }}
          </h2>
          <p class="section-subtitle">{{ $t('home.newSubtitle') }}</p>
        </div>
      </div>
      <!-- 骨架屏 -->
      <div v-if="loadingNew" class="new-products-grid">
        <div v-for="i in 4" :key="i" class="new-product-skeleton">
          <el-skeleton :rows="2" animated />
        </div>
      </div>
      <div v-else class="new-products-grid">
        <div v-for="(product, index) in newProducts" :key="product.id" class="new-product-card" :style="{ animationDelay: `${index * 0.2}s` }">
          <div class="new-product-image">
            <img v-lazy="product.image" :alt="product.name" />
            <div class="new-badge">{{ $t('home.new') }}</div>
          </div>
          <div class="new-product-info">
            <h3>{{ product.name }}</h3>
            <p class="new-product-price">¥{{ product.price }}</p>
            <el-button type="primary" size="small" class="add-to-cart-btn" @click="addToCart(product)">
              {{ $t('home.addToCart') }}
            </el-button>
          </div>
        </div>
        <el-empty v-if="newProducts.length === 0" :description="$t('home.noNewProducts')" />
      </div>
    </section>

    <!-- 品牌故事 -->
    <section class="brand-story-section">
      <div class="brand-story-content">
        <h2>{{ $t('home.brandStoryTitle') }}</h2>
        <p>{{ $t('home.brandStoryDesc') }}</p>
        <el-button type="primary" size="large" class="learn-more-btn">
          {{ $t('home.learnMore') }}
        </el-button>
      </div>
    </section>

    <!-- 商品预览模态框 -->
    <ProductPreview
      v-model:visible="previewVisible"
      :product="previewProduct"
      @add-to-cart="addToCart"
      @toggle-favorite="toggleFavorite"
    />

    <!-- 回到顶部 -->
    <el-backtop :right="100" :bottom="100" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, reactive, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Search, ArrowRight } from '@element-plus/icons-vue'
import ProductCard from '@/components/ProductCard.vue'
import ProductPreview from '@/components/ProductPreview.vue'
import { ElMessage } from 'element-plus'

const { t } = useI18n()
const store = useStore()
const router = useRouter()
const particlesRef = ref(null)

// 搜索相关
const searchKeyword = ref('')
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/products',
      query: { keyword: searchKeyword.value.trim() }
    })
  }
}

// 加载状态
const loadingCategories = ref(false)
const loadingHot = ref(false)
const loadingNew = ref(false)
const activeBanner = ref(1)

// 预览相关
const previewVisible = ref(false)
const previewProduct = ref(null)

// 轮播图数据
const banners = ref([
  { 
    id: 1, 
    titleKey: 'home.banner1Title', 
    descKey: 'home.banner1Desc', 
    badgeKey: 'home.banner1Badge',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' 
  },
  { 
    id: 2, 
    titleKey: 'home.banner2Title', 
    descKey: 'home.banner2Desc', 
    badgeKey: 'home.banner2Badge',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' 
  },
  { 
    id: 3, 
    titleKey: 'home.banner3Title', 
    descKey: 'home.banner3Desc', 
    badgeKey: 'home.banner3Badge',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' 
  }
])

// 统计数据
const stats = ref([
  { id: 1, icon: 'ShoppingCart', value: '10K+', labelKey: 'home.statProducts', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', hovered: false },
  { id: 2, icon: 'User', value: '50K+', labelKey: 'home.statUsers', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', hovered: false },
  { id: 3, icon: 'Star', value: '4.9', labelKey: 'home.statRating', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', hovered: false },
  { id: 4, icon: 'Medal', value: '100+', labelKey: 'home.statBrands', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)', hovered: false }
])

// 分类数据
const categories = ref([])

// 热门商品
const hotProducts = ref([])

// 新品推荐
const newProducts = ref([])

// 收藏列表
const favorites = ref(new Set())

// 跳转到分类
const goToCategory = (category) => {
  router.push({
    path: '/products',
    query: { categoryId: category.id }
  })
}

// 显示商品预览
const showProductPreview = (product) => {
  previewProduct.value = product
  previewVisible.value = true
}

// 添加到购物车
const addToCart = async (product) => {
  try {
    await store.dispatch('cart/addToCart', { productId: product.id, quantity: 1 })
    ElMessage.success(t('home.addToCartSuccess'))
  } catch (error) {
    ElMessage.error(t('home.addToCartFailed'))
  }
}

// 切换收藏状态
const toggleFavorite = async (product) => {
  try {
    if (favorites.value.has(product.id)) {
      await store.dispatch('user/removeFavorite', product.id)
      favorites.value.delete(product.id)
      ElMessage.success(t('home.removeFavoriteSuccess'))
    } else {
      await store.dispatch('user/addFavorite', product.id)
      favorites.value.add(product.id)
      ElMessage.success(t('home.addFavoriteSuccess'))
    }
  } catch (error) {
    ElMessage.error(t('home.favoriteFailed'))
  }
}

// 创建粒子背景
const createParticles = () => {
  if (!particlesRef.value) return
  // 简化版粒子效果，实际可使用 canvas 或第三方库
}

// 滚动动画观察器
let observer = null
const setupScrollAnimations = () => {
  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('animate-in')
        observer.unobserve(entry.target)
      }
    })
  }, { threshold: 0.1 })

  document.querySelectorAll('.section').forEach(section => {
    observer.observe(section)
  })
}

// 获取数据
const fetchData = async () => {
  // 获取分类
  loadingCategories.value = true
  try {
    const cats = await store.dispatch('product/getCategories')
    categories.value = (cats || []).map(c => ({ ...c, active: false, count: Math.floor(Math.random() * 100) }))
  } catch (error) {
    console.error('获取分类失败:', error)
  } finally {
    loadingCategories.value = false
  }

  // 获取热门商品
  loadingHot.value = true
  try {
    hotProducts.value = await store.dispatch('product/getHotProducts', 8)
  } catch (error) {
    console.error('获取热门商品失败:', error)
  } finally {
    loadingHot.value = false
  }

  // 获取新品推荐
  loadingNew.value = true
  try {
    newProducts.value = await store.dispatch('product/getNewProducts', 4)
  } catch (error) {
    console.error('获取新品失败:', error)
  } finally {
    loadingNew.value = false
  }
}

onMounted(() => {
  fetchData()
  createParticles()
  setupScrollAnimations()
})

onUnmounted(() => {
  if (observer) {
    observer.disconnect()
  }
})
</script>

<style lang="scss" scoped>
.home-container {
  position: relative;
  overflow: hidden;
}

// 粒子背景
.particles-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

// 搜索区域
.search-section {
  position: relative;
  z-index: 10;
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.search-wrapper {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.search-input {
  :deep(.el-input__wrapper) {
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
  
  :deep(.el-input-group__append) {
    background: linear-gradient(135deg, #409EFF 0%, #6366f1 100%);
    border: none;
    
    .el-button {
      color: #fff;
      
      &:hover {
        opacity: 0.9;
      }
    }
  }
}

// 轮播图区域
.hero-section {
  position: relative;
  z-index: 1;
  margin-bottom: 40px;
}

.banner-carousel {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  cursor: pointer;
}

.banner-content {
  text-align: center;
  color: #fff;
  position: relative;
  z-index: 2;
  opacity: 0;
  transform: translateY(30px);
  animation: fadeInUp 0.8s ease forwards;
}

.banner-badge {
  display: inline-block;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  padding: 8px 20px;
  border-radius: 50px;
  font-size: 14px;
  margin-bottom: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.banner-title {
  font-size: 48px;
  font-weight: 800;
  margin-bottom: 16px;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  background: linear-gradient(to right, #fff, #e0e7ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.banner-desc {
  font-size: 20px;
  opacity: 0.95;
  margin-bottom: 32px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.banner-btn {
  padding: 16px 40px;
  font-size: 18px;
  border-radius: 50px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.4);
  transition: all 0.3s;
  
  &:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: scale(1.05);
  }
}

.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  pointer-events: none;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 100px;
  height: 100px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.shape-3 {
  width: 80px;
  height: 80px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

// 统计区域
.stats-section {
  position: relative;
  z-index: 1;
  margin-top: -60px;
  margin-bottom: 40px;
  padding: 0 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  }
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-number {
  font-size: 28px;
  font-weight: 800;
  color: #1a1a2e;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.stat-glow {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  opacity: 0;
  transition: opacity 0.3s;
  filter: blur(40px);
  
  .stat-card:hover & {
    opacity: 0.1;
  }
}

// 通用区块样式
.section {
  position: relative;
  z-index: 1;
  margin-bottom: 60px;
  padding: 0 20px;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.6s ease;
  
  &.animate-in {
    opacity: 1;
    transform: translateY(0);
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.section-title {
  font-size: 32px;
  font-weight: 800;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.section-subtitle {
  font-size: 16px;
  color: #666;
}

.fire-icon, .new-icon {
  font-size: 28px;
  margin-right: 8px;
}

.view-all-btn {
  font-size: 16px;
}

// 分类区域
.categories-section {
  background: #fff;
  padding: 40px;
  border-radius: 24px;
  max-width: 1200px;
  margin: 0 auto 60px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.05);
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
  min-height: 100px;
}

.category-skeleton {
  padding: 24px;
  border-radius: 16px;
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f4ff 100%);
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 16px;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s;
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f4ff 100%);
  position: relative;
  overflow: hidden;
  animation: fadeInUp 0.6s ease forwards;
  opacity: 0;
  
  &:hover {
    transform: translateY(-8px) scale(1.02);
    background: linear-gradient(135deg, #409EFF 0%, #6366f1 100%);
    box-shadow: 0 20px 40px rgba(64, 158, 255, 0.3);
    
    .category-icon-wrapper {
      background: rgba(255, 255, 255, 0.2);
    }
    
    .category-name {
      color: #fff;
    }
    
    .category-count {
      color: rgba(255, 255, 255, 0.8);
    }
  }
}

.category-icon-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(64, 158, 255, 0.1);
  transition: all 0.3s;
  margin-bottom: 12px;
}

.category-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  transition: all 0.3s;
}

.category-count {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  transition: all 0.3s;
}

.category-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: radial-gradient(circle, rgba(64, 158, 255, 0.3) 0%, transparent 70%);
  transition: all 0.5s;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  
  .category-card:hover & {
    width: 200%;
    height: 200%;
  }
}

// 热门商品区域
.hot-section {
  background: linear-gradient(135deg, #fff5f5 0%, #fff0f6 100%);
  padding: 60px 20px;
  border-radius: 32px;
  max-width: 1240px;
  margin: 0 auto 60px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.product-skeleton {
  background: #fff;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.product-card-wrapper {
  animation: fadeInUp 0.6s ease forwards;
  opacity: 0;
}

// 新品区域
.new-section {
  max-width: 1200px;
  margin: 0 auto 60px;
}

.new-products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.new-product-skeleton {
  background: #fff;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.new-product-card {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  animation: fadeInUp 0.6s ease forwards;
  opacity: 0;
  
  &:hover {
    transform: translateY(-10px);
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    
    .new-product-image img {
      transform: scale(1.1);
    }
  }
}

.new-product-image {
  height: 200px;
  overflow: hidden;
  position: relative;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s;
  }
}

.new-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
  padding: 6px 16px;
  border-radius: 50px;
  font-size: 12px;
  font-weight: 600;
}

.new-product-info {
  padding: 20px;
  
  h3 {
    font-size: 18px;
    font-weight: 600;
    color: #1a1a2e;
    margin-bottom: 8px;
  }
}

.new-product-price {
  font-size: 24px;
  font-weight: 800;
  color: #f5576c;
  margin-bottom: 16px;
}

.add-to-cart-btn {
  width: 100%;
  border-radius: 12px;
}

// 品牌故事区域
.brand-story-section {
  position: relative;
  z-index: 1;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  padding: 80px 20px;
  text-align: center;
  color: #fff;
}

.brand-story-content {
  max-width: 800px;
  margin: 0 auto;
  
  h2 {
    font-size: 36px;
    font-weight: 800;
    margin-bottom: 20px;
    background: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
  
  p {
    font-size: 18px;
    opacity: 0.9;
    line-height: 1.8;
    margin-bottom: 32px;
  }
}

.learn-more-btn {
  padding: 16px 48px;
  font-size: 18px;
  border-radius: 50px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
  
  &:hover {
    transform: scale(1.05);
    box-shadow: 0 10px 30px rgba(79, 172, 254, 0.4);
  }
}

// 动画关键帧
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .banner-title {
    font-size: 32px;
  }
  
  .banner-desc {
    font-size: 16px;
  }
  
  .section-title {
    font-size: 24px;
  }
  
  .categories-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .search-section {
    padding: 10px;
  }
  
  .search-wrapper {
    padding: 10px;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .categories-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .products-grid {
    grid-template-columns: 1fr;
  }
  
  .new-products-grid {
    grid-template-columns: 1fr;
  }
}
</style>
