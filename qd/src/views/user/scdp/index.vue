<template>
  <div class="user-account-page">
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
          <span class="link" @click="$emit('toScdp')">我的收藏</span>
          <span class="link" @click="$emit('toOrder')">我的订单</span>
          <span class="divider">|</span>
          <span class="link">网站导航</span>
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
        <div class="page-title">账号管理</div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main-content container">
      <div class="account-container">
        <!-- Sidebar Tabs -->
        <div class="account-sidebar">
          <div 
            class="sidebar-item" 
            @click="emit('toAccount', 'profile')"
          >
            <el-icon><User /></el-icon> 个人资料
          </div>
          <div 
            class="sidebar-item" 
            @click="emit('toAccount', 'address')"
          >
            <el-icon><Location /></el-icon> 收货地址
          </div>
          <div 
            class="sidebar-item active"
          >
            <el-icon><Star /></el-icon> 收藏店铺
          </div>
        </div>

        <!-- Content Area -->
        <div class="account-content">
          <div class="content-section">
            <h3 class="section-title">收藏的店铺</h3>
            
            <div v-if="favoriteShops.length > 0" class="shop-list" v-loading="loading">
              <div v-for="shop in favoriteShops" :key="shop.id" class="shop-card">
                <div class="shop-main">
                  <div class="shop-logo">
                    <img :src="shop.logo" :alt="shop.name">
                  </div>
                  <div class="shop-info">
                    <h4 class="shop-name">{{ shop.name }}</h4>
                    <div class="shop-meta">
                      <span class="shop-phone">电话: {{ shop.phone }}</span>
                      <span class="shop-status" :class="shop.status === 2 ? 'active' : 'inactive'">
                        {{ shop.status === 2 ? '营业中' : '已歇业' }}
                      </span>
                    </div>
                    <div class="shop-addresses" v-if="shop.addressList && shop.addressList.length > 0">
                      <span v-for="(addr, index) in shop.addressList" :key="index" class="shop-address">
                        {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}
                      </span>
                    </div>
                  </div>
                </div>
                <div class="shop-actions">
                  <el-button type="primary" plain @click="goToShop(shop)">进入店铺</el-button>
                  <ActionButton type="danger" size="medium" @click="cancelFavorite(shop.id)">取消收藏</ActionButton>
                </div>
              </div>
            </div>
            
            <el-empty v-else description="暂无收藏的店铺" />
          </div>
        </div>
      </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <p class="copyright">Copyright © 2024-2026 智码数云 zhishuyun.com 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Location, Star } from '@element-plus/icons-vue'
import { getFavoriteMerchantList, deleteFavoriteMerchant } from '../../../api/User'
import { userLogout as logoutApi } from '../../../api/Login'
import ActionButton from '../../../components/ActionButton.vue'

const emit = defineEmits(['toLogin', 'toRegister', 'toUser', 'toAccount', 'toShop', 'toScdp', 'toOrder'])

const userInfo = ref(null)
const favoriteShops = ref([])
const loading = ref(false)

onMounted(() => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }
  fetchFavoriteList()
})

const fetchFavoriteList = async () => {
  loading.value = true
  try {
    const res = await getFavoriteMerchantList(1, 100)
    if (res.data && res.data.data) {
      favoriteShops.value = res.data.data.map(item => ({
        id: item.merchantId,
        name: item.merchantName,
        phone: item.phone,
        status: item.status,
        addressList: item.addressList || [],
        logo: 'https://picsum.photos/100/100?random=' + item.merchantId
      }))
    } else {
      favoriteShops.value = []
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

const handleLogout = async () => {
  try {
    await logoutApi()
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

const goToShop = (shop) => {
  emit('toShop', shop)
}

const cancelFavorite = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏该店铺吗？', '提示', {
      type: 'warning'
    })
    await deleteFavoriteMerchant(id)
    favoriteShops.value = favoriteShops.value.filter(item => item.id !== id)
    ElMessage.success('已取消收藏')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消收藏失败')
    }
  }
}
</script>

<style lang="scss" scoped>
.user-account-page {
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
  
  .account-container {
    display: flex;
    gap: 20px;
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0,0,0,0.05);
    min-height: 600px;
  }

  .account-sidebar {
    width: 240px;
    background: #f8fbff;
    padding: 20px 0;
    border-right: 1px solid #edf4ff;

    .sidebar-item {
      padding: 15px 30px;
      font-size: 15px;
      color: #666;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 10px;
      transition: all 0.3s;
      &:hover { color: #2a9eff; background: #f0f7ff; }
      &.active {
        color: #2a9eff;
        background: #e6f1ff;
        border-right: 3px solid #2a9eff;
        font-weight: 600;
      }
    }
  }

  .account-content {
    flex: 1;
    padding: 30px 40px;

    .section-title {
      font-size: 20px;
      color: #333;
      margin-bottom: 30px;
      font-weight: 600;
    }

    .shop-list {
      display: flex;
      flex-direction: column;
      gap: 20px;
    }

    .shop-card {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;
      border: 1px solid #eee;
      border-radius: 8px;
      transition: all 0.3s;
      
      &:hover {
        border-color: #2a9eff;
        box-shadow: 0 4px 12px rgba(42, 158, 255, 0.1);
      }

      .shop-main {
        display: flex;
        gap: 20px;
        align-items: center;
      }

      .shop-logo {
        width: 80px;
        height: 80px;
        border-radius: 8px;
        overflow: hidden;
        border: 1px solid #f0f0f0;
        img { width: 100%; height: 100%; object-fit: cover; }
      }

      .shop-info {
        .shop-name { margin: 0 0 8px; font-size: 18px; color: #333; }
        .shop-meta {
          display: flex;
          align-items: center;
          gap: 15px;
          margin-bottom: 8px;
          .shop-phone { font-size: 13px; color: #666; }
          .shop-status {
            font-size: 12px;
            padding: 2px 8px;
            border-radius: 4px;
            &.active { background: #e6f9e6; color: #3dc33d; }
            &.inactive { background: #f5f5f5; color: #999; }
          }
        }
        .shop-addresses {
          display: flex;
          flex-direction: column;
          gap: 4px;
          .shop-address { font-size: 12px; color: #999; }
        }
      }

      .shop-actions {
        display: flex;
        flex-direction: column;
        gap: 10px;
        align-items: flex-end;
      }
    }
  }
}

.footer {
  padding: 40px 0;
  text-align: center;
  .copyright { color: #999; font-size: 13px; }
}
</style>
