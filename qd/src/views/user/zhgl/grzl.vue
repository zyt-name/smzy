<template>
  <div class="content-section">
    <h3 class="section-title">个人资料</h3>
    <el-form :model="userForm" label-width="100px" class="profile-form">
      <el-form-item label="用户名">
        <el-input v-model="userForm.userName" disabled />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="userForm.password" type="password" placeholder="请输入新密码，不修改请留空" show-password />
      </el-form-item>
      <el-form-item label="手机号码">
        <el-input v-model="userForm.phone" placeholder="请输入手机号码" />
      </el-form-item>
      <el-form-item>
        <ActionButton type="primary" size="large" @click="saveProfile">保存修改</ActionButton>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { updateUser } from '../../../api/User'
import ActionButton from '../../../components/ActionButton.vue'

const userForm = ref({
  userName: '',
  password: '',
  phone: ''
})

onMounted(() => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    const userInfo = JSON.parse(info)
    userForm.value.userName = userInfo.userName || ''
  }
})

const saveProfile = async () => {
  try {
    await updateUser({
      password: userForm.value.password || undefined,
      phone: userForm.value.phone
    })
    ElMessage.success('个人资料已更新')
  } catch (error) {
    console.error('更新个人资料失败:', error)
    ElMessage.error('更新失败，请重试')
  }
}
</script>

<style lang="scss" scoped>
.content-section {
  .section-title {
    font-size: 20px;
    color: #333;
    margin-bottom: 30px;
    font-weight: 600;
  }

  .profile-form {
    max-width: 500px;
  }
}
</style>
