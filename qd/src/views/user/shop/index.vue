<template>
  <div class="product-detail-page">
    <!-- Top Navigation -->
    <nav class="top-nav">
      <div class="container">
        <div class="left">
          <span class="link" @click="handleNavClick('toUser')">商城首页</span>
        </div>
        <div class="right">
          <template v-if="userInfo">
            <span class="link user-name">你好，{{ userInfo.userName || userInfo.nickname || '用户' }}</span>
            <span class="link logout-btn" @click="handleLogout">退出登录</span>
          </template>
          <template v-else>
            <span class="link" @click="handleNavClick('toLogin')">你好，请登录</span>
            <span class="link" @click="handleNavClick('toRegister')">免费注册</span>
          </template>
          <span class="divider">|</span>
          <span class="link" @click="handleNavClick('toAccount')">个人中心</span>
          <span class="link" @click="handleNavClick('toCart')">购物车</span>
          <span class="link" @click="handleNavClick('toScdp')">我的收藏</span>
          <span class="link" @click="handleNavClick('toOrder')">我的订单</span>
        </div>
      </div>
    </nav>

    <!-- Header Area -->
    <header class="header-area">
      <div class="container">
        <div class="logo" @click="handleNavClick('toUser')">
          <div class="brand-logo">智码<span>数云</span></div>
          <div class="brand-slogan">智慧连接 · 码动未来</div>
        </div>
        
        <div class="search-box">
          <div class="search-input-wrapper">
            <el-input 
              v-model="searchQuery" 
              placeholder="搜索感兴趣的数码产品..."
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

    <!-- Main Content -->
    <main class="main-content">
      <div class="container">
        <!-- Breadcrumb -->
        <el-breadcrumb separator="/" class="breadcrumb">
          <el-breadcrumb-item @click="handleNavClick('toUser')">首页</el-breadcrumb-item>
          <el-breadcrumb-item v-if="!isStandalone" @click="handleBackToSearch">搜索</el-breadcrumb-item>
          <el-breadcrumb-item>商品详情</el-breadcrumb-item>
        </el-breadcrumb>

        <!-- Back Button -->
        <div class="back-button-wrapper" v-if="searchQuery && !isStandalone">
          <el-button 
            type="primary" 
            plain 
            class="back-btn"
            @click="handleBackToSearch"
          >
            <el-icon><ArrowLeft /></el-icon>
            返回搜索页
          </el-button>
        </div>

        <div class="product-intro" v-if="productDetail && productDetail.shopId">
          <!-- Left: Product Image -->
          <div class="preview-wrap">
            <div class="main-img">
              <ProductImage :src="productDetail.imageUrls" alt="product" class="main-img" />
            </div>
          </div>

          <!-- Right: Product Info -->
          <div class="itemInfo-wrap">
            <h1 class="product-title">{{ productDetail.name }}</h1>
            <p class="product-subtitle">{{ productDetail.description }}</p>
            
            <div class="summary">
              <div class="summary-price">
                <div class="dt">商城价</div>
                <div class="dd">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ productDetail.price || '--' }}</span>
                </div>
              </div>
              <div class="product-labels" v-if="productDetail.label && productDetail.label.length > 0">
                <span class="label-tag" v-for="(tag, index) in productDetail.label" :key="index">{{ tag }}</span>
              </div>
            </div>

            <div class="action-row">
              <div class="product-meta">
                <div class="meta-item">
                  <span class="label">分类：</span>
                  <span class="value">{{ productDetail.category }}</span>
                </div>
              </div>
              <div class="cart-control">
                <el-input-number v-model="quantity" :min="1" />
                <el-button class="add-cart-btn" @click="addToCart">加入购物车</el-button>
                <el-button class="buy-now-btn" @click="buyNow">立即购买</el-button>
                <el-button class="enter-shop-btn" @click="handleToShop">进店逛逛</el-button>
                <el-dropdown trigger="click" @command="handleProductCommand">
                  <span class="more-actions">
                    <el-icon><More /></el-icon>
                  </span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="report">举报商品</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </div>
        </div>
        <div v-else-if="loading" class="loading-product">
          <el-skeleton :rows="10" animated />
        </div>
        <div v-else class="empty-product">
          <el-empty description="暂无商品信息" />
        </div>

        <!-- Product Tabs -->
        <div class="product-detail-tabs" v-if="productDetail && productDetail.shopId">
          <el-tabs v-model="activeTab" type="border-card">
            <el-tab-pane label="商品详情" name="detail">
              <div class="detail-text">
                <h3>商品描述</h3>
                <p>{{ productDetail.description || '暂无描述' }}</p>
              </div>
            </el-tab-pane>
            <el-tab-pane label="规格参数" name="specs">
              <div class="specs-table" v-if="productDetail.specification">
                <div class="spec-row" v-for="(spec, index) in parseSpecification(productDetail.specification)" :key="index">
                  <div class="spec-label">{{ spec.label }}</div>
                  <div class="spec-value">{{ spec.value }}</div>
                </div>
              </div>
              <el-empty v-else description="暂无规格参数" />
            </el-tab-pane>
            <el-tab-pane label="累计评价" name="comments">
              <CommentStyles 
                ref="commentsRef"
                :productId="productDetail?.shopId" 
                :userInfo="userInfo"
                @login="handleNavClick('toLogin')"
              />
            </el-tab-pane>
          </el-tabs>
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
      title="举报商品"
      width="500px"
      destroy-on-close
    >
      <el-form :model="reportForm" label-width="100px">
        <el-form-item label="商品ID">
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
        <el-button type="primary" @click="handleReportProduct">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { userLogout } from '../../../api/Login'
