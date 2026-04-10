<template>
  <div class="product-edit">
    <div class="page-header">
      <el-page-header @back="goBack" :content="isEdit ? '编辑商品' : '新增商品'" />
    </div>

    <div class="form-card card">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="120px"
        v-loading="loading"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入商品名称" maxlength="100" />
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-cascader
            v-model="formData.categoryId"
            :options="categoryTree"
            :props="{ value: 'id', label: 'name', checkStrictly: true, emitPath: false }"
            placeholder="请选择分类"
            clearable
            style="width: 100%"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="formData.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价">
              <el-input-number v-model="formData.originalPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="formData.stock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="formData.status">
                <el-radio :label="1">上架</el-radio>
                <el-radio :label="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品图片">
          <div class="image-upload">
            <div
              v-for="(url, index) in formData.imageList"
              :key="index"
              class="image-item"
            >
              <el-image :src="url" fit="cover" />
              <div class="image-actions">
                <el-icon @click="removeImage(index)"><Delete /></el-icon>
              </div>
            </div>
            <div v-if="formData.imageList.length < 5" class="image-upload-btn" @click="showImageDialog = true">
              <el-icon><Plus /></el-icon>
              <span>添加图片</span>
            </div>
          </div>
          <div class="upload-tip">最多上传5张图片，第一张为主图</div>
        </el-form-item>

        <el-form-item label="商品描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="商品详情">
          <el-input
            v-model="formData.detail"
            type="textarea"
            :rows="6"
            placeholder="请输入商品详情(支持HTML)"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ submitting ? '保存中...' : '保存' }}
          </el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 图片URL输入对话框 -->
    <el-dialog v-model="showImageDialog" title="添加图片" width="500px">
      <el-input
        v-model="imageUrl"
        placeholder="请输入图片URL地址"
        @keyup.enter="addImage"
      />
      <template #footer>
        <el-button @click="showImageDialog = false">取消</el-button>
        <el-button type="primary" @click="addImage">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useStore } from 'vuex'
import { getAdminProductDetail, createProduct, updateProduct } from '@/api/admin/product'

const route = useRoute()
const router = useRouter()
const store = useStore()

const formRef = ref(null)
const loading = ref(false)
const submitting = ref(false)
const showImageDialog = ref(false)
const imageUrl = ref('')

const isEdit = computed(() => !!route.params.id)

const formData = reactive({
  name: '',
  categoryId: null,
  price: null,
  originalPrice: null,
  stock: 0,
  status: 1,
  imageList: [],
  description: '',
  detail: ''
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const categoryTree = computed(() => store.state.product.categories || [])

// 添加图片
const addImage = () => {
  if (!imageUrl.value) {
    ElMessage.warning('请输入图片URL')
    return
  }
  if (formData.imageList.length >= 5) {
    ElMessage.warning('最多上传5张图片')
    return
  }
  formData.imageList.push(imageUrl.value)
  imageUrl.value = ''
  showImageDialog.value = false
}

// 删除图片
const removeImage = (index) => {
  formData.imageList.splice(index, 1)
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true

    const data = {
      name: formData.name,
      categoryId: formData.categoryId,
      price: formData.price,
      originalPrice: formData.originalPrice,
      stock: formData.stock,
      status: formData.status,
      images: formData.imageList.length > 0 ? JSON.stringify(formData.imageList) : null,
      description: formData.description,
      detail: formData.detail
    }

    if (isEdit.value) {
      await updateProduct(route.params.id, data)
      ElMessage.success('更新成功')
    } else {
      await createProduct(data)
      ElMessage.success('创建成功')
    }

    router.push('/admin/products')
  } catch (error) {
    if (error !== false) {
      console.error('保存失败:', error)
    }
  } finally {
    submitting.value = false
  }
}

// 返回
const goBack = () => {
  router.push('/admin/products')
}

// 加载商品详情
const loadProduct = async () => {
  if (!route.params.id) return

  loading.value = true
  try {
    const response = await getAdminProductDetail(route.params.id)
    const product = response.data

    formData.name = product.name
    formData.categoryId = product.categoryId
    formData.price = product.price
    formData.originalPrice = product.originalPrice
    formData.stock = product.stock
    formData.status = product.status ?? 1
    formData.imageList = product.images || []
    formData.description = product.description || ''
    formData.detail = product.detail || ''
  } catch (error) {
    console.error('加载商品失败:', error)
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  store.dispatch('product/getCategories')
  loadProduct()
})
</script>

<style lang="scss" scoped>
.product-edit {
  .page-header {
    margin-bottom: 20px;
  }

  .form-card {
    padding: 24px;
    background: #fff;
    border-radius: 8px;
  }

  .image-upload {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;

    .image-item {
      position: relative;
      width: 100px;
      height: 100px;
      border-radius: 8px;
      overflow: hidden;

      .el-image {
        width: 100%;
        height: 100%;
      }

      .image-actions {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        opacity: 0;
        transition: opacity 0.3s;

        .el-icon {
          font-size: 24px;
          color: #fff;
          cursor: pointer;
        }
      }

      &:hover .image-actions {
        opacity: 1;
      }
    }

    .image-upload-btn {
      width: 100px;
      height: 100px;
      border: 1px dashed #dcdfe6;
      border-radius: 8px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      color: #909399;

      &:hover {
        border-color: #409eff;
        color: #409eff;
      }

      .el-icon {
        font-size: 24px;
      }

      span {
        font-size: 12px;
        margin-top: 4px;
      }
    }
  }

  .upload-tip {
    color: #909399;
    font-size: 12px;
    margin-top: 8px;
  }
}
</style>
