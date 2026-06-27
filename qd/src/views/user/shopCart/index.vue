<template>
  <div class="shop-cart-page">
    <!-- Top Navigation -->
    <nav class="top-nav">
      <div class="container">
        <div class="left">
          <span class="link" @click="$emit('toUser')">商城首页</span>
        </div>
        <div class="right">
          <template v-if="userInfo">
            <span class="link user-name">你好，{{ userInfo.userName || userInfo.nickname || '用户' }}</span>
            <span class="link logout-btn" @click="handleLogout">退出登录</span>
          </template>
          <template v-else>
            <span class="link" @click="$emit('toLogin')">你好，请登录</span>
            <span class="link" @click="$emit('toRegister')">免费注册</span>
          </template>
          <span class="divider">|</span>
          <span class="link" @click="$emit('toAccount')">个人中心</span>
          <span class="link" @click="$emit('toScdp')">我的收藏</span>
          <span class="link" @click="$emit('toOrder')">我的订单</span>
        </div>
      </div>
    </nav>

    <!-- Header Area -->
    <header class="header-area">
      <div class="container">
        <div class="logo" @click="$emit('toUser')">
          <div class="brand-logo">智码<span>数云</span></div>
          <div class="brand-slogan">智慧连接 · 码动未来</div>
        </div>
        <div class="cart-title">购物车</div>
        <div class="search-box">
          <div class="search-input-wrapper">
            <el-input 
              v-model="searchQuery" 
              placeholder="搜索感兴趣的数码产品..."
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button type="primary" class="search-btn" @click="handleSearch">搜索</el-button>
              </template>
            </el-input>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <div class="container">
        <div v-if="cartItems.length > 0" class="cart-table-container">
          <div class="cart-header">
            <div class="col-info">商品信息</div>
            <div class="col-price">单价</div>
            <div class="col-num">数量</div>
            <div class="col-total">小计</div>
            <div class="col-action">操作</div>
          </div>

          <div class="cart-list">
            <div v-for="item in cartItems" :key="item.productId" class="cart-item">
              <div class="col-info">
                <div class="product-info" @click="handleToDetail(item)">
                  <ProductImage :src="item.imageUrl" :alt="item.productName" class="product-img" />
                  <div class="details">
                    <p class="name">{{ item.productName }}</p>
                    <p class="spec">{{ item.userSpecification }}</p>
                  </div>
                </div>
              </div>
              <div class="col-price">¥{{ parseFloat(item.price || 0).toFixed(2) }}</div>
              <div class="col-num">
                <el-input-number v-model="item.productCount" :min="1" :max="99" size="small" @change="handleCountChange(item)" />
              </div>
              <div class="col-total">¥{{ (parseFloat(item.price || 0) * parseInt(item.productCount || 1)).toFixed(2) }}</div>
              <div class="col-action">
                <el-button type="danger" link @click="removeFromCart(item)">删除</el-button>
              </div>
            </div>
          </div>

          <!-- Cart Footer -->
          <div class="cart-footer">
            <div class="footer-left">
              <span class="cart-count">共 <em>{{ totalCount }}</em> 件商品</span>
            </div>
            <div class="footer-right">
              <div class="summary">
                <span class="total">总价：<em class="price">¥{{ totalPrice.toFixed(2) }}</em></span>
              </div>
              <el-button type="primary" size="large" class="checkout-btn" @click="handleCheckout">结算购物车</el-button>
            </div>
          </div>
        </div>

        <!-- Empty Cart -->
        <div v-else class="empty-cart">
          <el-empty description="购物车还是空的，去挑选喜欢的商品吧~">
            <el-button type="primary" @click="$emit('toUser')">去逛逛</el-button>
          </el-empty>
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userLogout } from '../../../api/Login'
import { showCart, updateCartCount, deleteCart, deleteCartItem } from '../../../api/User'
import ProductImage from '../../../components/ProductImage.vue'

const emit = defineEmits(['toLogin', 'toRegister', 'toUser', 'toSearch', 'toDetail', 'toAccount', 'toScdp', 'toOrder'])

const searchQuery = ref('')
const userInfo = ref(null)

const cartItems = ref([])

onMounted(async () => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }
  await fetchCart()
})