import { getShopDetails, addCart } from '../../../api/User'
import { reportProduct } from '../../../api/user/UserAppication'
import ProductImage from '../../../components/ProductImage.vue'
import CommentStyles from '../../../components/CommentStyles.vue'

const props = defineProps({
  product: {
    type: Object,
    default: () => ({})
  },
  searchQuery: {
    type: String,
    default: ''
  },
  shopId: {
    type: [String, Number],
    default: null
  },
  isStandalone: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['toLogin', 'toRegister', 'toMerchant', 'toAdmin', 'toUser', 'toSearch', 'toShop', 'toCart', 'toAccount', 'toScdp', 'toOrder'])

const searchQuery = ref('')
const searchType = ref('product')
const quantity = ref(1)
const activeTab = ref('detail')
const userInfo = ref(null)
const productDetail = ref(null)
const loading = ref(false)
const reportDialogVisible = ref(false)
const reportForm = ref({
  targetId: null,
  reason: '',
  phone: ''
})

// 评论组件引用
const commentsRef = ref(null)

const fetchProductDetail = async (shopId) => {
  if (!shopId) return
  loading.value = true
  try {
    const res = await getShopDetails(shopId)
    if (res.code === 200) {
      productDetail.value = res.data
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
  } finally {
    loading.value = false
  }
}

const getUrlParam = (name) => {
  const urlParams = new URLSearchParams(window.location.search)
  return urlParams.get(name)
}

onMounted(() => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }
  const urlShopId = getUrlParam('shopId')
  if (urlShopId) {
    fetchProductDetail(urlShopId)
  } else if (props.shopId) {
    fetchProductDetail(props.shopId)
  } else if (props.product?.id) {
    fetchProductDetail(props.product.id)
  }
})

watch(() => props.product, (newVal) => {
  if (newVal?.id) {
    fetchProductDetail(newVal.id)
  }
}, { immediate: true })

watch(() => props.shopId, (newVal) => {
  if (newVal) {
    fetchProductDetail(newVal)
  }
}, { immediate: true })

