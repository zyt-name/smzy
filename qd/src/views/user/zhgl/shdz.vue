<template>
  <div class="content-section address-section">
    <div class="section-header">
      <h3 class="section-title">收货地址</h3>
      <ActionButton type="success" size="xlarge" @click="showAddressDialog()">添加新地址</ActionButton>
    </div>
    
    <div class="address-scroll">
      <div class="address-list">
        <div v-for="(addr, index) in addressList" :key="addr.id || index" class="address-card">
          <div class="card-header">
            <span class="tag" v-if="addr.isDefault === 0">默认地址</span>
            <div class="actions">
              <ActionButton v-if="addr.isDefault !== 0" type="default" size="small" @click="setDefaultAddress(addr.id)">设为默认</ActionButton>
              <ActionButton type="primary" size="small" @click="showAddressDialog(addr, index)">编辑</ActionButton>
              <ActionButton type="danger" size="small" @click="deleteAddress(index)">删除</ActionButton>
            </div>
          </div>
          <div class="card-body">
            <p class="name"><el-icon><User /></el-icon> {{ addr.receiverName }}</p>
            <p class="phone"><el-icon><Iphone /></el-icon> {{ addr.receiverPhone }}</p>
            <p class="detail"><el-icon><Location /></el-icon> {{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detailAddress }}</p>
          </div>
        </div>
      </div>
    </div>

    <el-dialog 
      v-model="addressDialogVisible" 
      :title="editingIndex === -1 ? '添加新地址' : '编辑地址'"
      width="500px"
    >
      <el-form :model="addressForm" label-width="80px">
        <el-form-item label="收货人">
          <el-input v-model="addressForm.receiverName" placeholder="收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="addressForm.receiverPhone" placeholder="收货人手机号" />
        </el-form-item>
        <el-form-item label="所在地区">
          <el-cascader
            v-model="selectedRegion"
            :options="regionData"
            placeholder="请选择省/市/区"
            style="width: 100%"
            filterable
          />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="addressForm.detailAddress" type="textarea" placeholder="街道、楼牌号等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Location, Iphone } from '@element-plus/icons-vue'
import { addAddress, updateIsDefault, getAddressList, updateAddress, deleteAddress as deleteAddressApi } from '../../../api/User'
import ActionButton from '../../../components/ActionButton.vue'
import { regionData } from 'element-china-area-data'

const addressList = ref([])
const addressDialogVisible = ref(false)
const editingIndex = ref(-1)
const addressForm = ref({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 1
})
const selectedRegion = ref([])

const findRegionCodes = (provinceName, cityName, districtName) => {
  for (const p of regionData) {
    if (p.label === provinceName && p.children) {
      if (!cityName) return [p.value]
      for (const c of p.children) {
        if (c.label === cityName) {
          if (!districtName || !c.children) return [p.value, c.value]
          for (const d of c.children) {
            if (d.label === districtName) return [p.value, c.value, d.value]
          }
          return [p.value, c.value]
        }
      }
      return [p.value]
    }
  }
  return []
}

const getSelectedLabels = () => {
  const labels = []
  let options = regionData
  for (const code of selectedRegion.value) {
    const found = options.find(item => item.value === code)
    if (found) {
      labels.push(found.label)
      options = found.children || []
    }
  }
  return labels
}

onMounted(() => {
  loadAddressList()
})

const loadAddressList = async () => {
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
  } catch (error) {
    console.error('获取地址列表失败:', error)
  }
}

const showAddressDialog = (addr = null, index = -1) => {
  editingIndex.value = index
  if (addr) {
    addressForm.value = { ...addr }
    selectedRegion.value = findRegionCodes(addr.province, addr.city, addr.district)
  } else {
    addressForm.value = {
      receiverName: '',
      receiverPhone: '',
      province: '',
      city: '',
      district: '',
      detailAddress: '',
      isDefault: 1
    }
    selectedRegion.value = []
  }
  addressDialogVisible.value = true
}

const saveAddress = async () => {
  try {
    const labels = getSelectedLabels()
    const submitData = {
      ...addressForm.value,
      province: labels[0] || '',
      city: labels[1] || '',
      district: labels[2] || ''
    }
    if (editingIndex.value === -1) {
      await addAddress(submitData)
    } else {
      await updateAddress(submitData)
    }
    addressDialogVisible.value = false
    loadAddressList()
  } catch (error) {
    console.error('保存地址失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

const setDefaultAddress = (addressId) => {
  ElMessageBox.confirm('确定将该地址设为默认收货地址吗？', '提示', {
    type: 'info',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await updateIsDefault(addressId)
      loadAddressList()
    } catch (error) {
      console.error('设置默认地址失败:', error)
      ElMessage.error('设置失败，请重试')
    }
  })
}

const deleteAddress = (index) => {
  const addr = addressList.value[index]
  ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await deleteAddressApi(addr.id || addr.addressId)
      loadAddressList()
    } catch (error) {
      console.error('删除地址失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  })
}
</script>

<style lang="scss" scoped>
.address-section {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 200px);
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;

  .section-title {
    font-size: 20px;
    color: #333;
    margin-bottom: 0;
    font-weight: 600;
  }
}

.address-scroll {
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;

  &::-webkit-scrollbar { width: 5px; }
  &::-webkit-scrollbar-thumb { background: #d0d8e0; border-radius: 3px; }
  &::-webkit-scrollbar-thumb:hover { background: #b0bcc8; }
}

.address-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.address-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 15px;
  transition: all 0.3s;
  &:hover { border-color: #2a9eff; box-shadow: 0 4px 12px rgba(42, 158, 255, 0.1); }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 15px;
    .tag { background: #e6f1ff; color: #2a9eff; font-size: 12px; padding: 2px 8px; border-radius: 4px; }
  }

  .card-body {
    p { margin: 8px 0; color: #666; font-size: 14px; display: flex; align-items: center; gap: 8px; }
    .name { color: #333; font-weight: 600; font-size: 16px; }
  }
}
</style>
