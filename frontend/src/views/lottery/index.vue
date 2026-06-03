<template>
  <div class="lottery-page">
    <!-- 动态背景 -->
    <div class="bg-animation">
      <div class="bg-gradient"></div>
      <div class="floating-shapes">
        <span v-for="i in 15" :key="i" class="shape" :style="getShapeStyle(i)"></span>
      </div>
      <div class="particles">
        <span v-for="i in 30" :key="i" class="particle"></span>
      </div>
    </div>

    <!-- 顶部横幅 -->
    <header class="page-header">
      <div class="header-content">
        <div class="festival-badge">
          <span class="badge-icon">🎊</span>
          <span class="badge-text">五一劳动节</span>
        </div>
        <h1 class="main-title">
          <span class="title-line">幸运大转盘</span>
          <span class="title-glow">幸运大转盘</span>
        </h1>
        <p class="subtitle">
          <span class="highlight">豪礼送不停</span>
          <span class="divider">|</span>
          <span class="highlight">百分百中奖</span>
        </p>
      </div>
      <div class="header-decoration">
        <div class="confetti">
          <span v-for="i in 20" :key="i" class="confetti-piece"></span>
        </div>
      </div>
    </header>

    <!-- 主要内容 -->
    <main class="page-main">
      <!-- 剩余次数卡片 -->
      <div class="times-card">
        <div class="times-bg"></div>
        <div class="times-content">
          <div class="times-label">今日剩余次数</div>
          <div class="times-value">
            <span class="times-number">{{ remainingTimes }}</span>
            <span class="times-unit">次</span>
          </div>
        </div>
        <div class="times-decoration">
          <span class="star">⭐</span>
          <span class="star">⭐</span>
          <span class="star">⭐</span>
        </div>
      </div>

      <!-- 转盘区域 -->
      <section class="wheel-section">
        <div class="wheel-glow"></div>
        <LotteryWheel ref="wheelRef" @success="handleLotterySuccess" />
      </section>

      <!-- 奖品展示 -->
      <section class="prizes-section">
        <h2 class="section-title">
          <span class="title-icon">🎁</span>
          <span>奖品设置</span>
        </h2>
        <div class="prizes-grid">
          <div
            v-for="prize in prizeList"
            :key="prize.level"
            class="prize-card"
            :class="'prize-level-' + prize.level"
          >
            <div class="prize-medal">{{ prize.medal }}</div>
            <div class="prize-icon">{{ prize.icon }}</div>
            <div class="prize-name">{{ prize.name }}</div>
            <div class="prize-prob">中奖率 {{ prize.prob }}</div>
            <div class="prize-value">价值 ¥{{ prize.value }}</div>
          </div>
        </div>
      </section>

      <!-- 我的奖品 -->
      <section class="records-section">
        <h2 class="section-title">
          <span class="title-icon">🏆</span>
          <span>我的奖品</span>
        </h2>
        <div v-if="records.length === 0" class="empty-state">
          <div class="empty-icon">🎁</div>
          <p>暂无中奖记录</p>
          <p class="empty-tip">快去抽奖吧，好运等着你！</p>
        </div>
        <div v-else class="records-list">
          <div
            v-for="record in records"
            :key="record.id"
            class="record-card"
            :class="{ 'is-win': record.prizeLevel < 4 }"
          >
            <div class="record-left">
              <div class="record-icon">{{ getPrizeIcon(record.prizeLevel) }}</div>
              <div class="record-info">
                <div class="record-name">{{ record.prizeName }}</div>
                <div class="record-time">{{ formatTime(record.lotteryTime) }}</div>
              </div>
            </div>
            <div class="record-right">
              <template v-if="record.prizeLevel < 4">
                <button
                  v-if="record.receiveStatus === 0"
                  class="receive-btn"
                  @click="handleReceive(record)"
                >
                  领取
                </button>
                <span v-else class="received-tag">已领取</span>
              </template>
              <span v-else class="thanks-tag">已参与</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 活动规则 -->
      <section class="rules-section">
        <h2 class="section-title">
          <span class="title-icon">📋</span>
          <span>活动规则</span>
        </h2>
        <div class="rules-content">
          <ul class="rules-list">
            <li>
              <span class="rule-icon">📅</span>
              <span>活动时间：2026年5月1日 - 2026年5月7日</span>
            </li>
            <li>
              <span class="rule-icon">🎲</span>
              <span>每位用户每天可获得 <strong>3次</strong> 免费抽奖机会</span>
            </li>
            <li>
              <span class="rule-icon">🎁</span>
              <span>奖品以实际发放为准，图片仅供参考</span>
            </li>
            <li>
              <span class="rule-icon">⏰</span>
              <span>中奖后请在 <strong>7天内</strong> 领取，逾期作废</span>
            </li>
            <li>
              <span class="rule-icon">⚖️</span>
              <span>本活动最终解释权归平台所有</span>
            </li>
          </ul>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import LotteryWheel from '@/components/lottery-wheel.vue'
