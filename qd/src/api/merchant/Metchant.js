import request from '../../utils/request'

const BASE_URL = '/merchant-demo/merchant'

/**
 * 更新商户信息
 * @param {Object} data - 商户信息 { username, password, phone }
 * @returns {Promise}
 */
export function updateMerchant(data) {
  return request({
    url: `${BASE_URL}/update`,
    method: 'put',
    data
  })
}

/**
 * 添加商户地址
 * @param {Object} data - 地址信息 { province, city, district, detailAddress }
 * @returns {Promise}
 */
export function addAddress(data) {
  return request({
    url: `${BASE_URL}/addAddress`,
    method: 'post',
    data
  })
}

/**
 * 获取商户地址列表
 * @returns {Promise}
 */
export function getAddressList() {
  return request({
    url: `${BASE_URL}/addressList`,
    method: 'get'
  })
}

/**
 * 获取商户信息
 * @returns {Promise}
 */
export function getMerchantInfo() {
  return request({
    url: `${BASE_URL}/info`,
    method: 'get'
  })
}