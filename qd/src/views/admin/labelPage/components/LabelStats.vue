<template>
  <div class="label-stats">
    <el-card shadow="never" class="stat-card">
      <div class="stat-content">
        <div class="stat-icon category">
          <el-icon><Folder /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.categoryCount }}</div>
          <div class="stat-label">商品分类</div>
        </div>
      </div>
    </el-card>
    <el-card shadow="never" class="stat-card">
      <div class="stat-content">
        <div class="stat-icon category-enabled">
          <el-icon><FolderOpened /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.categoryEnabledCount }}</div>
          <div class="stat-label">分类启用</div>
        </div>
      </div>
    </el-card>
    <el-card shadow="never" class="stat-card">
      <div class="stat-content">
        <div class="stat-icon tag">
          <el-icon><CollectionTag /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.tagCount }}</div>
          <div class="stat-label">商品标签</div>
        </div>
      </div>
    </el-card>
    <el-card shadow="never" class="stat-card">
      <div class="stat-content">
        <div class="stat-icon level1">
          <el-icon><Star /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.level1Count }}</div>
          <div class="stat-label">一级标签</div>
        </div>
      </div>
    </el-card>
    <el-card shadow="never" class="stat-card">
      <div class="stat-content">
        <div class="stat-icon level2">
          <el-icon><StarFilled /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.level2Count }}</div>
          <div class="stat-label">二级标签</div>
        </div>
      </div>
    </el-card>
    <el-card shadow="never" class="stat-card">
      <div class="stat-content">
        <div class="stat-icon level3">
          <el-icon><Medal /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.level3Count }}</div>
          <div class="stat-label">三级标签</div>
        </div>
      </div>
    </el-card>
    <el-card shadow="never" class="stat-card">
      <div class="stat-content">
        <div class="stat-icon enabled">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.tagEnabledCount }}</div>
          <div class="stat-label">已启用标签</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { Folder, FolderOpened, CollectionTag, Star, StarFilled, Medal, CircleCheck } from '@element-plus/icons-vue'
import { getLabelStats } from '../../../../api/admin/AdminLabel'
import { ElMessage } from 'element-plus'

const stats = reactive({
  categoryCount: 0,
  categoryEnabledCount: 0,
  tagCount: 0,
  level1Count: 0,
  level2Count: 0,
  level3Count: 0,
  tagEnabledCount: 0
})

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await getLabelStats()
    
    if (res.code === 200 && res.data) {
      stats.categoryCount = res.data.categoryCount || 0
      stats.categoryEnabledCount = res.data.categoryEnabledCount || 0
      stats.tagCount = res.data.tagCount || 0
      stats.level1Count = res.data.level1Count || 0
      stats.level2Count = res.data.level2Count || 0
      stats.level3Count = res.data.level3Count || 0
      stats.tagEnabledCount = res.data.tagEnabledCount || 0
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
}

// 暴露方法给父组件
defineExpose({
  loadStats
})

onMounted(() => {
  loadStats()
})
</script>

<style lang="scss" scoped>
.label-stats {
  display: flex;
  gap: 12px;
  width: 100%;

  .stat-card {
    flex: 1;
    border-radius: 12px;
    border: none;
    min-width: 0;

    :deep(.el-card__body) {
      padding: 12px;
    }

    .stat-content {
      display: flex;
      align-items: center;
      gap: 8px;

      .stat-icon {
        width: 36px;
        height: 36px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 18px;
        flex-shrink: 0;

        &.category {
          background: #ecf5ff;
          color: #409eff;
        }

        &.category-enabled {
          background: #e6f7ff;
          color: #1890ff;
        }

        &.tag {
          background: #f0f9eb;
          color: #67c23a;
        }

        &.level1 {
          background: #fdf6ec;
          color: #e6a23c;
        }

        &.level2 {
          background: #fef0f0;
          color: #f56c6c;
        }

        &.level3 {
          background: #f9f0ff;
          color: #9254de;
        }

        &.enabled {
          background: #f4f4f5;
          color: #909399;
        }
      }

      .stat-info {
        flex: 1;
        min-width: 0;

        .stat-value {
          font-size: 18px;
          font-weight: 600;
          color: #303133;
          line-height: 1;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 12px;
          color: #909399;
          white-space: nowrap;
        }
      }
    }
  }
}
</style>
