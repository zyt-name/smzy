<template>
  <el-card class="structure-card" shadow="never">
    <template #header>
      <div class="card-header">
        <span class="title">交易构成</span>
      </div>
    </template>
    <CommonChart ref="chartRef" :options="structureChartOptions" height="280px" />
  </el-card>
</template>

<script setup>
import { computed, ref, watch, onUnmounted } from 'vue'
import CommonChart from '../../../components/CommonChart.vue'

const props = defineProps({
  structureData: {
    type: Array,
    default: () => []
  }
})

const chartRef = ref(null)
let autoPlayTimer = null
let currentIndex = 0

// 预设颜色数组
const colorList = ['#409EFF', '#67C23A', '#E6A23C', '#909399', '#F56C6C', '#9757D7', '#14B8A6']

// 自动轮播
const startAutoPlay = () => {
  if (!props.structureData || props.structureData.length === 0) return
  
  stopAutoPlay()
  currentIndex = 0
  
  const chart = chartRef.value?.getChartInstance?.()
  if (!chart) return
  
  // 初始高亮第一个
  chart.dispatchAction({
    type: 'highlight',
    seriesIndex: 0,
    dataIndex: 0
  })
  chart.dispatchAction({
    type: 'showTip',
    seriesIndex: 0,
    dataIndex: 0
  })
  
  autoPlayTimer = setInterval(() => {
    const dataLength = props.structureData.length
    if (dataLength === 0) return
    
    // 取消上一个高亮
    chart.dispatchAction({
      type: 'downplay',
      seriesIndex: 0,
      dataIndex: currentIndex
    })
    
    // 切换到下一个
    currentIndex = (currentIndex + 1) % dataLength
    
    // 高亮当前
    chart.dispatchAction({
      type: 'highlight',
      seriesIndex: 0,
      dataIndex: currentIndex
    })
    chart.dispatchAction({
      type: 'showTip',
      seriesIndex: 0,
      dataIndex: currentIndex
    })
  }, 3000)
}

const stopAutoPlay = () => {
  if (autoPlayTimer) {
    clearInterval(autoPlayTimer)
    autoPlayTimer = null
  }
}

// 监听数据变化，重新启动轮播
watch(() => props.structureData, () => {
  setTimeout(() => {
    startAutoPlay()
  }, 100)
}, { deep: true, immediate: true })

onUnmounted(() => {
  stopAutoPlay()
})

const structureChartOptions = computed(() => {
  // 如果没有数据，显示空状态（去掉暂无数据字样）
  if (!props.structureData || props.structureData.length === 0) {
    return {
      series: []
    }
  }

  // 计算总值
  const total = props.structureData.reduce((sum, item) => sum + item.value, 0)

  return {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      bottom: '0%',
      left: 'center',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: {
        fontSize: 12
      }
    },
    series: [
      {
        name: '交易构成',
        type: 'pie',
        radius: ['35%', '60%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          scale: true,
          scaleSize: 10,
          label: {
            show: true,
            formatter: (params) => {
              const percent = total > 0 ? ((params.value / total) * 100).toFixed(1) : 0
              return `{name|${params.name}}\n{percent|${percent}%}`
            },
            rich: {
              name: {
                fontSize: 14,
                color: '#606266',
                lineHeight: 20,
                padding: [0, 0, 4, 0]
              },
              percent: {
                fontSize: 18,
                fontWeight: 'bold',
                color: '#303133'
              }
            }
          }
        },
        labelLine: {
          show: false
        },
        data: props.structureData.map((item, index) => ({
          value: item.value,
          name: item.name,
          itemStyle: {
            color: colorList[index % colorList.length]
          }
        }))
      }
    ]
  }
})
</script>

<style lang="scss" scoped>
.structure-card {
  border-radius: 12px;
  border: 1px solid #f0f2f5;
  height: 100%;

  :deep(.el-card__body) {
    padding: 10px;
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
}
</style>
