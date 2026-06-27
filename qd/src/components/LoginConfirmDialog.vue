<template>
  <el-dialog
    v-model="visible"
    title="登录提示"
    width="400px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :show-close="false"
    center
  >
    <div class="login-confirm-content">
      <el-icon class="warning-icon" :size="50" color="#E6A23C">
        <WarningFilled />
      </el-icon>
      <p class="message">未登录账号只能浏览部分页面，是否前往进行登录？</p>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确认登录</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { WarningFilled } from '@element-plus/icons-vue'

const visible = ref(false)
let resolvePromise = null

const show = () => {
  return new Promise((resolve) => {
    resolvePromise = resolve
    visible.value = true
  })
}

const handleConfirm = () => {
  visible.value = false
  if (resolvePromise) {
    resolvePromise(true)
    resolvePromise = null
  }
}

const handleCancel = () => {
  visible.value = false
  if (resolvePromise) {
    resolvePromise(false)
    resolvePromise = null
  }
}

defineExpose({
  show
})
</script>

<style scoped>
.login-confirm-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.warning-icon {
  margin-bottom: 20px;
}

.message {
  font-size: 16px;
  color: #333;
  text-align: center;
  margin: 0;
  line-height: 1.6;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>
