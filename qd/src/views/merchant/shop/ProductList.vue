<template>
  <el-card shadow="never" class="full-height-card">
    <template #header>
      <div class="card-header">
        <div class="header-left">
          <span>商品管理</span>
          <el-input
            v-model="searchQuery"
            placeholder="搜索商品名称"
            style="width: 200px; margin-left: 20px"
            prefix-icon="Search"
            clearable
          />
        </div>
        <div class="header-right">
          <ActionButton type="success" size="xlarge" @click="addProductDialogVisible = true">
            添加商品
          </ActionButton>
        </div>
      </div>
    </template>

    <div class="card-body-content">
      <div class="product-grid">
        <div v-for="product in filteredProducts" :key="product.id" class="product-item">
          <div class="product-image-wrapper" @click="handleViewDetails(product)">
            <ProductImage :src="product.image || product.imageUrls" alt="" class="product-image" />
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
            <div class="product-labels">
              <template v-if="product.label && parseProductLabels(product.label).length > 0">
                <el-tag
                  v-for="(tag, index) in parseProductLabels(product.label)"
                  :key="index"
                  size="small"
                  type="primary"
                  class="product-label-tag"
                >
                  {{ tag }}
                </el-tag>
              </template>
              <el-tag v-else size="small" type="info" class="product-label-tag empty-label">
                未设置标签
              </el-tag>
            </div>
            <div class="product-meta">
              <span class="product-price">¥{{ product.price }}</span>
            </div>
          </div>
          <div class="product-actions">
            <ActionButton type="primary" size="small" @click="handleEditProduct(product)">编辑</ActionButton>
            <ActionButton 
              :type="product.status === 0 ? 'orange' : 'success'" 
              size="small" 
              @click="handleToggleStatus(product)"
            >
              {{ product.status === 0 ? '下架' : '上架' }}
            </ActionButton>
            <ActionButton type="danger" size="small" @click="handleDeleteProduct(product)">删除</ActionButton>
            <ActionButton type="success" size="small" @click="handleViewComments(product)">评论</ActionButton>
            <ActionButton 
              v-if="product.adminStatus === 1" 
              type="orange" 
              size="small" 
              @click="openRestoreDialog(product)"
            >
              申请恢复
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
          :page-sizes="[14, 28, 42, 56]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="fetchProducts"
          @size-change="fetchProducts"
        />
      </div>
    </div>

    <!-- 编辑商品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑商品"
      width="600px"
    >
      <el-form :model="productForm" label-width="90px">
        <el-form-item label="商品名称">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="productForm.price" :precision="2" :step="0.1" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品分类">
          <el-select v-model="productForm.category" placeholder="请选择分类" style="width: 100%">
            <el-option 
              v-for="item in categoryOptions" 
              :key="item.id" 
              :label="item.tagName" 
              :value="item.tagName" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="productForm.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
        <el-form-item label="商品规格">
          <el-input v-model="productForm.specification" placeholder="请输入商品规格" />
        </el-form-item>
        <div class="label-tip-row">选择相对应的商品标签可以提高商品曝光度哦！</div>
        <el-form-item label="商品标签">
          <div class="label-selector">
            <div class="label-section">
              <span class="section-title">一级标签：</span>
              <div class="tag-list">
                <span
                  v-for="item in level1Options"
                  :key="item.id"
                  :class="['tag-option', { active: selectedLevel1 === item.id }]"
                  @click="handleLevel1Click(item)"
                >
                  {{ item.tagName }}
                </span>
              </div>
            </div>
            <div class="label-section">
              <span class="section-title">二级标签：</span>
              <div class="tag-list">
                <span
                  v-for="item in level2Options"
                  :key="item.id"
                  :class="['tag-option', { active: selectedLevel2 === item.id, disabled: !selectedLevel1 }]"
                  @click="handleLevel2Click(item)"
                >
                  {{ item.tagName }}
                </span>
              </div>
            </div>
            <div class="label-section">
              <span class="section-title">三级标签：</span>
              <div class="tag-list">
                <span
                  v-for="item in level3Options"
                  :key="item.id"
                  :class="['tag-option', { active: selectedLevel3 === item.id, disabled: !selectedLevel2 }]"
                  @click="handleLevel3Click(item)"
                >
                  {{ item.tagName }}
                </span>
              </div>
            </div>
            <div class="selected-tags" v-if="selectedTags.length > 0">
              <span class="section-title">已选标签：</span>
              <el-tag
                v-for="(tag, index) in selectedTags"
                :key="index"
                closable
                @close="handleRemoveTag(index)"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
            </div>
            <!-- 显示原有标签（如果用户没有选择新标签） -->
            <div class="original-tags" v-if="originalLabelTags.length > 0 && selectedTags.length === 0">
              <span class="section-title">原有标签（未修改）：</span>
              <el-tag
                v-for="(tag, index) in originalLabelTags"
                :key="index"
                type="info"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="商品图片">
          <el-upload
            class="product-uploader"
            action="#"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleImageChange"
          >
            <ProductImage v-if="productForm.imageUrls" :src="productForm.imageUrls" class="uploaded-image" />
            <div v-else class="uploader-placeholder">
              <el-icon class="uploader-icon"><Plus /></el-icon>
              <span>点击上传图片</span>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditForm" :loading="loading">确定</el-button>
        </span>
      </template>
    </el-dialog>

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
            <span class="details-label">商品名称：</span>
            <span class="details-value">{{ productDetails.name }}</span>
          </div>
          <div class="details-row">
            <span class="details-label">商品价格：</span>
            <span class="details-value price">¥{{ productDetails.price }}</span>
          </div>
          <div class="details-row">
            <span class="details-label">商品分类：</span>
            <span class="details-value">{{ productDetails.category }}</span>
          </div>
          <div class="details-row">
            <span class="details-label">商品标签：</span>
            <div class="details-tags">
              <el-tag
                v-for="(tag, index) in productDetails.label || []"
                :key="index"
                size="small"
                type="primary"
                class="detail-tag"
              >
                {{ tag }}
              </el-tag>
              <span v-if="!productDetails.label || productDetails.label.length === 0" class="details-value">暂无标签</span>
            </div>
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

    <!-- 添加商品对话框 -->
    <el-dialog
      v-model="addProductDialogVisible"
      width="1300px"
      class="add-product-dialog"
      :show-title="false"
      destroy-on-close
    >
      <AddProduct @product-added="handleAddProductSuccess" @close="addProductDialogVisible = false" />
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
          :userInfo="merchantInfo"
          :isMerchant="true"
        />
      </div>
    </el-dialog>

    <!-- 申请恢复商品悬浮窗 -->
    <el-dialog
      v-model="restoreDialogVisible"
      title="申请恢复商品"
      width="500px"
      destroy-on-close
    >
      <el-form :model="restoreForm" label-width="100px">
        <el-form-item label="商品名称">
          <el-input :value="restoreForm.productName" disabled />
        </el-form-item>
        <el-form-item label="联系电话" required>
          <el-input v-model="restoreForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="申请理由" required>
          <el-input
            v-model="restoreForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入申请理由"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="restoreDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRestore" :loading="restoreSubmitting">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, computed, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ProductImage from '../../../components/ProductImage.vue'
