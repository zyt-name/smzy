import request from '../../utils/request'

const BASE_URL = '/user-demo/user/comment'

/**
 * 发表主评论
 * @param {Object} data 评论参数 { productId, content }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function addComment(data) {
  return request({
    url: `${BASE_URL}/add`,
    method: 'post',
    data
  })
}

/**
 * 分页查询商品评论
 * @param {string} productId 商品ID
 * @param {number} page 主评论页码（默认1）
 * @param {number} size 主评论每页条数（默认10）
 * @param {number} replyPage 子评论页码（默认1）
 * @param {number} replySize 子评论每页条数（默认10）
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { total: number, list: [...] } }
 */
export function getComments(productId, page = 1, size = 10, replyPage = 1, replySize = 10) {
  return request({
    url: `${BASE_URL}/list`,
    method: 'get',
    params: {
      productId,
      page,
      size,
      replyPage,
      replySize
    }
  })
}

/**
 * 发表子评论（回复）
 * @param {Object} data 回复参数 { commentId, content, toUserId, toUsername }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function addReply(data) {
  return request({
    url: `${BASE_URL}/reply/add`,
    method: 'post',
    data
  })
}

/**
 * 删除主评论
 * @param {string} commentId 主评论ID
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function deleteComment(commentId) {
  return request({
    url: `${BASE_URL}/delete`,
    method: 'delete',
    params: { commentId }
  })
}

/**
 * 删除子评论
 * @param {string} commentId 主评论ID
 * @param {string} replyId 子评论ID
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function deleteReply(commentId, replyId) {
  return request({
    url: `${BASE_URL}/reply/delete`,
    method: 'delete',
    params: { commentId, replyId }
  })
}

/**
 * 点赞主评论
 * @param {string} commentId 主评论ID
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function likeComment(commentId) {
  return request({
    url: `${BASE_URL}/like`,
    method: 'post',
    params: { commentId }
  })
}

/**
 * 点赞子评论
 * @param {string} commentId 主评论ID
 * @param {string} replyId 子评论ID
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function likeReply(commentId, replyId) {
  return request({
    url: `${BASE_URL}/reply/like`,
    method: 'post',
    params: { commentId, replyId }
  })
}
