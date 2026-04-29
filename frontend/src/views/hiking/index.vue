<template>
  <div class="hiking-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">徒步路线推荐</h1>
        <p class="page-desc">探索自然，发现精彩路线</p>

        <!-- 搜索和筛选 -->
        <div class="filter-bar">
          <el-input
            v-model="searchQuery"
            placeholder="搜索路线名称或地点"
            class="search-input"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>

          <el-select v-model="filterDifficulty" placeholder="难度等级" clearable class="filter-item">
            <el-option label="简单" value="easy">
              <el-tag size="small" type="success">简单</el-tag>
            </el-option>
            <el-option label="中等" value="medium">
              <el-tag size="small" type="warning">中等</el-tag>
            </el-option>
            <el-option label="困难" value="hard">
              <el-tag size="small" type="danger">困难</el-tag>
            </el-option>
          </el-select>

          <el-select v-model="filterLocation" placeholder="地区" clearable class="filter-item">
            <el-option
              v-for="loc in locations"
              :key="loc"
              :label="loc"
              :value="loc"
            />
          </el-select>

          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>
      </div>

      <!-- 统计数据 -->
      <div class="stats-bar">
        <div class="stat-item">
          <div class="stat-value">{{ stats.totalRoutes }}</div>
          <div class="stat-label">推荐路线</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.totalDistance }}</div>
          <div class="stat-label">总里程(km)</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.totalHikers }}</div>
          <div class="stat-label">徒步人次</div>
        </div>
      </div>
    </div>

    <!-- 路线列表 -->
    <div class="route-list">
      <el-row :gutter="20">
        <el-col
          v-for="route in routeList"
          :key="route.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <div class="route-card" @click="goToDetail(route.id)">
            <div class="route-image">
              <el-image
                :src="route.coverImage"
                fit="cover"
                class="cover-img"
              >
                <template #error>
                  <div class="image-placeholder">
                    <el-icon :size="40"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="difficulty-badge" :class="route.difficulty">
                {{ difficultyText(route.difficulty) }}
              </div>
              <div v-if="route.isHot" class="hot-badge">热门</div>
            </div>

            <div class="route-info">
              <h3 class="route-name">{{ route.name }}</h3>
              <p class="route-location">
                <el-icon><Location /></el-icon>
                {{ route.location }}
              </p>

              <div class="route-stats">
                <span class="stat">
                  <el-icon><Odometer /></el-icon>
                  {{ route.distance }}km
                </span>
                <span class="stat">
                  <el-icon><Timer /></el-icon>
                  {{ route.duration }}h
                </span>
                <span class="stat">
                  <el-icon><TrendCharts /></el-icon>
                  {{ route.elevationGain }}m
                </span>
              </div>

              <div class="route-tags">
                <el-tag
                  v-for="tag in route.tags"
                  :key="tag"
                  size="small"
                  effect="plain"
                >
                  {{ tag }}
                </el-tag>
              </div>

              <div class="route-footer">
                <div class="rating">
                  <el-rate
                    :model-value="route.rating"
                    disabled
                    show-score
                    text-color="#ff9900"
                  />
                  <span class="review-count">({{ route.reviewCount }}条评价)</span>
                </div>
                <el-button type="primary" size="small" text>
                  查看详情
                  <el-icon class="el-icon--right"><ArrowRight /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty
        v-if="routeList.length === 0 && !loading"
        description="暂无符合条件的路线"
      />

      <!-- 加载更多 -->
      <div v-if="routeList.length > 0" class="load-more">
        <el-button
          v-if="hasMore"
          :loading="loading"
          @click="loadMore"
        >
          加载更多
        </el-button>
        <p v-else class="no-more">没有更多数据了</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHikingRoutes } from '@/api/hiking'

const router = useRouter()

// 筛选条件
const searchQuery = ref('')
const filterDifficulty = ref('')
const filterLocation = ref('')

// 数据状态
const routeList = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(12)
const hasMore = ref(true)

// 地区选项
const locations = ref([
  '北京',
  '上海',
  '杭州',
  '成都',
  '西安',
  '云南',
  '西藏',
  '新疆'
])