import ActionButton from '../../../components/ActionButton.vue'
import CommentStyles from '../../../components/CommentStyles.vue'
import { Search } from '@element-plus/icons-vue'
import AddProduct from './AddProduct.vue'
import { 
  getShopByMerchantId, 
  updateShop, 
  updateShopStatus, 
  deleteShop, 
  getShopDetails 
} from '../../../api/merchant/MerchantShop'
import { applyRestoreProduct } from '../../../api/merchant/MerchantApplication'
import { uploadImage } from '../../../api/merchant/MerchantImg'
import { getTagNameList } from '../../../api/merchant/MerchantLable'
import { getCategoryList } from '../../../api/admin/AdminLabel'
import { Plus } from '@element-plus/icons-vue'

const products = ref([])
const total = ref(0)
const loading = ref(false)
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(14)

const dialogVisible = ref(false)
const detailsDialogVisible = ref(false)
const addProductDialogVisible = ref(false)
const commentsDialogVisible = ref(false)
const currentProductId = ref(null)
const commentsRef = ref(null)
const productForm = ref({
  id: null,
  name: '',
  price: 0,
  category: '',
  status: 0,
  description: '',
  specification: '',
  label: '',
  imageUrls: ''
})
const productDetails = ref(null)

// 分类相关数据
const categoryOptions = ref([])

// 获取商品分类列表
const fetchCategoryList = async () => {
  try {
    const res = await getCategoryList({})
    categoryOptions.value = res?.data || []
  } catch (error) {
    console.error('获取商品分类失败', error)
  }
}

