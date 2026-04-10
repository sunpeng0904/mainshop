<template>
  <div class="product-list-container">
    <!-- 搜索筛选栏 -->
    <div class="filter-bar card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item :label="$t('common.search')">
          <el-input
            v-model="filterForm.keyword"
            :placeholder="$t('product.searchPlaceholder')"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item :label="$t('product.list')">
          <el-select
            v-model="filterForm.categoryId"
            :placeholder="$t('product.allCategories')"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="cat in flatCategories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('product.priceRange')">
          <el-input-number
            v-model="filterForm.minPrice"
            :min="0"
            :precision="2"
            :placeholder="$t('product.minPrice')"
            style="width: 120px"
          />
          <span class="price-separator">-</span>
          <el-input-number
            v-model="filterForm.maxPrice"
            :min="0"
            :precision="2"
            :placeholder="$t('product.maxPrice')"
            style="width: 120px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            {{ $t('common.search') }}
          </el-button>
          <el-button @click="resetFilter">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 排序和结果 -->
    <div class="result-header">
      <div class="sort-bar">
        <span class="sort-label">{{ $t('product.sortBy') }}：</span>
        <el-radio-group v-model="filterForm.sortField" @change="handleSearch">
          <el-radio-button value="createTime">{{ $t('product.newest') }}</el-radio-button>
          <el-radio-button value="sales">{{ $t('product.sales') }}</el-radio-button>
          <el-radio-button value="price">{{ $t('product.price') }}</el-radio-button>
        </el-radio-group>
        <el-button
          :icon="filterForm.sortOrder === 'asc' ? 'ArrowUp' : 'ArrowDown'"
          circle
          size="small"
          @click="toggleSortOrder"
          style="margin-left: 8px"
        />
      </div>
      <span class="result-count">{{ $t('product.totalProducts', { count: pagination.total }) }}</span>
    </div>

    <!-- 商品列表 -->
    <div v-loading="loading" class="products-grid">
      <ProductCard
        v-for="product in products"
        :key="product.id"
        :product="product"
      />
      <el-empty v-if="!loading && products.length === 0" :description="$t('product.noProducts')" />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="pagination.total > 0">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { useI18n } from 'vue-i18n'
import { getProductList } from '@/api/product'
import ProductCard from '@/components/ProductCard.vue'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const store = useStore()

const loading = ref(false)
const products = ref([])

const filterForm = reactive({
  keyword: '',
  categoryId: null,
  minPrice: null,
  maxPrice: null,
  sortField: 'createTime',
  sortOrder: 'desc'
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 12,
  total: 0
})

// 扁平化分类列表
const flatCategories = computed(() => {
  const cats = store.state.product.categories || []
  const result = []

  const flatten = (list) => {
    list.forEach(item => {
      result.push(item)
      if (item.children && item.children.length) {
        flatten(item.children)
      }
    })
  }

  flatten(cats)
  return result
})

// 搜索
const handleSearch = async () => {
  loading.value = true
  try {
    const params = {
      name: filterForm.keyword || undefined,
      categoryId: filterForm.categoryId || undefined,
      minPrice: filterForm.minPrice || undefined,
      maxPrice: filterForm.maxPrice || undefined,
      sortField: filterForm.sortField,
      sortOrder: filterForm.sortOrder,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }

    const response = await getProductList(params)
    const data = response.data
    products.value = data.records || data.list || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置筛选
const resetFilter = () => {
  filterForm.keyword = ''
  filterForm.categoryId = null
  filterForm.minPrice = null
  filterForm.maxPrice = null
  filterForm.sortField = 'createTime'
  filterForm.sortOrder = 'desc'
  pagination.pageNum = 1
  handleSearch()
}

// 切换排序顺序
const toggleSortOrder = () => {
  filterForm.sortOrder = filterForm.sortOrder === 'asc' ? 'desc' : 'asc'
  handleSearch()
}

// 监听路由参数变化
watch(
  () => route.query,
  (query) => {
    if (query.keyword) {
      filterForm.keyword = query.keyword
    }
    if (query.categoryId) {
      filterForm.categoryId = Number(query.categoryId)
    }
    handleSearch()
  },
  { immediate: true }
)

onMounted(() => {
  store.dispatch('product/getCategories')
})
</script>

<style lang="scss" scoped>
.product-list-container {
  max-width: 1200px;
  margin: 0 auto;
}

.filter-bar {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;

  :deep(.el-form-item) {
    margin-bottom: 0;
  }
}

.price-separator {
  margin: 0 8px;
  color: #909399;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.sort-bar {
  display: flex;
  align-items: center;

  .sort-label {
    margin-right: 12px;
    color: #606266;
  }
}

.result-count {
  color: #909399;
  font-size: 14px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  min-height: 400px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-bottom: 20px;
}
</style>
