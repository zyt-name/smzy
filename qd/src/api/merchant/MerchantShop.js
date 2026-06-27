import request from '../../utils/request'

const BASE_URL = '/merchant-demo/merchant/shop'

// 商家添加商品
export function addShop(data) {
  return request({
    url: `${BASE_URL}/add`,
    method: 'post',
    data
  })
}

// 商家更新商品
export function updateShop(data) {
  return request({
    url: `${BASE_URL}/update`,
    method: 'put',
    data
  })
}

// 商家更新商品状态
export function updateShopStatus(data) {
  return request({
    url: `${BASE_URL}/updateStatus`,
    method: 'put',
    data
  })
}

// 商家删除商品
export function deleteShop(shopId) {
  return request({
    url: `${BASE_URL}/delete/${shopId}`,
    method: 'delete'
  })
}

// 商家查询自己的所有商品（分页）
export function getShopByMerchantId(page = 1, size = 12) {
  return request({
    url: `${BASE_URL}/byMerchantId`,
    method: 'get',
    params: { page, size }
  })
}

// 根据id查询商品详情（全量数据）
export function getShopDetails(shopId) {
  return request({
    url: `${BASE_URL}/details/${shopId}`,
    method: 'get'
  })
}