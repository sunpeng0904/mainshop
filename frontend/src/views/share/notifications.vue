<template>
  <div class="notifications-page">
    <div class="page-container">
      <!-- 页面头部 -->
      <div class="page-header">
        <el-button text @click="$router.push('/share')">
          <el-icon><ArrowLeft /></el-icon> 返回朋友圈
        </el-button>
        <h2 class="page-title">消息通知</h2>
        <el-button
          text
          type="primary"
          :disabled="unreadTotal === 0"
          @click="handleMarkAllRead"
        >
          全部已读
        </el-button>
      </div>

      <!-- 通知类型Tab -->
      <el-tabs v-model="activeType" @tab-change="handleTypeChange">
        <el-tab-pane name="all">
          <template #label>
            <span>全部</span>
            <el-badge v-if="unreadTotal > 0" :value="unreadTotal" class="tab-badge" />
          </template>
        </el-tab-pane>
        <el-tab-pane name="LIKE">
          <template #label>
            <span>赞</span>
            <el-badge v-if="unreadCounts.LIKE > 0" :value="unreadCounts.LIKE" class="tab-badge" />
          </template>
        </el-tab-pane>
        <el-tab-pane name="COMMENT">
          <template #label>
            <span>评论</span>
            <el-badge v-if="unreadCounts.COMMENT > 0" :value="unreadCounts.COMMENT" class="tab-badge" />
          </template>
        </el-tab-pane>
        <el-tab-pane name="FORWARD">
          <template #label>
            <span>转发</span>
            <el-badge v-if="unreadCounts.FORWARD > 0" :value="unreadCounts.FORWARD" class="tab-badge" />
          </template>
        </el-tab-pane>
        <el-tab-pane name="FOLLOW">
          <template #label>
            <span>关注</span>
            <el-badge v-if="unreadCounts.FOLLOW > 0" :value="unreadCounts.FOLLOW" class="tab-badge" />
          </template>
        </el-tab-pane>
      </el-tabs>

      <!-- 通知列表 -->
      <div class="notification-list">
        <template v-if="notifications.length > 0">
          <div
            v-for="item in notifications"
            :key="item.notificationId"
            class="notification-item"
            :class="{ unread: !item.isRead }"
            @click="handleItemClick(item)"
          >
            <div class="notification-avatar">
              <el-avatar :size="40" :src="getImageUrl(item.sender?.avatar)">
                {{ item.sender?.nickname?.charAt(0) }}
              </el-avatar>
              <div class="type-icon" :class="item.type.toLowerCase()">
                <el-icon :size="12">
                  <StarFilled v-if="item.type === 'LIKE'" />
                  <ChatDotRound v-else-if="item.type === 'COMMENT'" />
                  <Share v-else-if="item.type === 'FORWARD'" />
                  <User v-else-if="item.type === 'FOLLOW'" />
                  <Bell v-else />
                </el-icon>
              </div>
            </div>
            <div class="notification-content">
              <div class="notification-text">
                <span class="sender-name">{{ item.sender?.nickname }}</span>
                <span class="action-text">{{ getActionText(item.type) }}</span>
              </div>
              <div v-if="item.shareSummary" class="notification-summary">
                {{ item.shareSummary }}
              </div>
              <div class="notification-time">{{ formatTime(item.createTime) }}</div>
            </div>
            <div v-if="!item.isRead" class="unread-dot" />
          </div>
        </template>
        <el-empty v-else-if="!loading" description="暂无通知消息" />

        <div v-if="notifications.length > 0" class="load-more">
          <el-button
            v-if="hasMore"
            :loading="loading"
            @click="loadMore"
          >
            加载更多
          </el-button>
          <span v-else class="no-more">没有更多通知了</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { getNotifications, markAsRead, markAllAsRead } from '@/api/share'
import { getImageUrl } from '@/utils/image'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const router = useRouter()
const store = useStore()

const activeType = ref('all')
const notifications = ref([])
const loading = ref(false)
const page = ref(1)
const hasMore = ref(true)

