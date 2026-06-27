<template>
  <div class="merchant-layout">
    <!-- Sidebar -->
    <aside class="merchant-sidebar">
      <div class="sidebar-header">
        <div class="brand-logo">智码<span>商服</span></div>
      </div>
      <nav class="sidebar-menu">
        <div 
          v-for="item in menuItems" 
          :key="item.index"
          class="menu-item"
          :class="{ active: activeMenu === item.index }"
          @click="handleMenuClick(item.index)"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </div>
      </nav>
      <div class="sidebar-footer" @click="handleLogout">
        <el-icon><SwitchButton /></el-icon>
        <span>退出登录</span>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="merchant-main">
      <!-- Header -->
      <header class="merchant-header">
        <div class="header-left">
          <h2>{{ currentMenuTitle }}</h2>
        </div>
        <div class="header-right">
          <div class="user-info">
            <el-avatar :size="32" :src="merchantAvatar" style="background: transparent" />
            <span class="name">{{ userName }}</span>
          </div>
        </div>
      </header>

      <!-- Content Body -->
      <div class="merchant-body">
        <keep-alive>
          <component 
            :is="currentComponent" 
            :key="activeMenu"
          />
        </keep-alive>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, markRaw } from 'vue'
import { ElMessage } from 'element-plus'
import { merchantLogout } from '../../api/Login'
import merchantAvatar from '../../assets/merchant.png'
import ShopManagement from './shop/index.vue'
import OrderManagement from './order/index.vue'
import CwManagement from './cw/index.vue'
import DpszManagement from './dpsz/index.vue'
import SqglManagement from './sqgl/index.vue'

const emit = defineEmits(['toUser'])

// 从 localStorage 恢复菜单状态，默认为 finance（财务报表）
const savedMenu = localStorage.getItem('merchantActiveMenu')
const activeMenu = ref(savedMenu || 'finance')
const userName = ref('商家')

// 组件映射表
const componentMap = {
  'products': markRaw(ShopManagement),
  'orders': markRaw(OrderManagement),
  'finance': markRaw(CwManagement),
  'settings': markRaw(DpszManagement),
  'application': markRaw(SqglManagement)
}

// 当前组件
const currentComponent = computed(() => {
  return componentMap[activeMenu.value] || null
})

const currentMenuTitle = computed(() => {
  const item = menuItems.find(m => m.index === activeMenu.value)
  return item ? item.title : '工作台'
})

onMounted(() => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    const user = JSON.parse(userInfo)
    userName.value = user.userName || '商家'
  }
})

// 菜单点击事件 - 保存状态到 localStorage
const handleMenuClick = (menuIndex) => {
  activeMenu.value = menuIndex
  localStorage.setItem('merchantActiveMenu', menuIndex)
}

const handleLogout = async () => {
  try {
    await merchantLogout()
  } catch (error) {
    console.error('Merchant logout error:', error)
  } finally {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('merchantActiveMenu')
    ElMessage.success('已退出登录')
    emit('toUser')
  }
}

const menuItems = [
  { title: '商品管理', index: 'products', icon: 'Goods' },
  { title: '订单中心', index: 'orders', icon: 'List' },
  { title: '财务报表', index: 'finance', icon: 'DataLine' },
  { title: '店铺设置', index: 'settings', icon: 'Setting' },
  { title: '申请管理', index: 'application', icon: 'Document' },
]
</script>

<style lang="scss" scoped>
.merchant-layout {
  display: flex;
  height: 100vh;
  background-color: #EDF4FD;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  padding: 15px;
  box-sizing: border-box;
  gap: 15px;
}

