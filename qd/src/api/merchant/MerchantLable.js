import request from '../../utils/request'

const BASE_URL = '/merchant-demo/merchantLabel'

/**
 * 获取商品标签名称列表
 * @param {Object} data - 查询条件 { tagType, parentTagId, level }
 * @returns {Promise}
 */
export function getTagNameList(data) {
  return request({
    url: `${BASE_URL}/getTagNameList`,
    method: 'post',
    data
  })
}
