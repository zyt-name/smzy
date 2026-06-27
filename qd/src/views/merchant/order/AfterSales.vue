<template>
  <el-card shadow="never" class="request-panel full-height-card">
    <template #header>
      <div class="card-header">
        <span>售后申请</span>
        <el-tag :type="pendingCount > 0 ? 'danger' : 'success'" size="small">
          {{ pendingCount }} 待处理
        </el-tag>
      </div>
    </template>

    <div class="card-body-content">
      <div class="application-list">
        <div v-for="item in paginatedAftersales" :key="item.id" class="app-item">
          <div class="app-info">
            <div class="app-title">
              <strong>{{ item.buyer }}</strong> 申请{{ item.type }}
              <el-tag :type="statusTagType(item.requestStatus)" size="small" style="margin-left: 8px">
                {{ statusText(item.requestStatus) }}
              </el-tag>
            </div>
            <div class="app-time">{{ item.time }}</div>
            <div class="app-desc">
              <span class="highlight-text danger">
                订单号: {{ item.targetId }}
              </span>
              <p>{{ item.reason }}</p>
              <p v-if="item.feedback" class="feedback-text">处理反馈: {{ item.feedback }}</p>
            </div>
          </div>
          <div v-if="item.requestStatus === 1" class="app-ops">
            <ActionButton
              type="success"
              size="small"
              @click="handleApprove(item)"
            >
              同意
            </ActionButton>
            <ActionButton
              type="danger"
              size="small"
              style="margin-left: 6px"
              @click="handleReject(item)"
            >
              不同意
            </ActionButton>
          </div>
        </div>
        <el-empty v-if="aftersales.length === 0" description="暂无待处理申请" :image-size="60" />
      </div>

      <div class="pagination-wrapper small-pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          small
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ActionButton from '../../../components/ActionButton.vue'
import { getRefundApplicationList, processRefundApplication } from '../../../api/merchant/MerchantApplication'

const aftersales = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const total = ref(0)

// 分页数据
const paginatedAftersales = computed(() => {
  return aftersales.value
})

// 待处理数量
const pendingCount = computed(() => {
  return aftersales.value.filter(item => item.requestStatus === 1).length
})

// 状态文本
const statusText = (status) => {
  const map = { 1: '处理中', 2: '已驳回', 3: '已同意' }
  return map[status] || '未知'
}

// 状态标签类型
const statusTagType = (status) => {
  const map = { 1: 'warning', 2: 'danger', 3: 'success' }
  return map[status] || 'info'
}

// 页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  loadAftersales()
}

// 加载售后数据
const loadAftersales = async () => {
  try {
    loading.value = true
    const res = await getRefundApplicationList({
      page: currentPage.value,
      size: pageSize.value
    })
    if (res.code === 200 || res.code === 1) {
      const data = res.data || {}
      aftersales.value = (data.data || []).map(item => ({
        id: item.id,
        buyer: `用户${item.userId}`,
        type: '退款',
        time: item.createTime,
        amount: '0',
        reason: item.reason,
        targetId: item.targetId,
        userId: item.userId,
        requestStatus: item.requestStatus
      }))
      total.value = data.total || 0
    }
  } catch (error) {
    console.error('加载售后数据失败:', error)
    ElMessage.error('加载售后数据失败')
  } finally {
    loading.value = false
  }
}

// 处理售后申请（同意）
const handleApprove = (item) => {
  ElMessageBox.prompt(`确定同意 ${item.buyer} 的${item.type}申请吗？请输入处理原因：`, '确认处理', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: '请输入处理原因（可选）',
    type: 'info'
  }).then(async ({ value }) => {
    try {
      await processRefundApplication({
        applicationId: item.id,
        operationType: 1,
        feedback: value || ''
      })
      ElMessage.success(`已同意 ${item.buyer} 的${item.type}申请`)
      loadAftersales()
    } catch (error) {
      console.error('处理失败:', error)
      ElMessage.error('处理失败')
    }
  }).catch(() => {})
}

// 拒绝售后申请
const handleReject = (item) => {
  ElMessageBox.prompt(`确定拒绝 ${item.buyer} 的${item.type}申请吗？请输入拒绝原因：`, '确认拒绝', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: '请输入拒绝原因',
    inputPattern: /\S+/,
    inputErrorMessage: '请输入拒绝原因',
    type: 'warning'
  }).then(async ({ value }) => {
    try {
      await processRefundApplication({
        applicationId: item.id,
        operationType: 2,
        feedback: value
      })
      ElMessage.warning(`已拒绝 ${item.buyer} 的${item.type}申请`)
      loadAftersales()
    } catch (error) {
      console.error('拒绝失败:', error)
      ElMessage.error('拒绝失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadAftersales()
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

  span {
    font-weight: bold;
    font-size: 16px;
  }
}

.card-body-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
}

.application-list {
  flex: 1;
  overflow-y: auto;

  .app-item {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 15px;
    border-bottom: 1px solid #f0f2f5;
    transition: background-color 0.2s ease;

    &:hover {
      background-color: #f8fbff;
    }

    .app-info {
      flex: 1;

      .app-title {
        font-size: 14px;
        margin-bottom: 6px;

        strong {
          color: #333;
          font-weight: 600;
        }
      }

      .app-time {
        font-size: 12px;
        color: #999;
        margin-bottom: 6px;
      }

      .app-desc {
        .highlight-text {
          font-size: 12px;
          font-weight: bold;

          &.danger {
            color: #f56c6c;
          }

          &.primary {
            color: #2a9eff;
          }
        }

        p {
          margin: 4px 0 0 0;
          font-size: 12px;
          color: #666;

          &.feedback-text {
            color: #909399;
            font-style: italic;
          }
        }
      }
    }

    .app-ops {
      display: flex;
      align-items: center;
    }
  }
}

.pagination-wrapper {
  margin-top: 15px;
  display: flex;
  justify-content: center;
}
</style>
