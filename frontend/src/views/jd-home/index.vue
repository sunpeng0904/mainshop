<template>
  <div class="jd-home">
    <!-- 顶部导航 -->
    <header class="jd-header">
      <div class="header-top">
        <div class="container">
          <div class="header-left">
            <span class="location">
              <el-icon><Location /></el-icon>
              北京
            </span>
            <span class="hello">Hi，欢迎来到京东！</span>
          </div>
          <div class="header-right">
            <a href="#" class="link">请登录</a>
            <a href="#" class="link">免费注册</a>
            <span class="divider">|</span>
            <a href="#" class="link">我的订单</a>
            <a href="#" class="link">我的京东</a>
            <a href="#" class="link">企业采购</a>
            <a href="#" class="link">客户服务</a>
          </div>
        </div>
      </div>

      <div class="header-main">
        <div class="container">
          <div class="logo">
            <div class="logo-text">JD</div>
            <span class="logo-slogan">多快好省</span>
          </div>

          <div class="search-box">
            <div class="search-input-wrapper">
              <input
                v-model="searchKeyword"
                type="text"
                class="search-input"
                placeholder="请输入搜索关键词"
                @keyup.enter="handleSearch"
              />
              <button class="search-btn" @click="handleSearch">
                <el-icon><Search /></el-icon>
              </button>
            </div>
            <div class="search-hot">
              <span class="hot-label">热门搜索：</span>
              <a href="#" class="hot-item">手机</a>
              <a href="#" class="hot-item">笔记本</a>
              <a href="#" class="hot-item">家电</a>
              <a href="#" class="hot-item">服装</a>
              <a href="#" class="hot-item">美妆</a>
            </div>
          </div>

          <div class="header-cart">
            <el-badge :value="cartCount" :hidden="cartCount === 0">
              <el-button class="cart-btn">
                <el-icon size="20"><ShoppingCart /></el-icon>
                <span>购物车</span>
              </el-button>
            </el-badge>
          </div>
        </div>
      </div>

      <nav class="header-nav">
        <div class="container">
          <div class="nav-categories" @mouseenter="showCategories = true" @mouseleave="showCategories = false">
            <span class="nav-title">
              <el-icon><Menu /></el-icon>
              全部商品分类
            </span>
          </div>
          <div class="nav-links">
            <a href="#" class="nav-link active">首页</a>
            <a href="#" class="nav-link">服装城</a>
            <a href="#" class="nav-link">美妆馆</a>
            <a href="#" class="nav-link">超市</a>
            <a href="#" class="nav-link">生鲜</a>
            <a href="#" class="nav-link">全球购</a>
            <a href="#" class="nav-link">闪购</a>
            <a href="#" class="nav-link">拍卖</a>
            <a href="#" class="nav-link">金融</a>
          </div>
        </div>
      </nav>
    </header>

    <!-- 主要内容区 -->
    <main class="jd-main">
      <div class="container">
        <!-- 左侧分类菜单 + 中间轮播图 + 右侧信息 -->
        <div class="main-banner">
          <!-- 左侧分类菜单 -->
          <div class="category-menu" @mouseenter="showCategories = true" @mouseleave="showCategories = false">
            <div
              v-for="(category, index) in categoryList"
              :key="index"
              class="category-item"
              @mouseenter="activeCategory = index"
            >
              <el-icon><component :is="category.icon" /></el-icon>
              <span class="category-name">{{ category.name }}</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </div>
          </div>

          <!-- 中间轮播图 -->
          <div class="banner-carousel">
            <el-carousel height="400px" :interval="5000">
              <el-carousel-item v-for="banner in banners" :key="banner.id">
                <div class="banner-item" :style="{ background: banner.bg }">
                  <div class="banner-content">
                    <h2>{{ banner.title }}</h2>
                    <p>{{ banner.desc }}</p>
                    <el-button type="danger" size="large">立即抢购</el-button>
                  </div>
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>

          <!-- 右侧用户信息 -->
          <div class="user-panel">
            <div class="user-info">
              <el-avatar :size="60" src="https://via.placeholder.com/60" />
              <p class="user-greeting">Hi，欢迎来到京东！</p>
              <div class="user-actions">
                <el-button type="danger" size="small" plain>登录</el-button>
                <el-button size="small">注册</el-button>
              </div>
            </div>
            <div class="user-services">
              <div class="service-item" v-for="service in userServices" :key="service.name">
                <el-icon :size="20"><component :is="service.icon" /></el-icon>
                <span>{{ service.name }}</span>
              </div>
            </div>
            <div class="news-list">
              <div class="news-header">
                <span class="news-tab active">促销</span>
                <span class="news-tab">公告</span>
                <a href="#" class="news-more">更多</a>
              </div>
              <ul class="news-content">
                <li v-for="(news, index) in newsList" :key="index">
                  <a href="#">{{ news }}</a>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 快捷入口 -->
        <div class="quick-entries">
          <div class="entry-item" v-for="entry in quickEntries" :key="entry.name">
            <div class="entry-icon" :style="{ background: entry.color }">
              <el-icon :size="24"><component :is="entry.icon" /></el-icon>
            </div>
            <span class="entry-name">{{ entry.name }}</span>
          </div>
        </div>

        <!-- 秒杀专区 -->
        <section class="section seckill-section">
          <div class="section-header">
            <div class="section-title">
              <el-icon size="24" color="#e4393c"><Timer /></el-icon>
              <h3>京东秒杀</h3>
              <div class="countdown">
                <span class="countdown-label">距离结束还剩</span>
                <div class="countdown-time">
                  <span class="time-block">{{ countdown.hours }}</span>
                  <span class="time-sep">:</span>
                  <span class="time-block">{{ countdown.minutes }}</span>
                  <span class="time-sep">:</span>
                  <span class="time-block">{{ countdown.seconds }}</span>
                </div>
              </div>
            </div>
            <a href="#" class="more-link">
              更多秒杀
              <el-icon><ArrowRight /></el-icon>
            </a>
          </div>
          <div class="seckill-list">
            <div class="seckill-item" v-for="item in seckillProducts" :key="item.id">
              <div class="seckill-img">
                <img :src="item.image" :alt="item.name" />
              </div>
              <p class="seckill-name">{{ item.name }}</p>
              <div class="seckill-price">
                <span class="current-price">¥{{ item.price }}</span>
                <span class="original-price">¥{{ item.originalPrice }}</span>
              </div>
              <div class="seckill-progress">
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: item.progress + '%' }"></div>
                </div>
                <span class="progress-text">已抢{{ item.progress }}%</span>
              </div>
            </div>
          </div>
        </section>

        <!-- 优惠券专区 -->
        <section class="section coupon-section">
          <div class="section-header">
            <div class="section-title">
              <h3>领券中心</h3>
            </div>
            <a href="#" class="more-link">
              更多优惠券
              <el-icon><ArrowRight /></el-icon>
            </a>
          </div>
          <div class="coupon-list">
            <div class="coupon-item" v-for="coupon in coupons" :key="coupon.id">
              <div class="coupon-left">
                <span class="coupon-value">
                  <span class="coupon-symbol">¥</span>
                  <span class="coupon-amount">{{ coupon.amount }}</span>
                </span>
                <span class="coupon-condition">满{{ coupon.condition }}可用</span>
              </div>
              <div class="coupon-right">
                <p class="coupon-name">{{ coupon.name }}</p>
                <p class="coupon-time">{{ coupon.time }}</p>
                <el-button type="danger" size="small" class="coupon-btn">立即领取</el-button>
              </div>
            </div>
          </div>
        </section>

        <!-- 推荐商品 -->
        <section class="section recommend-section">
          <div class="section-header">
            <div class="section-title">
              <h3>为你推荐</h3>
            </div>
            <div class="section-tabs">
              <span
                v-for="tab in recommendTabs"
                :key="tab"
                class="tab-item"
                :class="{ active: activeTab === tab }"
                @click="activeTab = tab"
              >
                {{ tab }}
              </span>
            </div>
          </div>
          <div class="product-grid">
            <div class="product-card" v-for="product in recommendProducts" :key="product.id">
              <div class="product-img">
                <img :src="product.image" :alt="product.name" />
                <span class="product-tag" v-if="product.tag">{{ product.tag }}</span>
              </div>
              <div class="product-info">
                <p class="product-name">{{ product.name }}</p>
                <p class="product-desc">{{ product.desc }}</p>
                <div class="product-price-row">
                  <span class="product-price">¥{{ product.price }}</span>
                  <span class="product-original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
                </div>
                <div class="product-meta">
                  <span class="product-comment">评价 {{ product.comments }}</span>
                  <span class="product-sell">已售 {{ product.sold }}</span>
                </div>
                <div class="product-actions">
                  <el-button size="small" class="buy-btn">加入购物车</el-button>
                  <el-button size="small" class="collect-btn">
                    <el-icon><Star /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 品牌专区 -->
        <section class="section brand-section">
          <div class="section-header">
            <div class="section-title">
              <h3>品牌精选</h3>
            </div>
            <a href="#" class="more-link">
              更多品牌
              <el-icon><ArrowRight /></el-icon>
            </a>
          </div>
          <div class="brand-grid">
            <div class="brand-card" v-for="brand in brands" :key="brand.id">
              <div class="brand-logo">
                <img :src="brand.logo" :alt="brand.name" />
              </div>
              <p class="brand-name">{{ brand.name }}</p>
              <p class="brand-desc">{{ brand.desc }}</p>
            </div>
          </div>
        </section>
      </div>
    </main>

    <!-- 底部 -->
    <footer class="jd-footer">
      <div class="container">
        <div class="footer-service">
          <div class="service-item" v-for="service in footerServices" :key="service.name">
            <el-icon :size="28"><component :is="service.icon" /></el-icon>
            <div class="service-text">
              <h4>{{ service.name }}</h4>
              <p>{{ service.desc }}</p>
            </div>
          </div>
        </div>

        <div class="footer-links">
          <div class="link-group" v-for="group in footerLinks" :key="group.title">
            <h4>{{ group.title }}</h4>
            <ul>
              <li v-for="link in group.links" :key="link">
                <a href="#">{{ link }}</a>
              </li>
            </ul>
          </div>
        </div>

        <div class="footer-bottom">
          <p>Copyright © 2024 京东JD.com 版权所有</p>
        </div>
      </div>
    </footer>

    <!-- 回到顶部 -->
    <el-backtop :bottom="100" :right="50">
      <div class="back-top">
        <el-icon><Top /></el-icon>
      </div>
    </el-backtop>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import {
  Location, Search, ShoppingCart, Menu, ArrowRight,
  Timer, Star, Top, Goods, Iphone, Cpu, Female,
  ShoppingBag, Apple, CoffeeCup, Bowl, Bicycle,
  Monitor, Headset, Watch, School, Van, CreditCard,
  Service, Phone, ChatDotRound, User
} from '@element-plus/icons-vue'

