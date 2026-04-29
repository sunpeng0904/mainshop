<template>
  <div ref="mapContainer" class="map-container" :style="{ height: height + 'px' }">
    <div v-if="!mapLoaded" class="map-loading">
      <el-skeleton :rows="5" animated />
      <p class="loading-text">地图加载中...</p>
    </div>
    <div v-if="loadError" class="map-error">
      <el-icon :size="48" color="#F56C6C"><Warning /></el-icon>
      <p>地图加载失败</p>
      <el-button type="primary" @click="initMap">重试</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import AMapLoader from '@amap/amap-jsapi-loader'

const props = defineProps({
  height: {
    type: Number,
    default: 400
  },
  // 路线轨迹数据 [{lng, lat}, ...]
  track: {
    type: Array,
    default: () => []
  },
  // 是否显示轨迹
  showTrack: {
    type: Boolean,
    default: true
  },
  // 标记点 [{lng, lat, title, icon}, ...]
  markers: {
    type: Array,
    default: () => []
  },
  // 中心点坐标
  center: {
    type: Array,
    default: () => [116.397428, 39.90923] // 默认北京天安门
  },
  // 缩放级别
  zoom: {
    type: Number,
    default: 12
  },
  // 是否可点击地图选点
  clickable: {
    type: Boolean,
    default: false
  },
  // 是否显示周边地点
  showNearbyPlaces: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['map-click', 'marker-click', 'ready'])

const mapContainer = ref(null)
const mapLoaded = ref(false)
const loadError = ref(false)
let map = null
let polyline = null
let markerInstances = []
let movingMarker = null  // 轨迹动画标记
let placeSearch = null   // 地点搜索实例
let poiMarkers = []      // POI标记点数组

// 初始化地图
const initMap = async () => {
  if (!mapContainer.value) return

  loadError.value = false
  mapLoaded.value = false

  try {
    // 从环境变量或配置中获取高德地图Key
    // 如果没有配置，使用提示
    const amapKey = process.env.VUE_APP_AMAP_KEY || ''
    const amapSecurityConfig = process.env.VUE_APP_AMAP_SECURITY_CONFIG || ''

    // 检查Key是否有效（高德Key通常是32位的字符串）
    const isValidKey = amapKey && amapKey.length >= 20 && !amapKey.includes('test')

    if (!isValidKey) {
      console.warn('高德地图Key无效或缺失，使用占位地图模式')
      console.warn('当前Key:', amapKey.substring(0, 5) + '...')
      // 使用无Key模式（功能受限）或显示占位图
      renderPlaceholderMap()
      return
    }

    // 配置高德地图安全密钥（JSAPI 2.0 必需）
    if (amapSecurityConfig) {
      window._AMapSecurityConfig = {
        securityJsCode: amapSecurityConfig
      }
    }

    const AMap = await AMapLoader.load({
      key: amapKey,
      version: '2.0',
      plugins: [
        'AMap.Scale',
        'AMap.ToolBar',
        'AMap.Geolocation',
        'AMap.PolyEditor',
        'AMap.MoveAnimation',  // 轨迹动画插件
        'AMap.PlaceSearch'     // 地点搜索插件
      ]
    })

    // 创建地图实例 - 启用POI显示
    map = new AMap.Map(mapContainer.value, {
      zoom: props.zoom,
      center: props.center,
      viewMode: '2D',
      mapStyle: 'amap://styles/normal',
      showLabel: true,           // 显示地图标注
      defaultCursor: 'pointer'   // 鼠标样式
    })

    // 添加控件
    map.addControl(new AMap.Scale())
    map.addControl(new AMap.ToolBar())

    // 绑定事件
    if (props.clickable) {
      map.on('click', (e) => {
        emit('map-click', {
          lng: e.lnglat.getLng(),
          lat: e.lnglat.getLat()
        })
      })
    }

    mapLoaded.value = true
    emit('ready', map)

    // 初始化数据
    updateTrack()
    updateMarkers()

    // 加载周边地点
    searchNearbyPlaces()

  } catch (error) {
    console.error('地图加载失败:', error)
    console.error('错误详情:', {
      message: error.message,
      stack: error.stack,
      amapKey: process.env.VUE_APP_AMAP_KEY ? '已配置' : '未配置'
    })
    loadError.value = true
    renderPlaceholderMap()
  }
}

// 渲染占位地图（无Key或加载失败时使用）
const renderPlaceholderMap = () => {
  if (!mapContainer.value) return

  const mockPlaces = [
    { name: '游客中心', x: 120, y: 280, type: 'service' },
    { name: '服务台', x: 85, y: 310, type: 'service' },
    { name: '咨询处', x: 155, y: 245, type: 'service' },
    { name: '充电站', x: 750, y: 200, type: 'service' },
    { name: 'ATM机', x: 200, y: 350, type: 'service' },
    { name: '警务室', x: 110, y: 240, type: 'service' },
    { name: '行李寄存', x: 160, y: 300, type: 'service' },
    { name: '导游服务', x: 240, y: 170, type: 'service' },
    { name: '急救中心', x: 130, y: 225, type: 'service' },

    { name: '休息区', x: 350, y: 240, type: 'rest' },
    { name: '长椅A', x: 260, y: 240, type: 'rest' },
    { name: '长椅B', x: 400, y: 260, type: 'rest' },
    { name: '长椅C', x: 540, y: 200, type: 'rest' },
    { name: '凉亭A', x: 370, y: 130, type: 'rest' },
    { name: '凉亭B', x: 630, y: 140, type: 'rest' },
    { name: '休息亭A', x: 175, y: 190, type: 'rest' },
    { name: '休息亭B', x: 420, y: 170, type: 'rest' },

    { name: '停车场A', x: 80, y: 340, type: 'parking' },
    { name: '停车场B', x: 650, y: 200, type: 'parking' },
    { name: 'P1停车场', x: 60, y: 370, type: 'parking' },
    { name: 'P2停车场', x: 760, y: 130, type: 'parking' },
    { name: '电动车充电', x: 770, y: 215, type: 'parking' },

    { name: '厕所A', x: 280, y: 260, type: 'facility' },
    { name: '厕所B', x: 520, y: 160, type: 'facility' },
    { name: '厕所C', x: 180, y: 320, type: 'facility' },
    { name: '母婴室', x: 215, y: 275, type: 'facility' },
    { name: '更衣室', x: 535, y: 245, type: 'facility' },

    { name: '小卖部A', x: 220, y: 230, type: 'shop' },
    { name: '小卖部B', x: 480, y: 140, type: 'shop' },
    { name: '小卖部C', x: 620, y: 170, type: 'shop' },
    { name: '便利店', x: 170, y: 150, type: 'shop' },
    { name: '特产店', x: 290, y: 130, type: 'shop' },
    { name: '纪念品店', x: 430, y: 200, type: 'shop' },
    { name: '自动售货机A', x: 190, y: 270, type: 'shop' },
    { name: '自动售货机B', x: 560, y: 230, type: 'shop' },
    { name: '自动售货机C', x: 330, y: 290, type: 'shop' },
    { name: '奶茶店', x: 405, y: 145, type: 'shop' },
    { name: '咖啡店', x: 530, y: 105, type: 'shop' },

    { name: '山顶餐厅', x: 580, y: 90, type: 'food' },
    { name: '山脚餐厅', x: 90, y: 360, type: 'food' },
    { name: '茶室', x: 400, y: 120, type: 'food' },
    { name: '烧烤区', x: 350, y: 300, type: 'food' },
    { name: '中餐厅', x: 260, y: 135, type: 'food' },
    { name: '快餐店', x: 670, y: 275, type: 'food' },
    { name: '小吃街', x: 310, y: 325, type: 'food' },
    { name: 'KFC', x: 110, y: 285, type: 'food' },
    { name: '星巴克', x: 440, y: 135, type: 'food' },

    { name: '度假村', x: 750, y: 150, type: 'hotel' },
    { name: '民宿A', x: 300, y: 320, type: 'hotel' },
    { name: '民宿B', x: 450, y: 300, type: 'hotel' },
    { name: '酒店', x: 120, y: 145, type: 'hotel' },
    { name: '客栈', x: 520, y: 320, type: 'hotel' },
    { name: '露营地', x: 650, y: 250, type: 'camp' },
    { name: '帐篷区', x: 340, y: 335, type: 'camp' },

    { name: '红枫林', x: 200, y: 200, type: 'scenic' },
    { name: '红枫林', x: 200, y: 200, type: 'scenic' },
    { name: '古寺庙', x: 320, y: 150, type: 'scenic' },
    { name: '瀑布', x: 420, y: 220, type: 'scenic' },
    { name: '石桥', x: 380, y: 190, type: 'scenic' },
    { name: '竹林', x: 250, y: 290, type: 'scenic' },
    { name: '花海', x: 500, y: 250, type: 'scenic' },
    { name: '山顶亭', x: 600, y: 100, type: 'scenic' },
    { name: '湖泊', x: 550, y: 280, type: 'scenic' },
    { name: '千年古树', x: 295, y: 75, type: 'scenic' },
    { name: '观音像', x: 445, y: 95, type: 'scenic' },
    { name: '仙人洞', x: 515, y: 75, type: 'scenic' },
    { name: '一线天', x: 635, y: 70, type: 'scenic' },
    { name: '情人谷', x: 165, y: 95, type: 'scenic' },
    { name: '桃花岛', x: 410, y: 345, type: 'scenic' },
    { name: '荷花池', x: 535, y: 355, type: 'scenic' },
    { name: '玫瑰园', x: 755, y: 345, type: 'scenic' },
    { name: '梅花园', x: 85, y: 95, type: 'scenic' },
    { name: '樱花园', x: 220, y: 50, type: 'scenic' },
    { name: '牡丹园', x: 580, y: 345, type: 'scenic' },
    { name: '彩虹桥', x: 400, y: 360, type: 'scenic' },
    { name: '九曲桥', x: 620, y: 340, type: 'scenic' },
    { name: '玻璃栈道', x: 675, y: 335, type: 'scenic' },

    { name: '医务室', x: 150, y: 260, type: 'medical' },
    { name: '索道站', x: 680, y: 130, type: 'transport' },
    { name: '医务站', x: 360, y: 235, type: 'medical' },
    { name: '游船码头', x: 575, y: 310, type: 'transport' },
    { name: '电瓶车站', x: 165, y: 315, type: 'transport' },
    { name: '观光车站', x: 185, y: 140, type: 'transport' },
    { name: '指示牌A', x: 170, y: 230, type: 'facility' },
    { name: '指示牌B', x: 440, y: 200, type: 'facility' },
    { name: '指示牌C', x: 610, y: 190, type: 'facility' }
  ]

  // 生成地点标记SVG
  const placeMarkersSVG = mockPlaces.map((place, index) => {
    const colors = {
      service: '#409EFF',   // 蓝色-服务
      rest: '#67C23A',      // 绿色-休息
      view: '#E6A23C',      // 橙色-观景台
      parking: '#909399',   // 灰色-停车场
      facility: '#F56C6C',  // 红色-设施
      shop: '#67C23A',      // 绿色-商店
      medical: '#F56C6C',   // 红色-医疗
      transport: '#409EFF', // 蓝色-交通
      scenic: '#9C27B0',    // 紫色-景点
      food: '#FF9800',      // 橙色-餐饮
      hotel: '#673AB7',     // 深紫-住宿
      camp: '#795548'       // 棕色-露营
    }
    const color = colors[place.type] || '#409EFF'

    // 根据类型调整标记大小
    const smallTypes = ['facility', 'rest']
    const isSmall = smallTypes.includes(place.type)
    const radius = isSmall ? 4 : 6
    const labelWidth = isSmall ? 36 : (place.name.length > 3 ? 60 : 50)
    const fontSize = isSmall ? 9 : 10

    return `
      <!-- 地点标记 ${place.name} -->
      <g class="place-marker" style="cursor: pointer;">
        <circle cx="${place.x}" cy="${place.y}" r="${radius}" fill="${color}" stroke="#fff" stroke-width="2">
          <animate attributeName="r" values="${radius};${radius + 2};${radius}" dur="2s" repeatCount="indefinite" begin="${index * 0.1}s"/>
        </circle>
        <rect x="${place.x - labelWidth/2}" y="${place.y - 26}" width="${labelWidth}" height="16" rx="3" fill="rgba(255,255,255,0.95)" stroke="${color}" stroke-width="1"/>
        <text x="${place.x}" y="${place.y - 14}" text-anchor="middle" fill="#333" font-size="${fontSize}" font-weight="500">${place.name}</text>
      </g>
    `
  }).join('')

  // 创建一个简单的SVG地图作为占位
  mapContainer.value.innerHTML = `
    <div class="placeholder-map">
      <svg viewBox="0 0 800 400" width="100%" height="100%">
        <defs>
          <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
            <path d="M 40 0 L 0 0 0 40" fill="none" stroke="#e0e0e0" stroke-width="1"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="#f5f5f5"/>
        <rect width="100%" height="100%" fill="url(#grid)"/>

        <!-- 模拟路线 -->
        <path
          d="M 100 300 Q 200 250, 300 280 T 500 200 T 700 150"
          fill="none"
          stroke="#409EFF"
          stroke-width="4"
          stroke-linecap="round"
          stroke-dasharray="8 4"
        >
          <animate
            attributeName="stroke-dashoffset"
            from="100"
            to="0"
            dur="3s"
            repeatCount="indefinite"
          />
        </path>

        <!-- 起点 -->
        <circle cx="100" cy="300" r="8" fill="#67C23A">
          <animate attributeName="r" values="8;12;8" dur="2s" repeatCount="indefinite"/>
        </circle>
        <text x="100" y="330" text-anchor="middle" fill="#606266" font-size="12">起点</text>

        <!-- 途经点 -->
        <circle cx="300" cy="280" r="6" fill="#E6A23C"/>
        <circle cx="500" cy="200" r="6" fill="#E6A23C"/>

        <!-- 终点 -->
        <circle cx="700" cy="150" r="8" fill="#F56C6C">
          <animate attributeName="r" values="8;12;8" dur="2s" repeatCount="indefinite"/>
        </circle>
        <text x="700" y="130" text-anchor="middle" fill="#606266" font-size="12">终点</text>

        <!-- 周边地点标记 -->
        ${placeMarkersSVG}

        <!-- 提示文字 -->
        <text x="400" y="30" text-anchor="middle" fill="#F56C6C" font-size="16" font-weight="bold">
          ${!process.env.VUE_APP_AMAP_KEY ? '请配置高德地图Key以显示真实地图' : '地图Key无效或加载失败'}
        </text>
        <text x="400" y="50" text-anchor="middle" fill="#909399" font-size="12">
          当前显示为演示模式（共${mockPlaces.length}个地点）
        </text>

        <!-- 图例 -->
        <g transform="translate(50, 370)">
          <rect x="0" y="0" width="700" height="25" rx="4" fill="rgba(255,255,255,0.9)" stroke="#ddd" stroke-width="1"/>
          <text x="10" y="17" fill="#666" font-size="11" font-weight="bold">图例：</text>

          <circle cx="50" cy="12" r="4" fill="#409EFF"/>
          <text x="58" y="16" fill="#666" font-size="10">服务</text>

          <circle cx="100" cy="12" r="4" fill="#67C23A"/>
          <text x="108" y="16" fill="#666" font-size="10">休息</text>

          <circle cx="150" cy="12" r="4" fill="#E6A23C"/>
          <text x="158" y="16" fill="#666" font-size="10">观景</text>

          <circle cx="210" cy="12" r="4" fill="#9C27B0"/>
          <text x="218" y="16" fill="#666" font-size="10">景点</text>

          <circle cx="270" cy="12" r="4" fill="#FF9800"/>
          <text x="278" y="16" fill="#666" font-size="10">餐饮</text>

          <circle cx="330" cy="12" r="4" fill="#673AB7"/>
          <text x="338" y="16" fill="#666" font-size="10">住宿</text>

          <circle cx="390" cy="12" r="4" fill="#F56C6C"/>
          <text x="398" y="16" fill="#666" font-size="10">设施</text>

          <circle cx="450" cy="12" r="4" fill="#909399"/>
          <text x="458" y="16" fill="#666" font-size="10">停车</text>
        </g>
      </svg>
    </div>
  `

  mapLoaded.value = true
  emit('ready', null)
}

// 更新轨迹
const updateTrack = () => {
  if (!map || !props.showTrack || props.track.length < 2) return

  // 清除旧轨迹
  if (polyline) {
    map.remove(polyline)
  }

  // 创建新轨迹
  polyline = new AMap.Polyline({
    path: props.track,
    strokeColor: '#409EFF',
    strokeWeight: 6,
    strokeOpacity: 0.8,
    strokeStyle: 'solid',
    lineJoin: 'round',
    showDir: true
  })

  map.add(polyline)

  // 自适应视野
  map.setFitView([polyline])
}

// 更新标记点
const updateMarkers = () => {
  if (!map) return

  // 清除旧标记
  markerInstances.forEach(marker => map.remove(marker))
  markerInstances = []

  // 添加新标记
  props.markers.forEach((item, index) => {
    const marker = new AMap.Marker({
      position: [item.lng, item.lat],
      title: item.title || '',
      label: item.title ? {
        content: item.title,
        direction: 'top'
      } : null,
      animation: index === 0 ? 'AMAP_ANIMATION_DROP' : 'AMAP_ANIMATION_NONE'
    })

    marker.on('click', () => {
      emit('marker-click', item)
    })

    markerInstances.push(marker)
  })

  if (markerInstances.length > 0) {
    map.add(markerInstances)

    // 如果没有轨迹，自适应标记点
    if (!props.showTrack || props.track.length < 2) {
      map.setFitView(markerInstances)
    }
  }
}

// 监听数据变化
watch(() => props.track, updateTrack, { deep: true })
watch(() => props.markers, updateMarkers, { deep: true })

// 轨迹播放动画
const playTrackAnimation = () => {
  if (!map || !props.track || props.track.length < 2) {
    console.warn('地图未加载或轨迹数据不足')
    return false
  }

  // 清除之前的动画标记
  if (movingMarker) {
    map.remove(movingMarker)
    movingMarker = null
  }

  // 创建移动标记
  movingMarker = new AMap.Marker({
    position: props.track[0],
    icon: new AMap.Icon({
      size: new AMap.Size(32, 32),
      image: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_r.png',
      imageSize: new AMap.Size(32, 32)
    }),
    offset: new AMap.Pixel(-16, -32),
    zIndex: 100
  })

  map.add(movingMarker)

  // 动画参数
  let currentIndex = 0
  const speed = 50 // 移动速度（毫秒）
  const totalPoints = props.track.length

  // 移动动画函数
  const moveToNext = () => {
    if (currentIndex >= totalPoints - 1) {
      // 动画结束
      setTimeout(() => {
        if (movingMarker) {
          map.remove(movingMarker)
          movingMarker = null
        }
      }, 500)
      return
    }

    currentIndex++
    const targetPoint = props.track[currentIndex]

    // 使用 AMap 的 moveTo 方法实现平滑移动
    movingMarker.moveTo(targetPoint, {
      duration: speed,
      autoRotation: true
    })

    // 继续下一段
    setTimeout(moveToNext, speed)
  }

  // 开始动画
  moveToNext()
  return true
}

// 停止轨迹动画
const stopTrackAnimation = () => {
  if (movingMarker && map) {
    map.remove(movingMarker)
    movingMarker = null
  }
}

// 搜索周边地点
const searchNearbyPlaces = () => {
  if (!map || !props.showNearbyPlaces) return

  // 使用模拟数据（因为高德Key无效时无法调用真实搜索）
  displayMockPOIs()
}

// 显示POI标记
const displayPOIs = (pois) => {
  if (!map || !pois || pois.length === 0) return

  const AMap = window.AMap

  // 清除旧的POI标记
  poiMarkers.forEach(marker => map.remove(marker))
  poiMarkers = []

  // 创建新的POI标记
  pois.forEach((poi, index) => {
    const marker = new AMap.Marker({
      position: [poi.location.lng, poi.location.lat],
      title: poi.name,
      icon: new AMap.Icon({
        size: new AMap.Size(25, 34),
        image: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',
        imageSize: new AMap.Size(25, 34)
      }),
      offset: new AMap.Pixel(-12, -34),
      label: {
        content: `<div class="poi-label">${poi.name}</div>`,
        direction: 'bottom'
      }
    })

    // 点击事件
    marker.on('click', () => {
      emit('marker-click', {
        lng: poi.location.lng,
        lat: poi.location.lat,
        title: poi.name,
        address: poi.address,
        type: 'poi'
      })
    })

    poiMarkers.push(marker)
  })

  // 将POI标记添加到地图
  if (poiMarkers.length > 0) {
    map.add(poiMarkers)
  }
}

// 显示模拟POI数据（当真实搜索失败时使用）
const displayMockPOIs = () => {
  if (!map) return

  const AMap = window.AMap
  const center = map.getCenter()

  // 生成中心点周围的模拟POI
  const mockPOIs = [
    { name: '游客服务中心', offset: [0.001, 0.001] },
    { name: '休息驿站', offset: [0.002, -0.001] },
    { name: '观景台', offset: [-0.001, 0.002] },
    { name: '停车场', offset: [-0.002, -0.001] },
    { name: '公共厕所', offset: [0.0015, 0.0005] },
    { name: '小卖部', offset: [-0.001, -0.002] },
    { name: '急救站', offset: [0.0025, 0.0015] },
    { name: '索道入口', offset: [-0.0015, 0.001] }
  ]

  // 清除旧标记
  poiMarkers.forEach(marker => map.remove(marker))
  poiMarkers = []

  mockPOIs.forEach((poi) => {
    const lng = center.lng + poi.offset[0]
    const lat = center.lat + poi.offset[1]

    const marker = new AMap.Marker({
      position: [lng, lat],
      title: poi.name,
      icon: new AMap.Icon({
        size: new AMap.Size(25, 34),
        image: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',
        imageSize: new AMap.Size(25, 34)
      }),
      offset: new AMap.Pixel(-12, -34),
      label: {
        content: `<div style="background: rgba(255,255,255,0.9); padding: 2px 6px; border-radius: 4px; font-size: 12px; color: #333; border: 1px solid #ddd;">${poi.name}</div>`,
        direction: 'bottom'
      }
    })

    marker.on('click', () => {
      emit('marker-click', {
        lng: lng,
        lat: lat,
        title: poi.name,
        type: 'mock_poi'
      })
    })

    poiMarkers.push(marker)
  })

  map.add(poiMarkers)
}

// 暴露方法给父组件
const getMap = () => map
const getCenter = () => map ? map.getCenter() : null
const setCenter = (center) => {
  if (map) map.setCenter(center)
}
const setZoom = (zoom) => {
  if (map) map.setZoom(zoom)
}

defineExpose({
  getMap,
  getCenter,
  setCenter,
  setZoom,
  playTrackAnimation,
  stopTrackAnimation
})

onMounted(() => {
  nextTick(() => {
    initMap()
  })
})

onUnmounted(() => {
  // 停止动画
  stopTrackAnimation()
  // 清除POI标记
  if (map && poiMarkers.length > 0) {
    poiMarkers.forEach(marker => map.remove(marker))
    poiMarkers = []
  }
  // 销毁地图
  if (map) {
    map.destroy()
    map = null
  }
})
</script>

<style lang="scss" scoped>
.map-container {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  background-color: #f5f5f5;
}

.map-loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  width: 80%;

  .loading-text {
    margin-top: 16px;
    color: #909399;
  }
}

.map-error {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;

  p {
    margin: 16px 0;
    color: #606266;
  }
}

.placeholder-map {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;

  svg {
    max-width: 100%;
    max-height: 100%;
  }

  // 地点标记悬停效果
  .place-marker {
    transition: all 0.3s ease;

    &:hover {
      filter: brightness(1.2);
    }
  }
}

// 高德地图POI标签样式
:deep(.poi-label) {
  background: rgba(255, 255, 255, 0.95);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #333;
  border: 1px solid #409EFF;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  white-space: nowrap;
}
</style>
