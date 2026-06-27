<template>
  <div class="digital-home">
    <!-- Top Nav -->
    <div class="top-nav">
      <div class="container">
        <div class="left">
          <span class="location">欢迎来到智码数云</span>
          <template v-if="userInfo">
            <span class="link user-name">{{ userInfo.userName }}</span>
            <span class="link logout" @click="handleLogout">退出登录</span>
          </template>
          <template v-else>
            <span class="link" @click="emit('toLogin')">请登录</span>
            <span class="link" @click="emit('toRegister')">免费注册</span>
          </template>
        </div>
        <div class="right">
          <span class="link" @click="emit('toUser')">商城首页</span>
          <span class="link" @click="emit('toAccount')">个人中心</span>
          <span class="link" @click="emit('toCart')"><el-icon><ShoppingCart /></el-icon> 购物车</span>
          <span class="link" @click="emit('toScdp')">我的收藏</span>
          <span class="link" @click="emit('toOrder')">我的订单</span>
          <span class="divider">|</span>
          <span class="link">网站导航</span>
        </div>
      </div>
    </div>

    <!-- Header Area -->
    <div class="header-area container">
      <div class="logo">
        <div class="brand-logo">智码<span>数云</span></div>
        <div class="brand-slogan">智领数码 · 云集好物</div>
      </div>
      <div class="search-box">
        <div class="search-input-wrapper">
          <el-input v-model="searchText" placeholder="搜索 4090 显卡、MacBook Pro、智能家居">
            <template #prepend>
              <el-select v-model="searchType" style="width: 80px">
                <el-option label="产品" value="product" />
                <el-option label="商家" value="merchant" />
              </el-select>
            </template>
            <template #append>
              <el-button type="primary" class="search-btn" @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="main-content container">
      <div class="content-top">
        <!-- Sidebar -->
        <div class="sidebar">
          <div class="sidebar-header">数码分类</div>
          <div class="category-tags">
            <ActionButton 
              v-for="(item, index) in categoryList" 
              :key="index"
              type="default" 
              size="small" 
              class="category-btn"
              @click="handleCategoryClick(item)"
            >
              {{ item }}
            </ActionButton>
          </div>
        </div>

        <!-- Banner & Right Cards -->
        <div class="banner-section">
          <el-carousel height="400px" v-if="bannerList.length > 0">
            <el-carousel-item v-for="banner in bannerList" :key="banner.id">
              <div class="banner-item banner-dynamic" @click="handleBannerClick(banner)">
                <ProductImage :src="banner.imagePath" alt="banner" class="banner-image-full" />
                <div class="banner-hover-title">{{ banner.title }}</div>
              </div>
            </el-carousel-item>
          </el-carousel>
          <el-carousel height="400px" v-else>
            <el-carousel-item v-for="i in 4" :key="i">
              <div class="banner-item" :class="'bg-' + i">
                <div class="banner-content">
                  <div class="tag">新品上市</div>
                  <h2>全新一代<br>智能数码体验</h2>
                  <p>极速性能，未来科技触手可及</p>
                  <el-button type="primary" size="large" class="banner-btn">立即探索</el-button>
                </div>
                <div class="banner-img-placeholder">
                  <el-icon v-if="i===1" size="200" color="rgba(255,255,255,0.2)"><Monitor /></el-icon>
                  <el-icon v-if="i===2" size="200" color="rgba(255,255,255,0.2)"><Iphone /></el-icon>
                  <el-icon v-if="i===3" size="200" color="rgba(255,255,255,0.2)"><Cpu /></el-icon>
                  <el-icon v-if="i===4" size="200" color="rgba(255,255,255,0.2)"><Headset /></el-icon>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>

        <!-- Right Side Cards -->
        <div class="right-cards">
          <div class="promo-card blue" @click="handlePromoCardClick('顶级配件')">
            <div class="card-info">
              <h3>顶级配件</h3>
              <p>性能怪兽</p>
            </div>
            <el-icon size="40"><Cpu /></el-icon>
          </div>
          <div class="promo-card cyan" @click="handlePromoCardClick('智能穿戴')">
            <div class="card-info">
              <h3>智能穿戴</h3>
              <p>时尚科技</p>
            </div>
            <el-icon size="40"><Watch /></el-icon>
          </div>
          <div class="promo-card dark-blue" @click="handlePromoCardClick('极速存储')">
            <div class="card-info">
              <h3>极速存储</h3>
              <p>秒传体验</p>
            </div>
            <el-icon size="40"><Box /></el-icon>
          </div>
          <div class="promo-card purple" @click="handlePromoCardClick('电竞外设')">
            <div class="card-info">
              <h3>电竞外设</h3>
              <p>致胜关键</p>
            </div>
            <el-icon size="40"><Mouse /></el-icon>
          </div>
        </div>
      </div>

      <!-- Recommendation Section -->
      <div class="recommendation">
        <div class="section-title">
          <el-icon color="#2a9eff"><VideoCamera /></el-icon> 精选数码
          <span class="sub-title">科技改变生活</span>
        </div>
        <div class="product-grid">
          <div class="product-card" v-for="item in productList" :key="item.id">
            <div class="product-img" @click="handleProductClick(item)">
              <ProductImage :src="item.imageUrls" alt="product" class="product-img" />
              <div class="img-overlay">查看详情</div>
            </div>
            <div class="product-info">
              <div class="product-tag" v-if="item.isSelf">自营</div>
              <p class="product-name" @click="handleProductClick(item)">{{ item.name }}</p>
              <p class="product-category">{{ item.category }}</p>
              <div class="price-row">
                <div class="price">
                  <span class="symbol">¥</span>
                  <span class="value">{{ item.price }}</span>
                </div>
                <div class="action-buttons">
                  <ActionButton type="orange" size="medium" class="add-cart-btn" @click.stop="handleAddCart(item)">+</ActionButton>
                  <ActionButton type="primary" size="small" @click.stop="handleToShop(item)">进店</ActionButton>
                  <el-dropdown trigger="click" @command="(cmd) => handleProductCommand(cmd, item)" @click.stop>
                    <span class="more-actions" @click.stop>
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
        </div>
      </div>
    </div>

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
          <div class="footer-auth">
            <div class="auth-item">可信网站</div>
            <div class="auth-item">网络警署</div>
            <div class="auth-item">隐私保护</div>
            <div class="auth-item">消费维权</div>
          </div>
        </div>
      </div>
    </footer>

    <!-- Side Toolbar -->
    <div 
      class="side-toolbar" 
      :class="{ 'collapsed': isSidebarCollapsed, 'is-dragging': isDragging }"
      :style="{ top: sidebarTop }"
      @mousedown="handleMouseDown"
    >
      <div class="tool-item ai-assistant">
        <div class="ai-circle-wrapper">
          <div class="ai-circle">
            <img :src="aiIcon" alt="AI" />
          </div>
          <div class="ai-glow"></div>
        </div>
      </div>

      <template v-if="!isSidebarCollapsed">
        <div class="tool-item" @click="emit('toAccount')">
          <el-icon><User /></el-icon>
          <span>账号</span>
        </div>
        <div class="tool-item" @click="emit('toCart')">
          <el-icon><ShoppingCart /></el-icon>
          <span>购物</span>
        </div>
        <div class="tool-item back-top" @click="scrollToTop">
          <el-icon><ArrowUp /></el-icon>
          <span>顶部</span>
        </div>
      </template>

      <div class="tool-item toggle-btn" @click.stop="isSidebarCollapsed = !isSidebarCollapsed">
        <el-icon>
          <DArrowRight v-if="!isSidebarCollapsed" />
          <DArrowLeft v-else />
        </el-icon>
      </div>
    </div>

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
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { userLogout } from '../../api/Login'
import { getShopList, addCart, getRecommendProducts, getCategoryList, getMerchantList } from '../../api/User'
import { reportProduct } from '../../api/user/UserAppication'
import { getBannerList, incrementBannerClick } from '../../api/user/UserBanner'
import aiIcon from '../../assets/ai.png'
import ProductImage from '../../components/ProductImage.vue'
import ActionButton from '../../components/ActionButton.vue'

