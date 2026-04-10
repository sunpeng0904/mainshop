<template>
  <div class="user-center-container">
    <el-row :gutter="20">
      <!-- 左侧菜单 -->
      <el-col :span="5">
        <div class="user-menu card">
          <div class="user-avatar">
            <el-avatar :size="80" :src="userInfo?.avatar">
              {{ userInfo?.nickname?.charAt(0) || userInfo?.username?.charAt(0) }}
            </el-avatar>
            <h3>{{ userInfo?.nickname || userInfo?.username }}</h3>
          </div>
          <el-menu :default-active="activeMenu" @select="handleMenuSelect">
            <el-menu-item index="profile">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="address">
              <el-icon><Location /></el-icon>
              <span>收货地址</span>
            </el-menu-item>
            <el-menu-item index="password">
              <el-icon><Lock /></el-icon>
              <span>修改密码</span>
            </el-menu-item>
          </el-menu>
        </div>
      </el-col>

      <!-- 右侧内容 -->
      <el-col :span="19">
        <!-- 个人信息 -->
        <div v-show="activeMenu === 'profile'" class="content-card card">
          <h3 class="card-title">个人信息</h3>
          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-width="80px"
            style="max-width: 500px"
          >
            <el-form-item label="用户名">
              <el-input :model-value="userInfo?.username" disabled />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="profileForm.gender">
                <el-radio :value="0">保密</el-radio>
                <el-radio :value="1">男</el-radio>
                <el-radio :value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSaveProfile">
                保存
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 收货地址 -->
        <div v-show="activeMenu === 'address'" class="content-card card">
          <div class="card-header">
            <h3 class="card-title">收货地址</h3>
            <el-button type="primary" @click="handleAddAddress">
              新增地址
            </el-button>
          </div>
          <div class="address-list">
            <div v-for="addr in addresses" :key="addr.id" class="address-item">
              <div class="address-info">
                <span class="name">{{ addr.receiverName }}</span>
                <span class="phone">{{ addr.receiverPhone }}</span>
                <el-tag v-if="addr.isDefault === 1" type="success" size="small">默认</el-tag>
              </div>
              <div class="address-detail">
                {{ addr.fullAddress || `${addr.province}${addr.city}${addr.district}${addr.detailAddress}` }}
              </div>
              <div class="address-actions">
                <el-button text type="primary" @click="handleEditAddress(addr)">
                  编辑
                </el-button>
                <el-button text type="danger" @click="handleDeleteAddress(addr.id)">
                  删除
                </el-button>
                <el-button
                  v-if="addr.isDefault !== 1"
                  text
                  @click="handleSetDefault(addr.id)"
                >
                  设为默认
                </el-button>
              </div>
            </div>
            <el-empty v-if="addresses.length === 0" description="暂无收货地址" />
          </div>
        </div>

        <!-- 修改密码 -->
        <div v-show="activeMenu === 'password'" class="content-card card">
          <h3 class="card-title">修改密码</h3>
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
            style="max-width: 400px"
          >
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入当前密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请确认新密码"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleChangePassword">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>

    <!-- 地址编辑弹窗 -->
    <el-dialog
      v-model="addressDialogVisible"
      :title="addressForm.id ? '编辑地址' : '新增地址'"
      width="500px"
    >
      <el-form
        ref="addressFormRef"
        :model="addressForm"
        :rules="addressRules"
        label-width="80px"
      >
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="addressForm.receiverPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所在地区" required>
          <el-input v-model="addressForm.province" placeholder="省" style="width: 100px; margin-right: 8px" />
          <el-input v-model="addressForm.city" placeholder="市" style="width: 100px; margin-right: 8px" />
          <el-input v-model="addressForm.district" placeholder="区" style="width: 100px" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input
            v-model="addressForm.detailAddress"
            type="textarea"
            :rows="2"
            placeholder="请输入详细地址"
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault" :true-value="1" :false-value="0">
            设为默认地址
          </el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addressDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveAddress">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import { updateUserInfo, changePassword } from '@/api/user'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'

const store = useStore()

const activeMenu = ref('profile')
const saving = ref(false)
const addressDialogVisible = ref(false)

// 用户信息
const userInfo = computed(() => store.state.user.userInfo)

// 个人信息表单
const profileFormRef = ref()
const profileForm = reactive({
  nickname: '',
  email: '',
  phone: '',
  gender: 0
})

