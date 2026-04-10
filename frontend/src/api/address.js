/**
 * 收货地址模块 API
 * 对接后端 /address 接口
 */
import request from '@/utils/request'

/**
 * 获取地址列表
 */
export function getAddressList() {
  return request.get('/address/list')
}

/**
 * 获取地址详情
 * @param {number} addressId - 地址ID
 */
export function getAddressDetail(addressId) {
  return request.get(`/address/${addressId}`)
}

/**
 * 添加地址
 * @param {Object} data - 地址数据
 * {
 *   receiverName: string,    // 收货人姓名
 *   receiverPhone: string,   // 收货人电话
 *   province: string,        // 省
 *   city: string,            // 市
 *   district: string,        // 区
 *   detailAddress: string,   // 详细地址
 *   isDefault: number        // 是否默认 0-否 1-是
 * }
 */
export function addAddress(data) {
  return request.post('/address/add', data)
}

/**
 * 更新地址
 * @param {Object} data - 地址数据（需包含id）
 */
export function updateAddress(data) {
  return request.put('/address/update', data)
}

/**
 * 删除地址
 * @param {number} addressId - 地址ID
 */
export function deleteAddress(addressId) {
  return request.delete(`/address/${addressId}`)
}

/**
 * 设为默认地址
 * @param {number} addressId - 地址ID
 */
export function setDefaultAddress(addressId) {
  return request.put(`/address/default/${addressId}`)
}

/**
 * 获取默认地址
 */
export function getDefaultAddress() {
  return request.get('/address/default')
}
