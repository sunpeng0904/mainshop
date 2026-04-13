/**
 * 图片URL处理工具
 */

// 默认占位图
const DEFAULT_IMAGE = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI0MDAiIGhlaWdodD0iNDAwIiB2aWV3Qm94PSIwIDAgNDAwIDQwMCI+PHJlY3Qgd2lkdGg9IjQwMCIgaGVpZ2h0PSI0MDAiIGZpbGw9IiNmNWY3ZmEiLz48dGV4dCB4PSIxNTAiIHk9IjIwMCIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjI0IiBmaWxsPSIjOTB5OTl6Ij7mlrDmiqXoioLngrk8L3RleHQ+PC9zdmc+'

/**
 * 获取完整的图片URL
 * @param {string} path - 图片路径（可能是相对路径或完整URL）
 * @returns {string} 完整的图片URL
 */
export function getImageUrl(path) {
  if (!path) {
    return DEFAULT_IMAGE
  }

  // 如果已经是完整URL，直接返回
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }

  // 如果是 base64 图片，直接返回
  if (path.startsWith('data:')) {
    return path
  }

  // 确保 path 以 / 开头
  if (!path.startsWith('/')) {
    path = '/' + path
  }

  // 图片路径统一通过后端API访问（后端context-path为/api）
  // 开发环境通过vue.config.js代理，生产环境直接访问
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

/**
 * 获取默认占位图
 */
export function getDefaultImage() {
  return DEFAULT_IMAGE
}

export default {
  getImageUrl,
  getImageUrls,
  getDefaultImage
}
