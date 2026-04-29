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

      <el-sub-menu index="lottery">
        <template #title>
          <el-icon><Present /></el-icon>
          <span>抽奖管理</span>
        </template>
        <el-menu-item index="/admin/lottery/prizes">奖品管理</el-menu-item>
        <el-menu-item index="/admin/lottery/records">中奖记录</el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="hiking">
        <template #title>
          <el-icon><MapLocation /></el-icon>
          <span>徒步管理</span>
        </template>
        <el-menu-item index="/admin/hiking/routes">路线管理</el-menu-item>
        <el-menu-item index="/admin/hiking/reviews">评价管理</el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { MapLocation } from '@element-plus/icons-vue'

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
