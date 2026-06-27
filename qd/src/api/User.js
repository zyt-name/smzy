import request from '../utils/request'

const SHOP_BASE_URL = '/user-demo/user/shop'
const CART_BASE_URL = '/user-demo/user/cart'
const USERS_BASE_URL = '/user-demo/users'
const ADDRESS_BASE_URL = '/user-demo/user/address'
const MERCHANT_BASE_URL = '/user-demo/user/merchant'
const COMMODITY_BASE_URL = '/shop-demo/shop'

const PAGE_SIZE = 100

/**
 * 获取商品列表（分页查询）
 * @param {Object} data 搜索条件 { name, minPrice, maxPrice, category, pageNum }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { count: number, data: [...] } }
 */
export function getShopList(data) {
  const params = {
    ...data,
    pageNum: data.pageNum || 1
  }
  return request({
    url: `${SHOP_BASE_URL}/list`,
    method: 'post',
    data: params
  })
}

/**
 * 获取商品详情
 * @param {number|string} shopId 商品ID
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: {...} }
 */
export function getShopDetails(shopId) {
  return request({
    url: `${SHOP_BASE_URL}/details/${shopId}`,
    method: 'get'
  })
}

/**
 * 获取推荐商品（基于用户行为日志的个性化推荐）
 * @param {number} pageNum 页码
 * @param {number} pageSize 每页数量
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { total: number, data: [...] } }
 */
export function getRecommendProducts(pageNum = 1, pageSize = 20) {
  return request({
    url: `${SHOP_BASE_URL}/recommend`,
    method: 'get',
    params: { pageNum, pageSize }
  })
}

/**
 * 获取商品分类列表
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: [{ id, tagName }] }
 */
export function getCategoryList() {
  return request({
    url: `${SHOP_BASE_URL}/categoryList`,
    method: 'get'
  })
}

/**
 * 查看购物车
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: [...] }
 */
export function showCart() {
  return request({
    url: `${CART_BASE_URL}/showCart`,
    method: 'get'
  })
}

/**
 * 添加商品到购物车
 * @param {Object} data 购物车参数 { productId, productCount, userSpecification }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function addCart(data) {
  return request({
    url: `${CART_BASE_URL}/addCart`,
    method: 'post',
    data
  })
}

/**
 * 更新购物车中商品数量
 * @param {Object} data 更新参数 { productId, productCount, userSpecification }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function updateCartCount(data) {
  return request({
    url: `${CART_BASE_URL}/updateCartCount`,
    method: 'put',
    data
  })
}

/**
 * 清空购物车
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function deleteCart() {
  return request({
    url: `${CART_BASE_URL}/deleteCart`,
    method: 'delete'
  })
}

/**
 * 删除购物车中单个商品
 * @param {Object} data 删除参数 { productId, userSpecification }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function deleteCartItem(data) {
  return request({
    url: `${CART_BASE_URL}/deleteCartItem`,
    method: 'delete',
    data
  })
}

/**
 * 更新用户信息
 * @param {Object} data 用户信息 { userName, nickname, phone, email, ... }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function updateUser(data) {
  return request({
    url: `${USERS_BASE_URL}/updateUser`,
    method: 'put',
    data
  })
}

/**
 * 添加收货地址
 * @param {Object} data 地址信息 { receiverName, receiverPhone, province, city, district, detailAddress }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function addAddress(data) {
  return request({
    url: `${ADDRESS_BASE_URL}/add`,
    method: 'post',
    data
  })
}

/**
 * 设置默认收货地址
 * @param {number|string} addressId 地址ID
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function updateIsDefault(addressId) {
  return request({
    url: `${ADDRESS_BASE_URL}/default/${addressId}`,
    method: 'put'
  })
}

/**
 * 获取收货地址列表
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: [...] }
 */
export function getAddressList() {
  return request({
    url: `${ADDRESS_BASE_URL}/list`,
    method: 'get'
  })
}

/**
 * 更新收货地址
 * @param {Object} data 地址信息 { id, receiverName, receiverPhone, province, city, district, detailAddress }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function updateAddress(data) {
  return request({
    url: `${ADDRESS_BASE_URL}/update`,
    method: 'put',
    data
  })
}

/**
 * 删除收货地址
 * @param {number|string} addressId 地址ID
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function deleteAddress(addressId) {
  return request({
    url: `${ADDRESS_BASE_URL}/delete/${addressId}`,
    method: 'delete'
  })
}

/**
 * 收藏商家
 * @param {number|string} merchantId 商家ID
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function addFavoriteMerchant(merchantId) {
  return request({
    url: `${MERCHANT_BASE_URL}/favorite/${merchantId}`,
    method: 'post'
  })
}

/**
 * 取消收藏商家
 * @param {number|string} merchantId 商家ID
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: null }
 */
export function deleteFavoriteMerchant(merchantId) {
  return request({
    url: `${MERCHANT_BASE_URL}/favorite/${merchantId}`,
    method: 'delete'
  })
}

/**
 * 获取收藏商家列表（分页）
 * @param {number} pageNum 页码
 * @param {number} pageSize 每页数量
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { count: number, data: [...] } }
 */
export function getFavoriteMerchantList(pageNum, pageSize) {
  return request({
    url: `${MERCHANT_BASE_URL}/favorite/list`,
    method: 'get',
    params: { pageNum, pageSize }
  })
}

/**
 * 检查是否已收藏指定商家
 * @param {number|string} merchantId 商家ID
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: boolean }
 */
export function checkFavorite(merchantId) {
  return request({
    url: `${MERCHANT_BASE_URL}/favorite/${merchantId}/check`,
    method: 'get'
  })
}

/**
 * 获取商家信息
 * @param {number|string} merchantId 商家ID
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { merchantId, merchantName, phone, status, addressList } }
 */
export function getMerchantInfo(merchantId) {
  return request({
    url: `${MERCHANT_BASE_URL}/info/${merchantId}`,
    method: 'get'
  })
}

/**
 * 获取指定商家的所有商品（分页）
 * @param {number|string} merchantId 商家ID
 * @param {number} pageNum 页码
 * @param {number} pageSize 每页数量
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { total: number, data: [...] } }
 */
export function getShopListByMerchant(merchantId, pageNum = 1, pageSize = 100) {
  return request({
    url: `${SHOP_BASE_URL}/merchant/${merchantId}`,
    method: 'get',
    params: { pageNum, pageSize }
  })
}

/**
 * 获取商家列表（分页查询，固定100条/页）
 * @param {Object} data 查询条件 { username, pageNum }
 * @returns {Promise} 响应对象 { code: 200, msg: null, data: { count: number, data: [...] } }
 */
export function getMerchantList(data = {}) {
  const params = {
    username: data.username || '',
    pageNum: data.pageNum || 1
  }
  return request({
    url: `${SHOP_BASE_URL}/merchantList`,
    method: 'get',
    params
  })
}

export function getProductImages(productIds) {
  return request({
    url: `${COMMODITY_BASE_URL}/images`,
    method: 'post',
    data: productIds
  })
}

export { PAGE_SIZE }