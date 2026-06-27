<template>
  <div class="shop-merchant-page">
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
          <span class="link" @click="$emit('toCart')">购物车</span>
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
        
        <div class="search-box">
          <div class="search-input-wrapper">
            <el-input 
              v-model="searchQuery" 
              placeholder="在店内搜索..."
              @keyup.enter="handleSearch"
            >
              <template #prepend>
                <el-select v-model="searchType" style="width: 80px">
                  <el-option label="产品" value="product" />
                  <el-option label="资讯" value="news" />
                  <el-option label="服务" value="service" />
                </el-select>
              </template>
              <template #append>
                <el-button type="primary" class="search-btn" @click="handleSearch">搜索</el-button>
              </template>
            </el-input>
          </div>
        </div>
      </div>
    </header>

    <!-- Shop Banner -->
    <div class="shop-banner">
      <div class="container">
        <div class="shop-info-card">
          <div class="shop-logo">
            <img :src="merchantImg" alt="shop logo">
          </div>
          <div class="shop-detail">
            <h1 class="shop-name">{{ merchantInfo?.merchantName || '-' }}</h1>
            <div class="shop-meta">
              <span class="phone">联系电话: {{ merchantInfo?.phone || '-' }}</span>
              <span class="status" :class="merchantInfo?.status === 0 ? 'active' : 'inactive'">
                {{ merchantInfo?.status === 0 ? '营业中' : '已歇业' }}
              </span>
            </div>
            <div class="shop-addresses" v-if="merchantInfo?.addressList && merchantInfo.addressList.length > 0">
              <span v-for="(addr, index) in merchantInfo.addressList" :key="index" class="address-item">
                {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}
              </span>
            </div>
          </div>
          <div class="shop-action">
            <span class="favorite-status" @click="handleAddFavorite">
              <span :class="isFavorited ? 'star favorited' : 'star'">★</span>
              <span class="favorite-text">{{ isFavorited ? '已收藏该店铺' : '收藏该店铺' }}</span>
            </span>
            <el-button class="report-btn" @click="openReportDialog">
              <el-icon><Warning /></el-icon>
              举报商家
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <main class="main-content">
      <div class="container">
        <!-- Product Sections -->
        <div class="product-section">
          <div class="section-header">
            <span class="title">店内商品</span>
            <span class="more">查看更多 ></span>
          </div>
          <div class="product-grid" v-loading="loading">
            <div class="product-card" v-for="product in shopProducts" :key="product.id" @click="handleProductClick(product)">
              <div class="product-img">
                <ProductImage :src="product.imageUrls" alt="product" />
                <div class="img-overlay">查看详情</div>
              </div>
              <div class="product-info">
                <p class="name">{{ product.name }}</p>
                <div class="price">
                  <span class="symbol">¥</span>
                  <span class="value">{{ product.price }}</span>
                </div>
              </div>
            </div>
          </div>
          <el-empty v-if="!loading && shopProducts.length === 0" description="暂无商品" />
        </div>
      </div>
    </main>

    <!-- Footer Area -->
    <footer class="footer">
      <div class="container">
        <div class="footer-links">
          <div class="link-group">
            <h4>购物指南</h4>
            <ul>
              <li>购物流程</li>
              <li>会员介绍</li>
              <li>生活旅行</li>
              <li>常见问题</li>
            </ul>
          </div>
          <div class="link-group">
            <h4>配送方式</h4>
            <ul>
              <li>上门自提</li>
              <li>211限时达</li>
              <li>配送服务查询</li>
              <li>配送费收取标准</li>
            </ul>
          </div>
          <div class="link-group">
            <h4>支付方式</h4>
            <ul>
              <li>货到付款</li>
              <li>在线支付</li>
              <li>分期付款</li>
              <li>邮局汇款</li>
            </ul>
          </div>
          <div class="link-group">
            <h4>售后服务</h4>
            <ul>
              <li>售后政策</li>
              <li>价格保护</li>
              <li>退款说明</li>
              <li>返修/退换货</li>
            </ul>
          </div>
        </div>
        
        <div class="footer-bottom">
          <p class="copyright">Copyright © 2024-2026 智码数云 zhishuyun.com 版权所有</p>
          <p class="icp-info">
            <span class="link">京ICP备xxxxxxxx号-x</span>
            <span class="divider">|</span>
            <span class="link">京公网安备 xxxxxxxxxxxxxx号</span>
            <span class="divider">|</span>
            <span class="link">增值电信业务经营许可证：xxxxxxxx</span>
          </p>
        </div>
      </div>
    </footer>

    <el-dialog
      v-model="reportDialogVisible"
      title="举报商家"
      width="500px"
      destroy-on-close
    >
      <el-form :model="reportForm" label-width="100px">
        <el-form-item label="商家ID">
          <el-input v-model="reportForm.targetId" disabled />
        </el-form-item>
        <el-form-item label="联系电话" required>
          <el-input v-model="reportForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="举报理由" required>
          <el-input
            v-model="reportForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入举报理由"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReportMerchant">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userLogout } from '../../../api/Login'
