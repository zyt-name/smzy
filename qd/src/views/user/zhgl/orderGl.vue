<template>
  <div class="order-manage-component">
    <div class="order-tabs">
      <div
        v-for="tab in tabs"
        :key="tab.value"
        class="tab-item"
        :class="{ active: currentTab === tab.value }"
        @click="currentTab = tab.value"
      >
        {{ tab.label }}
        <span v-if="tab.count > 0" class="count">({{ tab.count }})</span>
      </div>
    </div>

    <div class="order-list">
      <div v-if="filteredOrders.length > 0" class="order-items">
        <div v-for="order in filteredOrders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <span class="order-time">{{ formatTime(order.createdAt) }}</span>
              <span v-if="order.status === 0 && getOrderCountdown(order)" class="countdown">
                剩余支付时间：{{ getOrderCountdown(order) }}
              </span>
            </div>
            <div class="order-status" :class="getStatusClass(order.status)">
              {{ getStatusText(order.status) }}
            </div>
          </div>

          <div class="order-body">
            <div class="products-grid">
              <template v-for="(item, index) in getDisplayItems(order.items)" :key="item.id">
                <div v-if="index < 4" class="product-item-compact">
                  <div class="product-img">
                    <ProductImage :src="productImages[item.productId]" :alt="item.productName" />
                  </div>
                  <div class="product-name">{{ item.productName }}</div>
                  <div class="product-price">¥{{ item.price?.toFixed(2) }}</div>
                  <div class="product-quantity">x {{ item.quantity }}</div>
                </div>
              </template>
              <div v-if="order.items && order.items.length > 4" class="more-items">
                +{{ order.items.length - 4 }}
              </div>
            </div>
          </div>

          <div class="order-footer">
            <div class="order-total">
              <span class="label">共 {{ getOrderItemCount(order) }} 件商品</span>
              <span class="total">合计：<em>¥{{ order.totalPrice?.toFixed(2) }}</em></span>
            </div>
            <div class="order-actions">
              <el-button v-if="order.status === 0" type="primary" size="small" @click="handlePay(order)">
                立即支付
              </el-button>
              <el-button v-if="order.status === 0" size="small" @click="handleCancel(order)">
                取消订单
              </el-button>
              <el-button v-if="order.status === 2" type="primary" size="small" @click="handleConfirmReceive(order)">
                确认收货
              </el-button>
              <el-button size="small" @click="handleViewDetail(order)">
                查看详情
              </el-button>
              <ActionButton v-if="order.status === 1 || order.status === 2 || order.status === 3" type="orange" size="medium" @click="openRefundDialog(order)">
                退款
              </ActionButton>
              <el-button v-if="order.status === 3 || order.status === 4" type="danger" link size="small" @click="handleDelete(order)">
                删除订单
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-orders">
        <el-empty description="暂无订单">
          <el-button type="primary" @click="emit('toUser')">去购物</el-button>
        </el-empty>
      </div>
    </div>

    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="1300px"
      destroy-on-close
    >
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-section">
          <h4>订单信息</h4>
          <div class="detail-row">
            <span class="label">订单号：</span>
            <span class="value">{{ currentOrder.orderNo }}</span>
          </div>
          <div class="detail-row">
            <span class="label">订单状态：</span>
            <span class="value" :class="getStatusClass(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">创建时间：</span>
            <span class="value">{{ formatTime(currentOrder.createdAt) }}</span>
          </div>
          <div v-if="currentOrder.status === 4 && currentOrder.items?.[0]?.cancelReason" class="detail-row">
            <span class="label">取消原因：</span>
            <span class="value">{{ currentOrder.items[0].cancelReason }}</span>
          </div>
          <div class="detail-row">
            <span class="label">订单备注：</span>
            <span class="value">{{ currentOrder.remark || '无' }}</span>
          </div>
        </div>

        <div class="detail-section">
          <h4>收货信息</h4>
          <div class="detail-row">
            <span class="label">收货地址：</span>
            <span class="value">{{ formatAddress(currentOrder.items?.[0]?.userAddress) }}</span>
          </div>
        </div>

        <div class="detail-section">
          <h4>商品信息</h4>
          <div class="product-list">
            <div v-for="item in currentOrder.items" :key="item.id" class="product-item">
              <div class="product-img">
                <ProductImage :src="productImages[item.productId]" :alt="item.productName" />
              </div>
              <div class="product-info">
                <h4 class="product-name">{{ item.productName }}</h4>
              </div>
              <div class="product-price">¥{{ item.price?.toFixed(2) }}</div>
              <div class="product-quantity">x {{ item.quantity }}</div>
              <div class="product-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
            </div>
          </div>
        </div>

        <div class="detail-section total-section">
          <div class="total-row">
            <span class="label">商品总价：</span>
            <span class="value">¥{{ currentOrder.totalPrice?.toFixed(2) }}</span>
          </div>
          <div class="total-row">
            <span class="label">运费：</span>
            <span class="value">¥0.00</span>
          </div>
          <div class="total-row grand-total">
            <span class="label">订单总额：</span>
            <span class="value">¥{{ currentOrder.totalPrice?.toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </el-dialog>

    <el-dialog
      v-model="refundDialogVisible"
      title="退款申请"
      width="500px"
      destroy-on-close
    >
      <el-form :model="refundForm" label-width="100px">
        <el-form-item label="订单ID">
          <el-input v-model="refundForm.targetId" disabled />
        </el-form-item>
        <el-form-item label="联系电话" required>
          <el-input v-model="refundForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="退款理由" required>
          <el-input
            v-model="refundForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入退款理由"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleApplyRefund">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listOrder, getOrderDetail, cancelOrder, confirmReceive, deleteOrder } from '../../../api/user/UserOrder'
import { applyRefund } from '../../../api/user/UserAppication'
import { getProductImages } from '../../../api/User'
import ProductImage from '../../../components/ProductImage.vue'
import ActionButton from '../../../components/ActionButton.vue'

const emit = defineEmits(['toUser', 'toPay'])

const orders = ref([])
const currentTab = ref('all')
const loading = ref(false)
const detailDialogVisible = ref(false)
const currentOrder = ref(null)
const productImages = ref({})
const refundDialogVisible = ref(false)
const refundForm = ref({
  targetId: null,
  reason: '',
  phone: ''
})
let countdownTimer = null

// 支付超时时间（30分钟 = 1800000毫秒）
const PAY_TIMEOUT = 1800000

const tabs = [
  { label: '全部订单', value: 'all', count: 0 },
  { label: '待付款', value: 'pending', count: 0 },
  { label: '待发货', value: 'paid', count: 0 },
  { label: '待收货', value: 'shipped', count: 0 },
  { label: '已完成', value: 'completed', count: 0 },
  { label: '已取消', value: 'cancelled', count: 0 },
  { label: '已退款', value: 'refunded', count: 0 }
]

const filteredOrders = computed(() => {
  if (currentTab.value === 'all') {
    return orders.value
  }
  const statusMap = {
    'pending': 0,
    'paid': 1,
    'shipped': 2,
    'completed': 3,
    'cancelled': 4,
    'refunded': 5
  }
  return orders.value.filter(order => order.status === statusMap[currentTab.value])
})

onMounted(() => {
  fetchOrders()
  startCountdownTimer()
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})

const startCountdownTimer = () => {
  countdownTimer = setInterval(() => {
    // 触发重新渲染倒计时
    orders.value = [...orders.value]
  }, 1000)
}

const getOrderCountdown = (order) => {
  if (!order.createdAt) return null
  
  const createdAt = new Date(order.createdAt).getTime()
  const now = Date.now()
  const elapsed = now - createdAt
  const remaining = PAY_TIMEOUT - elapsed
  
  if (remaining <= 0) {
    if (order.status === 0) {
      order.status = 4
      updateTabCounts()
    }
    return null
  }
  
  const minutes = Math.floor(remaining / 60000)
  const seconds = Math.floor((remaining % 60000) / 1000)
  
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await listOrder()
    if (res.code === 200) {
      orders.value = res.data || []
      for (let order of orders.value) {
        await fetchOrderDetail(order)
      }
      await fetchProductImages()
      updateTabCounts()
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const fetchOrderDetail = async (order) => {
  try {
    const res = await getOrderDetail(order.id)
    if (res.code === 200) {
      order.items = res.data || []
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
  }
}

const fetchProductImages = async () => {
  const productIds = new Set()
  orders.value.forEach(order => {
    if (order.items) {
      order.items.forEach(item => {
        if (item.productId) {
          productIds.add(item.productId)
        }
      })
    }
  })
  
  if (productIds.size === 0) return
  
  try {
    const res = await getProductImages(Array.from(productIds))
    if (res.code === 200 && res.data) {
      productImages.value = res.data
    }
  } catch (error) {
    console.error('获取商品图片失败:', error)
  }
}

const updateTabCounts = () => {
  tabs[0].count = orders.value.length
  tabs[1].count = orders.value.filter(o => o.status === 0).length
  tabs[2].count = orders.value.filter(o => o.status === 1).length
  tabs[3].count = orders.value.filter(o => o.status === 2).length
  tabs[4].count = orders.value.filter(o => o.status === 3).length
  tabs[5].count = orders.value.filter(o => o.status === 4).length
  tabs[6].count = orders.value.filter(o => o.status === 5).length
}

const getStatusText = (status) => {
  const statusMap = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消',
    5: '已退款'
  }
  return statusMap[status] || '未知状态'
}

const getStatusClass = (status) => {
  const classMap = {
    0: 'status-pending',
    1: 'status-paid',
    2: 'status-shipped',
    3: 'status-completed',
    4: 'status-cancelled',
    5: 'status-refunded'
  }
  return classMap[status] || ''
}

const getOrderItemCount = (order) => {
  if (!order.items) return 0
  return order.items.reduce((acc, item) => acc + item.quantity, 0)
}

const getDisplayItems = (items) => {
  if (!items) return []
  return items.slice(0, 4)
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

const formatAddress = (addressJson) => {
  if (!addressJson) return ''
  try {
    const addr = JSON.parse(addressJson)
    const province = addr.province || ''
    const city = addr.city || ''
    const district = addr.district || ''
    const detail = addr.detailAddress || addr.detail || ''
    const name = addr.receiverName || addr.name || ''
    const phone = addr.receiverPhone || addr.phone || ''

    let address = `${province}${city}${district}${detail}`
    if (name && phone) {
      address += ` (${name} ${phone})`
    }
    return address
  } catch (e) {
    return addressJson
  }
}

const handleViewDetail = (order) => {
  currentOrder.value = order
  detailDialogVisible.value = true
}

const handlePay = (order) => {
  // 构建订单信息，供支付页面使用
  const orderInfo = {
    orderType: 'shop',
    orderId: order.id,
    totalPrice: order.totalPrice,
    totalCount: getOrderItemCount(order),
    createdAt: order.createdAt,
    productInfo: order.items && order.items.length > 0 ? {
      productId: order.items[0].productId,
      name: order.items[0].productName,
      price: order.items[0].price,
      quantity: order.items[0].quantity,
      imageUrl: productImages.value[order.items[0].productId]
    } : null,
    address: order.items && order.items.length > 0 ? JSON.parse(order.items[0].userAddress || '{}') : null,
    remark: order.remark || ''
  }
  localStorage.setItem('currentOrderInfo', JSON.stringify(orderInfo))
  emit('toPay', { type: 'shop', orderId: order.id })
}

const handleCancel = (order) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await cancelOrder(order.id, '用户主动取消')
      if (res.code === 200) {
        ElMessage.success('订单已取消')
        order.status = 4
        updateTabCounts()
      } else {
        ElMessage.error(res.msg || '取消订单失败')
      }
    } catch (error) {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  })
}

const handleConfirmReceive = (order) => {
  ElMessageBox.confirm('确认已收到商品？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      const res = await confirmReceive(order.id)
      if (res.code === 200) {
        ElMessage.success('确认收货成功')
        order.status = 3
        updateTabCounts()
      } else {
        ElMessage.error(res.msg || '确认收货失败')
      }
    } catch (error) {
      console.error('确认收货失败:', error)
      ElMessage.error('确认收货失败')
    }
  })
}

