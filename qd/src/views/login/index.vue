<template>
  <div class="login-container">
    <div class="login-content">
      <!-- Left: Brand/Image Decoration -->
      <div class="login-left">
        <div class="brand">
          <div class="brand-logo">智码<span>数云</span></div>
          <div class="brand-slogan">智领数码 · 云集好物</div>
        </div>
        <div class="illustration">
          <!-- Placeholder for a tech illustration -->
          <el-icon size="180" color="rgba(42, 158, 255, 0.15)"><Monitor /></el-icon>
          <div class="floating-icons">
            <el-icon class="icon-1"><Iphone /></el-icon>
            <el-icon class="icon-2"><Cpu /></el-icon>
            <el-icon class="icon-3"><Headset /></el-icon>
          </div>
        </div>
      </div>

      <!-- Right: Login Form -->
      <div class="login-right">
        <div class="login-box">
          <h2 class="login-title">欢迎登录</h2>
          <el-form :model="loginForm" :rules="rules" ref="loginFormRef" size="large">
            <el-form-item prop="username">
              <el-input 
                v-model="loginForm.username" 
                placeholder="账号/手机号/邮箱"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            
            <div class="login-options">
              <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
              <span class="forget-pwd">忘记密码？</span>
            </div>

            <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
              立即登录
            </el-button>

            <div class="browse-without-login" @click="emit('toUser')">
              暂时不登录，先浏览 <el-icon><ArrowRight /></el-icon>
            </div>

            <div class="login-footer">
              <span class="register-link" @click="emit('toRegister')">还没有账号？立即注册</span>
              <div class="third-party">
                <p>其他登录方式</p>
                <div class="icons">
                  <div class="icon-wrapper"><el-icon><ChatDotRound /></el-icon></div>
                  <div class="icon-wrapper"><el-icon><Postcard /></el-icon></div>
                </div>
              </div>
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
import { ElMessageBox } from 'element-plus'
import { adminLogin, merchantLogin, userLogin } from '../../api/Login'
import { add } from '../../utils/notify'

const emit = defineEmits(['toUser', 'toRegister', 'loginSuccess'])

const loading = ref(false)
const loginFormRef = ref(null)

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const { username, password } = loginForm
        let loginApi
        let loginData = { password }

        // 根据前三位判断登录类型
        if (username.startsWith('100')) {
          loginApi = adminLogin
          loginData.adminNo = username
        } else if (username.startsWith('200')) {
          loginApi = merchantLogin
          loginData.merchantNo = username
        } else if (username.startsWith('300')) {
          loginApi = userLogin
          loginData.userNo = username
        } else {
          add('无效的账号格式，请检查账号前缀', 'error')
          loading.value = false
          return
        }

        const res = await loginApi(loginData)

        // 处理账号被封禁的情况（code 为 403）
        if (res.code === 403) {
          ElMessageBox.alert(res.msg || '该账户存在违规行为，请联系管理员！', '账号封禁提示', {
            confirmButtonText: '确定',
            type: 'warning',
            showClose: false,
            closeOnClickModal: false,
            closeOnPressEscape: false
          })
          loading.value = false
          return
        }
        
        if (res.code === 200) {
          // 保存 token
          localStorage.setItem('token', res.data.token)
          // 保存用户信息
          localStorage.setItem('userInfo', JSON.stringify(res.data))
          
          add('登录成功', 'success')
          
          // 解析 pagePath 决定跳转页面
          let targetPage = 'user' // 默认跳转到用户首页
          const path = res.data.pagePath || ''
          
          if (path === 'src/views/admin/index.vue') {
            targetPage = 'admin'
          } else if (path === 'src/views/merchant/index.vue') {
            targetPage = 'merchant'
          } else if (path === 'src/views/user/index.vue') {
            targetPage = 'user'
          } else if (path.includes('admin')) {
            targetPage = 'admin'
          } else if (path.includes('merchant')) {
            targetPage = 'merchant'
          }

          setTimeout(() => {
            emit('loginSuccess', targetPage)
          }, 1000)
        } else {
          add(res.msg || '登录失败', 'error')
        }
      } catch (error) {
        console.error('Login Error:', error)
        // 处理403账号被封禁的情况
        if (error.response && error.response.status === 403) {
          const errorMsg = error.response.data?.msg || '该账户存在违规行为，请联系管理员！'
          ElMessageBox.alert(errorMsg, '账号封禁提示', {
            confirmButtonText: '确定',
            type: 'warning',
            showClose: false,
            closeOnClickModal: false,
            closeOnPressEscape: false
          })
        } else {
          add('网络错误或服务器异常', 'error')
        }
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  background-color: #EDF4FD;
  background-image: url('../../assets/bj.png');
  background-repeat: no-repeat;
  background-position: center;
  background-size: cover;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.login-content {
  width: 900px;
  height: 500px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  display: flex;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.login-left {
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

.login-right {
  width: 450px;
  background: #fff;
  padding: 40px 50px;
  display: flex;
  align-items: center;

  .login-box {
    width: 100%;
    
    .login-title {
      font-size: 24px;
      color: #333;
      margin-bottom: 30px;
      font-weight: 600;
    }

    .login-options {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      .forget-pwd {
        font-size: 13px;
        color: #888;
        cursor: pointer;
        &:hover { color: #2a9eff; }
      }
    }

    .login-btn {
        width: 100%;
        height: 45px;
        font-size: 16px;
        border-radius: 8px;
        background: #2a9eff;
        border: none;
        margin-bottom: 15px;
        &:hover {
          background: #1a8ef5;
        }
      }

      .browse-without-login {
        text-align: center;
        font-size: 13px;
        color: #999;
        cursor: pointer;
        margin-bottom: 25px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 4px;
        transition: color 0.3s;
        &:hover {
          color: #2a9eff;
        }
      }

      .login-footer {
      .register-link {
        display: block;
        text-align: center;
        font-size: 14px;
        color: #2a9eff;
        cursor: pointer;
        margin-bottom: 30px;
      }

      .third-party {
        text-align: center;
        p {
          font-size: 12px;
          color: #bbb;
          position: relative;
          margin-bottom: 15px;
          &::before, &::after {
            content: '';
            position: absolute;
            top: 50%;
            width: 60px;
            height: 1px;
            background: #eee;
          }
          &::before { left: 0; }
          &::after { right: 0; }
        }
        .icons {
          display: flex;
          justify-content: center;
          gap: 20px;
          .icon-wrapper {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            border: 1px solid #eee;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #888;
            cursor: pointer;
            transition: all 0.3s;
            &:hover {
              border-color: #2a9eff;
              color: #2a9eff;
              background: rgba(42, 158, 255, 0.05);
            }
          }
        }
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