.merchant-sidebar {
  width: 240px;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 10px rgba(42, 158, 255, 0.05);
  z-index: 100;
  border-radius: 16px;
  overflow: hidden;

  .sidebar-header {
    padding: 20px 16px;
    .brand-logo {
      font-size: 22px;
      font-weight: 800;
      color: #1a1a1a;
      background: #f8fbff;
      padding: 12px;
      border-radius: 12px;
      text-align: center;
      span { color: #2a9eff; }
    }
  }

  .sidebar-menu {
    flex: 1;
    padding: 0 12px;
    .menu-item {
      display: flex;
      align-items: center;
      padding: 14px 16px;
      margin-bottom: 4px;
      border-radius: 8px;
      color: #666;
      cursor: pointer;
      transition: all 0.3s;
      gap: 12px;

      &:hover {
        background-color: rgba(42, 158, 255, 0.05);
        color: #2a9eff;
      }

      &.active {
        background-color: #2a9eff;
        color: #fff;
        box-shadow: 0 4px 12px rgba(42, 158, 255, 0.3);
      }

      .el-icon { font-size: 18px; }
    }
  }

  .sidebar-footer {
    padding: 16px;
    margin: 10px 12px;
    background: #f8fbff;
    border-radius: 10px;
    display: flex;
    align-items: center;
    gap: 10px;
    color: #999;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.3s;
    &:hover { 
      color: #2a9eff;
      background: rgba(42, 158, 255, 0.1);
    }
  }
}

.merchant-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.merchant-header {
  height: 64px;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  border-radius: 16px;
  margin-bottom: 15px;

  .header-left {
    h2 {
      font-size: 18px;
      font-weight: 600;
      color: #333;
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 24px;
    .notice-item {
      cursor: pointer;
      font-size: 20px;
      color: #666;
      background: #f8fbff;
      padding: 8px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .user-info {
      display: flex;
      align-items: center;
      gap: 10px;
      background: #f8fbff;
      padding: 6px 12px;
      border-radius: 10px;
      .name { font-size: 14px; color: #333; }
    }
  }
}

.merchant-body {
  padding: 0;
  flex: 1;
  overflow-y: auto;
  
  /* 自定义滚动条样式 */
  &::-webkit-scrollbar {
    width: 6px;
  }
  &::-webkit-scrollbar-thumb {
    background: rgba(42, 158, 255, 0.2);
    border-radius: 3px;
  }
  &::-webkit-scrollbar-track {
    background: transparent;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 24px;

    .stat-card {
      background: #fff;
      padding: 20px;
      border-radius: 12px;
      box-shadow: 0 2px 12px rgba(0,0,0,0.02);

      .stat-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
        .label { font-size: 14px; color: #999; }
      }
      .stat-value {
        font-size: 28px;
        font-weight: 700;
        color: #1a1a1a;
        margin-bottom: 8px;
      }
      .stat-footer { font-size: 12px; color: #bbb; }
    }
  }

  .content-row {
    display: flex;
    gap: 20px;

    .content-col {
      background: #fff;
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 2px 12px rgba(0,0,0,0.02);
      
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        h3 { font-size: 16px; font-weight: 600; color: #333; }
      }
    }

    .orders-list { flex: 3; }
    .recent-products { flex: 2; }

    .todo-items {
      .todo-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 15px;
        background: #f8fbff;
        border-radius: 8px;
        margin-bottom: 12px;
        .todo-info {
          display: flex;
          align-items: center;
          gap: 15px;
          .todo-text { color: #555; font-size: 14px; }
          .todo-count { 
            font-weight: 600; 
            color: #2a9eff;
            background: rgba(42, 158, 255, 0.1);
            padding: 2px 8px;
            border-radius: 10px;
            font-size: 12px;
          }
        }
      }
    }

    .rank-list {
      .rank-item {
        display: flex;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #f5f5f5;
        gap: 15px;
        &:last-child { border-bottom: none; }

        .rank-index {
          width: 24px;
          height: 24px;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 50%;
          font-size: 12px;
          background: #f0f0f0;
          color: #999;
          &.rank-1 { background: #ff4d4f; color: #fff; }
          &.rank-2 { background: #ffa940; color: #fff; }
        }
        .prod-name { flex: 1; font-size: 14px; color: #555; }
        .prod-sales { font-size: 14px; color: #999; }
      }
    }
  }
}
</style>
