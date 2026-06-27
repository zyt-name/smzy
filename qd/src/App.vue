<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import UserIndex from './views/user/index.vue'
import LoginIndex from './views/login/index.vue'
import RegisterIndex from './views/login/register/index.vue'
import MerchantIndex from './views/merchant/index.vue'
import AdminIndex from './views/admin/index.vue'
import MerchantRegister from './views/login/merchantRegister/index.vue'
import SearchIndex from './views/user/ss/index.vue'
import ProductDetail from './views/user/shop/index.vue'
import ShopMerchant from './views/user/shopMerchant/index.vue'
import ShopCart from './views/user/shopCart/index.vue'
import UserAccount from './views/user/zhgl/index.vue'
import ScdpIndex from './views/user/scdp/index.vue'
import OrderIndex from './views/user/order/index.vue'
import ZfIndex from './views/user/zf/index.vue'
import ResponseNotify from './components/ResponseNotify.vue'

const getUrlParam = (name) => {
  const urlParams = new URLSearchParams(window.location.search)
  return urlParams.get(name)
}

const isStandaloneDetail = ref(false)
const standaloneShopId = ref(null)
const isStandaloneShop = ref(false)
const standaloneMerchantId = ref(null)
const orderType = ref('shop')
const orderProductData = ref(null)
const orderCartData = ref(null)
const payOrderData = ref(null)

const currentPage = ref(localStorage.getItem('currentPage') || 'user')
const storedSearchQuery = localStorage.getItem('searchQuery')
const searchQuery = ref((storedSearchQuery && storedSearchQuery !== '[object Object]') ? storedSearchQuery : '')
const searchResults = ref([])
const selectedCategory = ref('')
const selectedCategories = ref([])
const searchTypeProp = ref('product')
const selectedProduct = ref(JSON.parse(localStorage.getItem('selectedProduct') || '{}'))
const selectedShop = ref(JSON.parse(localStorage.getItem('selectedShop') || '{}'))
const accountTab = ref(localStorage.getItem('accountTab') || 'profile')

const pagesRequiringLogin = ['merchant', 'admin', 'detail', 'shop', 'shopMerchant', 'shopCart', 'account', 'order', 'zf']
const pagesNotRequiringLogin = ['user', 'search', 'login', 'register', 'merchantRegister']

const checkUserLoggedIn = () => {
  const token = localStorage.getItem('token')
  const userInfo = localStorage.getItem('userInfo')
  return !!(token && userInfo)
}

const effectivePage = computed(() => {
  if (isStandaloneDetail.value) {
    return 'detail'
  }
  if (isStandaloneShop.value) {
    return 'shop'
  }
  return currentPage.value
})

watch([currentPage, searchQuery, selectedProduct, selectedShop, accountTab], () => {
  localStorage.setItem('currentPage', currentPage.value)
  localStorage.setItem('searchQuery', searchQuery.value)
  localStorage.setItem('selectedProduct', JSON.stringify(selectedProduct.value))
  localStorage.setItem('selectedShop', JSON.stringify(selectedShop.value))
  localStorage.setItem('accountTab', accountTab.value)
}, { deep: true })

onMounted(async () => {
  const pageParam = getUrlParam('page')
  const shopIdParam = getUrlParam('shopId')
  const merchantIdParam = getUrlParam('merchantId')

  if (pageParam === 'detail' && shopIdParam) {
    if (!checkUserLoggedIn()) {
      currentPage.value = 'login'
      window.history.replaceState({ page: 'login' }, '')
      return
    }
    isStandaloneDetail.value = true
    standaloneShopId.value = shopIdParam
    return
  }

  if (pageParam === 'shop' && merchantIdParam) {
    if (!checkUserLoggedIn()) {
      currentPage.value = 'login'
      window.history.replaceState({ page: 'login' }, '')
      return
    }
    isStandaloneShop.value = true
    standaloneMerchantId.value = merchantIdParam
    return
  }

  if (pageParam === 'shopMerchant' && merchantIdParam) {
    if (!checkUserLoggedIn()) {
      currentPage.value = 'login'
      window.history.replaceState({ page: 'login' }, '')
      return
    }
    isStandaloneShop.value = true
    standaloneMerchantId.value = merchantIdParam
    return
  }
  
  if (pageParam === 'search') {
    currentPage.value = 'search'
    const queryParam = getUrlParam('query')
    if (queryParam) {
      searchQuery.value = queryParam
    }
    return
  }
  
  if (pageParam === 'shopCart') {
    if (!checkUserLoggedIn()) {
      currentPage.value = 'login'
      window.history.replaceState({ page: 'login' }, '')
      return
    }
    currentPage.value = 'shopCart'
    return
  }

  if (pageParam === 'account') {
    if (!checkUserLoggedIn()) {
      currentPage.value = 'login'
      window.history.replaceState({ page: 'login' }, '')
      return
    }
    const tabParam = getUrlParam('tab')
    if (tabParam) {
      accountTab.value = tabParam
    }
    currentPage.value = 'account'
    return
  }

  if (pageParam === 'order') {
    if (!checkUserLoggedIn()) {
      currentPage.value = 'login'
      window.history.replaceState({ page: 'login' }, '')
      return
    }
    const typeParam = getUrlParam('type')
    orderType.value = typeParam || 'shop'
    currentPage.value = 'order'
    return
  }

  if (pageParam === 'zf') {
    if (!checkUserLoggedIn()) {
      currentPage.value = 'login'
      window.history.replaceState({ page: 'login' }, '')
      return
    }
    currentPage.value = 'zf'
    return
  }

  window.addEventListener('popstate', (event) => {
    if (event.state && event.state.page) {
      currentPage.value = event.state.page
      if (event.state.data) {
        if (event.state.page === 'search') {
          isFromDetail.value = event.state.data === '__from_detail__'
          if (!isFromDetail.value) {
            searchQuery.value = event.state.data
          }
        }
        if (event.state.page === 'detail') selectedProduct.value = event.state.data
        if (event.state.page === 'shop') selectedShop.value = event.state.data
      }
    }
  })

  if (!window.history.state) {
    window.history.replaceState({ page: currentPage.value }, '')
  }
})

