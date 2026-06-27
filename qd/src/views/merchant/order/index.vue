<template>
  <div class="order-management">
    <div class="main-layout">
      <!-- 左侧订单列表 -->
      <div class="order-list-container">
        <OrderCenter />
      </div>

      <!-- 右侧售后申请 -->
      <div class="order-sidebar">
        <AfterSales />
      </div>
    </div>
  </div>
</template>

<script setup>
import OrderCenter from './OrderCenter.vue'
import AfterSales from './AfterSales.vue'
</script>

<style lang="scss" scoped>
.order-management {
  padding: 0;
  height: calc(100vh - 100px);

  .main-layout {
    display: flex;
    gap: 20px;
    align-items: stretch;
    height: 100%;

    .order-list-container {
      flex: 1;
      display: flex;
      height: 100%;
    }

    .order-sidebar {
      width: 380px;
      display: flex;
      height: 100%;
    }
  }
}
</style>
