<template>
  <div class="application-sidebar">
    <RequestPanel
      title="用户申请"
      :data="formattedRefunds"
      :total="refundRequests.length"
      v-model:currentPage="currentRefundPage"
      :pageSize="refundPageSize"
      @approve="handleApproveRefund"
      @reject="handleRejectRefund"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import RequestPanel from '../../../../components/RequestPanel.vue'

const currentRefundPage = ref(1)
const refundPageSize = ref(5)

// 模拟退款申请数据
const refundRequests = ref([
  { id: 1, userName: '李明', amount: '299.00', time: '10分钟前', reason: '商品质量不满意，要求退货退款', status: 'pending' },
  { id: 2, userName: '王小红', amount: '45.50', time: '25分钟前', reason: '拍错了，想重新下单', status: 'pending' },
  { id: 3, userName: '张伟', amount: '1280.00', time: '1小时前', reason: '商品一直未收到，物流无更新', status: 'pending' }
])

const formattedRefunds = computed(() => {
  const start = (currentRefundPage.value - 1) * refundPageSize.value
  const end = start + refundPageSize.value
  return refundRequests.value.slice(start, end).map(r => ({
    id: r.id,
    subject: r.userName,
    actionText: '申请退款',
    time: r.time,
    highlightLabel: '金额',
    highlightValue: `¥${r.amount}`,
    highlightType: 'danger',
    description: r.reason
  }))
})

const handleApproveRefund = (item) => {
  ElMessage.success(`已通过 ${item.subject} 的退款申请`)
}

const handleRejectRefund = (item) => {
  ElMessage.warning(`已拒绝 ${item.subject} 的退款申请`)
}
</script>

<style lang="scss" scoped>
.application-sidebar {
  width: 380px;
  display: flex;
}
</style>
