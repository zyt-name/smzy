<template>
  <div class="search-page">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="container">
        <div class="left">
          <span class="location">欢迎来到智码数云</span>
          <template v-if="userInfo">
            <span class="link user-name">你好，{{ userInfo.userName || userInfo.nickname || '用户' }}</span>
            <span class="link logout-btn" @click="handleLogout">退出登录</span>
          </template>
          <template v-else>
            <span class="link" @click="emit('toLogin')">请登录</span>
            <span class="link" @click="emit('toRegister')">免费注册</span>
          </template>
        </div>
        <div class="right">
          <span class="link" @click="emit('toUser')">商城首页</span>
          <span class="link" @click="emit('toAccount')">个人中心 <el-icon><ArrowDown /></el-icon></span>
          <span class="link" @click="emit('toCart')"><el-icon><ShoppingCart /></el-icon> 购物车</span>
          <span class="link" @click="emit('toScdp')">我的收藏</span>
          <span class="link" @click="emit('toOrder')">我的订单</span>
        </div>
      </div>
    </div>

    <!-- 搜索栏区域 -->
    <div class="search-header">
      <div class="container">
        <div class="brand-area" @click="emit('toUser')">
          <div class="brand-logo">智码<span>数云</span></div>
          <div class="brand-slogan">智领数码 · 云集好物</div>
        </div>
        <div class="search-main">
          <div class="search-box">
            <div class="search-input-wrapper">
              <el-input
                v-model="searchQuery"
                placeholder="搜索感兴趣的数码产品..."
                class="search-input"
                @keyup.enter="handleSearch"
              >
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
            <!-- 分类标签显示区 -->
            <div v-if="selectedCategoryTag || (selectedCategories && selectedCategories.length > 0)" class="selected-tag-wrapper">
              <template v-if="selectedCategories && selectedCategories.length > 0">
                <el-tag
                  v-for="(cat, index) in selectedCategories"
                  :key="index"
                  closable
                  type="primary"
                  @close="removeCategory(index)"
                >
                  {{ formatCategoryTag(cat) }}
                </el-tag>
              </template>
              <el-tag v-else closable type="primary" @close="clearCategoryTag">
                {{ formatCategoryTag(selectedCategoryTag) }}
              </el-tag>
            </div>
          </div>
          <!-- 搜索分类关键词 -->
          <div class="search-keywords">
            <span 
              v-for="kw in hotKeywords" 
              :key="kw" 
              class="kw-item"
              :class="{ active: currentKeyword === kw }"
              @click="handleKeywordClick(kw)"
            >
              {{ kw }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 分类标签区域 -->
    <div class="filter-section">
      <div class="container">
        <div class="category-tags">
          <ActionButton 
            v-for="(cat, index) in flattenedBrands" 
            :key="index"
            :type="selectedCategoryTag === cat.name ? 'primary' : 'default'"
            size="small"
            class="category-btn"
            @click="handleCategoryClick(cat)"
          >
            {{ cat.name }}
          </ActionButton>
        </div>
      </div>
    </div>

    <!-- 搜索结果区域 -->
    <div class="search-results">
      <div class="container">
        <!-- 商品列表 -->
        <div v-if="currentSearchType !== 'merchant'" class="product-grid">
          <div v-for="item in results" :key="item.id" class="product-card">
            <div class="p-img" @click="handleProductClick(item)">
              <ProductImage :src="item.imageUrls" alt="" class="p-img" />
            </div>
            <div class="p-info">
              <div class="p-price">
                <span class="symbol">¥</span>
                <span class="value">{{ item.price }}</span>
              </div>
              <div class="p-title" @click="handleProductClick(item)" v-html="highlightKeyword(item.name)"></div>
              <div class="p-category-row">
                <span class="p-category">{{ item.category }}</span>
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

        <!-- 商家列表 -->
        <div v-else class="merchant-grid">
          <div v-for="item in results" :key="item.id" class="merchant-card" @click="handleMerchantClick(item)">
            <div class="m-avatar">
              <img :src="merchantImg" alt="商家头像">
            </div>
            <div class="m-info">
              <div class="m-name" v-html="highlightKeyword(item.username)"></div>
              <div class="m-phone">📞 {{ item.phone || '暂无电话' }}</div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination background layout="prev, pager, next" :total="100" />
        </div>
      </div>
    </div>

    <!-- 页脚 (复用) -->
    <footer class="footer">
      <div class="container">
        <div class="footer-links">
          <span class="link">关于我们</span>
          <span class="line">|</span>
          <span class="link">联系我们</span>
          <span class="line">|</span>
          <span class="link">商家入驻</span>
          <span class="line">|</span>
          <span class="link">法律声明</span>
        </div>
        <div class="copyright">
          <p>© 2026 智码数云 版权所有</p>
          <p>增值电信业务经营许可证：浙B2-xxxxxxxx | 浙ICP备xxxxxxxx号</p>
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
import { ElMessage } from 'element-plus'
import { ArrowDown, DCaret, ShoppingCart, Service, More } from '@element-plus/icons-vue'
import { getShopList, addCart, getCategoryList, getMerchantList } from '../../../api/User'
import { userLogout } from '../../../api/Login'
import { reportProduct } from '../../../api/user/UserAppication'
import ProductImage from '../../../components/ProductImage.vue'
import ActionButton from '../../../components/ActionButton.vue'
import merchantImg from '../../../assets/merchant.png'

const props = defineProps(['initialQuery', 'searchResults', 'isFromDetail', 'category', 'categories', 'searchType'])
const emit = defineEmits(['toUser', 'toLogin', 'toRegister', 'toMerchant', 'toAdmin', 'toDetail', 'toShop', 'toShopMerchant', 'toScdp', 'toAccount', 'toCart', 'toOrder'])

const searchQuery = ref('')
const searchType = ref('product')
const currentSearchType = ref('product')
const currentKeyword = ref('全部')
const hotKeywords = ref([])
const brands = ref([])
const userInfo = ref(null)
const selectedCategoryTag = ref('')
const selectedCategories = ref([])

const results = ref([])

onMounted(async () => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }
  try {
    const categoryRes = await getCategoryList()
    if (categoryRes.code === 200) {
      brands.value = categoryRes.data || []
    }
  } catch (error) {
    console.error('获取商品分类失败:', error)
  }
  if (props.categories && props.categories.length > 0) {
    selectedCategories.value = props.categories
    selectedCategoryTag.value = ''
    await handleSearch()
    return
  }
  if (props.category) {
    selectedCategoryTag.value = props.category
    selectedCategories.value = []
    await handleSearch()
    return
  }
  if (props.isFromDetail && props.searchResults && props.searchResults.length > 0) {
    results.value = props.searchResults
    searchQuery.value = props.initialQuery || ''
    currentSearchType.value = props.searchType || 'product'
    searchType.value = props.searchType || 'product'
    return
  }
  if (props.searchResults && props.searchResults.length > 0) {
    results.value = props.searchResults
    searchQuery.value = props.initialQuery || ''
    currentSearchType.value = props.searchType || 'product'
    searchType.value = props.searchType || 'product'
  } else if (props.initialQuery && props.initialQuery !== '__from_detail__') {
    searchQuery.value = props.initialQuery
    await handleSearch()
  } else {
    await handleSearch()
  }
})

