<template>
  <div class="application-management">
    <el-card shadow="never" class="full-height-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span>申请管理</span>
            <el-select v-model="filter.requestType" placeholder="申请类型" style="width: 160px; margin-left: 20px" clearable>
              <el-option label="申请删除评论" :value="4" />
              <el-option label="申请恢复商品" :value="5" />
            </el-select>
            <el-select v-model="filter.requestStatus" placeholder="申请状态" style="width: 140px; margin-left: 10px" clearable>
              <el-option label="处理中" :value="1" />
              <el-option label="已通过" :value="2" />
              <el-option label="已驳回" :value="3" />
            </el-select>
          </div>
          <div class="header-right">
            <ActionButton type="primary" @click="handleSearch">查询</ActionButton>
            <ActionButton @click="handleReset">重置</ActionButton>
          </div>
        </div>
      </template>

      <div class="card-body-content">
        <div class="table-wrapper">
          <el-table
            :data="applicationList"
            v-loading="loading"
            style="width: 100%"
            :header-cell-style="{ background: '#f5f7fa', fontWeight: 'bold' }"
          >
            <el-table-column prop="id" label="申请ID" width="100" />
            <el-table-column label="申请类型" width="140">
              <template #default="{ row }">
                <el-tag :type="getTypeTag(row.requestType)">
                  {{ getTypeText(row.requestType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="目标类型" width="100">
              <template #default="{ row }">
                {{ getTargetTypeText(row.targetType) }}
              </template>
            </el-table-column>
            <el-table-column prop="targetId" label="目标ID" min-width="200" show-overflow-tooltip />
            <el-table-column prop="phone" label="联系电话" width="120" />
            <el-table-column label="申请状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTag(row.requestStatus)">
                  {{ getStatusText(row.requestStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="申请时间" width="180">
              <template #default="{ row }">
                {{ formatTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <ActionButton type="primary" size="small" @click="handleViewDetail(row)">详情</ActionButton>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pageNum"
            :page-size="5"
            :total="total"
            layout="prev, pager, next, jumper, total"
            background
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>

    <!-- 申请详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="申请详情"
      width="500px"
      center
      align-center
      destroy-on-close
    >
      <div v-if="currentApplication" class="detail-content">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="申请ID">{{ currentApplication.id }}</el-descriptions-item>
          <el-descriptions-item label="申请类型">
            <el-tag :type="getTypeTag(currentApplication.requestType)">
              {{ getTypeText(currentApplication.requestType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="目标类型">
            {{ getTargetTypeText(currentApplication.targetType) }}
          </el-descriptions-item>
          <el-descriptions-item label="目标ID">{{ currentApplication.targetId }}</el-descriptions-item>
          <el-descriptions-item label="申请状态">
            <el-tag :type="getStatusTag(currentApplication.requestStatus)">
              {{ getStatusText(currentApplication.requestStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentApplication.phone || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ formatTime(currentApplication.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="申请理由">
            {{ currentApplication.reason || '未填写' }}
          </el-descriptions-item>
          <el-descriptions-item label="反馈" v-if="currentApplication.feedback">
            {{ currentApplication.feedback }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getApplicationList } from '../../../api/merchant/MerchantApplication'
import ActionButton from '../../../components/ActionButton.vue'

const applicationList = ref([])
const loading = ref(false)
const pageNum = ref(1)
const total = ref(0)
const detailVisible = ref(false)
const currentApplication = ref(null)

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
      page: pageNum.value,
      size: 5
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
      total.value = res.data?.total || 0
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

const handleViewDetail = (row) => {
  currentApplication.value = row
  detailVisible.value = true
}

const getTypeText = (type) => {
  const typeMap = {
    4: '申请删除评论',
    5: '申请恢复商品'
  }
  return typeMap[type] || '未知类型'
}

const getTypeTag = (type) => {
  const tagMap = {
    4: 'info',
    5: 'primary'
  }
  return tagMap[type] || ''
}

const getStatusText = (status) => {
  const statusMap = {
    1: '处理中',
    2: '驳回',
    3: '同意'
  }
  return statusMap[status] || '未知状态'
}

const getStatusTag = (status) => {
  const tagMap = {
    1: 'warning',
    2: 'danger',
    3: 'success'
  }
  return tagMap[status] || ''
}

const getTargetTypeText = (type) => {
  const typeMap = {
    3: '商品',
    4: '评论'
  }
  return typeMap[type] || '未知'
}

const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).replace(/\//g, '-')
}
</script>

<style lang="scss" scoped>
.application-management {
  padding: 0;
  height: 100%;

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
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .header-right {
      display: flex;
      gap: 10px;
    }
  }

  .card-body-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .table-wrapper {
      flex: 1;
      overflow: auto;
    }

    .pagination-wrapper {
      display: flex;
      justify-content: flex-end;
      margin-top: 20px;
      padding-top: 15px;
      border-top: 1px solid #f0f2f5;
    }
  }

  .detail-content {
    padding: 10px 0;
  }
}
</style>
