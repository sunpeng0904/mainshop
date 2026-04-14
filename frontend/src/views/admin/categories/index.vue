<template>
  <div class="category-container">
    <!-- 操作栏 -->
    <div class="toolbar card">
      <el-button type="primary" @click="handleAdd(0)">
        <el-icon><Plus /></el-icon>
        添加一级分类
      </el-button>
      <el-button @click="fetchCategoryTree">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <!-- 分类树形表格 -->
    <div class="category-table card">
      <el-table
        v-loading="loading"
        :data="categoryTree"
        row-key="id"
        border
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="name" label="分类名称" min-width="200" />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="level" label="层级" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.level === 1 ? 'primary' : 'success'">
              {{ row.level === 1 ? '一级' : '二级' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon" :size="20">
              <component :is="row.icon" />
            </el-icon>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.description || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.level === 1"
              type="primary"
              link
              size="small"
              @click="handleAdd(row.id)"
            >
              添加子分类
            </el-button>
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-popconfirm
              title="确定要删除该分类吗？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父级分类" prop="parentId">
          <el-cascader
            v-model="formData.parentId"
            :options="parentOptions"
            :props="{
              value: 'id',
              label: 'name',
              checkStrictly: true,
              emitPath: false
            }"
            clearable
            placeholder="请选择父级分类（不选则为一级分类）"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="formData.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import {
  getAdminCategoryTree,
  createCategory,
  updateCategory,
  deleteCategory,
  enableCategory,
  disableCategory
} from '@/api/category'

const loading = ref(false)
const submitting = ref(false)
const categoryTree = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑分类' : '新增分类'))
const formRef = ref(null)

// 表单数据
const formData = reactive({
  id: null,
  name: '',
  parentId: 0,
  level: 1,
  sort: 0,
  icon: '',
  description: '',
  status: 1
})

// 表单验证
const formRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

// 父级分类选项
const parentOptions = computed(() => {
  // 只显示一级分类作为父级选项
  return categoryTree.value.map(item => ({
    id: item.id,
    name: item.name
  }))
})

// 获取分类树
const fetchCategoryTree = async () => {
  loading.value = true
  try {
    const res = await getAdminCategoryTree()
    categoryTree.value = res.data || []
  } catch (error) {
    console.error('获取分类列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 新增分类
const handleAdd = (parentId) => {
  resetForm()
  formData.parentId = parentId || 0
  formData.level = parentId ? 2 : 1
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row) => {
  resetForm()
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    parentId: row.parentId || 0,
    level: row.level,
    sort: row.sort,
    icon: row.icon || '',
    description: row.description || '',
    status: row.status
  })
  dialogVisible.value = true
}

// 删除分类
const handleDelete = async (row) => {
  try {
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    fetchCategoryTree()
  } catch (error) {
    ElMessage.error(error.message || '删除失败')
  }
}

// 状态切换
const handleStatusChange = async (row) => {
  try {
    if (row.status === 1) {
      await enableCategory(row.id)
      ElMessage.success('已启用')
    } else {
      await disableCategory(row.id)
      ElMessage.success('已禁用')
    }
  } catch (error) {
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error(error.message || '操作失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    const data = {
      name: formData.name,
      parentId: formData.parentId || 0,
      sort: formData.sort,
      icon: formData.icon,
      description: formData.description,
      status: formData.status
    }

    if (formData.id) {
      await updateCategory(formData.id, data)
      ElMessage.success('更新成功')
    } else {
      await createCategory(data)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    fetchCategoryTree()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    id: null,
    name: '',
    parentId: 0,
    level: 1,
    sort: 0,
    icon: '',
    description: '',
    status: 1
  })
  formRef.value?.resetFields()
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return time.replace('T', ' ').slice(0, 19)
}

onMounted(() => {
  fetchCategoryTree()
})
</script>

<style lang="scss" scoped>
.category-container {
  padding: 20px;
}

.toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  padding: 16px;
}

.category-table {
  padding: 16px;
}

.text-muted {
  color: #909399;
}
</style>
