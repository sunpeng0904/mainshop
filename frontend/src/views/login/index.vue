<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <el-icon :size="48" color="#409EFF"><Shop /></el-icon>
        <h2>{{ $t('common.appName', '在线商城') }}</h2>
      </div>

      <el-tabs v-model="activeTab" class="login-tabs">
        <!-- 登录表单 -->
        <el-tab-pane :label="$t('auth.loginTitle')" name="login">
          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                :placeholder="$t('auth.usernamePlaceholder')"
                prefix-icon="User"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                :placeholder="$t('auth.passwordPlaceholder')"
                prefix-icon="Lock"
                size="large"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-checkbox v-model="loginForm.rememberMe">{{ $t('auth.rememberMe') }}</el-checkbox>
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="submit-btn"
                :loading="loading"
                @click="handleLogin"
              >
                {{ $t('common.login') }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 注册表单 -->
        <el-tab-pane :label="$t('auth.registerTitle')" name="register">
          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                :placeholder="$t('common.username') + ' (3-20)'"
                prefix-icon="User"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                :placeholder="$t('common.password') + ' (6-20)'"
                prefix-icon="Lock"
                size="large"
                show-password
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                :placeholder="$t('auth.confirmPassword')"
                prefix-icon="Lock"
                size="large"
                show-password
              />
            </el-form-item>
            <el-form-item prop="email">
              <el-input
                v-model="registerForm.email"
                :placeholder="$t('common.email')"
                prefix-icon="Message"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input
                v-model="registerForm.phone"
                :placeholder="$t('common.phone')"
                prefix-icon="Phone"
                size="large"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="submit-btn"
                :loading="loading"
                @click="handleRegister"
              >
                {{ $t('common.register') }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'

const { t } = useI18n()
const store = useStore()
const router = useRouter()

const activeTab = ref('login')
const loading = ref(false)
const loginFormRef = ref()
const registerFormRef = ref()

// 登录表单
const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

// 注册表单
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: ''
})

// 登录验证规则
const loginRules = {
  username: [
    { required: true, message: t('auth.usernameRequired'), trigger: 'blur' }
  ],
  password: [
    { required: true, message: t('auth.passwordRequired'), trigger: 'blur' }
  ]
}

// 注册验证规则
const registerRules = {
  username: [
    { required: true, message: t('auth.usernameRequired'), trigger: 'blur' },
    { min: 3, max: 20, message: '3-20 characters', trigger: 'blur' }
  ],
  password: [
    { required: true, message: t('auth.passwordRequired'), trigger: 'blur' },
    { min: 6, max: 20, message: t('auth.passwordMinLength'), trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: t('auth.confirmPassword'), trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error(t('auth.passwordNotMatch')))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  email: [
    { type: 'email', message: t('auth.emailInvalid'), trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: t('auth.phoneInvalid'), trigger: 'blur' }
  ]
}

// 登录
const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true

    await store.dispatch('user/login', loginForm)
    ElMessage.success(t('auth.loginSuccess'))

    // 获取用户信息
    await store.dispatch('user/getUserInfo')

    // 跳转到首页或之前的页面
    const redirect = router.currentRoute.value.query.redirect || '/home'
    router.push(redirect)
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

// 注册
const handleRegister = async () => {
  try {
    await registerFormRef.value.validate()
    loading.value = true

    await store.dispatch('user/register', registerForm)
    ElMessage.success(t('auth.registerSuccess'))

    // 切换到登录表单
    activeTab.value = 'login'
    loginForm.username = registerForm.username

    // 清空注册表单
    registerFormRef.value.resetFields()
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;

  h2 {
    margin-top: 16px;
    font-size: 24px;
    color: #303133;
  }
}

.login-tabs {
  :deep(.el-tabs__header) {
    margin-bottom: 30px;
  }

  :deep(.el-tabs__nav-wrap::after) {
    display: none;
  }

  :deep(.el-tabs__item) {
    font-size: 16px;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 20px;
  }
}

.submit-btn {
  width: 100%;
}
</style>
