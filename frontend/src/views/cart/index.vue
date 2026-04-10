<template>
  <div class="cart-container">
    <div class="cart-header card">
      <el-checkbox
        v-model="isAllSelected"
        :indeterminate="isIndeterminate"
        @change="handleSelectAll"
      >
        全选
      </el-checkbox>
      <span class="col-product">商品信息</span>
      <span class="col-price">单价</span>
      <span class="col-quantity">数量</span>
      <span class="col-total">小计</span>
      <span class="col-action">操作</span>
    </div>

    <!-- 购物车列表 -->
    <div v-loading="loading" class="cart-list">
      <div v-if="cartList.length === 0" class="empty-cart card">
        <el-empty description="购物车是空的">
          <el-button type="primary" @click="$router.push('/products')">
            去购物
          </el-button>
        </el-empty>
      </div>

      <div
        v-for="item in cartList"
        :key="item.id"
        class="cart-item card"
      >
        <el-checkbox
          :model-value="item.selected === 1"
          @change="(val) => handleSelect(item.id, val)"
        />
        <div class="col-product">
          <el-image
            :src="item.productImage"
            fit="cover"
            class="product-image"
            @click="$router.push(`/product/${item.productId}`)"
          />
          <div class="product-info">
            <h4 class="product-name" @click="$router.push(`/product/${item.productId}`)">
              {{ item.productName }}
            </h4>
            <span v-if="item.productStatus === 0" class="product-status text-danger">
              已下架
            </span>
          </div>
        </div>
        <span class="col-price">¥{{ item.productPrice }}</span>
        <div class="col-quantity">
          <el-input-number
            v-model="item.quantity"
            :min="1"
            :max="item.productStock"
            size="small"
            @change="(val) => handleQuantityChange(item.id, val)"
          />
        </div>
        <span class="col-total">¥{{ item.subtotal }}</span>
        <div class="col-action">
          <el-button text type="danger" @click="handleRemove(item.id)">
            删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- 结算栏 -->
    <div v-if="cartList.length > 0" class="cart-footer card">
      <div class="footer-left">
        <el-button text type="danger" @click="handleClearSelected">
          删除选中
        </el-button>
      </div>
      <div class="footer-right">
        <span class="selected-info">
          已选择 <strong>{{ selectedCount }}</strong> 件商品
        </span>
        <span class="total-price">
          合计：<strong>¥{{ selectedTotalPrice }}</strong>
        </span>
        <el-button
          type="primary"
          size="large"
          :disabled="selectedCount === 0"
          @click="handleCheckout"
        >
          去结算
        </el-button>
      </div>
    </div>

    <!-- 结算弹窗 -->
    <el-dialog v-model="checkoutDialogVisible" title="确认订单" width="600px">
      <div class="checkout-content">
        <!-- 收货地址选择 -->
        <div class="address-section">
          <h4>收货地址</h4>
          <div v-if="selectedAddress" class="selected-address" @click="showAddressDialog = true">
            <p><strong>{{ selectedAddress.receiverName }}</strong> {{ selectedAddress.receiverPhone }}</p>
            <p class="text-muted">{{ selectedAddress.fullAddress }}</p>
          </div>
          <el-button v-else type="primary" text @click="showAddressDialog = true">
            选择收货地址
          </el-button>
        </div>

        <!-- 商品清单 -->
        <div class="goods-section">
          <h4>商品清单</h4>
          <div v-for="item in selectedItems" :key="item.id" class="checkout-item">
            <el-image :src="item.productImage" fit="cover" class="item-image" />
            <div class="item-info">
              <span class="item-name">{{ item.productName }}</span>
              <span class="item-price">¥{{ item.productPrice }} × {{ item.quantity }}</span>
            </div>
            <span class="item-total">¥{{ item.subtotal }}</span>
          </div>
        </div>

        <!-- 支付方式 -->
        <div class="pay-type-section">
          <h4>支付方式</h4>
          <el-radio-group v-model="payType">
            <el-radio :value="1">支付宝</el-radio>
            <el-radio :value="2">微信支付</el-radio>
          </el-radio-group>
        </div>

        <!-- 备注 -->
        <div class="remark-section">
          <el-input v-model="remark" placeholder="订单备注（选填）" />
        </div>
      </div>
      <template #footer>
        <span class="total">实付：<strong>¥{{ selectedTotalPrice }}</strong></span>
        <el-button @click="checkoutDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitOrder">
          提交订单
        </el-button>
      </template>
    </el-dialog>

    <!-- 地址选择弹窗 -->
    <el-dialog v-model="showAddressDialog" title="选择收货地址" width="500px">
      <div class="address-list">
        <div
          v-for="addr in addressList"
          :key="addr.id"
          class="address-item"
          :class="{ active: selectedAddress?.id === addr.id }"
          @click="selectAddress(addr)"
        >
          <p><strong>{{ addr.receiverName }}</strong> {{ addr.receiverPhone }}</p>
          <p class="text-muted">{{ addr.fullAddress }}</p>
          <el-tag v-if="addr.isDefault === 1" type="success" size="small">默认</el-tag>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAddressList, getDefaultAddress } from '@/api/address'
