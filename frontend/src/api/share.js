/**
 * 分享功能相关接口
 */
import request from '@/utils/request'

// ==================== 分享接口 ====================

/**
 * 发布分享
 * @param {Object} data - 分享数据
 * @param {string} data.content - 文本内容
 * @param {string[]} data.imageUrls - 图片URL列表
 * @param {string} data.location - 所在位置
 * @param {number} data.visibility - 可见范围: 1公开 2好友 3部分可见 4不给谁看 5仅自己
 * @param {number[]} data.mentionUserIds - @的用户ID列表
 */
export const createShare = (data) => {
  return request.post('/share', data)
}

/**
 * 获取分享详情
 * @param {number} shareId - 分享ID
 */
export const getShareDetail = (shareId) => {
  return request.get(`/share/${shareId}`)
}

/**
 * 获取我的分享列表
 * @param {Object} params - 分页参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 */
export const getMyShares = (params = {}) => {
  return request.get('/share/my', params)
}

/**
 * 获取好友圈动态
 * @param {Object} params - 分页参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 */
export const getFriendShares = (params = {}) => {
  return request.get('/share/friends', params)
}

/**
 * 获取热门分享
 * @param {Object} params - 分页参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 */
export const getHotShares = (params = {}) => {
  return request.get('/share/hot', params)
}

/**
 * 获取用户分享列表（圈子主页）
 * @param {number} userId - 用户ID
 * @param {Object} params - 分页参数
 */
export const getUserShares = (userId, params = {}) => {
  return request.get(`/share/user/${userId}`, params)
}

/**
 * 删除分享
 * @param {number} shareId - 分享ID
 */
export const deleteShare = (shareId) => {
  return request.delete(`/share/${shareId}`)
}

// ==================== 点赞接口 ====================

/**
 * 点赞/取消点赞
 * @param {number} shareId - 分享ID
 */
export const toggleLike = (shareId) => {
  return request.post(`/share/${shareId}/like`)
}

/**
 * 获取点赞列表
 * @param {number} shareId - 分享ID
 * @param {Object} params - 分页参数
 */
export const getLikeUsers = (shareId, params = {}) => {
  return request.get(`/share/${shareId}/likes`, params)
}

// ==================== 评论接口 ====================

/**
 * 发表评论
 * @param {number} shareId - 分享ID
 * @param {Object} data - 评论数据
 * @param {string} data.content - 评论内容
 * @param {number} data.parentId - 父评论ID，0表示一级评论
 * @param {number} data.replyUserId - 回复用户ID
 */
export const createComment = (shareId, data) => {
  return request.post(`/share/${shareId}/comment`, data)
}

/**
 * 获取评论列表
 * @param {number} shareId - 分享ID
 * @param {Object} params - 分页参数
 */
export const getComments = (shareId, params = {}) => {
  return request.get(`/share/${shareId}/comments`, params)
}

/**
 * 删除评论
 * @param {number} shareId - 分享ID
 * @param {number} commentId - 评论ID
 */
export const deleteComment = (shareId, commentId) => {
  return request.delete(`/share/${shareId}/comment/${commentId}`)
}

// ==================== 收藏接口 ====================

/**
 * 收藏/取消收藏
 * @param {number} shareId - 分享ID
 */
export const toggleCollect = (shareId) => {
  return request.post(`/share/${shareId}/collect`)
}

/**
 * 获取我的收藏列表
 * @param {Object} params - 分页参数
 */
export const getMyCollects = (params = {}) => {
  return request.get('/share/collects', params)
}

// ==================== 转发接口 ====================

/**
 * 转发分享
 * @param {number} shareId - 分享ID
 * @param {Object} data - 转发数据
 * @param {string} data.content - 转发评论
 * @param {boolean} data.alsoLike - 是否同时点赞
 */
export const forwardShare = (shareId, data) => {
  return request.post(`/share/${shareId}/forward`, data)
}

// ==================== 举报接口 ====================

/**
 * 举报分享
 * @param {number} shareId - 分享ID
 * @param {Object} data - 举报数据
 * @param {string} data.reason - 举报原因
 * @param {string} data.description - 举报描述
 */
export const reportShare = (shareId, data) => {
  return request.post(`/share/${shareId}/report`, data)
}

// ==================== 好友接口 ====================

/**
 * 关注用户
 * @param {number} userId - 用户ID
 */
export const followUser = (userId) => {
  return request.post(`/friend/follow/${userId}`)
}

/**
 * 取消关注
 * @param {number} userId - 用户ID
 */
export const unfollowUser = (userId) => {
  return request.delete(`/friend/follow/${userId}`)
}

/**
 * 获取关注列表
 * @param {Object} params - 分页参数
 */
export const getFollowing = (params = {}) => {
  return request.get('/friend/following', params)
}

/**
 * 获取粉丝列表
 * @param {Object} params - 分页参数
 */
export const getFollowers = (params = {}) => {
  return request.get('/friend/followers', params)
}

/**
 * 获取好友列表（互相关注）
 * @param {Object} params - 分页参数
 */
export const getFriends = (params = {}) => {
  return request.get('/friend/friends', params)
}

// ==================== 通知接口 ====================

/**
 * 获取通知列表
 * @param {Object} params - 查询参数
 * @param {string} params.type - 通知类型: LIKE, COMMENT, FORWARD, FOLLOW, MENTION
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 */
export const getNotifications = (params = {}) => {
  return request.get('/notification/list', params)
}

/**
 * 获取未读通知数量
 */
export const getUnreadCount = () => {
  return request.get('/notification/unread-count')
}

/**
 * 标记通知已读
 * @param {number} notificationId - 通知ID
 */
export const markAsRead = (notificationId) => {
  return request.put(`/notification/${notificationId}/read`)
}

/**
 * 标记所有通知已读
 * @param {string} type - 通知类型（可选）
 */
export const markAllAsRead = (type) => {
  return request.put('/notification/read-all', null, { params: { type } })
}

// ==================== 图片上传接口 ====================

/**
 * 上传分享图片（复用通用上传接口）
 * @param {File} file - 图片文件
 */
export const uploadShareImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)

  return request.post('/admin/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 30000
  })
}
