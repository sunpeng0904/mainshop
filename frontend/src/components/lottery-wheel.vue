<template>
  <div class="lottery-wheel">
    <!-- 外圈装饰灯 -->
    <div class="wheel-outer">
      <div class="light-ring">
        <span
          v-for="i in 24"
          :key="i"
          class="light"
          :class="{ active: lightActive }"
          :style="{ transform: `rotate(${i * 15}deg) translateY(-170px)` }"
        ></span>
      </div>
    </div>

    <!-- 转盘主体 -->
    <div class="wheel-main" :style="wheelStyle">
      <!-- 中心装饰 -->
      <div class="wheel-center">
        <div class="center-inner"></div>
      </div>
      <!-- 奖品扇区 -->
      <div
        v-for="(prize, index) in displayPrizes"
        :key="prize.id"
        class="prize-sector"
        :style="getSectorStyle(index)"
      >
        <div class="prize-content">
          <span class="prize-icon">{{ getPrizeIcon(prize.level) }}</span>
          <span class="prize-name">{{ prize.name }}</span>
        </div>
      </div>
    </div>

    <!-- 指针 -->
    <div class="wheel-pointer">
      <div class="pointer-inner">▼</div>
    </div>

    <!-- 开始按钮 -->
    <button
      class="start-button"
      :class="{ spinning: spinning, disabled: remainingTimes <= 0 }"
      :disabled="spinning || remainingTimes <= 0"
      @click="handleStart"
    >
      <div class="btn-bg"></div>
      <div class="btn-content">
        <span v-if="spinning" class="btn-text">抽奖中</span>
        <span v-else-if="remainingTimes <= 0" class="btn-text">已用完</span>
        <span v-else class="btn-text">开始</span>
      </div>
    </button>

    <!-- 结果弹窗 -->
    <Teleport to="body">
      <Transition name="popup">
        <div v-if="showResult" class="result-popup" @click.self="showResult = false">
          <div class="popup-content" :class="{ win: result?.win }">
            <div class="popup-bg"></div>
            <div class="popup-body">
              <!-- 中奖效果 -->
              <template v-if="result?.win">
                <div class="fireworks">
                  <span v-for="i in 20" :key="i" class="firework"></span>
                </div>
                <div class="result-icon win-icon">🎉</div>
                <h2 class="result-title">恭喜中奖</h2>
                <div class="prize-display">
                  <div class="prize-badge" :class="'level-' + result.prizeLevel">
                    {{ getLevelName(result.prizeLevel) }}
                  </div>
                  <div class="prize-name">{{ result.prizeName }}</div>
                </div>
              </template>
              <!-- 未中奖 -->
              <template v-else>
                <div class="result-icon lose-icon">😊</div>
                <h2 class="result-title lose">再接再厉</h2>
                <div class="prize-name muted">{{ result?.prizeName }}</div>
                <p class="encourage">下次一定中奖！</p>
              </template>

              <div class="remaining-times">
                <span>剩余次数</span>
                <strong>{{ result?.remainingTimes }}</strong>
                <span>次</span>
              </div>

              <button class="confirm-btn" @click="showResult = false">
                {{ result?.win ? '太棒了！' : '再来一次' }}
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getLotteryPrizes, getRemainingTimes, doLottery } from '@/api/lottery'

