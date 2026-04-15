/**
 * 抽奖活动 API
 */
import request from '@/utils/request'

/**
 * 获取奖品列表
 */
export function getLotteryPrizes() {
  return request.get('/lottery/prizes')
}

/**
 * 获取剩余抽奖次数
 */
export function getRemainingTimes() {
  return request.get('/lottery/remaining-times')
}

/**
 * 执行抽奖
 */
export function doLottery() {
  return request.post('/lottery/draw')
}

/**
 * 获取我的中奖记录
 */
export function getMyRecords() {
  return request.get('/lottery/my-records')
}

/**
 * 领取奖品
 * @param {number} recordId - 记录ID
 */
export function receivePrize(recordId) {
  return request.post(`/lottery/receive/${recordId}`)
}

// 管理端API

/**
 * 初始化奖品数据
 */
export function initLotteryPrizes() {
  return request.post('/admin/lottery/init')
}

/**
 * 获取中奖记录列表（管理端）
 */
export function getLotteryRecords(params) {
  return request.get('/admin/lottery/records', { params })
}
