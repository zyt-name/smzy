import request from '../../utils/request'

export function syncAllToEs() {
  return request({
    url: '/shop-demo/shop/sync/all',
    method: 'post'
  })
}