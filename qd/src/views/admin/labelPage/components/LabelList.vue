<template>
  <el-card shadow="never" class="full-height-card">
    <template #header>
      <div class="card-header">
        <span>标签列表</span>
        <div class="header-actions">
          <el-input
            v-model="queryParams.tagName"
            placeholder="搜索标签名称"
            prefix-icon="Search"
            clearable
            style="width: 150px; margin-right: 10px;"
            @keyup.enter="handleQuery"
          />
          <el-select
            v-model="queryParams.status"
            placeholder="状态"
            clearable
            style="width: 100px; margin-right: 10px;"
            @change="handleQuery"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
          <el-date-picker
            v-model="queryParams.createdTimeMonth"
            type="month"
            placeholder="创建月份"
            clearable
            style="width: 140px; margin-right: 10px;"
            value-format="YYYY-MM-DD"
            @change="handleQuery"
          />
          <el-select
            v-if="activeTab === 'tag'"
            v-model="queryParams.parentTagId"
            placeholder="前置标签"
            clearable
            style="width: 130px; margin-right: 10px;"
            @change="handleQuery"
          >
            <el-option
              v-for="item in categoryOptions"
              :key="item.id"
              :label="item.tagName"
              :value="item.id"
            />
          </el-select>
          <el-select
            v-if="activeTab === 'tag'"
            v-model="queryParams.level"
            placeholder="等级"
            style="width: 100px;"
            @change="handleQuery"
          >
            <el-option label="全部" :value="null" />
            <el-option label="1级" :value="1" />
            <el-option label="2级" :value="2" />
            <el-option label="3级" :value="3" />
          </el-select>
          <ActionButton type="primary" size="medium" @click="handleQuery" style="margin-left: 10px;">查询</ActionButton>
          <ActionButton type="default" size="medium" @click="handleReset">重置</ActionButton>
        </div>
      </div>
    </template>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="商品分类" name="category">
        <el-table
          :data="categoryList"
          row-key="id"
          style="width: 100%"
          v-loading="loading"
        >
          <el-table-column prop="tagName" label="分类名称" min-width="180">
            <template #default="scope">
              <el-tag type="primary" size="small" class="m-r-5">分类</el-tag>
              {{ scope.row.tagName }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80" align="center">
            <template #default="scope">
              <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="0"
                @change="(val) => handleStatusChange(scope.row, val)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
          <el-table-column prop="createdBy" label="创建人" width="100" />
          <el-table-column prop="createdTime" label="创建时间" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.createdTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <ActionButton type="primary" size="small" @click="handleEdit(scope.row)">编辑</ActionButton>
              <ActionButton type="danger" size="small" @click="handleDelete(scope.row)">删除</ActionButton>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="商品标签" name="tag">
        <el-table
          :data="tagList"
          row-key="id"
          lazy
          :load="loadTagChildren"
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
          style="width: 100%"
          v-loading="loading"
        >
          <el-table-column prop="tagName" label="标签名称" min-width="180">
            <template #default="scope">
              <el-tag type="success" size="small" class="m-r-5">标签</el-tag>
              {{ scope.row.tagName }}
            </template>
          </el-table-column>
          <el-table-column prop="level" label="等级" width="80" align="center">
            <template #default="scope">
              <el-tag v-if="scope.row.level" size="small" type="warning">{{ scope.row.level }}级</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80" align="center">
            <template #default="scope">
              <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="0"
                @change="(val) => handleStatusChange(scope.row, val)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
          <el-table-column prop="createdBy" label="创建人" width="100" />
          <el-table-column prop="createdTime" label="创建时间" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.createdTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <ActionButton type="primary" size="small" @click="handleEdit(scope.row)">编辑</ActionButton>
              <ActionButton type="danger" size="small" @click="handleDelete(scope.row)">删除</ActionButton>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="`编辑${editForm.tagType === 1 ? '商品分类' : '商品标签'}`"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="名称" prop="tagName">
          <el-input v-model="editForm.tagName" placeholder="请输入名称" maxlength="50" show-word-limit />
        </el-form-item>
        
        <el-form-item v-if="editForm.tagType === 2" label="标签等级">
          <el-select v-model="editForm.level" placeholder="请选择标签等级" style="width: 100%" disabled>
            <el-option label="1级" :value="1" />
            <el-option label="2级" :value="2" />
            <el-option label="3级" :value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="editForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="editForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <ActionButton type="default" size="medium" @click="editDialogVisible = false">取消</ActionButton>
        <ActionButton type="primary" size="medium" @click="handleEditSubmit">确定</ActionButton>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteLabel, getCategoryList, getTagLazyList, updateLabel } from '../../../../api/admin/AdminLabel'
