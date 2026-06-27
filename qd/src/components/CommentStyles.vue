<template>
  <!-- 评论组件 -->
  <div class="comments-section">
    <!-- 发表评论 -->
    <div class="comment-input-area" v-if="userInfo">
      <div class="comment-user-info">
        <span class="username">{{ userInfo.username || userInfo.userName }}</span>
      </div>
      <el-input
        v-model="newCommentContent"
        type="textarea"
        :rows="3"
        placeholder="分享您的使用体验..."
        maxlength="500"
        show-word-limit
      />
      <div class="comment-actions">
        <el-button type="primary" @click="submitComment" :loading="commentSubmitting">
          发表评论
        </el-button>
      </div>
    </div>
    <div class="comment-login-tip" v-else>
      <el-alert
        title="请登录后发表评论"
        type="info"
        :closable="false"
      >
        <template #default>
          <el-button type="primary" link @click="handleLogin">立即登录</el-button>
        </template>
      </el-alert>
    </div>

    <!-- 评论列表 -->
    <div class="comments-list" v-loading="commentsLoading">
      <div class="comments-header">
        <span class="total-count">共 {{ commentTotal }} 条评价</span>
      </div>
      
      <div v-if="commentsList.length === 0 && !commentsLoading" class="no-comments">
        <el-empty description="暂无评价，快来抢沙发吧~" />
      </div>

      <div class="comment-item" v-for="comment in commentsList" :key="comment.id">
        <!-- 主评论 -->
        <div class="main-comment">
          <div class="comment-body">
            <div class="username">{{ comment.username }}</div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-meta">
              <span class="time">{{ formatTime(comment.createTime) }}</span>
              <div class="comment-actions-bar">
                <span class="action-btn" @click="handleLikeComment(comment.id)">
                  <el-icon><Pointer /></el-icon>
                  {{ comment.likeCount || 0 }}
                </span>
                <span class="action-btn" @click="showReplyInput(comment.id)">
                  <el-icon><ChatDotRound /></el-icon>
                  回复
                </span>
                <span
                  v-if="canDeleteComment(comment.userId)"
                  class="action-btn delete"
                  @click="handleDeleteComment(comment.id)"
                >
                  删除
                </span>
                <span
                  v-if="isMerchant && !isOwnComment(comment.userId)"
                  class="action-btn appeal"
                  @click="openAppealDialog(comment.id, 'comment')"
                >
                  申诉
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 回复输入框 -->
        <div class="reply-input-wrapper" v-if="activeReplyCommentId === comment.id">
          <el-input
            v-model="replyContent"
            type="textarea"
            :rows="2"
            :placeholder="replyPlaceholder"
            maxlength="300"
            show-word-limit
          />
          <div class="reply-actions">
            <el-button size="small" @click="cancelReply">取消</el-button>
            <el-button type="primary" size="small" @click="submitReply(comment)" :loading="replySubmitting">
              发送
            </el-button>
          </div>
        </div>

        <!-- 子评论列表 -->
        <div class="replies-container" v-if="comment.replyList && comment.replyList.length > 0">
          <div class="replies-box">
            <div class="reply-item" v-for="reply in comment.replyList" :key="reply.replyId">
              <div class="reply-username">{{ reply.username }}</div>
              <div class="reply-content">
                <span v-if="reply.toUsername" class="reply-to">
                  回复 <span class="to-username">@{{ reply.toUsername }}</span>：
                </span>
                {{ reply.content }}
              </div>
              <div class="reply-meta">
                <span class="time">{{ formatTime(reply.createTime) }}</span>
                <div class="reply-actions-bar">
                  <span class="action-btn" @click="handleLikeReply(comment.id, reply.replyId)">
                    <el-icon><Pointer /></el-icon>
                    {{ reply.likeCount || 0 }}
                  </span>
                  <span class="action-btn" @click="showReplyInput(comment.id, reply)">
                    回复
                  </span>
                  <span
                    v-if="canDeleteComment(reply.userId)"
                    class="action-btn delete"
                    @click="handleDeleteReply(comment.id, reply.replyId)"
                  >
                    删除
                  </span>
                  <span
                    v-if="isMerchant && !isOwnComment(reply.userId)"
                    class="action-btn appeal"
                    @click="openAppealDialog(reply.replyId, 'reply')"
                  >
                    申诉
                  </span>
                </div>
              </div>
            </div>
          </div>
          <!-- 子评论展开/收起 -->
          <div class="reply-expand" v-if="comment.replyTotal > comment.replyList.length">
            <span class="expand-btn" @click="loadMoreReplies(comment.id)">
              查看全部 {{ comment.replyTotal }} 条回复 <el-icon><ArrowRight /></el-icon>
            </span>
          </div>
        </div>
      </div>

      <!-- 主评论分页 -->
      <div class="main-pagination" v-if="commentTotal > 0">
        <el-pagination
          v-model:current-page="commentPage"
          v-model:page-size="commentSize"
          :total="commentTotal"
          :page-sizes="[10, 20, 30]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleCommentSizeChange"
          @current-change="handleCommentPageChange"
        />
      </div>
    </div>

    <!-- 申诉悬浮窗 -->
    <el-dialog
      v-model="appealDialogVisible"
      title="申诉删除评论"
      width="500px"
      destroy-on-close
    >
      <el-form :model="appealForm" label-width="100px">
        <el-form-item label="联系电话" required>
          <el-input v-model="appealForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="申诉理由" required>
          <el-input
            v-model="appealForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入申诉理由"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="appealDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAppeal" :loading="appealSubmitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Pointer, ChatDotRound, ArrowRight } from '@element-plus/icons-vue'
