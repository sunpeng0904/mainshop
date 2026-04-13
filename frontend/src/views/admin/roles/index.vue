<template>
  <div class="admin-role-list">
    <!-- 操作栏 -->
    <div class="action-bar card">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增角色
      </el-button>
    </div>

    <!-- 角色表格 -->
    <div class="table-card card">
      <el-table v-loading="loading" :data="roles" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.roleCode !== 'ROLE_ADMIN' && row.roleCode !== 'ROLE_USER'"
              link
              type="danger"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑角色对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px">
      <el-form ref="formRef" :model="roleForm" :rules="roleRules" label-width="80px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input
            v-model="roleForm.roleCode"
            placeholder="如：ROLE_MANAGER"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="roleForm.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item v-if="isEdit" label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole } from '@/api/admin/role'

const loading = ref(false)
const roles = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const roleForm = reactive({
  id: null,
  roleCode: '',
  roleName: '',
  description: '',
  sort: 0,
  status: 1
})

const roleRules = {
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^ROLE_[A-Z_]+$/, message: '角色编码格式：ROLE_XXX（大写字母和下划线）', trigger: 'blur' }
  ],
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ]
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').slice(0, 19)
}

// 获取角色列表
const fetchRoles = async () => {
  loading.value = true
  try {
    const response = await getRoleList()
    roles.value = response.data || []
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 新增角色
const handleAdd = () => {
  isEdit.value = false
  roleForm.id = null
  roleForm.roleCode = ''
  roleForm.roleName = ''
  roleForm.description = ''
  roleForm.sort = 0
  roleForm.status = 1
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = (row) => {
  isEdit.value = true
  roleForm.id = row.id
  roleForm.roleCode = row.roleCode
  roleForm.roleName = row.roleName
  roleForm.description = row.description
  roleForm.sort = row.sort
  roleForm.status = row.status
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    const data = {
      roleCode: roleForm.roleCode,
      roleName: roleForm.roleName,
      description: roleForm.description,
      sort: roleForm.sort,
      status: roleForm.status
    }

    if (isEdit.value) {
      await updateRole(roleForm.id, data)
      ElMessage.success('更新成功')
    } else {
      await createRole(data)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    fetchRoles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

// 删除角色
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除角色 "${row.roleName}" 吗？`, '提示', {
      type: 'warning'
    })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    fetchRoles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchRoles()
})
</script>

<style lang="scss" scoped>
.admin-role-list {
  .action-bar {
    margin-bottom: 16px;
    padding: 16px;
    background: #fff;
    border-radius: 8px;
  }

  .table-card {
    padding: 16px;
    background: #fff;
    border-radius: 8px;
  }
}
</style>