// 搜索关键词
const searchKeyword = ref('')
const cartCount = ref(3)

// 分类菜单显示
const showCategories = ref(false)
const activeCategory = ref(-1)

// 当前标签
const activeTab = ref('精选')

// 分类列表
const categoryList = ref([
  { name: '手机 / 运营商 / 数码', icon: 'Iphone' },
  { name: '电脑 / 办公', icon: 'Monitor' },
  { name: '家用电器', icon: 'Cpu' },
  { name: '服饰 / 内衣 / 珠宝', icon: 'Female' },
  { name: '男鞋 / 运动 / 户外', icon: 'Bicycle' },
  { name: '美妆 / 个护 / 清洁', icon: 'ShoppingBag' },
  { name: '食品 / 酒类 / 生鲜', icon: 'Apple' },
  { name: '母婴 / 童装 / 玩具', icon: 'CoffeeCup' },
  { name: '家居 / 家装 / 厨具', icon: 'Bowl' },
  { name: '图书 / 文娱 / 教育', icon: 'School' },
  { name: '医药 / 保健 / 养生', icon: 'Watch' }
])

// 轮播图
const banners = ref([
  {
    id: 1,
    title: '618年中大促',
    desc: '全场商品低至5折起，更有满减优惠等你来拿',
    bg: 'linear-gradient(135deg, #e4393c 0%, #ff6b6b 100%)'
  },
  {
    id: 2,
    title: '新品首发',
    desc: '最新科技产品，抢先体验',
    bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    id: 3,
    title: '品牌闪购',
    desc: '大牌正品，限时特卖',
    bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    id: 4,
    title: 'PLUS会员日',
    desc: '会员专享价，更多优惠等你来',
    bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  }
])

