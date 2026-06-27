<template>
  <div class="cw-management">
    <!-- 第一行：统计卡组(左) + 交易构成(右) -->
    <div class="top-row">
      <!-- 红色框：统计卡组 - 3x2布局 -->
      <div class="stats-section">
        <StatCards
          :total-order-count="orderStatistics.totalOrderCount"
          :valid-order-count="orderStatistics.validOrderCount"
          :total-amount="orderStatistics.totalAmount"
          :valid-amount="orderStatistics.validAmount"
        />
      </div>
      <!-- 黄色框：交易构成 -->
      <div class="structure-section">
        <TransactionStructureChart :structure-data="structureData" />
      </div>
    </div>

    <!-- 第二行：时间范围选择器 -->
    <div class="time-range-bar">
      <el-radio-group v-model="chartTimeRange" size="small" @change="handleTimeRangeChange">
        <el-radio-button :label="1">近7天</el-radio-button>
        <el-radio-button :label="2">近30天</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 第三行：订单统计（单独占一行） -->
    <div class="chart-row">
      <OrderStatisticsChart :statistics-data="orderStatistics" />
    </div>

    <!-- 第四行：交易额统计（单独占一行） -->
    <div class="chart-row chart-row-last">
      <AmountStatisticsChart :statistics-data="orderStatistics" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import StatCards from './StatCards.vue'
import OrderStatisticsChart from './OrderStatisticsChart.vue'
import AmountStatisticsChart from './AmountStatisticsChart.vue'
import TransactionStructureChart from './TransactionStructureChart.vue'
import { getOrderStatisticsCurve, getTransactionStructure } from '../../../api/merchant/MerchantStatisticsOrder'

const loading = ref(false)
const chartTimeRange = ref(1)

// 订单统计数据（新格式：独立列表）
const orderStatistics = ref({
  days: 7,
  dates: [],
  orderCounts: [],
  validOrderCounts: [],
  amounts: [],
  validAmounts: [],
  totalOrderCount: 0,
  validOrderCount: 0,
  totalAmount: 0,
  validAmount: 0
})

// 交易构成数据（从后端获取）
const structureData = ref([])

// 加载订单统计数据
const fetchOrderStatistics = async (viewType = 1) => {
  try {
    loading.value = true
    const res = await getOrderStatisticsCurve(viewType)
    if (res.code === 1 || res.code === 200) {
      orderStatistics.value = res.data
    } else {
      ElMessage.error(res.msg || '获取订单统计数据失败')
    }
  } catch (error) {
    console.error('获取订单统计数据失败:', error)
    ElMessage.error('获取订单统计数据失败')
  } finally {
    loading.value = false
  }
}

// 加载交易构成数据
const fetchTransactionStructure = async () => {
  try {
    const res = await getTransactionStructure()
    if (res.code === 1 || res.code === 200) {
      // 将后端返回的 structureMap 转换为组件需要的数组格式
      const structureMap = res.data.structureMap || {}
      structureData.value = Object.entries(structureMap).map(([name, value]) => ({
        name,
        value: Number(value)
      }))
    } else {
      console.error('获取交易构成数据失败:', res.msg)
    }
  } catch (error) {
    console.error('获取交易构成数据失败:', error)
  }
}

// 处理时间范围变化
const handleTimeRangeChange = (viewType) => {
  fetchOrderStatistics(viewType)
}

// 组件挂载时加载数据
onMounted(() => {
  fetchOrderStatistics(1)
  fetchTransactionStructure()
})
</script>

<style lang="scss" scoped>
.cw-management {
  display: flex;
  flex-direction: column;
  gap: 20px;
  background-color: transparent;
}

/* 第一行：统计卡组 + 交易构成 */
.top-row {
  display: flex;
  gap: 20px;
  height: 360px;

  /* 红色框：统计卡组 - 占60%宽度 */
  .stats-section {
    flex: 0 0 60%;
    height: 100%;
  }

  /* 黄色框：交易构成 - 占40%宽度 */
  .structure-section {
    flex: 0 0 40%;
    height: 100%;
  }
}

/* 时间范围选择器 */
.time-range-bar {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 0 10px;
}

/* 订单统计和交易额统计 - 各单独占一行 */
.chart-row {
  width: 100%;
  height: 400px;
  margin-bottom: 20px;

  &:last-child,
  &.chart-row-last {
    margin-bottom: 0;
  }
}
</style>
