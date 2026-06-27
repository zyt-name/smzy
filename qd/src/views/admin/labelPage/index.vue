<template>
  <div class="label-management">
    <!-- 标签统计 -->
    <div class="stats-section">
      <LabelStats ref="labelStatsRef" />
    </div>

    <!-- 主内容区 -->
    <div class="main-layout">
      <!-- 左侧：标签列表 -->
      <div class="list-container">
        <LabelList ref="labelListRef" @refresh-stats="handleRefreshStats" />
      </div>

      <!-- 右侧：标签操作 -->
      <div class="operation-sidebar">
        <LabelOperation @success="handleSuccess" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import LabelStats from './components/LabelStats.vue'
import LabelList from './components/LabelList.vue'
import LabelOperation from './components/LabelOperation.vue'

const labelListRef = ref(null)
const labelStatsRef = ref(null)

// 处理操作成功
const handleSuccess = () => {
  labelListRef.value?.loadCategoryList()
  labelListRef.value?.loadTagList()
  labelStatsRef.value?.loadStats()
}

// 刷新统计
const handleRefreshStats = () => {
  labelStatsRef.value?.loadStats()
}
</script>

<style lang="scss" scoped>
.label-management {
  padding: 0;
  min-height: 100%;

  .stats-section {
    margin-bottom: 20px;
  }

  .main-layout {
    display: flex;
    gap: 20px;
    align-items: stretch;

    .list-container {
      flex: 1;
      display: flex;
    }

    .operation-sidebar {
      width: 400px;
      display: flex;
    }
  }
}
</style>
