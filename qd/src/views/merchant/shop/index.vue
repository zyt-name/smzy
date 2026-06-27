<template>
  <div class="product-management">
    <div class="main-layout">
      <ProductList />
    </div>
  </div>
</template>

<script setup>
import ProductList from './ProductList.vue'
</script>

<style lang="scss" scoped>
.product-management {
  padding: 0;
  min-height: 100%;

  .main-layout {
    height: calc(100vh - 100px);
  }
}
</style>
