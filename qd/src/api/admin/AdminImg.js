import request from '../../utils/request'

const IMG_BASE_URL = '/admin-demo/admin/img'

export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: IMG_BASE_URL,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
