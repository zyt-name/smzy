<template>
  <el-card shadow="never" class="full-height-card">
    <template #header>
      <div class="card-header">
        <div class="header-left">
          <span>商品管理</span>
        </div>
      </div>
    </template>

    <!-- 搜索区域 -->
    <div class="search-area">
      <el-input
        v-model="searchForm.name"
        placeholder="商品名称"
        style="width: 200px"
        clearable
        @keyup.enter="handleSearch"
      />
      <el-input
        v-model="searchForm.merchantId"
        placeholder="商家ID"
        style="width: 150px; margin-left: 10px"
        clearable
        @keyup.enter="handleSearch"
      />
      <el-select
        v-model="searchForm.category"
        placeholder="商品分类"
        style="width: 150px; margin-left: 10px"
        clearable
        :loading="categoryLoading"
      >
        <el-option
          v-for="category in categoryList"
          :key="category.id"
          :label="category.tagName"
          :value="category.tagName"
        />
      </el-select>
      <el-button type="primary" style="margin-left: 10px" @click="handleSearch">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
      <el-button style="margin-left: 10px" @click="handleReset">
        重置
      </el-button>
    </div>

    <div class="card-body-content">
      <div class="product-grid">
        <div v-for="product in products" :key="product.id" class="product-item">
          <div class="product-image-wrapper" @click="handleViewDetails(product)">
            <ProductImage :src="product.imageUrls" alt="" class="product-image" />
            <div class="product-status-tag">
              <el-tag :type="product.status === 0 ? 'success' : 'info'" size="small">
                {{ product.status === 0 ? '在售' : '下架' }}
              </el-tag>
            </div>
            <div class="product-admin-status-tag" v-if="product.adminStatus === 1">
              <el-tag type="danger" size="small">强制下架</el-tag>
            </div>
          </div>
          <div class="product-info" @click="handleViewDetails(product)">
            <span class="product-name" :title="product.name">{{ product.name }}</span>
            <div class="product-meta">
              <span class="product-price">¥{{ product.price }}</span>
            </div>
          </div>
          <div class="product-actions">
            <ActionButton type="primary" size="small" @click="handleViewDetails(product)">查看详情</ActionButton>
            <ActionButton type="success" size="small" @click="handleViewComments(product)">评论</ActionButton>
            <ActionButton 
              :type="product.adminStatus === 1 ? 'warning' : 'danger'" 
              size="small" 
              @click="handleToggleAdminStatus(product)"
            >
              {{ product.adminStatus === 1 ? '恢复正常' : '强制下架' }}
            </ActionButton>
          </div>
        </div>
      </div>

      <div v-if="products.length === 0" class="empty-state">
        <el-empty description="暂无商品数据" />
      </div>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="fetchProducts"
          @size-change="fetchProducts"
        />
      </div>
    </div>

    <!-- 商品详情对话框 -->
    <el-dialog
      v-model="detailsDialogVisible"
      title="商品详情"
      width="600px"
    >
      <div v-if="productDetails" class="product-details">
        <div class="details-image-wrapper">
          <ProductImage :src="productDetails.imageUrls || productDetails.image" alt="" class="details-image" />
        </div>
        <div class="details-info">
          <div class="details-row">
            <span class="details-label">商品ID：</span>
            <span class="details-value">{{ productDetails.id || productDetails.shopId }}</span>
          </div>
          <div class="details-row">
            <span class="details-label">商品名称：</span>
            <span class="details-value">{{ productDetails.name }}</span>
          </div>
          <div class="details-row">
            <span class="details-label">商品价格：</span>
            <span class="details-value price">¥{{ productDetails.price }}</span>
          </div>
          <div class="details-row">
            <span class="details-label">商家ID：</span>
            <span class="details-value">{{ productDetails.merchantId }}</span>
          </div>
          <div class="details-row">
            <span class="details-label">商品分类：</span>
            <span class="details-value">{{ productDetails.category }}</span>
          </div>
          <div class="details-row">
            <span class="details-label">商品状态：</span>
            <el-tag :type="productDetails.status === 0 ? 'success' : 'info'">
              {{ productDetails.status === 0 ? '在售' : '下架' }}
            </el-tag>
          </div>
          <div class="details-row">
            <span class="details-label">管理状态：</span>
            <el-tag :type="productDetails.adminStatus === 1 ? 'danger' : 'success'">
              {{ productDetails.adminStatus === 1 ? '强制下架' : '正常' }}
            </el-tag>
          </div>
          <div class="details-row">
            <span class="details-label">商品描述：</span>
            <span class="details-value">{{ productDetails.description || '暂无描述' }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailsDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 评论对话框 -->
    <el-dialog
      v-model="commentsDialogVisible"
      title="商品评论"
      width="800px"
      class="comments-dialog"
      destroy-on-close
      @open="handleCommentsDialogOpen"
    >
      <div class="comments-dialog-content">
        <CommentStyles
          ref="commentsRef"
          :productId="currentProductId"
          :userInfo="adminInfo"
          :isAdmin="true"
        />
      </div>
    </el-dialog>

    <!-- 强制下架/恢复正常对话框 -->
    <el-dialog
      v-model="adminStatusDialogVisible"
      :title="currentProduct?.adminStatus === 1 ? '恢复正常' : '强制下架'"
      width="500px"
    >
      <div class="admin-status-form">
        <p>商品名称：{{ currentProduct?.name }}</p>
        <p>当前状态：
          <el-tag :type="currentProduct?.adminStatus === 1 ? 'danger' : 'success'">
            {{ currentProduct?.adminStatus === 1 ? '强制下架' : '正常' }}
          </el-tag>
        </p>
        <el-input
          v-model="adminStatusReason"
          type="textarea"
          :rows="4"
          :placeholder="currentProduct?.adminStatus === 1 ? '请输入恢复正常的原因' : '请输入强制下架的原因'"
        />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="adminStatusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmToggleAdminStatus">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import ProductImage from '../../../components/ProductImage.vue'
import ActionButton from '../../../components/ActionButton.vue'
import CommentStyles from '../../../components/CommentStyles.vue'
import {
  searchShops,
  getShopDetails,
  updateShopAdminStatus
} from '../../../api/admin/AdminShop'
import { getCategoryList } from '../../../api/admin/AdminLabel'

const products = ref([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)

// 搜索表单
const searchForm = reactive({
  name: '',
  merchantId: '',
  category: ''
})

// 商品分类列表
const categoryList = ref([])
const categoryLoading = ref(false)

const detailsDialogVisible = ref(false)
const commentsDialogVisible = ref(false)
const currentProductId = ref(null)
const commentsRef = ref(null)
const productDetails = ref(null)

// 强制下架/恢复正常相关
const adminStatusDialogVisible = ref(false)
const currentProduct = ref(null)
const adminStatusReason = ref('')

// 管理员信息（用于评论组件）
const adminInfo = ref(null)

// 获取商品列表
const fetchProducts = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    // 添加动态查询条件
    if (searchForm.name) params.name = searchForm.name
    if (searchForm.merchantId) params.merchantId = searchForm.merchantId
    if (searchForm.category) params.category = searchForm.category

    const res = await searchShops(params)
    if (res.code === 200 || res.code === 1) {
      products.value = res.data?.data || []
      total.value = res.data?.total || 0
    } else {
      ElMessage.error(res.msg || '获取商品列表失败')
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchProducts()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.merchantId = ''
  searchForm.category = ''
  currentPage.value = 1
  fetchProducts()
}

// 查看商品详情
const handleViewDetails = async (product) => {
  try {
    loading.value = true
    const res = await getShopDetails(product.id)
    if (res.code === 200 || res.code === 1) {
      // 合并 API 返回的数据和列表中的 product 数据
      productDetails.value = { ...product, ...res.data }
      detailsDialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取商品详情失败')
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error('获取商品详情失败')
  } finally {
    loading.value = false
  }
}

// 查看评论
const handleViewComments = (product) => {
  currentProductId.value = product.id
  commentsDialogVisible.value = true
}

// 评论对话框打开时加载评论
const handleCommentsDialogOpen = () => {
  if (commentsRef.value && currentProductId.value) {
    commentsRef.value.fetchComments()
  }
}

// 打开强制下架/恢复正常对话框
const handleToggleAdminStatus = (product) => {
  currentProduct.value = product
  adminStatusReason.value = ''
  adminStatusDialogVisible.value = true
}

// 确认强制下架/恢复正常
const confirmToggleAdminStatus = async () => {
  if (!adminStatusReason.value.trim()) {
    ElMessage.warning('请输入操作原因')
    return
  }

  try {
    const newAdminStatus = currentProduct.value.adminStatus === 1 ? 0 : 1
    const res = await updateShopAdminStatus({
      shopId: currentProduct.value.id,
      adminStatus: newAdminStatus,
      reason: adminStatusReason.value.trim()
    })

    if (res.code === 200 || res.code === 1) {
      ElMessage.success(newAdminStatus === 1 ? '强制下架成功' : '恢复正常成功')
      // 更新本地数据
      currentProduct.value.adminStatus = newAdminStatus
      adminStatusDialogVisible.value = false
      // 刷新列表
      fetchProducts()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 获取商品分类列表
const fetchCategoryList = async () => {
  try {
    categoryLoading.value = true
    const res = await getCategoryList()
    if (res.code === 200 || res.code === 1) {
      categoryList.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取分类列表失败')
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  } finally {
    categoryLoading.value = false
  }
}

// 使用 onMounted，在组件挂载时加载数据
onMounted(() => {
  fetchProducts()
  fetchCategoryList()
  // 获取管理员信息用于评论组件
  const info = localStorage.getItem('adminInfo') || localStorage.getItem('userInfo')
  if (info) {
    adminInfo.value = JSON.parse(info)
  }
})
</script>

<style lang="scss" scoped>
.full-height-card {
  width: 100%;
  height: 100%;
  border-radius: 12px;
  border: none;
  display: flex;
  flex-direction: column;
  
  :deep(.el-card__header) {
    border-bottom: 1px solid #f0f2f5;
    padding: 15px 20px;
  }

  :deep(.el-card__body) {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
    overflow: hidden;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .header-left {
      display: flex;
      align-items: center;
      span { font-weight: bold; font-size: 16px; }
    }
  }

  .card-body-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    overflow: hidden;
  }
}

// 搜索区域
.search-area {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.product-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, 200px);
  grid-auto-rows: min-content;
  align-content: start;
  gap: 20px;
  padding: 10px 0;
  overflow-y: auto;

  .product-item {
    display: flex;
    flex-direction: column;
    background: #f8f9fb;
    border-radius: 12px;
    padding: 12px;
    transition: all 0.3s;
    cursor: pointer;
    height: fit-content;
    
    &:hover {
      background: #fff;
      box-shadow: 0 4px 12px rgba(0,0,0,0.05);
      transform: translateY(-2px);
    }

    .product-image-wrapper {
      position: relative;
      width: 100%;
      height: 150px;
      border-radius: 8px;
      overflow: hidden;
      margin-bottom: 12px;
      background: #eee;

      .product-image {
        width: 100%;
        height: 100%;
      }

      .product-status-tag {
        position: absolute;
        top: 8px;
        right: 8px;
      }

      .product-admin-status-tag {
        position: absolute;
        top: 8px;
        left: 8px;
      }
    }

    .product-info {
      flex: 1;
      margin-bottom: 12px;
      
      .product-name {
        display: block;
        font-weight: 600;
        font-size: 14px;
        color: #333;
        margin-bottom: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .product-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .product-price {
          font-size: 16px;
          font-weight: bold;
          color: #f56c6c;
        }
      }
    }

    .product-actions {
      display: flex;
      justify-content: space-between;
      padding-top: 10px;
      border-top: 1px dashed #ebeef5;
    }
  }
}

.empty-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

// 商品详情样式
.product-details {
  display: flex;
  gap: 20px;

  .details-image-wrapper {
    width: 200px;
    height: 200px;
    border-radius: 8px;
    overflow: hidden;
    background: #f5f7fa;
    flex-shrink: 0;

    .details-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .details-info {
    flex: 1;

    .details-row {
      margin-bottom: 15px;
      display: flex;
      align-items: flex-start;

      .details-label {
        color: #606266;
        font-weight: 500;
        min-width: 80px;
      }

      .details-value {
        color: #303133;
        flex: 1;

        &.price {
          color: #f56c6c;
          font-size: 18px;
          font-weight: bold;
        }
      }
    }
  }
}
</style>

<style lang="scss">
.comments-dialog {
  .el-dialog__body {
    padding: 0;
  }

  .comments-dialog-content {
    height: 500px;
    overflow-y: auto;
    padding: 20px;
  }
}

// 管理员状态表单样式
.admin-status-form {
  p {
    margin-bottom: 15px;
    font-size: 14px;
    color: #606266;
  }
}
</style>
