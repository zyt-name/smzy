<template>
  <el-card shadow="never" class="full-height-card">
    <template #header>
      <div class="card-header">
        <span>新增{{ activeType === 1 ? '商品分类' : '商品标签' }}</span>
      </div>
    </template>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      class="label-form"
    >
      <!-- 类型选择 -->
      <el-form-item label="类型" prop="tagType">
        <el-radio-group v-model="form.tagType" @change="handleTypeChange">
          <el-radio :label="1">商品分类</el-radio>
          <el-radio :label="2">商品标签</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 标签名称 -->
      <el-form-item label="名称" prop="tagName">
        <el-input v-model="form.tagName" placeholder="请输入名称" maxlength="50" show-word-limit />
      </el-form-item>

      <!-- 商品标签特有：标签等级 -->
      <el-form-item v-if="form.tagType === 2" label="标签等级" prop="level">
        <el-select 
          v-model="form.level" 
          placeholder="请选择标签等级" 
          style="width: 100%"
          @change="handleLevelChange"
        >
          <el-option label="1级" :value="1" />
          <el-option label="2级" :value="2" />
          <el-option label="3级" :value="3" />
        </el-select>
      </el-form-item>

      <!-- 商品标签特有：上级标签（2级和3级显示） -->
      <el-form-item 
        v-if="form.tagType === 2 && form.level && form.level > 1" 
        label="上级标签" 
        prop="parentTagId"
      >
        <el-select 
          v-model="form.parentTagId" 
          placeholder="请选择上级标签" 
          style="width: 100%"
          :disabled="!form.level || parentTagOptions.length === 0"
        >
          <el-option 
            v-for="item in parentTagOptions" 
            :key="item.id" 
            :label="item.tagName" 
            :value="item.id" 
          />
        </el-select>
        <div v-if="form.level && parentTagOptions.length === 0" class="tip-text">
          暂无{{ form.level - 1 }}级标签，请先创建
        </div>
      </el-form-item>

      <!-- 备注 -->
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注信息"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>

      <el-form-item>
        <ActionButton type="primary" size="medium" @click="handleSubmit">立即创建</ActionButton>
        <ActionButton type="default" size="medium" @click="handleReset">重置</ActionButton>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addLabel, getTagList } from '../../../../api/admin/AdminLabel'
import ActionButton from '../../../../components/ActionButton.vue'

const emit = defineEmits(['success'])

const formRef = ref(null)
const submitting = ref(false)
const parentTagOptions = ref([])
const activeType = ref(1)

const form = reactive({
  tagName: '',
  tagType: 1,
  parentTagId: null,
  level: null,
  remark: ''
})

const rules = {
  tagName: [
    { required: true, message: '请输入名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  tagType: [
    { required: true, message: '请选择类型', trigger: 'change' }
  ],
  level: [
    { required: true, message: '请选择标签等级', trigger: 'change', validator: (rule, value, callback) => {
      if (form.tagType === 2 && !value) {
        callback(new Error('请选择标签等级'))
      } else {
        callback()
      }
    }}
  ]
}

// 类型切换
const handleTypeChange = (val) => {
  activeType.value = val
  // 重置相关字段
  if (val === 1) {
    // 商品分类：清空等级和上级标签
    form.level = null
    form.parentTagId = null
    parentTagOptions.value = []
  } else {
    // 商品标签：保持等级选择，重新加载上级标签
    if (form.level && form.level > 1) {
      loadParentTags()
    }
  }
}

// 等级切换
const handleLevelChange = (val) => {
  form.parentTagId = null
  if (val && val > 1) {
    loadParentTags()
  } else {
    parentTagOptions.value = []
  }
}

// 加载上级标签
const loadParentTags = async () => {
  if (!form.level || form.level <= 1) {
    parentTagOptions.value = []
    return
  }
  
  try {
    // 查询上一级标签（如选择2级，查询1级标签）
    const parentLevel = form.level - 1
    const res = await getTagList({ level: parentLevel })
    if (res.code === 200) {
      parentTagOptions.value = res.data || []
    }
  } catch (error) {
    console.error('加载上级标签失败:', error)
    parentTagOptions.value = []
  }
}

// 重置表单
const handleReset = () => {
  form.tagName = ''
  form.tagType = 1
  form.parentTagId = null
  form.level = null
  form.remark = ''
  parentTagOptions.value = []
  activeType.value = 1
  formRef.value?.resetFields()
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // 构建提交数据
    const data = {
      tagName: form.tagName,
      tagType: form.tagType,
      remark: form.remark || null
    }
    
    // 商品标签特有字段
    if (form.tagType === 2) {
      data.level = form.level
      // 1级标签没有上级标签
      data.parentTagId = form.level > 1 ? form.parentTagId : null
    } else {
      // 商品分类：这些字段为null
      data.level = null
      data.parentTagId = null
    }

    await addLabel(data)
    ElMessage.success('创建成功')
    emit('success')
    handleReset()
  } catch (error) {
    console.error('创建失败:', error)
    ElMessage.error('创建失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.full-height-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-radius: 12px;
  border: none;

  :deep(.el-card__body) {
    flex: 1;
    padding: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .label-form {
    .el-form-item {
      margin-bottom: 20px;
    }
  }

  .tip-text {
    font-size: 12px;
    color: #909399;
    margin-top: 5px;
  }
}
</style>