const props = defineProps({
  modelValue: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue', 'success'])

const prizes = ref([])
const remainingTimes = ref(0)
const spinning = ref(false)
const currentAngle = ref(0)
const showResult = ref(false)
const result = ref(null)
const lightActive = ref(true)
let lightTimer = null

// 直接使用后端返回的奖品列表，确保和后端角度计算一致
const displayPrizes = computed(() => prizes.value)

const wheelStyle = computed(() => ({
  transform: `rotate(${currentAngle.value}deg)`,
  transition: spinning.value
    ? 'transform 5s cubic-bezier(0.17, 0.67, 0.12, 0.99)'
    : 'none'
}))

// 获取奖品图标
const getPrizeIcon = (level) => {
  const icons = { 1: '💻', 2: '📱', 3: '🥤', 4: '🎁' }
  return icons[level] || '🎁'
}

// 获取等级名称
const getLevelName = (level) => {
  const names = { 1: '一等奖', 2: '二等奖', 3: '三等奖' }
  return names[level] || ''
}

// 计算扇区样式
const getSectorStyle = (index) => {
  const count = displayPrizes.value.length
  const angle = 360 / count
  const skew = 90 - angle
  const rotate = angle * index - 90

  const colors = [
    'linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%)',
    'linear-gradient(135deg, #4ECDC4 0%, #44A08D 100%)',
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)'
  ]

  return {
    transform: `rotate(${rotate}deg) skewY(-${skew}deg)`,
    background: colors[index % colors.length]
  }
}

// 获取奖品列表
const fetchPrizes = async () => {
  try {
    const res = await getLotteryPrizes()
    prizes.value = res.data || []
  } catch (error) {
    console.error('获取奖品列表失败:', error)
  }
}

// 获取剩余次数
const fetchRemainingTimes = async () => {
  try {
    const res = await getRemainingTimes()
    remainingTimes.value = res.data || 0
  } catch (error) {
    console.error('获取剩余次数失败:', error)
  }
}

// 开始抽奖
const handleStart = async () => {
  if (spinning.value || remainingTimes.value <= 0) return

  spinning.value = true

  try {
    const res = await doLottery()
    result.value = res.data
    remainingTimes.value = res.data.remainingTimes

    // 使用后端返回的旋转角度，确保转盘停在正确的奖品位置
    // 后端已经计算好了角度，前端直接累加即可
    currentAngle.value += res.data.rotateAngle

    // 等待动画完成
    setTimeout(() => {
      spinning.value = false
      showResult.value = true
      emit('success', res.data)
    }, 5500)
  } catch (error) {
    spinning.value = false
    ElMessage.error(error.message || '抽奖失败')
  }
}

// 灯光闪烁效果
const startLightAnimation = () => {
  lightTimer = setInterval(() => {
    lightActive.value = !lightActive.value
  }, 500)
}

onMounted(() => {
  fetchPrizes()
  fetchRemainingTimes()
  startLightAnimation()
})

onUnmounted(() => {
  if (lightTimer) clearInterval(lightTimer)
})

defineExpose({ fetchRemainingTimes })
</script>

<style lang="scss" scoped>
.lottery-wheel {
  position: relative;
  width: 340px;
  height: 340px;
  margin: 0 auto;
}

// 外圈装饰
.wheel-outer {
  position: absolute;
  top: -20px;
  left: -20px;
  right: -20px;
  bottom: -20px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 50%, #FF8C00 100%);
  box-shadow:
    0 0 30px rgba(255, 165, 0, 0.5),
    0 0 60px rgba(255, 140, 0, 0.3),
    inset 0 0 20px rgba(255, 255, 255, 0.3);
}

.light-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
}

.light {
  position: absolute;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 0 10px #fff, 0 0 20px #FFD700;
  transition: all 0.3s;

  &.active {
    background: #FFD700;
    box-shadow: 0 0 15px #FFD700, 0 0 30px #FF8C00, 0 0 45px #FF4500;
  }
}

// 转盘主体
.wheel-main {
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  bottom: 10px;
  border-radius: 50%;
  background: #1a1a2e;
  box-shadow:
    inset 0 0 30px rgba(0, 0, 0, 0.5),
    0 10px 30px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  z-index: 2;
}

.wheel-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
  box-shadow: 0 0 20px rgba(255, 165, 0, 0.5);
  z-index: 10;

  .center-inner {
    position: absolute;
    top: 8px;
    left: 8px;
    right: 8px;
    bottom: 8px;
    border-radius: 50%;
    background: linear-gradient(135deg, #FFF 0%, #FFE4B5 100%);
    box-shadow: inset 0 2px 10px rgba(0, 0, 0, 0.1);
  }
}

// 奖品扇区
.prize-sector {
  position: absolute;
  top: 0;
  left: 50%;
  width: 50%;
  height: 50%;
  transform-origin: 0% 100%;
  clip-path: polygon(0 100%, 100% 0, 0 0);

  .prize-content {
    position: absolute;
    left: 10px;
    top: 50%;
    transform: translateY(-50%) skewY(45deg);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
  }

  .prize-icon {
    font-size: 24px;
  }

  .prize-name {
    font-size: 12px;
    font-weight: bold;
    color: #fff;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
    writing-mode: vertical-rl;
    max-height: 60px;
    overflow: hidden;
  }
}

// 指针
.wheel-pointer {
  position: absolute;
  top: -5px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 20;

  .pointer-inner {
    width: 0;
    height: 0;
    border-left: 20px solid transparent;
    border-right: 20px solid transparent;
    border-top: 35px solid #FF4500;
    filter: drop-shadow(0 3px 6px rgba(255, 69, 0, 0.5));

    &::after {
      content: '';
      position: absolute;
      top: -30px;
      left: -10px;
      border-left: 10px solid transparent;
      border-right: 10px solid transparent;
      border-top: 18px solid #FF6347;
    }
  }
}

