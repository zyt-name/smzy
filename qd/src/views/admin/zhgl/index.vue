<template>
  <div class="zhgl-container">
    <el-row :gutter="20">
      <!-- Left: Profile Summary -->
      <el-col :span="8">
        <el-card shadow="never" class="profile-card">
          <div class="user-profile">
            <div class="avatar-section">
              <el-avatar :size="100" :src="adminForm.avatar || adminAvatar">
                <el-icon><User /></el-icon>
              </el-avatar>
            </div>
            <div class="user-info">
              <h3>{{ currentAdminName || '系统管理员' }}</h3>
              <p class="role-tag">超级管理员</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- Right: Detailed Settings -->
      <el-col :span="16">
        <el-card shadow="never" class="settings-card">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="账号设置" name="basic">
              <el-form :model="adminForm" label-position="top" class="settings-form">
                <el-row :gutter="20">
                  <el-col :span="24">
                    <el-form-item label="登录账号">
                      <el-input v-model="adminForm.username" placeholder="请输入新的登录账号" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-form-item>
                  <el-button type="primary" @click="handleSaveProfile" :loading="loading">保存修改</el-button>
                  <el-button @click="resetForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="安全设置" name="security">
              <div class="security-list">
                <div class="security-item">
                  <div class="item-info">
                    <div class="item-title">登录密码</div>
                    <div class="item-desc">安全性高的密码由字母、数字和符号组成</div>
                  </div>
                  <el-button type="primary" link @click="showPasswordDialog = true">修改密码</el-button>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <!-- Password Change Dialog -->
    <el-dialog v-model="showPasswordDialog" title="修改登录密码" width="400px" append-to-body>
      <el-form :model="passwordForm" label-position="top">
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdatePassword" :loading="passLoading">确定修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import adminAvatar from '../../../assets/admin.png'
import { updateAdminInfo } from '../../../api/admin/AdminManagement'

const activeTab = ref('basic')
const showPasswordDialog = ref(false)
const loading = ref(false)
const passLoading = ref(false)
const currentAdminName = ref('')

const adminForm = reactive({
  username: '',
  avatar: ''
})

const initialData = {
  username: ''
}

const passwordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

onMounted(() => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    const user = JSON.parse(userInfo)
    adminForm.username = user.userName || ''
    initialData.username = user.userName || ''
    currentAdminName.value = user.nickname || user.userName || '系统管理员'
  }
})

const handleSaveProfile = async () => {
  if (!adminForm.username) {
    ElMessage.warning('登录账号不能为空')
    return
  }

  // 只有当用户名发生变化时才发送请求
  if (adminForm.username === initialData.username) {
    ElMessage.info('未检测到修改')
    return
  }

  loading.value = true
  try {
    const res = await updateAdminInfo({ username: adminForm.username })
    if (res.code === 200) {
      ElMessage.success('登录账号修改成功')
      initialData.username = adminForm.username
      // 更新本地存储
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      userInfo.userName = adminForm.username
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    } else {
      ElMessage.error(res.msg || '修改失败')
    }
  } catch (error) {
    console.error('Update username error:', error)
    ElMessage.error('服务器错误')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  adminForm.username = initialData.username
}

const handleUpdatePassword = async () => {
  if (!passwordForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }

  passLoading.value = true
  try {
    const res = await updateAdminInfo({ password: passwordForm.newPassword })
    if (res.code === 200) {
      ElMessage.success('密码修改成功')
      showPasswordDialog.value = false
      // Reset form
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    } else {
      ElMessage.error(res.msg || '修改失败')
    }
  } catch (error) {
    console.error('Update password error:', error)
    ElMessage.error('服务器错误')
  } finally {
    passLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
.zhgl-container {
  padding-bottom: 20px;
}

.profile-card {
  border-radius: 12px;
  border: 1px solid #f0f2f5;
  margin-bottom: 20px;
  text-align: center;

  .user-profile {
    padding: 20px 0;

    .avatar-section {
      position: relative;
      display: inline-block;
      margin-bottom: 15px;
    }

    .user-info {
      margin-bottom: 20px;
      h3 {
        margin: 0 0 8px 0;
        font-size: 20px;
        color: #303133;
      }
      .role-tag {
        font-size: 13px;
        color: #409EFF;
        background: #ecf5ff;
        padding: 2px 10px;
        border-radius: 4px;
        display: inline-block;
      }
    }
  }
}

.settings-card {
  border-radius: 12px;
  border: 1px solid #f0f2f5;
  min-height: 400px;

  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
    background-color: #f0f2f5;
  }

  :deep(.el-tabs__item) {
    font-size: 15px;
    height: 50px;
  }
}

.settings-form {
  padding: 20px 0;
  max-width: 800px;

  :deep(.el-form-item__label) {
    font-weight: 600;
    color: #606266;
  }
}

.security-list {
  padding: 10px 0;
  .security-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 0;
    border-bottom: 1px solid #f0f2f5;

    &:last-child {
      border-bottom: none;
    }

    .item-info {
      .item-title {
        font-size: 14px;
        color: #303133;
        font-weight: 600;
        margin-bottom: 6px;
      }
      .item-desc {
        font-size: 13px;
        color: #909399;
      }
    }
  }
}
</style>
