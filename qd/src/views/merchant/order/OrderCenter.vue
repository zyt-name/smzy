<template>
  <el-card shadow="never" class="full-height-card">
    <template #header>
      <div class="card-header">
        <div class="header-left">
          <span>订单中心</span>
          <el-input
            v-model="searchQuery"
            placeholder="搜索订单号"
            style="width: 200px; margin-left: 20px"
            prefix-icon="Search"
            clearable
          />
          <el-select v-model="statusFilter" placeholder="订单状态" style="width: 120px; margin-left: 10px" clearable>
            <el-option label="全部状态" value="" />
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
            <el-option label="已退款" :value="5" />
          </el-select>
        </div>
        <ActionButton type="primary" size="medium" @click="handleExport">
          <el-icon><Download /></el-icon>导出报表
        </ActionButton>
      </div>
    </template>

    <div class="card-body-content">
      <!-- 订单列表 -->
      <div class="table-wrapper">
        <el-table
          :data="orderList"
          v-loading="loading"
          style="width: 100%"
          :header-cell-style="{ background: '#f5f7fa', fontWeight: 'bold' }"
        >
          <el-table-column prop="orderNo" label="订单编号" width="200" />
          <el-table-column prop="userId" label="买家ID" width="100" />
          <el-table-column prop="totalPrice" label="订单金额" width="120">
            <template #default="{ row }">
              <span class="order-amount">¥{{ row.totalPrice?.toFixed(2) || '0.00' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="下单时间" width="180">
            <template #default="{ row }">
              {{ formatTime(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="200" fixed="right">
            <template #default="{ row }">
              <ActionButton type="primary" size="small" @click="handleViewDetail(row)">详情</ActionButton>
              <ActionButton
                v-if="row.status === 1"
                type="success"
                size="small"
                @click="handleShip(row)"
                style="margin-left: 8px"
              >发货</ActionButton>
              <ActionButton
                v-if="row.status === 0"
                type="danger"
                size="small"
                @click="handleCancel(row)"
                style="margin-left: 8px"
              >取消</ActionButton>
              <ActionButton
                v-if="row.status === 3 || row.status === 4"
                type="danger"
                size="small"
                @click="handleDelete(row)"
                style="margin-left: 8px"
              >删除</ActionButton>
              <ActionButton
                v-if="row.status === 1 || row.status === 3"
                type="orange"
                size="small"
                @click="handleRefund(row)"
                style="margin-left: 8px"
              >退款</ActionButton>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="10"
          :total="total"
          layout="prev, pager, next, jumper, total"
          background
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="600px"
      center
      align-center
      destroy-on-close
    >
      <div v-if="currentOrder" class="order-detail-content">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ formatTime(currentOrder.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="买家ID">{{ currentOrder.userId }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusTag(currentOrder.status)">
              {{ getStatusText(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="支付方式" :span="2">
            {{ formatPaymentMethod(currentOrder.paymentMethod) }}
          </el-descriptions-item>
          <el-descriptions-item label="订单金额" :span="2">
            <span class="highlight-price">¥{{ currentOrder.totalPrice?.toFixed(2) || '0.00' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="收货人" :span="2">
            {{ currentOrder.shippingInformation?.receiverName || '-' }} {{ currentOrder.shippingInformation?.receiverPhone || '' }}
          </el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">
            {{ formatShippingAddress(currentOrder.shippingAddress) }}
          </el-descriptions-item>
          <el-descriptions-item label="取消原因" :span="2" v-if="currentOrder.cancelReason">
            {{ currentOrder.cancelReason }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="detail-products" v-if="orderItems.length > 0">
          <h4 class="section-title">商品清单</h4>
          <div class="product-item-mini" v-for="item in orderItems" :key="item.id">
            <ProductImage
              :src="item.productImage || ''"
              alt="商品图片"
              className="product-thumb"
              :style="{ width: '50px', height: '50px', objectFit: 'cover', borderRadius: '6px' }"
            />
            <div class="prod-info">
              <span class="name">{{ item.productName || '未知商品' }}</span>
              <span class="meta">¥{{ item.price?.toFixed(2) || '0.00' }} x {{ item.quantity || 1 }}</span>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="detail-actions">
          <ActionButton
            v-if="currentOrder && currentOrder.status === 1"
            type="success"
            size="medium"
            @click="handleShip(currentOrder); detailVisible = false"
          >确认发货</ActionButton>
          <ActionButton
            v-if="currentOrder && currentOrder.status === 0"
            type="danger"
            size="medium"
            @click="handleCancel(currentOrder); detailVisible = false"
          >取消订单</ActionButton>
          <ActionButton
            v-if="currentOrder && (currentOrder.status === 3 || currentOrder.status === 4)"
            type="danger"
            size="medium"
            @click="handleDelete(currentOrder); detailVisible = false"
          >删除订单</ActionButton>
          <ActionButton
            v-if="currentOrder && (currentOrder.status === 1 || currentOrder.status === 3)"
            type="orange"
            size="medium"
            @click="handleRefund(currentOrder); detailVisible = false"
          >退款</ActionButton>
          <ActionButton
            type="default"
            size="medium"
            @click="detailVisible = false"
          >关闭</ActionButton>
        </div>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Download } from '@element-plus/icons-vue'
import ActionButton from '../../../components/ActionButton.vue'
import ProductImage from '../../../components/ProductImage.vue'
import {
  getMerchantOrders,
  getOrderDetailById,
  confirmShip,
  cancelOrder,
  deleteOrder,
  refundOrder
} from '../../../api/merchant/MerchantOrder.js'

const loading = ref(false)
const searchQuery = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const total = ref(0)
const orderList = ref([])
const orderItems = ref([])
const detailVisible = ref(false)
const currentOrder = ref(null)

// 状态映射
const statusMap = {
  0: { text: '待付款', tag: 'warning' },
  1: { text: '待发货', tag: 'primary' },
  2: { text: '待收货', tag: 'info' },
  3: { text: '已完成', tag: 'success' },
  4: { text: '已取消', tag: 'danger' },
  5: { text: '已退款', tag: 'danger' }
}

const getStatusText = (status) => {
  return statusMap[status]?.text || '未知'
}

const getStatusTag = (status) => {
  return statusMap[status]?.tag || 'info'
}

// 格式化支付方式
const formatPaymentMethod = (method) => {
  const methodMap = {
    'wechat': '微信支付',
    'alipay': '支付宝',
    'cash': '货到付款',
    'card': '银行卡',
    'balance': '余额支付'
  }
  return methodMap[method] || method || '未支付'
}

// 格式化收货地址
const formatShippingAddress = (address) => {
  if (!address) return '暂无地址信息'
  const { province, city, district, detailAddress } = address
  const parts = []
  if (province) parts.push(province)
  if (city) parts.push(city)
  if (district) parts.push(district)
  if (detailAddress) parts.push(detailAddress)
  return parts.length > 0 ? parts.join('') : '暂无地址信息'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  if (isNaN(date.getTime())) return time
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 加载订单数据
const loadOrders = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      pageSize: 10
    }
    // 添加状态筛选条件
    if (statusFilter.value !== '' && statusFilter.value !== null) {
      params.status = statusFilter.value
    }
    // 添加订单号搜索条件
    if (searchQuery.value && searchQuery.value.trim() !== '') {
      params.orderNo = searchQuery.value.trim()
    }

    const res = await getMerchantOrders(params)
    if (res.code === 200 || res.code === 1) {
      const pageData = res.data || { total: 0, data: [] }
      total.value = pageData.total || 0
      orderList.value = pageData.data || []
    } else {
      ElMessage.error(res.msg || '获取订单失败')
    }
  } catch (error) {
    console.error('加载订单数据失败:', error)
    ElMessage.error('加载订单数据失败')
  } finally {
    loading.value = false
  }
}

// 页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  loadOrders()
}

// 查看详情
const handleViewDetail = async (row) => {
  currentOrder.value = row
  detailVisible.value = true
  // 加载订单详情
  try {
    const res = await getOrderDetailById(row.id)
    if (res.code === 200 || res.code === 1) {
      orderItems.value = res.data || []
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
    orderItems.value = []
  }
}

// 发货
const handleShip = (row) => {
  ElMessageBox.confirm(`确定要为订单 ${row.orderNo} 发货吗？`, '确认发货', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      const res = await confirmShip(row.id)
      if (res.code === 200 || res.code === 1) {
        ElMessage.success('发货成功')
        loadOrders()
      } else {
        ElMessage.error(res.msg || '发货失败')
      }
    } catch (error) {
      console.error('发货失败:', error)
      ElMessage.error('发货失败')
    }
  }).catch(() => {})
}

// 取消订单
const handleCancel = (row) => {
  ElMessageBox.prompt('请输入取消原因', '取消订单', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    inputValidator: (value) => {
      if (!value || value.trim() === '') {
        return '请输入取消原因'
      }
      return true
    }
  }).then(async ({ value }) => {
    try {
      const res = await cancelOrder(row.id, value)
      if (res.code === 200 || res.code === 1) {
        ElMessage.success('订单已取消')
        loadOrders()
      } else {
        ElMessage.error(res.msg || '取消失败')
      }
    } catch (error) {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }).catch(() => {})
}

// 删除订单（逻辑删除）
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除订单 ${row.orderNo} 吗？删除后订单将不再显示`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteOrder(row.id)
      if (res.code === 200 || res.code === 1) {
        ElMessage.success('订单已删除')
        loadOrders()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除订单失败:', error)
      ElMessage.error('删除订单失败')
    }
  }).catch(() => {})
}

// 退款
const handleRefund = (row) => {
  ElMessageBox.confirm(`确定为订单 ${row.orderNo} 进行退款吗？退款后订单状态将变为"已退款"`, '确认退款', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await refundOrder(row.id)
      if (res.code === 200 || res.code === 1) {
        ElMessage.success('退款成功')
        loadOrders()
      } else {
        ElMessage.error(res.msg || '退款失败')
      }
    } catch (error) {
      console.error('退款失败:', error)
      ElMessage.error('退款失败')
    }
  }).catch(() => {})
}

// 导出报表
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 监听状态筛选变化
watch(statusFilter, () => {
  currentPage.value = 1
  loadOrders()
})

// 搜索订单号（防抖）
let searchTimer = null
watch(searchQuery, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    currentPage.value = 1
    loadOrders()
  }, 500)
})

onMounted(() => {
  loadOrders()
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
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-left {
    display: flex;
    align-items: center;

    span {
      font-weight: bold;
      font-size: 16px;
    }
  }
}

.card-body-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
}

.table-wrapper {
  flex: 1;
  overflow: auto;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.order-amount {
  font-weight: bold;
  color: #f56c6c;
}

.product-thumb {
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

// 详情弹窗样式
.order-detail-content {
  max-height: 500px;
  overflow-y: auto;

  .highlight-price {
    font-size: 16px;
    font-weight: bold;
    color: #f56c6c;
  }

  .section-title {
    margin: 15px 0 10px;
    padding-left: 10px;
    border-left: 4px solid #2a9eff;
    font-size: 14px;
    font-weight: bold;
  }

  .product-item-mini {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 10px;
    background: #f8fbff;
    border-radius: 8px;
    margin-bottom: 8px;

    .prod-info {
      display: flex;
      flex-direction: column;
      flex: 1;

      .name {
        font-size: 13px;
        color: #333;
        margin-bottom: 4px;
      }

      .meta {
        font-size: 12px;
        color: #999;
      }
    }
  }
}

.detail-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
