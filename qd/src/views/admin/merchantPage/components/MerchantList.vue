<template>
  <div class="merchant-list-container">
    <el-card shadow="never" class="full-height-card">
      <template #header>
        <div class="card-header">
          <span>所有商家</span>
          <el-input
            v-model="searchQuery"
            placeholder="搜索商家名称/联系人"
            style="width: 200px"
            prefix-icon="Search"
            clearable
          />
        </div>
      </template>

      <div class="card-body-content" v-loading="loading">
        <div class="merchant-grid">
          <div v-for="merchant in paginatedMerchants" :key="merchant.id" class="merchant-item">
            <div class="merchant-avatar-wrapper">
              <el-avatar :size="60" :src="merchant.logo">
                <el-icon><Shop /></el-icon>
              </el-avatar>
            </div>
            <div class="merchant-info">
              <span class="merchant-name">{{ merchant.username }}</span>
              <div class="merchant-details">
                <span class="merchant-no">编号: {{ merchant.merchantNo }}</span>
                <span class="merchant-phone">手机: {{ merchant.phone || '暂无' }}</span>
              </div>
            </div>
            <div class="merchant-actions">
              <el-switch
                v-model="merchant.status"
                :active-value="0"
                :inactive-value="1"
                active-text="开启"
                inactive-text="禁用"
                inline-prompt
                style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
                :before-change="() => handleStatusChange(merchant)"
              />
            </div>
          </div>
        </div>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="totalMerchants"
            layout="prev, pager, next"
            background
          />
        </div>
      </div>
    </el-card>

    <!-- 封禁/解封原因输入对话框 -->
    <el-dialog
      v-model="reasonDialogVisible"
      :title="dialogTitle"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form :model="reasonForm" label-position="top">
        <el-form-item label="请输入原因" required>
          <el-input
            v-model="reasonForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入封禁/解封原因..."
            maxlength="255"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reasonDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmStatusChange" :loading="dialogLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Shop, Search } from '@element-plus/icons-vue'
import merchantAvatar from '../../../../assets/merchant.png'
import { showMerchants, banOrUnbanMerchant } from '../../../../api/admin/AdminManagement'

const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(15)
const totalMerchants = ref(0)
const loading = ref(false)

// 商家数据
const merchants = ref([])

// 封禁/解封对话框相关
const reasonDialogVisible = ref(false)
const dialogLoading = ref(false)
const dialogTitle = ref('')
const reasonForm = ref({
  reason: ''
})
const currentMerchant = ref(null)
const currentOperateType = ref(1)

const fetchMerchants = async () => {
  loading.value = true
  try {
    const res = await showMerchants(currentPage.value, pageSize.value)
    if (res.code === 200) {
      merchants.value = res.data.map(m => ({
        ...m,
        logo: merchantAvatar,
        status: m.status // 0开启，1禁用
      }))
      totalMerchants.value = 32 // 模拟总数
    } else {
      ElMessage.error(res.msg || '获取商家列表失败')
    }
  } catch (error) {
    console.error('Fetch merchants error:', error)
    ElMessage.error('网络请求失败')
  } finally {
    loading.value = false
  }
}

// 打开封禁/解封原因输入对话框
const handleStatusChange = async (merchant) => {
  const operateType = merchant.status === 0 ? 1 : 2
  const actionText = operateType === 1 ? '禁用' : '开启'
  
  // 先确认操作
  try {
    await ElMessageBox.confirm(
      `确定要${actionText}商家 "${merchant.username}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch (error) {
    return false
  }
  
  // 保存当前操作信息
  currentMerchant.value = merchant
  currentOperateType.value = operateType
  dialogTitle.value = `请输入${actionText}原因`
  reasonForm.value.reason = ''
  reasonDialogVisible.value = true
  
  return false
}

// 确认封禁/解封操作
const confirmStatusChange = async () => {
  if (!reasonForm.value.reason.trim()) {
    ElMessage.warning('请输入原因')
    return
  }
  
  dialogLoading.value = true
  try {
    const res = await banOrUnbanMerchant({
      targetId: currentMerchant.value.id,
      reason: reasonForm.value.reason.trim(),
      operateType: currentOperateType.value
    })
    
    if (res.code === 200) {
      const actionText = currentOperateType.value === 1 ? '禁用' : '开启'
      ElMessage.success(`${actionText}成功`)
      reasonDialogVisible.value = false
      // 更新本地状态
      currentMerchant.value.status = currentOperateType.value === 1 ? 1 : 0
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('Status change error:', error)
    ElMessage.error('操作失败')
  } finally {
    dialogLoading.value = false
  }
}

onMounted(() => {
  fetchMerchants()
})

watch(currentPage, () => {
  fetchMerchants()
})

const paginatedMerchants = computed(() => {
  if (!searchQuery.value) return merchants.value
  return merchants.value.filter(m => 
    m.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    (m.merchantNo && m.merchantNo.includes(searchQuery.value))
  )
})
</script>

<style lang="scss" scoped>
.merchant-list-container {
  flex: 1;
  display: flex;
}

.full-height-card {
  width: 100%;
  height: 100%;
  border-radius: 12px;
  border: none;
  display: flex;
  flex-direction: column;
  
  :deep(.el-card__body) {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
    overflow: hidden;
  }
}

.card-body-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
}

.merchant-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 15px;
  padding: 10px 0;
  overflow-y: auto;

  .merchant-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 15px 10px;
    background: transparent;
    border: none;
    border-radius: 12px;
    transition: all 0.3s;
    
    &:hover {
      background: #fff;
    }

    .merchant-avatar-wrapper {
      position: relative;
      margin-bottom: 12px;

      :deep(.el-avatar) {
        background: transparent;
      }
    }

    .merchant-info {
      text-align: center;
      margin-bottom: 15px;
      .merchant-name {
        display: block;
        font-weight: 600;
        font-size: 18px;
        color: #333;
        margin-bottom: 6px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        width: 120px;
      }
      .merchant-details {
        display: flex;
        flex-direction: column;
        gap: 2px;
        .merchant-no, .merchant-phone {
          font-size: 11px;
          color: #909399;
          line-height: 1.2;
        }
      }
    }

    .merchant-actions {
      display: flex;
      gap: 10px;
    }
  }
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
