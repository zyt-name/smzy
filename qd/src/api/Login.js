import request from '../utils/request'

/**
 * 管理员登录
 * @param {Object} data 登录参数 { username, password }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: {...} }
 */
export function adminLogin(data) {
  return request({
    url: '/admin-demo/admin/login',
    method: 'post',
    data
  })
}

/**
 * 商家登录
 * @param {Object} data 登录参数 { username, password }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: {...} }
 */
export function merchantLogin(data) {
  return request({
    url: '/merchant-demo/merchant/login',
    method: 'post',
    data
  })
}

/**
 * 用户登录
 * @param {Object} data 登录参数 { username, password }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: {...} }
 */
export function userLogin(data) {
  return request({
    url: '/user-demo/users/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 * @param {Object} data 注册参数 { username, phone, password }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function userRegister(data) {
  return request({
    url: '/user-demo/users/addUser',
    method: 'post',
    data
  })
}

/**
 * 商户注册
 * @param {Object} data 注册参数 { merchantName, username, phone, password }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function merchantRegister(data) {
  return request({
    url: '/merchant-demo/merchant/add',
    method: 'post',
    data
  })
}

/**
 * 管理员退出登录
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function adminLogout() {
  return request({
    url: '/admin-demo/admin/exit',
    method: 'post'
  })
}

/**
 * 商家退出登录
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function merchantLogout() {
  return request({
    url: '/merchant-demo/merchant/exit',
    method: 'post'
  })
}

/**
 * 用户退出登录
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function userLogout() {
  return request({
    url: '/user-demo/users/exit',
    method: 'post'
  })
}