// 标签相关数据
const level1Options = ref([])
const level2Options = ref([])
const level3Options = ref([])
const selectedLevel1 = ref(null)
const selectedLevel2 = ref(null)
const selectedLevel3 = ref(null)
const selectedTags = ref([])
const selectedTagNames = ref([])
const originalLabelTags = ref([]) // 存储原有标签
const hasLabelChanged = ref(false) // 标记标签是否被修改

// 获取一级标签
const fetchLevel1Options = async () => {
  try {
    const res = await getTagNameList({ tagType: 2, level: 1 })
    level1Options.value = res?.data || []
  } catch (error) {
    console.error('获取一级标签失败', error)
  }
}

// 获取二级标签
const fetchLevel2Options = async (parentTagId) => {
  try {
    const res = await getTagNameList({ tagType: 2, level: 2, parentTagId })
    level2Options.value = res?.data || []
  } catch (error) {
    console.error('获取二级标签失败', error)
  }
}

// 获取三级标签
const fetchLevel3Options = async (parentTagId) => {
  try {
    const res = await getTagNameList({ tagType: 2, level: 3, parentTagId })
    level3Options.value = res?.data || []
  } catch (error) {
    console.error('获取三级标签失败', error)
  }
}

// 处理一级标签点击
const handleLevel1Click = async (item) => {
  hasLabelChanged.value = true
  if (selectedLevel1.value === item.id) {
    selectedLevel1.value = null
    selectedLevel2.value = null
    selectedLevel3.value = null
    level2Options.value = []
    level3Options.value = []
  } else {
    selectedLevel1.value = item.id
    selectedLevel2.value = null
    selectedLevel3.value = null
    level3Options.value = []
    await fetchLevel2Options(item.id)
  }
  updateSelectedTags()
}

// 处理二级标签点击
const handleLevel2Click = async (item) => {
  if (!selectedLevel1.value) return
  hasLabelChanged.value = true
  if (selectedLevel2.value === item.id) {
    selectedLevel2.value = null
    selectedLevel3.value = null
    level3Options.value = []
  } else {
    selectedLevel2.value = item.id
    selectedLevel3.value = null
    await fetchLevel3Options(item.id)
  }
  updateSelectedTags()
}

// 处理三级标签点击
const handleLevel3Click = (item) => {
  if (!selectedLevel2.value) return
  hasLabelChanged.value = true
  if (selectedLevel3.value === item.id) {
    selectedLevel3.value = null
  } else {
    selectedLevel3.value = item.id
  }
  updateSelectedTags()
}

// 更新已选标签
const updateSelectedTags = () => {
  selectedTags.value = []
  selectedTagNames.value = []

  if (selectedLevel1.value) {
    const level1Item = level1Options.value.find(item => item.id === selectedLevel1.value)
    if (level1Item) {
      selectedTags.value.push(level1Item.tagName)
      selectedTagNames.value.push(level1Item.tagName)
    }
  }
  if (selectedLevel2.value) {
    const level2Item = level2Options.value.find(item => item.id === selectedLevel2.value)
    if (level2Item) {
      selectedTags.value.push(level2Item.tagName)
      selectedTagNames.value.push(level2Item.tagName)
    }
  }
  if (selectedLevel3.value) {
    const level3Item = level3Options.value.find(item => item.id === selectedLevel3.value)
    if (level3Item) {
      selectedTags.value.push(level3Item.tagName)
      selectedTagNames.value.push(level3Item.tagName)
    }
  }
  // 只有在标签改变时才更新 productForm.label
  if (hasLabelChanged.value) {
    productForm.value.label = selectedTagNames.value.length > 0 ? JSON.stringify(selectedTagNames.value) : ''
  }
}

// 移除标签
const handleRemoveTag = (index) => {
  hasLabelChanged.value = true
  if (index === 2) {
    selectedLevel3.value = null
  } else if (index === 1) {
    selectedLevel2.value = null
    selectedLevel3.value = null
    level3Options.value = []
  } else if (index === 0) {
    selectedLevel1.value = null
    selectedLevel2.value = null
    selectedLevel3.value = null
    level2Options.value = []
    level3Options.value = []
  }
  updateSelectedTags()
}

// 重置标签选择状态
const resetLabelSelection = () => {
  selectedLevel1.value = null
  selectedLevel2.value = null
  selectedLevel3.value = null
  selectedTags.value = []
  selectedTagNames.value = []
  level2Options.value = []
  level3Options.value = []
  originalLabelTags.value = []
  hasLabelChanged.value = false
}

