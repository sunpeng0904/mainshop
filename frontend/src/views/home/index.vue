<template>
  <div class="home-container">
    <!-- 轮播图 -->
    <el-carousel height="400px" class="banner-carousel">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <div class="banner-item" :style="{ backgroundColor: item.bgColor }">
          <div class="banner-content">
            <h2>{{ $t(item.titleKey) }}</h2>
            <p>{{ $t(item.descKey) }}</p>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 商品分类 -->
    <section class="section categories-section">
      <div class="section-header">
        <h3>{{ $t('home.categories') }}</h3>
      </div>
      <div v-loading="loadingCategories" class="categories-grid">
        <div
          v-for="category in categories"
          :key="category.id"
          class="category-item"
          @click="goToCategory(category)"
        >
          <el-icon :size="40" :color="'#409EFF'">
            <component :is="category.icon || 'Folder'" />
          </el-icon>
          <span class="category-name">{{ category.name }}</span>
        </div>
        <el-empty v-if="!loadingCategories && categories.length === 0" :description="$t('home.noCategories')" />
      </div>
    </section>

    <!-- 热门商品 -->
    <section class="section">
      <div class="section-header">
        <h3>{{ $t('home.hotProducts') }}</h3>
        <el-button text type="primary" @click="$router.push('/products')">
          {{ $t('home.viewMore') }}
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <div v-loading="loadingHot" class="products-grid">
        <ProductCard
          v-for="product in hotProducts"
          :key="product.id"
          :product="product"
        />
        <el-empty v-if="!loadingHot && hotProducts.length === 0" :description="$t('home.noHotProducts')" />
      </div>
    </section>

    <!-- 新品推荐 -->
    <section class="section">
      <div class="section-header">
        <h3>{{ $t('home.newProducts') }}</h3>
      </div>
      <div v-loading="loadingNew" class="products-grid">
        <ProductCard
          v-for="product in newProducts"
          :key="product.id"
          :product="product"
        />
        <el-empty v-if="!loadingNew && newProducts.length === 0" :description="$t('home.noNewProducts')" />
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import ProductCard from '@/components/ProductCard.vue'

const { t } = useI18n()
const store = useStore()
const router = useRouter()

// 加载状态
const loadingCategories = ref(false)
const loadingHot = ref(false)
const loadingNew = ref(false)

// 轮播图数据
const banners = ref([
  { id: 1, titleKey: 'home.banner1Title', descKey: 'home.banner1Desc', bgColor: '#409EFF' },
  { id: 2, titleKey: 'home.banner2Title', descKey: 'home.banner2Desc', bgColor: '#67C23A' },
  { id: 3, titleKey: 'home.banner3Title', descKey: 'home.banner3Desc', bgColor: '#E6A23C' }
])

// 分类数据
const categories = ref([])

// 热门商品
const hotProducts = ref([])

// 新品推荐
const newProducts = ref([])

// 跳转到分类
const goToCategory = (category) => {
  router.push({
    path: '/products',
    query: { categoryId: category.id }
  })
}

// 获取数据
const fetchData = async () => {
  // 获取分类
  loadingCategories.value = true
  try {
    const cats = await store.dispatch('product/getCategories')
    categories.value = cats || []
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
})
</script>

<style lang="scss" scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
}

.banner-carousel {
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 30px;
}

.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.banner-content {
  text-align: center;
  color: #fff;

  h2 {
    font-size: 36px;
    margin-bottom: 16px;
  }

  p {
    font-size: 18px;
    opacity: 0.9;
  }
}

.section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  h3 {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
  }
}

.categories-section {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 16px;
  min-height: 100px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background-color: #f5f7fa;
    transform: translateY(-2px);
  }

  .category-name {
    margin-top: 12px;
    font-size: 14px;
    color: #606266;
  }
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  min-height: 200px;
}
</style>