import { getRemainingTimes, getMyRecords, receivePrize } from '@/api/lottery'

const wheelRef = ref(null)
const remainingTimes = ref(0)
const records = ref([])

// 奖品列表展示
const prizeList = [
  { level: 1, medal: '🥇', icon: '💻', name: '笔记本电脑', prob: '0.1%', value: '5000' },
  { level: 2, medal: '🥈', icon: '📱', name: '智能手机', prob: '1%', value: '2000' },
  { level: 3, medal: '🥉', icon: '🥤', name: '保温杯', prob: '5%', value: '100' },
  { level: 4, medal: '🎖️', icon: '🎁', name: '谢谢参与', prob: '93.9%', value: '0' }
]

// 获取随机样式
const getShapeStyle = (i) => ({
  '--delay': `${i * 0.5}s`,
  '--duration': `${15 + i * 2}s`,
  '--x': `${Math.random() * 100}%`,
  '--size': `${20 + Math.random() * 40}px`,
  '--color': ['#FF6B6B', '#4ECDC4', '#FFE66D', '#95E1D3', '#F38181'][i % 5]
})

// 获取剩余次数
const fetchRemainingTimes = async () => {
  try {
    const res = await getRemainingTimes()
    remainingTimes.value = res.data || 0
  } catch (error) {
    console.error('获取剩余次数失败:', error)
  }
}

// 获取中奖记录
const fetchRecords = async () => {
  try {
    const res = await getMyRecords()
    records.value = res.data || []
  } catch (error) {
    console.error('获取中奖记录失败:', error)
  }
}

// 抽奖成功回调
const handleLotterySuccess = (result) => {
  remainingTimes.value = result.remainingTimes
  fetchRecords()
}

// 领取奖品
const handleReceive = async (record) => {
  try {
    await receivePrize(record.id)
    ElMessage.success('领取成功，请联系客服发货')
    fetchRecords()
  } catch (error) {
    ElMessage.error(error.message || '领取失败')
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').slice(0, 16)
}

// 获取奖品图标
const getPrizeIcon = (level) => {
  const icons = { 1: '💻', 2: '📱', 3: '🥤', 4: '🎁' }
  return icons[level] || '🎁'
}

onMounted(() => {
  fetchRemainingTimes()
  fetchRecords()
})
</script>

<style lang="scss" scoped>
.lottery-page {
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  background: #0f0f23;
}

// 动态背景
.bg-animation {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-gradient {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at top, rgba(255, 107, 107, 0.15) 0%, transparent 50%),
    radial-gradient(ellipse at bottom, rgba(78, 205, 196, 0.1) 0%, transparent 50%),
    linear-gradient(180deg, #0f0f23 0%, #1a1a2e 50%, #0f0f23 100%);
}

.floating-shapes {
  position: absolute;
  inset: 0;

  .shape {
    position: absolute;
    border-radius: 50%;
    opacity: 0.3;
    animation: float-shape var(--duration) ease-in-out infinite;
    animation-delay: var(--delay);
    left: var(--x);
    width: var(--size);
    height: var(--size);
    background: var(--color);
    filter: blur(20px);
  }
}

.particles {
  position: absolute;
  inset: 0;

  .particle {
    position: absolute;
    width: 3px;
    height: 3px;
    background: #FFD700;
    border-radius: 50%;
    animation: particle-rise 8s linear infinite;

    @for $i from 1 through 30 {
      &:nth-child(#{$i}) {
        left: #{random(100)}%;
        animation-delay: #{$i * 0.3}s;
        animation-duration: #{6 + random(4)}s;
      }
    }
  }
}

@keyframes float-shape {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-50px) scale(1.1); }
}

