<template>
  <div class="add-product-container">
    <div class="dialog-header">
      <span class="dialog-title">添加商品</span>
      <ActionButton type="danger" size="xlarge" class="close-btn" @click="handleClose">×</ActionButton>
    </div>
    <div class="dialog-body">
      <el-form :model="productForm" label-position="top" class="add-product-form">
        <el-form-item label="商品名称">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>

        <div class="form-row-inline">
          <el-form-item label="价格">
            <el-input-number v-model="productForm.price" :precision="2" :step="0.1" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="库存">
            <el-input-number v-model="productForm.stock" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="商品分类">
            <el-select v-model="productForm.category" placeholder="请选择分类" style="width: 100%">
              <el-option v-for="item in categoryOptions" :key="item.id" :label="item.tagName" :value="item.tagName" />
            </el-select>
          </el-form-item>
        </div>

        <el-form-item label="商品描述">
          <el-input v-model="productForm.description" type="textarea" placeholder="请输入商品描述" :rows="2" />
        </el-form-item>

        <el-form-item>
          <template #label>
            <div class="label-header">
              <span>商品规格</span>
              <ActionButton type="primary" size="small" @click.prevent="openSpecFormatDialog">
                规格格式参考
              </ActionButton>
            </div>
          </template>
          <el-input v-model="productForm.specification" placeholder="请输入商品规格" />
        </el-form-item>

        <el-form-item>
          <template #label>
            <div class="label-header">
              <span>商品标签</span>
              <span class="label-tip">选择相对应的商品标签可以提高商品曝光度哦！</span>
            </div>
          </template>
          <div class="label-selector">
            <div class="label-section">
              <span class="section-title">一级标签：</span>
              <div class="tag-list">
                <span
                  v-for="item in level1Options"
                  :key="item.id"
                  :class="['tag-option', { active: selectedLevel1 === item.id }]"
                  @click="handleLevel1Click(item)"
                >
                  {{ item.tagName }}
                </span>
              </div>
            </div>
            <div class="label-section">
              <span class="section-title">二级标签：</span>
              <div class="tag-list">
                <span
                  v-for="item in level2Options"
                  :key="item.id"
                  :class="['tag-option', { active: selectedLevel2 === item.id, disabled: !selectedLevel1 }]"
                  @click="handleLevel2Click(item)"
                >
                  {{ item.tagName }}
                </span>
              </div>
            </div>
            <div class="label-section">
              <span class="section-title">三级标签：</span>
              <div class="tag-list">
                <span
                  v-for="item in level3Options"
                  :key="item.id"
                  :class="['tag-option', { active: selectedLevel3 === item.id, disabled: !selectedLevel2 }]"
                  @click="handleLevel3Click(item)"
                >
                  {{ item.tagName }}
                </span>
              </div>
            </div>
            <div class="selected-tags" v-if="selectedTags.length > 0">
              <span class="section-title">已选标签：</span>
              <el-tag
                v-for="(tag, index) in selectedTags"
                :key="index"
                closable
                @close="handleRemoveTag(index)"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="商品图片">
          <el-upload
            class="product-uploader sidebar-uploader"
            action="#"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleImageChange"
          >
            <ProductImage v-if="productForm.imageUrls" :src="productForm.imageUrls" class="uploaded-image" />
            <div v-else class="uploader-placeholder">
              <el-icon class="uploader-icon"><Plus /></el-icon>
              <span>上传图片</span>
            </div>
          </el-upload>
        </el-form-item>

        <div class="form-actions">
          <ActionButton type="success" size="large" @click="handleSubmit">立即添加</ActionButton>
          <ActionButton type="default" size="large" @click="resetForm">全部重置</ActionButton>
        </div>
      </el-form>
    </div>
    
    <SpecFormatDialog ref="specFormatDialogRef" />
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import ProductImage from '../../../components/ProductImage.vue'
import ActionButton from '../../../components/ActionButton.vue'
import SpecFormatDialog from './gggssl.vue'
import { Plus } from '@element-plus/icons-vue'
import { addShop } from '../../../api/merchant/MerchantShop'
import { uploadImage } from '../../../api/merchant/MerchantImg'
import { getTagNameList } from '../../../api/merchant/MerchantLable'

const emit = defineEmits(['product-added', 'close'])

const specFormatDialogRef = ref(null)

const handleClose = () => {
  emit('close')
}

const openSpecFormatDialog = () => {
  specFormatDialogRef.value?.open()
}