const isFromDetail = ref(false)

const togglePage = async (page, data = null) => {
  if (pagesRequiringLogin.includes(page) && !checkUserLoggedIn()) {
    currentPage.value = 'login'
    window.history.pushState({ page: 'login' }, '')
    return
  }
  
  // 当跳转到非独立页面时，清除独立页面状态
  if (page !== 'shop' && page !== 'shopMerchant' && page !== 'detail') {
    isStandaloneShop.value = false
    isStandaloneDetail.value = false
    standaloneMerchantId.value = null
    standaloneShopId.value = null
  }
  
  currentPage.value = page
  if (page === 'search') {
    isFromDetail.value = data === '__from_detail__'
    if (isFromDetail.value) {
      selectedCategory.value = ''
      selectedCategories.value = []
    } else if (typeof data === 'string') {
      searchQuery.value = data
      searchResults.value = []
      selectedCategory.value = ''
      selectedCategories.value = []
      searchTypeProp.value = 'product'
    } else if (data && typeof data === 'object') {
      searchQuery.value = data.query || ''
      searchResults.value = data.results || []
      selectedCategory.value = data.category || ''
      selectedCategories.value = data.categories || []
      searchTypeProp.value = data.searchType || 'product'
    }
  }
  if (page === 'detail' && data) {
    selectedProduct.value = data
  }
  if (page === 'shop' && data) {
    selectedShop.value = data
  }
  if (page === 'shopMerchant' && data) {
    isStandaloneShop.value = true
    standaloneMerchantId.value = data.merchantId
    currentPage.value = 'shop'
  }
  if (page === 'account' && typeof data === 'string') {
    accountTab.value = data
  }
  if (page === 'order' && data) {
    if (data.type) {
      orderType.value = data.type
    }
    if (data.orderType) {
      orderType.value = data.orderType
    }
    if (data.productInfo) {
      orderProductData.value = data.productInfo
    }
    if (data.cartData) {
      orderCartData.value = data.cartData
    }
  }
  if (page === 'zf' && data) {
    payOrderData.value = data
  }
  
  window.history.pushState({ page, data }, '')
}
</script>

