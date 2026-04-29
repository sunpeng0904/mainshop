<template>
  <div class="hiking-routes-manage">
    <div class="page-header">
      <h2 class="page-title">路线管理</h2>
      <el-button type="primary" @click="goToAdd">
        <el-icon><Plus /></el-icon>
        新增路线
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="filterForm" inline>
        <el-form-item label="路线名称">
          <el-input
            v-model="filterForm.name"
            placeholder="请输入路线名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="难度等级">
          <el-select v-model="filterForm.difficulty" placeholder="请选择" clearable>
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </el-form-item>
        <el-form-item label="地区">
          <el-select v-model="filterForm.location" placeholder="请选择" clearable>
            <el-option
              v-for="loc in locations"
              :key="loc"
              :label="loc"
              :value="loc"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择" clearable>
            <el-option label="已上线" value="published" />
            <el-option label="已下线" value="offline" />
            <el-option label="草稿" value="draft" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="routeList"
        stripe
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="封面" width="120">
          <template #default="{ row }">
            <el-image
              :src="row.coverImage"
              fit="cover"
              class="cover-image"
              :preview-src-list="[row.coverImage]"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="路线名称" min-width="180" show-overflow-tooltip />
        <el-table-column label="难度" width="100">
          <template #default="{ row }">
            <el-tag :type="difficultyType(row.difficulty)">
              {{ difficultyText(row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="地区" width="120" />
        <el-table-column label="距离" width="100">
          <template #default="{ row }">
            {{ row.distance }} km
          </template>
        </el-table-column>
        <el-table-column label="用时" width="100">
          <template #default="{ row }">
            {{ row.duration }} h
          </template>
        </el-table-column>
        <el-table-column label="评分" width="120">
          <template #default="{ row }">
            <el-rate
              v-model="row.rating"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}"
            />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="goToEdit(row.id)">
              编辑
            </el-button>
            <el-button
              :type="row.status === 'published' ? 'warning' : 'success'"
              link
              @click="toggleStatus(row)"
            >
              {{ row.status === 'published' ? '下线' : '上线' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'

const router = useRouter()

// 筛选表单
const filterForm = ref({
  name: '',
  difficulty: '',
  location: '',
  status: ''
})

// 地区选项
const locations = ref(['北京', '上海', '杭州', '成都', '西安', '云南', '西藏', '新疆', '四川', '浙江', '山东', '安徽'])

// 表格数据
const loading = ref(false)
const routeList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedRows = ref([])

// 难度映射
const difficultyMap = {
  easy: { text: '简单', type: 'success' },
  medium: { text: '中等', type: 'warning' },
  hard: { text: '困难', type: 'danger' }
}

const difficultyText = (difficulty) => difficultyMap[difficulty]?.text || difficulty
const difficultyType = (difficulty) => difficultyMap[difficulty]?.type || 'info'

// 状态映射
const statusMap = {
  published: { text: '已上线', type: 'success' },
  offline: { text: '已下线', type: 'info' },
  draft: { text: '草稿', type: 'warning' }
}

const statusText = (status) => statusMap[status]?.text || status
const statusType = (status) => statusMap[status]?.type || 'info'

// 获取路线列表
const fetchRoutes = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))

    const mockData = [
      {
        id: 1,
        name: '香山红叶徒步路线',
        difficulty: 'easy',
        location: '北京',
        distance: 8.5,
        duration: 3,
        rating: 4.8,
        status: 'published',
        coverImage: 'https://picsum.photos/200/150?random=1',
        createTime: '2024-01-15 10:30:00'
      },
      {
        id: 2,
        name: '灵山大峡谷穿越',
        difficulty: 'hard',
        location: '北京',
        distance: 18.2,
        duration: 8,
        rating: 4.9,
        status: 'published',
        coverImage: 'https://picsum.photos/200/150?random=2',
        createTime: '2024-01-14 09:15:00'
      },
      {
        id: 3,
        name: '西湖环湖步道',
        difficulty: 'easy',
        location: '杭州',
        distance: 12.0,
        duration: 4,
        rating: 4.7,
        status: 'draft',
        coverImage: 'https://picsum.photos/200/150?random=3',
        createTime: '2024-01-13 14:20:00'
      }
    ]

    routeList.value = mockData
    total.value = 128
  } catch (error) {
    ElMessage.error('获取路线列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  page.value = 1
  fetchRoutes()
}

// 重置
const handleReset = () => {
  filterForm.value = {
    name: '',
    difficulty: '',
    location: '',
    status: ''
  }
  handleSearch()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchRoutes()
}

const handlePageChange = (val) => {
  page.value = val
  fetchRoutes()
}

// 选择
const handleSelectionChange = (val) => {
  selectedRows.value = val
}

// 新增
const goToAdd = () => {
  router.push('/admin/hiking/routes/add')
}

// 编辑
const goToEdit = (id) => {
  router.push(`/admin/hiking/routes/edit/${id}`)
}

// 切换状态
const toggleStatus = async (row) => {
  const action = row.status === 'published' ? '下线' : '上线'
  try {
    await ElMessageBox.confirm(`确定要${action}该路线吗？`, '提示', {
      type: 'warning'
    })
    // 调用API
    row.status = row.status === 'published' ? 'offline' : 'published'
    ElMessage.success(`${action}成功`)
  } catch (error) {
    // 取消操作
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该路线吗？删除后不可恢复！', '警告', {
      type: 'error'
    })
    // 调用API
    ElMessage.success('删除成功')
    fetchRoutes()
  } catch (error) {
    // 取消操作
  }
}

onMounted(() => {
  fetchRoutes()
})
</script>

<style lang="scss" scoped>
.hiking-routes-manage {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .page-title {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
    }
  }

  .filter-card {
    margin-bottom: 20px;
  }

  .table-card {
    .cover-image {
      width: 80px;
      height: 60px;
      border-radius: 4px;
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}
</style>