@keyframes particle-rise {
  0% { transform: translateY(100vh) scale(0); opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { transform: translateY(-100px) scale(1); opacity: 0; }
}

// 页面头部
.page-header {
  position: relative;
  padding: 40px 20px 60px;
  text-align: center;
  z-index: 1;
}

.header-content {
  position: relative;
  z-index: 2;
}

.festival-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.2) 0%, rgba(255, 140, 0, 0.2) 100%);
  border: 1px solid rgba(255, 215, 0, 0.3);
  border-radius: 30px;
  margin-bottom: 20px;
  animation: pulse-badge 2s ease-in-out infinite;

  .badge-icon { font-size: 18px; }
  .badge-text {
    font-size: 14px;
    font-weight: 600;
    color: #FFD700;
    letter-spacing: 2px;
  }
}

@keyframes pulse-badge {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.02); }
}

.main-title {
  position: relative;
  margin-bottom: 16px;

  .title-line {
    font-size: 42px;
    font-weight: 900;
    background: linear-gradient(135deg, #FFD700 0%, #FF6B6B 50%, #FF8C00 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    letter-spacing: 4px;
  }

  .title-glow {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    font-size: 42px;
    font-weight: 900;
    background: linear-gradient(135deg, #FFD700 0%, #FF6B6B 50%, #FF8C00 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    letter-spacing: 4px;
    filter: blur(20px);
    opacity: 0.5;
    animation: title-glow 3s ease-in-out infinite;
  }
}

@keyframes title-glow {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.02); }
}

.subtitle {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  font-size: 18px;
  color: #fff;

  .highlight {
    color: #FFD700;
    font-weight: 600;
  }

  .divider {
    color: rgba(255, 255, 255, 0.3);
  }
}

.header-decoration {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.confetti {
  position: absolute;
  inset: 0;

  .confetti-piece {
    position: absolute;
    width: 10px;
    height: 10px;
    background: #FFD700;
    animation: confetti-fall 4s linear infinite;

    @for $i from 1 through 20 {
      &:nth-child(#{$i}) {
        left: #{random(100)}%;
        animation-delay: #{$i * 0.2}s;
        background: ['#FF6B6B', '#4ECDC4', '#FFD700', '#95E1D3', '#F38181'][$i % 5];
        transform: rotate(#{random(360)}deg);
      }
    }
  }
}

@keyframes confetti-fall {
  0% { transform: translateY(-20px) rotate(0deg); opacity: 0; }
  10% { opacity: 1; }
  100% { transform: translateY(200px) rotate(720deg); opacity: 0; }
}

// 主要内容
.page-main {
  position: relative;
  z-index: 1;
  max-width: 500px;
  margin: 0 auto;
  padding: 0 16px 60px;
}

// 次数卡片
.times-card {
  position: relative;
  padding: 20px 24px;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.1) 0%, rgba(255, 140, 0, 0.1) 100%);
  border: 1px solid rgba(255, 215, 0, 0.2);
  border-radius: 16px;
  margin-bottom: 30px;
  overflow: hidden;

  .times-bg {
    position: absolute;
    inset: 0;
    background: radial-gradient(circle at top right, rgba(255, 215, 0, 0.1) 0%, transparent 50%);
  }

  .times-content {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .times-label {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.7);
  }

  .times-value {
    display: flex;
    align-items: baseline;
    gap: 4px;
  }

  .times-number {
    font-size: 48px;
    font-weight: 900;
    background: linear-gradient(135deg, #FFD700 0%, #FF6B6B 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    line-height: 1;
  }

  .times-unit {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.7);
  }

  .times-decoration {
    position: absolute;
    right: 20px;
    top: 50%;
    transform: translateY(-50%);
    display: flex;
    gap: 4px;

    .star {
      font-size: 14px;
      opacity: 0.5;
      animation: twinkle 1.5s ease-in-out infinite;

      &:nth-child(2) { animation-delay: 0.3s; }
      &:nth-child(3) { animation-delay: 0.6s; }
    }
  }
}

