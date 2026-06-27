import request from '../../utils/request'

const BASE_URL = '/order-demo/order/statistics'

/**
 * 获取商家订单统计曲线图数据（近7天或近30天）
 * @param {number} viewType - 查看类型（1-近7天，2-近30天）
 * @returns {Promise} 返回订单统计数据
 *   - days: 统计天数
 *   - merchantId: 商家ID
 *   - dailyStatistics: 每日统计数据列表
 *     - date: 日期
 *     - orderCount: 订单数量
 *     - validOrderCount: 有效订单数量
 *   - totalOrderCount: 订单总数
 *   - validOrderCount: 有效订单总数
 */
export const getOrderStatisticsCurve = (viewType = 1) => {
  return request({
    url: `${BASE_URL}/curve`,
    method: 'get',
    params: {
      viewType
    }
  })
}

/**
 * 获取商家交易构成统计数据（按商品分类统计）
 * @returns {Promise} 返回交易构成统计数据
 *   - merchantId: 商家ID
 *   - structureMap: 交易构成数据（key为商品分类名称，value为对应分类的商品数量）
 */
export const getTransactionStructure = () => {
  return request({
    url: `${BASE_URL}/structure`,
    method: 'get'
  })
}
