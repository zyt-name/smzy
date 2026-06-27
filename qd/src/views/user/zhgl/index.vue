<template>
  <div class="user-account-page">
    <nav class="top-nav">
      <div class="container">
        <div class="left">
          <span class="link" @click="$emit('toUser')">商城首页</span>
        </div>
        <div class="right">
          <template v-if="userInfo">
            <span class="link user-name">你好，{{ userInfo.userName || userInfo.nickname || '用户' }}</span>
            <span class="link logout-btn" @click="handleLogout">退出登录</span>
          </template>
          <template v-else>
            <span class="link" @click="$emit('toLogin')">你好，请登录</span>
            <span class="link" @click="$emit('toRegister')">免费注册</span>
          </template>
          <span class="divider">|</span>
          <span class="link" @click="emit('toAccount', 'profile')">个人中心</span>
          <span class="link" @click="emit('toScdp')">我的收藏</span>
          <span class="link" @click="emit('toOrder')">我的订单</span>
          <span class="divider">|</span>
          <span class="link">网站导航</span>
        </div>
      </div>
    </nav>

    <header class="header-area">
      <div class="container">
        <div class="logo" @click="$emit('toUser')">
          <div class="brand-logo">智码<span>数云</span></div>
          <div class="brand-slogan">智慧连接 · 码动未来</div>
        </div>
        <div class="page-title">账号管理</div>
      </div>
    </header>

    <main class="main-content container">
      <div class="account-container">
        <div class="account-sidebar">
          <div 
            class="sidebar-item" 
            :class="{ active: activeTab === 'profile' }"
            @click="activeTab = 'profile'"
          >
            <el-icon><User /></el-icon> 个人资料
          </div>
          <div 
            class="sidebar-item" 
            :class="{ active: activeTab === 'address' }"
            @click="activeTab = 'address'"
          >
            <el-icon><Location /></el-icon> 收货地址
          </div>
          <div 
            class="sidebar-item" 
            :class="{ active: activeTab === 'order' }"
            @click="activeTab = 'order'"
          >
            <el-icon><List /></el-icon> 订单管理
          </div>
          <div 
            class="sidebar-item" 
            :class="{ active: activeTab === 'favorite' }"
            @click="activeTab = 'favorite'"
          >
            <el-icon><Star /></el-icon> 收藏店铺
          </div>
          <div 
            class="sidebar-item" 
            :class="{ active: activeTab === 'application' }"
            @click="activeTab = 'application'"
          >
            <el-icon><Document /></el-icon> 申请管理
          </div>
        </div>

        <div class="account-content">
          <Grzl v-if="activeTab === 'profile'" />
          <Shdz v-if="activeTab === 'address'" />
          <OrderGl v-if="activeTab === 'order'" @toUser="emit('toUser')" @toPay="handleToPay" />
          <Scdp v-if="activeTab === 'favorite'" />
          <Sqgl v-if="activeTab === 'application'" />
        </div>
      </div>
    </main>

    <footer class="footer">
      <div class="container">
        <p class="copyright">Copyright © 2024-2026 智码数云 zhishuyun.com 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Location, Star, List, Document } from '@element-plus/icons-vue'
import { userLogout } from '../../../api/Login'
import Grzl from './grzl.vue'
import Shdz from './shdz.vue'
import Scdp from './scdp.vue'
import OrderGl from './orderGl.vue'
import Sqgl from './sqgl.vue'

const props = defineProps({
  initialTab: {
    type: String,
    default: 'profile'
  }
})

const emit = defineEmits(['toLogin', 'toRegister', 'toUser', 'toScdp', 'toPay', 'toOrder'])

const userInfo = ref(null)
const activeTab = ref(props.initialTab)

watch(() => props.initialTab, (newVal) => {
  if (newVal) {
    activeTab.value = newVal
  }
})

onMounted(() => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }
})

const handleLogout = async () => {
  try {
    await userLogout()
  } catch (error) {
    console.error('Logout failed:', error)
  } finally {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    userInfo.value = null
    ElMessage.success('已退出登录')
    window.location.href = window.location.origin
  }
}

const handleToPay = (orderInfo) => {
  emit('toPay', orderInfo)
}
</script>

<style lang="scss" scoped>
.user-account-page {
  background-color: #EDF4FD;
  background-image: url('../../../assets/bj.png');
  background-repeat: no-repeat;
  background-position: center top;
  background-size: cover;
  min-height: 100vh;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.container {
  width: 1200px;
  margin: 0 auto;
}

.top-nav {
  background: linear-gradient(180deg, rgba(176, 224, 255, 0.8) 0%, rgba(214, 238, 255, 0.6) 70%, rgba(237, 244, 253, 0) 100%);
  height: 52px;
  line-height: 52px;
  font-size: 13px;
  color: #0b3a67;
  position: relative;
  z-index: 10;
  .container { display: flex; justify-content: space-between; }
  .left, .right { display: flex; gap: 20px; }
  .link { cursor: pointer; &:hover { color: #2a9eff; } }
}

.header-area {
  padding: 30px 0;
  .container { display: flex; align-items: center; gap: 40px; }
  .logo {
    cursor: pointer;
    display: flex;
    flex-direction: column;
    .brand-logo { font-size: 32px; font-weight: 800; color: #1a1a1a; span { color: #2a9eff; } }
    .brand-slogan { color: #999; font-size: 12px; margin-top: 5px; letter-spacing: 2px; }
  }
  .page-title { font-size: 24px; color: #333; font-weight: 600; }
}

.main-content {
  padding: 20px 0 60px;
  
  .account-container {
    display: flex;
    gap: 20px;
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0,0,0,0.05);
    min-height: 600px;
  }

  .account-sidebar {
    width: 240px;
    background: #f8fbff;
    padding: 20px 0;
    border-right: 1px solid #edf4ff;

    .sidebar-item {
      padding: 15px 30px;
      font-size: 15px;
      color: #666;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 10px;
      transition: all 0.3s;
      &:hover { color: #2a9eff; background: #f0f7ff; }
      &.active {
        color: #2a9eff;
        background: #e6f1ff;
        border-right: 3px solid #2a9eff;
        font-weight: 600;
      }
    }
  }

  .account-content {
    flex: 1;
    padding: 30px 40px;
    min-height: 600px;
    max-height: calc(100vh - 200px);
    overflow-y: auto;
  }
}

.footer {
  padding: 40px 0;
  text-align: center;
  .copyright { color: #999; font-size: 13px; }
}
</style>
