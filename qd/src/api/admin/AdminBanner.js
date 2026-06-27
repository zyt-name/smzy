import request from '../../utils/request'

const BANNER_BASE_URL = '/admin-demo/admin/banner'

export function addBanner(data) {
  return request({
    url: BANNER_BASE_URL,
    method: 'post',
    data
  })
}

export function getBannerClickStats() {
  return request({
    url: `${BANNER_BASE_URL}/stats`,
    method: 'get'
  })
}

export function updateBanner(data) {
  return request({
    url: BANNER_BASE_URL,
    method: 'put',
    data
  })
}

export function getAllBanners() {
  return request({
    url: BANNER_BASE_URL,
    method: 'get'
  })
}

export function deleteBanner(bannerId) {
  return request({
    url: `${BANNER_BASE_URL}/${bannerId}`,
    method: 'delete'
  })
}
