<template>
  <div class="order-container">
    <!-- 订单状态标签 -->
    <div class="order-tabs card">
      <el-radio-group v-model="currentStatus" @change="handleStatusChange">
        <el-radio-button value="">全部订单</el-radio-button>
        <el-radio-button value="0">待付款</el-radio-button>
        <el-radio-button value="1">待发货</el-radio-button>
        <el-radio-button value="2">已发货</el-radio-button>
        <el-radio-button value="3">已完成</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 订单列表 -->
    <div v-loading="loading" class="order-list">
      <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />

      <div v-for="order in orders" :key="order.id" class="order-item card">
        <!-- 订单头部 -->
        <div class="order-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <span class="order-time">{{ formatTime(order.createTime) }}</span>
          <el-tag :type="getStatusType(order.status)" size="small">
            {{ order.statusName }}
          </el-tag>
        </div>

        <!-- 订单商品 -->
        <div class="order-goods">
          <div
            v-for="item in (order.items || [])"
            :key="item.id"
            class="goods-item"
          >
            <el-image
              :src="getImageUrl(item.productImage)"
              fit="cover"
              class="goods-image"
            />
            <div class="goods-info">
              <h4>{{ item.productName }}</h4>
              <p class="goods-price">¥{{ item.productPrice || 0 }} × {{ item.quantity || 0 }}</p>
            </div>
            <span class="goods-total">¥{{ item.subtotal || 0 }}</span>
          </div>
        </div>

        <!-- 订单底部 -->
        <div class="order-footer">
          <div class="order-total">
            共 {{ getOrderItemCount(order) }} 件商品，实付
            <span class="price">¥{{ order.payAmount || 0 }}</span>
          </div>
          <div class="order-actions">
            <el-button
              v-if="Number(order.status) === 0"
              type="primary"
              size="small"
              @click="handlePay(order)"
            >
              立即付款
            </el-button>
            <el-button
              v-if="Number(order.status) === 2"
              type="success"
              size="small"
              @click="handleConfirm(order)"
            >
              确认收货
            </el-button>
            <el-button
              v-if="Number(order.status) === 0"
              size="small"
              @click="handleCancel(order)"
            >
              取消订单
            </el-button>
            <el-button
              v-if="Number(order.status) === 3 || Number(order.status) === 4"
              size="small"
              @click="handleDelete(order)"
            >
              删除订单
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="pagination.total > 0" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, prev, pager, next"
        @current-change="fetchOrders"
      />
    </div>

    <!-- 微信支付对话框 -->
    <PayDialog
      v-model="payDialogVisible"
      :order-id="currentOrder?.id"
      :amount="currentOrder?.payAmount"
      @success="handlePaySuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, cancelOrder, confirmOrder, deleteOrder, getOrderStatusMap } from '@/api/order'
import { getImageUrl } from '@/utils/image'
import PayDialog from '@/components/PayDialog.vue'

const router = useRouter()

const loading = ref(false)
const currentStatus = ref('')
const orders = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 支付对话框
const payDialogVisible = ref(false)
const currentOrder = ref(null)

// 状态类型映射
const statusMap = getOrderStatusMap()

const getStatusType = (status) => {
  return statusMap[Number(status)]?.type || 'info'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').slice(0, 19)
}

// 获取订单商品数量
const getOrderItemCount = (order) => {
  return order.items?.reduce((sum, item) => sum + item.quantity, 0) || 0
}

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const response = await getOrderList({
      status: currentStatus.value !== '' ? currentStatus.value : undefined,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })

    // 后端返回的是分页对象
    const data = response.data
    orders.value = data.records || data.list || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 状态变化
const handleStatusChange = () => {
  pagination.pageNum = 1
  fetchOrders()
}

// 立即付款 - 打开微信支付对话框
const handlePay = (order) => {
  currentOrder.value = order
  payDialogVisible.value = true
}

// 支付成功回调
const handlePaySuccess = () => {
  ElMessage.success('支付成功')
  fetchOrders()
}

// 确认收货
const handleConfirm = async (order) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '确认收货', {
      type: 'info'
    })
    await confirmOrder(order.id)
    ElMessage.success('已确认收货')
    fetchOrders()
  } catch (e) {
    // 取消
  }
}

// 取消订单
const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '取消订单', {
      type: 'warning'
    })
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (e) {
    // 取消
  }
}

// 删除订单
const handleDelete = async (order) => {
  try {
    await ElMessageBox.confirm('确定要删除该订单吗？', '删除订单', {
      type: 'warning'
    })
    await deleteOrder(order.id)
    ElMessage.success('订单已删除')
    fetchOrders()
  } catch (e) {
    // 取消
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style lang="scss" scoped>
.order-container {
  max-width: 1000px;
  margin: 0 auto;
}

.order-tabs {
  margin-bottom: 20px;
  text-align: center;
}

.order-item {
  margin-bottom: 16px;
}

.order-header {
  display: flex;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 12px;

  .order-no {
    font-weight: 500;
  }

  .order-time {
    flex: 1;
    margin-left: 20px;
    color: #909399;
    font-size: 13px;
  }
}

.order-goods {
  .goods-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f5f7fa;

    &:last-child {
      border-bottom: none;
    }

    .goods-image {
      width: 80px;
      height: 80px;
      border-radius: 4px;
    }

    .goods-info {
      flex: 1;
      margin-left: 16px;

      h4 {
        font-size: 14px;
        font-weight: normal;
        margin-bottom: 8px;
      }

      .goods-price {
        color: #909399;
        font-size: 13px;
      }
    }

    .goods-total {
      font-weight: 500;
    }
  }
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  margin-top: 12px;
  border-top: 1px solid #ebeef5;

  .order-total {
    color: #606266;

    .price {
      font-size: 18px;
      font-weight: bold;
      color: #f56c6c;
    }
  } 

  .order-actions {
    display: flex;
    gap: 8px;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
