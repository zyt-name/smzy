<template>
  <div class="confirm-order-page">
    <nav class="top-nav">
      <div class="container">
        <div class="left">
          <span class="link" @click="handleToUser">商城首页</span>
        </div>
        <div class="right">
          <template v-if="userInfo">
            <span class="link user-name">你好，{{ userInfo.userName || userInfo.nickname || '用户' }}</span>
            <span class="link logout-btn" @click="handleLogout">退出登录</span>
          </template>
          <template v-else>
            <span class="link" @click="handleToLogin">你好，请登录</span>
            <span class="link" @click="handleToRegister">免费注册</span>
          </template>
          <span class="divider">|</span>
          <span class="link" @click="handleToAccount">个人中心</span>
          <span class="link" @click="handleToCart">购物车</span>
          <span class="link" @click="handleToScdp">我的收藏</span>
          <span class="link" @click="handleToOrderList">我的订单</span>
        </div>
      </div>
    </nav>

    <header class="header-area">
      <div class="container">
        <div class="logo" @click="handleToUser">
          <div class="brand-logo">智码<span>数云</span></div>
          <div class="brand-slogan">智慧连接 · 码动未来</div>
        </div>
        <div class="page-title">确认订单</div>
      </div>
    </header>

    <main class="main-content">
      <div class="container">
        <div class="section address-section">
          <h3 class="section-title">收货地址</h3>
          <div v-if="addressList.length > 0" class="address-list">
            <div
              v-for="addr in addressList"
              :key="addr.id"
              class="address-card"
              :class="{ active: selectedAddress?.id === addr.id }"
              @click="selectAddress(addr)"
            >
              <div class="address-header">
                <span class="name">{{ addr.receiverName }}</span>
                <span class="phone">{{ addr.receiverPhone }}</span>
                <el-tag v-if="addr.isDefault === 0" type="primary" size="small">默认</el-tag>
              </div>
              <div class="address-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}</div>
            </div>
          </div>
          <div v-else class="no-address">
            <el-empty description="暂无收货地址，请先到个人中心添加">
              <el-button type="primary" @click="handleToAccount">去添加</el-button>
            </el-empty>
          </div>
        </div>

        <div class="section product-section">
          <h3 class="section-title">商品信息</h3>
          <div v-if="productInfo" class="product-card single">
            <div class="product-img">
              <ProductImage :src="productImages[productInfo.id]" :alt="productInfo.name" />
            </div>
            <div class="product-info">
              <h4 class="product-name">{{ productInfo.name }}</h4>
              <p class="product-spec">{{ productInfo.specification }}</p>
            </div>
            <div class="product-price">¥{{ productInfo.price?.toFixed(2) }}</div>
            <div class="product-quantity">x {{ quantity }}</div>
            <div class="product-subtotal">¥{{ (productInfo.price * quantity).toFixed(2) }}</div>
          </div>
          <div v-else-if="cartItems.length > 0" class="product-list">
            <div v-for="item in cartItems" :key="item.productId" class="product-card">
              <div class="product-img">
                <ProductImage :src="productImages[item.productId]" :alt="item.productName" />
              </div>
              <div class="product-info">
                <h4 class="product-name">{{ item.productName }}</h4>
                <p class="product-spec" v-if="item.userSpecification">{{ item.userSpecification }}</p>
              </div>
              <div class="product-price">¥{{ parseFloat(item.price || 0).toFixed(2) }}</div>
              <div class="product-quantity">x {{ item.productCount }}</div>
              <div class="product-subtotal">¥{{ (item.price * item.productCount).toFixed(2) }}</div>
            </div>
          </div>
        </div>

        <div class="section remark-section">
          <h3 class="section-title">订单备注</h3>
          <el-input
            v-model="remark"
            type="textarea"
            :rows="3"
            placeholder="请输入订单备注（选填）"
            maxlength="200"
            show-word-limit
          />
        </div>

        <div class="section summary-section">
          <div class="summary-row">
            <span class="label">商品总价：</span>
            <span class="value">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span class="label">运费：</span>
            <span class="value">¥0.00</span>
          </div>
          <div class="summary-row total">
            <span class="label">应付总额：</span>
            <span class="value price">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <div class="submit-row">
            <el-button size="large" @click="handleCancel">取消</el-button>
            <el-button type="primary" size="large" :disabled="!selectedAddress" :loading="submitting" @click="handleSubmit">
              去支付
            </el-button>
          </div>
        </div>
      </div>
    </main>

    <footer class="footer">
      <div class="container">
        <div class="footer-bottom">
          <p class="copyright">Copyright © 2024-2026 智码数云 zhishuyun.com 版权所有</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { buySingleItem, cartToOrder } from '../../../api/user/UserOrder'