const emit = defineEmits(['toLogin', 'toRegister', 'toSearch', 'toDetail', 'toCart', 'toAccount', 'toScdp', 'toOrder'])

const searchText = ref('')
const searchType = ref('product')
const userInfo = ref(null)
const isSidebarCollapsed = ref(false)
const productList = ref([])
const bannerList = ref([])
const sidebarTop = ref(localStorage.getItem('sidebarTop') || 'calc(100vh - 400px)')
const isDragging = ref(false)
const reportDialogVisible = ref(false)
const reportForm = ref({
  targetId: null,
  reason: '',
  phone: ''
})
let startY = 0
let startTop = 0

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleMouseDown = (e) => {
  // 只有点击非按钮区域或者特定拖拽区域才触发拖拽，这里我们让整个侧边栏背景可拖拽，但排除掉具体的点击事件
  if (e.target.closest('.tool-item') && !e.target.closest('.ai-assistant')) {
    // 如果点击的是普通功能按钮，不触发拖拽
    return
  }
  isDragging.value = true
  startY = e.clientY
  const rect = document.querySelector('.side-toolbar').getBoundingClientRect()
  startTop = rect.top
  
  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('mouseup', handleMouseUp)
  
  // 防止文本选中
  e.preventDefault()
}

const handleMouseMove = (e) => {
  if (!isDragging.value) return
  const deltaY = e.clientY - startY
  let newTop = startTop + deltaY
  
  // 边界检查
  const vh = window.innerHeight
  const toolbarHeight = document.querySelector('.side-toolbar').offsetHeight
  if (newTop < 20) newTop = 20
  if (newTop > vh - toolbarHeight - 20) newTop = vh - toolbarHeight - 20
  
  sidebarTop.value = `${newTop}px`
}

