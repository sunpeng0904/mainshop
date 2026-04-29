<template>
  <div class="route-edit-page">
    <div class="page-header">
      <div class="header-left">
        <el-button link @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2 class="page-title">{{ isEdit ? '编辑路线' : '新增路线' }}</h2>
      </div>
      <div class="header-right">
        <el-button @click="handleSave('draft')">保存草稿</el-button>
        <el-button type="primary" @click="handleSave('published')">
          {{ isEdit ? '保存修改' : '立即发布' }}
        </el-button>
      </div>
    </div>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      class="route-form"
    >
      <el-row :gutter="20">
        <el-col :span="16">
          <!-- 基本信息 -->
          <el-card class="form-card" title="基本信息">
            <template #header>
              <span>基本信息</span>
            </template>

            <el-form-item label="路线名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入路线名称" />
            </el-form-item>

            <el-form-item label="路线简介" prop="description">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="4"
                placeholder="请输入路线简介"
              />
            </el-form-item>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="难度等级" prop="difficulty">
                  <el-select v-model="form.difficulty" placeholder="请选择" style="width: 100%">
                    <el-option label="简单" value="easy" />
                    <el-option label="中等" value="medium" />
                    <el-option label="困难" value="hard" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="所在地区" prop="location">
                  <el-select v-model="form.location" placeholder="请选择" style="width: 100%">
                    <el-option
                      v-for="loc in locations"
                      :key="loc"
                      :label="loc"
                      :value="loc"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="距离(km)" prop="distance">
                  <el-input-number v-model="form.distance" :min="0" :precision="1" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="用时(小时)" prop="duration">
                  <el-input-number v-model="form.duration" :min="0" :precision="1" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="爬升(m)" prop="elevationGain">
                  <el-input-number v-model="form.elevationGain" :min="0" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="最佳季节" prop="bestSeason">
              <el-input v-model="form.bestSeason" placeholder="例如：3月-5月、9月-11月" />
            </el-form-item>

            <el-form-item label="路线标签">
              <el-select
                v-model="form.tags"
                multiple
                filterable
                allow-create
                placeholder="请输入标签"
                style="width: 100%"
              >
                <el-option
                  v-for="tag in tagOptions"
                  :key="tag"
                  :label="tag"
                  :value="tag"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="设为热门">
              <el-switch v-model="form.isHot" />
            </el-form-item>
          </el-card>

          <!-- 路线图片 -->
          <el-card class="form-card" title="路线图片">
            <template #header>
              <span>路线图片</span>
            </template>

            <el-form-item label="封面图片" prop="coverImage">
              <el-upload
                class="cover-uploader"
                action="#"
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleCoverChange"
              >
                <img v-if="form.coverImage" :src="form.coverImage" class="cover-image" />
                <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>

            <el-form-item label="详情图片">
              <el-upload
                action="#"
                list-type="picture-card"
                :auto-upload="false"
                :file-list="imageList"
                :on-change="handleImageChange"
                :on-remove="handleImageRemove"
              >
                <el-icon><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-card>

          <!-- 行程安排 -->
          <el-card class="form-card" title="行程安排">
            <template #header>
              <div class="card-header">
                <span>行程安排</span>
                <el-button type="primary" link @click="addItinerary">
                  <el-icon><Plus /></el-icon>
                  添加节点
                </el-button>
              </div>
            </template>

            <div v-for="(item, index) in form.itinerary" :key="index" class="itinerary-item">
              <el-row :gutter="10">
                <el-col :span="6">
                  <el-input v-model="item.name" placeholder="节点名称" />
                </el-col>
                <el-col :span="4">
                  <el-time-select
                    v-model="item.time"
                    start="06:00"
                    step="00:30"
                    end="22:00"
                    placeholder="时间"
                  />
                </el-col>
                <el-col :span="4">
                  <el-input-number v-model="item.distance" :min="0" :precision="1" placeholder="距离" />
                </el-col>
                <el-col :span="4">
                  <el-input-number v-model="item.elevation" :min="0" placeholder="海拔" />
                </el-col>
                <el-col :span="4">
                  <el-select v-model="item.type" placeholder="类型">
                    <el-option label="起点" value="start" />
                    <el-option label="途径点" value="waypoint" />
                    <el-option label="景点" value="attraction" />
                    <el-option label="休息点" value="rest" />
                    <el-option label="终点" value="end" />
                  </el-select>
                </el-col>
                <el-col :span="2">
                  <el-button type="danger" link @click="removeItinerary(index)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-col>
              </el-row>
              <el-input
                v-model="item.description"
                type="textarea"
                :rows="2"
                placeholder="节点描述"
                class="itinerary-desc"
              />
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <!-- 地图轨迹 -->
          <el-card class="form-card" title="地图轨迹">
            <template #header>
              <span>地图轨迹</span>
            </template>

            <div class="map-wrapper">
              <MapContainer
                ref="mapRef"
                :height="300"
                :track="form.track"
                :markers="form.markers"
                :clickable="true"
                @map-click="onMapClick"
              />
            </div>

            <div class="track-actions">
              <el-button type="primary" @click="drawTrack">
                <el-icon><Edit /></el-icon>
                绘制轨迹
              </el-button>
              <el-button @click="importGPX">
                <el-icon><Upload /></el-icon>
                导入GPX
              </el-button>
              <el-button type="danger" link @click="clearTrack">
                清除轨迹
              </el-button>
            </div>

            <div class="track-points">
              <p class="points-info">轨迹点数：{{ form.track.length }}</p>
            </div>
          </el-card>

          <!-- 装备建议 -->
          <el-card class="form-card" title="装备建议">
            <template #header>
              <span>装备建议</span>
            </template>

            <el-form-item label="必备装备">
              <el-select
                v-model="form.equipment.required"
                multiple
                filterable
                allow-create
                placeholder="请选择或输入"
                style="width: 100%"
              >
                <el-option label="登山鞋" value="登山鞋" />
                <el-option label="背包" value="背包" />
                <el-option label="登山杖" value="登山杖" />
                <el-option label="雨具" value="雨具" />
                <el-option label="防晒霜" value="防晒霜" />
              </el-select>
            </el-form-item>

            <el-form-item label="食品饮料">
              <el-select
                v-model="form.equipment.food"
                multiple
                filterable
                allow-create
                placeholder="请选择或输入"
                style="width: 100%"
              >
                <el-option label="饮用水" value="饮用水" />
                <el-option label="能量棒" value="能量棒" />
                <el-option label="水果" value="水果" />
                <el-option label="午餐" value="午餐" />
              </el-select>
            </el-form-item>

            <el-form-item label="安全装备">
              <el-select
                v-model="form.equipment.safety"
                multiple
                filterable
                allow-create
                placeholder="请选择或输入"
                style="width: 100%"
              >
                <el-option label="急救包" value="急救包" />
                <el-option label="头灯" value="头灯" />
                <el-option label="哨子" value="哨子" />
                <el-option label="充电宝" value="充电宝" />
              </el-select>
            </el-form-item>
          </el-card>

          <!-- 注意事项 -->
          <el-card class="form-card" title="注意事项">
            <template #header>
              <div class="card-header">
                <span>注意事项</span>
                <el-button type="primary" link @click="addWarning">
                  <el-icon><Plus /></el-icon>
                  添加
                </el-button>
              </div>
            </template>

            <div v-for="(item, index) in form.warnings" :key="index" class="warning-item">
              <el-row :gutter="10">
                <el-col :span="8">
                  <el-input v-model="item.title" placeholder="标题" />
                </el-col>
                <el-col :span="6">
                  <el-select v-model="item.type" placeholder="类型">
                    <el-option label="提示" value="info" />
                    <el-option label="警告" value="warning" />
                    <el-option label="危险" value="error" />
                  </el-select>
                </el-col>
                <el-col :span="8">
                  <el-input v-model="item.content" placeholder="内容" />
                </el-col>
                <el-col :span="2">
                  <el-button type="danger" link @click="removeWarning(index)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-col>
              </el-row>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Delete, ArrowLeft, Edit, Upload } from '@element-plus/icons-vue'
