<template>
  <div class="stats-section">
    <!-- 新增商家统计 -->
    <el-card shadow="never" class="chart-card">
      <template #header>
        <div class="card-header">
          <span>新增商家统计</span>
          <el-radio-group v-model="timeRange" size="small" @change="handleTimeRangeChange">
            <el-radio-button label="7d">近7天</el-radio-button>
            <el-radio-button label="30d">近30天</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <CommonChart :options="newMerchantChartOptions" height="350px" v-loading="loading" />
    </el-card>

    <!-- 活跃商家统计 -->
    <el-card shadow="never" class="chart-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>活跃商家统计</span>
        </div>
      </template>
      <CommonChart :options="activeMerchantChartOptions" height="350px" v-loading="loading" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import CommonChart from '../../../../components/CommonChart.vue'
import { getMerchantStatistics } from '../../../../api/admin/AdminStatistics'
import { ElMessage } from 'element-plus'

const timeRange = ref('7d')
const loading = ref(false)

// 后端返回的数据
const dates = ref([])
const newMerchantCounts = ref([])
const activeMerchantCounts = ref([])

// 获取统计数据
const fetchData = async () => {
  loading.value = true
  try {
    const type = timeRange.value === '30d' ? 2 : 1
    const res = await getMerchantStatistics(type)
    if (res.code === 200 || res.code === 1) {
      dates.value = res.data.dates || []
      newMerchantCounts.value = res.data.newMerchantCounts || []
      activeMerchantCounts.value = res.data.activeMerchantCounts || []
    } else {
      ElMessage.error(res.msg || '获取数据失败')
    }
  } catch (error) {
    console.error('获取商家统计数据失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 时间范围变化处理
const handleTimeRangeChange = () => {
  fetchData()
}

// 新增商家图表配置
const newMerchantChartOptions = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'line',
      lineStyle: {
        color: '#67c23a',
        width: 2,
        type: 'dashed'
      }
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: dates.value,
    axisLine: { lineStyle: { color: '#999' } }
  },
  yAxis: {
    type: 'value',
    axisLine: { show: false },
    splitLine: { lineStyle: { type: 'dashed', color: '#eee' } }
  },
  series: [
    {
      name: '新增商家',
      type: 'line',
      smooth: true,
      data: newMerchantCounts.value,
      itemStyle: { color: '#67c23a' },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0)' }
          ]
        }
      },
      lineStyle: { width: 3 }
    }
  ]
}))

// 活跃商家图表配置
const activeMerchantChartOptions = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'line',
      lineStyle: {
        color: '#e6a23c',
        width: 2,
        type: 'dashed'
      }
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: dates.value,
    axisLine: { lineStyle: { color: '#999' } }
  },
  yAxis: {
    type: 'value',
    axisLine: { show: false },
    splitLine: { lineStyle: { type: 'dashed', color: '#eee' } }
  },
  series: [
    {
      name: '活跃商家',
      type: 'line',
      smooth: true,
      data: activeMerchantCounts.value,
      itemStyle: { color: '#e6a23c' },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(230, 162, 60, 0.3)' },
            { offset: 1, color: 'rgba(230, 162, 60, 0)' }
          ]
        }
      },
      lineStyle: { width: 3 }
    }
  ]
}))

// 组件挂载时获取数据
onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.stats-section {
  margin-bottom: 20px;
  .chart-card {
    border-radius: 12px;
    border: none;
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      span { font-weight: bold; font-size: 16px; }
    }
  }
}
</style>
