<template>
  <div class="register-container">
    <div class="register-content">
      <!-- Left: Brand/Image Decoration (Same as Login) -->
      <div class="register-left">
        <div class="brand">
          <div class="brand-logo">智码<span>数云</span></div>
          <div class="brand-slogan">智领数码 · 云集好物</div>
        </div>
        <div class="illustration">
          <el-icon size="180" color="rgba(42, 158, 255, 0.15)"><Monitor /></el-icon>
          <div class="floating-icons">
            <el-icon class="icon-1"><Iphone /></el-icon>
            <el-icon class="icon-2"><Cpu /></el-icon>
            <el-icon class="icon-3"><Headset /></el-icon>
          </div>
        </div>
      </div>

      <!-- Right: Register Form -->
      <div class="register-right">
        <div class="register-box">
          <h2 class="register-title">新用户注册</h2>
          <el-form :model="registerForm" :rules="rules" ref="registerFormRef" size="large">
            <el-form-item prop="username">
              <el-input 
                v-model="registerForm.username" 
                placeholder="设置用户名"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input 
                v-model="registerForm.phone" 
                placeholder="手机号"
                prefix-icon="Iphone"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="registerForm.password" 
                type="password" 
                placeholder="设置密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input 
                v-model="registerForm.confirmPassword" 
                type="password" 
                placeholder="确认密码"
                prefix-icon="CircleCheck"
                show-password
              />
            </el-form-item>
            
            <div class="agreement">
              <el-checkbox v-model="registerForm.agree">
                阅读并同意 <span class="link">《用户服务协议》</span> 和 <span class="link">《隐私政策》</span>
              </el-checkbox>
            </div>

            <el-button type="primary" class="register-btn" :loading="loading" @click="handleRegister">
              立即注册
            </el-button>

            <div class="login-redirect">
              <span>已有账号？</span>
              <span class="to-login" @click="emit('toLogin')">直接登录</span>
            </div>
            
            <div class="merchant-redirect">
              <span class="to-merchant" @click="emit('toMerchantRegister')">我是商户，前去申请</span>
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
import { userRegister } from '../../../api/Login'

const emit = defineEmits(['toLogin', 'toUser', 'toMerchantRegister'])
const loading = ref(false)
const registerFormRef = ref(null)

const registerForm = reactive({
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
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请设置密码', trigger: 'blur' }],
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
        await userRegister({
          username: registerForm.username,
          phone: registerForm.phone,
          password: registerForm.password
        })
        ElMessage.success('注册成功！即将前往登录')
        setTimeout(() => {
          emit('toLogin')
        }, 1500)
      } catch (error) {
        ElMessage.error(error.message || '注册失败，请稍后重试')
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

    .code-item {
      :deep(.el-input-group__append) {
        padding: 0;
        background-color: transparent;
        border: none;
        .send-code {
          color: #2a9eff;
          border: none;
          background: transparent;
          padding: 0 15px;
          height: 100%;
          font-size: 13px;
          &:hover { color: #1a8ef5; }
        }
      }
    }

    .agreement {
      margin-bottom: 20px;
      :deep(.el-checkbox__label) {
        font-size: 12px;
        color: #888;
      }
      .link {
        color: #2a9eff;
        cursor: pointer;
      }
    }

    .register-btn {
      width: 100%;
      height: 45px;
      font-size: 16px;
      border-radius: 8px;
      background: #2a9eff;
      border: none;
      margin-bottom: 20px;
      &:hover {
        background: #1a8ef5;
      }
    }

    .login-redirect {
      text-align: center;
      font-size: 14px;
      color: #666;
      margin-bottom: 10px;
      .to-login {
        color: #2a9eff;
        cursor: pointer;
        margin-left: 5px;
        font-weight: 500;
        &:hover { color: #1a8ef5; }
      }
    }

    .merchant-redirect {
      text-align: center;
      font-size: 13px;
      .to-merchant {
        color: #888;
        cursor: pointer;
        transition: color 0.3s;
        &:hover { color: #2a9eff; }
      }
    }
  }
}

.simple-footer {
  margin-top: 30px;
  text-align: center;
  color: #999;
  font-size: 12px;
  p { margin-bottom: 5px; }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}
</style>