// 解析原有标签
const parseOriginalLabel = (labelStr) => {
  originalLabelTags.value = []
  if (!labelStr) return
  try {
    const parsed = JSON.parse(labelStr)
    if (Array.isArray(parsed)) {
      originalLabelTags.value = parsed
    }
  } catch (e) {
    // 如果不是JSON格式，按逗号分割
    originalLabelTags.value = labelStr.split(',').filter(tag => tag.trim())
  }
}

// 商家信息（用于评论组件）
const merchantInfo = ref(null)

// 申请恢复相关数据
const restoreDialogVisible = ref(false)
const restoreSubmitting = ref(false)
const restoreForm = ref({
  productId: null,
  productName: '',
  phone: '',
  reason: ''
})

// 后端已分页，直接使用过滤后的数据
const filteredProducts = computed(() => {
  if (!searchQuery.value) return products.value
  return products.value.filter(p =>
    p.name && p.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

// 解析商品标签
const parseProductLabels = (labelStr) => {
  if (!labelStr) return []
  try {
    const parsed = JSON.parse(labelStr)
    if (Array.isArray(parsed)) {
      return parsed
    }
  } catch (e) {
    // 如果不是JSON格式，按逗号分割
    return labelStr.split(',').filter(tag => tag.trim())
  }
  return []
}

// 获取商品列表
const fetchProducts = async () => {
  try {
    loading.value = true
    const res = await getShopByMerchantId(currentPage.value, pageSize.value)
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

// 查看商品详情
const handleViewDetails = async (product) => {
  try {
    loading.value = true
    const res = await getShopDetails(product.id)
    if (res.code === 200 || res.code === 1) {
      // 合并 API 返回的数据和列表中的 product 数据，确保 status 等字段存在
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

// 编辑商品
const handleEditProduct = async (product) => {
  // 重置标签选择状态
  resetLabelSelection()

  productForm.value = {
    id: product.id,
    name: product.name,
    price: product.price,
    category: product.category,
    status: product.status,
    description: product.description || '',
    specification: product.specification || '',
    label: product.label || '',
    imageUrls: product.imageUrls || product.image || ''
  }

  // 解析原有标签
  parseOriginalLabel(product.label)

  // 加载分类列表
  await fetchCategoryList()

  // 加载一级标签选项
  await fetchLevel1Options()

  dialogVisible.value = true
}

// 处理图片上传
const handleImageChange = async (file) => {
  const formData = new FormData()
  formData.append('file', file.raw)
  try {
    loading.value = true
    const res = await uploadImage(formData)
    // 后端直接返回图片路径字符串
    if (res && typeof res === 'string') {
      productForm.value.imageUrls = res
      ElMessage.success('图片上传成功')
    } else if (res && (res.code === 200 || res.code === 1)) {
      productForm.value.imageUrls = res.data
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error('图片上传失败')
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败')
  } finally {
    loading.value = false
  }
}

// 提交编辑表单
const submitEditForm = async () => {
  try {
    loading.value = true
    // 构建提交数据
    const submitData = { ...productForm.value }

    // 如果标签没有被修改，则不传递 label 字段
    if (!hasLabelChanged.value) {
      delete submitData.label
    } else {
      // 如果用户清空了所有标签，传递空字符串
      submitData.label = selectedTagNames.value.length > 0 ? JSON.stringify(selectedTagNames.value) : ''
    }

    const res = await updateShop(submitData)
    if (res.code === 200 || res.code === 1) {
      ElMessage.success('更新成功')
      dialogVisible.value = false
      fetchProducts()
    } else {
      ElMessage.error(res.msg || '更新失败')
    }
  } catch (error) {
    console.error('更新商品失败:', error)
    ElMessage.error('更新商品失败')
  } finally {
    loading.value = false
  }
}

// 切换商品上下架状态
const handleToggleStatus = (product) => {
  const newStatus = product.status === 0 ? 1 : 0
  const action = newStatus === 0 ? '上架' : '下架'
  ElMessageBox.confirm(`确定要${action}该商品吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      loading.value = true
      const res = await updateShopStatus({
        id: product.id,
        status: newStatus
      })
      if (res.code === 200 || res.code === 1) {
        ElMessage.success(`${action}成功`)
        fetchProducts()
      } else {
        ElMessage.error(res.msg || `${action}失败`)
      }
    } catch (error) {
      console.error('更新商品状态失败:', error)
      ElMessage.error('更新商品状态失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}

// 删除商品
const handleDeleteProduct = (product) => {
  ElMessageBox.confirm('确定要删除该商品吗？此操作不可恢复', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'error'
  }).then(async () => {
    try {
      loading.value = true
      const res = await deleteShop(product.id)
      if (res.code === 200 || res.code === 1) {
        ElMessage.success('删除成功')
        fetchProducts()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除商品失败:', error)
      ElMessage.error('删除商品失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}

// 添加商品成功回调
const handleAddProductSuccess = () => {
  fetchProducts()
  addProductDialogVisible.value = false
  ElMessage.success('添加成功')
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

// 打开申请恢复悬浮窗
const openRestoreDialog = (product) => {
  restoreForm.value = {
    productId: product.id,
    productName: product.name,
    phone: '',
    reason: ''
  }
  restoreDialogVisible.value = true
}

// 提交申请恢复
const submitRestore = async () => {
  if (!restoreForm.value.reason.trim()) {
    ElMessage.warning('请输入申请理由')
    return
  }
  if (!restoreForm.value.phone.trim()) {
    ElMessage.warning('请输入联系电话')
    return
  }
  
  restoreSubmitting.value = true
  try {
    const res = await applyRestoreProduct({
      productId: restoreForm.value.productId,
      reason: restoreForm.value.reason,
      phone: restoreForm.value.phone
    })
    if (res.code === 200) {
      ElMessage.success('申请已提交')
      restoreDialogVisible.value = false
    } else {
      ElMessage.error(res.msg || '申请失败')
    }
  } catch (error) {
    console.error('申请失败:', error)
    ElMessage.error('申请失败')
  } finally {
    restoreSubmitting.value = false
  }
}

// 使用 onActivated 替代 onMounted，只在组件被激活时加载数据
onActivated(() => {
  fetchProducts()
  // 获取商家信息用于评论组件
  const info = localStorage.getItem('merchantInfo') || localStorage.getItem('userInfo')
  if (info) {
    merchantInfo.value = JSON.parse(info)
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
    .header-right {
      display: flex;
      align-items: center;
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
        margin-bottom: 6px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .product-labels {
        display: flex;
        flex-wrap: wrap;
        gap: 4px;
        margin-bottom: 6px;
        min-height: 20px;

        .product-label-tag {
          font-size: 10px;
        }
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

// 图片上传样式
.product-uploader {
  :deep(.el-upload) {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: border-color 0.3s;
    width: 150px;
    height: 150px;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      border-color: #409eff;
    }
  }

  .uploaded-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .uploader-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #8c939d;

    .uploader-icon {
      font-size: 28px;
      margin-bottom: 8px;
    }

    span {
      font-size: 12px;
    }
  }
}

// 标签选择器样式
.label-tip-row {
  font-size: 11px;
  color: #f56c6c;
  margin-bottom: 8px;
  padding-left: 90px;
}

.label-selector {
  .label-section {
    margin-bottom: 12px;

    .section-title {
      display: block;
      font-size: 13px;
      color: #606266;
      margin-bottom: 8px;
      font-weight: 500;
    }

    .tag-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .tag-option {
        padding: 6px 12px;
        border-radius: 4px;
        background: #f4f4f5;
        color: #606266;
        font-size: 13px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: #e6e8eb;
        }

        &.active {
          background: #409eff;
          color: #fff;
        }

        &.disabled {
          opacity: 0.5;
          cursor: not-allowed;

          &:hover {
            background: #f4f4f5;
          }
        }
      }
    }
  }

  .selected-tags,
  .original-tags {
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px dashed #ebeef5;

    .section-title {
      display: block;
      font-size: 13px;
      color: #606266;
      margin-bottom: 8px;
      font-weight: 500;
    }

    .tag-item {
      margin-right: 8px;
      margin-bottom: 8px;
    }
  }
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

      .details-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        flex: 1;

        .detail-tag {
          font-size: 12px;
        }
      }
    }
  }
}
</style>

<style lang="scss">
.add-product-dialog {
  display: flex;
  justify-content: center;
  align-items: center;
  .el-dialog {
    margin: 0 !important;
    max-height: 85vh;
    display: flex;
    flex-direction: column;
    border-radius: 12px;
  }
  .el-dialog__header {
    display: none;
  }
  .el-dialog__body {
    flex: 1;
    overflow: hidden;
    padding: 0;
  }
}

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
</style>
