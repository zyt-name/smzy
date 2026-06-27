<template>
  <Teleport to="body">
    <div class="response-notify-container">
      <TransitionGroup name="notify-slide">
        <div
          v-for="item in notifications"
          :key="item.id"
          class="notify-item"
          :class="item.type"
          @click="remove(item.id)"
        >
          <span class="notify-icon">
            <svg v-if="item.type === 'success'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
          </span>
          <span class="notify-text">{{ item.message }}</span>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { setNotifyInstance } from '../utils/notify'

const notifications = ref([])
let idCounter = 0

const add = (message, type = 'success', duration = 3000) => {
  const id = ++idCounter
  const item = { id, message, type }
  notifications.value.push(item)
  if (duration > 0) {
    setTimeout(() => remove(id), duration)
  }
}

const remove = (id) => {
  const index = notifications.value.findIndex(n => n.id === id)
  if (index > -1) {
    notifications.value.splice(index, 1)
  }
}

const handleResponse = (res, silent = false) => {
  if (silent || !res) return res
  if (res.code === 200) {
    add(res.msg && res.msg !== '未知错误' ? res.msg : '操作成功！', 'success')
  } else if (res.code === 500) {
    add(res.msg && res.msg !== '未知错误' ? res.msg : '服务器繁忙，请稍后重试~', 'error', 4000)
  } else if (res.code && res.code !== 200) {
    add(res.msg || '操作失败', 'error', 4000)
  }
  return res
}

onMounted(() => {
  setNotifyInstance({ add, remove, handleResponse })
})

defineExpose({ add, remove, handleResponse })
</script>

<style lang="scss" scoped>
.response-notify-container {
  position: fixed;
  top: 16px;
  right: 0;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 8px;
  pointer-events: none;
  max-width: 360px;
}

.notify-item {
  pointer-events: auto;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 20px 8px 24px;
  font-size: 13px;
  line-height: 1.4;
  cursor: pointer;
  clip-path: polygon(16px 0, 100% 0, 100% 100%, 0 100%);
  border: none;

  &.success {
    background: linear-gradient(to right, #fff 0%, #d4f5d4 30%, #7dd87d 100%);
    color: #2d7d2d;

    .notify-icon svg { width: 16px; height: 16px; }
  }

  &.error {
    background: linear-gradient(to right, #fff 0%, #fcc 12%, #ff8a8a 55%, #ff4d4f 100%);
    color: #b91c1c;

    .notify-icon svg { width: 15px; height: 15px; }
  }

  .notify-icon {
    flex-shrink: 0;
    display: flex;
    align-items: center;
  }

  .notify-text {
    word-break: break-word;
  }
}

.notify-slide-enter-active {
  transition: all 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.notify-slide-leave-active {
  transition: all 0.3s ease-in;
}
.notify-slide-enter-from {
  opacity: 0;
  transform: translateX(60px) scale(0.9);
}
.notify-slide-leave-to {
  opacity: 0;
  transform: translateX(40px) scale(0.9);
}
.notify-slide-move {
  transition: transform 0.3s ease;
}
</style>