// 统计数据
const stats = ref({
  totalRoutes: 128,
  totalDistance: 2560,
  totalHikers: 15800
})

// 难度等级文本
const difficultyText = (difficulty) => {
  const map = {
    easy: '简单',
    medium: '中等',
    hard: '困难'
  }
  return map[difficulty] || difficulty
}

// 获取路线列表
const fetchRoutes = async (isLoadMore = false) => {
  if (loading.value) return

  loading.value = true
  try {
    const params = {
      page: isLoadMore ? page.value : 1,
      pageSize: pageSize.value,
      keyword: searchQuery.value,
      difficulty: filterDifficulty.value,
      location: filterLocation.value
    }

    // 模拟数据（实际项目中从API获取）
    // const { data } = await getHikingRoutes(params)

    // 模拟数据
    await new Promise(resolve => setTimeout(resolve, 500))

    const mockData = generateMockData(isLoadMore ? page.value : 1)

    if (isLoadMore) {
      routeList.value.push(...mockData)
    } else {
      routeList.value = mockData
    }

    hasMore.value = mockData.length === pageSize.value
    if (isLoadMore) {
      page.value++
    }
  } catch (error) {
    ElMessage.error('获取路线列表失败')
  } finally {
    loading.value = false
  }
}

// 生成模拟数据
const generateMockData = (currentPage) => {
  const difficulties = ['easy', 'medium', 'hard']
  const tagsPool = ['风景优美', '适合新手', '亲子友好', '挑战性强', '历史文化', '摄影圣地', '避暑胜地', '红叶观赏']

  const routes = [
    {
      id: 1,
      name: '香山红叶徒步路线',
      location: '北京',
      coverImage: 'https://picsum.photos/400/300?random=1',
      difficulty: 'easy',
      distance: 8.5,
      duration: 3,
      elevationGain: 450,
      rating: 4.8,
      reviewCount: 256,
      tags: ['风景优美', '红叶观赏'],
      isHot: true
    },
    {
      id: 2,
      name: '灵山大峡谷穿越',
      location: '北京',
      coverImage: 'https://picsum.photos/400/300?random=2',
      difficulty: 'hard',
      distance: 18.2,
      duration: 8,
      elevationGain: 1200,
      rating: 4.9,
      reviewCount: 128,
      tags: ['挑战性强', '风景优美'],
      isHot: true
    },
    {
      id: 3,
      name: '西湖环湖步道',
      location: '杭州',
      coverImage: 'https://picsum.photos/400/300?random=3',
      difficulty: 'easy',
      distance: 12.0,
      duration: 4,
      elevationGain: 80,
      rating: 4.7,
      reviewCount: 512,
      tags: ['适合新手', '亲子友好'],
      isHot: false
    },
    {
      id: 4,
      name: '四姑娘山大峰攀登',
      location: '四川',
      coverImage: 'https://picsum.photos/400/300?random=4',
      difficulty: 'hard',
      distance: 28.5,
      duration: 12,
      elevationGain: 2200,
      rating: 4.9,
      reviewCount: 89,
      tags: ['挑战性强', '摄影圣地'],
      isHot: true
    },
    {
      id: 5,
      name: '莫干山竹林小径',
      location: '浙江',
      coverImage: 'https://picsum.photos/400/300?random=5',
      difficulty: 'medium',
      distance: 10.5,
      duration: 4.5,
      elevationGain: 380,
      rating: 4.6,
      reviewCount: 334,
      tags: ['避暑胜地', '亲子友好'],
      isHot: false
    },
    {
      id: 6,
      name: '泰山经典登山路线',
      location: '山东',
      coverImage: 'https://picsum.photos/400/300?random=6',
      difficulty: 'medium',
      distance: 9.5,
      duration: 6,
      elevationGain: 1400,
      rating: 4.8,
      reviewCount: 892,
      tags: ['历史文化', '挑战性强'],
      isHot: true
    },
    {
      id: 7,
      name: '喀纳斯湖环湖徒步',
      location: '新疆',
      coverImage: 'https://picsum.photos/400/300?random=7',
      difficulty: 'medium',
      distance: 25.0,
      duration: 10,
      elevationGain: 600,
      rating: 4.9,
      reviewCount: 156,
      tags: ['风景优美', '摄影圣地'],
      isHot: true
    },
    {
      id: 8,
      name: '黄山云谷寺步道',
      location: '安徽',
      coverImage: 'https://picsum.photos/400/300?random=8',
      difficulty: 'medium',
      distance: 15.0,
      duration: 7,
      elevationGain: 1000,
      rating: 4.8,
      reviewCount: 445,
      tags: ['风景优美', '摄影圣地'],
      isHot: true
    }
  ]

  // 根据页码生成更多数据
  const startIndex = (currentPage - 1) * pageSize.value
  const result = []

  for (let i = 0; i < pageSize.value; i++) {
    const index = (startIndex + i) % routes.length
    const baseRoute = routes[index]
    result.push({
      ...baseRoute,
      id: startIndex + i + 1,
      name: baseRoute.name + (currentPage > 1 ? ` - ${currentPage}` : ''),
      coverImage: `https://picsum.photos/400/300?random=${startIndex + i + 1}`
    })
  }

  return result
}

