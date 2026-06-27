<template>
  <div class="pay-page">
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
        <div class="page-title">订单支付</div>
      </div>
    </header>

    <main class="main-content">
      <div class="container">
        <div class="pay-card">
          <div class="countdown-section">
            <div class="countdown-label">支付剩余时间</div>
            <div class="countdown-time">
              <span class="time-block">{{ countdown.minutes }}</span>
              <span class="time-separator">:</span>
              <span class="time-block">{{ countdown.seconds }}</span>
            </div>
            <div class="countdown-tip">请尽快完成支付，超时订单将自动取消</div>
          </div>

          <div class="order-info-section">
            <h3 class="section-title">订单信息</h3>
            <div class="order-detail">
              <div class="detail-row">
                <span class="label">订单编号：</span>
                <span class="value">{{ orderInfo.orderNo || '--' }}</span>
              </div>
              <div v-if="orderItems.length > 0" class="product-list">
                <div v-for="item in orderItems" :key="item.id" class="product-info">
                  <div class="product-img">
                    <ProductImage :src="productImages[item.productId]" :alt="item.productName" />
                  </div>
                  <div class="product-detail">
                    <h4>{{ item.productName }}</h4>
                    <p class="price-quantity">
                      <span class="price">¥{{ item.price?.toFixed(2) }}</span>
                      <span class="quantity">x {{ item.quantity }}</span>
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="payment-section">
            <h3 class="section-title">支付方式</h3>
            <div class="payment-methods">
              <div 
                class="payment-method" 
                :class="{ active: selectedPayment === 'alipay' }"
                @click="selectedPayment = 'alipay'"
              >
                <el-icon :size="24"><Wallet /></el-icon>
                <span>支付宝</span>
              </div>
              <div 
                class="payment-method" 
                :class="{ active: selectedPayment === 'wechat' }"
                @click="selectedPayment = 'wechat'"
              >
                <el-icon :size="24"><Wallet /></el-icon>
                <span>微信支付</span>
              </div>
              <div 
                class="payment-method" 
                :class="{ active: selectedPayment === 'balance' }"
                @click="selectedPayment = 'balance'"
              >
                <el-icon :size="24"><Wallet /></el-icon>
                <span>余额支付</span>
              </div>
            </div>
          </div>

          <div class="amount-section">
            <div class="amount-row">
              <span class="label">商品金额：</span>
              <span class="value">¥{{ orderInfo.totalPrice?.toFixed(2) || '0.00' }}</span>
            </div>
            <div class="amount-row">
              <span class="label">运费：</span>
              <span class="value">¥0.00</span>
            </div>
            <div class="amount-row total">
              <span class="label">应付金额：</span>
              <span class="value price">¥{{ orderInfo.totalPrice?.toFixed(2) || '0.00' }}</span>
            </div>
          </div>

          <div class="action-section">
            <el-button 
              type="primary" 
              size="large" 
              :loading="paying" 
              @click="handlePay"
              class="pay-btn"
            >
              立即付款
            </el-button>
            <el-button 
              size="large" 
              @click="handleCancelPay"
              class="cancel-btn"
            >
              取消支付
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Wallet } from '@element-plus/icons-vue'
import { payOrder, getOrderById, getOrderDetail } from '../../../api/user/UserOrder'
import { userLogout } from '../../../api/Login'
import { getProductImages } from '../../../api/User'
import ProductImage from '../../../components/ProductImage.vue'

const emit = defineEmits(['toOrderList', 'toUser', 'toCart', 'toLogin', 'toRegister', 'toAccount', 'toScdp'])

const userInfo = ref(null)
const orderInfo = ref({})
const orderItems = ref([])
const productImages = ref({})
const selectedPayment = ref('alipay')
const paying = ref(false)

const countdown = ref({ minutes: '30', seconds: '00' })
let countdownTimer = null
const PAY_TIMEOUT = 1800000

onMounted(async () => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }

  let orderId = localStorage.getItem('currentOrderId')
  
  if (!orderId) {
    const orderInfoStr = localStorage.getItem('currentOrderInfo')
    if (orderInfoStr) {
      const data = JSON.parse(orderInfoStr)
      orderId = data.orderId
    }
  }

  if (!orderId) {
    ElMessage.warning('订单信息不存在')
    handleToOrderList()
    return
  }

  await fetchOrderData(orderId)
})

const fetchOrderData = async (orderId) => {
  try {
    const orderRes = await getOrderById(orderId)
    if (orderRes.code === 200 && orderRes.data) {
      orderInfo.value = orderRes.data
      
      const itemsRes = await getOrderDetail(orderId)
      if (itemsRes.code === 200) {
        orderItems.value = itemsRes.data || []
        await fetchProductImages()
      }
      
      startCountdown(orderInfo.value.createdAt)
    } else {
      ElMessage.error('获取订单信息失败')
      handleToOrderList()
    }
  } catch (error) {
    console.error('获取订单数据失败:', error)
    ElMessage.error('获取订单数据失败')
    handleToOrderList()
  }
}