import ActionButton from '../../../../components/ActionButton.vue'

const loading = ref(false)
const activeTab = ref('category')
const categoryList = ref([])
const tagList = ref([])
const categoryOptions = ref([])
const total = ref(0)

// 编辑对话框
const editDialogVisible = ref(false)
const editFormRef = ref(null)
const editForm = reactive({
  id: null,
  tagName: '',
  tagType: 1,
  status: 1,
  level: null,
  remark: ''
})

const editRules = {
  tagName: [
    { required: true, message: '请输入名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

const queryParams = reactive({
  tagName: '',
  status: null,
  createdTimeMonth: null,
  parentTagId: null,
  level: 1
})

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.split('T')[0]
}

// 加载分类列表
const loadCategoryList = async () => {
  loading.value = true
  try {
    const params = {
      tagName: queryParams.tagName || null,
      status: queryParams.status,
      createdTimeMonth: queryParams.createdTimeMonth
    }
    const res = await getCategoryList(params)
    if (res.code === 200) {
      categoryList.value = res.data || []
      total.value = categoryList.value.length
      // 更新分类选项供标签查询使用
      categoryOptions.value = categoryList.value.map(item => ({
        id: item.id,
        tagName: item.tagName
      }))
    }
  } catch (error) {
    console.error('加载分类列表失败:', error)
    ElMessage.error('加载分类列表失败')
  } finally {
    loading.value = false
  }
}

// 加载标签列表（只加载一级标签）
const loadTagList = async () => {
  loading.value = true
  try {
    const params = {
      level: queryParams.level,
      tagName: queryParams.tagName || null,
      status: queryParams.status,
      createdTimeMonth: queryParams.createdTimeMonth,
      parentTagId: queryParams.parentTagId
    }
    const res = await getTagLazyList(params)
    if (res.code === 200) {
      tagList.value = res.data || []
      total.value = tagList.value.length
    }
  } catch (error) {
    console.error('加载标签列表失败:', error)
    ElMessage.error('加载标签列表失败')
  } finally {
    loading.value = false
  }
}

// 懒加载子标签
const loadTagChildren = async (row, treeNode, resolve) => {
  try {
    const nextLevel = row.level + 1
    const params = {
      parentTagId: row.id,
      level: nextLevel
    }
    const res = await getTagLazyList(params)
    if (res.code === 200) {
      const children = res.data || []
      resolve(children)
    } else {
      resolve([])
    }
  } catch (error) {
    console.error('加载子标签失败:', error)
    ElMessage.error('加载子标签失败')
    resolve([])
  }
}

// 查询
const handleQuery = () => {
  if (activeTab.value === 'category') {
    loadCategoryList()
  } else {
    loadTagList()
  }
}

// 重置
const handleReset = () => {
  queryParams.tagName = ''
  queryParams.status = null
  queryParams.createdTimeMonth = null
  queryParams.parentTagId = null
  queryParams.level = 1
  handleQuery()
}

// Tab 切换
const handleTabChange = () => {
  handleQuery()
}

const emit = defineEmits(['refresh-stats'])

// 暴露方法给父组件
defineExpose({
  loadCategoryList,
  loadTagList
})

// 编辑标签 - 打开对话框
const handleEdit = (row) => {
  editForm.id = row.id
  editForm.tagName = row.tagName
  editForm.tagType = row.tagType
  editForm.status = row.status
  editForm.level = row.level
  editForm.remark = row.remark || ''
  editDialogVisible.value = true
}

// 提交编辑
const handleEditSubmit = async () => {
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    const data = {
      id: editForm.id,
      tagName: editForm.tagName,
      status: editForm.status,
      remark: editForm.remark || null
    }
    
    await updateLabel(data)
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    handleQuery()
    emit('refresh-stats')
  } catch (error) {
    console.error('修改失败:', error)
    ElMessage.error('修改失败')
  }
}

// 删除标签
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定删除 "${row.tagName}" 吗？删除后将同时删除其子标签！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteLabel(row.id)
      ElMessage.success('删除成功')
      handleQuery()
      emit('refresh-stats')
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 状态变更
const handleStatusChange = async (row, val) => {
  try {
    const data = {
      id: row.id,
      status: val
    }
    await updateLabel(data)
    ElMessage.success(`"${row.tagName}" ${val === 1 ? '已启用' : '已禁用'}`)
    emit('refresh-stats')
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败')
    // 恢复原状态
    row.status = val === 1 ? 0 : 1
  }
}

onMounted(() => {
  loadCategoryList()
  loadTagList()
})
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
    display: flex;
    flex-direction: column;
    padding: 15px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-actions {
      display: flex;
      align-items: center;
    }
  }
}
</style>
