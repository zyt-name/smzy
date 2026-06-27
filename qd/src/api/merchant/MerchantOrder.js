import request from '../../utils/request'

const BASE_URL = '/order-demo/order/merchant'

/**
 * 商户查看自己的订单列表（合并接口）
 * 支持：查全部、按状态筛选、按订单号查询
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页大小
 * @param {number} params.status - 订单状态（可选）
 * @param {string} params.orderNo - 订单号（可选）
 * @returns {Promise}
 */
export const getMerchantOrders = (params = {}) => {
  const { page = 1, pageSize = 10, status, orderNo } = params
  return request({
    url: `${BASE_URL}/list`,
    method: 'get',
    params: {
      page,
      pageSize,
      status,
      orderNo
    }
  })
}

/**
 * 根据订单ID查询订单详情
 * @param {number} orderId - 订单ID
 * @returns {Promise}
 */
export const getOrderDetailById = (orderId) => {
  return request({
    url: `${BASE_URL}/byId/${orderId}`,
    method: 'get'
  })
}

/**
 * 取消订单
 * @param {number} orderId - 订单ID
 * @param {string} cancelReason - 取消原因
 * @returns {Promise}
 */
export const cancelOrder = (orderId, cancelReason) => {
  return request({
    url: `${BASE_URL}/cancel/${orderId}`,
    method: 'put',
    params: {
      cancelReason
    }
  })
}

/**
 * 商家发货
 * @param {number} orderId - 订单ID
 * @returns {Promise}
 */
export const confirmShip = (orderId) => {
  return request({
    url: `${BASE_URL}/confirm/${orderId}`,
    method: 'put'
  })
}

/**
 * 商家逻辑删除订单
 * @param {number} orderId - 订单ID
 * @returns {Promise}
 */
export const deleteOrder = (orderId) => {
  return request({
    url: `${BASE_URL}/delete/${orderId}`,
    method: 'put'
  })
}

/**
 * 订单退款
 * @param {number} orderId - 订单ID
 * @returns {Promise}
 */
export const refundOrder = (orderId) => {
  return request({
    url: `/order-demo/order/admin/refund/${orderId}`,
    method: 'post'
  })
}