import { getAddressList, getProductImages } from '../../../api/User'
import { userLogout } from '../../../api/Login'
import ProductImage from '../../../components/ProductImage.vue'

const props = defineProps({
  orderType: {
    type: String,
    default: 'single'
  },
  productData: {
    type: Object,
    default: null
  },
  cartData: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['toPay', 'toUser', 'toCart', 'toLogin', 'toRegister', 'toAccount', 'toScdp', 'toOrderList'])

const userInfo = ref(null)
const addressList = ref([])
const selectedAddress = ref(null)
const productInfo = ref(null)
const cartItems = ref([])
const productImages = ref({})
const quantity = ref(1)
const remark = ref('')
const submitting = ref(false)

const totalPrice = computed(() => {
  if (productInfo.value) {
    return productInfo.value.price * quantity.value
  } else if (cartItems.value.length > 0) {
    return cartItems.value.reduce((sum, item) => sum + (item.price * item.productCount), 0)
  }
  return 0
})

onMounted(async () => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }

  const productDataStr = localStorage.getItem('orderProductInfo')
  const cartDataStr = localStorage.getItem('orderCartInfo')

  if (productDataStr) {
    const data = JSON.parse(productDataStr)
    productInfo.value = data.product
    quantity.value = data.quantity || 1
    if (productInfo.value?.id) {
      await fetchProductImages([productInfo.value.id])
    }
  } else if (cartDataStr) {
    const data = JSON.parse(cartDataStr)
    cartItems.value = data.items || []
    const productIds = cartItems.value.filter(item => item.productId).map(item => item.productId)
    if (productIds.length > 0) {
      await fetchProductImages(productIds)
    }
  }

  await fetchAddressList()
})

const fetchProductImages = async (productIds) => {
  if (productIds.length === 0) return
  
  try {
    const res = await getProductImages(productIds)
    if (res.code === 200 && res.data) {
      productImages.value = res.data
    }
  } catch (error) {
    console.error('获取商品图片失败:', error)
  }
}

const fetchAddressList = async () => {
  try {
    const res = await getAddressList()
    if (res.code === 200) {
      addressList.value = res.data || []
      const defaultAddr = addressList.value.find(addr => addr.isDefault === 0)
      if (defaultAddr) {
        selectedAddress.value = defaultAddr
      } else if (addressList.value.length > 0) {
        selectedAddress.value = addressList.value[0]
      }
    }
  } catch (error) {
    console.error('获取地址列表失败:', error)
    ElMessage.error('获取地址列表失败')
  }
}

const selectAddress = (addr) => {
  selectedAddress.value = addr
}

