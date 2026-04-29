<template>
  <div class="hiking-detail">
    <!-- 页面头部导航 -->
    <div class="detail-header">
      <div class="header-inner">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/hiking' }">徒步路线</el-breadcrumb-item>
          <el-breadcrumb-item>{{ routeInfo.name }}</el-breadcrumb-item>
        </el-breadcrumb>

        <div class="header-actions">
          <el-button
            :type="isFavorited ? 'danger' : 'default'"
            :icon="isFavorited ? StarFilled : Star"
            @click="toggleFavorite"
          >
            {{ isFavorited ? '已收藏' : '收藏' }}
          </el-button>
          <el-button icon="Share">分享</el-button>
        </div>
      </div>
    </div>

    <div class="detail-content">
      <!-- 路线基本信息 -->
      <div class="info-section">
        <el-row :gutter="30">
          <el-col :xs="24" :lg="14">
            <div class="route-gallery">
              <el-carousel height="400px" indicator-position="outside">
                <el-carousel-item v-for="(img, index) in routeInfo.images" :key="index">
                  <el-image :src="img" fit="cover" class="gallery-img" />
                </el-carousel-item>
              </el-carousel>
            </div>
          </el-col>

          <el-col :xs="24" :lg="10">
            <div class="route-basic-info">
              <div class="title-section">
                <h1 class="route-title">{{ routeInfo.name }}</h1>
                <div class="route-tags">
                  <el-tag :type="difficultyType(routeInfo.difficulty)" size="large">
                    {{ difficultyText(routeInfo.difficulty) }}
                  </el-tag>
                  <el-tag v-if="routeInfo.isHot" type="danger" size="large">热门</el-tag>
                </div>
              </div>

              <div class="rating-section">
                <el-rate
                  v-model="routeInfo.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                />
                <span class="review-count">{{ routeInfo.reviewCount }}条评价</span>
              </div>

              <div class="info-list">
                <div class="info-item">
                  <el-icon><Location /></el-icon>
                  <span class="label">位置：</span>
                  <span class="value">{{ routeInfo.location }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Odometer /></el-icon>
                  <span class="label">距离：</span>
                  <span class="value">{{ routeInfo.distance }} 公里</span>
                </div>
                <div class="info-item">
                  <el-icon><Timer /></el-icon>
                  <span class="label">预计用时：</span>
                  <span class="value">{{ routeInfo.duration }} 小时</span>
                </div>
                <div class="info-item">
                  <el-icon><TrendCharts /></el-icon>
                  <span class="label">累计爬升：</span>
                  <span class="value">{{ routeInfo.elevationGain }} 米</span>
                </div>
                <div class="info-item">
                  <el-icon><TopRight /></el-icon>
                  <span class="label">最高海拔：</span>
                  <span class="value">{{ routeInfo.maxElevation }} 米</span>
                </div>
                <div class="info-item">
                  <el-icon><Calendar /></el-icon>
                  <span class="label">最佳季节：</span>
                  <span class="value">{{ routeInfo.bestSeason }}</span>
                </div>
              </div>

              <div class="description">
                <h3>路线简介</h3>
                <p>{{ routeInfo.description }}</p>
              </div>

              <div class="feature-tags">
                <h3>路线特色</h3>
                <div class="tags-list">
                  <el-tag
                    v-for="tag in routeInfo.tags"
                    :key="tag"
                    effect="dark"
                    size="large"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 地图和轨迹 -->
      <div class="map-section">
        <h2 class="section-title">
          <el-icon><MapLocation /></el-icon>
          路线地图
        </h2>
        <MapContainer
          ref="mapRef"
          :height="500"
          :track="routeTrack"
          :markers="routeMarkers"
          :show-track="true"
          @marker-click="onMarkerClick"
          @ready="onMapReady"
        />

        <!-- 轨迹控制 -->
        <div class="track-controls">
          <el-button-group>
            <el-button :icon="VideoPlay" @click="playTrack">播放轨迹</el-button>
            <el-button :icon="Refresh" @click="resetTrack">重置视角</el-button>
          </el-button-group>
          <el-switch
            v-model="show3D"
            active-text="3D视图"
            @change="toggle3DView"
          />
        </div>

        <!-- 海拔剖面图 -->
        <div class="elevation-chart">
          <h3>海拔剖面</h3>
          <div ref="elevationChart" class="chart-container"></div>
        </div>
      </div>

      <!-- 行程详情 -->
      <div class="itinerary-section">
        <h2 class="section-title">
          <el-icon><List /></el-icon>
          行程安排
        </h2>
        <el-timeline>
          <el-timeline-item
            v-for="(point, index) in itinerary"
            :key="index"
            :type="point.type"
            :icon="point.icon"
            :timestamp="point.time"
            placement="top"
          >
            <el-card>
              <template #header>
                <div class="itinerary-header">
                  <span class="point-name">{{ point.name }}</span>
                  <el-tag size="small" :type="point.tagType">{{ point.tag }}</el-tag>
                </div>
              </template>
              <div class="itinerary-content">
                <p>{{ point.description }}</p>
                <div v-if="point.images" class="point-images">
                  <el-image
                    v-for="(img, imgIndex) in point.images"
                    :key="imgIndex"
                    :src="img"
                    :preview-src-list="point.images"
                    fit="cover"
                    class="point-img"
                  />
                </div>
                <div class="point-info">
                  <span><el-icon><Odometer /></el-icon> {{ point.distance }}km</span>
                  <span><el-icon><Timer /></el-icon> {{ point.duration }}min</span>
                  <span><el-icon><TopRight /></el-icon> {{ point.elevation }}m</span>
                </div>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>

      <!-- 装备建议 -->
      <div class="equipment-section">
        <h2 class="section-title">
          <el-icon><Suitcase /></el-icon>
          装备建议
        </h2>
        <el-row :gutter="20">
          <el-col :xs="24" :md="8" v-for="(category, index) in equipmentList" :key="index">
            <el-card class="equipment-card">
              <template #header>
                <div class="equipment-header">
                  <el-icon :size="24"><component :is="category.icon" /></el-icon>
                  <span>{{ category.name }}</span>
                </div>
              </template>
              <ul class="equipment-list">
                <li v-for="item in category.items" :key="item">
                  <el-icon><Check /></el-icon>
                  {{ item }}
                </li>
              </ul>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 注意事项 -->
      <div class="warning-section">
        <h2 class="section-title">
          <el-icon><Warning /></el-icon>
          注意事项
        </h2>
        <el-alert
          v-for="(warning, index) in warnings"
          :key="index"
          :title="warning.title"
          :type="warning.type"
          :description="warning.content"
          show-icon
          :closable="false"
          class="warning-item"
        />
      </div>

      <!-- 评价区域 -->
      <div class="reviews-section">
        <h2 class="section-title">
          <el-icon><ChatDotRound /></el-icon>
          用户评价
        </h2>

        <div class="review-summary">
          <div class="rating-big">{{ routeInfo.rating }}</div>
          <div class="rating-detail">
            <el-rate v-model="routeInfo.rating" disabled />
            <p>{{ routeInfo.reviewCount }}条评价</p>
          </div>
        </div>

        <div class="review-list">
          <div v-for="review in reviews" :key="review.id" class="review-item">
            <div class="reviewer-info">
              <el-avatar :src="review.avatar" :size="48" />
              <div class="reviewer-meta">
                <span class="reviewer-name">{{ review.username }}</span>
                <span class="review-date">{{ review.date }}</span>
              </div>
            </div>
            <div class="review-content">
              <el-rate v-model="review.rating" disabled />
              <p>{{ review.content }}</p>
              <div v-if="review.images" class="review-images">
                <el-image
                  v-for="(img, imgIndex) in review.images"
                  :key="imgIndex"
                  :src="img"
                  :preview-src-list="review.images"
                  fit="cover"
                  class="review-img"
                />
              </div>
            </div>
          </div>
        </div>

        <div class="load-more-reviews">
          <el-button type="primary" plain @click="loadMoreReviews">加载更多评价</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Star, StarFilled, Share, Location, Odometer, Timer,
  TrendCharts, TopRight, Calendar, MapLocation, List,
  VideoPlay, Refresh, Suitcase, Warning, ChatDotRound,
  Check, Flag, Food, Place, Compass
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import MapContainer from '@/components/MapContainer.vue'
import { getRouteDetail, getRouteTrack, favoriteRoute, unfavoriteRoute, getRouteReviews } from '@/api/hiking'

const route = useRoute()
const router = useRouter()
const routeId = route.params.id

// 地图相关
const mapRef = ref(null)
const show3D = ref(false)

// 数据状态
const routeInfo = ref({
  name: '',
  difficulty: 'easy',
  rating: 5,
  reviewCount: 0,
  location: '',
  distance: 0,
  duration: 0,
  elevationGain: 0,
  maxElevation: 0,
  bestSeason: '',
  description: '',
  images: [],
  tags: []
})

const isFavorited = ref(false)
const routeTrack = ref([])
const routeMarkers = ref([])
const itinerary = ref([])
const equipmentList = ref([])
const warnings = ref([])
const reviews = ref([])

// 难度等级映射
const difficultyMap = {
  easy: { text: '简单', type: 'success' },
  medium: { text: '中等', type: 'warning' },
  hard: { text: '困难', type: 'danger' }
}

const difficultyText = (difficulty) => difficultyMap[difficulty]?.text || difficulty
const difficultyType = (difficulty) => difficultyMap[difficulty]?.type || 'info'

// 获取路线详情
const fetchRouteDetail = async () => {
  try {
    // 模拟数据（实际项目中从API获取）
    // const { data } = await getRouteDetail(routeId)

    // 模拟数据
    routeInfo.value = {
      id: routeId,
      name: '香山红叶徒步路线',
      difficulty: 'easy',
      rating: 4.8,
      reviewCount: 256,
      location: '北京市海淀区香山公园',
      distance: 8.5,
      duration: 3,
      elevationGain: 450,
      maxElevation: 575,
      bestSeason: '9月-11月',
      description: '香山红叶徒步路线是北京最经典的秋季徒步路线之一。路线从香山公园东门出发，途经双清别墅、香炉峰，最后到达鬼见愁。全程约8.5公里，爬升450米，适合各个年龄段的徒步爱好者。每年10月中旬至11月初是最佳观赏期，满山的红叶美不胜收。',
      images: [
        'https://picsum.photos/800/600?random=1',
        'https://picsum.photos/800/600?random=2',
        'https://picsum.photos/800/600?random=3',
        'https://picsum.photos/800/600?random=4'
      ],
      tags: ['风景优美', '红叶观赏', '适合新手', '亲子友好'],
      isHot: true
    }

    // 模拟轨迹数据
    generateMockTrack()

    // 模拟行程数据
    generateMockItinerary()

    // 模拟装备数据
    generateMockEquipment()

    // 模拟注意事项
    generateMockWarnings()

    // 模拟评价
    generateMockReviews()

  } catch (error) {
    ElMessage.error('获取路线详情失败')
  }
}

// 生成模拟轨迹数据
const generateMockTrack = () => {
  // 生成香山附近的模拟轨迹点
  const baseLng = 116.185
  const baseLat = 39.992
  const track = []

  for (let i = 0; i <= 100; i++) {
    const progress = i / 100
    track.push([
      baseLng + progress * 0.01 + Math.sin(progress * Math.PI * 2) * 0.002,
      baseLat + progress * 0.008 + Math.cos(progress * Math.PI) * 0.003
    ])
  }

  routeTrack.value = track

  // 标记点
  routeMarkers.value = [
    { lng: track[0][0], lat: track[0][1], title: '起点：香山公园东门' },
    { lng: track[30][0], lat: track[30][1], title: '双清别墅' },
    { lng: track[60][0], lat: track[60][1], title: '香炉峰' },
    { lng: track[100][0], lat: track[100][1], title: '终点：鬼见愁' }
  ]

  // 初始化海拔图
  nextTick(() => {
    initElevationChart(track)
  })
}

// 生成模拟行程
const generateMockItinerary = () => {
  itinerary.value = [
    {
      name: '香山公园东门',
      time: '08:00',
      type: 'primary',
      icon: Flag,
      tag: '起点',
      tagType: 'success',
      description: '从香山公园东门集合出发，购买门票（旺季10元，淡季5元）。建议提前在网上预约购票。',
      distance: 0,
      duration: 0,
      elevation: 60
    },
    {
      name: '双清别墅',
      time: '09:00',
      type: 'success',
      icon: Place,
      tag: '景点',
      tagType: 'primary',
      description: '双清别墅是香山著名景点，曾是毛主席居住过的地方。这里环境清幽，有双清池和六角亭。可以稍作休息，参观历史遗迹。',
      distance: 2.5,
      duration: 60,
      elevation: 180,
      images: ['https://picsum.photos/200/150?random=10', 'https://picsum.photos/200/150?random=11']
    },
    {
      name: '休息点',
      time: '09:30',
      type: 'warning',
      icon: Food,
      tag: '补给',
      tagType: 'warning',
      description: '途中的休息点，有售卖饮料和简单食物的小卖部。建议补充水和能量。',
      distance: 3.5,
      duration: 30,
      elevation: 250
    },
    {
      name: '香炉峰',
      time: '10:30',
      type: 'success',
      icon: Compass,
      tag: '观景',
      tagType: 'primary',
      description: '香炉峰是香山的主峰，海拔575米。登上峰顶可以俯瞰整个北京城，视野开阔。秋季红叶满山，景色绝佳。',
      distance: 5.5,
      duration: 120,
      elevation: 450,
      images: ['https://picsum.photos/200/150?random=12']
    },
    {
      name: '鬼见愁',
      time: '11:30',
      type: 'danger',
      icon: Flag,
      tag: '终点',
      tagType: 'danger',
      description: '鬼见愁是香山的最高峰，因山势险峻而得名。这里是本次徒步的终点，可以在此拍照留念后下山。',
      distance: 8.5,
      duration: 180,
      elevation: 575
    }
  ]
}

// 生成装备建议
const generateMockEquipment = () => {
  equipmentList.value = [
    {
      name: '必备装备',
      icon: Suitcase,
      items: ['登山鞋/运动鞋', '双肩背包（20-30L）', '速干衣裤', '防晒霜和墨镜', '登山杖', '雨具']
    },
    {
      name: '食品饮料',
      icon: Food,
      items: ['饮用水（2L以上）', '能量棒/巧克力', '水果', '简易午餐', '运动饮料', '垃圾袋']
    },
    {
      name: '安全装备',
      icon: Warning,
      items: ['急救包', '头灯/手电筒', '哨子', '手机充电宝', '身份证', '现金']
    }
  ]
}

// 生成注意事项
const generateMockWarnings = () => {
  warnings.value = [
    {
      title: '天气注意',
      type: 'warning',
      content: '秋季香山天气多变，建议出发前查看天气预报。如遇大风、雷雨天气，请取消行程。'
    },
    {
      title: '人流高峰',
      type: 'info',
      content: '红叶季（10月中旬-11月初）为高峰期，建议错峰出行，工作日人流相对较少。'
    },
    {
      title: '安全提示',
      type: 'error',
      content: '部分路段较陡峭，请注意脚下安全。老人儿童需在成人陪同下登山。建议购买户外意外险。'
    }
  ]
}

// 生成模拟评价
const generateMockReviews = () => {
  reviews.value = [
    {
      id: 1,
      username: '徒步达人小王',
      avatar: 'https://picsum.photos/100/100?random=20',
      date: '2024-10-15',
      rating: 5,
      content: '非常棒的路线！红叶季的风景真的太美了，推荐大家一定要去看看。路线难度适中，新手也能完成。',
      images: ['https://picsum.photos/200/200?random=21', 'https://picsum.photos/200/200?random=22']
    },
    {
      id: 2,
      username: '户外爱好者',
      avatar: 'https://picsum.photos/100/100?random=23',
      date: '2024-10-12',
      rating: 4,
      content: '路线不错，就是周末人太多了。建议工作日去，人少风景好。双清别墅那段路比较平缓，适合休息。',
    },
    {
      id: 3,
      username: '亲子游妈妈',
      avatar: 'https://picsum.photos/100/100?random=24',
      date: '2024-10-08',
      rating: 5,
      content: '带着孩子一起走的，8岁的孩子也能坚持下来。一路上给孩子讲解植物知识，很有意义的一次徒步。',
      images: ['https://picsum.photos/200/200?random=25']
    }
  ]
}

// 初始化海拔图表
const initElevationChart = (track) => {
  const chartDom = document.querySelector('.elevation-chart .chart-container')
  if (!chartDom) return

  const chart = echarts.init(chartDom)

  // 生成海拔数据（模拟）
  const xData = track.map((_, i) => `${(i * 0.085).toFixed(1)}km`)
  const yData = track.map((_, i) => {
    const progress = i / track.length
    return Math.round(60 + progress * 515 + Math.sin(progress * Math.PI * 4) * 50)
  })

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>海拔: {c}m'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xData.filter((_, i) => i % 10 === 0),
      axisLabel: {
        interval: 2
      }
    },
    yAxis: {
      type: 'value',
      name: '海拔(m)',
      min: 0
    },
    series: [
      {
        name: '海拔',
        type: 'line',
        smooth: true,
        symbol: 'none',
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(64,158,255,0.4)' },
              { offset: 1, color: 'rgba(64,158,255,0.05)' }
            ]
          }
        },
        lineStyle: {
          color: '#409EFF',
          width: 3
        },
        data: yData.filter((_, i) => i % 10 === 0)
      }
    ]
  }

  chart.setOption(option)

  window.addEventListener('resize', () => chart.resize())
}

