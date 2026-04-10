<template>
  <div class="sidebar-container">
    <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      background-color="#fff"
      text-color="#303133"
      active-text-color="#409EFF"
      router
    >
      <el-menu-item index="/admin/dashboard">
        <el-icon><Monitor /></el-icon>
        <span>仪表盘</span>
      </el-menu-item>

      <el-sub-menu index="product">
        <template #title>
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </template>
        <el-menu-item index="/admin/products">商品列表</el-menu-item>
        <el-menu-item index="/admin/categories">分类管理</el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="order">
        <template #title>
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </template>
        <el-menu-item index="/admin/orders">订单列表</el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="user">
        <template #title>
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </template>
        <el-menu-item index="/admin/users">用户列表</el-menu-item>
        <el-menu-item index="/admin/roles">角色管理</el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeMenu = computed(() => {
  return route.path
})
</script>

<style lang="scss" scoped>
.sidebar-container {
  height: 100%;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}
</style>
