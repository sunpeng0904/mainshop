/**
 * 抽奖管理 API
 */
import request from '@/utils/request'

/**
 * 获取奖品列表
 */
export function getAdminPrizeList() {
  return request.get('/admin/lottery/prizes')
}

/**
 * 创建奖品
 * @param {Object} data - 奖品数据
 */
export function createPrize(data) {
  return request.post('/admin/lottery/prizes', data)
}

/**
 * 更新奖品
 * @param {number} id - 奖品ID
 * @param {Object} data - 奖品数据
 */
export function updatePrize(id, data) {
  return request.put(`/admin/lottery/prizes/${id}`, data)
}

/**
 * 删除奖品
 * @param {number} id - 奖品ID
 */
export function deletePrize(id) {
  return request.delete(`/admin/lottery/prizes/${id}`)
}

/**
 * 获取中奖记录列表
 * @param {Object} params - 查询参数
 */
export function getAdminLotteryRecords(params) {
  return request.get('/admin/lottery/records', params)
}

/**
 * 获取抽奖统计数据
 */
export function getLotteryStatistics() {
  return request.get('/admin/lottery/statistics')
}

/**
 * 导出中奖记录
 * @param {Object} params - 查询参数
 */
export function exportLotteryRecords(params) {
  return request.download('/admin/lottery/records/export', params, '中奖记录.xlsx')
}

/**
 * 初始化奖品数据
 */
export function initLotteryPrizes() {
  return request.post('/admin/lottery/init')
}
