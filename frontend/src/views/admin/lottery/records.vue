<template>
  <div class="admin-lottery-records">
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="stat-icon" style="background: #409eff;">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.totalDraws || 0 }}</div>
          <div class="stat-label">总抽奖次数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #67c23a;">
          <el-icon><TrophyBase /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.winCount || 0 }}</div>
          <div class="stat-label">中奖人数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #e6a23c;">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.pendingCount || 0 }}</div>
          <div class="stat-label">待领取</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #f56c6c;">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.receivedCount || 0 }}</div>
          <div class="stat-label">已领取</div>
        </div>
      </div>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="filter-card card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="用户名">
          <el-input
            v-model="filterForm.username"
            placeholder="请输入用户名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="奖品">
          <el-select v-model="filterForm.prizeId" placeholder="全部奖品" clearable>
            <el-option
              v-for="prize in prizes"
              :key="prize.id"
              :label="prize.name"
              :value="prize.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="领取状态">
          <el-select v-model="filterForm.status" placeholder="全部状态" clearable>
            <el-option label="待领取" :value="0" />
            <el-option label="已领取" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="中奖时间">
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
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="resetFilter">重置</el-button>
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon> 导出
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 记录表格 -->
    <div class="table-card card">
      <el-table v-loading="loading" :data="records" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column label="奖品" min-width="150">
          <template #default="{ row }">
            <div class="prize-info">
              <el-tag :type="getLevelType(row.prizeLevel)" size="small">
                {{ getLevelName(row.prizeLevel) }}
              </el-tag>
              <span class="prize-name">{{ row.prizeName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="奖品价值" width="100">
          <template #default="{ row }">
            ¥{{ row.prizeValue }}
          </template>
        </el-table-column>
        <el-table-column label="领取状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已领取' : '待领取' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="中奖时间" width="160" />
        <el-table-column prop="receiveTime" label="领取时间" width="160">
          <template #default="{ row }">
            {{ row.receiveTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchRecords"
          @current-change="fetchRecords"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getAdminLotteryRecords,
  getAdminPrizeList,
  getLotteryStatistics,
  exportLotteryRecords
} from '@/api/admin/lottery'

const loading = ref(false)
const records = ref([])
const prizes = ref([])
const statistics = ref({})

const filterForm = reactive({
  username: '',
  prizeId: null,
  status: null,
  dateRange: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 获取等级名称
const getLevelName = (level) => {
  const names = { 1: '一等奖', 2: '二等奖', 3: '三等奖', 4: '四等奖', 5: '五等奖' }
  return names[level] || `${level}等奖`
}

// 获取等级标签类型
const getLevelType = (level) => {
  const types = { 1: 'danger', 2: 'warning', 3: 'success', 4: 'info', 5: '' }
  return types[level] || ''
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const response = await getLotteryStatistics()
    statistics.value = response.data || {}
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取奖品列表（用于筛选）
const fetchPrizes = async () => {
  try {
    const response = await getAdminPrizeList()
    prizes.value = response.data || []
  } catch (error) {
    console.error('获取奖品列表失败:', error)
  }
}

// 获取中奖记录
const fetchRecords = async () => {
  loading.value = true
  try {
    const params = {
      username: filterForm.username || undefined,
      prizeId: filterForm.prizeId || undefined,
      status: filterForm.status,
      startTime: filterForm.dateRange?.[0],
      endTime: filterForm.dateRange?.[1],
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const response = await getAdminLotteryRecords(params)
    records.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    console.error('获取中奖记录失败:', error)
    ElMessage.error('获取中奖记录失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchRecords()
}

// 重置
const resetFilter = () => {
  filterForm.username = ''
  filterForm.prizeId = null
  filterForm.status = null
  filterForm.dateRange = null
  handleSearch()
}

// 导出
const handleExport = async () => {
  try {
    const params = {
      username: filterForm.username || undefined,
      prizeId: filterForm.prizeId || undefined,
      status: filterForm.status,
      startTime: filterForm.dateRange?.[0],
      endTime: filterForm.dateRange?.[1]
    }
    await exportLotteryRecords(params)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  fetchStatistics()
  fetchPrizes()
  fetchRecords()
})
</script>

<style lang="scss" scoped>
.admin-lottery-records {
  .stat-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 16px;

    .stat-card {
      background: #fff;
      border-radius: 8px;
      padding: 20px;
      display: flex;
      align-items: center;
      gap: 16px;

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 24px;
      }

      .stat-content {
        .stat-value {
          font-size: 24px;
          font-weight: bold;
          color: #303133;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }
  }

  .filter-card {
    margin-bottom: 16px;
    padding: 16px;
    background: #fff;
    border-radius: 8px;
  }

  .filter-form {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;

    :deep(.el-form-item) {
      margin-bottom: 0;
    }
  }

  .table-card {
    padding: 16px;
    background: #fff;
    border-radius: 8px;

    .prize-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .prize-name {
        color: #606266;
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }
  }
}

@media (max-width: 1200px) {
  .admin-lottery-records .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .admin-lottery-records .stat-cards {
    grid-template-columns: 1fr;
  }
}
</style>
