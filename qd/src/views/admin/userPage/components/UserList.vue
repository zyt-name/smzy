<template>
  <div class="user-list-container">
    <el-card shadow="never" class="full-height-card">
      <template #header>
        <div class="card-header">
          <span>所有用户</span>
          <el-input
            v-model="searchQuery"
            placeholder="搜索用户名/手机号"
            style="width: 200px"
            prefix-icon="Search"
            clearable
          />
        </div>
      </template>

      <div class="card-body-content" v-loading="loading">
        <div class="user-grid">
          <div v-for="user in paginatedUsers" :key="user.id" class="user-item">
            <div class="user-avatar-wrapper">
              <el-avatar :size="60" :src="user.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
            </div>
            <div class="user-info">
              <span class="user-name">{{ user.username }}</span>
              <div class="user-details">
                <span class="user-no">编号: {{ user.userNo }}</span>
                <span class="user-phone">手机: {{ user.phone }}</span>
              </div>
            </div>
            <div class="user-actions">
              <el-switch
                v-model="user.status"
                :active-value="0"
                :inactive-value="1"
                active-text="开启"
                inactive-text="禁用"
                inline-prompt
                style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
                :before-change="() => handleStatusChange(user)"
              />
            </div>
          </div>
        </div>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="totalUsers"
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
import { User, Search } from '@element-plus/icons-vue'
import userAvatar from '../../../../assets/user.png'
import { showUsers, banOrUnbanUser } from '../../../../api/admin/AdminManagement'

const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(15)
const totalUsers = ref(0)
const loading = ref(false)

// 用户数据
const users = ref([])

// 封禁/解封对话框相关
const reasonDialogVisible = ref(false)
const dialogLoading = ref(false)
const dialogTitle = ref('')
const reasonForm = ref({
  reason: ''
})
const currentUser = ref(null)
const currentOperateType = ref(1)

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await showUsers(currentPage.value, pageSize.value)
    if (res.code === 200) {
      users.value = res.data.map(user => ({
        ...user,
        avatar: userAvatar,
        status: user.status || 0
      }))
      totalUsers.value = 45
    } else {
      ElMessage.error(res.msg || '获取用户列表失败')
    }
  } catch (error) {
    console.error('Fetch users error:', error)
    ElMessage.error('网络请求失败')
  } finally {
    loading.value = false
  }
}

// 打开封禁/解封原因输入对话框
const handleStatusChange = async (user) => {
  const operateType = user.status === 0 ? 1 : 2
  const actionText = operateType === 1 ? '禁用' : '开启'
  
  // 先确认操作
  try {
    await ElMessageBox.confirm(
      `确定要${actionText}用户 "${user.username}" 吗？`,
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
  currentUser.value = user
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
    const res = await banOrUnbanUser({
      targetId: currentUser.value.id,
      reason: reasonForm.value.reason.trim(),
      operateType: currentOperateType.value
    })
    
    if (res.code === 200) {
      const actionText = currentOperateType.value === 1 ? '禁用' : '开启'
      ElMessage.success(`${actionText}成功`)
      reasonDialogVisible.value = false
      // 更新本地状态
      currentUser.value.status = currentOperateType.value === 1 ? 1 : 0
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
  fetchUsers()
})

watch(currentPage, () => {
  fetchUsers()
})

const paginatedUsers = computed(() => {
  if (!searchQuery.value) return users.value
  return users.value.filter(u => 
    u.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    (u.phone && u.phone.includes(searchQuery.value)) ||
    (u.userNo && u.userNo.includes(searchQuery.value))
  )
})
</script>

<style lang="scss" scoped>
.user-list-container {
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

.user-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 15px;
  padding: 10px 0;
  overflow-y: auto;

  .user-item {
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

    .user-avatar-wrapper {
      position: relative;
      margin-bottom: 12px;

      :deep(.el-avatar) {
        background: transparent;
      }
    }

    .user-info {
      text-align: center;
      margin-bottom: 15px;
      .user-name {
        display: block;
        font-weight: 600;
        font-size: 18px;
        color: #333;
        margin-bottom: 6px;
      }
      .user-details {
        display: flex;
        flex-direction: column;
        gap: 2px;
        .user-no, .user-phone {
          font-size: 11px;
          color: #909399;
          line-height: 1.2;
        }
      }
    }

    .user-actions {
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
