<template>
  <div class="share-detail-page">
    <div class="detail-container">
      <!-- 返回按钮 -->
      <div class="back-bar">
        <el-button text @click="goBack">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
      </div>

      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="10" animated />
      </div>

      <template v-else-if="share">
        <!-- 分享内容卡片 -->
        <div class="detail-card">
          <!-- 用户信息 -->
          <div class="share-header">
            <div class="user-info" @click="goToCircle(share.user.userId)">
              <el-avatar :size="48" :src="getImageUrl(share.user.avatar)">
                {{ share.user.nickname?.charAt(0) }}
              </el-avatar>
              <div class="user-detail">
                <span class="nickname">{{ share.user.nickname }}</span>
                <span class="time">{{ formatTime(share.createTime) }}</span>
              </div>
            </div>
            <el-button
              v-if="!isOwner"
              :type="isFollowing ? 'default' : 'primary'"
              size="small"
              round
              @click="handleFollow"
            >
              {{ isFollowing ? '已关注' : '关注' }}
            </el-button>
          </div>

          <!-- 内容 -->
          <div class="share-content">
            <p v-if="share.content" class="content-text">{{ share.content }}</p>

            <!-- 图片 -->
            <div v-if="processedImageUrls.length > 0" class="image-gallery">
              <el-image
                v-for="(url, index) in processedImageUrls"
                :key="index"
                :src="url"
                :preview-src-list="processedImageUrls"
                :initial-index="index"
                fit="contain"
                class="gallery-image"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>

            <!-- 位置 -->
            <div v-if="share.location" class="share-location">
              <el-icon><Location /></el-icon>
              <span>{{ share.location }}</span>
            </div>
          </div>

          <!-- 互动栏 -->
          <div class="interaction-bar">
            <div class="interaction-item" @click="handleForward">
              <el-icon><Share /></el-icon>
              <span>转发 {{ share.forwardCount || '' }}</span>
            </div>
            <div class="interaction-item">
              <el-icon><ChatDotRound /></el-icon>
              <span>评论 {{ share.commentCount || '' }}</span>
            </div>
            <div
              class="interaction-item"
              :class="{ active: share.isCollected }"
              @click="handleCollect"
            >
              <el-icon><Star /></el-icon>
              <span>收藏 {{ share.collectCount || '' }}</span>
            </div>
            <div
              class="interaction-item like-btn"
              :class="{ active: share.isLiked }"
              @click="handleLike"
            >
              <el-icon><StarFilled v-if="share.isLiked" /><Star v-else /></el-icon>
              <span>点赞 {{ share.likeCount || '' }}</span>
            </div>
          </div>
        </div>

        <!-- 评论区 -->
        <div class="comment-section">
          <h3 class="section-title">评论 ({{ share.commentCount || 0 }})</h3>
          <CommentPanel
            ref="commentPanelRef"
            :share-id="shareId"
            @comment-added="handleCommentAdded"
            @comment-deleted="handleCommentDeleted"
          />
        </div>
      </template>

      <el-empty v-else description="分享不存在或已被删除" />
    </div>

    <!-- 转发对话框 -->
    <el-dialog
      v-model="forwardDialogVisible"
      title="转发分享"
      width="480px"
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
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getShareDetail,
  toggleLike,
  toggleCollect,
  forwardShare,
  followUser,
  unfollowUser
} from '@/api/share'
import { getImageUrl, getImageUrls } from '@/utils/image'
import CommentPanel from '@/components/CommentPanel.vue'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const router = useRouter()
const route = useRoute()
const store = useStore()

const shareId = computed(() => Number(route.params.id))
const share = ref(null)
const loading = ref(true)
const isFollowing = ref(false)

const currentUserId = computed(() => store.state.user.userInfo?.id)
const isOwner = computed(() => share.value?.user?.userId === currentUserId.value)

// 处理图片URL（添加/api前缀）
const processedImageUrls = computed(() => getImageUrls(share.value?.imageUrls))

// 评论组件引用
const commentPanelRef = ref(null)

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

// 返回
const goBack = () => {
  router.back()
}

// 跳转到用户圈子
const goToCircle = (userId) => {
  router.push(`/share/circle/${userId}`)
}

// 获取分享详情
const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await getShareDetail(shareId.value)
    share.value = res.data
  } catch (error) {
    ElMessage.error('获取分享详情失败')
  } finally {
    loading.value = false
  }
}

// 关注/取消关注
const handleFollow = async () => {
  try {
    if (isFollowing.value) {
      await unfollowUser(share.value.user.userId)
      isFollowing.value = false
      ElMessage.success('已取消关注')
    } else {
      await followUser(share.value.user.userId)
      isFollowing.value = true
      ElMessage.success('关注成功')
    }
  } catch (error) {
    // 错误已在拦截器处理
  }
}

// 点赞
const handleLike = async () => {
  try {
    const res = await toggleLike(shareId.value)
    share.value.isLiked = res.data.isLiked
    share.value.likeCount = res.data.likeCount
  } catch (error) {
    // 错误已在拦截器处理
  }
}

// 收藏
const handleCollect = async () => {
  try {
    const res = await toggleCollect(shareId.value)
    share.value.isCollected = res.data.isCollected
    share.value.collectCount = res.data.collectCount
  } catch (error) {
    // 错误已在拦截器处理
  }
}

// 转发
const handleForward = () => {
  forwardContent.value = ''
  forwardAlsoLike.value = false
  forwardDialogVisible.value = true
}

const submitForward = async () => {
  forwardLoading.value = true
  try {
    await forwardShare(shareId.value, {
      content: forwardContent.value,
      alsoLike: forwardAlsoLike.value
    })
    forwardDialogVisible.value = false
    ElMessage.success('转发成功')
    fetchDetail()
  } catch (error) {
    // 错误已在拦截器处理
  } finally {
    forwardLoading.value = false
  }
}

// 评论事件
const handleCommentAdded = () => {
  share.value.commentCount = (share.value.commentCount || 0) + 1
}

const handleCommentDeleted = () => {
  share.value.commentCount = Math.max(0, (share.value.commentCount || 0) - 1)
}

onMounted(() => {
  fetchDetail()
})
</script>

<style lang="scss" scoped>
.share-detail-page {
  padding: 0 0 40px;
}

.detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.back-bar {
  padding: 16px 0;
}

.detail-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.share-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;

  .user-detail {
    display: flex;
    flex-direction: column;

    .nickname {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }

    .time {
      font-size: 13px;
      color: #909399;
      margin-top: 2px;
    }
  }
}

.share-content {
  .content-text {
    font-size: 16px;
    line-height: 1.8;
    color: #303133;
    margin-bottom: 16px;
    word-break: break-all;
    white-space: pre-wrap;
  }
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 8px;
  margin-bottom: 16px;

  .gallery-image {
    width: 100%;
    border-radius: 8px;
    overflow: hidden;
    aspect-ratio: 1;

    .image-error {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f5f7fa;
      color: #c0c4cc;
    }
  }
}

.share-location {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #909399;
  margin-bottom: 16px;
}

.interaction-bar {
  display: flex;
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
  gap: 0;

  .interaction-item {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 10px 0;
    cursor: pointer;
    border-radius: 8px;
    font-size: 14px;
    color: #606266;
    transition: all 0.2s;

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

.comment-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #f0f0f0;
  }
}

.loading-state {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
}

@media (max-width: 768px) {
  .detail-container {
    padding: 0 12px;
  }

  .detail-card {
    padding: 16px;
  }

  .image-gallery {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
