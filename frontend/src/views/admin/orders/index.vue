<template>
  <div class="admin-order-list">
    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-value">{{ statistics.totalOrders || 0 }}</div>
        <div class="stat-label">总订单数</div>
      </div>
      <div class="stat-card warning">
        <div class="stat-value">{{ statistics.pendingPayment || 0 }}</div>
        <div class="stat-label">待付款</div>
      </div>
      <div class="stat-card primary">
        <div class="stat-value">{{ statistics.pendingShipment || 0 }}</div>
        <div class="stat-label">待发货</div>
      </div>
      <div class="stat-card info">
        <div class="stat-value">{{ statistics.shipped || 0 }}</div>
        <div class="stat-label">已发货</div>
      </div>
      <div class="stat-card success">
        <div class="stat-value">{{ statistics.completed || 0 }}</div>
        <div class="stat-label">已完成</div>
      </div>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="filter-card card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="订单编号">
          <el-input
            v-model="filterForm.orderNo"
            placeholder="请输入订单编号"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="filterForm.status" placeholder="全部状态" clearable>
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="已发货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
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

    <!-- 订单表格 -->
    <div class="table-card card">
      <el-table v-loading="loading" :data="orders" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column label="商品" min-width="200">
          <template #default="{ row }">
            <div class="goods-info">
              <template v-if="row.items && row.items.length > 0">
                <div v-for="item in row.items.slice(0, 2)" :key="item.id" class="goods-item">
                  <el-image :src="item.productImage" fit="cover" class="goods-image" />
                  <span class="goods-name">{{ item.productName }}</span>
                  <span class="goods-qty">x{{ item.quantity }}</span>
                </div>
                <div v-if="row.items.length > 2" class="more-items">
                  共{{ row.items.length }}件商品
                </div>
              </template>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.payAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="联系电话" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusName }}
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
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button
              v-if="row.status === 1"
              link
              type="success"
              @click="handleShip(row)"
            >
              发货
            </el-button>
            <el-button
              v-if="row.status === 0 || row.status === 1"
              link
              type="warning"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
            <el-button
              v-if="row.status === 3 || row.status === 4"
              link
              type="danger"
              @click="handleDelete(row)"
            >
              删除
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
          @size-change="fetchOrders"
          @current-change="fetchOrders"
        />
      </div>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <template v-if="currentOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.status)">{{ currentOrder.statusName }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="用户">{{ currentOrder.username }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ currentOrder.payTypeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">
            <span class="price">¥{{ currentOrder.payAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.receiverAddress }}</el-descriptions-item>
          <el-descriptions-item label="订单备注" :span="2">{{ currentOrder.remark || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ formatTime(currentOrder.payTime) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ formatTime(currentOrder.deliveryTime) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="收货时间">{{ formatTime(currentOrder.receiveTime) || '-' }}</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin: 20px 0 10px">商品信息</h4>
        <el-table :data="currentOrder.items" border size="small">
          <el-table-column label="商品图片" width="80">
            <template #default="{ row }">
              <el-image :src="row.productImage" fit="cover" style="width: 50px; height: 50px;" />
            </template>
          </el-table-column>
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column label="单价" width="100">
            <template #default="{ row }">¥{{ row.productPrice }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="小计" width="100">
            <template #default="{ row }">¥{{ row.subtotal }}</template>
          </el-table-column>
        </el-table>
      </template>
    </el-dialog>

    <!-- 发货对话框 -->
    <el-dialog v-model="shipVisible" title="订单发货" width="400px">
      <el-form :model="shipForm" label-width="80px">
        <el-form-item label="物流公司">
          <el-input v-model="shipForm.logisticsCompany" placeholder="请输入物流公司" />
        </el-form-item>
        <el-form-item label="物流单号">
          <el-input v-model="shipForm.logisticsNo" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminOrderList,
  getAdminOrderDetail,
  shipOrder,
  cancelOrder,
  deleteOrder,
  getOrderStatistics
} from '@/api/admin/order'

const loading = ref(false)
const orders = ref([])
const statistics = ref({})
const detailVisible = ref(false)
const shipVisible = ref(false)
const currentOrder = ref(null)

const filterForm = reactive({
  orderNo: '',
  status: null
})

const shipForm = reactive({
  orderId: null,
  logisticsCompany: '',
  logisticsNo: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 状态类型映射
const statusTypeMap = {
  0: 'warning',
  1: 'primary',
  2: 'info',
  3: 'success',
  4: 'danger'
}

const getStatusType = (status) => statusTypeMap[status] || 'info'

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').slice(0, 19)
}

// 获取订单统计
const fetchStatistics = async () => {
  try {
    const response = await getOrderStatistics()
    statistics.value = response.data || {}
  } catch (error) {
    console.error('获取统计失败:', error)
  }
}

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      orderNo: filterForm.orderNo || undefined,
      status: filterForm.status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const response = await getAdminOrderList(params)
    orders.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchOrders()
}

// 重置
const resetFilter = () => {
  filterForm.orderNo = ''
  filterForm.status = null
  handleSearch()
}

// 查看详情
const handleDetail = async (row) => {
  try {
    const response = await getAdminOrderDetail(row.id)
    currentOrder.value = response.data
    detailVisible.value = true
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  }
}

// 发货
const handleShip = (row) => {
  shipForm.orderId = row.id
  shipForm.logisticsCompany = ''
  shipForm.logisticsNo = ''
  shipVisible.value = true
}

// 确认发货
const confirmShip = async () => {
  try {
    await shipOrder(shipForm.orderId, {
      logisticsCompany: shipForm.logisticsCompany,
      logisticsNo: shipForm.logisticsNo
    })
    ElMessage.success('发货成功')
    shipVisible.value = false
    fetchOrders()
    fetchStatistics()
  } catch (error) {
    console.error('发货失败:', error)
    ElMessage.error('发货失败')
  }
}

// 取消订单
const handleCancel = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入取消原因', '取消订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入取消原因'
    })
    await cancelOrder(row.id, value)
    ElMessage.success('订单已取消')
    fetchOrders()
    fetchStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

// 删除订单
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该订单吗？', '提示', {
      type: 'warning'
    })
    await deleteOrder(row.id)
    ElMessage.success('删除成功')
    fetchOrders()
    fetchStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchStatistics()
  fetchOrders()
})
</script>

<style lang="scss" scoped>
.admin-order-list {
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

    &.warning .stat-value { color: #e6a23c; }
    &.primary .stat-value { color: #409eff; }
    &.info .stat-value { color: #909399; }
    &.success .stat-value { color: #67c23a; }
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

    .goods-info {
      .goods-item {
        display: flex;
        align-items: center;
        margin-bottom: 4px;

        &:last-child {
          margin-bottom: 0;
        }

        .goods-image {
          width: 32px;
          height: 32px;
          border-radius: 4px;
          margin-right: 8px;
        }

        .goods-name {
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          font-size: 13px;
        }

        .goods-qty {
          color: #909399;
          font-size: 12px;
          margin-left: 8px;
        }
      }

      .more-items {
        color: #909399;
        font-size: 12px;
      }
    }

    .price {
      color: #f56c6c;
      font-weight: bold;
    }

    .pagination-wrapper {
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }
  }
}
</style>
