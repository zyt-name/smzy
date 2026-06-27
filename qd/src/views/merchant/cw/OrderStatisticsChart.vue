<template>
  <el-card class="chart-card" shadow="never">
    <template #header>
      <div class="card-header">
        <span class="title">订单统计</span>
      </div>
    </template>
    <CommonChart :options="orderChartOptions" height="340px" />
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import CommonChart from '../../../components/CommonChart.vue'

const props = defineProps({
  statisticsData: {
    type: Object,
    default: () => ({
      days: 7,
      dates: [],
      orderCounts: [],
      validOrderCounts: [],
      totalOrderCount: 0,
      validOrderCount: 0
    })
  }
})

// 计算图表配置
const orderChartOptions = computed(() => {
  const dates = props.statisticsData.dates || []
  const orderCounts = props.statisticsData.orderCounts || []
  const validOrderCounts = props.statisticsData.validOrderCounts || []

  // 如果没有数据，使用默认空数据
  if (dates.length === 0) {
    return getEmptyChartOptions()
  }

  // 格式化日期显示（MM-DD）
  const formattedDates = dates.map(dateStr => {
    const date = new Date(dateStr)
    return `${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  })

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['订单总数', '有效订单数'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      top: '5%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: formattedDates,
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
        name: '订单总数',
        type: 'line',
        smooth: true,
        data: orderCounts,
        itemStyle: { color: '#409EFF' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(64,158,255,0.3)' },
              { offset: 1, color: 'rgba(64,158,255,0)' }
            ]
          }
        }
      },
      {
        name: '有效订单数',
        type: 'line',
        smooth: true,
        data: validOrderCounts,
        itemStyle: { color: '#67C23A' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(103,194,58,0.3)' },
              { offset: 1, color: 'rgba(103,194,58,0)' }
            ]
          }
        }
      }
    ]
  }
})

// 空数据时的图表配置
const getEmptyChartOptions = () => {
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['订单总数', '有效订单数'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      top: '5%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: [],
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
        name: '订单总数',
        type: 'line',
        smooth: true,
        data: [],
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '有效订单数',
        type: 'line',
        smooth: true,
        data: [],
        itemStyle: { color: '#67C23A' }
      }
    ]
  }
}
</script>

<style lang="scss" scoped>
.chart-card {
  border-radius: 12px;
  border: 1px solid #f0f2f5;
  height: 100%;
  background-color: #fff;

  :deep(.el-card__body) {
    padding: 10px;
    height: calc(100% - 55px);
    box-sizing: border-box;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
}
</style>
