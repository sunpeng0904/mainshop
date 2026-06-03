<template>
  <div class="share-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">朋友圈</h1>
        <p class="page-desc">分享生活，记录美好</p>
      </div>
      <el-button type="primary" class="publish-btn" @click="showPublishDialog = true">
        <el-icon><EditPen /></el-icon>
        发布分享
      </el-button>
    </div>

    <!-- 主体内容 -->
    <div class="share-container">
      <!-- 左侧动态流 -->
      <div class="share-main">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="好友圈" name="friend">
            <div class="feed-list">
              <template v-if="friendList.length > 0">
                <ShareCard
                  v-for="item in friendList"
                  :key="item.shareId"
                  :share="item"
                  @like="handleLikeUpdate"
                  @collect="handleCollectUpdate"
                  @refresh="fetchFriendShares"
                />
              </template>
              <el-empty v-else-if="!friendLoading" description="暂无好友动态，快去关注好友吧" />

              <div v-if="friendList.length > 0" class="load-more">
                <el-button
                  v-if="friendHasMore"
                  :loading="friendLoading"
                  @click="loadMoreFriends"
                >
                  加载更多
                </el-button>
                <span v-else class="no-more">没有更多动态了</span>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="热门" name="hot">
            <div class="feed-list">
              <template v-if="hotList.length > 0">
                <ShareCard
                  v-for="item in hotList"
                  :key="item.shareId"
                  :share="item"
                  @like="handleLikeUpdate"
                  @collect="handleCollectUpdate"
                  @refresh="fetchHotShares"
                />
              </template>
              <el-empty v-else-if="!hotLoading" description="暂无热门分享" />

              <div v-if="hotList.length > 0" class="load-more">
                <el-button
                  v-if="hotHasMore"
                  :loading="hotLoading"
                  @click="loadMoreHot"
                >
                  加载更多
                </el-button>
                <span v-else class="no-more">没有更多动态了</span>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 右侧边栏 -->
      <div class="share-sidebar">
        <!-- 快捷入口 -->
        <div class="sidebar-card">
          <h3 class="sidebar-title">快捷入口</h3>
          <div class="quick-links">
            <div class="quick-link" @click="$router.push('/share/favorites')">
              <el-icon :size="24" color="#E6A23C"><Star /></el-icon>
              <span>我的收藏</span>
            </div>
            <div class="quick-link" @click="$router.push('/share/notifications')">
              <el-badge :value="unreadCount" :hidden="unreadCount === 0">
                <el-icon :size="24" color="#F56C6C"><Bell /></el-icon>
              </el-badge>
              <span>消息通知</span>
            </div>
            <div class="quick-link" @click="$router.push('/share/circle/' + currentUserId)">
              <el-icon :size="24" color="#409EFF"><User /></el-icon>
              <span>我的圈子</span>
            </div>
          </div>
        </div>

        <!-- 热门话题（预留） -->
        <div class="sidebar-card">
          <h3 class="sidebar-title">热门话题</h3>
          <div class="topic-list">
            <div v-for="topic in hotTopics" :key="topic.id" class="topic-item">
              <span class="topic-name">#{{ topic.name }}#</span>
              <span class="topic-count">{{ topic.count }}条</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 发布对话框 -->
    <SharePublishDialog
      v-model="showPublishDialog"
      @published="handlePublished"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { getFriendShares, getHotShares } from '@/api/share'
import ShareCard from '@/components/share-card.vue'
import SharePublishDialog from '@/components/share-publish-dialog.vue'

const store = useStore()

const currentUserId = computed(() => store.state.user.userInfo?.id)
const unreadCount = computed(() => store.getters['share/totalUnreadCount'])

// 当前tab
const activeTab = ref('friend')

// 发布对话框
const showPublishDialog = ref(false)

// 好友圈数据
const friendList = ref([])
const friendLoading = ref(false)
const friendPage = ref(1)
const friendHasMore = ref(true)

// 热门数据
const hotList = ref([])
const hotLoading = ref(false)
const hotPage = ref(1)
const hotHasMore = ref(true)

// 热门话题（模拟数据）
const hotTopics = ref([
  { id: 1, name: '周末出游', count: 328 },
  { id: 2, name: '美食分享', count: 256 },
  { id: 3, name: '户外运动', count: 189 },
  { id: 4, name: '摄影作品', count: 145 },
  { id: 5, name: '旅行日记', count: 120 }
])

