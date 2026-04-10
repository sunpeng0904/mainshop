/**
 * 图片URL处理工具
 */

/**
 * 获取完整的图片URL
 * @param {string} path - 图片路径（可能是相对路径或完整URL）
 * @returns {string} 完整的图片URL
 */
export function getImageUrl(path) {
  if (!path) {
    return 'https://via.placeholder.com/400x400?text=No+Image'
  }

  // 如果已经是完整URL，直接返回
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }

  // 如果是相对路径，添加 /api 前缀（后端 context-path）
  // 确保 path 以 / 开头
  if (!path.startsWith('/')) {
    path = '/' + path
  }

  // 后端 context-path 是 /api，所以静态资源也需要加 /api 前缀
  return '/api' + path
}

/**
 * 批量处理图片URL数组
 * @param {Array|string} images - 图片数组或逗号分隔的字符串
 * @returns {Array} 完整URL的图片数组
 */
export function getImageUrls(images) {
  if (!images) {
    return []
  }

  // 如果是字符串，尝试解析JSON或按逗号分割
  if (typeof images === 'string') {
    try {
      const parsed = JSON.parse(images)
      return Array.isArray(parsed) ? parsed.map(getImageUrl) : [getImageUrl(parsed)]
    } catch (e) {
      // 不是JSON，按逗号分割
      return images.split(',').map(img => getImageUrl(img.trim()))
    }
  }

  // 如果是数组
  if (Array.isArray(images)) {
    return images.map(getImageUrl)
  }

  return []
}

export default {
  getImageUrl,
  getImageUrls
}