import { addFavoriteMerchant, deleteFavoriteMerchant, checkFavorite, getMerchantInfo, getShopListByMerchant } from '../../../api/User'
import { reportMerchant } from '../../../api/user/UserAppication'
import ProductImage from '../../../components/ProductImage.vue'
import merchantImg from '../../../assets/merchant.png'

const props = defineProps({
  shop: {
    type: Object,
    default: () => ({})
  },
  merchantId: {
    type: [String, Number],
    default: null
  }
})

const emit = defineEmits(['toLogin', 'toRegister', 'toMerchant', 'toAdmin', 'toUser', 'toSearch', 'toDetail', 'toCart', 'toAccount', 'toScdp', 'toOrder'])

const searchQuery = ref('')
const searchType = ref('product')
const userInfo = ref(null)
const isFavorited = ref(false)
const merchantInfo = ref(null)
const shopProducts = ref([])
const loading = ref(false)
const reportDialogVisible = ref(false)
const reportForm = ref({
  targetId: null,
  reason: '',
  phone: ''
})

onMounted(() => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }
  fetchFavoriteStatus()
  fetchMerchantInfo()
  fetchShopProducts()
})

const fetchFavoriteStatus = async () => {
  if (!props.merchantId) return
  try {
    const res = await checkFavorite(props.merchantId)
    isFavorited.value = res.data === true
  } catch (error) {
    console.error('查询收藏状态失败:', error)
  }
}

const fetchMerchantInfo = async () => {
  if (!props.merchantId) return
  try {
    const res = await getMerchantInfo(props.merchantId)
    if (res.data) {
      merchantInfo.value = res.data
    }
  } catch (error) {
    console.error('获取商家信息失败:', error)
  }
}

