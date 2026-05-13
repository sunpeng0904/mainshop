<template>
  <div class="share-card" :class="{ 'is-forward': !!share.forwardFrom }">
    <!-- 转发来源 -->
    <div v-if="share.forwardFrom" class="forward-source">
      <el-icon><Share /></el-icon>
      <span>{{ share.forwardFrom.user.nickname }} 转发了</span>
    </div>

    <!-- 用户信息 -->
    <div class="share-header">
      <div class="user-info" @click="goToCircle(share.user.userId)">
        <el-avatar :size="42" :src="getImageUrl(share.user.avatar)">
          {{ share.user.nickname?.charAt(0) }}
        </el-avatar>
        <div class="user-detail">
          <span class="nickname">{{ share.user.nickname }}</span>
          <span class="time">{{ formatTime(share.createTime) }}</span>
        </div>
      </div>
      <div class="share-actions-menu">
        <el-dropdown trigger="click" @command="handleCommand">
          <el-button text circle>
            <el-icon><MoreFilled /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-if="isOwner" command="delete">
                <el-icon><Delete /></el-icon> 删除
              </el-dropdown-item>
              <el-dropdown-item v-if="!isOwner" command="report">
                <el-icon><WarningFilled /></el-icon> 举报
              </el-dropdown-item>
              <el-dropdown-item command="copyLink">
                <el-icon><Link /></el-icon> 复制链接
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="share-content" @click="goToDetail">
      <p v-if="share.content" class="content-text">{{ share.content }}</p>

      <!-- 图片网格 -->
      <div v-if="share.imageUrls && share.imageUrls.length > 0" class="image-grid" :class="`grid-${Math.min(share.imageUrls.length, 3)}`">
        <div
          v-for="(url, index) in displayImages"
          :key="index"
          class="image-item"
          @click.stop="previewImage(index)"
        >
          <el-image
            :src="url"
            fit="cover"
            class="share-image"
            :preview-src-list="allImages"
            :initial-index="index"
          >
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          <!-- 超过9张时显示剩余数量 -->
          <div v-if="index === 8 && share.imageUrls.length > 9" class="image-more">
            +{{ share.imageUrls.length - 9 }}
          </div>
        </div>
      </div>

      <!-- 位置信息 -->
      <div v-if="share.location" class="share-location">
        <el-icon><Location /></el-icon>
        <span>{{ share.location }}</span>
      </div>
    </div>

    <!-- 互动栏 -->
    <div class="share-footer">
      <div class="interaction-bar">
        <!-- 转发 -->
        <div class="interaction-item" @click="handleForward">
          <el-icon :size="18"><Share /></el-icon>
          <span v-if="share.forwardCount > 0">{{ share.forwardCount }}</span>
        </div>
        <!-- 评论 -->
        <div class="interaction-item" @click="goToDetail">
          <el-icon :size="18"><ChatDotRound /></el-icon>
          <span v-if="share.commentCount > 0">{{ share.commentCount }}</span>
        </div>
        <!-- 收藏 -->
        <div class="interaction-item" :class="{ active: share.isCollected }" @click="handleCollect">
          <el-icon :size="18"><Star /></el-icon>
          <span v-if="share.collectCount > 0">{{ share.collectCount }}</span>
        </div>
        <!-- 点赞 -->
        <div class="interaction-item like-btn" :class="{ active: share.isLiked }" @click="handleLike">
          <el-icon :size="18"><StarFilled v-if="share.isLiked" /><Star v-else /></el-icon>
          <span v-if="share.likeCount > 0">{{ share.likeCount }}</span>
        </div>
      </div>
    </div>

    <!-- 转发对话框 -->
    <el-dialog
      v-model="forwardDialogVisible"
      title="转发分享"
      width="480px"
      :close-on-click-modal="false"
    >
      <el-input
        v-model="forwardContent"
        type="textarea"
        :rows="3"
        placeholder="说点什么..."
        maxlength="500"
        show-word-limit
      />
      <template #footer>
        <el-checkbox v-model="forwardAlsoLike">同时点赞</el-checkbox>
        <el-button @click="forwardDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="forwardLoading" @click="submitForward">转发</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import { toggleLike, toggleCollect, forwardShare, deleteShare, reportShare } from '@/api/share'