watch(() => props.searchResults, async (newVal) => {
  if (newVal && newVal.length > 0) {
    results.value = newVal
    searchQuery.value = props.initialQuery || ''
  }
}, { immediate: true })

const formatCategoryTag = (tag) => {
  return tag.replace(/ \//g, '/')
}

const getFirstCategory = (tag) => {
  return tag.split(' / ')[0]
}

const handleSearch = async () => {
  try {
    currentSearchType.value = searchType.value
    if (searchType.value === 'merchant') {
      const params = { pageNum: 1 }
      if (searchQuery.value) {
        params.username = searchQuery.value
      }
      const res = await getMerchantList(params)
      if (res.code === 200) {
        results.value = res.data?.data || []
      }
    } else {
      const params = { pageNum: 1 }
      if (selectedCategories.value && selectedCategories.value.length > 0) {
        params.categories = selectedCategories.value
      } else if (selectedCategoryTag.value) {
        params.categories = [selectedCategoryTag.value]
      }
      if (searchQuery.value) {
        params.name = searchQuery.value
      }
      const res = await getShopList(params)
      if (res.code === 200) {
        results.value = res.data?.data || []
      }
    }
  } catch (error) {
    console.error('Search error:', error)
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

const handleMerchantClick = (item) => {
  emit('toShopMerchant', { merchantId: item.id })
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

const reportDialogVisible = ref(false)
const reportForm = ref({
  targetId: null,
  reason: '',
  phone: ''
})

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

const handleKeywordClick = (kw) => {
  currentKeyword.value = kw
  if (kw === '全部') {
    searchQuery.value = ''
  } else {
    searchQuery.value = kw
  }
}

const flattenedBrands = computed(() => {
  const result = []
  brands.value.forEach(cat => {
    if (cat.tagName) {
      const parts = cat.tagName.split(' / ')
      parts.forEach(part => {
        result.push({ name: part })
      })
    }
  })
  return result
})

const handleCategoryClick = (cat) => {
  selectedCategoryTag.value = cat.name
  selectedCategories.value = []
  searchQuery.value = ''
  handleSearch()
}

const clearCategoryTag = () => {
  selectedCategoryTag.value = ''
  selectedCategories.value = []
  handleSearch()
}

const removeCategory = (index) => {
  if (selectedCategories.value && selectedCategories.value.length > index) {
    selectedCategories.value.splice(index, 1)
    if (selectedCategories.value.length === 0) {
      selectedCategories.value = []
    }
    handleSearch()
  }
}

const highlightKeyword = (title) => {
  if (!searchQuery.value) return title
  const reg = new RegExp(`(${searchQuery.value})`, 'gi')
  return title.replace(reg, '<span style="color: #ff5000">$1</span>')
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
</script>

<style lang="scss" scoped>
.search-page {
  min-height: 100vh;
  background-color: #EDF4FD;
  background-image: url('../../../assets/bj.png');
  background-repeat: no-repeat;
  background-position: center top;
  background-size: cover;
}

.container {
  width: 1200px;
  margin: 0 auto;
}

/* 顶部导航 */
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
    &.merchant-entry, &.admin-entry {
      color: #2a9eff;
      margin-left: 10px;
      font-weight: 500;
    }
  }

  .location {
    color: #003a8c;
    font-weight: 500;
  }
}

/* 搜索头部 */
.search-header {
  background-color: transparent;
  padding: 25px 0 15px;
  
  .container {
    display: flex;
    align-items: center;
  }

  .brand-area {
    width: 200px;
    cursor: pointer;
    .brand-logo {
      font-size: 28px;
      font-weight: bold;
      color: #2a9eff;
      span { color: #1a1a1a; }
    }
    .brand-slogan {
      font-size: 12px;
      color: #8c8c8c;
      margin-top: 4px;
      letter-spacing: 1px;
    }
  }

  .search-main {
    padding-left: 50px;

    .search-box {
      width: 650px;
      
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
          &:hover {
            background-color: #1a8eff;
          }
        }
      }
    }

    .search-keywords {
      margin-top: 8px;
      .kw-item {
        font-size: 12px;
        color: #666;
        margin-right: 15px;
        cursor: pointer;
        &.active { color: #2a9eff; font-weight: bold; }
        &:hover { color: #2a9eff; }
      }
    }

    .selected-tag-wrapper {
      margin-top: 10px;
      :deep(.el-tag) {
        font-size: 13px;
        padding: 4px 12px;
        border-radius: 16px;
      }
    }
  }
}

/* 分类标签区 */
.filter-section {
  background-color: transparent;
  padding: 5px 0 15px;
  
  .category-tags {
    background-color: rgba(255, 255, 255, 0.5);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    padding: 15px 20px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;

    .category-btn {
      font-size: 13px;
    }
  }
}

/* 搜索结果 */
.search-results {
  margin-top: 20px;
  padding-bottom: 50px;

  .sort-bar {
    background-color: #fff;
    padding: 10px 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-radius: 4px;
    margin-bottom: 15px;

    .sort-item {
      margin-right: 30px;
      font-size: 13px;
      color: #333;
      cursor: pointer;
      &.active { color: #2a9eff; font-weight: bold; }
      &:hover { color: #2a9eff; }
    }

    .sort-right { font-size: 12px; color: #999; }
  }

  .product-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 15px;
  }

  .merchant-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
  }

  .merchant-card {
    background-color: #fff;
    border-radius: 12px;
    padding: 25px;
    display: flex;
    align-items: center;
    gap: 20px;
    transition: transform 0.3s, box-shadow 0.3s;
    cursor: pointer;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 25px rgba(0,0,0,0.1);
      border: 2px solid #2a9eff;
    }

    .m-avatar {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      overflow: hidden;
      flex-shrink: 0;
      border: 3px solid #e8f4ff;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .m-info {
      flex: 1;
      
      .m-name {
        font-size: 18px;
        font-weight: bold;
        color: #333;
        margin-bottom: 8px;
        
        :deep(span) {
          color: #ff5000;
        }
      }

      .m-phone {
        font-size: 14px;
        color: #666;
      }
    }
  }

  .product-card {
    background-color: #fff;
    border-radius: 8px;
    overflow: hidden;
    transition: transform 0.3s, box-shadow 0.3s;
    cursor: pointer;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 20px rgba(0,0,0,0.1);
      border: 1px solid #2a9eff;
    }

    .p-img {
      height: 220px;
      img { width: 100%; height: 100%; object-fit: cover; }
    }

    .p-info {
      padding: 12px;
      
      .p-price {
        color: #ff5000;
        margin-bottom: 8px;
        .symbol { font-size: 14px; }
        .value { font-size: 20px; font-weight: bold; }
      }

      .p-title {
        font-size: 13px;
        color: #333;
        line-height: 1.4;
        height: 36px;
        overflow: hidden;
        margin-bottom: 8px;
      }

      .p-category-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 8px;

        .p-category {
          font-size: 12px;
          color: #999;
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
            width: 28px;
            height: 28px;
            cursor: pointer;
            border-radius: 4px;
            &:hover {
              background-color: #f0f0f0;
            }
          }
        }
      }
    }
  }

  .pagination {
    margin-top: 40px;
    display: flex;
    justify-content: center;
  }
}

/* 页脚 */
.footer {
  background-color: #fff;
  padding: 40px 0;
  border-top: 1px solid #eee;
  text-align: center;

  .footer-links {
    margin-bottom: 20px;
    .link { cursor: pointer; color: #666; font-size: 14px; &:hover { color: #2a9eff; } }
    .line { margin: 0 15px; color: #eee; }
  }

  .copyright {
    color: #999;
    font-size: 12px;
    line-height: 1.8;
  }
}
</style>