const handleMouseUp = () => {
  if (isDragging.value) {
    isDragging.value = false
    localStorage.setItem('sidebarTop', sidebarTop.value)
    window.removeEventListener('mousemove', handleMouseMove)
    window.removeEventListener('mouseup', handleMouseUp)
  }
}

onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('mouseup', handleMouseUp)
})

onMounted(() => {
  const savedUserInfo = localStorage.getItem('userInfo')
  if (savedUserInfo) {
    userInfo.value = JSON.parse(savedUserInfo)
  }
  fetchProductList()
  fetchCategoryList()
  fetchBannerList()
})

const fetchProductList = async () => {
  try {
    const res = await getRecommendProducts(1, 20)
    if (res.code === 200) {
      productList.value = res.data?.data || []
    }
  } catch (error) {
    console.error('获取推荐商品列表失败:', error)
  }
}

const handleLogout = async () => {
  try {
    await userLogout()
  } catch (error) {
    console.error('Logout API error:', error)
  } finally {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    userInfo.value = null
    ElMessage.success('已退出登录')
    window.location.href = window.location.origin
  }
}

const handleSearch = async () => {
  try {
    if (searchType.value === 'merchant') {
      const res = await getMerchantList({ username: searchText.value, pageNum: 1 })
      if (res.code === 200) {
        emit('toSearch', { 
          query: searchText.value, 
          searchType: 'merchant',
          results: res.data?.data || [], 
          total: res.data?.count || 0 
        })
      }
    } else {
      const res = await getShopList({ name: searchText.value, pageNum: 1 })
      if (res.code === 200) {
        emit('toSearch', { query: searchText.value, results: res.data?.data || [], total: res.data?.total || 0 })
      }
    }
  } catch (error) {
    console.error('Search error:', error)
  }
}

const handleCategoryClick = (categoryName) => {
  emit('toSearch', { categories: [categoryName], results: [], total: 0 })
}

const handlePromoCardClick = (cardType) => {
  const categoryKeywordsMap = {
    '顶级配件': ['显卡', 'CPU', '主板', '核心硬件', '内存', '电源', '机箱'],
    '智能穿戴': ['手表', '手环', '智能穿戴', '智能手表', '运动手环'],
    '极速存储': ['存储设备', '固态硬盘', 'NAS', '硬盘', 'U盘', '存储'],
    '电竞外设': ['外设产品', '键盘', '鼠标', '音箱', '耳机', '显示器', '电竞']
  }
  
  const keywords = categoryKeywordsMap[cardType] || []
  const matchedCategories = keywords.filter(keyword => 
    categoryList.value.some(cat => cat && cat.includes(keyword))
  )
  
  if (matchedCategories.length > 0) {
    emit('toSearch', { categories: matchedCategories, results: [], total: 0 })
  }
}

const categoryList = ref([])