const categoryOptions = ref([])
const level1Options = ref([])
const level2Options = ref([])
const level3Options = ref([])

const selectedLevel1 = ref(null)
const selectedLevel2 = ref(null)
const selectedLevel3 = ref(null)
const selectedTags = ref([])
const selectedTagNames = ref([])

const fetchCategoryOptions = async () => {
  try {
    const res = await getTagNameList({ tagType: 1 })
    categoryOptions.value = res?.data || []
  } catch (error) {
    console.error('获取商品分类失败', error)
  }
}

const fetchLevel1Options = async () => {
  try {
    const res = await getTagNameList({ tagType: 2, level: 1 })
    level1Options.value = res?.data || []
  } catch (error) {
    console.error('获取一级标签失败', error)
  }
}

const fetchLevel2Options = async (parentTagId) => {
  try {
    const res = await getTagNameList({ tagType: 2, level: 2, parentTagId })
    level2Options.value = res?.data || []
  } catch (error) {
    console.error('获取二级标签失败', error)
  }
}

const fetchLevel3Options = async (parentTagId) => {
  try {
    const res = await getTagNameList({ tagType: 2, level: 3, parentTagId })
    level3Options.value = res?.data || []
  } catch (error) {
    console.error('获取三级标签失败', error)
  }
}

const handleLevel1Click = async (item) => {
  if (selectedLevel1.value === item.id) {
    selectedLevel1.value = null
    selectedLevel2.value = null
    selectedLevel3.value = null
    level2Options.value = []
    level3Options.value = []
  } else {
    selectedLevel1.value = item.id
    selectedLevel2.value = null
    selectedLevel3.value = null
    level3Options.value = []
    await fetchLevel2Options(item.id)
  }
  updateSelectedTags()
}

const handleLevel2Click = async (item) => {
  if (!selectedLevel1.value) return
  if (selectedLevel2.value === item.id) {
    selectedLevel2.value = null
    selectedLevel3.value = null
    level3Options.value = []
  } else {
    selectedLevel2.value = item.id
    selectedLevel3.value = null
    await fetchLevel3Options(item.id)
  }
  updateSelectedTags()
}

const handleLevel3Click = (item) => {
  if (!selectedLevel2.value) return
  if (selectedLevel3.value === item.id) {
    selectedLevel3.value = null
  } else {
    selectedLevel3.value = item.id
  }
  updateSelectedTags()
}

const updateSelectedTags = () => {
  selectedTags.value = []
  selectedTagNames.value = []
  
  if (selectedLevel1.value) {
    const level1Item = level1Options.value.find(item => item.id === selectedLevel1.value)
    if (level1Item) {
      selectedTags.value.push(level1Item.tagName)
      selectedTagNames.value.push(level1Item.tagName)
    }
  }
  if (selectedLevel2.value) {
    const level2Item = level2Options.value.find(item => item.id === selectedLevel2.value)
    if (level2Item) {
      selectedTags.value.push(level2Item.tagName)
      selectedTagNames.value.push(level2Item.tagName)
    }
  }
  if (selectedLevel3.value) {
    const level3Item = level3Options.value.find(item => item.id === selectedLevel3.value)
    if (level3Item) {
      selectedTags.value.push(level3Item.tagName)
      selectedTagNames.value.push(level3Item.tagName)
    }
  }
  productForm.label = selectedTagNames.value.length > 0 ? JSON.stringify(selectedTagNames.value) : ''
}

const handleRemoveTag = (index) => {
  if (index === 2) {
    selectedLevel3.value = null
  } else if (index === 1) {
    selectedLevel2.value = null
    selectedLevel3.value = null
    level3Options.value = []
  } else if (index === 0) {
    selectedLevel1.value = null
    selectedLevel2.value = null
    selectedLevel3.value = null
    level2Options.value = []
    level3Options.value = []
  }
  updateSelectedTags()
}

const productForm = reactive({
  name: '',
  price: 0,
  stock: 0,
  category: '',
  description: '',
  specification: '',
  label: '',
  imageUrls: '',
  status: 'onsale'
})