import { getImageUrl, getImageUrls } from '@/utils/image'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const props = defineProps({
  share: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['like', 'collect', 'forward', 'delete', 'refresh'])

const router = useRouter()
const store = useStore()

// 当前用户ID
const currentUserId = computed(() => store.state.user.userInfo?.id)
const isOwner = computed(() => props.share.user?.userId === currentUserId.value)

// 处理图片URL（添加/api前缀）并最多显示9张
const allImages = computed(() => getImageUrls(props.share.imageUrls))
const displayImages = computed(() => allImages.value.slice(0, 9))

// 转发相关
const forwardDialogVisible = ref(false)
const forwardContent = ref('')
const forwardAlsoLike = ref(false)
const forwardLoading = ref(false)

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

// 跳转到用户圈子
const goToCircle = (userId) => {
  router.push(`/share/circle/${userId}`)
}

// 跳转到详情
const goToDetail = () => {
  router.push(`/share/${props.share.shareId}`)
}

// 预览图片
const previewImage = (index) => {
  // Element Plus 的 el-image preview 已内置处理
}

// 点赞
const handleLike = async () => {
  try {
    const res = await toggleLike(props.share.shareId)
    emit('like', {
      shareId: props.share.shareId,
      isLiked: res.data.isLiked,
      likeCount: res.data.likeCount
    })
  } catch (error) {
    // 错误已在拦截器处理
  }
}

// 收藏
const handleCollect = async () => {
  try {
    const res = await toggleCollect(props.share.shareId)
    emit('collect', {
      shareId: props.share.shareId,
      isCollected: res.data.isCollected,
      collectCount: res.data.collectCount
    })
  } catch (error) {
    // 错误已在拦截器处理
  }
}

// 打开转发对话框
const handleForward = () => {
  forwardContent.value = ''
  forwardAlsoLike.value = false
  forwardDialogVisible.value = true
}

// 提交转发
const submitForward = async () => {
  forwardLoading.value = true
  try {
    await forwardShare(props.share.shareId, {
      content: forwardContent.value,
      alsoLike: forwardAlsoLike.value
    })
    forwardDialogVisible.value = false
    ElMessage.success('转发成功')
    emit('forward', props.share.shareId)
    emit('refresh')
  } catch (error) {
    // 错误已在拦截器处理
  } finally {
    forwardLoading.value = false
  }
}

// 更多操作
const handleCommand = async (command) => {
  switch (command) {
    case 'delete':
      try {
        await ElMessageBox.confirm('确定要删除这条分享吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteShare(props.share.shareId)
        ElMessage.success('删除成功')
        emit('delete', props.share.shareId)
        emit('refresh')
      } catch (e) {
        // 取消删除
      }
      break
    case 'report':
      ElMessageBox.prompt('请输入举报原因', '举报', {
        confirmButtonText: '提交',
        cancelButtonText: '取消',
        inputPlaceholder: '请描述举报原因'
      }).then(async ({ value }) => {
        if (value) {
          await reportShare(props.share.shareId, {
            reason: 'OTHER',
            description: value
          })
          ElMessage.success('举报已提交')
        }
      }).catch(() => {})
      break
    case 'copyLink':
      const link = `${window.location.origin}/share/${props.share.shareId}`
      navigator.clipboard.writeText(link).then(() => {
        ElMessage.success('链接已复制')
      }).catch(() => {
        ElMessage.error('复制失败，请手动复制')
      })
      break
  }
}
</script>

<style lang="scss" scoped>
.share-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s ease;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  }

  &.is-forward {
    background: #fafbfc;
  }
}

.forward-source {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.share-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;

  .user-detail {
    display: flex;
    flex-direction: column;

    .nickname {
      font-size: 15px;
      font-weight: 600;
      color: #303133;
    }

    .time {
      font-size: 12px;
      color: #909399;
      margin-top: 2px;
    }
  }
}

.share-content {
  cursor: pointer;

  .content-text {
    font-size: 15px;
    line-height: 1.6;
    color: #303133;
    margin-bottom: 10px;
    word-break: break-all;
    white-space: pre-wrap;
  }
}

.image-grid {
  display: grid;
  gap: 4px;
  margin-bottom: 10px;
  border-radius: 8px;
  overflow: hidden;

  &.grid-1 {
    grid-template-columns: 1fr;
    max-width: 360px;
  }

  &.grid-2 {
    grid-template-columns: repeat(2, 1fr);
    max-width: 360px;
  }

  &.grid-3 {
    grid-template-columns: repeat(3, 1fr);
  }

  .image-item {
    position: relative;
    aspect-ratio: 1;
    overflow: hidden;

    .share-image {
      width: 100%;
      height: 100%;
    }

    .image-error {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f5f7fa;
      color: #c0c4cc;
    }

    .image-more {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      color: #fff;
      font-weight: 600;
    }
  }
}

.share-location {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
}

.share-footer {
  border-top: 1px solid #f0f0f0;
  padding-top: 10px;
  margin-top: 10px;
}

.interaction-bar {
  display: flex;
  justify-content: space-around;

  .interaction-item {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 6px 12px;
    cursor: pointer;
    border-radius: 20px;
    font-size: 13px;
    color: #909399;
    transition: all 0.2s ease;

    &:hover {
      background: #f5f7fa;
    }

    &.active {
      color: #409EFF;
    }

    &.like-btn.active {
      color: #F56C6C;
    }
  }
}
</style>
