/**
 * 管理员文件上传 API
 */
import request from '@/utils/request'

/**
 * 上传图片
 * @param {File} file - 图片文件
 * @returns {Promise} 包含图片URL
 */
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)

  return request.post('/admin/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 30000  // 上传超时30秒
  })
}
