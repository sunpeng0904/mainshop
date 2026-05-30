<template>
  <div class="insider-page">
    <!-- 安全提示 -->
    <div class="security-bar">发行监管信息系统-禁止处理涉密信息</div>

    <!-- 顶部导航 -->
    <div class="top-nav">
      <div class="nav-left">
        <span class="nav-logo">📋 发行监管系统</span>
        <div class="nav-links">
          <a href="#">工作台</a>
          <a href="#">帮助</a>
          <span class="update-log">更新日志</span>
        </div>
      </div>
      <div class="nav-right">
        <span>您好，监管员</span>
        <span class="sep">|</span>
        <a href="#">返回政务平台</a>
        <span class="sep">|</span>
        <a href="#">退出</a>
      </div>
    </div>

    <!-- 工作台标题 -->
    <div class="workspace-header">
      <span class="workspace-title">我的工作台</span>
    </div>

    <div class="main-layout">
      <!-- 左侧菜单 -->
      <div class="sidebar">
        <div class="menu-item">我的工作台</div>
        <div class="menu-group-title">注册审核</div>
        <div class="menu-item">证监会注册</div>
        <div class="menu-group-title">收藏意见</div>
        <div class="menu-item">收藏意见管理</div>
        <div class="menu-group-title">内幕信息</div>
        <div class="menu-item active">内幕信息知情人登记</div>
        <div class="menu-group-title">档案</div>
        <div class="menu-item">档案管理</div>
      </div>

      <!-- 主内容 -->
      <div class="content">
        <!-- 内容头部 -->
        <div class="content-header">
          <span class="page-title">内幕信息知情人登记列表</span>
          <div class="header-actions">
            <button class="btn btn-primary">+ 新建</button>
            <button class="btn">📤 导出</button>
          </div>
        </div>

        <!-- 内容主体 -->
        <div class="content-body">
          <!-- 搜索表单 -->
          <div class="search-form">
            <div class="search-row">
              <div class="form-item">
                <span class="form-label">企业名称</span>
                <input
                  v-model="searchForm.companyName"
                  type="text"
                  class="form-input"
                  placeholder="请输入企业名称"
                />
              </div>
              <div class="form-item">
                <span class="form-label">证监会行业</span>
                <select v-model="searchForm.industry" class="form-select">
                  <option value="">请选择</option>
                  <option value="制造业">制造业</option>
                  <option value="金融业">金融业</option>
                  <option value="信息技术">信息技术</option>
                </select>
              </div>
              <div class="form-item">
                <span class="form-label">所属板块</span>
                <select v-model="searchForm.board" class="form-select">
                  <option value="">请选择</option>
                  <option value="主板（沪市）">主板（沪市）</option>
                  <option value="主板（深市）">主板（深市）</option>
                  <option value="科创板">科创板</option>
                  <option value="创业板">创业板</option>
                </select>
              </div>
              <div class="form-item">
                <span class="form-label">融资类型</span>
                <select v-model="searchForm.financingType" class="form-select">
                  <option value="">请选择</option>
                  <option value="首次公开发行股票">首次公开发行股票</option>
                  <option value="向不特定对象募集股份">向不特定对象募集股份</option>
                  <option value="向特定对象发行股票">向特定对象发行股票</option>
                </select>
              </div>
            </div>
            <div class="search-row">
              <div class="form-item">
                <span class="form-label">受理时间</span>
                <input
                  v-model="searchForm.acceptTimeStart"
                  type="text"
                  class="form-input"
                  placeholder="开始日期"
                />
                <span>—</span>
                <input
                  v-model="searchForm.acceptTimeEnd"
                  type="text"
                  class="form-input"
                  placeholder="结束日期"
                />
              </div>
              <div class="form-item">
                <span class="form-label">知情日期</span>
                <input
                  v-model="searchForm.knowledgeTimeStart"
                  type="text"
                  class="form-input"
                  placeholder="开始日期"
                />
                <span>—</span>
                <input
                  v-model="searchForm.knowledgeTimeEnd"
                  type="text"
                  class="form-input"
                  placeholder="结束日期"
                />
              </div>
              <div class="form-item">
                <span class="form-label">登记时间</span>
                <input
                  v-model="searchForm.registerTimeStart"
                  type="text"
                  class="form-input"
                  placeholder="开始日期"
                />
                <span>—</span>
                <input
                  v-model="searchForm.registerTimeEnd"
                  type="text"
                  class="form-input"
                  placeholder="结束日期"
                />
              </div>
            </div>
            <div class="search-row">
              <div class="form-item">
                <span class="form-label">知情人姓名</span>
                <input
                  v-model="searchForm.insiderName"
                  type="text"
                  class="form-input"
                  placeholder="请输入知情人姓名"
                />
              </div>
              <div class="form-item">
                <span class="form-label">理由</span>
                <input
                  v-model="searchForm.reason"
                  type="text"
                  class="form-input"
                  placeholder="请输入理由"
                />
              </div>
              <div class="form-item">
                <span class="form-label">知情内容</span>
                <input
                  v-model="searchForm.content"
                  type="text"
                  class="form-input"
                  placeholder="请输入知情内容"
                  style="width: 240px"
                />
              </div>
              <div class="search-actions">
                <button class="btn btn-text" @click="toggleSearch">
                  {{ showAllSearch ? '收起查询' : '展开查询' }}
                </button>
                <button class="btn" @click="handleReset">重置</button>
                <button class="btn btn-primary" @click="handleSearch">查询</button>
              </div>
            </div>
          </div>

          <!-- 数据表格 -->
          <table class="data-table">
            <thead>
              <tr>
                <th class="col-idx">序号</th>
                <th class="col-name">企业名称</th>
                <th>受理时间</th>
                <th>所属板块</th>
                <th>融资类型</th>
                <th>证监会行业细分</th>
                <th>知情日期</th>
                <th>理由</th>
                <th>知情内容</th>
                <th>知情人姓名</th>
                <th>登记时间</th>
                <th>状态</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in tableData" :key="item.id">
                <td class="col-idx">{{ (currentPage - 1) * pageSize + index + 1 }}</td>
                <td>{{ item.companyName }}</td>
                <td>{{ item.acceptTime }}</td>
                <td>{{ item.board }}</td>
                <td>{{ item.financingType }}</td>
                <td>{{ item.industry }}</td>
                <td>{{ item.knowledgeTime }}</td>
                <td>{{ item.reason }}</td>
                <td>{{ item.content }}</td>
                <td>{{ item.insiderName }}</td>
                <td>{{ item.registerTime }}</td>
                <td>
                  <span :class="['status-tag', getStatusClass(item.status)]">
                    {{ getStatusLabel(item.status) }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>

          <!-- 分页 -->
          <div class="pagination-bar">
            <div class="pagination-left">
              <span class="page-info">共{{ total }}条</span>
              <div class="page-size">
                <span>每页</span>
                <select v-model="pageSize" @change="handlePageSizeChange">
                  <option :value="10">10条</option>
                  <option :value="20">20条</option>
                  <option :value="50">50条</option>
                </select>
              </div>
            </div>
            <div class="pagination-right">
              <button
                class="page-btn"
                :class="{ disabled: currentPage === 1 }"
                @click="handlePrevPage"
              >
                上一页
              </button>
              <button
                v-for="page in displayPages"
                :key="page"
                class="page-btn"
                :class="{ active: currentPage === page }"
                @click="handlePageChange(page)"
              >
                {{ page }}
              </button>
              <button
                class="page-btn"
                :class="{ disabled: currentPage === totalPages }"
                @click="handleNextPage"
              >
                下一页
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getInsiderList } from '@/api/admin/insider'