const handleDelete = (order) => {
  ElMessageBox.confirm('确定要删除该订单吗？删除后不可恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    try {
      const res = await deleteOrder(order.id)
      if (res.code === 200) {
        ElMessage.success('订单已删除')
        orders.value = orders.value.filter(o => o.id !== order.id)
        updateTabCounts()
      } else {
        ElMessage.error(res.msg || '删除订单失败')
      }
    } catch (error) {
      console.error('删除订单失败:', error)
      ElMessage.error('删除订单失败')
    }
  })
}

const openRefundDialog = (order) => {
  refundForm.value = {
    targetId: order.id,
    reason: '',
    phone: ''
  }
  refundDialogVisible.value = true
}

const handleApplyRefund = async () => {
  if (!refundForm.value.reason.trim()) {
    ElMessage.warning('请输入退款理由')
    return
  }
  if (!refundForm.value.phone.trim()) {
    ElMessage.warning('请输入联系电话')
    return
  }
  try {
    const res = await applyRefund(refundForm.value)
    if (res.code === 200) {
      ElMessage.success('退款申请已提交')
      refundDialogVisible.value = false
    } else {
      ElMessage.error(res.msg || '退款申请失败')
    }
  } catch (error) {
    console.error('退款申请失败:', error)
    ElMessage.error('退款申请失败')
  }
}
</script>

