import request from '../utils/request'

const BASE_URL = '/admin-demo/admin'

// ============================================
// 注意：本文件已弃用，所有接口已迁移到 AdminManagement.js
// 请使用：import { xxx } from './admin/AdminManagement'
// ============================================

/**
 * [已弃用] 管理员管理用户状态（封禁用户或解封用户）
 * 请使用 AdminManagement.js 中的 banOrUnbanUser 接口
 * @param {number|string} userId 用户ID
 * @param {number} status 状态（0是开启，1是封禁）
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
// export function updateUserStatus(userId, status) {
//   return request({
//     url: `${BASE_URL}/updateUserStatus`,
//     method: 'put',
//     params: {
//       userId,
//       status
//     }
//   })
// }

/**
 * [已弃用] 管理员获取用户列表（分页）
 * 请使用 AdminManagement.js 中的 showUsers 接口
 * @param {number} pageNum 页码
 * @param {number} pageSize 每页数量
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { count: number, data: [...] } }
 */
// export function showUsers(pageNum, pageSize) {
//   return request({
//     url: `${BASE_URL}/showUser`,
//     method: 'get',
//     params: {
//       pageNum,
//       pageSize
//     }
//   })
// }

/**
 * [已弃用] 管理员获取商家列表（分页）
 * 请使用 AdminManagement.js 中的 showMerchants 接口
 * @param {number} pageNum 页码
 * @param {number} pageSize 每页数量
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { count: number, data: [...] } }
 */
// export function showMerchants(pageNum, pageSize) {
//   return request({
//     url: `${BASE_URL}/showMerchant`,
//     method: 'get',
//     params: {
//       pageNum,
//       pageSize
//     }
//   })
// }

/**
 * [已弃用] 管理员更新商家状态
 * 请使用 AdminManagement.js 中的 banOrUnbanMerchant 接口
 * @param {number|string} merchantId 商家ID
 * @param {number} status 状态
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
// export function updateMerchantStatus(merchantId, status) {
//   return request({
//     url: `${BASE_URL}/updateAdminStatus`,
//     method: 'put',
//     params: {
//       merchantId,
//       status
//     }
//   })
// }

/**
 * [已弃用] 管理员更新个人信息
 * 请使用 AdminManagement.js 中的 updateAdminInfo 接口
 * @param {Object} data 动态更新数据（username/password）
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
// export function updateAdminInfo(data) {
//   return request({
//     url: `${BASE_URL}/update`,
//     method: 'put',
//     data
//   })
// }
