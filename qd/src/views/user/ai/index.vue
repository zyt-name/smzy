<template>
  <Transition name="slide-fade">
    <div class="ai-chat-window" v-if="visible">
      <div class="chat-header">
        <div class="ai-info">
          <div class="ai-avatar-mini">
            <img :src="aiIcon" alt="AI" />
          </div>
          <div class="ai-status">
            <h3>智码AI助手</h3>
            <span class="online-tag">在线</span>
          </div>
        </div>
        <div class="header-actions">
          <el-icon class="close-btn" @click="$emit('close')"><Close /></el-icon>
        </div>
      </div>
      
      <div class="chat-body" ref="chatBody">
        <div v-for="msg in chatMessages" :key="msg.id" :class="['message-item', msg.type]">
          <div class="message-content">
            {{ msg.text }}
          </div>
        </div>
      </div>

      <div class="chat-footer">
        <div class="input-container">
          <el-input
            v-model="chatInput"
            placeholder="请输入您的问题..."
            @keyup.enter="sendChatMessage"
          />
          <el-button type="primary" class="send-btn" @click="sendChatMessage">发送</el-button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, nextTick, watch } from 'vue'
import { Close } from '@element-plus/icons-vue'
import aiIcon from '../../../assets/ai.png'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close'])

const chatInput = ref('')
const chatBody = ref(null)
const chatMessages = ref([
  { id: 1, type: 'ai', text: '您好！我是您的智能购物助手。有什么我可以帮您的吗？' }
])

const sendChatMessage = () => {
  if (!chatInput.value.trim()) return

  chatMessages.value.push({
    id: Date.now(),
    type: 'user',
    text: chatInput.value
  })

  const userMsg = chatInput.value
  chatInput.value = ''

  setTimeout(() => {
    let aiReply = '正在为您查询相关信息...'
    if (userMsg.includes('优惠')) aiReply = '目前智码数云全线产品正在进行春季大促，部分数码配件低至7折哦！'
    else if (userMsg.includes('物流')) aiReply = '您的订单通常会在24小时内发出，顺丰包邮，预计2-3天送达。'
    else if (userMsg.includes('人工')) aiReply = '正在为您转接人工客服，请稍候...'

    chatMessages.value.push({
      id: Date.now() + 1,
      type: 'ai',
      text: aiReply
    })

    nextTick(() => {
      if (chatBody.value) {
        chatBody.value.scrollTop = chatBody.value.scrollHeight
      }
    })
  }, 1000)
}

watch(() => props.visible, (val) => {
  if (val) {
    nextTick(() => {
      if (chatBody.value) {
        chatBody.value.scrollTop = chatBody.value.scrollHeight
      }
    })
  }
})
</script>

<style lang="scss" scoped>
.ai-chat-window {
  position: fixed;
  right: 85px;
  top: 50px;
  bottom: 50px;
  width: 33.33vw;
  background: white;
  border-radius: 24px;
  box-shadow: -10px 0 40px rgba(0,0,0,0.15);
  z-index: 99;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid #eee;

  .chat-header {
    padding: 20px 24px;
    background: linear-gradient(90deg, #f0f7ff 0%, #ffffff 100%);
    border-bottom: 1px solid #eee;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .ai-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .ai-avatar-mini {
        width: 40px;
        height: 40px;
        background: white;
        border-radius: 50%;
        border: 2px solid #2a9eff;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 4px;
        img {
          width: 100%;
          height: 100%;
          object-fit: contain;
        }
      }

      .ai-status {
        h3 {
          margin: 0;
          font-size: 16px;
          color: #333;
        }
        .online-tag {
          font-size: 12px;
          color: #52c41a;
          display: flex;
          align-items: center;
          &:before {
            content: '';
            width: 6px;
            height: 6px;
            background: #52c41a;
            border-radius: 50%;
            margin-right: 4px;
          }
        }
      }
    }

    .close-btn {
      font-size: 20px;
      color: #999;
      cursor: pointer;
      transition: all 0.3s;
      &:hover {
        color: #ff4d4f;
        transform: rotate(90deg);
      }
    }
  }

  .chat-body {
    flex: 1;
    padding: 24px;
    overflow-y: auto;
    background: #fcfcfc;
    display: flex;
    flex-direction: column;
    gap: 16px;

    .message-item {
      max-width: 85%;
      display: flex;
      
      &.ai {
        align-self: flex-start;
        .message-content {
          background: white;
          color: #333;
          border-radius: 4px 16px 16px 16px;
          box-shadow: 0 2px 8px rgba(0,0,0,0.05);
          border: 1px solid #f0f0f0;
        }
      }

      &.user {
        align-self: flex-end;
        .message-content {
          background: #2a9eff;
          color: white;
          border-radius: 16px 4px 16px 16px;
          box-shadow: 0 4px 12px rgba(42, 158, 255, 0.2);
        }
      }

      .message-content {
        padding: 12px 16px;
        font-size: 14px;
        line-height: 1.6;
      }
    }
  }

  .chat-footer {
    padding: 20px 24px;
    background: white;
    border-top: 1px solid #eee;

    .input-container {
      display: flex;
      gap: 12px;
      align-items: center;

      :deep(.el-input__wrapper) {
        border-radius: 20px;
        padding-left: 15px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.04) inset;
        background-color: #f8f8f8;
        border: 1px solid #eee;
        transition: all 0.3s;
        
        &.is-focus {
          background-color: #fff;
          border-color: #2a9eff;
          box-shadow: 0 0 0 1px #2a9eff inset !important;
        }
      }

      .send-btn {
        border-radius: 20px;
        padding: 0 24px;
        height: 32px;
        font-weight: 500;
        box-shadow: 0 4px 12px rgba(42, 158, 255, 0.3);
        transition: all 0.3s;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 15px rgba(42, 158, 255, 0.4);
        }
        
        &:active {
          transform: translateY(0);
        }
      }
    }
  }
}

.slide-fade-enter-active, .slide-fade-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}
.slide-fade-enter-from, .slide-fade-leave-to {
  transform: translateX(30px);
  opacity: 0;
}
</style>
