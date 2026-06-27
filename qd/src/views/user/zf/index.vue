<template>
  <div class="pay-page">
    <!-- Top Navigation -->
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

    <!-- Header Area -->
    <header class="header-area">
      <div class="container">
        <div class="logo" @click="handleToUser">
          <div class="brand-logo">智码<span>数云</span></div>
          <div class="brand-slogan">智慧连接 · 码动未来</div>
        </div>
        <div class="page-title">订单支付</div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <div class="container">
        <!-- Countdown Section -->
        <div class="section countdown-section">
          <div class="countdown-box">
            <div class="countdown-label">支付剩余时间</div>
            <div class="countdown-time">
              <span class="time-block">{{ countdown.minutes }}</span>
              <span class="separator">:</span>
              <span class="time-block">{{ countdown.seconds }}</span>
            </div>
            <div class="countdown-tip">超时后订单将自动取消</div>
          </div>
        </div>

        <!-- Order Info Section -->
        <div class="section order-info-section">
          <h3 class="section-title">订单信息</h3>
          <div class="order-detail">
            <div class="info-row">
              <span class="label">订单类型：</span>
              <span class="value">{{ orderType === 'cart' ? '购物车结算' : '直接购买' }}</span>
            </div>
            <div class="info-row">
              <span class="label">商品数量：</span>
              <span class="value">{{ totalCount }} 件</span>
            </div>
            <div class="info-row">
              <span class="label">收货地址：</span>
              <span class="value">{{ addressText }}</span>
            </div>
            <div class="info-row">
              <span class="label">订单备注：</span>
              <span class="value">{{ remark || '无' }}</span>
            </div>
          </div>
        </div>

        <!-- Product List Section -->
        <div class="section product-section">
          <h3 class="section-title">商品清单</h3>
          <div class="product-list">
            <div v-for="(item, index) in productList" :key="index" class="product-item">
              <div class="product-img">
                <img :src="item.imageUrl || '/placeholder.png'" :alt="item.productName || item.name" />
              </div>
              <div class="product-info">
                <h4 class="product-name">{{ item.productName || item.name }}</h4>
              </div>
              <div class="product-price">¥{{ parseFloat(item.price || 0).toFixed(2) }}</div>
              <div class="product-quantity">x {{ item.productCount || item.quantity }}</div>
              <div class="product-subtotal">¥{{ (parseFloat(item.price || 0) * parseInt(item.productCount || item.quantity || 1)).toFixed(2) }}</div>
            </div>
          </div>
        </div>

        <!-- Payment Section -->
        <div class="section payment-section">
          <h3 class="section-title">选择支付方式</h3>
          <div class="payment-methods">
            <div
              class="payment-method"
              :class="{ active: selectedPayment === 'alipay' }"
              @click="selectedPayment = 'alipay'"
            >
              <div class="method-icon alipay">支</div>
              <div class="method-name">支付宝</div>
            </div>
            <div
              class="payment-method"
              :class="{ active: selectedPayment === 'wechat' }"
              @click="selectedPayment = 'wechat'"
            >
              <div class="method-icon wechat">微</div>
              <div class="method-name">微信支付</div>
            </div>
            <div
              class="payment-method"
              :class="{ active: selectedPayment === 'card' }"
              @click="selectedPayment = 'card'"
            >
              <div class="method-icon card">卡</div>
              <div class="method-name">银行卡</div>
            </div>
          </div>
        </div>

        <!-- Total Section -->
        <div class="section total-section">
          <div class="total-row">
            <span class="label">应付总额：</span>
            <span class="price">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <div class="action-row">
            <el-button size="large" @click="handleCancel">暂不支付</el-button>
            <el-button type="primary" size="large" :loading="paying" @click="handlePay">
              立即支付
            </el-button>
          </div>
        </div>
      </div>
    </main>

    <!-- Footer Area -->
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
import { payOrder } from '../../../api/user/UserOrder'
import { userLogout } from '../../../api/Login'