import { 
  addComment, 
  getComments, 
  addReply, 
  deleteComment, 
  deleteReply, 
  likeComment, 
  likeReply 
} from '../api/user/UsetComment'
import { applyDeleteComment } from '../api/merchant/MerchantApplication'

const props = defineProps({
  productId: {
    type: [String, Number],
    required: true
  },
  userInfo: {
    type: Object,
    default: null
  },
  isAdmin: {
    type: Boolean,
    default: false
  },
  isMerchant: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['login'])

// 评论相关数据
const commentsList = ref([])
const commentTotal = ref(0)
const commentPage = ref(1)
const commentSize = ref(10)
const commentsLoading = ref(false)
const newCommentContent = ref('')
const commentSubmitting = ref(false)

// 回复相关数据
const activeReplyCommentId = ref(null)
const replyContent = ref('')
const replySubmitting = ref(false)
const replyToUser = ref(null)
const replyPlaceholder = computed(() => {
  if (replyToUser.value) {
    return `回复 @${replyToUser.value.username}:`
  }
  return '写下你的回复...'
})

const currentUserId = computed(() => {
  return props.userInfo?.id || null
})

// 判断是否可以删除评论（管理员可以删除所有，普通用户只能删除自己的）
const canDeleteComment = (commentUserId) => {
  if (props.isAdmin) return true
  return commentUserId === currentUserId.value
}

// 判断是否是自己的评论
const isOwnComment = (commentUserId) => {
  return commentUserId === currentUserId.value
}

// 申诉相关数据
const appealDialogVisible = ref(false)
const appealSubmitting = ref(false)
const appealForm = ref({
  commentId: null,
  phone: '',
  reason: ''
})

// 打开申诉悬浮窗
const openAppealDialog = (commentId) => {
  appealForm.value = {
    commentId: commentId,
    phone: '',
    reason: ''
  }
  appealDialogVisible.value = true
}

// 提交申诉
const submitAppeal = async () => {
  if (!appealForm.value.reason.trim()) {
    ElMessage.warning('请输入申诉理由')
    return
  }
  if (!appealForm.value.phone.trim()) {
    ElMessage.warning('请输入联系电话')
    return
  }
  
  appealSubmitting.value = true
  try {
    const res = await applyDeleteComment({
      commentId: appealForm.value.commentId.toString(),
      reason: appealForm.value.reason,
      phone: appealForm.value.phone
    })
    if (res.code === 200) {
      ElMessage.success('申诉已提交')
      appealDialogVisible.value = false
    } else {
      ElMessage.error(res.msg || '申诉失败')
    }
  } catch (error) {
    console.error('申诉失败:', error)
    ElMessage.error('申诉失败')
  } finally {
    appealSubmitting.value = false
  }
}

// 获取评论列表
const fetchComments = async () => {
  if (!props.productId) return
  
  commentsLoading.value = true
  try {
    const res = await getComments(
      props.productId.toString(),
      commentPage.value,
      commentSize.value,
      1,
      10
    )
    if (res.code === 200) {
      commentsList.value = res.data?.list || []
      commentTotal.value = res.data?.total || 0
    }
  } catch (error) {
    console.error('获取评论失败:', error)
  } finally {
    commentsLoading.value = false
  }
}

// 暴露方法供父组件调用
const refreshComments = () => {
  commentPage.value = 1
  fetchComments()
}

defineExpose({
  fetchComments,
  refreshComments
})

// 发表主评论
const submitComment = async () => {
  if (!newCommentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  if (!props.productId) {
    ElMessage.warning('商品信息加载中')
    return
  }
  
  commentSubmitting.value = true
  try {
    const res = await addComment({
      productId: props.productId.toString(),
      content: newCommentContent.value.trim()
    })
    if (res.code === 200) {
      ElMessage.success('评论发表成功')
      newCommentContent.value = ''
      // 刷新评论列表
      commentPage.value = 1
      fetchComments()
    } else {
      ElMessage.error(res.msg || '评论发表失败')
    }
  } catch (error) {
    console.error('发表评论失败:', error)
    ElMessage.error('评论发表失败')
  } finally {
    commentSubmitting.value = false
  }
}

// 主评论分页变化
const handleCommentPageChange = (page) => {
  commentPage.value = page
  fetchComments()
}

// 主评论每页条数变化
const handleCommentSizeChange = (size) => {
  commentSize.value = size
  commentPage.value = 1
  fetchComments()
}

// 显示回复输入框
const showReplyInput = (commentId, reply = null) => {
  if (activeReplyCommentId.value === commentId) {
    // 如果点击的是同一个评论，切换关闭
    if (replyToUser.value?.replyId === reply?.replyId) {
      activeReplyCommentId.value = null
      replyToUser.value = null
      return
    }
  }
  activeReplyCommentId.value = commentId
  replyToUser.value = reply
  replyContent.value = ''
}

// 取消回复
const cancelReply = () => {
  activeReplyCommentId.value = null
  replyContent.value = ''
  replyToUser.value = null
}

// 发表回复
const submitReply = async (comment) => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  replySubmitting.value = true
  try {
    const data = {
      commentId: comment.id,
      content: replyContent.value.trim()
    }
    
    // 如果是回复某个人的子评论
    if (replyToUser.value) {
      data.toUserId = replyToUser.value.userId
      data.toUsername = replyToUser.value.username
    }
    
    const res = await addReply(data)
    if (res.code === 200) {
      ElMessage.success('回复发表成功')
      cancelReply()
      // 刷新评论列表
      fetchComments()
    } else {
      ElMessage.error(res.msg || '回复发表失败')
    }
  } catch (error) {
    console.error('发表回复失败:', error)
    ElMessage.error('回复发表失败')
  } finally {
    replySubmitting.value = false
  }
}

// 删除主评论
const handleDeleteComment = async (commentId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteComment(commentId)
    if (res.code === 200) {
      ElMessage.success('评论删除成功')
      fetchComments()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 删除子评论
const handleDeleteReply = async (commentId, replyId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条回复吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteReply(commentId, replyId)
    if (res.code === 200) {
      ElMessage.success('回复删除成功')
      fetchComments()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除回复失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 点赞主评论
const handleLikeComment = async (commentId) => {
  try {
    const res = await likeComment(commentId)
    if (res.code === 200) {
      ElMessage.success('点赞成功')
      // 更新本地数据
      const comment = commentsList.value.find(c => c.id === commentId)
      if (comment) {
        comment.likeCount = (comment.likeCount || 0) + 1
      }
    } else {
      ElMessage.error(res.msg || '点赞失败')
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败')
  }
}

// 点赞子评论
const handleLikeReply = async (commentId, replyId) => {
  try {
    const res = await likeReply(commentId, replyId)
    if (res.code === 200) {
      ElMessage.success('点赞成功')
      // 更新本地数据
      const comment = commentsList.value.find(c => c.id === commentId)
      if (comment && comment.replyList) {
        const reply = comment.replyList.find(r => r.replyId === replyId)
        if (reply) {
          reply.likeCount = (reply.likeCount || 0) + 1
        }
      }
    } else {
      ElMessage.error(res.msg || '点赞失败')
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败')
  }
}

// 加载更多回复
const loadMoreReplies = (commentId) => {
  // 这里可以实现加载更多回复的逻辑
  ElMessage.info('更多回复功能开发中')
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 登录
const handleLogin = () => {
  emit('login')
}
</script>

<style lang="scss">
// 评论系统公共样式变量
$comment-primary-color: #2a9eff;
$comment-text-color: #333;
$comment-text-secondary: #666;
$comment-text-muted: #999;
$comment-border-color: #e8e8e8;
$comment-bg-light: #f8f9fa;
$comment-bg-lighter: #f5f6f7;
$comment-danger-color: #f56c6c;

// 评论区域容器
.comments-section {
  padding: 20px 0;
}

// 评论输入区域
.comment-input-area {
  background: $comment-bg-light;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;

  .comment-user-info {
    margin-bottom: 10px;

    .username {
      font-weight: 600;
      color: $comment-text-color;
    }
  }

  .comment-actions {
    margin-top: 15px;
    text-align: right;
  }
}

// 登录提示
.comment-login-tip {
  margin-bottom: 30px;
}

// 评论列表头部
.comments-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid $comment-border-color;

  .total-count {
    font-size: 16px;
    font-weight: 600;
    color: $comment-text-color;
  }
}

// 无评论提示
.no-comments {
  padding: 40px 0;
}

// 评论项
.comment-item {
  display: block !important;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
}

// 主评论
.main-comment {
  display: block;
  width: 100%;
  margin-bottom: 10px;

  .comment-body {
    .username {
      font-size: 15px;
      font-weight: 600;
      color: $comment-primary-color;
      margin-bottom: 8px;
    }

    .comment-content {
      font-size: 14px;
      color: $comment-text-color;
      line-height: 1.8;
      margin-bottom: 12px;
      word-wrap: break-word;
    }

    .comment-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .time {
        color: $comment-text-muted;
        font-size: 13px;
      }
    }
  }
}

// 操作按钮栏
.comment-actions-bar,
.reply-actions-bar {
  display: flex;
  gap: 20px;

  .action-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: $comment-text-secondary;
    cursor: pointer;
    transition: color 0.2s;

    &:hover {
      color: $comment-primary-color;
    }

    .el-icon {
      font-size: 14px;
    }

    &.delete {
      &:hover {
        color: $comment-danger-color;
      }
    }

    &.appeal {
      &:hover {
        color: #ff9f43;
      }
    }
  }
}

.reply-actions-bar {
  gap: 15px;

  .action-btn {
    font-size: 12px;

    .el-icon {
      font-size: 13px;
    }
  }
}

// 回复输入框
.reply-input-wrapper {
  margin: 15px 0;
  padding: 15px;
  background: $comment-bg-light;
  border-radius: 8px;

  .reply-actions {
    margin-top: 10px;
    text-align: right;
  }
}

// 回复容器
.replies-container {
  margin-top: 15px;
  width: 100%;
}

// 回复列表盒子
.replies-box {
  background: $comment-bg-lighter;
  border-radius: 8px;
  padding: 0 20px;
  margin: 0 10px;
}

// 回复项
.reply-item {
  padding: 15px 0;
  border-bottom: 1px solid #e0e0e0;

  &:last-child {
    border-bottom: none;
  }

  .reply-username {
    font-size: 14px;
    font-weight: 600;
    color: $comment-primary-color;
    margin-bottom: 6px;
  }

  .reply-content {
    font-size: 14px;
    color: $comment-text-color;
    line-height: 1.6;
    margin-bottom: 8px;

    .reply-to {
      color: $comment-text-secondary;

      .to-username {
        color: $comment-primary-color;
      }
    }
  }

  .reply-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .time {
      color: $comment-text-muted;
      font-size: 12px;
    }
  }
}

// 展开更多回复
.reply-expand {
  margin-top: 10px;
  padding: 8px 0;
  text-align: left;

  .expand-btn {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: $comment-primary-color;
    cursor: pointer;
    transition: color 0.2s;

    &:hover {
      color: darken($comment-primary-color, 10%);
    }

    .el-icon {
      font-size: 12px;
    }
  }
}

// 分页
.main-pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>