const handleSubmit = async () => {
  if (!productForm.name) {
    ElMessage.warning('请输入商品名称')
    return
  }
  if (selectedTags.value.length === 0) {
    ElMessage.warning('请至少选择一个商品标签')
    return
  }
  try {
    const data = {
      name: productForm.name,
      price: productForm.price,
      stock: productForm.stock,
      category: productForm.category,
      description: productForm.description,
      specification: productForm.specification,
      label: productForm.label,
      imageUrls: productForm.imageUrls || ''
    }
    await addShop(data)
    ElMessage.success('商品添加成功')
    emit('product-added', { ...productForm })
    resetForm()
  } catch (error) {
    ElMessage.error('商品添加失败')
  }
}

const resetForm = () => {
  Object.assign(productForm, {
    name: '',
    price: 0,
    stock: 0,
    category: '',
    description: '',
    specification: '',
    label: '',
    imageUrls: '',
    status: 'onsale'
  })
  selectedLevel1.value = null
  selectedLevel2.value = null
  selectedLevel3.value = null
  selectedTags.value = []
  selectedTagNames.value = []
  level2Options.value = []
  level3Options.value = []
}

const handleImageChange = async (file) => {
  try {
    const formData = new FormData()
    formData.append('file', file.raw)
    const res = await uploadImage(formData)
    if (res) {
      productForm.imageUrls = res
      ElMessage.success('图片上传成功')
    }
  } catch (error) {
    ElMessage.error('图片上传失败')
  }
}

onMounted(() => {
  fetchCategoryOptions()
  fetchLevel1Options()
})
</script>

<style lang="scss" scoped>
.add-product-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.dialog-header {
  padding: 20px 24px;
  text-align: center;
  border-bottom: 1px solid #e4e7ed;
  position: relative;

  .dialog-title {
    font-size: 20px;
    font-weight: 700;
    color: #1a3a5c;
  }

  .close-btn {
    position: absolute;
    top: 12px;
    right: 12px;
    font-size: 35px;
    line-height: 1;
    border-radius: 50%;
    width: 44px;
    height: 44px;
    padding: 0;
  }
}

.dialog-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
}

.add-product-form {
  .form-row-inline {
    display: flex;
    gap: 16px;
    margin-bottom: 0;

    > .el-form-item { flex: 1; }
  }

  :deep(.el-form-item__label) {
    font-weight: 600;
    padding-bottom: 4px;
  }

  :deep(.el-form-item) {
    margin-bottom: 20px;
  }

  .form-actions {
    display: flex;
    justify-content: center;
    gap: 12px;
    margin-top: 24px;
    padding-top: 20px;
    border-top: 1px solid #f0f2f5;
  }
}

.label-header {
  display: flex;
  align-items: center;
  gap: 8px;

  .label-tip {
    color: #f56c6c;
    font-size: 12px;
    font-weight: 400;
  }
}

.label-selector {
  background: #f5f7fa;
  border-radius: 12px;
  padding: 16px;

  .label-section {
    margin-bottom: 12px;
    .section-title {
      font-size: 13px;
      font-weight: 600;
      color: #606266;
      margin-bottom: 8px;
      display: block;
    }
    .tag-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      .tag-option {
        padding: 6px 14px;
        border-radius: 16px;
        font-size: 13px;
        background: transparent;
        color: #409eff;
        cursor: pointer;
        transition: all 0.2s;
        border: 1px solid #409eff;
        &:hover {
          background: rgba(64, 158, 255, 0.1);
        }
        &.active {
          background: rgba(64, 158, 255, 0.1);
          color: #409eff;
          border-color: #409eff;
        }
        &.disabled {
          opacity: 0.4;
          cursor: not-allowed;
          &:hover {
            background: transparent;
          }
        }
      }
    }
  }

  .selected-tags {
    min-height: 32px;
    margin-bottom: 10px;
    .section-title {
      font-size: 13px;
      font-weight: 600;
      color: #606266;
      margin-right: 8px;
    }
    .tag-item {
      margin-right: 8px;
      margin-bottom: 8px;
    }
  }
}

.product-uploader {
  :deep(.el-upload) {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 100px;
    height: 100px;
    display: flex;
    justify-content: center;
    align-items: center;

    &:hover {
      border-color: #409eff;
    }
  }

  .uploaded-image {
    width: 100px;
    height: 100px;
    object-fit: cover;
  }

  .uploader-icon {
    font-size: 28px;
    color: #8c939d;
  }

  .sidebar-uploader {
    :deep(.el-upload) {
      width: 100%;
      height: 120px;
      background-color: #f8f9fb;
    }

    .uploader-placeholder {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      color: #909399;
      font-size: 13px;
    }

    .uploaded-image {
      width: 100%;
      height: 120px;
    }
  }
}
</style>
