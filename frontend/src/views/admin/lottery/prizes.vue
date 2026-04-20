<template>
  <div class="admin-prize-list">
    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增奖品
      </el-button>
      <el-button @click="handleInit">
        <el-icon><Refresh /></el-icon> 初始化奖品
      </el-button>
    </div>

    <!-- 奖品表格 -->
    <div class="table-card card">
      <el-table v-loading="loading" :data="prizes" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="level" label="奖项等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">{{ getLevelName(row.level) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="奖品名称" min-width="150" />
        <el-table-column label="奖品图片" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.image"
              :src="row.image"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px;"
            >
              <template #error>
                <div class="no-image">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div v-else class="no-image">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="奖品数量" width="100" />
        <el-table-column prop="remainingQuantity" label="剩余数量" width="100" />
        <el-table-column label="中奖率" width="100">
          <template #default="{ row }">
            {{ (row.probability * 100).toFixed(1) }}%
          </template>
        </el-table-column>
        <el-table-column prop="value" label="奖品价值" width="100">
          <template #default="{ row }">
            ¥{{ row.value }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑奖品' : '新增奖品'"
      width="500px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="奖项等级" prop="level">
          <el-select v-model="form.level" placeholder="请选择奖项等级">
            <el-option label="一等奖" :value="1" />
            <el-option label="二等奖" :value="2" />
            <el-option label="三等奖" :value="3" />
            <el-option label="四等奖" :value="4" />
            <el-option label="五等奖" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="奖品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入奖品名称" />
        </el-form-item>
        <el-form-item label="奖品图片" prop="image">
          <el-input v-model="form.image" placeholder="请输入奖品图片URL" />
        </el-form-item>
        <el-form-item label="奖品数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" :max="10000" />
        </el-form-item>
        <el-form-item label="中奖概率" prop="probability">
          <el-input-number
            v-model="form.probabilityPercent"
            :min="0"
            :max="100"
            :precision="2"
            :step="0.1"
          />
          <span style="margin-left: 8px;">%</span>
        </el-form-item>
        <el-form-item label="奖品价值" prop="value">
          <el-input-number v-model="form.value" :min="0" :precision="2" />
          <span style="margin-left: 8px;">元</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminPrizeList,
  createPrize,
  updatePrize,
  deletePrize,
  initLotteryPrizes
} from '@/api/admin/lottery'

const loading = ref(false)
const prizes = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  level: null,
  name: '',
  image: '',
  quantity: 1,
  probabilityPercent: 0,
  probability: 0,
  value: 0,
  status: 1
})

const rules = {
  level: [{ required: true, message: '请选择奖项等级', trigger: 'change' }],
  name: [{ required: true, message: '请输入奖品名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入奖品数量', trigger: 'blur' }],
  probabilityPercent: [{ required: true, message: '请输入中奖概率', trigger: 'blur' }]
}

// 获取等级名称
const getLevelName = (level) => {
  const names = { 1: '一等奖', 2: '二等奖', 3: '三等奖', 4: '四等奖', 5: '五等奖' }
  return names[level] || `${level}等奖`
}

// 获取等级标签类型
const getLevelType = (level) => {
  const types = { 1: 'danger', 2: 'warning', 3: 'success', 4: 'info', 5: '' }
  return types[level] || ''
}

// 获取奖品列表
const fetchPrizes = async () => {
  loading.value = true
  try {
    const response = await getAdminPrizeList()
    prizes.value = response.data || []
  } catch (error) {
    console.error('获取奖品列表失败:', error)
    ElMessage.error('获取奖品列表失败')
  } finally {
    loading.value = false
  }
}

// 新增奖品
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑奖品
const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.level = row.level
  form.name = row.name
  form.image = row.image || ''
  form.quantity = row.quantity
  form.probabilityPercent = row.probability * 100
  form.probability = row.probability
  form.value = row.value
  form.status = row.status
  dialogVisible.value = true
}

// 删除奖品
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该奖品吗？', '提示', { type: 'warning' })
    await deletePrize(row.id)
    ElMessage.success('删除成功')
    fetchPrizes()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 状态切换
const handleStatusChange = async (row) => {
  try {
    await updatePrize(row.id, { status: row.status })
    ElMessage.success(row.status === 1 ? '已启用' : '已禁用')
  } catch (error) {
    console.error('状态切换失败:', error)
    ElMessage.error('操作失败')
    row.status = row.status === 1 ? 0 : 1
  }
}

// 初始化奖品
const handleInit = async () => {
  try {
    await ElMessageBox.confirm('初始化将重置所有奖品数据，确定继续吗？', '提示', { type: 'warning' })
    await initLotteryPrizes()
    ElMessage.success('初始化成功')
    fetchPrizes()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('初始化失败:', error)
      ElMessage.error('初始化失败')
    }
  }
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.level = null
  form.name = ''
  form.image = ''
  form.quantity = 1
  form.probabilityPercent = 0
  form.probability = 0
  form.value = 0
  form.status = 1
  formRef.value?.resetFields()
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    const data = {
      level: form.level,
      name: form.name,
      image: form.image,
      quantity: form.quantity,
      probability: form.probabilityPercent / 100,
      value: form.value,
      status: form.status
    }

    if (isEdit.value) {
      await updatePrize(form.id, data)
      ElMessage.success('更新成功')
    } else {
      await createPrize(data)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    fetchPrizes()
  } catch (error) {
    if (error !== false) {
      console.error('提交失败:', error)
      ElMessage.error('操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchPrizes()
})
</script>

<style lang="scss" scoped>
.admin-prize-list {
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
  }
}
</style>
