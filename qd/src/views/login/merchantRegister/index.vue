<template>
  <div class="register-container">
    <div class="register-content">
      <!-- Left: Brand/Image Decoration -->
      <div class="register-left">
        <div class="brand">
          <div class="brand-logo">智码<span>数云</span></div>
          <div class="brand-slogan">智领数码 · 云集好物</div>
        </div>
        <div class="illustration">
          <el-icon size="180" color="rgba(42, 158, 255, 0.15)"><Shop /></el-icon>
          <div class="floating-icons">
            <el-icon class="icon-1"><Iphone /></el-icon>
            <el-icon class="icon-2"><Cpu /></el-icon>
            <el-icon class="icon-3"><Headset /></el-icon>
          </div>
        </div>
      </div>

      <!-- Right: Merchant Register Form -->
      <div class="register-right">
        <div class="register-box">
          <h2 class="register-title">商家入驻申请</h2>
          <el-form :model="registerForm" :rules="rules" ref="registerFormRef" size="large">
            <el-form-item prop="merchantName">
              <el-input 
                v-model="registerForm.merchantName" 
                placeholder="店铺/企业名称"
                prefix-icon="Shop"
              />
            </el-form-item>
            <el-form-item prop="username">
              <el-input 
                v-model="registerForm.username" 
                placeholder="管理员姓名"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input 
                v-model="registerForm.phone" 
                placeholder="联系电话"
                prefix-icon="Iphone"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="registerForm.password" 
                type="password" 
                placeholder="设置登录密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input 
                v-model="registerForm.confirmPassword" 
                type="password" 
                placeholder="确认登录密码"
                prefix-icon="CircleCheck"
                show-password
              />
            </el-form-item>
            
            <div class="agreement">
              <el-checkbox v-model="registerForm.agree">
                阅读并同意 <span class="link">《商家入驻协议》</span> 和 <span class="link">《平台经营规范》</span>
              </el-checkbox>
            </div>

            <el-button type="primary" class="register-btn" :loading="loading" @click="handleRegister">
              提交申请
            </el-button>

            <div class="login-redirect">
              <span>已有商家账号？</span>
              <span class="to-login" @click="emit('toLogin')">直接登录</span>
            </div>
          </el-form>
        </div>
      </div>
    </div>
    
    <!-- Simple Footer -->
    <div class="simple-footer">
      <p>Copyright © 2024-2026 智码数云 版权所有</p>
      <p>京ICP备xxxxxxxx号-x</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Shop, User, Iphone, Lock, CircleCheck, Cpu, Headset } from '@element-plus/icons-vue'
import { merchantRegister } from '../../../api/Login'

const emit = defineEmits(['toLogin', 'toUser'])
const loading = ref(false)
const registerFormRef = ref(null)

const registerForm = reactive({
  merchantName: '',
  username: '',
  phone: '',
  password: '',
  confirmPassword: '',
  agree: false
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  merchantName: [{ required: true, message: '请输入店铺/企业名称', trigger: 'blur' }],
  username: [{ required: true, message: '请输入管理员姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请设置登录密码', trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }]
}

const handleRegister = async () => {
  if (!registerForm.agree) {
    ElMessage.warning('请先阅读并同意相关协议')
    return
  }
  registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await merchantRegister({
          username: registerForm.username,
          phone: registerForm.phone,
          password: registerForm.password
        })
        ElMessage.success('申请提交成功！请耐心等待平台审核')
        setTimeout(() => {
          emit('toLogin')
        }, 2000)
      } catch (error) {
        ElMessage.error(error.message || '申请提交失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  background-color: #EDF4FD;
  background-image: url('../../../assets/bj.png');
  background-repeat: no-repeat;
  background-position: center;
  background-size: cover;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.register-content {
  width: 900px;
  height: 600px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  display: flex;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.register-left {
  flex: 1;
  background: linear-gradient(135deg, rgba(42, 158, 255, 0.1) 0%, rgba(42, 158, 255, 0.05) 100%);
  padding: 40px;
  display: flex;
  flex-direction: column;
  position: relative;

  .brand {
    .brand-logo {
      font-size: 28px;
      font-weight: 800;
      color: #1a1a1a;
      span { color: #2a9eff; }
    }
    .brand-slogan {
      font-size: 14px;
      color: #888;
      margin-top: 5px;
      letter-spacing: 1px;
    }
  }

  .illustration {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;

    .floating-icons {
      position: absolute;
      width: 100%;
      height: 100%;
      .el-icon {
        position: absolute;
        color: rgba(42, 158, 255, 0.3);
        font-size: 40px;
        animation: float 3s ease-in-out infinite;
      }
      .icon-1 { top: 20%; left: 10%; animation-delay: 0s; }
      .icon-2 { bottom: 20%; right: 15%; animation-delay: 1s; }
      .icon-3 { top: 40%; right: 5%; animation-delay: 0.5s; }
    }
  }
}

.register-right {
  width: 450px;
  background: #fff;
  padding: 30px 50px;
  display: flex;
  align-items: center;

  .register-box {
    width: 100%;
    
    .register-title {
      font-size: 24px;
      color: #333;
      margin-bottom: 25px;
      font-weight: 600;
    }

    .agreement {
      margin-bottom: 20px;
      :deep(.el-checkbox__label) {
        font-size: 13px;
        color: #666;
      }
      .link {
        color: #2a9eff;
        cursor: pointer;
        &:hover { text-decoration: underline; }
      }
    }

    .register-btn {
      width: 100%;
      height: 45px;
      font-size: 16px;
      font-weight: 600;
      background-color: #2a9eff;
      border: none;
      margin-bottom: 20px;
      &:hover { background-color: #1a8ef5; }
    }

    .login-redirect {
      text-align: center;
      font-size: 14px;
      color: #666;
      .to-login {
        color: #2a9eff;
        cursor: pointer;
        margin-left: 5px;
        font-weight: 500;
        &:hover { color: #1a8ef5; }
      }
    }
  }
}

.simple-footer {
  margin-top: 30px;
  text-align: center;
  color: #888;
  font-size: 12px;
  p { margin: 5px 0; }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}
</style>