import { createOrder } from '@/api/order'
import { createPayment, mockPaymentSuccess } from '@/api/payment'

const store = useStore()
const router = useRouter()

const loading = ref(false)

// 购物车列表
const cartList = computed(() => store.state.cart.cartList)

// 选中数量
const selectedCount = computed(() => store.getters['cart/selectedCount'])

// 选中总价
const selectedTotalPrice = computed(() => store.getters['cart/selectedTotalPrice'])

// 选中商品
const selectedItems = computed(() => store.getters['cart/selectedItems'])

// 是否全选
const isAllSelected = computed(() => store.getters['cart/isAllSelected'])

// 是否半选
const isIndeterminate = computed(() => {
  return !isAllSelected.value && selectedCount.value > 0
})

// 结算弹窗
const checkoutDialogVisible = ref(false)
const showAddressDialog = ref(false)
const submitting = ref(false)

// 地址相关
const addressList = ref([])
const selectedAddress = ref(null)
const payType = ref(1)
const remark = ref('')

// 获取购物车列表
const fetchCartList = async () => {
  loading.value = true
  try {
    await store.dispatch('cart/getCartList')
  } finally {
    loading.value = false
  }
}

// 获取地址列表
const fetchAddresses = async () => {
  try {
    const response = await getAddressList()
    addressList.value = response.data || []

    // 获取默认地址
    if (addressList.value.length > 0) {
      const defaultAddr = addressList.value.find(a => a.isDefault === 1)
      selectedAddress.value = defaultAddr || addressList.value[0]
    }
  } catch (error) {
    console.error('获取地址失败:', error)
  }
}

// 选择地址
const selectAddress = (addr) => {
  selectedAddress.value = addr
  showAddressDialog.value = false
}

// 选中/取消选中
const handleSelect = (cartId, selected) => {
  store.dispatch('cart/selectItem', { cartId, selected })
}

// 全选/取消全选
const handleSelectAll = (selected) => {
  store.dispatch('cart/selectAll', selected)
}

// 修改数量
const handleQuantityChange = (cartId, quantity) => {
  store.dispatch('cart/updateQuantity', { cartId, quantity })
}

// 删除商品
const handleRemove = async (cartId) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      type: 'warning'
    })
    await store.dispatch('cart/removeItem', cartId)
    ElMessage.success('删除成功')
  } catch (e) {
    // 取消删除
  }
}

// 删除选中商品
const handleClearSelected = async () => {
  const selectedIds = selectedItems.value.map(item => item.id)

  if (selectedIds.length === 0) {
    ElMessage.warning('请选择要删除的商品')
    return
  }

  try {
    await ElMessageBox.confirm('确定要删除选中的商品吗？', '提示', {
      type: 'warning'
    })
    await store.dispatch('cart/batchRemove', selectedIds)
    ElMessage.success('删除成功')
  } catch (e) {
    // 取消删除
  }
}