const props = defineProps({
  orderData: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['toOrderList', 'toUser', 'toLogin', 'toRegister', 'toAccount', 'toCart', 'toScdp'])

const userInfo = ref(null)
const orderId = ref(null)
const orderType = ref('') // 'cart' 或 'shop'
const productList = ref([])
const totalCount = ref(0)
const totalPrice = ref(0)
const address = ref(null)
const remark = ref('')
const selectedPayment = ref('alipay')
const paying = ref(false)

// 倒计时相关
const countdown = ref({ minutes: '30', seconds: '00' })
let countdownTimer = null

// 支付超时时间（30分钟 = 1800000毫秒）
const PAY_TIMEOUT = 1800000

const addressText = computed(() => {
  if (!address.value) return ''
  return `${address.value.province}${address.value.city}${address.value.district}${address.value.detail} (${address.value.name} ${address.value.phone})`
})

onMounted(() => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }

  // 获取订单信息
  const orderInfoStr = localStorage.getItem('currentOrderInfo')
  if (orderInfoStr) {
    const orderInfo = JSON.parse(orderInfoStr)
    orderId.value = orderInfo.orderId
    orderType.value = orderInfo.orderType
    productList.value = orderInfo.orderType === 'cart' ? orderInfo.cartItems : [orderInfo.productInfo]
    totalCount.value = orderInfo.totalCount || orderInfo.quantity
    totalPrice.value = orderInfo.totalPrice
    address.value = orderInfo.address
    remark.value = orderInfo.remark

    // 开始倒计时，传入订单创建时间
    startCountdown(orderInfo.createdAt)
  } else {
    ElMessage.warning('订单信息不存在')
    handleToOrderList()
  }
})

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
    const remainingTime = PAY_TIMEOUT - elapsed

    if (remainingTime <= 0) {
      countdown.value = { minutes: '00', seconds: '00' }
      if (countdownTimer) {
        clearInterval(countdownTimer)
      }
      ElMessage.warning('订单已超时，请重新下单')
      handleToOrderList()
      return
    }

    const minutes = Math.floor(remainingTime / 60000)
    const seconds = Math.floor((remainingTime % 60000) / 1000)

    countdown.value.minutes = minutes.toString().padStart(2, '0')
    countdown.value.seconds = seconds.toString().padStart(2, '0')
  }

  updateCountdown()
  countdownTimer = setInterval(updateCountdown, 1000)
}

const handlePay = async () => {
  if (!orderId.value) {
    ElMessage.error('订单信息不完整')
    return
  }

  paying.value = true
  try {
    // 调用实际支付接口
    const res = await payOrder({
      id: orderId.value,
      paymentMethod: selectedPayment.value
    })

    if (res.code === 200) {
      ElMessage.success('支付成功！')
      // 清除订单信息
      localStorage.removeItem('currentOrderInfo')
      // 跳转到订单列表
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

const handleCancel = () => {
  ElMessageBox.confirm(
    '您可以选择稍后支付，订单将保留在"我的订单"中，是否继续？',
    '提示',
    {
      confirmButtonText: '去订单列表',
      cancelButtonText: '继续支付',
      type: 'info'
    }
  ).then(() => {
    handleToOrderList()
  }).catch(() => {
    // 继续支付，不做任何操作
  })
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

.countdown-section {
  text-align: center;
  padding: 40px;

  .countdown-box {
    display: inline-block;
    padding: 30px 60px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px;
    color: #fff;
  }

  .countdown-label {
    font-size: 16px;
    margin-bottom: 16px;
    opacity: 0.9;
  }

  .countdown-time {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    margin-bottom: 16px;

    .time-block {
      width: 60px;
      height: 60px;
      background: rgba(255,255,255,0.2);
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 32px;
      font-weight: 700;
    }

    .separator {
      font-size: 32px;
      font-weight: 700;
    }
  }

  .countdown-tip {
    font-size: 14px;
    opacity: 0.8;
  }
}

.order-info-section {
  .order-detail {
    .info-row {
      display: flex;
      margin-bottom: 12px;
      font-size: 14px;

      .label {
        width: 100px;
        color: #666;
      }

      .value {
        flex: 1;
        color: #333;
      }
    }
  }
}

.product-section {
  .product-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .product-item {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 16px;
    background: #f8fbff;
    border-radius: 8px;

    .product-img {
      width: 80px;
      height: 80px;
      border-radius: 8px;
      overflow: hidden;
      img { width: 100%; height: 100%; object-fit: cover; }
    }

    .product-info {
      flex: 1;

      .product-name {
        font-size: 14px;
        font-weight: 500;
        color: #333;
        margin-bottom: 4px;
      }

      .product-spec {
        font-size: 12px;
        color: #999;
      }
    }

    .product-price {
      width: 100px;
      text-align: center;
      color: #666;
      font-size: 14px;
    }

    .product-quantity {
      width: 60px;
      text-align: center;
      color: #666;
      font-size: 14px;
    }

    .product-subtotal {
      width: 100px;
      text-align: center;
      color: #e4393c;
      font-weight: 600;
      font-size: 14px;
    }
  }
}

.payment-section {
  .payment-methods {
    display: flex;
    gap: 20px;
  }

  .payment-method {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px 24px;
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover, &.active {
      border-color: #2a9eff;
      background: #f0f9ff;
    }

    .method-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 16px;
      font-weight: 600;
      color: #fff;

      &.alipay { background: #1677ff; }
      &.wechat { background: #07c160; }
      &.card { background: #ff6b6b; }
    }

    .method-name {
      font-size: 14px;
      color: #333;
    }
  }
}

.total-section {
  .total-row {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 20px;
    margin-bottom: 24px;
    padding-bottom: 24px;
    border-bottom: 1px solid #eee;

    .label {
      font-size: 16px;
      color: #666;
    }

    .price {
      font-size: 32px;
      color: #e4393c;
      font-weight: 700;
    }
  }

  .action-row {
    display: flex;
    justify-content: flex-end;
    gap: 16px;

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