const fetchProductImages = async () => {
  const productIds = orderItems.value
    .filter(item => item.productId)
    .map(item => item.productId)
  
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

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})

const startCountdown = (createdAt) => {
  const updateCountdown = () => {
    if (!createdAt) {
      countdown.value = { minutes: '30', seconds: '00' }
      return
    }

    const created = new Date(createdAt).getTime()
    const now = Date.now()
    const elapsed = now - created
    const remaining = PAY_TIMEOUT - elapsed

    if (remaining <= 0) {
      countdown.value = { minutes: '00', seconds: '00' }
      if (countdownTimer) {
        clearInterval(countdownTimer)
      }
      ElMessage.warning('订单已超时，请重新下单')
      handleToOrderList()
      return
    }

    const minutes = Math.floor(remaining / 60000)
    const seconds = Math.floor((remaining % 60000) / 1000)

    countdown.value = {
      minutes: minutes.toString().padStart(2, '0'),
      seconds: seconds.toString().padStart(2, '0')
    }
  }

  updateCountdown()
  countdownTimer = setInterval(updateCountdown, 1000)
}

const handlePay = async () => {
  if (!orderInfo.value.id) {
    ElMessage.error('订单信息不完整')
    return
  }

  paying.value = true
  try {
    const res = await payOrder({
      id: orderInfo.value.id,
      paymentMethod: selectedPayment.value
    })

    if (res.code === 200) {
      ElMessage.success('支付成功')
      localStorage.removeItem('currentOrderId')
      localStorage.removeItem('currentOrderInfo')
      handleToOrderList()
    } else {
      ElMessage.error(res.msg || '支付失败')
    }
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败，请重试')
  } finally {
    paying.value = false
  }
}

const handleCancelPay = async () => {
  try {
    await ElMessageBox.confirm(
      '确定取消支付吗？30分钟内您仍可在订单管理页面继续支付。',
      '取消支付',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '继续支付',
        type: 'warning'
      }
    )
    localStorage.removeItem('currentOrderId')
    localStorage.removeItem('currentOrderInfo')
    handleToOrderList()
  } catch (error) {
    // 用户点击继续支付，不做任何操作
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
.pay-page {
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
}

.pay-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}

.countdown-section {
  text-align: center;
  padding: 30px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 30px;

  .countdown-label {
    font-size: 16px;
    color: #666;
    margin-bottom: 15px;
  }

  .countdown-time {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
    margin-bottom: 15px;

    .time-block {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 60px;
      height: 60px;
      background: linear-gradient(135deg, #e6a23c 0%, #f56c6c 100%);
      color: #fff;
      font-size: 32px;
      font-weight: 700;
      border-radius: 8px;
    }

    .time-separator {
      font-size: 32px;
      font-weight: 700;
      color: #e6a23c;
    }
  }

  .countdown-tip {
    font-size: 14px;
    color: #e6a23c;
  }
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.order-info-section {
  margin-bottom: 30px;

  .order-detail {
    .detail-row {
      display: flex;
      margin-bottom: 15px;
      .label { color: #666; width: 100px; }
      .value { color: #333; }
    }

    .product-info {
      display: flex;
      gap: 20px;
      padding: 15px;
      background: #f8fbff;
      border-radius: 8px;

      .product-img {
        width: 80px;
        height: 80px;
        border-radius: 4px;
        overflow: hidden;
        img { width: 100%; height: 100%; object-fit: cover; }
      }

      .product-detail {
        flex: 1;
        h4 { font-size: 16px; color: #333; margin-bottom: 8px; }
        .spec { font-size: 13px; color: #999; margin-bottom: 8px; }
        .price-quantity {
          display: flex;
          justify-content: space-between;
          .price { color: #e4393c; font-weight: 600; }
          .quantity { color: #666; }
        }
      }
    }
  }
}

.payment-section {
  margin-bottom: 30px;

  .payment-methods {
    display: flex;
    gap: 20px;

    .payment-method {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 15px 30px;
      border: 2px solid #e0e0e0;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        border-color: #2a9eff;
      }

      &.active {
        border-color: #2a9eff;
        background: #f0f9ff;
        color: #2a9eff;
      }

      span {
        font-size: 15px;
        font-weight: 500;
      }
    }
  }
}

.amount-section {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8fbff;
  border-radius: 8px;

  .amount-row {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 10px;

    .label { color: #666; }
    .value { color: #333; margin-left: 20px; min-width: 100px; text-align: right; }

    &.total {
      margin-top: 15px;
      padding-top: 15px;
      border-top: 1px solid #e0e0e0;

      .label { font-weight: 600; }
      .price { color: #e4393c; font-size: 24px; font-weight: 700; }
    }
  }
}

.action-section {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding-top: 20px;

  .pay-btn {
    min-width: 200px;
    height: 50px;
    font-size: 18px;
  }

  .cancel-btn {
    min-width: 150px;
    height: 50px;
    font-size: 18px;
  }
}

.footer {
  padding: 40px 0;
  text-align: center;
  .copyright { color: #999; font-size: 13px; }
}
</style>
