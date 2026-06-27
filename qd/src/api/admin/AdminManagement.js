import request from '../../utils/request'

const BASE_URL = '/admin-demo/admin'

/**
 * 管理员获取用户列表（分页）
 * @param {number} pageNum 页码
 * @param {number} pageSize 每页数量
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { count: number, data: [...] } }
 */
export function showUsers(pageNum, pageSize) {
  return request({
    url: `${BASE_URL}/showUser`,
    method: 'get',
    params: {
      pageNum,
      pageSize
    }
  })
}

/**
 * 管理员获取商家列表（分页）
 * @param {number} pageNum 页码
 * @param {number} pageSize 每页数量
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { count: number, data: [...] } }
 */
export function showMerchants(pageNum, pageSize) {
  return request({
    url: `${BASE_URL}/showMerchant`,
    method: 'get',
    params: {
      pageNum,
      pageSize
    }
  })
}

/**
 * 管理员封禁/解封用户
 * @param {Object} data 封禁操作数据 { targetId, reason, operateType }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function banOrUnbanUser(data) {
  return request({
    url: `${BASE_URL}/banUser`,
    method: 'post',
    data
  })
}

/**
 * 管理员封禁/解封商家
 * @param {Object} data 封禁操作数据 { targetId, reason, operateType }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function banOrUnbanMerchant(data) {
  return request({
    url: `${BASE_URL}/banMerchant`,
    method: 'post',
    data
  })
}

/**
 * 管理员更新个人信息
 * @param {Object} data 动态更新数据（username/password）
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function updateAdminInfo(data) {
  return request({
    url: `${BASE_URL}/update`,
    method: 'put',
    data
  })
}