// 用户服务
const userServices = ref([
  { name: '我的订单', icon: 'Document' },
  { name: '我的收藏', icon: 'Star' },
  { name: '我的优惠券', icon: 'CreditCard' },
  { name: '我的足迹', icon: 'Van' }
])

// 新闻列表
const newsList = ref([
  '618大促预售开启，定金翻倍',
  'PLUS会员专享9.5折',
  '新人首单立减20元',
  '京东超市满199减100'
])

// 快捷入口
const quickEntries = ref([
  { name: '京东超市', icon: 'ShoppingCart', color: '#e4393c' },
  { name: '数码家电', icon: 'Iphone', color: '#ff6b6b' },
  { name: '服装服饰', icon: 'Female', color: '#f5576c' },
  { name: '美妆护肤', icon: 'ShoppingBag', color: '#f093fb' },
  { name: '京东生鲜', icon: 'Apple', color: '#43e97b' },
  { name: '充值缴费', icon: 'Phone', color: '#38f9d7' },
  { name: '京东到家', icon: 'Van', color: '#667eea' },
  { name: '领券中心', icon: 'CreditCard', color: '#764ba2' },
  { name: 'PLUS会员', icon: 'User', color: '#f9d423' },
  { name: '京东拍卖', icon: 'Gavel', color: '#a18cd1' }
])

