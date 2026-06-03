<template>
  <el-dialog
    v-model="dialogVisible"
    :title="product?.name"
    width="800px"
    class="product-preview-dialog"
    @close="handleClose"
  >
    <div v-if="product" class="preview-content">
      <div class="preview-images">
        <div class="main-image">
          <img :src="product.image" :alt="product.name" />
        </div>
        <div v-if="product.images?.length > 1" class="thumbnail-list">
          <div
            v-for="(image, index) in product.images"
            :key="index"
            class="thumbnail"
            :class="{ active: currentImageIndex === index }"
            @click="currentImageIndex = index"
          >
            <img :src="image" :alt="`${product.name} - ${index + 1}`" />
          </div>
        </div>
      </div>
      
      <div class="preview-info">
        <h2 class="product-name">{{ product.name }}</h2>
        
        <div class="product-price">
          <span class="current-price">¥{{ product.price }}</span>
          <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
          <span v-if="product.discount" class="discount">{{ product.discount }}折</span>
        </div>
        
        <div class="product-meta">
          <div class="meta-item">
            <span class="label">销量</span>
            <span class="value">{{ product.sales || 0 }}</span>
          </div>
          <div class="meta-item">
            <span class="label">库存</span>
            <span class="value">{{ product.stock || 0 }}</span>
          </div>
          <div class="meta-item">
            <span class="label">评分</span>
            <span class="value">
              <el-rate v-model="product.rating" disabled show-score />
            </span>
          </div>
        </div>
        
        <div class="product-description">
          <h3>商品描述</h3>
          <p>{{ product.description || '暂无描述' }}</p>
        </div>
        
        <div class="product-specs" v-if="product.specs?.length">
          <h3>规格参数</h3>
          <div class="specs-grid">
            <div v-for="spec in product.specs" :key="spec.name" class="spec-item">
              <span class="spec-name">{{ spec.name }}</span>
              <span class="spec-value">{{ spec.value }}</span>
            </div>
          </div>
        </div>
        
        <div class="quantity-selector">
          <span class="label">数量</span>
          <el-input-number
            v-model="quantity"
            :min="1"
            :max="product.stock || 99"
            size="large"
          />
        </div>
        
        <div class="action-buttons">
          <el-button
            type="primary"
            size="large"
            class="add-to-cart-btn"
            @click="handleAddToCart"
          >
            <el-icon><ShoppingCart /></el-icon>
            加入购物车
          </el-button>
          
          <el-button
            size="large"
            class="favorite-btn"
            :class="{ favorited: isFavorited }"
            @click="handleToggleFavorite"
          >
            <el-icon><Star /></el-icon>
            {{ isFavorited ? '已收藏' : '收藏' }}
          </el-button>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ShoppingCart, Star } from '@element-plus/icons-vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  product: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'add-to-cart', 'toggle-favorite'])

const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

const currentImageIndex = ref(0)
const quantity = ref(1)
const isFavorited = ref(false)

// 监听产品变化，重置状态
watch(() => props.product, (newProduct) => {
  if (newProduct) {
    currentImageIndex.value = 0
    quantity.value = 1
    isFavorited.value = newProduct.isFavorited || false
  }
})

const handleClose = () => {
  emit('update:visible', false)
}

const handleAddToCart = () => {
  emit('add-to-cart', {
    ...props.product,
    quantity: quantity.value
  })
}

const handleToggleFavorite = () => {
  isFavorited.value = !isFavorited.value
  emit('toggle-favorite', props.product)
}
</script>

<style lang="scss" scoped>
.product-preview-dialog {
  :deep(.el-dialog) {
    border-radius: 20px;
    overflow: hidden;
  }
  
  :deep(.el-dialog__header) {
    padding: 20px 24px;
    margin: 0;
    border-bottom: 1px solid #eee;
  }
  
  :deep(.el-dialog__body) {
    padding: 0;
  }
}

.preview-content {
  display: flex;
  gap: 24px;
  padding: 24px;
}

.preview-images {
  flex: 1;
  max-width: 400px;
}

.main-image {
  width: 100%;
  height: 400px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 12px;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.thumbnail-list {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.thumbnail {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s;
  
  &.active {
    border-color: #409EFF;
  }
  
  &:hover {
    border-color: #409EFF;
  }
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.preview-info {
  flex: 1;
}

.product-name {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 16px;
  line-height: 1.4;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 20px;
}

.current-price {
  font-size: 28px;
  font-weight: 800;
  color: #f56c6c;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}

.discount {
  font-size: 12px;
  color: #fff;
  background: #f56c6c;
  padding: 2px 8px;
  border-radius: 4px;
}

.product-meta {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  
  .label {
    font-size: 12px;
    color: #999;
  }
  
  .value {
    font-size: 14px;
    color: #333;
    font-weight: 500;
  }
}

.product-description {
  margin-bottom: 20px;
  
  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #1a1a2e;
    margin-bottom: 8px;
  }
  
  p {
    font-size: 14px;
    color: #666;
    line-height: 1.6;
  }
}

.product-specs {
  margin-bottom: 20px;
  
  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #1a1a2e;
    margin-bottom: 12px;
  }
}

.specs-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.spec-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 8px;
  
  .spec-name {
    font-size: 14px;
    color: #666;
  }
  
  .spec-value {
    font-size: 14px;
    color: #333;
    font-weight: 500;
  }
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  
  .label {
    font-size: 14px;
    color: #666;
  }
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.add-to-cart-btn {
  flex: 1;
  height: 48px;
  font-size: 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #409EFF 0%, #6366f1 100%);
  border: none;
  
  &:hover {
    opacity: 0.9;
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(64, 158, 255, 0.3);
  }
}

.favorite-btn {
  width: 120px;
  height: 48px;
  font-size: 16px;
  border-radius: 12px;
  
  &.favorited {
    color: #f56c6c;
    border-color: #f56c6c;
    background: #fef0f0;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .preview-content {
    flex-direction: column;
  }
  
  .preview-images {
    max-width: 100%;
  }
  
  .main-image {
    height: 300px;
  }
  
  .product-meta {
    flex-wrap: wrap;
    gap: 16px;
  }
  
  .specs-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .favorite-btn {
    width: 100%;
  }
}
</style>