// 收藏/取消收藏
const toggleFavorite = async () => {
  try {
    if (isFavorited.value) {
      await unfavoriteRoute(routeId)
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await favoriteRoute(routeId)
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 地图事件
const onMapReady = (map) => {
  console.log('地图加载完成', map)
}

const onMarkerClick = (marker) => {
  ElMessage.info(marker.title)
}

// 轨迹播放
const playTrack = () => {
  if (mapRef.value) {
    const success = mapRef.value.playTrackAnimation()
    if (success) {
      ElMessage.success('开始播放轨迹动画')
    } else {
      ElMessage.warning('轨迹播放失败，请检查地图是否加载完成')
    }
  }
}

// 重置视角
const resetTrack = () => {
  if (mapRef.value) {
    mapRef.value.setCenter(routeTrack.value[0])
    mapRef.value.setZoom(13)
  }
}

// 切换3D视图
const toggle3DView = (val) => {
  if (mapRef.value) {
    const success = mapRef.value.toggle3D(val)
    if (success) {
      ElMessage.success(val ? '已切换到3D视图' : '已切换到2D视图')
    } else {
      ElMessage.warning('地图未加载完成或当前模式不支持3D视图')
    }
  }
}

// 加载更多评价
const loadMoreReviews = () => {
  ElMessage.info('加载更多评价')
}

onMounted(() => {
  fetchRouteDetail()
})
</script>

<style lang="scss" scoped>
.hiking-detail {
  background: #f5f7fa;
  min-height: 100vh;
}

.detail-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 16px 0;
  position: sticky;
  top: 0;
  z-index: 100;

  .header-inner {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .header-actions {
    display: flex;
    gap: 12px;
  }
}

.detail-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

// 基本信息区域
.info-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
}

.route-gallery {
  .gallery-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.route-basic-info {
  .title-section {
    margin-bottom: 16px;

    .route-title {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 12px;
    }

    .route-tags {
      display: flex;
      gap: 8px;
    }
  }

  .rating-section {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid #ebeef5;

    .review-count {
      color: #909399;
      font-size: 14px;
    }
  }

  .info-list {
    margin-bottom: 20px;

    .info-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 0;
      color: #606266;

      .el-icon {
        color: #409EFF;
      }

      .label {
        color: #909399;
      }

      .value {
        font-weight: 500;
        color: #303133;
      }
    }
  }

  .description {
    margin-bottom: 20px;

    h3 {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 12px;
    }

    p {
      color: #606266;
      line-height: 1.8;
    }
  }

  .feature-tags {
    h3 {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 12px;
    }

    .tags-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }
  }
}

// 地图区域
.map-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
}

