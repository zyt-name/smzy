import request from '../../utils/request'

const BASE_URL = '/order-demo/order/user'

/**
 * 购物车结算创建订单
 * @param {number} addressId 地址id
 * @param {string} remark 备注
 * @returns {Promise} 响应对象
 */
export function cartToOrder(addressId, remark = '') {
  return request({
    url: `${BASE_URL}/cartToOrder`,
    method: 'post',
    params: { addressId, remark }
  })
}

/**
 * 单个商品下单
 * @param {number} productId 商品id
 * @param {number} quantity 购买数量
 * @param {number} addressId 地址id
 * @param {string} remark 备注
 * @returns {Promise} 响应对象
 */
export function buySingleItem(productId, quantity, addressId, remark = '') {
  return request({
    url: `${BASE_URL}/buySingleItem`,
    method: 'post',
    params: { productId, quantity, addressId, remark }
  })
}

/**
 * 用户逻辑删除订单
 * @param {number} orderId 订单id
 * @returns {Promise} 响应对象
 */
export function deleteOrder(orderId) {
  return request({
    url: `${BASE_URL}/deleteOrder/${orderId}`,
    method: 'put'
  })
}

/**
 * 获取订单列表
 * @returns {Promise} 响应对象 { code: 200, data: [...] }
 */
export function listOrder() {
  return request({
    url: `${BASE_URL}/listOrder`,
    method: 'get'
  })
}

/**
 * 获取订单详情
 * @param {number} orderId 订单id
 * @returns {Promise} 响应对象 { code: 200, data: [...] }
 */
export function getOrderDetail(orderId) {
  return request({
    url: `${BASE_URL}/orderDetail/${orderId}`,
    method: 'get'
  })
}

/**
 * 根据订单号查询订单
 * @param {string} orderNo 订单号
 * @returns {Promise} 响应对象
 */
export function getOrderByOrderNo(orderNo) {
  return request({
    url: `${BASE_URL}/orderDetailByOrderNo`,
    method: 'get',
    params: { orderNo }
  })
}

/**
 * 根据订单ID查询订单信息（用于支付页面）
 * @param {number} orderId 订单id
 * @returns {Promise} 响应对象 { code: 200, data: { id, orderNo, userId, totalPrice, status, paymentMethod, cancelReason, remark, createdAt } }
 */
export function getOrderById(orderId) {
  return request({
    url: `${BASE_URL}/getOrderById/${orderId}`,
    method: 'get'
  })
}

/**
 * 支付订单
 * @param {Object} data 支付参数 { id, paymentMethod }
 * @returns {Promise} 响应对象
 */
export function payOrder(data) {
  return request({
    url: `${BASE_URL}/payOrder`,
    method: 'put',
    data
  })
}

/**
 * 取消订单
 * @param {number} orderId 订单id
 * @param {string} cancelReason 取消原因
 * @returns {Promise} 响应对象
 */
export function cancelOrder(orderId, cancelReason = '') {
  return request({
    url: `${BASE_URL}/cancelOrder/${orderId}`,
    method: 'put',
    params: { cancelReason }
  })
}

/**
 * 确认收货
 * @param {number} orderId 订单id
 * @returns {Promise} 响应对象
 */
export function confirmReceive(orderId) {
  return request({
    url: `${BASE_URL}/confirmReceive/${orderId}`,
    method: 'put'
  })
}
