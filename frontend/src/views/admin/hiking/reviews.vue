<template>
  <div class="hiking-reviews-manage">
    <div class="page-header">
      <h2 class="page-title">评价管理</h2>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">总评价数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.averageRating }}</div>
            <div class="stat-label">平均评分</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.pending }}</div>
            <div class="stat-label">待审核</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.today }}</div>
            <div class="stat-label">今日新增</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选栏 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="filterForm" inline>
        <el-form-item label="路线名称">
          <el-input
            v-model="filterForm.routeName"
            placeholder="请输入路线名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="评分">
          <el-select v-model="filterForm.rating" placeholder="请选择" clearable>
            <el-option label="5星" :value="5" />
            <el-option label="4星" :value="4" />
            <el-option label="3星" :value="3" />
            <el-option label="2星" :value="2" />
            <el-option label="1星" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择" clearable>
            <el-option label="已通过" value="approved" />
            <el-option label="待审核" value="pending" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="评价时间">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
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

    <!-- 批量操作 -->
    <div class="batch-actions">
      <el-button
        type="success"
        :disabled="selectedRows.length === 0"
        @click="handleBatchApprove"
      >
        批量通过
      </el-button>
      <el-button
        type="danger"
        :disabled="selectedRows.length === 0"
        @click="handleBatchReject"
      >
        批量拒绝
      </el-button>
      <el-button
        type="danger"
        plain
        :disabled="selectedRows.length === 0"
        @click="handleBatchDelete"
      >
        批量删除
      </el-button>
    </div>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="reviewList"
        stripe
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="用户信息" min-width="150">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :src="row.userAvatar" :size="40" />
              <span class="username">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="路线" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link type="primary" @click="goToRoute(row.routeId)">
              {{ row.routeName }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="180">
          <template #default="{ row }">
            <el-rate
              v-model="row.rating"
              disabled
              show-score
              text-color="#ff9900"
            />
          </template>
        </el-table-column>
        <el-table-column label="评价内容" min-width="250">
          <template #default="{ row }">
            <div class="review-content">
              <p class="content-text">{{ row.content }}</p>
              <div v-if="row.images && row.images.length > 0" class="review-images">
                <el-image
                  v-for="(img, index) in row.images.slice(0, 3)"
                  :key="index"
                  :src="img"
                  fit="cover"
                  class="review-thumb"
                  :preview-src-list="row.images"
                />
                <span v-if="row.images.length > 3" class="more-images">
                  +{{ row.images.length - 3 }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评价时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              link
              @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="danger"
              link
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
            <el-button type="primary" link @click="handleReply(row)">
              回复
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

    <!-- 回复对话框 -->
    <el-dialog
      v-model="replyDialog.visible"
      title="回复评价"
      width="500px"
    >
      <div class="reply-preview">
        <div class="original-review">
          <div class="review-header">
            <el-avatar :src="replyDialog.review?.userAvatar" :size="32" />
            <span class="username">{{ replyDialog.review?.username }}</span>
            <el-rate
              :model-value="replyDialog.review?.rating || 0"
              disabled
              size="small"
            />
          </div>
          <p class="review-text">{{ replyDialog.review?.content }}</p>
        </div>
      </div>
      <el-input
        v-model="replyDialog.content"
        type="textarea"
        :rows="4"
        placeholder="请输入回复内容"
      />
      <template #footer>
        <el-button @click="replyDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()

// 统计数据
const stats = ref({
  total: 1286,
  averageRating: 4.7,
  pending: 23,
  today: 18
})

// 筛选表单
const filterForm = ref({
  routeName: '',
  rating: null,
  status: '',
  dateRange: null
})

// 表格数据
const loading = ref(false)
const reviewList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedRows = ref([])

// 状态映射
const statusMap = {
  approved: { text: '已通过', type: 'success' },
  pending: { text: '待审核', type: 'warning' },
  rejected: { text: '已拒绝', type: 'danger' }
}

const statusText = (status) => statusMap[status]?.text || status
const statusType = (status) => statusMap[status]?.type || 'info'

// 回复对话框
const replyDialog = ref({
  visible: false,
  review: null,
  content: ''
})

// 获取评价列表
const fetchReviews = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))

    const mockData = [
      {
        id: 1,
        routeId: 1,
        routeName: '香山红叶徒步路线',
        userId: 101,
        username: '徒步达人小王',
        userAvatar: 'https://picsum.photos/100/100?random=1',
        rating: 5,
        content: '非常棒的路线！红叶季的风景真的太美了，推荐大家一定要去看看。路线难度适中，新手也能完成。',
        images: [
          'https://picsum.photos/200/200?random=11',
          'https://picsum.photos/200/200?random=12'
        ],
        status: 'approved',
        createTime: '2024-10-15 14:30:00',
        reply: null
      },
      {
        id: 2,
        routeId: 2,
        routeName: '灵山大峡谷穿越',
        userId: 102,
        username: '户外爱好者',
        userAvatar: 'https://picsum.photos/100/100?random=2',
        rating: 4,
        content: '路线不错，就是周末人太多了。建议工作日去，人少风景好。',
        images: [],
        status: 'approved',
        createTime: '2024-10-14 09:15:00',
        reply: null
      },
      {
        id: 3,
        routeId: 1,
        routeName: '香山红叶徒步路线',
        userId: 103,
        username: '亲子游妈妈',
        userAvatar: 'https://picsum.photos/100/100?random=3',
        rating: 5,
        content: '带着孩子一起走的，8岁的孩子也能坚持下来。一路上给孩子讲解植物知识，很有意义的一次徒步。',
        images: [
          'https://picsum.photos/200/200?random=13',
          'https://picsum.photos/200/200?random=14',
          'https://picsum.photos/200/200?random=15',
          'https://picsum.photos/200/200?random=16'
        ],
        status: 'pending',
        createTime: '2024-10-13 16:45:00',
        reply: null
      },
      {
        id: 4,
        routeId: 3,
        routeName: '西湖环湖步道',
        userId: 104,
        username: '摄影师阿明',
        userAvatar: 'https://picsum.photos/100/100?random=4',
        rating: 5,
        content: '环湖步道风景太棒了，特别是日落时分，非常适合拍照。全程平坦，老人小孩都适合。',
        images: [
          'https://picsum.photos/200/200?random=17'
        ],
        status: 'pending',
        createTime: '2024-10-12 18:20:00',
        reply: null
      },
      {
        id: 5,
        routeId: 4,
        routeName: '四姑娘山大峰攀登',
        userId: 105,
        username: '登山狂人',
        userAvatar: 'https://picsum.photos/100/100?random=5',
        rating: 3,
        content: '难度比想象中大，新手不要轻易尝试。风景确实美，但是太累了。',
        images: [],
        status: 'rejected',
        createTime: '2024-10-10 11:30:00',
        reply: null
      }
    ]

    reviewList.value = mockData
    total.value = 1286
  } catch (error) {
    ElMessage.error('获取评价列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  page.value = 1
  fetchReviews()
}

// 重置
const handleReset = () => {
  filterForm.value = {
    routeName: '',
    rating: null,
    status: '',
    dateRange: null
  }
  handleSearch()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchReviews()
}

const handlePageChange = (val) => {
  page.value = val
  fetchReviews()
}

// 选择
const handleSelectionChange = (val) => {
  selectedRows.value = val
}

// 跳转到路线详情
const goToRoute = (routeId) => {
  window.open(`/hiking/routes/${routeId}`, '_blank')
}

// 通过
const handleApprove = async (row) => {
  try {
    // 调用API
    row.status = 'approved'
    ElMessage.success('已通过')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 拒绝
const handleReject = async (row) => {
  try {
    await ElMessageBox.confirm('确定要拒绝该评价吗？', '提示', {
      type: 'warning'
    })
    // 调用API
    row.status = 'rejected'
    ElMessage.success('已拒绝')
  } catch (error) {
    // 取消
  }
}

// 回复
const handleReply = (row) => {
  replyDialog.value = {
    visible: true,
    review: row,
    content: row.reply || ''
  }
}

// 提交回复
const submitReply = async () => {
  if (!replyDialog.value.content.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  try {
    // 调用API
    replyDialog.value.review.reply = replyDialog.value.content
    ElMessage.success('回复成功')
    replyDialog.value.visible = false
  } catch (error) {
    ElMessage.error('回复失败')
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该评价吗？删除后不可恢复！', '警告', {
      type: 'error'
    })
    // 调用API
    ElMessage.success('删除成功')
    fetchReviews()
  } catch (error) {
    // 取消
  }
}

// 批量通过
const handleBatchApprove = async () => {
  try {
    const count = selectedRows.value.length
    await ElMessageBox.confirm(`确定要通过选中的 ${count} 条评价吗？`, '提示')
    // 调用API
    selectedRows.value.forEach(row => {
      row.status = 'approved'
    })
    ElMessage.success(`已通过 ${count} 条评价`)
    selectedRows.value = []
  } catch (error) {
    // 取消
  }
}

// 批量拒绝
const handleBatchReject = async () => {
  try {
    const count = selectedRows.value.length
    await ElMessageBox.confirm(`确定要拒绝选中的 ${count} 条评价吗？`, '警告', {
      type: 'warning'
    })
    // 调用API
    selectedRows.value.forEach(row => {
      row.status = 'rejected'
    })
    ElMessage.success(`已拒绝 ${count} 条评价`)
    selectedRows.value = []
  } catch (error) {
    // 取消
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    const count = selectedRows.value.length
    await ElMessageBox.confirm(`确定要删除选中的 ${count} 条评价吗？删除后不可恢复！`, '危险操作', {
      type: 'error'
    })
    // 调用API
    ElMessage.success(`已删除 ${count} 条评价`)
    selectedRows.value = []
    fetchReviews()
  } catch (error) {
    // 取消
  }
}

onMounted(() => {
  fetchReviews()
})
</script>

<style lang="scss" scoped>
.hiking-reviews-manage {
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

  .stats-row {
    margin-bottom: 20px;

    .stat-item {
      text-align: center;
      padding: 10px;

      .stat-value {
        font-size: 32px;
        font-weight: 600;
        color: #409EFF;
        margin-bottom: 8px;
      }

      .stat-label {
        color: #909399;
        font-size: 14px;
      }
    }
  }

  .filter-card {
    margin-bottom: 16px;
  }

  .batch-actions {
    margin-bottom: 16px;
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 12px;

    .username {
      font-weight: 500;
    }
  }

  .review-content {
    .content-text {
      color: #606266;
      line-height: 1.6;
      margin-bottom: 8px;
    }

    .review-images {
      display: flex;
      gap: 8px;
      align-items: center;

      .review-thumb {
        width: 60px;
        height: 60px;
        border-radius: 4px;
        cursor: pointer;
      }

      .more-images {
        width: 60px;
        height: 60px;
        background: #f5f7fa;
        border-radius: 4px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #909399;
        font-size: 14px;
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }

  .reply-preview {
    margin-bottom: 16px;

    .original-review {
      background: #f5f7fa;
      padding: 16px;
      border-radius: 8px;

      .review-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 12px;

        .username {
          font-weight: 500;
        }
      }

      .review-text {
        color: #606266;
        line-height: 1.6;
        margin: 0;
      }
    }
  }
}
</style>
