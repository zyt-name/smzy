<template>
  <div class="merchant-management">
    <!-- 统计图表区域 -->
    <MerchantStats />

    <div class="main-layout">
      <!-- 商家列表 -->
      <MerchantList />
    </div>
  </div>
</template>

<script setup>
import MerchantStats from './components/MerchantStats.vue'
import MerchantList from './components/MerchantList.vue'
</script>

<style lang="scss" scoped>
.merchant-management {
  padding: 0;
  min-height: 100%;

  .main-layout {
    display: flex;
    gap: 20px;
    align-items: stretch;
  }
}
</style>
