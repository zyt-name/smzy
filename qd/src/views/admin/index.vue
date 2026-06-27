<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <div class="brand-logo">智码<span>管理端</span></div>
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
    <main class="admin-main">
      <!-- Header -->
      <header class="admin-header">
        <div class="header-left">
          <h2>{{ menuItems.find(m => m.index === activeMenu)?.title || '控制台' }}</h2>
        </div>
        <div class="header-right">
          <div class="user-info">
            <el-avatar :size="32" :src="adminAvatar" style="background: transparent" />
            <span class="name">{{ userName }}</span>
          </div>
        </div>
      </header>

      <!-- Content Body -->
      <div class="admin-body">
        <div v-if="activeMenu === 'dashboard'">
          <!-- Stats Cards -->
          <div class="stats-container">
            <!-- 左侧2x2区域 -->
            <div class="stats-left">
              <div class="stat-card" v-for="stat in leftStats" :key="stat.title" :style="{ background: stat.bgColor }">
                <div class="stat-header">
                  <el-icon :size="24" :color="stat.color">
                    <component :is="stat.icon" />
                  </el-icon>
                  <span class="label" :style="{ color: stat.color }">{{ stat.title }}</span>
                </div>
                <div class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</div>
              </div>
            </div>
            <!-- 右侧一行区域 -->
            <div class="stats-right">
              <div class="stat-card stat-card-vertical" v-for="stat in rightStats" :key="stat.title" :style="{ background: stat.bgColor }">
                <div class="stat-header-large">
                  <el-icon :size="48" :color="stat.color">
                    <component :is="stat.icon" />
                  </el-icon>
                  <span class="label" :style="{ color: stat.color }">{{ stat.title }}</span>
                </div>
                <div class="stat-value-large" :style="{ color: stat.color }">{{ stat.value }}</div>
              </div>
            </div>
          </div>

          <!-- Charts and Lists -->
          <div class="content-row">
            <!-- 平台活跃度 -->
            <div class="content-col chart-col">
              <div class="card-header">
                <h3>平台活跃度趋势</h3>
                <el-radio-group v-model="chartTimeRange" size="small" @change="fetchActivityData">
                  <el-radio-button :label="1">近7天</el-radio-button>
                  <el-radio-button :label="2">近30天</el-radio-button>
                </el-radio-group>
              </div>
              <div class="chart-container">
                <CommonChart :options="chartOptions" height="300px" />
              </div>
            </div>
            
            <!-- 待办事项 -->
            <div class="content-col todo-col">
              <div class="card-header">
                <h3>系统待处理</h3>
                <ActionButton type="primary" size="small" @click="handleMoreTodos">更多</ActionButton>
              </div>
              <div class="todo-items">
                <div class="todo-item" v-for="(todo, index) in todos" :key="index">
                  <div class="todo-info">
                    <el-tag :type="todo.type" size="small" class="todo-tag">{{ todo.tagName }}</el-tag>
                    <span class="todo-text">{{ todo.content }}</span>
                  </div>
                  <span class="todo-time">{{ todo.time }}</span>
                </div>
              </div>
            </div>
          </div>

        </div>

        <!-- 用户管理页面 -->
        <UserPage v-if="activeMenu === 'users'" />

        <!-- 商家管理页面 -->
        <MerchantPage v-if="activeMenu === 'merchants'" />

        <!-- 内容管理页面 -->
        <ContentPage v-if="activeMenu === 'content'" />



        <!-- 账号管理页面 -->
        <ZhglPage v-if="activeMenu === 'account'" />

        <!-- 标签管理页面 -->
        <LabelPage v-if="activeMenu === 'label'" />

        <!-- 商品管理页面 -->
        <ShopglPage v-if="activeMenu === 'shop'" />

        <!-- 请求管理页面 -->
        <QqglPage v-if="activeMenu === 'application'" />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { 
  DataBoard, User, Shop, Files, Setting, 
  Bell, Back, Odometer, Goods, List, Promotion, DataLine, SwitchButton,
  Document, CircleCheck, CircleClose, Warning, CollectionTag, UserFilled
} from '@element-plus/icons-vue'
import { adminLogout } from '../../api/Login'
import { getPlatformOverview, getUserStatistics, getMerchantStatistics } from '../../api/admin/AdminStatistics'
import { getApplicationAggregate } from '../../api/admin/AdminApplication'
import { add } from '../../utils/notify'
import UserPage from './userPage/index.vue'
import MerchantPage from './merchantPage/index.vue'
import ContentPage from './contentPage/index.vue'
import ZhglPage from './zhgl/index.vue'
import LabelPage from './labelPage/index.vue'
import ShopglPage from './shopglPage/index.vue'
import QqglPage from './qqglPage/index.vue'
import CommonChart from '../../components/CommonChart.vue'
import ActionButton from '../../components/ActionButton.vue'
import adminAvatar from '../../assets/admin.png'

