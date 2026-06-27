<template>
  <div class="user-management">
    <!-- 统计图表区域 -->
    <UserStats />

    <div class="main-layout">
      <!-- 用户列表 -->
      <UserList />
    </div>
  </div>
</template>

<script setup>
import UserStats from './components/UserStats.vue'
import UserList from './components/UserList.vue'
</script>

<style lang="scss" scoped>
.user-management {
  padding: 0;
  min-height: 100%;

  .main-layout {
    display: flex;
    gap: 20px;
    align-items: stretch;
  }
}
</style>
