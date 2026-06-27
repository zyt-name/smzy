<template>
  <div class="order-page">
    <component
      :is="currentComponent"
      :order-type="orderType"
      :product-data="productData"
      :cart-data="cartData"
      @to-order-list="handleToOrderList"
      @to-user="handleToUser"
      @to-login="handleToLogin"
      @to-register="handleToRegister"
      @to-account="handleToAccount"
      @to-cart="handleToCart"
      @to-scdp="handleToScdp"
      @to-pay="handleToPay"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import ShopOrder from './ShopOrder.vue'
import ConfirmOrder from './ConfirmOrder.vue'

const props = defineProps({
  orderType: {
    type: String,
    default: 'confirm'
  },
  productData: {
    type: Object,
    default: null
  },
  cartData: {
    type: Object,
    default: null
  }
})

const currentComponent = computed(() => {
  if (props.orderType === 'cart' || props.orderType === 'shop') {
    return ShopOrder
  } else if (props.orderType === 'confirm') {
    return ConfirmOrder
  }
  return null
})

const emit = defineEmits(['toOrderList', 'toUser', 'toCart', 'toLogin', 'toRegister', 'toAccount', 'toScdp', 'toPay'])

const handleToOrderList = () => {
  emit('toOrderList')
}

const handleToUser = () => {
  emit('toUser')
}

const handleToLogin = () => {
  emit('toLogin')
}

const handleToRegister = () => {
  emit('toRegister')
}

const handleToAccount = () => {
  emit('toAccount')
}

const handleToCart = () => {
  emit('toCart')
}

const handleToScdp = () => {
  emit('toScdp')
}

const handleToPay = (data) => {
  emit('toPay', data)
}
</script>

<style lang="scss" scoped>
.order-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}
</style>
