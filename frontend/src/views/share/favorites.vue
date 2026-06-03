<template>
  <div class="favorites-page">
    <div class="page-container">
      <!-- 页面头部 -->
      <div class="page-header">
        <el-button text @click="$router.push('/share')">
          <el-icon><ArrowLeft /></el-icon> 返回朋友圈
        </el-button>
        <h2 class="page-title">我的收藏</h2>
      </div>

      <!-- 收藏列表 -->
      <div class="favorites-list">
        <template v-if="collectList.length > 0">
          <ShareCard
            v-for="item in collectList"
            :key="item.shareId"
            :share="item"
            @like="handleLikeUpdate"
            @collect="handleCollectUpdate"
            @refresh="fetchCollects"
          />
        </template>
        <el-empty v-else-if="!loading" description="暂无收藏内容">
          <el-button type="primary" @click="$router.push('/share')">去逛逛</el-button>
        </el-empty>

        <div v-if="collectList.length > 0" class="load-more">
          <el-button
            v-if="hasMore"
            :loading="loading"
            @click="loadMore"
          >
            加载更多
          </el-button>
          <span v-else class="no-more">没有更多收藏了</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyCollects } from '@/api/share'
import ShareCard from '@/components/share-card.vue'

const collectList = ref([])
const loading = ref(false)
const page = ref(1)
const hasMore = ref(true)

// 获取收藏列表
const fetchCollects = async (isLoadMore = false) => {
  if (loading.value) return
  loading.value = true

  try {
    const res = await getMyCollects({
      pageNum: isLoadMore ? page.value : 1,
      pageSize: 10
    })

    const records = res.data.records || []
    if (isLoadMore) {
      collectList.value.push(...records)
      page.value++
    } else {
      collectList.value = records
      page.value = 2
    }
    hasMore.value = records.length === 10
  } catch (error) {
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

// 加载更多
const loadMore = () => fetchCollects(true)

// 点赞更新
const handleLikeUpdate = ({ shareId, isLiked, likeCount }) => {
  const item = collectList.value.find(s => s.shareId === shareId)
  if (item) {
    item.isLiked = isLiked
    item.likeCount = likeCount
  }
}

// 收藏更新（取消收藏时从列表移除）
const handleCollectUpdate = ({ shareId, isCollected }) => {
  if (!isCollected) {
    collectList.value = collectList.value.filter(s => s.shareId !== shareId)
  }
}

onMounted(() => {
  fetchCollects()
})
</script>

<style lang="scss" scoped>
.favorites-page {
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
  }
}

.favorites-list {
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
</style>