// 秒杀倒计时
const countdown = reactive({
  hours: '02',
  minutes: '34',
  seconds: '56'
})

let countdownTimer = null

const startCountdown = () => {
  let totalSeconds = 2 * 3600 + 34 * 60 + 56

  countdownTimer = setInterval(() => {
    totalSeconds--
    if (totalSeconds <= 0) {
      clearInterval(countdownTimer)
      return
    }

    const hours = Math.floor(totalSeconds / 3600)
    const minutes = Math.floor((totalSeconds % 3600) / 60)
    const seconds = totalSeconds % 60

    countdown.hours = String(hours).padStart(2, '0')
    countdown.minutes = String(minutes).padStart(2, '0')
    countdown.seconds = String(seconds).padStart(2, '0')
  }, 1000)
}

// 秒杀商品
const seckillProducts = ref([
  {
    id: 1,
    name: 'Apple iPhone 15 Pro Max 256GB',
    image: 'https://via.placeholder.com/200x200?text=iPhone',
    price: 8999,
    originalPrice: 9999,
    progress: 78
  },
  {
    id: 2,
    name: '华为 Mate 60 Pro 512GB',
    image: 'https://via.placeholder.com/200x200?text=Huawei',
    price: 6999,
    originalPrice: 7999,
    progress: 65
  },
  {
    id: 3,
    name: '小米14 Ultra 16GB+512GB',
    image: 'https://via.placeholder.com/200x200?text=Xiaomi',
    price: 5999,
    originalPrice: 6499,
    progress: 82
  },
  {
    id: 4,
    name: '戴森 V15 Detect 吸尘器',
    image: 'https://via.placeholder.com/200x200?text=Dyson',
    price: 3999,
    originalPrice: 4999,
    progress: 45
  },
  {
    id: 5,
    name: '索尼 WH-1000XM5 降噪耳机',
    image: 'https://via.placeholder.com/200x200?text=Sony',
    price: 1999,
    originalPrice: 2999,
    progress: 91
  }
])

// 优惠券
const coupons = ref([
  { id: 1, amount: 50, condition: 299, name: '全品类通用券', time: '2024.06.01-2024.06.30' },
  { id: 2, amount: 100, condition: 599, name: '数码家电专享券', time: '2024.06.01-2024.06.30' },
  { id: 3, amount: 200, condition: 999, name: '手机专区券', time: '2024.06.01-2024.06.30' },
  { id: 4, amount: 30, condition: 199, name: '超市专享券', time: '2024.06.01-2024.06.30' }
])

// 推荐标签
const recommendTabs = ref(['精选', '手机', '电脑', '家电', '服装', '美妆'])