const emit = defineEmits(['toUser'])
const activeMenu = ref('dashboard')
const userName = ref('管理员')

onMounted(() => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    const user = JSON.parse(userInfo)
    userName.value = user.userName || '管理员'
  }

  // 恢复上次选中的菜单
  const savedMenu = localStorage.getItem('adminActiveMenu')
  if (savedMenu) {
    activeMenu.value = savedMenu
  }

  // 获取平台概览数据
  fetchPlatformOverview()
  
  // 获取申请聚合数据
  fetchApplicationAggregate()
  
  // 获取活跃度数据
  fetchActivityData()
})

// 切换菜单时保存状态
const handleMenuClick = (menuIndex) => {
  activeMenu.value = menuIndex
  localStorage.setItem('adminActiveMenu', menuIndex)
}

// 跳转到请求管理页面
const handleMoreTodos = () => {
  handleMenuClick('application')
}

const handleLogout = async () => {
  try {
    await adminLogout()
  } catch (error) {
    console.error('Admin logout error:', error)
  } finally {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('adminActiveMenu')
    add('已退出登录', 'success')
    emit('toUser')
  }
}

const menuItems = [
  { title: '控制台概览', index: 'dashboard', icon: 'Odometer' },
  { title: '用户管理', index: 'users', icon: 'User' },
  { title: '商家管理', index: 'merchants', icon: 'Shop' },
  { title: '商品管理', index: 'shop', icon: 'Goods' },
  { title: '内容管理', index: 'content', icon: 'Files' },
  { title: '标签管理', index: 'label', icon: 'CollectionTag' },
  { title: '请求管理', index: 'application', icon: 'Document' },
  { title: '账号管理', index: 'account', icon: 'UserFilled' },
]

// 平台概览统计数据
const overviewData = ref({
  totalUsers: 0,
  totalMerchants: 0,
  totalProducts: 0,
  totalOrders: 0,
  completedOrders: 0,
  bannedUsers: 0,
  bannedMerchants: 0
})

// 申请聚合数据
const aggregateData = ref({
  goodsReportCount: 0,
  merchantReportCount: 0,
  refundTimeoutCount: 0,
  deleteCommentCount: 0
})

// 活跃度数据
const chartTimeRange = ref(1)
const activityData = ref({
  dates: [],
  userActiveCounts: [],
  merchantActiveCounts: [],
  totalActiveCounts: []
})

// 格式化数字，添加千位分隔符
const formatNumber = (num) => {
  if (num === undefined || num === null) return '0'
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

// 左侧2x2区域卡片数据（基于后端数据）
const leftStats = computed(() => [
  { title: '平台总用户', value: formatNumber(overviewData.value.totalUsers), icon: 'User', color: '#409EFF', bgColor: '#ecf5ff' },
  { title: '封禁用户数', value: formatNumber(overviewData.value.bannedUsers), icon: 'CircleClose', color: '#F56C6C', bgColor: '#fef0f0' },
  { title: '平台商家数', value: formatNumber(overviewData.value.totalMerchants), icon: 'Shop', color: '#67C23A', bgColor: '#f0f9eb' },
  { title: '封禁商家数', value: formatNumber(overviewData.value.bannedMerchants), icon: 'Warning', color: '#E6A23C', bgColor: '#fdf6ec' }
])

// 右侧一行区域卡片数据（基于后端数据）
const rightStats = computed(() => [
  { title: '商品总数', value: formatNumber(overviewData.value.totalProducts), icon: 'Goods', color: '#E6A23C', bgColor: '#fdf6ec' },
  { title: '订单总数', value: formatNumber(overviewData.value.totalOrders), icon: 'Document', color: '#F56C6C', bgColor: '#fef0f0' },
  { title: '成交订单数', value: formatNumber(overviewData.value.completedOrders), icon: 'CircleCheck', color: '#909399', bgColor: '#f4f4f5' }
])

// 获取平台概览数据
const fetchPlatformOverview = async () => {
  try {
    const res = await getPlatformOverview()
    if (res.code === 200 || res.code === 1) {
      overviewData.value = res.data
    } else {
      ElMessage.error(res.msg || '获取平台概览数据失败')
    }
  } catch (error) {
    console.error('获取平台概览数据失败:', error)
    ElMessage.error('获取平台概览数据失败')
  }
}

// 获取申请聚合数据
const fetchApplicationAggregate = async () => {
  try {
    const res = await getApplicationAggregate()
    if (res.code === 200 || res.code === 1) {
      aggregateData.value = res.data
    } else {
      ElMessage.error(res.msg || '获取申请聚合数据失败')
    }
  } catch (error) {
    console.error('获取申请聚合数据失败:', error)
    ElMessage.error('获取申请聚合数据失败')
  }
}

// 获取活跃度数据
const fetchActivityData = async () => {
  try {
    const [userRes, merchantRes] = await Promise.all([
      getUserStatistics(chartTimeRange.value),
      getMerchantStatistics(chartTimeRange.value)
    ])
    
    if (userRes.code === 200 || userRes.code === 1) {
      const userData = userRes.data
      const merchantData = merchantRes.code === 200 || merchantRes.code === 1 ? merchantRes.data : { dates: [], activeMerchantCounts: [] }
      
      activityData.value.dates = userData.dates || []
      activityData.value.userActiveCounts = userData.activeUserCounts || []
      activityData.value.merchantActiveCounts = merchantData.activeMerchantCounts || []
      
      // 计算总活跃度
      activityData.value.totalActiveCounts = activityData.value.userActiveCounts.map((userCount, index) => {
        return userCount + (activityData.value.merchantActiveCounts[index] || 0)
      })
    } else {
      ElMessage.error(userRes.msg || '获取活跃度数据失败')
    }
  } catch (error) {
    console.error('获取活跃度数据失败:', error)
    ElMessage.error('获取活跃度数据失败')
  }
}

const chartOptions = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'shadow' }
  },
  legend: {
    data: ['用户活跃度', '商家活跃度', '总活跃度'],
    bottom: 0
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '15%',
    top: '10%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: activityData.value.dates,
    axisLine: { lineStyle: { color: '#f0f2f5' } },
    axisLabel: { color: '#909399' }
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { type: 'dashed', color: '#f0f2f5' } },
    axisLabel: { color: '#909399' }
  },
  series: [
    {
      name: '用户活跃度',
      type: 'line',
      data: activityData.value.userActiveCounts,
      smooth: true,
      itemStyle: { color: '#409EFF' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ])
      }
    },
    {
      name: '商家活跃度',
      type: 'line',
      data: activityData.value.merchantActiveCounts,
      smooth: true,
      itemStyle: { color: '#67C23A' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
          { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
        ])
      }
    },
    {
      name: '总活跃度',
      type: 'line',
      data: activityData.value.totalActiveCounts,
      smooth: true,
      itemStyle: { color: '#E6A23C' },
      lineStyle: { width: 3 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(230, 162, 60, 0.3)' },
          { offset: 1, color: 'rgba(230, 162, 60, 0.05)' }
        ])
      }
    }
  ]
}))

