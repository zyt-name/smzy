<template>
  <div class="stats-grid">
    <!-- 第一行：订单相关 -->
    <el-card class="stat-card" shadow="never">
      <div class="stat-content">
        <div class="stat-info">
          <div class="stat-title">订单总数</div>
          <div class="stat-value">{{ totalOrderCount }}</div>
          <div class="stat-compare">
            <span class="compare-label">统计周期内</span>
          </div>
        </div>
        <div class="stat-icon" style="background-color: #ecf5ff;">
          <el-icon style="color: #409EFF;"><List /></el-icon>
        </div>
      </div>
    </el-card>

    <el-card class="stat-card" shadow="never">
      <div class="stat-content">
        <div class="stat-info">
          <div class="stat-title">有效订单数</div>
          <div class="stat-value">{{ validOrderCount }}</div>
          <div class="stat-compare">
            <span class="compare-label">统计周期内</span>
          </div>
        </div>
        <div class="stat-icon" style="background-color: #f0f9eb;">
          <el-icon style="color: #67C23A;"><CircleCheck /></el-icon>
        </div>
      </div>
    </el-card>

    <el-card class="stat-card" shadow="never">
      <div class="stat-content">
        <div class="stat-info">
          <div class="stat-title">有效单率</div>
          <div class="stat-value">{{ validOrderRate }}%</div>
          <div class="stat-compare">
            <span class="compare-label">有效订单占比</span>
          </div>
        </div>
        <div class="stat-icon" style="background-color: #fdf6ec;">
          <el-icon style="color: #E6A23C;"><TrendCharts /></el-icon>
        </div>
      </div>
    </el-card>

    <!-- 第二行：金额相关 -->
    <el-card class="stat-card" shadow="never">
      <div class="stat-content">
        <div class="stat-info">
          <div class="stat-title">交易总额</div>
          <div class="stat-value">¥{{ formatAmount(totalAmount) }}</div>
          <div class="stat-compare">
            <span class="compare-label">统计周期内</span>
          </div>
        </div>
        <div class="stat-icon" style="background-color: #f4f4f5;">
          <el-icon style="color: #606266;"><Money /></el-icon>
        </div>
      </div>
    </el-card>

    <el-card class="stat-card" shadow="never">
      <div class="stat-content">
        <div class="stat-info">
          <div class="stat-title">有效交易额</div>
          <div class="stat-value">¥{{ formatAmount(validAmount) }}</div>
          <div class="stat-compare">
            <span class="compare-label">统计周期内</span>
          </div>
        </div>
        <div class="stat-icon" style="background-color: #f0f9eb;">
          <el-icon style="color: #67C23A;"><Wallet /></el-icon>
        </div>
      </div>
    </el-card>

    <el-card class="stat-card" shadow="never">
      <div class="stat-content">
        <div class="stat-info">
          <div class="stat-title">有效金额率</div>
          <div class="stat-value">{{ validAmountRate }}%</div>
          <div class="stat-compare">
            <span class="compare-label">有效金额占比</span>
          </div>
        </div>
        <div class="stat-icon" style="background-color: #e6f7ff;">
          <el-icon style="color: #1890ff;"><PieChart /></el-icon>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { List, CircleCheck, TrendCharts, Money, Wallet, PieChart } from '@element-plus/icons-vue'

const props = defineProps({
  totalOrderCount: {
    type: Number,
    default: 0
  },
  validOrderCount: {
    type: Number,
    default: 0
  },
  totalAmount: {
    type: Number,
    default: 0
  },
  validAmount: {
    type: Number,
    default: 0
  }
})

// 计算有效单率
const validOrderRate = computed(() => {
  if (props.totalOrderCount === 0) return 0
  return ((props.validOrderCount / props.totalOrderCount) * 100).toFixed(1)
})

// 计算有效金额率
const validAmountRate = computed(() => {
  if (props.totalAmount === 0) return 0
  return ((props.validAmount / props.totalAmount) * 100).toFixed(1)
})

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return parseFloat(amount).toFixed(2)
}
</script>

<style lang="scss" scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 12px;
  height: 100%;

  .stat-card {
    border-radius: 12px;
    border: 1px solid #f0f2f5;

    :deep(.el-card__body) {
      padding: 12px;
      height: 100%;
      box-sizing: border-box;
    }

    .stat-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 100%;
    }

    .stat-title {
      font-size: 13px;
      color: #909399;
      margin-bottom: 6px;
    }

    .stat-value {
      font-size: 18px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 4px;
    }

    .stat-compare {
      font-size: 11px;
      color: #c0c4cc;

      .compare-value {
        margin-left: 4px;
        font-weight: 500;

        &.up { color: #67C23A; }
        &.down { color: #F56C6C; }
      }
    }

    .stat-icon {
      width: 36px;
      height: 36px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
    }
  }
}
</style>