// 推荐商品
const recommendProducts = ref([
  {
    id: 1,
    name: 'Apple MacBook Pro 14英寸 M3 Pro芯片',
    desc: '16GB+512GB 深空黑色',
    image: 'https://via.placeholder.com/220x220?text=MacBook',
    price: 14999,
    originalPrice: 16999,
    tag: '自营',
    comments: '10万+',
    sold: '5万+'
  },
  {
    id: 2,
    name: '华为 MatePad Pro 12.6英寸',
    desc: '8GB+256GB WiFi版',
    image: 'https://via.placeholder.com/220x220?text=MatePad',
    price: 4999,
    originalPrice: 5499,
    tag: '新品',
    comments: '5万+',
    sold: '2万+'
  },
  {
    id: 3,
    name: '索尼 PS5 国行光驱版',
    desc: '双手柄套装',
    image: 'https://via.placeholder.com/220x220?text=PS5',
    price: 3899,
    originalPrice: 4299,
    tag: '热卖',
    comments: '8万+',
    sold: '10万+'
  },
  {
    id: 4,
    name: '戴尔 27英寸 4K显示器',
    desc: 'Type-C 90W反向充电',
    image: 'https://via.placeholder.com/220x220?text=Dell',
    price: 3299,
    originalPrice: 3999,
    tag: '',
    comments: '2万+',
    sold: '1万+'
  },
  {
    id: 5,
    name: 'Airpods Pro 第二代',
    desc: 'MagSafe充电盒',
    image: 'https://via.placeholder.com/220x220?text=Airpods',
    price: 1599,
    originalPrice: 1899,
    tag: '自营',
    comments: '50万+',
    sold: '100万+'
  },
  {
    id: 6,
    name: '小米 Redmi Note 13 Pro+',
    desc: '12GB+256GB',
    image: 'https://via.placeholder.com/220x220?text=Redmi',
    price: 1999,
    originalPrice: 2199,
    tag: '新品',
    comments: '3万+',
    sold: '8万+'
  },
  {
    id: 7,
    name: '罗技 MX Master 3S 鼠标',
    desc: '无线蓝牙办公鼠标',
    image: 'https://via.placeholder.com/220x220?text=Logitech',
    price: 699,
    originalPrice: 899,
    tag: '',
    comments: '10万+',
    sold: '20万+'
  },
  {
    id: 8,
    name: 'Kindle Paperwhite 5',
    desc: '8GB 墨黑色',
    image: 'https://via.placeholder.com/220x220?text=Kindle',
    price: 998,
    originalPrice: 1098,
    tag: '',
    comments: '15万+',
    sold: '30万+'
  },
  {
    id: 9,
    name: '飞利浦 电动牙刷 HX6730',
    desc: '含2支刷头',
    image: 'https://via.placeholder.com/220x220?text=Philips',
    price: 299,
    originalPrice: 399,
    tag: '热卖',
    comments: '30万+',
    sold: '50万+'
  },
  {
    id: 10,
    name: '任天堂 Switch OLED版',
    desc: '红蓝手柄',
    image: 'https://via.placeholder.com/220x220?text=Switch',
    price: 2399,
    originalPrice: 2599,
    tag: '自营',
    comments: '20万+',
    sold: '15万+'
  }
])

// 品牌列表
const brands = ref([
  { id: 1, name: 'Apple', logo: 'https://via.placeholder.com/120x60?text=Apple', desc: 'iPhone、Mac、iPad' },
  { id: 2, name: '华为', logo: 'https://via.placeholder.com/120x60?text=Huawei', desc: '手机、平板、穿戴' },
  { id: 3, name: '小米', logo: 'https://via.placeholder.com/120x60?text=Xiaomi', desc: '手机、智能家居' },
  { id: 4, name: '三星', logo: 'https://via.placeholder.com/120x60?text=Samsung', desc: '手机、显示器' },
  { id: 5, name: '索尼', logo: 'https://via.placeholder.com/120x60?text=Sony', desc: '游戏、音频' },
  { id: 6, name: '戴尔', logo: 'https://via.placeholder.com/120x60?text=Dell', desc: '电脑、显示器' }
])

// 底部服务
const footerServices = ref([
  { name: '正品保障', desc: '正品行货，放心选购', icon: 'Service' },
  { name: '极速物流', desc: '多仓直发，极速配送', icon: 'Van' },
  { name: '售后无忧', desc: '7天无理由退换', icon: 'Phone' },
  { name: '品质服务', desc: '专属客服，贴心服务', icon: 'ChatDotRound' }
])

// 底部链接
const footerLinks = ref([
  {
    title: '购物指南',
    links: ['购物流程', '会员介绍', '生活旅行', '常见问题', '大家电', '联系客服']
  },
  {
    title: '配送方式',
    links: ['上门自提', '配送服务查询', '配送费收取标准', '海外配送']
  },
  {
    title: '支付方式',
    links: ['货到付款', '在线支付', '分期付款', '公司转账']
  },
  {
    title: '售后服务',
    links: ['售后政策', '价格保护', '退款说明', '取消订单', '退换货流程']
  },
  {
    title: '特色服务',
    links: ['企业采购', '会员服务', '装机服务', '延保服务', 'DIY上门']
  }
])

// 搜索
const handleSearch = () => {
  console.log('搜索:', searchKeyword.value)
}

