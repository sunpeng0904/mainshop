<template>
  <div v-loading="loading" class="product-detail-container">
    <div class="product-main card">
      <!-- 商品图片 -->
      <div class="product-images">
        <el-image
          :src="currentImage"
          :preview-src-list="previewImages"
          fit="contain"
          class="main-image"
        >
          <template #placeholder>
            <div class="image-placeholder">
              <el-icon :size="40"><Picture /></el-icon>
            </div>
          </template>
          <template #error>
            <div class="image-placeholder">
              <el-icon :size="40"><Picture /></el-icon>
            </div>
          </template>
        </el-image>
        <div v-if="productImages.length > 1" class="thumbnail-list">
          <el-image
            v-for="(img, index) in productImages"
            :key="index"
            :src="img"
            fit="cover"
            class="thumbnail"
            :class="{ active: currentImage === img }"
            @click="currentImage = img"
          />
        </div>
      </div>

      <!-- 商品信息 -->
      <div class="product-info">
        <h1 class="product-name">{{ product.name }}</h1>
        <p class="product-desc">{{ product.description }}</p>

        <div class="price-box">
          <span class="current-price">¥{{ product.price }}</span>
          <span v-if="product.originalPrice && product.originalPrice > product.price" class="original-price">
            ¥{{ product.originalPrice }}
          </span>
          <span v-if="hasDiscount" class="discount-tag">
            {{ discountPercent }}% {{ $t('product.off') }}
          </span>
        </div>

        <div class="info-row">
          <span class="label">{{ $t('product.sold') }}：</span>
          <span>{{ product.sales || 0 }}</span>
        </div>
        <div class="info-row">
          <span class="label">{{ $t('product.stock') }}：</span>
          <span>{{ product.stock }}</span>
        </div>
        <div v-if="product.categoryName" class="info-row">
          <span class="label">{{ $t('product.list') }}：</span>
          <span>{{ product.categoryName }}</span>
        </div>

        <!-- 数量选择 -->
        <div class="quantity-row">
          <span class="label">{{ $t('cart.quantity') }}：</span>
          <el-input-number
            v-model="quantity"
            :min="1"
            :max="product.stock"
            size="large"
          />
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button type="primary" size="large" @click="handleAddToCart">
            <el-icon><ShoppingCart /></el-icon>
            {{ $t('product.addToCart') }}
          </el-button>
          <el-button size="large" @click="handleBuyNow">
            {{ $t('product.buyNow') }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 商品详情 -->
    <div class="product-detail card mt-20">
      <el-tabs>
        <el-tab-pane :label="$t('product.detail')">
          <div class="detail-content" v-html="product.detail"></div>
        </el-tab-pane>
        <el-tab-pane :label="$t('product.specifications')">
          <div v-if="hasSpecifications" class="specifications">
            <el-descriptions :column="2" border>
              <el-descriptions-item
                v-for="(value, key) in specList"
                :key="key"
                :label="value.label"
              >
                {{ value.value }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
          <el-empty v-else :description="$t('product.noSpecs')" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { getProductDetail } from '@/api/product'
import { getImageUrls } from '@/utils/image'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const store = useStore()

const loading = ref(false)
const product = ref({})
const currentImage = ref('')
const quantity = ref(1)

// 处理商品图片URL
const productImages = computed(() => {
  return getImageUrls(product.value.images)
})

// 预览图片列表
const previewImages = computed(() => {
  return productImages.value
})

// 是否有折扣
const hasDiscount = computed(() => {
  return product.value.originalPrice && product.value.originalPrice > product.value.price
})

// 折扣百分比
const discountPercent = computed(() => {
  if (hasDiscount.value) {
    return Math.round((1 - product.value.price / product.value.originalPrice) * 100)
  }
  return 0
})

// 处理规格参数
const hasSpecifications = computed(() => {
  const spec = product.value.specifications
  if (!spec) return false
  if (typeof spec === 'string') return spec.trim().length > 0
  if (typeof spec === 'object') return Object.keys(spec).length > 0
  return false
})

const specList = computed(() => {
  const spec = product.value.specifications
  if (!spec) return []

  if (typeof spec === 'object' && !Array.isArray(spec)) {
    return Object.entries(spec).map(([key, value]) => ({
      label: key,
      value: typeof value === 'object' ? JSON.stringify(value) : String(value)
    }))
  }

  if (Array.isArray(spec)) {
    return spec.map((item, index) => ({
      label: item.label || item.name || item.key || `参数${index + 1}`,
      value: item.value || item
    }))
  }

  if (typeof spec === 'string') {
    try {
      const parsed = JSON.parse(spec)
      if (typeof parsed === 'object') {
        return Object.entries(parsed).map(([key, value]) => ({
          label: key,
          value: typeof value === 'object' ? JSON.stringify(value) : String(value)
        }))
      }
    } catch (e) {
      return [{ label: '参数', value: spec }]
    }
  }

  return []
})

// 获取商品详情
const fetchProductDetail = async (id) => {
  loading.value = true
  try {
    const response = await getProductDetail(id)
    product.value = response.data || {}
    // 设置默认图片
    if (productImages.value.length > 0) {
      currentImage.value = productImages.value[0]
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error(t('product.noProducts'))
    router.push('/products')
  } finally {
    loading.value = false
  }
}

// 加入购物车
const handleAddToCart = async () => {
  if (!store.getters['user/isLogin']) {
    ElMessage.warning(t('auth.pleaseLogin'))
    router.push('/login')
    return
  }

  try {
    await store.dispatch('cart/addToCart', {
      productId: product.value.id,
      quantity: quantity.value
    })
    ElMessage.success(t('cart.addSuccess') || '已加入购物车')
  } catch (error) {
    console.error('加入购物车失败:', error)
  }
}

// 立即购买
const handleBuyNow = async () => {
  if (!store.getters['user/isLogin']) {
    ElMessage.warning(t('auth.pleaseLogin'))
    router.push('/login')
    return
  }

  try {
    await store.dispatch('cart/addToCart', {
      productId: product.value.id,
      quantity: quantity.value
    })
    router.push('/cart')
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 监听路由参数变化
watch(
  () => route.params.id,
  (id) => {
    if (id) {
      fetchProductDetail(Number(id))
    }
  },
  { immediate: true }
)

onMounted(() => {
  if (route.params.id) {
    fetchProductDetail(Number(route.params.id))
  }
})
</script>

<style lang="scss" scoped>
.product-detail-container {
  max-width: 1200px;
  margin: 0 auto;
}

.product-main {
  display: flex;
  gap: 40px;
}

.product-images {
  width: 450px;
  flex-shrink: 0;

  .main-image {
    width: 100%;
    height: 450px;
    border-radius: 8px;
    background: #f5f7fa;
  }

  .image-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #c0c4cc;
  }

  .thumbnail-list {
    display: flex;
    gap: 10px;
    margin-top: 10px;

    .thumbnail {
      width: 80px;
      height: 80px;
      border-radius: 4px;
      cursor: pointer;
      border: 2px solid transparent;

      &.active {
        border-color: #409EFF;
      }
    }
  }
}

.product-info {
  flex: 1;

  .product-name {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
  }

  .product-desc {
    color: #909399;
    margin-bottom: 20px;
  }
}

.price-box {
  background: #fff5f0;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 20px;

  .current-price {
    font-size: 28px;
    font-weight: bold;
    color: #f56c6c;
  }

  .original-price {
    font-size: 16px;
    color: #909399;
    text-decoration: line-through;
    margin-left: 12px;
  }

  .discount-tag {
    display: inline-block;
    margin-left: 12px;
    padding: 2px 8px;
    background: #f56c6c;
    color: #fff;
    font-size: 12px;
    border-radius: 4px;
  }
}

.info-row {
  margin-bottom: 12px;
  font-size: 14px;

  .label {
    color: #909399;
    margin-right: 8px;
  }
}

.quantity-row {
  display: flex;
  align-items: center;
  margin: 20px 0;

  .label {
    color: #909399;
    margin-right: 12px;
  }
}

.action-buttons {
  display: flex;
  gap: 16px;
  margin-top: 30px;

  .el-button {
    min-width: 150px;
  }
}

.detail-content {
  padding: 20px;
  line-height: 1.8;

  :deep(img) {
    max-width: 100%;
  }
}

.specifications {
  padding: 20px;

  :deep(.el-descriptions) {
    .el-descriptions__label {
      width: 150px;
      font-weight: 500;
    }
  }
}
</style>