export default {
  name: 'InsiderList',
  data() {
    return {
      showAllSearch: true,
      loading: false,
      searchForm: {
        companyName: '',
        industry: '',
        board: '',
        financingType: '',
        acceptTimeStart: '',
        acceptTimeEnd: '',
        knowledgeTimeStart: '',
        knowledgeTimeEnd: '',
        registerTimeStart: '',
        registerTimeEnd: '',
        insiderName: '',
        reason: '',
        content: ''
      },
      currentPage: 1,
      pageSize: 10,
      total: 0,
      tableData: []
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.pageSize)
    },
    displayPages() {
      const pages = []
      const maxDisplay = 7
      if (this.totalPages <= maxDisplay) {
        for (let i = 1; i <= this.totalPages; i++) {
          pages.push(i)
        }
      } else {
        pages.push(1)
        if (this.currentPage > 3) {
          pages.push('...')
        }
        const start = Math.max(2, this.currentPage - 1)
        const end = Math.min(this.totalPages - 1, this.currentPage + 1)
        for (let i = start; i <= end; i++) {
          pages.push(i)
        }
        if (this.currentPage < this.totalPages - 2) {
          pages.push('...')
        }
        pages.push(this.totalPages)
      }
      return pages
    }
  },
  created() {
    this.fetchData()
  },
  watch: {
    currentPage() {
      this.fetchData()
    },
    pageSize() {
      this.fetchData()
    }
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const params = {
          page: this.currentPage,
          pageSize: this.pageSize,
          ...this.searchForm
        }
        // 移除空值参数
        Object.keys(params).forEach(key => {
          if (params[key] === '' || params[key] === null || params[key] === undefined) {
            delete params[key]
          }
        })
        const res = await getInsiderList(params)
        if (res.code === 200) {
          this.tableData = res.data.records || []
          this.total = res.data.total || 0
        }
      } catch (error) {
        console.error('获取数据失败:', error)
      } finally {
        this.loading = false
      }
    },
    getStatusClass(status) {
      const statusMap = {
        'draft': 'status-draft',
        'approved': 'status-approved',
        'rejected': 'status-rejected',
        'pending': 'status-pending',
        'submitted': 'status-submitted',
        'approved_submitted': 'status-approved-submitted'
      }
      return statusMap[status] || 'status-draft'
    },
    getStatusLabel(status) {
      const statusMap = {
        'draft': '草稿',
        'approved': '审核通过',
        'rejected': '审核不通过',
        'pending': '待审核',
        'submitted': '已提交',
        'approved_submitted': '审核通过（已提交）'
      }
      return statusMap[status] || status
    },
    toggleSearch() {
      this.showAllSearch = !this.showAllSearch
    },
    handleReset() {
      Object.keys(this.searchForm).forEach(key => {
        this.searchForm[key] = ''
      })
      this.currentPage = 1
      this.fetchData()
    },
    handleSearch() {
      this.currentPage = 1
      this.fetchData()
    },
    handlePageChange(page) {
      if (page === '...') return
      this.currentPage = page
    },
    handlePrevPage() {
      if (this.currentPage > 1) {
        this.currentPage--
      }
    },
    handleNextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
      }
    },
    handlePageSizeChange() {
      this.currentPage = 1
    }
  }
}
</script>

