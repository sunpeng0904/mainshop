<template>
  <div class="comment-panel">
    <!-- 评论输入区 -->
    <div class="comment-input-area">
      <el-avatar :size="36" :src="getImageUrl(currentUser?.avatar)">
        {{ currentUser?.nickname?.charAt(0) }}
      </el-avatar>
      <div class="input-wrapper">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="2"
          :placeholder="replyPlaceholder"
          maxlength="500"
          show-word-limit
          resize="none"
        />
        <div class="input-actions">
          <el-button
            type="primary"
            size="small"
            :disabled="!commentContent.trim()"
            :loading="submitting"
            @click="submitComment"
          >
            发表
          </el-button>
        </div>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="3" animated />
      </div>

      <template v-else>
        <div
          v-for="comment in comments"
          :key="comment.commentId"
          class="comment-item"
        >
          <el-avatar :size="36" :src="getImageUrl(comment.user.avatar)">
            {{ comment.user.nickname?.charAt(0) }}
          </el-avatar>
          <div class="comment-body">
            <div class="comment-header">
              <span class="comment-nickname">{{ comment.user.nickname }}</span>
              <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <div class="comment-content">
              <template v-if="comment.replyUser">
                回复 <span class="reply-name">@{{ comment.replyUser.nickname }}</span>：
              </template>
              {{ comment.content }}
            </div>
            <div class="comment-actions">
              <span class="action-btn" @click="replyTo(comment)">
                <el-icon><ChatRound /></el-icon> 回复
              </span>
              <span
                class="action-btn"
                :class="{ liked: comment.isLiked }"
                @click="handleCommentLike(comment)"
              >
                <el-icon><StarFilled v-if="comment.isLiked" /><Star v-else /></el-icon>
                {{ comment.likeCount || '' }}
              </span>
              <span
                v-if="isCommentOwner(comment)"
                class="action-btn delete-btn"
                @click="handleDeleteComment(comment)"
              >
                <el-icon><Delete /></el-icon> 删除
              </span>
            </div>

            <!-- 子评论 -->
            <div v-if="comment.replyList && comment.replyList.length > 0" class="sub-comments">
              <div
                v-for="reply in comment.replyList"
                :key="reply.commentId"
                class="sub-comment-item"
              >
                <el-avatar :size="28" :src="getImageUrl(reply.user.avatar)">
                  {{ reply.user.nickname?.charAt(0) }}
                </el-avatar>
                <div class="sub-comment-body">
                  <div class="comment-header">
                    <span class="comment-nickname">{{ reply.user.nickname }}</span>
                    <span class="comment-time">{{ formatTime(reply.createTime) }}</span>
                  </div>
                  <div class="comment-content">
                    <template v-if="reply.replyUser">
                      回复 <span class="reply-name">@{{ reply.replyUser.nickname }}</span>：
                    </template>
                    {{ reply.content }}
                  </div>
                  <div class="comment-actions">
                    <span class="action-btn" @click="replyTo(reply)">
                      <el-icon><ChatRound /></el-icon> 回复
                    </span>
                    <span
                      v-if="isCommentOwner(reply)"
                      class="action-btn delete-btn"
                      @click="handleDeleteComment(reply)"
                    >
                      <el-icon><Delete /></el-icon>
                    </span>
                  </div>
                </div>
              </div>

              <!-- 查看更多回复 -->
              <div
                v-if="comment.replyCount > comment.replyList.length"
                class="more-replies"
                @click="loadMoreReplies(comment)"
              >
                查看更多 {{ comment.replyCount - comment.replyList.length }} 条回复
                <el-icon><ArrowDown /></el-icon>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="comments.length === 0" description="暂无评论，快来发表第一条吧" :image-size="100" />

        <!-- 加载更多 -->
        <div v-if="comments.length > 0" class="load-more">
          <el-button
            v-if="hasMore"
            text
            :loading="loadingMore"
            @click="loadMore"
          >
            加载更多评论
          </el-button>
          <span v-else class="no-more">没有更多评论了</span>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createComment, getComments, deleteComment } from '@/api/share'
import { getImageUrl } from '@/utils/image'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const props = defineProps({
  shareId: {
    type: [Number, String],
    required: true
  }
})

const emit = defineEmits(['commentAdded', 'commentDeleted'])

const store = useStore()

const currentUser = computed(() => store.state.user.userInfo)

const comments = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const submitting = ref(false)
const commentContent = ref('')
const pageNum = ref(1)
const pageSize = ref(20)
const hasMore = ref(true)