// 开始按钮
.start-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 70px;
  height: 70px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  z-index: 30;
  overflow: hidden;

  .btn-bg {
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, #FF4500 0%, #FF6B6B 50%, #FFD700 100%);
    border-radius: 50%;
    animation: btn-pulse 2s ease-in-out infinite;
  }

  .btn-content {
    position: relative;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #FF6B6B 0%, #FF4500 100%);
    border-radius: 50%;
    border: 3px solid #FFD700;
    box-shadow:
      0 0 20px rgba(255, 165, 0, 0.5),
      inset 0 2px 10px rgba(255, 255, 255, 0.3);
  }

  .btn-text {
    font-size: 14px;
    font-weight: bold;
    color: #fff;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  }

  &:hover:not(.disabled) .btn-content {
    transform: scale(1.05);
    box-shadow:
      0 0 30px rgba(255, 165, 0, 0.8),
      inset 0 2px 10px rgba(255, 255, 255, 0.3);
  }

  &.spinning .btn-bg {
    animation: btn-rotate 1s linear infinite;
  }

  &.disabled {
    cursor: not-allowed;
    opacity: 0.6;

    .btn-bg {
      animation: none;
      background: #999;
    }

    .btn-content {
      background: #aaa;
      border-color: #ccc;
    }
  }
}

@keyframes btn-pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.1); opacity: 0.8; }
}

@keyframes btn-rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>

<style lang="scss">
// 弹窗样式（非scoped）
.result-popup {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;

  .popup-content {
    position: relative;
    width: 320px;
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
    border-radius: 20px;
    overflow: hidden;
    box-shadow:
      0 20px 60px rgba(0, 0, 0, 0.5),
      0 0 100px rgba(255, 165, 0, 0.2);

    &.win {
      .popup-bg {
        background: linear-gradient(135deg, rgba(255, 215, 0, 0.1) 0%, rgba(255, 140, 0, 0.1) 100%);
      }
    }
  }

  .popup-bg {
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.05) 0%, rgba(255, 255, 255, 0.02) 100%);
  }

  .popup-body {
    position: relative;
    padding: 30px 20px;
    text-align: center;
  }

  .result-icon {
    font-size: 80px;
    line-height: 1;
    margin-bottom: 10px;

    &.win-icon {
      animation: bounce 0.6s ease infinite;
    }
  }

  .result-title {
    font-size: 28px;
    font-weight: bold;
    color: #FFD700;
    margin-bottom: 20px;
    text-shadow: 0 0 20px rgba(255, 215, 0, 0.5);

    &.lose {
      color: #aaa;
    }
  }

  .prize-display {
    margin-bottom: 20px;
  }

  .prize-badge {
    display: inline-block;
    padding: 6px 20px;
    border-radius: 20px;
    font-size: 14px;
    font-weight: bold;
    color: #fff;
    margin-bottom: 10px;

    &.level-1 { background: linear-gradient(135deg, #FF6B6B, #FF4500); }
    &.level-2 { background: linear-gradient(135deg, #4ECDC4, #44A08D); }
    &.level-3 { background: linear-gradient(135deg, #667eea, #764ba2); }
  }

  .prize-name {
    font-size: 24px;
    font-weight: bold;
    color: #fff;

    &.muted { color: #888; }
  }

  .encourage {
    color: #888;
    font-size: 14px;
    margin-top: 10px;
  }

  .remaining-times {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    margin: 20px 0;
    color: #888;
    font-size: 14px;

    strong {
      font-size: 24px;
      color: #FFD700;
      font-weight: bold;
    }
  }

  .confirm-btn {
    width: 100%;
    padding: 14px;
    border: none;
    border-radius: 12px;
    background: linear-gradient(135deg, #FF6B6B 0%, #FF4500 100%);
    color: #fff;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 10px 30px rgba(255, 69, 0, 0.4);
    }
  }

  // 烟花效果
  .fireworks {
    position: absolute;
    inset: 0;
    pointer-events: none;
    overflow: hidden;
  }

  .firework {
    position: absolute;
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #FFD700;
    animation: firework-burst 1s ease-out forwards;

    @for $i from 1 through 20 {
      &:nth-child(#{$i}) {
        top: 50%;
        left: 50%;
        animation-delay: #{$i * 0.05}s;
        --angle: #{$i * 18}deg;
        --distance: #{60 + random(40)}px;
      }
    }
  }

  @keyframes firework-burst {
    0% {
      transform: translate(-50%, -50%) scale(1);
      opacity: 1;
    }
    100% {
      transform: translate(
        calc(-50% + cos(var(--angle)) * var(--distance)),
        calc(-50% + sin(var(--angle)) * var(--distance))
      ) scale(0);
      opacity: 0;
    }
  }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

// 弹窗动画
.popup-enter-active,
.popup-leave-active {
  transition: all 0.3s ease;

  .popup-content {
    transition: all 0.3s ease;
  }
}

.popup-enter-from,
.popup-leave-to {
  opacity: 0;

  .popup-content {
    transform: scale(0.8);
  }
}
</style>