.track-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.elevation-chart {
  margin-top: 24px;

  h3 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
  }

  .chart-container {
    height: 200px;
  }
}

// 行程区域
.itinerary-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
}

.itinerary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .point-name {
    font-weight: 600;
    font-size: 16px;
  }
}

.itinerary-content {
  p {
    color: #606266;
    line-height: 1.6;
    margin-bottom: 12px;
  }

  .point-images {
    display: flex;
    gap: 8px;
    margin-bottom: 12px;

    .point-img {
      width: 100px;
      height: 75px;
      border-radius: 4px;
      cursor: pointer;
    }
  }

  .point-info {
    display: flex;
    gap: 16px;
    color: #909399;
    font-size: 13px;

    span {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}

// 装备区域
.equipment-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
}

.equipment-card {
  margin-bottom: 16px;

  .equipment-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    font-size: 16px;
  }
}

.equipment-list {
  list-style: none;
  padding: 0;
  margin: 0;

  li {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 0;
    color: #606266;

    .el-icon {
      color: #67C23A;
    }
  }
}

// 注意事项区域
.warning-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
}

.warning-item {
  margin-bottom: 12px;
}

// 评价区域
.reviews-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
}

.review-summary {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 24px;

  .rating-big {
    font-size: 48px;
    font-weight: 600;
    color: #ff9900;
  }

  .rating-detail {
    p {
      margin-top: 4px;
      color: #909399;
    }
  }
}

.review-list {
  .review-item {
    padding: 20px 0;
    border-bottom: 1px solid #ebeef5;

    &:last-child {
      border-bottom: none;
    }
  }

  .reviewer-info {
    display: flex;
    gap: 12px;
    margin-bottom: 12px;

    .reviewer-meta {
      display: flex;
      flex-direction: column;
      justify-content: center;

      .reviewer-name {
        font-weight: 500;
        color: #303133;
      }

      .review-date {
        font-size: 13px;
        color: #909399;
      }
    }
  }

  .review-content {
    p {
      color: #606266;
      line-height: 1.6;
      margin: 8px 0;
    }

    .review-images {
      display: flex;
      gap: 8px;
      margin-top: 12px;

      .review-img {
        width: 100px;
        height: 100px;
        border-radius: 4px;
        cursor: pointer;
      }
    }
  }
}

.load-more-reviews {
  text-align: center;
  padding-top: 20px;
}

@media (max-width: 768px) {
  .detail-header {
    .header-inner {
      flex-direction: column;
      gap: 12px;
      align-items: flex-start;
    }
  }

  .route-basic-info {
    margin-top: 20px;
  }

  .track-controls {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
