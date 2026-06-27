import request from '../../utils/request'

const BASE_URL = '/admin-demo/application'

export function getApplicationList(params) {
  return request({
    url: `${BASE_URL}/list`,
    method: 'get',
    params
  })
}

export function getApplicationAggregate() {
  return request({
    url: `${BASE_URL}/aggregate`,
    method: 'get'
  })
}

export function processApplication(data) {
  return request({
    url: `${BASE_URL}/process`,
    method: 'post',
    data
  })
}