const profileRules = {
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]
}

// 密码表单
const passwordFormRef = ref()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 地址列表
const addresses = ref([])

// 地址表单
const addressFormRef = ref()
const addressForm = reactive({
  id: null,
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 0
})

const addressRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

// 菜单选择
const handleMenuSelect = (index) => {
  activeMenu.value = index
  if (index === 'address') {
    fetchAddresses()
  }
}

// 保存个人信息
const handleSaveProfile = async () => {
  try {
    await profileFormRef.value.validate()
    saving.value = true

    await updateUserInfo(profileForm)
    await store.dispatch('user/getUserInfo')
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    saving.value = true

    await changePassword(passwordForm.oldPassword, passwordForm.newPassword)
    ElMessage.success('密码修改成功，请重新登录')
    await store.dispatch('user/logout')
    window.location.href = '/login'
  } catch (error) {
    console.error('修改密码失败:', error)
  } finally {
    saving.value = false
  }
}

// 获取地址列表
const fetchAddresses = async () => {
  try {
    const response = await getAddressList()
    addresses.value = response.data || []
  } catch (error) {
    console.error('获取地址失败:', error)
  }
}

// 新增地址
const handleAddAddress = () => {
  Object.assign(addressForm, {
    id: null,
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: 0
  })
  addressDialogVisible.value = true
}

// 编辑地址
const handleEditAddress = (addr) => {
  Object.assign(addressForm, {
    id: addr.id,
    receiverName: addr.receiverName,
    receiverPhone: addr.receiverPhone,
    province: addr.province,
    city: addr.city,
    district: addr.district,
    detailAddress: addr.detailAddress,
    isDefault: addr.isDefault
  })
  addressDialogVisible.value = true
}

// 保存地址
const handleSaveAddress = async () => {
  try {
    await addressFormRef.value.validate()
    saving.value = true

    if (addressForm.id) {
      await updateAddress(addressForm)
    } else {
      await addAddress(addressForm)
    }

    ElMessage.success('保存成功')
    addressDialogVisible.value = false
    fetchAddresses()
  } catch (error) {
    console.error('保存地址失败:', error)
  } finally {
    saving.value = false
  }
}

// 删除地址
const handleDeleteAddress = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', { type: 'warning' })
    await deleteAddress(id)
    ElMessage.success('删除成功')
    fetchAddresses()
  } catch (e) {
    // 取消
  }
}

// 设为默认
const handleSetDefault = async (id) => {
  try {
    await setDefaultAddress(id)
    ElMessage.success('设置成功')
    fetchAddresses()
  } catch (error) {
    console.error('设置默认地址失败:', error)
  }
}

// 监听用户信息变化，初始化表单
watch(userInfo, (val) => {
  if (val) {
    Object.assign(profileForm, {
      nickname: val.nickname || '',
      email: val.email || '',
      phone: val.phone || '',
      gender: val.gender || 0
    })
  }
}, { immediate: true })

onMounted(() => {
  if (userInfo.value) {
    Object.assign(profileForm, {
      nickname: userInfo.value.nickname || '',
      email: userInfo.value.email || '',
      phone: userInfo.value.phone || '',
      gender: userInfo.value.gender || 0
    })
  }
})
</script>

<style lang="scss" scoped>
.user-center-container {
  max-width: 1200px;
  margin: 0 auto;
}

.user-menu {
  .user-avatar {
    text-align: center;
    padding: 24px 0;
    border-bottom: 1px solid #ebeef5;

    h3 {
      margin-top: 12px;
      font-size: 16px;
    }
  }

  :deep(.el-menu) {
    border-right: none;
  }
}

.content-card {
  min-height: 500px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .card-title {
    margin-bottom: 0;
    padding-bottom: 0;
    border-bottom: none;
  }
}

.address-list {
  .address-item {
    padding: 16px;
    border: 1px solid #ebeef5;
    border-radius: 4px;
    margin-bottom: 12px;

    &:hover {
      border-color: #409EFF;
    }

    .address-info {
      margin-bottom: 8px;

      .name {
        font-weight: 500;
        margin-right: 16px;
      }

      .phone {
        color: #606266;
        margin-right: 12px;
      }
    }

    .address-detail {
      color: #909399;
      font-size: 14px;
      margin-bottom: 12px;
    }

    .address-actions {
      text-align: right;
    }
  }
}
</style>