<style scoped>
.insider-page {
  min-width: 1200px;
  font-family: "Microsoft YaHei", "微软雅黑", sans-serif;
  background: #f0f2f5;
  color: #191919;
  font-size: 14px;
  min-height: 100vh;
}

.security-bar {
  background: #e8e8e8;
  text-align: center;
  padding: 4px 0;
  font-size: 12px;
  color: #494949;
  font-weight: 700;
  letter-spacing: 2px;
}

.top-nav {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 42px;
  border-bottom: 1px solid #e8e8e8;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.nav-logo {
  font-size: 15px;
  font-weight: 700;
  color: #191919;
}

.nav-links {
  display: flex;
  gap: 20px;
}

.nav-links a {
  text-decoration: none;
  color: #49495b;
  font-size: 14px;
}

.nav-links a:hover {
  color: #1890ff;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #49495b;
}

.nav-right a {
  color: #49495b;
  text-decoration: none;
  font-size: 13px;
}

.nav-right a:hover {
  color: #1890ff;
}

.nav-right .sep {
  color: #d9d9d9;
}

.update-log {
  font-size: 13px;
  color: rgba(79, 79, 79, 0.86);
}

.workspace-header {
  background: #fff;
  padding: 12px 24px;
  border-bottom: 1px solid #e8e8e8;
}

.workspace-title {
  font-size: 18px;
  font-weight: 700;
  color: #191919;
}

