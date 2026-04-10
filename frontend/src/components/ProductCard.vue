<template>
  <div class="product-card" @click="goToDetail">
    <div class="product-image">
      <el-image
        :src="productImage"
        :preview-src-list="[]"
        fit="cover"
        lazy
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
      <div v-if="hasDiscount" class="discount-tag">
        {{ discountPercent }}% {{ $t('product.off') }}
      </div>
    </div>
    <div class="product-info">
      <h4 class="product-name">{{ product.name }}</h4>
      <p class="product-desc">{{ product.description }}</p>
      <div class="product-footer">
        <div class="price-box">
          <span class="current-price">¥{{ product.price }}</span>
          <span v-if="hasDiscount" class="original-price">
            ¥{{ product.originalPrice }}
          </span>
        </div>
        <div class="sales">{{ $t('product.sold') }} {{ product.sales || 0 }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getImageUrl } from '@/utils/image'

const { t } = useI18n()
const router = useRouter()

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

// 商品图片（取第一张或默认）
const productImage = computed(() => {
  const images = props.product.images
  if (images && images.length > 0) {
    const firstImage = Array.isArray(images) ? images[0] : images
    return getImageUrl(firstImage)
  }
  return 'https://via.placeholder.com/400x400?text=No+Image'
})

// 是否有折扣
const hasDiscount = computed(() => {
  return props.product.originalPrice && props.product.originalPrice > props.product.price
})

// 折扣百分比
const discountPercent = computed(() => {
  if (hasDiscount.value) {
    return Math.round((1 - props.product.price / props.product.originalPrice) * 100)
  }
  return 0
})

const goToDetail = () => {
  router.push(`/product/${props.product.id}`)
}
</script>

<style lang="scss" scoped>
.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);

    .product-name {
      color: #409EFF;
    }
  }
}

.product-image {
  position: relative;
  height: 250px;
  background: #f5f7fa;

  :deep(.el-image) {
    width: 100%;
    height: 100%;
  }

  .image-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #c0c4cc;
  }

  .discount-tag {
    position: absolute;
    top: 8px;
    right: 8px;
    background: #f56c6c;
    color: #fff;
    font-size: 12px;
    padding: 2px 8px;
    border-radius: 4px;
  }
}

.product-info {
  padding: 12px;

  .product-name {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-bottom: 6px;
  }

  .product-desc {
    font-size: 12px;
    color: #909399;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-bottom: 8px;
  }
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-box {
  .current-price {
    font-size: 18px;
    font-weight: bold;
    color: #f56c6c;
  }

  .original-price {
    font-size: 12px;
    color: #c0c4cc;
    text-decoration: line-through;
    margin-left: 6px;
  }
}

.sales {
  font-size: 12px;
  color: #909399;
}
</style>