const handleSubmit = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }

  submitting.value = true
  try {
    let res
    if (productInfo.value) {
      res = await buySingleItem(
        productInfo.value.id,
        quantity.value,
        selectedAddress.value.id,
        remark.value
      )
    } else if (cartItems.value.length > 0) {
      res = await cartToOrder(selectedAddress.value.id, remark.value)
    } else {
      ElMessage.error('商品信息不存在')
      return
    }

    if (res.code === 200) {
      ElMessage.success('订单创建成功')
      localStorage.removeItem('orderProductInfo')
      localStorage.removeItem('orderCartInfo')

      const orderId = res.data
      localStorage.setItem('currentOrderId', orderId)

      emit('toPay', { type: 'shop', orderId: orderId })
    } else {
      ElMessage.error(res.msg || '订单创建失败')
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('创建订单失败，请重试')
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  localStorage.removeItem('orderProductInfo')
  localStorage.removeItem('orderCartInfo')
  if (cartItems.value.length > 0) {
    emit('toCart')
  } else {
    emit('toUser')
  }
}

const handleLogout = async () => {
  try {
    await userLogout()
  } catch (error) {
    console.error('Logout failed:', error)
  } finally {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    userInfo.value = null
    ElMessage.success('已退出登录')
    window.location.href = window.location.origin
  }
}

const handleToUser = () => emit('toUser')
const handleToLogin = () => emit('toLogin')
const handleToRegister = () => emit('toRegister')
const handleToAccount = () => emit('toAccount')
const handleToCart = () => emit('toCart')
const handleToScdp = () => emit('toScdp')
const handleToOrderList = () => emit('toOrderList')
</script>

<style lang="scss" scoped>
.confirm-order-page {
  background-color: #EDF4FD;
  background-image: url('../../../assets/bj.png');
  background-repeat: no-repeat;
  background-position: center top;
  background-size: cover;
  min-height: 100vh;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.container {
  width: 1200px;
  margin: 0 auto;
}

.top-nav {
  background: linear-gradient(180deg, rgba(176, 224, 255, 0.8) 0%, rgba(214, 238, 255, 0.6) 70%, rgba(237, 244, 253, 0) 100%);
  height: 52px;
  line-height: 52px;
  font-size: 13px;
  color: #0b3a67;
  position: relative;
  z-index: 10;
  .container { display: flex; justify-content: space-between; }
  .left, .right { display: flex; gap: 20px; }
  .link { cursor: pointer; &:hover { color: #2a9eff; } }
}

.header-area {
  padding: 30px 0;
  .container { display: flex; align-items: center; gap: 40px; }
  .logo {
    cursor: pointer;
    display: flex;
    flex-direction: column;
    .brand-logo { font-size: 32px; font-weight: 800; color: #1a1a1a; span { color: #2a9eff; } }
    .brand-slogan { color: #999; font-size: 12px; margin-top: 5px; letter-spacing: 2px; }
  }
  .page-title { font-size: 24px; color: #333; font-weight: 600; }
}

.main-content {
  padding: 20px 0 60px;

  .section {
    background: #fff;
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 20px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  }

  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid #eee;
  }
}

.address-section {
  .address-list {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
  }

  .address-card {
    width: 280px;
    padding: 16px;
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover, &.active {
      border-color: #2a9eff;
      background: #f0f9ff;
    }

    .address-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 8px;

      .name { font-weight: 600; color: #333; }
      .phone { color: #666; }
    }

    .address-detail {
      color: #666;
      font-size: 14px;
      line-height: 1.5;
    }
  }

  .no-address {
    padding: 40px 0;
  }
}

.product-section {
  .product-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .product-card {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 16px;
    background: #f8fbff;
    border-radius: 8px;

    .product-img {
      width: 100px;
      height: 100px;
      border-radius: 8px;
      overflow: hidden;
      img { width: 100%; height: 100%; object-fit: cover; }
    }

    .product-info {
      flex: 1;

      .product-name {
        font-size: 16px;
        font-weight: 500;
        color: #333;
        margin-bottom: 8px;
      }

      .product-spec {
        font-size: 14px;
        color: #999;
      }
    }

    .product-price {
      width: 120px;
      text-align: center;
      color: #666;
    }

    .product-quantity {
      width: 80px;
      text-align: center;
      color: #666;
    }

    .product-subtotal {
      width: 120px;
      text-align: center;
      color: #e4393c;
      font-weight: 600;
      font-size: 16px;
    }
  }
}

.remark-section {
  .el-textarea {
    width: 100%;
  }
}

.summary-section {
  .summary-row {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 20px;
    margin-bottom: 12px;
    font-size: 14px;

    .label { color: #666; }
    .value { color: #333; min-width: 100px; text-align: right; }

    &.total {
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #eee;
      font-size: 16px;

      .label { font-weight: 600; }
      .price { color: #e4393c; font-size: 24px; font-weight: 700; }
    }
  }

  .submit-row {
    display: flex;
    justify-content: flex-end;
    gap: 16px;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid #eee;

    .el-button {
      padding: 0 40px;
      font-size: 16px;
    }
  }
}

.footer {
  padding: 40px 0;
  text-align: center;
  .copyright { color: #999; font-size: 13px; }
}
</style>
