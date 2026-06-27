<template>
  <div class="application-sidebar">
    <RequestPanel
      title="商家申请"
      :data="formattedApps"
      :total="applications.length"
      v-model:currentPage="currentAppPage"
      :pageSize="appPageSize"
      approveTitle="批准"
      @approve="handleApproveMerchant"
      @reject="handleRejectMerchant"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import RequestPanel from '../../../../components/RequestPanel.vue'

const currentAppPage = ref(1)
const appPageSize = ref(5)

// 模拟商家申请数据
const applications = ref([
  { id: 1, merchantName: '极客先锋科技', category: '电脑/配件', time: '15分钟前', reason: '申请开设品牌专营店', status: 'pending' },
  { id: 2, merchantName: '音悦听耳机店', category: '影音娱乐', time: '40分钟前', reason: '智享生活旗舰店入驻', status: 'pending' },
  { id: 3, merchantName: '快科技手机店', category: '手机通讯', time: '2小时前', reason: '全系列手机代理入驻', status: 'pending' },
  { id: 4, merchantName: '智能管家', category: '智能家居', time: '5小时前', reason: '小米生态链产品入驻', status: 'pending' }
])

const formattedApps = computed(() => {
  const start = (currentAppPage.value - 1) * appPageSize.value
  const end = start + appPageSize.value
  return applications.value.slice(start, end).map(a => ({
    id: a.id,
    subject: a.merchantName,
    actionText: '申请入驻',
    time: a.time,
    highlightLabel: '类目',
    highlightValue: a.category,
    highlightType: 'warning',
    description: a.reason
  }))
})

const handleApproveMerchant = (item) => {
  ElMessage.success(`已批准 ${item.subject} 的入驻申请`)
}

const handleRejectMerchant = (item) => {
  ElMessage.warning(`已拒绝 ${item.subject} 的入驻申请`)
}
</script>

<style lang="scss" scoped>
.application-sidebar {
  width: 380px;
  display: flex;
}
</style>
