import request from '../../utils/request'

const BASE_URL = '/merchant-demo/merchant/application'

export function applyDeleteComment(data) {
  return request({
    url: `${BASE_URL}/comment/delete`,
    method: 'post',
    params: data
  })
}

export function applyRestoreProduct(data) {
  return request({
    url: `${BASE_URL}/product/restore`,
    method: 'post',
    params: data
  })
}

export function getApplicationList(params) {
  return request({
    url: `${BASE_URL}/list`,
    method: 'get',
    params
  })
}

export function getRefundApplicationList(params) {
  return request({
    url: `${BASE_URL}/refund/list`,
    method: 'get',
    params
  })
}

export function processRefundApplication(data) {
  return request({
    url: `${BASE_URL}/refund/process`,
    method: 'post',
    data
  })
}