<template>
  <div>
    <ResponseNotify />
    <UserIndex v-if="effectivePage === 'user'" 
      @toLogin="togglePage('login')" 
      @toRegister="togglePage('register')" 
      @toMerchant="togglePage('merchant')" 
      @toAdmin="togglePage('admin')"
      @toSearch="(q) => togglePage('search', q)"
      @toDetail="(p) => togglePage('detail', p)"
      @toCart="togglePage('shopCart')"
      @toAccount="(tab) => togglePage('account', tab)"
      @toScdp="togglePage('account', 'favorite')"
      @toOrder="togglePage('account', 'order')"
    />
    <LoginIndex v-if="effectivePage === 'login'" 
      @loginSuccess="(page) => togglePage(page)" 
      @toUser="togglePage('user')" 
      @toRegister="togglePage('register')" 
    />
    <RegisterIndex v-if="effectivePage === 'register'" 
      @toLogin="togglePage('login')" 
      @toUser="togglePage('user')" 
      @toMerchantRegister="togglePage('merchantRegister')"
    />
    <MerchantRegister v-if="effectivePage === 'merchantRegister'"
      @toLogin="togglePage('login')"
      @toUser="togglePage('user')"
    />
    <MerchantIndex v-if="effectivePage === 'merchant'" @toUser="togglePage('user')" />
    <AdminIndex v-if="effectivePage === 'admin'" @toUser="togglePage('user')" />
    <SearchIndex v-if="effectivePage === 'search'" 
      :initialQuery="searchQuery"
      :searchResults="searchResults"
      :isFromDetail="isFromDetail"
      :category="selectedCategory"
      :categories="selectedCategories"
      :searchType="searchTypeProp"
      @toUser="togglePage('user')" 
      @toLogin="togglePage('login')" 
      @toRegister="togglePage('register')"
      @toMerchant="togglePage('merchant')"
      @toAdmin="togglePage('admin')"
      @toSearch="(q) => togglePage('search', q)"
      @toDetail="(p) => togglePage('detail', p)"
      @toShop="(s) => togglePage('shop', s)"
      @toShopMerchant="(data) => togglePage('shopMerchant', data)"
      @toCart="togglePage('shopCart')"
      @toAccount="(tab) => togglePage('account', tab)"
      @toScdp="togglePage('account', 'favorite')"
      @toOrder="togglePage('account', 'order')"
    />
    <ProductDetail v-if="effectivePage === 'detail'"
      :product="selectedProduct"
      :searchQuery="searchQuery"
      :shopId="standaloneShopId"
      :isStandalone="isStandaloneDetail"
      @toUser="togglePage('user')"
      @toLogin="togglePage('login')"
      @toRegister="togglePage('register')"
      @toMerchant="togglePage('merchant')"
      @toAdmin="togglePage('admin')"
      @toSearch="(q) => togglePage('search', q)"
      @toShop="(s) => togglePage('shop', s)"
      @toCart="togglePage('shopCart')"
      @toAccount="(tab) => togglePage('account', tab)"
      @toScdp="togglePage('account', 'favorite')"
      @toOrder="togglePage('account', 'order')"
    />
    <ShopMerchant v-if="effectivePage === 'shop'"
      :shop="selectedShop"
      :merchantId="standaloneMerchantId"
      @toUser="togglePage('user')"
      @toLogin="togglePage('login')"
      @toRegister="togglePage('register')"
      @toMerchant="togglePage('merchant')"
      @toAdmin="togglePage('admin')"
      @toSearch="(q) => togglePage('search', q)"
      @toDetail="(p) => togglePage('detail', p)"
      @toCart="togglePage('shopCart')"
      @toAccount="(tab) => togglePage('account', tab)"
      @toScdp="togglePage('account', 'favorite')"
      @toOrder="togglePage('account', 'order')"
    />
    <ShopCart v-if="effectivePage === 'shopCart'"
      @toUser="togglePage('user')"
      @toLogin="togglePage('login')"
      @toRegister="togglePage('register')"
      @toSearch="(q) => togglePage('search', q)"
      @toDetail="(p) => togglePage('detail', p)"
      @toAccount="(tab) => togglePage('account', tab)"
      @toScdp="togglePage('account', 'favorite')"
      @toOrder="(data) => togglePage('order', data)"
    />
    <OrderIndex v-if="effectivePage === 'order'"
      :orderType="orderType"
      :productData="orderProductData"
      :cartData="orderCartData"
      @toUser="togglePage('user')"
      @toLogin="togglePage('login')"
      @toRegister="togglePage('register')"
      @toAccount="(tab) => togglePage('account', tab)"
      @toCart="togglePage('shopCart')"
      @toScdp="togglePage('account', 'favorite')"
      @toOrderList="togglePage('account', 'order')"
      @toPay="(data) => togglePage('order', data)"
    />
    <ZfIndex v-if="effectivePage === 'zf'"
      :orderData="payOrderData"
      @toUser="togglePage('user')"
      @toLogin="togglePage('login')"
      @toRegister="togglePage('register')"
      @toAccount="(tab) => togglePage('account', tab)"
      @toCart="togglePage('shopCart')"
      @toScdp="togglePage('account', 'favorite')"
      @toOrderList="togglePage('account', 'order')"
    />
    <UserAccount v-if="effectivePage === 'account'" 
      :initialTab="accountTab"
      @toUser="togglePage('user')" 
      @toLogin="togglePage('login')" 
      @toRegister="togglePage('register')"
      @toScdp="togglePage('account', 'favorite')"
      @toPay="(data) => togglePage('zf', data)"
      @toOrder="togglePage('account', 'order')"
    />
  </div>
</template>

<style>
body {
  margin: 0;
  padding: 0;
}
</style>
