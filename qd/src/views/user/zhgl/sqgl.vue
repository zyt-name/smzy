<template>
  <div class="content-section">
    <h3 class="section-title">申请管理</h3>

    <!-- 筛选条件 -->
    <div class="filter-section">
      <el-select v-model="filter.requestType" placeholder="申请类型" clearable style="width: 160px">
        <el-option label="退款(用户)" :value="1" />
        <el-option label="举报商家" :value="2" />
        <el-option label="举报商品" :value="3" />
        <el-option label="申请删除评论(商家)" :value="4" />
      </el-select>
      <el-select v-model="filter.requestStatus" placeholder="申请状态" clearable style="width: 160px">
        <el-option label="处理中" :value="1" />
        <el-option label="已通过" :value="2" />
        <el-option label="已驳回" :value="3" />
      </el-select>
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 申请列表 -->
    <div v-if="applicationList.length > 0" class="application-list" v-loading="loading">
      <div v-for="item in applicationList" :key="item.id" class="application-card">
        <div class="application-header">
          <div class="application-info">
            <span class="info-item">
              <span class="label">申请ID：</span>
              <span class="value">{{ item.id }}</span>
            </span>
            <span class="info-item">
              <span class="label">申请时间：</span>
              <span class="value">{{ formatTime(item.createTime) }}</span>
            </span>
          </div>
          <div class="status-tag" :class="getStatusClass(item.requestStatus)">
            {{ getStatusText(item.requestStatus) }}
          </div>
        </div>
        <div class="application-body">
          <div class="info-row">
            <span class="info-item">
              <span class="label">申请类型：</span>
              <span class="value type-tag" :class="getTypeClass(item.requestType)">
                {{ getTypeText(item.requestType) }}
              </span>
            </span>
            <span class="info-item">
              <span class="label">目标ID：</span>
              <span class="value">{{ item.targetId }}</span>
            </span>
            <span class="info-item">
              <span class="label">目标类型：</span>
              <span class="value">{{ getTargetTypeText(item.targetType) }}</span>
            </span>
          </div>
          <div class="info-row">
            <span class="info-item full-width">
              <span class="label">联系电话：</span>
              <span class="value">{{ item.phone }}</span>
            </span>
          </div>
          <div class="info-row">
            <span class="info-item full-width">
              <span class="label">申请理由：</span>
              <span class="value reason">{{ item.reason }}</span>
            </span>
          </div>
          <div class="info-row" v-if="item.feedback">
            <span class="info-item full-width">
              <span class="label">反馈：</span>
              <span class="value feedback">{{ item.feedback }}</span>
            </span>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="10"
          :total="total"
          layout="total, prev, pager, next, jumper"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <el-empty v-else description="暂无申请记录" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getApplicationList } from '../../../api/user/UserAppication'

const applicationList = ref([])
const loading = ref(false)
const pageNum = ref(1)
const total = ref(0)

const filter = ref({
  requestType: null,
  requestStatus: null
})

onMounted(() => {
  fetchApplicationList()
})

const fetchApplicationList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: 10
    }
    if (filter.value.requestType !== null && filter.value.requestType !== '') {
      params.requestType = filter.value.requestType
    }
    if (filter.value.requestStatus !== null && filter.value.requestStatus !== '') {
      params.requestStatus = filter.value.requestStatus
    }

    const res = await getApplicationList(params)
    if (res.code === 200) {
      applicationList.value = res.data?.data || []
      total.value = res.data?.count || 0
    } else {
      ElMessage.error(res.msg || '获取申请列表失败')
    }
  } catch (error) {
    console.error('获取申请列表失败:', error)
    ElMessage.error('获取申请列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchApplicationList()
}

const handleReset = () => {
  filter.value.requestType = null
  filter.value.requestStatus = null
  pageNum.value = 1
  fetchApplicationList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  fetchApplicationList()
}

const getTypeText = (type) => {
  const typeMap = {
    1: '退款(用户)',
    2: '举报商家',
    3: '举报商品',
    4: '申请删除评论(商家)'
  }
  return typeMap[type] || '未知类型'
}

const getTypeClass = (type) => {
  const classMap = {
    1: 'refund',
    2: 'report-merchant',
    3: 'report-product',
    4: 'delete-comment'
  }
  return classMap[type] || ''
}

const getStatusText = (status) => {
  const statusMap = {
    1: '处理中',
    2: '驳回',
    3: '同意'
  }
  return statusMap[status] || '未知状态'
}

const getStatusClass = (status) => {
  const classMap = {
    1: 'processing',
    2: 'rejected',
    3: 'approved'
  }
  return classMap[status] || ''
}

const getTargetTypeText = (type) => {
  const typeMap = {
    1: '订单',
    2: '商家',
    3: '商品'
  }
  return typeMap[type] || '未知'
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}
</script>

<style lang="scss" scoped>
.content-section {
  .section-title {
    font-size: 20px;
    color: #333;
    margin-bottom: 20px;
    font-weight: 600;
  }

  .filter-section {
    display: flex;
    gap: 15px;
    margin-bottom: 20px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;
  }

  .application-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }

  .application-card {
    border: 1px solid #eee;
    border-radius: 8px;
    overflow: hidden;
    transition: all 0.3s;

    &:hover {
      border-color: #2a9eff;
      box-shadow: 0 4px 12px rgba(42, 158, 255, 0.1);
    }

    .application-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 15px 20px;
      background: #f8f9fa;
      border-bottom: 1px solid #eee;

      .application-info {
        display: flex;
        gap: 20px;

        .info-item {
          .label {
            color: #999;
            font-size: 13px;
          }
          .value {
            color: #333;
            font-size: 13px;
          }
        }
      }

      .status-tag {
        padding: 4px 12px;
        border-radius: 4px;
        font-size: 13px;
        font-weight: 500;

        &.processing {
          background: #fff7e6;
          color: #ff9f43;
        }

        &.approved {
          background: #e6f9e6;
          color: #3dc33d;
        }

        &.rejected {
          background: #ffe6e6;
          color: #ff4d4f;
        }
      }
    }

    .application-body {
      padding: 20px;

      .info-row {
        display: flex;
        gap: 30px;
        margin-bottom: 15px;

        &:last-child {
          margin-bottom: 0;
        }

        .info-item {
          display: flex;
          align-items: center;
          gap: 8px;

          &.full-width {
            flex: 1;
          }

          .label {
            color: #666;
            font-size: 14px;
            min-width: 70px;
          }

          .value {
            color: #333;
            font-size: 14px;

            &.type-tag {
              padding: 2px 10px;
              border-radius: 4px;
              font-size: 13px;

              &.refund {
                background: #e6f7ff;
                color: #2a9eff;
              }

              &.report-merchant {
                background: #fff2e8;
                color: #ff7a45;
              }

              &.report-product {
                background: #f6ffed;
                color: #52c41a;
              }

              &.delete-comment {
                background: #f0f0f0;
                color: #666;
              }
            }

            &.reason {
              flex: 1;
              word-break: break-all;
              line-height: 1.6;
            }
          }
        }
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 30px;
    padding-top: 20px;
    border-top: 1px solid #eee;
  }
}
</style>
