<template>
  <div class="admin-user-list">
    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-value">{{ statistics.totalUsers || 0 }}</div>
        <div class="stat-label">用户总数</div>
      </div>
      <div class="stat-card success">
        <div class="stat-value">{{ statistics.activeUsers || 0 }}</div>
        <div class="stat-label">正常用户</div>
      </div>
      <div class="stat-card danger">
        <div class="stat-value">{{ statistics.disabledUsers || 0 }}</div>
        <div class="stat-label">禁用用户</div>
      </div>
      <div class="stat-card primary">
        <div class="stat-value">{{ statistics.todayNewUsers || 0 }}</div>
        <div class="stat-label">今日新增</div>
      </div>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="filter-card card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="用户名">
          <el-input
            v-model="filterForm.username"
            placeholder="请输入用户名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="filterForm.phone"
            placeholder="请输入手机号"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
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

    <!-- 用户表格 -->
    <div class="table-card card">
      <el-table v-loading="loading" :data="users" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar">
              {{ row.nickname?.charAt(0) || row.username?.charAt(0) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="性别" width="80">
          <template #default="{ row }">
            {{ genderMap[row.gender] || '未知' }}
          </template>
        </el-table-column>
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.roles?.includes('admin')" type="danger" size="small">管理员</el-tag>
            <el-tag v-else type="info" size="small">普通用户</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="160">
          <template #default="{ row }">
            {{ formatTime(row.lastLoginTime) || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button link type="info" @click="handleAssignRole(row)">角色</el-button>
            <el-button
              v-if="row.status === 1"
              link
              type="warning"
              @click="handleDisable(row)"
            >
              禁用
            </el-button>
            <el-button
              v-if="row.status === 0"
              link
              type="success"
              @click="handleEnable(row)"
            >
              启用
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleResetPassword(row)"
            >
              重置密码
            </el-button>
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
          @size-change="fetchUsers"
          @current-change="fetchUsers"
        />
      </div>
    </div>

    <!-- 用户详情对话框 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="500px">
      <template v-if="currentUser">
        <div class="user-detail">
          <div class="avatar-section">
            <el-avatar :size="80" :src="currentUser.avatar">
              {{ currentUser.nickname?.charAt(0) || currentUser.username?.charAt(0) }}
            </el-avatar>
            <h3>{{ currentUser.nickname || currentUser.username }}</h3>
            <el-tag v-if="currentUser.roles?.includes('admin')" type="danger">管理员</el-tag>
          </div>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ currentUser.nickname || '-' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ currentUser.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ currentUser.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ genderMap[currentUser.gender] || '未知' }}</el-descriptions-item>
            <el-descriptions-item label="生日">{{ currentUser.birthday ? formatTime(currentUser.birthday).slice(0, 10) : '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'" size="small">
                {{ currentUser.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ formatTime(currentUser.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="最后登录">{{ formatTime(currentUser.lastLoginTime) || '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog v-model="resetPasswordVisible" title="重置密码" width="400px">
      <el-form :model="resetForm" label-width="80px">
        <el-form-item label="用户">
          <span>{{ resetForm.username }}</span>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input
            v-model="resetForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input
            v-model="resetForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPasswordVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmResetPassword">确认重置</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog v-model="roleVisible" title="分配角色" width="500px">
      <div class="role-dialog-content">
        <div class="user-info">
          <span>用户：{{ roleForm.username }}</span>
        </div>
        <el-checkbox-group v-model="roleForm.selectedRoleIds" class="role-checkbox-group">
          <el-checkbox
            v-for="role in allRoles"
            :key="role.id"
            :label="role.id"
            :value="role.id"
            class="role-checkbox"
          >
            <span class="role-name">{{ role.roleName }}</span>
            <span class="role-code">({{ role.roleCode }})</span>
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="roleVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssignRole">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminUserList,
  getAdminUserDetail,
  disableUser,
  enableUser,
  resetUserPassword,
  getUserStatistics
} from '@/api/admin/user'
import { getRoleList, setUserRoles } from '@/api/admin/role'

const loading = ref(false)
const users = ref([])
const statistics = ref({})
const detailVisible = ref(false)
const resetPasswordVisible = ref(false)
const roleVisible = ref(false)
const currentUser = ref(null)
const allRoles = ref([])

const filterForm = reactive({
  username: '',
  phone: '',
  status: null
})

const resetForm = reactive({
  userId: null,
  username: '',
  newPassword: '',
  confirmPassword: ''
})

const roleForm = reactive({
  userId: null,
  username: '',
  selectedRoleIds: []
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 性别映射
const genderMap = {
  0: '未知',
  1: '男',
  2: '女'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').slice(0, 19)
}

// 获取用户统计
const fetchStatistics = async () => {
  try {
    const response = await getUserStatistics()
    statistics.value = response.data || {}
  } catch (error) {
    console.error('获取统计失败:', error)
  }
}

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {
      username: filterForm.username || undefined,
      phone: filterForm.phone || undefined,
      status: filterForm.status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const response = await getAdminUserList(params)
    users.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchUsers()
}

// 重置
const resetFilter = () => {
  filterForm.username = ''
  filterForm.phone = ''
  filterForm.status = null
  handleSearch()
}

// 查看详情
const handleDetail = async (row) => {
  try {
    const response = await getAdminUserDetail(row.id)
    currentUser.value = response.data
    detailVisible.value = true
  } catch (error) {
    console.error('获取用户详情失败:', error)
    ElMessage.error('获取用户详情失败')
  }
}

// 禁用用户
const handleDisable = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要禁用用户 "${row.username}" 吗？`, '提示', {
      type: 'warning'
    })
    await disableUser(row.id)
    ElMessage.success('用户已禁用')
    fetchUsers()
    fetchStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('禁用失败:', error)
      ElMessage.error('禁用失败')
    }
  }
}

// 启用用户
const handleEnable = async (row) => {
  try {
    await enableUser(row.id)
    ElMessage.success('用户已启用')
    fetchUsers()
    fetchStatistics()
  } catch (error) {
    console.error('启用失败:', error)
    ElMessage.error('启用失败')
  }
}

// 重置密码
const handleResetPassword = (row) => {
  resetForm.userId = row.id
  resetForm.username = row.username
  resetForm.newPassword = ''
  resetForm.confirmPassword = ''
  resetPasswordVisible.value = true
}

// 确认重置密码
const confirmResetPassword = async () => {
  if (!resetForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  if (resetForm.newPassword.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    return
  }
  if (resetForm.newPassword !== resetForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  try {
    await resetUserPassword(resetForm.userId, resetForm.newPassword)
    ElMessage.success('密码重置成功')
    resetPasswordVisible.value = false
  } catch (error) {
    console.error('重置密码失败:', error)
    ElMessage.error('重置密码失败')
  }
}

// 获取所有角色
const fetchAllRoles = async () => {
  try {
    const response = await getRoleList()
    allRoles.value = response.data || []
  } catch (error) {
    console.error('获取角色列表失败:', error)
  }
}

// 分配角色
const handleAssignRole = async (row) => {
  roleForm.userId = row.id
  roleForm.username = row.username

  // 获取用户当前角色
  try {
    const response = await getAdminUserDetail(row.id)
    const user = response.data
    // 根据角色名称匹配角色ID
    const roleIds = allRoles.value
      .filter(role => user.roles?.includes(role.roleCode.replace('ROLE_', '').toLowerCase()))
      .map(role => role.id)
    roleForm.selectedRoleIds = roleIds
  } catch (error) {
    roleForm.selectedRoleIds = []
  }

  roleVisible.value = true
}

// 确认分配角色
const confirmAssignRole = async () => {
  try {
    await setUserRoles(roleForm.userId, roleForm.selectedRoleIds)
    ElMessage.success('角色分配成功')
    roleVisible.value = false
    fetchUsers()
  } catch (error) {
    console.error('角色分配失败:', error)
    ElMessage.error('角色分配失败')
  }
}

onMounted(() => {
  fetchStatistics()
  fetchUsers()
  fetchAllRoles()
})
</script>

<style lang="scss" scoped>
.admin-user-list {
  .stats-row {
    display: flex;
    gap: 16px;
    margin-bottom: 16px;
  }

  .stat-card {
    flex: 1;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    text-align: center;

    .stat-value {
      font-size: 28px;
      font-weight: bold;
      color: #303133;
    }

    .stat-label {
      margin-top: 8px;
      color: #909399;
      font-size: 14px;
    }

    &.success .stat-value { color: #67c23a; }
    &.danger .stat-value { color: #f56c6c; }
    &.primary .stat-value { color: #409eff; }
  }

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

  .table-card {
    padding: 16px;
    background: #fff;
    border-radius: 8px;

    .pagination-wrapper {
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }
  }

  .user-detail {
    .avatar-section {
      text-align: center;
      margin-bottom: 20px;

      h3 {
        margin: 12px 0 8px;
        font-size: 18px;
      }
    }
  }

  .role-dialog-content {
    .user-info {
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid #ebeef5;
      font-weight: 500;
    }

    .role-checkbox-group {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .role-checkbox {
        margin-right: 0;
        height: auto;

        .role-name {
          font-weight: 500;
        }

        .role-code {
          margin-left: 8px;
          color: #909399;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