const fetchCategoryList = async () => {
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      categoryList.value = res.data?.map(item => item.tagName) || []
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

const fetchBannerList = async () => {
  try {
    const res = await getBannerList()
    if (res.code === 200) {
      bannerList.value = res.data || []
    }
  } catch (error) {
    console.error('获取 Banner 列表失败:', error)
  }
}

const handleBannerClick = async (banner) => {
  try {
    await incrementBannerClick(banner.id)
    const url = window.location.origin + '?page=detail&shopId=' + banner.goodsId
    window.open(url, '_blank')
  } catch (error) {
    console.error('增加点击率失败:', error)
  }
}

const handleProductClick = (item) => {
  const url = window.location.origin + '?page=detail&shopId=' + item.id
  window.open(url, '_blank')
}

const handleToShop = (item) => {
  const merchantId = item.merchantId || 1
  const url = window.location.origin + '?page=shop&merchantId=' + merchantId
  window.open(url, '_blank')
}

const handleAddCart = async (item) => {
  try {
    const res = await addCart({
      productId: item.id,
      productCount: 1,
      userSpecification: '默认规格'
    })
    if (res.code === 200) {
      ElMessage.success('已加入购物车')
    }
  } catch (error) {
    console.error('加入购物车失败:', error)
    ElMessage.error('加入购物车失败')
  }
}

const handleProductCommand = (command, item) => {
  if (command === 'report') {
    reportForm.value = {
      targetId: item.id,
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

const categories = [
  { name: '电脑整机 / 笔记本 / 工作站', icon: 'Monitor' },
  { name: '智能手机 / 通讯设备 / 5G终端', icon: 'Iphone' },
  { name: '核心硬件 / 显卡 / CPU / 主板', icon: 'Cpu' },
  { name: '智能穿戴 / 手表 / 手环', icon: 'Watch' },
  { name: '外设产品 / 键盘 / 鼠标 / 音箱', icon: 'Mouse' },
  { name: '显示设备 / 显示器 / 投影仪', icon: 'VideoCamera' },
  { name: '存储设备 / 固态硬盘 / NAS', icon: 'Box' },
  { name: '数码影音 / 耳机 / 麦克风', icon: 'Headset' },
  { name: '办公设备 / 打印机 / 扫描仪', icon: 'Printer' },
  { name: '智能家居 / 安防监控', icon: 'House' },
  { name: '电竞文化 / 游戏主机', icon: 'Coordinate' },
]
</script>

<style lang="scss" scoped>
.digital-home {
  background-color: #EDF4FD;
  background-image: url('../../assets/bj.png');
  background-repeat: no-repeat;
  background-position: center top;
  background-size: cover;
  min-height: 100vh;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  position: relative;
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
    transition: color 0.3s;
    &:hover {
      color: #2a9eff;
    }
  }

  .location {
    color: #003a8c;
    font-weight: 500;
  }
}

.header-area {
  padding: 30px 0;
  display: flex;
  align-items: center;
  gap: 100px;

  .logo {
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
      }
      :deep(.el-input__wrapper) {
        border: none;
        box-shadow: none !important;
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

    .hot-keywords {
      margin-top: 8px;
      font-size: 12px;
      color: #888;
      display: flex;
      gap: 15px;
      span {
        cursor: pointer;
        &:hover {
          color: #2a9eff;
        }
      }
    }
  }
}

.main-content {
  .content-top {
    display: flex;
    gap: 15px;
    min-height: 400px;
  }

  .sidebar {
    width: 230px;
    height: 400px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .sidebar-header {
      padding: 20px;
      font-weight: bold;
      font-size: 16px;
      border-bottom: 1px solid #f5f5f5;
      color: #1a1a1a;
      flex-shrink: 0;
    }

    .category-tags {
      padding: 15px;
      display: flex;
      flex-wrap: wrap;
      align-content: flex-start;
      gap: 8px;
      overflow-y: auto;
      flex: 1;

      &::-webkit-scrollbar {
        width: 4px;
      }
      &::-webkit-scrollbar-track {
        background: transparent;
      }
      &::-webkit-scrollbar-thumb {
        background: rgba(42, 158, 255, 0.3);
        border-radius: 2px;
      }
      &::-webkit-scrollbar-thumb:hover {
        background: rgba(42, 158, 255, 0.5);
      }

      .category-btn {
        font-size: 13px;
      }
    }
  }

  .banner-section {
    flex: 1;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);

    .banner-item {
      position: relative;
      height: 100%;
      display: flex;
      align-items: center;
      padding: 0 60px;
      
      &.bg-1 { background: linear-gradient(135deg, #2a9eff 0%, #0056b3 100%); }
      &.bg-2 { background: linear-gradient(135deg, #00c6ff 0%, #0072ff 100%); }
      &.bg-3 { background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%); }
      &.bg-4 { background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%); }

      .banner-content {
        color: white;
        z-index: 1;
        max-width: 400px;

        .tag {
          background: rgba(255,255,255,0.2);
          display: inline-block;
          padding: 4px 12px;
          border-radius: 4px;
          font-size: 14px;
          margin-bottom: 20px;
          border: 1px solid rgba(255,255,255,0.3);
        }

        h2 {
          font-size: 42px;
          margin: 0;
          line-height: 1.2;
          font-weight: 700;
        }

        p {
          font-size: 18px;
          margin: 20px 0 30px;
          opacity: 0.8;
        }

        .banner-btn {
          border-radius: 4px;
          padding: 12px 35px;
          background: #fff;
          color: #2a9eff;
          border: none;
          font-weight: bold;
          &:hover {
            background: #f0f0f0;
          }
        }
      }

      .banner-img-placeholder {
        position: absolute;
        right: 40px;
        opacity: 0.5;
      }
    }

    .banner-dynamic {
      cursor: pointer;
      overflow: hidden;
      
      .banner-image {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        object-fit: cover;
        opacity: 0.3;
      }
      
      .banner-image-full {
        width: 100%;
        height: 100%;
        object-fit: fill;
        display: block;
      }
      
      .banner-hover-title {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        padding: 20px 30px;
        background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
        color: #fff;
        font-size: 18px;
        font-weight: 600;
        opacity: 0;
        transform: translateY(10px);
        transition: all 0.3s ease;
      }
      
      &:hover .banner-hover-title {
        opacity: 1;
        transform: translateY(0);
      }
      
      .banner-content {
        position: relative;
        z-index: 2;
      }
    }
  }

  .right-cards {
    width: 220px;
    display: grid;
    grid-template-rows: repeat(4, 1fr);
    gap: 12px;

    .promo-card {
      border-radius: 8px;
      padding: 15px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      color: white;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        transform: translateX(-5px);
        box-shadow: 0 4px 10px rgba(0,0,0,0.1);
      }

      &.blue { background: #2a9eff; }
      &.cyan { background: #00bcd4; }
      &.dark-blue { background: #3f51b5; }
      &.purple { background: #9c27b0; }

      .card-info {
        h3 { font-size: 16px; margin: 0; }
        p { font-size: 12px; margin: 5px 0 0; opacity: 0.8; }
      }
    }
  }
}

.recommendation {
  margin-top: 40px;
  .section-title {
    font-size: 24px;
    font-weight: 800;
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 25px;
    color: #1a1a1a;
    .sub-title {
      font-size: 14px;
      color: #999;
      font-weight: normal;
      margin-left: 10px;
    }
  }

  .product-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 20px;
    padding-bottom: 60px;
  }

  .product-card {
    background: white;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid #f0f0f0;

    &:hover {
      box-shadow: 0 15px 30px rgba(0,0,0,0.08);
      transform: translateY(-8px);
      border-color: #2a9eff;
      .img-overlay { opacity: 1; }
      .product-name { color: #2a9eff; }
    }

    .product-img {
      position: relative;
      width: 100%;
      height: 220px;
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      .img-overlay {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        background: rgba(42, 158, 255, 0.9);
        color: white;
        text-align: center;
        padding: 8px 0;
        font-size: 12px;
        opacity: 0;
        transition: opacity 0.3s;
      }
    }

    .product-info {
      padding: 15px;

      .product-tag {
        display: inline-block;
        background-color: #2a9eff;
        color: white;
        font-size: 10px;
        padding: 2px 6px;
        border-radius: 2px;
        margin-bottom: 8px;
      }

      .product-name {
        font-size: 14px;
        color: #333;
        margin: 0;
        line-height: 1.5;
        height: 42px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        transition: color 0.3s;
      }

      .product-category {
        font-size: 12px;
        color: #999;
        margin: 5px 0 0;
        line-height: 1.4;
      }

      .price-row {
        margin-top: 12px;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .price {
          display: flex;
          align-items: baseline;
          gap: 2px;
          .symbol { font-size: 12px; color: #ff4d4f; font-weight: bold; }
          .value { font-size: 22px; color: #ff4d4f; font-weight: bold; }
        }

        .action-buttons {
          display: flex;
          gap: 8px;
          align-items: center;
          .add-cart-btn {
            font-size: 18px;
            font-weight: bold;
          }
          .more-actions {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 24px;
            height: 24px;
            cursor: pointer;
            color: #999;
            transition: color 0.3s;
            &:hover {
              color: #2a9eff;
            }
          }
        }
      }
    }
  }
}

.side-toolbar {
  position: fixed;
  right: 15px;
  background: white;
  border-radius: 25px; /* 增大圆角 */
  box-shadow: 0 4px 25px rgba(0,0,0,0.12); /* 加深阴影 */
  display: flex;
  flex-direction: column;
  z-index: 100;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1), border-radius 0.3s, background 0.3s, box-shadow 0.3s;
  cursor: grab;
  border: 1px solid rgba(42, 158, 255, 0.1); /* 淡淡的边框 */

  &.is-dragging {
    cursor: grabbing;
    transition: none;
    box-shadow: 0 12px 40px rgba(0,0,0,0.18);
  }

  &.collapsed {
    border-radius: 30px;
  }

  .tool-item {
    width: 50px;
    height: 52px; /* 稍微增加高度 */
    background: #fff;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-bottom: 1px solid #f5f5f5;
    transition: all 0.3s;
    position: relative;
    z-index: 2;
    
    &:first-child {
      border-top-left-radius: 25px;
      border-top-right-radius: 25px;
    }

    &:last-child {
      border-bottom-left-radius: 25px;
      border-bottom-right-radius: 25px;
      border-bottom: none;
    }

    &:hover {
      background: #f0f7ff;
      color: #2a9eff;
      
      .el-icon {
        transform: scale(1.15);
      }
    }

    .el-icon {
      font-size: 22px;
      transition: transform 0.3s;
    }

    span {
      font-size: 11px;
      margin-top: 3px;
    }

    &.ai-assistant {
      height: 75px;
      border-bottom: none;
      background: transparent;
      overflow: visible;
      margin-top: -12px;
      cursor: pointer;
      z-index: 10;
      
      .ai-circle-wrapper {
        position: relative;
        width: 70px;
        height: 70px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
      
      .ai-circle {
        width: 64px;
        height: 64px;
        background: #fff;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4px 15px rgba(42, 158, 255, 0.25);
        transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        overflow: hidden;
        border: 2px solid #2a9eff;
        position: relative;
        z-index: 2;
        
        img {
          width: 85%;
          height: 85%;
          object-fit: contain;
          transition: transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); /* 添加平滑过渡动画 */
        }
      }

      .ai-glow {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 76px;
        height: 76px;
        background: radial-gradient(circle, rgba(42, 158, 255, 0.45) 0%, rgba(42, 158, 255, 0) 70%);
        border-radius: 50%;
        z-index: 1;
        animation: ai-pulse 2s infinite;
      }
      
      &:hover {
        background: transparent;
        .ai-circle {
          transform: scale(1.1) rotate(5deg);
          box-shadow: 0 6px 20px rgba(42, 158, 255, 0.45);
          border-width: 3px;
          
          img {
            transform: scale(1.2); /* 机器人图标放大 */
          }
        }
      }
    }

    &.toggle-btn {
      color: #999;
      height: 42px;
      border-top: 1px solid #f5f5f5;
      
      &:hover {
        color: #2a9eff;
      }
    }
  }

  .back-top {
    border-bottom: 1px solid #f5f5f5;
  }
}

.footer {
  background-color: #fff;
  margin-top: 60px;
  padding: 60px 0 30px;
  border-top: 1px solid #e1eaf2;

  .footer-links {
    display: flex;
    justify-content: space-between;
    padding-bottom: 40px;
    border-bottom: 1px solid #f0f5fa;

    .link-group {
      h4 {
        font-size: 16px;
        color: #333;
        margin-bottom: 20px;
        font-weight: 600;
      }
      ul {
        list-style: none;
        padding: 0;
        margin: 0;
        li {
          font-size: 14px;
          color: #666;
          margin-bottom: 10px;
          cursor: pointer;
          transition: color 0.3s;
          &:hover {
            color: #2a9eff;
          }
        }
      }
    }
  }

  .footer-bottom {
    padding-top: 30px;
    text-align: center;
    
    .copyright {
      font-size: 14px;
      color: #999;
      margin-bottom: 10px;
    }

    .icp-info {
      font-size: 12px;
      color: #b0b0b0;
      margin-bottom: 20px;
      .divider {
        margin: 0 10px;
      }
      .link {
        cursor: pointer;
        &:hover {
          color: #2a9eff;
        }
      }
    }

    .footer-auth {
      display: flex;
      justify-content: center;
      gap: 15px;
      .auth-item {
        padding: 5px 15px;
        border: 1px solid #e0e0e0;
        color: #ccc;
        font-size: 12px;
        border-radius: 4px;
      }
    }
  }
}
</style>
