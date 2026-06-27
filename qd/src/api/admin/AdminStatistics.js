import request from '../../utils/request'

const BASE_URL = '/admin-demo/admin/statistics'

/**
 * 获取用户统计数据（新增用户曲线和用户活跃度曲线）
 * @param {number} type 查询类型：1=近7天，2=近30天
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { dates: [], newUserCounts: [], activeUserCounts: [] } }
 */
export function getUserStatistics(type) {
  return request({
    url: `${BASE_URL}/user`,
    method: 'get',
    params: {
      type
    }
  })
}

/**
 * 获取商家统计数据（新增商家曲线和商家活跃度曲线）
 * @param {number} type 查询类型：1=近7天，2=近30天
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { dates: [], newMerchantCounts: [], activeMerchantCounts: [] } }
 */
export function getMerchantStatistics(type) {
  return request({
    url: `${BASE_URL}/merchant`,
    method: 'get',
    params: {
      type
    }
  })
}

/**
 * 获取平台概览统计数据
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { totalUsers, totalMerchants, totalProducts, totalOrders, completedOrders, bannedUsers, bannedMerchants } }
 */
export function getPlatformOverview() {
  return request({
    url: `${BASE_URL}/overview`,
    method: 'get'
  })
}
