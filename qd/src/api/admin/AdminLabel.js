import request from '../../utils/request'

const BASE_URL = '/admin-demo/admin/Label'

/**
 * 添加标签/分类
 * @param {Object} data 标签数据 { tagName, tagType, parentTagId, remark, level }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function addLabel(data) {
  return request({
    url: `${BASE_URL}/add`,
    method: 'post',
    data
  })
}

/**
 * 删除标签/分类（会递归删除子标签）
 * @param {number} id 标签ID
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function deleteLabel(id) {
  return request({
    url: `${BASE_URL}/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除标签/分类（会递归删除子标签）
 * @param {Array<number>} ids 标签ID数组
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function deleteLabelBatch(ids) {
  return request({
    url: `${BASE_URL}/deleteBatch`,
    method: 'delete',
    data: ids
  })
}

/**
 * 更新标签/分类（动态更新）
 * @param {Object} data 标签数据 { id, tagName, tagType, parentTagId, status, remark, level }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function updateLabel(data) {
  return request({
    url: `${BASE_URL}/update`,
    method: 'put',
    data
  })
}

/**
 * 查询商品分类列表
 * @param {Object} params 查询参数 { tagName, status, createdTimeMonth }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: Array }
 */
export function getCategoryList(params) {
  return request({
    url: `${BASE_URL}/category/list`,
    method: 'get',
    params
  })
}

/**
 * 查询商品标签列表
 * @param {Object} params 查询参数 { tagName, status, createdTimeMonth, parentTagId, level }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: Array }
 */
export function getTagList(params) {
  return request({
    url: `${BASE_URL}/tag/list`,
    method: 'get',
    params
  })
}

/**
 * 懒加载查询商品标签
 * @param {Object} params 查询参数 { parentTagId, level }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: Array }
 */
export function getTagLazyList(params) {
  return request({
    url: `${BASE_URL}/tag/lazyList`,
    method: 'get',
    params
  })
}

/**
 * 查询标签统计数据
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { categoryCount, categoryEnabledCount, tagCount, level1Count, level2Count, level3Count, tagEnabledCount } }
 */
export function getLabelStats() {
  return request({
    url: `${BASE_URL}/stats`,
    method: 'get'
  })
}
