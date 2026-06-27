<template>
  <el-dialog
    v-model="visible"
    title="商品规格格式说明"
    width="600px"
    :close-on-click-modal="false"
    class="spec-format-dialog"
    :append-to-body="true"
  >
    <div class="spec-format-scroll-wrapper">
      <div class="spec-format-content">
        <div class="format-section">
          <h4 class="format-title">支持的规格格式示例</h4>
          <p class="format-tip">请按照以下任意一种格式填写商品规格，系统会自动识别并格式化显示</p>
        </div>

        <div class="format-item">
          <div class="format-header">
            <span class="format-number">1</span>
            <span class="format-name">JSON 格式（推荐）</span>
            <el-tag type="success" size="small">最稳定</el-tag>
          </div>
          <div class="format-example">
            <code>[{"label":"品牌","value":"Apple"},{"label":"型号","value":"MacBook Pro"},{"label":"内存","value":"16GB"}]</code>
          </div>
        </div>

        <div class="format-item">
          <div class="format-header">
            <span class="format-number">2</span>
            <span class="format-name">中文分隔符格式</span>
            <el-tag type="primary" size="small">常用</el-tag>
          </div>
          <div class="format-example">
            <code>品牌：Apple；型号：MacBook Pro；内存：16GB</code>
          </div>
        </div>

        <div class="format-item">
          <div class="format-header">
            <span class="format-number">3</span>
            <span class="format-name">英文分隔符格式</span>
          </div>
          <div class="format-example">
            <code>Brand: Apple; Model: MacBook Pro; RAM: 16GB</code>
          </div>
        </div>

        <div class="format-item">
          <div class="format-header">
            <span class="format-number">4</span>
            <span class="format-name">等号分隔格式</span>
          </div>
          <div class="format-example">
            <code>品牌=Apple;型号=MacBook Pro;内存=16GB</code>
          </div>
        </div>

        <div class="format-item">
          <div class="format-header">
            <span class="format-number">5</span>
            <span class="format-name">换行符分隔格式</span>
          </div>
          <div class="format-example">
            <code>品牌：Apple<br/>型号：MacBook Pro<br/>内存：16GB</code>
          </div>
        </div>

        <div class="format-item">
          <div class="format-header">
            <span class="format-number">6</span>
            <span class="format-name">逗号分隔格式</span>
          </div>
          <div class="format-example">
            <code>品牌：Apple, 型号：MacBook Pro, 内存：16GB</code>
          </div>
        </div>

        <div class="format-item">
          <div class="format-header">
            <span class="format-number">7</span>
            <span class="format-name">空格分隔格式</span>
          </div>
          <div class="format-example">
            <code>品牌 Apple 型号 MacBook Pro 内存 16GB</code>
          </div>
        </div>

        <div class="format-notice">
          <el-icon color="#E6A23C"><WarningFilled /></el-icon>
          <span>提示：推荐使用 JSON 格式或中文分隔符格式，解析更准确</span>
        </div>
      </div>
    </div>

    <template #footer>
      <ActionButton type="primary" size="large" @click="handleClose">我知道了</ActionButton>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { WarningFilled } from '@element-plus/icons-vue'
import ActionButton from '../../../components/ActionButton.vue'

const visible = ref(false)

const open = () => {
  visible.value = true
}

const handleClose = () => {
  visible.value = false
}

defineExpose({
  open
})
</script>

<style lang="scss" scoped>
.spec-format-dialog {
  :deep(.el-overlay) {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  :deep(.el-dialog) {
    margin: 0 !important;
    position: relative;
    top: auto !important;
    left: auto !important;
    transform: none !important;
    max-height: 80vh;
    display: flex;
    flex-direction: column;
  }

  :deep(.el-dialog__header) {
    border-bottom: 1px solid #e4e7ed;
    padding-bottom: 15px;
    margin-right: 0;
    
    .el-dialog__title {
      font-size: 18px;
      font-weight: 700;
      color: #1a3a5c;
    }
  }

  :deep(.el-dialog__body) {
    padding: 0 !important;
    overflow: hidden;
    flex: 1;
    min-height: 0;
  }

  :deep(.el-dialog__footer) {
    border-top: 1px solid #e4e7ed;
    padding: 15px 24px;
    text-align: center;
  }
}

.spec-format-scroll-wrapper {
  height: 450px;
  overflow-y: auto;
  padding: 20px 24px;
  
  &::-webkit-scrollbar {
    width: 8px;
  }
  
  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(42, 158, 255, 0.5);
    border-radius: 4px;
    
    &:hover {
      background: rgba(42, 158, 255, 0.7);
    }
  }

  &::-webkit-scrollbar-corner {
    background: transparent;
  }
}

.spec-format-content {
  .format-section {
    margin-bottom: 20px;
    
    .format-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin: 0 0 8px 0;
    }

    .format-tip {
      font-size: 13px;
      color: #666;
      margin: 0;
    }
  }

  .format-item {
    background: #f8f9fb;
    border-radius: 8px;
    padding: 12px 16px;
    margin-bottom: 12px;
    border-left: 3px solid #2a9eff;

    .format-header {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 8px;

      .format-number {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 22px;
        height: 22px;
        background: #2a9eff;
        color: white;
        border-radius: 50%;
        font-size: 12px;
        font-weight: 600;
      }

      .format-name {
        font-size: 14px;
        font-weight: 600;
        color: #333;
      }
    }

    .format-example {
      background: white;
      border-radius: 6px;
      padding: 10px 12px;
      border: 1px solid #e4e7ed;

      code {
        font-family: 'Courier New', Courier, monospace;
        font-size: 13px;
        color: #2a9eff;
        word-break: break-all;
        line-height: 1.6;
      }
    }
  }

  .format-notice {
    display: flex;
    align-items: center;
    gap: 8px;
    background: #fdf6ec;
    border: 1px solid #faecd8;
    border-radius: 6px;
    padding: 12px 16px;
    margin-top: 16px;

    span {
      font-size: 13px;
      color: #e6a23c;
    }
  }
}
</style>
