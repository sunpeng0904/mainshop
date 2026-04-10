<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <div class="stat-card card">
          <div class="stat-icon" style="background: #409EFF">
            <el-icon :size="32"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.userCount }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card card">
          <div class="stat-icon" style="background: #67C23A">
            <el-icon :size="32"><Goods /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.productCount }}</div>
            <div class="stat-label">商品总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card card">
          <div class="stat-icon" style="background: #E6A23C">
            <el-icon :size="32"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.orderCount }}</div>
            <div class="stat-label">订单总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card card">
          <div class="stat-icon" style="background: #F56C6C">
            <el-icon :size="32"><Money /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ stats.totalSales }}</div>
            <div class="stat-label">销售总额</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <div class="quick-actions card">
      <h3>快捷操作</h3>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-button class="action-btn" @click="$router.push('/admin/products')">
            <el-icon><Goods /></el-icon>
            商品管理
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button class="action-btn" @click="$router.push('/admin/orders')">
            <el-icon><Document /></el-icon>
            订单管理
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button class="action-btn" @click="$router.push('/admin/users')">
            <el-icon><User /></el-icon>
            用户管理
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button class="action-btn" @click="$router.push('/admin/categories')">
            <el-icon><Menu /></el-icon>
            分类管理
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 最近订单 -->
    <div class="recent-orders card">
      <h3>最近订单</h3>
      <el-table :data="recentOrders" stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column label="订单金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间">
          <template #default="{ row }">
            {{ row.createTime ? row.createTime.replace('T', ' ').slice(0, 19) : '' }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOrderStatusMap } from '@/api/order'
import { getAdminOrderList, getOrderStatistics } from '@/api/admin/order'
import { getUserStatistics } from '@/api/admin/user'
import { getAdminProductList } from '@/api/admin/product'

// 统计数据
const stats = reactive({
  userCount: 0,
  productCount: 0,
  orderCount: 0,
  totalSales: '0.00'
})

// 最近订单
const recentOrders = ref([])

// 状态映射
const statusMap = getOrderStatusMap()

const getStatusType = (status) => {
  return statusMap[status]?.type || 'info'
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    // 获取用户统计
    const userRes = await getUserStatistics()
    stats.userCount = userRes.data?.totalUsers || 0

    // 获取商品统计
    const productRes = await getAdminProductList({ pageNum: 1, pageSize: 1 })
    stats.productCount = productRes.data?.total || 0

    // 获取订单统计
    const orderRes = await getOrderStatistics()
    stats.orderCount = orderRes.data?.totalOrders || 0
  } catch (error) {
    console.error('获取统计失败:', error)
  }
}

// 获取最近订单
const fetchRecentOrders = async () => {
  try {
    const response = await getAdminOrderList({ pageNum: 1, pageSize: 5 })
    recentOrders.value = response.data.records || response.data.list || []
  } catch (error) {
    console.error('获取订单失败:', error)
  }
}

onMounted(() => {
  fetchStatistics()
  fetchRecentOrders()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  .stat-row {
    margin-bottom: 20px;
  }
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 24px;

  .stat-icon {
    width: 64px;
    height: 64px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
  }

  .stat-info {
    margin-left: 16px;

    .stat-value {
      font-size: 28px;
      font-weight: bold;
      color: #303133;
    }

    .stat-label {
      font-size: 14px;
      color: #909399;
      margin-top: 4px;
    }
  }
}

.quick-actions {
  margin-bottom: 20px;

  h3 {
    margin-bottom: 20px;
  }

  .action-btn {
    width: 100%;
    height: 80px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
    font-size: 16px;
  }
}

.recent-orders {
  h3 {
    margin-bottom: 16px;
  }
}
</style>
