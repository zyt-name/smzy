import request from '../../utils/request'

// 图片上传
export function uploadImage(formData) {
  return request({
    url: '/merchant-demo/merchant/img',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}