const todos = computed(() => [
  { tagName: '商品举报', type: 'danger', content: `${aggregateData.value.goodsReportCount} 个商品举报待处理`, time: '' },
  { tagName: '商家举报', type: 'warning', content: `${aggregateData.value.merchantReportCount} 个商家举报待处理`, time: '' },
  { tagName: '退款超时', type: 'danger', content: `${aggregateData.value.refundTimeoutCount} 个退款申请超时待处理`, time: '' },
  { tagName: '删除评论', type: 'info', content: `${aggregateData.value.deleteCommentCount} 个删除评论申请待处理`, time: '' }
])
</script>

<style lang="scss" scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  background-color: #EDF4FD;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  padding: 15px;
  box-sizing: border-box;
  gap: 15px;
}

.admin-sidebar {
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

.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.admin-header {
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

.admin-body {
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

  .stats-container {
    display: flex;
    gap: 20px;
    margin-bottom: 24px;
    height: 280px;

    .stats-left {
      flex: 0 0 40%;
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      grid-template-rows: repeat(2, 1fr);
      gap: 20px;
    }

    .stats-right {
      flex: 1;
      display: flex;
      gap: 20px;

      .stat-card-vertical {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        text-align: center;

        .stat-header-large {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 12px;
          margin-bottom: 16px;

          .el-icon {
            font-size: 48px;
          }

          .label {
            font-size: 16px;
            font-weight: 500;
          }
        }

        .stat-value-large {
          font-size: 36px;
          font-weight: 700;
        }
      }
    }

    .stat-card {
      padding: 20px;
      border-radius: 12px;
      box-shadow: 0 2px 12px rgba(0,0,0,0.02);
      transition: transform 0.3s;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 16px rgba(0,0,0,0.05);
      }

      .stat-header {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 12px;
        .label { font-size: 14px; font-weight: 500; }
      }
      .stat-value {
        font-size: 28px;
        font-weight: 700;
        margin-bottom: 8px;
      }
    }
  }

  .content-row {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;

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

    .chart-col {
      flex: 1.5;
    }

    .chart-container {
      padding: 10px 0;
    }

    .todo-col {
      flex: 1;
    }

    .table-col {
      flex: 1;
      width: 100%;
    }

    .todo-items {
      .todo-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px;
        background: #f8fbff;
        border-radius: 8px;
        margin-bottom: 10px;
        .todo-info {
          display: flex;
          align-items: center;
          gap: 10px;
          flex: 1;
          overflow: hidden;
          .todo-tag { flex-shrink: 0; }
          .todo-text { 
            color: #555; 
            font-size: 13px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }
        .todo-time { font-size: 12px; color: #bbb; margin-left: 10px; }
      }
    }
  }
}
</style>