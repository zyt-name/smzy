<template>
  <div class="content-management">
    <!-- Banner 点击率统计 -->
    <div class="stats-section">
      <el-card shadow="never" class="chart-card">
        <template #header>
          <div class="card-header">
            <span>Banner 点击率统计</span>
            <el-button type="primary" size="small" @click="fetchClickStats">刷新数据</el-button>
          </div>
        </template>
        <div class="stats-grid-inner" v-loading="statsLoading">
          <div v-for="stat in clickStats" :key="stat.bannerId" class="inner-stat-card">
            <div class="inner-stat-label">{{ stat.title }}</div>
            <div class="inner-stat-value">{{ stat.clickCount }}</div>
            <div class="inner-stat-footer">
              <span class="rate-text">点击率: {{ stat.clickRate.toFixed(1) }}%</span>
            </div>
          </div>
          <el-empty v-if="clickStats.length === 0 && !statsLoading" description="暂无 Banner 数据" />
        </div>
      </el-card>
    </div>

    <!-- 首页动态横幅配置 -->
    <div class="banner-section">
      <el-card shadow="never" class="chart-card">
        <template #header>
          <div class="card-header">
            <span>首页动态横幅配置</span>
          </div>
        </template>
        <div class="banner-form-container">
          <el-form :model="bannerForm" label-width="100px" class="banner-form">
            <el-row :gutter="40">
              <el-col :span="12">
                <el-form-item label="标题" required>
                  <el-input v-model="bannerForm.title" placeholder="请输入横幅标题" />
                </el-form-item>
                <el-form-item label="商品ID" required>
                  <el-input v-model="bannerForm.goodsId" placeholder="请输入商品ID" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="图片" required>
                  <div class="upload-area">
                    <el-upload
                      class="image-uploader"
                      action="#"
                      :auto-upload="false"
                      :show-file-list="false"
                      :on-change="handleImageChange"
                      accept="image/*"
                    >
                      <div v-if="bannerForm.imagePath" class="image-preview">
                        <ProductImage :src="bannerForm.imagePath" alt="banner" class="preview-image" />
                        <div class="image-actions">
                          <el-button type="primary" size="small" icon="Edit">更换图片</el-button>
                        </div>
                      </div>
                      <div v-else class="upload-placeholder">
                        <el-icon class="upload-icon"><Plus /></el-icon>
                        <div class="upload-text">点击上传图片</div>
                      </div>
                    </el-upload>
                    <div class="upload-tip">支持 jpg、png 格式，建议尺寸 1280x520</div>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item class="form-actions">
              <el-button type="primary" size="large" @click="handleAddBanner" :loading="submitLoading">
                添加横幅
              </el-button>
              <el-button size="large" @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-card>
    </div>

    <!-- 已有横幅列表 -->
    <div class="banner-list-section" v-if="banners.length > 0">
      <el-card shadow="never" class="chart-card">
        <template #header>
          <div class="card-header">
            <span>已有横幅列表</span>
            <el-button type="primary" size="small" @click="fetchBanners">刷新列表</el-button>
          </div>
        </template>
        <div class="banner-list-container" v-loading="bannerLoading">
          <div v-for="banner in banners" :key="banner.id" class="banner-item">
            <div class="banner-preview">
              <ProductImage :src="banner.imagePath" alt="banner" class="banner-image" />
              <div class="banner-actions">
                <el-button size="small" circle icon="Edit" @click="openEditDialog(banner)" />
                <el-button size="small" circle icon="Delete" type="danger" @click="handleDeleteBanner(banner.id)" />
              </div>
            </div>
            <div class="banner-info">
              <div class="info-row">
                <span class="label">标题：</span>
                <span class="value">{{ banner.title }}</span>
              </div>
              <div class="info-row">
                <span class="label">商品ID：</span>
                <span class="value">{{ banner.goodsId }}</span>
              </div>
              <div class="info-row">
                <span class="label">点击次数：</span>
                <span class="value">{{ banner.clickCount }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 搜索引擎管理 -->
    <div class="search-engine-section">
      <el-card shadow="never" class="chart-card">
        <template #header>
          <div class="card-header">
            <span>搜索引擎管理</span>
          </div>
        </template>
        <div class="sync-section">
          <div class="sync-info">
            <div class="sync-title">重置搜索引擎</div>
            <div class="sync-desc">重置系统搜索引擎数据，用于数据同步。此操作将重新索引所有商品数据，可能需要较长时间。</div>
          </div>
          <el-button type="warning" :icon="Refresh" :loading="syncLoading" @click="handleSyncEs" class="sync-btn">重置搜索引擎</el-button>
        </div>
      </el-card>
    </div>

    <!-- 编辑 Banner 弹窗 -->
    <el-dialog v-model="showEditDialog" title="编辑横幅" width="600px" destroy-on-close>
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="标题" required>
          <el-input v-model="editForm.title" placeholder="请输入横幅标题" />
        </el-form-item>
        <el-form-item label="商品ID" required>
          <el-input v-model="editForm.goodsId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="图片" required>
          <div class="upload-area">
            <el-upload
              class="image-uploader"
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleEditImageChange"
              accept="image/*"
            >
              <div v-if="editForm.imagePath" class="image-preview">
                <ProductImage :src="editForm.imagePath" alt="banner" class="preview-image" />
                <div class="image-actions">
                  <el-button type="primary" size="small" icon="Edit">更换图片</el-button>
                </div>
              </div>
              <div v-else class="upload-placeholder">
                <el-icon class="upload-icon"><Plus /></el-icon>
                <div class="upload-text">点击上传图片</div>
              </div>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateBanner" :loading="submitLoading">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Picture, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import { syncAllToEs } from '../../../api/admin/AdminShopES'
import { addBanner, getBannerClickStats, updateBanner, getAllBanners, deleteBanner } from '../../../api/admin/AdminBanner'
import { uploadImage } from '../../../api/admin/AdminImg'
import ProductImage from '../../../components/ProductImage.vue'

const syncLoading = ref(false)
const bannerLoading = ref(false)
const statsLoading = ref(false)
const submitLoading = ref(false)
const showEditDialog = ref(false)

const banners = ref([])
const clickStats = ref([])

const bannerForm = reactive({
  title: '',
  imagePath: '',
  goodsId: ''
})

const editForm = reactive({
  id: '',
  title: '',
  imagePath: '',
  goodsId: ''
})

const fetchBanners = async () => {
  bannerLoading.value = true
  try {
    const res = await getAllBanners()
    if (res.code === 200) {
      banners.value = res.data || []
    }
  } catch (error) {
    console.error('获取 Banner 列表失败:', error)
  } finally {
    bannerLoading.value = false
  }
}

const fetchClickStats = async () => {
  statsLoading.value = true
  try {
    const res = await getBannerClickStats()
    if (res.code === 200) {
      clickStats.value = res.data || []
    }
  } catch (error) {
    console.error('获取点击率统计失败:', error)
  } finally {
    statsLoading.value = false
  }
}

const handleImageChange = async (file) => {
  if (!file.raw) return
  try {
    const res = await uploadImage(file.raw)
    if (res) {
      bannerForm.imagePath = res
      ElMessage.success('图片上传成功')
    }
  } catch (error) {
    ElMessage.error('图片上传失败')
  }
}

const handleEditImageChange = async (file) => {
  if (!file.raw) return
  try {
    const res = await uploadImage(file.raw)
    if (res) {
      editForm.imagePath = res
      ElMessage.success('图片上传成功')
    }
  } catch (error) {
    ElMessage.error('图片上传失败')
  }
}

const handleAddBanner = async () => {
  if (!bannerForm.title || !bannerForm.imagePath || !bannerForm.goodsId) {
    ElMessage.warning('请填写完整信息')
    return
  }
  submitLoading.value = true
  try {
    const res = await addBanner(bannerForm)
    if (res.code === 200) {
      ElMessage.success('添加成功')
      resetForm()
      fetchBanners()
      fetchClickStats()
    } else {
      ElMessage.error(res.msg || '添加失败')
    }
  } catch (error) {
    ElMessage.error('添加失败')
  } finally {
    submitLoading.value = false
  }
}

const resetForm = () => {
  bannerForm.title = ''
  bannerForm.imagePath = ''
  bannerForm.goodsId = ''
}

const openEditDialog = (banner) => {
  editForm.id = banner.id
  editForm.title = banner.title
  editForm.imagePath = banner.imagePath
  editForm.goodsId = banner.goodsId
  showEditDialog.value = true
}

const handleUpdateBanner = async () => {
  if (!editForm.title || !editForm.imagePath || !editForm.goodsId) {
    ElMessage.warning('请填写完整信息')
    return
  }
  submitLoading.value = true
  try {
    const res = await updateBanner(editForm)
    if (res.code === 200) {
      ElMessage.success('修改成功')
      showEditDialog.value = false
      fetchBanners()
    } else {
      ElMessage.error(res.msg || '修改失败')
    }
  } catch (error) {
    ElMessage.error('修改失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDeleteBanner = async (bannerId) => {
  try {
    await ElMessageBox.confirm('确定要删除该横幅吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteBanner(bannerId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchBanners()
      fetchClickStats()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSyncEs = async () => {
  syncLoading.value = true
  try {
    await syncAllToEs()
    ElMessage.success('ES数据全量同步成功')
  } catch (error) {
    ElMessage.error('同步失败，请重试')
  } finally {
    syncLoading.value = false
  }
}

onMounted(() => {
  fetchBanners()
  fetchClickStats()
})
</script>

<style lang="scss" scoped>
.content-management {
  padding: 0;
  min-height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;

  .chart-card {
    border-radius: 12px;
    border: none;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      span { font-weight: bold; font-size: 16px; }
    }
  }

  .stats-section {
    .stats-grid-inner {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      gap: 20px;
      padding: 10px 0;

      .inner-stat-card {
        padding: 24px;
        background: linear-gradient(135deg, #f8fbff 0%, #e8f4ff 100%);
        border-radius: 12px;
        border: 1px solid #e8f4ff;
        transition: all 0.3s;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 20px rgba(42, 158, 255, 0.15);
        }
        
        .inner-stat-label { 
          font-size: 14px; 
          color: #606266; 
          margin-bottom: 12px; 
        }
        .inner-stat-value { 
          font-size: 32px; 
          font-weight: bold; 
          color: #2a9eff; 
          margin-bottom: 12px; 
        }
        .inner-stat-footer {
          font-size: 13px;
          color: #909399;
          .rate-text {
            color: #67c23a;
            font-weight: 500;
          }
        }
      }
    }
  }

  .banner-section {
    .banner-form-container {
      padding: 20px 0;
    }

    .banner-form {
      .form-actions {
        margin-top: 20px;
        padding-top: 20px;
        border-top: 1px solid #f0f0f0;
      }
    }

    .upload-area {
      width: 100%;
      
      .image-uploader {
        width: 100%;
        
        :deep(.el-upload) {
          width: 100%;
          border: 2px dashed #d9d9d9;
          border-radius: 8px;
          cursor: pointer;
          position: relative;
          overflow: hidden;
          transition: all 0.3s;
          
          &:hover {
            border-color: #2a9eff;
          }
        }
      }

      .upload-placeholder {
        width: 100%;
        height: 200px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        background: #fafafa;
        
        .upload-icon {
          font-size: 48px;
          color: #c0c4cc;
        }
        
        .upload-text {
          margin-top: 12px;
          font-size: 14px;
          color: #909399;
        }
      }

      .image-preview {
        width: 100%;
        height: 200px;
        position: relative;
        
        .preview-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
        
        .image-actions {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background: rgba(0, 0, 0, 0.5);
          display: flex;
          justify-content: center;
          align-items: center;
          opacity: 0;
          transition: opacity 0.3s;
        }
        
        &:hover .image-actions {
          opacity: 1;
        }
      }

      .upload-tip {
        margin-top: 10px;
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .banner-list-section {
    .banner-list-container {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 20px;
      padding: 10px 0;
    }

    .banner-item {
      background: #f8fbff;
      border-radius: 12px;
      overflow: hidden;
      transition: all 0.3s;

      &:hover {
        background: #fff;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }

      .banner-preview {
        position: relative;
        height: 160px;
        background: #f5f7fa;

        .banner-image { 
          width: 100%; 
          height: 100%; 
          object-fit: cover; 
        }
        
        .banner-actions {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background: rgba(0, 0, 0, 0.4);
          display: flex;
          justify-content: center;
          align-items: center;
          gap: 15px;
          opacity: 0;
          transition: opacity 0.3s;
        }

        &:hover .banner-actions { opacity: 1; }
      }

      .banner-info {
        padding: 15px;
        
        .info-row {
          display: flex;
          margin-bottom: 8px;
          font-size: 13px;
          
          .label {
            color: #909399;
            width: 70px;
          }
          
          .value {
            color: #333;
            flex: 1;
          }
        }
      }
    }
  }

  .search-engine-section {
    .sync-section {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px 0;

      .sync-info {
        .sync-title {
          font-weight: 600;
          font-size: 16px;
          color: #333;
          margin-bottom: 8px;
        }
        .sync-desc {
          font-size: 14px;
          color: #909399;
          max-width: 600px;
        }
      }

      .sync-btn {
        min-width: 160px;
        height: 44px;
        font-size: 15px;
      }
    }
  }
}
</style>
