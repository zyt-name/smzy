import request from '../../utils/request'

const BASE_URL = '/admin-demo/admin/shop'

/**
 * 分页查询商品（支持动态条件：商品名称模糊查询、商家ID、商品分类）
 * @param {Object} params 查询参数 { name, merchantId, category, pageNum, pageSize }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { total: number, data: [...] } }
 */
export function searchShops(params) {
  return request({
    url: `${BASE_URL}/search`,
    method: 'get',
    params
  })
}

/**
 * 根据商户ID查询商品
 * @param {number} merchantId 商户ID
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: [...] }
 */
export function getShopsByMerchantId(merchantId) {
  return request({
    url: `${BASE_URL}/byMerchantId/${merchantId}`,
    method: 'get'
  })
}

/**
 * 根据商品ID查询商品详情（全量数据）
 * @param {number} shopId 商品ID
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: {...} }
 */
export function getShopDetails(shopId) {
  return request({
    url: `${BASE_URL}/details/${shopId}`,
    method: 'get'
  })
}

/**
 * 管理员更新商品admin_status状态（强制下架/恢复正常）
 * @param {Object} data 请求参数 { shopId, adminStatus, reason }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function updateShopAdminStatus(data) {
  return request({
    url: `${BASE_URL}/updateAdminStatus`,
    method: 'put',
    params: data
  })
}
