<template>
  <el-container class="layout-container">
    <!-- 顶部导航 -->
    <el-header class="layout-header">
      <Header />
    </el-header>

    <!-- 主体内容 -->
    <el-container class="layout-main">
      <!-- 侧边栏（仅管理后台显示） -->
      <el-aside v-if="showSidebar" width="200px" class="layout-sidebar">
        <Sidebar />
      </el-aside>

      <!-- 内容区域 -->
      <el-main class="layout-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'

const route = useRoute()

// 是否显示侧边栏（管理后台页面显示）
const showSidebar = computed(() => {
  return route.path.startsWith('/admin')
})
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  background-color: #f5f7fa;
}

.layout-header {
  padding: 0;
  height: 60px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.layout-main {
  margin-top: 60px;
  min-height: calc(100vh - 60px);
}

.layout-sidebar {
  background-color: #fff;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.05);
}

.layout-content {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