const fetchCart = async () => {
  try {
    const res = await showCart()
    if (res.code === 200) {
      cartItems.value = res.data || []
    }
  } catch (error) {
    console.error('获取购物车失败:', error)
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

const handleSearch = () => {
  emit('toSearch', searchQuery.value)
}

const handleToDetail = (item) => {
  emit('toDetail', {
    id: item.productId,
    name: item.productName,
    price: item.price
  })
}

const totalCount = computed(() => {
  return cartItems.value.reduce((acc, item) => acc + parseInt(item.productCount || 1), 0)
})

const totalPrice = computed(() => {
  return cartItems.value.reduce((acc, item) => acc + parseFloat(item.price || 0) * parseInt(item.productCount || 1), 0)
})

const removeFromCart = (item) => {
  ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await deleteCartItem({
        productId: item.productId,
        userSpecification: item.userSpecification
      })
      cartItems.value = cartItems.value.filter(i =>
        !(i.productId === item.productId && i.userSpecification === item.userSpecification)
      )
      ElMessage.success('已删除')
    } catch (error) {
      console.error('删除购物车商品失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  })
}

const handleCountChange = async (item) => {
  try {
    await updateCartCount({
      productId: item.productId,
      productCount: item.productCount
    })
  } catch (error) {
    console.error('更新数量失败:', error)
    ElMessage.error('更新数量失败，请重试')
  }
}

const handleCheckout = async () => {
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }

  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    emit('toLogin')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要结算购物车吗？共 ${totalCount.value} 件商品，总计 ¥${totalPrice.value.toFixed(2)}`,
      '结算购物车',
      {
        confirmButtonText: '确定结算',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    const cartInfo = {
      items: cartItems.value,
      totalCount: totalCount.value,
      totalPrice: totalPrice.value
    }
    localStorage.setItem('orderCartInfo', JSON.stringify(cartInfo))

    emit('toOrder', { type: 'confirm' })
  } catch (error) {
    // 用户取消
  }
}
</script>

<style lang="scss" scoped>
.shop-cart-page {
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
  .cart-title { font-size: 24px; color: #333; font-weight: 600; flex-shrink: 0; }
  .search-box {
    flex: 1;
    max-width: 500px;
    margin-left: auto;
    .search-input-wrapper {
      border: 2px solid #2a9eff;
      border-radius: 8px;
      overflow: hidden;
      :deep(.el-input__wrapper) { border: none; box-shadow: none !important; }
      .search-btn { background-color: #2a9eff; border: none; color: white; padding: 0 25px; height: 100%; border-radius: 0; }
    }
  }
}

.main-content {
  padding: 20px 0 60px;
  
  .cart-table-container {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  }

  .cart-header {
    display: flex;
    align-items: center;
    background: #f8fbff;
    padding: 15px 20px;
    color: #666;
    font-size: 14px;
    border-bottom: 1px solid #edf4ff;
  }

  .cart-list {
    max-height: 600px;
    overflow-y: auto;
    
    &::-webkit-scrollbar {
      width: 6px;
    }
    
    &::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 3px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: #c1c1c1;
      border-radius: 3px;
      
      &:hover {
        background: #a8a8a8;
      }
    }
  }

  .cart-item {
    display: flex;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid #f5f9ff;
    transition: background 0.3s;
    &:hover { background: #fdfdff; }
  }

  .col-info { flex: 1; }
  .col-price { width: 120px; text-align: center; color: #333; }
  .col-num { width: 150px; text-align: center; }
  .col-total { width: 120px; text-align: center; color: #e4393c; font-weight: bold; }
  .col-action { width: 100px; text-align: center; }

  .product-info {
    display: flex;
    gap: 15px;
    cursor: pointer;
    img { width: 80px; height: 80px; border-radius: 8px; object-fit: cover; border: 1px solid #eee; }
    .details {
      display: flex;
      flex-direction: column;
      justify-content: space-around;
      .name { font-size: 14px; color: #333; font-weight: 500; line-height: 1.4; }
      .spec { font-size: 12px; color: #999; }
    }
  }

  .cart-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 30px;
    background: #fff;
    margin-top: 2px;
    border-top: 1px solid #eee;

    .footer-left { 
      display: flex; 
      align-items: center; 
      .cart-count {
        font-size: 14px;
        color: #666;
        em { 
          font-style: normal; 
          font-weight: bold; 
          color: #2a9eff;
          margin: 0 4px;
        }
      }
    }
    
    .footer-right {
      display: flex;
      align-items: center;
      gap: 30px;
      .summary {
        font-size: 14px;
        color: #666;
        em { font-style: normal; font-weight: bold; }
        .total { .price { color: #e4393c; font-size: 24px; margin-left: 10px; } }
      }
      .checkout-btn { padding: 0 40px; font-size: 18px; border-radius: 8px; }
    }
  }

  .empty-cart {
    background: #fff;
    padding: 100px 0;
    border-radius: 12px;
    text-align: center;
  }
}

.footer {
  padding: 40px 0;
  text-align: center;
  .copyright { color: #999; font-size: 13px; }
}
</style>