.main-layout {
  display: flex;
  min-height: calc(100vh - 100px);
}

.sidebar {
  width: 180px;
  background: #fff;
  border-right: 1px solid #e8e8e8;
  padding: 16px 0;
  flex-shrink: 0;
}

.menu-item {
  padding: 10px 24px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: all 0.15s;
}

.menu-item:hover {
  background: #f0f5ff;
}

.menu-item.active {
  background: #e6f7ff;
  color: #1890ff;
  font-weight: 500;
  border-right: 3px solid #1890ff;
}

.menu-group-title {
  padding: 8px 24px 4px;
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.content {
  flex: 1;
  background: #f5f5f5;
}

.content-header {
  background: #fff;
  padding: 14px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e8e8e8;
}

.page-title {
  font-size: 18px;
  font-weight: 500;
  color: #191919;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.content-body {
  padding: 16px 24px;
}

.search-form {
  background: #fff;
  border-radius: 4px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.search-row {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.search-row:last-child {
  margin-bottom: 0;
}

.form-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.form-label {
  font-size: 14px;
  color: #4a4a4a;
  white-space: nowrap;
  min-width: 70px;
}

.form-input {
  width: 180px;
  height: 32px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 0 10px;
  font-size: 14px;
  color: #333;
  background: #fff;
  outline: none;
  transition: border-color 0.2s;
}

.form-input:focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.form-input::placeholder {
  color: #bfbfbf;
}

.form-select {
  width: 180px;
  height: 32px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 0 10px;
  font-size: 14px;
  color: #333;
  background: #fff;
  outline: none;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23999' d='M2 4l4 4 4-4'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;
  cursor: pointer;
}

.form-select:focus {
  border-color: #1890ff;
}

.search-actions {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 5px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  border: 1px solid #d9d9d9;
  background: #fff;
  color: #333;
  transition: all 0.2s;
  height: 32px;
  white-space: nowrap;
}

.btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.btn-primary {
  background: #1890ff;
  color: #fff;
  border-color: #1890ff;
}

.btn-primary:hover {
  background: #40a9ff;
  border-color: #40a9ff;
  color: #fff;
}

.btn-text {
  background: none;
  border: none;
  color: #376df2;
  padding: 5px 8px;
}

.btn-text:hover {
  color: #1890ff;
  background: none;
  border: none;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.data-table th {
  background: #fafafa;
  padding: 10px 12px;
  text-align: left;
  font-weight: 600;
  color: #333;
  border: 1px solid #e8e8e8;
  font-size: 13px;
  white-space: nowrap;
}

.data-table td {
  padding: 10px 12px;
  border: 1px solid #e8e8e8;
  font-size: 13px;
  color: #333;
}

.data-table tr:hover td {
  background: #fafafa;
}

.data-table .col-idx {
  width: 50px;
  text-align: center;
}

.data-table .col-name {
  min-width: 160px;
}

.status-tag {
  display: inline-block;
  padding: 1px 8px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.8;
}

.status-draft {
  background: #f5f5f5;
  color: #999;
  border: 1px solid #d9d9d9;
}

.status-approved {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.status-rejected {
  background: #fff1f0;
  color: #f5222d;
  border: 1px solid #ffa39e;
}

.status-pending {
  background: #fff7e6;
  color: #fa8c16;
  border: 1px solid #ffd591;
}

.status-submitted {
  background: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.status-approved-submitted {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.pagination-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
  font-size: 13px;
  color: #666;
}

.pagination-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pagination-right {
  display: flex;
  align-items: center;
  gap: 6px;
}

.page-btn {
  min-width: 28px;
  height: 28px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: #fff;
  font-size: 12px;
  padding: 0 6px;
  color: #333;
}

.page-btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.page-btn.active {
  background: #1890ff;
  color: #fff;
  border-color: #1890ff;
}

.page-btn.disabled {
  color: #d9d9d9;
  cursor: not-allowed;
  border-color: #d9d9d9;
}

.page-info {
  color: #999;
}

.page-size {
  display: flex;
  align-items: center;
  gap: 4px;
}

.page-size select {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 2px 6px;
  font-size: 12px;
}
</style>
