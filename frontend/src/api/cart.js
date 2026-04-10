/**
 * 购物车模块 API
 * 对接后端 /cart 接口
 */
import request from '@/utils/request'

/**
 * 获取购物车列表
 */
export function getCartList() {
  return request.get('/cart/list')
}

/**
 * 添加商品到购物车
 * @param {Object} data - { productId, quantity }
 */
export function addToCart(data) {
  return request.post('/cart/add', null, {
    params: {
      productId: data.productId,
      quantity: data.quantity || 1
    }
  })
}

/**
 * 更新购物车商品数量
 * @param {Object} data - { cartId, quantity }
 */
export function updateCartQuantity(data) {
  return request.put('/cart/update', null, {
    params: {
      cartId: data.cartId,
      quantity: data.quantity
    }
  })
}

/**
 * 删除购物车商品
 * @param {number} cartId - 购物车项ID
 */
export function removeCartItem(cartId) {
  return request.delete(`/cart/${cartId}`)
}

/**
 * 批量删除购物车商品
 * @param {Array} cartIds - 购物车项ID数组
 */
export function batchRemoveCartItems(cartIds) {
  return request.delete('/cart/batch', cartIds)
}

/**
 * 清空购物车
 */
export function clearCart() {
  return request.delete('/cart/clear')
}

/**
 * 获取购物车商品数量
 */
export function getCartCount() {
  return request.get('/cart/count')
}

/**
 * 选中/取消选中购物车商品
 * @param {Object} data - { cartId, selected }
 */
export function selectCartItem(data) {
  return request.put('/cart/select', null, {
    params: {
      cartId: data.cartId,
      selected: data.selected
    }
  })
}

/**
 * 全选/取消全选
 * @param {boolean} selected - 是否全选
 */
export function selectAllCart(selected) {
  return request.put('/cart/select-all', null, {
    params: { selected }
  })
}

/**
 * 获取选中的购物车商品
 */
export function getSelectedCartItems() {
  return request.get('/cart/selected')
}

/**
 * 计算购物车总金额
 */
export function calculateCartTotal() {
  return request.get('/cart/total')
}