const handleNavClick = (action) => {
  if (props.isStandalone) {
    const pageMap = {
      'toUser': '',
      'toLogin': 'page=login',
      'toRegister': 'page=register',
      'toAccount': 'page=account',
      'toCart': 'page=shopCart',
      'toScdp': 'page=account&tab=favorite',
      'toOrder': 'page=account&tab=order'
    }
    const param = pageMap[action]
    if (param !== undefined) {
      window.location.href = window.location.origin + (param ? '?' + param : '')
    }
  } else {
    emit(action)
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
  if (props.isStandalone) {
    window.location.href = window.location.origin + '?page=search&query=' + encodeURIComponent(searchQuery.value)
  } else {
    emit('toSearch', searchQuery.value)
  }
}

const handleBackToSearch = () => {
  if (props.isStandalone) {
    window.location.href = window.location.origin + '?page=search'
  } else {
    emit('toSearch', '__from_detail__')
  }
}

const handleToShop = () => {
  const merchantId = productDetail.value?.merchantId || 1
  const url = window.location.origin + '?page=shop&merchantId=' + merchantId
  window.open(url, '_blank')
}

const parseSpecification = (specStr) => {
  if (!specStr) return []
  
  // 尝试解析JSON格式
  if (typeof specStr === 'string') {
    try {
      const parsed = JSON.parse(specStr)
      if (Array.isArray(parsed)) {
        return parsed.filter(item => item.label && item.value).map(item => ({
          label: item.label,
          value: item.value
        }))
      }
    } catch (e) {
      // 不是JSON格式，继续其他解析方式
    }
  }
  
  // 尝试多种分隔符组合
  let items = []
  
  // 优先尝试换行符分隔
  if (specStr.includes('\n')) {
    items = specStr.split('\n').map(s => s.trim()).filter(s => s)
  }
  // 尝试中文分号分隔
  else if (specStr.includes('；')) {
    items = specStr.split('；').map(s => s.trim()).filter(s => s)
  }
  // 尝试英文分号分隔
  else if (specStr.includes(';')) {
    items = specStr.split(';').map(s => s.trim()).filter(s => s)
  }
  // 尝试逗号分隔（需要同时包含冒号或等号）
  else if (specStr.includes(',') && (specStr.includes(':') || specStr.includes('：') || specStr.includes('='))) {
    items = specStr.split(',').map(s => s.trim()).filter(s => s)
  }
  // 单个规格项
  else {
    items = [specStr.trim()]
  }
  
  // 解析每个规格项
  return items.map(item => {
    // 尝试中文冒号
    if (item.includes('：')) {
      const parts = item.split('：')
      if (parts.length >= 2) {
        return {
          label: parts[0].trim(),
          value: parts.slice(1).join('：').trim()
        }
      }
    }
    // 尝试英文冒号
    else if (item.includes(':')) {
      const parts = item.split(':')
      if (parts.length >= 2) {
        return {
          label: parts[0].trim(),
          value: parts.slice(1).join(':').trim()
        }
      }
    }
    // 尝试等号
    else if (item.includes('=')) {
      const parts = item.split('=')
      if (parts.length >= 2) {
        return {
          label: parts[0].trim(),
          value: parts.slice(1).join('=').trim()
        }
      }
    }
    // 尝试空格分隔（第一个空格作为分隔符）
    else if (item.includes(' ')) {
      const firstSpace = item.indexOf(' ')
      return {
        label: item.substring(0, firstSpace).trim(),
        value: item.substring(firstSpace + 1).trim()
      }
    }
    
    return null
  }).filter(Boolean)
}

const addToCart = async () => {
  if (!productDetail.value?.shopId) {
    ElMessage.warning('商品信息加载中，请稍后')
    return
  }
  try {
    await addCart({
      merchantId: productDetail.value.merchantId,
      productId: productDetail.value.shopId,
      productCount: quantity.value,
      userSpecification: productDetail.value.specification || ''
    })
    ElMessage.success('已成功加入购物车！')
  } catch (error) {
    console.error('添加购物车失败:', error)
    ElMessage.error('添加购物车失败，请重试')
  }
}

const buyNow = async () => {
  if (!productDetail.value?.shopId) {
    ElMessage.warning('商品信息加载中，请稍后')
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
      `确定购买 ${quantity.value} 个 ${productDetail.value.name}？`,
      '确认购买',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    const orderProductInfo = {
      product: {
        id: productDetail.value.shopId,
        name: productDetail.value.name,
        price: productDetail.value.price,
        imageUrl: productDetail.value.imageUrls,
        specification: productDetail.value.specification,
        merchantId: productDetail.value.merchantId
      },
      quantity: quantity.value
    }
    localStorage.setItem('orderProductInfo', JSON.stringify(orderProductInfo))

    if (props.isStandalone) {
      window.location.href = window.location.origin + '?page=order&type=confirm'
    } else {
      emit('toOrder', { type: 'confirm' })
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

// 监听标签页变化，切换到评论时加载评论
watch(activeTab, (newVal) => {
  if (newVal === 'comments' && productDetail.value?.shopId && commentsRef.value) {
    commentsRef.value.fetchComments()
  }
})

const handleProductCommand = (command) => {
  if (command === 'report') {
    reportForm.value = {
      targetId: productDetail.value?.shopId,
      reason: '',
      phone: ''
    }
    reportDialogVisible.value = true
  }
}

const handleReportProduct = async () => {
  if (!reportForm.value.reason.trim()) {
    ElMessage.warning('请输入举报理由')
    return
  }
  if (!reportForm.value.phone.trim()) {
    ElMessage.warning('请输入联系电话')
    return
  }
  try {
    const res = await reportProduct(reportForm.value)
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
.product-detail-page {
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
    &:hover {
      color: #2a9eff;
    }
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
      span {
        color: #2a9eff;
      }
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
        .el-select {
          margin: 0;
          .el-input__wrapper {
            box-shadow: none !important;
            border: none !important;
            background-color: transparent;
          }
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

.main-content {
  padding: 20px 0 60px;

  .breadcrumb {
    margin-bottom: 10px;
    :deep(.el-breadcrumb__item) {
      cursor: pointer;
      &:hover {
        color: #2a9eff;
      }
    }
    .back-to-search {
      color: #2a9eff;
      font-weight: 500;
      &:hover {
        text-decoration: underline;
      }
    }
  }

  .back-button-wrapper {
    margin-bottom: 15px;
    .back-btn {
      display: flex;
      align-items: center;
      gap: 5px;
      padding: 10px 20px;
      font-size: 14px;
      .el-icon {
        font-size: 16px;
      }
    }
  }

  .product-intro {
    background: #fff;
    padding: 30px;
    border-radius: 8px;
    display: flex;
    gap: 50px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);

    .preview-wrap {
      width: 450px;
      .main-img {
        width: 450px;
        height: 450px;
        border: 1px solid #eee;
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
      .spec-list {
        margin-top: 20px;
        display: flex;
        gap: 15px;
        .spec-item {
          width: 70px;
          height: 70px;
          border: 2px solid transparent;
          cursor: pointer;
          &.active {
            border-color: #2a9eff;
          }
          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }
      }
    }

    .itemInfo-wrap {
      flex: 1;
      min-width: 0;
      .product-title {
        font-size: 24px;
        color: #333;
        margin-bottom: 15px;
        font-weight: 600;
        word-break: break-all;
        overflow-wrap: break-word;
      }
      .product-subtitle {
        font-size: 14px;
        color: #ff4400;
        margin-bottom: 25px;
        line-height: 1.6;
        word-break: break-all;
        overflow-wrap: break-word;
        display: -webkit-box;
        -webkit-line-clamp: 3;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .summary {
        padding: 20px;
        margin-bottom: 25px;

        .summary-price, .summary-promotion {
          display: flex;
          align-items: center;
          margin-bottom: 15px;
          .dt { width: 70px; color: #999; font-size: 13px; }
        }

        .summary-price {
          .price-symbol { color: #e4393c; font-size: 18px; margin-right: 5px; }
          .price-value { color: #e4393c; font-size: 32px; font-weight: bold; }
          .price-notice { color: #005aa0; font-size: 12px; margin-left: 15px; cursor: pointer; }
        }

        .product-labels {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
          margin-top: 10px;
          .label-tag {
            padding: 3px 10px;
            font-size: 12px;
            color: #2a9eff;
            background: rgba(42, 158, 255, 0.1);
            border: 1px solid #2a9eff;
            border-radius: 4px;
            white-space: nowrap;
          }
        }

        .summary-promotion {
          margin-bottom: 0;
          .tag { 
            background: #e4393c; color: #fff; padding: 2px 6px; 
            border-radius: 2px; font-size: 12px; margin-right: 10px;
          }
          .text { color: #666; font-size: 13px; }
        }
      }

      .action-row {
        display: flex;
        flex-direction: column;
        gap: 15px;
        margin: 20px 0;
        padding: 15px 0;
        border-bottom: 1px solid #f0f0f0;

        .product-meta {
          display: block;
          .meta-item {
            font-size: 14px;
            margin-bottom: 8px;
            .label { color: #999; }
            .value { color: #333; margin-left: 5px; }
          }
        }

        .cart-control {
          display: flex;
          gap: 15px;
          align-items: center;
          justify-content: space-between;
        }
      }

      .choose-attrs {
        .choose-item {
          display: flex;
          align-items: center;
          margin-bottom: 20px;
          .dt { width: 70px; color: #999; font-size: 13px; }
          .dd {
            display: flex;
            gap: 10px;
            .attr-btn {
              border: 1px solid #ccc;
              padding: 8px 20px;
              font-size: 13px;
              color: #666;
              cursor: pointer;
              background: #fff;
              &:hover { border-color: #2a9eff; color: #2a9eff; }
              &.active { border-color: #2a9eff; color: #2a9eff; position: relative; }
            }
          }
        }
      }

      .cart-control {
        display: flex;
        gap: 15px;
        align-items: center;
        .add-cart-btn, .buy-now-btn, .enter-shop-btn {
          padding: 12px 35px;
          font-size: 16px;
          font-weight: bold;
          background: transparent;
          border: none;
          position: relative;
          z-index: 1;
          overflow: hidden;
          border-radius: 16px;
          transition: color 0.3s ease;
          &::before {
            content: '';
            position: absolute;
            top: 50%;
            left: 50%;
            width: 0;
            height: 0;
            border-radius: 50%;
            transform: translate(-50%, -50%);
            transition: width 0.4s ease, height 0.4s ease;
            z-index: -1;
          }
        }
        .add-cart-btn {
           color: #67c23a;
           &::before {
             background: rgba(103, 194, 58, 0.2);
           }
           &:hover {
             color: #529b2e;
             &::before {
               width: 150%;
               height: 300%;
             }
           }
         }
         .buy-now-btn {
           color: #e6a23c;
           &::before {
             background: rgba(230, 162, 60, 0.2);
           }
           &:hover {
             color: #cf9236;
             &::before {
               width: 150%;
               height: 300%;
             }
           }
          }
          .enter-shop-btn {
           color: #89c4ff;
           &::before {
             background: rgba(137, 196, 255, 0.2);
           }
            &:hover {
             color: #2a9eff;
              &::before {
               width: 150%;
               height: 300%;
              }
            }
          }
        .more-actions {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 40px;
          height: 40px;
          cursor: pointer;
          color: #999;
          transition: color 0.3s;
          border-radius: 50%;
          &:hover {
            color: #2a9eff;
            background: rgba(42, 158, 255, 0.1);
          }
        }
      }

      .service-promise {
        font-size: 13px;
        color: #999;
        span { margin-right: 20px; }
      }
    }
  }

  .product-detail-tabs {
    margin-top: 30px;
    :deep(.el-tabs--border-card) {
      border: none;
      box-shadow: 0 2px 12px rgba(0,0,0,0.05);
      border-radius: 8px;
      overflow: hidden;
      .el-tabs__header {
        background-color: #f5f7fa;
        border-bottom: 1px solid #eee;
      }
    }

    .detail-content {
      padding: 20px;
      text-align: center;
      img { max-width: 100%; margin-bottom: 20px; }
      .detail-text {
        text-align: left;
        padding: 20px;
        h3 { margin-bottom: 15px; color: #333; }
        p { color: #666; line-height: 2; font-size: 14px; }
      }
    }

    .specs-table {
      padding: 20px;
      .spec-row {
        display: flex;
        border-bottom: 1px solid #eee;
        &:last-child { border-bottom: none; }
        .spec-label { width: 200px; padding: 15px; background: #f9f9f9; color: #999; font-size: 14px; }
        .spec-value { flex: 1; padding: 15px; color: #666; font-size: 14px; }
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