// 搜索
const handleSearch = () => {
  page.value = 1
  fetchRoutes(false)
}

// 加载更多
const loadMore = () => {
  fetchRoutes(true)
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/hiking/routes/${id}`)
}

onMounted(() => {
  fetchRoutes()
})
</script>

<style lang="scss" scoped>
.hiking-page {
  padding: 0 0 40px;
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  color: #fff;
  margin-bottom: 30px;

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    text-align: center;
  }

  .page-title {
    font-size: 36px;
    font-weight: 600;
    margin-bottom: 8px;
  }

  .page-desc {
    font-size: 16px;
    opacity: 0.9;
    margin-bottom: 24px;
  }
}

.filter-bar {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;

  .search-input {
    width: 300px;
  }

  .filter-item {
    width: 140px;
  }
}

.stats-bar {
  max-width: 600px;
  margin: 30px auto 0;
  display: flex;
  justify-content: space-around;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);

  .stat-item {
    text-align: center;

    .stat-value {
      font-size: 32px;
      font-weight: 600;
      margin-bottom: 4px;
    }

    .stat-label {
      font-size: 14px;
      opacity: 0.8;
    }
  }
}

.route-list {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.route-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);

    .cover-img {
      transform: scale(1.05);
    }
  }
}

.route-image {
  position: relative;
  height: 180px;
  overflow: hidden;

  .cover-img {
    width: 100%;
    height: 100%;
    transition: transform 0.5s ease;
  }

  .image-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f5f5;
    color: #909399;
  }

  .difficulty-badge {
    position: absolute;
    top: 12px;
    left: 12px;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
    color: #fff;

    &.easy {
      background: #67C23A;
    }

    &.medium {
      background: #E6A23C;
    }

    &.hard {
      background: #F56C6C;
    }
  }

  .hot-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    padding: 4px 12px;
    background: #F56C6C;
    color: #fff;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
  }
}

.route-info {
  padding: 16px;

  .route-name {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
    line-height: 1.4;
  }

  .route-location {
    font-size: 13px;
    color: #909399;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .route-stats {
    display: flex;
    gap: 16px;
    margin-bottom: 12px;

    .stat {
      font-size: 13px;
      color: #606266;
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }

  .route-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    margin-bottom: 12px;
  }

  .route-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 12px;
    border-top: 1px solid #ebeef5;

    .rating {
      display: flex;
      align-items: center;
      gap: 8px;

      .review-count {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}

.load-more {
  text-align: center;
  padding: 40px 0;

  .no-more {
    color: #909399;
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 30px 16px;

    .page-title {
      font-size: 28px;
    }
  }

  .filter-bar {
    .search-input {
      width: 100%;
    }

    .filter-item {
      width: calc(50% - 6px);
    }
  }

  .stats-bar {
    margin-top: 20px;

    .stat-item {
      .stat-value {
        font-size: 24px;
      }
    }
  }
}
</style>
