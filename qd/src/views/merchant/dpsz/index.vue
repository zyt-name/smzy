<template>
  <div class="dpsz-container">
    <el-row :gutter="20">
      <!-- 基础信息模块 -->
      <el-col :span="12">
        <el-card shadow="never" class="settings-card">
          <template #header>
            <div class="card-header">
              <span class="title">基础信息</span>
              <el-button type="primary" @click="handleSaveInfo">保存修改</el-button>
            </div>
          </template>
          
          <el-form :model="shopForm" label-position="top" class="shop-form" :rules="basicRules" ref="basicFormRef">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="shopForm.username" placeholder="请输入用户名" />
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input v-model="shopForm.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>

            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="shopForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 店铺地址模块 -->
      <el-col :span="12">
        <el-card shadow="never" class="settings-card">
          <template #header>
            <div class="card-header">
              <span class="title">店铺地址</span>
              <el-button type="primary" @click="showAddAddressDialog">添加地址</el-button>
            </div>
          </template>
          
          <div class="address-list">
            <el-empty v-if="addressList.length === 0" description="暂无地址，请添加" />
            <div v-else v-for="(address, index) in addressList" :key="index" class="address-item">
              <div class="address-info">
                <div class="address-region">
                  <el-tag size="small" type="info">{{ address.province }}</el-tag>
                  <el-tag size="small" type="info">{{ address.city }}</el-tag>
                  <el-tag size="small" type="info">{{ address.district }}</el-tag>
                </div>
                <div class="address-detail">{{ address.detailAddress }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加地址对话框 -->
    <el-dialog v-model="addressDialogVisible" title="添加店铺地址" width="500px">
      <el-form :model="addressForm" label-position="top" :rules="addressRules" ref="addressFormRef">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item label="省份" prop="province">
              <el-input v-model="addressForm.province" placeholder="省份" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市" prop="city">
              <el-input v-model="addressForm.city" placeholder="城市" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区/县" prop="district">
              <el-input v-model="addressForm.district" placeholder="区/县" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="addressForm.detailAddress" type="textarea" :rows="3" placeholder="请输入详细地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddAddress">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onActivated } from 'vue'
import { ElMessage } from 'element-plus'
import { updateMerchant, addAddress, getAddressList, getMerchantInfo } from '../../../api/merchant/Metchant'

const loading = ref(false)
const basicFormRef = ref(null)
const addressFormRef = ref(null)
const addressDialogVisible = ref(false)

// 基础信息表单
const shopForm = reactive({
  username: '',
  password: '',
  phone: ''
})

// 地址列表
const addressList = reactive([])

// 地址表单
const addressForm = reactive({
  province: '',
  city: '',
  district: '',
  detailAddress: ''
})

// 基础信息校验规则
const basicRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 地址校验规则
const addressRules = {
  province: [
    { required: true, message: '请输入省份', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请输入城市', trigger: 'blur' }
  ],
  district: [
    { required: true, message: '请输入区/县', trigger: 'blur' }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

// 保存基础信息
const handleSaveInfo = async () => {
  if (!basicFormRef.value) return
  
  await basicFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await updateMerchant({
          username: shopForm.username,
          password: shopForm.password,
          phone: shopForm.phone
        })
        if (res.code === 200 || res.code === 1) {
          ElMessage.success('基础信息保存成功')
        } else {
          ElMessage.error(res.msg || '保存失败')
        }
      } catch (error) {
        console.error('保存基础信息失败:', error)
        ElMessage.error('保存失败')
      }
    }
  })
}

// 显示添加地址对话框
const showAddAddressDialog = () => {
  addressDialogVisible.value = true
  // 重置表单
  addressForm.province = ''
  addressForm.city = ''
  addressForm.district = ''
  addressForm.detailAddress = ''
}

// 添加地址
const handleAddAddress = async () => {
  if (!addressFormRef.value) return
  
  await addressFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await addAddress({
          province: addressForm.province,
          city: addressForm.city,
          district: addressForm.district,
          detailAddress: addressForm.detailAddress
        })
        if (res.code === 200 || res.code === 1) {
          ElMessage.success('地址添加成功')
          addressDialogVisible.value = false
          // 刷新地址列表
          await fetchAddressList()
        } else {
          ElMessage.error(res.msg || '添加失败')
        }
      } catch (error) {
        console.error('添加地址失败:', error)
        ElMessage.error('添加失败')
      }
    }
  })
}

// 获取地址列表
const fetchAddressList = async () => {
  try {
    const res = await getAddressList()
    if (res.code === 200 || res.code === 1) {
      addressList.length = 0
      if (res.data && Array.isArray(res.data)) {
        addressList.push(...res.data)
      }
    }
  } catch (error) {
    console.error('获取地址列表失败:', error)
  }
}

// 获取商户信息
const fetchMerchantInfo = async () => {
  try {
    const res = await getMerchantInfo()
    if (res.code === 200 || res.code === 1) {
      if (res.data) {
        shopForm.username = res.data.username || ''
        shopForm.phone = res.data.phone || ''
        // 密码不显示在页面上，保持为空
        shopForm.password = ''
      }
    }
  } catch (error) {
    console.error('获取商户信息失败:', error)
  }
}

// 加载数据
const fetchData = async () => {
  try {
    loading.value = true
    await Promise.all([
      fetchMerchantInfo(),
      fetchAddressList()
    ])
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 使用 onActivated 只在组件被激活时加载数据
onActivated(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.dpsz-container {
  padding: 20px;
}

.settings-card {
  border-radius: 12px;
  border: 1px solid #f0f2f5;
  margin-bottom: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }
}

.shop-form {
  padding: 10px 0;
  
  :deep(.el-form-item__label) {
    font-weight: 600;
    color: #606266;
  }
}

.address-list {
  padding: 10px 0;
  
  .address-item {
    padding: 16px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    margin-bottom: 12px;
    background-color: #f8fbff;
    transition: all 0.3s;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    &:hover {
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }
    
    .address-info {
      .address-region {
        margin-bottom: 8px;
        display: flex;
        gap: 8px;
      }
      
      .address-detail {
        font-size: 14px;
        color: #303133;
        line-height: 1.5;
      }
    }
  }
}
</style>