// 去结算
const handleCheckout = async () => {
  if (!store.getters['user/isLogin']) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (selectedCount.value === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }

  // 获取地址
  await fetchAddresses()

  if (addressList.value.length === 0) {
    ElMessage.warning('请先添加收货地址')
    router.push('/user')
    return
  }

  checkoutDialogVisible.value = true
}

// 提交订单
const submitOrder = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }

  submitting.value = true
  try {
    // 创建订单
    const orderData = {
      cartIds: selectedItems.value.map(item => item.id),
      addressId: selectedAddress.value.id,
      remark: remark.value,
      payType: payType.value,
      receiverName: selectedAddress.value.receiverName,
      receiverPhone: selectedAddress.value.receiverPhone,
      receiverAddress: selectedAddress.value.fullAddress
    }

    const orderRes = await createOrder(orderData)
    const order = orderRes.data

    ElMessage.success('订单创建成功')

    // 创建支付
    const paymentRes = await createPayment({
      orderId: order.id,
      payType: payType.value
    })
    const payment = paymentRes.data

    // 模拟支付（测试环境）
    await mockPaymentSuccess(payment.paymentNo)

    ElMessage.success('支付成功')

    // 刷新购物车
    await store.dispatch('cart/getCartList')

    checkoutDialogVisible.value = false

    // 跳转到订单列表
    router.push('/order')
  } catch (error) {
    console.error('订单创建失败:', error)
    ElMessage.error(error.message || '订单创建失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchCartList()
})
</script>

<style lang="scss" scoped>
.cart-container {
  max-width: 1200px;
  margin: 0 auto;
}

.cart-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  margin-bottom: 12px;
  font-weight: 500;
  color: #606266;
}

.col-product {
  flex: 1;
  margin-left: 20px;
  display: flex;
  align-items: center;
}

.col-price,
.col-quantity,
.col-total,
.col-action {
  width: 120px;
  text-align: center;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px;
  margin-bottom: 12px;

  .product-image {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    cursor: pointer;
  }

  .product-info {
    margin-left: 16px;

    .product-name {
      font-size: 14px;
      font-weight: normal;
      cursor: pointer;

      &:hover {
        color: #409EFF;
      }
    }

    .product-status {
      font-size: 12px;
    }
  }

  .col-price {
    color: #606266;
  }

  .col-total {
    color: #f56c6c;
    font-weight: bold;
  }
}

.empty-cart {
  padding: 60px;
  text-align: center;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  position: sticky;
  bottom: 20px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 20px;

  .selected-info {
    color: #606266;

    strong {
      color: #409EFF;
    }
  }

  .total-price {
    font-size: 16px;

    strong {
      font-size: 24px;
      color: #f56c6c;
    }
  }
}

.checkout-content {
  h4 {
    margin-bottom: 12px;
    font-size: 14px;
    color: #303133;
  }

  .address-section,
  .goods-section,
  .pay-type-section {
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid #ebeef5;
  }

  .selected-address {
    padding: 12px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    cursor: pointer;

    &:hover {
      border-color: #409EFF;
    }
  }

  .checkout-item {
    display: flex;
    align-items: center;
    padding: 8px 0;

    .item-image {
      width: 60px;
      height: 60px;
      border-radius: 4px;
    }

    .item-info {
      flex: 1;
      margin-left: 12px;

      .item-name {
        display: block;
        margin-bottom: 4px;
      }

      .item-price {
        font-size: 12px;
        color: #909399;
      }
    }

    .item-total {
      font-weight: 500;
    }
  }
}

.dialog-footer {
  .total {
    margin-right: 20px;

    strong {
      font-size: 18px;
      color: #f56c6c;
    }
  }
}

.address-list {
  .address-item {
    padding: 12px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    margin-bottom: 10px;
    cursor: pointer;

    &:hover {
      border-color: #409EFF;
    }

    &.active {
      border-color: #409EFF;
      background-color: #ecf5ff;
    }
  }
}
</style>
