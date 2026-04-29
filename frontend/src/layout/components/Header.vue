<template>
  <div class="header-container">
    <!-- Logo -->
    <div class="header-logo" @click="$router.push('/home')">
      <el-icon :size="28" color="#409EFF"><Shop /></el-icon>
      <span class="logo-text">{{ $t('common.appName', '在线商城') }}</span>
    </div>

    <!-- 导航菜单 -->
    <div class="header-nav">
      <el-menu
        :default-active="activeMenu"
        mode="horizontal"
        :ellipsis="false"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/home">
          <el-icon><House /></el-icon>
          <span>{{ $t('nav.home') }}</span>
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><Goods /></el-icon>
          <span>{{ $t('nav.products') }}</span>
        </el-menu-item>
        <el-menu-item index="/hiking">
          <el-icon><MapLocation /></el-icon>
          <span>徒步路线</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 右侧操作区 -->
    <div class="header-actions">
      <!-- 搜索框 -->
      <el-input
        v-model="searchKeyword"
        :placeholder="$t('product.searchPlaceholder')"
        class="search-input"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <!-- 语言切换 -->
      <el-dropdown @command="handleLanguageChange" trigger="click">
        <el-button text>
          <el-icon :size="18"><Promotion /></el-icon>
          <span class="lang-text">{{ currentLangLabel }}</span>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="zh" :class="{ active: currentLang === 'zh' }">
              中文
            </el-dropdown-item>
            <el-dropdown-item command="en" :class="{ active: currentLang === 'en' }">
              English
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <!-- 购物车 -->
      <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
        <el-button text @click="$router.push('/cart')">
          <el-icon :size="20"><ShoppingCart /></el-icon>
        </el-button>
      </el-badge>

      <!-- 五一抽奖活动入口 -->
      <el-button type="danger" class="lottery-btn" @click="$router.push('/lottery')">
        <el-icon><Present /></el-icon>
        <span>五一抽奖</span>
      </el-button>

      <!-- 用户菜单 -->
      <template v-if="isLogin">
        <el-dropdown @command="handleUserCommand">
          <span class="user-dropdown">
            <el-avatar :size="32" :src="userInfo?.avatar">
              {{ userInfo?.nickname || userInfo?.username?.charAt(0) }}
            </el-avatar>
            <span class="username">{{ userInfo?.nickname || userInfo?.username }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="user">
                <el-icon><User /></el-icon>
                {{ $t('user.profile') }}
              </el-dropdown-item>
              <el-dropdown-item command="order">
                <el-icon><Document /></el-icon>
                {{ $t('nav.orders') }}
              </el-dropdown-item>
              <el-dropdown-item command="admin" v-if="isAdmin">
                <el-icon><Monitor /></el-icon>
                {{ $t('nav.admin') }}
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                {{ $t('common.logout') }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
      <template v-else>
        <el-button type="primary" @click="$router.push('/login')">
          {{ $t('common.login') }}
        </el-button>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Present, MapLocation } from '@element-plus/icons-vue'
import { setLanguage } from '@/locales'

const { t, locale } = useI18n()
const store = useStore()
const router = useRouter()
const route = useRoute()

const searchKeyword = ref('')

// 当前语言
const currentLang = computed(() => locale.value)
const currentLangLabel = computed(() => {
  return locale.value === 'zh' ? '中文' : 'EN'
})

// 当前激活的菜单
const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/product')) return '/products'
  return path
})

// 是否登录
const isLogin = computed(() => store.getters['user/isLogin'])

// 用户信息
const userInfo = computed(() => store.state.user.userInfo)

// 是否管理员
const isAdmin = computed(() => store.getters['user/isAdmin'])

// 购物车数量
const cartCount = computed(() => store.state.cart?.cartCount || 0)

// 菜单选择
const handleMenuSelect = (index) => {
  router.push(index)
}

// 搜索
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/products',
      query: { keyword: searchKeyword.value.trim() }
    })
  }
}

// 语言切换
const handleLanguageChange = (lang) => {
  setLanguage(lang)
  ElMessage.success(lang === 'zh' ? '已切换为中文' : 'Switched to English')
}

// 用户菜单命令
const handleUserCommand = async (command) => {
  switch (command) {
    case 'user':
      router.push('/user')
      break
    case 'order':
      router.push('/order')
      break
    case 'admin':
      router.push('/admin/dashboard')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm(t('auth.logoutConfirm') || '确定要退出登录吗？', t('common.confirm'), {
          confirmButtonText: t('common.confirm'),
          cancelButtonText: t('common.cancel'),
          type: 'warning'
        })
        await store.dispatch('user/logout')
        ElMessage.success(t('auth.logoutSuccess'))
        router.push('/home')
      } catch (e) {
        // 取消退出
      }
      break
  }
}
</script>

<style lang="scss" scoped>
.header-container {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.header-logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 40px;

  .logo-text {
    font-size: 20px;
    font-weight: bold;
    margin-left: 8px;
    color: #303133;
  }
}

.header-nav {
  flex: 1;

  :deep(.el-menu) {
    border-bottom: none;
    background: transparent;
  }

  :deep(.el-menu-item) {
    font-size: 15px;
  }
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 180px;
}

.lang-text {
  margin-left: 4px;
  font-size: 14px;
}

.cart-badge {
  cursor: pointer;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 8px;

  .username {
    margin: 0 8px;
    font-size: 14px;
    color: #606266;
  }
}

:deep(.el-dropdown-menu__item.active) {
  color: #409EFF;
  background-color: #ecf5ff;
}

.lottery-btn {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  border: none;
  animation: pulse 2s infinite;

  &:hover {
    background: linear-gradient(135deg, #FF5252 0%, #FF7043 100%);
  }

  .el-icon {
    margin-right: 4px;
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}
</style>