<style lang="scss" scoped>
.order-manage-component {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}

.order-tabs {
  display: flex;
  gap: 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;

  .tab-item {
    padding: 12px 24px;
    cursor: pointer;
    font-size: 14px;
    color: #666;
    border-bottom: 2px solid transparent;
    transition: all 0.3s;

    &:hover {
      color: #2a9eff;
    }

    &.active {
      color: #2a9eff;
      border-bottom-color: #2a9eff;
      font-weight: 600;
    }

    .count {
      margin-left: 4px;
      font-size: 12px;
    }
  }
}

.order-list {
  min-height: 300px;
}

.order-card {
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 16px;
  overflow: hidden;

  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background: #f8f9fa;
    border-bottom: 1px solid #eee;

    .order-info {
      display: flex;
      gap: 20px;
      align-items: center;

      .order-no {
        font-weight: 500;
        color: #333;
      }

      .order-time {
        color: #999;
        font-size: 13px;
      }

      .countdown {
        color: #e6a23c;
        font-size: 13px;
        font-weight: 500;
      }
    }

    .order-status {
      font-weight: 600;

      &.status-pending { color: #e6a23c; }
      &.status-paid { color: #409eff; }
      &.status-shipped { color: #67c23a; }
      &.status-completed { color: #909399; }
      &.status-cancelled { color: #f56c6c; }
      &.status-refunded { color: #ff4d4f; }
    }
  }

  .order-body {
    padding: 16px;

    .products-grid {
      display: flex;
      gap: 12px;
      align-items: flex-start;
    }

    .product-item-compact {
      width: calc(25% - 9px);
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;

      .product-img {
        width: 60px;
        height: 60px;
        border-radius: 4px;
        overflow: hidden;
        margin-bottom: 6px;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .product-name {
        font-size: 12px;
        color: #333;
        margin-bottom: 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        max-width: 100%;
      }

      .product-price {
        font-size: 12px;
        color: #e4393c;
        font-weight: 600;
        margin-bottom: 2px;
      }

      .product-quantity {
        font-size: 11px;
        color: #999;
      }
    }

    .more-items {
      width: calc(25% - 9px);
      height: 90px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f5f5f5;
      border-radius: 4px;
      font-size: 14px;
      color: #999;
      font-weight: 500;
    }
  }

  .order-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background: #f8f9fa;
    border-top: 1px solid #eee;

    .order-total {
      display: flex;
      align-items: center;
      gap: 20px;

      .label {
        color: #666;
        font-size: 13px;
      }

      .total {
        color: #333;

        em {
          font-style: normal;
          color: #e4393c;
          font-size: 18px;
          font-weight: 700;
        }
      }
    }

    .order-actions {
      display: flex;
      gap: 8px;
    }
  }
}

.empty-orders {
  padding: 60px 0;
}

.order-detail {
  .detail-section {
    margin-bottom: 24px;

    h4 {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 16px;
      padding-bottom: 8px;
      border-bottom: 1px solid #eee;
    }

    .detail-row {
      display: flex;
      margin-bottom: 12px;

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

  .product-list {
    .product-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;

      .product-img {
        width: 60px;
        height: 60px;
        border-radius: 4px;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
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

      .product-price,
      .product-quantity,
      .product-subtotal {
        width: 80px;
        text-align: center;
        color: #666;
      }

      .product-subtotal {
        color: #e4393c;
        font-weight: 600;
      }
    }
  }

  .total-section {
    background: #f8f9fa;
    padding: 16px;
    border-radius: 8px;

    .total-row {
      display: flex;
      justify-content: flex-end;
      margin-bottom: 8px;

      .label {
        color: #666;
      }

      .value {
        margin-left: 20px;
        min-width: 100px;
        text-align: right;
      }

      &.grand-total {
        margin-top: 12px;
        padding-top: 12px;
        border-top: 1px solid #eee;

        .label {
          font-weight: 600;
        }

        .value {
          color: #e4393c;
          font-size: 20px;
          font-weight: 700;
        }
      }
    }
  }
}
</style>
