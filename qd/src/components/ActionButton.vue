<template>
  <button class="action-btn" :class="[type, size]" @click="$emit('click', $event)">
    <slot />
  </button>
</template>

<script setup>
defineEmits(['click'])
defineProps({
  type: {
    type: String,
    default: 'primary',
    validator: v => ['primary', 'danger', 'success', 'default', 'orange'].includes(v)
  },
  size: {
    type: String,
    default: 'medium',
    validator: v => ['small', 'medium', 'large', 'xlarge'].includes(v)
  }
})
</script>

<style lang="scss" scoped>
.action-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  position: relative;
  z-index: 1;
  overflow: hidden;
  border-radius: 12px;
  transition: color 0.3s ease, background-color 0.2s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;

  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    border-radius: 50%;
    transform: translate(-50%, -50%);
    transition: width 0.4s ease, height 0.4s ease;
    z-index: -1;
  }

  &.primary {
    color: #89c4ff;
    &::before { background: rgba(137, 196, 255, 0.2); }
    &:hover { color: #2a9eff; }
    &:hover::before { width: 150%; height: 300%; }
  }

  &.danger {
    color: #ff8a8a;
    &::before { background: rgba(255, 138, 138, 0.15); }
    &:hover { color: #ff4d4f; }
    &:hover::before { width: 150%; height: 300%; }
  }

  &.success {
    color: #7dd87d;
    &::before { background: rgba(125, 216, 125, 0.15); }
    &:hover { color: #3dc33d; }
    &:hover::before { width: 150%; height: 300%; }
  }

  &.orange {
    color: #ff9f43;
    &::before { background: rgba(255, 159, 67, 0.15); }
    &:hover { color: #ff7f00; }
    &:hover::before { width: 150%; height: 300%; }
  }

  &.default {
    color: #999;
    &::before { background: rgba(153, 153, 153, 0.12); }
    &:hover { color: #666; }
    &:hover::before { width: 150%; height: 300%; }
  }

  &.small {
    padding: 2px 10px;
    font-size: 12px;
  }
  &.medium {
    padding: 4px 14px;
    font-size: 13px;
  }
  &.large {
    padding: 6px 18px;
    font-size: 14px;
  }
  &.xlarge {
    padding: 10px 28px;
    font-size: 16px;
  }
}
</style>
