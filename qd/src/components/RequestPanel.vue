<template>
  <el-card shadow="never" class="request-panel full-height-card">
    <template #header>
      <div class="card-header">
        <span>{{ title }}</span>
        <el-tag :type="tagType" size="small">{{ total }} 待处理</el-tag>
      </div>
    </template>
    
    <div class="card-body-content">
      <div class="application-list">
        <div v-for="item in data" :key="item.id" class="app-item">
          <div class="app-info">
            <div class="app-title">
              <strong>{{ item.subject }}</strong> {{ item.actionText }}
            </div>
            <div class="app-time">{{ item.time }}</div>
            <div class="app-desc">
              <span 
                class="highlight-text" 
                :class="item.highlightType || 'primary'"
              >
                {{ item.highlightLabel }}: {{ item.highlightValue }}
              </span>
              <p>{{ item.description }}</p>
            </div>
          </div>
          <div class="app-ops">
            <el-button 
              type="success" 
              icon="Check" 
              circle 
              size="small" 
              :title="approveTitle" 
              @click="$emit('approve', item)"
            />
            <el-button 
              type="danger" 
              icon="Close" 
              circle 
              size="small" 
              :title="rejectTitle" 
              @click="$emit('reject', item)"
            />
          </div>
        </div>
        <el-empty v-if="data.length === 0" :description="emptyText" :image-size="60" />
      </div>
      
      <div class="pagination-wrapper small-pagination">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          small
          @update:current-page="$emit('update:currentPage', $event)"
        />
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { Check, Close } from '@element-plus/icons-vue'

defineProps({
  title: {
    type: String,
    required: true
  },
  data: {
    type: Array,
    required: true
  },
  total: {
    type: Number,
    default: 0
  },
  currentPage: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 5
  },
  tagType: {
    type: String,
    default: 'danger'
  },
  emptyText: {
    type: String,
    default: '暂无待处理申请'
  },
  approveTitle: {
    type: String,
    default: '通过'
  },
  rejectTitle: {
    type: String,
    default: '拒绝'
  }
})

defineEmits(['approve', 'reject', 'update:currentPage'])
</script>

<style lang="scss" scoped>
.full-height-card {
  width: 100%;
  border-radius: 12px;
  border: none;
  display: flex;
  flex-direction: column;
  height: 100%;
  
  :deep(.el-card__header) {
    padding: 18px 20px;
    border-bottom: 1px solid #f0f2f5;
  }

  :deep(.el-card__body) {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
    overflow: hidden;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  span { font-weight: bold; font-size: 16px; }
}

.card-body-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.application-list {
  flex: 1;
  overflow-y: auto;
  
  .app-item {
    padding: 20px 0;
    border-bottom: 1px solid #f0f2f5;
    display: flex;
    justify-content: space-between;
    align-items: center;
    &:last-child { border-bottom: none; }

    .app-info {
      flex: 1;
      padding-right: 20px;
      .app-title {
        font-size: 14px;
        color: #333;
        line-height: 1.4;
        margin-bottom: 4px;
        strong { color: #2a9eff; }
      }
      .app-time {
        font-size: 12px;
        color: #c0c4cc;
        margin-bottom: 8px;
      }
      .app-desc {
        font-size: 13px;
        color: #666;
        background: #f8f9fa;
        padding: 10px;
        border-radius: 6px;
        margin-top: 4px;
        line-height: 1.5;

        .highlight-text {
          display: inline-block;
          font-weight: bold;
          margin-bottom: 6px;
          
          &.danger { color: #f56c6c; }
          &.warning { color: #e6a23c; }
          &.success { color: #67c23a; }
          &.primary { color: #409eff; }
        }
        p { margin: 0; }
      }
    }

    .app-ops {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 12px;
      width: 40px;
      flex-shrink: 0;

      :deep(.el-button + .el-button) {
        margin-left: 0;
        margin-top: 0;
      }
    }
  }
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;

  &.small-pagination {
    margin-top: auto;
    padding-top: 15px;
    border-top: 1px solid #f5f5f5;
  }
}
</style>