@keyframes twinkle {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

// 转盘区域
.wheel-section {
  position: relative;
  display: flex;
  justify-content: center;
  margin-bottom: 40px;

  .wheel-glow {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, rgba(255, 215, 0, 0.15) 0%, transparent 70%);
    border-radius: 50%;
    animation: wheel-pulse 3s ease-in-out infinite;
    pointer-events: none;
  }
}

@keyframes wheel-pulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.5; }
  50% { transform: translate(-50%, -50%) scale(1.1); opacity: 0.8; }
}

// 通用标题
.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 20px;

  .title-icon {
    font-size: 24px;
  }
}

// 奖品展示
.prizes-section {
  margin-bottom: 40px;
}

.prizes-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.prize-card {
  position: relative;
  padding: 20px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.05) 0%, rgba(255, 255, 255, 0.02) 100%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  text-align: center;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    border-color: rgba(255, 215, 0, 0.3);
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  }

  .prize-medal {
    position: absolute;
    top: -5px;
    right: 10px;
    font-size: 24px;
  }

  .prize-icon {
    font-size: 40px;
    margin-bottom: 10px;
  }

  .prize-name {
    font-size: 16px;
    font-weight: 600;
    color: #fff;
    margin-bottom: 6px;
  }

  .prize-prob {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.5);
    margin-bottom: 4px;
  }

  .prize-value {
    font-size: 14px;
    color: #FFD700;
    font-weight: 600;
  }

  &.prize-level-1 {
    grid-column: span 2;
    background: linear-gradient(135deg, rgba(255, 107, 107, 0.15) 0%, rgba(255, 69, 0, 0.1) 100%);
    border-color: rgba(255, 107, 107, 0.3);

    .prize-icon { font-size: 50px; }
  }

  &.prize-level-2 {
    background: linear-gradient(135deg, rgba(78, 205, 196, 0.15) 0%, rgba(68, 160, 141, 0.1) 100%);
    border-color: rgba(78, 205, 196, 0.3);
  }

  &.prize-level-3 {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.1) 100%);
    border-color: rgba(102, 126, 234, 0.3);
  }
}

// 我的奖品
.records-section {
  margin-bottom: 40px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 16px;
  border: 1px dashed rgba(255, 255, 255, 0.1);

  .empty-icon {
    font-size: 60px;
    margin-bottom: 16px;
    opacity: 0.5;
  }

  p {
    color: rgba(255, 255, 255, 0.5);
    margin-bottom: 8px;
  }

  .empty-tip {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.3);
  }
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  transition: all 0.3s;

  &:hover {
    background: rgba(255, 255, 255, 0.05);
  }

  &.is-win {
    background: linear-gradient(135deg, rgba(255, 215, 0, 0.05) 0%, rgba(255, 140, 0, 0.03) 100%);
    border-color: rgba(255, 215, 0, 0.2);
  }

  .record-left {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .record-icon {
    font-size: 32px;
  }

  .record-info {
    .record-name {
      font-size: 15px;
      font-weight: 600;
      color: #fff;
      margin-bottom: 4px;
    }

    .record-time {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.4);
    }
  }

  .receive-btn {
    padding: 8px 20px;
    background: linear-gradient(135deg, #FF6B6B 0%, #FF4500 100%);
    border: none;
    border-radius: 20px;
    color: #fff;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: scale(1.05);
      box-shadow: 0 4px 15px rgba(255, 69, 0, 0.4);
    }
  }

  .received-tag {
    padding: 6px 14px;
    background: rgba(78, 205, 196, 0.2);
    border-radius: 20px;
    color: #4ECDC4;
    font-size: 13px;
    font-weight: 500;
  }

  .thanks-tag {
    padding: 6px 14px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 20px;
    color: rgba(255, 255, 255, 0.5);
    font-size: 13px;
  }
}

// 活动规则
.rules-section {
  margin-bottom: 40px;
}

.rules-content {
  padding: 24px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
}

.rules-list {
  list-style: none;
  padding: 0;
  margin: 0;

  li {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    padding: 12px 0;
    color: rgba(255, 255, 255, 0.7);
    font-size: 14px;
    line-height: 1.6;
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);

    &:last-child {
      border-bottom: none;
    }

    .rule-icon {
      flex-shrink: 0;
      font-size: 18px;
    }

    strong {
      color: #FFD700;
      font-weight: 600;
    }
  }
}
</style>