// 回复相关
const replyingTo = ref(null)
const replyPlaceholder = computed(() => {
  if (replyingTo.value) {
    return `回复 @${replyingTo.value.user.nickname}...`
  }
  return '写评论...'
})

// 是否是评论作者
const isCommentOwner = (comment) => {
  return comment.user?.userId === currentUser.value?.id
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = dayjs(time)
  const now = dayjs()
  if (date.isSame(now, 'day')) {
    return date.fromNow()
  }
  if (date.isSame(now, 'year')) {
    return date.format('MM-DD HH:mm')
  }
  return date.format('YYYY-MM-DD HH:mm')
}

// 获取评论列表
const fetchComments = async (isLoadMore = false) => {
  if (isLoadMore) {
    loadingMore.value = true
  } else {
    loading.value = true
    pageNum.value = 1
  }

  try {
    const res = await getComments(props.shareId, {
      pageNum: isLoadMore ? pageNum.value : 1,
      pageSize: pageSize.value
    })

    const records = res.data.records || []

    if (isLoadMore) {
      comments.value.push(...records)
      pageNum.value++
    } else {
      comments.value = records
      pageNum.value = 2
    }

    hasMore.value = records.length === pageSize.value
  } catch (error) {
    ElMessage.error('获取评论失败')
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

// 加载更多
const loadMore = () => {
  fetchComments(true)
}

// 加载更多回复
const loadMoreReplies = async (comment) => {
  // 可以扩展：调用接口获取更多子评论
}

// 回复某条评论
const replyTo = (comment) => {
  replyingTo.value = comment
  commentContent.value = ''
}

// 取消回复
const cancelReply = () => {
  replyingTo.value = null
}

// 提交评论
const submitComment = async () => {
  if (!commentContent.value.trim()) return

  submitting.value = true
  try {
    const data = {
      content: commentContent.value.trim(),
      parentId: replyingTo.value?.parentId || replyingTo.value?.commentId || 0,
      replyUserId: replyingTo.value?.user?.userId || 0
    }

    await createComment(props.shareId, data)
    ElMessage.success('评论成功')
    commentContent.value = ''
    replyingTo.value = null

    emit('commentAdded')
    // 刷新评论列表
    fetchComments()
  } catch (error) {
    // 错误已在拦截器处理
  } finally {
    submitting.value = false
  }
}

// 删除评论
const handleDeleteComment = async (comment) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteComment(props.shareId, comment.commentId)
    ElMessage.success('删除成功')
    emit('commentDeleted')
    fetchComments()
  } catch (e) {
    // 取消删除
  }
}

// 评论点赞（预留）
const handleCommentLike = async (comment) => {
  // 后端接口预留
}

onMounted(() => {
  fetchComments()
})

defineExpose({
  refresh: () => fetchComments()
})
</script>

<style lang="scss" scoped>
.comment-panel {
  padding: 0;
}

.comment-input-area {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;

  .input-wrapper {
    flex: 1;

    .input-actions {
      display: flex;
      justify-content: flex-end;
      margin-top: 8px;
    }
  }
}

.comment-list {
  .comment-item {
    display: flex;
    gap: 12px;
    padding: 14px 0;
    border-bottom: 1px solid #f5f5f5;

    &:last-child {
      border-bottom: none;
    }
  }
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;

  .comment-nickname {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
  }

  .comment-time {
    font-size: 12px;
    color: #c0c4cc;
  }
}

.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  margin-bottom: 6px;
  word-break: break-all;

  .reply-name {
    color: #409EFF;
    cursor: pointer;
  }
}

.comment-actions {
  display: flex;
  gap: 16px;

  .action-btn {
    display: flex;
    align-items: center;
    gap: 3px;
    font-size: 12px;
    color: #909399;
    cursor: pointer;
    transition: color 0.2s;

    &:hover {
      color: #409EFF;
    }

    &.liked {
      color: #F56C6C;
    }

    &.delete-btn:hover {
      color: #F56C6C;
    }
  }
}

.sub-comments {
  margin-top: 10px;
  padding: 10px 12px;
  background: #f9f9f9;
  border-radius: 8px;

  .sub-comment-item {
    display: flex;
    gap: 8px;
    padding: 8px 0;

    &:not(:last-child) {
      border-bottom: 1px solid #eee;
    }
  }

  .sub-comment-body {
    flex: 1;
  }

  .more-replies {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: #409EFF;
    cursor: pointer;
    padding-top: 8px;

    &:hover {
      text-decoration: underline;
    }
  }
}

.loading-state {
  padding: 16px 0;
}

.load-more {
  text-align: center;
  padding: 16px 0;

  .no-more {
    font-size: 13px;
    color: #c0c4cc;
  }
}
</style>