import MapContainer from '@/components/MapContainer.vue'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const mapRef = ref(null)

// 判断是否编辑模式
const isEdit = computed(() => !!route.params.id)
const routeId = computed(() => route.params.id)

// 表单数据
const form = ref({
  name: '',
  description: '',
  difficulty: 'easy',
  location: '',
  distance: 0,
  duration: 0,
  elevationGain: 0,
  maxElevation: 0,
  bestSeason: '',
  tags: [],
  isHot: false,
  coverImage: '',
  images: [],
  track: [],
  markers: [],
  itinerary: [],
  equipment: {
    required: [],
    food: [],
    safety: []
  },
  warnings: []
})

// 图片列表
const imageList = ref([])

// 地区选项
const locations = ref(['北京', '上海', '杭州', '成都', '西安', '云南', '西藏', '新疆', '四川', '浙江', '山东', '安徽'])

// 标签选项
const tagOptions = ref(['风景优美', '适合新手', '亲子友好', '挑战性强', '历史文化', '摄影圣地', '避暑胜地', '红叶观赏'])

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入路线名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入路线简介', trigger: 'blur' }],
  difficulty: [{ required: true, message: '请选择难度等级', trigger: 'change' }],
  location: [{ required: true, message: '请选择所在地区', trigger: 'change' }],
  distance: [{ required: true, message: '请输入距离', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入用时', trigger: 'blur' }],
  coverImage: [{ required: true, message: '请上传封面图片', trigger: 'change' }]
}

// 获取路线详情
const fetchRouteDetail = async () => {
  if (!isEdit.value) return

  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))

    // 模拟数据
    form.value = {
      name: '香山红叶徒步路线',
      description: '香山红叶徒步路线是北京最经典的秋季徒步路线之一。',
      difficulty: 'easy',
      location: '北京',
      distance: 8.5,
      duration: 3,
      elevationGain: 450,
      maxElevation: 575,
      bestSeason: '9月-11月',
      tags: ['风景优美', '红叶观赏'],
      isHot: true,
      coverImage: 'https://picsum.photos/400/300?random=1',
      images: [],
      track: [],
      markers: [],
      itinerary: [
        {
          name: '香山公园东门',
          time: '08:00',
          distance: 0,
          elevation: 60,
          type: 'start',
          description: '集合出发'
        }
      ],
      equipment: {
        required: ['登山鞋', '背包'],
        food: ['饮用水', '能量棒'],
        safety: ['急救包']
      },
      warnings: [
        {
          title: '天气注意',
          type: 'warning',
          content: '秋季天气多变，请提前查看天气预报'
        }
      ]
    }
  } catch (error) {
    ElMessage.error('获取路线详情失败')
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 保存
const handleSave = async (status) => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    // 调用API保存
    await new Promise(resolve => setTimeout(resolve, 500))

    ElMessage.success(status === 'published' ? '发布成功' : '保存草稿成功')
    router.push('/admin/hiking/routes')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 封面图片上传
const handleCoverChange = (file) => {
  // 实际项目中需要上传到服务器
  const reader = new FileReader()
  reader.readAsDataURL(file.raw)
  reader.onload = () => {
    form.value.coverImage = reader.result
  }
}

// 详情图片上传
const handleImageChange = (file, fileList) => {
  imageList.value = fileList
}

// 详情图片删除
const handleImageRemove = (file, fileList) => {
  imageList.value = fileList
}

// 添加行程节点
const addItinerary = () => {
  form.value.itinerary.push({
    name: '',
    time: '',
    distance: 0,
    elevation: 0,
    type: 'waypoint',
    description: ''
  })
}

// 删除行程节点
const removeItinerary = (index) => {
  form.value.itinerary.splice(index, 1)
}

// 添加注意事项
const addWarning = () => {
  form.value.warnings.push({
    title: '',
    type: 'info',
    content: ''
  })
}

// 删除注意事项
const removeWarning = (index) => {
  form.value.warnings.splice(index, 1)
}

// 地图点击
const onMapClick = (point) => {
  console.log('地图点击:', point)
}

// 绘制轨迹
const drawTrack = () => {
  ElMessage.info('绘制轨迹功能开发中...')
}

// 导入GPX
const importGPX = () => {
  ElMessage.info('导入GPX功能开发中...')
}

// 清除轨迹
const clearTrack = () => {
  form.value.track = []
  form.value.markers = []
  ElMessage.success('轨迹已清除')
}

onMounted(() => {
  fetchRouteDetail()
})
</script>

<style lang="scss" scoped>
.route-edit-page {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .page-title {
        margin: 0;
        font-size: 20px;
        font-weight: 600;
      }
    }

    .header-right {
      display: flex;
      gap: 12px;
    }
  }

  .form-card {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .cover-uploader {
    :deep(.el-upload) {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);

      &:hover {
        border-color: var(--el-color-primary);
      }
    }

    .cover-image {
      width: 200px;
      height: 150px;
      object-fit: cover;
    }

    .cover-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 200px;
      height: 150px;
      text-align: center;
    }
  }

  .itinerary-item {
    padding: 16px;
    background: #f5f7fa;
    border-radius: 8px;
    margin-bottom: 12px;

    .itinerary-desc {
      margin-top: 10px;
    }
  }

  .map-wrapper {
    margin-bottom: 16px;
  }

  .track-actions {
    display: flex;
    gap: 8px;
    margin-bottom: 12px;
  }

  .track-points {
    .points-info {
      color: #909399;
      font-size: 14px;
    }
  }

  .warning-item {
    padding: 12px;
    background: #f5f7fa;
    border-radius: 8px;
    margin-bottom: 8px;
  }
}
</style>
