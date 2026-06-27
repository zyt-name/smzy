<template>
  <el-card shadow="never" class="full-height-card">
    <template #header>
      <div class="card-header">
        <div class="header-left">
          <span>请求管理</span>
        </div>
      </div>
    </template>

    <div class="search-area">
      <el-select
        v-model="searchForm.requesterType"
        placeholder="请求人类型"
        style="width: 150px"
        clearable
        @change="handleRequesterTypeChange"
      >
        <el-option label="用户" :value="1" />
        <el-option label="商家" :value="2" />
      </el-select>
      <el-input
        v-model="searchForm.userId"
        placeholder="用户/商家ID"
        style="width: 150px; margin-left: 10px"
        clearable
        :disabled="!searchForm.requesterType"
        @keyup.enter="handleSearch"
      />
      <el-select
        v-model="searchForm.requestStatus"
        placeholder="请求状态"
        style="width: 150px; margin-left: 10px"
        clearable
      >
        <el-option label="处理中" :value="1" />
        <el-option label="驳回" :value="2" />
        <el-option label="同意" :value="3" />
      </el-select>
      <el-select
        v-model="searchForm.requestType"
        placeholder="请求类型"
        style="width: 150px; margin-left: 10px"
        clearable
      >
        <el-option label="退款" :value="1" />
        <el-option label="举报商家" :value="2" />
        <el-option label="举报商品" :value="3" />
        <el-option label="申请删除评论" :value="4" />
        <el-option label="申请恢复商品" :value="5" />
      </el-select>
      <el-select
        v-model="searchForm.targetType"
        placeholder="目标ID类型"
        style="width: 150px; margin-left: 10px"
        clearable
        @change="handleTargetTypeChange"
      >
        <el-option label="订单" :value="1" />
        <el-option label="商家" :value="2" />
        <el-option label="商品" :value="3" />
        <el-option label="评论" :value="4" />
      </el-select>
      <el-input
        v-model="searchForm.targetId"
        placeholder="目标ID"
        style="width: 150px; margin-left: 10px"
        clearable
        :disabled="!searchForm.targetType"
        @keyup.enter="handleSearch"
      />
      <ActionButton type="primary" style="margin-left: 10px" @click="handleSearch">
        搜索
      </ActionButton>
      <ActionButton style="margin-left: 10px" @click="handleReset">
        重置
      </ActionButton>
    </div>

    <div class="card-body-content">
      <el-table :data="applications" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户/商家ID" width="120" />
        <el-table-column label="请求人类型" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.requesterType === 1 ? 'primary' : 'success'" size="small">
              {{ scope.row.requesterType === 1 ? '用户' : '商家' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="targetId" label="目标ID" width="120" />
        <el-table-column label="目标类型" width="100">
          <template #default="scope">
            <el-tag size="small">{{ getTargetTypeName(scope.row.targetType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请求类型" width="120">
          <template #default="scope">
            <el-tag :type="getRequestTypeTag(scope.row.requestType)" size="small">
              {{ getRequestTypeName(scope.row.requestType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.requestStatus)" size="small">
              {{ getStatusName(scope.row.requestStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="申请理由" min-width="200" show-overflow-tooltip />
        <el-table-column prop="feedback" label="反馈" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-dropdown
              v-if="shouldShowHandleButton(scope.row)"
              @command="(command) => handleDropdownCommand(command, scope.row)"
            >
              <ActionButton type="primary" size="small">
                处理
              </ActionButton>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="3">同意</el-dropdown-item>
                  <el-dropdown-item :command="2">驳回</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="fetchApplications"
          @size-change="fetchApplications"
        />
      </div>
    </div>

    <el-dialog
      v-model="processDialogVisible"
      :title="processForm.requestStatus === 2 ? '驳回请求' : '同意请求'"
      width="500px"
    >
      <div class="process-form">
        <p><strong>请求ID：</strong>{{ currentApplication?.id }}</p>
        <p><strong>请求类型：</strong>{{ getRequestTypeName(currentApplication?.requestType) }}</p>
        <p><strong>申请理由：</strong>{{ currentApplication?.reason }}</p>
        <el-divider />
        <el-form :model="processForm" label-width="80px">
          <el-form-item label="反馈内容">
            <el-input
              v-model="processForm.feedback"
              type="textarea"
              :rows="4"
              placeholder="请输入反馈内容"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="processDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmProcess">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getApplicationList, processApplication } from '../../../api/admin/AdminApplication'
import ActionButton from '../../../components/ActionButton.vue'

const applications = ref([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  requesterType: null,
  userId: null,
  requestStatus: null,
  requestType: null,
  targetType: null,
  targetId: null
})

const processDialogVisible = ref(false)
const currentApplication = ref(null)
const processForm = reactive({
  id: null,
  feedback: '',
  requestStatus: null
})

const fetchApplications = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    
    if (searchForm.requesterType) params.requesterType = searchForm.requesterType
    if (searchForm.userId) params.userId = searchForm.userId
    if (searchForm.requestStatus) params.requestStatus = searchForm.requestStatus
    if (searchForm.requestType) params.requestType = searchForm.requestType
    if (searchForm.targetType) params.targetType = searchForm.targetType
    if (searchForm.targetId) params.targetId = searchForm.targetId

    const res = await getApplicationList(params)
    if (res.code === 200 || res.code === 1) {
      applications.value = res.data?.data || []
      total.value = res.data?.total || 0
    } else {
      ElMessage.error(res.msg || '获取请求列表失败')
    }
  } catch (error) {
    console.error('获取请求列表失败:', error)
    ElMessage.error('获取请求列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchApplications()
}

const handleReset = () => {
  searchForm.requesterType = null
  searchForm.userId = null
  searchForm.requestStatus = null
  searchForm.requestType = null
  searchForm.targetType = null
  searchForm.targetId = null
  currentPage.value = 1
  fetchApplications()
}

const handleRequesterTypeChange = () => {
  searchForm.userId = null
}

const handleTargetTypeChange = () => {
  searchForm.targetId = null
}

const getTargetTypeName = (type) => {
  const types = {
    1: '订单',
    2: '商家',
    3: '商品',
    4: '评论'
  }
  return types[type] || '未知'
}

const getRequestTypeName = (type) => {
  const types = {
    1: '退款',
    2: '举报商家',
    3: '举报商品',
    4: '申请删除评论',
    5: '申请恢复商品'
  }
  return types[type] || '未知'
}

const getRequestTypeTag = (type) => {
  const tags = {
    1: 'warning',
    2: 'danger',
    3: 'danger',
    4: 'info',
    5: 'primary'
  }
  return tags[type] || 'info'
}

const getStatusName = (status) => {
  const statuses = {
    1: '处理中',
    2: '驳回',
    3: '同意'
  }
  return statuses[status] || '未知'
}

const getStatusTag = (status) => {
  const tags = {
    1: 'warning',
    2: 'danger',
    3: 'success'
  }
  return tags[status] || 'info'
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const shouldShowHandleButton = (row) => {
  if (row.requestStatus !== 1) {
    return false
  }
  
  if (row.requestType === 1) {
    const createTime = new Date(row.createTime)
    const now = new Date()
    const diffInHours = (now - createTime) / (1000 * 60 * 60)
    return diffInHours > 24
  }
  
  return true
}

const handleDropdownCommand = (command, row) => {
  currentApplication.value = row
  processForm.id = row.id
  processForm.requestStatus = command
  processForm.feedback = ''
  processDialogVisible.value = true
}

const confirmProcess = async () => {
  if (!processForm.feedback.trim()) {
    ElMessage.warning('请输入反馈内容')
    return
  }

  try {
    const res = await processApplication({
      id: processForm.id,
      feedback: processForm.feedback,
      requestStatus: processForm.requestStatus
    })
    if (res.code === 200 || res.code === 1) {
      ElMessage.success('处理成功')
      processDialogVisible.value = false
      fetchApplications()
    } else {
      ElMessage.error(res.msg || '处理失败')
    }
  } catch (error) {
    console.error('处理失败:', error)
    ElMessage.error('处理失败')
  }
}

onMounted(() => {
  fetchApplications()
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

.search-area {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.process-form {
  p {
    margin-bottom: 10px;
    font-size: 14px;
    color: #606266;
  }
}
</style>
