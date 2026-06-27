import request from '../../utils/request'

const BASE_URL = '/user-demo/user/application'

export function applyRefund(data) {
  return request({
    url: `${BASE_URL}/refund`,
    method: 'post',
    data
  })
}

export function reportMerchant(data) {
  return request({
    url: `${BASE_URL}/report/merchant`,
    method: 'post',
    data
  })
}

export function reportProduct(data) {
  return request({
    url: `${BASE_URL}/report/product`,
    method: 'post',
    data
  })
}

export function getApplicationList(params) {
  return request({
    url: `${BASE_URL}/list`,
    method: 'get',
    params
  })
}
