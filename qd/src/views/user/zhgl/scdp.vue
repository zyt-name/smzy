<template>
  <div class="content-section">
    <h3 class="section-title">收藏的店铺</h3>
    
    <div v-if="favoriteShops.length > 0" class="shop-list" v-loading="loading">
      <div v-for="shop in favoriteShops" :key="shop.id" class="shop-card">
        <div class="shop-main" @click="goToShop(shop.id)">
          <div class="shop-logo">
            <img :src="merchantImg" :alt="shop.name">
          </div>
          <div class="shop-info">
            <h4 class="shop-name">{{ shop.name }}</h4>
            <div class="shop-meta">
              <span class="shop-phone">电话: {{ shop.phone }}</span>
              <span class="shop-status" :class="shop.status === 0 ? 'active' : 'inactive'">
                {{ shop.status === 0 ? '营业中' : '已歇业' }}
              </span>
            </div>
            <div class="shop-addresses" v-if="shop.addressList && shop.addressList.length > 0">
              <span v-for="(addr, index) in shop.addressList" :key="index" class="shop-address">
                {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}
              </span>
            </div>
          </div>
        </div>
        <div class="shop-actions">
          <ActionButton type="danger" size="medium" @click.stop="cancelFavorite(shop.id)">取消收藏</ActionButton>
        </div>
      </div>
    </div>
    
    <el-empty v-else description="暂无收藏的店铺" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFavoriteMerchantList, deleteFavoriteMerchant } from '../../../api/User'
import ActionButton from '../../../components/ActionButton.vue'
import merchantImg from '../../../assets/merchant.png'

const emit = defineEmits(['toShop'])

const favoriteShops = ref([])
const loading = ref(false)

onMounted(() => {
  fetchFavoriteList()
})

const fetchFavoriteList = async () => {
  loading.value = true
  try {
    const res = await getFavoriteMerchantList(1, 100)
    if (res.data && res.data.data) {
      favoriteShops.value = res.data.data.map(item => ({
        id: item.merchantId,
        name: item.merchantName,
        phone: item.phone,
        status: item.status,
        addressList: item.addressList || []
      }))
    } else {
      favoriteShops.value = []
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

const goToShop = (shopId) => {
  console.log('goToShop called:', shopId)
  const url = `${window.location.origin}${window.location.pathname}?page=shop&merchantId=${shopId}`
  window.open(url, '_blank')
}

const cancelFavorite = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏该店铺吗？', '提示', {
      type: 'warning'
    })
    await deleteFavoriteMerchant(id)
    favoriteShops.value = favoriteShops.value.filter(item => item.id !== id)
    ElMessage.success('已取消收藏')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消收藏失败')
    }
  }
}
</script>

<style lang="scss" scoped>
.content-section {
  .section-title {
    font-size: 20px;
    color: #333;
    margin-bottom: 30px;
    font-weight: 600;
  }

  .shop-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .shop-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    border: 1px solid #eee;
    border-radius: 8px;
    transition: all 0.3s;
    
    &:hover {
      border-color: #2a9eff;
      box-shadow: 0 4px 12px rgba(42, 158, 255, 0.1);
    }

    .shop-main {
      display: flex;
      gap: 20px;
      align-items: center;
      cursor: pointer;
      flex: 1;
    }

    .shop-logo {
      width: 80px;
      height: 80px;
      border-radius: 8px;
      overflow: hidden;
      border: 1px solid #f0f0f0;
      img { width: 100%; height: 100%; object-fit: cover; }
    }

    .shop-info {
      .shop-name { margin: 0 0 8px; font-size: 18px; color: #333; }
      .shop-meta {
        display: flex;
        align-items: center;
        gap: 15px;
        margin-bottom: 8px;
        .shop-phone { font-size: 13px; color: #666; }
        .shop-status {
          font-size: 12px;
          padding: 2px 8px;
          border-radius: 4px;
          &.active { background: #e6f9e6; color: #3dc33d; }
          &.inactive { background: #f5f5f5; color: #999; }
        }
      }
      .shop-addresses {
        display: flex;
        flex-direction: column;
        gap: 4px;
        .shop-address { font-size: 12px; color: #999; }
      }
    }

    .shop-actions {
      display: flex;
      flex-direction: column;
      gap: 10px;
      align-items: flex-end;
    }
  }
}
</style>