// 启动倒计时
onMounted(() => {
  startCountdown()
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style lang="scss" scoped>
.jd-home {
  min-height: 100vh;
  background: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
}

// 顶部导航
.jd-header {
  .header-top {
    background: #e3e4e5;
    color: #999;
    font-size: 12px;
    height: 30px;
    line-height: 30px;

    .container {
      display: flex;
      justify-content: space-between;
    }

    .header-left {
      display: flex;
      align-items: center;
      gap: 10px;

      .location {
        display: flex;
        align-items: center;
        gap: 2px;
        color: #e4393c;
        cursor: pointer;
      }
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 10px;

      .link {
        color: #999;
        text-decoration: none;

        &:hover {
          color: #e4393c;
        }
      }

      .divider {
        color: #ccc;
      }
    }
  }

  .header-main {
    background: #fff;
    padding: 10px 0;

    .container {
      display: flex;
      align-items: center;
      gap: 30px;
    }

    .logo {
      display: flex;
      align-items: center;
      gap: 8px;
      flex-shrink: 0;

      .logo-text {
        width: 120px;
        height: 60px;
        background: linear-gradient(135deg, #e4393c 0%, #ff6b6b 100%);
        color: #fff;
        font-size: 40px;
        font-weight: bold;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 4px;
      }

      .logo-slogan {
        font-size: 12px;
        color: #666;
      }
    }

    .search-box {
      flex: 1;

      .search-input-wrapper {
        display: flex;
        border: 2px solid #e4393c;
        border-radius: 4px;
        overflow: hidden;

        .search-input {
          flex: 1;
          height: 36px;
          padding: 0 12px;
          border: none;
          outline: none;
          font-size: 14px;
        }

        .search-btn {
          width: 50px;
          background: #e4393c;
          border: none;
          color: #fff;
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;

          &:hover {
            background: #c1272d;
          }
        }
      }

      .search-hot {
        margin-top: 8px;
        font-size: 12px;

        .hot-label {
          color: #999;
        }

        .hot-item {
          color: #666;
          text-decoration: none;
          margin-right: 10px;

          &:hover {
            color: #e4393c;
          }
        }
      }
    }

    .header-cart {
      flex-shrink: 0;

      .cart-btn {
        display: flex;
        align-items: center;
        gap: 5px;
        border: 1px solid #e4393c;
        color: #e4393c;
        padding: 8px 20px;

        &:hover {
          background: #e4393c;
          color: #fff;
        }
      }
    }
  }

  .header-nav {
    background: #fff;
    border-bottom: 2px solid #e4393c;

    .container {
      display: flex;
      align-items: center;
    }

    .nav-categories {
      width: 210px;
      background: #e4393c;
      color: #fff;
      padding: 10px 0;
      cursor: pointer;

      .nav-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        font-weight: 500;
      }
    }

    .nav-links {
      display: flex;
      flex: 1;
      padding-left: 20px;

      .nav-link {
        padding: 10px 20px;
        color: #333;
        text-decoration: none;
        font-size: 14px;

        &:hover,
        &.active {
          color: #e4393c;
        }
      }
    }
  }
}

// 主要内容区
.jd-main {
  padding-top: 20px;
  padding-bottom: 40px;

  .main-banner {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;

    .category-menu {
      width: 210px;
      background: rgba(0, 0, 0, 0.7);
      border-radius: 4px;
      overflow: hidden;
      flex-shrink: 0;

      .category-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 10px 15px;
        color: #fff;
        font-size: 13px;
        cursor: pointer;
        transition: all 0.2s;

        &:hover {
          background: rgba(228, 57, 60, 0.8);
        }

        .arrow {
          margin-left: auto;
          font-size: 12px;
        }
      }
    }

    .banner-carousel {
      flex: 1;
      border-radius: 4px;
      overflow: hidden;

      .banner-item {
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;

        .banner-content {
          text-align: center;
          color: #fff;

          h2 {
            font-size: 36px;
            margin-bottom: 10px;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
          }

          p {
            font-size: 18px;
            margin-bottom: 20px;
            opacity: 0.9;
          }
        }
      }
    }

    .user-panel {
      width: 210px;
      background: #fff;
      border-radius: 4px;
      padding: 15px;
      flex-shrink: 0;

      .user-info {
        text-align: center;
        padding-bottom: 15px;
        border-bottom: 1px solid #eee;

        .user-greeting {
          margin: 10px 0;
          font-size: 13px;
          color: #666;
        }

        .user-actions {
          display: flex;
          gap: 8px;
          justify-content: center;
        }
      }

      .user-services {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 10px;
        padding: 15px 0;
        border-bottom: 1px solid #eee;

        .service-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 5px;
          font-size: 12px;
          color: #666;
          cursor: pointer;

          &:hover {
            color: #e4393c;
          }
        }
      }

      .news-list {
        padding-top: 15px;

        .news-header {
          display: flex;
          align-items: center;
          margin-bottom: 10px;

          .news-tab {
            font-size: 13px;
            color: #666;
            margin-right: 15px;
            cursor: pointer;

            &.active {
              color: #e4393c;
              font-weight: 500;
            }
          }

          .news-more {
            margin-left: auto;
            font-size: 12px;
            color: #999;
            text-decoration: none;

            &:hover {
              color: #e4393c;
            }
          }
        }

        .news-content {
          list-style: none;
          padding: 0;
          margin: 0;

          li {
            padding: 5px 0;
            font-size: 12px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;

            a {
              color: #666;
              text-decoration: none;

              &:hover {
                color: #e4393c;
              }
            }
          }
        }
      }
    }
  }

  // 快捷入口
  .quick-entries {
    display: flex;
    justify-content: space-between;
    background: #fff;
    border-radius: 4px;
    padding: 20px;
    margin-bottom: 20px;

    .entry-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      cursor: pointer;

      .entry-icon {
        width: 50px;
        height: 50px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        transition: transform 0.2s;
      }

      .entry-name {
        font-size: 12px;
        color: #666;
      }

      &:hover .entry-icon {
        transform: scale(1.1);
      }
    }
  }

  // 通用区块样式
  .section {
    background: #fff;
    border-radius: 4px;
    padding: 20px;
    margin-bottom: 20px;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .section-title {
        display: flex;
        align-items: center;
        gap: 10px;

        h3 {
          font-size: 20px;
          font-weight: 600;
          color: #333;
        }
      }

      .more-link {
        color: #999;
        text-decoration: none;
        font-size: 13px;
        display: flex;
        align-items: center;
        gap: 5px;

        &:hover {
          color: #e4393c;
        }
      }

      .section-tabs {
        display: flex;
        gap: 20px;

        .tab-item {
          font-size: 14px;
          color: #666;
          cursor: pointer;
          padding-bottom: 5px;

          &:hover,
          &.active {
            color: #e4393c;
            border-bottom: 2px solid #e4393c;
          }
        }
      }
    }
  }

  // 秒杀专区
  .seckill-section {
    .section-title {
      .countdown {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-left: 20px;

        .countdown-label {
          font-size: 13px;
          color: #666;
        }

        .countdown-time {
          display: flex;
          align-items: center;
          gap: 5px;

          .time-block {
            background: #333;
            color: #fff;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 16px;
            font-weight: 600;
          }

          .time-sep {
            font-size: 16px;
            font-weight: 600;
          }
        }
      }
    }

    .seckill-list {
      display: flex;
      gap: 15px;
      overflow-x: auto;

      .seckill-item {
        width: 200px;
        flex-shrink: 0;
        text-align: center;
        padding: 15px;
        border: 1px solid #eee;
        border-radius: 4px;
        cursor: pointer;
        transition: all 0.2s;

        &:hover {
          border-color: #e4393c;
          box-shadow: 0 2px 8px rgba(228, 57, 60, 0.2);
        }

        .seckill-img {
          width: 150px;
          height: 150px;
          margin: 0 auto 10px;

          img {
            width: 100%;
            height: 100%;
            object-fit: contain;
          }
        }

        .seckill-name {
          font-size: 13px;
          color: #333;
          margin-bottom: 10px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .seckill-price {
          margin-bottom: 10px;

          .current-price {
            font-size: 20px;
            font-weight: 600;
            color: #e4393c;
          }

          .original-price {
            font-size: 12px;
            color: #999;
            text-decoration: line-through;
            margin-left: 5px;
          }
        }

        .seckill-progress {
          .progress-bar {
            height: 20px;
            background: #ffe0e0;
            border-radius: 10px;
            overflow: hidden;
            position: relative;

            .progress-fill {
              height: 100%;
              background: linear-gradient(90deg, #ff6b6b 0%, #e4393c 100%);
              border-radius: 10px;
              transition: width 0.3s;
            }
          }

          .progress-text {
            font-size: 12px;
            color: #999;
            margin-top: 5px;
          }
        }
      }
    }
  }

  // 优惠券专区
  .coupon-section {
    .coupon-list {
      display: flex;
      gap: 15px;

      .coupon-item {
        flex: 1;
        display: flex;
        border: 1px solid #e4393c;
        border-radius: 4px;
        overflow: hidden;
        transition: all 0.2s;

        &:hover {
          box-shadow: 0 2px 8px rgba(228, 57, 60, 0.2);
        }

        .coupon-left {
          width: 120px;
          background: linear-gradient(135deg, #e4393c 0%, #ff6b6b 100%);
          color: #fff;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          padding: 15px;

          .coupon-value {
            display: flex;
            align-items: baseline;

            .coupon-symbol {
              font-size: 14px;
            }

            .coupon-amount {
              font-size: 32px;
              font-weight: 600;
            }
          }

          .coupon-condition {
            font-size: 12px;
            opacity: 0.9;
          }
        }

        .coupon-right {
          flex: 1;
          padding: 15px;
          display: flex;
          flex-direction: column;
          justify-content: center;

          .coupon-name {
            font-size: 14px;
            font-weight: 500;
            color: #333;
            margin-bottom: 5px;
          }

          .coupon-time {
            font-size: 12px;
            color: #999;
            margin-bottom: 10px;
          }

          .coupon-btn {
            align-self: flex-start;
          }
        }
      }
    }
  }

  // 推荐商品
  .recommend-section {
    .product-grid {
      display: grid;
      grid-template-columns: repeat(5, 1fr);
      gap: 15px;

      .product-card {
        background: #fff;
        border: 1px solid #eee;
        border-radius: 4px;
        overflow: hidden;
        transition: all 0.2s;
        cursor: pointer;

        &:hover {
          border-color: #e4393c;
          box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
        }

        .product-img {
          position: relative;
          height: 220px;
          padding: 15px;

          img {
            width: 100%;
            height: 100%;
            object-fit: contain;
          }

          .product-tag {
            position: absolute;
            top: 10px;
            left: 10px;
            background: #e4393c;
            color: #fff;
            padding: 2px 8px;
            font-size: 12px;
            border-radius: 2px;
          }
        }

        .product-info {
          padding: 10px 15px 15px;

          .product-name {
            font-size: 13px;
            color: #333;
            margin-bottom: 5px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .product-desc {
            font-size: 12px;
            color: #999;
            margin-bottom: 10px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .product-price-row {
            display: flex;
            align-items: baseline;
            gap: 8px;
            margin-bottom: 8px;

            .product-price {
              font-size: 18px;
              font-weight: 600;
              color: #e4393c;
            }

            .product-original-price {
              font-size: 12px;
              color: #999;
              text-decoration: line-through;
            }
          }

          .product-meta {
            display: flex;
            gap: 15px;
            margin-bottom: 10px;

            span {
              font-size: 12px;
              color: #999;
            }
          }

          .product-actions {
            display: flex;
            gap: 8px;

            .buy-btn {
              flex: 1;
              background: #e4393c;
              border-color: #e4393c;
              color: #fff;

              &:hover {
                background: #c1272d;
                border-color: #c1272d;
              }
            }

            .collect-btn {
              width: 32px;
              padding: 0;
            }
          }
        }
      }
    }
  }

  // 品牌专区
  .brand-section {
    .brand-grid {
      display: grid;
      grid-template-columns: repeat(6, 1fr);
      gap: 15px;

      .brand-card {
        background: #fff;
        border: 1px solid #eee;
        border-radius: 4px;
        padding: 20px;
        text-align: center;
        cursor: pointer;
        transition: all 0.2s;

        &:hover {
          border-color: #e4393c;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        .brand-logo {
          height: 60px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 10px;

          img {
            max-width: 100%;
            max-height: 100%;
            object-fit: contain;
          }
        }

        .brand-name {
          font-size: 14px;
          font-weight: 500;
          color: #333;
          margin-bottom: 5px;
        }

        .brand-desc {
          font-size: 12px;
          color: #999;
        }
      }
    }
  }
}

// 底部
.jd-footer {
  background: #fff;
  padding-top: 40px;

  .footer-service {
    display: flex;
    justify-content: space-around;
    padding-bottom: 30px;
    border-bottom: 1px solid #eee;

    .service-item {
      display: flex;
      align-items: center;
      gap: 15px;

      .el-icon {
        color: #e4393c;
      }

      .service-text {
        h4 {
          font-size: 14px;
          font-weight: 500;
          color: #333;
          margin-bottom: 5px;
        }

        p {
          font-size: 12px;
          color: #999;
        }
      }
    }
  }

  .footer-links {
    display: flex;
    justify-content: space-around;
    padding: 30px 0;
    border-bottom: 1px solid #eee;

    .link-group {
      h4 {
        font-size: 14px;
        font-weight: 500;
        color: #333;
        margin-bottom: 15px;
      }

      ul {
        list-style: none;
        padding: 0;
        margin: 0;

        li {
          margin-bottom: 8px;

          a {
            font-size: 12px;
            color: #666;
            text-decoration: none;

            &:hover {
              color: #e4393c;
            }
          }
        }
      }
    }
  }

  .footer-bottom {
    padding: 20px 0;
    text-align: center;

    p {
      font-size: 12px;
      color: #999;
    }
  }
}

// 回到顶部
.back-top {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  color: #666;

  &:hover {
    color: #e4393c;
    border-color: #e4393c;
  }
}
</style>
