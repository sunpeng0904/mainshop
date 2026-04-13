<template>
  <el-dialog
    v-model="visible"
    title="微信支付"
    width="400px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="pay-dialog-content">
      <!-- 支付二维码 -->
      <div v-if="payStatus === 'pending'" class="qrcode-wrapper">
        <div class="amount-info">
          <span class="label">支付金额</span>
          <span class="amount">¥{{ amount }}</span>
        </div>
        <div class="qrcode-box">
          <canvas ref="qrcodeCanvas" class="qrcode-canvas"></canvas>
        </div>
        <p class="tip">请使用微信扫描二维码完成支付</p>

        <!-- 沙箱模式：模拟支付按钮 -->
        <div class="sandbox-actions">
          <el-button type="primary" @click="handleMockPay" :loading="mockPaying">
            模拟支付成功（测试）
          </el-button>
        </div>
      </div>

      <!-- 支付成功 -->
      <div v-else-if="payStatus === 'success'" class="result-wrapper">
        <el-icon class="success-icon"><CircleCheckFilled /></el-icon>
        <p class="success-text">支付成功</p>
      </div>

      <!-- 支付超时 -->
      <div v-else-if="payStatus === 'timeout'" class="result-wrapper">
        <el-icon class="timeout-icon"><WarningFilled /></el-icon>
        <p class="timeout-text">支付超时，请重新下单</p>
        <el-button type="primary" @click="handleClose">关闭</el-button>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <span v-if="payStatus === 'pending'" class="countdown">
          请在 <em>{{ countdown }}</em> 秒内完成支付
        </span>
        <el-button @click="handleClose">{{ payStatus === 'success' ? '完成' : '取消' }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheckFilled, WarningFilled } from '@element-plus/icons-vue'
import { createWechatNativePayment, queryWechatPayStatus, mockWechatPaySuccess } from '@/api/payment'
import QRCode from 'qrcode'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  orderId: {
    type: Number,
    default: null
  },
  amount: {
    type: [Number, String],
    default: 0
  }
})

const emit = defineEmits(['update:modelValue', 'success', 'close'])

const visible = ref(false)
const payStatus = ref('pending') // pending, success, timeout
const countdown = ref(300) // 5分钟倒计时
const qrcodeCanvas = ref(null)
const orderNo = ref('')
const codeUrl = ref('')
const mockPaying = ref(false)

let pollTimer = null
let countdownTimer = null

// 监听显示状态
watch(() => props.modelValue, async (val) => {
  visible.value = val
  if (val && props.orderId) {
    await initPayment()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 初始化支付
const initPayment = async () => {
  payStatus.value = 'pending'
  countdown.value = 300

  try {
    const res = await createWechatNativePayment(props.orderId)
    orderNo.value = res.data.orderNo
    codeUrl.value = res.data.codeUrl

    // 生成二维码
    await nextTick()
    if (qrcodeCanvas.value && codeUrl.value) {
      QRCode.toCanvas(qrcodeCanvas.value, codeUrl.value, {
        width: 200,
        margin: 2,
        color: {
          dark: '#000000',
          light: '#ffffff'
        }
      })
    }

    // 开始轮询支付状态
    startPolling()
    startCountdown()
  } catch (error) {
    console.error('创建支付失败:', error)
    ElMessage.error('创建支付失败，请重试')
    handleClose()
  }
}

// 开始轮询支付状态
const startPolling = () => {
  stopPolling()
  pollTimer = setInterval(async () => {
    try {
      const res = await queryWechatPayStatus(orderNo.value)
      if (res.data === 'SUCCESS') {
        handlePaySuccess()
      }
    } catch (error) {
      console.error('查询支付状态失败:', error)
    }
  }, 3000) // 每3秒查询一次
}

// 停止轮询
const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

// 开始倒计时
const startCountdown = () => {
  stopCountdown()
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      payStatus.value = 'timeout'
      stopPolling()
      stopCountdown()
    }
  }, 1000)
}

// 停止倒计时
const stopCountdown = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
}

// 支付成功
const handlePaySuccess = () => {
  payStatus.value = 'success'
  stopPolling()
  stopCountdown()
  ElMessage.success('支付成功')
  emit('success')
}

// 沙箱模式：模拟支付成功
const handleMockPay = async () => {
  mockPaying.value = true
  try {
    await mockWechatPaySuccess(orderNo.value)
    handlePaySuccess()
  } catch (error) {
    console.error('模拟支付失败:', error)
    ElMessage.error('模拟支付失败')
  } finally {
    mockPaying.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  stopPolling()
  stopCountdown()
  visible.value = false
  emit('close')
}

// 组件销毁时清理定时器
onUnmounted(() => {
  stopPolling()
  stopCountdown()
})
</script>

<style lang="scss" scoped>
.pay-dialog-content {
  text-align: center;
  padding: 20px 0;
}

.qrcode-wrapper {
  .amount-info {
    margin-bottom: 20px;

    .label {
      color: #909399;
      margin-right: 10px;
    }

    .amount {
      font-size: 24px;
      font-weight: bold;
      color: #f56c6c;
    }
  }

  .qrcode-box {
    display: flex;
    justify-content: center;
    margin-bottom: 16px;

    .qrcode-canvas {
      border: 1px solid #ebeef5;
      border-radius: 4px;
    }
  }

  .tip {
    color: #909399;
    font-size: 14px;
    margin-bottom: 16px;
  }

  .sandbox-actions {
    padding-top: 16px;
    border-top: 1px dashed #ebeef5;

    .el-button {
      width: 100%;
    }
  }
}

.result-wrapper {
  padding: 40px 0;

  .success-icon {
    font-size: 64px;
    color: #67c23a;
  }

  .timeout-icon {
    font-size: 64px;
    color: #e6a23c;
  }

  .success-text {
    color: #67c23a;
    font-size: 18px;
    margin-top: 16px;
  }

  .timeout-text {
    color: #e6a23c;
    font-size: 18px;
    margin-top: 16px;
    margin-bottom: 20px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .countdown {
    color: #909399;
    font-size: 14px;

    em {
      color: #f56c6c;
      font-style: normal;
      font-weight: bold;
    }
  }
}
</style>
