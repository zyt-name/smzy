<template>
  <div class="common-table-container">
    <el-table
      v-bind="$attrs"
      :data="data"
      style="width: 100%"
      header-cell-class-name="common-table-header"
      row-class-name="common-table-row"
    >
      <slot></slot>
      <template #empty>
        <el-empty :description="emptyText" />
      </template>
    </el-table>
  </div>
</template>

<script setup>
defineProps({
  data: {
    type: Array,
    required: true
  },
  emptyText: {
    type: String,
    default: '暂无数据'
  }
})
</script>

<style lang="scss" scoped>
.common-table-container {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;

  :deep(.el-table) {
    --el-table-border-color: #f0f2f5;
    --el-table-header-bg-color: #f8fbff;
    
    .common-table-header {
      background-color: var(--el-table-header-bg-color) !important;
      color: #333;
      font-weight: 600;
      height: 50px;
      border-bottom: 1px solid #f0f2f5;
    }

    .common-table-row {
      height: 60px;
      transition: all 0.3s;
      
      &:hover > td {
        background-color: #f8fbff !important;
      }
    }

    .el-table__cell {
      padding: 12px 0;
    }

    &::before {
      display: none;
    }
  }
}
</style>