const unreadCounts = computed(() => store.state.share.unreadCount)
const unreadTotal = computed(() => store.getters['share/totalUnreadCount'])

// 获取操作文本
const getActionText = (type) => {
  const map = {
    LIKE: '赞了你的分享',
    COMMENT: '评论了你的分享',
    FORWARD: '转发了你的分享',
    FOLLOW: '关注了你',
    MENTION: '在分享中@了你'
  }
  return map[type] || '产生了互动'
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

// 获取通知列表
const fetchNotifications = async (isLoadMore = false) => {
  if (loading.value) return
  loading.value = true

  try {
    const params = {
      pageNum: isLoadMore ? page.value : 1,
      pageSize: 20
    }
    if (activeType.value !== 'all') {
      params.type = activeType.value
    }

    const res = await getNotifications(params)
    const records = res.data.records || []

    if (isLoadMore) {
      notifications.value.push(...records)
      page.value++
    } else {
      notifications.value = records
      page.value = 2
    }
    hasMore.value = records.length === 20
  } catch (error) {
    ElMessage.error('获取通知失败')
  } finally {
    loading.value = false
  }
}

// 类型切换
const handleTypeChange = () => {
  fetchNotifications()
}

// 加载更多
const loadMore = () => fetchNotifications(true)

// 点击通知项
const handleItemClick = async (item) => {
  // 标记已读
  if (!item.isRead) {
    try {
      await markAsRead(item.notificationId)
      item.isRead = true
      store.dispatch('share/fetchUnreadCount')
    } catch (error) {
      // 静默处理
    }
  }

  // 跳转到相关内容
  if (item.type === 'FOLLOW') {
    router.push(`/share/circle/${item.sender?.userId}`)
  } else if (item.relatedId) {
    router.push(`/share/${item.relatedId}`)
  }
}

// 全部标记已读
const handleMarkAllRead = async () => {
  try {
    const type = activeType.value !== 'all' ? activeType.value : undefined
    await markAllAsRead(type)
    notifications.value.forEach(item => {
      item.isRead = true
    })
    store.dispatch('share/fetchUnreadCount')
    ElMessage.success('已全部标记为已读')
  } catch (error) {
    // 错误已在拦截器处理
  }
}

onMounted(() => {
  fetchNotifications()
  store.dispatch('share/fetchUnreadCount')
})
</script>

<style lang="scss" scoped>
.notifications-page {
  padding: 0 0 40px;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 0;

  .page-title {
    font-size: 22px;
    font-weight: 600;
    color: #303133;
    flex: 1;
  }
}

.tab-badge {
  margin-left: 4px;
}

.notification-list {
  min-height: 300px;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: background 0.2s;
  position: relative;

  &:hover {
    background: #f5f7fa;
  }

  &.unread {
    background: #ecf5ff;
  }
}

.notification-avatar {
  position: relative;
  flex-shrink: 0;

  .type-icon {
    position: absolute;
    bottom: -2px;
    right: -2px;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    border: 2px solid #fff;

    &.like {
      background: #F56C6C;
    }

    &.comment {
      background: #409EFF;
    }

    &.forward {
      background: #67C23A;
    }

    &.follow {
      background: #E6A23C;
    }

    &.mention {
      background: #909399;
    }
  }
}

.notification-content {
  flex: 1;
  min-width: 0;

  .notification-text {
    font-size: 14px;
    line-height: 1.5;
    color: #303133;
    margin-bottom: 4px;

    .sender-name {
      font-weight: 600;
    }

    .action-text {
      color: #606266;
    }
  }

  .notification-summary {
    font-size: 13px;
    color: #909399;
    margin-bottom: 4px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .notification-time {
    font-size: 12px;
    color: #c0c4cc;
  }
}

.unread-dot {
  position: absolute;
  top: 20px;
  right: 16px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #F56C6C;
}

.load-more {
  text-align: center;
  padding: 30px 0;

  .no-more {
    color: #909399;
    font-size: 14px;
  }
}
</style>
