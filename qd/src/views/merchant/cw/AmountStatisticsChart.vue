<template>
  <el-card class="chart-card" shadow="never">
    <template #header>
      <div class="card-header">
        <span class="title">交易额统计</span>
      </div>
    </template>
    <CommonChart :options="amountChartOptions" height="340px" />
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
      amounts: [],
      validAmounts: [],
      totalAmount: 0,
      validAmount: 0
    })
  }
})

// 计算图表配置
const amountChartOptions = computed(() => {
  const dates = props.statisticsData.dates || []
  const amounts = props.statisticsData.amounts || []
  const validAmounts = props.statisticsData.validAmounts || []

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
      axisPointer: { type: 'shadow' },
      formatter: function(params) {
        let result = params[0].name + '<br/>'
        params.forEach(param => {
          result += param.marker + param.seriesName + ': ¥' + parseFloat(param.value).toFixed(2) + '<br/>'
        })
        return result
      }
    },
    legend: {
      data: ['交易总额', '有效交易额'],
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
      axisLabel: {
        color: '#909399',
        formatter: function(value) {
          return '¥' + (value / 1000).toFixed(0) + 'k'
        }
      }
    },
    series: [
      {
        name: '交易总额',
        type: 'line',
        smooth: true,
        data: amounts,
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
        name: '有效交易额',
        type: 'line',
        smooth: true,
        data: validAmounts,
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
      data: ['交易总额', '有效交易额'],
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
        name: '交易总额',
        type: 'line',
        smooth: true,
        data: [],
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '有效交易额',
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