// 获取好友圈动态
const fetchFriendShares = async (isLoadMore = false) => {
  if (friendLoading.value) return
  friendLoading.value = true

  try {
    const res = await getFriendShares({
      pageNum: isLoadMore ? friendPage.value : 1,
      pageSize: 10
    })

    const records = res.data.records || []
    if (isLoadMore) {
      friendList.value.push(...records)
      friendPage.value++
    } else {
      friendList.value = records
      friendPage.value = 2
    }
    friendHasMore.value = records.length === 10
  } catch (error) {
    ElMessage.error('获取好友动态失败')
  } finally {
    friendLoading.value = false
  }
}

// 获取热门分享
const fetchHotShares = async (isLoadMore = false) => {
  if (hotLoading.value) return
  hotLoading.value = true

  try {
    const res = await getHotShares({
      pageNum: isLoadMore ? hotPage.value : 1,
      pageSize: 10
    })

    const records = res.data.records || []
    if (isLoadMore) {
      hotList.value.push(...records)
      hotPage.value++
    } else {
      hotList.value = records
      hotPage.value = 2
    }
    hotHasMore.value = records.length === 10
  } catch (error) {
    ElMessage.error('获取热门分享失败')
  } finally {
    hotLoading.value = false
  }
}

// Tab切换
const handleTabChange = (tab) => {
  if (tab === 'friend' && friendList.value.length === 0) {
    fetchFriendShares()
  } else if (tab === 'hot' && hotList.value.length === 0) {
    fetchHotShares()
  }
}

// 加载更多
const loadMoreFriends = () => fetchFriendShares(true)
const loadMoreHot = () => fetchHotShares(true)

// 点赞更新
const handleLikeUpdate = ({ shareId, isLiked, likeCount }) => {
  const updateList = (list) => {
    const item = list.value.find(s => s.shareId === shareId)
    if (item) {
      item.isLiked = isLiked
      item.likeCount = likeCount
    }
  }
  updateList(friendList)
  updateList(hotList)
}

// 收藏更新
const handleCollectUpdate = ({ shareId, isCollected, collectCount }) => {
  const updateList = (list) => {
    const item = list.value.find(s => s.shareId === shareId)
    if (item) {
      item.isCollected = isCollected
      item.collectCount = collectCount
    }
  }
  updateList(friendList)
  updateList(hotList)
}

// 发布成功
const handlePublished = () => {
  fetchFriendShares()
  activeTab.value = 'friend'
}

onMounted(() => {
  fetchFriendShares()
  // 获取未读通知数
  store.dispatch('share/fetchUnreadCount')
})
</script>

<style lang="scss" scoped>
.share-page {
  padding: 0 0 40px;
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto 24px;
  border-radius: 12px;

  .header-content {
    .page-title {
      font-size: 32px;
      font-weight: 600;
      margin-bottom: 6px;
    }

    .page-desc {
      font-size: 15px;
      opacity: 0.9;
    }
  }

  .publish-btn {
    height: 42px;
    padding: 0 24px;
    font-size: 15px;
    border-radius: 21px;

    .el-icon {
      margin-right: 6px;
    }
  }
}

.share-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  gap: 24px;
}

.share-main {
  flex: 1;
  min-width: 0;

  :deep(.el-tabs__header) {
    margin-bottom: 16px;
  }
}

.share-sidebar {
  width: 300px;
  flex-shrink: 0;
}

.sidebar-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  .sidebar-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
    padding-bottom: 10px;
    border-bottom: 1px solid #f0f0f0;
  }
}

.quick-links {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .quick-link {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 12px;
    border-radius: 8px;
    cursor: pointer;
    transition: background 0.2s;
    font-size: 14px;
    color: #606266;

    &:hover {
      background: #f5f7fa;
    }
  }
}

.topic-list {
  .topic-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px solid #f5f5f5;

    &:last-child {
      border-bottom: none;
    }

    .topic-name {
      font-size: 14px;
      color: #409EFF;
      cursor: pointer;

      &:hover {
        text-decoration: underline;
      }
    }

    .topic-count {
      font-size: 12px;
      color: #909399;
    }
  }
}

.feed-list {
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

@media (max-width: 900px) {
  .page-header {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .share-container {
    flex-direction: column;
  }

  .share-sidebar {
    width: 100%;
  }
}
</style>
