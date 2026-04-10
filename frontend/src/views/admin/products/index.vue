<template>
  <div class="admin-product-list">
    <!-- 搜索筛选区域 -->
    <div class="filter-card card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="商品名称">
          <el-input
            v-model="filterForm.name"
            placeholder="请输入商品名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-cascader
            v-model="filterForm.categoryId"
            :options="categoryTree"
            :props="{ value: 'id', label: 'name', checkStrictly: true, emitPath: false }"
            placeholder="全部分类"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部状态" clearable>
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增商品
      </el-button>
      <el-button
        type="success"
        :disabled="selectedIds.length === 0"
        @click="handleBatchPublish"
      >
        批量上架
      </el-button>
      <el-button
        type="warning"
        :disabled="selectedIds.length === 0"
        @click="handleBatchUnpublish"
      >
        批量下架
      </el-button>
      <el-button
        type="danger"
        :disabled="selectedIds.length === 0"
        @click="handleBatchDelete"
      >
        批量删除
      </el-button>
    </div>

    <!-- 商品表格 -->
    <div class="table-card card">
      <el-table
        v-loading="loading"
        :data="products"
        @selection-change="handleSelectionChange"
        stripe
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.images && row.images.length > 0"
              :src="row.images[0]"
              :preview-src-list="row.images"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px;"
            />
            <div v-else class="no-image">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column label="价格" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
            <span v-if="row.originalPrice" class="original-price">
              ¥{{ row.originalPrice }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button
              link
              :type="row.status === 1 ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchProducts"
          @current-change="fetchProducts"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useStore } from 'vuex'
import {
  getAdminProductList,
  deleteProduct,
  publishProduct,
  unpublishProduct,
  batchPublish,
  batchUnpublish,
  batchDelete
} from '@/api/admin/product'

const router = useRouter()
const store = useStore()

const loading = ref(false)
const products = ref([])
const selectedIds = ref([])

const filterForm = reactive({
  name: '',
  categoryId: null,
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 分类树
const categoryTree = computed(() => store.state.product.categories || [])

// 获取商品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      name: filterForm.name || undefined,
      categoryId: filterForm.categoryId || undefined,
      status: filterForm.status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const response = await getAdminProductList(params)
    products.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchProducts()
}

// 重置
const resetFilter = () => {
  filterForm.name = ''
  filterForm.categoryId = null
  filterForm.status = null
  handleSearch()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 新增商品
const handleAdd = () => {
  router.push('/admin/products/add')
}

// 编辑商品
const handleEdit = (row) => {
  router.push(`/admin/products/edit/${row.id}`)
}

// 切换状态
const handleToggleStatus = async (row) => {
  try {
    if (row.status === 1) {
      await unpublishProduct(row.id)
      ElMessage.success('下架成功')
    } else {
      await publishProduct(row.id)
      ElMessage.success('上架成功')
    }
    fetchProducts()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除商品
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      type: 'warning'
    })
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    fetchProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量上架
const handleBatchPublish = async () => {
  try {
    await batchPublish(selectedIds.value)
    ElMessage.success('批量上架成功')
    fetchProducts()
  } catch (error) {
    console.error('批量上架失败:', error)
    ElMessage.error('批量上架失败')
  }
}

// 批量下架
const handleBatchUnpublish = async () => {
  try {
    await batchUnpublish(selectedIds.value)
    ElMessage.success('批量下架成功')
    fetchProducts()
  } catch (error) {
    console.error('批量下架失败:', error)
    ElMessage.error('批量下架失败')
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个商品吗？`, '提示', {
      type: 'warning'
    })
    await batchDelete(selectedIds.value)
    ElMessage.success('批量删除成功')
    fetchProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

onMounted(() => {
  store.dispatch('product/getCategories')
  fetchProducts()
})
</script>

<style lang="scss" scoped>
.admin-product-list {
  .filter-card {
    margin-bottom: 16px;
    padding: 16px;
    background: #fff;
    border-radius: 8px;
  }

  .filter-form {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;

    :deep(.el-form-item) {
      margin-bottom: 0;
    }
  }

  .action-bar {
    margin-bottom: 16px;
  }

  .table-card {
    padding: 16px;
    background: #fff;
    border-radius: 8px;

    .no-image {
      width: 60px;
      height: 60px;
      background: #f5f7fa;
      border-radius: 4px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #c0c4cc;
    }

    .price {
      color: #f56c6c;
      font-weight: bold;
    }

    .original-price {
      margin-left: 8px;
      color: #909399;
      text-decoration: line-through;
      font-size: 12px;
    }

    .pagination-wrapper {
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }
  }
}
</style>
