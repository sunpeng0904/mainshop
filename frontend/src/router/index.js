import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import store from '@/store'

// 懒加载组件
const Layout = () => import('@/layout/index.vue')
const Login = () => import('@/views/login/index.vue')
const Home = () => import('@/views/home/index.vue')
const ProductList = () => import('@/views/product/list.vue')
const ProductDetail = () => import('@/views/product/detail.vue')
const Cart = () => import('@/views/cart/index.vue')
const Order = () => import('@/views/order/index.vue')
const UserCenter = () => import('@/views/user/center.vue')
const AdminDashboard = () => import('@/views/admin/dashboard.vue')
const HikingRoutes = () => import('@/views/hiking/index.vue')
const HikingDetail = () => import('@/views/hiking/detail.vue')

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: Home,
        meta: {
          title: '首页',
          requiresAuth: false,
          icon: 'House'
        }
      },
      {
        path: 'products',
        name: 'ProductList',
        component: ProductList,
        meta: {
          title: '商品列表',
          requiresAuth: false,
          icon: 'Goods'
        }
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: ProductDetail,
        meta: {
          title: '商品详情',
          requiresAuth: false,
          hideInMenu: true
        }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: Cart,
        meta: {
          title: '购物车',
          requiresAuth: true,
          icon: 'ShoppingCart'
        }
      },
      {
        path: 'order',
        name: 'Order',
        component: Order,
        meta: {
          title: '我的订单',
          requiresAuth: true,
          icon: 'Document'
        }
      },
      {
        path: 'user',
        name: 'UserCenter',
        component: UserCenter,
        meta: {
          title: '个人中心',
          requiresAuth: true,
          icon: 'User'
        }
      },
      {
        path: 'lottery',
        name: 'Lottery',
        component: () => import('@/views/lottery/index.vue'),
        meta: {
          title: '五一抽奖',
          requiresAuth: true,
          icon: 'Present'
        }
      },
      {
        path: 'hiking',
        name: 'HikingRoutes',
        component: HikingRoutes,
        meta: {
          title: '徒步路线',
          requiresAuth: false,
          icon: 'MapLocation'
        }
      },
      {
        path: 'hiking/routes/:id',
        name: 'HikingDetail',
        component: HikingDetail,
        meta: {
          title: '路线详情',
          requiresAuth: false,
          hideInMenu: true
        }
      }
    ]
  },
  {
    path: '/admin',
    component: Layout,
    redirect: '/admin/dashboard',
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: AdminDashboard,
        meta: {
          title: '管理后台',
          icon: 'Monitor',
          requiresAdmin: true
        }
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/products/index.vue'),
        meta: {
          title: '商品列表',
          icon: 'Goods',
          requiresAdmin: true
        }
      },
      {
        path: 'products/add',
        name: 'AdminProductAdd',
        component: () => import('@/views/admin/products/edit.vue'),
        meta: {
          title: '新增商品',
          hideInMenu: true,
          requiresAdmin: true
        }
      },
      {
        path: 'products/edit/:id',
        name: 'AdminProductEdit',
        component: () => import('@/views/admin/products/edit.vue'),
        meta: {
          title: '编辑商品',
          hideInMenu: true,
          requiresAdmin: true
        }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/orders/index.vue'),
        meta: {
          title: '订单列表',
          icon: 'Document',
          requiresAdmin: true
        }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/users/index.vue'),
        meta: {
          title: '用户列表',
          icon: 'User',
          requiresAdmin: true
        }
      },
      {
        path: 'roles',
        name: 'AdminRoles',
        component: () => import('@/views/admin/roles/index.vue'),
        meta: {
          title: '角色管理',
          icon: 'Key',
          requiresAdmin: true
        }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/categories/index.vue'),
        meta: {
          title: '分类管理',
          icon: 'Menu',
          requiresAdmin: true
        }
      },
      {
        path: 'lottery',
        name: 'AdminLottery',
        redirect: '/admin/lottery/prizes',
        meta: {
          title: '抽奖管理',
          icon: 'Present',
          requiresAdmin: true
        },
        children: [
          {
            path: 'prizes',
            name: 'AdminLotteryPrizes',
            component: () => import('@/views/admin/lottery/prizes.vue'),
            meta: {
              title: '奖品管理',
              requiresAdmin: true
            }
          },
          {
            path: 'records',
            name: 'AdminLotteryRecords',
            component: () => import('@/views/admin/lottery/records.vue'),
            meta: {
              title: '中奖记录',
              requiresAdmin: true
            }
          }
        ]
      },
      {
        path: 'hiking',
        name: 'AdminHiking',
        redirect: '/admin/hiking/routes',
        meta: {
          title: '徒步管理',
          icon: 'MapLocation',
          requiresAdmin: true
        },
        children: [
          {
            path: 'routes',
            name: 'AdminHikingRoutes',
            component: () => import('@/views/admin/hiking/routes.vue'),
            meta: {
              title: '路线管理',
              requiresAdmin: true
            }
          },
          {
            path: 'routes/add',
            name: 'AdminHikingRouteAdd',
            component: () => import('@/views/admin/hiking/route-edit.vue'),
            meta: {
              title: '新增路线',
              hideInMenu: true,
              requiresAdmin: true
            }
          },
          {
            path: 'routes/edit/:id',
            name: 'AdminHikingRouteEdit',
            component: () => import('@/views/admin/hiking/route-edit.vue'),
            meta: {
              title: '编辑路线',
              hideInMenu: true,
              requiresAdmin: true
            }
          },
          {
            path: 'reviews',
            name: 'AdminHikingReviews',
            component: () => import('@/views/admin/hiking/reviews.vue'),
            meta: {
              title: '评价管理',
              requiresAdmin: true
            }
          }
        ]
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/home'
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 在线商城`
  }

  const token = store.state.user.token

  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    if (!token) {
      ElMessage.warning('请先登录')
      next('/login')
      return
    }

    // 如果没有用户信息，先获取
    if (!store.state.user.userInfo) {
      try {
        await store.dispatch('user/getUserInfo')
        // 获取购物车数据
        await store.dispatch('cart/getCartList')
      } catch (error) {
        // token过期，清除登录状态
        store.commit('user/CLEAR_USER')
        ElMessage.warning('登录已过期，请重新登录')
        next('/login')
        return
      }
    }

    // 检查是否需要管理员权限
    if (to.meta.requiresAdmin) {
      const isAdmin = store.getters['user/isAdmin']
      if (!isAdmin) {
        ElMessage.error('权限不足')
        next('/home')
        return
      }
    }
  }

  // 如果已登录且访问登录页，跳转到首页
  if (to.path === '/login') {
    if (token) {
      next('/home')
      return
    }
  }

  next()
})

// 路由错误处理
router.onError((error) => {
  console.error('路由错误:', error)
  ElMessage.error('页面加载失败，请刷新重试')
})

export default router