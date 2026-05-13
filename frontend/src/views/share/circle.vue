<template>
  <div class="circle-page">
    <div class="page-container">
      <!-- 用户信息头部 -->
      <div class="user-profile-card">
        <div class="profile-header">
          <el-avatar :size="80" :src="getImageUrl(userInfo?.avatar)">
            {{ userInfo?.nickname?.charAt(0) }}
          </el-avatar>
          <div class="profile-info">
            <h2 class="nickname">{{ userInfo?.nickname || '加载中...' }}</h2>
            <p v-if="userInfo?.bio" class="bio">{{ userInfo.bio }}</p>
            <div class="profile-stats">
              <div class="stat-item" @click="showFollowingDialog = true">
                <span class="stat-value">{{ userInfo?.followingCount || 0 }}</span>
                <span class="stat-label">关注</span>
              </div>
              <div class="stat-item" @click="showFollowersDialog = true">
                <span class="stat-value">{{ userInfo?.followerCount || 0 }}</span>
                <span class="stat-label">粉丝</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ userInfo?.shareCount || 0 }}</span>
                <span class="stat-label">分享</span>
              </div>
            </div>
          </div>
          <div v-if="!isOwner" class="profile-actions">
            <el-button
              :type="isFollowing ? 'default' : 'primary'"
              round
              @click="handleFollow"
            >
              {{ isFollowing ? '已关注' : '关注' }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- 分享列表 -->
      <div class="share-list">
        <template v-if="shareList.length > 0">
          <ShareCard
            v-for="item in shareList"
            :key="item.shareId"
            :share="item"
            @like="handleLikeUpdate"
            @collect="handleCollectUpdate"
            @refresh="fetchUserShares"
          />
        </template>
        <el-empty v-else-if="!loading" :description="isOwner ? '你还没有发布过分享' : '该用户还没有分享'" />

        <div v-if="shareList.length > 0" class="load-more">
          <el-button
            v-if="hasMore"
            :loading="loading"
            @click="loadMore"
          >
            加载更多
          </el-button>
          <span v-else class="no-more">没有更多分享了</span>
        </div>
      </div>
    </div>

    <!-- 关注列表弹窗 -->
    <el-dialog v-model="showFollowingDialog" title="关注列表" width="500px">
      <div class="user-list">
        <div
          v-for="user in followingList"
          :key="user.userId"
          class="user-list-item"
          @click="goToCircle(user.userId)"
        >
          <el-avatar :size="40" :src="getImageUrl(user.avatar)">{{ user.nickname?.charAt(0) }}</el-avatar>
          <span class="user-name">{{ user.nickname }}</span>
        </div>
        <el-empty v-if="followingList.length === 0" description="暂无关注" :image-size="60" />
      </div>
    </el-dialog>

    <!-- 粉丝列表弹窗 -->
    <el-dialog v-model="showFollowersDialog" title="粉丝列表" width="500px">
      <div class="user-list">
        <div
          v-for="user in followersList"
          :key="user.userId"
          class="user-list-item"
          @click="goToCircle(user.userId)"
        >
          <el-avatar :size="40" :src="getImageUrl(user.avatar)">{{ user.nickname?.charAt(0) }}</el-avatar>
          <span class="user-name">{{ user.nickname }}</span>
        </div>
        <el-empty v-if="followersList.length === 0" description="暂无粉丝" :image-size="60" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import {
  getUserShares,
  followUser,
  unfollowUser,
  getFollowing,
  getFollowers
} from '@/api/share'
import { getImageUrl } from '@/utils/image'
import ShareCard from '@/components/ShareCard.vue'

const router = useRouter()
const route = useRoute()
const store = useStore()

const userId = computed(() => Number(route.params.userId))
const currentUserId = computed(() => store.state.user.userInfo?.id)
const isOwner = computed(() => userId.value === currentUserId.value)

const userInfo = ref(null)
const isFollowing = ref(false)
const shareList = ref([])
const loading = ref(false)
const page = ref(1)
const hasMore = ref(true)

// 关注/粉丝弹窗
const showFollowingDialog = ref(false)
const showFollowersDialog = ref(false)
const followingList = ref([])
const followersList = ref([])

// 获取用户分享列表
const fetchUserShares = async (isLoadMore = false) => {
  if (loading.value) return
  loading.value = true

  try {
    const res = await getUserShares(userId.value, {
      pageNum: isLoadMore ? page.value : 1,
      pageSize: 10
    })

    const records = res.data.records || []
    if (isLoadMore) {
      shareList.value.push(...records)
      page.value++
    } else {
      shareList.value = records
      page.value = 2
    }
    hasMore.value = records.length === 10
  } catch (error) {
    ElMessage.error('获取分享列表失败')
  } finally {
    loading.value = false
  }
}

// 加载更多
const loadMore = () => fetchUserShares(true)

// 关注/取消关注
const handleFollow = async () => {
  try {
    if (isFollowing.value) {
      await unfollowUser(userId.value)
      isFollowing.value = false
      userInfo.value.followerCount = Math.max(0, (userInfo.value.followerCount || 1) - 1)
      ElMessage.success('已取消关注')
    } else {
      await followUser(userId.value)
      isFollowing.value = true
      userInfo.value.followerCount = (userInfo.value.followerCount || 0) + 1
      ElMessage.success('关注成功')
    }
  } catch (error) {
    // 错误已在拦截器处理
  }
}

// 跳转到用户圈子
const goToCircle = (uid) => {
  if (uid !== userId.value) {
    router.push(`/share/circle/${uid}`)
    showFollowingDialog.value = false
    showFollowersDialog.value = false
  }
}

// 点赞更新
const handleLikeUpdate = ({ shareId, isLiked, likeCount }) => {
  const item = shareList.value.find(s => s.shareId === shareId)
  if (item) {
    item.isLiked = isLiked
    item.likeCount = likeCount
  }
}

// 收藏更新
const handleCollectUpdate = ({ shareId, isCollected, collectCount }) => {
  const item = shareList.value.find(s => s.shareId === shareId)
  if (item) {
    item.isCollected = isCollected
    item.collectCount = collectCount
  }
}

// 加载关注列表
const loadFollowing = async () => {
  try {
    const res = await getFollowing({ pageNum: 1, pageSize: 100 })
    followingList.value = res.data.records || []
  } catch (error) {
    // 静默处理
  }
}

// 加载粉丝列表
const loadFollowers = async () => {
  try {
    const res = await getFollowers({ pageNum: 1, pageSize: 100 })
    followersList.value = res.data.records || []
  } catch (error) {
    // 静默处理
  }
}

// 监听弹窗打开
watch(showFollowingDialog, (val) => {
  if (val && followingList.value.length === 0) loadFollowing()
})
watch(showFollowersDialog, (val) => {
  if (val && followersList.value.length === 0) loadFollowers()
})

// 监听路由参数变化
watch(userId, () => {
  page.value = 1
  shareList.value = []
  fetchUserShares()
})

onMounted(() => {
  fetchUserShares()
})
</script>

<style lang="scss" scoped>
.circle-page {
  padding: 0 0 40px;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.user-profile-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.profile-header {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.profile-info {
  flex: 1;

  .nickname {
    font-size: 22px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 6px;
  }

  .bio {
    font-size: 14px;
    color: #909399;
    margin-bottom: 12px;
    line-height: 1.5;
  }
}

.profile-stats {
  display: flex;
  gap: 32px;

  .stat-item {
    text-align: center;
    cursor: pointer;

    .stat-value {
      display: block;
      font-size: 20px;
      font-weight: 600;
      color: #303133;
    }

    .stat-label {
      font-size: 13px;
      color: #909399;
    }
  }
}

.profile-actions {
  flex-shrink: 0;
}

.share-list {
  min-height: 300px;
}

.load-more {
  text-align: center;
  padding: 30px 0;

  .no-more {
    color: #909399;
    font-size: 14px;
  }
}

.user-list {
  max-height: 400px;
  overflow-y: auto;

  .user-list-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    border-radius: 8px;
    cursor: pointer;
    transition: background 0.2s;

    &:hover {
      background: #f5f7fa;
    }

    .user-name {
      font-size: 15px;
      color: #303133;
    }
  }
}

@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .profile-stats {
    justify-content: center;
  }

  .profile-actions {
    width: 100%;
    text-align: center;
  }
}
</style>
