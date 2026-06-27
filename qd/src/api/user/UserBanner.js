import request from '../../utils/request'

const BANNER_BASE_URL = '/user-demo/user/banner'

export function getBannerList() {
  return request({
    url: `${BANNER_BASE_URL}/list`,
    method: 'get'
  })
}

export function incrementBannerClick(bannerId) {
  return request({
    url: `${BANNER_BASE_URL}/click/${bannerId}`,
    method: 'post'
  })
}