const fetchShopProducts = async () => {
  if (!props.merchantId) return
  loading.value = true
  try {
    const res = await getShopListByMerchant(props.merchantId, 1, 100)
    if (res.data && res.data.data) {
      shopProducts.value = res.data.data
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
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

const handleAddFavorite = async () => {
  if (!props.merchantId) {
    ElMessage.warning('商家ID不存在')
    return
  }
  try {
    if (isFavorited.value) {
      await deleteFavoriteMerchant(props.merchantId)
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavoriteMerchant(props.merchantId)
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleSearch = () => {
  emit('toSearch', searchQuery.value)
}

const handleProductClick = (product) => {
  const url = window.location.origin + '?page=detail&shopId=' + product.id
  window.open(url, '_blank')
}

const openReportDialog = () => {
  reportForm.value = {
    targetId: props.merchantId,
    reason: '',
    phone: ''
  }
  reportDialogVisible.value = true
}

const handleReportMerchant = async () => {
  if (!reportForm.value.reason.trim()) {
    ElMessage.warning('请输入举报理由')
    return
  }
  if (!reportForm.value.phone.trim()) {
    ElMessage.warning('请输入联系电话')
    return
  }
  try {
    const res = await reportMerchant(reportForm.value)
    if (res.code === 200) {
      ElMessage.success('举报已提交')
      reportDialogVisible.value = false
    } else {
      ElMessage.error(res.msg || '举报失败')
    }
  } catch (error) {
    console.error('举报失败:', error)
    ElMessage.error('举报失败')
  }
}
</script>

<style lang="scss" scoped>
.shop-merchant-page {
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
  background: linear-gradient(
    180deg,
    rgba(176, 224, 255, 0.8) 0%,
    rgba(214, 238, 255, 0.6) 70%,
    rgba(237, 244, 253, 0) 100%
  );
  height: 52px;
  line-height: 52px;
  font-size: 13px;
  color: #0b3a67;
  position: relative;
  z-index: 10;

  .container {
    display: flex;
    justify-content: space-between;
  }

  .left, .right {
    display: flex;
    gap: 20px;
  }

  .link {
    cursor: pointer;
    &:hover { color: #2a9eff; }
    &.merchant-entry, &.admin-entry {
      color: #2a9eff;
      margin-left: 10px;
      font-weight: 500;
    }
  }
}

.header-area {
  padding: 30px 0;
  background-color: transparent;
  display: flex;
  align-items: center;

  .container {
    display: flex;
    align-items: center;
    gap: 100px;
  }

  .logo {
    cursor: pointer;
    display: flex;
    flex-direction: column;
    .brand-logo {
      font-size: 36px;
      font-weight: 800;
      color: #1a1a1a;
      letter-spacing: -1px;
      span { color: #2a9eff; }
    }
    .brand-slogan {
      color: #999;
      font-size: 14px;
      margin-top: 5px;
      letter-spacing: 2px;
    }
  }

  .search-box {
    flex: 1;
    max-width: 630px;
    
    .search-input-wrapper {
      border: 2px solid #2a9eff;
      border-radius: 8px;
      overflow: hidden;
      :deep(.el-input-group__prepend) {
        background-color: #fff;
        border: none;
        box-shadow: none !important;
        padding: 0;
        .el-select .el-input__wrapper {
          box-shadow: none !important;
          border: none !important;
          background-color: transparent;
        }
      }
      :deep(.el-input__wrapper) {
        border: none;
        box-shadow: none !important;
      }
      :deep(.el-input-group__append) {
        background-color: #2a9eff;
        border: none;
        padding: 0;
      }
      .search-btn {
        background-color: #2a9eff;
        border: none;
        color: white;
        padding: 0 30px;
        height: 100%;
        border-radius: 0;
        font-size: 16px;
      }
    }
  }
}

.shop-banner {
  background: transparent;
  padding: 40px 0;
  margin-bottom: 20px;

  .shop-info-card {
    display: flex;
    align-items: center;
    gap: 30px;

    .shop-logo {
      width: 100px;
      height: 100px;
      border-radius: 12px;
      overflow: hidden;
      border: 4px solid #fff;
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
      img { width: 100%; height: 100%; object-fit: cover; }
    }

    .shop-detail {
      flex: 1;
      .shop-name { font-size: 28px; color: #333; margin-bottom: 12px; }
      .shop-meta {
        display: flex;
        align-items: center;
        gap: 15px;
        margin-bottom: 10px;
        .phone { font-size: 14px; color: #666; }
        .status {
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
        margin-bottom: 10px;
        .address-item { font-size: 12px; color: #999; }
      }
    }

    .shop-action {
      display: flex;
      gap: 15px;
      align-items: center;

      .favorite-status {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;
        padding: 10px 20px;
        border-radius: 16px;
        transition: all 0.3s;

        .star {
          font-size: 18px;
          color: #ccc;
          transition: color 0.3s;

          &.favorited {
            color: #ffd700;
          }
        }

        .favorite-text {
          font-size: 14px;
          color: #89c4ff;
          transition: color 0.3s;
        }

        &:hover {
          background: rgba(137, 196, 255, 0.1);
        }
      }

      .collect-btn, .report-btn {
        padding: 10px 20px;
        font-size: 14px;
        background: transparent;
        border: none;
        position: relative;
        z-index: 1;
        overflow: hidden;
        border-radius: 16px;
        transition: color 0.3s ease;
        color: #ff9f43;
        &::before {
          content: '';
          position: absolute;
          top: 50%;
          left: 50%;
          width: 0;
          height: 0;
          background: rgba(255, 159, 67, 0.2);
          border-radius: 50%;
          transform: translate(-50%, -50%);
          transition: width 0.4s ease, height 0.4s ease;
          z-index: -1;
        }
        &:hover {
          color: #ff7f00;
          &::before {
            width: 150%;
            height: 300%;
          }
        }
      }
    }
  }
}

.main-content {
  padding-bottom: 60px;

  .shop-nav {
    display: flex;
    background: #fff;
    border-radius: 8px;
    margin-bottom: 20px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);

    .nav-item {
      padding: 15px 40px;
      font-size: 16px;
      color: #666;
      cursor: pointer;
      transition: all 0.3s;
      &:hover, &.active {
        background: #2a9eff;
        color: #fff;
      }
    }
  }

  .product-section {
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      .title { font-size: 22px; font-weight: bold; color: #333; }
      .more { font-size: 14px; color: #999; cursor: pointer; &:hover { color: #2a9eff; } }
    }

    .product-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;

      .product-card {
        background: #fff;
        border-radius: 12px;
        overflow: hidden;
        transition: all 0.3s;
        cursor: pointer;
        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 10px 25px rgba(0,0,0,0.1);
          .img-overlay { opacity: 1; }
        }

        .product-img {
          height: 280px;
          position: relative;
          img { width: 100%; height: 100%; object-fit: cover; }
          .img-overlay {
            position: absolute;
            top: 0; left: 0; right: 0; bottom: 0;
            background: rgba(42, 158, 255, 0.4);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 16px;
            opacity: 0;
            transition: opacity 0.3s;
          }
        }

        .product-info {
          padding: 15px;
          .name {
            font-size: 14px;
            color: #333;
            margin-bottom: 10px;
            height: 40px;
            overflow: hidden;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
          }
          .price {
            margin-bottom: 5px;
            .symbol { color: #e4393c; font-size: 14px; }
            .value { color: #e4393c; font-size: 20px; font-weight: bold; }
          }
          .sales { font-size: 12px; color: #999; }
        }
      }
    }
  }
}

.footer {
  background-color: #fff;
  padding: 60px 0 30px;
  border-top: 1px solid #eee;

  .footer-links {
    display: flex;
    justify-content: space-around;
    padding-bottom: 40px;
    border-bottom: 1px solid #f0f0f0;

    .link-group {
      h4 { font-size: 16px; color: #333; margin-bottom: 20px; }
      ul {
        list-style: none;
        padding: 0;
        li { font-size: 13px; color: #666; margin-bottom: 10px; cursor: pointer; &:hover { color: #2a9eff; } }
      }
    }
  }

  .footer-bottom {
    padding-top: 30px;
    text-align: center;
    .copyright { color: #999; font-size: 13px; margin-bottom: 10px; }
    .icp-info {
      color: #ccc;
      font-size: 12px;
      .link { cursor: pointer; &:hover { color: #2a9eff; } }
      .divider { margin: 0 10px; }
    }
  }
}
</style>
